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

package gov.nasa.gsfc.irc.devices.ports.connections;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.queues.KeepOptionalBoundedQueue;
import gov.nasa.gsfc.irc.description.Descriptor;

/**
 * A common pattern for Connection components is to have two dedicated Threads,
 * one to read from the Connection(s) and another to write data to the
 * Connection(s). This abstract class manages a Thread for writing data. The
 * Thread for reading data is managed by the super class
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection AbstractThreadedConnection}
 * <p>
 * Typically subclasses will only need to implement the
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection#process(ByteBuffer) process(ByteBuffer)}
 * method to handle data going out of the connection. 
 * 
 * <p>
 * Subclasses will still need to implement the inherited abstract
 * {@link #openConnection()}, {@link #closeConnection()}, and
 * {@link #serviceConnection()} methods to read from a connection as well as
 * publish the events described in the
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection 
 * AbstractConnection} super class.
 * 
 * <P>The configuration of this connection can be specified by a ConnectionDescriptor.
 * The table below gives the configuration parameters that this connection uses.
 * If the parameter is missing then the default value will be used. If there
 * is not a default value specified then the parameter is required to be
 * in the descriptor except where noted. 
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Key</th>
 *      <th>Default</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>queueKeepMode</td><td>keepAll</td>
 *      <td align="left">The queue mode for OutputBufferEvents. The options are
 *       "keepAll" which will queue and potentially block the publisher of 
 *       OutputBufferEvents when the queue capacity is reached, "keepLatest" which
 *       will start discarding the events from the head of the queue when the 
 *       capacity is reached, and "keepEarliest" which will discard new events
 *       if there is not room on the queue.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>queueCapacity</td><td>10</td>
 *      <td align="left">The maximum number of OutputBufferEvents that will
 *      be queued up.
 *      </td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" ...&gt;
 *         &lt;Parameter name="queueKeepMode" value="keepLatest" /&gt;
 *         &lt;Parameter name="queueCapacity" value="1" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/03/14 14:57:16 $
 * @author tames
 */
public abstract class AbstractDualThreadedConnection 
	extends AbstractThreadedConnection implements Connection
{
	//--- Logging support
	private final static String CLASS_NAME = 
		AbstractDualThreadedConnection.class.getName();
	private final static Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Dual Threaded Connection";
	
	public static final String KEEP_KEY = "queueKeepMode";
	public static final String KEEP_ALL_STR = "keepAll";
	public static final String KEEP_LATEST_STR = "keepLatest";
	public static final String KEEP_EARLIEST_STR = "keepEarliest";
	public static final String KEEP_CAPACITY_KEY = "queueCapacity";

	private WriterThread fWriterThread = null;
	private KeepOptionalBoundedQueue fOutputQueue = 
		new KeepOptionalBoundedQueue(10);


	/**
	 *	Constructs a new DualThreadedConnection having a default name and managed 
	 *  by the default ComponentManager.
	 *
	 */
	public AbstractDualThreadedConnection()
	{
		super(DEFAULT_NAME);
		
		fOutputQueue.setKeepAll();
	}
	
	
	/**
	 *  Constructs a new DualThreadedConnection having the given base name and 
	 *  managed by the default ComponentManager.
	 * 
	 *  @param name The base name of the new DualThreadedConnection
	 **/

	public AbstractDualThreadedConnection(String name)
	{
		super(name);
		
		fOutputQueue.setKeepAll();
	}
	
	
	/**
	 * Constructs a new DualThreadedConnection, configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new DualThreadedConnection
	 */
	public AbstractDualThreadedConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
		
		fOutputQueue.setKeepAll();
		
		configureFromDescriptor(descriptor);
	}


	/**
	 * Starts the connection for sending and receiving data. This calls
	 * {@link AbstractThreadedConnection#start()} and then starts the writer
	 * thread.
	 */
	public synchronized void start()
	{
		if (!isStarted() && !isKilled())
		{
			super.start();
			startWriterThread();
		}
	}
	
	/**
	 * Stops the connection. This stops the writer thread and then 
	 * calls {@link AbstractThreadedConnection#stop}.
	 */
	public synchronized void stop()
	{
		if (!isStopped() && !isKilled())
		{
			stopWriterThread();
			super.stop();
		}
	}

	/**
	 *  Sets the Descriptor of this Component to the given Descriptor. The 
	 *  Component will in turn be (re)configured in accordance with the given 
	 *  Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/
	public void setDescriptor(Descriptor descriptor)
	{
	    super.setDescriptor(descriptor);
	    
		if (descriptor instanceof ConnectionDescriptor)
		{
			configureFromDescriptor((ConnectionDescriptor) descriptor);
		}
	}	

	/**
	 * Sets the maximum number of OutputBufferEvents that this connection will
	 * queue up for writing. If the new capacity is less then the number of
	 * events currently in the queue they will be dicarded.
	 * 
	 * @param capacity the new capacity of the event Queue
	 */
	public void setCapacity(int capacity) throws IllegalArgumentException
	{
		try
		{
			fOutputQueue.setCapacity(capacity);
		}
		catch (IllegalArgumentException e)
		{
			fOutputQueue.clear();
			fOutputQueue.setCapacity(capacity);
		}
	}

	/**
	 * Returns the maximum number of OutputBufferEvents that this 
	 * connection will queue up for writing.
	 *
	 * @return maximum capacity
	 **/
	public int getCapacity()
	{
		return (fOutputQueue.getCapacity());
	}
	
	/**
	 * Returns true if the current keep mode is to keep all the contents of the 
	 * Queue.
	 * 
	 * @return Returns true if the keep mode is keep all.
	 */
	public boolean isKeepAll()
	{
		return (fOutputQueue.isKeepAll());
	}

	/**
	 * Sets the keep mode for the Queue to keep all.
	 */
	public void setKeepAll()
	{
		fOutputQueue.setKeepAll();
	}

	/**
	 * Returns true if the current keep mode is to keep the latest objects
	 * added to the Queue if full and discarding the earliest if needed.
	 * 
	 * @return Returns true if the keep mode is keep latest.
	 */
	public boolean isKeepLatest()
	{
		return (fOutputQueue.isKeepLatest());
	}

	/**
	 * Sets the keep mode for the Queue to keep latest.
	 */
	public void setKeepLatest()
	{
		fOutputQueue.setKeepLatest();
	}

	/**
	 * Returns true if the current keep mode is to keep the earliest objects
	 * added to the Queue if full and discarding the latest if needed.
	 * 
	 * @return Returns true if the keep mode is keep earliest.
	 */
	public boolean isKeepEarliest()
	{
		return (fOutputQueue.isKeepEarliest());
	}

	/**
	 * Sets the keep mode for the Queue to keep earliest.
	 */
	public void setKeepEarliest()
	{
		fOutputQueue.setKeepEarliest();
	}

	/**
	 * Overrides super class implementation to Queue the event
	 * for later handling by the internally managed writer Thread. The 
	 * internal Thread will call the protected method
	 * <code>handleQueuedOutputEvent</code>.
	 *
	 * @param event event containing a buffer
	 * @see #handleQueuedOutputEvent(OutputBufferEvent)
	 */
	public final void handleOutputBufferEvent(OutputBufferEvent event)
	{
		//System.out.println("AbstractDualThreaded Queue size:" + fOutputQueue.size());
		
		try
		{
			fOutputQueue.blockingAdd(event);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the buffer from the event and calls the <code>process</code>
	 * method.
	 * 
	 * @param event event containing a buffer
	 * @see #process(ByteBuffer)
	 */
	protected void handleQueuedOutputEvent(OutputBufferEvent event) 
	{
		try
		{
			process(event.getData());
		}
		catch (IOException e)
		{
			// Since we don't know the cause of the exception
			// log it as a warning.
			String message = "Exception from Connection";
			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"WriterThread.run", message, e);
		}
	}

	/**
	 * Causes this Connection to (re)configure itself in accordance 
	 * with its current Descriptor.
	 *  
	 * @param descriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		if (descriptor == null)
		{
			return;
		}
		
		String keepMode = descriptor.getParameter(KEEP_KEY);
		if (keepMode != null)
		{
			if (keepMode.equals(KEEP_ALL_STR))
			{
				fOutputQueue.setKeepAll();
			}
			else if (keepMode.equals(KEEP_LATEST_STR))
			{
				fOutputQueue.setKeepLatest();
			}
			else if (keepMode.equals(KEEP_EARLIEST_STR))
			{
				fOutputQueue.setKeepEarliest();
			}
		}

		String keepCapacity = descriptor.getParameter(KEEP_CAPACITY_KEY);
		if (keepCapacity != null)
		{
			try
			{
				int capacity = Integer.parseInt(keepCapacity);
				
				if (capacity < 1)
				{
					String message = 
						"Output Queue capacity must be greater than 0 instead of "
						+ capacity;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the capacity 
					fOutputQueue.setCapacity(capacity);			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to set output Queue capacity with invalid number "
					+ keepCapacity;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
	}
	
	/**
	 * Starts the writer thread to wait for new data to write to the connection.
	 */
	private void startWriterThread()
	{
		//---If the Thread is already running skip it
		if(fWriterThread != null && fWriterThread.isAlive())
		{
			return;
		}

		//---Start reader thread
		fWriterThread = new WriterThread();
		fWriterThread.start();
	}

	/**
	 * Stops the writer thread.
	 */
	private void stopWriterThread()
	{
		//---Interrupt reader thread, if it's running
		if(fWriterThread != null && fWriterThread.isAlive())
		{
			fWriterThread.interrupt();
			fWriterThread = null;
		}
	}
	
	// --- Utility classes ----------------------------------------------------
	
	/**
	 * The ServiceThread class listens for new data on the connection.
	**/
	protected class WriterThread extends Thread
	{
		/**
		 * Creates a new Reader.
		 */
		public WriterThread()
		{			
			setName(getFullyQualifiedName() + " Service Thread");
		}

		/**
		 * Continously listens for new data on client connections.
		 */
		public void run()
		{
			while(!isInterrupted())
			{
				try
				{
					OutputBufferEvent event = 
						(OutputBufferEvent) fOutputQueue.blockingRemove();
					
					handleQueuedOutputEvent(event);
				}
				catch (InterruptedException e)
				{
					String message = "Exception from interrupted Connection";
					sLogger.logp(
							Level.FINE, CLASS_NAME, 
							"WriterThread.run", message, e);
					
					// The thread interrupt status should already be set due
					// to this exception, but it appears that this is not
					// always the case so set it anyway.
					interrupt();
					//System.out.println(" Service thread ClosedByInterruptEx");
				}
			}
		}
	} // End WriterThread Class
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDualThreadedConnection.java,v $
//  Revision 1.4  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/11/21 16:12:14  tames
//  Changed some String constants from Object to String.
//
//  Revision 1.1  2005/10/21 20:21:20  tames_cvs
//  Initial version
//
//