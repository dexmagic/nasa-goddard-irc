//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
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
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * The class provides access to information describing a state model.
 * Right now this class only contains a type for the state model, but
 * that is expected to change.<P><BR>
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
 * @version $Date: 2004/10/14 15:16:51 $
 * @author John Higinbotham
**/

public class StateModelDescriptor extends ComponentDescriptor
{
	/**
	 * Constructs a new StateModelDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new StateModelDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		StateModelDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		StateModelDescriptor		
	**/
	
	public StateModelDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_STATE_MODELS);
	}
	

	/**
	 * Constructs a new StateModelDescriptor having the given name.
	 * 
	 * @param name The name of the new StateModelDescriptor
	**/
	
	public StateModelDescriptor(String name)
	{
		super(name);
	}

	
	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	 *
	**/
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		//---Store state model 
		fSerializer.storeAttribute(Iml.A_TYPE, getType(), element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: StateModelDescriptor.java,v $
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.4  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.3  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/06/04 14:37:04  tames_cvs
//  Removed references to obsolete singletons
//
//  Revision 1.1  2004/05/12 22:09:03  chostetter_cvs
//  Initial version
// 
