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
 * when the queue is empty and discards the earliest objects inserted when the
 * queue is full. KeepLatestBoundedQueue itself is serializable but does not
 * enforce this restriction on the objects inserted in the queue.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/10/21 20:19:45 $
 * @author Troy Ames
 */
public class KeepLatestBoundedQueue extends BoundedFifoQueue
{
	/**
	 * Creates a queue with the specified capacity.
	 *
	 * @param		capacity	maximum capacity of the queue.
	 * @exception	IllegalArgumentException	capacity must be > 0
	**/
	public KeepLatestBoundedQueue(int capacity) throws IllegalArgumentException
	{
		super(capacity);
	}

	/**
	 * Adds the object x to the end of the queue.  Discards the earliest
	 * inserted object if the queue is full.
	 *
	 * @param	x	object to add
	 * @return true if this collection changed as a result of the call
	**/
	public synchronized boolean add(Object x)
	{
		fArray[fTailPointer] = x;
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
		return true;
	}

	/**
	 * Adds the object x to the end of the queue.  Identical to 
	 * <code>add</code> in this implementation.
	 * 
	 * @param object The Object to add to this Queue
	 * @see #add(Object)
	 */
	public synchronized void blockingAdd(Object object)
	{
		add(object);
	}
}

//--- Development History -----------------------------------------------------
//
// $Log: KeepLatestBoundedQueue.java,v $
// Revision 1.2  2005/10/21 20:19:45  tames_cvs
// Added missing blockingAdd() method.
//
//  2	IRC	   1.1		 11/13/2001 10:31:26 AMJohn Higinbotham Javadoc
//	   update.
//  1	IRC	   1.0		 9/28/1999 9:03:34 AM Craig Warsaw	
// 
