//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/NumTokensSelectorDescriptor.java,v 1.1 2005/09/29 18:18:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: NumTokensSelectorDescriptor.java,v $
//  Revision 1.1  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
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
 * A NumTokensSelectorDescriptor describes the means to select the number of 
 * tokens in a given data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/29 18:18:23 $
 * @author Carl F. Hostetter
 */

public class NumTokensSelectorDescriptor extends AbstractCharDataSelectorDescriptor
{
	private String fTokenSeparator;
	
	
	/**
	 * Constructs a new NumTokensSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new NumTokensSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		NumTokensSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		NumTokensSelectorDescriptor		
	**/
	
	public NumTokensSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given NumTokensSelectorDescriptor.
	 *
	 * @param descriptor The NumTokensSelectorDescriptor to be copied
	**/
	
	protected NumTokensSelectorDescriptor
		(NumTokensSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this NumTokensSelectorDescriptor.
	 *
	 * @return A (deep) copy of this NumTokensSelectorDescriptor 
	**/
	
	public Object clone()
	{
		NumTokensSelectorDescriptor result = 
			(NumTokensSelectorDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns the token separator associated with this 
	 *  NumTokensSelectorDescriptor.
	 *
	 *  @return The token separator associated with this 
	 *  	NumTokensSelectorDescriptor
	**/
	
	public String getTokenSeparator()
	{
		return (fTokenSeparator);
	}
	
	
	/** 
	 *  Returns a String representation of this NumTokensSelectorDescriptor.
	 *
	 *  @return A String representation of this NumTokensSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("NumTokensSelectorDescriptor:");
		result.append("\n" + super.toString());
		
		result.append("\nToken separator = \'" + fTokenSeparator + "\'");
		
		return (result.toString());
	}
	
	
	/**
	 * Unmarshalls this NumTokensSelectorDescriptor from its associated JDOM Element. 
	 *
	**/
	private void xmlUnmarshall()
	{
		// Unmarshall the token separator attribute (if any).
		fTokenSeparator  = fSerializer.loadStringAttribute
			(Dataselml.A_SEPARATOR, " ", fElement); 
	}
}
