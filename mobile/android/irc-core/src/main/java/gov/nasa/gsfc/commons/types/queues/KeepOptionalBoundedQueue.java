//=== File Prolog =============================================================
//	This code was developed for NASA, Goddard Space Flight Center, Code 588
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes -------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning -----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration.  Unauthorized use or duplication of this software is
//	strictly prohibited.  Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog =========================================================

package gov.nasa.gsfc.commons.types.queues;


/**
 * A thread safe queue that blocks accessing threads on
 * {@link gov.nasa.gsfc.commons.types.queues.BoundedFifoQueue#blockingRemove() blockingRemove}
 * when the queue is empty and optionally discards objects or blocks when
 * inserting objects with the {@link #blockingAdd(Object)} method.
 * <p>
 * If the Queue is full the result of the <code>blockingAdd</code> and
 * <code>{@link #add(Object)}</code> methods are as follows. If the result of
 * {@link #isKeepAll()} is <code>true</code> the <code>add</code> method
 * will return <code>false</code> and the <code>blockingAdd</code> method
 * will block until space is available in the Queue. If the result of
 * {@link #isKeepLatest()} is <code>true</code> the <code>add</code> method
 * will return true and the <code>blockingAdd</code> method will not block.
 * Both methods will add the given Object and discard the earliest or oldest
 * Object on the Queue. If the result of {@link #isKeepEarliest()} is
 * <code>true</code> both the <code>add</code> and <code>blockingAdd</code>
 * methods will not change the contents of the queue effectively discarding the
 * latest Object.
 * <p>
 * KeepOptionalBoundedQueue itself is serializable but does not enforce this
 * restriction on the objects inserted in the queue.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/10/21 20:19:57 $
 * @author Troy Ames
 */
public class KeepOptionalBoundedQueue extends BoundedFifoQueue
{
	private static final int KEEP_ALL = 0;
	private static final int KEEP_LATEST = 1;
	private static final int KEEP_EARLIEST = 2;
	
	private int fKeepMode = KEEP_ALL;
	
	/**
	 * Creates a queue with the specified capacity.
	 *
	 * @param		capacity	maximum capacity of the queue.
	 * @exception	IllegalArgumentException	capacity must be > 0
	**/
	public KeepOptionalBoundedQueue(int capacity) throws IllegalArgumentException
	{
		super(capacity);
	}

	/**
	 * Attempts to add the given Object to this Queue. If the 
	 * keep mode of the Queue is keep all or keep earliest
	 * and there is insufficient space in this Queue to accommodate the
	 * new Object, this method will return false. Otherwise the object will
	 * be added potentially discarding the earliest object on the Queue. 
	 * 
	 * @param object The Object to add to this Queue
	 * @return False if the add attempt failed
	 */
	public synchronized boolean add(Object object)
	{
		boolean result = false;

		if (fCount != fArray.length || fKeepMode == KEEP_LATEST)
		{
			try
			{
				blockingAdd(object);

				result = true;
			}
			catch (InterruptedException ex)
			{

			}
		}

		return (result);
	}

	/**
	 * Adds the given Object to this Queue. If there is insufficient space in
	 * this Queue to accommodate the new Object and the keep mode is keep all,
	 * this method will block until sufficient space becomes available, and then
	 * add the given Object. If the keep mode is keep latest the method will
	 * discard the earliest object on the Queue and add the given object. If the
	 * keep mode is keep earliest then this method discards the given Object,
	 * the Queue is not changed and this method does not block.
	 * 
	 * @param object The Object to add to this Queue
	 * @throws InterruptedException if this method blocks and then is
	 *             interrupted
	 */
	public synchronized void blockingAdd(Object object) throws InterruptedException
	{
		if (fKeepMode == KEEP_ALL || fCount != fArray.length)
		{
			// The mode is KEEP ALL or there is room in the Queue for the object 
			super.blockingAdd(object);
		}
		else if (fKeepMode == KEEP_LATEST)
		{
			fArray[fTailPointer] = object;
			fTailPointer = (fTailPointer + 1) % fArray.length; // cyclically inc
			if (fCount == fArray.length)
			{
				// array was full, so forget the earliest object inserted
				fHeadPointer = (fHeadPointer + 1) % fArray.length;
			}
			else
			{
				if (fCount == 0)
				{
					notifyAll();
				}
	
				fCount++;
			}
		}
		else
		{
			// The keep mode is == KEEP_EARLIEST and the Queue is full so 
			// discard the object and do nothing here.
		}
	}

	/**
	 * Returns true if the current keep mode is to keep all the contents of the 
	 * Queue.
	 * 
	 * @return Returns true if the keep mode is keep all.
	 */
	public boolean isKeepAll()
	{
		return (fKeepMode == KEEP_ALL);
	}

	/**
	 * Sets the keep mode for the Queue to keep all.
	 * 
	 * @see #blockingAdd(Object)
	 * @see #add(Object)
	 */
	public synchronized void setKeepAll()
	{
		fKeepMode = KEEP_ALL;
	}

	/**
	 * Returns true if the current keep mode is to keep the latest objects
	 * added to the Queue if full and discarding the earliest if needed.
	 * 
	 * @return Returns true if the keep mode is keep latest.
	 */
	public boolean isKeepLatest()
	{
		return (fKeepMode == KEEP_LATEST);
	}

	/**
	 * Sets the keep mode for the Queue to keep latest.
	 * 
	 * @see #blockingAdd(Object)
	 * @see #add(Object)
	 */
	public synchronized void setKeepLatest()
	{
		fKeepMode = KEEP_LATEST;
	}

	/**
	 * Returns true if the current keep mode is to keep the earliest objects
	 * added to the Queue if full and discarding the latest if needed.
	 * 
	 * @return Returns true if the keep mode is keep earliest.
	 */
	public boolean isKeepEarliest()
	{
		return (fKeepMode == KEEP_EARLIEST);
	}

	/**
	 * Sets the keep mode for the Queue to keep earliest.
	 * 
	 * @see #blockingAdd(Object)
	 * @see #add(Object)
	 */
	public synchronized void setKeepEarliest()
	{
		fKeepMode = KEEP_EARLIEST;
	}
}

//--- Development History -----------------------------------------------------
//
// $Log: KeepOptionalBoundedQueue.java,v $
// Revision 1.1  2005/10/21 20:19:57  tames_cvs
// Initial version.
//
// 
