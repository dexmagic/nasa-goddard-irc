//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/connections/InputBufferListener.java,v 1.1 2004/10/14 15:16:51 chostetter_cvs Exp $
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
 * The listener interface for receiving connection input events. The class that 
 * is interested in processing an event implements this interface, and registers
 * with a connection, using the 
 * connection's addInputBufferListener method. When the event occurs, 
 * the listener's handleInputBufferEvent method is invoked.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/10/14 15:16:51 $
 * @author	T. Ames
 */
public interface InputBufferListener extends EventListener
{

	/**
	 * Handles a new InputBufferEvent.
	 * 
	 * @param event the InputBufferEvent
	 */
	public void handleInputBufferEvent(InputBufferEvent event);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: InputBufferListener.java,v $
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/27 20:33:45  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.2  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.1  2004/04/30 20:30:43  tames_cvs
//  Initial Version
//
