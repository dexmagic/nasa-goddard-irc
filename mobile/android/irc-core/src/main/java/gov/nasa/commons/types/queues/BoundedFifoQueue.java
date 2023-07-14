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

import java.io.Serializable;

/**
 * A thread safe queue that blocks accessing threads on <code>blockingAdd</code>
 * if the queue is full and on <code>blockingRemove</code> when the queue is
 * empty. Alternatively a nonblocking <code>add</code> or <code>remove</code>
 * method can be used. BoundedFifoQueue itself is serializable but does not
 * enforce this restriction on the objects inserted in the queue. This class was
 * adapted from code presented in the book <a
 * href="http://gee.cs.oswego.edu/dl/cpj/index.html">Concurrent Programming in
 * Java: Design Principles and Patterns</a> by Doug Lea.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/10/21 20:17:37 $
 * @author Troy Ames
 */
public class BoundedFifoQueue extends Object implements Serializable, Queue
{
	protected Object[] fArray; // the elements
	protected int fTailPointer = 0; // circular indices
	protected int fHeadPointer = 0; // Head pointer
	protected int fCount = 0; // the # of elements contained

	private boolean fUnblocked = false;

	/**
	 * Creates a queue with the specified capacity.
	 *
	 * @param		capacity	the maximum capacity of the queue.
	 * @exception	IllegalArgumentException	capacity must be greater than 0
	 **/
	public BoundedFifoQueue(int capacity) throws IllegalArgumentException
	{
		if (capacity <= 0)
		{
			throw new IllegalArgumentException();
		}

		fArray = new Object[capacity];
	}

	/**
	 * Attempts to add the given Object to this Queue. If this Queue is bounded
	 * and if there is insufficient space in this Queue to accommodate the
	 * new Object, this method will return false.
	 * 
	 * @param object The Object to add to this Queue
	 * @return False if the add attempt failed
	 */
	public synchronized boolean add(Object object)
	{
		boolean result = false;

		if (fCount != fArray.length)
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
	 * this Queue to accommodate the new Object, this method will block until
	 * sufficient space becomes available, and then add the given Object.
	 * 
	 * @param object The Object to add to this Queue
	 * @throws InterruptedException if this method blocks and then is
	 *             interrupted
	 */
	public synchronized void blockingAdd(Object object) throws InterruptedException
	{
		while (fCount == fArray.length) // wait until not full
		{
			wait();
		}

		fArray[fTailPointer] = object;
		fTailPointer = (fTailPointer + 1) % fArray.length; // cyclically inc

		if (fCount == 0)
		{
			notifyAll();
		}

		fCount++;
	}

	/**
	 * Returns the next available Object on this Queue, or null if no Object is 
	 * available. Does not change the content of the Queue.
	 * 
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	 * @see #remove()
	 **/
	public synchronized Object get()
	{
		Object result = fArray[fHeadPointer];

		return (result);
	}

	/**
	 * Returns the next available Object on this Queue. If there is no Object 
	 * available, then this method blocks until an Object becomes available.
	 * Does not change the content of the Queue.
	 * 
	 * @return The next available Object on this Queue
	 * @throws InterruptedException if this method blocks and then is interrupted
	 * @see #blockingRemove()
	 **/
	public synchronized Object blockingGet() throws InterruptedException
	{
		Object result = null;

		while (size() == 0 && !fUnblocked) // wait until not empty or unblocked
		{
			try
			{
				wait();
			}
			catch (InterruptedException ex)
			{

			}
		}

		if (fUnblocked)
		{
			fUnblocked = false;
		}
		else
		{
			result = get();
		}

		return (result);
	}

	/**
	 * Removes the next available Object on this Queue, and returns it. If no
	 * Object is availble, returns null.
	 * 
	 * @return The next available Object on this Queue, or null if no Object is
	 *         available
	 */
	public synchronized Object remove()
	{
		Object result = null;

		if (fCount > 0)
		{
			try
			{
				result = blockingRemove();
			}
			catch (InterruptedException ex)
			{

			}
		}

		return (result);
	}

	/**
	 * Removes the next available Object on this Queue, and returns it. If no
	 * Object is availble, then this methodl blocks until an Object becomes
	 * available.
	 * 
	 * @return The next available Object on this Queue, or null if no Object is
	 *         available
	 * @throws InterruptedException if this method blocks and then is
	 *             interrupted
	 */
	public synchronized Object blockingRemove() throws InterruptedException
	{
		while (fCount == 0) // wait until not empty
		{
			wait();
		}

		Object x = fArray[fHeadPointer];
		fArray[fHeadPointer] = null;
		fHeadPointer = (fHeadPointer + 1) % fArray.length; // cyclically inc

		if (fCount == fArray.length)
		{
			notifyAll();
		}

		fCount--;

		return x;
	}

	/**
	 * Returns the number of Objects currently in this Queue. 
	 * 
	 * @return The number of Objects currently in this Queue 
	 **/
	public int size()
	{
		return (fCount);
	}

	/**
	 * Sets the maximum number of elements that this queue can contain. This 
	 * implementation allocates a new array of the given capacity and copies
	 * the current contents of the queue into the new array.
	 * 
	 * @param capacity the new capacity of the Queue
	 * @throws IllegalArgumentException if the specified capacity is less then
	 * the current <code>size</code> of the queue.
	 **/
	public synchronized void setCapacity(int capacity) throws IllegalArgumentException
	{
		if (capacity < fCount)
		{
			throw new IllegalArgumentException(
					"Specified capacity most be greater than or equal to the current count");
		}
		
		Object[] tempArray = new Object[capacity];
		int currentCount = fCount;
		
		for (int i=0; i < currentCount; i++)
		{
			tempArray[i] = remove();
		}
		
		// Update Queue state
		fCount = currentCount;
		fHeadPointer = 0;
		fTailPointer = currentCount % tempArray.length;
		fArray = tempArray;
	}

	/**
	 * Returns the maximum number of elements that this queue can contain.
	 *
	 * @return maximum capacity
	 **/
	public int getCapacity()
	{
		return (fArray.length);
	}

	/**
	 * Returns true if this Queue is currently empty, false otherwise. 
	 * 
	 * @return True if this Queue is currently empty, false otherwise 
	 **/
	public boolean isEmpty()
	{
		return (fCount == 0);
	}

	/**
	 * If this Queue is not currently empty, then this method blocks until this
	 * Queue becomes empty.
	 * 
	 * @throws InterruptedException if this method blocks and then is
	 *             interrupted
	 */
	public synchronized void blockUntilEmpty() throws InterruptedException
	{
		while (size() > 0 && !fUnblocked) // wait until empty or unblocked
		{
			wait();
		}

		fUnblocked = false;
	}

	/**
	 * Clears all Objects from this Queue.
	 **/
	public synchronized void clear()
	{
		fTailPointer = 0;
		fHeadPointer = 0;
		fCount = 0;

		notifyAll();
	}

	/**
	 * Unblocks any blocked methods of this Queue.
	 **/
	public synchronized void unblock()
	{
		fUnblocked = true;

		notifyAll();
	}
}

//--- Development History -----------------------------------------------------
//
// $Log: BoundedFifoQueue.java,v $
// Revision 1.2  2005/10/21 20:17:37  tames_cvs
// Corrected synchronization bugs and faulty get() method. Added a setCapacity() property.
//
//  6	IRC	   1.5		 11/12/2001 5:00:58 PMJohn Higinbotham Javadoc
//	   update.
//  5	IRC	   1.4		 9/28/1999 9:00:46 AM Craig Warsaw	AI_109: Make
//	   fields protected to support extension
//  4	IRC	   1.3		 7/27/1999 9:25:22 AM Ken Wootton	 This branch is
//	   from the Version 2.0 version of the tree to version 2.1 and beyond.
//  3	IRC	   1.2		 6/29/1999 11:49:28 AMKen Wootton	   The current
//	   VSS tree needs to be "branched" to allow for development of
//	   two simultaneous versions of the software, namely 2.0 (July release) and
//	   2.1
//	   (August release).  The idea is that there will be changes made for the
//	   July
//	   release that will not be placed in the August release, such as retrofited
//	   changes to meet performance.  Also, there needs to be a place where people
//	   can work on changes for the August release and not alter the software that
//	   will be release in July, such as the new GUI.  The soon to come branching
//	   will hopefully solve this problem by providing two separate development
//	   trees until the July release.  I assume that at that time some type of
//	   merging of the trees must occur.
// 
//  2	IRC	   1.1		 2/28/1999 8:27:44 PM Troy Ames	   Updated
//	   comments
//  1	IRC	   1.0		 2/28/1999 5:23:24 PM Troy Ames	   
// 
