//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/events/OutputMessageListener.java,v 1.1 2006/04/18 04:05:56 tames Exp $
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

import java.util.EventListener;



/**
 * The listener interface for receiving an OutputMessageEvent. The class that is
 * interested in processing an event implements this interface, and registers
 * with a source using the addOutputMessageListener method. When the event
 * occurs, the listener's handleOutputMessageEvent method is invoked.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 04:05:56 $
 * @author T. Ames
 */
public interface OutputMessageListener extends EventListener
{

	/**
	 * Causes this OutputMessageListener to receive the given
	 * event.
	 *
	 * @param event An output MessageEvent
	**/
	public void handleOutputMessageEvent(OutputMessageEvent event);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: OutputMessageListener.java,v $
//  Revision 1.1  2006/04/18 04:05:56  tames
//  Relocated and refactored Input and Output messages.
//
//  Revision 1.1  2004/09/27 20:13:24  tames
//  Reflects changes to message event types and message handling.
//
//
