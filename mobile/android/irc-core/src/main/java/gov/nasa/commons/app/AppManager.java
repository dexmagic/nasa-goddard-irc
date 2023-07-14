//=== File Prolog ============================================================
//	This code was developed by AppNet, Inc. and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
// $Log: AppManager.java,v $
// Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
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
 * An AppManager is a source of application configuration and 
 * environment information, as well as of application services.
 *
 * @version $Date: 2004/07/12 14:26:24 $	
 * @author Carl F. Hostetter
 * @author Troy Ames
**/
public interface AppManager
{
	/**
	 * Get the PreferenceManager specified by this AppManager.
	 * @return a PreferenceManager
	**/
	public PreferenceManager getPreferenceManager();
}
