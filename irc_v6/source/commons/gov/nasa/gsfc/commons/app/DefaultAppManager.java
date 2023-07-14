//=== File Prolog ============================================================
//	This code was developed by AppNet, Inc. and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
// $Log: DefaultAppManager.java,v $
// Revision 1.8  2006/01/23 17:59:55  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.7  2004/08/03 20:24:00  tames_cvs
// Changes to reflect instantiateClass exception handling changes
//
// Revision 1.6  2004/07/11 21:24:54  chostetter_cvs
// Organized imports
//
// Revision 1.5  2004/06/30 03:21:40  tames_cvs
// Removed direct references to boot properties.
//
// Revision 1.4  2004/06/03 04:43:38  chostetter_cvs
// Organized imports
//
// Revision 1.3  2004/06/01 15:50:41  tames_cvs
// Changes to reflect instantiateClass method modifications
//
// Revision 1.2  2004/05/28 22:00:55  tames_cvs
// many bug fixes
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

import gov.nasa.gsfc.commons.app.preferences.PreferenceManager;


/**
 *  An AppManager is a source of application configuration and 
 *  environment information, as well as of application services.
 *
 *  @version $Date: 2006/01/23 17:59:55 $	
 *  @author Carl F. Hostetter
**/

public class DefaultAppManager implements AppManager 
{
	/** Property name for Preference Manager class implementation. */
	public static final String PREFERENCE_MANAGER_PROPERTY =
		"app.preference.manager";

	private PreferenceManager fPreferenceManager = null;
	
	
	/**
	 * Returns the Preference Manager associated with this application. If the 
	 * preference manager has not been intialized this method will call
	 * <code>loadPreferenceManager</code> method.
	 * 
	 * @return The PreferenceManager associated with this application.
	 */
	public synchronized PreferenceManager getPreferenceManager()
	{
		if (fPreferenceManager == null)
		{
			fPreferenceManager = loadPreferenceManager();
		}
		
		return fPreferenceManager;
	}

	/**
	 * Loads the preference manager defined by the 
	 * {@link #PREFERENCE_MANAGER_PROPERTY PREFERENCE_MANAGER_PROPERTY}
	 * System property. If property is not defined then this method loads the 
	 * {@link gov.nasa.gsfc.commons.app.preferences.DefaultPreferenceManager DefaultPreferenceManager} 
	 * class.
	 * 
	 * @return a preference manager or null if load failed.
	 */
	protected PreferenceManager loadPreferenceManager()
	{
		PreferenceManager manager = null;
		
		String className =
			System.getProperty(
				PREFERENCE_MANAGER_PROPERTY,
				"gov.nasa.gsfc.commons.app.preferences.DefaultPreferenceManager");
		
		try
		{
			manager = (PreferenceManager) App.instantiateClass(className);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return manager;
	}
}
