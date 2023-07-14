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
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Timer;
import java.util.TimerTask;
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
 * A UdpPeerConnection is a UDP connection where the address of the remote
 * peer or server is known. If the connection mode property is set to true
 * this Connection will attempt to "connect" to the specified remote
 * address in order to avoid the overhead of the security checks that would 
 * otherwise be performed as part of every send and receive operation. 
 * This connection is compatible communicating with 
 * another UdpPeerConnection, UdpPeerlessConnection, UdpServerConnection, or 
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
 *      <td>hostname</td><td>-</td>
 *      <td align="left">The remote host name to connect to</td>
 *  </tr>
 *  <tr align="center">
 *      <td>remotePort</td><td>-</td>
 *      <td align="left">The remote port to connect to</td>
 *  </tr>
 *  <tr align="center">
 *      <td>localPort</td><td>0</td>
 *      <td align="left">The local port to bind with for receiving packets. 
 * 			If this parameter is missing or 0 an unused port is 
 * 			dynamically assigned.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>packetSize</td><td>1024</td>
 *      <td align="left">The default UDP packet size to use for 
 * 			receiving data</td>
 *  </tr>
 *  <tr align="center">
 *      <td>expirationPeriod</td><td>0</td>
 *      <td align="left">If greater than 0, empty "I am alive" packets will be
 * 			sent to the remote host at the specified period in milliseconds. 
 *			This parameter should be set if this connection is 
 * 			communicating with a UdpServerConnection.
 *      </td>
 *  </tr>
 *  <tr align="center">
 *      <td>connectedMode</td><td>false</td>
 *      <td align="left">If true all communication to and from the remote peer 
 * 			must be done over the same remote address and port. This mode has the 
 * 			advantage that the required security checks for the remote address  
 * 			need only be done once at the initial connection. Otherwise
 * 			every packet sent and received will incur this overhead.
 *      </td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="UDP Peer"&gt;
 *         &lt;Parameter name="hostname" value="localhost" /&gt;
 *         &lt;Parameter name="remotePort" value="9999" /&gt;
 *         &lt;Parameter name="localPort" value="9000" /&gt;
 *         &lt;Parameter name="expirationPeriod" value="5000" /&gt;
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
public class UdpPeerConnection extends AbstractThreadedConnection 
		implements Connection
{
	//---Logging support
	private final static String CLASS_NAME = UdpPeerConnection.class.getName();
	private final static Logger sLogger    = Logger.getLogger(CLASS_NAME);

	public final static String DEFAULT_NAME = "UDP Peer Connection";

	private static Timer sTimer = null;

	//----------- Descriptor keys ---------------
	/** The key for getting the host name to connect to from a Descriptor. */
	public static final String HOST_KEY = "hostname";

	/** The key for getting the remote port number to use from a Descriptor. */
	public static final String REMOTE_PORT_KEY = "remotePort";

	/** The key for getting the local receive port number to use from a Descriptor. */
	public static final String LOCAL_PORT_KEY = "localPort";

	/** The key for getting the packet size to use from a Descriptor. */
	public static final String PACKET_SIZE_KEY = "packetSize";

	/** The key for getting the connected mode to use from a Descriptor. */
	public static final String CONNECTED_KEY = "connectedMode";

	/** The key for getting the expiration period to use from a Descriptor. */
	public static final String EXPIRATION_KEY = "expirationPeriod";

	/**
	 * Remote destination port number to use for this port.
	 */
	private int fRemotePort;

	/**
	 * Local receive port number to use for this port.
	 */
	private int fLocalPort = 0;

	/**
	 * Host name to connect to.
	 */
	private String fHostName;
	private InetSocketAddress fRemoteAddress;

	private int fPacketSize = 1024;
	private long fExpirePeriod = 0;
	private boolean fConnectedMode = false;
	private DatagramChannel fDatagramChannel = null;
	
	// Cached receive buffer and handle
	private ByteBuffer fReceiveBuffer;
	private BufferHandle fReceiveBufferHandle;
	private boolean fPacketSizeChange = false;
	

	/**
	 *	Constructs a new UdpPeerConnection having a default name.
	 *
	 */
	
	public UdpPeerConnection()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new UdpPeerConnection having the given base name.
	 *  
	 *  @param name The base name of the new UdpPeerConnection
	 */
	
	public UdpPeerConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new UdpPeerConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new UdpPeerConnection
	 */
	public UdpPeerConnection(ConnectionDescriptor descriptor)
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
			InetSocketAddress remoteAddress) 
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
	 * Writes the contents of the Buffer to the connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public synchronized void process(ByteBuffer buffer) throws IOException
	{
		if (isStarted())
		{
			writeDataToChannel(buffer, fDatagramChannel, fRemoteAddress);
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
	 * Opens and initializes a connection.
	 */
	protected void openConnection()
	{
		// Initialize receive buffer
		fReceiveBuffer = ByteBuffer.allocate(fPacketSize);
		fReceiveBufferHandle = new DefaultBufferHandle(fReceiveBuffer);

		try
		{
			// Set up channel to remote peer or server
			fRemoteAddress = new InetSocketAddress(fHostName, fRemotePort);

			fDatagramChannel = DatagramChannel.open();
			
			// Bind channel to local port
			DatagramSocket socket = fDatagramChannel.socket();
			socket.bind(new InetSocketAddress(fLocalPort));			
			
			if (fConnectedMode)
			{
				fDatagramChannel.connect(fRemoteAddress);
			}

			// Check if the connection needs to be renewed at regular intervals
			if (fExpirePeriod > 0)
			{
				final ByteBuffer tempBuffer = ByteBuffer.allocate(0);

				// This empty buffer announcement might only be useable if 
				// connecting to a UDP server connection.
				fDatagramChannel.send(tempBuffer, fRemoteAddress);

				// Create a timer task to renew connection to server
				if (sTimer == null)
				{
					sTimer = new Timer(true);
				}

				sTimer.schedule(new TimerTask()
				{
					public void run()
					{
                        try
                        {
                            fDatagramChannel.send(tempBuffer, fRemoteAddress);
                        }
                        catch (IOException ex)
                        {
                    		sLogger.logp(
                    			Level.WARNING, CLASS_NAME, "TimerTask.run", 
								"Exception renewing DataChannel connection", ex);
                        }
					}
				}, fExpirePeriod, fExpirePeriod);
			}
			
			publishConnectionAdded(fRemoteAddress);
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
		// Cancel timer task
		if (sTimer != null)
		{
			sTimer.cancel();
			sTimer = null;
		}

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

		int bytesRead = 0;
		
		fDatagramChannel.receive(fReceiveBuffer);
		bytesRead = fReceiveBuffer.position();

		if (bytesRead > 0)
		{
			fReceiveBuffer.flip();

			// Set a unique context for the input
			fReceiveBufferHandle.setContext(fRemoteAddress);
			
			fireInputBufferEvent(new InputBufferEvent(this, fReceiveBufferHandle));
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
		
		//---Extract remote port number from descriptor
		String strPortNumber = descriptor.getParameter(REMOTE_PORT_KEY);
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
					fRemotePort = port;			
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
		
		//---Extract local receive port number from descriptor
		strPortNumber = descriptor.getParameter(LOCAL_PORT_KEY);
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

		//---Extract hostname from descriptor
		String strHostName = descriptor.getParameter(HOST_KEY);
		if (strHostName != null)
		{		
			fHostName = strHostName;
		}
		else
		{
			String message = HOST_KEY
				+ " property not specified, using " + fHostName;

			sLogger.logp(
					Level.FINE, CLASS_NAME, 
					"configureFromDescriptor", message);
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
	 * Get the current connection mode setting.
	 * 
	 * @return Returns true if connection mode enabled, false otherwise.
	 */
	public boolean isConnectedMode()
	{
		return fConnectedMode;
	}
	
	/**
	 * Set the connection mode. If true all communication to and from the 
	 * remote peer must be done over the same remote address and port. 
	 * This mode has the advantage that the required security checks for 
	 * the remote address need only be done once at the initial connection. 
	 * Otherwise every packet sent and received will incur this overhead. May 
	 * force a Connection stop and restart.
	 * 
	 * @param enabled The new connected mode setting.
	 */
	public synchronized void setConnectedMode(boolean enabled)
	{
		if (enabled != fConnectedMode)
		{
			boolean initiallyStarted = isStarted();
			
			stop();

			boolean oldValue = fConnectedMode;
			fConnectedMode = enabled;
			firePropertyChange("connectedMode", oldValue, fConnectedMode);

			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
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
	 * If greater than 0, empty "I am alive" packets will be 
	 * sent to the remote host at the specified period in milliseconds. 
	 * This parameter should be set if this connection is 
	 * communicating with a UdpServerConnection.
	 * 
	 * @param period The expire period to set.
	 */
	public synchronized void setExpirationPeriod(long period)
	{
		if (period != fExpirePeriod)
		{
			boolean initiallyStarted = isStarted();
			
			stop();

			long oldValue = fExpirePeriod;
			fExpirePeriod = period;
			firePropertyChange("expirationPeriod", oldValue, fExpirePeriod);

			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the remote host name.
	 * 
	 * @return the hostName.
	 */
	public String getHostName()
	{
		return fHostName;
	}
	
	/**
	 * Set the remote host name to connect to.
	 * 
	 * @param hostName The host name to set.
	 */
	public synchronized void setHostName(String hostName)
	{
		if (fHostName != null && !fHostName.equals(hostName))
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			String oldValue = fHostName;
			fHostName = hostName;
			firePropertyChange("hostName", oldValue, fHostName);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the local port that is used for receiving packets.
	 * 
	 * @return the local port.
	 */
	public int getLocalPort()
	{
		return fLocalPort;
	}
	
	/**
	 * The local port to bind with for receiving packets. If set to 0 an 
	 * unused port is dynamically assigned.
	 * 
	 * @param localPort The local port to set.
	 */
	public synchronized void setLocalPort(int localPort)
	{
		if (localPort != fLocalPort)
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			int oldValue = fLocalPort;
			fLocalPort = localPort;
			firePropertyChange("localPort", oldValue, fLocalPort);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the remote address to connect to.
	 * 
	 * @return the remote address.
	 */
	public InetSocketAddress getRemoteAddress()
	{
		return fRemoteAddress;
	}
	
	/**
	 * Set the remote address to connect to.
	 * 
	 * @param remoteAddress The remote address.
	 */
	public synchronized void setRemoteAddress(InetSocketAddress remoteAddress)
	{
		fRemoteAddress = remoteAddress;
		if (remoteAddress != fRemoteAddress)
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			Object oldValue = fRemoteAddress;
			fRemoteAddress = remoteAddress;
			firePropertyChange("remoteAddress", oldValue, fRemoteAddress);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the remote port to connect to.
	 * 
	 * @return the remote port.
	 */
	public int getRemotePort()
	{
		return fRemotePort;
	}
	
	/**
	 * Set the remote port to connect to.
	 * 
	 * @param remotePort the remote port.
	 */
	public synchronized void setRemotePort(int remotePort)
	{
		if (remotePort != fRemotePort)
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			int oldValue = fRemotePort;
			fRemotePort = remotePort;
			firePropertyChange("remotePort", oldValue, fRemotePort);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: UdpPeerConnection.java,v $
//  Revision 1.13  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.12  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.11  2005/10/21 20:29:36  tames_cvs
//  Added writeDataToChannel method that handles partial write cases better.
//
//  Revision 1.10  2005/05/13 04:10:23  tames
//  *** empty log message ***
//
//  Revision 1.9  2005/05/04 17:20:58  tames_cvs
//  Added ConnectSource support.
//
//  Revision 1.8  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.7  2005/03/03 20:18:40  tames_cvs
//  Removed some redundant logging statements.
//
//  Revision 1.6  2005/03/01 00:08:51  tames
//  Added get/set methods as well as added javadoc comments and bug
//  fixes.
//
//  Revision 1.5  2005/02/25 22:37:48  tames_cvs
//  Fixed bug related to setting the port from a descriptor.
//
//  Revision 1.4  2005/02/25 05:20:25  tames
//  Changed to subclass AbstractThreadedConnection.
//
//  Revision 1.3  2005/02/23 22:24:06  tames_cvs
//  Added context to received data.
//
//  Revision 1.2  2005/02/22 23:28:24  tames_cvs
//  Updated comments.
//
//  Revision 1.1  2005/02/22 21:46:40  tames_cvs
//  Initial version ported to V6 with some additional capabilities.
//
//