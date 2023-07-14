//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/publishing/messages/MessageEvent.java,v 1.2 2006/04/18 14:02:48 tames Exp $
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

package gov.nasa.gsfc.commons.publishing.messages;

import gov.nasa.gsfc.commons.publishing.AddressableEvent;
import gov.nasa.gsfc.commons.publishing.paths.Path;


/**
 * A MessageEvent is an event created by a source to hold a
 * {@link gov.nasa.gsfc.commons.publishing.messages.Message}.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 14:02:48 $
 * @author Troy Ames
 */
public class MessageEvent extends AddressableEvent
{
	private Message fMessage = null;

	/**
	 * Constructs a MessageEvent with the given source and message.
	 *
	 * @param source	The source of this event
	 * @param message	The message for the event
	**/
	public MessageEvent(Object source, Message message)
	{
		this(source, message, null, null);
	}

	/**
	 * Construct a MessageEvent for the specified Message and destination.
	 *
	 * @param source	The object on which the event initially occurred.
	 * @param message	The Message contained in the event
	 * @param destination	The destination path for the event
	 */
	public MessageEvent(Object source, Message message, Path destination)
	{
		this(source, message, destination, null);
	}

	/**
	 * Construct a MessageEvent for the specified Message, destination, and
	 * reply.
	 *
	 * @param source	The object on which the event initially occurred.
	 * @param message	The Message contained in the event
	 * @param destination	The destination path for the event
	 * @param reply	The reply to path for the event
	 */
	public MessageEvent(Object source, Message message, Path destination, Path reply)
	{
		super(source, destination, reply);
		
		fMessage = message;
	}

	/**
	 * Returns the message contained in the event.
	 * 
	 * @return the Message
	 */
	public Message getMessage()
	{
		return (fMessage);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageEvent.java,v $
//  Revision 1.2  2006/04/18 14:02:48  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 03:57:06  tames
//  Relocated implementation.
//
//
