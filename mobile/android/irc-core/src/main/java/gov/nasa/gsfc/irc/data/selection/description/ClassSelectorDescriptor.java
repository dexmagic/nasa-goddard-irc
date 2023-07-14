//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/ClassSelectorDescriptor.java,v 1.1 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ClassSelectorDescriptor.java,v $
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
 * A ClassNameSelectorDescriptor describes the means to select the Class of  
 * data Objects.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class ClassSelectorDescriptor extends AbstractDataSelectorDescriptor
{
	/**
	 * Constructs a new ClassSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ClassSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ClassNameSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ClassNameSelectorDescriptor		
	**/
	
	public ClassSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a (shallow) copy of the given ClassSelectorDescriptor.
	 *
	 * @param descriptor The ClassSelectorDescriptor to be copied
	**/
	
	protected ClassSelectorDescriptor
		(ClassSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this ClassSelectorDescriptor.
	 *
	 * @return A (deep) copy of this ClassSelectorDescriptor 
	**/
	
	public Object clone()
	{
		ClassSelectorDescriptor result = 
			(ClassSelectorDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this ClassSelectorDescriptor.
	 *
	 *  @return A String representation of this ClassSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ClassSelectorDescriptor:");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
}
