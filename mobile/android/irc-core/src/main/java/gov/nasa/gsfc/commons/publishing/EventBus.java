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

package gov.nasa.gsfc.commons.publishing;

import java.util.EventObject;

/**
 * Specifies the Publish/Subscribe pattern for an <code>EventBus</code> of
 * arbitrary event types. Implementors must support both adding one listener
 * with multiple selectors and multiple listeners with the same selector.
 * Listeners should not receive events that they are the source of (via the
 * <code>getSource</code> method on the event). Listeners that are registered
 * with more than one selector may receive the event multiple times if more than
 * one selector matches the event.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 03:53:58 $
 * @author Troy Ames
 */
public interface EventBus extends SelectableBusEventPublisher, BusEventListener
{
	/**
	 * Publishes the given event on the bus.
	 * 
	 * @param event the event to publish
	 */
	public void publish(EventObject event);
	
	/**
	 * Publishes the given event on the bus and does not return until all
	 * interested listeners have received the event.
	 * 
	 * @param event the event to publish
	 * @throws InterruptedException if another thread has interrupted the
	 *             waiting thread
	 */
	public void publishAndWait(EventObject event) throws InterruptedException;
}


//--- Development History  ---------------------------------------------------
//
//  $Log: EventBus.java,v $
//  Revision 1.2  2006/04/18 03:53:58  tames
//  Changed interface specification.
//
//  Revision 1.1  2006/04/05 18:46:35  tames
//  Refactoring and implementation of a Publish/Subscribe pattern for EventObjects.
//
//