//=== File Prolog ============================================================
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;

/**
 * This DefaultTaskManager manages a sequence of 
 * {@link java.lang.Runnable Runnables} and 
 * {@link gov.nasa.gsfc.commons.processing.tasks.Task Tasks}.
 * Tasks and runnables are given to the manager for later processing by the
 * <code>invokeLater</code> or the <code>invokeAndWait</code> method. 
 * Tasks are started or run be a single thread in the
 * same order they were received.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/04/16 03:57:56 $
 * @author	Troy Ames
**/
public class DefaultTaskManager implements TaskManager
{
	private static final String CLASS_NAME = 
		DefaultTaskManager.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	// Holder of tasks to run
	private Queue fQueue = new FifoQueue();
	private Thread fTaskRunnerThread = null;

	/**
	 * Constructs and starts a DefaultTaskManager.
	**/
	public DefaultTaskManager()
	{
		fTaskRunnerThread = new Thread(new TaskRunner());
		fTaskRunnerThread.start();
	}

	/**
	 * Causes a task to be run asynchronously by this task manager. The task
	 * will be run after all pending tasks are processed.
	 *
	 * @param task	the Runnable task to run
	**/
	public void invokeLater(Runnable task)
	{
		fQueue.add(task);
	}

	/**
	 * Causes a task to be started asynchronously by this task manager. The task
	 * will be started in the order it was received.
	 *
	 * @param task	the Startable task to run
	 **/
	public void invokeLater(Task task)
	{
		fQueue.add(task);
	}

	/**
	 * Causes a task to be run synchronously by this task manager. The task
	 * will be run in the order it was received. The call blocks until
	 * the task has completed.
	 *
	 * @param task	the Runnable task to run
	 * @exception	InterruptedException  if another thread has
	 *				interrupted this thread
	 */
	public void invokeAndWait(Runnable task) throws InterruptedException
	{
		Runnable synchronizedTask = new RunnableInvocationTask(task);

		synchronized (synchronizedTask)
		{
			fQueue.add(synchronizedTask);
			synchronizedTask.wait();
		}
	}

	/**
	 * Causes a task to be started synchronously by this task manager. The task
	 * will be started in the order it was received. The call blocks 
	 * until the task has completed.
	 *
	 * @param task	the Startable task to run
	 * @exception	InterruptedException  if another thread has
	 *				interrupted this thread
	 */
	public void invokeAndWait(Task task) throws InterruptedException
	{
		Startable synchronizedTask = new InvocationTask(task);

		synchronized (synchronizedTask)
		{
			fQueue.add(synchronizedTask);
			synchronizedTask.wait();
		}
	}

	/**
	 * TaskRunner class waits for a task to be added to a queue and runs it.
	**/
	private class TaskRunner implements Runnable
	{
		/**
		 * Loops indefinitely running or starting tasks. Tasks are run or 
		 * startedin the order that they were added with the 
		 * <code>invoke</code> methods.
		 *
		 * @see DefaultTaskManager#invokeLater(Runnable)
		 * @see DefaultTaskManager#invokeLater(Task)
		 * @see DefaultTaskManager#invokeAndWait(Runnable)
		 * @see DefaultTaskManager#invokeAndWait(Task)
		**/
		public void run()
		{
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Starting...";
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"run", message);
			}

			try
			{
				while(true)
				{
					Object task = fQueue.blockingRemove();
					
					if (task instanceof Runnable)
					{
						if (sLogger.isLoggable(Level.FINE))
						{
							String message = "Running task: " + task;
							
							sLogger.logp(Level.FINE, CLASS_NAME, 
								"run", message);
						}
						
						((Runnable) task).run();
					}
					else if (task instanceof Startable)
					{
						if (sLogger.isLoggable(Level.FINE))
						{
							String message = "Starting task: " + task;
							
							sLogger.logp(Level.FINE, CLASS_NAME, 
								"run", message);
						}
						
						((Startable) task).start();
					}
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
	 * InvocationTask class is a holder of a Task and some 
	 * additional context information for processing a task. Notifies all 
	 * waiting threads when the task has stopped.
	**/
	private static class InvocationTask implements Startable, PropertyChangeListener
	{
		Task fTask = null;

		/**
		 * Constructs a InvocationTask with a task and a lock object.
		 *
		 * @param task  a task to run
		**/
		public InvocationTask(Task task)
		{
			fTask = task;
		}

		/**
		 * Starts the enclosed task.
		**/
		public synchronized void start()
		{
			// Run the task
			fTask.start();
			fTask.addStateListener(this);
			
			// Check to see if the task is finished
			if (fTask.isFinished() || fTask.isKilled())
			{
				// Release all waiting threads on this task
				notifyAll();

				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "Task has finished: " + fTask;
					
					sLogger.logp(Level.FINE, CLASS_NAME, 
						"start", message);
				}
			}
		}

		/**
		 * Returns true if this Task is started, false otherwise. 
		 *
		 * @return True if this Task is started, false otherwise
		 */
		public boolean isStarted()
		{
			return fTask.isStarted();
		}
		
		/**
		 * Stops this instance.
		 */	
		public void stop()
		{
			fTask.stop();
		}
			
		/**
		 * Returns true if this Task is stopped, false otherwise.
		 *
		 * @return True if this Task is stopped, false otherwise
		 */	
		public boolean isStopped()
		{
			return fTask.isStopped();
		}
		
		/**
		 * Kills this instance. 
		 */
		public void kill()
		{
			fTask.kill();
		}

		/**
		 * Returns true if this Task has been killed, false otherwise. 
		 *
		 * @return True if this Task has been killed, false otherwise
		 */	
		public boolean isKilled()
		{
			return fTask.isKilled();
		}

		/**
	     * This method gets called when the tasks state changed.
	     * @param evt A PropertyChangeEvent object describing the event source 
	     *   	and the property that has changed.
	     */
		public void propertyChange(PropertyChangeEvent evt)
		{
			// Check to see if the task is done
			if (fTask.isFinished() || fTask.isKilled())
			{
				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "Task has finished: " + fTask;
					
					sLogger.logp(Level.FINE, CLASS_NAME, 
						"propertyChange", message);
				}

				synchronized(this)
				{
					// Release all waiting threads on this task
					notifyAll();
				}
			}
		}
		
		/**
		 * Return the String representation of the task.
		**/
		public String toString()
		{
			return fTask.toString();
		}
	}

	/**
	 * RunnableInvocationTask class is a holder of a Runnable task and some 
	 * additional context information for running a task.
	**/
	private static class RunnableInvocationTask implements Runnable
	{
		Runnable fTask = null;

		/**
		 * Constructs a InvocationTask with a Runnable task.
		 *
		 * @param task  a Runnable task to run
		**/
		public RunnableInvocationTask(Runnable task)
		{
			fTask = task;
		}

		/**
		 * Runs the enclosed task and notifies all waiting threads that the task
		 * has completed.
		**/
		public synchronized void run()
		{
			// Run the task
			fTask.run();
					
			// Release all waiting threads
			notifyAll();
		}

		/**
		 * Return the String representation of the task.
		**/
		public String toString()
		{
			return fTask.toString();
		}
	}
} // end DefaultTaskManager

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultTaskManager.java,v $
//	Revision 1.5  2005/04/16 03:57:56  tames
//	Changes to reflect activity package refactoring.
//	
//	Revision 1.4  2005/04/06 21:03:04  tames_cvs
//	Expanded TaskManager for a new "Task" type.
//	
//	Revision 1.3  2005/03/01 23:25:15  chostetter_cvs
//	Revised Queue design and package for better blocking behavior
//	
//	Revision 1.2  2005/02/25 22:27:55  tames_cvs
//	Added try/catch around the run method to handle interrupt exceptions.
//	
//	Revision 1.1  2005/01/09 05:40:21  tames
//	Updated to reflect the refactoring of the TaskManager classes.
//	
//	Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.4  2004/05/27 18:21:45  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.3  2004/05/12 21:55:40  chostetter_cvs
//	Further tweaks for new structure, design
//	
//	Revision 1.2  2004/05/04 14:59:54  tames_cvs
//	Changed name of Buffer interface to ObjectBuffer to avoid confusion 
//	with the Buffer defined in Java's NIO. This class was modified to reflect 
//	that change.
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
