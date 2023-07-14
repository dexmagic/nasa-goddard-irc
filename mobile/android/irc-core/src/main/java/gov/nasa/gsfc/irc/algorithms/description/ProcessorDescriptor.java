//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ProcessorDescriptor.java,v $
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/06/03 00:19:59  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//  Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
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

package gov.nasa.gsfc.irc.algorithms.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A ProcessorDescriptor is a Descriptor of a Processor.
 *
 * <p>The object is built based on information contained in a PEML XML file
 * which describes the PipelineElements available to IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/10/14 15:16:51 $
 * @author Carl F. Hostetter
**/


public class ProcessorDescriptor extends ComponentDescriptor
{
	/**
	 * Constructs a new ProcessorDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ProcessorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new ProcessorDescriptor 
	 * 		will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ProcessorDescriptor		
	**/
	
	public ProcessorDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a new ProcessorDescriptor having the given name.
	 *
	 * @param The name of the new ProcessorDescriptor
	**/
	
	public ProcessorDescriptor(String name)
	{
		super(name);
	}
}
