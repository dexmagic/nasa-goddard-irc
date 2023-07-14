//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import gov.nasa.gsfc.commons.publishing.selectors.EventSelector;

/**
 * Publishers maintain a collection of listeners, and send published events
 * to each listener known to it.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 03:51:18 $
 * @author Troy Ames
 */
public interface SelectableBusEventPublisher extends BusEventPublisher
{
	/**
	 * Subscribes the given listener to all events that match the 
	 * given selector published by this BusEventPublisher.
	 * 
	 * @param selector the selector to use for this listener.
	 * @param listener the listener to register.
	 */
	public void addBusEventListener(EventSelector selector, BusEventListener listener);
	
	/**
	 * Unsubscribes the given listener to all events that match the 
	 * given selector published by this BusEventPublisher.
	 * 
	 * @param selector the selector to unregister from.
	 * @param listener the listener to unregister.
	 */
	public void removeBusEventListener(EventSelector selector, BusEventListener listener);
	
}

//--- Development History ----------------------------------------------------
//$Log: SelectableBusEventPublisher.java,v $
//Revision 1.1  2006/04/18 03:51:18  tames
//Relocated or new implementation.
//
//Revision 1.1  2006/04/05 18:46:35  tames
//Refactoring and implementation of a Publish/Subscribe pattern for EventObjects.
//
//
