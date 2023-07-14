//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/connections/AbstractConnection.java,v 1.15 2006/03/14 14:57:16 chostetter_cvs Exp $
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

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.components.AbstractManagedComponent;
import gov.nasa.gsfc.irc.description.Descriptor;

/**
 * An abstract implementation of the Connection component interface. A 
 * Connection is a link between two processes or devices that can be read from 
 * and written to. To maintain plug in compatibility among Connections, 
 * subclasses are expected to do the following: 
 * <ul>
 * <li>Implement the <code>process</code> method to 
 * handle data being sent out by the Connection. 
 * <li>Generate and publish 
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent InputBufferEvents} 
 * for data received by the Connnection. See the 
 * {@link #fireInputBufferEvent(InputBufferEvent) fireInputBufferEvent} 
 * method.
 * <li>Fire a {@link gov.nasa.gsfc.irc.devices.ports.connections.ConnectEvent ConnectEvent}
 * when an initial or new connection has been made or received. See the 
 * {@link #publishConnectionAdded(Object) publishConnectionAdded} method.
 * </ul> 
 * 
 * <P>Optionally, all InputBufferEvent objects can be dumped to a file.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	Troy Ames
 */
public abstract class AbstractConnection extends AbstractManagedComponent
		implements Connection
{
	private static final String CLASS_NAME = AbstractConnection.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Connection";

	// ConnectEvent listeners
	private transient List fConnectListeners = new CopyOnWriteArrayList();
	
	// InputBufferEvent listeners
	private transient List fInputListeners = new CopyOnWriteArrayList();
	
	// Parameters for dumping connection data to a file
	public final static String FILE_NAME_KEY = "filename";
	public final static String ENABLE_FILE_DUMP_KEY = "dumpToFile";
	public final static String CLEAN_FILE_KEY = "cleanDumpFile";
	
	// Whether to dump all connection data to a file
	private boolean fDumpToFile = false;
	
	// Should the dump file be cleaned upon startup
	private boolean fCleanDumpFileAtStartup = true;
	
	// Name of the dump file
	private String fDumpFileName;
	
	// State for file dumping
	private boolean fWarnedAboutBadFile = false;
	private FileOutputStream fFileOutputStream = null;
	private FileChannel fFileChannel = null;
	
	
	/**
	 *  Constructs a new Connection having a default name and managed by the 
	 *  default ComponentManager.
	 * 
	 **/

	public AbstractConnection()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new Connection having the given base name and managed by 
	 *  the default ComponentManager.
	 * 
	 *  @param name The base name of the new Connection
	 **/

	public AbstractConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new Connection configured according to the given 
	 *  ConnectionDescriptor.
	 *
	 *  @param descriptor The ConnectionDescriptor of the new Connection
	 */
	
	public AbstractConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Processes the contents of the Buffer with respect to the connection(s).
	 *
	 * @param buffer ByteBuffer to process
	 * @throws IOException if the processing fails.
	 */
	public abstract void process(ByteBuffer buffer) throws IOException;

	/**
	 * Gets the buffer from the event and calls the 
	 * <code>process</code> method.
	 *
	 * @param event event containing a buffer
	 * @see #process(ByteBuffer)
	 */
	public void handleOutputBufferEvent(OutputBufferEvent event)
	{
		try
		{
			process(event.getData());
		}
		catch (IOException e)
		{
			String message = "Exception when writing data to the connection";

			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"handleOutputBufferEvent", message, e);
		}
	}

	// Event support methods that this component is a source of --------------
	
	/**
	 * Convenience method for publishing a new connection event. This
	 * method calls the 
	 * {@link #fireConnectEvent(ConnectEvent) fireConnectEvent}
	 * method with the event type set to connection added.
	 * 
	 * @param context the context of the connection
	 * @return true if no listeners veto the connection, false otherwise.
	 * @see VetoableConnectListener
	 */
	protected void publishConnectionAdded(Object context)
	{
		ConnectEvent event = new ConnectEvent(this, context);
		event.fEventType = ConnectEvent.CONNECTION_ADDED;
		
		fireConnectEvent(event);
	}

	/**
	 * Fire an ConnectEvent to any registered listeners.
	 * 
	 * @param event  The ConnectEvent object.
	 */
	protected void fireConnectEvent(ConnectEvent event) 
	{		
		// Add a context to the Event if it was not set by the subclass.
		// Listeners may need a context to correctly handle the event.
		if (event.getContext() == null)
		{
			event.setContext(getName());
		}
		
		for (Iterator iter = fConnectListeners.iterator(); iter.hasNext();)
		{
			((ConnectListener) iter.next()).connectionChanged(event);
		}
	}

	/**
	 * Registers the given listener for ConnectEvent objects generated by 
	 * this connection.
	 * 
	 * @param listener a ConnectListener to register
	 */
	public void addConnectListener(ConnectListener listener)
	{
		fConnectListeners.add(listener);
	}

	/**
	 * Unregisters the given listener.
	 * 
	 * @param listener a ConnectListener to unregister
	 */
	public void removeConnectListener(ConnectListener listener)
	{
		fConnectListeners.remove(listener);
	}

	/**
	 * Gets an array of listeners registered to receive notification
	 * of new ConnectEvent objects.
	 * 
	 * @return an array of registered ConnectListeners
	 */
	public ConnectListener[] getConnectListeners()
	{
		return (ConnectListener[])(
				fConnectListeners.toArray(
						new ConnectListener[fConnectListeners.size()]));
	}

	/**
	 * Fire an InputBufferEvent to any registered listeners. The BufferHandle
	 * is set in use for delivery of the event and then released.
	 * 
	 * @param event  The InputBufferEvent object.
	 */
	protected void fireInputBufferEvent(InputBufferEvent event) 
	{
		if (event == null || event.getHandle() == null)
		{
			throw new IllegalArgumentException(
				"Event or enclosed buffer cannot be null");
		}
		
		BufferHandle handle = event.getHandle();
		
		if (fDumpToFile == true)
		{
			appendBufferToFile(
					(ByteBuffer) handle.getBuffer(), 
					fDumpFileName, fCleanDumpFileAtStartup);
		}
		
		// Add a context to the Buffer if it was not set by the subclass.
		// Listeners may need a context to correctly process the buffer.
		if (handle.getContext() == null)
		{
			handle.setContext(getName());
		}
		
		// Lock the buffer for delivery
		handle.setInUse();
		
		for (Iterator iter = fInputListeners.iterator(); iter.hasNext();)
		{
			((InputBufferListener) iter.next()).handleInputBufferEvent(event);
		}
		
		// Release the enclosing handle
		handle.release();
	}

	/**
	 * Registers the given listener for InputBufferEvent objects generated by 
	 * this connection.
	 * 
	 * @param listener a InputBufferListener to register
	 */
	public void addInputBufferListener(InputBufferListener listener)
	{
		fInputListeners.add(listener);
	}

	/**
	 * Unregisters the given listener.
	 * 
	 * @param listener a InputBufferListener to unregister
	 */
	public void removeInputBufferListener(InputBufferListener listener)
	{
		fInputListeners.remove(listener);
	}

	/**
	 * Gets an array of listeners registered to receive notification
	 * of new InputBufferEvent objects.
	 * 
	 * @return an array of registered InputBufferListeners
	 */
	public InputBufferListener[] getInputBufferListeners()
	{
		return (InputBufferListener[])(
				fInputListeners.toArray(
						new InputBufferListener[fInputListeners.size()]));
	}


	/**
	 * Causes this Component to stop. If necessary, closes FileOutputStream.
	 * @see gov.nasa.gsfc.commons.processing.activity.Startable#stop()
	 */
	public void stop()
	{
		if (!isStopped() && !isKilled())
		{
			super.stop();
	
			// Close the FileStream if we were dumping to a file
			if (fFileOutputStream != null)
			{
				try
				{
	
					fFileOutputStream.close();
					// the associated FileChannel will be automatically closed
	
				}
				catch (IOException e)
				{
					// Don't care about exception
				}
			}
		}
	}
	

	/**
	 * Causes this Connection to start.
	 * @see gov.nasa.gsfc.commons.processing.activity.Startable#start()
	 */
	public void start()
	{
		if (!isStarted() && !isKilled())
		{
			super.start();
	
			// reset file dump state
			fWarnedAboutBadFile = false;
			fFileOutputStream = null;
			fFileChannel = null;
		}
	}
	
	private void appendBufferToFile(ByteBuffer buffer, String fileName,
			boolean cleanOnInit)
	{
		try
		{
			if (fFileChannel == null)
			{
				FileOutputStream fileOutputStream = new FileOutputStream(
						new File(fileName), !cleanOnInit);
				fFileChannel = fileOutputStream.getChannel();
			}

			fFileChannel.write(buffer);
		}
		catch (FileNotFoundException e)
		{
			if (!fWarnedAboutBadFile)
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "appendBufferToFile",
						"couldn't open file for appending: " + fileName);
				fWarnedAboutBadFile = true;
				fFileChannel = null;
			}
		}
		catch (IOException e)
		{
			if (!fWarnedAboutBadFile)
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "appendBufferToFile", e
						.getMessage());
				fWarnedAboutBadFile = true;
				fFileChannel = null;
			}
		}

	}
	
	/**
	 * Causes this AbstractConnection to (re)configure itself in accordance with
	 * the descriptor.
	 * 
	 * @param descriptor a ConnectionDescriptor
	 */
	public void setDescriptor(Descriptor descriptor)
	{
		super.setDescriptor(descriptor);
		if (descriptor instanceof ConnectionDescriptor)
		{
			configureFromDescriptor((ConnectionDescriptor) descriptor);
		}
	}

	/**
	 * Causes this AbstractConnection to (re)configure itself in accordance with
	 * the descriptor.
	 * 
	 * @param descriptor a ConnectionDescriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		if (descriptor == null)
		{
			return;
		}

		//---Set name based on descriptor
		String name = descriptor.getName();
		if (name != null)
		{
			try
			{
				setName(descriptor.getName());
				setNameQualifier(descriptor.getNameQualifier());
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set name from descriptor name = " + 
						descriptor.getFullyQualifiedName();
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, ex);
				}
			}
		}
		
		// Dump to file?
		String str = descriptor.getParameter(ENABLE_FILE_DUMP_KEY);
		if (str != null)
		{
			fDumpToFile = Boolean.valueOf(str).booleanValue();
		}

		//  FileName
		fDumpFileName = descriptor.getParameter(FILE_NAME_KEY);
		if (fDumpFileName == null)
		{
		    fDumpFileName = "/tmp/ConnectionDump";
		}
		// Clean file?
		str = descriptor.getParameter(CLEAN_FILE_KEY);
		if (str != null)
		{
			fCleanDumpFileAtStartup = Boolean.valueOf(str).booleanValue();
		}
	}

    public String getDumpFileName() {
        return fDumpFileName;
    }
    public void setDumpFileName(String dumpFileName) {
        fDumpFileName = dumpFileName;
        if (fFileChannel != null)
        {
            try {
                fFileChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            fFileChannel = null;
        }
    }
    public boolean getDumpToFile() {
        return fDumpToFile;
    }
    public void setDumpToFile(boolean dumpToFile) {
        fDumpToFile = dumpToFile;
    }
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractConnection.java,v $
//  Revision 1.15  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.14  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.13  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.12  2005/07/16 03:33:52  tames
//  Fixed ClassCastException when publishing a ConnectionEvent.
//
//  Revision 1.11  2005/07/12 17:16:49  tames
//  Small modification to how events are published.
//
//  Revision 1.10  2005/06/03 14:32:54  smaher_cvs
//  Exposed file dumping properties in getters/setters to
//  allow manipulation in property editors (e.g., Component
//  Browser).  This allows users to interactively dump
//  the raw data in a connection.
//
//  Revision 1.9  2005/05/04 17:13:19  tames_cvs
//  Added support for ConnectEvent registration and publishing.
//
//  Revision 1.8  2005/03/01 00:04:48  tames
//  The delivery of an InputEvent in the fire method is now done with the
//  BufferHandle set as inuse to avoid the buffer being reclaimed before
//  listeners have a chance to process it.
//
//  Revision 1.7  2005/02/25 22:30:34  tames_cvs
//  Changed the start and stop methods to check current state and do nothing
//  if already in the target state.
//
//  Revision 1.6  2005/02/07 04:58:18  tames
//  Modified fireInputBufferEvent to always add a buffer context if the
//  concrete subclass did not.
//
//  Revision 1.5  2005/01/24 23:50:41  jhiginbotham_cvs
//  Set name on construction based on the name from the xml/descriptor.
//
//  Revision 1.4  2005/01/20 21:03:11  tames
//  Fixed ClassCastException when getting the array of listeners.
//
//  Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.2  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/09/27 20:33:45  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.3  2004/09/13 20:39:33  tames_cvs
//  Javadoc updates only
//
//  Revision 1.2  2004/09/10 14:55:31  smaher_cvs
//  Added ability to dump the ByteBuffers of ConnectionDataEvents to a file.
//
//  Revision 1.1  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
