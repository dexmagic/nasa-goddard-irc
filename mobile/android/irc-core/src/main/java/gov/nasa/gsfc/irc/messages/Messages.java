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

package gov.nasa.gsfc.irc.messages;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.messages.MessageEvent;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;

/**
 * A collection of static functions that provide a simple facade on
 * the IRC messaging functionality. This class contains functions for publishing
 * messages. While this class is primarily intended for
 * use by external scripts, it may be used by internal Java code as well.
 *
 * @version     $Date: 2006/04/18 14:02:48 $
 * @author      Jeremy Jones
 * @author      Troy Ames
**/
public class Messages
{
	/**
	 * Publishes a message asynchronously (i.e., just publish it out). This
	 * method will create, initialize, and validate the message before
	 * publishing it.
	 * 
	 * @param messageName name of message to publish
	 * @throws InvalidMessageException if message does not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(String messageName) 
			throws InvalidMessageException, UnknownMessageException
	{
		publishMessage(getMessageDescriptor(messageName), new Object[0]);
	}

	/**
	 * Publishes a message asynchronously (i.e., just publish it out). This
	 * method will create, initialize, and validate the message before
	 * publishing it.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @throws InvalidMessageException if message does not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(MessageDescriptor descriptor) 
			throws InvalidMessageException, UnknownMessageException
	{
		publishMessage(descriptor, new Object[0]);
	}

	/**
	 * Publishes a message. This method will create, initialize, and validate
	 * the message before publishing it.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @param arg1 message argument value
	 * @throws InvalidMessageException if arguments do not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor,
			Object arg1) 
			throws 	InvalidMessageException,
					UnknownMessageException
	{
		publishMessage(descriptor, new Object[] { arg1 });
	}

	/**
	 * Publishes a message. This method will create, initialize, and validate
	 * the message before publishing it. Array of message arguments must be in
	 * the same order as listed in associated MessageDescriptor.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @param arg1 message argument value
	 * @param arg2 message argument value
	 * @throws InvalidMessageException if arguments do not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor,
			Object arg1,
			Object arg2) 
			throws InvalidMessageException,
				UnknownMessageException
	{
		publishMessage(descriptor, new Object[] { arg1, arg2 });
	}

	/**
	 * Publishes a message. This method will create, initialize, and validate
	 * the message before publishing it. Array of message arguments must be in
	 * the same order as listed in associated MessageDescriptor.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @param arg1 message argument value
	 * @param arg2 message argument value
	 * @param arg3 message argument value
	 * @throws InvalidMessageException if arguments do not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor,
			Object arg1,
			Object arg2,
			Object arg3) 
			throws InvalidMessageException,
				UnknownMessageException
	{
		publishMessage(descriptor, new Object[] { arg1, arg2, arg3 });
	}

	/**
	 * Publishes a message. This method will create, initialize, and validate
	 * the message before publishing it. Array of message arguments must be in
	 * the same order as listed in associated MessageDescriptor.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @param arg1 message argument value
	 * @param arg2 message argument value
	 * @param arg3 message argument value
	 * @param arg4 message argument value
	 * @throws InvalidMessageException if arguments do not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor,
			Object arg1,
			Object arg2,
			Object arg3,
			Object arg4) 
			throws InvalidMessageException,
				UnknownMessageException
	{
		publishMessage(descriptor, new Object[] { arg1, arg2, arg3, arg4 });
	}

	/**
	 * Publishes a message, with arguments from the specified Set. This method
	 * will create, initialize, and validate the message before publishing it.
	 * Array of message arguments must be in the same order as listed in
	 * associated MessageDescriptor.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @param argSet set of message argument values
	 * @throws InvalidMessageException if arguments do match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor, 
			Set argSet) 
			throws InvalidMessageException,
				UnknownMessageException
	{
		publishMessage(descriptor, argSet.toArray());
	}
	
	/**
	 * Publishes a message. This method will create, initialize, and validate
	 * the message before publishing it. Array of message arguments must be in
	 * the same order as listed in associated MessageDescriptor.
	 * 
	 * @param descriptor name of message type to publish
	 * @param args message argument values
	 * @throws InvalidMessageException if arguments do not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor,
			Object[] args) 
			throws InvalidMessageException,
				UnknownMessageException
	{
		// Create message
		Message message = createMessage(descriptor);
		fillMessageArgs(message, args);
		
		// Validate and publish message
		validateMessage(message);
		publishMessage(message, descriptor.getPath());
	}

	/**
	 * Publishes a message. This method will create, initialize, and validate
	 * the message before publishing it. Map of message arguments must use the
	 * field names as keys as listed in associated MessageDescriptor.
	 * 
	 * @param descriptor descriptor of message to publish
	 * @param args map of message argument keys and values
	 * @throws InvalidMessageException if arguments do not match descriptor
	 *             constraints
	 * @throws UnknownMessageException if message cannot be found
	 */
	public static void publishMessage(
			MessageDescriptor descriptor,
			Map args) 
			throws InvalidMessageException,
				UnknownMessageException 
	{
		// Create message
		Message message = createMessage(descriptor);
		fillMessageArgs(message, args);
		
		// Validate and publish message
		validateMessage(message);
		publishMessage(message, descriptor.getPath());
	}

	/**
	 * Publish the message with the given destination. 
	 * 
	 * @param message	the Message to publish
	 * @param destination the destination Path specification or null
	 */
	public static void publishMessage(Message message, Path destination) 
	{		
		// Create event to hold message
		MessageEvent event = 
			new MessageEvent(Irc.getEventBus(), message, destination);
		
		// Publish message
		if (message.isSynchronous())
		{
			publishSynchronousMessage(event);
		}
		else
		{
			publishMessage(event);
		}
	}

	/**
	 * Publish the Message by giving it to the
	 * {@link gov.nasa.gsfc.commons.publishing.EventBus EventBus}.
	 * 
	 * @param	message	Message object to publish
	**/
	public static void publishMessage(Message message) 
	{
		Path destination = null;
		MessageDescriptor descriptor = 
			(MessageDescriptor) message.getProperty(MessageKeys.DESCRIPTOR);
		
		if (descriptor != null)
		{
			destination = descriptor.getPath();
		}
		
		publishMessage(message, destination);
	}

	/**
	 * Publish the MessageEvent by giving it to the
	 * {@link gov.nasa.gsfc.commons.publishing.EventBus EventBus}.
	 * 
	 * @param event MessageEvent to publish
	 */
	public static void publishMessage(MessageEvent event)
	{		
		Irc.getEventBus().publish(event);
	}

	/**
	 * Publishes the event synchronously by giving it to the
	 * {@link gov.nasa.gsfc.commons.publishing.EventBus EventBus} and blocking
	 * until the event is handled.
	 * 
	 * @param event MessageEvent object to publish
	 */
	public static void publishSynchronousMessage(MessageEvent event)
	{		
		try
		{
			Irc.getEventBus().publishAndWait(event);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Publishes the Message synchronously by giving it to the
	 * {@link gov.nasa.gsfc.commons.publishing.EventBus EventBus} and blocking
	 * until the message is handled.
	 * 
	 * @param	message	Message object to publish
	**/
	public static void publishSynchronousMessage(Message message)
	{		
		// Publish message
		MessageEvent event = new MessageEvent(new Object(), message);
		
		try
		{
			Irc.getEventBus().publishAndWait(event);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve the message from the message cache.
	 *
	 * @param	messageName		name of message
	 * @throws 	UnknownMessageException	if message cannot be found
	**/
	public static Message retrieveMessage(String messageName)
			throws UnknownMessageException
	{
		// Retrieve message
		MessageDescriptor descriptor = getMessageDescriptor(messageName);
		Message message = 
			Irc.getMessageFactory().retrieveMessage(descriptor);
		
		if (message == null)
		{
			throw new UnknownMessageException(
					"Message cache does not contain message: " + messageName);
		}
		
		return message;
	}

	/**
	 * Save the message to the message cache.
	 *
	 * @param	message		the message to save
	**/
	public static void saveMessage(Message message)
	{
		// Store message
		Irc.getMessageFactory().storeMessage(message);
	}

	/**
	 * Create an empty message.
	**/
	public static Message createMessage()
	{
		// Create message
		return Irc.getMessageFactory().createMessage();
	}

	/**
	 * Create a new Message from an existing message.
	 *
	 * @param	message	the message to use as a template.
	 * @return	a new Message
	 */
	public static Message createMessage(Message message)
	{
		// Create message
		return Irc.getMessageFactory().createMessage(message);
	}
	
	/**
	 * Create a message with the given fully qualified name.
	 * 
	 * @param	messageName		name of message
	 * @throws 	UnknownMessageException	if message descriptor cannot be found
	**/
	public static Message createMessage(String messageName)
			throws UnknownMessageException
	{
		// Create message
		MessageDescriptor descriptor = getMessageDescriptor(messageName);
		
		return Irc.getMessageFactory().createMessage(descriptor);
	}

	/**
	 * Create a message with the given descriptor.
	 * 
	 * @param descriptor the descriptor of the message to create
	**/
	public static Message createMessage(MessageDescriptor descriptor)
	{
		return Irc.getMessageFactory().createMessage(descriptor);
	}

	/**
	 * Validate message against descriptor constraints.
	 * 
	 * @param	message	Message to validate
	 * @throws 	InvalidMessageException	
	 * 			if message does not match descriptor constraints
	**/
	public static void validateMessage(Message message)
			throws InvalidMessageException
	{
		// Validate message
		Irc.getMessageValidator().validate(message);
	}
	
	/**
	 * Retrieves the named device Descriptor.
	 * 
	 * @param	name	name of device descriptor to find
	 * @return	the device descriptor, or null if not found
	**/
	public static DeviceDescriptor getDeviceDescriptor(String name)
	{
		return Irc.getDescriptorLibrary().getDeviceDescriptor(name);
	}
	
	/**
	 * Locates a message descriptor from the given fully qualified name.
	 * 
	 * @param	messageName	fully qualified name.
	 * @return	descriptor for specified message name
	 * @throws	UnknownMessageException	if the message cannot be found
	**/
	public static MessageDescriptor getMessageDescriptor(
		String messageName)
		throws UnknownMessageException
	{
		MessageDescriptor descriptor = 
			Irc.getDescriptorLibrary().getMessageDescriptor(messageName);
		
		if (descriptor == null)
		{
			throw new UnknownMessageException(
					"Descriptor not found for message: " + messageName);
		}
		
		return descriptor;
	}

	/**
	 * Fills up a Message with values for its attributes according to the
	 * specified arguments. Values in array must be in the same order as
	 * fields in the descriptor.
	 * 
	 * @param	message	field arguments set in this message object
	 * @param	args	ordered set of message argument values
	 * @throws IllegalArgumentException
	**/
	public static void fillMessageArgs(Message message, Object[] args)
	{
		if (args == null)
		{
			throw new IllegalArgumentException(
					"Messages.fillMessageArgs() args not allowed to be null");
		}
		
		MessageDescriptor descriptor = 
			(MessageDescriptor) message.getProperty(MessageKeys.DESCRIPTOR);

		// Assume the arguments match the order specified for the attributes
		// in the descriptor
		int i = 0;
		Iterator fields = descriptor.getEntries().iterator();
		
		while (fields.hasNext())
		{
			FieldDescriptor field = (FieldDescriptor) fields.next();
			
			if (i < args.length)
			{
				putMessageField(message, field, args[i]);
				++i;				
			}
			else
			{
				putMessageField(message, field, field.getDefaultValue());
			}
		}
		if (i < args.length)
		{
			throw new IllegalArgumentException(
					"Number of arguments (" + args.length 
					+ ") exceeds number of fields in " + 
					descriptor.getFullyQualifiedName());
		}
	}
	
	/**
	 * Fills up a Message with values for its attributes according to the
	 * specified arguments. Keys in arugument map should match field names
	 * in descriptor.
	 * 
	 * @param	message	field arguments set in this message object
	 * @param	argMap	map of message argument values
	 * @throws IllegalArgumentException
	**/
	public static void fillMessageArgs(Message message, Map argMap)
	{
		if (argMap == null)
		{
			throw new IllegalArgumentException(
					"Messages.fillMessageArgs() argMap not allowed to be null");
		}
		
		MessageDescriptor descriptor = 
			(MessageDescriptor) message.getProperty(MessageKeys.DESCRIPTOR);

		// Fill in default values first
		Iterator fields = descriptor.getEntries().iterator();
		
		while (fields.hasNext())
		{
			FieldDescriptor field = (FieldDescriptor) fields.next();
			putMessageField(message, field, field.getDefaultValue());
		}
		
		// Fill in arguments
		for (Iterator argIter = argMap.keySet().iterator(); argIter.hasNext();)
		{
			String argName = (String) argIter.next();
			FieldDescriptor field = (FieldDescriptor) descriptor.getEntry(argName);
			
			if (field != null)
			{
				putMessageField(message, field, argMap.get(argName));
			}
			else
			{
				throw new IllegalArgumentException(
						"Argument (" + argName 
						+ ") is not defined in descriptor " 
						+ descriptor.getFullyQualifiedName());
			}
		}
	}
	
//	/**
//	 * Sets default fields in the specified Message.
//	 * 
//	 * @param	message		values will be set within this message object
//	 * @param	destination	name of destination instrument for message
//	 * @param	messageName	name of message descriptor
//	 * @throws 	UnknownMessageException
//	**/
//	private static void fillMessageSkeleton(
//			Message message, 
//			String messageName) 
//			throws UnknownMessageException 
//	{		
//		MessageDescriptor descriptor = 
//			Irc.getDescriptorLibrary().getMessageDescriptor(messageName);
//		
//		if (descriptor == null)
//		{
//			throw new UnknownMessageException(
//					"Descriptor not found for message: " + messageName);
//		}
//
//		message.setDescriptor(descriptor);
//		
//		// Fill in default values if they are defined
//		for (Iterator fields = descriptor.getEntries().iterator(); 
//			fields.hasNext();)
//		{
//			FieldDescriptor field = (FieldDescriptor) fields.next();
//			Object defaultValue = field.getDefaultValue();
//			
//			if (defaultValue != null)
//			{
//				message.put(field.getName(), defaultValue);
//			}
//		}
//	}
		
	/**
	 * Stores an attribute value in the specified message object.
	 * 
	 * @param	message		attribute value stored in this message object
	 * @param	attribute	attribute in which to store the value
	 * @param	value		the new value to store
	**/
	private static void putMessageField(
			Message message, 
			FieldDescriptor attribute, 
			Object value)
	{
		Object convertedValue = value;

		Class expectedType = attribute.getValueClass();

		// If field is a type of Number, value must be too
		if (Number.class.isAssignableFrom(expectedType) 
				&& !(value instanceof Number))
		{
			try
			{
				value = new Double(value.toString());
			}
			catch (NumberFormatException ex)
			{
				throw new IllegalArgumentException(
						"Number type expected for data object field \"" 
						+ attribute.getName() + "\", got " 
						+ value.getClass().getName());
			}
		}
		
		// Convert value to the expected type
		if (expectedType.equals(Byte.class))
		{
			convertedValue = new Byte(((Number) value).byteValue());
		}
		else if (expectedType.equals(Double.class))
		{	
			convertedValue = new Double(((Number) value).doubleValue());
		}
		else if (expectedType.equals(Float.class))
		{	
			convertedValue = new Float(((Number) value).floatValue());
		}
		else if (expectedType.equals(Integer.class))
		{	
			convertedValue = new Integer(((Number) value).intValue());
		}
		else if (expectedType.equals(Long.class))
		{	
			convertedValue = new Long(((Number) value).longValue());
		}
		else if (expectedType.equals(Short.class))
		{	
			convertedValue = new Short(((Number) value).shortValue());
		}
		
		// Store the value in the Message
		message.put(attribute.getName(), convertedValue);
	}
	
	/**
	 * Defined as private to prevent instantiation.
	**/
	private Messages()
	{
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: Messages.java,v $
//	Revision 1.22  2006/04/18 14:02:48  tames
//	Reflects relocated Path and DefaultPath.
//	
//	Revision 1.21  2006/04/18 04:16:01  tames
//	Relocated and refactored Message related classes.
//	
//	Revision 1.20  2006/01/25 21:42:25  chostetter_cvs
//	Support for arbitrary-length Message parsing
//	
//	Revision 1.19  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.18  2005/11/21 14:29:08  tames_cvs
//	Made createMessage(Message) static.
//	
//	Revision 1.17  2005/09/13 19:57:37  tames_cvs
//	Added a no arg createMessage method and a createMessage(Message)
//	method.
//	
//	Revision 1.16  2005/04/12 15:33:18  tames_cvs
//	Removed unnecessary throws for createMessage method.
//	
//	Revision 1.15  2005/04/08 20:57:45  tames_cvs
//	Removed references to the unused UnknownDestinationException.
//	Updated some Javadoc comments.
//	
//	Revision 1.14  2005/02/01 18:55:11  tames
//	Updated to reflect DesriptorLibrary method name changes.
//	
//	Revision 1.13  2005/02/01 18:27:01  tames
//	Redesign to reflect DescriptorLibrary changes.
//	
//	Revision 1.12  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.11  2004/10/01 15:47:40  chostetter_cvs
//	Extensive refactoring of field/property/argument descriptors
//	
//	Revision 1.10  2004/09/28 19:26:32  tames_cvs
//	Reflects changing the name of Instrument related classes and methods
//	to Device since a device can include sensors, software, simulators etc.
//	Instrument maybe used in the future for a specific device type.
//	
//	Revision 1.9  2004/09/27 22:19:02  tames
//	Reflects the relocation of Instrument descriptors and
//	implementation classes to the devices package.
//	
//	Revision 1.8  2004/09/27 20:12:45  tames
//	Reflect changes to message handling and method signatures.
//	
//	Revision 1.7  2004/09/21 15:09:33  tames
//	Added functionality to support a destination path for messages.
//	
//	Revision 1.6  2004/09/05 13:37:51  tames
//	removed some of the specific synchronous message methods
//	
//	Revision 1.5  2004/08/19 13:41:04  jhiginbotham_cvs
//	Replace asserts with runtime exceptions.
//	
//	Revision 1.4  2004/08/10 13:34:22  tames
//	*** empty log message ***
//	
//	Revision 1.3  2004/08/09 18:34:46  tames_cvs
//	Added fillMessageArgs utility methods
//	
//	Revision 1.2  2004/08/09 17:27:19  tames_cvs
//	misc message changes
//	
//	Revision 1.1  2004/08/06 14:26:28  tames_cvs
//	Initial version
//	
