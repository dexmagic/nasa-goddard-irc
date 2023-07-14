//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: FieldFormatterDescriptor.java,v $
//  Revision 1.7  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.6  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.5  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.4  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.3  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
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

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A FieldFormatterDescriptor describes the parsing of a field of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class FieldFormatterDescriptor extends AbstractDataFormatterDescriptor
{
	private DataFormatDescriptor fPrefix;
	private DataFormatDescriptor fLabel;
	private DataFormatDescriptor fLabelSeparator;
	private DataFormatDescriptor fValue;
	private DataFormatDescriptor fPostfix;
	
	private boolean fApplyToRemainingFields = false;
	private boolean fUseDataNameAsLabel = false;
	private boolean fUseNameAsLabel = false;
	

	/**
	 * Constructs a new FieldFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new FieldFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		FieldFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		FieldFormatterDescriptor		
	**/
	
	public FieldFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given FieldFormatterDescriptor.
	 *
	 * @param descriptor The FieldFormatterDescriptor to be copied
	**/
	
	protected FieldFormatterDescriptor(FieldFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fPrefix = descriptor.fPrefix;
		fLabel = descriptor.fLabel;
		fLabelSeparator = descriptor.fLabelSeparator;
		fValue = descriptor.fValue;
		fPostfix = descriptor.fPostfix;
		
		fApplyToRemainingFields = descriptor.fApplyToRemainingFields;
		fUseDataNameAsLabel = descriptor.fUseDataNameAsLabel;
		fUseNameAsLabel = descriptor.fUseNameAsLabel;
	}
	

	/**
	 * Returns a (deep) copy of this FieldFormatterDescriptor.
	 *
	 * @return A (deep) copy of this FieldFormatterDescriptor 
	**/
	
	public Object clone()
	{
		FieldFormatterDescriptor result = (FieldFormatterDescriptor) super.clone();
		
		result.fPrefix = (DataFormatDescriptor) fPrefix.clone();
		result.fLabel = (DataFormatDescriptor) fLabel.clone();
		result.fLabelSeparator = (DataFormatDescriptor) fLabelSeparator.clone();
		result.fValue = (DataFormatDescriptor) fValue.clone();
		result.fPostfix = (DataFormatDescriptor) fPostfix.clone();
		
		result.fApplyToRemainingFields = fApplyToRemainingFields;
		result.fUseDataNameAsLabel = fUseDataNameAsLabel;
		result.fUseNameAsLabel = fUseNameAsLabel;
		
		return (result);
	}
	

	/**
	 * Configures this FieldFormatterDescriptor to use the name of the data it 
	 * formats as its label.
	 *
	**/
	
	void useDataNameAsLabel()
	{
		fUseDataNameAsLabel = true;
	}
	

	/**
	 * Returns true if this FieldFormatterDescriptor is configured to use the name 
	 * of the data it formats as its label, false otherwise.
	 *
	 * @return True if this FieldFormatterDescriptor is configured to use the name 
	 * 		of the data it formats as its label, false otherwise
	**/
	
	public boolean usesDataNameAsLabel()
	{
		return (fUseDataNameAsLabel);
	}
	

	/**
	 * Configures this FieldFormatterDescriptor to use its name as its label.
	 *
	**/
	
	void useNameAsLabel()
	{
		fUseNameAsLabel = true;
	}
	

	/**
	 * Returns true if this FieldFormatterDescriptor is configured to use its name 
	 * as its label, false otherwise.
	 *
	 * @return True if this FieldFormatterDescriptor is configured to use its name 
	 * 		as its label, false otherwise
	**/
	
	public boolean usesNameAsLabel()
	{
		return (fUseNameAsLabel);
	}
	

	/**
	 * Returns true if this FieldFormatterDescriptor is confiugured to be applied to 
	 * all the remaining fields in the input, false otherwise.
	 *
	 * @return True if this FieldFormatterDescriptor is confiugured to be applied to 
	 * 		all the remaining fields in the input, false otherwise
	**/
	
	public boolean applyToRemainingFields()
	{
		return (fApplyToRemainingFields);
	}
	

	/**
	 * Sets the prefix DataFormatDescriptor of this FieldFormatterDescriptor to 
	 * the given DataFormatDescriptor.
	 *
	 * @param prefix The new prefix DataFormatDescriptor of this 
	 * 		FieldFormatterDescriptor 
	**/
	
	void setPrefix(DataFormatDescriptor prefix)
	{
		fPrefix = prefix;
	}
	

	/**
	 * Returns the prefix DataFormatDescriptor of this FieldFormatterDescriptor 
	 * (if any).
	 *
	 * @return The prefix DataFormatDescriptor of this FieldFormatterDescriptor 
	 * 		(if any)
	**/
	
	public DataFormatDescriptor getPrefix()
	{
		return (fPrefix);
	}
	

	/**
	 * Sets the label DataFormatDescriptor of this FieldFormatterDescriptor to 
	 * the given DataFormatDescriptor.
	 *
	 * @param label The new label DataFormatDescriptor of this 
	 * 		FieldFormatterDescriptor 
	**/
	
	void setLabel(DataFormatDescriptor label)
	{
		fLabel = label;
	}
	

	/**
	 * Returns the label DataFormatDescriptor associated with this 
	 * FieldFormatterDescriptor (if any).
	 *
	 * @return The label DataFormatDescriptor associated with this 
	 * 		FieldFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getLabel()
	{
		return (fLabel);
	}
	

	/**
	 * Sets the label separator DataFormatDescriptor of this 
	 * FieldFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param The new label separator DataFormatDescriptor of this 
	 * 		FieldFormatterDescriptor 
	**/
	
	void setLabelSeparator(DataFormatDescriptor separator)
	{
		fLabelSeparator = separator;
	}
	

	/**
	 * Returns the label separator DataFormatDescriptor of this 
	 * FieldFormatterDescriptor (if any).
	 *
	 * @return The label separator DataFormatDescriptor of this 
	 * 		FieldFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getLabelSeparator()
	{
		return (fLabelSeparator);
	}
	

	/**
	 * Sets the value DataFormatDescriptor of this FieldFormatterDescriptor to the 
	 * given DataFormatDescriptor.
	 *
	 * @param The new value DataFormatDescriptor of this FieldFormatterDescriptor 
	**/
	
	void setValue(DataFormatDescriptor format)
	{
		fValue = format;
	}
	

	/**
	 * Returns the value DataFormatDescriptor of this FieldFormatterDescriptor.
	 *
	 * @return The value DataFormatDescriptor of this FieldFormatterDescriptor 
	**/
	
	public DataFormatDescriptor getValue()
	{
		return (fValue);
	}
	

	/**
	 * Sets the postfix DataFormatDescriptor of this FieldFormatterDescriptor 
	 * to the given DataFormatDescriptor.
	 *
	 * @param postfix The new postfix DataFormatDescriptor of this 
	 * 		FieldFormatterDescriptor 
	**/
	
	void setPostfix(DataFormatDescriptor postfix)
	{
		fPostfix = postfix;
	}
	

	/**
	 * Returns the postfix DataFormatDescriptor associated with this 
	 * FieldFormatterDescriptor (if any).
	 *
	 * @return The postfix DataFormatDescriptor associated with this 
	 * 		FieldFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getPostfix()
	{
		return (fPostfix);
	}
	

	/** 
	 *  Returns a String representation of this FieldFormatterDescriptor.
	 *
	 *  @return A String representation of this FieldFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("FieldFormatterDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fUseDataNameAsLabel)
		{
			result.append("\nUses data name as label");
		}

		if (fUseNameAsLabel)
		{
			result.append("\nUses name as label");
		}

		if (fApplyToRemainingFields)
		{
			result.append("\nApplies to remaining fields");
		}
		
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
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this FieldFormatterDescriptor from its associated JDOM 
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
		
		if (fApplyToRemainingFields)
		{
			selectAllData();
		}
		
		// Unmarshall the useDataKeyAsLabel attribute (if any).
		fUseNameAsLabel = fSerializer.loadBooleanAttribute
			(Datatransml.A_USE_NAME_AS_LABEL, false, fElement);
		
		// Unmarshall the useDataKeyAsLabel attribute (if any).
		fUseDataNameAsLabel = fSerializer.loadBooleanAttribute
			(Datatransml.A_USE_DATA_NAME_AS_LABEL, false, fElement);
		
		// Unmarshall the field prefix DataFormatDescriptor (if any).
		fPrefix = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_PREFIX, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the field label DataFormatDescriptor (if any).
		fLabel = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_LABEL, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the field label separator DataFormatDescriptor (if any).
		fLabelSeparator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_LABEL_SEPARATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the field value DataFormatDescriptor (if any).
		fValue = (DataFormatDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Datatransml.E_VALUE, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the field postfix DataFormatDescriptor (if any).
		fPostfix = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_POSTFIX, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
	}
}
