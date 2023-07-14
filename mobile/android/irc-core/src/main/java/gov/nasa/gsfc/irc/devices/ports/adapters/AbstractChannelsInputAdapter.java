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
import java.nio.channels.Pipe;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;

/**
 * This class converts the many InputBuffers received from a Connection into a 
 * set of readable Channels. This class takes the InputBuffers and writes them 
 * to dedicated internal pipes. One pipe per unique BufferHandle 
 * {@link gov.nasa.gsfc.commons.types.buffers.BufferHandle#getContext() context}. 
 * When a pipe has data available for reading the abstract
 * {@link #processChannel(ScatteringByteChannel,Object) processChannel} method 
 * is called with the readable channel. Since one internal thread is used to 
 * monitor the status of all the pipes the channel is in nonblocking mode and 
 * subclasses should not block in this method. A nonblocking channel does impose
 * some limitations on how the channel can be used.
 * <p>
 * Since parsing messages may not be applicable to all subclasses, this 
 * abstract class does not declare that it implements
 * {@link gov.nasa.gsfc.irc.devices.events.InputMessageSource InputMessageSource},
 * however it does have implementations for all the methods needed. 
 * If messages are applicable, it is the responsibility of the 
 * extending class to declare that it implements <code>InputMessageSource</code>.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 14:02:49 $
 * @author 	Troy Ames
 */
public abstract class AbstractChannelsInputAdapter extends AbstractInputAdapter
{
	// InputMessageEvent listeners
	private transient List fListeners = new CopyOnWriteArrayList();

	private ServiceThread fServiceThread = null;
	private Map fPipeByContextMap = new HashMap();

	//---DataSelector
	private Selector fSelector = null;
	private Queue fPendingPipeQueue = new FifoQueue();

	/**
	 *  Constructs a new ChannelsInputAdapter having the given base name.
	 * 
	 *  @param name The base name of the new ChannelsInputAdapter
	 **/

	public AbstractChannelsInputAdapter(String name)
	{
		super(name);

		try
		{
			//---DataSelector
			fSelector = Selector.open();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 *	Constructs a new ChannelsInputAdapter configured according to the given 
	 *  InputAdapterDescriptor.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new ChannelsInputAdapter
	 */

	public AbstractChannelsInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);

		try
		{
			//---DataSelector
			fSelector = Selector.open();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	

	/**
	 * Causes this InputAdapter to process the specified buffer by writing the
	 * data to an internal pipe.
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
		Object context = handle.getContext();
		Pipe pipe = getPipe(context);
		
		try
		{
			if (pipe == null)
			{
				// Create a new Pipe for the new context
				pipe = Pipe.open();
				
				// Save the pipe for future use
				addPipe(context, pipe);
			}
			
			// Set up the bridge between a ByteBuffer and the pipe
			WritableByteChannel channel = pipe.sink();
			
			// Write the new data to the internal pipe
			while (buffer.remaining() > 0)
			{
				channel.write(buffer);
			}
		}
		catch (IOException e)
		{
			String detail = "Input channel adapter unable to write to internal pipe";
			InputException exception = new InputException(detail);
			exception.initCause(e);
			
			throw exception;
		}
		
		return null;
	}
	
	/**
	 * Process the available data in the channel. This method will be called 
	 * repeatedly from a dedicated Thread. This method should not block. 
	 * <p>
	 * If the subclass is interpreting and creating Message 
	 * objects then they can be returned by this method and 
	 * and they will be handled by calling the 
	 * {@link #processInputMessage(Message,Object)} method.
	 * 
	 * @param channel the nonblocking channel to read from
	 * @param context the BufferHandle context associated with this channel
	 * @return Object or null.
	 * 
	 * @see #processInputMessage(Message,Object)
	**/
	public abstract Object processChannel(
			ScatteringByteChannel channel, Object context);
	
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
	 * Get the Pipe associated with the given context.
	 * 
	 * @param context the BufferHandle context
	 * @return the associated Pipe
	 */
	private Pipe getPipe(Object context)
	{
		return (Pipe) fPipeByContextMap.get(context);
	}
	
	/**
	 * Add the given pipe and map it to the given context.
	 * 
	 * @param context the BufferHandle context
	 * @param pipe the associated pipe.
	 */
	private void addPipe(Object context, Pipe pipe)
	{		
		// Save the pipe for future use
		fPipeByContextMap.put(context, pipe);		
		fPendingPipeQueue.add(new PipeEntry(pipe, context));
		
		// Wake up blocked selector to handle new pipe
		fSelector.wakeup();
	}
	
	/**
	 * Starts the reader thread.
	 */
	private void startReaderThread()
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
	 * Stops the reader thread.
	 */
	private void stopReaderThread()
	{
		//---Interrupt reader thread, if it's running
		if(fServiceThread != null && fServiceThread.isAlive())
		{
			fServiceThread.interrupt();
			fServiceThread = null;
		}
	}
	
	/**
	 * Process the received input Message. This method will be called 
	 * if the 
	 * {@link #processChannel(ScatteringByteChannel,Object) processChannel} 
	 * method returns a result of type Message. The context is used as the 
	 * initial reply context for an InputMessageEvent.
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
		 * Creates a new ServiceThread.
		 */
		public ServiceThread()
		{			
			setName(getFullyQualifiedName() + " Service Thread");
		}

		/**
		 * Continously listens for new data on internal pipes.
		 */
		public void run()
		{
			while(!isInterrupted())
			{
				try
				{
					// Block until a channel has a pending operation
					fSelector.select();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				//---Check if any channels have a pending reads
				Set selectedSet = fSelector.selectedKeys();
				int setSize = selectedSet.size();
				
				if (setSize > 0)	
				{	
					Iterator iter = fSelector.selectedKeys().iterator();
					
					while (iter.hasNext())
					{
						SelectionKey key = (SelectionKey) iter.next();

						//---Check if there is data to read from connection
						if (key.isReadable())
						{
							ScatteringByteChannel channel = 
								(ScatteringByteChannel) key.channel();

							Object result = 
								processChannel(channel, key.attachment());

							// Check if the result is a message
							if (result != null && result instanceof Message)
							{
								processInputMessage(
									(Message) result, key.attachment());
							}
						}
						
						//---Remove key from selected set
						iter.remove();
					}
				}
				
				// Get and register any new pipes
				while (fPendingPipeQueue.get() != null)
				{
					PipeEntry entry = (PipeEntry) fPendingPipeQueue.remove();
					
					if (entry != null)
					{
						try
						{
							SelectableChannel channel = entry.fPipe.source();
							channel.configureBlocking(false);
							channel.register(
								fSelector, SelectionKey.OP_READ, entry.fContext);
						}
						catch (IOException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
	} // End ServiceThread Class
	
	/**
	 * Internal temp structure for keeping an association between 
	 * a Pipe and a context.
	 */
	protected static class PipeEntry
	{
		private Pipe fPipe;
		private Object fContext;

		/**
		 * Constructor.
		 * 
		 * @param pipe the Pipe
		 * @param context the associated context of the Pipe.
		 */
		public PipeEntry(Pipe pipe, Object context)
		{
			fPipe = pipe;
			fContext = context;
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractChannelsInputAdapter.java,v $
//  Revision 1.8  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.7  2006/04/18 04:09:52  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.6  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.5  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.3  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.2  2005/07/12 17:13:44  tames
//  Small modification to how events are published.
//
//  Revision 1.1  2005/05/13 19:31:15  tames
//  Initial version.
//
//