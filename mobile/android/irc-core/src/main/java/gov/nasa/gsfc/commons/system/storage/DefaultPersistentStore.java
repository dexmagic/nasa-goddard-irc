//=== File Prolog ============================================================
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

package gov.nasa.gsfc.commons.system.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class provides a persistent store for Serializable Objects. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/04/06 14:59:46 $
 * @author S. Clark
 * @author Troy Ames
**/
public class DefaultPersistentStore implements PersistentObjectStore
{
	private static final String CLASS_NAME = 
		DefaultPersistentStore.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private Map fObjects = null;
	private String fFileName = "PersistentObjectStore";

	//  Listeners for Object store changes
	private HashSet fListeners = new HashSet();
	

	/**
	 * Constructs a new PersistentObjectStore. 
	 */
	public DefaultPersistentStore()
	{
		fObjects = new TreeMap();
	}

	/**
	 * Constructs a new PersistentObjectStore with the specified name. The 
	 * file will be read in if it exists.
	 * The serialized form of the PersistentObjectStore will be placed in
	 * <storeName>.
	 *
	 * @param storeName
	 */
	public DefaultPersistentStore(String storeName)
	{
		fFileName = storeName;

		// Read object store from disk, or create a new one if none
		// found.
		if (!readFromDisk())
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Creating a new persistent store " + storeName;
					
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"DefaultPersistentStore", message);
			}
			
			fObjects = new TreeMap();
		}
	}
	
	/**
	 * Store a Serializable in the store with the specified key.
	 *
	 * @param key		The key under which to store the Serializable
	 * @param object	The Serializable to store
	 */
	public synchronized void store(String key, Serializable object)
	{
		if (object != null)
		{
			//  Add it to the store and fire the proper event depending on
			//  if the object is new or not.
			if (fObjects.put(key, object) == null)
			{
				fireEntryAdded(object);
			}
			else
			{
				fireEntryChanged(object);
			}
		}
		else
		{
			// TBD
		}
	}

	/**
	 * Retrieve a Serializable from the store.
	 *
	 * @param key	The key of the Serializable to be retrieved
	 * @return The Serializable stored under the specified key, or null
	 */
	public synchronized Serializable retrieve(String key)
	{
		Serializable result = (Serializable) fObjects.get(key);

		return (result);
	}

	/**
	 * Remove a Serializable from the store.
	 *
	 * @param key	The key to be removed
	 */
	public synchronized void delete(String key)
	{
		Serializable removedObject = (Serializable) fObjects.remove(key);
		if (removedObject != null)
		{
			fireEntryRemoved(removedObject);
		}
	}

	/**
	 * Retrieve all of the keys currently in the store.
	 *
	 * @return Iterator	over all keys
	 */
	public synchronized Iterator getAllKeys()
	{
		return fObjects.keySet().iterator();
	}

	/**
	 * Write the current contents of the store to a file.
	 *
	 * @return True if the store was successfully written; false otherwise.
	 */
	public synchronized boolean saveToDisk()
	{
		boolean result = true;
		
		try
		{
			FileOutputStream fileStream = new FileOutputStream(fFileName);
			ObjectOutputStream outStream = new ObjectOutputStream(fileStream);

			outStream.writeObject(fObjects);
			outStream.close();
		}
		catch (Exception e)
		{
			String message = "Exception saving persistent store " 
				+ fFileName + " to disk";
			
			sLogger.logp(Level.WARNING, CLASS_NAME, "saveToDisk", message, e);
	
			result = false;
		}
		
		return result;
	}

	/**
	 * Read in the contents of the store from a file.
	 *
	 * @return True if the store was successfully read; false otherwise.
	 */
	public synchronized boolean readFromDisk()
	{
		boolean result = true;

		try
		{
			FileInputStream fileStream = new FileInputStream(fFileName);
			ObjectInputStream inStream = new ObjectInputStream(fileStream);
			fObjects = (Map) inStream.readObject();
			inStream.close();
		}
		catch (Exception ex)
		{
			String message = "Exception reading persistent store from disk - " 
				+ ex.getLocalizedMessage();
			
			sLogger.logp(Level.WARNING, CLASS_NAME, "readFromDisk", message);
			result = false;
		}
		
		return result;
	}

	/**
	 * Test dump for debugging purposes.
	 */
	public synchronized void dumpValues()
	{
		Iterator iter = fObjects.values().iterator();
		while (iter.hasNext())
		{
			Serializable obj = (Serializable) iter.next();
			System.out.println("=====================");
			System.out.println(obj);
		}
	}

	/**
	 * Add a listener for changes to the data store.<p>
	 * Note that because of the synchronized nature of the methods of this 
	 * class listeners should be careful what they do in their response to 
	 * events and always return quickly as this event occurs in the calling
	 * thread.
	 *
	 * @param l  the listener to add
	 */
	public void addPersistentObjectStoreListener(PersistentObjectStoreListener l)
	{
		fListeners.add(l);		
	}

	/**
	 * Remove a listener for changes that is listening for changed to the 
	 * data store.
	 *  
	 * @param l  the listener to remove
	 */
	public void removePersistentObjectStoreListener(PersistentObjectStoreListener l)
	{
		fListeners.remove(l);		
	}

	/**
	 * Inform all listeners that an entry was added to the data store.
	 *
	 * @param entry  the data object that was added
	 */
	protected void fireEntryAdded(Serializable entry)
	{
		Object[] listeners = fListeners.toArray();
		PersistentObjectStoreEvent dataStoreEvent = new PersistentObjectStoreEvent(this, entry);
		
		for (int i = 0;  i < listeners.length; i++)
		{
			((PersistentObjectStoreListener) listeners[i]).objectAdded(dataStoreEvent);
		}
	}

	/**
	 * Inform all listeners that an entry was removed from the data store.
	 *
	 * @param entry  the data object that was removed
	 */
	protected void fireEntryRemoved(Serializable entry)
	{
		Object[] listeners = fListeners.toArray();
		PersistentObjectStoreEvent dataStoreEvent = new PersistentObjectStoreEvent(this, entry);
		
		for (int i = 0;  i < listeners.length; i++)
		{
			((PersistentObjectStoreListener) listeners[i]).objectRemoved(dataStoreEvent);
		}
	}

	/**
	 * Inform all listeners that an entry was removed from the data store.
	 *
	 * @param entry  the data object that was removed
	 */
	protected void fireEntryChanged(Serializable entry)
	{
		Object[] listeners = fListeners.toArray();
		PersistentObjectStoreEvent dataStoreEvent = new PersistentObjectStoreEvent(this, entry);
		
		for (int i = 0;  i < listeners.length; i++)
		{
			((PersistentObjectStoreListener) listeners[i]).objectChanged(dataStoreEvent);
		}
	}
	/**
	 * gets the current name of the store;
	 * @return the name of this store
	 */
	public String getStoreName()
	{
		return fFileName;
	}

	/**
	 * Sets the name of this store. This name will be used when the store
	 * is written out to disk by the <code>saveToDisk</code> method.
	 * 
	 * @param name the name of the persistent store
	 */
	public void setStoreName(String name)
	{
		fFileName = name;
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: DefaultPersistentStore.java,v $
// Revision 1.6  2005/04/06 14:59:46  chostetter_cvs
// Adjusted logging levels
//
// Revision 1.5  2005/01/20 08:15:11  tames
// Change to error log message
//
// Revision 1.4  2005/01/07 20:16:26  tames
// Improved error handling and logging.
//
// Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.2  2004/06/07 14:11:25  tames_cvs
// Minor format changes
//
// Revision 1.1  2004/05/28 22:02:11  tames_cvs
// added an interface and removed IRC specific code
//
// Revision 1.1  2004/05/05 19:11:52  chostetter_cvs
// Further restructuring
//
