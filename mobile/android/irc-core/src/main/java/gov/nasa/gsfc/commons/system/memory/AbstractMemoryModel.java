//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/AbstractMemoryModel.java,v 1.10 2005/11/09 18:43:23 tames_cvs Exp $
//
// This code was developed by NASA Goddard Space Flight Center, 
// Code 588 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// *  Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
// *  The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
// *  Altered versions of this software must be plainly marked as such.
// *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.system.memory;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/*


*/


/**
 * A MemoryModel maintains statistics describing the state of some extent of memory 
 * and Allocations from within that memory. 
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/11/09 18:43:23 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */
public abstract class AbstractMemoryModel implements MemoryModel
{
	private static final String CLASS_NAME = 
		AbstractMemoryModel.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private List fMemoryModelListeners = new CopyOnWriteArrayList();
	private List fReleaseRequestListeners = new CopyOnWriteArrayList();

	protected int fSize = 0;

	/**
	 * Default constructor of a MemoryModel.
	 */
	public AbstractMemoryModel()
	{
		this(100);
	}

	/**
	 * Contructor for a MemoryModel of the given size.
	 * 
	 * @param size The size of the new MemoryModel
	 * @throws IllegalArgumentException if the given size is negative or zero
	 */
	public AbstractMemoryModel(int size)
	{
		if (size > 0)
		{
			fSize = size;
		}
		else
		{
			String message = "The given size cannot be negative or zero";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Adds the given MemoryModelListener as a listener for MemoryModelEvents
	 * from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public final void addMemoryModelListener(MemoryModelListener listener)
	{
		if (listener != null)
		{
			synchronized (fMemoryModelListeners)
			{
				if (!fMemoryModelListeners.contains(listener))
				{
					fMemoryModelListeners.add(listener);
				}
			}
		}
		else
		{
			String message = "The given MemoryModelListener cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Removes the given MemoryModelListener as a listener for MemoryModelEvents
	 * from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public final void removeMemoryModelListener(MemoryModelListener listener)
	{
		if (listener != null)
		{
			fMemoryModelListeners.remove(listener);
		}
		else
		{
			String message = "The given MemoryModelListener cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Returns true if this MemoryModel has any MemoryModelListeners (excluding
	 * DiscontinuityEvent or ReleaseRequestEvent listeners), false otherwise.
	 * 
	 * @return True if this MemoryModel has any MemoryModelListeners (excluding
	 *         DiscontinuityEvent or ReleaseRequestEvent listeners), false
	 *         otherwise
	 */
	public boolean hasMemoryModelListeners()
	{
		boolean result = false;

		result = (!fMemoryModelListeners.isEmpty());

		return (result);
	}

	/**
	 * Returns the Set of MemoryModelListeners on this MemoryModel as an array
	 * of MemoryModelListeners.
	 * 
	 * @return The Set of MemoryModelListeners on this MemoryModel as an array
	 *         of MemoryModelListeners
	 */
	public final MemoryModelListener[] getMemoryModelListeners()
	{
		return ((MemoryModelListener[]) 
				fMemoryModelListeners.toArray(
					new MemoryModelListener[fMemoryModelListeners.size()]));
	}

	/**
	 * Removes all MemoryModelListeners from this MemoryModel.
	 */
	protected final void removeMemoryModelListeners()
	{
		fMemoryModelListeners.clear();
	}

	/**
	 * Sends the given MemoryModelEvent to all the MemoryModelListeners of this
	 * MemoryModel.
	 * 
	 * @param event A MemoryModelEvent
	 */
	protected final void fireMemoryModelEvent(MemoryModelEvent event)
	{
		if (event != null)
		{
			for (Iterator iter = fMemoryModelListeners.iterator(); iter.hasNext();)
			{
				((MemoryModelListener) iter.next()).receiveMemoryModelEvent(event);
			}
		}
		else
		{
			String message = "The given MemoryModelEvent cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Adds the given MemoryModelListener as a listener for (only)
	 * ReleaseRequestEvents from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public final void addReleaseRequestListener(MemoryModelListener listener)
	{
		if (listener != null)
		{
			synchronized (fReleaseRequestListeners)
			{
				if (!fReleaseRequestListeners.contains(listener))
				{
					fReleaseRequestListeners.add(listener);
				}
			}
		}
		else
		{
			String message = "The given MemoryModelListener cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Removes the given MemoryModelListener as a listener for (only)
	 * ReleaseRequestEvents from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public final void removeReleaseRequestListener(MemoryModelListener listener)
	{
		if (listener != null)
		{
			fReleaseRequestListeners.remove(listener);
		}
		else
		{
			String message = "The given MemoryModelListener cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Returns true if this MemoryModel has any ReleaseRequestEvent listeners,
	 * false otherwise.
	 * 
	 * @return True if this MemoryModel has any ReleaseRequestEvent listeners,
	 *         false otherwise
	 */
	public boolean hasReleaseRequestListeners()
	{
		boolean result = false;

		result = (!fReleaseRequestListeners.isEmpty());

		return (result);
	}

	/**
	 * Returns the ReleaseRequestEvent listeners on this MemoryModel as
	 * an array of MemoryModelListeners.
	 * 
	 * @return The ReleaseRequestEvent listeners on this MemoryModel as
	 *         an array of MemoryModelListeners
	 */
	public final MemoryModelListener[] getReleaseRequestListeners()
	{
		return (MemoryModelListener[]) 
			fReleaseRequestListeners.toArray(
				new MemoryModelListener[fReleaseRequestListeners.size()]);
	}

	/**
	 * Sends the given ReleaseRequestEvent to all the ReleaseRequestEvent
	 * listeners of this MemoryModel.
	 * 
	 * @param event A ReleaseRequestEvent
	 */
	protected final void fireReleaseRequestEvent(ReleaseRequestEvent event)
	{
		if (event != null)
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Alerting listeners of release request";

				sLogger.logp(Level.FINE, CLASS_NAME,
					"fireReleaseRequestEvent", message);
			}

			for (Iterator iter = fReleaseRequestListeners.iterator(); 
					iter.hasNext();)
			{
				((MemoryModelListener) iter.next()).receiveMemoryModelEvent(event);
			}
		}
		else
		{
			String message = "The given ReleaseRequestEvent cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Alerts all listeners to this MemoryModel that it is requesting them to
	 * release any of its Allocations that they hold.
	 */
	protected final void reportReleaseRequest()
	{
		ReleaseRequestEvent event = new ReleaseRequestEvent(this);

		fireReleaseRequestEvent(event);
		fireMemoryModelEvent(event);
	}

	/**
	 * Returns true if this MemoryModel has any MemoryModelListeners (including
	 * ReleaseRequestEvent listeners), false otherwise.
	 * 
	 * @return True if this MemoryModel has any MemoryModelListeners (including
	 *         DiscontinuityEvent or ReleaseRequestEvent listeners), false
	 *         otherwise
	 */
	public boolean hasAnyListeners()
	{
		boolean result = false;

		result = (hasMemoryModelListeners() || hasReleaseRequestListeners());

		return (result);
	}

	/**
	 * Returns true if this MemoryModel is currently able to allocate the given
	 * amount, false otherwise.
	 * 
	 * @param amount The allocation amount requested
	 * @return True if this MemoryModel is currently able to allocate the given
	 *         amount, false otherwise
	 */
	public boolean isAvailable(int amount)
	{
		boolean result = false;

		if (amount > 0 && amount <= getAmountAvailable())
		{
			result = true;
		}

		return (result);
	}

	/**
	 * Returns the current amount of available memory in this MemoryModel.
	 * 
	 * @return The current amount of available memory in this MemoryModel
	 */
	public abstract int getAmountAvailable();

	/**
	 * Returns the total size of this BoundedMemoryModel.
	 * 
	 * @return The total size of this BoundedMemoryModel
	 */
	public final int getSize()
	{
		return (fSize);
	}

	/**
	 * Attempts to allocate the given amount from within this MemoryModel. If
	 * the requested amount is available (as determined by a call of
	 * <code>isAvailable(amount)</code>, an Allocation representing the
	 * allocated amount is returned. Otherwise, the result is null.
	 * <p>
	 * Here, simply checks to see if <code>isAvailable(amount)</code> is true,
	 * and then constructs and returns a new Allocation of the given amount.
	 * </p>
	 * 
	 * @param amount The desired amount to allocate
	 * @return An Allocation representing the allocated amount, or null if the
	 *         Allocation attempt failed
	 */
	public abstract Allocation allocate(int amount);

	/**
	 * Returns true if the given amount is a rational allocation amount for this
	 * MemoryModel, false otherwise. This implementation returns true if the
	 * amount is greater than 0 and less than the size of the MemoryModel.
	 * 
	 * @param The amount to be allocated.
	 * @return True if the given amount is a rational allocation amount for this
	 *         MemoryModel, false otherwise
	 */
	protected boolean isRationalAllocationAmount(int amount)
	{
		boolean result = false;
		
		if ((amount > 0) && (amount <= fSize))
		{
			result = true;
		}
		else
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = null;
				
				if (amount <= 0)
				{
					message = "The given allocation amount (" + amount + 
						") must be greater than 0";
				}
				else if (amount > fSize)
				{
					message = "The given allocation amount (" + amount + 
						") exceeds the total size (" + fSize + 
						") of this MemoryModel";
				}
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"isRationalAllocationAmount", message);
			}
		}
		
		return (result);
	}
	
	/**
	 * Attempts to allocate the given amount from within this MemoryModel,
	 * blocking until the request can be filled.
	 * <p>
	 * Here, simply waits until <code>isAvailable(amount)</code> returns true,
	 * and then calls <code>allocate(amount)</code>.
	 * </p>
	 * 
	 * @param amount The desired amount to allocate
	 * @return An Allocation representing the allocated amount
	 * @throws InterruptedException if the call blocks but is interrupted before
	 *             the allocation can be filled
	 */
	public synchronized final Allocation blockingAllocate(int amount)
			throws InterruptedException
	{
		Allocation result = null;

		result = allocate(amount);

		if (result == null)
		{
			// If our first allocation request fails, then we ask our listeners
			// to release any Allocations they hold and try again.

			reportReleaseRequest();

			result = allocate(amount);

			if (result == null)
			{
				// If at this point we still haven't found sufficient space,
				// then we block until more memory becomes available, and
				// then try yet again.

				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "Attempted allocation of size " + amount
							+ " is blocking...\n" + this;

					sLogger.logp(Level.WARNING, CLASS_NAME, "blockingAllocate",
						message);
				}

				while (result == null)
				{
					wait();

					result = allocate(amount);
				}

				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "Allocation of size " + amount
							+ " proceeding...";

					sLogger.logp(Level.FINE, CLASS_NAME, "blockingAllocate",
						message);
				}
			}
		}

		return (result);
	}

	/**
	 * Immediately releases the given Allocation from this MemoryModel
	 * (regardless of the value of its reference count).
	 * <p>
	 * The operation results in a ReleasedEvent being sent to all current
	 * MemoryModelListeners.
	 * </p>
	 * 
	 * @param allocation An Allocation from this MemoryModel
	 * @throws IllegalArgumentException if the given Allocation was not made on
	 *             this MemoryModel
	 */
	public abstract void release(Allocation allocation);

	/**
	 * Returns a String representation of the listeners on this MemoryModel.
	 * 
	 * @return A String representation of the listeners on this MemoryModel
	 */
	public String listenersToString()
	{
		StringBuffer result = new StringBuffer();

		Iterator listeners = fMemoryModelListeners.iterator();

		for (int i = 1; listeners.hasNext(); i++)
		{
			MemoryModelListener listener = (MemoryModelListener) listeners
					.next();

			result.append("\nMemoryMdodel listener " + i + ": " + listener);
		}

		listeners = fReleaseRequestListeners.iterator();

		for (int i = 1; listeners.hasNext(); i++)
		{
			MemoryModelListener listener = (MemoryModelListener) listeners
					.next();

			result.append("\nReleaseRequest listener " + i + ": " + listener);
		}

		return (result.toString());
	}

	/**
	 * Returns a String representation of this MemoryModel.
	 * 
	 * @return A String representation of this MemoryModel
	 */
	public String toString()
	{
		StringBuffer result = new StringBuffer("MemoryModel "
				+ super.toString());

		result.append(" of size: " + fSize);

		return (result.toString());
	}
}

//--- Development History ---------------------------------------------------
//
// $Log: AbstractMemoryModel.java,v $
// Revision 1.10  2005/11/09 18:43:23  tames_cvs
// Modified event publishing to use the CopyOnWriteArrayList class to
// hold listeners. This reduces the overhead when publishing events.
//
// Revision 1.9  2005/10/28 18:38:24  tames
// Fixed class cast exception in get listeners methods.
//
// Revision 1.8  2005/08/17 18:42:56  chostetter_cvs
// Fixed typo in message
//
// Revision 1.7  2005/07/12 17:05:47  tames
// Refactored memory package to improve performance and to
// further encapsulate the MemoryModel from users.
//
// Revision 1.6 2005/04/13 19:01:21 chostetter_cvs
// Fixed incomplete search for next available allocation position
//
// Revision 1.5 2005/04/05 20:35:36 chostetter_cvs
// Fixed problem with release status of BasisSets from which a copy was made;
// fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//
// Revision 1.4  2005/04/05 18:52:23  chostetter_cvs
// Fixed bug in case of split and release of just one element from a larger Allocation
//
// Revision 1.3  2005/04/04 22:35:39  chostetter_cvs
// Adjustments to synchronization scheme
//
// Revision 1.2  2005/04/04 16:09:41  chostetter_cvs
// Adjusted blocking behavior
//
// Revision 1.1  2005/04/04 15:40:58  chostetter_cvs
// Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
