//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.JScience;

import gov.nasa.gsfc.commons.app.App;
import gov.nasa.gsfc.commons.app.plugins.PluginClassLoader;
import gov.nasa.gsfc.commons.processing.tasks.TaskManager;
import gov.nasa.gsfc.commons.publishing.EventBus;
import gov.nasa.gsfc.commons.system.storage.PersistentObjectStore;
import gov.nasa.gsfc.commons.types.namespaces.Namespace;
import gov.nasa.gsfc.irc.components.ComponentFactory;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.data.BasisBundleFactory;
import gov.nasa.gsfc.irc.data.BasisRequesterFactory;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.description.DescriptorLibrary;
import gov.nasa.gsfc.irc.gui.GuiFactory;
import gov.nasa.gsfc.irc.messages.MessageFactory;
import gov.nasa.gsfc.irc.messages.MessageValidator;
import gov.nasa.gsfc.irc.scripts.ScriptEvaluator;
import gov.nasa.gsfc.irc.scripts.ScriptFactory;
import gov.nasa.gsfc.irc.scripts.ScriptValidator;
import gov.nasa.gsfc.irc.workspace.WorkspaceManager;


/**
 * This class provides the 'main' for IRC, it is responsible for driving the
 * application startup sequence.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 03:59:02 $
 * @author	Troy Ames
 * @author	Carl F. Hostetter
**/
public class Irc extends App
{
	private static final String CLASS_NAME = Irc.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	//---PLIST PROPERTY CONSTANTS ---

	//---MISC CONSTANTS ---
	/** Property name for the default IRC manager */
	public static final String IRC_MANAGER_PROPERTY = "irc.manager";

	//-------------------------------------------------------------------------

	/**
	 * Holds the IrcManager for this application. 
	 * @see #getIrcManager()
	 */
	protected static IrcManager sIrcManager = null;

	//---Data store
	private static PersistentObjectStore sObjectStore;

	//---Global variable map
	private static Map sGlobals = new HashMap();

	private static Namespace sGlobalNamespace;
	private static ComponentFactory sComponentFactory;
	private static ComponentManager sComponentManager;
	private static WorkspaceManager sWorkspaceManager;
	private static DescriptorFramework sDescriptorFramework;
	private static DataSpace sDataSpace;
	private static BasisBundleFactory sBasisBundleFactory;
	private static BasisRequesterFactory sBasisRequesterFactory;
	private static MessageFactory sMessageFactory;
	private static MessageValidator sMessageValidator;
	private static EventBus sEventBus;

	private static ScriptFactory sScriptFactory;
	private static ScriptValidator sScriptValidator;
	private static ScriptEvaluator sScriptEvaluator;

	private static TaskManager sTaskManager;

	/**
	 * Defined as protected to prevent instantiation from non extending classes.
	 * In order for subclasses to extend this class the constructor cannot be
	 * private.
	**/
	protected Irc()
	{
	}

	/**
	 * Gets the Application Manager associated with this application.  This
	 * method calls <code>loadIrcManager</code> if the <code>sIrcManager</code>
	 * field has not been set yet.
	 *  
	 * @return The AppManager associated with this application.
	 * @see #loadIrcManager()
	**/
	public synchronized static IrcManager getIrcManager()
	{
		// This method is synchronized to prevent creating multiple managers.
		if (sIrcManager == null)
		{
			sIrcManager = loadIrcManager();
		}
		
		return (sIrcManager);
	}


    /**
     * Returns the Class corresponding to the given Class name/id String. 
     *
     * @param classString The name/id of a Class 
     * @return The Class corresponding to the given Class name/id String 
    **/
	
    public static Class getCorrespondingClass(String classString) 
    {
        Class result = null;

        try
        {
            result = Irc.loadClass(classString);
        }
        catch (ClassNotFoundException ex)
        {
			try 
			{
				PluginClassLoader cl = PluginClassLoader.getInstance();
				result = cl.loadClass(classString);
			}
			catch (Exception ex2)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Could not determine corresponding Class of " + 
						classString;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"getCorrespondingClass", message);
				}
			}
        }
        
        return (result); 
    }
    
    
	/**
	 * Returns the DataSpace to be used by the current instance of IRC.
	 * 
	 * @return The DataSpace to be used by the current instance of IRC
	**/
	public static DataSpace getDataSpace()
	{
		if (sDataSpace == null)
		{
			sDataSpace = getIrcManager().getDataSpace();
		}
		
		return (sDataSpace);
	}
	
	/**
	 * Returns the BasisBundleFactory to be used by the current instance of 
	 * IRC.
	 * 
	 * @return The BasisBundleFactory to be used by the current instance of 
	 *  		IRC
	**/
	public static BasisBundleFactory getBasisBundleFactory()
	{
		if (sBasisBundleFactory == null)
		{
			sBasisBundleFactory = getIrcManager().getBasisBundleFactory();
		}
		
		return (sBasisBundleFactory);
	}
	
	/**
	 * Returns the BasisRequesterFactory to be used by the current instance of 
	 * IRC.
	 * 
	 * @return The BasisRequesterFactory to be used by the current instance of 
	 *  		IRC
	**/
	public static BasisRequesterFactory getBasisRequesterFactory()
	{
		if (sBasisRequesterFactory == null)
		{
			sBasisRequesterFactory = getIrcManager().getBasisRequesterFactory();
		}
		
		return (sBasisRequesterFactory);
	}
	
	/**
	 * Returns the ComponentFactory to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The ComponentFactory to be used by the current instance 
	 *  		of IRC
	**/
	public static ComponentFactory getComponentFactory()
	{
		if (sComponentFactory == null)
		{
			sComponentFactory = getIrcManager().getComponentFactory();
		}
		
		return (sComponentFactory);
	}
	
	/**
	 * Returns the ComponentManager to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The ComponentManager to be used by the current instance 
	 *  		of IRC
	**/
	public static ComponentManager getComponentManager()
	{
		if (sComponentManager == null)
		{
			sComponentManager = getIrcManager().getComponentManager();
		}
		
		return (sComponentManager);
	}
	
	/**
	 * Returns the global Namespace to be used by the current instance 
	 * of IRC.
	 * 
	 * @return The global Namespace to be used by the current instance 
	 * 	of IRC
	**/
	public static Namespace getGlobalNamespace()
	{
		if (sGlobalNamespace == null)
		{
			sGlobalNamespace = getIrcManager().getGlobalNamespace();
		}
		
		return (sGlobalNamespace);
	}
	
	/**
	 * Returns the WorkspaceManager to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The WorkspaceManager to be used by the current instance 
	 *  		of IRC
	**/
	public static WorkspaceManager getWorkspaceManager()
	{
		if (sWorkspaceManager == null)
		{
			sWorkspaceManager = getIrcManager().getWorkspaceManager();
		}
		
		return (sWorkspaceManager);
	}
	
	/**
	 * Returns the TaskManager to be used by the current instance of IRC.
	 * 
	 * @return	The TaskManager to be used by the current instance of IRC
	**/
	public static TaskManager getTaskManager()
	{
		if (sTaskManager == null)
		{
			sTaskManager = getIrcManager().getTaskManager();
		}
		
		return (sTaskManager);
	}
	
	/**
	  * Returns the Descriptor framework to be used by the current instance 
	  * of IRC.
	  * 
	  * @return	The Descriptorframework to be used by the current instance 
	  *  		of IRC
	 **/
	 public static DescriptorFramework getDescriptorFramework()
	 {
		 if (sDescriptorFramework == null)
		 {
			 sDescriptorFramework = getIrcManager().getDescriptorFramework();
		 }
		
		 return (sDescriptorFramework);
	 }
	
	/**
	 * Returns the DescriptorLibrary to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The DescriptorLibrary to be used by the current instance 
	 *  		of IRC
	**/
	public static DescriptorLibrary getDescriptorLibrary()
	{
		return (getIrcManager().getDescriptorLibrary());
	}

	/**
	 * Get an instance of the class specifed by the key in the specified 
	 * namespace from the global type map. If the class is a singleton with
	 * a static <code>getInstance</code> method then this method will be used
	 * to get an instance, otherwise the default no argument constructor will
	 * be called.
	 * Returns null if an instance cannot be created. 
	 *
	 * @param  namespace Namespace to look at. 
	 * @param  key	   Key to get value for. 
	 * @return an instance of the class mapped to namespace and key. 
	**/
	public static Object instantiateFromTypemap(String namespace, String key)
	{
		DescriptorFramework desciptorFramework = getDescriptorFramework();

		//TODO should this class use Bean.instantiate or component factory
		return desciptorFramework.instantiateFromGlobalMap(namespace, key);
	}

	/**
	 * Returns the GUI Builder to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The GuiFactory to be used by the current instance 
	 *  		of IRC
	**/
	public static GuiFactory getGuiFactory()
	{
		return (getIrcManager().getGuiFactory());
	}


	//--- Global IRC state access ----------------------------------------------

	/**
	 *  This is a convenience method to retrieve a globally available key value pair.
	**/
	public static Object getGlobal(String key)
	{
		return sGlobals.get(key);
	}

	/**
	 *  This is a convenience method to store a globally available key value pair.
	**/
	public static void putGlobal(String key, Object value)
	{
		sGlobals.put(key, value);
	}

	//--- Persistent DataObjectStore access ------------------------------------

	/**
  	 *  Stores the given Serializable Object in the persistent Store, 
  	 *  associated with the given key.
	 *
	 * 	@param key The key under which to store the given Object
	 * 	@param object The Serializable Object to store
	**/
	public static void store(String key, Serializable object)
	{
		if (sObjectStore == null)
		{
			sObjectStore = getIrcManager().getPersistentStore();
		}

		sObjectStore.store(key, object);
	}

	/**
	 *  Retrieves the Object associated with the given key from the 
	 *  persistent Store. If no such Object is found, the result is null.
	 *
	 *  @param key The key of the Object to be retrieved
	 *  @return The Object associated with the given key
	**/
	public static Serializable retrieveFromStore(String key)
	{
		if (sObjectStore == null)
		{
			sObjectStore = getIrcManager().getPersistentStore();
		}

		return sObjectStore.retrieve(key);
	}

	/**
	 *  Removes the Object associated with the given key from the 
	 *  persistent Store.
	 *
	 *  @param key The key of the Object to be removed
	 */
	public static void deleteFromStore(String key)
	{
		if (sObjectStore == null)
		{
			sObjectStore = getIrcManager().getPersistentStore();
		}

		sObjectStore.delete(key);
	}

	/**
	 *  Writes the current contents of the persistent Store to the file
	 *  lib/<storeName>, from whence it can be read in on application
	 *  startup.
	**/
	public static void checkpointStore()
	{
		if (sObjectStore == null)
		{
			sObjectStore = getIrcManager().getPersistentStore();
		}
		
		sObjectStore.saveToDisk();
	}

	/**
	 *  This is a convenience method to retrieve the applications singleton
	 *  DataStore.
	 *
	 *  @param - the applicatons single DataObjectStore.
	**/
	public static PersistentObjectStore getPersistentStore()
	{
		if (sObjectStore == null)
		{
			sObjectStore = getIrcManager().getPersistentStore();
		}
		
		return sObjectStore;
	}

	/**
	 * Returns the script factory to be used by the current instance 
	 * of IRC. 
	 * 
	 * @return	The ScriptFactory to be used by the current instance of IRC
	**/
	public static ScriptFactory getScriptFactory()
	{
		if (sScriptFactory == null)
		{
			sScriptFactory = getIrcManager().getScriptFactory();
		}
		
		return (sScriptFactory);
	}

	/**
	 * Returns the script validator to be used by the current instance 
	 * of IRC. 
	 * 
	 * @return	The ScriptValidator to be used by the current instance 
	 *  		of IRC
	**/
	public static ScriptValidator getScriptValidator()
	{
		if (sScriptValidator == null)
		{
			sScriptValidator = getIrcManager().getScriptValidator();
		}
		
		return (sScriptValidator);
	}

	/**
	 * Returns the script evaluator to be used by the current instance 
	 * of IRC. 
	 * 
	 * @return	The ScriptEvaluator to be used by the current instance 
	 *  		of IRC
	**/
	public static ScriptEvaluator getScriptEvaluator()
	{
		if (sScriptEvaluator == null)
		{
			sScriptEvaluator = getIrcManager().getScriptEvaluator();
		}
		
		return (sScriptEvaluator);
	}

	/**
	 * Returns the EventBus to be used by the current instance 
	 * of IRC. This EventBus typically publishes message events but can 
	 * publish any event.
	 * 
	 * @return	The EventBus to be used by the current instance 
	 *  		of IRC
	**/
	public static EventBus getEventBus()
	{
		if (sEventBus == null)
		{
			sEventBus = getIrcManager().getEventBus();
		}
		
		return (sEventBus);
	}

	/**
	 * Returns the message factory to be used by the current instance 
	 * of IRC. 
	 * 
	 * @return	The MessageFactory to be used by the current instance 
	 *  		of IRC
	**/
	public static MessageFactory getMessageFactory()
	{
		if (sMessageFactory == null)
		{
			sMessageFactory = getIrcManager().getMessageFactory();
		}
		
		return (sMessageFactory);
	}

	/**
	 * Returns the message validator to be used by the current instance 
	 * of IRC. 
	 * 
	 * @return	The MessageValidator to be used by the current instance 
	 *  		of IRC
	**/
	public static MessageValidator getMessageValidator()
	{
		if (sMessageValidator == null)
		{
			sMessageValidator = getIrcManager().getMessageValidator();
		}
		
		return (sMessageValidator);
	}

	//	------------------------------------------------------------------------

	/**
	 * This method will execute the shutdown specified in the Irc Manager's
	 * <code>shutdown</code> method and then exit the application.
	**/
	public static void shutdown()
	{
		int status = sIrcManager.shutdown();
        System.exit(status);
	}

	//	------------------------------------------------------------------------

	/**
	 * This is the main method for the IRC application.
	 */
	public static void main(String argv[])
	{
		try
		{
			sLogger.setLevel(Level.ALL);

			// Initialize application
			JScience.initialize();
			sIrcManager = getIrcManager();
			sIrcManager.initializeApp(argv);

			// Initialize cached static variables
			sObjectStore = sIrcManager.getPersistentStore();
			//PeerNetworkManager netMgr = PeerNetworkManager.getInstance();
			//InstrumentManager.getInstance().start();

		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Encountered fatal exception";

				sLogger.logp(Level.SEVERE, CLASS_NAME, "main", message, ex);
			}
		}
	}

	/**
	 *  This method takes the name of a script to execute. The
	 *  method looks the script up in the DescriptorLibrary and
	 *  then used the ScriptEvaluator to execute the script.
	 *
	 *  @param  fileName Name of the script to execute
	**/
	public static void runScript(String fileName)
	{
	}

	/**
	 * Loads the IRC application manager defined by the 
	 * {@link #IRC_MANAGER_PROPERTY} System property. If the 
	 * property is not defined then this method loads the 
	 * {@link gov.nasa.gsfc.irc.app.DefaultIrcManager DefaultIrcManager} 
	 * class. To use a different manager set the property in the 
	 * sysBootProperties.txt file or as a command line -D parameter, 
	 * or override this method and <code>main</code> in a subclass if Irc.
	 * 
	 * @return an IRC manager or null if load failed.
	 */
	protected static IrcManager loadIrcManager()
	{
		IrcManager manager = null;
		
		String className =
			System.getProperty(
				IRC_MANAGER_PROPERTY,
				"gov.nasa.gsfc.irc.app.DefaultIrcManager");
		
		try
		{
			manager = (IrcManager) instantiateClass(className);
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return manager;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Irc.java,v $
//  Revision 1.37  2006/04/18 03:59:02  tames
//  Changed to support event bus.
//
//  Revision 1.36  2006/04/05 20:24:12  tames
//  Added method to support EventBus.
//
//  Revision 1.35  2006/03/07 23:32:42  chostetter_cvs
//  NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
//  Revision 1.34  2006/01/23 17:59:53  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.33  2005/12/02 19:05:44  tames_cvs
//  Moved the stopAllComponents() functionality of shutdown from the Irc
//  static class to the DefaultIrcManager so that applications can override
//  shutdown behavior if desired.
//
//  Revision 1.32  2005/10/14 13:42:12  pjain_cvs
//  Modified shutdown method such that it stops all components before exiting.
//
//  Revision 1.31  2005/08/26 22:08:44  tames_cvs
//  Added methods to support a plug in BasisRequesterFactory.
//
//  Revision 1.30  2005/07/19 17:58:21  tames_cvs
//  Changed getCorrespondingClass method to use Irc.loadClass.
//
//  Revision 1.29  2005/05/24 19:19:50  tames_cvs
//  Changed getCorrespondingClass method to use the default class loader.
//
//  Revision 1.28  2005/04/04 14:20:16  tames
//  Added initial support for workspaces.
//
//  Revision 1.27  2005/03/15 00:36:03  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.26  2005/01/10 23:06:04  tames_cvs
//  Updated to reflect reorganized TypeMap namespaces and GuiBuilder changes.
//
//  Revision 1.25  2005/01/10 21:08:08  tames_cvs
//  Changed the constructor and loadIrcManager methods to protected so
//  this class can be extended.
//
//  Revision 1.24  2005/01/09 06:13:22  tames
//  Added support for a TaskManager.
//
//  Revision 1.23  2005/01/07 21:31:16  tames
//  Added getScriptFactory method.
//
//  Revision 1.22  2004/12/14 20:43:29  tames
//  Added instantiateFromTypemap static method.
//
//  Revision 1.21  2004/11/23 22:33:48  tames_cvs
//  Fixed bug where Persistence Store reference was not being initialized.
//
//  Revision 1.20  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.19  2004/09/27 20:07:24  tames
//  Reflect changes to message router implementations and access methods.
//
//  Revision 1.18  2004/09/09 17:04:31  tames
//  comment change only
//
//  Revision 1.17  2004/09/04 13:26:07  tames
//  *** empty log message ***
//
//  Revision 1.16  2004/08/11 05:42:57  tames
//  Script support
//
//  Revision 1.15  2004/08/10 13:33:14  tames
//  Added message related methods.
//
//  Revision 1.14  2004/08/03 20:24:00  tames_cvs
//  Changes to reflect instantiateClass exception handling changes
//
//  Revision 1.13  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.12  2004/06/30 03:23:40  tames_cvs
//  Removed direct references to boot properties.
//
//  Revision 1.11  2004/06/08 20:52:33  chostetter_cvs
//  Added DataSpace, BasisBundleFactory access
//
//  Revision 1.10  2004/06/08 14:14:03  tames_cvs
//  added getGuiBuilder method
//
//  Revision 1.9  2004/06/04 14:28:28  tames_cvs
//  Added getDescriptorLibrary method
//
//  Revision 1.7  2004/06/01 16:01:08  tames_cvs
//  Added getDescriptorFramework method
//
//  Revision 1.5  2004/05/28 22:04:06  tames_cvs
//  added IrcManager logic and bootup implementation
//
//  Revision 1.4  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.3  2004/05/14 20:07:30  chostetter_cvs
//  Further tweaks, still needs work
//
//  Revision 1.2  2004/05/12 21:51:50  chostetter_cvs
//  Revised for design updates. Still needs some work.
//
//  Revision 1.1  2004/04/30 20:25:17  tames_cvs
//  Initial Version
//
//  Revision 1.1  2004/04/15 20:10:03  tames_cvs
//  initial version
//
