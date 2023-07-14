//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ListValueFormatter.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2006/04/27 21:38:15  chostetter_cvs
//  Fixed error in configuration from descriptor
//
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.ListValueFormatterDescriptor;


/**
 * A ListValueFormatter formats List, Collection, or Map data into a sequential list 
 * value representation.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class ListValueFormatter extends AbstractDataFormatter 
	implements ValueFormatter
{
	private ListValueFormatterDescriptor fDescriptor;
	
	private DataSelector fSource;
	private DataSelector fTarget;
		
	private DataFormatter fInitiator;
	private DataFormatter fInitiatorSeparator;
	private DataFormatter fPrefix;
	private DataFormatter fValue;
	private DataFormatter fPostfix;
	private DataFormatter fTerminator;
	
	private int fLength = 0;
	private boolean fSuppressFirstPrefix = false;
	private boolean fSuppressLastPostfix = false;
	
	
	/**
	 * Default constructor of a new ListValueFormatter.
	 *
	**/
	
	public ListValueFormatter()
	{
		
	}
	

	/**
	 * Constructs a new ListValueFormatter and configures it in accordance with 
	 * the given ListValueFormatterDescriptor.
	 *
	 * @param descriptor The ListValueFormatterDescriptor according to which to 
	 * 		configure the new ListValueFormatter
	**/
	
	public ListValueFormatter(ListValueFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this ListValueFormatter in accordance with its current 
	 * ListValueFormatterDescriptor.
	**/
	
	private void configureFromDescriptor()
	{
		fSource = getSource();
		fTarget = getTarget();
		
		fLength = fDescriptor.getLength();
		fSuppressFirstPrefix = fDescriptor.suppressesFirstPrefix();
		fSuppressLastPostfix = fDescriptor.suppressesLastPostfix();
		
		DataFormatDescriptor initiatorDescriptor = fDescriptor.getInitiator();
		
		if (initiatorDescriptor != null)
		{
			fInitiator = 
				sDataFormatterFactory.getDataFormatter(initiatorDescriptor);
		}
		
		DataFormatDescriptor initiatorSeparatorDescriptor = 
			fDescriptor.getInitiatorSeparator();
		
		if (initiatorSeparatorDescriptor != null)
		{
			fInitiatorSeparator = sDataFormatterFactory.getDataFormatter
				(initiatorSeparatorDescriptor);
		}
		
		DataFormatDescriptor prefixDescriptor = fDescriptor.getPrefix();
		
		if (prefixDescriptor != null)
		{
			fPrefix = (DataFormatter) 
				sDataFormatterFactory.getDataFormatter(prefixDescriptor);
		}
		
		DataFormatDescriptor valueDescriptor = fDescriptor.getValue();
		
		if (valueDescriptor != null)
		{
			fValue = (DataFormatter) 
				sDataFormatterFactory.getDataFormatter(valueDescriptor);
		}
		
		DataFormatDescriptor postfixDescriptor = fDescriptor.getPostfix();
		
		if (postfixDescriptor != null)
		{
			fPostfix = (DataFormatter) 
				sDataFormatterFactory.getDataFormatter(postfixDescriptor);
		}
		
		DataFormatDescriptor terminatorDescriptor = fDescriptor.getTerminator();
		
		if (terminatorDescriptor != null)
		{
			fTerminator = (DataFormatter) 
				sDataFormatterFactory.getDataFormatter(terminatorDescriptor);
		}
	}
	

	/**
	 * Causes this ListValueFormatter to format the given data Object as specified by 
	 * its associated ListValueFormatterDescriptor into the given target Object, and 
	 * return the results.
	 *
	 * @param data The data to be formatted
	 * @param context An optional Map of contextual information
	 * @param target The target to receive the formatted data
	 * @return The result of formatting the given data
	 * @throws UnsupportedOperationException if this FieldFormatter is unable to 
	 * 		format the given data
	**/
	
	public Object format(Object data, Map context, Object target)
		throws UnsupportedOperationException
	{
		Object result = null;
		
		Object formatResults = null;
		
		Object selectedData = data;
		
		if (fSource != null)
		{
			selectedData = fSource.select(selectedData);	
		}
		
		Object selectedTarget = target;
		
		if (fTarget != null)
		{
			selectedTarget = fTarget.select(target);
		}
		
		if (selectedData instanceof Map.Entry)
		{
			selectedData = ((Map.Entry) selectedData).getValue();
		}
		
		if (! (selectedData instanceof List))
		{
			if (selectedData instanceof Map)
			{
				selectedData = ((Map) selectedData).values();
			}
			
			if (selectedData instanceof Collection)
			{
				selectedData = new ArrayList((Collection) selectedData);
			}
		}
		
		if (fInitiator != null)
		{
			formatResults = fInitiator.format
				(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		if (fInitiatorSeparator != null)
		{
			formatResults = fInitiatorSeparator.format
				(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		if (selectedData instanceof List)
		{
			List valueList = (List) selectedData;
		
			Iterator values = valueList.iterator();
			
			boolean isFirst = true;
			boolean isLast = false;
			
			while (values.hasNext())
			{
				Object value = values.next();
				
				if (fPrefix != null && (! fSuppressFirstPrefix || ! isFirst))
				{
					formatResults = fPrefix.format
						(selectedData, context, selectedTarget);
					
					if (formatResults != null)
					{
						result = append(result, formatResults);
					}
					
					if (isFirst)
					{
						isFirst = false;
					}
				}
				
				if (fValue != null)
				{
					formatResults = fValue.format
						(value, context, selectedTarget);
					
					if (formatResults != null)
					{
						result = append(result, formatResults);
					}
				}
				
				if (! values.hasNext())
				{
					isLast = true;
				}
				
				if (fPostfix != null && (! fSuppressLastPostfix || ! isLast))
				{
					formatResults = fPostfix.format
						(selectedData, context, selectedTarget);
					
					if (formatResults != null)
					{
						result = append(result, formatResults);
					}
				}
			}
			
			if (fTerminator != null)
			{
				formatResults = fTerminator.format
					(selectedData, context, selectedTarget);
				
				if (formatResults != null)
				{
					result = append(result, formatResults);
				}
			}
		}
		
		return (result);
	}


	/** 
	 *  Returns a String representation of this ListValueFormatter.
	 *
	 *  @return A String representation of this ListValueFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ListValueFormatter: ");
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		
		return (result.toString());
	}
}
