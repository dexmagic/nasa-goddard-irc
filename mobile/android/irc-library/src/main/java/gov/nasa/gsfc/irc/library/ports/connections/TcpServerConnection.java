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

package gov.nasa.gsfc.irc.library.ports.connections;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;



import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.SingleUseBufferHandle;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractDualThreadedConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferEvent;


/**
 * A TcpServerConnection is a connection component that listens for and accepts
 * connections and data from clients. This class manages two Threads. One Thread
 * both listens for client connections and reads data from one or more accepted
 * connections and a second Thread that writes queued output messages to one or
 * more accepted connections. (see
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractDualThreadedConnection 
 * AbstractDualThreadedConnection})
 * <p>
 * The number of client connections is determined by the connections allowed
 * property. The block size of each read attempt is determined by the block size
 * property.
 * 
 * <p>
 * Note: Since this connection can accept multiple connections, registered
 * listeners for InputBufferEvents may receive buffers from different sources
 * interleaved. The context of the BufferHandle is set to uniquely identify the
 * actual source of the buffer. It is the listeners responsibility to handle any
 * data demultiplexing if necessary, particularly if a complete message or data
 * block can be larger then the value of the read block size property.
 * 
 * <P>
 * The configuration of this connection is specified by a ConnectionDescriptor.
 * The table below gives the configuration parameters specific to this connection.
 * If the parameter is missing then the default value will be used. If there is
 * not a default value specified then the parameter is required to be in the
 * descriptor except where noted. See
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractDualThreadedConnection AbstractDualThreadedConnection}
 * for some additional configuration parameters.
 * 
 * <P>
 * <center><table border="1">
 * <tr align="center">
 * <th>Key</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * <tr align="center">
 * <td>port</td>
 * <td>-</td>
 * <td align="left">The local port to listen on for new client connections</td>
 * </tr>
 * <tr align="center">
 * <td>readBlockSize</td>
 * <td>1024</td>
 * <td align="left">The default read size to use for receiving data</td>
 * </tr>
 * <tr align="center">
 * <td>connectionsAllowed</td>
 * <td>1</td>
 * <td align="left">The maximum number of simultaneous client connections
 * allowed. </td>
 * </tr>
 * </table> </center>
 * 
 * <P>
 * A partial example IML port description for this type of Connection: <BR>
 * 
 * <pre>
 *      &lt;Connection name=&quot;Test Connection&quot; type=&quot;TCP Server&quot;&gt;
 *          &lt;Parameter name=&quot;port&quot; value=&quot;9999&quot; /&gt;
 *          &lt;Parameter name=&quot;connectionsAllowed&quot; value=&quot;5&quot; /&gt;
 *          ...
 *      &lt;/Connection&gt;
 * </pre>
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/05/07 21:44:26 $
 * @author Troy Ames
 * @author John Higinbotham (Emergent Space Technologies)
 */
public class TcpServerConnection extends AbstractDualThreadedConnection 
	implements Connection
{
	//============================================================================
	// CONSTANTS
	//============================================================================
	
	//---Logging support
	private final static String CLASS_NAME = TcpServerConnection.class.getName();
	private final static Logger sLogger    = Logger.getLogger(CLASS_NAME);
	
	//---TCP/IP paramaters
	public final static String PORT_KEY                = "port";
	public final static String CONNECTIONS_ALLOWED_KEY = "connectionsAllowed";
	public final static String BLOCK_SIZE_KEY          = "readBlockSize";
	
	//============================================================================
	// VARS
	//============================================================================

	//---Configuration
	private int fConnectionsAllowed = 1;
	private int fReadBlockSize      = 1024;
	private int fServerPort         = 9999;
	
	//---Default object name
	public final static String DEFAULT_NAME = "TCP/IP Server Socket Connection";
	
	//---Open clients
	private List fClientSockets = new CopyOnWriteArrayList();
	
	//---Selector
	private Selector fSelector = null;

	//---Server socket channel
	private ServerSocketChannel fServerSocket = null;
	
	//----------------------------------------------------------------------------
	
	//============================================================================
	// CONSTRUCTION
	//============================================================================

	/**
	 *	Constructs a new TcpServerConnection having a default name.
	 *
	 */
	public TcpServerConnection()
	{
		this(DEFAULT_NAME);
	}	
	
	/**
	 *  Constructs a new TcpServerConnection having the given base name.
	 * 
	 *  @param name The base name of the new TcpServerConnection
	 **/

	public TcpServerConnection(String name)
	{
		super(name);
	}
	
	/**
	 * Constructs a new TcpServerConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new TcpServerConnection
	 */
	public TcpServerConnection(ConnectionDescriptor descriptor)
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
	 * Causes this TcpServerConnection to (re)configure itself in accordance 
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
		
		String strPortNumber = descriptor.getParameter(PORT_KEY);
		if (strPortNumber != null)
		{
			try
			{
				int port = Integer.parseInt(strPortNumber);
				
				if (port < 0)
				{
					String message = 
						"Attempt to build TcpServerConnection with invalid port number "
						+ strPortNumber;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the port number
					fServerPort = port;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build TcpServerConnection with invalid port number "
					+ strPortNumber;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}

		String strBlockSize = descriptor.getParameter(BLOCK_SIZE_KEY);
		if (strBlockSize != null)
		{
			try
			{
				int blockSize = Integer.parseInt(strBlockSize);
				
				if (blockSize < 0)
				{
					String message = 
						"Attempt to build TcpServerConnection with invalid number "
						+ blockSize;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the block size
					fReadBlockSize = blockSize;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build TcpServerConnection with invalid number "
					+ strBlockSize;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}

		String strConnections = descriptor.getParameter(CONNECTIONS_ALLOWED_KEY);
		if (strConnections != null)
		{		
			try
			{
				int connections = Integer.parseInt(strConnections);
				
				if (connections < 1)
				{
					String message = 
						"Attempt to build TcpServerConnection with invalid parameter "
						+ CONNECTIONS_ALLOWED_KEY + " = " +strConnections;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the port number
					fConnectionsAllowed = connections;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build TcpServerConnection with invalid parameter "
					+ CONNECTIONS_ALLOWED_KEY + " = " +strConnections;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
	}
	
	//============================================================================
	// CONNECTION SUPPORT
	//============================================================================

	/**
	 * Starts a server socket connection.
	 */
	protected void openConnection()
	{
		sLogger.logp(Level.FINE, CLASS_NAME, 
				"openConnection", "Opening connection...");
		
		//---Open read selector
		try
		{
			fSelector = Selector.open();

			//---Create listening server socket
			startServerSocket(fServerPort);
		}
		catch (IOException e)
		{
			sLogger.logp(Level.WARNING, CLASS_NAME, "openConnection", 
					getFullyQualifiedName() + "could not open server", e);
			return;
		}
	}

	/**
	 * Convenience method for checking if a connection request is allowed. This
	 * implementation always returns true. Subclasses can override this method
	 * to provide security or a filter on allowed remote connections.
	 * 
	 * @param address the address of the connection
	 * @return true if the connection should be allowed.
	 */
	protected boolean allowConnection(InetAddress address)
	{
		return true;
	}

	/**
	 * Closes a server socket connection.
	 */
	protected void closeConnection()
	{
		sLogger.logp(Level.FINE, CLASS_NAME, "closeConnection", 
				"Closing connection...");
		
		try
		{
			stopServerSocket();
			closeClientConnections();
			
			if (fSelector != null)
			{
				//fSelector.close();
				fSelector.wakeup();
			}
		}
		catch (IOException e)
		{
			String message = getFullyQualifiedName() + " could not close ";
			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"closeConnection", message, e);
		}
	}
	
	/**
	 * Accepts a connection if the maximum number of connections has not been 
	 * reached and the call to 
	 * {@link #allowConnection(SocketAddress) allowConnection} returns true.
	 *
	 * @param   channel  new SocketChannel to accept.
	 */
	public void acceptConnection(SocketChannel channel)
	{
		if (fClientSockets.size() < fConnectionsAllowed
				&& allowConnection(channel.socket().getInetAddress()))
		{
			try
			{
				//---Configure the channel for reading
				channel.configureBlocking(false);
				channel.register(fSelector, SelectionKey.OP_READ);
				
				//---Add new client socket to list of open sockets
				fClientSockets.add(channel);
				
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = getFullyQualifiedName() 
						+ " received connection from " 
						+ channel.socket().getInetAddress();
					sLogger.logp(Level.INFO, CLASS_NAME, "acceptConnection", 
							message);
				}
				
				if (isWaiting())
				{
					declareActive();
				}
				
				publishConnectionAdded(channel.socket().getInetAddress());
			}
			catch (IOException e)
			{
				String message = getFullyQualifiedName() 
					+ " unable to complete connection from " 
					+ channel.socket().getInetAddress();
				sLogger.logp(Level.WARNING, CLASS_NAME, "acceptConnection", 
					message, e);
			}
		}
		else
		{
			try
			{
				String message = getFullyQualifiedName() 
					+ " refusing connection from " 
					+ channel.socket().getInetAddress();
				sLogger.logp(Level.INFO, CLASS_NAME, "acceptConnection", 
					message);

				//---Refuse new channel
				channel.close();
			}
			catch (IOException e)
			{
				String message = getFullyQualifiedName() 
					+ " exception closing connection from " 
					+ channel.socket().getInetAddress();
				sLogger.logp(Level.WARNING, CLASS_NAME, "acceptConnection", 
					message, e);
			}
		}
	}

	/**
	 * Listens for new connections or data on client connections and calls the 
	 * <code>acceptConnection</code> or <code>readDataFromChannel</code>
	 * method on the Connection.
	 */
	protected void serviceConnection()
	{
		try
		{
			int channelsReady = fSelector.select();
				
			//---Check if any channels have a pending read or accept operation
			Set selectedSet = fSelector.selectedKeys();
			
			if (selectedSet != null && selectedSet.size() > 0)	
			{	
				Iterator iter = fSelector.selectedKeys().iterator();
				
				while (iter.hasNext())
				{
					SelectionKey key = (SelectionKey) iter.next();

					//---Check if a new connection is pending
					if (key.isAcceptable())
					{
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel channel = server.accept();
						acceptConnection(channel);
					}

					//---Check if there is data to read from connection
					else if (key.isReadable())
					{
						SocketChannel channel = (SocketChannel) key.channel();
						readDataFromChannel(channel);
					}
					
					//---Remove key from selected set
					iter.remove();
				}
			}
		}
		catch (IOException e)
		{
			sLogger.logp(Level.WARNING, CLASS_NAME, "ChannelReader.run",
					getFullyQualifiedName() + " could not read connection ", e);	
		}			
	}

	/**
	 * Overrides super class implementation to first check if the event has Path
	 * information specific to this connection. If the send context is null or
	 * not relevant then this implementation calls <code>super</code>. If the
	 * send context contains context information that is relevant this method
	 * calls <code>process(ByteBuffer, InetAddress)</code>.
	 * 
	 * @param event event containing a buffer
	 * @see AbstractConnection
	 * @see #process(ByteBuffer, InetAddress)
	 */
	protected void handleQueuedOutputEvent(OutputBufferEvent event)
	{
		Path sendContext = event.getSendContext();
		SocketAddress context = null;
		
		if (sendContext != null)
		{
			// Only the last element might be relevant.
			Object element = sendContext.getLastPathComponent();
			
			if (element instanceof SocketAddress)
			{
				context = (SocketAddress) element;
			}
		}
		
		if (context != null)
		{
			process(event.getData(), context);
		}
		else
		{
			super.handleQueuedOutputEvent(event);
		}
	}

	/**
	 * Writes the data to the given channel. This method will not return until 
	 * all the data from the buffer is written to the channel.
	 * 
	 * @param buffer	the data to write
	 * @param channel	the channel to write to
	 * @throws IOException if writing to the channel results in an exception
	 */
	protected void writeDataToChannel(
			ByteBuffer buffer, SocketChannel channel) throws IOException
	{
		try
		{
			//System.out.println(" Before TCP write remaining:" + buffer.remaining());
			//System.out.println(" TCP send buffer size: " + channel.socket().getSendBufferSize());
			int bytesToWrite = buffer.remaining();
			int bytesWritten = 0;
			int loops = 0;
			
			//---Loop while data is available
			while (bytesWritten < bytesToWrite)
			{
				int bytesThisWrite = channel.write(buffer);
				bytesWritten += bytesThisWrite;
				loops++;
				
				if (bytesThisWrite == 0)
				{
					// Give the receiver time to catch up and reduce the
					// risk of saturating the local processor.
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						// Nothing to do here
					}
				}
			}
			
			//System.out.println("bytes written loop: " + loops + " total:"+ bytesWritten );
		}
		catch (IOException ex)
		{
			String message = getFullyQualifiedName()
					+ " exception writing to connection " + channel.toString();
			sLogger.logp(Level.WARNING, CLASS_NAME, "writeDataToChannel",
					message, ex);
			
			// Rethrow exception so caller can handle it
			throw ex;
		}
	}
	
	/**
	 * Writes the contents of the Buffer to the connection(s) using 
	 * information from the given context.
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @param context the context associated with the buffer.
	 * @throws IOException if the write fails.
	 */
	public void process(ByteBuffer buffer, SocketAddress context)
	{
		if (isStarted())
		{
			int initialOffset = buffer.position();

			// Loop over the list of sockets for the given context.
			for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
			{
				SocketChannel channel = (SocketChannel) iter.next();
				
				if (context.equals(channel.socket().getRemoteSocketAddress()))
				{
	
					try
					{
						writeDataToChannel(buffer, channel);
		
						// reset position for the next write
						buffer.position(initialOffset);
					}
					catch (IOException ex)
					{
						fClientSockets.remove(channel);
						
						try
						{
							channel.close();
						}
						catch (IOException e)
						{
							// Since we are closing the socket anyway there is 
							// nothing else to do here.
						}
					}
				}
			}
		}
	}
	
	/**
	 * Writes the contents of the Buffer to the connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public void process(ByteBuffer buffer)
	{
		if (isStarted())
		{
			int initialOffset = buffer.position();
	
			// Loop over the list of sockets sending the buffer to each.
			for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
			{
				SocketChannel socket = (SocketChannel) iter.next();
	
				try
				{
					writeDataToChannel(buffer, socket);
	
					// reset position for the next write
					buffer.position(initialOffset);
				}
				catch (IOException ex)
				{
					fClientSockets.remove(socket);
					
					try
					{
						socket.close();
					}
					catch (IOException e)
					{
						// Since we are closing the socket anyway there is 
						// nothing else to do here.
					}
				}
			}
		}
	}

	/**
	 * Reads data from the specified channel and fires a 
	 * <code>fireConnectionDataEvent</code> method on the Connection.
	 * 
	 * @param channel the socket channel to read from
	 * @throws IOException
	 */
	protected void readDataFromChannel(SocketChannel channel) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(fReadBlockSize);
		int bytesRead = 0;
		int totalBytes = 0;
		try
		{
			//---Loop while data is available
			while ((bytesRead = channel.read(buffer)) > 0)
			{
				totalBytes += bytesRead;
			}

			if (totalBytes > 0)
			{
				buffer.flip();
				BufferHandle handle = new SingleUseBufferHandle(buffer);
				
				// Get a unique context for the input. If
				// we see this context for outgoing data we can limit the 
				// send to the specific port.
				Object context = channel.socket().getRemoteSocketAddress();
				handle.setContext(context);
				
				fireInputBufferEvent(new InputBufferEvent(this, handle));
			}
			
			if (bytesRead < 0)
			{
				//---Close Channel
				closeClientConnection(channel);
			}
		}
		catch (IOException e)
		{
			String message = getFullyQualifiedName() + " could not read connection ";
			sLogger.logp(Level.WARNING, CLASS_NAME, "ChannelReader.run",
					message, e);
			
			/*	When we arrive here it means a connection was probably lost with 
			 	the remote client we need to remove that connection so we 
			 	don't try to re connect to it and get the same 
			 	error over and over.
			 */

			closeClientConnection(channel);
		}	
	}
	
	/**
	 * Helper to support closing a channel connection to a client and 
	 * removing it from our 
	 * list of connections.
	 * 
	 * @param channel - Client channel to close 
	 * @throws ClosedByInterruptException 
	 */
	protected void closeClientConnection(SocketChannel channel)
	{
		try
		{
			channel.socket().close();
		}
		catch (IOException ex)
		{
			String message = getFullyQualifiedName() 
				+ " exception closing connection "
				+ channel.toString();
			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"closeClientConnection", 
					message, ex);
		}
		finally
		{
			// Make sure we remove the channel regardless of exception
			fClientSockets.remove(channel);			
		}
	}
	
	/**
	 * Close all client connections.
	 * @throws ClosedByInterruptException 
	 *
	 */
	private void closeClientConnections()
	{
		// Loop over the list of sockets closing each.
		for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
		{
			SocketChannel socket = (SocketChannel) iter.next();
			closeClientConnection(socket);
		}
	}

	/**
	 * Starts and configures the server socket to listen for new connections.
	 * @param port the port to listen for new connections on.
	 */
	private void startServerSocket(int port)
	{
		try
		{
			fServerSocket = ServerSocketChannel.open();
			InetSocketAddress isa = new InetSocketAddress(port);
			fServerSocket.socket().bind(isa);
			fServerSocket.configureBlocking(false);
			fServerSocket.register(fSelector, SelectionKey.OP_ACCEPT);
		}
		catch (IOException ex)
		{
			String message = getFullyQualifiedName() 
				+ " could not create server socket ";
			sLogger.logp(Level.WARNING, CLASS_NAME, "startServerSocket", 
					message, ex);
		}
	}

	/**
	 * Stops the server socket from listening for new connections.
	 * @throws IOException
	 */
	private void stopServerSocket() throws IOException
	{
		//---Interrupt the Server thread, if it's running
		if (fServerSocket != null)
		{
			fServerSocket.socket().close();
			fServerSocket.close();
			fServerSocket = null;
		}
	}

	//============================================================================
	// SET/GET
	//============================================================================

	/**
	 * Get the number of simultaneous client connections allowed for this 
	 * connection server.
	 * 
	 * @return Returns the number of connections allowed.
	 */
	public int getConnectionsAllowed()
	{
		return fConnectionsAllowed;
	}
	
	/**
	 * Set the number of simultaneous client connections allowed for this 
	 * connection server.
	 * 
	 * @param connectionsAllowed The number of connections allowed.
	 */
	public void setConnectionsAllowed(int connectionsAllowed)
	{
		if (connectionsAllowed >= 0)
		{
			int oldValue = fConnectionsAllowed;
			fConnectionsAllowed = connectionsAllowed;
			firePropertyChange(CONNECTIONS_ALLOWED_KEY, oldValue,
					fConnectionsAllowed);
		}
	}

	/**
	 * Get the default block size for each socket read.
	 * @return Returns the readBlockSize.
	 */
	public int getReadBlockSize()
	{
		return fReadBlockSize;
	}
	
	/**
	 * Set the default block size for each socket read.
	 * @param readBlockSize The readBlockSize to set.
	 */
	public void setReadBlockSize(int readBlockSize)
	{
		if (readBlockSize > 0)
		{
			int oldValue = fReadBlockSize;
			fReadBlockSize = readBlockSize;
			firePropertyChange(BLOCK_SIZE_KEY, oldValue,
					fReadBlockSize);
		}
	}
	
	/**
	 * Get the port number listening for new client connections.
	 * @return Returns the serverPort.
	 */
	public int getPort()
	{
		return fServerPort;
	}
	
	/**
	 * Set the port number listening for new client connections.
	 * @param serverPort The serverPort to set.
	 */
	public void setPort(int serverPort)
	{
		if (serverPort > 0)
		{
			int oldValue = fServerPort;
			fServerPort = serverPort;
			firePropertyChange(PORT_KEY, oldValue, fServerPort);
		}
	}
	
	//============================================================================
	// MISC SUPPORT
	//============================================================================

	/**
	 *  Returns a String representation of this Connection.
	 * 
	 *  @return A String representation of this Connection
	 */	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString());	
		
		stringRep.append("\nParameters ");
		stringRep.append("[ServerPort=" + getPort());
		stringRep.append(" AllowedConnections=" + getConnectionsAllowed());
		stringRep.append(" ReadSize=" + getReadBlockSize());
		stringRep.append("]");
		stringRep.append("\nConnection(s): ");
		
		// Loop over the list of sockets.
		for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
		{
			SocketChannel socket = (SocketChannel) iter.next();

			stringRep.append("/n/t" + socket.socket().getInetAddress());
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TcpServerConnection.java,v $
//  Revision 1.33  2006/05/07 21:44:26  jhiginbotham_cvs
//  Corrected handling of remote close and fixed accepting followon connections.
//
//  Revision 1.32  2006/04/18 14:03:07  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.31  2006/04/18 04:25:07  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.30  2006/03/23 17:44:15  smaher_cvs
//  Dealt with exceptions during connection closing causing an infinite loop.
//
//  Revision 1.29  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.28  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.27  2005/11/30 19:18:00  tames
//  JavaDoc updates only.
//
//  Revision 1.26  2005/11/17 16:13:45  tames
//  Modified class to use an instance of the CopyOnWriteArrayList class to
//  hold the client sockets.
//
//  Revision 1.25  2005/11/16 21:12:46  tames
//  Changed serviceConnection method to avoid a potential NullPointerException.
//
//  Revision 1.24  2005/10/21 20:29:09  tames_cvs
//  Added writeDataToChannel method that handles partial write cases better.
//  Also changed superclass to AbstractDualThreadedConnection.
//
//  Revision 1.23  2005/05/17 14:19:06  tames
//  Removed potential null pointer bug.
//
//  Revision 1.22  2005/05/13 04:11:57  tames
//  Changed context from InetAddress to a SocketAddress. Added
//  support for sending output to a specific client.
//
//  Revision 1.21  2005/05/12 21:36:55  tames_cvs
//  Added code to send a message to a specific client if the
//  OuptutMessageEvent specifies a client in the path.
//
//  Revision 1.20  2005/05/09 22:34:22  tames_cvs
//  Simplified and refactored based on the AbstractThreadedConnection
//  class. Added Javadoc descriptions.
//
//  Revision 1.19  2005/05/05 21:57:27  tames
//  Fixed bug when writing to multiple connections. The buffer was not being reset.
//
//  Revision 1.18  2005/05/04 17:20:09  tames_cvs
//  Added ConnectSource support as well as an allowConnection method.
//
//  Revision 1.17  2005/03/15 17:24:50  chostetter_cvs
//  Organized imports
//
//  Revision 1.16  2005/02/04 21:57:01  tames_cvs
//  Updated class to insert a context into the path associated with
//  Input events and to recognize the context on Output events. The
//  behavior to act on the context with Output events is incomplete.
//
//  Revision 1.15  2005/02/03 20:10:46  tames_cvs
//  Modified to set the context of a buffer so that receivers of the buffer
//  can correctly demultiplex buffers from multiple sources if necessary.
//
//  Revision 1.14  2005/02/01 05:29:51  jhiginbotham_cvs
//  Add reconnect support and improve overall robustness.
//
//  Revision 1.13  2005/01/25 23:43:09  tames
//  Removed debug print statement.
//
//  Revision 1.12  2005/01/12 13:20:44  jhiginbotham_cvs
//  Correct use of select operation. Resolve start/stop issues. Fix reaction to unexpected disconnects.
//
//  Revision 1.10  2004/12/02 20:11:58  tames_cvs
//  Added code to better handle missing descriptor parameters.
//
//  Revision 1.9  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.8  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.7  2004/09/27 20:39:30  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.6  2004/09/04 13:33:59  tames
//  added setDescriptor method
//
//  Revision 1.5  2004/08/23 14:00:59  tames
//  *** empty log message ***
//
//  Revision 1.4  2004/08/09 17:29:15  tames_cvs
//  Misc bug fixes
//
//  Revision 1.3  2004/08/04 21:15:34  tames_cvs
//  gave threads more meaningful names
//
//  Revision 1.2  2004/08/03 20:36:59  tames_cvs
//  Added Reader thread
//
//  Revision 1.1  2004/07/27 21:13:22  tames_cvs
//  intial implementation
//
