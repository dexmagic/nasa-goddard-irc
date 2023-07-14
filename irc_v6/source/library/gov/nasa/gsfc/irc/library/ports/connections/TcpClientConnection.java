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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
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
 * A TcpClientConnection is a connection component that connects to and 
 * reads data from a TCP server. Writing to 
 * a connection is done by the calling thread of the <code>process</code>
 * method. Reading data is done by a Thread owned by this instance 
 * (see 
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection 
 * AbstractThreadedConnection}). The block size of each read attempt is 
 * determined by the block
 * size property. This connection also provides reconnection support for
 * recovering connections dropped by remote systems while the client 
 * connection component is running. 
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
 *      <td>hostname</td><td>localhost</td>
 *      <td align="left">The remote host name to connect to</td>
 *  </tr>
 *  <tr align="center">
 *      <td>port</td><td>-</td>
 *      <td align="left">The remote port to connect to</td>
 *  </tr>
 *  <tr align="center">
 *      <td>readBlockSize</td><td>2048</td>
 *      <td align="left">The default read size to use for 
 * 			receiving data</td>
 *  </tr>
 *  <tr align="center">
 *      <td>retryEnabled</td><td>true</td>
 *      <td align="left">If true an attempt will be made to reconnect repeatedly
 * 			if the a connection is lost. If false the only way to reconnect is
 * 			to manually restart the Connection component.
 *      </td>
 *  </tr>
 *  <tr align="center">
 *      <td>retryDelay</td><td>1000</td>
 *      <td align="left">The delay in milliseconds between repeated reconnect
 * 			attempts when retryEnabled is true.
 *      </td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="TCP Client"&gt;
 *         &lt;Parameter name="hostname" value="localhost" /&gt;
 *         &lt;Parameter name="port" value="9999" /&gt;
 *         &lt;Parameter name="retryDelay" value="5000" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/08/01 18:32:13 $
 * @author	Troy Ames
 * @author	John Higinbotham (Emergent Space Technologies)
 */
public class TcpClientConnection extends AbstractThreadedConnection 
	implements Connection
{
	//============================================================================
	// CONSTANTS
	//============================================================================

	//---Logging support
	private final static String CLASS_NAME = TcpClientConnection.class.getName();
	private final static Logger sLogger    = Logger.getLogger(CLASS_NAME);

	//---TCP/IP paramaters
	public final static String HOST_KEY            = "hostname";
	public final static String PORT_KEY            = "port";
	public final static String BLOCK_SIZE_KEY      = "readBlockSize";
	public final static String RETRY_ENABLED_KEY   = "retryEnabled";
	public final static String RETRY_DELAY_KEY     = "retryDelay";
	
	//============================================================================
	// VARS
	//============================================================================

	//---Configuration
	private int fReadBlockSize    = 2048;
	private int fPort             = 9999;
	private String fHostName      = "localhost";
	private boolean fRetryEnabled = true;
	private long fRetryDelay      = 1000; //ms
	
	//---Default name
	private final static String DEFAULT_NAME = "TCP/IP Client Socket Connection";

	//---Socket
	private SocketChannel fSocket = null;

	//---Selector
	private Selector fReadSelector = null;

	//---Drop data warning flag
	private boolean fDropDataWarningIssued = false;
	
	//----------------------------------------------------------------------------
	
	//============================================================================
	// CONSTRUCTION
	//============================================================================

	/**
	 *	Constructs a new TcpClientConnection having a default name.
	 *
	 */
	public TcpClientConnection()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new TcpClientConnection having the given base name.
	 * 
	 *  @param name The base name of the new TcpClientConnection
	 **/

	public TcpClientConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 * Constructs a new TcpClientConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new TcpClientConnection
	 */
	public TcpClientConnection(ConnectionDescriptor descriptor)
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
	 * Causes this TcpClientConnection to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a ConnectionDescriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		String METHOD = "configureFromDescriptor()";
		
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
						"Attempt to build TcpClientConnection with invalid port number "
						+ strPortNumber;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message);
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
					"Attempt to build TcpClientConnection with invalid port number "
					+ strPortNumber;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
			}
		}
		
		//---Extract read block size from descriptor
		String strBlockSize = descriptor.getParameter(BLOCK_SIZE_KEY);
		if (strBlockSize != null)
		{
			try
			{
				int blockSize = Integer.parseInt(strBlockSize);
				
				if (blockSize < 0)
				{
					String message = 
						"Attempt to build TcpClientConnection with invalid number "
						+ blockSize;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message);
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
					"Attempt to build TcpClientConnection with invalid number "
					+ strBlockSize;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
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

			sLogger.logp(Level.FINE, CLASS_NAME, METHOD, message);
		}
		
		//---Extract retry delay from descriptor
		String strRetryDelay = descriptor.getParameter(RETRY_DELAY_KEY);
		if (strRetryDelay != null)
		{
			String message = 
				"Attempt to build TcpClientConnection with invalid retry delay "
				+ strRetryDelay;
			
			try
			{
				long delay = Long.parseLong(strRetryDelay);
				
				if (delay < 0)
				{
					sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message);
				}
				else 
				{
					fRetryDelay = delay;			
				}
			}
			catch (NumberFormatException e)
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
			}
		}

		//---Extract retry enabled from descriptor
		String strRetry = descriptor.getParameter(RETRY_ENABLED_KEY);
		sLogger.logp(Level.FINE, CLASS_NAME, METHOD, "Found parameter: " + RETRY_ENABLED_KEY + "=" + strRetry);
		
		if (strRetry != null)
		{
			boolean val = Boolean.valueOf(strRetry).booleanValue();
			
			//---Save value
			fRetryEnabled = val;	
		}	
	}

	//============================================================================
	// CONNECTION SUPPORT
	//============================================================================
	
	/**
	 * Starts a client socket connection.
	 */
	protected void openConnection()
	{
		sLogger.logp(Level.FINE, CLASS_NAME, "openConnection", "Opening connection...");
		
		// Opening the connection is done in the serviceConnection method.
	}
	
	/**
	 * Restarts a client socket connection.
	 */
	private void restartConnection()
	{
		sLogger.logp(Level.FINE, CLASS_NAME, "restartConnection", "Restarting connection...");
		
		InetSocketAddress address = new InetSocketAddress(fHostName, fPort);
		
		while (fSocket == null)
		{
			try
			{
				fReadSelector = Selector.open();

				SocketChannel channel = SocketChannel.open();
				channel.connect(address);

				//---Configure channel
				channel.configureBlocking(false);
				fSocket = channel;
				
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = getFullyQualifiedName() 
						+ " established connection with " 
						+ channel.socket().getInetAddress();
					sLogger.logp(Level.FINE, CLASS_NAME, "restartConnection", 
							message);
				}
				
				while(!fSocket.finishConnect())
				{
					sleep(100);
				}
				
				fSocket.register(fReadSelector, SelectionKey.OP_READ);
				publishConnectionAdded(null);
				
				//---Clear warning flag
				fDropDataWarningIssued = false;
			}
			catch (IOException e)
			{
				String message = e.getLocalizedMessage() + " " 
					+ getFullyQualifiedName()
					+ " could not connect to " 
					+ address;
				sLogger.logp(Level.WARNING, CLASS_NAME, "restartConnection",
						message);

				//---Control rate of connection retries
				sleep(fRetryDelay);
			}
		}
	}

	/**
	 * Closes a client socket connection.
	 */
	protected void closeConnection()
	{
		sLogger.logp(Level.FINE, CLASS_NAME, "closeConnection", 
				"Closing connection...");
		
		if (fSocket != null)
		{
			try
			{
				//fSocket.socket().close();
				fSocket.close();
				fSocket = null;
				
				fReadSelector.wakeup().close();
			}
			catch (IOException e)
			{
				String message = getFullyQualifiedName() + " could not close ";
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"closeConnection", message, e);
			}
		}
	}
	
	//============================================================================
	// READ/WRITE
	//============================================================================

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
	 * Writes the contents of the Buffer to the connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public synchronized void process(ByteBuffer buffer)
	{
		if (isStarted())
		{
			try
			{
				if (fSocket != null && fSocket.isConnected())
				{
					writeDataToChannel(buffer, fSocket);
				}
				else
				{
					//TODO: Should we queue data?
					
					//---Prevent flooding log file with redundant warnings
					if (!fDropDataWarningIssued)
					{
						sLogger.logp(Level.WARNING, CLASS_NAME, "process()", 
							" Outgoing data will be dropped until connection established with server socket!");
						fDropDataWarningIssued = true;
					}
				}			
			}
			catch (IOException ex)
			{
				try
				{
					fSocket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				fSocket = null;
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
	protected void readDataFromChannel(SocketChannel channel) 
		throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(fReadBlockSize);
		int bytesRead = 0;
		int totalBytes = 0;
		//System.out.println("socket buffer size:" + channel.socket().getReceiveBufferSize());
		
		if (channel.isConnected())
		{
			try
			{
				//---Loop while data is available
				while ((bytesRead = channel.read(buffer)) > 0)
				{
					totalBytes += bytesRead;
					//System.out.println("bytes read: " 
					//	+ bytesRead + " total: " + totalBytes );
				}
				//System.out.println(" total: " + totalBytes );

				if (totalBytes > 0)
				{
					buffer.flip();
					BufferHandle handle = new SingleUseBufferHandle(buffer);

					if (sLogger.isLoggable(Level.FINE))
					{
						ByteBuffer byteBuffer = (ByteBuffer)handle.getBuffer();
						
						
						StringBuffer buf = new StringBuffer();
						while (byteBuffer.remaining() > 0)
						{
							buf.append((char) byteBuffer.get());
						}
						sLogger.logp(Level.FINE, "TcpClientConnection",
								"readDataFromChannel(SocketChannel)",
								"Received message over socket - buffer="
										+ buf.toString());
						byteBuffer.rewind();
					}

					fireInputBufferEvent(new InputBufferEvent(this, handle));
				}
			}
			catch (IOException e)
			{
				channel.close();
				String message = getFullyQualifiedName() 
					+ " exception reading from connection "
					+ channel.toString();
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"readDataFromChannel", message, e);

				throw new IOException(e.toString());
			}
			
			if (bytesRead < 0)
			{
				throw new IOException(
						"Connection appears to have be closed by remote host");
			}
		}
	}
	
	/**
	 * Listens for new connections or data on client connections and calls the 
	 * <code>readDataFromChannel</code> method on the Connection.
	 */
	protected void serviceConnection()
	{
		try
		{
			// Establish or reestablish connection
			if (fSocket == null)
			{
				restartConnection();

				if (isWaiting())
				{
					declareActive();
				}
			}
			
			// Block until a channel has a pending operation
			fReadSelector.select();
			
			//---Check if channel has a pending read operation
			Set selectedSet = fReadSelector.selectedKeys();
			
			if (selectedSet != null && selectedSet.size() > 0)	
			{	
				Iterator iter = fReadSelector.selectedKeys().iterator();
				
				while (iter.hasNext())
				{
					SelectionKey key = (SelectionKey) iter.next();
					//---Remove key from selected set
					iter.remove();

					//---Check if there is data to read from connection
					if (key.isReadable())
					{
						SocketChannel channel = (SocketChannel) key.channel();
						readDataFromChannel(channel);
					}
					else
					{
						System.out.println("encountered unexpected key in checkChannelStatus");
					}
				}
			}	
			else
			{
				// There's probably nothing to do in this case. This may occur when
				// a select is interrupted.
			}
		}
		catch (IOException e)
		{
			sLogger.logp(Level.WARNING, CLASS_NAME, "serviceConnection", 
					getFullyQualifiedName() + " exception servicing connection ",
					e);

			if (fRetryEnabled)
			{
				declareWaiting();
				
				//---Clean up old connection
				closeConnection();
			}
			else
			{
				stop();
			}
		}	
	}

	//============================================================================
	// SET/GET
	//============================================================================

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
			firePropertyChange(BLOCK_SIZE_KEY, oldValue, fReadBlockSize);
		}
	}
	
	/**
	 * Get the remote server port number.
	 * @return Returns the serverPort.
	 */
	public int getPort()
	{
		return fPort;
	}
	
	/**
	 * Set the port number listening for new client connections.
	 * @param serverPort The serverPort to set.
	 */
	public void setPort(int serverPort)
	{
		if (serverPort > 0)
		{
			int oldValue = fPort;
			fPort = serverPort;
			firePropertyChange(PORT_KEY, oldValue, fPort);
		}
	}
	
	/**
	 * Get the remote server hostname.
	 * @return Returns the remote server hostname
	 */
	public String getHostname()
	{
		return fHostName;
	}
	
	/**
	 * Determine if connection retries are enabled.
	 * @return Returns the retryEnabled.
	 */
	public boolean isRetryEnabled()
	{
		return fRetryEnabled;
	}
	/**
	 * Get connection retry delay.
	 * @return Returns the retryDelay.
	 */
	public long getRetryDelay()
	{
		return fRetryDelay;
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
		stringRep.append("[RemoteHost=" + fHostName);
		stringRep.append(" RemotePort=" + getPort());
		stringRep.append(" ReadSize=" + getReadBlockSize());
		stringRep.append("]");
		stringRep.append("\nConnection: ");
		
		if (fSocket != null)
		{
			stringRep.append(fSocket.socket().getInetAddress());
		}
		
		return (stringRep.toString());
	}

	/**
	 * Cause thread to sleep.
	 * 
	 * @param ms Milliseconds to sleep
	 */
	private final void sleep(long ms)
	{
		if (ms > 0)
		{
			try
			{
				Thread.sleep(ms);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TcpClientConnection.java,v $
//  Revision 1.29  2006/08/01 18:32:13  smaher_cvs
//  Added logging message read from buffer (as a string).
//
//  Revision 1.28  2006/05/10 02:34:48  jhiginbotham_cvs
//  Eliminate redundant data drop warning.
//
//  Revision 1.27  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.26  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.25  2005/11/18 22:21:10  tames_cvs
//  Minor changes to wait for a channel connection to finish before going on.
//
//  Revision 1.24  2005/11/18 18:29:26  tames
//  Changed fReadSelector.close() to fReadSelector.wakeup().close()
//  as an attempt to avoid a potential deadlock.
//
//  Revision 1.23  2005/11/17 22:59:48  tames_cvs
//  Changed the default read block size to 2048.
//
//  Revision 1.22  2005/11/16 21:12:46  tames
//  Changed serviceConnection method to avoid a potential NullPointerException.
//
//  Revision 1.21  2005/10/21 20:28:08  tames_cvs
//  Added writeDataToChannel method that handles partial write cases better.
//
//  Revision 1.20  2005/05/13 04:10:23  tames
//  *** empty log message ***
//
//  Revision 1.19  2005/05/09 22:35:18  tames_cvs
//  Added code to close the selector when closing the connection.
//
//  Revision 1.18  2005/05/09 18:59:47  tames_cvs
//  Simplified and refactored based on the AbstractThreadedConnection
//  class. Added Javadoc descriptions.
//
//  Revision 1.17  2005/05/09 14:22:58  tames_cvs
//  Fixed potential sync problem with registering a connection and
//  attempting to read from it.
//
//  Revision 1.16  2005/05/04 17:18:20  tames_cvs
//  Added ConnectSource support.
//
//  Revision 1.15  2005/04/22 22:27:07  tames_cvs
//  Commented out debug code.
//
//  Revision 1.14  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.13  2005/03/15 17:24:50  chostetter_cvs
//  Organized imports
//
//  Revision 1.12  2005/02/01 05:29:51  jhiginbotham_cvs
//  Add reconnect support and improve overall robustness.
//
//  Revision 1.11  2005/01/12 13:20:44  jhiginbotham_cvs
//  Correct use of select operation. Resolve start/stop issues. Fix reaction to unexpected disconnects.
//
//  Revision 1.9  2004/12/14 03:25:14  jhiginbotham_cvs
//  Update to read data, when available.
//
//  Revision 1.8  2004/12/02 20:06:58  tames_cvs
//  Updated javadoc and added code to better handle missing descriptor
//  parameters.
//
//  Revision 1.7  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.6  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.5  2004/09/27 20:39:30  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.4  2004/09/04 13:33:59  tames
//  added setDescriptor method
//
//  Revision 1.3  2004/08/09 17:29:15  tames_cvs
//  Misc bug fixes
//
//  Revision 1.2  2004/08/04 21:15:34  tames_cvs
//  gave threads more meaningful names
//
//  Revision 1.1  2004/08/03 20:35:52  tames_cvs
//  Initial Version
//
