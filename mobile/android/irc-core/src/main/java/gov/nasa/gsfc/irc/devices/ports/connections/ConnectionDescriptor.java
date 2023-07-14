//=== File Prolog ============================================================
//
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC)project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.ports.connections;

import org.jdom.Element;

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;


/**
 * The class provides access to information describing a connection as well
 * as the following items logicaly contained within a connection: <P><BR>
 *
 *  1) parameters <BR>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.<P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/10/14 15:22:16 $
 * @author Troy Ames
**/
public class ConnectionDescriptor extends ComponentDescriptor
{
	/**
	 * Constructs a new ConnectionDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ConnectionDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ConnectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ConnectionDescriptor		
	**/
	
	public ConnectionDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_CONNECTIONS);
	}
	

	/**
	 * Constructs a new ConnectionDescriptor having the given name.
	 * 
	 * @param name The name of the new ConnectionDescriptor
	**/
	
	public ConnectionDescriptor(String name)
	{
		super(name);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ConnectionDescriptor.java,v $
//  Revision 1.1  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.6  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.5  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.4  2004/09/07 05:30:16  tames
//  Descriptor cleanup
//
//  Revision 1.3  2004/08/26 14:35:24  tames
//  Added Property element
//
//  Revision 1.2  2004/08/03 20:34:05  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.1  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
