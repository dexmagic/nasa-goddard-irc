//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.library.ports.adapters;

import gov.nasa.gsfc.commons.xml.XmlException;
import gov.nasa.gsfc.commons.xml.XmlUtil;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractPortAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;
import org.jdom.output.Format;

/**
 * Base class for BasisSet Tcp input adapters.  Handles common descriptor
 * and socket processing.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: AbstractBasisSetTcpInputAdapter.java,v 1.2 2006/05/23 16:07:03 smaher_cvs Exp $
 * @author	$Author: smaher_cvs $
 */
public abstract class AbstractBasisSetTcpInputAdapter extends AbstractPortAdapter
{
	static final String CLASS_NAME = BasisSetTcpJavaSerializationInputAdapter.class.getName();
	
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger
			.getLogger(AbstractBasisSetTcpInputAdapter.class.getName());
	protected SocketChannel fSocket = null;
	public static final String HOST_KEY = "hostname";
	public static final String PORT_KEY = "port";
	public static final String RETRY_DELAY_KEY = "retryDelay";
	public static final String BASIS_BUNDLE_SIZE_KEY = "basisBundleSize";
	public static final String REMOTE_NAMES_KEY = "useRemoteBundleNames";

	public static final String BUFFER_SIZE_KEY = "bufferSizeBytes";
	protected int fPort = 9999;
	protected int fBasisBundleSize = 5000;
	protected String fHostName = "localhost";
	protected long fRetryDelay = 1000;
	protected boolean fUseRemoteSourceName = false;

	protected Output fOutput;

	ServiceThread fServiceThread = null;

	/**
	 * Size of underlying XDR decoding buffer
	 */
	protected int fBufferSizeBytes = AbstractBasisSetTcpOutputAdapter.DEFAULT_TCP_BUFFER_SIZE;

	/**
	 * @param name
	 */
	public AbstractBasisSetTcpInputAdapter(String name)
	{
		super(name);
	}

	/**
	 * @param descriptor
	 */
	public AbstractBasisSetTcpInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}

	/**
	 * Look for a data request element in the descriptor of this component
	 * and send it through the socket if found.  This will tell the server
	 * the desired basis set requests (assuming a BasisSetTcpGenericOutputAdapter
	 * is used).
	 * <p>
	 * @param socketChannel 
	 * @see BasisSetTcpGenericOutputAdapter.DataSetRequest.E_DATA_SET_REQUEST
	 * 
	 */
	protected void sendDataSetRequestIfPresent(SocketChannel socketChannel)
	{
		InputAdapterDescriptor descriptor = (InputAdapterDescriptor) getDescriptor();
		Element element = descriptor.getElement();
		if (element != null)
		{
			boolean dataSetRequestExists = false;
			Object dataSetRequestE = null;
			try
			{
				// Get data request element.  If it doesn't exist
				// an XMLException is thrown.				
				dataSetRequestE = XmlUtil.selectNode(element, "//" + BasisSetTcpGenericOutputAdapter.DataSetRequest.E_DATA_SET_REQUEST, null);
				dataSetRequestExists = true;
			} catch (XmlException e1)
			{
				// Ignore - no data set request exists
			}
			
			if (dataSetRequestExists == true)
			{
				// Get string version of DataSetRequest element (and children)
				// and send to server (instance of BasisSetTcpGenericOutputAdapter 
				// presumably).
				String dataSetRequestXmlString = XmlUtil.domToString(
						dataSetRequestE, Format.getCompactFormat());
				if (socketChannel != null)
				{
					int size = dataSetRequestXmlString.getBytes().length;
					try
					{
						ByteBuffer buf = ByteBuffer.allocate(4 + size);
						buf.asIntBuffer().put(size);
						buf.position(4);
						buf.put(dataSetRequestXmlString.getBytes());
//						System.out.println("XXX send size " + size);
						buf.flip();
						socketChannel.write(buf);
						buf.flip();
//						String mesg = "XXX Sent data set request to server: "
//								+ new String(buf.array());
//						System.out.println(mesg);
					} catch (IOException e)
					{
						throw new IllegalStateException(
								"Exception while writing data request to socket: " + e.getMessage());
					}
				} else
				{
					throw new IllegalStateException("Socket is null");
				}
			}
		}
		else
		{
			sLogger.severe("Descriptor.getElement() returned null");
		}
	}

	/**
	 * Opens a client socket connection.
	 */
	protected void openConnection()
	{
		sLogger.logp(Level.FINE, CLASS_NAME, "openConnection", "Opening connection...");
		
		InetSocketAddress address = new InetSocketAddress(fHostName, fPort);
		
		while (fSocket == null)
		{
			try
			{
				//---Configure channel
				SocketChannel channel = SocketChannel.open();
				channel.configureBlocking(true);
				channel.connect(address);
	
				fSocket = channel;
				
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = getFullyQualifiedName() 
						+ " established connection with " 
						+ channel.socket().getInetAddress();
					sLogger.logp(Level.FINE, CLASS_NAME, "openConnection", 
							message);
				}
				
				while(!fSocket.finishConnect())
				{
					try
					{
						Thread.sleep(100);
					} catch (InterruptedException e)
					{
						// Don't really care
						e.printStackTrace();
					}
				}				
			}
			catch (IOException e)
			{
				String message = e.getLocalizedMessage() + " " 
					+ getFullyQualifiedName()
					+ " could not connect to " 
					+ address;
				sLogger.logp(Level.WARNING, CLASS_NAME, "openConnection",
						message);
	
				//---Control rate of connection retries
				try
				{
					Thread.sleep(fRetryDelay);
				} catch (InterruptedException e1)
				{
					// Don't really care					
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Starts the adapter for receiving data. This 
	 * starts a reader thread.
	 */
	public synchronized void start()
	{
		if (!isStarted() && !isKilled())
		{
			super.start();
			fOutput.start();
			startServiceThread();
		}
	}

	/**
	 * Stops the connection. This stops the reader thread.
	 */
	public synchronized void stop()
	{
		if (!isStopped() && !isKilled())
		{
			super.stop();
			fOutput.stop();
			stopServiceThread();
			closeConnection();
		}
	}

	/**
	 * Close a connection and associated streams
	 */
	abstract protected void closeConnection();
	
	/**
	 * Service a connection
	 * @throws IOException
	 */
	abstract protected void serviceConnection() throws IOException;

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
	 * Starts the reader thread to listen for new data.
	 */
	private void startServiceThread()
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
	 * Stops the reader thread from listening for new data.
	 */
	private void stopServiceThread()
	{
		//---Interrupt reader thread, if it's running
		if(fServiceThread != null && fServiceThread.isAlive())
		{
			fServiceThread.interrupt();
			fServiceThread = null;
		}
	}

	/**
	 * Sets the Descriptor of this Component to the given Descriptor. The
	 * Component will in turn be (re)configured in accordance with the given
	 * Descriptor.
	 * 
	 * @param descriptor A Descriptor
	 */
	public void setDescriptor(Descriptor descriptor)
	{
	    super.setDescriptor(descriptor);
	    
		if (descriptor instanceof InputAdapterDescriptor)
		{
			// Set the name of the output to a name that reflects this component.
			// Bundle Ids received by this input adapter will reflect this name.
			String name = descriptor.getFullyQualifiedName();
	
			try
			{
				fOutput.setName(name);
			}
			catch (PropertyVetoException e)
			{
				String message = 
					"Attempt to set output name to " + name;
	
				sLogger.logp(
					Level.WARNING, CLASS_NAME, "setDescriptor", message, e);
			}
			
			configureFromDescriptor((InputAdapterDescriptor) descriptor);
		}
	}

	/**
	 * Causes this adapter to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a InputAdapterDescriptor
	 */
	protected void configureFromDescriptor(InputAdapterDescriptor descriptor)
	{
		String METHOD = "configureFromDescriptor";
		
		if (descriptor == null)
		{
			return;
		}
		
		//---Extract useRemoteBundleNames from descriptor
		String strUseRemoteNamesFlag = descriptor.getParameter(REMOTE_NAMES_KEY);
		if (strUseRemoteNamesFlag != null)
		{		
			fUseRemoteSourceName = 
				Boolean.valueOf(strUseRemoteNamesFlag).booleanValue();
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
						"Attempt to build InputAdapterDescriptor with invalid port number "
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
					"Attempt to build InputAdapterDescriptor with invalid port number "
					+ strPortNumber;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
			}
		}
		String bufferSizeNumber = descriptor.getParameter(BUFFER_SIZE_KEY);		
		if (bufferSizeNumber != null)
		{
			try
			{
				int size = Integer.parseInt(bufferSizeNumber);
				
				if (size < 0)
				{
					String message = 
						"Attempt to build TcpServerConnection with invalid buffer size "
						+ bufferSizeNumber;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					fBufferSizeBytes = size;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build TcpServerConnection with invalid buffer size"
					+ bufferSizeNumber;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
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
	
			sLogger.logp(Level.FINE, CLASS_NAME, METHOD, message);
		}
		
		//---Extract retry delay from descriptor
		String strRetryDelay = descriptor.getParameter(RETRY_DELAY_KEY);
		if (strRetryDelay != null)
		{
			String message = 
				"Attempt to build InputAdapterDescriptor with invalid retry delay "
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
		
		//---Extract basis bundle size from descriptor
		String strBasisBundleSize = descriptor.getParameter(BASIS_BUNDLE_SIZE_KEY);
		if (strBasisBundleSize != null)
		{
			try
			{
				int basisBundleSize = Integer.parseInt(strBasisBundleSize);
				
				if (basisBundleSize < 0)
				{
					String message = 
						"Attempt to build InputAdapterDescriptor with invalid basis bundle size "
						+ strBasisBundleSize;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message);
				}
				else 
				{
					//---Set the basisBundleSize number
					fBasisBundleSize = basisBundleSize;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build InputAdapterDescriptor with invalid basis bundle size"
					+ strBasisBundleSize;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
			}
		}
		
	}

	/**
	 * The ServiceThread class listens for new data on the connection.
	**/
	protected class ServiceThread extends Thread
	{
		/**
		 * Logger for this class
		 */
		private final Logger sLogger = Logger.getLogger(ServiceThread.class
				.getName());

		/**
		 * Creates a new ServiceThread.
		 */
		public ServiceThread()
		{			
			setName(getFullyQualifiedName() + " Service Thread");
		}

		/**
		 * Continously listens for new data on client connections.
		 */
		public void run()
		{
			while(!isInterrupted())
			{
				try
				{
					openConnection();
					
					sendDataSetRequestIfPresent(fSocket);					
					
					serviceConnection();
				}
				catch (ClosedByInterruptException e)
				{
					String message = "Exception from interrupted Connection";
					sLogger.logp(
							Level.FINE, CLASS_NAME, 
							"ServiceThread.run", message, e);
					
					// The thread interrupt status should already be set due
					// to this exception, but it appears that this is not
					// always the case so set it anyway.
					interrupt();
				}
				catch (IOException e)
				{
					if (isInterrupted())
					{
						// Since interrupting the thread may result in 
						// an IOException, we can assume that this is 
						// an expected exception and not log it as a warning.
						String message = "Exception from Connection";
						sLogger.logp(
								Level.FINE, CLASS_NAME, 
								"ServiceThread.run", message, e);
					}
					else
					{
						// Since we don't know the cause of the exception
						// log it as a warning.
						String message = "Exception from Connection";
						sLogger.logp(
								Level.WARNING, CLASS_NAME, 
								"ServiceThread.run", message, e);
					}
				}
				
				closeConnection();
				interrupt();
			}
		}
	} // End ServiceThread Class
	
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractBasisSetTcpInputAdapter.java,v $
//	Revision 1.2  2006/05/23 16:07:03  smaher_cvs
//	Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//	
//	Revision 1.1  2006/05/18 14:05:19  smaher_cvs
//	Checkpointing working generic TCP basis set adapters.
//	