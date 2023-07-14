//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RecordParserDescriptor.java,v $
//  Revision 1.8  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.7  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.6  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.5  2006/02/02 17:04:50  chostetter_cvs
//  Added support for Sequencer in Records
//
//  Revision 1.4  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.3  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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
 * A RecordParserDescriptor describes the parsing of data as a record.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class RecordParserDescriptor extends AbstractDataParserDescriptor
{
	private DataParseDescriptor fSequencer;
	private DataParseDescriptor fSequencerSeparator;
	
	private DataParseDescriptor fInitiator;
	private DataParseDescriptor fInitiatorSeparator;
	
	private FieldParserDescriptor fFieldDefaults;
	private Map fFieldsByName = new LinkedHashMap();
	
	private DataParseDescriptor fTerminator;
	
	private boolean fUseInitiatorAsParseName = false;
	private boolean fSuppressFirstPrefix = false;
	private boolean fSuppressLastPostfix = false;

	
	/**
	 * Constructs a new RecordParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new RecordParserDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		RecordParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		RecordParserDescriptor		
	**/
	
	public RecordParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given RecordParserDescriptor.
	 *
	 * @param descriptor The RecordParserDescriptor to be copied
	**/
	
	protected RecordParserDescriptor(RecordParserDescriptor descriptor)
	{
		super(descriptor);
		
		fSequencer = descriptor.fSequencer;
		fSequencerSeparator = descriptor.fSequencerSeparator;
		fInitiator = descriptor.fInitiator;
		fInitiatorSeparator = descriptor.fInitiatorSeparator;
		fFieldDefaults = descriptor.fFieldDefaults;
		fFieldsByName = descriptor.fFieldsByName;
		fTerminator = descriptor.fTerminator;
		
		fUseInitiatorAsParseName = descriptor.fUseInitiatorAsParseName;
		fSuppressFirstPrefix = descriptor.fSuppressFirstPrefix;
		fSuppressLastPostfix = descriptor.fSuppressLastPostfix;
	}
	

	/**
	 * Returns a (deep) copy of this RecordParserDescriptor.
	 *
	 * @return A (deep) copy of this RecordParserDescriptor 
	**/
	
	public Object clone()
	{
		RecordParserDescriptor result = (RecordParserDescriptor) super.clone();
		
		result.fSequencer = (DataParseDescriptor) fSequencer.clone();
		result.fSequencerSeparator = (DataParseDescriptor) 
			fSequencerSeparator.clone();
		result.fInitiator = (DataParseDescriptor) fInitiator.clone();
		result.fInitiatorSeparator = (DataParseDescriptor) 
			fInitiatorSeparator.clone();
		result.fFieldDefaults = (FieldParserDescriptor) fFieldDefaults.clone();
		result.fFieldsByName = new LinkedHashMap(fFieldsByName);
		result.fTerminator = (DataParseDescriptor) fTerminator.clone();
		
		result.fUseInitiatorAsParseName = fUseInitiatorAsParseName;
		result.fSuppressFirstPrefix = fSuppressFirstPrefix;
		result.fSuppressLastPostfix = fSuppressLastPostfix;
		
		return (result);
	}
	

	/**
	 * Sets the record sequencer DataParseDescriptor of this 
	 * RecordFormatterDescriptor to the given DataParseDescriptor.
	 *
	 * @param sequencer The record sequencer DataParseDescriptor of this 
	 * 		RecordFormatterDescriptor 
	**/
	
	public void setSequencer(DataParseDescriptor sequencer)
	{
		fSequencer = sequencer;
	}
	

	/**
	 * Returns the record sequencer DataParseDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record sequencer DataParseDescriptor of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public DataParseDescriptor getSequencer()
	{
		return (fSequencer);
	}
	

	/**
	 * Sets the record sequencer separtor DataParseDescriptor of this 
	 * RecordFormatterDescriptor to the given DataParseDescriptor.
	 *
	 * @param separator The record sequencer separator DataParseDescriptor 
	 * 		of this RecordFormatterDescriptor 
	**/
	
	public void setSequencerSeparator(DataParseDescriptor separator)
	{
		fSequencerSeparator = separator;
	}
	

	/**
	 * Returns the record sequencer separator DataParseDescriptor of this 
	 * RecordFormatterDescriptor (if any).
	 *
	 * @return The record initiator sequencer DataParseDescriptor of this 
	 * 		RecordFormatterDescriptor (if any)
	**/
	
	public DataParseDescriptor getSequencerSeparator()
	{
		return (fSequencerSeparator);
	}
	

	/**
	 * Sets the record initiator DataParseDescriptor of this 
	 * RecordParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param initiator The record initiator DataParseDescriptor of this 
	 * 		RecordParserDescriptor 
	**/
	
	public void setInitiator(DataParseDescriptor initiator)
	{
		fInitiator = initiator;
	}
	

	/**
	 * Returns the record initiator DataParseDescriptor of this 
	 * RecordParserDescriptor (if any).
	 *
	 * @return The record initiator DataParseDescriptor of this 
	 * 		RecordParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getInitiator()
	{
		return (fInitiator);
	}
	

	/**
	 * Sets the record initiator separtor DataParseDescriptor of this 
	 * RecordParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param separator The record initiator separator DataParseDescriptor 
	 * 		of this RecordParserDescriptor 
	**/
	
	public void setInitiatorSeparator(DataParseDescriptor separator)
	{
		fInitiatorSeparator = separator;
	}
	

	/**
	 * Returns the record initiator separator DataParseDescriptor of this 
	 * RecordParserDescriptor (if any).
	 *
	 * @return The record initiator separator DataParseDescriptor of this 
	 * 		RecordParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getInitiatorSeparator()
	{
		return (fInitiatorSeparator);
	}
	

	/**
	 * Returns the set of FieldParseDescriptors of this 
	 * RecordParserDescriptor (if any).
	 *
	 * @return The List of FieldParseDescriptors of this 
	 * 		RecordParserDescriptor (if any)
	**/
	
	public Collection getFields()
	{
		return (Collections.unmodifiableCollection(fFieldsByName.values()));
	}
	

	/**
	 * Sets the record terminator DataParseDescriptor of this 
	 * RecordParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param terminator The record terminator DataParseDescriptor of this 
	 * 		RecordParserDescriptor 
	**/
	
	public void setTerminator(DataParseDescriptor terminator)
	{
		fTerminator = terminator;
	}
	

	/**
	 * Returns the record terminator DataParseDescriptor of this 
	 * RecordParserDescriptor (if any).
	 *
	 * @return The record terminator DataParseDescriptor of this 
	 * RecordParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getTerminator()
	{
		return (fTerminator);
	}
	

	/**
	 * Returns true if this RecordParserDescriptor is configured to use its 
	 * initiator as the name of the resulting parse, false otherwise.
	 *
	 * @return True if this RecordParserDescriptor is configured to use its 
	 * 		initiator as the name of the resulting parse, false otherwise
	**/
	
	public boolean usesInitiatorAsParseName()
	{
		return (fUseInitiatorAsParseName);
	}
	

	/**
	 * Returns true if this RecordParserDescriptor is configured to suppress the 
	 * first field's prefix, false otherwise.
	 *
	 * @return True if this RecordParserDescriptor is configured to suppress the 
	 * 		first field's prefix, false otherwise
	**/
	
	public boolean suppressesFirstPrefix()
	{
		return (fSuppressFirstPrefix);
	}
	

	/**
	 * Returns true if this RecordParserDescriptor is configured to suppress the 
	 * last field's postfix, false otherwise.
	 *
	 * @return True if this RecordParserDescriptor is configured to suppress the 
	 * 		last field's postfix, false otherwise
	**/
	
	public boolean suppressesLastPostfix()
	{
		return (fSuppressLastPostfix);
	}
	

	/** 
	 *  Returns a String representation of this RecordParserDescriptor.
	 *
	 *  @return A String representation of this RecordParserDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("RecordParserDescriptor:");
		
		result.append("\n" + super.toString());
				
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
				FieldParserDescriptor parse = (FieldParserDescriptor) 
					fields.next();
				
				result.append("\n" + i + ": " + parse);
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
	  * Unmarshalls this RecordParserDescriptor from its associated JDOM ELement. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the record sequencer DataParseDescriptor (if any).
		fSequencer = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_SEQUENCER, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the record sequencer separator DataParseDescriptor 
		// (if any).
		fSequencerSeparator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_SEQUENCER_SEPARATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
				
		// Unmarshall the record initiator DataParseDescriptor (if any).
		fInitiator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the record initiator separator DataParseDescriptor 
		// (if any).
		fInitiatorSeparator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR_SEPARATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
				
		// Unmarshall the suppressFirstPrefix attribute (if any).
		fSuppressFirstPrefix = fSerializer.loadBooleanAttribute
			(Datatransml.A_SUPPRESS_FIRST_PREFIX, false, fElement);

		// Unmarshall the suppressLastPostfix attribute (if any).
		fSuppressLastPostfix = fSerializer.loadBooleanAttribute
			(Datatransml.A_SUPPRESS_LAST_POSTFIX, false, fElement);
		
		// Unmarshall the default FieldParserDescriptor (if any)
		fFieldDefaults = (FieldParserDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_FIELD_DEFAULTS, 
				Datatransml.C_FIELD_PARSER, fElement, this, fDirectory);
		
		DataParseDescriptor defaultPrefix = null;
		DataParseDescriptor defaultLabel = null;
		DataParseDescriptor defaultLabelSeparator = null;
		DataParseDescriptor defaultValue = null;
		DataParseDescriptor defaultPostfix = null;
		
		boolean useFieldNameAsValueName = false;
		
		// Unmarshall the FieldParseDescriptors (if any).
		fFieldsByName.clear();
		
		fSerializer.loadChildDescriptorElements(Datatransml.E_FIELD, fFieldsByName, 
			Datatransml.C_FIELD_PARSER, fElement, this, fDirectory);
		
		// Set the FieldParserDescriptor defaults (if any)
		
		if (fFieldDefaults != null)
		{
			defaultPrefix = fFieldDefaults.getPrefix();
			defaultLabel = fFieldDefaults.getLabel();
			defaultLabelSeparator = fFieldDefaults.getLabelSeparator();
			defaultValue = fFieldDefaults.getValue();
			defaultPostfix = fFieldDefaults.getPostfix();
			
			Iterator fields = fFieldsByName.values().iterator();
			
			boolean isFirst = true;
			
			while (fields.hasNext())
			{
				FieldParserDescriptor field = (FieldParserDescriptor) fields.next();
				
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
				
				if ((defaultLabel != null) && (field.getLabel() == null))
				{
					field.setLabel(defaultLabel);
				}
				
				if ((defaultLabelSeparator != null) && 
					(field.getLabelSeparator() == null))
				{
					field.setLabelSeparator(defaultLabelSeparator);
				}
				
				if ((defaultValue != null) && (field.getValue() == null))
				{
					DataParseDescriptor value = 
						(DataParseDescriptor) defaultValue.clone();
					
					if (useFieldNameAsValueName)
					{
						value.setName(field.getName());
					}
					
					field.setValue(value);
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
		
		// Unmarshall the record terminator DataParseDescriptor (if any).
		fTerminator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_TERMINATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the useInitiatorAsParseName attribute (if any).
		fUseInitiatorAsParseName = fSerializer.loadBooleanAttribute
			(Datatransml.A_USE_INITIATOR_AS_PARSE_NAME, false, fElement);
	}
}
