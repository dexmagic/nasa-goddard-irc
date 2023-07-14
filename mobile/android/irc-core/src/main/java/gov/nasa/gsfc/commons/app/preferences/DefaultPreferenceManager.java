//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/commons/gov/nasa/gsfc/commons/app/preferences/DefaultPreferenceManager.java,v 1.9 2006/06/08 13:34:35 smaher_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.commons.app.preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  A PreferenceManager provides application access to preferences and 
 *  preference files. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/06/08 13:34:35 $
 *  @author T. Ames
**/

public class DefaultPreferenceManager implements PreferenceManager
{
	private static final String CLASS_NAME = DefaultPreferenceManager.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
//	private static final String DEFAULT_APP_PREFERENCES_FILE = 
//		"resources/app.plist";
	
	/** Search path separator **/
	public static final String PATH_SEPARATOR = ";";
	
	private static final String REGEX_PATTERN = "(.*)(\\$\\{)([\\S&&[^\\{\\}]]*)(\\})(.*)";
	
	// relative path and name of instrument preferences file
//	private String fPrefsPath = null;
	
	// name of user preferences file
	//private String fUserPrefsName = null;
	
	// Absolute directory path to store user preferences in
	private String fUserDirPathStr = null;
	
	// User preferences cache
	private Properties fUserProps = new Properties();
	
	private URL fUserPreferencesUrl = null;
	
	//---Support for finding variables in strings
	private Pattern fVariablePattern = Pattern.compile(REGEX_PATTERN);


	/**
	 * Default constructor of a DefaultPreferenceManager.
	 */
	public DefaultPreferenceManager()
	{
			
	}

	/**
	 * Sets the preference value indicated by the specified key.
	 *
	 * @param key	the name of the preference.
	 * @param value the value corresponding to <tt>key</tt>.
	 * @return	 the previous value of the specified key, 
	 * 				or <code>null</code> if it did not have one.
	 */
	public synchronized Object setPreference(String key, String value) 
	{
		fUserProps.setProperty(key, value);
		
		return System.setProperty(key, value);
	}

	/**
	 * Gets the preference value indicated by the specified key. The method returns
	 * <code>null</code> if the preference is not found or not set.
	 *
	 * @param   key   the preference key.
	 * @return  the value for the specified key.
	 */
	public String getPreference(String key) 
	{
		String result = System.getProperty(key);
		
		if ("".equals(result))
		{
			result = null;
		}
		
		return result;
	}

	/**
	 * Gets the preference value indicated by the specified key. The method returns
	 * <code>null</code> if the preference is not found or not set.
	 *
	 * @param   key   the preference key.
	 * @return  the value for the specified key.
	 */
	public List getPreferenceAsList(String key) 
	{
		ArrayList result = new ArrayList();
		String prefValue = getPreference(key);
		if (prefValue != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(prefValue, PreferenceManager.LIST_SEPARATOR);
			while (tokenizer.hasMoreTokens())
			{
				result.add(tokenizer.nextToken());
			}
		}
		
		return result;
	}
	
	/**
	 * Removes the preference value indicated by the specified key.
	 *
	 * @param key	the name of the preference.
	 * @return	 the previous value of the specified key, 
	 * 				or <code>null</code> if it did not have one.
	 */
	public synchronized Object remove(String key) 
	{
		return fUserProps.remove(key);
	}

	/**
	 * Returns the path name for the user home directory. This points to a
	 * directory on the user's local disk that contains IRC resources specific
	 * to the current instrument which belong to the current user.
	 * 
	 * @return	absolute path for user home directory location
	**/
	public String getUserHomePath()
	{
		if (fUserDirPathStr == null)
		{
			// Construct the user home path
			String userHome = System.getProperty(PrefKeys.USER_HOME_KEY);
			String userDir = System.getProperty(PrefKeys.USER_DIR_KEY);
		
			fUserDirPathStr = userHome + File.separator + userDir;
		}
		return fUserDirPathStr;
	}

    /**
     * Returns the user preference URL.  Result will be null if the <CODE>
     * loadUserPreferences</CODE> has not been called, or the resource does
     * not exist.<br>
     * 
     * @return  String  The user preference URL, or null is not initialized
     *
     * @see     java.net.URL
     * @see     DefaultPreferenceManager#loadUserPreferences(URL)
     */
    public URL getUserPreferencesUrl()
    {
        return fUserPreferencesUrl;
    }
    
	/**
	 * This method loads the application preferences.
	 *
	 * @param url	the URL of the preference configuration.
	**/
	public synchronized void loadPreferences(URL url)
	{
		Properties sysProps = System.getProperties();
		
		// Load generic Application preferences
		Properties props = getPropertiesFromUrl(url);
		sysProps.putAll(props);
		System.setProperties(sysProps);
	}
	
	/**
	 * This method loads the user preferences.
	 * 
	 * @param url	the URL of the preference configuration.
	**/
	public synchronized void loadUserPreferences(URL url)
	{
		Properties props = getPropertiesFromUrl(url);
		
		if (props != null)
		{
			fUserProps.putAll(props);
			Properties sysProps = System.getProperties();
			
			// Load user preferences
			sysProps.putAll(props);
			System.setProperties(sysProps);
		}

		fUserPreferencesUrl = url;
	}

    /**
     * This method reloads the user preferences from the user preference URL.
     * Doen nothing if the user preferences are not defined.<br>
    **/
    public void reloadUserPreferences()
    {
        if (fUserPreferencesUrl != null)
        {
            loadUserPreferences(fUserPreferencesUrl);
        }
    }

	/**
	 * Attempts to store the user preferences in the same file that was used
	 * in the <code>loadUserPreferences</code> method.
	**/
	public synchronized void storeUserPreferences()
	{
		if (fUserPreferencesUrl != null)
		{
			storeUserPreferences(
				fUserPreferencesUrl.getPath(),
				fUserPreferencesUrl.getFile());
		}
		else
		{
			String message = "Could not save the user preferences because"
				+ " loadUserPreferences was never called";
			sLogger.logp(
				Level.SEVERE, CLASS_NAME, "storeUserPreferences", message);
		}
	}

	/**
	 * Stores user preferences to the local user directory.
	 * 
	 * @param path the path of the directory to contain the user preferences
	 * @param fileName the file name to use for the preferences
	**/
	public synchronized void storeUserPreferences(String path, String fileName)
	{
		Properties props = fUserProps;
			
		File prefsFile = createPreferenceFile(path, fileName);
		
		// Check if user preferences file exists or was newly created
		if (prefsFile != null && prefsFile.exists())
		{
			try
			{
				OutputStream propsOutStream = null;

				//  Save the changes
				propsOutStream = new FileOutputStream(prefsFile);
				props.store(propsOutStream, null);
				propsOutStream.close();

			}	
			catch (IOException ex)
			{
				String message = "Could not save the user preferences";
				sLogger.logp(
					Level.SEVERE, CLASS_NAME, "storeUserPreferences", message, ex);
			}
		}
		else
		{
			String message =
				"Could not save the user preferences to the file named "
					+ fileName
					+ " in directory "
					+ path;

			sLogger.logp(Level.SEVERE, CLASS_NAME, "storeUserPreferences", message);
		}
	}
	
	/**
	 * Resolves all variables of the form <code>${variable_name}</code> 
	 * contained in a String.
	 *
	 * @param   value   the value to resolve.
	 * @return  the result string with all variables replaced with values.
	 */
	public String resolveVariables(String value)
	{
		String newValue = value;
				
		Matcher m = fVariablePattern.matcher(newValue);
		
		while (m.find())
		{
			String variableValue = getPreference(m.group(3));
			if (variableValue == null)
			{
				variableValue = "";
			}
			
			newValue = m.group(1) + variableValue + m.group(5);
			m = fVariablePattern.matcher(newValue);
		}
	
		return newValue;
	}

	/**
	 * This method loads the properties from the given URL.
	 *
	 * @param url	the url to load properties from.
	 * @return	the properties loaded, will be empty if url could not be
	 * 			read. 
	**/
	protected Properties getPropertiesFromUrl(URL url)
	{
		Properties props = new Properties();
		
		if (url != null)
		{
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Loading properties from " + 
					url.getPath();
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"loadPropertiesFromUrl", message);
			}
			
			try
			{
				InputStream input = url.openStream();
				props.load(input);
			}
			catch (IOException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Could not load properties from URL " + 
						url.getPath();
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"loadPropertiesFromUrl", message, ex);
				}
			}
		}
		else
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Properties URL must not be null";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"loadPropertiesFromUrl", message);
			}
		}
		
		return props;
	}

    /**
     * This method supplies the user defined properties.<br>
     *
     * @return  Properties  The current user properties
     */
    public Properties getUserPreferences() 
    {
        return fUserProps;
    }

	/**
	 * This method returns a reference to the specified file in the specified
	 * directory. The file and directories will be created if they do not exists.
	 *
	 * @param 	path		the parent pathname.
	 * @param 	fileName	the name of the preference file.
	 * @return	The file reference or null if this pathname and file could 
	 * 			not be created. 
	**/
	private File createPreferenceFile(String path, String fileName)
	{
			File prefsFile = null;
		
			// Check if directory exists
	 	File userDir = new File(path);

		//  If we don't have a user directory yet, create it.
		if (!userDir.exists())
		{
			if (userDir.mkdirs())
			{
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Creating new user directory as " + 
						userDir;
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"createPreferenceFile", message);
				}
			}
			else
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = 
						"Could not create user directory: " + path;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"createPreferenceFile", message);
				}

				return null;
			}
		}
		
		// Directory exists
		
		prefsFile = new File(path, fileName);

		//  Check if the file exists
		if (!prefsFile.exists())
		{
			// Try to create the file
			try
			{
				if (prefsFile.createNewFile())
				{
					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Creating new user file as " + 
							fileName;
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"createPreferenceFile", message);
					}
				}
				else
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Could not create user file: " + fileName;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"createPreferenceFile", message);
					}

					return null;
				}
			}
			catch (IOException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = 
						"Could not create the user preferences file";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"createPreferenceFile", message, ex);
				}
			}
		}

		return prefsFile;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultPreferenceManager.java,v $
//  Revision 1.9  2006/06/08 13:34:35  smaher_cvs
//  Added ability to read a single preference as a List.
//
//  Revision 1.8  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.7  2005/12/01 20:53:33  tames_cvs
//  Merged ASF changes into IRC. Changes were with respect to user preferences.
//
//  Revision 1.6  2004/12/14 20:45:23  tames
//  Changed getPreference method to return null if preference not found
//  OR not set.
//
//  Revision 1.5  2004/10/15 13:44:07  tames_cvs
//  Fixed a bug in loading User Preferences. They are now merged with other
//  properties.
//
//  Revision 1.4  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/05/28 22:00:28  tames_cvs
//  Fixed loading preferences and removed IRC specific code
//
//  Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
