//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.messages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.messages.StandardMessage;
import gov.nasa.gsfc.commons.system.storage.PersistentObjectStore;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;


/**
 * This is the default MessageFactory for IRC.  A MessageFactory is a
 * factory object which is responsible for creating and caching Messages.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/04/18 04:16:01 $
 * @author		Troy Ames
 */
public class DefaultMessageFactory implements MessageFactory
{
	private String MESSAGE_CACHE_KEY = "Message Cache";
	private PersistentObjectStore fPersistentStore = null;
	private Map fMessageCache = null;

	/**
	 * Default constructor
	 */
	public DefaultMessageFactory()
	{
		fPersistentStore = Irc.getPersistentStore();
		fMessageCache = (Map) fPersistentStore.retrieve(MESSAGE_CACHE_KEY);
		
		if (fMessageCache == null)
		{
			// Create a new cache
			fMessageCache = new HashMap();
			
			// Store the new cache so it will be written to disk on shutdown.
			if (fPersistentStore != null)
			{
				fPersistentStore.store(MESSAGE_CACHE_KEY, (HashMap) fMessageCache);
			}
		}
	}

	/**
	 * Create a new Message.
	 *
	 * @return	a new Message
	 */
	public Message createMessage()
	{
		return createMessage((MessageDescriptor) null);
	}

	/**
	 * Create a new Message from an existing message.
	 *
	 * @param	message	the message to use as a template.
	 * @return	a new Message
	 */
	public Message createMessage(Message message)
	{
		Message messageClone = null;
		
		if (message != null)
		{
			MessageDescriptor descriptor = 
				(MessageDescriptor) message.getProperty(MessageKeys.DESCRIPTOR);
			
			if (descriptor != null)
			{
				messageClone = createMessage(descriptor);
			}
			else
			{
				messageClone = createMessage();
			}
			
			// Set property fields
			Iterator propertyKeys = message.propertyKeySet().iterator();
			
			while (propertyKeys.hasNext())
			{
				Object key = propertyKeys.next();
				messageClone.putProperty(key, message.getProperty(key));
			}
			
			// Set data fields
			Iterator keys = message.keySet().iterator();
			
			while (keys.hasNext())
			{
				Object key = keys.next();
				messageClone.put(key, message.get(key));
			}
			
			// Set fields
			messageClone.setName(message.getName());
			messageClone.setNameQualifier(message.getNameQualifier());
			messageClone.setSynchronous(message.isSynchronous());
		}
		
		return messageClone;
	}

	/**
	 * Create a new Message from the specified Descriptor.
	 *
	 * @param	descriptor	the descriptor from which to create the message.
	 * @return	a new Message
	 */
	public Message createMessage(MessageDescriptor descriptor)
	{
		Message message = new StandardMessage();
		
		if (descriptor != null)
		{
			configureFromDescriptor(descriptor, message);
		}
		
		return message;
	}

	/**
	 * Retrieve the message from the message cache.
	 *
	 * @param	descriptor	key from which to locate the message.
	 * @return	a copy of a cached message or null if one does not exist.
	 * @see #storeMessage(Message)
	 */
	public synchronized Message retrieveMessage(MessageDescriptor descriptor)
	{
		Message message = null;
		
		message = 
			(Message) fMessageCache.get(descriptor.getFullyQualifiedName());
		
		if (message != null)
		{
			// Since descriptors may not be serializable we need to verify
			// that the cached message has a descriptor and set it if null.
			if (message.getProperty(MessageKeys.DESCRIPTOR) == null)
			{
				message.putProperty(MessageKeys.DESCRIPTOR, descriptor);
			}
			
			message = createMessage(message);
		}
		
		return message;
	}
	
	/**
	 * Configures the given message based on the descriptor.
	 *
	 * @param descriptor
	 * @param message
	 */
	private final void configureFromDescriptor(MessageDescriptor descriptor, Message message)
	{
		if (descriptor != null)
		{
			message.setName(descriptor.getName());
			message.putProperty(MessageKeys.DESCRIPTOR, descriptor);
			message.setNameQualifier(descriptor.getNameQualifier());			
			message.setSynchronous(((MessageDescriptor) descriptor).isSynchronous());
			
			for (Iterator fields = descriptor.getFields().iterator(); fields.hasNext();)
			{
				FieldDescriptor fieldDesc = (FieldDescriptor) fields.next();
				message.put(fieldDesc.getName(), fieldDesc.getDefaultValue());
			}
		}
	}

	/**
	 * Store the message in the message cache.
	 *
	 * @param	message	message to store in cache.
	 * @see #retrieveMessage(MessageDescriptor)
	 */
	public synchronized void storeMessage(Message message)
	{
		
		fMessageCache.put(message.getFullyQualifiedName(), message);
	}

	/**
	 * Clear the message cache.
	 */
	public synchronized void clearMessageCache()
	{
		fMessageCache.clear();
	}
}

//--- Development History ----------------------------------------------------
//
//	$Log: DefaultMessageFactory.java,v $
//	Revision 1.14  2006/04/18 04:16:01  tames
//	Relocated and refactored Message related classes.
//	
//	Revision 1.13  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.12  2005/04/08 20:56:31  tames_cvs
//	Changed to reflect removal of path methods in the message interface.
//	
//	Revision 1.11  2005/02/03 16:43:53  tames_cvs
//	Fixed a bug with respect to a null descriptor when retrieving a cached
//	message caused because descriptors are not serializable.
//	
//	Revision 1.10  2005/02/02 19:26:27  tames_cvs
//	Renamed IrcMessage class to DefaultMessage for naming
//	consistency.
//	
//	Revision 1.9  2005/02/01 18:25:22  tames
//	Changed retrieveMessage behavior to return a copy of the message
//	retrieved.
//	
//	Revision 1.8  2005/01/26 21:31:33  tames
//	Added a clearMessageCache method.
//	
//	Revision 1.7  2005/01/07 20:40:20  tames
//	Added methods for storing and retrieving messages from a cache as
//	well as creating a message using an existing message as a template.
//	
//	Revision 1.6  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.5  2004/09/27 22:19:02  tames
//	Reflects the relocation of Instrument descriptors and
//	implementation classes to the devices package.
//	
//	Revision 1.4  2004/09/20 21:07:48  tames
//	*** empty log message ***
//	
//	Revision 1.3  2004/07/27 21:10:07  tames_cvs
//	Message interface changes
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/06 14:44:25  tames_cvs
//	General message refactoring
//	
