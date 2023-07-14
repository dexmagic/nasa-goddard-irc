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

package gov.nasa.gsfc.commons.publishing.messages;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.types.namespaces.Member;

/**
 * A Message is an object that can indicate if it should be sent
 * synchronously or asynchronously to a given destination.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/04/18 03:57:06 $
 * @author		Troy Ames
**/
public interface Message extends Serializable, Map, Member
{
	/**
	 * Sets the base name of this Message to the given name.
	 * 
	 * @param name The desired new base name of this Message
	 */
	public void setName(String name);

	/**
	 * Sets the name qualifier of this Message to the given name qualifier.
	 * 
	 * @param nameQualifier The desired new name qualifier of this Message
	 */	
	public void setNameQualifier(String nameQualifier);

	/**
	 * Determine whether the message described should be handled synchronously.
	 * 
	 * @return true if the message should be handled synchronously
	 */
	public boolean isSynchronous();

	/**
	 * Set whether the message described should be handled synchronously.
	 * 
	 * @return value true if the message should be handled synchronously
	 */
	public void setSynchronous(boolean value);

	/**
	 * Associates the specified property value with the specified key in this
	 * message. If the message previously contained a mapping for this key, the
	 * old value is replaced.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *         if there was no mapping for key.
	 */
	public Object putProperty(Object key, Object value);

	/**
	 * Returns the property value to which this message maps the specified key.
	 * Returns <tt>null</tt> if the message contains no mapping for this key.
	 * 
	 * @return the value to which this message maps the specified key.
	 * @param key key whose associated value is to be returned.
	 */
	public Object getProperty(Object key);

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
	public Set propertyKeySet();

	/**
	 * Removes all property mappings from this message.
	 */
	public void clearProperties();
}

// --- Development History ---------------------------------------------------
//
//	$Log: Message.java,v $
//	Revision 1.1  2006/04/18 03:57:06  tames
//	Relocated implementation.
//	
//	
