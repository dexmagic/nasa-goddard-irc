//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: LookupTable.java,v $
//  Revision 1.11  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.10  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.9  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.8  2004/10/14 19:11:11  chostetter_cvs
//  Fixed remaining descriptor-related issues
//
//  Revision 1.7  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.6  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.5  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/06/01 16:05:18  tames_cvs
//  Removed old references to obsolete package names and constants.
//
//  Revision 1.2  2004/05/27 18:24:03  tames_cvs
//  CLASS_NAME assignment fix
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides support for mapping a name to a string value
 * based on the contents of an xml input file. This functionality has
 * several application throughout IRC. One of the most notable is processing
 * the IML file to create the corresponsing descriptors. The mapping
 * allows an IML writter to designate something like a port type as "TCP" 
 * instead of calling out the full class name gov.gsfc.nasa.irc.port.TcpIoPort. 
 * The lookup table is actually a collection of name space tables which allow
 * a name to be mapped to different values in the lookup table depending on
 * its namespace.<P>
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

public class LookupTable extends AbstractIrcElementDescriptor
{
	private static final String CLASS_NAME = 
		LookupTable.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private Map fNamespaceTablesByName; // Collection of namespace table objects


	/**
	 *  Constructs a new LookupTable having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new LookupTable
	 **/

	public LookupTable(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new LookupTable having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new LookupTable
	 *  @param nameQualifier The name qualifier of the new LookupTable
	 **/

	public LookupTable(String name, String nameQualifier)
	{
		super(name, nameQualifier);
		
		fNamespaceTablesByName = new LinkedHashMap();
	}
	
	
	/**
	 * Constructs a new LookupTable having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new LookupTable (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		LookupTable will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		LookupTable		
	**/
	
	public LookupTable(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Mml.N_MAPPINGS);
		
		fNamespaceTablesByName = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Merge the contents of another LookupTable into this LookupTable. When new 
	 * NamespaceTables are defined in the specified LookupTable but they do not yet 
	 * exist in this LookupTable, they are added along with their contained mappings. 
	 * If the NamespaceTable already exists then mappings from it are either added 
	 * to this LookupTable in the appropriate NamespaceTable or they override 
	 * existing mappings in the appropriate NamespaceTable.
	 *
	 * @param lookuptable A LookupTable that should be merged into this LookupTable
	**/
	public void merge(LookupTable lookuptable) 
	{
		//---Build a replacement hash array
		Map ha = new LinkedHashMap();

		//---Build a map of existing NamespaceTables
		HashMap nstMap = new HashMap();
		Iterator i = getNamespaceTables();
		NamespaceTable nst;
		while (i.hasNext())
		{
			nst = (NamespaceTable) i.next(); 
			nstMap.put(nst.getName(), nst);
		}

		//---Process each of the NamespaceTables from the overriding LookupTable
		NamespaceTable localNst;
		i = lookuptable.getNamespaceTables();
		while (i.hasNext())
		{
			nst	  = (NamespaceTable) i.next();
			localNst = (NamespaceTable) nstMap.get(nst.getName());

			//---If NamespaceTable is new, add it to this LookupTable 
			if (localNst == null)
			{
				nstMap.put(nst.getName(), nst);
			}
			//---Otherwise merge its mappings into the appropriate NamespaceTable 
			// in this LookupTable 
			else
			{
				localNst.merge(nst);
			}
		}

		//---Populate the replacement hash array
		String name;
		i = nstMap.keySet().iterator();
		while (i.hasNext())
		{
			name = (String) i.next();	
			ha.put(name, nstMap.get(name));
		}
		fNamespaceTablesByName = ha;
	}

	/**
	 * Get the value for the key in the specified namespace. 
	 *
	 * @param  namespace Namespace to look at. 
	 * @param  key	   Key to get value for. 
	 * @return Value mapped to namespace and key. 
	 *			
	**/
	public String lookup(String namespace, String key)
	{
		String rval		= null; 
		NamespaceTable nst = getNamespaceTable(namespace);
		
		if (nst != null)
		{
			rval = nst.getValueByName(key);  
			if (rval == null)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Failed to locate key: " + key + 
					" for namespace: " + namespace;
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"lookup", message);
				}
			}
		}
		else 
		{
	   		if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Failed to locate namespace: " + 
					namespace;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"lookup", message);
			}
		}
		return rval; 
	}

	/**
	 * Get the value (as a Class) for the key in the specified namespace. 
	 *
	 * @param  namespace Namespace to look at. 
	 * @param  key	   Key to get value for. 
	 * @return Value (as a Class) mapped to namespace and key. 
	**/
	public Class lookupClass(String namespace, String key)
	{
		Class  rval = null;
		String val  = lookup(namespace, key);

		if (val != null)
		{
			rval = DescriptorSerializer.getValueClass(val);
		}

		return rval;
	}

	/**
	 *  Lookup up the keys that match the given value within the specified
	 *  namespace.
	 *
	 *  @param namespace  the name space to search
	 *  @param value  the value to match
	 *
	 *  @return  a list (which may be empty) of keys that match the given
	 *		   value
	**/
	public List lookupKeys(String namespace, Object value)
	{
		List keys = null;

		//  Check the proper table for the keys.
		NamespaceTable table = getNamespaceTable(namespace);
		if (table != null)
		{
			keys = table.getKeysByValue(value);
		}

		//  If there was a problem, make sure we don't return a 'null' list.
		else
		{
			keys = new LinkedList();
			
	   		if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Failed to locate namespace: " + 
					namespace;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"lookupKeys", message);
			}
		}

		return keys;
	}

	/**
	 * Get the namespace table with the specified name. 
	 *
	 * @param  name Name of value map.
	 * @return Value map descriptor with the specified name.
	 *			
	**/
	public NamespaceTable getNamespaceTable(String name)
	{
		return (NamespaceTable) fNamespaceTablesByName.get(name);
	}

	/**
	 * Get all the namespace tables.
	 *
	 * @return All the name space tables. 
	 *			
	**/
	public Iterator getNamespaceTables()
	{
		return fNamespaceTablesByName.values().iterator();
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		fSerializer.loadChildDescriptorElements
			(Mml.E_NAMESPACE_TABLE, fNamespaceTablesByName, Mml.C_NAMESPACE_TABLE, 
				fElement, this, fDirectory);
	}
}
