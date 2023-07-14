//=== File Prolog ============================================================
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset;

import gov.nasa.gsfc.commons.types.queues.KeepOptionalBoundedQueue;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter.Header;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter.Trailer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses Java serialization to encode data sets over a (TCP) socket.
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: JavaSerializationTcpEncoder.java,v 1.8 2006/05/22 14:06:34 smaher_cvs Exp $
 * @author	$Author: smaher_cvs $
 */
public class JavaSerializationTcpEncoder extends Thread implements BasisSetTcpEncoder
{
	private static final String CLASS_NAME = JavaSerializationTcpEncoder.class.getName();
	
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger
			.getLogger(JavaSerializationTcpEncoder.class.getName());
	
	public static final String KEEP_ALL_STR = "keepAll";
	public static final String KEEP_LATEST_STR = "keepLatest";
	public static final String KEEP_EARLIEST_STR = "keepEarliest";
	
	private Socket fSocket = null;
	private ObjectOutputStream fStream = null;
	private KeepOptionalBoundedQueue fOutputQueue = null;
	private Header fHeader = new Header();
	private Trailer fTrailer = new Trailer();
	private boolean fEnabled = true;
	
	/**
	 * The Input(s) associated with the data requests
	 * for this encoder (if using the generic data request
	 * mechanism)
	 */
	private List fInputs; 
	
	/**
	 * The "parent" Tcp Output Adapter.  We need
	 * this reference to get access to a basis bundle
	 * id map and to see if the adapter is still running.
	 * 
	 * (This coupling could be reduced, probably; I didn't want
	 * to change ANY functionality when I pulled this class
	 * out of BasisSetTcpJavaSerializationOutputAdapter.  S. Maher)
	 */
	private AbstractBasisSetTcpOutputAdapter fTcpOutputAdapter;

	private String fSetupError;
	
	/**
	 * Constructs a new TcpDataSetPublishHandler object.
	 *
	 * @param socket	the socket to publish data to.
	 * @param keepMode	the queue keep mode to use for this publisher
	 * @param capacity	the capacity of the DataSet queue.
	 */
	public JavaSerializationTcpEncoder(AbstractBasisSetTcpOutputAdapter publisher,
			Socket socket, String keepMode, int capacity, int bufferSizeBytes, String setupError, List inputs)
	{
		fTcpOutputAdapter = publisher;
		fSocket = socket;
		fOutputQueue = new KeepOptionalBoundedQueue(capacity);
		fSetupError = setupError;
		fInputs = inputs;		
		
		if (keepMode != null)
		{
			if (keepMode.equals(KEEP_ALL_STR))
			{
				fOutputQueue.setKeepAll();
			}
			else if (keepMode.equals(KEEP_LATEST_STR))
			{
				fOutputQueue.setKeepLatest();
			}
			else if (keepMode.equals(KEEP_EARLIEST_STR))
			{
				fOutputQueue.setKeepEarliest();
			}
		}
		
		try
		{
			fSocket.setSendBufferSize(bufferSizeBytes);
		} 
		catch (SocketException e1)
		{
			e1.printStackTrace();
		}
		
		try
		{
			fStream = new ObjectOutputStream(fSocket.getOutputStream());
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}
	}
	
	/**
	 * Repeatedly removes DataSets from the queue and publishes them
	 * until interrupted or an exception occurs.
	 */
	public void run()
	{
		try
		{
			if (fSetupError != null)
			{
				publishErrorMessage(fStream, fSetupError);
			}			
			else
			{			
				publishAllDescriptors(fStream);
				
				while(!isInterrupted())
				{
					try
					{
						Object queuedObject = fOutputQueue.blockingRemove();
						
						if (queuedObject instanceof DataSet)
						{
							publishDataSet((DataSet) queuedObject);
						}
						else if (queuedObject instanceof BasisBundleId)
						{
							BasisBundleDescriptor descriptor = 
								(BasisBundleDescriptor) fTcpOutputAdapter.getBasisBundleDescriptorMap().get(queuedObject);
							
							if (descriptor != null)
							{
								writeDescriptorPacket(
										fStream, 
										(BasisBundleId) queuedObject, 
										descriptor);
							}
						}
					}
					catch (InterruptedException e)
					{
						String message = "Exception from interrupted Connection";
						sLogger.logp(
								Level.FINE, CLASS_NAME, 
								"WriterThread.run", message, e);
						
						// The thread interrupt status should already be set due
						// to this exception, but it appears that this is not
						// always the case so set it anyway.
						interrupt();
					}
				}
			}
		}
		catch (Exception e)
		{
			sLogger.severe(e.toString());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				close();
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds a BasisBundleId to the publish queue.
	 * 
	 * @param inputBasisBundleId the id of the change to publish to clients
	 */
	public void handleBasisBundleChange(
			BasisBundleId inputBasisBundleId)
	{
		if (fEnabled)
		{
			try
			{
				fOutputQueue.blockingAdd(inputBasisBundleId);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds DataSet to publish queue.
	 * 
	 * @param dataSet the DataSet to publish
	 */
	public void handleDataSet(DataSet dataSet)
	{
		if (fEnabled)
		{
			try
			{
				//TODO should this hold the dataset
				fOutputQueue.blockingAdd(dataSet);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes this handler and clears the publish queue.
	 */
	protected void close() throws Exception
	{
		fEnabled = false;
		
		try
		{		
			// Clear out any pending publishables.
			while (!fOutputQueue.isEmpty())
			{
				// TODO should this release the dataset.
				fOutputQueue.remove();
			}
			
			fStream.close();
			fSocket.close();
		} 	
		finally
		{
			String mesg = "Shutting down inputs";
			System.out.println(mesg);
			fTcpOutputAdapter.shutdownInputs(fInputs);
		}		
	}
	
	private void publishErrorMessage(ObjectOutputStream objectStream, String mesg) throws IOException
	{

		// Update header
		Header errorHeader = new Header();
		errorHeader.fPacketType = AbstractBasisSetTcpOutputAdapter.ERROR_PACKET;
		errorHeader.fMessage = mesg;
		
		// Write packet
		objectStream.writeUnshared(errorHeader);		
		objectStream.flush();
		objectStream.reset();

	}
	
	/**
	 * Publishes all current descriptors.
	 */
	private void publishAllDescriptors(ObjectOutputStream objectStream)
	{
		if (fTcpOutputAdapter.isStarted())
		{
			// Get the known descriptors
			Iterator entries = fTcpOutputAdapter.getBasisBundleDescriptorMap().entrySet().iterator();
			
			// Publish each descriptor
			while(entries.hasNext())
			{
				Map.Entry entry = (Map.Entry) entries.next();
				BasisBundleId bundleId = (BasisBundleId) entry.getKey();
				BasisBundleDescriptor descriptor = 
					(BasisBundleDescriptor) entry.getValue();
				
				try
				{
					writeDescriptorPacket(objectStream, bundleId, descriptor);
					objectStream.flush();
				}
				catch (IOException e)
				{
					String message = CLASS_NAME + " encountered Exception";
					
					sLogger.logp(
							Level.WARNING, CLASS_NAME, 
							"publishAllDescriptors", message, e);
				}
			}
		}		
	}	
	
	/**
	 * Causes this BasisSetProcessor to process the given DataSet.
	 * 
	 * @param dataSet A DataSet
	 */	
	public void publishDataSet(DataSet dataSet) throws IOException
	{
		//TODO should this release the dataset
		Iterator basisSets = dataSet.getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			if (sLogger.isLoggable(Level.FINEST))
			{
				String message = CLASS_NAME + 
				" processing BasisSet:\n" + basisSet;
				
				sLogger.logp(Level.FINEST, CLASS_NAME, 
						"processDataSet", message);
			}
			
			publishBasisSet(basisSet);
		}
	}
	
	/**
	 * Causes this handler to publish the given BasisSet.
	 * 
	 * @param BasisSet A BasisSet
	 */	
	protected void publishBasisSet(BasisSet basisSet) throws IOException
	{
		if (fTcpOutputAdapter.isStarted())
		{
			writeBasisSetPacket(fStream, basisSet);
			fStream.flush();
			
			// This will clear object caches/references in the
			// client ObjectInputStream so memory is not leaked
			//
			// See http://java.sun.com/products/jdk/serialization/faq/#OutOfMemoryError

			fStream.reset();
			
			if (sLogger.isLoggable(Level.FINE))
			{
				sLogger.info("BasisSet - Published BasisSet");
			}
		}		
	}
	
	/**
	 * Writes the specifed descriptor to the stream.
	 * 
	 * @param byteStream the stream to write the packet to.
	 * @param inputBasisBundleId the bundle id associated with this descriptor
	 * @param inputDescriptor the descriptor to write
	 * @throws IOException
	 */
	private void writeDescriptorPacket(
			ObjectOutputStream objectStream,
			BasisBundleId inputBasisBundleId,
			BasisBundleDescriptor inputDescriptor) 
		throws IOException
	{
		
		// Update header
		fHeader.fBundleId = inputBasisBundleId;
		fHeader.fPacketType = AbstractBasisSetTcpOutputAdapter.DESCRIPTOR_PACKET;
		fHeader.fSequenceNumber++;			
		
		// Update trailer
		fTrailer.fBundleId = inputBasisBundleId;
		fTrailer.fSequenceNumber = fHeader.fSequenceNumber;
		
		// Write packet
		objectStream.writeUnshared(fHeader);
		objectStream.writeUnshared(inputDescriptor);
		objectStream.writeUnshared(fTrailer);
		objectStream.flush();
		objectStream.reset();
	}
	
	/**
	 * Writes the specified basis set to the stream.
	 * 
	 * @param byteStream the stream to write the data to
	 * @param basisSet the basis set to write
	 * @throws IOException 
	 */
	private void writeBasisSetPacket(
			ObjectOutputStream objectStream,
			BasisSet basisSet) 
		throws IOException
	{
		BasisBundleId bundleId = basisSet.getBasisBundleId();
		
		// Update header
		fHeader.fSamples = basisSet.getSize();
		fHeader.fBundleId = bundleId;
		fHeader.fPacketType = AbstractBasisSetTcpOutputAdapter.DATA_PACKET;
		fHeader.fSequenceNumber++;
		
		// Calculate total size
		//fHeader.fSizeInBytes = basisSet.getSizeInBytes();
		
		// Set the length to include the number of data buffers + basis buffer.
		fHeader.fNumberOfBuffers = basisSet.getNumberOfDataBuffers() + 1;
		
		// Update trailer
		fTrailer.fBundleId = bundleId;
		fTrailer.fSequenceNumber = fHeader.fSequenceNumber;

		objectStream.writeUnshared(fHeader);
		
		// Write basis buffer
		writeDataBuffer(objectStream, basisSet.getBasisBuffer());
		
		// Write data buffers
		for (Iterator buffers = basisSet.getDataBuffers(); buffers.hasNext();)
		{
			writeDataBuffer(objectStream, (DataBuffer) buffers.next());
		}
		
		objectStream.writeUnshared(fTrailer);

	}
	
	/**
	 * Writes the DataBuffer to the given output stream.
	 * 
	 * @param outputStream the stream to write to
	 * @param buffer the DataBuffer to write out.
	 * @throws IOException if there is an exception writing to the stream.
	 */
	private void writeDataBuffer(
			ObjectOutputStream outputStream, DataBuffer buffer) 
		throws IOException
	{
		int size = buffer.getSize();
		Class type = buffer.getDataBufferType();
		
		if (type == double.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.DOUBLE_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeDouble(buffer.getAsDouble(i));
			}
		}
		else if (type == int.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.INT_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeInt(buffer.getAsInt(i));
			}
		}
		else if (type == float.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.FLOAT_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeFloat(buffer.getAsFloat(i));
			}
		}
		else if (type == short.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.SHORT_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeShort(buffer.getAsShort(i));
			}
		}
		else if (type == long.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.LONG_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeLong(buffer.getAsLong(i));
			}
		}
		else if(type == byte.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.BYTE_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeByte(buffer.getAsByte(i));
			}
		}
		else if (type == char.class)
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.CHAR_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeChar(buffer.getAsChar(i));
			}
		}
		else	// Type must be Object or subclass
		{
			outputStream.writeByte(AbstractBasisSetTcpOutputAdapter.OBJECT_TYPE);
			
			for (int i = 0; i < size; ++i)
			{
				outputStream.writeObject(buffer.getAsObject(i));
			}
		}
	}
}

//
//--- Development History  ---------------------------------------------------
//
//$Log: JavaSerializationTcpEncoder.java,v $
//Revision 1.8  2006/05/22 14:06:34  smaher_cvs
//Removed unnecessary synchronization; simplified some exception handling.
//
//Revision 1.7  2006/05/18 14:05:19  smaher_cvs
//Checkpointing working generic TCP basis set adapters.
//
//Revision 1.6  2006/05/16 12:43:18  smaher_cvs
//Renamed BasisSet input and output adapters.
//
//Revision 1.5  2006/04/17 18:51:30  tames_cvs
//Reverted back the serialization of DataBuffers to be the writing and reading
//of each element of the buffer instead of the array returned by the method
//getAs<Type>Array(). This array method call results in the DataBuffer data
//being copied into the array on the sender side. The client side then must
//deserialize the array which allocates the array before writing it to the buffer.
//It appears at least on a PC that this extra memory allocation on both sides,
//plus the data copy on the source side has higher cost then iterating over
//the contents of the DataBuffer. I did not test what influence the size of the
//DataBuffer has on the performance.
//
//Revision 1.4  2006/04/14 13:12:51  smaher_cvs
//Added some logging.
//
//Revision 1.3  2006/04/11 17:01:51  smaher_cvs
//Incorporated experimental serialization change.  The change serializes entire arrays of primitives so the "looping" through the primitives is done down in the object stream.  Also, I added a reset() call in the output stream to keep the object cache in the input stream on the client clean.  Performance improved over 20% in high-performance tests (30-40MBps).
//
//Revision 1.1  2006/04/11 13:52:12  smaher_cvs
//Experimenting with higher performance object stream use.
//
//Revision 1.2  2006/04/07 15:06:38  smaher_cvs
//Comments and support for underlying buffer size
//
//Revision 1.1  2006/03/31 16:27:23  smaher_cvs
//Added XDR basis set encoding, which included pulling out some common functionality from BasisSetTcpJavaSerializationOutputAdapter.
//