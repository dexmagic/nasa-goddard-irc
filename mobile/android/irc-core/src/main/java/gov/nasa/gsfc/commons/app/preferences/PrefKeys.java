//=== File Prolog ============================================================
//
//
//This code was developed by NASA, Goddard Space Flight Center, Code 580
//for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History:
//
//	$Log: PrefKeys.java,v $
//	Revision 1.8  2006/02/14 20:03:51  tames
//	Added app.log.handlers property
//	
//	Revision 1.7  2005/05/23 15:22:22  tames_cvs
//	Updated to include new app.xml.base.dir, app.xml.schema.dir,
//	and app.xml.validationEnabled parameters.
//	
//	Revision 1.6  2004/09/21 20:07:20  tames
//	Changed log configuration and log properties to utilize the
//	built in log configuration specified by the
//	java.util.logging.LogManager class.
//	
//	Revision 1.5  2004/09/16 18:38:16  tames
//	Added new app.log.level parameter
//	
//	Revision 1.4  2004/09/05 13:28:35  tames
//	Properties and preferences clean up
//	
//	Revision 1.3  2004/07/10 00:12:14  tames_cvs
//	Added log constants
//	
//	Revision 1.2  2004/05/28 22:00:28  tames_cvs
//	Fixed loading preferences and removed IRC specific code
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
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

/**
 * Application preference keys to be used with a preference manager.
 */
public abstract class PrefKeys
{
	//---PLIST PROPERTY CONSTANTS ---
	/** Key to get user home directory **/
	public static String USER_HOME_KEY = "user.home";

	/** Name of property containing optional log configuration file*/
	public static String LOG_CONFIG_FILE = "app.log.configFile";

	/** Name of property containing the global log level*/
	public static String LOG_LEVEL = "app.log.level";

	/** Name of property containing the global log handlers*/
	public static String LOG_HANDLERS = "app.log.handlers";

	/** Property for resources directory */
	public static String RESOURCES_DIR = "app.resources.directory";

	/** 
	 * Name of property specifing the relative location of the 
	 * application online help set. 
	 */
	public static String HELP_LOC = "app.resources.help";

	/** 
	 * Base directory for locating all the XML (.xml) relative file references. 
	 */
	public static String XML_BASE_DIR	= "app.xml.base.dir";

	/** 
	 * Base directory for locating all the XML schema (.xsd) relative file 
	 * references. 
	 */
	public static String XML_SCHEMA_DIR	= "app.xml.schema.dir";

	/** Key to get the XML validation enabled flag. **/
	public static String XML_VALIDATE_ENABLED = "app.xml.validationEnabled";

	/** Key to get App user directory name **/
	public static String USER_DIR_KEY = "app.user.directory";

	/** Key to get the App version **/
	public static String APP_VERSION = "app.version";

	public PrefKeys()
	{
	}
}
