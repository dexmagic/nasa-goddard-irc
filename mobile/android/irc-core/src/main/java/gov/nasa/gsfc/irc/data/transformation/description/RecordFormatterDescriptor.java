//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RecordFormatterDescriptor.java,v $
//  Revision 1.10  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.9  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.8  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.7  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.6  2006/04/07 17:55:05  chostetter_cvs
//  Fixed first/last field prefix/postfix suppression
//
//  Revision 1.5  2006/02/02 17:04:50  chostetter_cvs
//  Added support for Sequencer in Records
//
//  Revision 1.4  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.3  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.2  2005/09/13 13:26:10  chostetter_cvs
//  Fixes for HAWC command formatting
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
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

package gov.nasa.gsfc.irc.data.transformation.description;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A RecordFormatterDescriptor describes the parsing of data as a record.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class RecordFormatterDescriptor extends AbstractDataFormatterDescriptor
{
	private DataFormatDescriptor fSequencer;
	private DataFormatDescriptor fSequencerSeparator;
	
	private DataFormatDescriptor fInitiator;
	private DataFormatDescriptor fInitiatorSeparator;
	
	private FieldFormatterDescriptor fFieldDefaults;
	private Map fFieldsByName = new LinkedHashMap();
	
	private DataFormatDescriptor fTerminator;
	
	private boolean fSuppressFirstPrefix = false;
	private boolean fSuppressLastPostfix = false;
	
	private boolean fUseDataNameAsInitiator = false;
	
	
	/**
	 * Constructs a new RecordFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new RecordFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		RecordFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		RecordFormatterDescriptor		
	**/
	
	public RecordFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given RecordFormatterDescriptor.
	 *
	 * @param descriptor The RecordFormatterDescriptor to be copied
	**/
	
	protected RecordFormatterDescriptor(RecordFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fSequencer = descriptor.fSequencer;
		fSequencerSeparator = descriptor.fSequencerSeparator;
		fInitiator = descriptor.fInitiator;
		fInitiatorSeparator = descriptor.fInitiatorSeparator;
		fFieldDefaults = descriptor.fFieldDefaults;
		fFieldsByName = descriptor.fFieldsByName;
		fTerminator = descriptor.fTerminator;
		
		fSuppressFirstPrefix = descriptor.fSuppressFirstPrefix;
		fSuppressLastPostfix = descriptor.fSuppressLastPostfix;
		fUseDataNameAsInitiator = descriptor.fUseDataNameAsInitiator;
	}
	

	/**
	 * Returns a (deep) copy of this RecordFormatterDescriptor.
	 *
	 * @return A (deep) copy of this RecordFormatterDescriptor 
	**/
	
	public Object clone()
	{
		RecordFormatterDescriptor result = (RecordFormatterDescriptor) super.clone();
		
		result.fSequencer = (DataFormatDescriptor) fSequencer.clone();
		result.fSequencerSeparator = (DataFormatDescriptor) 
			fSequencerSeparator.clone();
		result.fInitiator = (DataFormatDescriptor) fInitiator.clone();
		result.fInitiatorSeparator = (DataFormatDescriptor) 
			fInitiatorSeparator.clone();
		result.fFieldDefaults = (FieldFormatterDescriptor) fFieldDefaults.clone();
		result.fFieldsByName = new LinkedHashMap(fFieldsByName);
		result.fTerminator = (DataFormatDescriptor) fTerminator.clone();
		
		result.fSuppressFirstPrefix = fSuppressFirstPrefix;
		result.fSuppressLastPostfix = fSuppressLastPostfix;
		result.fUseDataNameAsInitiator = fUseDataNameAsInitiator;
		
		return (result);
	}
	

	/**
	 * Sets the record sequencer DataFormatDescriptor of this 
	 * RecordFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param sequencer The record sequencer DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor 
	**/
	
	public void setSequencer(DataFormatDescriptor sequencer)
	{
		fSequencer = sequencer;
	}
	

	/**
	 * Returns the record sequencer DataFormatDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record sequencer DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getSequencer()
	{
		return (fSequencer);
	}
	

	/**
	 * Sets the record sequencer separator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param separator The record sequencer separator DataFormatDescriptor 
	 * 		of this RecordFormatterDescriptor 
	**/
	
	public void setSequencerSeparator(DataFormatDescriptor separator)
	{
		fSequencerSeparator = separator;
	}
	

	/**
	 * Returns the record sequencer separator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record initiator sequencer DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getSequencerSeparator()
	{
		return (fSequencerSeparator);
	}
	

	/**
	 * Sets the record initiator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param initiator The record initiator DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor 
	**/
	
	public void setInitiator(DataFormatDescriptor initiator)
	{
		fInitiator = initiator;
	}
	

	/**
	 * Returns the record initiator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record initiator DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getInitiator()
	{
		return (fInitiator);
	}
	

	/**
	 * Sets the record initiator separtor DataFormatDescriptor of this 
	 * RecordFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param separator The record initiator separator DataFormatDescriptor 
	 * 		of this RecordFormatterDescriptor 
	**/
	
	public void setInitiatorSeparator(DataFormatDescriptor separator)
	{
		fInitiatorSeparator = separator;
	}
	

	/**
	 * Returns the record initiator separator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record initiator separator DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getInitiatorSeparator()
	{
		return (fInitiatorSeparator);
	}
	

	/**
	 * Returns true if this RecordFormatterDescriptor is configured to use the name 
	 * of selected data as its initiator, false otherwise.
	 *
	 * @return True if this RecordFormatterDescriptor is configured to use the name 
	 * 		of selected data as its initiator, false otherwise
	**/
	
	public boolean usesDataNameAsInitiator()
	{
		return (fUseDataNameAsInitiator);
	}
	

	/**
	 * Returns the set of FieldFormatDescriptors of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The List of FieldFormatDescriptors of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public Collection getFields()
	{
		return (Collections.unmodifiableCollection(fFieldsByName.values()));
	}
	

	/**
	 * Sets the record terminator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param terminator The record terminator DataFormatDescriptor of this 
	 * 		RecordFormatterDescriptor 
	**/
	
	public void setTerminator(DataFormatDescriptor terminator)
	{
		fTerminator = terminator;
	}
	

	/**
	 * Returns the record terminator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record terminator DataFormatDescriptor of this 
	 * RecordFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getTerminator()
	{
		return (fTerminator);
	}
	

	/**
	 * Returns true if this RecordFormatterDescriptor is configured to suppress the 
	 * first field's prefix, false otherwise.
	 *
	 * @return True if this RecordFormatterDescriptor is configured to suppress the 
	 * 		first field's prefix, false otherwise
	**/
	
	public boolean suppressesFirstPrefix()
	{
		return (fSuppressFirstPrefix);
	}
	

	/**
	 * Returns true if this RecordFormatterDescriptor is configured to suppress the 
	 * last field's postfix, false otherwise.
	 *
	 * @return True if this RecordFormatterDescriptor is configured to suppress the 
	 * 		last field's postfix, false otherwise
	**/
	
	public boolean suppressesLastPostfix()
	{
		return (fSuppressLastPostfix);
	}
	

	/** 
	 *  Returns a String representation of this RecordFormatterDescriptor.
	 *
	 *  @return A String representation of this RecordFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("RecordFormatterDescriptor:");
		
		result.append("\n" + super.toString());
		
		if (fUseDataNameAsInitiator)
		{
			result.append("\nUses data name as initiator");
		}
				
		if (fSuppressFirstPrefix)
		{
			result.append("\nSuppresses first prefix");
		}
				
		if (fSuppressLastPostfix)
		{
			result.append("\nSuppresses last postfix");
		}
				
		if (fInitiator != null)
		{
			result.append("\nInitiator: " + fInitiator);
		}

		if (fInitiatorSeparator != null)
		{
			result.append("\nInitiator separator: " + fInitiatorSeparator);
		}

		if (fFieldsByName != null)
		{
			result.append("\nFields: ");
			
			Iterator fields = fFieldsByName.values().iterator();
			
			for (int i = 1; fields.hasNext(); i++)
			{
				FieldFormatterDescriptor format = (FieldFormatterDescriptor) 
					fields.next();
				
				result.append("\n" + i + ": " + format);
			}
		}
		else
		{
			result.append("\nHas no fields defined");
		}
		
		if (fTerminator != null)
		{
			result.append("\nTerminator: " + fTerminator);
		}
		
		return (result.toString());
	}
	

	/**
	  * Unmarshalls this RecordFormatterDescriptor from its associated JDOM ELement. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the useDataNameAsInitiator attribute (if any).
		fUseDataNameAsInitiator = fSerializer.loadBooleanAttribute
			(Datatransml.A_USE_DATA_NAME_AS_INITIATOR, false, fElement);

		// Unmarshall the record sequencer DataFormatDescriptor (if any).
		fSequencer = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_SEQUENCER, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the record sequencer separator DataFormatDescriptor 
		// (if any).
		fSequencerSeparator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_SEQUENCER_SEPARATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the record initiator DataFormatDescriptor (if any).
		fInitiator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the record initiator separator DataFormatDescriptor 
		// (if any).
		fInitiatorSeparator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR_SEPARATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the suppressFirstPrefix attribute (if any).
		fSuppressFirstPrefix = fSerializer.loadBooleanAttribute
			(Datatransml.A_SUPPRESS_FIRST_PREFIX, false, fElement);

		// Unmarshall the suppressLastPostfix attribute (if any).
		fSuppressLastPostfix = fSerializer.loadBooleanAttribute
			(Datatransml.A_SUPPRESS_LAST_POSTFIX, false, fElement);
		
		// Unmarshall the default FieldFormatterDescriptor (if any)
		fFieldDefaults = (FieldFormatterDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_FIELD_DEFAULTS, 
				Datatransml.C_FIELD_FORMATTER, fElement, this, fDirectory);
		
		// Unmarshall the FieldFormatDescriptors (if any).
		fFieldsByName.clear();
		
		fSerializer.loadChildDescriptorElements(Datatransml.E_FIELD, fFieldsByName, 
			Datatransml.C_FIELD_FORMATTER, fElement, this, fDirectory);
		
		// Set the FieldFormatterDescriptor defaults (if any)
		
		if (fFieldDefaults != null)
		{
			if (fFieldsByName.size() == 0)
			{
				String defaultFieldName = fFieldDefaults.getName();
				
				if (defaultFieldName == null)
				{
					defaultFieldName = "Anonymous";
				}
				
				fFieldsByName.put(defaultFieldName, fFieldDefaults);
			}
			else
			{
				DataFormatDescriptor defaultPrefix = fFieldDefaults.getPrefix();
				DataFormatDescriptor defaultLabel = fFieldDefaults.getLabel();
				DataFormatDescriptor defaultLabelSeparator = 
					fFieldDefaults.getLabelSeparator();
				DataFormatDescriptor defaultValue = fFieldDefaults.getValue();
				DataFormatDescriptor defaultPostfix = fFieldDefaults.getPostfix();
				boolean usesDataNameAsName = fFieldDefaults.usesDataNameAsName();
				boolean usesNameAsKeyedValueSelector = 
					fFieldDefaults.usesNameAsKeyedValueSelector();
				boolean usesNameAsLabel = fFieldDefaults.usesNameAsLabel();
			
				Iterator fields = fFieldsByName.values().iterator();
				
				boolean isFirst = true;
				
				while (fields.hasNext())
				{
					FieldFormatterDescriptor field = (FieldFormatterDescriptor) 
						fields.next();
					
					if (usesDataNameAsName)
					{
						field.useDataNameAsName();
					}
					
					if (usesNameAsKeyedValueSelector && 
						! field.overridesUseOfNameAsKeyedValueSelector() && 
						! field.applyToRemainingFields() && 
						(field.getSource() == null))
					{
						field.useNameAsKeyedValueSelector();
					}
					
					if ((defaultPrefix != null) && (field.getPrefix() == null))
					{
						field.setPrefix(defaultPrefix);
						
						if (isFirst)
						{
							if (fSuppressFirstPrefix)
							{
								field.setPrefix(null);
							}
							
							isFirst = false;
						}
					}
					
					if (field.getLabel() == null)
					{
						if (usesNameAsLabel)
						{
							field.useNameAsLabel();
						}
						else if (defaultLabel != null)
						{
							field.setLabel(defaultLabel);
						}
					}
					
					if ((defaultLabelSeparator != null) && 
						(field.getLabelSeparator() == null))
					{
						field.setLabelSeparator(defaultLabelSeparator);
					}
					
					if ((defaultValue != null) && (field.getValue() == null))
					{
						field.setValue(defaultValue);
					}
					
					if ((defaultPostfix != null) && (field.getPostfix() == null))
					{
						field.setPostfix(defaultPostfix);
						
						if (! fields.hasNext())
						{
							if (fSuppressLastPostfix)
							{
								field.setPostfix(null);
							}
						}
					}
				}
			}
		}
		
		// Unmarshall the record terminator DataFormatDescriptor (if any).
		fTerminator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_TERMINATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
	}
}
