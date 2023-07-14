//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/StringConstantValueSelectorDescriptor.java,v 1.1 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: StringConstantValueSelectorDescriptor.java,v $
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A StringConstantValueSelectorDescriptor describes the means to select a String value.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class StringConstantValueSelectorDescriptor 
	extends AbstractCharDataSelectorDescriptor
{
	private String fValue;
	
	
	/**
	 * Constructs a new StringConstantValueSelectorDescriptor that will select the 
	 * given String value, and that treats the char data it selects from as 
	 * ASCII according to the given flag.
	 *
	 * @param value The String value that the new StringConstantValueSelectorDescriptor 
	 * 		will select		
	 * @param isAscii True if the selected char data is ASCII, false otherwise	
	**/
	
	public StringConstantValueSelectorDescriptor(String value, boolean isAscii)
	{
		super(isAscii);
		
		fValue = value;
	}
	

	/**
	 * Constructs a new StringConstantValueSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		StringConstantValueSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		StringConstantValueSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		StringConstantValueSelectorDescriptor		
	**/
	
	public StringConstantValueSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given StringConstantValueSelectorDescriptor.
	 *
	 * @param descriptor The StringConstantValueSelectorDescriptor to be copied
	**/
	
	protected StringConstantValueSelectorDescriptor
		(StringConstantValueSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fValue = descriptor.fValue;
	}
	

	/**
	 * Returns a (deep) copy of this StringConstantValueSelectorDescriptor.
	 *
	 * @return A (deep) copy of this StringConstantValueSelectorDescriptor 
	**/
	
	public Object clone()
	{
		StringConstantValueSelectorDescriptor result = 
			(StringConstantValueSelectorDescriptor) super.clone();
			
		result.fValue = fValue;
		
		return (result);
	}
	
	
	/**
	 *  Returns the String value associated with this StringConstantValueSelectorDescriptor.
	 *  
	 *  @return The String value associated with this StringConstantValueSelectorDescriptor
	 */
	
	public String getValue()
	{
		return (fValue);
	}
	
	
	/** 
	 *  Returns a String representation of this StringConstantValueSelectorDescriptor.
	 *
	 *  @return A String representation of this StringConstantValueSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("StringConstantValueSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nValue: " + fValue);
		
		return (result.toString());
	}
	
				
	/**
	 * Unmarshalls a StringConstantValueSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the String value attribute (if any).
		fValue = fSerializer.loadStringAttribute(Dataselml.A_VALUE, fElement);
	}
}
