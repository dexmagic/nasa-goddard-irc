//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: NamespaceTable.java,v $
//  Revision 1.7  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.5  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.3  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.irc.description.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides support to the descriptors for mapping strings 
 * to alternate values specified in a mapping file that uses the mml.xsd
 * schema. Each entry in the name space table has a name and a value.
 * The namespace table itself has a name and is contained within a lookup table.<P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:54 $
 * @author John Higinbotham
**/

public class NamespaceTable extends AbstractIrcElementDescriptor
{
	private Map fMapping;  // Collection of name/value mappings 
	

	/**
	 *  Constructs a new NamespaceTable having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new NamespaceTable
	 **/

	public NamespaceTable(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new NamespaceTable having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new NamespaceTable
	 *  @param nameQualifier The name qualifier of the new NamespaceTable
	 **/

	public NamespaceTable(String name, String nameQualifier)
	{
		super(name, nameQualifier);
		
		fMapping = new LinkedHashMap();
	}
	
	
	/**
	 * Constructs a new NamespaceTable having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new NamespaceTable 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		NamespaceTable will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		NamespaceTable		
	**/
	
	public NamespaceTable(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Mml.N_MAPPINGS);
		
		fMapping = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * This method will merge the mappings from an existing NamespaceTable with those already
	 * in this NamespaceTable. Where mappings have the same name, those found in the specified
	 * table will override those in this table. Where mappings are new in the specified table,
	 * they will be added to this table. 
	 * 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void merge(NamespaceTable table)
	{
		//---Build a map of the existing mappings
		HashMap map = new HashMap();
		Iterator i = getValueNames(); 
		String name;
		while (i.hasNext())
		{
			name = (String) i.next();
			map.put(name, getValueByName(name));
		}

		//---Update this table with mappings from overriding table
		i = table.getValueNames();
		while (i.hasNext())
		{
			name = (String) i.next();	
			map.put(name, table.getValueByName(name));
		}

		//---Build a replacement hash array
		Map ha = new LinkedHashMap();
		i = map.keySet().iterator();
		String key;
		while (i.hasNext())
		{
			key = (String) i.next();
			ha.put(key, map.get(key));	
		}
		fMapping = ha;
	}

	/**
	 * Get the string value mapped to the specifed name.
	 *
	 * @return String value mapped to specified name. 
	 *			
	**/
	public String getValueByName(String name)
	{
		 return (String) fMapping.get(name);
	}

	/**
	 * Get the collection of value names.
	 *
	 * @return Collection of value names. 
	 *			
	**/
	public Iterator getValueNames()
	{
		 return fMapping.keySet().iterator();
	}

	/**
	 *	Retrieve all keys that match given value from the table.
	 *
	 *  @param value  value to match
	 *
	 *  @return  a list (which may be empty) of the keys that match the
	 *		   given value
	**/
	public List getKeysByValue(Object value)
	{
		String key = null;
		List matchingKeys = new LinkedList();

		//  Implementation note:  I (ksw) really wanted to put this 
		//  functionality in the 'Map' class to help with efficiency.
		//  However, given that 'Map' has some issues with "keyless"
		//  values, I thought it was safer to put here.
		Iterator keyIter = fMapping.keySet().iterator();
		while (keyIter.hasNext())
		{
			//  Check the value of each key and see if we should include
			//  it in the list.
			key = (String) keyIter.next();
			if (value.equals(fMapping.get(key)))
			{
				matchingKeys.add(key);
			}
		}

		return matchingKeys;
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		fSerializer.loadKeyValueElement(Mml.E_MAPPING, Mml.A_NAME, Mml.A_VALUE, 
			fMapping, fElement);
	}
}
