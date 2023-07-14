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

package gov.nasa.gsfc.irc.devices.ports.connections;

import java.util.EventObject;

/**
 * A ConnectEvent represents a change in the source of the event. Typically the 
 * source is a 
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.Connection Connection}
 * component. Currently the only change type represented by this event is when
 * a new or initial connection was received by the Connection. The event may 
 * have a source specific context such as an <code>InetAddress</code> for 
 * a new connection received by a TCP Connection component.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/05/04 16:02:33 $
 * @author	tames
 **/
public class ConnectEvent extends EventObject
{
	/** 
	 * Event type when a connection has been added or received by the 
	 * Connection. 
	 */
	public static final int CONNECTION_ADDED = 1;

	private Object fContext = null;
	protected int fEventType = 1;
	

    /**
     * Constructs an Event.
     *
     * @param source The object on which the Event initially occurred.
     */
	public ConnectEvent(Object source)
	{
		super(source);
	}

    /**
     * Constructs an Event with the given context.
     *
     * @param source The object on which the Event initially occurred.
     * @param context a source specific context
     */
	public ConnectEvent(Object source, Object context)
	{
		super(source);
		fContext = context;
	}

	/**
	 * Returns the type of event (connection added).
	 * 
	 * @return an integer that indicates the type of event.
	 * @see #CONNECTION_ADDED
	 */
	public int getType()
	{
		return fEventType;
	}
	
	/**
	 * Get the optional context of this Event.
	 * 
	 * @return context or null if not available
	 */
	public Object getContext()
	{
		return fContext;
	}

	/**
	 * Set the optional context of this Event.
	 * 
	 * @param context the context or null if not meaningful
	 */
	protected void setContext(Object context)
	{
		fContext = context;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ConnectEvent.java,v $
//  Revision 1.1  2005/05/04 16:02:33  tames_cvs
//  Initial version
//
//