//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for the
// Instrument Remote Control (IRC)
// project.
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

package gov.nasa.gsfc.irc.components.description;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A ComponentSetDescriptor is a Descriptor of a set of Component descriptors.
 *
 * <p><b>Note this class is a place holder for a more robust implementation that 
 * that will be developed in the future. </b>
 * <p>
 * Developers should be certain to refer to the IRC schemas to gain a 
 * better understanding of the structure and content of the data used to 
 * build Descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/13 03:21:06 $
 * @author Troy Ames
**/


public class ComponentSetDescriptor extends AbstractIrcElementDescriptor
{
	private Map fComponents;  // ComponentDescriptor collection
	

	/**
	 * Constructs a new ComponentDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element within the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new ComponentDescriptor
	 * @param directory The DescriptorDirectory to which the new ComponentDescriptor 
	 * 		will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ComponentDescriptor		
	 * @param namespace The namespace to which the new ComponentDescriptor should 
	 * 		belong		
	**/
	
	public ComponentSetDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element, String namespace)
	{
		super(parent, directory, element, namespace);
		
		fComponents = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	
	
	/**
	 * Constructs a new ComponentSetDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ComponentSetDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ComponentSetDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ComponentSetDescriptor		
	**/
	
	public ComponentSetDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		this(parent, directory, element, Cml.N_COMPONENT_SET);
	}
	

	/**
	 * Constructs a new ComponentSetDescriptor having the given name.
	 *
	 * @param The name of the new ComponentSetDescriptor
	**/
	
	public ComponentSetDescriptor(String name)
	{
		super(name);
		
		fComponents = new LinkedHashMap();
	}

	
	/**
	 * Get the set's component descriptor with the specified name. 
	 *
	 * @param  name Name of an component.
	 * @return component descriptor with the specified name.
	**/
	public ComponentDescriptor getComponentsByName(String name)
	{
		return (ComponentDescriptor)fComponents.get(name);
	}

	/**
	 * Get the sets's collection of component descriptors.
	 *
	 * @return All of the component's sub component descriptors.
	**/
	public Iterator getComponents()
	{
		return fComponents.values().iterator();
	}

	/**
	 * Set the collection of component descriptors.
	 *
	 * @param components All of the component descriptors.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setComponents(Map components)
	{
		fComponents = components;
	}

	/**
	 * Unmarshall descriptor from XML. 
	**/
	private void xmlUnmarshall()
	{
		//--- Load the Sub Components
		fSerializer.loadChildDescriptorElements(
			Cml.E_COMPONENT,
			fComponents,
			Cml.C_COMPONENT,
			fElement,
			this,
			fDirectory);
	}

	/**
	 * Marshall descriptor to XML.  
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
	{
		//super.xmlMarshall(fElement);

		fSerializer.storeDescriptorElement(
			Cml.E_COMPONENT,
			fComponents,
			Cml.C_COMPONENT,
			null,
			fDirectory);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentSetDescriptor.java,v $
//  Revision 1.1  2006/01/13 03:21:06  tames
//  Initial support for Component set descriptions.
//
//