//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ListValueFormatterDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.transformation.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A ListValueFormatterDescriptor describes the formatting of a List of data values.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class ListValueFormatterDescriptor 
	extends AbstractDataFormatterDescriptor implements ValueFormatterDescriptor
{
	private DataFormatDescriptor fInitiator;
	private DataFormatDescriptor fInitiatorSeparator;
	private DataFormatDescriptor fPrefix;
	private DataFormatDescriptor fValue;
	private DataFormatDescriptor fPostfix;
	private DataFormatDescriptor fTerminator;
	
	private int fLength = 0;
	private boolean fSuppressFirstPrefix = false;
	private boolean fSuppressLastPostfix = false;

	
	/**
	 * Constructs a new ListValueFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ListValueFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ListValueFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ListValueFormatterDescriptor		
	**/
	
	public ListValueFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given ListValueFormatterDescriptor.
	 *
	 * @param descriptor The ListValueFormatterDescriptor to be copied
	**/
	
	protected ListValueFormatterDescriptor(ListValueFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fInitiator = descriptor.fInitiator;
		fInitiatorSeparator = descriptor.fInitiatorSeparator;
		fPrefix = descriptor.fPrefix;
		fValue = descriptor.fValue;
		fPostfix = descriptor.fPostfix;
		fTerminator = descriptor.fTerminator;
		
		fLength = descriptor.fLength;
		fSuppressFirstPrefix = descriptor.fSuppressFirstPrefix;
		fSuppressLastPostfix = descriptor.fSuppressLastPostfix;
	}
	

	/**
	 * Returns a (deep) copy of this ListValueFormatterDescriptor.
	 *
	 * @return A (deep) copy of this ListValueFormatterDescriptor 
	**/
	
	public Object clone()
	{
		ListValueFormatterDescriptor result = (ListValueFormatterDescriptor) super.clone();
		
		result.fInitiator = (DataFormatDescriptor) fInitiator.clone();
		result.fInitiatorSeparator = (DataFormatDescriptor) 
			fInitiatorSeparator.clone();
		result.fPrefix = (DataFormatDescriptor) fPrefix.clone();
		result.fValue = (DataFormatDescriptor) fValue.clone();
		result.fPostfix = (DataFormatDescriptor) fPostfix.clone();
		result.fTerminator = (DataFormatDescriptor) fTerminator.clone();
		
		result.fLength = fLength;
		result.fSuppressFirstPrefix = fSuppressFirstPrefix;
		result.fSuppressLastPostfix = fSuppressLastPostfix;
		
		return (result);
	}
	

	/**
	 * Sets the list initiator DataFormatDescriptor of this 
	 * ListValueFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param initiator The list initiator DataFormatDescriptor of this 
	 * 		ListValueFormatterDescriptor 
	**/
	
	void setInitiator(DataFormatDescriptor initiator)
	{
		fInitiator = initiator;
	}
	

	/**
	 * Returns the list initiator DataFormatDescriptor of this 
	 * ListValueFormatterDescriptor (if any).
	 *
	 * @return The list initiator DataFormatDescriptor of this 
	 * 		ListValueFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getInitiator()
	{
		return (fInitiator);
	}
	

	/**
	 * Sets the list initiator separtor DataFormatDescriptor of this 
	 * ListValueFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param separator The list initiator separator DataFormatDescriptor 
	 * 		of this ListValueFormatterDescriptor 
	**/
	
	void setInitiatorSeparator(DataFormatDescriptor separator)
	{
		fInitiatorSeparator = separator;
	}
	

	/**
	 * Returns the list initiator separator DataFormatDescriptor of this 
	 * ListValueFormatterDescriptor (if any).
	 *
	 * @return The list initiator separator DataFormatDescriptor of this 
	 * 		ListValueFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getInitiatorSeparator()
	{
		return (fInitiatorSeparator);
	}
	

	/**
	 * Sets the value prefix DataFormatDescriptor of this 
	 * ListElementFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param prefix The new value prefix DataFormatDescriptor of this 
	 * 		ListElementFormatterDescriptor 
	**/
	
	void setPrefix(DataFormatDescriptor prefix)
	{
		fPrefix = prefix;
	}
	

	/**
	 * Returns the value prefix DataFormatDescriptor of this 
	 * ListElementFormatterDescriptor (if any).
	 *
	 * @return The value prefix DataFormatDescriptor of this 
	 * 		ListElementFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getPrefix()
	{
		return (fPrefix);
	}
	

	/**
	 * Returns the value DataFormatDescriptor of this ListValueFormatterDescriptor 
	 * (if any).
	 *
	 * @return The value DataFormatDescriptor of this ListValueFormatterDescriptor 
	 * 		(if any)
	**/
	
	public DataFormatDescriptor getValue()
	{
		return (fValue);
	}
	

	/**
	 * Sets the value postfix DataFormatDescriptor of this 
	 * ListElementFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param postfix The new postfix DataFormatDescriptor of this 
	 * 		ListElementFormatterDescriptor 
	**/
	
	void setPostfix(DataFormatDescriptor postfix)
	{
		fPostfix = postfix;
	}
	

	/**
	 * Returns the value postfix DataFormatDescriptor associated with this 
	 * ListElementFormatterDescriptor (if any).
	 *
	 * @return The value postfix DataFormatDescriptor associated with this 
	 * 		ListElementFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getPostfix()
	{
		return (fPostfix);
	}
	

	/**
	 * Sets the list terminator DataFormatDescriptor of this 
	 * ListValueFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param terminator The list terminator DataFormatDescriptor of this 
	 * 		ListValueFormatterDescriptor 
	**/
	
	void setTerminator(DataFormatDescriptor terminator)
	{
		fTerminator = terminator;
	}
	

	/**
	 * Returns the list terminator DataFormatDescriptor of this 
	 * ListValueFormatterDescriptor (if any).
	 *
	 * @return The list terminator DataFormatDescriptor of this 
	 * 		ListValueFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getTerminator()
	{
		return (fTerminator);
	}
	

	/**
	 * Returns the length (i.e., number of elements) of this 
	 * ListValueFormatterDescriptor. If the length is less than one, the number of 
	 * elements in the list is indeterminate.
	 *
	 * @return The length (i.e., number of elements) of this 
	 * 		ListValueFormatterDescriptor
	**/
	
	public int getLength()
	{
		return (fLength);
	}
	

	/**
	 * Returns true if this ListValueFormatterDescriptor is configured to suppress the 
	 * first value's prefix, false otherwise.
	 *
	 * @return True if this ListValueFormatterDescriptor is configured to suppress the 
	 * 		first value's prefix, false otherwise
	**/
	
	public boolean suppressesFirstPrefix()
	{
		return (fSuppressFirstPrefix);
	}
	

	/**
	 * Returns true if this ListValueFormatterDescriptor is configured to suppress the 
	 * last field's postfix, false otherwise.
	 *
	 * @return True if this ListValueFormatterDescriptor is configured to suppress the 
	 * 		last value's postfix, false otherwise
	**/
	
	public boolean suppressesLastPostfix()
	{
		return (fSuppressLastPostfix);
	}
	

	/** 
	 *  Returns a String representation of this ListValueFormatterDescriptor.
	 *
	 *  @return A String representation of this ListValueFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ListValueFormatterDescriptor:");
		
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

		if (fPrefix != null)
		{
			result.append("\nPrefix: " + fPrefix);
		}

		if (fValue != null)
		{
			result.append("\nValue: " + fValue);
			
			result.append("\nLength: ");
			
			if (fLength > 0)
			{
				result.append(fLength);
			}
			else
			{
				result.append("indeterminate");
			}
		}
		else
		{
			result.append("\nHas no value defined");
		}
		
		if (fPostfix != null)
		{
			result.append("\nPostfix: " + fPostfix);
		}
		
		if (fTerminator != null)
		{
			result.append("\nTerminator: " + fTerminator);
		}
		
		return (result.toString());
	}
	

	/**
	  * Unmarshalls this ListValueFormatterDescriptor from its associated JDOM ELement. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the list initiator SimpleValueFormatterDescriptor (if any).
		fInitiator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the list initiator separator SimpleValueFormatterDescriptor 
		// (if any).
		fInitiatorSeparator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR_SEPARATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
				
		// Unmarshall the value prefix SimpleValueFormatterDescriptor (if any).
		fPrefix = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_PREFIX, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the list value DataFormatDescriptor (if any).
		fValue = (DataFormatDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Datatransml.E_VALUE, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the value postfix SimpleValueFormatterDescriptor (if any).
		fPostfix = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_POSTFIX, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the list terminator SimpleValueFormatterDescriptor (if any).
		fTerminator = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_TERMINATOR, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
		
		// Unmarshall the length attribute (if any).
		fLength = fSerializer.loadIntAttribute
			(Datatransml.A_LENGTH, 0, fElement);

		// Unmarshall the suppressFirstPrefix attribute (if any).
		fSuppressFirstPrefix = fSerializer.loadBooleanAttribute
			(Datatransml.A_SUPPRESS_FIRST_PREFIX, false, fElement);

		// Unmarshall the suppressLastPostfix attribute (if any).
		fSuppressLastPostfix = fSerializer.loadBooleanAttribute
			(Datatransml.A_SUPPRESS_LAST_POSTFIX, false, fElement);
		
	}
}
