//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: RecordFormatter.java,v $
//  Revision 1.15  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.14  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.13  2006/04/18 04:00:47  tames
//  Modified to reflect relocated Message classes.
//
//  Revision 1.12  2006/02/07 22:27:36  chostetter_cvs
//  Now clones input data that can be consumed
//
//  Revision 1.11  2006/02/07 21:07:50  chostetter_cvs
//  Keyed data selector now removes selected entry from input data map
//
//  Revision 1.10  2006/02/02 17:04:50  chostetter_cvs
//  Added support for Sequencer in Records
//
//  Revision 1.9  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.8  2005/10/28 23:07:09  chostetter_cvs
//  Fixed error in using data name as label
//
//  Revision 1.7  2005/10/25 21:38:42  chostetter_cvs
//  Fixed binary data formatting
//
//  Revision 1.6  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.5  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.4  2005/09/13 13:26:10  chostetter_cvs
//  Fixes for HAWC command formatting
//
//  Revision 1.3  2005/09/08 22:18:32  chostetter_cvs
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.FieldFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.RecordFormatterDescriptor;
import gov.nasa.gsfc.irc.messages.MessageFactory;


/**
 * A RecordFormatter formats data according to a record structure.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class RecordFormatter extends AbstractDataFormatter
{
	private static final MessageFactory fMessageFactory = 
		Irc.getMessageFactory();
		
	private DataSelector fSource;
	private DataSelector fTarget;
		
	private RecordFormatterDescriptor fDescriptor;
	
	private DataFormatter fSequencer;
	private DataFormatter fSequencerSeparator;
	private DataFormatter fInitiator;
	private DataFormatter fInitiatorSeparator;
	private List fFields = new ArrayList();
	private DataFormatter fTerminator;
	
	private boolean fUseDataNameAsInitiator = false;

	
	/**
	 * Default constructor of a new RecordFormatter.
	 *
	**/
	
	public RecordFormatter()
	{
		
	}
	

	/**
	 * Constructs a new RecordFormatter and configures it in accordance with 
	 * the given RecordFormatterDescriptor.
	 *
	 * @param descriptor The RecordFormatterDescriptor according to which to 
	 * 		configure the new RecordFormatter
	**/
	
	public RecordFormatter(RecordFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this RecordFormatter in accordance with its current 
	 * RecordFormatterDescriptor.
	**/
	
	private void configureFromDescriptor()
	{
		fSource = getSource();
		fTarget = getTarget();
		
		DataFormatDescriptor sequencerDescriptor = fDescriptor.getSequencer();
		
		if (sequencerDescriptor != null)
		{
			fSequencer = 
				sDataFormatterFactory.getDataFormatter(sequencerDescriptor);
		}
		
		DataFormatDescriptor sequencerSeparatorDescriptor = 
			fDescriptor.getSequencerSeparator();
		
		if (sequencerSeparatorDescriptor != null)
		{
			fSequencerSeparator = sDataFormatterFactory.getDataFormatter
				(sequencerSeparatorDescriptor);
		}
		
		fUseDataNameAsInitiator = fDescriptor.usesDataNameAsInitiator();
		
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
		
		Iterator fields = fDescriptor.getFields().iterator();

		while (fields.hasNext())
		{
			FieldFormatterDescriptor fieldDescriptor = 
				(FieldFormatterDescriptor) fields.next();
			
			FieldFormatter field = (FieldFormatter) 
				sDataFormatterFactory.getDataFormatter(fieldDescriptor);
			
			fFields.add(field);
		}
		
		DataFormatDescriptor terminatorDescriptor = fDescriptor.getTerminator();
		
		if (terminatorDescriptor != null)
		{
			fTerminator = 
				sDataFormatterFactory.getDataFormatter(terminatorDescriptor);
		}
	}
	

	/**
	 * Causes this RecordFormatter to format the given data Object as specified by 
	 * its associated RecordFormatterDescriptor into the given target Object, and 
	 * return the results.
	 *
	 * @param data The data to be formatted
	 * @param context An optional Map of contextual information
	 * @param target The target to receive the formatted data
	 * @return The result of formatting the given data
	 * @throws UnsupportedOperationException if this RecordFormatter is unable to 
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
		
		// Clone an input Message, Map or Collection so that the data selectors can 
		// remove ("consume") entries/items from it without messing up any other 
		// users of the data.
		
		if (selectedData instanceof Message)
		{
			selectedData = fMessageFactory.createMessage((Message) selectedData);
		}
		else if (selectedData instanceof Map)
		{
			selectedData = new LinkedHashMap((Map) selectedData);
		}
		else if (selectedData instanceof Collection)
		{
			selectedData = new ArrayList((Collection) selectedData);
		}
		
		Object selectedTarget = target;
		
		if (fTarget != null)
		{
			selectedTarget = fTarget.select(target);
		}
		
		if (fSequencer != null)
		{
			formatResults = fSequencer.format
				(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
			}
		}
		
		if (fSequencerSeparator != null)
		{
			formatResults = fSequencerSeparator.format
				(selectedData, context, selectedTarget);
			
			if (formatResults != null)
			{
				result = append(result, formatResults);
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
		else if (fUseDataNameAsInitiator)
		{
			if (data instanceof HasName)
			{
				String dataName = ((HasName) data).getName();
				
				result = append(result, dataName);
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
		
		Iterator fields = fFields.iterator();
		
		boolean done = false;
		
		while (fields.hasNext() && ! done)
		{
			FieldFormatter field = (FieldFormatter) fields.next();
			
			if (field.applyToRemainingFields())
			{
				if (selectedData instanceof Map)
				{
					Iterator entries = ((Map) selectedData).entrySet().iterator();
					
					while (entries.hasNext())
					{
						Map.Entry entry = (Map.Entry) entries.next();
						
						formatResults = field.format
							(entry, context, selectedTarget);
						
						if (formatResults != null)
						{
							result = append(result, formatResults);
						}
						
						entries.remove();
					}
					
					done = true;
				}
				else if (selectedData instanceof Collection)
				{
					Iterator elements = ((Collection) selectedData).iterator();
					
					while (elements.hasNext())
					{
						Object element = elements.next();
						
						formatResults = field.format
							(element, context, selectedTarget);
						
						if (formatResults != null)
						{
							result = append(result, formatResults);
						}
						
						elements.remove();
					}
					
					done = true;
				}
			}
			else
			{
				formatResults = field.format
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

		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataFormatter.
	 *
	 *  @return A String representation of this DataFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("RecordFormatter: ");
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		
		return (result.toString());
	}
}
