//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 for 
//	the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorSerializer.java,v $
//  Revision 1.23  2006/08/02 20:41:38  chostetter_cvs
//  Made compatible with Java 1.4
//
//  Revision 1.22  2006/07/25 16:33:47  smaher_cvs
//  Added support for embedding properties in attributes of type int, long, and boolean.
//
//  Revision 1.21  2006/02/01 22:17:44  chostetter_cvs
//  Fixed bug with multiple anonymous child Elements in child item Map
//
//  Revision 1.20  2005/07/19 18:01:01  tames_cvs
//  Changed getValueClass method to use Irc.loadClass.
//
//  Revision 1.19  2005/05/23 15:27:47  tames_cvs
//  Added workaround to handle XML that does not use namespace. This is
//  needed until the schema and descriptor code is updated to be consistent.
//
//  Revision 1.18  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.17  2005/02/01 16:46:45  tames
//  updated method names from localized names to display name.
//
//  Revision 1.16  2005/01/07 20:31:13  tames
//  Changed localizedName to displayName to match bean naming
//  conventions.
//
//  Revision 1.15  2004/12/04 04:19:15  jhiginbotham_cvs
//  Variable resolution for attribute values in xml backed by descriptors.
//
//  Revision 1.14  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.13  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.12  2004/09/16 13:30:57  jhiginbotham_cvs
//  Fix bug with loadListElement.
//
//  Revision 1.11  2004/09/14 16:22:58  chostetter_cvs
//  Cleaned up code and comments
//
//  Revision 1.10  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.9  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.8  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.7  2004/08/06 14:24:28  tames_cvs
//  javadoc change only
//
//  Revision 1.6  2004/07/27 21:09:00  tames_cvs
//  Schema changes
//
//  Revision 1.5  2004/07/19 21:18:35  tames_cvs
//  *** empty log message ***
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
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description.xml;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

import gov.nasa.gsfc.commons.app.plugins.PluginClassLoader;
import gov.nasa.gsfc.commons.app.preferences.PreferenceManager;
import gov.nasa.gsfc.commons.types.maps.MultivalueMap;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The singleton class provides support for marshalling and unmarshalling 
 * descriptors to/from XML. It contains methods that build upon those 
 * that work with JDOM elements and attributes thus allowing for loading
 * of XML patterns that map to Java data structures. Additional support
 * is provided for creating objects/classes using reflection. In general
 * the exceptions and errors encountered by this class are development 
 * and integration related and should not occur during normal operation.<P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/08/02 20:41:38 $ 
 * @author John Higinbotham (Emergent Space Technologies)	
 * @author Troy Ames 	
**/

public class DescriptorSerializer
{
	private static final String CLASS_NAME = 
		DescriptorSerializer.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	//--- Shared error/exception messages
	private final static String MSG1 = "Attempt to load class from XML failed: ";
	private final static String MSG2 = "Attempt to get value object for class failed: ";
	private final static String MSG3 = "InvocationTargetException message: ";

   // private final static String PKG_PREFIX = "gov.nasa.gsfc.irc.description.xml.";
    private static DescriptorSerializer fSerializer = null;  // Singleton 
    private PreferenceManager fPrefMgr = null;
    
    private int fAnonymousItemCounter = 0;

//---------------------------------------------------------------------------

    /**
     * Singleton constructor.
    **/
    private DescriptorSerializer()
    {
    		fPrefMgr = Irc.getPreferenceManager();
    }

    /**
     * Get instance of descriptor serializer. 
     *
     * @return DescriptorSerializer
    **/
	public static DescriptorSerializer getInstance()
	{
		if (fSerializer == null)
		{
			fSerializer = new DescriptorSerializer();
		}
		return fSerializer;
	}

    /**
     * Load a string attribute from an element if it exists. Return the 
     * specified default value if the attribute is not found.
     *
     * @param	name 	Name of attribute. 
     * @param	defaultValue Default value to return if attribute does not exist. 
     * @param	element Element to load from. 
     * @return Value of attribute or default value if not found. 
    **/
    public String loadStringAttribute(String name, String defaultValue, Element element)
    {
        String rval = defaultValue;
        Attribute attribute = element.getAttribute(name);
        if (attribute != null)
        {
            rval = attribute.getValue();
        }
        if (rval != null)
        {
        	rval = fPrefMgr.resolveVariables(rval);
        }
        return rval;
    }

    /**
     * Load a string attribute from an element. This call also issues 
     * a debug message if the attribute is not found. Schemas should
     * prevent this from happening, but it is useful as new xml
     * parsing support is being developed and can assist in locating
     * problems resulting from schema changes.
     *
     * @param	name	Name of attribute. 
     * @param	element	Element to load from. 
     * @return Value of attribute. 
    **/
    public String loadStringAttribute(String name, Element element)
    {
		String rval = loadStringAttribute(name, null, element);
		if (rval == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Attempt to load non-existent attribute ("
					+ name + ") from element (" + element.getName() + ")";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"loadStringAttribute", message);
			}
		}
        return rval; 
    }

    /**
     * Store a string attribute to an element. 
     *
     * @param	name 	Name of attribute. 
     * @param	value	Value of attribute. 
     * @param	element Element to store attribute in. 
    **/
    public void storeAttribute(String name, String value, Element element)
    {
		if (value != null)
		{
			element.setAttribute(name, value);
		}
    }

    /**
     * Store a boolean attribute to an element. 
     *
     * @param	name 	Name of attribute. 
     * @param	value	Value of attribute. 
     * @param	element Element to store attribute in. 
    **/
    public void storeAttribute(String name, boolean value, Element element)
    {
		Boolean b = new Boolean(value);
		element.setAttribute(name, b.toString());
    }

    /**
     * Store a int attribute to an element. 
     *
     * @param	name 	Name of attribute. 
     * @param	value	Value of attribute. 
     * @param	element Element to store attribute in. 
    **/
    public void storeAttribute(String name, int value, Element element)
    {
		element.setAttribute(name, Integer.toString(value));
	}

    /**
     * Store a long attribute to an element. 
     *
     * @param	name 	Name of attribute. 
     * @param	value	Value of attribute. 
     * @param	element Element to store attribute in. 
    **/
    public void storeAttribute(String name, long value, Element element)
    {
		element.setAttribute(name, Long.toString(value));
	}

    /**
     * Load a boolean attribute from an element. 
     *
     * @param	name         Name of attribute. 
     * @param	defaultValue Default value if not present. 
     * @param	element      Element to load from. 
     * @return Value of attribute. 
    **/
    public boolean loadBooleanAttribute(String name, boolean defaultValue, Element element) 
    {
        boolean rval = defaultValue;
        try
        {
            Attribute attribute = element.getAttribute(name);
			if (attribute != null)
			{
				String value = fPrefMgr.resolveVariables(attribute.getValue());
				rval = new Boolean(value).booleanValue();
			}	
        }
        catch (NumberFormatException e)
        {
			//--- This should only happen during code developement, schema changes, or non-validting parsing
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Attempt to load non-boolean XML attribute as boolean: " + 
					name;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadBooleanAttribute", message);
			}
        }
        return rval;
    }

    /**
     * Load an int attribute from an element. 
     *
     * @param	name         Name of attribute. 
     * @param	defaultValue Default value if not present. 
     * @param	element      Element to load from. 
     * @return Value of attribute. 
    **/
    public int loadIntAttribute(String name, int defaultValue, Element element) 
    {
        int rval = defaultValue;
        try
        {
            Attribute attribute = element.getAttribute(name);
			if (attribute != null)
			{
				rval = Integer.parseInt(fPrefMgr.resolveVariables(attribute.getValue()));
			}
        }
        catch (NumberFormatException e)
        {
			//--- This should only happen during code developement, schema changes, or non-validting parsing
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Attempt to load non-int XML attribute as int: " + 
					name;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadBooleanAttribute", message);
			}
        }
        return rval;
    }

    /**
     * Load a long attribute from an element. 
     *
     * @param	name         Name of attribute. 
     * @param	defaultValue Default value if not present. 
     * @param	element      Element to load from. 
     * @return Value of attribute. 
    **/
    public long loadLongAttribute(String name, long defaultValue, Element element) 
    {
        long rval = defaultValue;
        try
        {
            Attribute attribute = element.getAttribute(name);
			if (attribute != null)
			{
				rval = Long.parseLong(fPrefMgr.resolveVariables(attribute.getValue()));
			}			
        }
        catch (NumberFormatException e)
        {
			//--- This should only happen during code developement, schema changes, or non-validting parsing
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Attempt to load non-long XML attribute as long: " + 
					name;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadBooleanAttribute", message);
			}
        }
        return rval;
    }

    /**
     * Load a descriptor element into a specified destination.
     *
     * @param	name          Name of element. 
     * @param	destination   Storage for elements. 
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	altName       Specify the attribute to be use as a name 
     * @param	element       Element to load from. 
     * @param	parent        Manager of new descriptor. 
     * @param	directory     Descriptor directory for new descriptor.
    **/
    public void loadDescriptorElement(String name, Map destination, String itemClassName, 
		String altName, Element element, Descriptor parent, DescriptorDirectory directory) 
	{
		Class[] sigArray = {Descriptor.class, DescriptorDirectory.class, Element.class};

		try
		{
			Class itemClass = Class.forName(itemClassName);
			Constructor constructor = itemClass.getConstructor(sigArray);
			Object[] parmArray = new Object[]{parent, directory, element};
			Object newItem = constructor.newInstance(parmArray);
	
			String elementName = loadStringAttribute(altName, null, element); 
	
			if (elementName != null) 
			{
				//--- Store a named descriptor 
				destination.put(elementName, newItem);
			}
			else 
			{
				fAnonymousItemCounter++;
				String key = "Anonymous Item " + fAnonymousItemCounter;
				
				//--- Store an unnamed descriptor 
				destination.put(key, newItem);
			}
		}
		catch (InvocationTargetException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = MSG1 + itemClassName + " " + ex.getLocalizedMessage();
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadDescriptorElement", message);
				
				message = MSG3 + ex.getTargetException().getMessage();
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadDescriptorElement", message, ex);
			}
		}
		catch (Exception ex)
		{
			//--- One of (IlleagalAccessException, InstantiationException, 
			//  ClassCastException, ClassNotFoundException, NoSuchMethodException)
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = MSG1 + itemClassName + " " + ex.getLocalizedMessage();
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadDescriptorElement", message, ex);
			}
		}
	}

    /**
     * Load a descriptor element into a specified destination.
     *
     * @param	name          Name of element. 
     * @param	destination   Storage for elements. 
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	element       Element to load from. 
     * @param	parent        Manager of new descriptor. 
     * @param	directory     Descriptor directory for new descriptor.
    **/
    public void loadDescriptorElement(String name, Map destination, String itemClassName, 
		Element element, Descriptor parent, DescriptorDirectory directory) 
	{
    		loadDescriptorElement(name, destination, itemClassName, 
    			Xsd.A_NAME, element, parent, directory);
	}

    /**
     * Load a collection of child descriptor elements and attributes into a 
     * specified destination.
     *
     * @param	name          Name of element. 
     * @param	destination   Storage for elements. 
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	altName       Specify the attribute to be use as a name 
     * @param	element       Element to load from. 
     * @param	parent        Manager of new descriptor. 
     * @param	directory     Descriptor directory for new descriptor.
    **/
    public void loadChildDescriptorElements(String name, Map destination, 
    		String itemClassName, String altName, Element element, Descriptor parent, 
		DescriptorDirectory directory) 
	{
    	// For backwards compatibility need to ignore namespace
    	// TODO namespace should be passed in as a parameter and the if in the for loop removed
		//List childList = element.getChildren(name, element.getNamespace());
		List childList = element.getChildren();

		for (Iterator i = childList.iterator(); i.hasNext(); )
		{
			Element subElement = (Element) i.next();
			
			// Check if element is matches what we are looking for
			if (subElement.getName().equals(name))
			{
				loadDescriptorElement(name, destination, itemClassName, altName, 
					subElement, parent, directory);
			}
		}
	}

    /**
     * Load a collection of child descriptor elements and attributes into a 
     * specified destination.
     *
     * @param	name          Name of element. 
     * @param	destination   Storage for elements. 
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	element       Element to load from. 
     * @param	parent        Manager of new descriptor. 
     * @param	directory     Descriptor directory for new descriptor.
    **/
    public void loadChildDescriptorElements(String name, Map destination, 
    		String itemClassName, Element element, Descriptor parent, 
		DescriptorDirectory directory) 
	{
    		loadChildDescriptorElements(name, destination, itemClassName, 
    			Xsd.A_NAME, element, parent, directory);
	}

    /**
     * Load and return a single child descriptor element. 
     *
     * @param	name          Name of element. 
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	element       Element to load from. 
     * @param	parent        Manager of new descriptor. 
     * @param	directory     Descriptor directory for new descriptor.
	 * @return Object.
    **/
	public Object loadSingleChildDescriptorElement(String name, String itemClassName, 
		Element element, Descriptor parent, DescriptorDirectory directory) 
	{
		Object rval = null;
		Map ha = new LinkedHashMap();
        loadChildDescriptorElements(name, ha, itemClassName, element, parent, directory);
		Iterator i = ha.values().iterator(); 
		if (i.hasNext())
		{
			rval = i.next();
		}
		return rval;
	}

    /** 
     * Store a collection of elements and attributes into a specified descriptor.
     *
     * @param	name          Name of element. 
     * @param	descriptor    Descriptor to store.
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	element       Element to load from. 
     * @param	directory     Descriptor directory for new descriptor.
    **/
    public void storeDescriptorElement(String name, Descriptor descriptor, String itemClassName, 
		Element element, DescriptorDirectory directory) 
	{
		Map ha = new LinkedHashMap();
		ha.put(descriptor.getName(), descriptor);
    		storeDescriptorElement(name, ha, itemClassName, element, directory);
	}

    /** 
     * Store a collection of elements and attributes into a specified descriptor.
     *
     * @param	name          Name of element. 
     * @param	source        Source of elements. 
     * @param	itemClassName Shorthand Class name of descriptor being loaded. 
     * @param	element       Element to load from. 
     * @param	directory     Descriptor directory for new descriptor.
    **/
    public void storeDescriptorElement(String name, Map source, String itemClassName, 
		Element element, DescriptorDirectory directory) 
	{
		//--- Get a hold of the xmlMarshall method for the respective class
		Class  itemClass     = null;
		Class[] sigArray     = {Element.class};
		Method  method       = null;

		try 
		{
			itemClass          = Class.forName(itemClassName);
			method             = itemClass.getMethod("xmlMarshall", sigArray);
			Object[] parmArray = new Object[1];

			for (Iterator i = source.values().iterator(); i.hasNext(); )
			{
				try 
				{
					Descriptor d = (Descriptor) i.next();

					//---Here there are 2 approaches for marshalling off a 
					//   descriptor. The first is that we will build everything
					//   from scratch. The second is that we will use what
					//   already exists and modify it as necessary. This second
					//   approach is also known as the hybrid marshalling approach
					//   and is currently used by the IML where only command procedures
					//   are mutable at this time.

					if (element != null)
					{
						//---Create a new element for the ith descriptor
						Element subElement = new Element(name); 

						//---Store that element in the parent element
						element.addContent(subElement);

						//---Invoke the xmlUnmarshall method on the ith descriptor 
						parmArray[0]  = subElement;
					}
					else
					{
						parmArray[0] = null;
					}
					method.invoke(d, parmArray);
				}
				catch (Exception ex)
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = "Encountered problems while storing: " + 
							itemClassName;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"storeDescriptorElement", message, ex);
					}
				}
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Encountered problems while storing: " + 
					itemClassName;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"storeDescriptorElement", message, ex);
			}
		}
	}

    /**
     * Load a reference to another descriptor. 
     *
     * @param	name          Name of element.
     * @param	attName       Name of reference attribute.
     * @param	destination   Storage for references. 
     * @param	element       Element to load from. 
    **/
    public void loadReferenceElement(String name, String attName, Map destination, Element element)
    {
        List   childList = element.getChildren(name, element.getNamespace());
        String longName;

        for (Iterator i = childList.iterator(); i.hasNext(); )
        {
            Element subElement = (Element) i.next();
            longName = loadStringAttribute(attName, subElement);
            destination.put(longName.substring(longName.lastIndexOf(".")), longName);
        }
    }

    /**
     * Load a java hash map element. 
     *
     * @param	tagName       Name of tag. 
     * @param	keyName       Name of key. 
     * @param	valueName     Name of value. 
     * @param	destination   Storage for elements. 
     * @param	element       Element to load from. 
    **/
    public void loadKeyValueElement(String tagName, String keyName, String valueName, Map destination, Element element)
	{
    		loadKeyValueElement(tagName, keyName, valueName, destination, false, element);
	}

    /**
     * Load a java hash map element. 
     *
     * @param	tagName       Name of tag. 
     * @param	keyName       Name of key. 
     * @param	valueName     Name of value. 
     * @param	destination   Storage for elements. 
     * @param	keyGen        True to generate missing keys, false not to 
     * @param	element       Element to load from. 
    **/
    public void loadKeyValueElement(String tagName, String keyName, 
    		String valueName, Map destination, boolean keyGen, Element element)
    {
        List   childList = null;

        childList = element.getChildren(tagName, element.getNamespace());
		Iterator i = childList.iterator();
		int index = 0;
		while (i.hasNext())
        {
            Element subElement = (Element) i.next();
            String key         = loadStringAttribute(keyName, subElement);
            String value       = loadStringAttribute(valueName, subElement);
            
            value = fPrefMgr.resolveVariables(value);
            
			if (key != null)
			{
            		destination.put(key, value);
			}
			else
			{
				if (keyGen)
				{
            			destination.put("key_" + Integer.toString(index), value);
				}
				else
				{
            			destination.put(value, value);
				}
			}
			index++;
        }
    }

    /**
     * Load a MultivalueMap element. 
     *
     * @param	tagName       Name of tag. 
     * @param	keyName       Name of key. 
     * @param	valueName     Name of value. 
     * @param	destination   Storage for elements. 
     * @param	element       Element to load from. 
    **/
    public void loadMultivalueMapElement(String tagName, String keyName, String valueName,
		MultivalueMap destination, Element element)
    {
        List   childList = null;

        childList = element.getChildren(tagName, element.getNamespace());
        for (Iterator i = childList.iterator(); i.hasNext(); )
        {
            Element subElement = (Element) i.next();
            String key   = loadStringAttribute(keyName, subElement);
            String value = loadStringAttribute(valueName, subElement);
            destination.put(key, value);
        }
    }

    /**
     * Store a MultivalueMap element. 
     *
     * @param	tagName       Name of tag. 
     * @param	keyName       Name of key. 
     * @param	valueName     Name of value. 
     * @param	source 		  Source of elements. 
     * @param	element       Element to load from. 
    **/
    public void storeMultivalueMapElement(String tagName, String keyName, String valueName,
		MultivalueMap source, Element element)
    {
		Iterator i = source.keys();
		Iterator j = null;
		String key;
		String val;
		Element subElement;

		while (i.hasNext())
        {
			key = (String) i.next();
			j = source.get(key);	
			while (j.hasNext())
			{
				val = (String) j.next();	
				subElement = new Element(tagName);
            	storeAttribute(keyName, key, subElement);
            	storeAttribute(valueName, (String) val, subElement);
				element.addContent(subElement);
			}
        }
    }

    /**
     * Load a java array list element. 
     *
     * @param	tagName       Name of tag. 
     * @param	valueName     Name of value. 
     * @param	destination   Storage for elements. 
     * @param	element       Element to load from. 
    **/
    public void loadListElement(String tagName, String valueName, List destination, 
    		Element element)
    {
    		List childList = element.getChildren(tagName, element.getNamespace());
    		
        for (Iterator i = childList.iterator(); i.hasNext(); )
        {
            Element subElement = (Element) i.next();
            String value = loadStringAttribute(valueName, subElement);
            destination.add(value);
        }
    }

	public void storeListElement(String tagName, String valueName, List source, 
		Element element)
	{
		Iterator i = source.iterator();
		String value;
		Element subElement;

		while (i.hasNext())
		{
			//---Get the value to be stored in the attribute
			value = i.next().toString();

			//---Create a new element for the ith descriptor
			subElement = new Element(tagName);

			//---Set the attribute value in the new element
			storeAttribute(valueName, value, subElement);

			//---Store that element in the parent element
			element.addContent(subElement);
		}
	}

    /**
     * Load a int[] from XML. 
     *
     * @param	tagName       Name of tag. 
     * @param	valueName     Name of value. 
     * @param	element       Element to load from. 
     * @return Array of ints or null if none found.
     **/
    public int[] loadIntArrayElement(String tagName, String valueName, Element element) 
    {
       return loadIntArrayElement(tagName, valueName, element, false);
    }

    /**
     * Load an int[] from XML that represents a collection of dimensions. 
     * This method is simlar to loadIntArrayElement except that additional
     * processing is done on the elements of the array. Additional processing 
     * insures that negative numbers are not entered for dimensions. It also
     * insures that a "*" entered for a dimension will be translated to a "-1"
     * in the returned array. The "*" is used to indicate that a particular 
     * dimension varies in size.
     *
     * @param	tagName       Name of tag. 
     * @param	valueName     Name of value. 
     * @param	element       Element to load from. 
     * @return Array of ints or null if none found.
    **/
    public int[] loadDimensionIntArrayElement(String tagName, String valueName, Element element) 
    {
       return loadIntArrayElement(tagName, valueName, element, true);
	}
	
    /**
     * Load an int[] from XML. If the array is to be treated as a list of dimensions, 
	 * the dimensions flag shoud be set to true. Additional processing is performed if 
	 * processing a collection of dimensions. First no integer less that one is permitted. 
	 * Second, a "*" in the orginal array will be converted to a -1 to indicate a variable
     * dimension. 
     *
     * @param	tagName       Name of tag. 
     * @param	valueName     Name of value. 
     * @param	element       Element to load from. 
     * @param	dimensions    Set to true if dimension processing should be applied. 
     * @return Array of ints or null if none found.
    **/
    private int[] loadIntArrayElement(String tagName, String valueName, Element element, boolean dimensions) 
    {
        ArrayList tmpArray  = new ArrayList();               // Temporary collection of dimensions
        List  childList     = element.getChildren(tagName, element.getNamespace());  // List of children to process
        int[] rval          = null;                          // Dimension array returned to caller
		int integer_value   = 0;                             // An int from a child element

		// Determine if there is anything to do.
        if (childList.size() > 0)
		{
			// Process each child 
        	for (Iterator i = childList.iterator(); i.hasNext(); )
        	{
				String numString   = null;
				Element subElement = (Element) i.next();
				try 
				{
					// Retreive the attribute from the element and try to convert it to an Integer
					numString      = loadStringAttribute(valueName, subElement);
					Integer tmpInt = new Integer(numString);
					integer_value  = tmpInt.intValue();
				
					// Dimensions of < 1 don't make sense here so don't allow them 
					if (dimensions && (integer_value < 1))
					{
						// issue a warning about the bogus nature of a number
						if (sLogger.isLoggable(Level.SEVERE))
						{
							String message = "Attempt to store a dimension < 1 found in XML. Dimension ignored.";
							
							sLogger.logp(Level.SEVERE, CLASS_NAME, 
								"loadIntArrayElement", message);
						}
					}
					else
					{
						tmpArray.add(tmpInt);
					}
				}
				catch (NumberFormatException e)
				{ 
					// We are here because we did not get a Integer when we tried to
					// to convert the child attribute above.

					if (dimensions && numString.equals("*"))
					{
						// We have stumbled across a variable dimension
						// and we want to store these as -1 in the array returned
						tmpArray.add(new Integer("-1"));
					}
					else
					{
						// Otherwise we got something we did not expect and need to complain.
						if (sLogger.isLoggable(Level.SEVERE))
						{
							String message = "Attempt to convert non-int (" + numString + 
								") to int while loading int[] from XML";
							
							sLogger.logp(Level.SEVERE, CLASS_NAME, 
								"loadIntArrayElement", message);
						}
					}
				}
			}

			// Finally we need to build the int[] to be returned
			rval = toPrimitiveIntArray(tmpArray);
		}
		return  rval;
    }

    /**
     * Convert a list of integers to a int[].
     *
     * @param  List of Integers	
     * @return A primitive int[] 
    **/
    public int[] toPrimitiveIntArray(List list)
    {
		int list_size = list.size();
		int[] rval    = new int[list_size];
		Iterator i    = list.iterator();

		for (int j = 0; j < list_size; j++)
		{
			Integer ival = (Integer) i.next();  
			rval[j] = ival.intValue();
		}
		return rval;
	}

    /**
     * This method takes an iterator of descriptors supporting the  
     * getDisplayName() method and returns a Map of those
     * descriptors mapped by display name. The order of objects in the
     * iterator is preserved via the Map.
     *
     * @param  descriptors Iterator of descriptors with localized display names. 
     * @return Map of descriptors mapped by local name  
    **/
	public static Map toMapByDisplayName(Iterator descriptors)
	{
	 	Map rval = new LinkedHashMap();

		if (descriptors.hasNext())
		{
			IrcElementDescriptor d = null;
			while (descriptors.hasNext())
			{
				d = (IrcElementDescriptor) descriptors.next();
				String localName = d.getDisplayName();
				if (localName != null)
				{
					rval.put(d.getDisplayName(), d);
				}
			}
		}
	 	return rval;
	}

    /**
     * This method takes an iterator of descriptors supporting the  
     * getName() method and returns a Map of those
     * descriptors mapped by name. The order of objects in the
     * iterator is preserved via the Map.
     *
     * @param  descriptors Iterator of descriptors with names. 
     * @return Map of descriptors mapped by name  
    **/
	public static Map toMapByName(Iterator descriptors)
	{
	 	Map rval = null;

		if (descriptors.hasNext())
		{
			rval = new LinkedHashMap();
			Descriptor d = null;
			while (descriptors.hasNext())
			{
				d = (Descriptor) descriptors.next();
				String name = d.getName();
				if (name != null)
				{
					rval.put(d.getName(), d);
				}
			}
		}
	 	return rval;
	}

    /**
     * Get a new object with the designated class. The object is constructed  
     * using the string constructor method and the valueString passed in.  
     * If either of the input arguments are null, then null is returned.
     *
     * @param	valueString String to pass to constructor.
     * @param	valueClass  Class of value to be created.
     * @return New object of the designated class. 
    **/
    public Object getValueObject(String valueString, Class valueClass) 
    {
        Object rval = null;

		if (valueString != null && valueClass !=null)
		{
	        	Class[] sigArray        = {String.class}; 
	        	Constructor constructor = null;
	        	Object[] parmArray      = new Object[1];
	
	        	try
	        	{
	           	constructor  = valueClass.getConstructor(sigArray);
	            	parmArray[0] = valueString;
	            	rval         = constructor.newInstance(parmArray);
	        	}
	        	catch (InvocationTargetException ex)
	        	{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = MSG2 + valueClass;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"getValueObject", message);
	
					message = MSG3 + ex.getTargetException().getMessage();
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"getValueObject", message, ex);
				}
	        	}
	        	catch (Exception ex)
	        	{
				//--- One of (ClassCastException, NoSuchMethodException, IllegalAccessException, InstantiationException)
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = MSG2 + valueClass;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"getValueObject", message, ex);
				}
	        	}
		}
        return rval; 
    }

	/**
	 * This method is useful for fixing java strings created by File.toString() such  
	 * that they can be written to XML like other paths.
	 *
	 * @param string String to process 
	 * @return String 
	**/
	public static String toXMLFilePath(String string)
	{
		return string.replace(File.separatorChar, '/');
	}

    /**
     * Get a Class for the specified string. 
     *
     * @param	valueClassString Class string. 
     * @return Class based on the specified string. 
    **/
    public static Class getValueClass(String valueClassString) 
    {
        Class rval = null;

        try
        {
            rval = Irc.loadClass(valueClassString);
        }
        catch (ClassNotFoundException ex)
        {
			try 
			{
				PluginClassLoader cl = PluginClassLoader.getInstance();
				rval = cl.loadClass(valueClassString);
			}
			catch (Exception ex2)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Could not locate requested class: " + 
						valueClassString;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"getValueClass", message);
				}
			}
        }
        return rval; 
    }
}
