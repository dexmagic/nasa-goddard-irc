//=== File Prolog ============================================================
//
//
//This code was developed by NASA, Goddard Space Flight Center, Code 580
//for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.app.preferences;

import gov.nasa.gsfc.commons.app.preferences.PrefKeys;

/**
 * Irc specific preference keys to be used with a preference manager.
 */
public abstract class IrcPrefKeys extends PrefKeys
{
	//---PLIST PROPERTY CONSTANTS ---
	/** Name of property specifying the archive repository root directory */
	public static final String ARCHIVE_DIRECTORY = "irc.archive.rootDirectory";

	/** 
	 * Name of property specifying the XML file used to define the 
	 * available components 
	 */
	public static final String COMPONENT_DESCRIPTION = "irc.description.components";

	/** 
	 * Name of property specifying the description of the interface 
	 * provided by this IRC Device.
	 */
	public static final String CLIENT_DESCRIPTION = "irc.description.client";
	
	/** Name of property specifying the description used by this IRC Device */
	public static final String INSTRUMENT_DESCRIPTION = "irc.description.instrument";

	/** XML file to use to define the instrument type mappings. **/
	public static String INSTRUMENT_TYPE_MAP = "irc.description.instrumentTypeMap";

	/** XML file to use to define the default type mappings. **/
	public static String DEFAULT_TYPE_MAP = "irc.description.typeMap";

	/** The XML file used to define the available scripts. **/
	public static String SCRIPTS_DESCRIPTION = "irc.description.scripts";

	/** XML file to use to define the user type mappings.
	 * Additional userTypeMaps may be incorporated by using
	 * irc.description.userTypeMap[1-9].  For example, 
	 * irc.description.userTypeMap1=moreTypes.xml
	 * irc.description.userTypeMap2=evenMoreTypes.xml
	 **/
	public static String USER_TYPE_MAP = "irc.description.userTypeMap";

	/** Default plist file key */
	public static final String DEFAULT_PLIST_KEY = "irc.manager.properties";
	
	/** 
	 * Plist file key if application is running in a nongraphical 
	 * environment.
	 */
	public static final String NO_GRAPHICS_PLIST_KEY = 
		"irc.manager.noGuiProperties";
	
	/** 
	 * The flag to determine if components created from a device description 
	 * file (IML) should be started by default.
	 */
	public static final String IRC_COMPONENT_AUTOSTART = "irc.component.autoStartEnabled";
	
	/** 
	 * The flag to determine if the properties of Saveable components should
	 * be loaded upon construction.
	 */
	public static final String IRC_COMPONENT_AUTOLOAD_PROPERTIES = "irc.component.autoPropertyLoadEnabled";
	
	/** 
	 * This value contains the (substring) names of classes that should be ignored when
	 * saving a Saveable component.  For example, "types.namespaces,irc.components".
	 */
	public static final String IRC_COMPONENT_IGNORE_CLASSNAME_SUBSTRING = "irc.component.classnameSubstringsToIgnore";
		
	/** 
	 * Name of the directory to store saved properties.  The directory is located in the first
	 * element of the resource path.  It is created if it doesn't exist.
	 */
	public static final String IRC_COMPONENT_SAVED_PROPERTIES_DIRECTORY = "irc.component.savedPropertiesDirectory";
	
	/** 
	 * The flag to determine if the gui should be disabled.
	 */
	public static final String IRC_MANAGER_GUI_DISABLED = "irc.manager.guiDisabled";
	
	/** 
	 * Name of property specifying the default gui loading directory.
	 */
	public static final String GUI_DIALOG_DEFAULT_DIRECTORY = 
		"irc.gui.dialog.defaultDirectory";
	
	/** 
	 * Name of property specifying the device loading directory.
	 */
	public static final String DEVICE_DIALOG_DEFAULT_DIRECTORY = 
		"irc.gui.dialog.defaultDeviceDirectory";	

	/** Name of property specifying the main gui panel description */
	public static final String GUI_DESKTOP = "irc.gui.desktop";

	/** Name of property specifying the component browser gui panel description */
	public static final String COMPONENT_BROWSER_GUI_FRAME = "irc.gui.componentBrowserFrame";
	
	/** Name of property specifying the data space browser gui panel description */
	public static final String DATA_SPACE_BROWSER_GUI_FRAME = "irc.gui.dataSpaceFrame";
	
    /** Name of property specifying the log viewer gui panel description */
    public static final String LOGVIEWER_GUI_FRAME = "irc.gui.logViewerFrame";

	/** Name of property specifying the instrument or device name */
	public static final String INSTRUMENT_ID = "irc.instrument.id";

	/** 
	 * Directory that will be used to store instrument specific 
	 * configuration files under the user directory.
	**/
	public static String INSTRUMENT_SUBDIRECTORY = "irc.instrument.subdirectory";

	/** Name of property containing peer network configuration file name */
	public static String NETWORK_CONFIG = "irc.network.config";

	/** Name of property containing peer network enabled flag */
	public static String NETWORK_ENABLED = "irc.network.enabled";

	/** Name of property containing network description name */
	public static String NETWORK_DESCRIPTION_NAME = "irc.network.clientDescription.name";

	/** Name of property containing network client description url */
	public static String NETWORK_DESCRIPTION_URL = "irc.network.clientDescription.url";

	/** Name of property containing network group name */
	public static String NETWORK_GROUP = "irc.network.group";

	/** Name of property specifying the persistence store name */
	public static final String PERSISTENCE_STORE_NAME = "irc.persistence.store.name";

	/** Name of property containing the plugin directory */
	public static String PLUGIN_DIRECTORY = "irc.plugin.directory";

	/** 
	 * Name of property containing the default search paths relative 
	 * to the class path for finding resources 
	 */
	public static String PROPERTIES_PATH = "irc.properties.path";

	/** 
	 * Name of property containing the location of the application schema 
	 * directory 
	 */
	public static String SCHEMA_BASE_DIRECTORY = "irc.schema.base.dir";

	/** Name of property containing the default script directory */
	public static String SCRIPT_DIRECTORY = "irc.scripting.directory";

	/** Name of property containing shutdown script */
	public static String SHUTDOWN_SCRIPT = "irc.scripting.shutdown";

	/** Name of property containing startup script */
	public static String STARTUP_SCRIPT = "irc.scripting.startup";

	/** 
	 * Name of property specifying if user commanding into the console is
	 * enabled.
	 */
	public static final String CONSOLE_COMMANDING_FLAG = 
		"irc.ui.consoleCommandingEnabled";

	/** IRC version **/
	public static String IRC_VERSION = "irc.version";

	/** Last Work Space **/
	public static String LAST_WORK_SPACE = "irc.workSpace";

	public IrcPrefKeys()
	{	
	}
}

//--- Development History:
//
//	$Log: IrcPrefKeys.java,v $
//	Revision 1.22  2006/07/13 17:14:45  smaher_cvs
//	Added ability to have numerous (ordered) user type maps.
//	This is useful for "multi-layered" projects such as GISMO and GBT on
//	top of MarkIII, which in turn is on top of IRC.
//	
//	Revision 1.21  2006/06/08 13:35:27  smaher_cvs
//	Added ability to ignore package names when persisting components.
//	
//	Revision 1.20  2006/03/31 16:20:26  smaher_cvs
//	Added keys for device file directory.
//	
//	Revision 1.19  2006/03/13 05:15:01  tames
//	Added GUI_DESKTOP and removed MAIN_GUI_FRAME and MAIN_GUI_FRAME_TITLE
//	
//	Revision 1.18  2005/09/01 16:25:45  pjain_cvs
//	Added support for Log viewer.
//	
//	Revision 1.17  2005/08/31 16:10:37  tames_cvs
//	Added support for a DataSpace browser.
//	
//	Revision 1.16  2005/01/19 15:59:10  tames_cvs
//	Added irc.gui.mainFrame.title definition.
//	
//	Revision 1.15  2004/12/14 20:41:27  tames
//	Added IRC_MANAGER_GUI_DISABLED preference key
//	
//	Revision 1.13  2004/12/06 20:21:47  smaher_cvs
//	Added property autoload setting.
//	
//	Revision 1.12  2004/12/05 14:24:40  smaher_cvs
//	Added COMPONENT_BROWSER_GUI_FRAME property to allow using the optional
//	extended component browser.
//	
//	Revision 1.11  2004/12/03 15:16:41  tames_cvs
//	Small change to preferences and the auto start of components
//	capability.
//	
//	Revision 1.9  2004/09/21 20:07:20  tames
//	Changed log configuration and log properties to utilize the
//	built in log configuration specified by the
//	java.util.logging.LogManager class.
//	
//	Revision 1.8  2004/09/05 13:28:35  tames
//	Properties and preferences clean up
//	
//	Revision 1.7  2004/08/29 19:07:45  tames
//	Added preference keys
//	
//	Revision 1.6  2004/08/23 13:53:59  tames
//	changed some key names for consistency
//	
//	Revision 1.5  2004/07/10 08:21:43  tames_cvs
//	Modified logging to a file items
//	
//	Revision 1.4  2004/07/10 00:15:15  tames_cvs
//	Updated preference constants
//	
//	Revision 1.3  2004/06/07 14:17:24  tames_cvs
//	added PERSISTANCE_STORE_NAME
//	
//	Revision 1.2  2004/06/04 14:23:03  tames_cvs
//	addition of preference keys
//	
//	Revision 1.1  2004/05/28 22:03:15  tames_cvs
//	Initial version with irc specific keys
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//