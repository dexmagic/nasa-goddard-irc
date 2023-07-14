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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.BasisBundleGroupDescriptor;
import gov.nasa.gsfc.irc.data.description.DataElementDescriptor;
import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;


/**
 * The class provides access to information describing an instrument's 
 * data interface as well as the following items logicaly contained 
 * within the data interface: <P>
 * 
 *  <BR>
 *  1) data elements <BR>
 *
 * <P>
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.<P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/03/31 21:57:38 $ 
 * @author John Higinbotham   
**/

public class DataInterfaceDescriptor extends AbstractIrcElementDescriptor
{
	private Map fDataElements;


	/**
	 * Constructs a new DataInterfaceDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataInterfaceDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataInterfaceDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataInterfaceDescriptor		
	**/
	
	public DataInterfaceDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		fDataElements = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DataInterfaceDescriptor having the given name.
	 * 
	 * @param name The name of the new DataInterfaceDescriptor
	**/
	
	public DataInterfaceDescriptor(String name)
	{
		super(name);
		
		fDataElements = new LinkedHashMap();
	}

	
	/**
	 * Get the data interface's data element descriptor with the specified name. 
	 *
	 * @param  name Name of data element.
	 * @return Data element descriptor with the specified name.
	 *			
	**/
	public DataElementDescriptor getDataElementByName(String name)
	{
		return (DataElementDescriptor) fDataElements.get(name);
	}

	/**
	 * Get the data interface's collection of data element descriptors.
	 *
	 * @return All of the data interface's data element descriptors.
	 *			
	**/
	public Iterator getDataElements()
	{
		return fDataElements.values().iterator();
	}

	/**
	 * Adds a Data Element.
	 * 
	 * @param dataElement The DataElement to add
	 * @throws IllegalStateException if descriptor is finalized.
	 */
	public void addDataElement(DataElementDescriptor dataElement)
	{
		String elementName = dataElement.getName(); 

		if (elementName != null) 
		{
			//--- Store a named descriptor 
			fDataElements.put(elementName, dataElement);
		}
		else 
		{
			//--- Store an unnamed descriptor 
			fDataElements.put("", dataElement);
		}
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		// Load any BasisBundleDescriptors
		
		Map basisBundlesMap = new LinkedHashMap();
		
		fSerializer.loadChildDescriptorElements(Dataml.E_BASIS_BUNDLE, 
			basisBundlesMap, Dataml.C_BASIS_BUNDLE, fElement, this, fDirectory);
		
		fDataElements.putAll(basisBundlesMap);
		
		// Load any BasisBundleGroupDescriptors
		
		Map basisBundleGroupsMap = new LinkedHashMap();
		
		fSerializer.loadChildDescriptorElements(Dataml.E_BASIS_BUNDLE_GROUP, 
			basisBundleGroupsMap, Dataml.C_BASIS_BUNDLE_GROUP, fElement, this, 
				fDirectory);
		
		Iterator basisBundleGroups = basisBundleGroupsMap.values().iterator();
		
		while (basisBundleGroups.hasNext())
		{
			BasisBundleGroupDescriptor basisBundleGroup = 
				(BasisBundleGroupDescriptor) basisBundleGroups.next();
			
			Map basisBundlesInGroup = 
				basisBundleGroup.getBasisBundlesByFullyQualifiedName();
			
			fDataElements.putAll(basisBundlesInGroup);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataInterfaceDescriptor.java,v $
//  Revision 1.8  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.7  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.5  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.4  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.4  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.3  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 22:09:03  chostetter_cvs
//  Initial version
//
