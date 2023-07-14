//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/DataElementDescriptor.java,v 1.5 2006/01/23 17:59:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataElementDescriptor.java,v $
//  Revision 1.5  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.3  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/09/10 23:15:00  chostetter_cvs
//  Checking in for weekend, still needs some PixelBundle debugging
//
//  Revision 1.2  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.1  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
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

package gov.nasa.gsfc.irc.data.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;


/**
 * A DataElementDescriptor is an abstract, named Descriptor of some unit or bundle 
 * of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public abstract class DataElementDescriptor extends AbstractIrcElementDescriptor
{
	/**
	 * Constructs a new DataElementDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataElementDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataElementDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataElementDescriptor		
	**/
	
	public DataElementDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_DATA);
	}
	

	/**
	 * Constructs a new DataElementDescriptor having the given base name.
	 * 
	 * @param name The base name of the new DataElementDescriptor
	**/
	
	public DataElementDescriptor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Returns a modifiable copy of this DataElementDescriptor.
	 *
	 *  @return A modifiable copy of this DataElementDescriptor
	 */
	
	public abstract DataElementDescriptor getModifiableCopy();
}
