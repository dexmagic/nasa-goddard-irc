//=== File Prolog ============================================================
//	This code was developed by AppNet, Inc. and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
// $Log: App.java,v $
// Revision 1.10  2004/09/05 13:28:35  tames
// Properties and preferences clean up
//
// Revision 1.9  2004/08/03 20:24:00  tames_cvs
// Changes to reflect instantiateClass exception handling changes
//
// Revision 1.8  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.7  2004/06/30 03:21:40  tames_cvs
// Removed direct references to boot properties.
//
// Revision 1.6  2004/06/04 14:15:11  tames_cvs
// Modified dumpThreads method
//
// Revision 1.5  2004/06/01 15:50:19  tames_cvs
// Changes to reflect instantiateClass method modifications
//
// Revision 1.4  2004/05/28 22:00:55  tames_cvs
// many bug fixes
//
// Revision 1.3  2004/05/12 22:35:15  chostetter_cvs
// Added some Resource directory static methods. May want to move these elsewhere eventually.
//
// Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
// Further tweaks for new structure, design
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
//
//
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

package gov.nasa.gsfc.commons.app;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.app.preferences.PrefKeys;
import gov.nasa.gsfc.commons.app.preferences.PreferenceManager;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.commons.system.io.FileUtil;


/**
 * An App represents the runtime configuration and environment of a 
 * particular instance of an application.
 *
 * @version $Date: 2004/09/05 13:28:35 $	
 * @author Carl F. Hostetter
 * @author Troy Ames
**/
public class App extends Sys
{
	private static final String CLASS_NAME = App.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static PreferenceManager sPreferenceManager = null;
	private static AppManager sAppManager = null;
	
	private static final String FILE_URL_PREFIX = "file:/";

	/** Property name for the default application manager */
	public static final String APP_MANAGER_PROPERTY = "app.manager";
	private static final String DEFAULT_APP_MANAGER = 
		"gov.nasa.gsfc.commons.app.DefaultAppManager";

	/**
	 * Get the Application Manager associated with this application.
	 *  
	 * @return The AppManager associated with this application.
	**/
	public synchronized static AppManager getAppManager()
	{
		// This method is synchronized to prevent creating multiple managers.
		if (sAppManager == null)
		{
			sAppManager = loadAppManager();
		}
		
		return (sAppManager);
	}

	/**
	 * Returns the PreferenceManager associated with this application. If the 
	 * preference manager has not been intialized this method will call
	 * <code>loadPreferenceManager</code> method.
	 * 
	 * @return The PreferenceManager associated with this application.
	 */
	public static PreferenceManager getPreferenceManager()
	{
		if (sPreferenceManager == null)
		{
			sPreferenceManager = getAppManager().getPreferenceManager();
		}
		
		return (sPreferenceManager);
	}

	/**
	 * The main method of this application.
	 * 
	**/
	public static void main(String argv[])
	{
		
	}
	

	/**
	 * returns a URL representing the full path to the resources
	 * directory based on the user-specified property, or null
	 * if the property has not been specified
	 */
	public static URL getResourcesDirectoryURL()
	{
		// Determine what the relative resources directory is
		String relativeResourcesDir = System.getProperty(
				PrefKeys.RESOURCES_DIR);

		URL relativeResourcesDirUrl;

		try
		{
			relativeResourcesDirUrl =
				getResourceManager().getResource(relativeResourcesDir);
		}
		catch(Exception e)
		{
			relativeResourcesDirUrl = null;
		}

		return relativeResourcesDirUrl;
	}


	/**
	 * returns a path which is relative to the resources directory, or
	 * the entire path if it is not relative
	 */
	public static String getRelativeToResourcesPath(File file)
	{
		URL relativeUrl = null;
		String remainder = null;

		String relativeResourcesDir = System.getProperty(PrefKeys.RESOURCES_DIR);

		try
		{
			relativeUrl = file.toURL();

			//---Get URL for relative script dir
			URL relativeResourcesDirUrl = getResourcesDirectoryURL();
			//---Does the new location refer to something in
			//the relative script directory?
			if (relativeUrl.toString().indexOf(
					relativeResourcesDirUrl.toString()) == 0)
			{
				//---Determine length of matching parts
				int len = relativeResourcesDirUrl.toString().length();
				//---Get a hold of the part of the new location
				// that is different
				remainder = relativeResourcesDir +
						relativeUrl.toString().substring(len);
			}
			else
			{
				//---Save url file path
				remainder = relativeUrl.toString().substring(
						FILE_URL_PREFIX.length());
			}
		}
		catch(Exception e)
		{
			remainder = file.getPath();
		}

		// remove trailing slash if there is one
		if(remainder.endsWith("/"))
		{
			remainder = remainder.substring(0, remainder.length() - 1);
		}

		return remainder;
	}
	

	/**
	 * If the given file's path is relative to the resources
	 * directory then it is converted to a full path based on
	 * the resources path. Otherwise, the existing path is
	 * returned
	 */	
	public static String convertToResourcesPath(File file)
	{
		String convertedPath;

		String relativeResourcesDir = System.getProperty(PrefKeys.RESOURCES_DIR);

		try
		{
			// get the URL for the resources directory
			URL relativeResourcesDirUrl = getResourcesDirectoryURL();
			String path = relativeResourcesDirUrl.toString().substring(
					FILE_URL_PREFIX.length());

			// convert the given file to a URL
			URL fileUrl = file.toURL();
			// get the part after the resources path
			int index = fileUrl.toString().lastIndexOf(relativeResourcesDir);
			if(index > -1)
			{
				// relative
				String filePath = fileUrl.toString().substring(index +
						relativeResourcesDir.length());
				convertedPath = path + filePath;
			}
			else
			{
				// not relative
				convertedPath = file.getPath();
			}
		}
		catch(Exception e)
		{
			convertedPath = file.getPath();
		}

		// remove trailing slash if there is one
		if(convertedPath.endsWith("/"))
		{
			convertedPath = convertedPath.substring(
					0, convertedPath.length() - 1);
		}

		// create a file so we get it in the system format
		File conversionFile = new File(convertedPath);
		return conversionFile.getPath() ;
	}
	
	
	/**
	 * Get url from property value. The value may either be a full path 
	 * or a relative path.
	 *
	 * @param property Name of system property
	 * @return URL
	**/
	public static URL getUrlFromProperty(String property)
	{
		String propertyValue =
			getPreferenceManager().getPreference(property);

		URL url = getResourceManager().getResource(propertyValue);

		if (url == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "URL is null for property";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"getUrlFromProperty", message, property);
			}
		}
		
		return url;
	}


	/**
	 * Get file from property value. The value may either be a full path to a file
	 * or a relative path based on the resources directory.
	 *
	 * @param property Name of system property
	 * @return File
	**/
	public static File getFileFromProperty(String property)
	{
		File rval = null;
		String propertyValue =
			getPreferenceManager().getPreference(property);
		URL url = getResourceManager().getResource(propertyValue);

		if (url != null)
		{
			rval  = FileUtil.urlToFile(url);
		}

		if (rval == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "File not found";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"getFileFromProperty", message, property);
			}
		}
		
		return rval;
	}
	
	/**
	 * Gets the preference value indicated by the specified key. The method 
	 * returns <code>null</code> if the preference is not found.
	 *
	 * @param   key   the preference key.
	 * @return  the value for the specified key.
	 */
	public static String getPreference(String key)
	{
		return (getPreferenceManager().getPreference(key));
	}

	/**
	 * Dump a list of current threads to System out.
	 */
	public static void dumpThreads()
	{
		int ac = Thread.activeCount();
		Thread threads[] = new Thread[ac];
		ac = Thread.enumerate(threads);

		System.out.println("Currently "
				+ ac + " active Threads in group "
				+ threads[0].getThreadGroup().getName());

		for (int i = 0; i < ac; ++i)
		{
			System.out.print(i + " " + threads[i].toString());
			
			if (threads[i].isDaemon())
			{
				System.out.print(" [daemon]");
			}
			
			System.out.println("");
		}
	}

	/**
	 * Loads the application manager defined by the 
	 * {@link #APP_MANAGER_PROPERTY APP_MANAGER_PROPERTY}
	 * boot property. If property is not defined then this method loads the 
	 * {@link gov.nasa.gsfc.commons.app.DefaultAppManager DefaultAppManager} 
	 * class.
	 * 
	 * @return an application manager or null if load failed.
	 */
	private static AppManager loadAppManager()
	{
		AppManager manager = null;
		
		String className =
			System.getProperty(APP_MANAGER_PROPERTY, DEFAULT_APP_MANAGER);
		
		try
		{
			manager = (AppManager) instantiateClass(className);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return manager;
	}
}
