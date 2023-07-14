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
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.ports.adapters;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;


/**
 * An OutputAdapterDescriptor describes an OutputAdapter.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2004/11/10 23:09:56 $
 * @author Carl F. Hostetter
**/

public class OutputAdapterDescriptor extends AbstractAdapterDescriptor
{
	/**
	 * Constructs a new OutputAdapterDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new OutputAdapterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		OutputAdapterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		OutputAdapterDescriptor		
	**/
	
	public OutputAdapterDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_OUTPUT_ADAPTERS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new OutputAdapterDescriptor having the given name.
	 * 
	 * @param name The name of the new OutputAdapterDescriptor
	**/
	
	public OutputAdapterDescriptor(String name)
	{
		super(name);
	}
	
	
	/**
	 * Unmarshalls this OutputAdapterDescriptor from its associated JDOM Element.
	 *
	**/
	private void xmlUnmarshall()
	{

	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: OutputAdapterDescriptor.java,v $
//  Revision 1.3  2004/11/10 23:09:56  chostetter_cvs
//  Initial debugging of Message formatting. Mostly works except for C-style formatting.
//
//  Revision 1.2  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.1  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//
