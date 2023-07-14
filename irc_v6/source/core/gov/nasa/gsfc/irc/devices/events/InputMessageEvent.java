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

package gov.nasa.gsfc.irc.devices.events;

import java.util.EventObject;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.paths.Path;

/**
 * An InputMessageEvent is the container between components for incoming 
 * {@link gov.nasa.gsfc.commons.publishing.messages.Message Messages} 
 * within the default DeviceProxy component architecture. The event can also 
 * contain a reply context which is typically a Path representing the 
 * components handling the event as it is passed through the DeviceProxy 
 * architecture. If this event results in a corresponding 
 * {@link gov.nasa.gsfc.irc.devices.events.OutputMessageEvent OutputMessageEvent}
 * the same reply context is likely to be assigned to the send context of 
 * the OutputMessageEvent. This allows a component to either verify that it
 * should handle a message event or handle it in an application specific way.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 14:02:48 $
 * @author 	Troy Ames
 */
public class InputMessageEvent extends EventObject
{
	private Message fMessage = null;
	private Path fReplyContext = null;

	/**
	 * Constructs an InputMessageEvent with the given source and message.
	 *
	 * @param source	The source of this event
	 * @param message	The message for the event
	**/
	public InputMessageEvent(Object source, Message message)
	{
		this(source, message, null);
	}

	/**
	 * Construct an InputMessageEvent for the specified Message and reply context.
	 *
	 * @param source	The source of this event.
	 * @param message	The Message contained in the event
	 * @param replyContext	The replyContext path for the event
	 */
	public InputMessageEvent(Object source, Message message, Path replyContext)
	{
		super(source);
		fMessage = message;
		fReplyContext = replyContext;
	}

	/**
	 * Returns the reply context path for this event. 
	 * 
	 * @return the reply context path for this event
	 */
	public Path getReplyContext()
	{
		return fReplyContext;
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
//  $Log: InputMessageEvent.java,v $
//  Revision 1.2  2006/04/18 14:02:48  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 04:05:56  tames
//  Relocated and refactored Input and Output messages.
//
//