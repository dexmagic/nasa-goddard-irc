//=== File Prolog ============================================================
//	This code was developed NASA Goddard Space Flight Center, Code 580 for 
//  the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.system;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.system.resources.ResourceManager;


/**
 * A SysManager as a source of system configuration and environment 
 * information, as well as of system services.
 *
 * @version $Date: 2005/12/01 20:54:00 $	
 * @author Carl F. Hostetter
 * @author Troy Ames
**/
public class DefaultSysManager implements SysManager
{
	private static final String CLASS_NAME = DefaultSysManager.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private ResourceManager fResourceManager = null;
	
	// Default ClassLoader for loading resources
	private ClassLoader fClassLoader = 
		Thread.currentThread().getContextClassLoader();
	
	/** Property name for Resource Manager class implementation. */
	public static final String RESOURCE_MANAGER_PROPERTY = "sys.resource.manager";
	private static final String DEFAULT_RESOURCE_MANAGER =
		"gov.nasa.gsfc.commons.system.resources.DefaultResourceManager";

	/** Search path separator **/
	public static final String RESOURCE_PATH_SEPARATOR = ";";
	
	/** Key to get search path for resources **/
	public static final String RESOURCE_PATH = "sys.resource.path";
	private static final String DEFAULT_RESOURCE_PATH = ".";
		
	/**
	 * Returns the default Class Loader. Some environments require specific
	 * instances of a class loader in order to find classes and resources.
	 * This method returns the default class loader defined by this class.
	 *
	 * @return the default ClassLoader
	 */
	public ClassLoader getDefaultLoader()
	{
		return fClassLoader;
	}
	
	/**
	 * Loads a class with the given string representation. A String 
	 * representation is typically of the form "<code>java.lang.String</code>";
	 * however this method also supports primitive and array representations.
	 * Examples: "<code>int</code>", "<code>java.lang.String []</code>", and
	 * "<code>byte []</code>".
	 * 
	 * @param typeString a String representation of the class to load
	 * @return the Class
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String typeString) throws ClassNotFoundException
	{
		ClassLoader loader = getDefaultLoader();
		Class typeClass = null;
		
		// Find the class for the type
		try
		{
			// Check for primitive types first since they cannot
			// be loaded from a class loader.
			// TODO is there a better way to handle primitives?
			// TODO would it be better to assume the most common case and only check for primitives and arrays if that failed.
			if (typeString.equals("boolean"))
			{
				typeClass = boolean.class;
			}
			else if (typeString.equals("boolean.class"))
			{
				typeClass = boolean.class;
			}
			else if (typeString.equals("byte"))
			{
				typeClass = byte.class;
			}
			else if (typeString.equals("byte.class"))
			{
				typeClass = byte.class;
			}
			else if (typeString.equals("short"))
			{
				typeClass = short.class;
			}
			else if (typeString.equals("short.class"))
			{
				typeClass = short.class;
			}
			else if (typeString.equals("char"))
			{
				typeClass = char.class;
			}
			else if (typeString.equals("char.class"))
			{
				typeClass = char.class;
			}
			else if (typeString.equals("int"))
			{
				typeClass = int.class;
			}
			else if (typeString.equals("int.class"))
			{
				typeClass = int.class;
			}
			else if (typeString.equals("integer"))
			{
				typeClass = int.class;
			}
			else if (typeString.equals("long"))
			{
				typeClass = long.class;
			}
			else if (typeString.equals("long.class"))
			{
				typeClass = long.class;
			}
			else if (typeString.equals("float"))
			{
				typeClass = float.class;
			}
			else if (typeString.equals("float.class"))
			{
				typeClass = float.class;
			}
			else if (typeString.equals("double"))
			{
				typeClass = double.class;
			}
			else if (typeString.equals("double.class"))
			{
				typeClass = double.class;
			}
			// Check for array types
			else if (typeString.lastIndexOf("[]") > 0)
			{
				int endIndex = typeString.lastIndexOf("[]");
				
				Class arrayType = 
					loadClass(typeString.substring(0, endIndex).trim());
				typeClass = Array.newInstance(arrayType, 0).getClass();
			}
			// Check for the specific class
			else
			{
				// Get the specific class
				typeClass = loader.loadClass(typeString);
			}
		}
		catch (ClassNotFoundException e)
		{
			String message = "Could not resolve class " + typeString;

			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"loadClass", message);
			
			// Let caller know we failed
			throw e;
		}

		return typeClass;
	}

	/**
     * Constructs an instance of a class using a <code>getInstance()</code>
     * method if it exists for a singleton class, otherwise it instantiates the
     * class using the default constructor.
     * 
     * @param className the name of the class to construct an instance of
     * @return an instance of the class or null if unsuccessful
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
	public Object instantiateClass(String className)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		Object instance = null;
		
		try
		{
			Class aClass = loadClass(className);
			
			
			// Look for a getInstance() method
			try
			{ 
				Method factoryMethod = aClass.getMethod(SysManager.GETINSTANCE, null);
				if (Modifier.isStatic(factoryMethod.getModifiers()))
				{
					// Use getInstance method
					instance =  factoryMethod.invoke(null, null);
				}
			}
			catch (NoSuchMethodException ex)
			{
				// Nothing to do here since this is the common case
			}

			if (instance == null)
			{
				// Instantiate class using default constructor
			    instance = aClass.newInstance();
			}
			
		}
		catch (Exception ex)
		{
			String message =
				"Could not instantiate class " + className + " " 
				+ ex.getLocalizedMessage();
			sLogger.logp( 
				Level.WARNING, CLASS_NAME, "instantiateClass", message, ex);
			
			if (ex instanceof ClassNotFoundException)
			{
				throw (ClassNotFoundException) ex;
			}
			else if (ex instanceof InstantiationException)
			{
				throw (InstantiationException) ex;
			}
			else if (ex instanceof IllegalAccessException)
			{
				throw (IllegalAccessException) ex;
			}
		}
		
		return instance;
	}

	/**
     * Constructs an instance of a class using the given parameters. If the
     * parameters array argument is null or empty this method will attempt to
     * create an instance using a no argument constructor. The 
     * parameters array argument cannot contain any <code>null</code> values.
     * 
     * @param aClass the class to construct an instance of
     * @param parameters the parameter array, in order or null
     * @return an instance of the class or null if unsuccessful
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public Object instantiateClass(Class aClass, Object[] parameters) throws
            InstantiationException, InvocationTargetException,
            IllegalArgumentException, IllegalAccessException
    {
        if (aClass == null)
        {
            return null;
        }
        
        // Create the instance
        Object instance = null;
        try
        {
            if (parameters != null)
            {
                // Try each constructor to create the instance
                Constructor constructors[] = aClass.getConstructors();
                for (int i=0; i<constructors.length; i++)
                {
                    try
                    {
                        instance = constructors[i].newInstance(parameters);
                        String message =
                            "Instantiated class with constructor " + constructors[i];
                        sLogger.logp( 
                            Level.FINER, CLASS_NAME, "instantiateClass", message);
                        break;
                    }
                    catch (IllegalArgumentException e)
                    {
                        // Only throw the exception if we've exhausted all the
                        // constructors
                        if (i == (constructors.length-1))
                        {
                            String message =
                                "Could not locate constructor with the supplied " +
                                "parameters for class " + aClass + " " 
                                + e.getLocalizedMessage();
                            sLogger.logp( 
                                Level.WARNING, CLASS_NAME, "instantiateClass", message, e);
                            throw e;
                        }
                    }
                }
            }
            else
            {
                // Use the default constructor
                instance = aClass.newInstance();
            }
        }
        catch (InstantiationException ex)
        {
            String message =
                "Could not instantiate class " + aClass + " " 
                + ex.getLocalizedMessage();
            sLogger.logp( 
                Level.WARNING, CLASS_NAME, "instantiateClass", message, ex);
            throw ex;
        }
        catch (IllegalAccessException ex)
        {
            String message =
                "No permission to access class " + aClass + " " 
                + ex.getLocalizedMessage();
            sLogger.logp( 
                Level.WARNING, CLASS_NAME, "instantiateClass", message, ex);
            throw ex;
        }
        catch (InvocationTargetException ex)
        {
            String message =
                "Constructor failed for class " + aClass + " " 
                + ex.getLocalizedMessage();
            sLogger.logp( 
                Level.WARNING, CLASS_NAME, "instantiateClass", message, ex);
            throw ex;
        }
        
        return instance;
    }

	/**
	 * Returns the ResourceManager associated with this application. If the 
	 * resource manager has not been intialized this method will call
	 * <code>loadResourceManager</code> method.
	 * 
	 * @return The ResourceManager associated with this application.
	 */
	public synchronized ResourceManager getResourceManager()
	{
		if (fResourceManager == null)
		{
			fResourceManager = loadResourceManager();
		}
		
		return (fResourceManager);
	}

	/**
	 * Loads the resource manager defined by the 
	 * {@link #REFERENCE_MANAGER_PROPERTY REFERENCE_MANAGER_PROPERTY}
	 * System property. If property is not defined then this method loads the 
	 * {@link gov.nasa.gsfc.commons.system.resources.DefaultResourceManager DefaultResourceManager} 
	 * class.
	 * 
	 * @return a resource manager or null if load failed.
	 */
	protected synchronized ResourceManager loadResourceManager()
	{
		ResourceManager manager = null;
		
		String className =
			System.getProperty(
				RESOURCE_MANAGER_PROPERTY, DEFAULT_RESOURCE_MANAGER);
		
		try
		{
			Class managerClass = getDefaultLoader().loadClass(className);
			manager = (ResourceManager) managerClass.newInstance();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		//  Configure the search path of the resource manager.
		String sysPaths = System.getProperty(
			RESOURCE_PATH, DEFAULT_RESOURCE_PATH);
		manager.setSearchPaths(sysPaths, RESOURCE_PATH_SEPARATOR);
		
		return manager;
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: DefaultSysManager.java,v $
// Revision 1.15  2005/12/01 20:54:00  tames_cvs
// Merged ASF changes into IRC. Changes were with respect to user preferences.
//
// Revision 1.14  2005/07/19 17:56:45  tames_cvs
// Changed loadClass method to handle primitive names of the form
// primitive_type.class such as "int.class".
//
// Revision 1.13  2005/05/23 19:55:22  tames_cvs
// Relocated the default class loader to the DefaultSysManager and
// added get methods to Sys and SysManager.
//
// Revision 1.12  2005/01/07 21:32:00  tames
// Added instantiateClass(Class aClass, Object[] parameters) method.
//
// Revision 1.11  2005/01/07 20:14:59  tames
// Updated loadClass method to handle more diverse String representations
// of Class types.
//
// Revision 1.10  2004/12/03 13:28:00  smaher_cvs
// Added support for instantiating a singleton in SysMgr.instantiateClass().
//
// Revision 1.9  2004/09/07 19:57:44  tames
// Added loadClass capability and instantiation from a singleton defined in
// the typemap file.
//
// Revision 1.8  2004/09/05 13:28:35  tames
// Properties and preferences clean up
//
// Revision 1.7  2004/08/03 20:24:00  tames_cvs
// Changes to reflect instantiateClass exception handling changes
//
// Revision 1.6  2004/07/12 14:26:23  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.5  2004/06/30 03:21:18  tames_cvs
// Removed direct references to boot properties.
//
// Revision 1.4  2004/06/01 15:40:04  tames_cvs
// Added instatiateClass method
//
// Revision 1.2  2004/05/28 22:02:30  tames_cvs
// Bug fixes and testing
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
//