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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*

*/

import gov.nasa.gsfc.commons.publishing.selectors.AllEventSelector;
import gov.nasa.gsfc.commons.publishing.selectors.EventSelector;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;

/**
 * Implements the Publish/Subscribe pattern for an <code>EventBus</code> of
 * arbitrary event types. This implementation manages a thread for publishing
 * the event to listeners. Listeners will not receive events that they are
 * the source of (via the <code>getSource</code> method on the event). Listeners 
 * that are registered with more than one selector may receive the event 
 * multiple times if more than one selector matches the event.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 03:56:11 $
 * @author 	Troy Ames
 */
public class ThreadedEventBus implements EventBus
{
	private static final String CLASS_NAME = ThreadedEventBus.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final EventSelector sDefaultSelector = 
		AllEventSelector.getInstance();
	
	// Holder of events to publish
	private Queue fQueue = new FifoQueue();
	private Thread fPublisherThread = null;
	private Map fListenerMap = new HashMap();

	/**
	 * Default constructor.
	 */
	public ThreadedEventBus()
	{
		fPublisherThread = new Thread(new EventPublisher(), CLASS_NAME);
		fPublisherThread.start();
	}

	/**
	 * Publishes the given event on the bus.
	 * 
	 * @param event the event to publish
	 */
	public void publish(EventObject event)
	{
		if (event != null)
		{
			fQueue.add(event);
		}
	}

	/**
	 * Publishes the given event on the bus and does not return until all
	 * interested subscribers have received the event.
	 * 
	 * @param event the event to publish
	 * @throws InterruptedException if another thread has interrupted the
	 *             waiting thread
	 */
	public void publishAndWait(EventObject event) throws InterruptedException
	{
		if (event != null)
		{
			synchronized (event)
			{
				fQueue.add(event);
				event.wait();
			}
		}
	}

	/**
	 * Subscribes the given listener to all events that match the 
	 * given selector published by this EventBus. If the selector is null 
	 * the listener will be registered for all events.
	 * 
	 * @param selector the selector to use for this listener or null.
	 * @param listener the listener to register.
	 */
	public synchronized void addBusEventListener(EventSelector selector, BusEventListener listener)
	{
		if (listener != null)
		{
			if (selector == null)
			{
				selector = sDefaultSelector;
			}
			
			EventPublisherSupport publisher = 
				(EventPublisherSupport)fListenerMap.get(selector);
			
			if (publisher == null) 
			{
				publisher = new EventPublisherSupport();
				fListenerMap.put(selector, publisher);
			}
			
			publisher.addBusEventListener(listener);
		}
	}

	/**
	 * Subscribes the given listener to all events published by this
	 * EventBus.
	 * 
	 * @param listener the listener to register.
	 */
	public synchronized void addBusEventListener(BusEventListener listener)
	{
		addBusEventListener(sDefaultSelector, listener);
	}
	
	/**
	 * Unsubscribes the given listener to all events that match the 
	 * given selector published by this BusEventPublisher.
	 * 
	 * @param selector the selector to unsubscribe from.
	 * @param listener the listener to unregister.
	 */
	public synchronized void removeBusEventListener(
			EventSelector selector, BusEventListener listener)
	{
		EventPublisherSupport publisher = 
			(EventPublisherSupport) fListenerMap.get(selector);
		
		if (publisher != null)
		{
			publisher.removeBusEventListener(listener);
		}
	}
	
	/**
	 * Unsubscribes the given listener to all events published by this
	 * BusEventPublisher.
	 * 
	 * @param listener the listener to unregister.
	 */
	public synchronized void removeBusEventListener(BusEventListener listener)
	{
		Iterator selectors = fListenerMap.keySet().iterator();
		
		while(selectors.hasNext())
		{
			EventPublisherSupport publisher = 
				(EventPublisherSupport) fListenerMap.get(selectors.next());

			if (publisher != null)
			{
				publisher.removeBusEventListener(listener);
				
				if (publisher.getSize() == 0)
				{
					// Since there is not any listeners for this selector,
					// remove it from the map.
					selectors.remove();
				}
			}
		}
	}
	
	/**
	 * Cause this BusEventListener to receive the given published event.
	 * 
	 * @param event A published EventObject
	 */
	public void receiveBusEvent(EventObject event)
	{
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = "Received event: " + event;
			
			sLogger.logp(Level.FINE, CLASS_NAME, "receiveBusEvent", message);
		}

		publish(event);
	}

	/**
	 * Publishes the given event on the bus.
	 * 
	 * @param event the event to publish
	 */
	private void publish0(EventObject event)
	{
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = "Publishing event: " + event;
			
			sLogger.logp(Level.FINE, CLASS_NAME, 
				"publish", message);
		}
		
		Object [] selectors = null;
		synchronized(this)
		{
			selectors = fListenerMap.keySet().toArray();
		}
		
		// Check all selectors to find matches and listeners
		for (int i=0; i < selectors.length; i++)
		{
			if (((EventSelector) selectors[i]).matches(event))
			{
				// The selector matches so publish to all registered listeners
				EventPublisherSupport publisher = 
					(EventPublisherSupport) fListenerMap.get(selectors[i]);

				if (publisher != null)
				{
					publisher.publish(event);
				}
			}
		}
		
		// Notify any waiting threads that the event has been published
		synchronized (event)
		{
			event.notifyAll();
		}
	}

	/**
	 * BusEventPublisher class waits for an event to be added to a queue and
	 * publishes it.
	 */
	private class EventPublisher implements Runnable
	{
		/**
		 * Loops indefinitely publishing events. Events are published in
		 * the order that they were added.
		**/
		public void run()
		{
			try
			{
				while(true)
				{
					EventObject event = (EventObject) fQueue.blockingRemove();
					publish0(event);
				}
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"run", "Run interrupted", e);
			}
		}
	}

	/**
	 * EventPublisherSupport class waits for an event to be added to a queue and
	 * publishes it.
	 */
	private static class EventPublisherSupport 
	{
		private List fListeners = new CopyOnWriteArrayList();

		/**
		 * Subscribes the given listener to all events that match the 
		 * given selector published by this BusEventPublisher.
		 * 
		 * @param listener the listener to register.
		 * @param listener the selector to use for this listener.
		 */
		public void addBusEventListener(BusEventListener listener)
		{
			fListeners.add(listener);
		}

		/**
		 * Unsubscribes the given listener to all events published by this
		 * BusEventPublisher.
		 * 
		 * @param listener the listener to unregister.
		 */
		public void removeBusEventListener(BusEventListener listener)
		{
			fListeners.remove(listener);
		}

		/**
		 * Publishes the given event on the bus.
		 * 
		 * @param event the event to publish
		 */
		public void publish(EventObject event)
		{
			for (Iterator iter = fListeners.iterator(); iter.hasNext();)
			{
				BusEventListener listener = (BusEventListener) iter.next();
				
				// Do not send the event to the event source
				if (event.getSource() != listener)
				{
					listener.receiveBusEvent(event);
				}
			}
		}

		/**
		 * Get the number of listeners.
		 * 
		 * @return the number of listeners
		 */
		public int getSize()
		{
			return fListeners.size();
		}
		
		/**
		 * Returns the set of listeners.
		 * 
		 * @return an array of registered listeners
		 */
		public BusEventListener[] getBusEventListeners()
		{
			return (BusEventListener[])(
					fListeners.toArray(new BusEventListener[fListeners.size()]));
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ThreadedEventBus.java,v $
//  Revision 1.2  2006/04/18 03:56:11  tames
//  Changed to implement receiveBusEvent.
//
//  Revision 1.1  2006/04/05 18:46:35  tames
//  Refactoring and implementation of a Publish/Subscribe pattern for EventObjects.
//
//