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

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;


/**
 * A MessageFactory is a factory object which is responsible for
 * creating Messages. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/04/18 04:16:01 $
 * @author		Troy Ames
 */
public interface MessageFactory
{
	/**
	 * Create a new Message with a null descriptor.
	 *
	 * @return	a new Message
	 */
	public Message createMessage();

	/**
	 * Create a new Message from an existing message.
	 *
	 * @param	message	the message to use as a template.
	 * @return	a new Message
	 */
	public Message createMessage(Message message);

	/**
	 * Create a new Message from the specified Descriptor.
	 *
	 * @param	descriptor	descriptor from which to create the message.
	 * @return	a new Message
	 */
	public Message createMessage(MessageDescriptor descriptor);

	/**
	 * Retrieve the message from the message cache.
	 *
	 * @param	descriptor	key from which to locate the message.
	 * @return	a copy of a cached message or null if one does not exist.
	 * @see #storeMessage(Message)
	 */
	public Message retrieveMessage(MessageDescriptor descriptor);
	
	/**
	 * Store the message in the message cache.
	 *
	 * @param	message	message to store in cache.
	 * @see #retrieveMessage(MessageDescriptor)
	 */
	public void storeMessage(Message message);

	/**
	 * Clear the message cache.
	 */
	public void clearMessageCache();
}

//--- Development History ----------------------------------------------------
//
//	$Log: MessageFactory.java,v $
//	Revision 1.8  2006/04/18 04:16:01  tames
//	Relocated and refactored Message related classes.
//	
//	Revision 1.7  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.6  2005/01/26 21:31:33  tames
//	Added a clearMessageCache method.
//	
//	Revision 1.5  2005/01/07 20:40:20  tames
//	Added methods for storing and retrieving messages from a cache as
//	well as creating a message using an existing message as a template.
//	
//	Revision 1.4  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//
//	Revision 1.3  2004/08/06 14:30:50  tames_cvs
//	*** empty log message ***
//
//	Revision 1.2  2004/07/27 21:10:07  tames_cvs
//	Message interface changes
//
//	Revision 1.1  2004/07/06 14:44:25  tames_cvs
//	General message refactoring
//
