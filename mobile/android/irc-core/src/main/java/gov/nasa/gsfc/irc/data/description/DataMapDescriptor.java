//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.data.description;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataMapDescriptor is a Map of DataEntryDescriptors that as a whole describes a 
 * Map of (key, value) data entries.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/01/07 20:25:38 $ 
 * @author Carl F. Hostetter
**/

public class DataMapDescriptor extends DataFeatureDescriptor
{
	protected Map fDataMapEntryDescriptorsByName;


	/**
	 * Constructs a new DataMapDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element within the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new DataMapDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataMapDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataMapDescriptor		
	 * @param namespace The namespace to which the new DataMapDescriptor should 
	 * 		belong		
	**/
	
	public DataMapDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element, String namespace)
	{
		super(parent, directory, element, namespace);
		
		fDataMapEntryDescriptorsByName = new LinkedHashMap();

		xmlUnmarshall();
	}
	
	
	/**
	 * Constructs a new DataMapDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataMapDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataMapDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataMapDescriptor		
	**/
	
	public DataMapDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		this(parent, directory, element, Dataml.N_DATA);
	}
	

	/**
	 * Constructs a new DataMapDescriptor having the given name.
	 * 
	 * @param name The name of the new DataMapDescriptor
	**/
	
	public DataMapDescriptor(String name)
	{
		super(name);
		
		fDataMapEntryDescriptorsByName = new LinkedHashMap();
	}

	
	/**
	 * Clears all DataEntryDescriptors from the DataMapDescriptor.
	 *
	**/
	
	protected void clear()
	{
		fDataMapEntryDescriptorsByName.clear();
	}
	

	/**
	 * Adds the given DataMapEntryDescriptor to this DataMapDescriptor.
	 *
	 * @param descriptor A DataMapEntryDescriptor
	**/
	
	protected void addEntry(DataMapEntryDescriptor descriptor)
	{
		fDataMapEntryDescriptorsByName.put(descriptor.getName(), descriptor);
	}
	

	/**
	 * Adds the Iterator of DataEntryDescriptors to this DataMapDescriptor.
	 *
	 * @param descriptors An Iterator of DataEntryDescriptors
	**/
	
	protected void addEntries(Iterator descriptors)
	{
		while (descriptors.hasNext())
		{
			DataMapEntryDescriptor descriptor = (DataMapEntryDescriptor) 
				descriptors.next();
			
			addEntry(descriptor);
		}
	}
	

	/**
	 * Returns the DataMapEntryDescriptor of this DataMapDescriptor that has 
	 * the given name (if any).
	 *
	 * @param name The name of a DataMapEntryDescriptor
	 * @return The DataMapEntryDescriptor that has the given name
	**/
	
	public DataMapEntryDescriptor getEntry(String name)
	{
		DataMapEntryDescriptor result = null;
		
		result = (DataMapEntryDescriptor) fDataMapEntryDescriptorsByName.get(name);
		
		return (result);
	}

	
	/**
	 * Removes the DataMapEntryDescriptor having the given name from this 
	 * DataMapDescriptor.
	 *
	 * @param name The name of the DataMapEntryDescriptor to remove
	**/
	
	protected void removeEntry(String name)
	{
		fDataMapEntryDescriptorsByName.remove(name);
	}
	

	/**
	 * Returns the (unmodifiable) Collection of DataEntryDescriptors of this 
	 * DataMapDescriptor.
	 *
	 * @return the (unmodifiable) Collection of DataEntryDescriptors of this 
	 * 		DataMapDescriptor
	**/
	
	public Collection getEntries()
	{
		return (Collections.unmodifiableCollection
			(fDataMapEntryDescriptorsByName.values()));
	}


	/**
	 * Unmarshall a DataMapDescriptor from XML.
	 *
	**/
	
	private void xmlUnmarshall()
	{
		//--- Load the DataEntryDescriptors
		
		fSerializer.loadChildDescriptorElements(Dataml.E_DATA_MAP_ENTRY, 
			fDataMapEntryDescriptorsByName, Dataml.C_DATA_MAP_ENTRY, fElement, this, 
			fDirectory);
	}

	/**
	 * Marshall this DataMapDescriptor to XML. 
	 *
	 * @param element Element to marshall into.
	**/
	
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		//---Store the DataEntryDescriptors
		
		fSerializer.storeDescriptorElement(Dataml.E_DATA_MAP_ENTRY, 
			fDataMapEntryDescriptorsByName, Dataml.C_DATA_MAP_ENTRY, element, 
			fDirectory);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataMapDescriptor.java,v $
//  Revision 1.4  2005/01/07 20:25:38  tames
//  Added support via super class for bean feature like attributes.
//
//  Revision 1.3  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.2  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
