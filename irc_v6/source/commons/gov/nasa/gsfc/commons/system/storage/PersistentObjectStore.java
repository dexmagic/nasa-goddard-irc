//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: PersistentObjectStore.java,v $
// Revision 1.2  2004/05/28 22:02:11  tames_cvs
// added an interface and removed IRC specific code
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
package gov.nasa.gsfc.commons.system.storage;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This interface defines a persistent store for Serializable Objects. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2004/05/28 22:02:11 $
 * @author Troy Ames
**/
public interface PersistentObjectStore
{
	/**
	 * Store a Serializable in the store with the specified key.
	 *
	 * @param key		The key under which to store the Serializable
	 * @param object	The Serializable to store
	 */
	public void store(String key, Serializable object);

	/**
	 * Retrieve a Serializable from the store.
	 *
	 * @param key	The key of the Serializable to be retrieved
	 *
	 * @return The Serializable stored under the specified key, or null
	 */
	public Serializable retrieve(String key);

	/**
	 * Remove a Serializable from the store.
	 *
	 * @param key	The key to be removed
	 */
	public void delete(String key);

	/**
	 * Retrieve all of the keys currently in the store.
	 *
	 * @return Iterator	over all keys
	 */
	public Iterator getAllKeys();

	/**
	 * Read in the contents of the store from a file.
	 *
	 * @return True if the store was successfully read; false otherwise.
	 */
	public boolean readFromDisk();

	/**
	 * Write the current contents of the store to a file.
	 *
	 * @return True if the store was successfully written; false otherwise.
	 */
	public boolean saveToDisk();

	/**
	 * Add a listener for changes to the data store.<p>
	 *
	 * @param l  the listener to add
	 */
	public void addPersistentObjectStoreListener(PersistentObjectStoreListener l);

	/**
	 * Remove a listener that is listening for changes to the 
	 * data store.
	 *  
	 * @param l  the listener to remove
	 */
	public void removePersistentObjectStoreListener(PersistentObjectStoreListener l);

	/**
	 * Gets the current name of the store;
	 * 
	 * @return the name of this store
	 */
	public String getStoreName();

	/**
	 * Sets the name of this store. This name will be used when the store
	 * is written out to disk by the <code>saveToDisk</code> method or read from 
	 * disk using the <code>readFromDisk</code> method.
	 * 
	 * @param name the name of the persistent store
	 */
	public void setStoreName(String name);
}