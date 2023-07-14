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

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.Datatransml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;


/**
 * The AbstractAdapterDescriptor class is an abstract description of a 
 * adapter of data.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
**/

public abstract class AbstractAdapterDescriptor extends ComponentDescriptor
{
	private DataTransformationDescriptor fDataTransformation;


	/**
	 * Constructs a new AbstractAdapterDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element and belonging to the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new AbstractAdapterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractAdapterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractAdapterDescriptor	
	 * @param namespace The namespace to which the new AbstractAdapterDescriptor 
	 * 		will belong
	**/
	
	public AbstractAdapterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element, String namespace)
	{
		super(parent, directory, element, namespace);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new AbstractAdapterDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractAdapterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractAdapterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractAdapterDescriptor		
	**/
	
	public AbstractAdapterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		this(parent, directory, element, Iml.N_ADAPTERS);
	}
	

	/**
	 * Constructs a new AbstractAdapterDescriptor having the given name.
	 * 
	 * @param name The name of the new AbstractAdapterDescriptor
	**/
	
	public AbstractAdapterDescriptor(String name)
	{
		super(name);
	}
	
	
	/**
	 * Returns the DataTransformationDescriptor associated with the 
	 * AbstractAdapter described by this AbstractAdapterDescriptor.
	 *
	 * @return The DataTransformationDescriptor associated with the 
	 * 		AbstractAdapter described by this AbstractAdapterDescriptor
	**/
	
	public DataTransformationDescriptor getDataTransformer()
	{
		return (fDataTransformation);
	}
	

	/**
	 * Unmarshalls this AbstractAdapterDescriptor from its associated JDOM Element.
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Load the DataTransformationDescriptor (if any).
		fDataTransformation = (DataTransformationDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_TRANSFORMATION, 
				Datatransml.C_TRANSFORMATION, fElement, this,fDirectory);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractAdapterDescriptor.java,v $
//  Revision 1.7  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.6  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.5  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.4  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.3  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
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
