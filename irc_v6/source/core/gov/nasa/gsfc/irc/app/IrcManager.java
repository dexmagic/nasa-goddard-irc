// === File Prolog ============================================================
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================
package gov.nasa.gsfc.irc.app;

import java.net.URL;

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
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.gui.GuiFactory;
import gov.nasa.gsfc.irc.messages.MessageFactory;
import gov.nasa.gsfc.irc.messages.MessageValidator;
import gov.nasa.gsfc.irc.scripts.ScriptEvaluator;
import gov.nasa.gsfc.irc.scripts.ScriptFactory;
import gov.nasa.gsfc.irc.scripts.ScriptValidator;
import gov.nasa.gsfc.irc.workspace.WorkspaceManager;

/**
 * This interface defines the necessary methods for the a configuration manager.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 03:59:02 $
 * @author Troy Ames
 */
public interface IrcManager
{
	/**
	 * Initialize the application based on the specified arguments.
	 * 
	 * @param argv an array of arguments
	 */
	public void initializeApp(String argv[]);

	/**
	 * Shuts down the application and releases all application resources.
	 * 
	 * @return
	 */
	public int shutdown();

	/**
	 * Add the device specified by the given descriptor.
	 * 
	 * @param descriptor a device descriptor.
	 */
	public void addExternalDevice(DeviceDescriptor descriptor);

	/**
	 * Add a new instrument from the description located at the specified URL.
	 * 
	 * @param url URL of device's IML description
	 */
	public void addExternalDevice(URL url);

	/**
	 * Returns the DataSpace to be used by the current instance of IRC.
	 * 
	 * @return The DataSpace to be used by the current instance of IRC
	 */
	public DataSpace getDataSpace();

	/**
	 * Returns the BasisBundleFactory to be used by the current instance of IRC.
	 * 
	 * @return The BasisBundleFactory to be used by the current instance of IRC
	 */
	public BasisBundleFactory getBasisBundleFactory();

	/**
	 * Returns the BasisRequesterFactory to be used by the current instance of
	 * IRC.
	 * 
	 * @return The BasisRequesterFactory to be used by the current instance of
	 *         IRC
	 */
	public BasisRequesterFactory getBasisRequesterFactory();

	/**
	 * Returns the ComponentFactory to be used by the current instance of IRC.
	 * 
	 * @return The ComponentFactory to be used by the current instance of IRC
	 */
	public ComponentFactory getComponentFactory();

	/**
	 * Returns the ComponentManager to be used by the current instance of IRC.
	 * 
	 * @return The ComponentManager to be used by the current instance of IRC
	 */
	public ComponentManager getComponentManager();

	/**
	 * Returns the global Namespace to be used by the current instance of IRC.
	 * 
	 * @return The global Namespace to be used by the current instance of IRC
	 */
	public Namespace getGlobalNamespace();

	/**
	 * Returns the WorkspaceManager to be used by the current instance of IRC.
	 * 
	 * @return The WorkspaceManager to be used by the current instance of IRC
	 */
	public WorkspaceManager getWorkspaceManager();

	/**
	 * Returns the DescriptorFramework to be used by the current instance of
	 * IRC.
	 * 
	 * @return The DescriptorFramework to be used by the current instance of IRC
	 */
	public DescriptorFramework getDescriptorFramework();

	/**
	 * Returns the DescriptorLibrary to be used by the current instance of IRC.
	 * 
	 * @return The DescriptorLibrary to be used by the current instance of IRC
	 */
	public DescriptorLibrary getDescriptorLibrary();

	/**
	 * Returns the GUI Builder to be used by the current instance of IRC.
	 * 
	 * @return The GuiFactory to be used by the current instance of IRC
	 */
	public GuiFactory getGuiFactory();

	/**
	 * Returns the message factory to be used by the current instance of IRC.
	 * 
	 * @return The MessageFactory to be used by the current instance of IRC
	 */
	public MessageFactory getMessageFactory();

	/**
	 * Returns the message validator to be used by the current instance of IRC.
	 * 
	 * @return The MessageValidator to be used by the current instance of IRC
	 */
	public MessageValidator getMessageValidator();

	/**
	 * Returns the EventBus to be used by the current instance of IRC.
	 * 
	 * @return The EventBus to be used by the current instance of IRC
	 */
	public EventBus getEventBus();

	/**
	 * Returns the task manager to be used by the current instance of IRC.
	 * 
	 * @return The TaskManager to be used by the current instance of IRC
	 */
	public TaskManager getTaskManager();

	/**
	 * Returns the Script factory to be used by the current instance of IRC.
	 * 
	 * @return The ScriptFactory to be used by the current instance of IRC
	 */
	public ScriptFactory getScriptFactory();

	/**
	 * Returns the script evaluator to be used by the current instance of IRC.
	 * 
	 * @return The ScriptEvaluator to be used by the current instance of IRC
	 */
	public ScriptEvaluator getScriptEvaluator();

	/**
	 * Returns the script validator to be used by the current instance of IRC.
	 * 
	 * @return The ScriptValidator to be used by the current instance of IRC
	 */
	public ScriptValidator getScriptValidator();

	/**
	 * Returns the PersistentObjectStore to be used by the current instance of
	 * IRC.
	 * 
	 * @return The PersistentObjectStore to be used by the current instance of
	 *         IRC
	 */
	public PersistentObjectStore getPersistentStore();
}

// --- Development History ---------------------------------------------------
//
// $Log: IrcManager.java,v $
// Revision 1.22  2006/04/18 03:59:02  tames
// Changed to support event bus.
//
// Revision 1.21  2006/04/05 18:55:21  tames
// Added methods to support EventBus.
//
// Revision 1.20 2006/03/07 23:32:42 chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the
// global Namespace by default
//
// Revision 1.19 2005/09/22 18:35:21 tames_cvs
// Added an addExternalDevice(DeviceDescriptor) method.
//
// Revision 1.18 2005/08/26 22:08:44 tames_cvs
// Added methods to support a plug in BasisRequesterFactory.
//
// Revision 1.17 2005/04/04 14:20:05 tames
// Added initial support for workspaces.
//
// Revision 1.16 2005/01/10 23:06:04 tames_cvs
// Updated to reflect reorganized TypeMap namespaces and GuiBuilder changes.
//
// Revision 1.15 2005/01/09 06:13:22 tames
// Added support for a TaskManager.
//
// Revision 1.14 2005/01/07 20:23:49 tames
// Added support for getting the Script Factory class.
//
// Revision 1.13 2004/10/14 15:16:51 chostetter_cvs
// Extensive descriptor-oriented refactoring
//
// Revision 1.12 2004/09/27 20:07:24 tames
// Reflect changes to message router implementations and access methods.
//
// Revision 1.11 2004/08/11 05:42:57 tames
// Script support
//
// Revision 1.10 2004/08/10 13:33:14 tames
// Added message related methods.
//
// Revision 1.9 2004/08/09 17:25:58 tames_cvs
// added getMessageValidator method
//
// Revision 1.8 2004/08/06 14:17:53 tames_cvs
// Message factory and router support
//
// Revision 1.7 2004/07/12 14:26:23 chostetter_cvs
// Reformatted for tabs
//
// Revision 1.6 2004/07/10 00:14:43 tames_cvs
// Changes to support log initialization and reading instrument descriptions.
//
// Revision 1.5 2004/06/08 20:52:33 chostetter_cvs
// Added DataSpace, BasisBundleFactory access
//
// Revision 1.4 2004/06/08 14:14:03 tames_cvs
// added getGuiBuilder method
//
// Revision 1.3 2004/06/04 14:24:23 tames_cvs
// Added getDescriptorLibrary to interface
//
// Revision 1.2 2004/06/01 15:58:31 tames_cvs
// Added getDescriptorFramework method
//
// Revision 1.1 2004/05/28 22:04:06 tames_cvs
// added IrcManager logic and bootup implementation
//
