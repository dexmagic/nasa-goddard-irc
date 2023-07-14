//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.description.xml.LookupTable;
import gov.nasa.gsfc.irc.devices.DeviceChangeListener;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.description.ScriptInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.ports.PortDescriptor;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptGroupDescriptor;

/**
 * The DescriptorLibrary is the central object which knows about all
 * of the Descriptors describing "top-level" objects in the system.
 * It is not aware of some of the organization of the various descriptor
 * types - i.e., it makes no attempt to provide a unified view of all
 * descriptor types (e.g., it is aware of the
 * hierarchical arrangement of devices and subsystems.  The 
 * DescriptorLibrary will discover all scripts and messages defined in an
 * added device or script group. The DescriptorLibrary can be used to get
 * the descriptor of a device, message, or script given a unique key. 
 * The unique key is specified with a Dot "." notation of the hierarchy to
 * the specific descriptor. The form of the Dot notation for a message is
 * <Device>.<Port>.<MessageInterface>.<Message> with each element replaced
 * with the name specified in the XML description.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/04/18 14:02:49 $
 * @author		Steve Clark
 * @author	 	John Higinbotham	
 * @author	Troy Ames
**/
public class DescriptorLibrary 
	implements DescriptorEventSource
{
	private static final String CLASS_NAME = DescriptorLibrary.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Map of all devices
	private Map fDeviceMap = new HashMap();
	
	// Collection of top level devices perserving order devices were
	// added.
	private List fDeviceList = new LinkedList();

	// Map of all scripts 
	private Map fScriptMap = new HashMap();

	// Map of all messages 
	private Map fMessageMap = new HashMap();
	
	// Map of script groups 
	private Map fScriptGroupMap = new HashMap();
	
	// Collection of top level script groups perserving the order groups
	// were added.
	private List fScriptGroupList = new LinkedList();

	// Map of lookup tables
	private Map fLookupTableMap = new HashMap();
	private List fLookupTableList = new LinkedList();

	// Device change listeners (to support jxta)
	private List fDeviceChangeListeners = new LinkedList();

	// Collection of library update listeners
	private List fLibraryUpdateListeners = new LinkedList(); 

	// Descriptor event listener
	DescriptorEventListener fDescriptorEventListener = null;

	//---Constants
	private final static String INSTRUMENT			= "instrument";
	private final static String SCRIPT_GROUP  		= "script group";
//	private final static String SCRIPT				= "script";
	private final static String LOOKUP_TABLE		= "lookup table";

	private final static int INSTRUMENT_UPDATE			= 1;
	private final static int SCRIPT_GROUP_UPDATE 		= 3;
	private final static int SCRIPT_UPDATE	   			= 4;
	private final static int LIBRARY_UPDATE				= 5;


//-----------------------------------------------------------------------------------------

	/**
	 * Add a new top-level device to the library.  Additionally
	 * all the scripts and messages defined within this device and
	 * its subsystems are added to the library. 
	 *
	 * @param device	Device to add
	 * @throws DuplicateDescriptorNameException
	**/
	public synchronized void addDevice(DeviceDescriptor device)
		throws DuplicateDescriptorNameException
	{
		if (fDeviceList.contains(device))
		{
			throw new DuplicateDescriptorNameException(device.getName(), INSTRUMENT);
		}

		fDeviceList.add(device);
		addDeviceToLibrary(null, device);
		fireLibraryUpdateEvent(INSTRUMENT_UPDATE);
	}

	/**
	 * Add device to library. This recursive method will add all subdevices, 
	 * scripts, and messages contained in the given device.
	 *
	 * @param parentKey key to generate sub keys from.
	 * @param device	Device to add
	**/
	private void addDeviceToLibrary(String parentKey, DeviceDescriptor device) 
		throws DuplicateDescriptorNameException
	{
		String key;
		
		if (parentKey != null)
		{
			//key = parentKey + device.getName();
			key = device.getName() + parentKey;
		}
		else 
		{
			key = device.getName();
		}

		fDeviceMap.put(key, device);
		
		addScriptsForDevice(key, device);
		addMessagesForDevice(key, device);
		
		for (Iterator devices = device.getSubdevices(); devices.hasNext();)
		{
			addDeviceToLibrary(key, (DeviceDescriptor) devices.next());
		}
	}

	/**
	 * Replace an existing top-level device (identified by name)
	 * in the library with the given device. If no device with the specified name
	 * has yet been defined, this call is equivalent to
	 * <code>addDevice</code>. 
	 *
	 * @param device	device to replace
	**/
	public synchronized void replaceDevice(String name, DeviceDescriptor device)
	{
		DeviceDescriptor existingDevice = (DeviceDescriptor) fDeviceMap.get(name);
		
		// Remove device if it exists
		if (existingDevice != null)
		{
			removeDevice(existingDevice);
		}

		// Add new device
		try
		{
			addDevice(device);
		}
		catch (DuplicateDescriptorNameException e)
		{
			// This should not happen since we are removing it first
			e.printStackTrace();
		}
	}

	/**
	 * Remove device in library. 
	 *
	 * @param device	Device to replace
	**/
	private void removeDeviceInLibrary(String parentKey, DeviceDescriptor device) 
	{
		String key;
		
		if (parentKey != null)
		{
			key = parentKey + device.getName();
		}
		else 
		{
			key = device.getName();
		}

		fDeviceMap.remove(key);
		key = key + Path.DOT;
		
		// Since all associated descriptors for this device should start with
		// the same key we can remove them without traversing the descriptor
		// tree.
		
		// Remove child DeviceDescriptors
		for (Iterator devices = fDeviceMap.keySet().iterator(); devices.hasNext();)
		{
			String currentKey = (String) devices.next();
			
			if (currentKey.startsWith(key))
			{
				devices.remove();
			}
		}

		// Remove MessageDescriptors
		for (Iterator messages = fMessageMap.keySet().iterator(); messages.hasNext();)
		{
			String currentKey = (String) messages.next();
			
			if (currentKey.startsWith(key))
			{
				messages.remove();
			}
		}

		// Remove ScriptDescriptors
		for (Iterator scripts = fScriptMap.keySet().iterator(); scripts.hasNext();)
		{
			String currentKey = (String) scripts.next();
			
			if (currentKey.startsWith(key))
			{
				scripts.remove();
			}
		}
	}

	/**
	 * Remove a top-level device from the library.
	 *
	 * @param device	Device to remove
	**/
	public synchronized void removeDevice(DeviceDescriptor device)
	{
		if (fDeviceList.contains(device))
		{
			fDeviceList.remove(device);
			removeDeviceInLibrary(null, device);
			fireLibraryUpdateEvent(INSTRUMENT_UPDATE);
		}
	}

	/**
	 * Add a new script group to the library.
	 *
	 * @param group	Script script group to add
	**/
	public synchronized void addScriptGroup(ScriptGroupDescriptor group)
		throws DuplicateDescriptorNameException
	{
		String name = group.getName();

		if (fScriptGroupMap.get(name) != null)
		{
			throw new DuplicateDescriptorNameException(name, SCRIPT_GROUP);
		}
		
		fScriptGroupMap.put(name, group);
		addScriptsToLibrary(group.getName(), group);
	}

	/**
	 * Replace an existing script group in the library.  If the
	 * script group has not yet been defined, this call is equivalent to
	 * addScriptGroup().
	 *
	 * @param group Script script group to replace
	**/
	public synchronized void replaceScriptGroup(ScriptGroupDescriptor group)
		throws DuplicateDescriptorNameException
	{
		String name = group.getName();
		Object orig = fScriptGroupMap.get(name);

		// Replace in list
		replace(fScriptGroupList, orig, group);

		// Replace in map 
		fScriptGroupMap.put(name, group);
		addScriptsToLibrary(name, group);
	}

	/**
	 * Remove a script group from the library.
	 *
	 * @param group Script script group to remove
	**/
	public synchronized void removeScriptGroup(ScriptGroupDescriptor group)
	{
		fScriptGroupMap.remove(group.getName());
		fScriptGroupList.remove(group);

		//updateScriptMapOnRemove();
	}


	/**
	 * Add a new lookup table to the library.
	 *
	 * @param lookupTable 	Table to add
	**/
	public synchronized void addLookupTable(LookupTable lookupTable)
		throws DuplicateDescriptorNameException
	{
		String name = lookupTable.getName();

		if (fLookupTableMap.get(name) != null)
		{
			throw new DuplicateDescriptorNameException(name, LOOKUP_TABLE);
		}
		
		fLookupTableMap.put(name, lookupTable);
		fLookupTableList.add(lookupTable);
	}

	/**
	 * Replace a existing lookup table in the library. If no lookup 
	 * table with the specified name has been defined, this call is 
	 * equivalent to addLookupTable();
	 *
	 * @param lookupTable	Lookup table to replace
	**/
	public synchronized void replaceLookupTable(LookupTable lookupTable)
	{
		String name = lookupTable.getName();
		Object orig = fLookupTableMap.get(name);

		// Replace in list
		replace(fLookupTableList, orig, lookupTable);

		// Replace in map
		fLookupTableMap.put(name, lookupTable);
	}

	/**
	 * Remove a lookup table from the library.
	 *
	 * @param lookupTable	Lookup table to remove
	**/
	public synchronized void removeLookupTable(LookupTable lookupTable)
	{
		fLookupTableMap.remove(lookupTable.getName());
		fLookupTableList.remove(lookupTable);
	}

	/**
	 * Retrieve all of the root-level DeviceDescriptors in the order they were
	 * added.
	 *
	 * @return Iterator over all DeviceDescriptors
	 */
	public Iterator getDevices()
	{
		return fDeviceList.iterator();
	}

	/**
	 * Find the message descriptor with the given name. 
	 * If the key does not exist null is returned.
	 *
	 * @param  key Name. 
	 * @return MessageDescriptor with the specified key.
	 *
	**/
	public MessageDescriptor getMessageDescriptor(String key)
	{
		return (MessageDescriptor) fMessageMap.get(key);
	}

	/**
	 * Retrieve all of the defined ScriptGroupDescriptors, in the
	 * order in which they were defined.
	 *
	 * @return Iterator over all ScriptGroupDescriptors
	 */
	public Iterator getScriptGroups()
	{
		return fScriptGroupList.iterator();
	}

	/**
	 * Find the script descriptor with the given name. 
	 * If the key does not exist null is returned.
	 *
	 * @param  key Name. 
	 * @return script descriptor with the specified key.
	 *
	**/
	public ScriptDescriptor getScriptDescriptor(String key)
	{
		return (ScriptDescriptor) fScriptMap.get(key);
	}

	/**
	 * This recursive method is used to add all the scripts contained in 
	 * the specified group, as well as those found in the contained sub groups. 
	 *
	 * @param group ScriptGroupDescriptor to add.
	 *
	 * @returns Success flag (true = no duplicates, false = duplicates)
	**/
	private boolean addScriptsToLibrary(String parentKey, ScriptGroupDescriptor group)
	{
		boolean rval = true;
		// Add the scripts contained in this group
		Iterator i = group.getScripts();
		
		while (i.hasNext())
		{
			ScriptDescriptor cp = (ScriptDescriptor) i.next();
			//String key = parentKey + Path.DOT + cp.getName();
			String key = cp.getName() + Path.DOT + parentKey;

			if (fScriptMap.containsKey(key))
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Duplicate named script NOT added to " +
						"map in Descriptor Library\n" + 
						"Script Group Name = " + group.getName() + "\n" +
						"Script Name = " + cp.getName();
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"addScripts", message);
				}

				rval = false;
			}
			else
			{
				fScriptMap.put(key, cp);
			}
		}

		// Add the scripts contained in sub groups	
		for (Iterator subGroups = group.getSubGroups(); subGroups.hasNext();)
		{
			ScriptGroupDescriptor sgd = (ScriptGroupDescriptor) subGroups.next();
			String key = sgd.getName() + Path.DOT + parentKey;
			rval = rval && addScriptsToLibrary(key, sgd);
		}
		return rval;
	}


	/**
	 * Retrieve all the lookup tables.
	 *
	 * @return Iterator over all lookup tables. 
	**/
	public Iterator getLookupTables()
	{
		return fLookupTableList.iterator();
	}


	/**
	 * Get the DeviceDescriptor for the given name.
	 *
	 * @param name	Name of device to find
	 */
	public DeviceDescriptor getDeviceDescriptor(String name)
	{
		return (DeviceDescriptor) fDeviceMap.get(name);
	}


	/**
	 * This method is used to add all the scripts contained in 
	 * the specified device. 
	 *
	 * @param parentKey the key to derive sub keys from.
	 * @param device DeviceDescriptor containing scripts.
	**/
	private boolean addScriptsForDevice(String parentKey, DeviceDescriptor device)
	{
		boolean rval = true;

		// Add the scripts contained in this instrument 
		for (Iterator i = device.getScriptInterfaces(); i.hasNext();)
		{
			//Need to look at each interface and process its scripts
			ScriptInterfaceDescriptor si = (ScriptInterfaceDescriptor) i.next();
			//String key = parentKey + Path.DOT + si.getName();
			String key = si.getName() + Path.DOT + parentKey;

			for (Iterator j = si.getScripts(); j.hasNext();)
			{
				ScriptDescriptor sd = (ScriptDescriptor) j.next();
				String scriptKey = sd.getName() + Path.DOT + key;
				
				if (fScriptMap.containsKey(scriptKey))
				{
					if (sLogger.isLoggable(Level.WARNING))
					{
						String message = "Duplicate named script NOT added to " +
							"map in Descriptor Library\n" + 
							"Containing Device Name = " + device.getName() + "\n" +
							"Script Name = " + sd.getName();
						
						sLogger.logp(Level.WARNING, CLASS_NAME, 
							"addScripts", message);
					}

					rval = false;
				}
				else
				{
					fScriptMap.put(scriptKey, sd);
				}
			}
		}

		return rval;
	}

	private boolean addMessagesForMessageInterface(String parentKey,
			MessageInterfaceDescriptor messageInterface)
	{
		boolean rval = true;		
		//String key = parentKey + Path.DOT + messageInterface.getName();
		String key = messageInterface.getName() + Path.DOT + parentKey;

		for (Iterator j = messageInterface.getMessages(); j.hasNext();)
		{
			MessageDescriptor md = (MessageDescriptor) j.next();
			//String messageKey = key + Path.DOT + md.getName();
			String messageKey = md.getName() + Path.DOT + key;
			//System.out.println("+Adding message:" + messageKey);
			
			if (fMessageMap.containsKey(messageKey))
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Duplicate named message NOT added to "
							+ "map in Descriptor Library\n"
							+ "Containing key = "
							+ key
							+ "\n"
							+ "Message Name = " + md.getName();

					sLogger.logp(Level.WARNING, CLASS_NAME,
						"addMessagesForDevice", message);
				}

				rval = false;
			}
			else
			{
				fMessageMap.put(messageKey, md);
			}
		}
		
		for (Iterator i = messageInterface.getMessageInterfaces(); i.hasNext();)
		{
			//Need to look at each interface and process its messages
			MessageInterfaceDescriptor mi = 
				(MessageInterfaceDescriptor) i.next();

			addMessagesForMessageInterface(key, mi);
		}
		
		return rval;
	}
	
	/**
	 * This method is used to add all the messages contained in 
	 * the specified device. 
	 *
	 * @param parentKey the key to derive sub keys from.
	 * @param device DeviceDescriptor containing scripts.
	 * @returns Success flag (true = no duplicates, false = duplicates)
	**/
	private boolean addMessagesForDevice(String parentKey,
			DeviceDescriptor device)
	{
		boolean rval = true;

		// Add messages defined at the device level
		for (Iterator i = device.getMessageInterfaces(); i.hasNext();)
		{
			//Need to look at each interface and process its messages
			MessageInterfaceDescriptor mi = 
				(MessageInterfaceDescriptor) i.next();

			addMessagesForMessageInterface(parentKey, mi);
		}

		// Add the messages contained in this device
		for (Iterator ports = device.getPorts(); ports.hasNext();)
		{
			PortDescriptor pd = (PortDescriptor) ports.next();
			//String portKey = parentKey + Path.DOT + pd.getName();
			String portKey = pd.getName() + Path.DOT + parentKey;

			for (Iterator i = pd.getMessageInterfaces(); i.hasNext();)
			{
				//Need to look at each interface and process its scripts
				MessageInterfaceDescriptor mi = 
					(MessageInterfaceDescriptor) i.next();

				addMessagesForMessageInterface(portKey, mi);
			}
		}

		return rval;
	}

	/**
	 * Locate the named script group.
	 *
	 * @param name	Name of script group to find
	**/
	public ScriptGroupDescriptor getScriptGroupDescriptor(String name)
	{
		return (ScriptGroupDescriptor) fScriptGroupMap.get(name);
	}

	/**
	 * Locate the named lookup table.
	 *
	 * @param name	Name of lookup table to find
	**/
	public LookupTable getLookupTable(String name)
	{
		return (LookupTable) fLookupTableMap.get(name);
	}

	/**
	 * Replace the specified unnamed object with an alternate unnamed
	 * object in the list. 
	 *
	 * @param list 			List in which the replacement is to occur.
	 * @param original		Original object (already in list). 
	 * @param replacement	Replacement object. 
	**/
	private void replace(List list, Object original, Object replacement)
	{
		ListIterator iter = list.listIterator();

		while (iter.hasNext())
		{
			if (iter.next() == original)
			{
				iter.set(replacement);
				return;
			}
		}
		list.add(replacement);
	}

	/**
	 * Notify registered listeners about library updates. 
	 *
	 * @param type Type of update that occured 
	**/
	protected void fireLibraryUpdateEvent(int type)
	{
		Iterator i = fLibraryUpdateListeners.iterator();
		DescriptorLibraryUpdateListener listener;

		switch (type)
		{
			case INSTRUMENT_UPDATE:
				while (i.hasNext())
				{
					listener = (DescriptorLibraryUpdateListener) i.next();
					listener.handleInstrumentUpdate();
					listener.handleLibraryUpdate();
				}
				break;
			case SCRIPT_GROUP_UPDATE:
				while (i.hasNext())
				{
					listener = (DescriptorLibraryUpdateListener) i.next();
					listener.handleScriptGroupUpdate();
					listener.handleLibraryUpdate();
				}
				break;
			case SCRIPT_UPDATE:
				while (i.hasNext())
				{
					listener = (DescriptorLibraryUpdateListener) i.next();
					listener.handleScriptUpdate();
					listener.handleLibraryUpdate();
				}
				break;
			case LIBRARY_UPDATE:
				while (i.hasNext())
				{
					listener = (DescriptorLibraryUpdateListener) i.next();
					listener.handleLibraryUpdate();
				}
				break;
			default:
				System.out.println("Descriptor Library Internal Error!");
				break;
		}
	}

	/**
	 * Notify all the DeviceChangeListeners that a change has occured.
	 *
	**/
	protected void fireDeviceChangeEvent()
	{
		DeviceChangeListener dcl = null;
		for (int i = 0; i<fDeviceChangeListeners.size(); i++)
		{
			dcl = (DeviceChangeListener) fDeviceChangeListeners.get(i);	
			dcl.handleDeviceChange();
		}
	}

	/** 
	 * Send the DescriptorEvent to the registered listener
	**/
	protected void fireDescriptorEvent()
	{
		if(fDescriptorEventListener != null)
		{
			fDescriptorEventListener.reloadDescriptorLibrary();
		}
		else
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "No listeners registered to receive DescriptorEvents";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"broadcastLibraryUpdates", message);
			}
		}
	}

	//------ Event registration methods ----------------------------------------
	
	/**
	 * Register a listener interested in DescriptorLibrary updates. 
	 *
	 * @param listener DescriptorLibraryUpdateListener to register
	**/
	public void addLibraryUpdateListener(DescriptorLibraryUpdateListener listener)
	{
		fLibraryUpdateListeners.add(listener);
	}

	/**
	 * Unregister a listener previously registered for DescriptorLibrary updates.
	 *
	 * @param listener DescriptorLibraryUpdateListener to unregister 
	**/
	public void removeLibraryUpdateListener(DescriptorLibraryUpdateListener listener)
	{
		fLibraryUpdateListeners.remove(listener);
	}

	/**
	 *  Adds the given DescriptorEventListener to this library, so
	 *  that it will receive the DescriptorEvents produced.
	 *
	 *  @param listener 	A DescriptorEventListener
	**/
	public void addDescriptorEventListener(DescriptorEventListener listener)
	{
		fDescriptorEventListener = listener;
	}

	/**
	 *  Removes the given DescriptorEventListener from this library, so
	 *  that it will no longer receive the DescriptorEvents produced.
	 *
	 *  @param listener 	A DescriptorEventListener
	**/
	public void removeDescriptorEventListener(DescriptorEventListener listener)
	{
		if (fDescriptorEventListener == listener)
		{
			fDescriptorEventListener = null;
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorLibrary.java,v $
//  Revision 1.27  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.26  2006/04/18 04:02:15  tames
//  Reversed the order of a fully qualified message name.
//
//  Revision 1.25  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.24  2005/07/14 19:47:53  tames_cvs
//  Fixed bug in adding Messages for a device.
//
//  Revision 1.23  2005/07/12 14:57:55  tames_cvs
//  Added support for nested MessageInterfaces in an XML description.
//
//  Revision 1.22  2005/07/12 14:45:08  tames_cvs
//  Added addMessagesForMessageInteface method.
//
//  Revision 1.21  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.20  2005/02/03 06:59:48  tames
//  Updated to reflect changes in DeviceDescriptor.
//
//  Revision 1.19  2005/02/02 06:11:35  tames
//  Completed redesign and rewrite of class.
//
//  Revision 1.18  2005/02/01 18:54:10  tames
//  Renamed "find" methods to "get" to match bean specifications.
//
//  Revision 1.17  2005/02/01 16:40:26  tames
//  Partial completion of a refactoring of this class to make it more efficient
//  and reflect the schema. Older version made assumtions that where
//  not always valid.
//
//  Revision 1.16  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.15  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.14  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.13  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.12  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.11  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.10  2004/09/04 13:27:02  tames
//  *** empty log message ***
//
//  Revision 1.9  2004/08/12 03:16:15  tames
//  Scripting support
//
//  Revision 1.8  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.7  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.6  2004/06/30 20:42:13  tames_cvs
//  Changed references of command procedures to scripts
//
//  Revision 1.5  2004/06/04 14:21:42  tames_cvs
//  No longer a singleton
//