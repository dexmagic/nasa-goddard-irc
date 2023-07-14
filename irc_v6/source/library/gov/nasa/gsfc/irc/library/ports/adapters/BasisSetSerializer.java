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

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.InputListener;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleIdFactory;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractOutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectListener;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A BasisSetSerializer is an OutputAdapter that converts a specified 
 * BasisSet to a serialized form and published as an 
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferEvent OutputBufferEvent}.
 * The format of the serialized BasisSet is subject to change.
 * <p>
 * This OutputAdapter paired with a 
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetDeserializer BasisSetDeserializer}
 * on the remote receiving end allows a BasisSet to be published across a 
 * Connection.
 * 
 * <P><B>Note:</B> Early versions of the 1.4 JVM and even later versions on some 
 * platforms have serious bugs with respect to NIO nonblocking channels. As a 
 * result this class and any derived subclasses may not work reliably on all platforms. 
 * Developers should test on the target platform before utilizing this class.
 *
 * <P>The configuration of this adapter is specified by an 
 * OutputAdapterDescriptor.
 * The table below gives the configuration parameters that this adapter uses.
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
 *      <td>basisRequest</td><td>-</td>
 *      <td align="left">The name of the BasisBundle to publish.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>downsamplingRate</td><td>0</td>
 *      <td align="left">The rate to downsample the BasisBundle.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>requestAmount</td><td>1</td>
 *      <td align="left">The requested amount to use for each publication.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>requestUnit</td><td>samples</td>
 *      <td align="left">The unit of the requested amount for each publication</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML Output Adapter description for this adapter:
 *  <BR>
 *  <pre>
 *     &lt;OutputAdapter name="dataFormatter" type="BasisSet"&gt;
 *         &lt;Parameter name="basisRequest" value="DetectorData from Anonymous" /&gt;
 *         &lt;Parameter name="requestAmount" value="0.5" /&gt;
 *         &lt;Parameter name="requestUnit" value="s" /&gt;
 *     &lt;/OutputAdapter&gt;
 *  </pre>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/23 16:07:03 $
 * @author	Troy Ames
 **/
public class BasisSetSerializer extends AbstractOutputAdapter
	implements OutputAdapter, InputListener, ConnectListener
{
	private static final String CLASS_NAME = BasisSetSerializer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "BasisSet Serializer";

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

	private Input fInput;
	private Map fDescriptorMap = new HashMap();
	private boolean fResendDescriptorsFlag = false;
	
	private Header fHeader = new Header();
	private Trailer fTrailer = new Trailer();
	

	/**
	 *  Constructs a new BasisSetSerializer having a default name and managed 
	 *  by the default ComponentManager.
	 * 
	 **/

	public BasisSetSerializer()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new BasisSetSerializer having the given name and managed 
	 *  by the default ComponentManager.
	 * 
	 *  @param name The name of the new BasisSetSerializer
	 **/

	public BasisSetSerializer(String name)
	{
		super(name);

		fInput = new DefaultInput();
		fInput.addInputListener(this);
	}
	
	
	/**
	 *	Constructs a new BasisSetSerializer configured according to the given 
	 *  OutputAdapterDescriptor.
	 *
	 *  @param descriptor The OutputAdapterDescriptor of the new BasisSetSerializer
	 */
	
	public BasisSetSerializer(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
		
		fInput = new DefaultInput();
		fInput.addInputListener(this);
		
		configureFromDescriptor();
	}


	/**
	 *  Causes this Adapter to start.
	 */
	public void start()
	{
		if (!isStarted())
		{
			super.start();
			configureFromDescriptor();
			fInput.start();
		}
	}

	/**
	 *  Causes this Adapter to stop.
	 */	
	public void stop()
	{
		if (!isStopped())
		{
			fInput.stop();
			super.stop();
		}
	}
	
	/**
	 *  Sets the Descriptor of this Adapter to the given Descriptor. The 
	 *  Adapter will in turn be (re)configured in accordance with the given 
	 *  Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/	
	public void setDescriptor(Descriptor descriptor)
	{
		if (descriptor instanceof OutputAdapterDescriptor)
		{
			super.setDescriptor(descriptor);
			
			configureFromDescriptor();
		}
	}

	/**
	 * Called when a registered Connection has changed. If the Connection 
	 * component has received a new connection then this 
	 * implementation will set a flag that will result in a resend of all  
	 * {@link BasisBundleDescriptor BasisBundleDescriptors} 
	 * for all the active (not closed) BasisBundles published by this adapter.
	 * 
	 * @param event the event describing the connection change.
	 * @see gov.nasa.gsfc.irc.devices.ports.connections.ConnectListener#connectionChanged(gov.nasa.gsfc.irc.devices.ports.connections.ConnectEvent)
	 */
	public void connectionChanged(ConnectEvent event)
	{
		if (event.getType() == ConnectEvent.CONNECTION_ADDED)
		{
			fResendDescriptorsFlag = true;
		}
	}
	
	/**
	 * Causes this Adapter to process the given BasisSet.
	 * 
	 * @param BasisSet A BasisSet
	 */	
	protected void processBasisSet(BasisSet basisSet)
	{
		if (isStarted())
		{
			// Check if there were any new connections since the last BasisSet
			if (fResendDescriptorsFlag)
			{
				fResendDescriptorsFlag = false;
				publishAllDescriptors();
			}
			
			// Process this basis set
		    ByteArrayOutputStream byteStream = null;
			ByteBuffer byteBuffer = null;
			
	    	byteStream = new ByteArrayOutputStream();
	    	
			try
			{
				writeBasisSetPacket(byteStream, basisSet);
				byteStream.flush();

				byteBuffer =  ByteBuffer.wrap(byteStream.toByteArray());
				//System.out.println("++Writing Buffer:" + byteBuffer.remaining());
				byteStream.close();
				
				fireOutputBufferEvent(new OutputBufferEvent(this, byteBuffer));
			}
			catch (IOException e)
			{
				String message = 
					getFullyQualifiedName() + " encountered Exception";
			
				sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"processBasisSet", message, e);
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
		    ByteArrayOutputStream byteStream = null;
			ByteBuffer byteBuffer = null;
			
	    	byteStream = new ByteArrayOutputStream();
	    	
			try
			{
				writeDescriptorPacket(byteStream, inputBasisBundleId, inputDescriptor);
				byteStream.flush();

				byteBuffer =  ByteBuffer.wrap(byteStream.toByteArray());
				byteStream.close();
				
				fireOutputBufferEvent(new OutputBufferEvent(this, byteBuffer));

				if (sLogger.isLoggable(Level.FINER))
				{
					String message = 
						"Sent new BasisBundle Descriptor, Id:" 
						+ inputBasisBundleId;
					
					sLogger.logp(Level.FINER, CLASS_NAME, 
						"readDescriptorPacket", message);
				}
			}
			catch (IOException e)
			{
				String message = 
					getFullyQualifiedName() + " encountered Exception";
			
				sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"handleNewInputBasisBundleStructure", message, e);
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
	 * Publishes all current descriptors.
	 */
	private void publishAllDescriptors()
	{
		if (isStarted())
		{
			// Get the known descriptors
			Iterator entries = fDescriptorMap.entrySet().iterator();
			
			// Publish each descriptor
			while(entries.hasNext())
			{
				Map.Entry entry = (Map.Entry) entries.next();
				BasisBundleId bundleId = (BasisBundleId) entry.getKey();
				BasisBundleDescriptor descriptor = 
					(BasisBundleDescriptor) entry.getValue();
				
			    ByteArrayOutputStream byteStream = null;
				ByteBuffer byteBuffer = null;
				
		    	byteStream = new ByteArrayOutputStream();
		    	
				try
				{
					writeDescriptorPacket(byteStream, bundleId, descriptor);
					byteStream.flush();
	
					byteBuffer =  ByteBuffer.wrap(byteStream.toByteArray());
					byteStream.close();
					
					fireOutputBufferEvent(new OutputBufferEvent(this, byteBuffer));
				}
				catch (IOException e)
				{
					String message = 
						getFullyQualifiedName() + " encountered Exception";
				
					sLogger.logp(
						Level.WARNING, CLASS_NAME, 
						"publishAllDescriptors", message, e);
				}
			}
		}		
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
				+ basisRequestName;

			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"configureFromDescriptor", message);
		}
	}

	/**
	 * Writes the specifed descriptor to the stream.
	 * 
	 * @param byteStream	the stream to write the packet to.
	 * @param inputBasisBundleId 	the bundle id associated with this descriptor
	 * @param inputDescriptor	the descriptor to write
	 */
	private void writeDescriptorPacket(
			OutputStream byteStream,
			BasisBundleId inputBasisBundleId,
			BasisBundleDescriptor inputDescriptor)
	{
		ObjectOutputStream objectStream = null;
		
		try
		{
			objectStream = new ObjectOutputStream(byteStream);

			// Update header
			fHeader.fBundleId = inputBasisBundleId;
			fHeader.fPacketType = DESCRIPTOR_PACKET;
			fHeader.fSequenceNumber++;
			
			
			// Update trailer
			fTrailer.fBundleId = inputBasisBundleId;
			fTrailer.fSequenceNumber = fHeader.fSequenceNumber;
		
			// Write packet
			objectStream.writeUnshared(fHeader);
			objectStream.writeUnshared(inputDescriptor);
			objectStream.writeUnshared(fTrailer);
			objectStream.flush();
		}
		catch (IOException e)
		{
			String message = "Exception while writing descriptor packet";

			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"writeDescriptorPacket", message, e);
		}
	}

	/**
	 * Writes the specified basis set to the stream.
	 * 
	 * @param byteStream the stream to write the data to
	 * @param basisSet the basis set to write
	 */
	private void writeBasisSetPacket(
			OutputStream byteStream,
			BasisSet basisSet)
	{
		BasisBundleId bundleId = basisSet.getBasisBundleId();
		ObjectOutputStream objectStream = null;
		
		// Update header
		fHeader.fSamples = basisSet.getSize();
		fHeader.fBundleId = bundleId;
		fHeader.fPacketType = DATA_PACKET;
		fHeader.fSequenceNumber++;
		
		// Calculate total size
		//fHeader.fSizeInBytes = basisSet.getSizeInBytes();
				
		// Set the length to include the number of data buffers + basis buffer.
		fHeader.fNumberOfBuffers = basisSet.getNumberOfDataBuffers() + 1;
//		System.out.println("--Writing BS #:" + fHeader.fSequenceNumber 
//				+ " Samples:" + fHeader.fSamples 
//				+ " Buffers:" + fHeader.fNumberOfBuffers);
		
		// Update trailer
		fTrailer.fBundleId = bundleId;
		fTrailer.fSequenceNumber = fHeader.fSequenceNumber;

		// Write packet
		try
		{
			objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeUnshared(fHeader);
			//System.out.println("Wrote Header: " + fHeader.fSequenceNumber);
			
			// Write basis buffer
			writeDataBuffer(objectStream, basisSet.getBasisBuffer());
			
			// Write data buffers
			for (Iterator buffers = basisSet.getDataBuffers(); buffers.hasNext();)
			{
				writeDataBuffer(objectStream, (DataBuffer) buffers.next());
			}
			
			objectStream.writeUnshared(fTrailer);
			objectStream.flush();
		}
		catch (IOException e)
		{
			String message = "Exception while writing descriptor packet";

			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"writeBasisSetPacket", message, e);
		}
	}

	/**
	 * Writes the DataBuffer to the given output stream.
	 * 
	 * @param outputStream the stream to write to
	 * @param buffer the DataBuffer to write out.
	 * @throws IOException if there is an exception writing to the stream.
	 */
	private void writeDataBuffer(
			ObjectOutputStream outputStream, DataBuffer buffer) throws IOException
	{
		int size = buffer.getSize();
		Class type = buffer.getDataBufferType();
		
		if (type == double.class)
		{
			outputStream.writeByte(DOUBLE_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeDouble(buffer.getAsDouble(i));
			}
		}
		else if (type == int.class)
		{
			outputStream.writeByte(INT_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeInt(buffer.getAsInt(i));
			}
		}
		else if (type == float.class)
		{
			outputStream.writeByte(FLOAT_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeFloat(buffer.getAsFloat(i));
			}
		}
		else if (type == short.class)
		{
			outputStream.writeByte(SHORT_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeShort(buffer.getAsShort(i));
			}
		}
		else if (type == long.class)
		{
			outputStream.writeByte(LONG_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeLong(buffer.getAsLong(i));
			}
		}
		else if(type == byte.class)
		{
			outputStream.writeByte(BYTE_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeByte(buffer.getAsByte(i));
			}
		}
		else if (type == char.class)
		{
			outputStream.writeByte(CHAR_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeChar(buffer.getAsChar(i));
			}
		}
		else	// Type must be Object or subclass
		{
			outputStream.writeByte(OBJECT_TYPE);

			for (int i = 0; i < size; ++i)
			{
				outputStream.writeObject(buffer.getAsObject(i));
			}
		}
	}
	
	/**
	 * Causes this BasisSetProcessor to process the given DataSet. Calls 
	 * <code>processBasisSet(BasisSet)</code> with each BasisSet contained
	 * in the given DataSet.
	 * 
	 * @param dataSet A DataSet
	 */	
	public void processDataSet(DataSet dataSet)
	{
		try
		{
			Iterator basisSets = dataSet.getBasisSets().iterator();
			
			while (basisSets.hasNext())
			{
				BasisSet basisSet = (BasisSet) basisSets.next();
				
				if (sLogger.isLoggable(Level.FINEST))
				{
					String message = getFullyQualifiedName() + 
						" processing BasisSet:\n" + basisSet;
					
					sLogger.logp(Level.FINEST, CLASS_NAME, 
						"processDataSet", message);
				}
				
				synchronized (getConfigurationChangeLock())
				{
					processBasisSet(basisSet);
				}
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}

	/**
	 * Registers the given listener for OutputBufferEvent generated by this
	 * OutputAdapter and sets a flag that will result in a resend of all  
	 * {@link BasisBundleDescriptor BasisBundleDescriptors} 
	 * for all the active (not closed) BasisBundles published by this adapter.
	 * 
	 * @param listener a OutputBufferListener to register
	 */
	public void addOutputBufferListener(OutputBufferListener listener)
	{
		super.addOutputBufferListener(listener);
		fResendDescriptorsFlag = true;
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
				if (sLogger.isLoggable(Level.FINEST))
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
					// need to handle the new descriptor.
					
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
	 * Causes this adapter to receive the given DataSetEvent. 
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
	
	// Utility classes -------------------------------------------------------
	
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
		protected BasisBundleId fBundleId = null;

		/** 
		 * Type of block that will follow this header.
		 * @see BasisSetSerializer.DESCRIPTOR_PACKET
		 * @see BasisSetSerializer.DATA_PACKET
		 */
		protected int fPacketType = DESCRIPTOR_PACKET;

		/** 
		 * Sequence number of this packet. Should always match sequence number
		 * in trailer.
		 */
		protected int fSequenceNumber = 0;

		/** 
		 * The number of samples in each Buffer in the data block that follow 
		 * the header for data packets. Currently undefined for descriptor packets.
		 */
		protected int fSamples;

		/** 
		 * The number of Buffers in the data block that follow the header for 
		 * data packets. Currently undefined for descriptor packets.
		 */
		protected int fNumberOfBuffers = 0;
		
		
		public String toString()
		{
			StringBuffer string = new StringBuffer();
			string.append("Header " 
				+ "\n BasisBundleId:" + fBundleId.toString()
				+ "\n Seq: " + fSequenceNumber 
				+ "\n Samples:" + fSamples
				+ "\n Buffers:" + fNumberOfBuffers
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
		protected BasisBundleId fBundleId = null;

		/** 
		 * Sequence number of this packet. Should always match sequence number
		 * in Header.
		 */
		protected int fSequenceNumber = 0;

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
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetSerializer.java,v $
//  Revision 1.15  2006/05/23 16:07:03  smaher_cvs
//  Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//
//  Revision 1.14  2006/05/18 14:05:19  smaher_cvs
//  Checkpointing working generic TCP basis set adapters.
//
//  Revision 1.13  2006/03/21 20:52:41  tames_cvs
//  Removed references to deprecated BasisRequestAmount.
//
//  Revision 1.12  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.11  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.10  2005/11/30 19:16:22  tames
//  JavaDoc updates only.
//
//  Revision 1.9  2005/11/21 16:13:30  tames
//  Updated Javadoc only.
//
//  Revision 1.8  2005/10/21 20:24:46  tames_cvs
//  Debug print statement changes only.
//
//  Revision 1.7  2005/09/13 22:34:06  tames
//  JavaDoc update only.
//
//  Revision 1.6  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.5  2005/07/20 20:01:09  tames_cvs
//  Updated for new DataBuffer architecture.
//
//  Revision 1.4  2005/07/18 21:16:20  tames_cvs
//  Parcial update to the new DataBuffer architecture. This version compiles
//  but is not complete.
//
//  Revision 1.3  2005/05/27 15:44:59  tames_cvs
//  Added a log statement and changed the start method to reconfigure
//  the component.
//
//  Revision 1.2  2005/05/04 17:16:11  tames_cvs
//  Added support as a ConnectListener.
//
//  Revision 1.1  2005/05/02 15:28:00  tames
//  Initial working version. Also relocated.
//
//  Revision 1.2  2005/04/22 22:26:27  tames_cvs
//  Another partially functioning snapshot with lots of test code.
//
//  Revision 1.1  2005/04/19 22:10:55  tames_cvs
//  Initial partial implementation of sending data over a connection to
//  remote listeners.
//
//