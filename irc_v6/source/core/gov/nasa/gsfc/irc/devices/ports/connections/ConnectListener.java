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

import java.util.EventListener;

/**
 * The listener interface for receiving connection 
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.ConnectEvent ConnectEvents}. 
 * The class that is interested in receiving an event implements this interface, 
 * and registers with a connection, using the 
 * connection's addConnectListener method. When the event occurs, 
 * the listener's connectionChanged method is invoked.
 * <p>
 * Because of potential deadlocks the listener should avoid directly or 
 * indirectly calling a synchronized method on the source of this event from 
 * the <code>connectionChanged</code> method.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/05/04 16:02:33 $
 * @author	tames
 **/
public interface ConnectListener extends EventListener
{
	/**
	 * Called when a {@link Connection} has changed in some way such as a 
	 * new connection was accepted.
	 * 
	 * @param event the event describing the connection change.
	 * @see ConnectEvent
	 */
	public void connectionChanged(ConnectEvent event);
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ConnectListener.java,v $
//  Revision 1.1  2005/05/04 16:02:33  tames_cvs
//  Initial version
//
//