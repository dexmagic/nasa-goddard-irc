//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/connections/InputBufferEvent.java,v 1.6 2006/04/18 14:02:49 tames Exp $
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

package gov.nasa.gsfc.irc.devices.ports.connections;

import java.util.EventObject;

import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;

/**
 * InputBufferEvent is used to notify interested parties that a source 
 * connection has received new data. The context path attached to this event 
 * represents information specific to the source of the event. 
 * Listeners of this event can optionally add information to the path, 
 * however they should always propogate the path in any events resulting 
 * from this input.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 14:02:49 $
 * @author		T. Ames
 */
public class InputBufferEvent extends EventObject
{
	private BufferHandle	fBuffer;
	private Path fReplyContext = null;
	private long fEventTimeMillis = 0;

	/**
	 * Create a new event with the given source and data. The reply context of
	 * the event is set to contain the object returned from
	 * <code>getContext</code> on the given buffer.
	 * 
	 * @param source The object on which the Event initially occurred.
	 * @param buffer The buffer handle generated by the source.
	 */
	public InputBufferEvent(Object source, BufferHandle buffer)
	{
		this(source, buffer, System.currentTimeMillis());
	}

	/**
	 * Create a new event with the given source, data, and source defined path.
	 * 
	 * @param source	The object on which the Event initially occurred.
	 * @param buffer	The buffer handle generated by the source.
	 * @param replyContext		The source defined reply context for the event
	 */
	public InputBufferEvent(Object source, BufferHandle buffer, Path replyContext)
	{
		this(source, buffer, replyContext, System.currentTimeMillis());
	}

	/**
	 * Create a new event with the given source, data, and source defined time.
	 * The reply context of the event is set to contain the object returned from
	 * <code>getContext</code> on the given buffer.
	 * 
	 * @param source The object on which the Event initially occurred.
	 * @param buffer The buffer handle generated by the source.
	 * @param eventTimeMillis The source defined time for this event
	 */
	public InputBufferEvent(Object source, BufferHandle buffer, long eventTimeMillis)
	{
		super(source);
		fBuffer = buffer;
		
		if (buffer.getContext() != null)
		{
			fReplyContext = new DefaultPath(buffer.getContext());
		}
		
		fEventTimeMillis = eventTimeMillis;
	}

	/**
	 * Create a new event with the given source, data, context, and time.
	 * 
	 * @param source The object on which the Event initially occurred.
	 * @param buffer The buffer handle generated by the source.
	 * @param replyContext The source defined reply context for the event
	 * @param eventTimeMillis The source defined time for this event
	 */
	public InputBufferEvent(
			Object source, BufferHandle buffer,
			Path replyContext, long eventTimeMillis)
	{
		super(source);
		fBuffer = buffer;
		fReplyContext = replyContext;
		fEventTimeMillis = eventTimeMillis;
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
	 * Get the handle to the data buffer.
	 * 
	 * @return the handle contained in this Event
	 * @see BufferHandle
	**/
	public BufferHandle getHandle()
	{
		return fBuffer;
	}
	
	/**
	 * Get the time associated with this event by the source. Typically it is
	 * the time the event was created.
	 * 
	 * @return the time associated with this event.
	 */
	public long getEventTimeMillis()
	{
		return fEventTimeMillis;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: InputBufferEvent.java,v $
//  Revision 1.6  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.5  2006/04/18 04:11:09  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.4  2005/05/13 04:05:09  tames
//  Added code to constructor to set the path usning the context of
//  the contained buffer.
//
//  Revision 1.3  2005/02/04 21:46:59  tames_cvs
//  Added constructors to allow specifying a path.
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/27 20:33:45  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.3  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/04/30 20:30:43  tames_cvs
//  Initial Version
//
