//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space
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

package gov.nasa.gsfc.irc.components.description;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.DescriptorSerializer;


/**
 * A ComponentDescriptor is a Descriptor of a Component.
 *
 * <p><b>Note this class is a place holder for a more robust implementation that 
 * that will be developed in the future. </b>
 * <p>Developers should be certain to refer to the IRC schemas to gain a 
 * better understanding of the structure and content of the data used to 
 * build Descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/02/07 14:33:09 $
 * @author Carl F. Hostetter
**/


public class ComponentDescriptor extends AbstractIrcElementDescriptor
{
	private Map fPropertiesByName;
	// Elements of fProperties by localized name
	private Map fPropertiesByLocalName;
	private boolean fStarted = false;	

	/**
	 *  Constructs a new ComponentDescriptor having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new ComponentDescriptor
	 **/

	public ComponentDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new ComponentDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new ComponentDescriptor
	 *  @param nameQualifier The name qualifier of the new ComponentDescriptor
	 **/

	public ComponentDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
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
	
	public ComponentDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element, String namespace)
	{
		super(parent, directory, element, namespace);
		
		fPropertiesByName = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	
	
	/**
	 * Constructs a new ComponentDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ComponentDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ComponentDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ComponentDescriptor		
	**/
	
	public ComponentDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		this(parent, directory, element, Cml.N_COMPONENT);
	}
	

	/**
	 * Returns the (unmodifiable) Collection of PropertyDescriptors associated with 
	 * this ComponentDescriptor.
	 *
	 * @return The (unmodifiable) Collection of PropertyDescriptors associated with 
	 * 		this ComponentDescriptor
	 **/
	
	public Collection getProperties()
	{
		return (Collections.unmodifiableCollection(fPropertiesByName.values()));
	}
	

	/**
	 * Adds the given property PropertyDescriptor to the Set of property 
	 * PropertyDescriptors defined on this ComponentDescriptor.
	 *
	 * @param propertyProperty A proeprty PropertyDescriptor
	 * @throws IllegalStateException if this Descriptor is finalized
	 **/
	
	public void addProperty(PropertyDescriptor property)
	{
		if (property != null)
		{
			fPropertiesByName.put(property.getName(), property);
			fPropertiesByLocalName = DescriptorSerializer.
				toMapByDisplayName(getProperties().iterator());
		}
		else
		{	
			throw (new NullPointerException
				("Cannot add null property to " + this.getName()));
		}
	}
	

	/**
	 * Returns the PropertyDescriptor of the property having the given 
	 * name.
	 *
	 * @param name The name of a property
	 * @return The PropertyDescriptor of the property having the given 
	 * 		name
	 **/
	
	public PropertyDescriptor getPropertyByName(String name)
	{
		return ((PropertyDescriptor) fPropertiesByName.get(name));
	}
	

	/**
	 * Returns the PropertyDescriptor of the property having the given 
	 * localized name.
	 *
	 * @param name The name of the desired property
	 * @return The PropertyDescriptor of the property having the given 
	 * 		name
	 **/
	
	public PropertyDescriptor getPropertyByLocalName(String name)
	{
		return ((PropertyDescriptor) fPropertiesByLocalName.get(name));
	}
	

	/**
	 * Returns the PropertyDescriptor of the property having the given name key. 
	 * If the key does not exist in the property namespace then null is returned.
	 *
	 * @param key The name of the desired property
	 * @return The PropertyDescriptor of the property having the given name
	 **/
	
	public PropertyDescriptor findProperty(String key)
	{
		PropertyDescriptor property = null;
		
		property = getPropertyByName(key);
		
		if (property == null)
		{
			property = getPropertyByLocalName(key);
		}
		
		return (property); 
	}


	/**
	 * Unmarshall descriptor from XML.
	 *
	**/
	
	private void xmlUnmarshall()
	{
		//--- Load the synchronous attribute
		fStarted  = fSerializer.loadBooleanAttribute(Cml.A_STARTED, false, 
			fElement);

		//---Load the properties of this Component (if any).
		fSerializer.loadChildDescriptorElements(Cml.E_PROPERTY, fPropertiesByName, 
			Cml.C_PROPERTY, fElement, this, fDirectory);

		//---Support property lookups by local name
		fPropertiesByLocalName = DescriptorSerializer.
			toMapByDisplayName(getProperties().iterator());
	}


	/**
	 * Returns true if the component should be started.
	 * 
	 * @return Returns true if the component should be started, false otherwise.
	 */
	public boolean isStarted()
	{
		return fStarted;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentDescriptor.java,v $
//  Revision 1.14  2006/02/07 14:33:09  chostetter_cvs
//  Organized imports
//
//  Revision 1.13  2006/02/04 17:10:28  tames
//  Added started property.
//
//  Revision 1.12  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.11  2006/01/13 03:20:42  tames
//  JavaDoc change only.
//
//  Revision 1.10  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.9  2005/02/01 16:50:09  tames
//  updated method names from localized names to display name.
//
//  Revision 1.8  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.7  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.6  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.5  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.4  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.3  2004/08/03 20:29:45  tames_cvs
//  Added constructor for compatibility with subclasses
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//  Initial version. Much functionality of implementation classes yet undefined, but 
//	many useful interfaces
//
