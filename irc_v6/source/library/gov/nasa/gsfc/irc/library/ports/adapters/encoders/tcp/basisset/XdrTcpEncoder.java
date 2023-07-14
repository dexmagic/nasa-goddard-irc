//=== File Prolog ============================================================
//
//This code was developed by NASA, Goddard Space Flight Center, Code 580
//for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrTcpEncodingStream;

/**
 * This class encodes basis sets over a TCP socket using the eXternal Data
 * Representation (XDR, .
 * <p>
 * The resulting data stream includes two types of elements: <em>Descriptor</em>
 * packets and <em>Data</em> packets. Descriptor packets contain a
 * BasisBundleDescriptor that describes the basis bundle from which the data
 * belongs. Data packets contain a basis set (which contains a data buffer for
 * the basis (time) values and data buffers for each of the data fields).
 * <p>
 * The Descriptor packet contains a (Java) serialized version of the current
 * BasisBundleDescriptor, wrapped in an XDR byte array. This makes it
 * essentially unusable if the client is not using Java (the
 * BasisSetTcpXdrInputAdapter client <i>does</i> correctly unwrap the Java
 * object). This decision was made because there is a large amount of fields in
 * the BasisBundleDescriptor that would need to be encoded in XDR (a single
 * instance over 1 megabyte when serialized) and most clients do not need this
 * information as basic descriptions of the data (basis bundle ID, number of
 * data buffers) are included in the other parts of the data stream.
 * <p>
 * Each type of packet, Description and Data, contains a Header at the beginning
 * and a Trailer at the end. (Note, this is the same structure as the Java
 * serialized basis set encoder).
 * <p>
 * The <strong>Header</strong> is encoded as follows:
 * <ul>
 * <li> XDR String - Bundle Id for either the descriptor or data that follows
 * this header
 * <li> XDR Int - Type code of block that will follow this header: 1 =
 * Descriptor, 2 = Data, 3 = error (e.g., with basis set request)
 * <li> XDR Int - Sequence number of this packet. Should always match sequence
 * number in trailer
 * <li> XDR Int - The number of samples in each Buffer in the data block that
 * follow the header for data packets. Currently undefined for descriptor
 * packets.
 * <li> XDR Int - The number of Buffers in the data block that follow the header
 * for data packets. Currently undefined for descriptor packets.
 * <li> XDR String - optional error message
 * </ul>
 * <p>
 * The <strong>Trailer</strong> contains the following information:
 * <ul>
 * <li> XDR String - Bundle Id for either the descriptor or data that preceeds
 * this trailer
 * <li> XDR Int - Sequence number of this packet. Should always match sequence
 * number in header
 * </ul>
 * <p>
 * The <strong>Middle</strong> section of the Descriptor packet, as mentioned,
 * contains a Java serialized object. These bytes can be ignored if the
 * descriptor is not needed.
 * <p>
 * The <strong>Middle</strong> section of the Data packet contains all the data
 * buffers of the basis set. The first data buffer sent over the wire is the
 * basis buffer (e.g., time) and the remaining data buffers are sent in the
 * order defined by the {@link gov.nasa.gsfc.irc.data.BasisSet()#getDataBuffers}
 * method in <code>BasisSet</code>. When using regular expressions for data
 * buffers, it is helpful to know the actual data buffer names that are being
 * used. There fore, each data buffer is encoded with an initial string that
 * specifies the data buffer name. Following the name is an integer, which
 * indicates the type of the values in the data buffer, followed by a fixed
 * length array of the values. The size of all the arrays in all the data
 * buffers will be the <em>number of samples</em> value in the Header.
 * <p>
 * The different types of data buffers are encoded as follows:
 * <p>
 * <em>Byte data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - BYTE_TYPE (0)
 * <li> XDR fixed length array of Ints
 * </ul>
 * 
 * <em>Char data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - CHAR_TYPE (1)
 * <li> XDR fixed length array of Ints
 * </ul>
 * 
 * <em>Short data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - SHORT_TYPE (2)
 * <li> XDR fixed length array of Ints
 * </ul>
 * 
 * <em>Int data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - INT_TYPE (3)
 * <li> XDR fixed length array of Ints
 * </ul>
 * 
 * <em>Long data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - LONG_TYPE (4)
 * <li> XDR fixed length array of Hyper (two ints, with most significant word
 * first)
 * </ul>
 * 
 * <em>Float data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - FLOAT_TYPE (5)
 * <li> XDR fixed length array of Float
 * </ul>
 * 
 * <em>Double data buffer</em>
 * <ul>
 * <li> XDR String - buffer name
 * <li> XDR Int - DOUBLE_TYPE (6)
 * <li> XDR fixed length array of Double
 * </ul>
 * 
 * <em>Note:</em> Since OBJECT_TYPE refers to serialized Java objects, which
 * aren't supported outside of Java, this type of data buffer is not supported.
 * 
 * <p>
 * <strong>Fragmenting</strong>
 * <p>
 * The data is broken into "fragments". The size of the fragments can be tuned
 * with the "bufferSizeBytes" property in the output adapter IML. This buffer
 * size also dictates the size of the output buffer of the underlying socket. A
 * group of fragments is called a "fragment group". Currently, the Header,
 * Trailer, Descriptor and data buffers are each sent as a fragment group.
 * <p>
 * Each fragment sent on the wire has an XDR int "header". The most significant
 * bit (after local XDR int unpacking) is set if the fragment is the last
 * fragment in the fragment group. The rightmost 31 bits contain the number of
 * bytes in the fragment.
 * <p>
 * <em>Example python code to read the fragment header:</em>
 * <pre>
 * 
 *  	import xdrlib
 *  	
 * 	data = sockRecvAll(sock, 4)                # grab 4 bytes from the socket
 * 	unpacker = xdrlib.Unpacker(data)           # initialize XDR unpacker
 * 	header = unpacker.unpack_uint()            # unpack Int
 * 	fragmentLength = header &amp; 0x7fffffff       # length in bytes of current fragment
 * 	lastFragment = (header &amp; 1 &lt;&lt; 31) &gt;&gt; 31L   # 1 = last fragment, 0 = not last fragment
 * 	
 * </pre>
 * 
 * <p>
 * Clients must therefore have, at their lowest level, code that reads the
 * fragment headers and assembles the fragments. Once the fragments are
 * assembled, then the XDR decoding can be performed. Although this sounds
 * tedious, it is quite easy. In fact, knowing how many bytes to read from a
 * socket makes socket programming easier.
 * <p>
 * Note, There is a sample Python client in IRC/tools/misc/sampleXdrClient.py
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Id: XdrTcpEncoder.java,v 1.9 2006/06/14 19:01:01 smaher_cvs Exp $
 * @author $Author: smaher_cvs $
 * @see <a href="http://www.ietf.org/rfc/rfc1832.txt">RFC 1832 (XDR)</a>
 * @see gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpXdrInputAdapter
 */
public class XdrTcpEncoder extends Thread implements BasisSetTcpEncoder
{
	private static final String CLASS_NAME = XdrTcpEncoder.class.getName();
	
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger
			.getLogger(XdrTcpEncoder.class.getName());
	
	public static final String KEEP_ALL_STR = "keepAll";
	public static final String KEEP_LATEST_STR = "keepLatest";
	public static final String KEEP_EARLIEST_STR = "keepEarliest";

	private static final String NULL_OBJECT_STRING_REPRESENTATION = "NullObject";
	
	private Socket fSocket = null;
	
	private Object fOutputHandle;
	
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
	 * Constructs a new XdrTcpEncoder object.
	 *
	 * @param socket The open client socket
	 * @param keepMode The keep policy for queue
	 * @param capacity The maximum number of DataSetEvents that will be queued up.
	 * @param bufferSizeBytes The size of underlying buffers, if appropriate
	 */
	public XdrTcpEncoder(AbstractBasisSetTcpOutputAdapter outputAdapter, Socket socket, String keepMode, 
			int capacity, int bufferSizeBytes, String setupError, List inputs)
	{
		if (sLogger.isLoggable(Level.CONFIG))
		{
			sLogger
					.config("AbstractBasisSetTcpOutputAdapter, Socket, String, int - start");
		}

		fTcpOutputAdapter = outputAdapter;
		fSocket = socket;
		fOutputQueue = new KeepOptionalBoundedQueue(capacity);
		fSetupError = setupError;
		fInputs = inputs;
		
		try
		{
			fSocket.setSendBufferSize(bufferSizeBytes);
		} catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			fOutputHandle = new XdrTcpEncodingStream(socket, bufferSizeBytes);
		} catch (IOException e1)
		{
			sLogger
			.severe("AbstractBasisSetTcpOutputAdapter, Socket, String, int -  : exception: "
					+ e1);
			
			e1.printStackTrace();
			
			// This will eventually cause massive failures so
			// don't worry about catching things
		}

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
				publishErrorMessage(fSetupError);
			}			
			else
			{
				publishAllDescriptors();

				while (!isInterrupted())
				{
					try
					{
						Object queuedObject = fOutputQueue.blockingRemove();

						if (queuedObject instanceof DataSet)
						{
							publishDataSet((DataSet) queuedObject);
						} else if (queuedObject instanceof BasisBundleId)
						{
							BasisBundleDescriptor descriptor = (BasisBundleDescriptor) fTcpOutputAdapter
									.getBasisBundleDescriptorMap().get(
											queuedObject);

							if (descriptor != null)
							{
								writeDescriptorPacket(fOutputHandle,
										(BasisBundleId) queuedObject,
										descriptor);
							}
						}
					} catch (InterruptedException e)
					{
						String message = "Exception from interrupted Connection";
						sLogger.logp(Level.SEVERE, CLASS_NAME,
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
		}
		finally
		{
			try
			{
				close();
			} catch (Exception e)
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
	protected void close()
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
			
			encoderStreamClose();
			fSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			String mesg = "Shutting down inputs";
			System.out.println(mesg);
			fTcpOutputAdapter.shutdownInputs(fInputs);
		}
	}
	
	private void encoderStreamClose() throws IOException
	{
		if (fOutputHandle instanceof ObjectOutputStream)
		{
			ObjectOutputStream ooStream = (ObjectOutputStream) fOutputHandle;
			ooStream.close();
		}
		else if (fOutputHandle instanceof XdrTcpEncodingStream)
		{
			XdrTcpEncodingStream xdrEncoder = (XdrTcpEncodingStream) fOutputHandle;
			try
			{
				xdrEncoder.close();
			} catch (OncRpcException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} 	
		}		
	}
	
	private void publishErrorMessage(String mesg) throws IOException
	{

		// Update header
		Header errorHeader = new Header();
		errorHeader.fPacketType = AbstractBasisSetTcpOutputAdapter.ERROR_PACKET;
		errorHeader.fMessage = mesg;
		
		// Write header
		encodeAndEmitHeader(errorHeader);

	}
	
	/**
	 * Publishes all current descriptors.
	 */
	private void publishAllDescriptors()
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
					writeDescriptorPacket(null, bundleId, descriptor);
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
			writeBasisSetPacket(fOutputHandle, basisSet);
		}		
	}
	
	
	/**
	 * Writes the specifed descriptor to the stream.
	 * 
	 * @param byteStream	the stream to write the packet to.
	 * @param inputBasisBundleId 	the bundle id associated with this descriptor
	 * @param inputDescriptor	the descriptor to write
	 * @throws IOException 
	 */
	private void writeDescriptorPacket(
			Object stream,
			BasisBundleId inputBasisBundleId,
			BasisBundleDescriptor inputDescriptor) throws IOException {
		
		// Update header
		fHeader.fBundleId = inputBasisBundleId;
		fHeader.fPacketType = AbstractBasisSetTcpOutputAdapter.DESCRIPTOR_PACKET;
		fHeader.fSequenceNumber++;
		
		
		// Update trailer
		fTrailer.fBundleId = inputBasisBundleId;
		fTrailer.fSequenceNumber = fHeader.fSequenceNumber;
		
		// Write packet
		encodeAndEmitHeader(fHeader);
		
		byte[] inputDescriptorBytes = objectToBytes(inputDescriptor);
		encodeAndEmitByteArray(inputDescriptorBytes);
		
		encodeAndEmitTrailer(fTrailer);
		
	}
	
	/**
	 * @param inputDescriptorBytes
	 * @throws IOException 
	 */
	private void encodeAndEmitByteArray(byte[] inputDescriptorBytes) throws IOException
	{
		if (fOutputHandle instanceof ObjectOutputStream)
		{
			ObjectOutputStream ooStream = (ObjectOutputStream) fOutputHandle;
			ooStream.writeUnshared(fHeader);			
		}
		else if (fOutputHandle instanceof XdrTcpEncodingStream)
		{
			XdrTcpEncodingStream xdrEncoder = (XdrTcpEncodingStream) fOutputHandle;
			try
			{
				xdrEncoder.beginEncoding(null, 0);
				xdrEncoder.xdrEncodeByteVector(inputDescriptorBytes);
				xdrEncoder.endEncoding();
				
			} catch (OncRpcException e)
			{
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} 	
		}					
		
	}

	/**
	 * Writes the specified basis set to the stream.
	 * 
	 * @param byteStream the stream to write the data to
	 * @param basisSet the basis set to write
	 * @throws IOException 
	 */
	private void writeBasisSetPacket(
			Object stream,
			BasisSet basisSet) throws IOException
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
		
		// Write packet
		try
		{
			
			encodeAndEmitHeader(fHeader);
			
			// Write basis buffer
			writeDataBuffer(basisSet.getBasisBuffer());
			
			// Write data buffers
			for (Iterator buffers = basisSet.getDataBuffers(); buffers.hasNext();)
			{
				writeDataBuffer((DataBuffer) buffers.next());
			}
			
			encodeAndEmitTrailer(fTrailer);			
			
		}
		catch (IOException e)
		{
			String message = "Exception while writing basis set packet";
			
			sLogger.logp(
					Level.WARNING, CLASS_NAME, 
					"writeBasisSetPacket", message, e);
			throw e;
		}
			}

	/**
	 * @param stream
	 * @param header
	 * @throws IOException 
	 */
	private void encodeAndEmitHeader(AbstractBasisSetTcpOutputAdapter.Header header) throws IOException
	{
	
		if (fOutputHandle instanceof ObjectOutputStream)
		{
			ObjectOutputStream ooStream = (ObjectOutputStream) fOutputHandle;
			ooStream.writeUnshared(fHeader);			
		}
		else if (fOutputHandle instanceof XdrTcpEncodingStream)
		{
			XdrTcpEncodingStream xdrEncoder = (XdrTcpEncodingStream) fOutputHandle;
			try
			{
				xdrEncoder.beginEncoding(null, 0);
				xdrEncoder.xdrEncodeString(header.fBundleId != null ? header.fBundleId.getFullyQualifiedName() : "null");
				xdrEncoder.xdrEncodeInt(header.fNumberOfBuffers);
				xdrEncoder.xdrEncodeInt(header.fPacketType);
				xdrEncoder.xdrEncodeInt(header.fSamples);				
				xdrEncoder.xdrEncodeInt(header.fSequenceNumber);
				xdrEncoder.xdrEncodeString(header.fMessage != null ? header.fMessage : "null");				
				xdrEncoder.endEncoding();
				
			} catch (OncRpcException e)
			{
				throw new IOException(e.getMessage());
			} 	
		}			
	}

	/**
	 * @param stream
	 * @param trailer
	 * @throws IOException 
	 */
	private void encodeAndEmitTrailer(AbstractBasisSetTcpOutputAdapter.Trailer trailer) throws IOException
	{

		if (fOutputHandle instanceof ObjectOutputStream)
		{
			ObjectOutputStream ooStream = (ObjectOutputStream) fOutputHandle;
			ooStream.writeUnshared(fHeader);			
		}
		else if (fOutputHandle instanceof XdrTcpEncodingStream)
		{
			XdrTcpEncodingStream xdrEncoder = (XdrTcpEncodingStream) fOutputHandle;
			try
			{
				xdrEncoder.beginEncoding(null, 0);
				xdrEncoder.xdrEncodeString(trailer.fBundleId.getFullyQualifiedName());			
				xdrEncoder.xdrEncodeInt(trailer.fSequenceNumber);
				xdrEncoder.endEncoding();
				
			} catch (OncRpcException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} 	
		}			
	}

	/**
	 * Writes the DataBuffer to the given output stream.
	 * 
	 * @param outputStream the stream to write to
	 * @param buffer the DataBuffer to write out.
	 * @throws IOException if there is an exception writing to the stream.
	 */
	private void writeDataBuffer(DataBuffer buffer) throws IOException
	{
		Class type = buffer.getDataBufferType();
		XdrTcpEncodingStream xdrEncoder = (XdrTcpEncodingStream) fOutputHandle;
		try
		{
			xdrEncoder.beginEncoding(null, 0);
			
			// Send buffer name
			xdrEncoder.xdrEncodeString(buffer.getName());
			
			if (type == double.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.DOUBLE_TYPE);
				double[] doubles = buffer.getAsDoubleArray();
				xdrEncoder.xdrEncodeDoubleVector(doubles);			
			}
			else if (type == int.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.INT_TYPE);
				int[] ints = buffer.getAsIntArray();				
				xdrEncoder.xdrEncodeIntVector(ints);
			}
			else if (type == float.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.FLOAT_TYPE);
				xdrEncoder.xdrEncodeFloatVector(buffer.getAsFloatArray());
			}
			else if (type == short.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.SHORT_TYPE);
				xdrEncoder.xdrEncodeShortVector(buffer.getAsShortArray());
			}
			else if (type == long.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.LONG_TYPE);
				xdrEncoder.xdrEncodeLongVector(buffer.getAsLongArray());
			}
			else if (type == byte.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.BYTE_TYPE);
				xdrEncoder.xdrEncodeByteVector(buffer.getAsByteArray());
			}		
			else if (type == char.class)
			{
				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.CHAR_TYPE);
				int[] ints = buffer.getAsIntArray();				
				xdrEncoder.xdrEncodeIntVector(ints);
			}		
			else 
			{
				// It's some "custom" object, so send the string
				// representation of each object.

				xdrEncoder.xdrEncodeByte(AbstractBasisSetTcpOutputAdapter.OBJECT_TYPE);				
				Object[] objects = buffer.getAsObjectArray();
				String[] strings = new String[objects.length];
				for (int i = 0; i < objects.length; i++)
				{
					if (objects[i] != null)
					{
						strings[i] = objects[i].toString();
					}
					else
					{
						strings[i] = NULL_OBJECT_STRING_REPRESENTATION;
					}
				}
				xdrEncoder.xdrEncodeStringVector(strings);				
			}
			
			xdrEncoder.endEncoding();
			
		} catch (OncRpcException e)
		{
			throw new IOException(e.getMessage());
		} 
		
	}

	/**
	 * Convert object to a byte representation the object after
	 *  Java serialization.
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	private final byte[] objectToBytes(Object obj) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeUnshared(obj);
		oos.flush();		
		byte[] serializedDescriptorBytes = baos.toByteArray();
		oos.close();
		baos.close();			
		return serializedDescriptorBytes;
	}	
}

//
//--- Development History  ---------------------------------------------------
//
//$Log: XdrTcpEncoder.java,v $
//Revision 1.9  2006/06/14 19:01:01  smaher_cvs
//Support for encoding objects as strings with XDR format.
//
//Revision 1.8  2006/05/23 16:07:35  smaher_cvs
//Added buffer name to output packets.
//
//Revision 1.7  2006/05/22 14:07:11  smaher_cvs
//Removed unnecessary synchronization; simplified some exception handling; added storing Inputs for later shutdown.
//
//Revision 1.6  2006/05/18 14:05:19  smaher_cvs
//Checkpointing working generic TCP basis set adapters.
//
//Revision 1.5  2006/05/16 12:43:18  smaher_cvs
//Renamed BasisSet input and output adapters.
//
//Revision 1.4  2006/04/07 16:45:52  smaher_cvs
//Comments
//
//Revision 1.3  2006/04/07 16:41:01  smaher_cvs
//Made socket output buffer size the same as the XDR buffer size.
//
//Revision 1.2  2006/04/07 15:06:38  smaher_cvs
//Comments and support for underlying buffer size
//
//Revision 1.1  2006/03/31 16:27:23  smaher_cvs
//Added XDR basis set encoding, which included pulling out some common functionality from BasisSetTcpJavaSerializationOutputAdapter.
//