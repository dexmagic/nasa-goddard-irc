//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/commons/gov/nasa/gsfc/commons/app/preferences/PreferenceManager.java,v 1.6 2006/06/08 13:34:35 smaher_cvs Exp $
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

import java.net.URL;
import java.util.List;
import java.util.Properties;


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
public interface PreferenceManager
{
    /** User preference file prefix **/
    public static final String USER_PREF_PREFIX = "user_";
    
    /** Used to separate separate values in preference lists **/
    public static final String LIST_SEPARATOR = ",";    
    
	/**
	 * Sets the preference value indicated by the specified key.
	 *
	 * @param key	the name of the preference.
	 * @param value the value corresponding to <tt>key</tt>.
	 * @return	 the previous value of the specified key, 
	 * 				or <code>null</code> if it did not have one.
	 */
	public Object setPreference(String key, String value);
	
	/**
	 * Gets the preference value indicated by the specified key as a list. 
	 * Strings separated by <code>LIST_SEPARATOR</code> will be returned as
	 * separate items in the list. The method returns
	 * <code>null</code> if the preference is not found or not set.
	 *
	 * @param   key   the preference key.
	 * @return  the list of values for the specified key.
	 */
	public List getPreferenceAsList(String key);
	
	/**
	 * Gets the preference value indicated by the specified key. The method returns
	 * <code>null</code> if the preference is not found or not set.
	 *
	 * @param   key   the preference key.
	 * @return  the value for the specified key.
	 */
	public String getPreference(String key);   

	/**
	 * Resolves all variables of the form <code>${variable_name}</code> 
	 * contained in a String.
	 *
	 * @param   value   the value to resolve.
	 * @return  the result string with all variables replaced with values.
	 */
	public String resolveVariables(String value);	

	/**
	 * Removes the preference value indicated by the specified key.
	 *
	 * @param key	the name of the preference.
	 * @return	 the previous value of the specified key, 
	 * 				or <code>null</code> if it did not have one.
	 */
	public Object remove(String key);	

	/**
	 * Returns the path name for the user home directory. This points to a
	 * directory on the user's local disk that contains IRC resources specific
	 * to the current instrument which belong to the current user.
	 * 
	 * @return	absolute path for user home directory location
	**/
	public String getUserHomePath();	
	
    /**
     * This method supplies the user defined properties.<br>
     *
     * @return  Properties  The current user properties
     */
    public Properties getUserPreferences();
    
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
    public URL getUserPreferencesUrl();
    
	/**
	 * This method loads the application preferences.
	 *
	 * @param url	the URL of the preference configuration.
	**/
	public void loadPreferences(URL url);
	
	/**
	 * This method loads the user preferences.
	 *
	 * @param url	the URL of the preference configuration.
	**/
	public void loadUserPreferences(URL url);

	/**
	 * Stores user preferences to the local user directory.
	 *
	**/
	public void storeUserPreferences();
    
    /**
     * Stores user preferences to the local user directory.
     * 
     * @param path the path of the directory to contain the user preferences
     * @param fileName the file name to use for the preferences
    **/
    public void storeUserPreferences(String path, String fileName);

}

//--- Development History  ---------------------------------------------------
//
//  $Log: PreferenceManager.java,v $
//  Revision 1.6  2006/06/08 13:34:35  smaher_cvs
//  Added ability to read a single preference as a List.
//
//  Revision 1.5  2005/12/01 20:53:33  tames_cvs
//  Merged ASF changes into IRC. Changes were with respect to user preferences.
//
//  Revision 1.4  2004/12/14 20:44:10  tames
//  Javadoc comments updated.
//
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/28 22:00:28  tames_cvs
//  Fixed loading preferences and removed IRC specific code
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
