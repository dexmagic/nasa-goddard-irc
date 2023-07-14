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

package gov.nasa.gsfc.irc.library.ports.connections.dma;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.Port;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferListener;

/**
 * A DmaConnection manages reading and writing data to Direct
 * Memory Access (DMA) buffers. Reading and writing to buffer(s) is done
 * "half-duplex" or not simultaneously with the priority given to writing data
 * to the DMA. Data read from DMA is published as a
 * {@link gov.nasa.gsfc.irc.port.connections.InputBufferEvent InputBufferEvent} 
 * to all
 * {@link gov.nasa.gsfc.irc.port.connections.InputBufferListener 
 * InputBufferListener}. The
 * DMA data in the event is represented by a
 * {@link gov.nasa.gsfc.commons.types.buffers.BufferHandle BufferHandle}.
 *
 * <P>The configuration of this DMA port is specified by a ConnectionDescriptor.
 * The table below gives the configuration parameters that this port uses.
 * If the parameter is missing then the default value will be used. If there
 * is not a default value specified then the parameter is required to be
 * in the descriptor. The "DMA Device" parameter is required and
 * must specify a Class that implements the
 * {@link gov.nasa.gsfc.irc.library.ports.connections.dma.DmaDevice DmaDevice} 
 * interface.
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Key</th>
 *      <th>Default</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>DMA Device</td><td>-</td>
 *      <td align="left">The DmaDevice implementing class for the DMA port</td>
 *  </tr>
 *  <tr align="center">
 *      <td>Buffer Size</td><td>1000000</td>
 *      <td align="left">The size of the DMA buffer in bytes. Accepts decimal,
 *          hexadecimal, and octal numbers.
 *      </td>
 *  </tr>
 *  <tr align="center">
 *      <td>Max Data Latency</td><td>100ms</td>
 *      <td align="left">The maximum data latency allowed for this DMA port in
 *      milliseconds. Translates into the frequency that the DMA is polled for
 *      slower data rates. The larger the number the less DMA overhead, but the
 *      greater potential lag between when the data was written to the DMA and
 *      when the data is made available for processing or display.
 * 		</td>
 *  </tr>
 *  <tr align="center">
 *      <td>Minimum Read Size</td><td>124</td>
 *      <td align="left">The minimum buffer read size in bytes. Typically there
 * 			is a minimum size for either efficiency or parser constraints that
 * 			the DMA reader should use. This generic class cannot guarantee that
 * 			the parser will not get buffer slices smaller than this size without 
 * 			the specified DmaDevice class also enforcing this constraint.
 * 		</td>
 *  </tr>
 *  <tr align="center">
 *      <td>DMA IO Thread Priority</td><td>5</td>
 *      <td align="left">The default Thread priority for DMA IO. Valid values
 * 			are 1-10 with 10 being the highest priority.
 * 		</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for a DmaConnection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Mark2" type="DMA"&gt;
 *         &lt;Parameter name="DMA Device"
 *             value="gov.nasa.gsfc.irc.library.ni.NiDio32hs" /&gt;
 *         &lt;Parameter name="Buffer Size" value="0x00100000" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 14:57:16 $
 *  @author	    Troy Ames
**/
public class DmaConnection extends   AbstractConnection
						implements	OutputBufferListener
{
	private static final String CLASS_NAME = DmaConnection.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "DMA Connection";

	//----------- Descriptor keys ---------------
	/** The key for getting the buffer size from a PortDescriptor. */
	public static final String BUFFER_SIZE_KEY = "Buffer Size";

	/** The key for getting the DmaDevice implementing class from a PortDescriptor. */
	public static final String DMA_DEVICE = "DMA Device";

	/** The key for getting the Max Data Latency from a PortDescriptor. */
	public static final String MAX_DATA_LATENCY = "Max Data Latency";

	/** The key for getting the Minimum Read Size from a PortDescriptor. */
	public static final String MIN_READ_SIZE_KEY = "Minimum Read Size";

	/** The key for setting the DMA IO thread priority from a PortDescriptor. */
	public static final String DMA_THREAD_PRIORITY_KEY = "DMA IO Thread Priority";

	private Thread fDmaIoHandlerThread = null;
	private Thread fDmaWriteHandlerThread = null;
	private Thread fDataPublisherThread = null;
	private int fDmaPriority = Thread.NORM_PRIORITY;
	private DmaWriteHandler fDmaWriteHandler = null;
	private DataPublisher fDataPublisher = null;
	//private Object fDmaLock = new Object();
	private DmaDevice fDmaImpl = null;

	// Out buffer fields
	private ByteBuffer fWriteBuffer = null;

	// In buffer fields
	private ByteBuffer fReadBuffer = null;
	private DmaBufferState fReadBufferState = null;
	private int fDmaCapacity = 1000000;
	private float fMaxDataLag = 100.0F; // in milliseconds

	// In buffer available fields
	//private int fAvailPosition = 0;
	//private int fAvailLimit = 0;
	private int fMinReadSize = 124;


	/**
	 *  Constructs a new DmaConnection having a default name.
	 * 
	 **/

	public DmaConnection()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new DmaConnection having the given base name.
	 * 
	 *  @param name The base name of the new DmaConnection
	 **/

	public DmaConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new DmaConnection configured according to the given 
	 *  ConnectionDescriptor.
	 *
	 *  @param descriptor The ConnectionDescriptor of the new DmaConnection
	 */
	
	public DmaConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
		
		configureFromDescriptor(descriptor);
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
	 * Causes this DMA Connection to (re)configure itself in accordance 
	 * with its current Descriptor.
	 *  
	 * @param descriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		if (descriptor != null)
		{
			String key = DMA_DEVICE;
			String value = descriptor.getParameter(key);
			if(value != null)
			{
				fDmaImpl = getDeviceImplementation(value, descriptor);
			}
			else
			{
				throw new IllegalArgumentException("Missing "
					+ key + " parameter in port descriptor");
			}

			key = BUFFER_SIZE_KEY;
			value = (String) descriptor.getParameter(key);
			if(value != null)
			{
				try
				{
					fDmaCapacity = Integer.decode(value).intValue();
				}
				catch (NumberFormatException ex)
				{
					String message = 
						"DMA connection was given incorrect "
						+ BUFFER_SIZE_KEY + " parameter";

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message, ex);
				}
			}

			key = MIN_READ_SIZE_KEY;
			value = (String) descriptor.getParameter(key);
			if(value != null)
			{
				try
				{
					fMinReadSize = Integer.decode(value).intValue();
				}
				catch (NumberFormatException ex)
				{
					String message = 
						"DMA connection was given incorrect "
						+ MIN_READ_SIZE_KEY + " parameter";

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message, ex);
				}
			}

			key = MAX_DATA_LATENCY;
			value = (String) descriptor.getParameter(key);
			if(value != null)
			{
				try
				{
					fMaxDataLag = Integer.decode(value).floatValue();
				}
				catch (NumberFormatException ex)
				{
					String message = 
						"DMA connection was given incorrect "
						+ MAX_DATA_LATENCY + " parameter";

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message, ex);
				}
			}
			
			key = DMA_THREAD_PRIORITY_KEY;
			value = (String) descriptor.getParameter(key);
			if(value != null)
			{
				try
				{
					fDmaPriority = Integer.decode(value).intValue();
				}
				catch (NumberFormatException ex)
				{
					String message = 
						"DMA connection was given incorrect "
						+ DMA_THREAD_PRIORITY_KEY + " parameter";

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message, ex);
				}
			}
		}
	}
	
	/**
	 * Closes the port.
	 *
	 * @returns true if the close was successful.
	 * @see Port
	 */
	public boolean close()
	{
		fDmaIoHandlerThread.interrupt();
		fDmaIoHandlerThread = null;

		fDataPublisherThread.interrupt();
		fDataPublisherThread = null;

		return true;
	}

	/**
	 * Starts the connection for sending and receiving data. 
	 * A connection must be started before the 
	 * <code>write</code> method can be called and before data will be received
	 * from this ConnectionManager.
	 */
	public synchronized void start()
	{
		// if killed or already started do nothing
		if (isKilled() || isStarted())
		{
			return;
		}
		
		super.start();
		declareWaiting();

		fReadBuffer = fDmaImpl.getReadBuffer(fDmaCapacity);
		//fDmaCapacity = fReadBuffer.capacity();
		if (!fReadBuffer.isDirect())
		{
			String message = "DMA is not using a direct Read buffer";

			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message);
		}

		fReadBufferState = new DmaBufferState(fReadBuffer);
		fWriteBuffer = fDmaImpl.getWriteBuffer(1024);
		fWriteBuffer.limit(fWriteBuffer.position());

		if (fDataPublisherThread == null)
		{
			fDataPublisher = new DataPublisher();
			fDataPublisherThread =
				new Thread(fDataPublisher, "DataPublisher");

			fDataPublisherThread.start();
		}

		if (fDmaIoHandlerThread == null)
		{
			DmaReadHandler readHandler = 
				new DmaReadHandler(this, fDmaImpl, fReadBufferState);
			readHandler.setMinimumReadSize(fMinReadSize);
			readHandler.setMaximumDataLag(fMaxDataLag);
			
			fDmaIoHandlerThread = new Thread(readHandler, "DmaReadHandler");

			fDmaIoHandlerThread.setPriority(fDmaPriority);
			fDmaIoHandlerThread.start();
		}

		if (fDmaWriteHandlerThread == null)
		{
			fDmaWriteHandler = new DmaWriteHandler(this, fDmaImpl, fWriteBuffer);
			fDmaWriteHandler.setMaximumDataLag(fMaxDataLag);
			fDmaWriteHandlerThread = new Thread(fDmaWriteHandler, "DmaWriteHandler");

			fDmaWriteHandlerThread.setPriority(fDmaPriority);
			fDmaWriteHandlerThread.start();
		}
		
		publishConnectionAdded(null);
	}

	/**
	 * Stops the connection. A connection that is closed will not generate 
	 * any ConnectionDataEvents.
	 */
	public synchronized void stop()
	{
		// if killed or already stopped do nothing
		if (isKilled() || isStopped())
		{
			return;
		}

		super.stop();
		
		fDmaIoHandlerThread.interrupt();
		fDmaIoHandlerThread = null;

		fDataPublisherThread.interrupt();
		fDataPublisherThread = null;

		fDmaWriteHandlerThread.interrupt();
		fDmaWriteHandlerThread = null;
	}
		
	/**
	 *  Causes this Component to immediately cease operation and release 
	 *  any allocated resources. A killed Component cannot subsequently be 
	 *  started or otherwise reused.
	 */	
	public void kill()
	{
		stop();
		super.kill();
 	}

	/**
	 * Writes the contents of the Buffer to DMA.
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public void process(ByteBuffer buffer) throws IOException
	{
		if (buffer != null)
		{
			fDmaWriteHandler.writeData(buffer);
		}		
	}

	/**
	 * Adds the new data to the Data Publisher queue for async publication.
	 * 
	 * @param handle data to publish.
	 */
	protected void publishBuffer(BufferHandle handle)
	{
		fDataPublisher.add(handle);
	}

	/**
	 *  Sets the state of this port to either READ_MODE or WRITE_MODE.
	 *
	 *  @param mode the new mode
	**/
	/*private void setMode(int mode)
	{
		if (mode != fMode)
		{
			synchronized (fDmaLock)
			{
				fMode = mode;
				fDmaLock.notifyAll();
			}
		}
	}*/

	/**
	 *  Returns an instance of a DmaDevice that implements the underlying
	 *  DMA communication.
	 *
	 *  @param className	the implementation class name
	 *  @param descriptor	the Connection Descriptor to pass to the constructor
	 *
	 *  @returns a DmaDevice
	**/
	protected DmaDevice getDeviceImplementation(
		String className, ConnectionDescriptor descriptor)
	{
		DmaDevice dmaDeviceImpl = null;

		try
		{
			Class dmaClass = Class.forName(className);

			// The constructor argument types
			Class argClasses [] = {ConnectionDescriptor.class};
			// The constructor's actual arguments
			Object args [] = {descriptor};
			// Use reflection to find the constructor
			Constructor ctor = dmaClass.getConstructor(argClasses);
			// Run the constructor to get the instance
			dmaDeviceImpl = (DmaDevice)ctor.newInstance(args);
		}
		catch (Exception e)
		{
			String message = 
				"Exception when creating DMA Device for the connection";

			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"getDeviceImplementation", message, e);
		}

		return dmaDeviceImpl;
	}

	/**
	 *  The DataPublisher class is a Runnable Object that publishes all data
	 *  input from the DMA to all listeners.
	**/
	private class DataPublisher implements Runnable
	{
		private Queue fPubQueue = new FifoQueue();

		/**
		 *  Default constructor of an DataPublisher.
		 *
		**/
		public DataPublisher()
		{
		}

		/**
		 *  Adds an object to the queue for publishing.
		 *
		 *	@param handle	object to publish.
		**/
		public void add(BufferHandle handle)
		{
			// Set handle in use so that it does not get reclaimed
			// before it gets published.
			handle.setInUse();
			fPubQueue.add(handle);
		}

		/**
		 *  Continuously publish data until interrupted.
		 */
		public void run()
		{
			Thread thread = Thread.currentThread();
			
			try
			{
				while (!thread.isInterrupted())
				{
					BufferHandle handle = (BufferHandle) fPubQueue.blockingRemove();
					
					fireInputBufferEvent(
						new InputBufferEvent(DmaConnection.this, handle));

					// Release the publishers lock on the buffer handle.
					handle.release();
				}
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	} // end DmaPublisher Class
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DmaConnection.java,v $
//  Revision 1.16  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.15  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.14  2005/05/04 17:21:39  tames_cvs
//  Added ConnectSource support.
//
//  Revision 1.13  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.12  2005/03/01 23:25:15  chostetter_cvs
//  Revised Queue design and package for better blocking behavior
//
//  Revision 1.11  2005/02/27 04:29:34  tames
//  Added logic to recognize thread interrupt in run method.
//
//  Revision 1.10  2005/02/25 22:33:16  tames_cvs
//  Added try/catch in the DataPublisher run method to correctly
//  handle interrupts.
//
//  Revision 1.9  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.8  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.7  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.6  2004/09/27 20:40:01  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.5  2004/09/13 20:27:04  tames_cvs
//  Changed stop to do nothing if already stopped.
//
//  Revision 1.4  2004/09/13 20:22:34  tames_cvs
//  Changed start method to do nothing if connection is already started.
//
//  Revision 1.3  2004/09/04 13:34:49  tames
//  added setDescriptor method
//
//  Revision 1.2  2004/08/23 17:51:27  tames
//  Javadoc corrections
//
//  Revision 1.1  2004/08/23 16:04:53  tames
//  Ported from version 5
//
