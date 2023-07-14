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

package gov.nasa.gsfc.commons.types.queues;

import java.util.Iterator;
import java.util.LinkedList;


/**
 *  This class implements a simple blocking FIFO Queue. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/10/21 20:18:43 $
 *  @author Carl F. Hostetter
**/


public class FifoQueue implements Queue 
{
	private LinkedList fList = new LinkedList();

	private boolean fUnblocked = false;
	
	
	/**
	 * Attempts to add the given Object to this Queue. If this Queue is bounded and 
	 * if there is in insufficient space in this Queue to accommodate the new Object, 
	 * this method will return false.
	 *
	 * @param object The Object to add to this Queue
	 * @return False if the add attempt failed
	**/
	
	public synchronized boolean add(Object object)
	{
		boolean result = true;
		
		fList.add(object);
		
		if (size() == 1)
		{
			notifyAll();
		}
		
		return (result);
	}
	

	/**
	 * Adds the given Object to this Queue. If this Queue is bounded and if there is 
	 * insufficient space in this Queue to accommodate the new Object, this method 
	 * will block until sufficient space becomes available, and then add the given 
	 * Object.
	 *
	 * @param object The Object to add to this Queue
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public synchronized void blockingAdd(Object object)
		throws InterruptedException
	{
		add(object);
	}
	

	/**
	 * Returns the next available Object on this Queue, or null if no Object is 
	 * available.
	 * 
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	**/
	
	public synchronized Object get()
	{
		Object result = null;
		
		if (! fList.isEmpty())
		{
			result = fList.getFirst();	
		}
		
		return (result);
	}
	

	/**
	 * Returns the last available Object in this Queue. If this Queue is currently 
	 * empty, the result will be null.
	 * 
	 * @return The last available Object in this Queue
	**/
	
	public synchronized Object getLast()
	{
		Object result = null;
		
		if (! fList.isEmpty())
		{
			result = fList.getLast();	
		}
		
		return (result);
	}
	

	/**
	 * Returns the next available Object on this Queue. If there is no Object 
	 * available, then this method blocks until an Object becomes available.
	 * 
	 * @return The next available Object on this Queue
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public synchronized Object blockingGet()
		throws InterruptedException
	{
		Object result = null;
		
		while (isEmpty() && ! fUnblocked) // wait until not empty or unblocked
		{
			wait();
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
	 * Removes the next available Object on this Queue, and returns it. If no Object 
	 * is availble, returns null.
	 *
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	**/
	
	public synchronized Object remove()
	{
		Object result = null;
		
		if (! fList.isEmpty())
		{
			result = fList.removeFirst();	
		}
		
		return (result);
	}
	

	/**
	 * Removes the current last Object from this Queue, and returns it. If 
	 * this Queue is currently empty, the result will be null.
	 *
	 * @return The current first Object in this Queue
	**/
	
	public synchronized Object removeLast()
	{
		Object result = null;
		
		if (! fList.isEmpty())
		{
			result = fList.removeLast();	
		}
		
		return (result);
	}
	

	/**
	 * Removes the next available Object on this Queue, and returns it. If no Object 
	 * is availble, then this method blocks until an Object becomes available.
	 *
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public synchronized Object blockingRemove()
		throws InterruptedException
	{
		Object result = null;
		
		while (isEmpty() && ! fUnblocked)
		{
			wait();
		}
		
		if (fUnblocked)
		{
			fUnblocked = false;
		}
		else
		{
			result = remove();
		}
			
		if (size() == 0)
		{
			notifyAll();
		}
		
		return (result);
	}
	
	
	/**
	 * Returns the number of Objects currently in this Queue. 
	 * 
	 * @return The number of Objects currently in this Queue 
	**/
	
	public int size()
	{
		return (fList.size());
	}
	

	/**
	 * Returns true if this Queue is currently empty, false otherwise. 
	 * 
	 * @return True if this Queue is currently empty, false otherwise 
	**/
	
	public boolean isEmpty()
	{
		return (fList.isEmpty());
	}
	
	
	/**
	 * If this Queue is not currently empty, then this method blocks until this 
	 * Queue becomes empty.
	 *
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public synchronized void blockUntilEmpty()
		throws InterruptedException
	{
		while (! isEmpty()  && ! fUnblocked) // wait until empty or unblocked
		{
			wait();
		}
		
		fUnblocked = false;
	}
	
	
	/**
	 * Clears all Objects from this Queue.
	 *
	**/
	
	public synchronized void clear()
	{
		fList.clear();
	}
	

	/**
	 * Unblocks any blocked methods of this Queue.
	 *
	**/
	
	public synchronized void unblock()
	{
		fUnblocked = true;
		
		notifyAll();
	}
	

	/**
	 * Returns a String representation of this Queue.
	 * 
	 * @return A String representation of this Queue
	**/
	
	public synchronized String toString()
	{
		StringBuffer stringRep = new StringBuffer();
		
		if (! fList.isEmpty())
		{
			Iterator contents = fList.iterator();
			
			while (contents.hasNext())
			{
				stringRep.append(contents.next() + "\n");
			}
		}
		else
		{
			stringRep.append("Empty");
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: FifoQueue.java,v $
//  Revision 1.8  2005/10/21 20:18:43  tames_cvs
//  Fixed synchronization problems. Some methods that should be synchronized
//  were not.
//
//   1	IRC	   1.0		 9/13/2001 6:11:20 PM John Higinbotham 
//  
