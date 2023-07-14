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
import java.nio.channels.ClosedByInterruptException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A common pattern for Connection components is to have a dedicated Thread to
 * service (typically read) a Connection. This abstract class manages a Thread
 * for this purpose. Typically subclasses will only need to implement the
 * {@link #openConnection()}, {@link #closeConnection()}, and
 * {@link #serviceConnection()} methods to service a connection.
 * 
 * <p>
 * Subclasses will still need to implement the inherited abstract
 * <code>process</code> method for sending data as well as publish the events
 * described in the
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection 
 * AbstractConnection} super class.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/03/14 14:57:16 $
 * @author tames
 */
public abstract class AbstractThreadedConnection extends AbstractConnection 
	implements Connection
{
	//--- Logging support
	private final static String CLASS_NAME = 
		AbstractThreadedConnection.class.getName();
	private final static Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Threaded Connection";

	private ServiceThread fServiceThread = null;


	/**
	 *	Constructs a new ThreadedConnection having a default name and managed 
	 *  by the default ComponentManager.
	 *
	 */
	public AbstractThreadedConnection()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new ThreadedConnection having the given base name and managed 
	 *  by the default ComponentManager.
	 * 
	 *  @param name The base name of the new ThreadedConnection
	 **/

	public AbstractThreadedConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 * Constructs a new ThreadedConnection, configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new ThreadedConnection
	 */
	public AbstractThreadedConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Starts the connection for sending and receiving data. This 
	 * calls {@link #openConnection()} and then starts the reader thread.
	 */
	public synchronized void start()
	{
		if (!isStarted() && !isKilled())
		{
			super.start();
			openConnection();
			startServiceThread();
		}
	}
	
	/**
	 * Stops the connection. This stops the reader thread and then
	 * calls {@link #closeConnection()}.
	 */
	public synchronized void stop()
	{
		if (!isStopped() && !isKilled())
		{
			super.stop();
			stopServiceThread();
			closeConnection();
		}
	}

	/**
	 *  Causes this Component to immediately cease operation and release 
	 *  any allocated resources. A killed Component cannot subsequently be 
	 *  started or otherwise reused.
	 */	
	public synchronized void kill()
	{
		stop();
		super.kill();
	}

	/**
	 * Opens and initializes a connection.
	 */
	protected abstract void openConnection();

	/**
	 * Closes and releases any resources held by a connection.
	 */
	protected abstract void closeConnection();

	/**
	 * Service the current open connection. This typically reads from and 
	 * processes data from the Connection. This method will be called 
	 * repeatedly from a dedicated Thread. This method should block if there is 
	 * nothing to read or if it must wait for an event on the Connection.
	 * 
	 * @throws IOException
	 */
	protected abstract void serviceConnection() throws IOException;

	/**
	 * Starts the reader thread to listen for new data.
	 */
	private void startServiceThread()
	{
		//---If the Thread is already running skip it
		if(fServiceThread != null && fServiceThread.isAlive())
		{
			return;
		}

		//---Start reader thread
		fServiceThread = new ServiceThread();
		fServiceThread.start();
	}

	/**
	 * Stops the reader thread from listening for new data.
	 */
	private void stopServiceThread()
	{
		//---Interrupt reader thread, if it's running
		if(fServiceThread != null && fServiceThread.isAlive())
		{
			fServiceThread.interrupt();
			fServiceThread = null;
		}
	}
	
	// --- Utility classes ----------------------------------------------------
	
	/**
	 * The ServiceThread class listens for new data on the connection.
	**/
	protected class ServiceThread extends Thread
	{
		/**
		 * Creates a new ServiceThread.
		 */
		public ServiceThread()
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
					serviceConnection();
				}
				catch (ClosedByInterruptException e)
				{
					String message = "Exception from interrupted Connection";
					sLogger.logp(
							Level.FINE, CLASS_NAME, 
							"ServiceThread.run", message, e);
					
					// The thread interrupt status should already be set due
					// to this exception, but it appears that this is not
					// always the case so set it anyway.
					interrupt();
					//System.out.println(" Service thread ClosedByInterruptEx");
				}
				catch (IOException e)
				{
					if (isInterrupted())
					{
						// Since interrupting the thread may result in 
						// an IOException, we can assume that this is 
						// an expected exception and not log it as a warning.
						String message = "Exception from Connection";
						sLogger.logp(
								Level.FINE, CLASS_NAME, 
								"ServiceThread.run", message, e);
						//System.out.println(" Service thread IOException");
					}
					else
					{
						// Since we don't know the cause of the exception
						// log it as a warning.
						String message = "Exception from Connection";
						sLogger.logp(
								Level.WARNING, CLASS_NAME, 
								"ServiceThread.run", message, e);
					}
				}
			}
		}
	} // End ServiceThread Class
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractThreadedConnection.java,v $
//  Revision 1.8  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.7  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/10/21 20:22:51  tames_cvs
//  Primarily JavaDoc updates. Renamed startReaderThread to startServiceThread.
//
//  Revision 1.5  2005/05/04 17:13:49  tames_cvs
//  Javadoc update only.
//
//  Revision 1.4  2005/03/03 20:17:45  tames_cvs
//  Added more catch blocks to service thread in order to correctly handle
//  more interrupt conditions. And to bypass an apparent JVM bug.
//
//  Revision 1.3  2005/03/01 00:07:07  tames
//  Refactored internal Thread and renamed the readFromConnection
//  method to seviceConnection since it is responsible for more than
//  reading.
//
//  Revision 1.2  2005/02/27 04:26:56  tames
//  synchronized start, stop, and kill methods.
//
//  Revision 1.1  2005/02/24 16:36:33  tames_cvs
//  Initial version
//
//