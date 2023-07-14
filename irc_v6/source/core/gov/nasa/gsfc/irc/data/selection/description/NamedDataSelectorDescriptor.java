//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/NamedDataSelectorDescriptor.java,v 1.3 2005/09/30 20:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: NamedDataSelectorDescriptor.java,v $
//  Revision 1.3  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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
 * A NamedDataSelectorDescriptor describes the means to select named data 
 * Objects.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 */

public class NamedDataSelectorDescriptor extends AbstractDataSelectorDescriptor
{
	/**
	 * Constructs a new NamedDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new NamedDataSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		NamedDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		NamedDataSelectorDescriptor		
	**/
	
	public NamedDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	
	
	/**
	 * Constructs a new NamedDataSelectorDescriptor for a named data Object that 
	 * has the given name.
	 *
	 * @param name The name that the new NamedDataSelectorDescriptor will use to 
	 * 		select data
	**/
	
	public NamedDataSelectorDescriptor(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a (shallow) copy of the given NamedDataSelectorDescriptor.
	 *
	 * @param descriptor The NamedDataSelectorDescriptor to be copied
	**/
	
	protected NamedDataSelectorDescriptor
		(NamedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this NamedDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this NamedDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		NamedDataSelectorDescriptor result = 
			(NamedDataSelectorDescriptor) super.clone();
		
		return (result);
	}
}
