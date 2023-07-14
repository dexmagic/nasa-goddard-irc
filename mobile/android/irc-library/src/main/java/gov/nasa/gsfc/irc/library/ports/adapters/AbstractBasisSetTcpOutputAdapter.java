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

package gov.nasa.gsfc.irc.library.ports.adapters;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.commons.system.io.ServerSocketManager;
import gov.nasa.gsfc.commons.system.io.SocketConnectionHandler;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.InputListener;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleIdFactory;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractPortAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.BasisSetTcpEncoder;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for Output Adapters that encode Basis Sets over a
 * TCP connection.  This class provides most of the TCP server 
 * property and execution management.  Subclasses generally provide
 * the actual encoding the basis sets (e.g., Java serialization
 * and XDR).
 * <p>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/05/23 16:07:03 $
 * @author Troy Ames
 * @author Steve Maher
 */
public abstract class AbstractBasisSetTcpOutputAdapter extends AbstractPortAdapter
	implements SocketConnectionHandler, InputListener
{
	private static final String CLASS_NAME = AbstractBasisSetTcpOutputAdapter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String DEFAULT_NAME = "BasisSet TCP Serializer";
	
	public static final int DESCRIPTOR_PACKET = 1;
	public static final int DATA_PACKET = 2;
	public static final int ERROR_PACKET = 3;	
	
	public static final byte BYTE_TYPE = 0;
	public static final byte CHAR_TYPE = 1;
	public static final byte SHORT_TYPE = 2;
	public static final byte INT_TYPE = 3;
	public static final byte LONG_TYPE = 4;
	public static final byte FLOAT_TYPE = 5;
	public static final byte DOUBLE_TYPE = 6;
	public static final byte OBJECT_TYPE = 7;

	public static final String KEEP_KEY = "queueKeepMode";
	public static final String KEEP_ALL_STR = "keepAll";
	public static final String KEEP_CAPACITY_KEY = "queueCapacity";

	
	private Map fDescriptorMap = new HashMap();

	/**
	 * The input used to retrieve basis sets that will be
	 * outputted on the TCP socket.
	 */
	protected Input fInput;	
	protected String fKeepMode = KEEP_ALL_STR;
	protected int fCapacity = 5;
	
	/**
	 * Size of underlying buffers, if appropriate.  For
	 * example, this defines the fragment size in the
	 * XdrTcpEncoder. 
	 */
	public static final int DEFAULT_TCP_BUFFER_SIZE = 1024*512;	
	protected int fBufferSizeBytes = DEFAULT_TCP_BUFFER_SIZE;

	//---TCP/IP paramaters
	public final static String PORT_KEY                = "port";
	public final static String CONNECTIONS_ALLOWED_KEY = "connectionsAllowed";
	public final static String BUFFER_SIZE_KEY = "bufferSizeBytes";	
	private int fConnectionsAllowed = 5;
	private int fServerPort         = 9999;
	private ServerSocketManager fServerManager = null;

	//---Open clients
	private List fClientSockets = new CopyOnWriteArrayList();

	
	/**
	 * Constructs a new BasisSetTcpJavaSerializationOutputAdapter having a default name.
	 *
	 */
	
	public AbstractBasisSetTcpOutputAdapter()
	{
		this(DEFAULT_NAME);
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationOutputAdapter having the given name.
	 *
	 */
	
	public AbstractBasisSetTcpOutputAdapter(String name)
	{
		super(name);
		
		fInput = new DefaultInput();
		fInput.addInputListener(this);
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationOutputAdapter configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new OutputAdapter
	 */
	public AbstractBasisSetTcpOutputAdapter(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
		
		fInput = new DefaultInput();
		fInput.addInputListener(this);
		
		configureFromDescriptor();
	}


	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.library.ports.adapters.publishers.BasisSetPublisher#getBasisBundleDescriptorMap()
	 */
	public Map getBasisBundleDescriptorMap()
	{
		return Collections.unmodifiableMap(fDescriptorMap); 
	}
	
	/**
	 *  Causes this Adapter to start. The adapter will start to listen for 
	 *  client connections and DataSetEvents.
	 */
	public void start()
	{
		if (!isStarted())
		{
			super.start();
			
			if (fServerManager != null)
			{
				fServerManager.stop();
			}
			
			fServerManager = new ServerSocketManager(this, fServerPort);
			fServerManager.start();
			fInput.start();
		}
	}

	/**
	 * Causes this Adapter to stop. The adapter will stop to listening for
	 * client connections and DataSetEvents.
	 */	
	public void stop()
	{
		if (!isStopped())
		{
			
			// Close client handlers
			for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
			{
				((BasisSetTcpEncoder) iter.next()).interrupt();				
			}

			
			if (fServerManager != null)
			{
				fServerManager.stop();
				fServerManager = null;
			}

			fInput.stop();
			super.stop();
		}
	}
	
	/**
	 * Sets the Descriptor of this Adapter to the given Descriptor. The Adapter
	 * will in turn be (re)configured in accordance with the given Descriptor.
	 * 
	 * @param descriptor A Descriptor
	 */	
	public void setDescriptor(Descriptor descriptor)
	{
		if (descriptor instanceof OutputAdapterDescriptor)
		{
			super.setDescriptor(descriptor);
			
			configureFromDescriptor();
		}
	}

	/**
	 * Allocate the encoder for this adapter
	 * @param socket The open client socket
	 * @param keepMode The keep policy for queue
	 * @param capacity The maximum number of DataSetEvents that will be queued up.
	 * @param bufferSizeBytes The size of underlying buffers, if appropriate
	 * @param errorMesg If non-null, will be sent back to client and encoding will be aborted
	 * @return
	 */
	abstract protected BasisSetTcpEncoder allocateDataSetWriter(Socket socket, String keepMode, 
			int capacity, int bufferSizeBytes, String errorMesg, List inputs);
	
	/**
	 * Accepts this client connection if the limit for client connections 
	 * has not been reached.
	 *
	 * @param   socket  the socket to publish data on.
	 */
	public void acceptConnection(Socket socket)
	{
		if (fClientSockets.size() < fConnectionsAllowed)
		{
			sLogger.info("Conection received from " + socket.getInetAddress());
			BasisSetTcpEncoder handler = allocateDataSetWriter(socket, fKeepMode, fCapacity, fBufferSizeBytes, null, null);
						
			if (handler != null)
			{
				// Start the encoder
				handler.start();
				fClientSockets.add(handler);
			}
		}
		else
		{
			// Refuse connection
			try
			{
				String message = 
					getFullyQualifiedName() + " refused connection from "
					+ socket.getInetAddress() 
					+ " because connection limit reached";
			
				sLogger.logp(
					Level.SEVERE, CLASS_NAME, "acceptConnection", message);

				socket.close();
			}
			catch (IOException e)
			{
				// Do nothing
			}
		}
	}

	/**
	 * Causes this adapter to respond as needed to a newly-associated input 
	 * BasisBundle, or to the change in the structure of an already-associated 
	 * input BasisBundle.
	 * 
	 * @param inputBasisBundleId The BasisBundleId of either a) a newly-associated 
	 * 		input BasisBundle; or b) an already-associated input BasisBundle that 
	 * 		has changed its structure
	 * @param inputDescriptor The (possibly new) BasisBundleDescriptor of the 
	 * 		indicated input BasisBundle
	 */
	protected void handleNewInputBasisBundleStructure
		(BasisBundleId inputBasisBundleId, BasisBundleDescriptor inputDescriptor)
	{
		// Save the descriptor for future use
		fDescriptorMap.put(inputBasisBundleId, inputDescriptor);
		
		if (isStarted())
		{
			// Give new change to all client handlers.
			for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
			{
				((BasisSetTcpEncoder) iter.next()).handleBasisBundleChange(
						inputBasisBundleId);				
			}
		}		
	}	
	
	/**
	 * Causes this adapter to respond as needed to the closing of an associated 
	 * input BasisBundle.
	 * 
	 * @param inputBasisBundleId The BasisBundleId of an already-associated input 
	 * 		BasisBundle that has closed (i.e., will no longer be producing data)
	 */
	protected void handleInputBasisBundleClosing(BasisBundleId inputBasisBundleId)
	{
		// Remove the descriptor
		fDescriptorMap.remove(inputBasisBundleId);
	}

	/**
	 * Causes this Adapter to (re)configure itself in accordance with 
	 * the current Descriptor.
	 */
	private void configureFromDescriptor()
	{
		OutputAdapterDescriptor descriptor = 
			(OutputAdapterDescriptor) getDescriptor();
		
		if (descriptor == null)
		{
			return;
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
		
		//---Extract TCP server socket properties from descriptor
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

		//---Extract Queue properties from descriptor
		String keepMode = descriptor.getParameter(KEEP_KEY);
		
		if (keepMode != null)
		{
			fKeepMode = keepMode;
		}

		String keepCapacity = descriptor.getParameter(KEEP_CAPACITY_KEY);
		
		if (keepCapacity != null)
		{
			try
			{
				int capacity = Integer.parseInt(keepCapacity);
				
				if (capacity < 1)
				{
					String message = 
						"Output Queue capacity must be greater than 0 instead of "
						+ capacity;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the capacity 
					fCapacity = capacity;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to set output Queue capacity with invalid number "
					+ keepCapacity;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}

		// Build request
		Amount basisRequestAmount = new Amount();
		basisRequestAmount.setAmount(1.0);

		//---Extract request amount number from descriptor
		String strRequestAmount = descriptor.getParameter("requestAmount");
		
		if (strRequestAmount != null)
		{
			float requestAmount = 1;

			try
			{
				requestAmount = Float.parseFloat(strRequestAmount);
				
				if (requestAmount < 0.0)
				{
					String message = 
						"Attempt to build Adapter with invalid request amount "
						+ requestAmount;
	
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
				else 
				{
					//---Set the request amount
					basisRequestAmount.setAmount(requestAmount);			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build Adapter with invalid request amount "
					+ requestAmount;
	
				sLogger.logp(
						Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
		
		//---Extract request Unit number from descriptor
		String strRequestUnit = descriptor.getParameter("requestUnit");
		
		if (strRequestUnit != null)
		{
			//---Set the request Unit
			basisRequestAmount.setUnit(strRequestUnit);			
		}
		
		//---Extract downsample rate number from descriptor
		String strDownsampleRate = descriptor.getParameter("downsamplingRate");
		int downsample = 0;
		
		if (strDownsampleRate != null)
		{
			try
			{
				downsample = Integer.parseInt(strDownsampleRate);
				
				if (downsample < 0)
				{
					String message = 
						"Attempt to build Adapter with invalid downsample amount "
						+ downsample;
	
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"configureFromDescriptor", message);
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build Adapter with invalid downsample amount "
					+ downsample;
	
				sLogger.logp(
						Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
		
		//---Extract Basis request name from descriptor
		String basisRequestName = descriptor.getParameter("basisRequest");
		
		BasisBundleId basisBundleId = null;
		
		if (basisRequestName != null)
		{
			basisBundleId = 
				new DefaultBasisBundleIdFactory().createBasisBundleId(basisRequestName);
		}
		
		// Build basis request
		if (basisBundleId != null)
		{
			BasisRequest basisRequest = 
				new BasisRequest(basisBundleId, basisRequestAmount);
		
			basisRequest.includePendingDataOnStop();
			basisRequest.setDownsamplingRate(downsample);
			fInput.removeBasisRequests();
			fInput.addBasisRequest(basisRequest);
		}
		else
		{
			//TODO need to handle the case when there is not the specified bundle in the dataStore yet.
			String message = 
				"Attempt to build request with unknown bundle ID "
				+ basisRequestName + ".  Ignore if this is a generic basis set OutputAdapter.";

			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"configureFromDescriptor", message);
		}
	}

	/**
	 * Causes this adapter to process asynchronously the given DataSet by
	 * handing it off to publish handlers. Depending on queue properties this
	 * call can block.
	 * 
	 * @param dataSet A DataSet
	 */	
	public void processDataSet(DataSet dataSet)
	{
		// Give new dataset to all client handlers.
		for (Iterator iter = fClientSockets.iterator(); iter.hasNext();)
		{
			((BasisSetTcpEncoder) iter.next()).handleDataSet(dataSet);			
		}
	}

	// InputListener methods -------------------------------------------------

	/**
	 * Causes this adapter to receive the given BasisBundleEvent.
	 * 
	 * @param event A DataListener
	 */	
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		if (event != null)
		{
			try
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = getFullyQualifiedName() + 
						" received BasisBundleEvent:\n" + event;
					
					sLogger.logp(Level.FINEST, CLASS_NAME, 
						"receiveDataEvent", message);
				}
				
				if (event.hasNewStructure())
				{
					// If the given event indicates that the input BasisBundle 
					// that generated it has changed its structure, then we 
					// need to remember this change.
					
					BasisBundleId inputBundleId = event.getBasisBundleId();
					BasisBundleDescriptor inputDescriptor = event.getDescriptor();
					
					handleNewInputBasisBundleStructure
						(inputBundleId, inputDescriptor);
				}
				else if (event.isClosed())
				{
					// If the given BasisBundleEvent indicated that the input 
					// BasisBundle that generated it has closed (i.e., will no 
					// longer produce any data), then we can remove all 
					// references to it.
					
					BasisBundleId inputBasisBundleId = event.getBasisBundleId();
					
					handleInputBasisBundleClosing(inputBasisBundleId);
				}
			}
			catch (Exception ex)
			{
				declareException(ex);
			}
		}
	}

	/**
	 * Causes this Processor to receive the given DataSetEvent. 
	 * 
	 * <p>Here, we simply extract the input DataSet from the given DataSetEvent 
	 * and call <code>processDataSet(DataSet)</code> with the extracted DataSet.
	 * 
	 * @param event A DataSetEvent
	 */
	public synchronized void receiveDataSetEvent(DataSetEvent event)
	{
		DataSet dataSet = event.getDataSet();
		
		if ((isStarted() && dataSet != null))
		{
			try
			{
				if (sLogger.isLoggable(Level.FINEST))
				{
					String message = getFullyQualifiedName() + 
						" received DataSet:\n" + dataSet;
					
					sLogger.logp(Level.FINEST, CLASS_NAME, 
						"receiveDataEvent", message);
				}
				
				processDataSet(dataSet);
			}
			catch (Exception ex)
			{
				declareException(ex);
			}
		}
	}

	/**
	 * Maintains header information that will preceed any data or descriptor
	 * information.
	 */
	public static class Header implements Serializable
	{
	
		/** 
		 * Bundle Id for either the descriptor or data that follows this 
		 * header.
		 */
		public BasisBundleId fBundleId = null;
	
		/** 
		 * Type of block that will follow this header.
		 * @see AbstractBasisSetTcpOutputAdapter
		 */
		public int fPacketType;
	
		/** 
		 * Sequence number of this packet. Should always match sequence number
		 * in trailer.
		 */
		public int fSequenceNumber = 0;
	
		/** 
		 * The number of samples in each Buffer in the data block that follow 
		 * the header for data packets. Currently undefined for descriptor packets.
		 */
		public int fSamples;
	
		/** 
		 * The number of Buffers in the data block that follow the header for 
		 * data packets. Currently undefined for descriptor packets.
		 */
		public int fNumberOfBuffers = 0;
		
		/**
		 * Optional (error) message
		 */
		public String fMessage;
				
		public String toString()
		{
			StringBuffer string = new StringBuffer();
			string.append("Header " 
				+ "\n BasisBundleId:" + ((fBundleId == null) ? "null" : fBundleId.toString())
				+ "\n Seq: " + fSequenceNumber 
				+ "\n Samples:" + fSamples
				+ "\n Buffers:" + fNumberOfBuffers
				+ "\n Message:" + fMessage				
				);
			
			return string.toString();
		}
	}

	/**
	 * Maintains trailer information that will follow any data or descriptor
	 * information.
	 */
	public static class Trailer implements Serializable
	{
	
		/** 
		 * Bundle Id for either the descriptor or data that preceeded this 
		 * trailer.
		 */
		public BasisBundleId fBundleId = null;
	
		/** 
		 * Sequence number of this packet. Should always match sequence number
		 * in Header.
		 */
		public int fSequenceNumber = 0;
	
		public String toString()
		{
			StringBuffer string = new StringBuffer();
			string.append("Tail " 
				+ "\n BasisBundleId:" + fBundleId.toString()
				+ "\n Seq: " + fSequenceNumber 
				);
			
			return string.toString();
		}
	}

	/**
	 * Shutdown the basis requests for each input and then
	 * shutdown the input itself.
	 * @param inputs
	 */
	public void shutdownInputs(List inputs)
	{
		
		if (inputs != null)
		{
			for (Iterator iter = inputs.iterator(); iter.hasNext();)
			{
				Input input = (Input) iter.next();
//				input.removeBasisRequests();
//				input.removeInputListener(this);
				input.stop();
			}
		}
		
	}


}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractBasisSetTcpOutputAdapter.java,v $
//  Revision 1.4  2006/05/23 16:07:03  smaher_cvs
//  Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//
//  Revision 1.3  2006/05/22 14:07:49  smaher_cvs
//  Added passing through the Inputs that are used in the generic output adapters so that they can be stopped when the client disconnects.
//
//  Revision 1.2  2006/05/18 14:05:19  smaher_cvs
//  Checkpointing working generic TCP basis set adapters.
//
//  Revision 1.1  2006/05/16 12:43:18  smaher_cvs
//  Renamed BasisSet input and output adapters.
//
//  Revision 1.2  2006/04/07 15:06:13  smaher_cvs
//  Comments and support for underlying buffer size
//
//  Revision 1.1  2006/03/31 16:27:23  smaher_cvs
//  Added XDR basis set encoding, which included pulling out some common functionality from BasisSetTcpJavaSerializationOutputAdapter.
//
//  Revision 1.6  2006/03/21 20:52:41  tames_cvs
//  Removed references to deprecated BasisRequestAmount.
//
//  Revision 1.5  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/11/30 19:29:33  tames
//  Changed how data publishers cleared out their pending queue when stopped.
//  The new method should be more reliable.
//
//  Revision 1.2  2005/11/30 19:17:24  tames
//  Implemented support for BasisBundle structure changes. Updated Javadoc.
//
//  Revision 1.1  2005/11/28 23:18:20  tames
//  Initial Implementation.
//
//
//