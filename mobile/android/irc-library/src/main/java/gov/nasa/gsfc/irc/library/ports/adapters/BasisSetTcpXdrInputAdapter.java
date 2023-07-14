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


import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.data.AbstractBasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleId;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter.Header;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter.Trailer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrTcpDecodingStream;


/**
 * A BasisSetTcpXdrInputAdapter manages a TCP/IP socket and recreates a
 * {@link gov.nasa.gsfc.irc.data.BasisSet BasisSet} from the data received over
 * the socket. The format of the data is specified by and must be paired with a
 * remote instance of a
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpXdrOutputAdapter BasisSetTcpXdrOutputAdapter}
 * output adapter.
 * <P>
 * The configuration of this adapter is specified by an InputAdapterDescriptor.
 * The table below gives the configuration parameters that this adapter uses. If
 * the parameter is missing then the default value will be used. If there is not
 * a default value specified then the parameter is required to be in the
 * descriptor except where noted.
 * <P>
 * <center><table border="1">
 * <tr align="center">
 * <th>Key</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * <tr align="center">
 * <td>hostname</td>
 * <td>localhost</td>
 * <td align="left">The remote host name to connect to</td>
 * </tr>
 * <tr align="center">
 * <td>port</td>
 * <td>-</td>
 * <td align="left">The remote port to connect to</td>
 * </tr>
 * <tr align="center">
 * <td>retryDelay</td>
 * <td>1000</td>
 * <td align="left">The delay in milliseconds between repeated reconnect
 * attempts. </td>
 * </tr>
 * <tr align="center">
 * <td>basisBundleSize</td>
 * <td>5000</td>
 * <td align="left">The default size of BasisBundles created by this source. </td>
 * </tr>
 * <tr align="center">
 * <td>useRemoteBundleNames</td>
 * <td>false</td>
 * <td align="left">Flag to use the same remote Bundle names locally. </td>
 * </tr>
 * </table> </center>
 * <P>
 * A partial example IML port description for this type of adapter: <BR>
 * 
 * <pre>
 *       &lt;InputAdapter name=&quot;Test Input&quot; type=&quot;BasisSetTcp&quot;&gt;
 *           &lt;Parameter name=&quot;hostname&quot; value=&quot;localhost&quot; /&gt;
 *           &lt;Parameter name=&quot;port&quot; value=&quot;9999&quot; /&gt;
 *           &lt;Parameter name=&quot;retryDelay&quot; value=&quot;5000&quot; /&gt;
 *           &lt;Parameter name=&quot;basisBundleSize&quot; value=&quot;5000&quot; /&gt;          
 *           &lt;Parameter name=&quot;useRemoteBundleNames&quot; value=&quot;true&quot; /&gt;          
 *           ...
 *       &lt;/InputAdapter&gt;
 * </pre>
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/06/14 19:01:01 $
 * @author tames
 * @author Steve Maher
 */
public class BasisSetTcpXdrInputAdapter extends AbstractBasisSetTcpInputAdapter
{
	static final String CLASS_NAME = BasisSetTcpXdrInputAdapter.class.getName();
	static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "BasisSet TCP XDR Decoder";

	private Map fDescriptorMap = new HashMap();
	private Map fIdMap = new HashMap();
	private XdrTcpDecodingStream fXdrTcpDecodingStream;
	
	
	/**
	 * Constructs a new BasisSetTcpJavaSerializationInputAdapter having a default name and managed by 
	 * the default ComponentManager.
	 *
	 */
	
	public BasisSetTcpXdrInputAdapter()
	{
		super(DEFAULT_NAME);	

		fOutput = new DefaultOutput(getFullyQualifiedName());
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationInputAdapter having the given name and managed by 
	 * the default ComponentManager.
	 *
	 */
	
	public BasisSetTcpXdrInputAdapter(String name)
	{
		super(name);	

		fOutput = new DefaultOutput(getFullyQualifiedName());
	}


	/**
	 *	Constructs a new BasisSetTcpJavaSerializationInputAdapter configured according to the given 
	 *  InputAdapterDescriptor.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new 
	 *  		BasisSetTcpJavaSerializationInputAdapter
	 */
	
	public BasisSetTcpXdrInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
		
		fOutput = new DefaultOutput(getFullyQualifiedName());

		configureFromDescriptor(descriptor);
	}


	private final Object bytesToObject(byte[] bytes) throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object object;
		try
		{
			object = ois.readUnshared();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		ois.close();
		bais.close();			
		return object;
	}


	/**
	 * @param inputDescriptorBytes
	 * @throws IOException 
	 */
	private byte[] readByteArray() throws IOException
	{
		XdrTcpDecodingStream xdrDecoder = (XdrTcpDecodingStream) fXdrTcpDecodingStream;		
		byte[] bytes = null;

		try
		{
			xdrDecoder.beginDecoding();
			bytes = xdrDecoder.xdrDecodeByteVector();
			xdrDecoder.endDecoding();
		} catch (OncRpcException e)
		{
			throw new IOException(e.getMessage());
		}	
		
		return bytes;

	}


	/**
	 * Reads a Descriptor from the specified stream.
	 * 
	 * @param input the stream to read from
	 * @param header the associated header for this BasisSetDescriptor
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readDescriptorPacket(Header header) 
		throws IOException
	{

		BasisBundleDescriptor descriptor = (BasisBundleDescriptor) bytesToObject(readByteArray());
		
		String remoteBundleName = header.fBundleId.getFullyQualifiedName();
		Trailer trailer = readAndDecodeTrailer(fXdrTcpDecodingStream);		
		
		
		// Verify we received a complete packet
		if (header.fSequenceNumber == trailer.fSequenceNumber)
		{

			// Save the descriptor for future use
			fDescriptorMap.put(remoteBundleName, descriptor);

			// Check if we have seen this bundle Id before
			BasisBundleId localBundleId = (BasisBundleId) fIdMap
					.get(remoteBundleName);

			if (localBundleId != null)
			{
				// Update descriptor for BasisBundle
				fOutput.restructureBasisBundle(localBundleId, descriptor);
			}
			else
			{
				// We have not seen this Id before so add it and save
				// the local bundle Id for future use.
				if (fUseRemoteSourceName)
				{
					BasisBundleSource remoteSourceProxy = 
						new AbstractBasisBundleSource(
							header.fBundleId.getNameQualifier()){};
						
					localBundleId = fOutput.addBasisBundle(
						descriptor, remoteSourceProxy, fBasisBundleSize);
				}
				else
				{
					localBundleId = 
						fOutput.addBasisBundle(descriptor, fBasisBundleSize);
				}
				
				fIdMap.put(remoteBundleName, localBundleId);

				if (sLogger.isLoggable(Level.FINER))
				{
					String message = 
						"Received new BasisBundle Descriptor, RemoteId:" 
						+ remoteBundleName + " LocalId:" + localBundleId;
					
					sLogger.logp(Level.FINER, CLASS_NAME, 
						"readDescriptorPacket", message);
				}
			}
		}
	}
	
	/**
	 * Reads a BasisSet from the specified stream.
	 * 
	 * @param input the stream to read from
	 * @param header the associated header for this BasisSet
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readBasisSetPacket(Header header) 
		throws IOException
	{
		String remoteBundleName = header.fBundleId.getFullyQualifiedName();

		// Get the cached descriptor
    	BasisBundleDescriptor descriptor = (BasisBundleDescriptor) 
				fDescriptorMap.get(remoteBundleName);
    	
    	BasisSet newBasisSet = null;

		// Read or skip data
    	if (descriptor != null)
    	{
    		BasisBundleId localBundleId = 
    			(BasisBundleId) fIdMap.get(remoteBundleName);
    		newBasisSet = fOutput.allocateBasisSet(localBundleId, header.fSamples);

    		// Fully read in the basis channel
    		readDataBuffer(newBasisSet.getBasisBuffer(), fXdrTcpDecodingStream);
        	
        	for (Iterator buffers = newBasisSet.getDataBuffers(); 
        		buffers.hasNext();)
        	{
        		// Fully read in the data channel
        		readDataBuffer((DataBuffer) buffers.next(), fXdrTcpDecodingStream);
        	}
    	}
    	else
    	{
    		// Since we do not have a descriptor for this data 
    		// just skip the block.
    		for (int i = 0; i < header.fNumberOfBuffers; i++)
    		{
    			discardDataBuffer(header.fSamples);
    		}
    	}
    	
    	// Read trailer object
    	Trailer trailer = readAndDecodeTrailer(fXdrTcpDecodingStream);	
		
		// Verify we received a complete valid packet and BasisSet
		if (newBasisSet != null 
				&& header.fSequenceNumber == trailer.fSequenceNumber)
		{
        	fOutput.makeAvailable(newBasisSet);   		
		}
	}

	/**
	 * Reads the DataBuffer from the given input stream.
	 * @param buffer the DataBuffer to read into.
	 * @param xdrTcpDecodingStream TODO
	 * @param inputStream the stream to read from
	 * 
	 * @throws IOException if there is an exception reading from the stream.
	 */
	private void readDataBuffer(DataBuffer buffer, XdrTcpDecodingStream xdrTcpDecodingStream) throws IOException
	{
		XdrTcpDecodingStream xdrDecoder = fXdrTcpDecodingStream;	
		try 
		{
			xdrTcpDecodingStream.beginDecoding();
			
			String bufferName = xdrDecoder.xdrDecodeString();
			
			// Just a double check - hopefully not too expensive
			if (bufferName.equals(buffer.getName()) == false)
			{
				throw new IOException("Buffer names differ: from descriptor=" + buffer.getName() + ", from OutputAdapter (socket)=" + bufferName);
			}
			
			byte type = xdrDecoder.xdrDecodeByte();
//			byte type = inputStream.readByte();
			
			switch (type)
			
			{
			case AbstractBasisSetTcpOutputAdapter.DOUBLE_TYPE:
				
				double[] doubles = xdrDecoder.xdrDecodeDoubleVector();
				buffer.put(0, doubles, 0, doubles.length);
				break;
			case AbstractBasisSetTcpOutputAdapter.INT_TYPE:
				int[] ints = xdrDecoder.xdrDecodeIntVector();
				buffer.put(0, ints, 0, ints.length);
//				System.out.println("XXXX  object input: " + Arrays.toString(ints));				
				break;
			case AbstractBasisSetTcpOutputAdapter.FLOAT_TYPE:
				float[] floats = xdrDecoder.xdrDecodeFloatVector();
				buffer.put(0, floats, 0, floats.length);
				break;
			case AbstractBasisSetTcpOutputAdapter.SHORT_TYPE:
				short[] shorts = xdrDecoder.xdrDecodeShortVector();
				buffer.put(0, shorts, 0, shorts.length);
				break;
			case AbstractBasisSetTcpOutputAdapter.LONG_TYPE:
				long[] longs = xdrDecoder.xdrDecodeLongVector();
				buffer.put(0, longs, 0, longs.length);
				break;
			case AbstractBasisSetTcpOutputAdapter.BYTE_TYPE:
				byte[] bytes = xdrDecoder.xdrDecodeByteVector();
				buffer.put(0, bytes, 0, bytes.length);
				break;	
			case AbstractBasisSetTcpOutputAdapter.CHAR_TYPE:
				// Need to do a conversion to char[]
				//
				int[] intsTemp = xdrDecoder.xdrDecodeIntVector();
				char[] chars = new char[intsTemp.length];
				for (int i = 0; i < intsTemp.length; i++)
				{
					chars[i] = (char) intsTemp[i];
				}
				buffer.put(0, chars, 0, chars.length);
				break;				

			case AbstractBasisSetTcpOutputAdapter.OBJECT_TYPE:
				String[] strings = xdrDecoder.xdrDecodeStringVector();
				buffer.put(0, strings, 0, strings.length);			
//				System.out.println("XXXX  object input: " + Arrays.toString(strings));
				break;	

			default:
				String mesg = "Unsupported type: " + type;
				sLogger.severe(mesg);
			
//				throw new IOException();
			}
			xdrDecoder.endDecoding();

		}
		catch (OncRpcException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Discards the DataBuffer from the given input stream.
	 * 
	 * @param inputStream the stream to read from
	 * @param samples the size in samples of the data buffer to discard.
	 * @throws IOException if there is an exception reading from the stream.
	 */
	private void discardDataBuffer(int samples)
			throws IOException
	{
		XdrTcpDecodingStream xdrDecoder = (XdrTcpDecodingStream) fXdrTcpDecodingStream;	

		try
		{		
			xdrDecoder.beginDecoding();
			byte type = xdrDecoder.xdrDecodeByte();
			
			int numBytes = 0;
			
			if (type == AbstractBasisSetTcpOutputAdapter.DOUBLE_TYPE)
			{
				
				numBytes = 8 * samples;
			}
			else if (type == AbstractBasisSetTcpOutputAdapter.INT_TYPE)
			{
				numBytes = 4 * samples;
			}
			else if (type == AbstractBasisSetTcpOutputAdapter.FLOAT_TYPE)
			{
				numBytes = 4 * samples;
			}
			else if (type == AbstractBasisSetTcpOutputAdapter.SHORT_TYPE)
			{
				numBytes = 2 * samples;
			}
			else if (type == AbstractBasisSetTcpOutputAdapter.LONG_TYPE)
			{
				numBytes = 8 * samples;
			}
			else if(type == AbstractBasisSetTcpOutputAdapter.BYTE_TYPE)
			{
				numBytes = 1 * samples;
			}
			else if (type == AbstractBasisSetTcpOutputAdapter.CHAR_TYPE)
			{
				numBytes = 2 * samples;
			}
			else	// Type must be Object or subclass
			{
//				for (int i = 0; i < samples; ++i)
//				{
//				try
//				{
//				// TODO should this be readUnshared
//				inputStream.readObject();
//				}
//				catch (ClassNotFoundException e)
//				{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				}
//				}
				System.err.println("Hmm trying to skip objects - not implemented yet");
			}
			

			xdrDecoder.xdrDecodeByteFixedVector(numBytes);
			xdrDecoder.endDecoding();			
		} catch (OncRpcException e)
		{
			throw new IOException(e.getMessage());
		} 				
	}
	
	//============================================================================
	// CONNECTION SUPPORT
	//============================================================================
	
	/**
	 * Reads data from a stream. This method does not return unless this
	 * component is killed, stopped, or an exception is encountered.
	 * @throws IOException 
	 */
	protected void serviceConnection() throws IOException
	{

		fXdrTcpDecodingStream = new XdrTcpDecodingStream(fSocket.socket(), fBufferSizeBytes);
		
		while (!isKilled())
		{
			Header header = readAndDecodeHeader(fXdrTcpDecodingStream);
//			System.out.println("XXX header: " + header);
			
			switch (header.fPacketType) {
			case AbstractBasisSetTcpOutputAdapter.ERROR_PACKET:
				String mesg = "Error returned from server: " + header.fMessage;
				System.out.println(mesg);
				sLogger.severe(mesg);
				break;
			case AbstractBasisSetTcpOutputAdapter.DESCRIPTOR_PACKET:
				readDescriptorPacket(header);
				break;
			case AbstractBasisSetTcpOutputAdapter.DATA_PACKET:
				readBasisSetPacket(header);
				break;
			default:
				String mesg2 = "Unknown header type: " + header.fPacketType;
				System.out.println(mesg2);
				sLogger.severe(mesg2);
				break;			
			}
			
	    }
	}
	
	/**
		 * @param xdrTcpDecodingStream TODO
	 * @return
		 * @throws IOException 
		 * @throws OncRpcException 
		 * @throws OncRpcException 
		 */
		private Header readAndDecodeHeader(XdrTcpDecodingStream xdrTcpDecodingStream) throws IOException
		{
			XdrTcpDecodingStream xdrDecoder = (XdrTcpDecodingStream) fXdrTcpDecodingStream;
			Header header = new Header();
	
			try
			{
	//			xdrDecoder.setCharacterEncoding(null);
				xdrTcpDecodingStream.beginDecoding();
				header.fBundleId = new DefaultBasisBundleId(xdrDecoder.xdrDecodeString());
				header.fNumberOfBuffers = xdrDecoder.xdrDecodeInt();
				header.fPacketType = xdrDecoder.xdrDecodeInt();
				header.fSamples = xdrDecoder.xdrDecodeInt();				
				header.fSequenceNumber = xdrDecoder.xdrDecodeInt();
				header.fMessage = xdrDecoder.xdrDecodeString();				
				xdrDecoder.endDecoding();
			} catch (OncRpcException e)
			{
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}			
		
			return header;
		}


	/**
	 * @param xdrTcpDecodingStream TODO
	 * @return
	 * @throws IOException 
	 * @throws OncRpcException 
	 * @throws OncRpcException 
	 */
	private Trailer readAndDecodeTrailer(XdrTcpDecodingStream xdrTcpDecodingStream) throws IOException
	{
		XdrTcpDecodingStream xdrDecoder = (XdrTcpDecodingStream) fXdrTcpDecodingStream;
		Trailer trailer = new Trailer();

		try
		{
			xdrTcpDecodingStream.beginDecoding();
			trailer.fBundleId = new DefaultBasisBundleId(xdrDecoder.xdrDecodeString());			
			trailer.fSequenceNumber = xdrDecoder.xdrDecodeInt();
			xdrDecoder.endDecoding();
		} catch (OncRpcException e)
		{
			throw new IOException(e.getMessage());
		}			
	
		return trailer;
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
				fXdrTcpDecodingStream.close();
			}
			catch (Exception e)
			{
				String message = getFullyQualifiedName() + " could not close ";
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"closeConnection", message, e);
			}
			finally
			{
				fSocket = null;
			}
		}
	}


	

}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetTcpXdrInputAdapter.java,v $
//  Revision 1.4  2006/06/14 19:01:01  smaher_cvs
//  Support for encoding objects as strings with XDR format.
//
//  Revision 1.3  2006/05/23 16:07:03  smaher_cvs
//  Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//
//  Revision 1.2  2006/05/18 14:05:19  smaher_cvs
//  Checkpointing working generic TCP basis set adapters.
//
//  Revision 1.1  2006/05/16 12:43:18  smaher_cvs
//  Renamed BasisSet input and output adapters.
//
//  Revision 1.3  2006/04/27 18:51:52  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2006/04/07 15:06:13  smaher_cvs
//  Comments and support for underlying buffer size
//
//  Revision 1.1  2006/03/31 16:27:23  smaher_cvs
//  Added XDR basis set encoding, which included pulling out some common functionality from BasisSetTcpJavaSerializationOutputAdapter.
//
//  Revision 1.7  2006/03/20 16:46:07  tames
//  Added the flag useRemoteBundleNames and capability to publish BasisBundles
//  locally by the same name as on the remote source side. With this the same
//  visualization description may be used on both sides of a connection.
//
//  Revision 1.6  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.5  2006/03/13 16:51:54  smaher_cvs
//  Added property to set basis bundle size
//
//  Revision 1.4  2006/03/10 19:30:29  tames
//  The BasisBundles created by this adapter now reflect the name of this adapter.
//
//  Revision 1.3  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/11/29 16:42:38  tames
//  Corrected Javadoc.
//
//  Revision 1.1  2005/11/28 23:18:20  tames
//  Initial Implementation.
//
//
//