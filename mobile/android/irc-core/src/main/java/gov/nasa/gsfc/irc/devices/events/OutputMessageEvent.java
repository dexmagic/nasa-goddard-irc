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
 * An OutputMessageEvent is the container between components for outgoing
 * {@link gov.nasa.gsfc.commons.publishing.messages.Message Messages} within the
 * default DeviceProxy component architecture. The event can also contain a send
 * context which is typically a Path representing the components that should
 * handle the event as it is passed through the DeviceProxy architecture. If
 * this event was a result of a corresponding
 * {@link gov.nasa.gsfc.irc.devices.events.InputMessageEvent InputMessageEvent}
 * then the send context is likely the same as the reply context that was
 * constructed with the InputMessageEvent. This allows a component to either
 * verify that it should handle a message event or handle it in an application
 * specific way based on the corresponding InputMessageEvent.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 14:02:48 $
 * @author Troy Ames
 */
public class OutputMessageEvent extends EventObject
{
	private Message fMessage = null;
	private Path fSendContext = null;

	/**
	 * Constructs an InputMessageEvent with the given source and 
	 * content of the given event. The message and send context will be 
	 * set from the given OutputMessageEvent. Useful when the user wants to 
	 * propagate an event but change the source.
	 *
	 * @param source	The source of this event
	 * @param event	The OutputMessageEvent to use as a template
	**/
	public OutputMessageEvent(Object source, OutputMessageEvent event)
	{
		this(source, event.getMessage(), event.getSendContext());
	}

	/**
	 * Constructs an InputMessageEvent with the given source and message.
	 *
	 * @param source	The source of this event
	 * @param message	The message for the event
	**/
	public OutputMessageEvent(Object source, Message message)
	{
		this(source, message, null);
	}

	/**
	 * Construct an InputMessageEvent for the specified Message and send context.
	 *
	 * @param source	The source of this event.
	 * @param message	The Message contained in the event
	 * @param sendContext	The sendContext path for the event
	 */
	public OutputMessageEvent(Object source, Message message, Path sendContext)
	{
		super(source);
		fMessage = message;
		fSendContext = sendContext;
	}

	/**
	 * Returns the send context path for this event. 
	 * 
	 * @return the send context path for this event
	 */
	public Path getSendContext()
	{
		return fSendContext;
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
//  $Log: OutputMessageEvent.java,v $
//  Revision 1.2  2006/04/18 14:02:48  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 04:05:56  tames
//  Relocated and refactored Input and Output messages.
//
//