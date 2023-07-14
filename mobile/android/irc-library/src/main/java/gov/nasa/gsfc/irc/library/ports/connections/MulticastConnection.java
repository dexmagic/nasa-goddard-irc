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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.SingleUseBufferHandle;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

/**
 * A MulticastConnection is a connection that sends and receives IP multicast
 * datagram packets. This Connection will join a specified "group" of other 
 * multicast hosts on the internet. A multicast group is specified by a 
 * class D IP address and by a standard UDP port number. Class D IP addresses 
 * are in the range 224.0.0.0 to 239.255.255.255, inclusive. 
 * The address 224.0.0.0 is reserved and should not be used.
 * 
 * <p>When one sends a message to a multicast group, all subscribing recipients 
 * to that group and port receive the message (within the time-to-live range of 
 * the packet). 
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
 *      <td>group</td><td>224.0.0.1</td>
 *      <td align="left">The multicast group to join. An IP address in the 
 * 			range 224.0.0.0 to 239.255.255.255, inclusive. The address 
 * 			224.0.0.0 is reserved and should not be used.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>port</td><td>0</td>
 *      <td align="left">The local port to bind with. 
 * 			If this parameter is missing or 0 an unused port is 
 * 			dynamically assigned.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>timeToLive</td><td>1</td>
 *      <td align="left">The default time-to-live for multicast packets sent 
 * 			out on the connection.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>loopbackEnabled</td><td>false</td>
 *      <td align="left">The setting for local loopback of multicast datagrams.
 * 			If enabled this connection will also receive packets sent by this
 * 			connection.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>packetSize</td><td>1024</td>
 *      <td align="left">The default buffer size to use for reading data.</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for a MulticastConnection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="Multicast"&gt;
 *         &lt;Parameter name="group" value="228.5.6.7" /&gt;
 *         &lt;Parameter name="port" value="6789" /&gt;
 *         &lt;Parameter name="timeToLive" value="2" /&gt;
 *         &lt;Parameter name="loopbackEnabled" value="true" /&gt;
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
public class MulticastConnection extends AbstractThreadedConnection 
		implements Connection
{
	//---Logging support
	private final static String CLASS_NAME = MulticastConnection.class.getName();
	private final static Logger sLogger    = Logger.getLogger(CLASS_NAME);

	private final static String DEFAULT_NAME = "Multicast Connection";

	//----------- Descriptor keys ---------------
	/** The key for getting the group name to connect to from a Descriptor. */
	public static final String GROUP_KEY = "group";

	/** The key for getting the port number to use from a Descriptor. */
	public static final String PORT_KEY = "port";

	/** The key for getting the time-to-live number to use from a Descriptor. */
	public static final String TIME_TO_LIVE_KEY = "timeToLive";

	/** The key for getting the local loopback mode to use from a Descriptor. */
	public static final String LOOPBACK_KEY = "loopbackEnabled";

	/** The key for getting the packet size to use from a Descriptor. */
	public static final String PACKET_SIZE_KEY = "packetSize";

	/**
	 * Local port number to use for this port.
	 */
	private int fPort = 0;

	/**
	 * Multicast group to join.
	 */
	private String fGroup = "224.0.0.1";
	private InetAddress fGroupAddress;

	private int fPacketSize = 1024;
	private boolean fLoopbackEnabled = false;
	private MulticastSocket fMulticastSocket = null;
	private int fTimeToLive = 1;
	

	/**
	 *	Constructs a new MulticastConnection having a default name.
	 *
	 */
	
	public MulticastConnection()
	{
		super(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new MulticastConnection having the given base name.
	 *  
	 *  @param name The base name of the new MulticastConnection
	 */
	
	public MulticastConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new MulticastConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new MulticastConnection
	 */
	public MulticastConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);

		configureFromDescriptor(descriptor);
	}
	

	/**
	 * Writes the contents of the Buffer to the connection.
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
		
		if (fGroupAddress != null && fMulticastSocket != null)
		{
			// TODO Using hasArray and getting the backing array would avoid a copy here
			int length = buffer.remaining();
			byte[] bytes = new byte[length];
			buffer.get(bytes);
			
			DatagramPacket packet = 
				new DatagramPacket(bytes, length, fGroupAddress, fPort);

			fMulticastSocket.setTimeToLive(fTimeToLive);
			fMulticastSocket.send(packet);
		}
	}

	/**
	 * Opens a multicast connection.
	 */
	protected void openConnection()
	{
		try
		{
			// Set up multicast socket
			fGroupAddress = InetAddress.getByName(fGroup);
			fMulticastSocket = new MulticastSocket(fPort);
			fMulticastSocket.joinGroup(fGroupAddress);
			fMulticastSocket.setLoopbackMode(!fLoopbackEnabled);
			fMulticastSocket.setTimeToLive(fTimeToLive);
		    publishConnectionAdded(fGroupAddress);
		}
		catch (IOException ex)
		{
    		sLogger.logp(
        			Level.WARNING, CLASS_NAME, "startConnection", 
					"Exception creating MulticastSocket", ex);
		}
	}

	/**
	 * Closes a multicast connection.
	 */
	protected void closeConnection()
	{
		if (fMulticastSocket != null)
		{
			try
			{
				fMulticastSocket.leaveGroup(fGroupAddress);
				fMulticastSocket.close();
			}
			catch (IOException e)
			{
	    		sLogger.logp(
	        			Level.WARNING, CLASS_NAME, "stopConnection", 
						"Exception closing MulticastSocket", e);
			}
		}		
	}

	/**
	 * Reads data from the specified multicast socket and fires a 
	 * <code>fireConnectionDataEvent</code> method on the Connection.
	 * 
	 * @throws IOException
	 */
	protected void serviceConnection() 
		throws IOException
	{
		byte [] byteArray = new byte[fPacketSize];
		DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length);
		fMulticastSocket.receive(packet);
		int bytesRead = packet.getLength();

		if (bytesRead > 0)
		{
			ByteBuffer buffer = ByteBuffer.wrap(byteArray, 0, bytesRead);
			BufferHandle handle = new SingleUseBufferHandle(buffer);

			// Get a unique context for the input. If
			// we see this context for outgoing data we can limit the 
			// send to the specific port.
			Object context = packet.getAddress();
			handle.setContext(context);
			
			fireInputBufferEvent(new InputBufferEvent(this, handle));
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
					fPort = port;			
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
		
		//---Extract time to live number from descriptor
		String strTtlNumber = descriptor.getParameter(TIME_TO_LIVE_KEY);
		if (strTtlNumber != null)
		{
			try
			{
				int ttl = Integer.parseInt(strTtlNumber);
				
				if (ttl < 0 || ttl > 255)
				{
					String message = 
						"Attempt to build Connection with invalid time to live "
						+ strTtlNumber;
	
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the time to live
					fTimeToLive = ttl;			
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

		//---Extract group name from descriptor
		String strGroupName = descriptor.getParameter(GROUP_KEY);
		if (strGroupName != null)
		{		
			fGroup = strGroupName;
		}
		else
		{
			String message = GROUP_KEY
				+ " property not specified, using " + fGroup;

			sLogger.logp(
					Level.FINE, CLASS_NAME, 
					"configureFromDescriptor", message);
		}
		
		//---Extract loopback flag from descriptor
		String strLoopback = descriptor.getParameter(LOOPBACK_KEY);
		if (strLoopback != null)
		{
			fLoopbackEnabled = Boolean.valueOf(strLoopback).booleanValue();
		}
	}
	
	// --- Property get/set methods -------------------------------------------
	
	/**
	 * Get the multicast group.
	 * 
	 * @return the group Address.
	 */
	public String getGroup()
	{
		return fGroup;
	}
	
	/**
	 * Set the multicast group to join. An IP address in the range 224.0.0.0 to 
	 * 239.255.255.255, inclusive. The address 224.0.0.0 is reserved and 
	 * should not be used.
	 * 
	 * @param group The group to set.
	 */
	public synchronized void setGroup(String group)
	{
		if (group != null && !fGroup.equals(group))
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			String oldGroup = fGroup;
			fGroup = group;
			firePropertyChange("group", oldGroup, fGroup);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the multicast group.
	 * 
	 * @return the group Address.
	 */
	public InetAddress getGroupAddress()
	{
		return fGroupAddress;
	}
	
	/**
	 * Set the multicast group to join. An IP address in the range 224.0.0.0 to 
	 * 239.255.255.255, inclusive. The address 224.0.0.0 is reserved and 
	 * should not be used.
	 * 
	 * @param groupAddress The group Address to set.
	 */
	public synchronized void setGroupAddress(InetAddress groupAddress)
	{
		if (groupAddress != null && !fGroupAddress.equals(groupAddress))
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			InetAddress oldGroup = fGroupAddress;
			fGroupAddress = groupAddress;
			firePropertyChange("groupAddress", oldGroup, fGroupAddress);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the loopback enabled setting.
	 * 
	 * @return true if loopback enabled, false if disabled.
	 */
	public boolean isLoopbackEnabled()
	{
		return fLoopbackEnabled;
	}
	
	/**
	 * The setting for local loopback of multicast datagrams. If enabled this 
	 * connection will also receive packets sent by this connection.
	 * 
	 * @param enabled true to enable loop[back.
	 */
	public synchronized void setLoopbackEnabled(boolean enabled)
	{
		if (fLoopbackEnabled != enabled)
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			boolean oldValue = fLoopbackEnabled;
			fLoopbackEnabled = enabled;
			firePropertyChange("loopbackEnabled", oldValue, fLoopbackEnabled);
		
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
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
		int oldValue = fPacketSize;
		fPacketSize = size;
		firePropertyChange("packetSize", oldValue, fPacketSize);
	}
	
	/**
	 * Get the local port to bind with.
	 * 
	 * @return the port.
	 */
	public int getPort()
	{
		return fPort;
	}
	
	/**
	 * Set the local port to bind with. If this property is 0 an unused port is 
	 * dynamically assigned.
	 * 
	 * @param port the port to set.
	 */
	public synchronized void setPort(int port)
	{
		if (port != fPort)
		{
			boolean initiallyStarted = isStarted();
			
			stop();
			int oldValue = fPort;
			fPort = port;
			firePropertyChange("port", oldValue, fPort);
			
			// Start only if it was started before this method call
			if (initiallyStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get the default time-to-live for multicast packets sent 
	 * out on the connection.
	 * 
	 * @return the time-to-live.
	 */
	public int getTimeToLive()
	{
		return fTimeToLive;
	}
	
	/**
	 * Set the default time-to-live for multicast packets sent 
	 * out on the connection.
	 * 
	 * @param timeToLive the timeToLive to set.
	 */
	public void setTimeToLive(int timeToLive)
	{
		if (fTimeToLive != timeToLive)
		{
			int oldValue = timeToLive;
			fTimeToLive = timeToLive;
			firePropertyChange("timeToLive", oldValue, fTimeToLive);
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MulticastConnection.java,v $
//  Revision 1.11  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.10  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/05/13 04:09:21  tames
//  *** empty log message ***
//
//  Revision 1.8  2005/05/04 17:17:33  tames_cvs
//  Added ConnectSource support.
//
//  Revision 1.7  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.6  2005/03/03 18:40:34  tames_cvs
//  Fixed bug where the closeConnection method was not actually closing
//  the socket.
//
//  Revision 1.5  2005/03/01 00:08:52  tames
//  Added get/set methods as well as added javadoc comments and bug
//  fixes.
//
//  Revision 1.4  2005/02/25 22:35:51  tames_cvs
//  Updated comments only.
//
//  Revision 1.3  2005/02/25 05:21:32  tames
//  Changed to subclass AbstractThreadedConnection. Added property get and
//  set methods.
//
//  Revision 1.2  2005/02/24 04:34:29  tames
//  Tested and corrected loopback attribute.
//
//  Revision 1.1  2005/02/23 22:23:42  tames_cvs
//  Initial version
//
//