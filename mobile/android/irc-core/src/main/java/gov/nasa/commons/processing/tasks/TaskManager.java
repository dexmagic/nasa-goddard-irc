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

package gov.nasa.gsfc.commons.processing.tasks;


/**
 * A TaskManager manages a sequence of {@link java.lang.Runnable Runnable} 
 * and {@link gov.nasa.gsfc.commons.processing.activity.Startable Startable} tasks.
 * Tasks are given to the manager for later processing by the
 * <code>invokeLater</code> methods or while the caller waits with the 
 * <code>invokeAndWait</code> methods. Tasks are processed (the run or start 
 * method is called) in the same order they were received. Note that while 
 * tasks are processed in the order they were received a TaskManager 
 * implementation may process them in parallel. The only way to assure that 
 * one task is processed and completed before another task is processed is 
 * for the caller to use the <code>invokeAndWait</code> methods giving the
 * tasks in sequence to the TaskManager.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/04/06 21:03:04 $
 * @author	Troy Ames
**/
public interface TaskManager
{
	/**
	 * Causes a task to be run asynchronously by this task manager. The task
	 * will be run in the order it was received.
	 *
	 * @param task	the Runnable task to run
	 **/
	public void invokeLater(Runnable task);

	/**
	 * Causes a task to be run synchronously by this task manager. The task
	 * will be run in the order it was received. The call blocks until
	 * the task has completed.
	 *
	 * @param task	the Runnable task to run
	 * @exception	InterruptedException  if another thread has
	 *				interrupted this thread
	 */
	public void invokeAndWait(Runnable task) throws InterruptedException;

	/**
	 * Causes a task to be started asynchronously by this task manager. The task
	 * will be started in the order it was received.
	 *
	 * @param task	the Startable task to run
	 **/
	public void invokeLater(Task task);

	/**
	 * Causes a task to be started synchronously by this task manager. The task
	 * will be started in the order it was received. The call blocks 
	 * until the task has completed (set to the killed state).
	 *
	 * @param task	the Startable task to run
	 * @exception	InterruptedException  if another thread has
	 *				interrupted this thread
	 */
	public void invokeAndWait(Task task) throws InterruptedException;
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TaskManager.java,v $
//  Revision 1.2  2005/04/06 21:03:04  tames_cvs
//  Expanded TaskManager for a new "Task" type.
//
//  Revision 1.1  2005/01/09 05:40:21  tames
//  Updated to reflect the refactoring of the TaskManager classes.
//
//