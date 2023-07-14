//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: FieldFormatter.java,v $
//  Revision 1.13  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.12  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.11  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.10  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.9  2005/10/25 21:38:42  chostetter_cvs
//  Fixed binary data formatting
//
//  Revision 1.8  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.7  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.6  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.5  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.4  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.FieldFormatterDescriptor;


/**
 * A FieldFormatter formats data according to a field structure.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class FieldFormatter extends AbstractDataFormatter
{
	private FieldFormatterDescriptor fDescriptor;
	
	private DataSelector fSource;
	private DataSelector fTarget;
	
	private DataFormatter fPrefix;
	private DataFormatter fLabel;
	private DataFormatter fLabelSeparator;
	private DataFormatter fValue;
	private DataFormatter fPostfix;
	
	private boolean fApplyToRemainingFields = false;
	private boolean fUseDataNameAsLabel = false;
	private boolean fUseNameAsLabel = false;
	
	private static Map fCachedLabels;
	
	
	/**
	 * Constructs a new FieldFormatter and configures it in accordance with 
	 * the given FieldFormatterDescriptor.
	 *
	 * @param descriptor The FieldFormatterDescriptor according to which to 
	 * 		configure the new FieldFormatter
	**/
	
	public FieldFormatter(FieldFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this FieldFormatter in accordance with its current 
	 * FieldFormatterDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		fSource = getSource();
		fTarget = getTarget();
		
		fApplyToRemainingFields = fDescriptor.applyToRemainingFields();
		
		DataFormatDescriptor prefixDescriptor = fDescriptor.getPrefix();
		
		if (prefixDescriptor != null)
		{
			fPrefix = sDataFormatterFactory.getDataFormatter(prefixDescriptor);
		}
		
		DataFormatDescriptor labelDescriptor = fDescriptor.getLabel();
		
		if (labelDescriptor != null)
		{
			fLabel = sDataFormatterFactory.getDataFormatter(labelDescriptor);
		}
		
		fUseNameAsLabel = fDescriptor.usesNameAsLabel();
		
		if ((fLabel == null) && fUseNameAsLabel)
		{
			useNameAsLabel();
		}
		
		fUseDataNameAsLabel = fDescriptor.usesDataNameAsLabel();
		
		if ((fLabel == null) && fUseDataNameAsLabel)
		{
			useDataNameAsLabel();
		}
		
		DataFormatDescriptor labelSeparatorDescriptor = 
			fDescriptor.getLabelSeparator();
		
		if (labelSeparatorDescriptor != null)
		{
			fLabelSeparator = 
				sDataFormatterFactory.getDataFormatter(labelSeparatorDescriptor);
		}
		
		DataFormatDescriptor valueDescriptor = fDescriptor.getValue();
		
		if (valueDescriptor != null)
		{
			fValue = sDataFormatterFactory.getDataFormatter(valueDescriptor);
		}
		
		DataFormatDescriptor postfixDescriptor = fDescriptor.getPostfix();
		
		if (postfixDescriptor != null)
		{
			fPostfix = sDataFormatterFactory.getDataFormatter(postfixDescriptor);
		}
	}
	

	/**
	 * Returns true if this FieldFormatter is configured to be applied to all the 
	 * remaining fields in the input, false otherwise.
	 *
	 * @return True if this FieldFormatter is configured to be applied to all the 
	 * 	remaining fields in the input, false otherwise
	**/
	
	public boolean applyToRemainingFields()
	{
		return (fApplyToRemainingFields);
	}
	

	/**
	 * Configures this FieldFormatter to use the name of the data it formats as its 
	 * label.
	 *
	**/
	
	void useDataNameAsLabel()
	{
		fUseDataNameAsLabel = true;
	}
	

	/**
	 * Returns true if this FieldFormatter is configured to use the name of the 
	 * data it formats as its label, false otherwise.
	 * 
	 * @return True if this FieldFormatter is configured to use the name of the 
	 * 		data it formats as its label, false otherwise
	**/
	
	public boolean usesDataNameAsLabel()
	{
		return (fUseDataNameAsLabel);
	}
	

	/**
	 * Configures this FieldFormatter to use its name as its label.
	 *
	**/
	
	void useNameAsLabel()
	{
		fUseNameAsLabel = true;
	}
	

	/**
	 * Returns true if this FieldFormatter is configured to use its name as its 
	 * label, false otherwise.
	 * 
	 * @return True if this FieldFormatter is configured to use its name as its 
	 * 		label, false otherwise
	**/
	
	public boolean usesNameAsLabel()
	{
		return (fUseNameAsLabel);
	}
	

	/**
	 * Causes this FieldFormatter to format the given data Object as specified by 
	 * its associated FieldFormatterDescriptor into the given target Object, and 
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
			selectedData = fSource.select(data);	
		}
		
		Object selectedTarget = target;
		
		if (fTarget != null)
		{
			selectedTarget = fTarget.select(target);
		}
		
		if (fPrefix != null)
		{
			formatResults = fPrefix.format(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		String dataName = null;
		
		if (usesDataNameAsName() || fUseDataNameAsLabel)
		{
			if (data instanceof HasName)
			{
				dataName = ((HasName) data).getName();
			} 
			else if (data instanceof Map.Entry)
			{
				Object entryKey = ((Map.Entry) data).getKey();
				
				dataName = entryKey.toString();
			}
			
			if (usesDataNameAsName() && (dataName != null))
			{
				setName(dataName);
			}
		}
		
		if (fUseNameAsLabel || fUseDataNameAsLabel)
		{
			String labelKey = null;
			
			if (fUseNameAsLabel)
			{
				labelKey = getName();
			}
			else if (fUseDataNameAsLabel)
			{
				labelKey = dataName;
			}
			
			if (labelKey != null)
			{
				if (fCachedLabels == null)
				{
					fCachedLabels = new HashMap();
				}
				
				fLabel = (DataFormatter) fCachedLabels.get(labelKey);
				
				if (fLabel == null)
				{
					fLabel = new SimpleValueFormatter(labelKey);
					fCachedLabels.put(labelKey, fLabel);
				}
			}
		}
		
		if (fLabel != null)
		{
			formatResults = fLabel.format(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		if (fLabelSeparator != null)
		{
			formatResults = fLabelSeparator.format
				(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		if (fValue != null)
		{
			formatResults = fValue.format(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		if (fPostfix != null)
		{
			formatResults = fPostfix.format(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this FieldFormatter.
	 *
	 *  @return A String representation of this FieldFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("FieldFormatter: ");
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		
		return (result.toString());
	}
}
