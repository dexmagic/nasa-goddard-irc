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

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.app.preferences.PrefKeys;
import gov.nasa.gsfc.commons.app.preferences.PreferenceManager;
import gov.nasa.gsfc.commons.processing.tasks.TaskManager;
import gov.nasa.gsfc.commons.publishing.EventBus;
import gov.nasa.gsfc.commons.publishing.selectors.MessageEventDestinationSelector;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.commons.system.resources.ResourceManager;
import gov.nasa.gsfc.commons.system.storage.PersistentObjectStore;
import gov.nasa.gsfc.commons.types.namespaces.Namespace;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;
import gov.nasa.gsfc.irc.components.ComponentFactory;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.components.description.ComponentSetDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleFactory;
import gov.nasa.gsfc.irc.data.BasisRequesterFactory;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.DescriptorException;
import gov.nasa.gsfc.irc.description.DescriptorLibrary;
import gov.nasa.gsfc.irc.description.DuplicateDescriptorNameException;
import gov.nasa.gsfc.irc.description.xml.LookupTable;
import gov.nasa.gsfc.irc.devices.DeviceProxy;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.description.DeviceSetDescriptor;
import gov.nasa.gsfc.irc.devices.description.RootDescriptor;
import gov.nasa.gsfc.irc.gui.GuiFactory;
import gov.nasa.gsfc.irc.messages.MessageFactory;
import gov.nasa.gsfc.irc.messages.MessageValidator;
import gov.nasa.gsfc.irc.scripts.ScriptEvaluator;
import gov.nasa.gsfc.irc.scripts.ScriptException;
import gov.nasa.gsfc.irc.scripts.ScriptFactory;
import gov.nasa.gsfc.irc.scripts.ScriptValidator;
import gov.nasa.gsfc.irc.ui.ProgressDisplay;
import gov.nasa.gsfc.irc.workspace.WorkspaceManager;

/**
 * This class provides the configuration manager for IRC, it is responsible 
 * for driving the application startup sequence as well as contructing
 * the application components.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/07/13 17:14:45 $
 * @author	Troy Ames
**/
public class DefaultIrcManager implements IrcManager
{
	private static final String CLASS_NAME = DefaultIrcManager.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private Logger fGlobalLogger = null;
	private String GLOBAL_LOGGER_NAME = "";

	private WorkspaceManager fWorkspaceManager = null;

	private Namespace fGlobalNamespace = null;
	private ComponentManager fComponentManager = null;
	private ComponentFactory fComponentFactory = null;
	private DescriptorFramework fDescriptorFramework = null;
	private PersistentObjectStore fObjectStore = null;
	private DescriptorLibrary fDescriptorLibrary = null;
	private GuiFactory fGuiBuilder = null;
	private DataSpace fDataSpace = null;
	private BasisBundleFactory fBasisBundleFactory = null;
	private BasisRequesterFactory fBasisRequesterFactory = null;
	
	private MessageFactory fMessageFactory = null;
	private MessageValidator fMessageValidator = null;
	private EventBus fEventBus = null;
	
	private TaskManager fTaskManager = null;

	private ScriptFactory fScriptFactory = null;
	private ScriptValidator fScriptValidator = null;
	private ScriptEvaluator fScriptEvaluator = null;
	
//	private String fMainScript = null;
	private boolean fGraphicsEnabled = false;
	private String fPropertiesFile = null;
	private HashMap fArgumentProperties = new HashMap(2);
	private Thread fConsoleThread = null;

	/** Namespace name in type map for default managers. */
	public static final String MANAGER_NAMESPACE = "ManagerType";

	/** Namespace name in type map for default factories. */
	public static final String FACTORY_NAMESPACE = "FactoryType";

	/** Name or default calibration set */
	public static final String DEFAULT_CALIBRATION_SET_NAME = "__Defaults";

	/** Plist extension */
	public static final String PLIST_EXT = ".plist";

	/** Default filename for DataObjectStore. */
	public static final String DEF_DATA_STORE_NAME = "PersistentObjectStore";
	
	/** Default number of logs to keep around */
	public static final int DEFAULT_LOG_KEEP = 15;

	/** Log file extension */
	public static final String LOG_EXT = "txt";
	
	private String DEFAULT_PLIST = "resources/configurations/irc.plist";

	private ProgressDisplay fProgress;

	/**
	 * Initialize the application based on the specified arguments. 
	 *
	 * @param argv an array of arguments 
	 * @see gov.nasa.gsfc.irc.app.IrcManager#initializeApp(java.lang.String[])
	 */
	public synchronized void initializeApp(String argv[])
	{
		// Set any environment related flags
		initializeEnvironment();

		// Track startup progress and inform user
		fProgress = new ProgressDisplay(fGraphicsEnabled);

		//---Process command line arguments
		fProgress.setString("Processing command line arguments");
		fProgress.setPercentComplete(0.0f);
		processArguments(argv);
							
		// Load preferences
		fProgress.setString("Loading preferences");
		fProgress.setPercentComplete(0.1f);
		loadPreferences();
		
		// Initialize global namespace
		loadGlobalNamespace();
		
		//---Setup logging
		fProgress.setString("Initializing logging");
		fProgress.setPercentComplete(0.2f);
		initializeLogging();

		//---Initialize typemap
		fProgress.setString("Initializing type map");
		fProgress.setPercentComplete(0.3f);
		loadTypeMap();

		//---Find the component types
		fProgress.setString("Loading Component Types");
		fProgress.setPercentComplete(0.4f);
		loadComponentTypes();

		//---Get handle to descriptor library
		//DescriptorLibrary library = DescriptorLibrary.getInstance();

		//---Setup peer network interface
		fProgress.setString("Initializing Peer Network");
		fProgress.setPercentComplete(0.5f);
		initializePeerNetwork();
		
		//---Setup this device interface
		fProgress.setString("Initializing Device");
		fProgress.setPercentComplete(0.6f);
		initializeDevice();
				
		//---Load the command procedures specified in the irc.plist and store them
		//   in the descriptor library.
		fProgress.setString("Loading Script Library");
		fProgress.setPercentComplete(0.65f);
		loadGeneralScripts();

		//---Initialize the algorithm plugin bus
		//PipelineElementPluginBus.getInstance();

		//---Open the ports

		//  Initialize the work space catalog.
		//new PipelineRestorer();

		//---Create the components as specified in a CML file
		fProgress.setString("Loading Components");
		fProgress.setPercentComplete(0.7f);
		initializeComponents();

		//---Handle startup script
		fProgress.setString("Running Startup Script");
		fProgress.setPercentComplete(0.8f);
		runStartupScript();

		//---Initialize GUI components if specified
		fProgress.setString("Initializing GUI");
		fProgress.setPercentComplete(0.9f);
		initializeGui();

		//---Initialize console UI if specified
		fProgress.setString("Initializing Console");
		initializeConsoleInterface();

		fProgress.setString("Startup Completed");
		fProgress.setPercentComplete(1.0f);

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Startup completed...";
				
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"main", message);
		}
	}
	
	/**
	 * Add the device specified by the given descriptor. 
	 *
	 * @param descriptor a device descriptor.
	**/
	public void addExternalDevice(DeviceDescriptor descriptor)
	{
		ComponentManager componentMgr = Irc.getComponentManager();
		PreferenceManager prefMgr = Irc.getPreferenceManager();
		boolean autoStart = 
			Boolean.valueOf(
					prefMgr.getPreference(IrcPrefKeys.IRC_COMPONENT_AUTOSTART))
					.booleanValue();

		// Remove existing device in ComponentManager if it exists		
		String deviceName = descriptor.getFullyQualifiedName();
		MinimalComponent device = componentMgr.getComponent(deviceName);
		
		if (device != null)
		{
			// Stop, kill, and remove component
			componentMgr.stopComponent(deviceName);
			componentMgr.killComponent(deviceName);
			componentMgr.removeComponent(deviceName);
		}

		getDescriptorLibrary().replaceDevice(
			descriptor.getName(), descriptor);
		
		// instantiate proxy from descriptor
		String className = descriptor.getClassName();
		try
		{
			ComponentFactory factory = Irc.getComponentFactory();
			DeviceProxy proxy =
				(DeviceProxy) factory.createComponent(className);

			proxy.setDescriptor(descriptor);
			componentMgr.addComponent(proxy);
			
			// Set up component connections 
			configureProxyConnections(proxy, descriptor);
			
			// Start Component
			if (autoStart)
			{
				proxy.start();
			}
		}
		catch (Exception e)
		{
			String message = "Error adding instrument";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addExternalDevice", message, e);
		}
	}
	
	/**
	 * Add a new instrument from the description located at the specified URL. 
	 *
	 * @param url URL of device's IML description
	**/
	public void addExternalDevice(URL url)
	{
		RootDescriptor rootDescriptor = null;
		DescriptorFramework descriptorFramework = getDescriptorFramework();

		try
		{
			rootDescriptor = descriptorFramework.loadInstruments(url);
			
			if (rootDescriptor instanceof DeviceSetDescriptor)
			{
				
				Iterator instruments = 
					((DeviceSetDescriptor) rootDescriptor).getInstruments();

				while (instruments.hasNext())
				{
					DeviceDescriptor descriptor = 
						(DeviceDescriptor) instruments.next();
					
					addExternalDevice(descriptor);
				}
			}
			else if (rootDescriptor instanceof DeviceDescriptor)
			{
				DeviceDescriptor descriptor = 
					(DeviceDescriptor) rootDescriptor;
				
				addExternalDevice(descriptor);
			}
		}
		catch (DuplicateDescriptorNameException e)
		{
			String message = "Error adding instrument";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addExternalDevice", message, e);
		}
		catch (DescriptorException e)
		{
			String message = "Error reading instrument description";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addExternalDevice", message, e);
		}
	}

	/**
	 * Add a new component with the given descriptor. 
	 *
	 * @param descriptor Descriptor of the component to add
	**/
	public void addComponent(ComponentDescriptor descriptor)
	{
		ComponentManager componentMgr = Irc.getComponentManager();
		ComponentFactory componentFactory = Irc.getComponentFactory();

		try
		{
			String deviceName = descriptor.getFullyQualifiedName();
			MinimalComponent device = componentMgr.getComponent(deviceName);
			
			// Remove existing device in ComponentManager if it exists
			if (device != null)
			{
				// Stop, kill, and remove component
			}

			// Instantiate component from descriptor
			MinimalComponent component = 
				componentFactory.createComponent(descriptor);

			Irc.getComponentManager().addComponent(component);
		}
		catch (Exception e)
		{
			String message = "Error adding component";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addComponent", message, e);
		}
	}

	/**
	 * Add a new component from the description located at the specified URL. 
	 *
	 * @param url URL of components's CML description
	**/
	public void addComponent(URL url)
	{
		//RootDescriptor rootDescriptor = null;
		Descriptor rootDescriptor = null;
		DescriptorFramework descriptorFramework = getDescriptorFramework();

		try
		{
			rootDescriptor = descriptorFramework.loadComponentElement(url);
			

			if (rootDescriptor instanceof ComponentDescriptor)
			{
				addComponent((ComponentDescriptor) rootDescriptor);
			}
			else if (rootDescriptor instanceof ComponentSetDescriptor)
			{
				Iterator components = 
					((ComponentSetDescriptor) rootDescriptor).getComponents();

				while (components.hasNext())
				{
					ComponentDescriptor descriptor = 
						(ComponentDescriptor) components.next();
					
					addComponent(descriptor);
				}				
			}
		}
		catch (DuplicateDescriptorNameException e)
		{
			String message = "Error adding component";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addComponent", message, e);
		}
		catch (DescriptorException e)
		{
			String message = "Error reading component description";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addComponent", message, e);
		}
		catch (Exception e)
		{
			String message = "Error adding component";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, 
				"addComponent", message, e);
		}
	}

	/**
	 * Add a new GUI panel from the description located at the specified URL. 
	 *
	 * @param url URL of device's IML description
	**/
	public void addGuiPanel(URL url)
	{
	}

	/**
	 * Returns the DataSpace to be used by the current instance of IRC.
	 * 
	 * @return	The DataSpace to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized DataSpace getDataSpace()
	{
		if (fDataSpace == null)
		{
			fDataSpace = loadDataSpace();
		}
		
		return (fDataSpace);
	}

	/**
	 * Returns the script factory to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadScripteFactory</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The ScriptFactory to be used by the current instance of IRC
	**/
	public synchronized ScriptFactory getScriptFactory()
	{
		if (fScriptFactory == null)
		{
			fScriptFactory = loadScriptFactory();
		}
		
		return (fScriptFactory);
	}

	/**
	 * Returns the script evaluator to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadScriptEvaluator</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The ScriptEvaluator to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized ScriptEvaluator getScriptEvaluator()
	{
		if (fScriptEvaluator == null)
		{
			fScriptEvaluator = loadScriptEvaluator();
		}
		
		return (fScriptEvaluator);
	}

	/**
	 * Returns the script validator to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadScriptValidator</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The ScriptValidator to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized ScriptValidator getScriptValidator()
	{
		if (fScriptValidator == null)
		{
			fScriptValidator = loadScriptValidator();
		}
		
		return (fScriptValidator);
	}

	/**
	 * Returns the EventBus to be used by the current instance of 
	 * IRC. The first time this method is called it will call 
	 * the <code>loadEventBus</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The EventBus to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized EventBus getEventBus()
	{
		if (fEventBus == null)
		{
			fEventBus = loadEventBus();
		}
		
		return (fEventBus);
	}

	/**
	 * Returns the BasisBundleFactory to be used by the current instance of 
	 * IRC. The first time this method is called it will call the
	 * <code>loadBasisBundleFactory</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return The BasisBundleFactory to be used by the current instance of 
	 *  		IRC
	**/
	public synchronized BasisBundleFactory getBasisBundleFactory()
	{
		if (fBasisBundleFactory == null)
		{
			fBasisBundleFactory = loadBasisBundleFactory();
		}
		
		return (fBasisBundleFactory);
	}

	/**
	 * Returns the BasisRequesterFactory to be used by the current instance of 
	 * IRC. The first time this method is called it will call the
	 * <code>loadBasisRequesterFactory</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return The BasisRequesterFactory to be used by the current instance of 
	 *  		IRC
	**/
	public synchronized BasisRequesterFactory getBasisRequesterFactory()
	{
		if (fBasisRequesterFactory == null)
		{
			fBasisRequesterFactory = loadBasisRequesterFactory();
		}
		
		return (fBasisRequesterFactory);
	}

	/**
	 * Returns the ComponentFactory to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadComponentFactory</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The ComponentFactory to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized ComponentFactory getComponentFactory()
	{
		if (fComponentFactory == null)
		{
			fComponentFactory = loadComponentFactory();
		}
		
		return (fComponentFactory);
	}
	
 	/**
	 * Returns the ComponentManager to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadComponentManager</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The ComponentManager to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized ComponentManager getComponentManager()
	{
		if (fComponentManager == null)
		{
			fComponentManager = loadComponentManager();
		}

		return (fComponentManager);
	}
	
 	/**
	 * Returns the global Namespace to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadGlobalNamespace</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The global Namespace to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized Namespace getGlobalNamespace()
	{
		if (fGlobalNamespace == null)
		{
			fGlobalNamespace = loadGlobalNamespace();
		}

		return (fGlobalNamespace);
	}
	
 	/**
	 * Returns the WorkspaceManager to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadWorkspaceManager</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The WorkspaceManager to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized WorkspaceManager getWorkspaceManager()
	{
		if (fWorkspaceManager == null)
		{
			fWorkspaceManager = loadWorkspaceManager();
		}

		return (fWorkspaceManager);
	}
	
	/**
	 * Returns the DescriptorFramework to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The DescriptorFramework to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized DescriptorFramework getDescriptorFramework()
	{
		if (fDescriptorFramework == null)
		{
			fDescriptorFramework = new DescriptorFramework();
		}
		
		return (fDescriptorFramework);
	}
	
	/**
	 * Returns the DescriptorLibrary to be used by the current instance 
	 * of IRC.
	 * 
	 * @return	The DescriptorLibrary to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized DescriptorLibrary getDescriptorLibrary()
	{
		if (fDescriptorLibrary == null)
		{
			fDescriptorLibrary = new DescriptorLibrary();
		}
		
		return (fDescriptorLibrary);
	}
	
	/**
	 * Returns the message factory to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadMessageFactory</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The MessageFactory to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized MessageFactory getMessageFactory()
	{
		if (fMessageFactory == null)
		{
			fMessageFactory = loadMessageFactory();
		}
		
		return (fMessageFactory);
	}

	/**
	 * Returns the message validator to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadMessageValidator</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The MessageValidator to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized MessageValidator getMessageValidator()
	{
		if (fMessageValidator == null)
		{
			fMessageValidator = loadMessageValidator();
		}
		
		return (fMessageValidator);
	}

	/**
	 * Returns the task manager to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadTaskManager</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The TaskManager to be used by the current instance of IRC
	**/
	public synchronized TaskManager getTaskManager()
	{
		if (fTaskManager == null)
		{
			fTaskManager = loadTaskManager();
		}
		
		return (fTaskManager);
	}

	/**
	 * Returns the GUI Builder to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadGuiBuilder</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The GuiFactory to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized GuiFactory getGuiFactory()
	{
		if (fGuiBuilder == null)
		{
			fGuiBuilder = loadGuiFactory();
		}
		
		return (fGuiBuilder);
	}

    /**
     * Obtains the master properties file specified in the command line
     * arguments.
     * 
     * @return  String  The master properties filename
     */
    public String getPropertiesFile()
    {
        return fPropertiesFile;
    }
    
    /**
     * Returns the file name for the user properties file.
     * 
     * @return  absolute path for user home directory location
    **/
    public String getUserPropertiesFile()
    {
        String propertiesFile = getPropertiesFile();
        String result = propertiesFile;
        if (propertiesFile != null)
        {
            int index = propertiesFile.lastIndexOf("/");
            if (index != -1)
            {
                // Eliminate te directory to extract the file name
                result = propertiesFile.substring(index+1);
            }
            result = PreferenceManager.USER_PREF_PREFIX + result;
        }
        return result;
    }
    
	/**
	 * Returns the PersistentObjectStore to be used by the current instance 
	 * of IRC. The first time this method is called it will call the
	 * <code>loadPersistentStore</code> method to create and cache 
	 * an instance for all future calls.
	 * 
	 * @return	The PersistentObjectStore to be used by the current instance 
	 *  		of IRC
	**/
	public synchronized PersistentObjectStore getPersistentStore()
	{
		if (fObjectStore == null)
		{
			fObjectStore = loadPersistentStore();
		}

		return (fObjectStore);
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
	public Object instantiateFromTypemap(String namespace, String key)
	{
		DescriptorFramework desciptorFramework = getDescriptorFramework();

		//TODO should this class use Bean.instantiate or component factory
		return desciptorFramework.instantiateFromGlobalMap(namespace, key);
	}
		
	/**
	 * This method will save the Object store and execute the shutdown script 
	 * specified in the properties plist file.
	**/
	public int shutdown()
	{
		if (fObjectStore != null)
		{
			fObjectStore.saveToDisk();
		}
		      
        //Stop all components
        getComponentManager().stopAllComponents();
		
		return 0;
	}

	/**
	 * Print a usage message.
	**/
	public void usage()
	{
		System.out.println("usage: java -classpath <pathinfo> "
			+ "gov.nasa.gsfc.irc.app.Irc [-PpropertiesFile] [-EscriptFile] "
			+"[-DsystemProperty] [-n]");
	}
	
	/**
	 * Configures the proxy connections. The default behavior is to register the
	 * proxy as a listener for Events from the source returned by the
	 * <code>getEventBus</code> method and register the listener returned by
	 * the <code>getEventBus</code> method with this proxy for bus events.
	 * 
	 * @param proxy the Instrument proxy to connect
	 * @param descriptor the descriptor of the proxy to connect
	 * @see #getEventBus()
	 */
	protected void configureProxyConnections(
			DeviceProxy proxy, DeviceDescriptor descriptor)
	{
		// Register proxy to the event bus.
		getEventBus().addBusEventListener(
			new MessageEventDestinationSelector(descriptor.getPath()), proxy);
		
		// Register the event bus with the proxy.
		proxy.addBusEventListener(getEventBus());
	}

	/**
	 * Loads the type maps defined by the 
	 * {@link IrcPrefKeys#DEFAULT_TYPE_MAP DEFAULT_TYPE_MAP},
	 * {@link IrcPrefKeys#INSTRUMENT_TYPE_MAP INSTRUMENT_TYPE_MAP}, and 
	 * {@link IrcPrefKeys#USER_TYPE_MAP USER_TYPE_MAP} preference keys.
	 */
	protected synchronized void loadTypeMap()
	{
		DescriptorFramework desciptorFramework = getDescriptorFramework();
		LookupTable table = null;
		
		//---Get default typemap file
		URL typeMapURL =
			Irc.getResource(Irc.getPreference(IrcPrefKeys.DEFAULT_TYPE_MAP));

		if (typeMapURL != null)
		{
			try
			{
				//---Load the default type map file
				table = desciptorFramework.loadLookupTable(typeMapURL);
				
				//---Get the instrument type map file
				typeMapURL = 
					Irc.getResource(
							Irc.getPreference(IrcPrefKeys.INSTRUMENT_TYPE_MAP));
				
				//---Load the instrument type map file
				if (typeMapURL != null)
				{
					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Loading instrument typemap:" 
							+ typeMapURL;
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
								"loadTypeMap", message);
					}
					
					table.merge(desciptorFramework.loadLookupTable(typeMapURL));
				}
				
				for (int fileNum = 0; fileNum < 11; fileNum++)
				{
					
					String postfix = "";
					if (fileNum > 0)
					{
						postfix = String.valueOf(fileNum);
					}
					//---Get the user type map file
					typeMapURL = Irc.getResource(Irc
							.getPreference(IrcPrefKeys.USER_TYPE_MAP + postfix));

					//---Load the user type map file
					if (typeMapURL != null) {
						if (sLogger.isLoggable(Level.INFO)) {
							String message = "Loading user typemap:"
									+ typeMapURL;

							sLogger.logp(Level.INFO, CLASS_NAME, "loadTypeMap",
									message);
						}

						table.merge(desciptorFramework
								.loadLookupTable(typeMapURL));
					}
				}
				
				desciptorFramework.setGlobalMap(table);
			}
			catch (DescriptorException e)
			{
				String message =
					"Exception trying to create type map";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"loadTypeMap", message, e);
			}
		}
		//---Complain if the we can not get default typemap url
		else
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message =
					"Invalid or undefined property: "
					+ IrcPrefKeys.DEFAULT_TYPE_MAP;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"loadTypeMap", message);
			}
		}
	}

	/**
	 *  This method will retrieve the CML file specified in the plist file.
	 *  Next, it needs to use the PipelineElementLibrary to parse the
	 *  the file and finally it will add each PipelineElement type to the
	 *  DescriptorLibrary.
	**/
	protected synchronized void loadComponentTypes()
	{
//		String pemlFilename = System.getProperty
//			(IrcPrefKeys.PIPELINE_ELEMENT_TYPES);
//		
//		if (pemlFilename == null || pemlFilename.length() == 0)
//		{
//			return;
//		}
//
//		URL pemlURL = getResource(pemlFilename);
//		PipelineElementLibraryDescriptor peld = null;
//
//		try
//		{
//			peld = DescriptorFramework.getInstance().
//				loadPipelineElementLibrary(pemlURL);
//			peld.addPipelineElementsToDescriptorLibrary();
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
	}

	/**
	 * This method will instantiate the components from a CML file 
	 * specified in the plist file.
	**/
	protected synchronized void initializeComponents()
	{
		String cmlFilename = System.getProperty
			(IrcPrefKeys.COMPONENT_DESCRIPTION);
		
		if (cmlFilename == null || cmlFilename.length() == 0)
		{
			return;
		}

		URL cmlURL = Irc.getResource(cmlFilename);
		addComponent(cmlURL);
//		PipelineElementLibraryDescriptor peld = null;
//
//		try
//		{
//			peld = DescriptorFramework.getInstance().
//				loadPipelineElementLibrary(pemlURL);
//			peld.addPipelineElementsToDescriptorLibrary();
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
	}

	/**
	 * Initializes a console interface if the console is enabled, 
	 * {@link IrcPrefKeys#CONSOLE_COMMANDING_FLAG CONSOLE_COMMANDING_FLAG}, 
	 * and there is a "ConsoleManager" type defined in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 */
	protected synchronized void initializeConsoleInterface()
	{
		Runnable console = (Runnable) instantiateFromTypemap(
				MANAGER_NAMESPACE,
				"ConsoleManager");
		String consoleEnabledStr = 
			Irc.getPreference(IrcPrefKeys.CONSOLE_COMMANDING_FLAG);
		boolean consoleEnabled = 
			Boolean.valueOf(consoleEnabledStr).booleanValue();
			
		if (console != null && consoleEnabled)
		{
			fConsoleThread = new Thread(console);
			fConsoleThread.setName("Console Interpreter");
			fConsoleThread.start();
		}
	}
	
	/**
	 * Initializes this device. Currently this only calls 
	 * <code>loadInstruments</code>.
	 * @see #loadInstruments()
	 */
	protected synchronized void initializeDevice()
	{
		//---Setup interface to external instrument/devices
		loadInstruments();
		
//		String serviceFilename = Irc.getPreference(IrcPrefKeys.SERVICE_DESCRIPTION);
//		if(serviceFilename != null)
//		{
//				if (sLogger.isLoggable(Level.INFO))
//				{
//					String message = "Loading service interface:" + serviceFilename;
//						
//					sLogger.logp(Level.INFO, CLASS_NAME, 
//						"main", message);
//				}
//
//			InstrumentManager.getInstance().setServiceDescription(
//				getResource(serviceFilename));
//		}
//
//		//---Setup client interface
//		String clientFilename = Irc.getPreference(IrcPrefKeys.CLIENT_DESCRIPTION);
//			
//		if(clientFilename != null)
//		{
//				if (sLogger.isLoggable(Level.INFO))
//				{
//					String message = "Loading client interface:" + clientFilename;
//						
//					sLogger.logp(Level.INFO, CLASS_NAME, 
//						"main", message);
//				}
//
//			InstrumentManager.getInstance().setClientDescription(
//				getResource(clientFilename));
//		}
//
//
	}
	
	/**
	 * Initializes the peer to peer network for this device. Currently this
	 * method is diabled until the underlying JXTA is updated.
	 */
	protected synchronized void initializePeerNetwork()
	{
//		String networkEnabled = Irc.getPreference(PrefKeys.NETWORK_ENABLED, "false");
//		if (networkEnabled.equalsIgnoreCase("true"))
//		{
//				if (sLogger.isLoggable(Level.INFO))
//				{
//					String message = "Configuring peer network";
//						
//					sLogger.logp(Level.INFO, CLASS_NAME, 
//						"main", message);
//				}
//
//			PeerNetworkManager netMgr = PeerNetworkManager.getInstance();
//
//			String name = Irc.getPreference(PrefKeys.NETWORK_DESCRIPTION_NAME);
//			String urlString = Irc.getPreference(PrefKeys.NETWORK_DESCRIPTION_URL);
//			String group = Irc.getPreference(PrefKeys.NETWORK_GROUP);
//
//			// group can be null
//			if (name != null && urlString != null)
//			{
//				netMgr.publishDescription(group, name, new URL(urlString));
//			}
//		}
//
	}
	
	/**
	 * Initializes logging.
	 * 
	 * @see PrefKeys#LOG_CONFIG_FILE
	 * @see PrefKeys#LOG_LEVEL
	 */
	protected synchronized void initializeLogging()
	{
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Setting up logging";
				
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"initializeLogging", message);
		}
		
		// Check if there is a user defined log config file
		String fileString = Irc.getPreference(PrefKeys.LOG_CONFIG_FILE);
		
		if (fileString != null && fileString.length() > 0)
		{
			URL fileUrl = Irc.getResource(fileString);
			
			if (fileUrl != null)
			{
				System.setProperty(
						"java.util.logging.config.file", 
						fileUrl.getFile());
				try
				{
					InputStream fileStream = fileUrl.openStream();
					
					// Reset configuration
					LogManager.getLogManager().readConfiguration(fileStream);
				}
				catch (SecurityException e) 
				{
					String message = "Error initializing logging";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"initializeLogging", message, e);
				} 
				catch (IOException e) 
				{
					String message = "Error initializing logging";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"initializeLogging", message, e);
				}
			}
		}

		// Configure global logger
		fGlobalLogger = Logger.getLogger(GLOBAL_LOGGER_NAME);
		String levelStr = Irc.getPreference(PrefKeys.LOG_LEVEL);
		
		if (levelStr != null)
		{
			try
			{
				fGlobalLogger.setLevel(Level.parse(levelStr));
			}
			catch (IllegalArgumentException e)
			{
				String message = "Exception setting log level: ";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"initializeLogging", message, e);
			}
		}
		
		// Configure global logger handlers
		String handlersStr = Irc.getPreference(PrefKeys.LOG_HANDLERS);
		
		if (handlersStr != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(handlersStr, ", ");

			while (tokenizer.hasMoreTokens())
			{
				String handlerName = tokenizer.nextToken();
				
				try
				{
					Object handler = Irc.instantiateClass(handlerName);
					
					if (handler instanceof Handler)
					{
						fGlobalLogger.addHandler((Handler) handler);
					}
				}
				catch (Exception e)
				{
					String message = "Exception adding log handler: ";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"initializeLogging", message, e);
				}
			}
		}
	}
	
	/**
	 * This method loads the application preferences.
	**/
	protected synchronized void loadPreferences()
	{
		ResourceManager resMgr = Irc.getResourceManager();
		PreferenceManager prefMgr = Irc.getPreferenceManager();
		
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Loading Preferences";
				
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadPreferences", message);
		}
		
		// Load generic application preferences
		String defaultPlist = 
			prefMgr.getPreference(IrcPrefKeys.DEFAULT_PLIST_KEY);
		
		if (defaultPlist == null)
		{
			defaultPlist = DEFAULT_PLIST;
		}
		
		URL applicationPrefs = resMgr.getResource(defaultPlist);
		prefMgr.loadPreferences(applicationPrefs);
		
		// Load optional application preferences specific to headless 
		// graphics mode.
		if (!fGraphicsEnabled)
		{
			// Load optional application preferences for a no GUI
			// environment
			String noGuiPlist = 
				prefMgr.getPreference(IrcPrefKeys.NO_GRAPHICS_PLIST_KEY);
			
			if (noGuiPlist != null)
			{
				URL prefs = resMgr.getResource(noGuiPlist);
				prefMgr.loadPreferences(prefs);
			}
		}
		
		// Load instrument and user proferences
		if (fPropertiesFile != null)
		{
			// Load instrument preferences
			URL instrumentPrefs = resMgr.getResource(fPropertiesFile);
			prefMgr.loadPreferences(instrumentPrefs);

			// Load user preferences
			URL userPrefs = resMgr.getResource("user_" + fPropertiesFile);
			
			if (userPrefs != null)
			{
				prefMgr.loadUserPreferences(userPrefs);
			}
		}

		// Load any command line properties specified, these will over ride
		// all preferences loaded above.
		if (fArgumentProperties != null)
		{
			Iterator properties = fArgumentProperties.keySet().iterator();
			
			while (properties.hasNext())
			{
				String key = (String) properties.next();
				prefMgr.setPreference(key, (String) fArgumentProperties.get(key));
			}
		}
	}

	/**
	 *  This method processes the applications command line arguments.
	 *  Specifically, it addresses the following argumments:
	 *  <tr align="center">
	 *	  <td>-D</td>
	 *	  <td align="left">Define a system property 
	 * 			(this overrides any value in the .plist)</td>
	 *  </tr>
	 *  <tr align="center">
	 *	  <td>-E</td>
	 *	  <td align="left">Define the main script to run (this overrides
	 *  		the script specified by MAIN_SCRIPT_PROP)</td>
	 *  </tr>
	 *  <tr align="center">
	 *	  <td>-P</td>
	 *	  <td align="left">Define the properties file to load</td>
	 *  </tr>
	 * 
	 * @param argv the array of arguments to process
	**/
	protected synchronized void processArguments(String argv[])
	{
		StringBuffer message = new StringBuffer("Processing Arguments \" ");
			
		for (int i=0; i < argv.length; i++)
		{
			message.append(argv[i].toString() + " ");
		}
			
		message.append("\"");
		sLogger.logp(Level.INFO, CLASS_NAME, "processArguments", message.toString());
			
		for (int i = 0; i < argv.length; ++i)
		{
			String arg = argv[i];

			if (arg.length() > 1 && arg.startsWith("-"))
			{
				switch (arg.charAt(1))
				{
				// -D: Define a system property (this overrides
				//	 any value in the .plist)
				case 'D':
					int eq = arg.indexOf("=");
					if (eq > 0)
					{
						// Cache property so it can be applied later
						fArgumentProperties.put(
							arg.substring(2, eq), arg.substring(eq+1));
					}
					break;

				// -E: Define the main script to run (this overrides
				//	 the script specified by MAIN_SCRIPT_PROP)
//				case 'E':
//					fMainScript = arg.substring(2);
//					break;

				// -P: Specify master properties file
				case 'P':
					fPropertiesFile = arg.substring(2);
					//---Make sure the plist file as the correct extension
					if (!fPropertiesFile.endsWith(PLIST_EXT))
					{
						fPropertiesFile = fPropertiesFile + PLIST_EXT;
					}
				   break;

				default:
					handleUnknownArgument(arg);
					break;
				}
			}
			else if (arg.length() <= 1)
			{
				handleUnknownArgument(arg);
				break;
			}
		}
	}

	/**
	 * Initializes the environment variables.
	 */
	protected synchronized void initializeEnvironment()
	{
		boolean graphicsEnabled = true;

		try
		{
			if (java.awt.GraphicsEnvironment.isHeadless())
			{
				graphicsEnabled = false;
			}
			else
			{
				// Check to see if the gui is disabled by the user
				String guiDisabled = 
					Irc.getPreference(IrcPrefKeys.IRC_MANAGER_GUI_DISABLED);
				
				if (Boolean.valueOf(guiDisabled).booleanValue())
				{
					graphicsEnabled = false;
				}
			}
		}
		catch (RuntimeException e)
		{
			// if for any reason the graphics environment is not reachable
			// then disable graphics.
			graphicsEnabled = false;
		}
		
		fGraphicsEnabled = graphicsEnabled;
	}

	/**
	 * If the <code>isGraphicsEnabled</code> method returns true the GUI will
	 * be initialized for this instance by
	 * calling <code>getGuiBuilder</code> to render the GUI defined by the 
	 * {@link IrcPrefKeys.GUI_DESKTOP GUI_DESKTOP} preference key.
	 */
	protected synchronized void initializeGui()
	{
		if (isGraphicsEnabled())
		{
			GuiFactory builder = getGuiFactory();

			URL url = Irc.getResource(Irc.getPreference(IrcPrefKeys.GUI_DESKTOP));
			
			if (url != null)
			{
				try
				{
					builder.render(url);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Handle an unknown commandline argument. Calls <code>usage</code>
	 * method.
	 *
	 * @param argument the unknown argument
	**/
	protected synchronized void handleUnknownArgument(String argument)
	{
		System.out.println("Unknown option: " + argument);
		usage();
		//System.exit(ERROR_TERMINATION);
	}

	/**
	 * Runs the startup script defined by the 
	 * {@link IrcPrefKeys#STARTUP_SCRIPT STARTUP_SCRIPT} preference key.
	 */
	protected synchronized void runStartupScript()
	{
		String script = Irc.getPreference(IrcPrefKeys.STARTUP_SCRIPT);
		
		if(script != null && script.trim().length() > 0)
		{
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Running startup script:" + script;
					
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"runStartupScript", message);
			}

			URL scriptUrl = Irc.getResource(script);
			
			if (scriptUrl != null)
			{
				try
				{
					getScriptEvaluator().execute(scriptUrl, null);
				}
				catch (ScriptException e)
				{
					if (sLogger.isLoggable(Level.WARNING))
					{
						String message = 
							"Exception running startup script:" + scriptUrl;
							
						sLogger.logp(Level.WARNING, CLASS_NAME, 
							"runStartupScript", message, e);
					}
				}
			}
			else
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Startup script not found:" + script;
						
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"runStartupScript", message);
				}
			}
		}
	}
	
	/**
	 * Loads the general purpose scripts defined by the 
	 * {@link IrcPrefKeys#SCRIPTS_DESCRIPTION SCRIPTS_DESCRIPTION}
	 * preference key.
	 */
	protected synchronized void loadGeneralScripts()
	{
//		String commandProceduresFilename	 = Irc.getPreference(IrcPrefKeys.COMMAND_PROCEDURES);
//		CommandProcedureGroupDescriptor cpgd = null;
//		if(commandProceduresFilename != null && commandProceduresFilename.length()>0)
//		{
//				if (sLogger.isLoggable(Level.INFO))
//				{
//					String message = "Loading command procedures:" + 
//					commandProceduresFilename;
//						
//					sLogger.logp(Level.INFO, CLASS_NAME, 
//						"main", message);
//				}
//
//			URL commandProceduresURL = getResource(commandProceduresFilename);
//			try
//			{
//				cpgd = DescriptorFramework.getInstance().loadCommandProcedures(commandProceduresURL);
//				if(cpgd != null)
//				{
//					library.addCommandProcedureGroup(cpgd);
//				}
//			}
//			catch(Exception ex)
//			{
//					if (sLogger.isLoggable(Level.WARNING))
//					{
//						String message = "Could not load command procedures";
//							
//						sLogger.logp(Level.WARNING, CLASS_NAME, 
//							"main", message, ex);
//					}
//			}
//		}
	}
	
	/**
	 * Loads the component specified by the "DataSpaceManager" key in
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * If the class cannot be instantiated from the type map this 
	 * implementation will return an instance of
	 * {@link gov.nasa.gsfc.irc.data.DefaultDataSpace DefaultDataSpace}.
	 * 
	 * @return a DataSpace component
	 * @see #instantiateFromTypemap
	 */
	protected synchronized DataSpace loadDataSpace()
	{
		DataSpace dataSpace =
			(DataSpace) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"DataSpaceManager");
		
		if (dataSpace == null)
		{
			dataSpace = 
				(DataSpace) instantiateClass(
					"gov.nasa.gsfc.irc.data.DefaultDataSpace");
		}
		
		return dataSpace;
	}
	
	/**
	 * Loads the component specified by the "BasisBundleFactory" key in 
	 * the {@linkplain #FACTORY_NAMESPACE} of the global type map.
	 * If the class cannot be instantiated from the type map this 
	 * implementation will return an instance of
	 * {@link gov.nasa.gsfc.irc.data.DefaultBasisBundleFactory DefaultBasisBundleFactory}.
	 * 
	 * @return a BasisBundleFactory component
	 * @see #instantiateFromTypemap
	 */
	protected synchronized BasisBundleFactory loadBasisBundleFactory()
	{
		BasisBundleFactory basisBundleFactory =
			(BasisBundleFactory) instantiateFromTypemap(
				FACTORY_NAMESPACE,
				"BasisBundleFactory");
				
		if (basisBundleFactory == null)
		{
			basisBundleFactory = 
				(BasisBundleFactory) instantiateClass(
					"gov.nasa.gsfc.irc.data.DefaultBasisBundleFactory");
		}

		return basisBundleFactory;
	}
	
	/**
	 * Loads the component specified by the "BasisRequesterFactory" key in 
	 * the {@linkplain #FACTORY_NAMESPACE} of the global type map.
	 * If the class cannot be instantiated from the type map this 
	 * implementation will return an instance of
	 * {@link gov.nasa.gsfc.irc.data.DefaultBasisRequesterFactory DefaultBasisRequesterFactory}.
	 * 
	 * @return a BasisRequesterFactory component
	 * @see #instantiateFromTypemap
	 */
	protected synchronized BasisRequesterFactory loadBasisRequesterFactory()
	{
		BasisRequesterFactory factory =
			(BasisRequesterFactory) instantiateFromTypemap(
				FACTORY_NAMESPACE,
				"BasisRequesterFactory");
				
		if (factory == null)
		{
			factory = 
				(BasisRequesterFactory) instantiateClass(
					"gov.nasa.gsfc.irc.data.DefaultBasisRequesterFactory");
		}

		return factory;
	}
	
	/**
	 * Loads the component specified by the "ComponentFactory" key in 
	 * the {@linkplain #FACTORY_NAMESPACE} of the global type map. 
	 * If the class cannot be instantiated from the type map this 
	 * implementation will return an instance of
	 * {@link gov.nasa.gsfc.irc.components.DefaultComponentFactory 
	 * DefaultComponentFactory}.
	 * 
	 * @return a ComponentFactory component
	 * @see #instantiateFromTypemap
	 */
	protected synchronized ComponentFactory loadComponentFactory()
	{
		ComponentFactory factory =
			(ComponentFactory) instantiateFromTypemap(
					FACTORY_NAMESPACE,
					"ComponentFactory");
				
		if (factory == null)
		{
			factory = 
				(ComponentFactory) instantiateClass(
					"gov.nasa.gsfc.irc.components.DefaultComponentFactory");
		}
		
		return factory;
	}
	
	/**
	 * Loads the component specified by the "ComponentManager" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a ComponentManager component
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized ComponentManager loadComponentManager()
	{
		ComponentManager manager = (ComponentManager) instantiateFromTypemap
			(MANAGER_NAMESPACE, "ComponentManager");
		
		if (manager == null)
		{
			if ((fGlobalNamespace != null) && 
				(fGlobalNamespace instanceof ComponentManager))
			{
				manager = (ComponentManager) fGlobalNamespace;
			}
			else
			{
				manager = (ComponentManager) instantiateClass
					("gov.nasa.gsfc.irc.components.DefaultComponentNamespaceManager");
			}
		}
		
		return manager;
	}
	
	/**
	 * Loads and initializes the global root namespace
	 */
	protected Namespace loadGlobalNamespace()
	{
		Namespace namespace = fGlobalNamespace;
		
		if (namespace == null)
		{
			if (fComponentManager == null)
			{
				fComponentManager = loadComponentManager();
			}
			
			if ((fComponentManager != null) && 
				(fComponentManager instanceof Namespace))
			{
				namespace = (Namespace) fComponentManager;
			}
			else
			{
				namespace = (Namespace) instantiateClass
					("gov.nasa.gsfc.irc.components.DefaultComponentNamespaceManager");
			}
		
			String name = Irc.getPreference(IrcPrefKeys.INSTRUMENT_ID);
			
			if (name != null)
			{
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Loading Global Namespace as: " + name;
						
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"loadGlobalNamespace", message);
				}
				
				try
				{
					namespace.setName(name);
				}
				catch (PropertyVetoException e)
				{
					// Do nothing here
				}
			}
		}
		
		return (namespace);
	}

	/**
	 * Loads the component specified by the "ComponentManager" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a ComponentManager component
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized WorkspaceManager loadWorkspaceManager()
	{
		WorkspaceManager manager =
			(WorkspaceManager) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"WorkspaceManager");
				
		return manager;
	}
	
	/**
	 * Constructs an instance of a class using the 
	 * {@link Sys#instantiateClass(String)} method. Catches and logs any
	 * exceptions.
	 * 
	 * @param className the name of the class to construct an instance of
	 * @return an instance of the class
	 */
	protected Object instantiateClass(String className) 
	{
		Object result = null;
		
		try
		{
			result = Sys.instantiateClass(className);
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Loads an instance specified by the "MessageFactory" key in 
	 * the {@linkplain #FACTORY_NAMESPACE} of the global type map.
	 * 
	 * @return a MessageFactory instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized MessageFactory loadMessageFactory()
	{
		MessageFactory factory =
			(MessageFactory) instantiateFromTypemap(
				FACTORY_NAMESPACE,
				"MessageFactory");
				
		return factory;
	}
	
	/**
	 * Loads an instance specified by the "MessageValidator" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a MessageValidator instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized MessageValidator loadMessageValidator()
	{
		MessageValidator validator =
			(MessageValidator) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"MessageValidator");
				
		return validator;
	}
	
	/**
	 * Loads an instance specified by the "EventBus" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a EventBus instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized EventBus loadEventBus()
	{
		EventBus bus =
			(EventBus) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"EventBus");
				
		return bus;
	}
	
	/**
	 * Loads an instance specified by the "TaskManager" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a TaskManager instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized TaskManager loadTaskManager()
	{
		TaskManager manager =
			(TaskManager) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"TaskManager");
				
		return manager;
	}
	
	/**
	 * Loads an instance specified by the "ScriptFactory" key in 
	 * the {@linkplain #FACTORY_NAMESPACE} of the global type map.
	 * 
	 * @return a ScriptFactory instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized ScriptFactory loadScriptFactory()
	{
		ScriptFactory factory =
			(ScriptFactory) instantiateFromTypemap(
					FACTORY_NAMESPACE,
					"ScriptFactory");
				
		return factory;
	}
	
	/**
	 * Loads an instance specified by the "ScriptValidator" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a ScriptValidator instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized ScriptValidator loadScriptValidator()
	{
		ScriptValidator validator =
			(ScriptValidator) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"ScriptValidator");
				
		return validator;
	}
	
	/**
	 * Loads an instance specified by the "ScriptEvaluator" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map.
	 * 
	 * @return a ScriptEvaluator instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized ScriptEvaluator loadScriptEvaluator()
	{
		ScriptEvaluator evaluator =
			(ScriptEvaluator) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"ScriptEvaluator");
				
		return evaluator;
	}
	
	/**
	 * Loads an instance specified by the "GuiFactory" key in 
	 * the {@linkplain #FACTORY_NAMESPACE} of the global type map.
	 * 
	 * @return a GuiFactory instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized GuiFactory loadGuiFactory()
	{
		GuiFactory builder =
			(GuiFactory) instantiateFromTypemap(
					FACTORY_NAMESPACE,
					"GuiFactory");
				
		return builder;
	}
	
	/**
	 * Loads an instance specified by the "Default" key in 
	 * the {@linkplain #MANAGER_NAMESPACE} of the global type map. The
	 * name of the archived store to read in is specified by the 
	 * {@link IrcPrefKeys#PERSISTANCE_STORE_NAME PERSISTANCE_STORE_NAME} 
	 * preference key.
	 * 
	 * @return a PersistentObjectStore instance
	 * @see #instantiateFromTypemap
	 * @see DesciptorFramework#getGlobalMap()
	 */
	protected synchronized PersistentObjectStore loadPersistentStore()
	{
		PersistentObjectStore store =
			(PersistentObjectStore) instantiateFromTypemap(
					MANAGER_NAMESPACE,
					"PersistentStoreManager");
		String storeName = Irc.getPreference(IrcPrefKeys.PERSISTENCE_STORE_NAME);
		
		if (storeName != null)
		{
			store.setStoreName(storeName);
			store.readFromDisk();
		}
		
		return store;
	}
	
	/**
	 * Loads the instrument specified by the preference key
	 * {@link IrcPrefKeys#INSTRUMENT_DESCRIPTION INSTRUMENT_DESCRIPTION}.
	 */
	protected synchronized void loadInstruments()
	{
		String filename = Irc.getPreference(IrcPrefKeys.INSTRUMENT_DESCRIPTION);
		
		if(filename != null)
		{
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Loading instrument description:" + filename;
					
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"initializeInstruments", message);
			}

			URL url = Irc.getResourceManager().getResource(filename);
			addExternalDevice(url);
		}
	}
	
	/**
	 * Returns true if the application is running in an environment that 
	 * supports a GUI and the GUI is enabled.
	 * 
	 * @return Returns the graphicsEnabled.
	 */
	public boolean isGraphicsEnabled()
	{
		return fGraphicsEnabled;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultIrcManager.java,v $
//  Revision 1.72  2006/07/13 17:14:45  smaher_cvs
//  Added ability to have numerous (ordered) user type maps.
//  This is useful for "multi-layered" projects such as GISMO and GBT on
//  top of MarkIII, which in turn is on top of IRC.
//
//  Revision 1.71  2006/04/18 03:59:02  tames
//  Changed to support event bus.
//
//  Revision 1.70  2006/04/05 18:55:21  tames
//  Added methods to support EventBus.
//
//  Revision 1.69  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.68  2006/03/13 05:13:41  tames
//  Modified initializeGui method to use desktop description.
//
//  Revision 1.67  2006/03/08 14:46:57  tames
//  Changed the order of applications startup so that devices are initialized before
//  components.
//
//  Revision 1.66  2006/03/07 23:32:42  chostetter_cvs
//  NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
//  Revision 1.65  2006/02/14 20:06:42  tames
//  Modified log initialization so that handlers can be added without reconfiguring the LogManager which is restricted under WebStart.
//
//  Revision 1.64  2006/02/07 14:33:09  chostetter_cvs
//  Organized imports
//
//  Revision 1.63  2006/02/04 17:06:44  tames
//  Removed some old commented out code.
//
//  Revision 1.62  2006/01/27 20:55:16  tames
//  Added loadGlobalNamespace to initialize the name of the namespace to the
//  instrument id user preference value.
//
//  Revision 1.61  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.60  2006/01/13 03:17:52  tames
//  Support for Component set descriptions.
//
//  Revision 1.59  2005/12/02 19:05:44  tames_cvs
//  Moved the stopAllComponents() functionality of shutdown from the Irc
//  static class to the DefaultIrcManager so that applications can override
//  shutdown behavior if desired.
//
//  Revision 1.58  2005/12/01 20:56:05  tames_cvs
//  Merged ASF changes into IRC. Changes were with respect to user preferences.
//
//  Revision 1.57  2005/09/22 18:35:21  tames_cvs
//  Added an addExternalDevice(DeviceDescriptor) method.
//
//  Revision 1.56  2005/08/26 22:08:44  tames_cvs
//  Added methods to support a plug in BasisRequesterFactory.
//
//  Revision 1.55  2005/07/12 17:23:32  tames
//  Added support for default classes if no typemap file was read.
//
//  Revision 1.54  2005/05/23 19:59:53  tames_cvs
//  Fixed an error overriding the logging.properties file location.
//
//  Revision 1.53  2005/04/06 18:56:30  tames_cvs
//  Comment additions only.
//
//  Revision 1.52  2005/04/06 18:39:00  tames_cvs
//  Changed the order of application startup so that initializing the GUI
//  is one of the last steps.
//
//  Revision 1.51  2005/04/04 14:19:29  tames
//  Added initial support for workspaces and loading a component from an
//  XML description.
//
//  Revision 1.50  2005/02/09 21:29:22  tames_cvs
//  Startup Script capability implemented.
//
//  Revision 1.49  2005/02/02 06:08:40  tames
//  Changed loading of a device description to replace a previous load if
//  the devices are the same.
//
//  Revision 1.48  2005/02/01 16:49:43  tames
//  Updated to reflect changes in DescriptorLibrary
//
//  Revision 1.47  2005/01/19 15:58:57  tames_cvs
//  Added support for setting the title of the main frame via the
//  irc.gui.mainFrame.title preference.
//
//  Revision 1.46  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.45  2005/01/10 23:06:04  tames_cvs
//  Updated to reflect reorganized TypeMap namespaces and GuiBuilder changes.
//
//  Revision 1.44  2005/01/09 06:13:22  tames
//  Added support for a TaskManager.
//
//  Revision 1.43  2005/01/07 20:20:39  tames
//  Added support for a Script Factory class. Fixed bug so properties
//  given as a command line argument are now applied.
//
//  Revision 1.42  2004/12/14 20:42:51  tames
//  Added support for disabling the GUI via a preference.
//
//  Revision 1.41  2004/12/03 15:16:41  tames_cvs
//  Small change to preferences and the auto start of components
//  capability.
//
//  Revision 1.39  2004/12/02 23:12:02  tames_cvs
//  Components created as a result of reading in an IML file are now started
//  by default.
//
//  Revision 1.38  2004/10/14 21:16:05  tames_cvs
//  Relocated the method to instantiate a class from the global type map to
//  the DescriptorFramework class.
//
//  Revision 1.37  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.36  2004/10/07 21:38:27  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.35  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.34  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.33  2004/09/27 20:07:24  tames
//  Reflect changes to message router implementations and access methods.
//
//  Revision 1.32  2004/09/21 20:07:20  tames
//  Changed log configuration and log properties to utilize the
//  built in log configuration specified by the
//  java.util.logging.LogManager class.
//
//  Revision 1.31  2004/09/20 21:05:58  tames
//  Main frame is now packed before display.
//
//  Revision 1.30  2004/09/16 18:38:48  tames
//  Modified log initialization
//
//  Revision 1.29  2004/09/16 15:23:36  tames
//  Removed attempt to load a null preference URL
//
//  Revision 1.28  2004/09/09 19:26:56  tames
//  *** empty log message ***
//
//  Revision 1.27  2004/09/07 20:25:39  tames
//  javadoc additions only.
//
//  Revision 1.26  2004/09/07 19:57:44  tames
//  Added loadClass capability and instantiation from a singleton defined in
//  the typemap file.
//
//  Revision 1.25  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.24  2004/09/05 13:28:35  tames
//  Properties and preferences clean up
//
//  Revision 1.23  2004/09/04 13:26:07  tames
//  *** empty log message ***
//
//  Revision 1.22  2004/08/31 22:02:31  tames
//  Added some simple progress info on startup.
//
//  Revision 1.21  2004/08/26 14:32:01  tames
//  Added graphics enabled flag
//
//  Revision 1.20  2004/08/23 13:53:23  tames
//  Added conditional for enabling a command line interpreter
//
//  Revision 1.19  2004/08/11 05:42:57  tames
//  Script support
//
//  Revision 1.18  2004/08/09 17:25:58  tames_cvs
//  added getMessageValidator method
//
//  Revision 1.17  2004/08/06 14:17:53  tames_cvs
//  Message factory and router support
//
//  Revision 1.16  2004/08/04 21:14:25  tames_cvs
//  Changes to reflect message routing modifications within IRC
//
//  Revision 1.15  2004/08/03 20:24:00  tames_cvs
//  Changes to reflect instantiateClass exception handling changes
//
//  Revision 1.14  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.13  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.12  2004/07/10 08:21:26  tames_cvs
//  Modified logging to a file items
//
//  Revision 1.11  2004/07/10 00:14:43  tames_cvs
//  Changes to support log initialization and reading instrument descriptions.
//
//  Revision 1.10  2004/06/30 03:26:22  tames_cvs
//  Removed direct references to boot properties.
//
//  Revision 1.7  2004/06/08 20:52:33  chostetter_cvs
//  Added DataSpace, BasisBundleFactory access
//
//  Revision 1.6  2004/06/08 19:08:09  tames_cvs
//  Added initializeGui method
//
//  Revision 1.5  2004/06/08 14:14:21  tames_cvs
//  added getGuiBuilder method
//
//  Revision 1.4  2004/06/07 14:14:57  tames_cvs
//  More stubbed functionality implemented
//
//  Revision 1.3  2004/06/04 14:23:42  tames_cvs
//  more bootup and initialization implemented
//
//  Revision 1.2  2004/06/01 15:57:36  tames_cvs
//  Implemented more of the stubbed out code.
//
//  Revision 1.1  2004/05/28 22:04:06  tames_cvs
//  added IrcManager logic and bootup implementation
//
