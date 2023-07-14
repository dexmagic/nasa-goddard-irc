//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, 
//	Code 580 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is listed at the end of the file
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
package gov.nasa.gsfc.irc.scripts;

import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.namespaces.AbstractCreatedMember;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;


/**
 * A Script represents a script invocation.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/08/01 19:55:48 $
 * @author	Troy Ames
 */
public class DefaultScript extends AbstractCreatedMember 
	implements Script
{
	private static final String CLASS_NAME = DefaultScript.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private transient Descriptor fDescriptor;
	
	private Map fMap = new HashMap(); // Holds script parameters
	private boolean fSynchronous = true;

	
	/**
	 * Constructs a new Script having the given base name and name qualifier 
	 * (which can be null), and created by the Creator indicated by the 
	 * given MemberId.
	 *
	 * @param name The base name of the new Script
	 * @param nameQaulifier The name qualifier of the new Script (if any)
	 * @param creatorId The MemberId of the Creator of the new Script
	**/
	
	public DefaultScript(String name, String nameQualifier, MemberId creatorId)
	{
		super(name, nameQualifier, creatorId);
	}

	/**
	 * Constructs a new Script having the same fully-qualified name as the 
	 * given ScriptDescriptor, and created by the Creator indicated by the 
	 * given MemberId.
	 *
	 * @param descriptor The ScriptDescriptor of the new Script
	 * @param creatorId The MemberId of the Creator of the new Script
	**/
	
	public DefaultScript(ScriptDescriptor descriptor, MemberId creatorId)
	{
		this(null, null, creatorId);
		
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the Descriptor of this Script to the given Descriptor.
	 *
	 * @param descriptor The Descriptor of this Script
	**/
	
	public final void setDescriptor(ScriptDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor(descriptor);
	}
	

	/**
	 * Returns the Descriptor of this Script.
	 *
	 * @return The Descriptor of this Script
	**/
	
	public final Descriptor getDescriptor()
	{
		return (fDescriptor);
	}
	

	/**
	 *  Sets the MemberId of the Creator of this Script to the given MemberId.
	 *
	 * @param creatorId The MemberId of the Creator of the new Script
	**/
	
	public void setCreatorId(MemberId creatorId)
	{
		super.setCreatorId(creatorId);
		
		if (fDescriptor == null)
		{
			setNameQualifier(creatorId.getFullyQualifiedName());
		}
	}
	
		
	/**
	 *  Sets the base name of this Script to the given name.
	 *
	 *  @param name The desired new base name of this Script
	 **/
	
	public void setName(String name)
	{
		try
		{
			super.setName(name);
		}
		catch (PropertyVetoException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Unable to set name of Script " + 
					getFullyQualifiedName() + " to " + name;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, "setName", message, ex);
			}
		}
	}
	

	/**
	 *  Sets the name qualifier of this Script to the given name qualifier.
	 *
	 *  @param nameQualifier The desired new name qualifier of this Script
	 **/
	
	public void setNameQualifier(String nameQualifier)
	{
		try
		{
			super.setNameQualifier(nameQualifier);
		}
		catch (PropertyVetoException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Unable to set name qualifier of Script " + 
					getFullyQualifiedName() + " to " + nameQualifier;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, "setName", message, ex);
			}
		}
	}
	
	
	/**
	 * Adds the Map of arguments to the script.
	 *
	 * @param	arguments Map of key/value pairs
	**/
	public void putArguments(Map arguments)
	{
		fMap.putAll(arguments);
	}

	/**
	 * Returns a Map of key/value pairs representing the arguments for this 
	 * script.
	 *
	 * @return	Map of key/value pairs
	**/
	public Map getArguments()
	{
		return fMap;
	}

	/**
	 * Determine whether the script should be executed
	 * synchronously.
	 *
	 * @return	boolean - Should the script be executed synchronously?
	 */
	public boolean isSynchronous()
	{
		return fSynchronous;
	}
	
	/**
	 * Set whether the script should be executed
	 * synchronously.
	 *
	 * @param	boolean - Should the script be executed synchronously?
	 */
	public void setSynchronous(boolean enable)
	{
		fSynchronous = enable;
	}
	
	/**
	 * Sets the Descriptor of this script to the given Descriptor. The name,
	 * synchronous, and fields will be set from the descriptor.
	 *
	 * @param	The Descriptor of this script
	**/
	private final void configureFromDescriptor(ScriptDescriptor descriptor)
	{
		if (descriptor != null)
		{
			setName(descriptor.getName());
			setNameQualifier(descriptor.getNameQualifier());
			
			fSynchronous = descriptor.isSynchronous();
			
			Iterator fields = descriptor.getFields().iterator();
			
			while (fields.hasNext())
			{
				FieldDescriptor fieldDesc = (FieldDescriptor) fields.next();
				put(fieldDesc.getName(), fieldDesc.getDefaultValue());
			}
		}
	}

	/**
     * Removes all mappings from this script.
     */
	public void clear()
	{
		fMap.clear();
	}
	
    /**
     * Returns <tt>true</tt> if this script contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this script is to be tested
     * @return <tt>true</tt> if this script contains a mapping for the specified
     * key.
     */
	public boolean containsKey(Object key)
	{
		return (fMap.containsKey(key));
	}
	
	
    /**
     * Returns <tt>true</tt> if this script maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this script is to be tested.
     * @return <tt>true</tt> if this script maps one or more keys to the
     *         specified value.
     */
	public boolean containsValue(Object value)
	{
		return (fMap.containsValue(value));
	}
	
    /**
     * Returns a collection view of the mappings contained in this script.  Each
     * element in the returned collection is a <tt>Map.Entry</tt>.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the mappings contained in this script.
     * @see Map.Entry
     */
	public Set entrySet()
	{
		return (fMap.entrySet());
	}
	
    /**
     * Returns a set view of the keys contained in this script.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  The set supports element removal, which removes the
     * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this script.
     */
	public Set keySet()
	{
		return (fMap.keySet());
	}
	
    /**
     * Returns <tt>true</tt> if this script contains no key-value mappings.
     *
     * @return <tt>true</tt> if this script contains no key-value mappings.
     */
	public boolean isEmpty()
	{
		return (fMap.isEmpty());
	}
	
    /**
     * Returns the value to which this script maps the specified key.  Returns
     * <tt>null</tt> if the script contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * script contains no mapping for the key; it's also possible that the script
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * @return the value to which this script maps the specified key.
     * @param key key whose associated value is to be returned.
     */
	public Object get(Object key)
	{
		return (fMap.get(key));
	}
	
    /**
     * Associates the specified value with the specified key in this script.
     * If the script previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the HashMap previously associated
     *	       <tt>null</tt> with the specified key.
     */
	public Object put(Object key, Object value)
	{
		return (fMap.put(key, value));
	}
	
    /**
     * Copies all of the mappings from the specified map to this script
     * These mappings will replace any mappings that
     * this script had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this script.
     * @throws NullPointerException if the specified map is null.
     */
	public void putAll(Map t)
	{
		fMap.putAll(t);
	}
	
    /**
     * Removes the mapping for this key from this script if present.
     *
     * @param  key key whose mapping is to be removed from the script.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the script previously associated <tt>null</tt>
     *	       with the specified key.
     */
	public Object remove(Object key)
	{
		return (fMap.remove(key));
	}
	
    /**
     * Returns the number of key-value mappings in this script.
     *
     * @return the number of key-value mappings in this script.
     */
	public int size()
	{
		return (fMap.size());
	}
	
    /**
     * Returns a collection view of the values contained in this script.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from this script, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this script.
     */
	public Collection values()
	{
		return (fMap.values());
	}
	
    /**
	 * Returns a String representation of this Script.
	 *
	 * @return A String representation of this Script
	**/
	public String toString() 
	{
		StringBuffer result = new StringBuffer("DefaultScript " + getName());
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		else
		{
			result.append("\nWARNING: Has no Descriptor!");
		}
		
		result.append("\nSynchronous: " + fSynchronous);
		
		result.append("\nContents: ");
		
		if ((fMap != null) && (fMap.size() > 0))
		{
			Iterator entries = fMap.entrySet().iterator();
			
			for (int i = 1; entries.hasNext(); i++)
			{
				Map.Entry entry = (Map.Entry) entries.next();
				
				result.append("\n>Entry " + i + ": Name = " + entry.getKey() + 
					" Value = " + entry.getValue());
			}
		}
		else
		{
			result.append(fMap);
		}
		
		return (result.toString());
	}
}

//--- Development History ----------------------------------------------------
//
//	$Log: DefaultScript.java,v $
//	Revision 1.4  2006/08/01 19:55:48  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.3  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.2  2005/03/14 20:24:55  tames
//	Added a setSynchronous() method.
//	
//	Revision 1.1  2005/02/02 19:32:17  tames_cvs
//	Renamed IrcScript class to DefaultScript for naming
//	consistency.
//	
//	Revision 1.2  2005/01/07 20:48:14  tames
//	Scripts now like Messagess implement the Map interface for arguments.
//	Also removed obsolete dot notation for keys in map.
//	
//	
