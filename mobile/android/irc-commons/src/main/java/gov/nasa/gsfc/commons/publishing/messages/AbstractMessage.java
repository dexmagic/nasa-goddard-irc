//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, 
//  Code 580 for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.commons.publishing.messages;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.namespaces.AbstractMember;


/**
 * An AbstractMessage is a message that can indicate if it should be executed
 * synchronously or asynchronously.  This class provides basic implementations
 * of the properties related to messages.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/04/18 14:02:48 $
 * @author		Troy Ames
**/
public abstract class AbstractMessage extends AbstractMember 
	implements Message
{
    /**
     * Logger for this class
     */
	private static final String CLASS_NAME = AbstractMessage.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	// Header fields
	private boolean fSynchronous = false;
	private Path fReplyTo;
	private Path fDestination;

	// Message properties
	private transient Map fPropertyMap = new LinkedHashMap(); 

	// Message data
	private Map fDataMap = new LinkedHashMap();

	/**
	 * Default constructor of a new Message.
	 *
	**/
	public AbstractMessage()
	{
		this(null, null);
	}
	
	/**
	 * Constructs a new Message having the given base name and name qualifier 
	 * (which can be null), and created by the Creator indicated by the 
	 * given CreatorId.
	 *
	 * @param name The base name of the new Message
	 * @param nameQaulifier The name qualifier of the new Message (if any)
	 * @param creatorId The CreatorId of the new Message
	**/	
	public AbstractMessage(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	/**
	 * Sets the base name of this Message to the given name.
	 * 
	 * @param name The desired new base name of this Message
	 */
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
				String message = "Unable to set name of Message " + 
					getFullyQualifiedName() + " to " + name;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, "setName", message, ex);
			}
		}
	}
	
	/**
	 * Sets the name qualifier of this Message to the given name qualifier.
	 * 
	 * @param nameQualifier The desired new name qualifier of this Message
	 */	
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
				String message = "Unable to set name qualifier of Message " + 
					getFullyQualifiedName() + " to " + nameQualifier;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, "setName", message, ex);
			}
		}
	}
		
	/**
	 * Sets the destination of this Message. This can be null.
	 * 
	 * @param destination The desired destination of this Message
	 */
	public void setDestination(Path destination)
	{
		fDestination = destination;
	}

	/**
	 * Gets the destination of this Message. This can be null.
	 * 
	 * @return The desired destination of this Message if any.
	 */
	public Path getDestination()
	{
		return fDestination;
	}

	/**
	 * Sets the reply to destination of this Message. This can be null.
	 * 
	 * @param destination The desired reply to destination of this Message
	 */
	public void setReplyTo(Path destination)
	{
		fReplyTo = destination;
	}

	/**
	 * Gets the reply to destination of this Message. This can be null.
	 * 
	 * @return The desired reply to destination of this Message if any.
	 */
	public Path getReplyTo()
	{
		return fReplyTo;
	}

	/**
	 * Determine whether the message described should be handled
	 * synchronously.
	 *
	 * @return	true if the message should be handled synchronously
	**/
	public boolean isSynchronous()
	{
		return fSynchronous;
	}

	/**
	 * Set whether the message described should be handled synchronously.
	 *
	 * @return	value true if the message should be handled synchronously
	**/
	public void setSynchronous(boolean value)
	{
		fSynchronous = value;
	}
	
	/**
	 * Removes all property mappings from this message.
	 */
	public void clearProperties()
	{
		fDataMap.clear();
	}
	
    /**
     * Associates the specified property value with the specified key in this 
     * message.
     * If the message previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.
     */
	public Object putProperty(Object key, Object value)
	{
		return (fPropertyMap.put(key, value));
	}

    /**
	 * Returns the property value to which this message maps the specified key.
	 * Returns <tt>null</tt> if the message contains no mapping for this key.
	 * 
	 * @return the value to which this message maps the specified key.
	 * @param key key whose associated value is to be returned.
	 */
	public Object getProperty(Object key)
	{
		return (fPropertyMap.get(key));
	}

    /**
	 * Returns a set view of the property keys contained in this message. The
	 * set is backed by the map, so changes to the map are reflected in the set,
	 * and vice-versa. The set supports element removal, which removes the
	 * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
	 * <tt>clear</tt> operations. It does not support the <tt>add</tt> or
	 * <tt>addAll</tt> operations.
	 * 
	 * @return a set view of the property keys contained in this message.
	 */
	public Set propertyKeySet()
	{
		return (fPropertyMap.keySet());
	}
	
	/**
	 * Removes all data mappings from this message.
	 */
	public void clear()
	{
		fDataMap.clear();
	}
	
    /**
     * Returns <tt>true</tt> if this message contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this message is to be tested
     * @return <tt>true</tt> if this message contains a mapping for the specified
     * key.
     */
	public boolean containsKey(Object key)
	{
		return (fDataMap.containsKey(key));
	}	
	
    /**
     * Returns <tt>true</tt> if this message maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this message is to be tested.
     * @return <tt>true</tt> if this message maps one or more keys to the
     *         specified value.
     */
	public boolean containsValue(Object value)
	{
		return (fDataMap.containsValue(value));
	}
	
    /**
     * Returns a collection view of the mappings contained in this message.  Each
     * element in the returned collection is a <tt>Map.Entry</tt>.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the mappings contained in this message.
     * @see Map.Entry
     */
	public Set entrySet()
	{
		return (fDataMap.entrySet());
	}
	
    /**
     * Returns a set view of the keys contained in this message.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  The set supports element removal, which removes the
     * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this message.
     */
	public Set keySet()
	{
		return (fDataMap.keySet());
	}
	
    /**
     * Returns <tt>true</tt> if this message contains no key-value mappings.
     *
     * @return <tt>true</tt> if this message contains no key-value mappings.
     */
	public boolean isEmpty()
	{
		return (fDataMap.isEmpty());
	}
	
    /**
     * Returns the value to which this message maps the specified key.  Returns
     * <tt>null</tt> if the message contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * message contains no mapping for the key; it's also possible that the message
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * @return the value to which this message maps the specified key.
     * @param key key whose associated value is to be returned.
     */
	public Object get(Object key)
	{
		return (fDataMap.get(key));
	}
	
    /**
     * Associates the specified value with the specified key in this message.
     * If the message previously contained a mapping for this key, the old
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
		return (fDataMap.put(key, value));
	}
	
    /**
     * Copies all of the mappings from the specified map to this message
     * These mappings will replace any mappings that
     * this message had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this message.
     * @throws NullPointerException if the specified map is null.
     */
	public void putAll(Map t)
	{
		fDataMap.putAll(t);
	}
	
    /**
     * Removes the mapping for this key from this message if present.
     *
     * @param  key key whose mapping is to be removed from the message.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the message previously associated <tt>null</tt>
     *	       with the specified key.
     */
	public Object remove(Object key)
	{
		return (fDataMap.remove(key));
	}
	
    /**
     * Returns the number of key-value mappings in this message.
     *
     * @return the number of key-value mappings in this message.
     */
	public int size()
	{
		return (fDataMap.size());
	}
	
    /**
     * Returns a collection view of the values contained in this message.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from this message, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this message.
     */
	public Collection values()
	{
		return (fDataMap.values());
	}
	
    /**
	 * Returns a String representation of this Message.
	 *
	 * @return A String representation of this Message
	**/
	public String toString() 
	{
		StringBuffer result = new StringBuffer("Message name: " + getName());
		
		result.append("\nFull Name: " + getFullyQualifiedName());		
		result.append("\nDestination: " + fDestination);		
		result.append("\nReplyTo: " + fReplyTo);		
		result.append("\nSynchronous: " + fSynchronous);		
		result.append("\nContents: ");
		
		if ((fDataMap != null) && (fDataMap.size() > 0))
		{
			Iterator entries = fDataMap.entrySet().iterator();
			
			for (int i = 1; entries.hasNext(); i++)
			{
				Map.Entry entry = (Map.Entry) entries.next();
				
				result.append("\n>Entry " + i + ": Name = " + entry.getKey() + 
					" Value = " + entry.getValue());
			}
		}
		else
		{
			result.append(fDataMap);
		}
		
		return (result.toString());
	}
	
	/**
	 * Read a serialized version of a Message.
	 * 
	 * @param stream The stream to read from
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream stream) 
		throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		fPropertyMap = new LinkedHashMap();
	}
}

//--- Development History ----------------------------------------------------
//
//	$Log: AbstractMessage.java,v $
//	Revision 1.2  2006/04/18 14:02:48  tames
//	Reflects relocated Path and DefaultPath.
//	
//	Revision 1.1  2006/04/18 03:57:06  tames
//	Relocated implementation.
//	
//	
