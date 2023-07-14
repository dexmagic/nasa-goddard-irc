//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: PropertyDescriptor.java,v $
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
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

package gov.nasa.gsfc.irc.components.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.DataMapEntryDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A PropertyDescriptor describes a property of a Component.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/10/14 15:16:51 $ 
 * @author Carl F. Hostetter   
**/

public class PropertyDescriptor extends DataMapEntryDescriptor
{
	/**
	 * Constructs a new PropertyDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new PropertyDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new PropertyDescriptor 
	 * 		will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		PropertyDescriptor		
	**/
	
	public PropertyDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a new PropertyDescriptor having the given name.
	 * 
	 * @param name The name of the new PropertyDescriptor
	**/
	
	public PropertyDescriptor(String name)
	{
		super(name);
	}
}
