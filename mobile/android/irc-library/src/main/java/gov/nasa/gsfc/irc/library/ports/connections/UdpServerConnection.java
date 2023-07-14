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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.DefaultBufferHandle;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

/**
 * A UdpServerConnection is a UDP connection that publishes data to all 
 * registered listeners. An interested listener registers by sending this
 * Connection a UDP packet to the specified server port. All initial 
 * registration and subsequent data packets can only be received by this single
 * server port. Depending on the "connectedMode" property, outgoing messages 
 * will either be sent out over this shared server port or over a connected
 * channel dedicated to the remote client address. Connected mode has the 
 * advantage of being more efficient since the security checks on the remote
 * address are only performed once at the initial registration and not for 
 * every packet as would otherwise be required. The disadvantage of connected 
 * mode is that a unique send port is required for every client and the client 
 * must be able to send and receive packets over two distinct ports. Connection
 * mode is off (false) by default.
 *  
 * <p>This connection is compatible 
 * with any UDP type connection that can send a packet to a known address.
 * If the client is of type UdpPeerConnection than an additional restriction
 * is that this server and the client cannot both be in connected mode for 
 * successful bidirectional communication.
 * 
 * <P>The configuration of this connection is specified by a ConnectionDescriptor.
 * The table below gives the configuration parameters that this connection uses.
 * If the parameter is missing then the default value will be used. If there
 * is not a default value specified then the parameter is required to be
 * in the descriptor unless where noted. 
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Key</th>
 *      <th>Default</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>port</td><td>-</td>
 *      <td align="left">The local port to listen for interested peers or 
 *           clients</td>
 *  </tr>
 *  <tr align="center">
 *      <td>packetSize</td><td>1024</td>
 *      <td align="left">The default UDP packet size to use for sending data</td>
 *  </tr>
 *  <tr align="center">
 *      <td>expirationPeriod</td><td>0</td>
 *      <td align="left">If greater than 0, registered listeners will be 
 * 			removed from the list if they have not renewed their registration
 * 			before the specified period in milliseconds has expired. This 
 * 			provides a mechanism to cleanup if listeners silently go away.
 * 			(Not currently enforced)
 *      </td>
 *  </tr>
 *  <tr align="center">
 *      <td>connectedMode</td><td>false</td>
 *      <td align="left">If true a unique local unused port and channel will be 
 * 			allocated to send outgoing packets to each registered listener.
 * 			Note received data must still be sent by the client to the well
 * 			known server port.
 *      </td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="UDP Server"&gt;
 *         &lt;Parameter name="port" value="9000" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:15 $
 * @author	tames
 **/
public class UdpServerConnection extends AbstractThreadedConnection 
	implements Connection
{
	//---Logging support
	private final static String CLASS_NAME = UdpServerConnection.class.getName();
	private final static Logger sLogger    = Logger.getLogger(CLASS_NAME);

	public final static String DEFAULT_NAME = "UDP Server Socket Connection";
	
	private static Timer sTimer = null;

	//----------- Descriptor keys ---------------
	/** The key for getting the local receive port number to use from a Descriptor. */
	public static final String PORT_KEY = "port";

	/** The key for getting the packet size to use from a Descriptor. */
	public static final String PACKET_SIZE_KEY = "packetSize";

	/** The key for getting the expiration period to use from a Descriptor. */
	public static final String EXPIRATION_KEY = "expirationPeriod";

	/** The key for getting the connected mode to use from a Descriptor. */
	public static final String CONNECTED_KEY = "connectedMode";

	/** Local receive port number to use for this port. */
	private int fLocalPort = 0;

	private int fPacketSize = 1024;
	private long fExpirePeriod = 0;
	private boolean fConnectedMode = false;
	private DatagramChannel fServerChannel;

	// Cached receive buffer and handle
	private ByteBuffer fReceiveBuffer;
	private BufferHandle fReceiveBufferHandle;
	
	private Map fConnectionMap = new HashMap();
	private Vector fBadConnections = new Vector(5, 5);
	private DatagramChannel[] fConnections = new DatagramChannel[0];
	private SocketAddress[] fRemoteAddress = new SocketAddress[0];
	private boolean fInvalidConnectionArray = false;
	private boolean fPacketSizeChange = false;


	/**
	 *	Constructs a new UdpServerConnection having a default name.
	 *
	 */
	
	public UdpServerConnection()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new UdpServerConnection having the given base name.
	 *  
	 *  @param name The base name of the new UdpServerConnection
	 */
	
	public UdpServerConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new UdpServerConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new UdpServerConnection
	 */
	public UdpServerConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);

		configureFromDescriptor(descriptor);
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
			boolean segmentedBuffer = false;
			int originalLimit = 0;
			
			if (bytesToWrite > fPacketSize)
			{
				segmentedBuffer = true;
				originalLimit = buffer.limit();
				buffer.limit(buffer.position()+ fPacketSize);
			}
			
			int bytesWritten = 0;
			
			//---Loop while data is available
			while (bytesWritten < bytesToWrite)
			{
				int bytesThisWrite = channel.send(buffer, remoteAddress);
				bytesWritten += bytesThisWrite;
				
				if (segmentedBuffer)
				{
					// Update limit for next write
					int position = buffer.position();
					int newLimit = position + fPacketSize;
					
					if (newLimit > originalLimit)
					{
						newLimit = originalLimit;
					}
					
					buffer.limit(newLimit);
				}
				
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
	 * Writes the contents of the Buffer to all connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public synchronized void process(ByteBuffer buffer) throws IOException
	{
		if (!isStarted())
		{
    		sLogger.logp(
    			Level.WARNING, CLASS_NAME, "process", 
				getName()
				+ " connection could not process buffer because "
				+ " connection is " + getState());
			return;
		}
		
		int initialOffset = buffer.position();
		int initialLimit = buffer.limit();

		// Check if connection array needs to be updated
		if (fInvalidConnectionArray)
		{
			updateConnectionArray();
		}

		// Iterate over known connections
		for (int i=0; i < fRemoteAddress.length; i++)
		{
			try
			{
				writeDataToChannel(buffer, fConnections[i], fRemoteAddress[i]);
			}
			catch (IOException e)
			{
				// Mark the connection for later removal
				fBadConnections.add(fRemoteAddress[i]);
				fInvalidConnectionArray = true;
			}

			// reset position for the next write
			buffer.position(initialOffset);
			buffer.limit(initialLimit);
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
	protected boolean allowConnection(SocketAddress address)
	{
		return true;
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
			fServerChannel = DatagramChannel.open();
			fServerChannel.socket().bind(new InetSocketAddress(fLocalPort));
		}
		catch (IOException e)
		{
			String message = "Exception creating listening DatagramChannel";
			
			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"ServerSocketManager.start", message, e);
		}
	}

	/**
	 * Closes and releases any resources held by a connection.
	 */
	protected void closeConnection()
	{
		// Cancel timer task
//		if (sTimer != null)
//		{
//			sTimer.cancel();
//			sTimer = null;
//		}
	
		if (fServerChannel != null)
		{
			try
			{
	            fServerChannel.close();
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
	 * Reads and processes data from the Connection. This method will be called 
	 * repeatedly from a dedicated Thread. This method should block if there is 
	 * nothing to read on the Connection.
	 * 
	 * @throws IOException
	 */
	protected void serviceConnection() throws IOException
	{
		DatagramChannel channel = fServerChannel;
		SocketAddress address = null;

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
		
		address = fServerChannel.receive(fReceiveBuffer);

		if (!fConnectionMap.containsKey(address)
				&& allowConnection(address))
		{
			if (fConnectedMode)
			{
				// Create and connect a new channel
				channel = DatagramChannel.open();
				channel.connect(address);
			}
			
			synchronized(fConnectionMap)
			{							
				sLogger.logp(
						Level.INFO, CLASS_NAME, 
						"serviceConnection", 
						"Connection made from:" + address 
						+ " on channel:" + channel);

				fConnectionMap.put(address, channel);
				fInvalidConnectionArray = true;
			}
			
			publishConnectionAdded(address);
		}
		else
		{
			String message = "Received duplicate address:" + address;
			
			sLogger.logp(
					Level.FINE, CLASS_NAME, 
					"ServerSocketManager.run", message);
		}
		
		int bytesRead = fReceiveBuffer.position();

		if (bytesRead > 0)
		{
			fReceiveBuffer.flip();

			// Set a unique context for the input. If
			// we see this context for outgoing data we can limit the 
			// send to the specific port.
			fReceiveBufferHandle.setContext(address);
			
			fireInputBufferEvent(new InputBufferEvent(this, fReceiveBufferHandle));
		}
	}
	
	/**
	 * Removes all known bad connections from the subscriber array.
	 */
	private synchronized void updateConnectionArray()
	{
		synchronized(fConnectionMap)
		{
			// Remove known bad connections
			if (!fBadConnections.isEmpty())
			{
				Iterator iter = fConnectionMap.entrySet().iterator();

				// Loop over all current connections removing the ones
				// flagged as bad.
				while (iter.hasNext())
				{
					Map.Entry entry = (Map.Entry)iter.next();

					// Check if connection is a bad connection
					if (fBadConnections.remove(entry.getKey()))
					{
						iter.remove();
					}
				}

				fBadConnections.clear();
			}

			// Create new arrays with remaining good connections
			int size = fConnectionMap.size();
			DatagramChannel[] channelArray = new DatagramChannel[size];
			SocketAddress[] addressArray = new SocketAddress[size];
			
			Iterator iter = fConnectionMap.entrySet().iterator();
			int index = 0;
			
			// Loop over all current connections and initialize arrays.
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry)iter.next();
				System.out.println(" Entry " + index + ": " + entry);
				channelArray[index] = (DatagramChannel) entry.getValue();
				addressArray[index] = (SocketAddress) entry.getKey();
				index++;
			}
				
			fRemoteAddress = addressArray;
			fConnections = channelArray;
			fInvalidConnectionArray = false;
			System.out.println("Update Connections length:" + fConnections.length);
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
		
		//---Extract port number from descriptor
		String strPortNumber = descriptor.getParameter(PORT_KEY);
		if (strPortNumber != null)
		{
			try
			{
				int port = Integer.parseInt(strPortNumber);
				
				if (port < 0)
				{
					String message = 
						"Attempt to build Connection with invalid port number "
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
					"Attempt to build Connection with invalid remote port "
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

		//---Extract expiration period from descriptor
		String strExpiration = descriptor.getParameter(EXPIRATION_KEY);
		if (strExpiration != null)
		{
			String message = 
				"Attempt to build Connection with invalid expiration period"
				+ strExpiration;
			
			try
			{
				long period = Long.parseLong(strExpiration);
				
				if (period < 0)
				{
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					fExpirePeriod = period;			
				}
			}
			catch (NumberFormatException e)
			{
				sLogger.logp(
						Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}

		//---Extract connected mode flag from descriptor
		String strConnectedMode = descriptor.getParameter(CONNECTED_KEY);
		if (strConnectedMode != null)
		{
			fConnectedMode = Boolean.valueOf(strConnectedMode).booleanValue();
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
	
	/**
	 * Get the local port to bind with.
	 * 
	 * @return the port.
	 */
	public int getPort()
	{
		return fLocalPort;
	}
	
	/**
	 * Set the local port to bind with. If this property is 0 an unused port is 
	 * dynamically assigned.
	 * 
	 * @param port the port to set.
	 */
	public synchronized void setPort(int port)
	{
		if (port != fLocalPort)
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			int oldValue = fLocalPort;
			fLocalPort = port;
			firePropertyChange("port", oldValue, fLocalPort);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the current connection mode setting.
	 * 
	 * @return Returns true if connection mode enabled, false otherwise.
	 */
	public boolean isConnectedMode()
	{
		return fConnectedMode;
	}
	
	/**
	 * Set the connection mode. If true a unique local unused port and channel 
	 * will be allocated to send outgoing packets to each registered listener.
	 * 
	 * @param enabled The new connected mode setting.
	 */
	public void setConnectedMode(boolean enabled)
	{
		if (enabled != fConnectedMode)
		{
			boolean oldValue = fConnectedMode;
			fConnectedMode = enabled;
			firePropertyChange("connectedMode", oldValue, fConnectedMode);
		}
	}
	
	/**
	 * Get the expiration period.
	 * 
	 * @return the expiration period.
	 * @see #setExpirationPeriod(long)
	 */
	public long getExpirationPeriod()
	{
		return fExpirePeriod;
	}
	
	/**
	 * Set the expiration period.
	 * If greater than 0, registered listeners will be removed from the list 
	 * if they have not renewed their registration before the specified period 
	 * in milliseconds has expired. This provides a mechanism to cleanup if 
	 * listeners silently go away. (Not currently enforced)
	 * 
	 * @param period The expire period to set.
	 */
	public void setExpirationPeriod(long period)
	{
		if (period != fExpirePeriod)
		{
			long oldValue = fExpirePeriod;
			fExpirePeriod = period;
			firePropertyChange("expirationPeriod", oldValue, fExpirePeriod);
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: UdpServerConnection.java,v $
//  Revision 1.11  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.10  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/11/18 22:23:39  tames_cvs
//  Added logic to handle cases when the buffer to send is larger than the
//  packet size.
//
//  Revision 1.8  2005/10/21 20:31:01  tames_cvs
//  Added writeDataToChannel method that handles partial write cases better.
//
//  Revision 1.7  2005/05/13 04:10:23  tames
//  *** empty log message ***
//
//  Revision 1.6  2005/05/04 17:20:41  tames_cvs
//  Added ConnectSource support as well as an allowConnection method.
//
//  Revision 1.5  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.4  2005/03/03 20:20:03  tames_cvs
//  Removed some redundant logging statements.
//
//  Revision 1.3  2005/03/01 00:08:52  tames
//  Added get/set methods as well as added javadoc comments and bug
//  fixes.
//
//  Revision 1.2  2005/02/25 04:58:01  tames
//  Removed unnecessary import statement.
//
//  Revision 1.1  2005/02/22 23:28:55  tames_cvs
//  Initial direct port to V6.
//
//