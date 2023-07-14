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

package gov.nasa.gsfc.irc.devices.ports.adapters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.List;



import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;

/**
 * This class converts the many InputBuffers received from a Connection into a 
 * single readable Channel for subclasses to read from. Either for convenience 
 * or necessity InputAdapters may want to read data from a continuous Channel or 
 * data stream and not the packet oriented InputBuffers that are the default for 
 * Connections. This class takes the InputBuffers and writes them to an internal
 * Pipe. Since the type of the read end of the Pipe is a
 * {@link java.nio.channels.ScatteringByteChannel ScatteringByteChannel}, 
 * subclasses can wrap or cast the read channel into a variety of input streams 
 * or readers. Note since this class uses only one pipe for all inputs received
 * it does not support cases where partial input data from multiple sources
 * can be interleaved. 
 * <p>
 * Subclasses are responsible for transforming the raw input data to an
 * internal representation by implementing the 
 * {@link #processChannel(ScatteringByteChannel)} 
 * method. Since streams and channels will likely block waiting for data this 
 * class manages an internal thread to call the <code>processChannel</code>
 * method repeatedly.
 * <p>
 * Since parsing messages may not be applicable to all subclasses, this 
 * abstract class does not declare that it implements
 * {@link gov.nasa.gsfc.irc.devices.events.InputMessageSource InputMessageSource},
 * however it does have implementations for all the methods needed. 
 * If messages are applicable, it is the responsibility of the 
 * extending class to declare that it implements <code>InputMessageSource</code>.
 * 
 * <P><B>Note:</B> Early versions of the 1.4 JVM and even later versions on some 
 * platforms have serious bugs with respect to NIO nonblocking channels. As a 
 * result this class and any derived subclasses may not work reliably on all platforms. 
 * Developers should test on the target platform before utilizing this class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 14:02:49 $
 * @author 	Troy Ames
 */
public abstract class AbstractPipeInputAdapter extends AbstractInputAdapter
{
	public static final String DEFAULT_NAME = "Pipe Input Adapter";
	
	// InputMessageEvent listeners
	private transient List fListeners = new CopyOnWriteArrayList();
	
	private WritableByteChannel fWriteChannel = null;
	private Pipe fStreamPipe = null;
	private ScatteringByteChannel fReadChannel = null;
	private ServiceThread fServiceThread = null;
	private Object fContext = null;

	
	/**
	 * Constructs a new PipeInputAdapter having a default name and managed by
	 * the default ComponentManager.
	 */
	public AbstractPipeInputAdapter()
	{
		super(DEFAULT_NAME);
		
		// Set up the bridge between a ByteBuffer and the Channel
		configurePipe();
	}
	
	/**
	 * Constructs a new PipeInputAdapter having the given name and managed by
	 * the default ComponentManager.
	 * 
	 * @param name The name of the new PipeInputAdapter
	 */
	public AbstractPipeInputAdapter(String name)
	{
		super(name);
		
		// Set up the bridge between a ByteBuffer and the Channel
		configurePipe();
	}
		
	/**
	 * Constructs a new PipeInputAdapter configured according to the given
	 * InputAdapterDescriptor.
	 * 
	 * @param descriptor The InputAdapterDescriptor of the new PipeInputAdapter
	 */	
	public AbstractPipeInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);

		// Set up the bridge between a ByteBuffer and the Channel
		configurePipe();
	}

	/**
	 * Configure the Pipe for both reading and writing.
	 * 
	 * @return true if configuration of the pipe is successful.
	 * @throws IOException
	 */
	private void configurePipe()
	{
		try
		{
			if (fWriteChannel != null)
			{
				//System.out.println("InterruptibleChannel:" + (fWriteChannel instanceof InterruptibleChannel));
				((InterruptibleChannel)fWriteChannel).close();
			}
			
			if (fReadChannel != null)
			{
				//fReadChannel.close();
			}
			
			if (fStreamPipe != null)
			{
			}
			// Set up the bridge between a ByteBuffer and the Channel
			fStreamPipe = Pipe.open();
			fWriteChannel = fStreamPipe.sink();
			fReadChannel = fStreamPipe.source();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Causes this InputAdapter to process the specified buffer by writing the
	 * data to an internal pipe. Use the {@link #getReadChannel()} method to 
	 * get a reference to the read end of the pipe.
	 *
	 * @param handle BufferHandle to a buffer to be processed
	 * @return null.
	 * 
	 * @throws InputException
	 * @see BufferHandle
	 */
	public Object process(BufferHandle handle) throws InputException
	{
		ByteBuffer buffer = (ByteBuffer) handle.getBuffer();
		fContext = handle.getContext();
		
		if (isStarted() && fWriteChannel.isOpen())
		{
			try
			{
				// Write the new data to the internal pipe
				while (buffer.remaining() > 0)
				{
					fWriteChannel.write(buffer);
				}
			}
			catch (IOException e)
			{
				String detail = "Input channel adapter unable to write to internal pipe";
				InputException exception = new InputException(detail);
				exception.initCause(e);
				
				throw exception;
			}
		}
		
		return null;
	}
	
	/**
	 * Process the available data in the channel. This method will be called
	 * repeatedly from a dedicated Thread. This method should block if there is
	 * nothing to read.
	 * <p>
	 * If the subclass is interpreting and creating Message objects then they
	 * can be returned by this method and handled in the
	 * {@link #processInputMessage(Message,Object) processInputMessage} method.
	 * 
	 * @param channel the channel to read from
	 * @return Object or null.
	 * @throws IOException if an unhandled exception occurs processing the
	 *             channel
	 * @see #processInputMessage(Message,Object)
	 */
	public abstract Object processChannel(ScatteringByteChannel channel)
			throws IOException;
	
	/**
	 * Starts the InputAdapter and internal thread for processing received data. 
	 * As a result of being started the <code>processChannel</code> method 
	 * will be repeatedly called.
	 */
	public synchronized void start()
	{
		if (!isStarted() && !isKilled())
		{
			super.start();
			startReaderThread();
		}
	}
	
	/**
	 * Stops the InputAdapter and internal thread.
	 */
	public synchronized void stop()
	{
		if (!isStopped() && !isKilled())
		{
			super.stop();
			stopReaderThread();
		}
	}

	/**
	 * Causes this Component to immediately cease operation and release any
	 * allocated resources. A killed Component cannot subsequently be started or
	 * otherwise reused.
	 */	
	public synchronized void kill()
	{
		stop();
		super.kill();
	}

	/**
	 * Starts the reader thread.
	 */
	private void startReaderThread()
	{
		//---Create thread if needed.
		if(fServiceThread == null || !fServiceThread.isAlive())
		{
			//---Start reader thread
			fServiceThread = new ServiceThread();
			fServiceThread.start();
		}
	}

	/**
	 * Stops the reader thread.
	 */
	private void stopReaderThread()
	{
		//---Interrupt reader thread, if it's running
		if(fServiceThread != null && fServiceThread.isAlive())
		{
			//TODO the following call blocks on the Mac
			//fServiceThread.interrupt();
			//fServiceThread = null;
		}
	}
	
	/**
	 * Process the received input Message. This method will be called 
	 * if the 
	 * {@link #processChannel(ScatteringByteChannel) processChannel} 
	 * method returns a result of type Message. 
	 * Calls {@link #fireInputMessageEvent(InputMessageEvent)}.
	 * 
	 * @param message The message to publish.
	 * @param context the source data context
	 * @see gov.nasa.gsfc.irc.devices.events.InputMessageSource
	 */
	protected void processInputMessage(Message message, Object context)
	{	
		Path replyContext = null;
		
		if (context != null)
		{
			replyContext = new DefaultPath(context);
		}

        fireInputMessageEvent(new InputMessageEvent(this, message, replyContext));
	}

	/**
	 * Fire an InputMessageEvent to any registered listeners.
	 * 
	 * @param event  The InputMessageEvent object.
	 */
	protected void fireInputMessageEvent(InputMessageEvent event) 
	{
		for (Iterator iter = fListeners.iterator(); iter.hasNext();)
		{
			((InputMessageListener) iter.next()).handleInputMessageEvent(event);
		}
	}

	/**
	 * Registers the given listener for a InputMessageEvent generated by this
	 * InputAdapter.
	 * 
	 * @param listener a InputMessageListener to register
	 */
	public void addInputMessageListener(InputMessageListener listener)
	{
		fListeners.add(listener);
	}

	/**
	 * Unregisters the given listener.
	 * 
	 * @param listener a InputMessageListener to unregister
	 */
	public void removeInputMessageListener(InputMessageListener listener)
	{
		fListeners.remove(listener);
	}

	/**
	 * Gets an array of listeners registered to receive notification
	 * of new InputMessageEvent objects.
	 * 
	 * @return an array of registered InputListeners
	 */
	public InputMessageListener[] getInputMessageListeners()
	{
		return (InputMessageListener[])(
				fListeners.toArray(new InputMessageListener[fListeners.size()]));
	}

	// --- Utility classes ----------------------------------------------------
	
	/**
	 * The ServiceThread class listens for new data from the connection.
	**/
	protected class ServiceThread extends Thread
	{
		/**
		 * Creates a new Reader.
		 */
		public ServiceThread()
		{			
			setName(getFullyQualifiedName() + " Service Thread");
		}

		/**
		 * Continously listens for new data on client channel.
		 */
		public void run()
		{
			while(!isInterrupted())
			{
				try
				{
					Object result = processChannel(fReadChannel);
					
					// Check if the result is a message
					if (result != null && result instanceof Message)
					{
						processInputMessage((Message) result, fContext);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
					
					if (!isInterrupted())
					{
						configurePipe();
					}
				}
			}
		}
	} // End ServiceThread Class
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractPipeInputAdapter.java,v $
//  Revision 1.12  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.11  2006/04/18 04:09:52  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.10  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.9  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.8  2005/12/02 22:41:56  tames_cvs
//  Fixed history comment artifact introduced by checking in from the mac.
//
//  Revision 1.7  2005/12/02 22:38:04  tames_cvs
//  Changed run method so the pipe is not reconfigured if the Thread was 
//  interrupted.
//
//  Revision 1.6  2005/11/21 16:11:13  tames
//  Removed debug println statements and updated Javadoc.
//
//  Revision 1.5  2005/11/17 22:58:03  tames_cvs
//  Added some debug code for Mac OS testing.
//
//  Revision 1.4  2005/11/16 21:06:06  tames
//  Revised stop behavior and channel configuration as an attempt to avoid a
//  deadlock on Mac OS.
//
//  Revision 1.3  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.2  2005/07/12 17:13:44  tames
//  Small modification to how events are published.
//
//  Revision 1.1  2005/05/13 03:52:30  tames
//  Renamed. Added InputMessageSource methods.
//
//  Revision 1.1  2005/04/25 15:48:32  tames
//  Initial Version
//
//