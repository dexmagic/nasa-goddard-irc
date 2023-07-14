//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: FieldParserDescriptor.java,v $
//  Revision 1.9  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.8  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.7  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.6  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.5  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
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

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A FieldParserDescriptor describes the parsing of a field of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class FieldParserDescriptor extends AbstractDataParserDescriptor
{
	private DataParseDescriptor fPrefix;
	private DataParseDescriptor fLabel;
	private DataParseDescriptor fLabelSeparator;
	private DataParseDescriptor fValue;
	private DataParseDescriptor fPostfix;
	
	private boolean fApplyToRemainingFields = false;
	private boolean fUseLabelAsParseKey = false;
	

	/**
	 * Constructs a new FieldParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new FieldParserDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		FieldParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		FieldParserDescriptor		
	**/
	
	public FieldParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given FieldParserDescriptor.
	 *
	 * @param descriptor The FieldParserDescriptor to be copied
	**/
	
	protected FieldParserDescriptor(FieldParserDescriptor descriptor)
	{
		super(descriptor);
		
		fPrefix = descriptor.fPrefix;
		fLabel = descriptor.fLabel;
		fLabelSeparator = descriptor.fLabelSeparator;
		fValue = descriptor.fValue;
		fPostfix = descriptor.fPostfix;
		
		fApplyToRemainingFields = descriptor.fApplyToRemainingFields;
		fUseLabelAsParseKey = descriptor.fUseLabelAsParseKey;
	}
	

	/**
	 * Returns a (deep) copy of this FieldParserDescriptor.
	 *
	 * @return A (deep) copy of this FieldParserDescriptor 
	**/
	
	public Object clone()
	{
		FieldParserDescriptor result = (FieldParserDescriptor) super.clone();
		
		result.fPrefix = (DataParseDescriptor) fPrefix.clone();
		result.fLabel = (DataParseDescriptor) fLabel.clone();
		result.fLabelSeparator = (DataParseDescriptor) fLabelSeparator.clone();
		result.fValue = (DataParseDescriptor) fValue.clone();
		result.fPostfix = (DataParseDescriptor) fPostfix.clone();
		
		result.fApplyToRemainingFields = fApplyToRemainingFields;
		result.fUseLabelAsParseKey = fUseLabelAsParseKey;
		
		return (result);
	}
	

	/**
	 * Sets the prefix DataParseDescriptor of this FieldParserDescriptor to 
	 * the given DataParseDescriptor.
	 *
	 * @param prefix The new prefix DataParseDescriptor of this 
	 * 		FieldParserDescriptor 
	**/
	
	void setPrefix(DataParseDescriptor prefix)
	{
		fPrefix = prefix;
	}
	

	/**
	 * Returns the prefix DataParseDescriptor of this FieldParserDescriptor 
	 * (if any).
	 *
	 * @return The prefix DataParseDescriptor of this FieldParserDescriptor 
	 * 		(if any)
	**/
	
	public DataParseDescriptor getPrefix()
	{
		return (fPrefix);
	}
	

	/**
	 * Sets the label DataParseDescriptor of this FieldParserDescriptor to 
	 * the given DataParseDescriptor.
	 *
	 * @param label The new label DataParseDescriptor of this 
	 * 		FieldParserDescriptor 
	**/
	
	void setLabel(DataParseDescriptor label)
	{
		fLabel = label;
	}
	

	/**
	 * Returns the label DataParseDescriptor associated with this 
	 * FieldParserDescriptor (if any).
	 *
	 * @return The label DataParseDescriptor associated with this 
	 * 		FieldParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getLabel()
	{
		return (fLabel);
	}
	

	/**
	 * Sets the label separator DataParseDescriptor of this 
	 * FieldParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param The new label separator DataParseDescriptor of this 
	 * 		FieldParserDescriptor 
	**/
	
	void setLabelSeparator(DataParseDescriptor separator)
	{
		fLabelSeparator = separator;
	}
	

	/**
	 * Returns the label separator DataParseDescriptor of this 
	 * FieldParserDescriptor (if any).
	 *
	 * @return The label separator DataParseDescriptor of this 
	 * 		FieldParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getLabelSeparator()
	{
		return (fLabelSeparator);
	}
	

	/**
	 * Sets the value DataParseDescriptor of this FieldParserDescriptor to the 
	 * given SimpleValueParserDescriptor.
	 *
	 * @param value The new value DataParseDescriptor of this 
	 * 		FieldParserDescriptor 
	**/
	
	void setValue(DataParseDescriptor value)
	{
		fValue = value;
	}
	

	/**
	 * Returns the value DataParseDescriptor of this FieldParserDescriptor.
	 *
	 * @return The value DataParseDescriptor of this FieldParserDescriptor 
	**/
	
	public DataParseDescriptor getValue()
	{
		return (fValue);
	}
	

	/**
	 * Sets the postfix DataParseDescriptor of this FieldParserDescriptor 
	 * to the given DataParseDescriptor.
	 *
	 * @param postfix The new postfix DataParseDescriptor of this 
	 * 		FieldParserDescriptor 
	**/
	
	void setPostfix(DataParseDescriptor postfix)
	{
		fPostfix = postfix;
	}
	

	/**
	 * Returns the postfix DataParseDescriptor associated with this 
	 * FieldParserDescriptor (if any).
	 *
	 * @return The postfix DataParseDescriptor associated with this 
	 * 		FieldParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getPostfix()
	{
		return (fPostfix);
	}
	

	/**
	 * Returns true if this FieldParserDescriptor is confiugured to be applied 
	 * to all the remaining fields in the input, false otherwise.
	 *
	 * @return True if this FieldParserDescriptor is confiugured to be applied 
	 * 		to all the remaining fields in the input, false otherwise
	**/
	
	public boolean applyToRemainingFields()
	{
		return (fApplyToRemainingFields);
	}
	

	/**
	 * Returns true if this FieldParserDescriptor is confiugured to use its label  
	 * as its parse key, false otherwise.
	 *
	 * @return True if this FieldParserDescriptor is confiugured to use its label 
	 * 		as its parse key, false otherwise
	**/
	
	public boolean usesLabelAsParseKey()
	{
		return (fUseLabelAsParseKey);
	}
	

	/** 
	 *  Returns a String representation of this FieldParserDescriptor.
	 *
	 *  @return A String representation of this FieldParserDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("FieldParserDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fPrefix != null)
		{
			result.append("\nPrefix: " + fPrefix);
		}

		if (fLabel != null)
		{
			result.append("\nLabel: " + fLabel);
		}

		if (fLabelSeparator != null)
		{
			result.append("\nLabel separator: " + fLabelSeparator);
		}

		if (fValue != null)
		{
			result.append("\nValue: " + fValue);
		}
		
		if (fPostfix != null)
		{
			result.append("\nPostfix: " + fPostfix);
		}
		
		if (fApplyToRemainingFields)
		{
			result.append("\nApplies to remaining fields");
		}
		
		if (fUseLabelAsParseKey)
		{
			result.append("\nUses label as parse key");
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this FieldParserDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the applyToRemainingFields attribute (if any).
		fApplyToRemainingFields = fSerializer.loadBooleanAttribute
			(Datatransml.A_APPLY_TO_REMAINING_FIELDS, false, fElement);
		
		if (! fApplyToRemainingFields)
		{
			fApplyToRemainingFields = fSerializer.loadBooleanAttribute
				(Datatransml.A_APPLY_TO_ALL_FIELDS, false, fElement);
		}
		
		// Unmarshall the useLabelAsParseKey attribute (if any).
		fUseLabelAsParseKey = fSerializer.loadBooleanAttribute
			(Datatransml.A_USE_LABEL_AS_PARSE_KEY, false, fElement);
		
		// Unmarshall the field prefix DataParseDescriptor (if any).
		fPrefix = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_PREFIX, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the field label DataParseDescriptor (if any).
		fLabel = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_LABEL, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the field label separator DataParseDescriptor (if any).
		fLabelSeparator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_LABEL_SEPARATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the field value DataParseDescriptor (if any).
		fValue = (DataParseDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Datatransml.E_VALUE, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		if ((fValue != null) && ((fValue.getName() == null) || 
			fValue.getName().equals(HasName.DEFAULT_NAME)))
		{
			fValue.setName(getName());
		}
		
		// Unmarshall the field postfix DataParseDescriptor (if any).
		fPostfix = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_POSTFIX, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
	}
}
