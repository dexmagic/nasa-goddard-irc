//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.system.resources.ResourceManager;

/**
 * A Sys represents a system configuration and environment and serves 
 * as a factory for a system manager. 
 *
 * At startup the SysManager class is located using the 
 * gov.nasa.gsfc.commons.system.manager.class property located in the 
 * configuration file described below or as a system property.
 * <p>
 * By default, the System Manager reads its initial configuration from 
 * a properties file "sysBootProperties.txt".
 * If you edit that property file you can change the default 
 * configuration for all uses of that System Manager.
 * <p>
 * Note that all classes loaded during SysManager configuration must
 * be on the system class path.  
 *
 * @version $Date: 2005/12/01 20:55:35 $	
 * @author Carl F. Hostetter
 * @author Troy Ames
**/
public class Sys
{
	private static final String CLASS_NAME = Sys.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	/** Termination code for a normal system exit. */
	public static final int NORMAL_TERMINATION = 0;

	/** Termination code for an error system exit. */
	public static final int ERROR_TERMINATION = 1;

	/** File name for boot properties. */
	public static final String BOOT_FILE_NAME = "sysBootProperties.txt";
	
	/** Property name for the default system manager */
	public static final String SYS_MANAGER_PROPERTY = "sys.manager";
	private static final String DEFAULT_SYS_MANAGER = 
		"gov.nasa.gsfc.commons.system.DefaultSysManager";

	//---Support for accessing resources
	private static ResourceManager sResourceMgr = null;
	private static SysManager sSysManager = null;
	private static ClassLoader sClassLoader = null;
	
	// Load the boot properties when the class is loaded
	static 
	{
		loadBootProperties();
	}

	/**
	 * Get the SysManager associated with this system.
	 *  
	 * @return The SysManager associated with this system.
	**/
	public synchronized static SysManager getSysManager()
	{
		// This method is synchronized to prevent creating multiple managers.
		if (sSysManager == null)
		{
			sSysManager = loadSysManager();
		}
		
		return (sSysManager);
	}

	/**
	 * Returns the ResourceManager associated with this application. 
	 * 
	 * @return The ResourceManager associated with this application.
	 */
	public static ResourceManager getResourceManager()
	{
		if (sResourceMgr == null)
		{
			sResourceMgr = getSysManager().getResourceManager();
		}
		
		return (sResourceMgr);
	}

	/**
	 * Returns the default Class Loader. Some environments require specific
	 * instances of a class loader in order to find classes and resources.
	 * This method returns the default class loader defined by the 
	 * {@link SysManager}.
	 *
	 * @return the default ClassLoader
	 * @see #getSysManager()
	 */
	public static ClassLoader getDefaultLoader()
	{
		if (sClassLoader == null)
		{
			sClassLoader = getSysManager().getDefaultLoader();
		}
		
		return (sClassLoader);
	}

	/**
	 * Convenience method to convert a delimited string of paths to a
	 * ordered list of paths. The characters in the <code>delim</code>
	 * argument are the delimiters for separating paths. The result can be
	 * passed to the {@link ResourceManager#getResource(String, Collection)} method.
	 *
	 * @param  paths	 A String of one or more paths
	 * @param  delim	 The path delimiter(s).
	 * @return An ordered <tt>List</tt> of paths.
	 */
	public static List pathStringToList(String paths, String delim)
	{
		ArrayList array = new ArrayList();

		if (paths!=null)
		{
			StringTokenizer t = new StringTokenizer(paths, delim);

			while (t.hasMoreTokens())
			{
				array.add(t.nextToken());
			}
		}

		return array;
	}

	/**
	 * Finds the resource with the given name. A resource is some data
	 * (images, audio, text, etc) that can be accessed by class code in a way
	 * that is independent of the location of the code.<p>
	 *
	 * @param  name resource name
	 * @return a URL for reading the resource, or <code>null</code> if
	 *		 the resource could not be found or the caller doesn't have
	 *		 adequate privileges to get the resource.
	 */
	public static URL getResource(String name)
	{
		return (getResourceManager().getResource(name));
	}

	/**
	 * Constructs an instance of a class using the SysManager.
	 * 
	 * @param className the name of the class to construct an instance of
	 * @return an instance of the class
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static Object instantiateClass(String className) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		return getSysManager().instantiateClass(className);
	}
	
	/**
     * Constructs an instance of a class using the given parameters. If the
     * parameters array argument is null or empty this method will attempt to
     * create an instance using a no argument constructor. Although the 
     * parameters array argument cannot contain any <code>null</code> values.
     * 
     * @param aClass the class to construct an instance of
     * @param parameters the parameter array, in order or null
     * @return an instance of the class or null if unsuccessful
     * @throws IllegalAccessException
     * @throws InstantiationException
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
     */
	public static Object instantiateClass(Class aClass, Object[] parameters)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException
	{
		return getSysManager().instantiateClass(aClass, parameters);
	}
	
	/**
	 * Loads a Class using the SysManager.
	 * 
	 * @param className a String representation of the Class to load
	 * @return the Class
	 * @throws ClassNotFoundException
	 */
	public static Class loadClass(String className) 
	throws ClassNotFoundException
	{
		return getSysManager().loadClass(className);
	}
	
	/**
	 * Loads the properties used during initial bootup from an optional
	 * {@link #BOOT_FILE_NAME BOOT_FILE_NAME} file 
	 * in the current directory.
	 * 
	 * @return the Properties for booting up the system
	 */
	private synchronized static void loadBootProperties()
	{		
		Properties bootProperties = new Properties();
		
		File file = new File(BOOT_FILE_NAME);
		
		try
		{
			URL url = file.toURI().toURL();
			
			if (url != null)
			{
				try
				{
					InputStream input = url.openStream();
					bootProperties.load(input);
					
					// Save boot properties as System properties.
					bootProperties.putAll(System.getProperties());
					System.setProperties(bootProperties);
				}
				catch (IOException ex)
				{
					String message =
						"Could not load boot properties from URL " 
						+ ex.getLocalizedMessage();
					sLogger.logp(
						Level.INFO, CLASS_NAME, "loadBootProperties", message);
				}
			}
			else
			{
				String message = "Could not load boot properties from a null URL";
				sLogger.logp(Level.INFO, CLASS_NAME, "loadBootProperties", message);
			}
		}
		catch (MalformedURLException e)
		{
			String message =
				"Could not load boot properties from the file "
					+ e.getLocalizedMessage();
			sLogger.logp(Level.INFO, CLASS_NAME, "loadBootProperties", message);
		}
	}

	/**
	 * Loads the system manager defined by the 
	 * {@link #SYS_MANAGER_PROPERTY SYS_MANAGER_PROPERTY}
	 * boot property. If property is not defined then this method loads the 
	 * {@link gov.nasa.gsfc.commons.system.DefaultSysManager DefaultSysManager} 
	 * class.
	 * 
	 * @return a system manager or null if load failed.
	 */
	private static SysManager loadSysManager()
	{
		SysManager manager = null;
		
		String className =
			System.getProperty(SYS_MANAGER_PROPERTY, DEFAULT_SYS_MANAGER);
		
		try
		{
			Class managerClass = 
				Thread.currentThread().getContextClassLoader().loadClass(className);
//				ClassLoader.getSystemClassLoader().loadClass(className);
			manager = (SysManager) managerClass.newInstance();
		}
		catch (Exception ex)
		{
			String message =
				"Could not load Sys Manager class " + className + " " 
				+ ex.getLocalizedMessage();
			sLogger.logp( 
				Level.INFO, CLASS_NAME, "loadSysManager", message);
		}
		
		return manager;
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: Sys.java,v $
// Revision 1.18  2005/12/01 20:55:35  tames_cvs
// Merged ASF changes into IRC. Changes were with respect to
// the instantiateClass method.
//
// Revision 1.17  2005/05/23 19:55:22  tames_cvs
// Relocated the default class loader to the DefaultSysManager and
// added get methods to Sys and SysManager.
//
// Revision 1.16  2005/04/06 19:31:53  chostetter_cvs
// Organized imports
//
// Revision 1.15  2005/01/07 21:32:00  tames
// Added instantiateClass(Class aClass, Object[] parameters) method.
//
// Revision 1.14  2005/01/07 20:14:59  tames
// Updated loadClass method to handle more diverse String representations
// of Class types.
//
// Revision 1.13  2004/09/07 19:57:44  tames
// Added loadClass capability and instantiation from a singleton defined in
// the typemap file.
//
// Revision 1.12  2004/09/05 13:28:35  tames
// Properties and preferences clean up
//
// Revision 1.11  2004/08/03 20:24:00  tames_cvs
// Changes to reflect instantiateClass exception handling changes
//
// Revision 1.10  2004/07/12 14:26:23  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.9  2004/07/11 21:24:54  chostetter_cvs
// Organized imports
//
// Revision 1.8  2004/06/30 03:20:28  tames_cvs
// Changed how boot properties are loaded.
//
// Revision 1.5  2004/06/01 15:42:16  tames_cvs
// Delegated implementation of the instantiateClass method 
// to the SysManager implementation.
//
// Revision 1.3  2004/05/28 22:02:30  tames_cvs
// Bug fixes and testing
//
// Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
// Further tweaks for new structure, design
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
//
