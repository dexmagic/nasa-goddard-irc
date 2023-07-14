//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Queue.java,v $
//  Revision 1.3  2005/03/01 23:25:15  chostetter_cvs
//  Revised Queue design and package for better blocking behavior
//
//  Revision 1.2  2004/07/06 21:53:50  chostetter_cvs
//  Queue is now an interface
//
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


/**
 *  A Queue is a simple, ordered collection of available Objects.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/03/01 23:25:15 $
 *  @author Carl F. Hostetter
**/


public interface Queue 
{
	/**
	 * Attempts to add the given Object to this Queue. If this Queue is bounded and 
	 * if there is in insufficient space in this Queue to accommodate the new Object, 
	 * this method will return false.
	 *
	 * @param object The Object to add to this Queue
	 * @return False if the add attempt failed
	**/
	
	public boolean add(Object object);
	

	/**
	 * Adds the given Object to this Queue. If this Queue is bounded and if there is 
	 * insufficient space in this Queue to accommodate the new Object, this method 
	 * will block until sufficient space becomes available, and then add the given 
	 * Object.
	 *
	 * @param object The Object to add to this Queue
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public void blockingAdd(Object object)
		throws InterruptedException;
	

	/**
	 * Returns the next available Object on this Queue, or null if no Object is 
	 * available.
	 * 
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	**/
	
	public Object get();
	

	/**
	 * Returns the next available Object on this Queue. If there is no Object 
	 * available, then this method blocks until an Object becomes available.
	 * 
	 * @return The next available Object on this Queue
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public Object blockingGet() 
		throws InterruptedException;
	

	/**
	 * Removes the next available Object on this Queue, and returns it. If no Object 
	 * is availble, returns null.
	 *
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	**/
	
	public Object remove();
	

	/**
	 * Removes the next available Object on this Queue, and returns it. If no Object 
	 * is availble, then this methodl blocks until an Object becomes available.
	 *
	 * @return The next available Object on this Queue, or null if no Object is 
	 * 		available
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public Object blockingRemove()
		throws InterruptedException;
	

	/**
	 * Returns the number of Objects currently in this Queue. 
	 * 
	 * @return The number of Objects currently in this Queue 
	**/
	
	public int size();
	

	/**
	 * Returns true if this Queue is currently empty, false otherwise. 
	 * 
	 * @return True if this Queue is currently empty, false otherwise 
	**/
	
	public boolean isEmpty();
	

	/**
	 * If this Queue is not currently empty, then this method blocks until this 
	 * Queue becomes empty.
	 *
	 * @throws InterruptedException if this method blocks and then is interrupted
	**/
	
	public void blockUntilEmpty()
		throws InterruptedException;
	
	
	/**
	 * Clears all Objects from this Queue.
	 *
	**/
	
	public void clear();
	
	
	/**
	 * Unblocks any blocked methods of this Queue.
	 *
	**/
	
	public void unblock();
}
