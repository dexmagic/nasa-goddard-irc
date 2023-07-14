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
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.DefaultBufferHandle;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferEvent;

/**
 * A UdpPeerlessConnection is a UDP connection that does not maintain any 
 * destination information. This connection is compatible communicating with 
 * another UdpPeerlessConnection, UdpPeerConnection, or 
 * any UDP type connection that has a known address.
 * 
 * <P>The configuration of this connection is specified by a ConnectionDescriptor.
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
 *      <td>localPort</td><td>0</td>
 *      <td align="left">The local port to bind with for receiving packets. 
 * 			If this parameter is missing or 0 an unused port is 
 * 			dynamically assigned</td>
 *  </tr>
 *  <tr align="center">
 *      <td>packetSize</td><td>1024</td>
 *      <td align="left">The default UDP packet size to use for receiving 
 * 				data</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connectionn:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="UDP"&gt;
 *         &lt;Parameter name="localPort" value="9000" /&gt;
 *         &lt;Parameter name="packetSize" value="2048" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:25:07 $
 * @author	tames
 **/
public class UdpPeerlessConnection extends AbstractThreadedConnection 
		implements Connection
{
	//---Logging support
	private final static String CLASS_NAME = 
			UdpPeerlessConnection.class.getName();
	private final static Logger sLogger = Logger.getLogger(CLASS_NAME);

	public final static String DEFAULT_NAME = "UDP Peerless Connection";
	
	//----------- Descriptor keys ---------------
	/** The key for getting the local receive port number to use from a Descriptor. */
	public static final String LOCAL_PORT_KEY = "localPort";

	/** The key for getting the packet size to use from a Descriptor. */
	public static final String PACKET_SIZE_KEY = "packetSize";

	/**
	 * Local receive port number to use for this port.
	 */
	private int fLocalPort = 0;

	private int fPacketSize = 1024;
	private DatagramChannel fDatagramChannel = null;

	// Cached receive buffer and handle
	private ByteBuffer fReceiveBuffer;
	private BufferHandle fReceiveBufferHandle;
	private boolean fPacketSizeChange = false;
	

	/**
	 * Constructs a new UdpPeerlessConnection having a default name.
	 */	
	public UdpPeerlessConnection()
	{
		super(DEFAULT_NAME);
	}

	/**
	 * Constructs a new UdpPeerlessConnection having the given base name.
	 * 
	 * @param name The base name of the new UdpPeerlessConnection
	 */	
	public UdpPeerlessConnection(String name)
	{
		super(name);
	}	

	/**
	 * Constructs a new UdpPeerlessConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new UdpPeerlessConnection
	 */
	public UdpPeerlessConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);

		configureFromDescriptor(descriptor);
	}
	
	/**
	 * Gets the buffer and the destination 
	 * <code>SocketAddress</code> from the event and sends the data to the 
	 * specified address. The address is expected to be the last element of 
	 * the context path specifed in the event.
	 *
	 * @param event event containing a buffer and a path.
	 */
	public void handleOutputBufferEvent(OutputBufferEvent event)
	{
		if (isStarted())
		{
			try
			{
				Object pathElement = 
					event.getSendContext().getLastPathComponent();
				
				if (pathElement instanceof SocketAddress)
				{
					process(event.getData(), (SocketAddress) pathElement);
				}
			}
			catch (IOException e)
			{
				String message = "Exception when writing data to the connection";
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"handleOutputBufferEvent", message, e);
			}
		}
	}

	/**
	 * Writes the data to the given channel. This method will not return until 
	 * all the data from the buffer is written to the channel.
	 * 
	 * @param buffer	the data to write
	 * @param channel	the channel to write to
	 * @param remoteAddress 	the address to which the data is to be sent
	 * @throws IOException if writing to the channel results in an exception
	 */
	protected void writeDataToChannel(
			ByteBuffer buffer, DatagramChannel channel, 
			SocketAddress remoteAddress) 
	throws IOException
	{
		try
		{
			int bytesToWrite = buffer.remaining();
			int bytesWritten = 0;
			
			//---Loop while data is available
			while (bytesWritten < bytesToWrite)
			{
				int bytesThisWrite = channel.send(buffer, remoteAddress);
				bytesWritten += bytesThisWrite;
				
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
	 * Writes the contents of the Buffer to the connection using the 
	 * specified address.
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @param address ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public synchronized void process(ByteBuffer buffer, SocketAddress address) 
		throws IOException
	{
		if (isStarted())
		{
			writeDataToChannel(buffer, fDatagramChannel, address);
		}
		else
		{
    		sLogger.logp(
    			Level.WARNING, CLASS_NAME, "process", 
				getName()
				+ " connection could not process buffer because "
				+ " connection is " + getState());
		}
	}

	/**
	 * Throws IOException since for this type of Connection the address of the
	 * destination must also be known. Use 
	 * {@link #process(ByteBuffer, SocketAddress)} instead.
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if this method is called.
	 * @see #process(ByteBuffer, SocketAddress)
	 */
	public void process(ByteBuffer buffer) throws IOException
	{
		throw new IOException(
				"This method should not be called for this Connection type");
	}

	/**
	 * Opens and initializes a connection.
	 */
	protected void openConnection()
	{
		// Initialize receive buffer
		fReceiveBuffer = ByteBuffer.allocate(fPacketSize);
		fReceiveBufferHandle = new DefaultBufferHandle(fReceiveBuffer);

		try
		{
			fDatagramChannel = DatagramChannel.open();
			
			// Bind channel to local port if it has been set
			if (fLocalPort > 0)
			{
				DatagramSocket socket = fDatagramChannel.socket();
				socket.bind(new InetSocketAddress(fLocalPort));			
			}
			
			publishConnectionAdded(null);
		}
		catch (IOException ex)
		{
    		sLogger.logp(
        			Level.WARNING, CLASS_NAME, "openConnection", 
					"Exception creating DatagramChannel", ex);
		}
	}

	/**
	 * Closes and releases any resources held by a connection.
	 */
	protected void closeConnection()
	{
		if (fDatagramChannel != null)
		{
			try
			{
				fDatagramChannel.disconnect();
				fDatagramChannel.close();
			}
			catch (IOException e)
			{
	    		sLogger.logp(
	        			Level.WARNING, CLASS_NAME, "closeConnection", 
						"Exception closing DatagramChannel", e);
			}
		}		

		fReceiveBuffer = null;
		fReceiveBufferHandle = null;
	}

	/**
	 * Reads data from the specified channel and fires a 
	 * <code>fireConnectionDataEvent</code> method on the Connection.
	 * 
	 * @throws IOException
	 */
	protected void serviceConnection() 
		throws IOException
	{
		// Create a new buffer if a previous listener is still using it.
		if (fReceiveBufferHandle.isInUse() || fPacketSizeChange)
		{
			fReceiveBuffer = ByteBuffer.allocate(fPacketSize);
			fReceiveBufferHandle = new DefaultBufferHandle(fReceiveBuffer);
			fPacketSizeChange = false;
		}
		else
		{
			// Reset buffer for next read
			fReceiveBuffer.position(0);
			fReceiveBuffer.limit(fPacketSize);
		}
		
		SocketAddress remoteAddress = fDatagramChannel.receive(fReceiveBuffer);
		int bytesRead = fReceiveBuffer.position();

		if (bytesRead > 0)
		{
			fReceiveBuffer.flip();
			
			// Set a unique context for the input. If
			// we see this context for outgoing data we can limit the 
			// send to the specific port.
			fReceiveBufferHandle.setContext(remoteAddress);
			
			fireInputBufferEvent(new InputBufferEvent(this, fReceiveBufferHandle));
		}
	}

	// --- Property get/set methods -------------------------------------------
	
	/**
	 * Get the default buffer size used for reading data.
	 * 
	 * @return the packet size.
	 */
	public int getPacketSize()
	{
		return fPacketSize;
	}
	
	/**
	 * Set the default buffer size to use for reading data.
	 * 
	 * @param size the packet size to set.
	 */
	public void setPacketSize(int size)
	{
		if (size != fPacketSize)
		{
			int oldValue = fPacketSize;
			fPacketSize = size;
			fPacketSizeChange = true;
			firePropertyChange("packetSize", oldValue, fPacketSize);
		}
	}

	// --- Descriptor related methods ------------------------------------------
	
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
	 * Causes this Connection to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a ConnectionDescriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		if (descriptor == null)
		{
			return;
		}
		
		//---Extract local receive port number from descriptor
		String strPortNumber = descriptor.getParameter(LOCAL_PORT_KEY);
		if (strPortNumber != null)
		{
			try
			{
				int port = Integer.parseInt(strPortNumber);
				
				if (port < 0)
				{
					String message = 
						"Attempt to build Connection with invalid receive port "
						+ strPortNumber;
	
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the port number
					fLocalPort = port;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build Connection with invalid port number "
					+ strPortNumber;
	
				sLogger.logp(
						Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
		
		//---Extract packet size from descriptor
		String strPacketSize = descriptor.getParameter(PACKET_SIZE_KEY);
		if (strPacketSize != null)
		{
			try
			{
				int packetSize = Integer.parseInt(strPacketSize);
				
				if (packetSize < 0)
				{
					String message = 
						"Attempt to build Connection with invalid number "
						+ packetSize;
	
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the packet size
					fPacketSize = packetSize;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build Connection with invalid number "
					+ strPacketSize;
	
				sLogger.logp(
						Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: UdpPeerlessConnection.java,v $
//  Revision 1.9  2006/04/18 04:25:07  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.8  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.7  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/10/21 20:30:39  tames_cvs
//  Added writeDataToChannel method that handles partial write cases better.
//
//  Revision 1.5  2005/05/13 04:10:23  tames
//  *** empty log message ***
//
//  Revision 1.4  2005/05/04 17:21:16  tames_cvs
//  Added ConnectSource support.
//
//  Revision 1.3  2005/03/03 20:19:36  tames_cvs
//  Added a disconnect call when closing a connection.
//
//  Revision 1.2  2005/03/01 00:08:52  tames
//  Added get/set methods as well as added javadoc comments and bug
//  fixes.
//
//  Revision 1.1  2005/02/25 22:38:02  tames_cvs
//  Initial version.
//
//
//