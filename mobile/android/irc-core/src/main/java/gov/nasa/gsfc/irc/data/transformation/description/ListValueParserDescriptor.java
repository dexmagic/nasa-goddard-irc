//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ListValueParserDescriptor.java,v $
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
 * A ListValueParserDescriptor describes the parsing of a list of data values.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class ListValueParserDescriptor extends AbstractDataParserDescriptor 
	implements ValueParserDescriptor
{
	private DataParseDescriptor fInitiator;
	private DataParseDescriptor fInitiatorSeparator;
	private DataParseDescriptor fPrefix;
	private DataParseDescriptor fValue;
	private DataParseDescriptor fPostfix;
	private DataParseDescriptor fTerminator;
	
	private int fLength = 0;
	private boolean fSuppressFirstPrefix = false;
	private boolean fSuppressLastPostfix = false;

	
	/**
	 * Constructs a new ListValueParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ListValueParserDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ListValueParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ListValueParserDescriptor		
	**/
	
	public ListValueParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given ListValueParserDescriptor.
	 *
	 * @param descriptor The ListValueParserDescriptor to be copied
	**/
	
	protected ListValueParserDescriptor(ListValueParserDescriptor descriptor)
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
	 * Returns a (deep) copy of this ListValueParserDescriptor.
	 *
	 * @return A (deep) copy of this ListValueParserDescriptor 
	**/
	
	public Object clone()
	{
		ListValueParserDescriptor result = (ListValueParserDescriptor) super.clone();
		
		result.fInitiator = (DataParseDescriptor) fInitiator.clone();
		result.fInitiatorSeparator = (DataParseDescriptor) 
			fInitiatorSeparator.clone();
		result.fPrefix = (DataParseDescriptor) fPrefix.clone();
		result.fValue = (DataParseDescriptor) fValue.clone();
		result.fPostfix = (DataParseDescriptor) fPostfix.clone();
		result.fTerminator = (DataParseDescriptor) fTerminator.clone();
		
		result.fLength = fLength;
		result.fSuppressFirstPrefix = fSuppressFirstPrefix;
		result.fSuppressLastPostfix = fSuppressLastPostfix;
		
		return (result);
	}
	

	/**
	 * Sets the list initiator DataParseDescriptor of this 
	 * ListValueParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param initiator The list initiator DataParseDescriptor of this 
	 * 		ListValueParserDescriptor 
	**/
	
	void setInitiator(DataParseDescriptor initiator)
	{
		fInitiator = initiator;
	}
	

	/**
	 * Returns the list initiator DataParseDescriptor of this 
	 * ListValueParserDescriptor (if any).
	 *
	 * @return The list initiator DataParseDescriptor of this 
	 * 		ListValueParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getInitiator()
	{
		return (fInitiator);
	}
	

	/**
	 * Sets the list initiator separtor DataParseDescriptor of this 
	 * ListValueParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param separator The list initiator separator DataParseDescriptor 
	 * 		of this ListValueParserDescriptor 
	**/
	
	void setInitiatorSeparator(DataParseDescriptor separator)
	{
		fInitiatorSeparator = separator;
	}
	

	/**
	 * Returns the list initiator separator DataParseDescriptor of this 
	 * ListValueParserDescriptor (if any).
	 *
	 * @return The list initiator separator DataParseDescriptor of this 
	 * 		ListValueParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getInitiatorSeparator()
	{
		return (fInitiatorSeparator);
	}
	

	/**
	 * Sets the value prefix DataParseDescriptor of this 
	 * ListElementParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param prefix The new value prefix DataParseDescriptor of this 
	 * 		ListElementParserDescriptor 
	**/
	
	void setPrefix(DataParseDescriptor prefix)
	{
		fPrefix = prefix;
	}
	

	/**
	 * Returns the value prefix DataParseDescriptor of this 
	 * ListElementParserDescriptor (if any).
	 *
	 * @return The value prefix DataParseDescriptor of this 
	 * 		ListElementParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getPrefix()
	{
		return (fPrefix);
	}
	

	/**
	 * Returns the value DataParseDescriptor of this ListValueParserDescriptor 
	 * (if any).
	 *
	 * @return The value DataParseDescriptor of this ListValueParserDescriptor 
	 * 		(if any)
	**/
	
	public DataParseDescriptor getValue()
	{
		return (fValue);
	}
	

	/**
	 * Sets the value postfix DataParseDescriptor of this 
	 * ListElementParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param postfix The new postfix DataParseDescriptor of this 
	 * 		ListElementParserDescriptor 
	**/
	
	void setPostfix(DataParseDescriptor postfix)
	{
		fPostfix = postfix;
	}
	

	/**
	 * Returns the value postfix DataParseDescriptor associated with this 
	 * ListElementParserDescriptor (if any).
	 *
	 * @return The value postfix DataParseDescriptor associated with this 
	 * 		ListElementParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getPostfix()
	{
		return (fPostfix);
	}
	

	/**
	 * Sets the list terminator DataParseDescriptor of this 
	 * ListValueParserDescriptor to the given DataParseDescriptor.
	 *
	 * @param terminator The list terminator DataParseDescriptor of this 
	 * 		ListValueParserDescriptor 
	**/
	
	void setTerminator(DataParseDescriptor terminator)
	{
		fTerminator = terminator;
	}
	

	/**
	 * Returns the list terminator DataParseDescriptor of this 
	 * ListValueParserDescriptor (if any).
	 *
	 * @return The list terminator DataParseDescriptor of this 
	 * 		ListValueParserDescriptor (if any)
	**/
	
	public DataParseDescriptor getTerminator()
	{
		return (fTerminator);
	}
	

	/**
	 * Returns the length (i.e., number of elements) of this 
	 * ListValueParserDescriptor. If the length is less than one, the number of 
	 * elements in the list is indeterminate.
	 *
	 * @return The length (i.e., number of elements) of this 
	 * 		ListValueParserDescriptor
	**/
	
	public int getLength()
	{
		return (fLength);
	}
	

	/**
	 * Returns true if this ListValueParserDescriptor is configured to suppress the 
	 * first value's prefix, false otherwise.
	 *
	 * @return True if this ListValueParserDescriptor is configured to suppress the 
	 * 		first value's prefix, false otherwise
	**/
	
	public boolean suppressesFirstPrefix()
	{
		return (fSuppressFirstPrefix);
	}
	

	/**
	 * Returns true if this ListValueParserDescriptor is configured to suppress the 
	 * last field's postfix, false otherwise.
	 *
	 * @return True if this ListValueParserDescriptor is configured to suppress the 
	 * 		last value's postfix, false otherwise
	**/
	
	public boolean suppressesLastPostfix()
	{
		return (fSuppressLastPostfix);
	}
	

	/** 
	 *  Returns a String representation of this ListValueParserDescriptor.
	 *
	 *  @return A String representation of this ListValueParserDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ListValueParserDescriptor:");
		
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
	  * Unmarshalls this ListValueParserDescriptor from its associated JDOM ELement. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the list initiator SimpleValueParserDescriptor (if any).
		fInitiator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the list initiator separator SimpleValueParserDescriptor 
		// (if any).
		fInitiatorSeparator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_INITIATOR_SEPARATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
				
		// Unmarshall the value prefix SimpleValueParserDescriptor (if any).
		fPrefix = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_PREFIX, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the list value DataParseDescriptor (if any).
		fValue = (DataParseDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Datatransml.E_VALUE, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the value postfix SimpleValueParserDescriptor (if any).
		fPostfix = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_POSTFIX, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
		// Unmarshall the list terminator SimpleValueParserDescriptor (if any).
		fTerminator = (DataParseDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_TERMINATOR, 
				Datatransml.C_VALUE_PARSE, fElement, this, fDirectory);
		
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
