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
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter.Header;
import gov.nasa.gsfc.irc.library.ports.adapters.AbstractBasisSetTcpOutputAdapter.Trailer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A BasisSetTcpJavaSerializationInputAdapter manages a TCP/IP socket and recreates a
 * {@link gov.nasa.gsfc.irc.data.BasisSet BasisSet} from the data received over
 * the socket. The format of the data is specified by and must be paired with a
 * remote instance of a
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpJavaSerializationOutputAdapter BasisSetTcpJavaSerializationOutputAdapter}
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
 * @version $Date: 2006/05/23 16:07:03 $
 * @author tames
 */
public class BasisSetTcpJavaSerializationInputAdapter extends AbstractBasisSetTcpInputAdapter
{
	static final String CLASS_NAME = BasisSetTcpJavaSerializationInputAdapter.class.getName();
	static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "BasisSet TCP Java Deserializer";

	private InputStream fInputStream = null;
	
	private ObjectInputStream fObjectInputStream = null;
	private Map fDescriptorMap = new HashMap();
	private Map fIdMap = new HashMap();
	/**
	 * Constructs a new BasisSetTcpJavaSerializationInputAdapter having a default name and managed by 
	 * the default ComponentManager.
	 *
	 */
	
	public BasisSetTcpJavaSerializationInputAdapter()
	{
		super(DEFAULT_NAME);	

		fOutput = new DefaultOutput(getFullyQualifiedName());
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationInputAdapter having the given name and managed by 
	 * the default ComponentManager.
	 *
	 */
	
	public BasisSetTcpJavaSerializationInputAdapter(String name)
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
	
	public BasisSetTcpJavaSerializationInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
		
		fOutput = new DefaultOutput(getFullyQualifiedName());

		configureFromDescriptor(descriptor);
	}


	/**
	 * Reads a Descriptor from the specified stream.
	 * 
	 * @param input the stream to read from
	 * @param header the associated header for this BasisSetDescriptor
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readDescriptorPacket(ObjectInputStream input, Header header) 
		throws IOException, ClassNotFoundException
	{
		// Read Descriptor
		BasisBundleDescriptor descriptor = 
			(BasisBundleDescriptor) input.readUnshared();

		String remoteBundleName = header.fBundleId.getFullyQualifiedName();
		
		Trailer trailer = (Trailer) input.readUnshared();
		
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
	private void readBasisSetPacket(ObjectInputStream input, Header header) 
		throws IOException, ClassNotFoundException
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
    		readDataBuffer(input, newBasisSet.getBasisBuffer());
        	
        	for (Iterator buffers = newBasisSet.getDataBuffers(); 
        		buffers.hasNext();)
        	{
        		// Fully read in the data channel
        		readDataBuffer(input, (DataBuffer) buffers.next());
        	}
    	}
    	else
    	{
    		// Since we do not have a descriptor for this data 
    		// just skip the block.
    		//System.out.println("skipping channel data |||||||||||||");
    		//input.skipBytes((int) header.fSizeInBytes);
    		for (int i = 0; i < header.fNumberOfBuffers; i++)
    		{
    			discardDataBuffer(input, header.fSamples);
    		}
    	}
    	
    	// Read trailer object
		Trailer trailer = (Trailer) input.readUnshared();
		
		// Verify we received a complete valid packet and BasisSet
		if (newBasisSet != null 
				&& header.fSequenceNumber == trailer.fSequenceNumber)
		{
        	fOutput.makeAvailable(newBasisSet);   		
		}
		
		if (sLogger.isLoggable(Level.FINE))
		{
			sLogger.info("ObjectInputStream, Header - Received BasisSet");
		}
	}

	/**
	 * Reads the DataBuffer from the given input stream.
	 * 
	 * @param inputStream the stream to read from
	 * @param buffer the DataBuffer to read into.
	 * @throws IOException if there is an exception reading from the stream.
	 */
	private void readDataBuffer(
			ObjectInputStream inputStream, DataBuffer buffer) throws IOException
	{
		int size = buffer.getSize();
		byte type = inputStream.readByte();
		
		if (type == AbstractBasisSetTcpOutputAdapter.DOUBLE_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readDouble());
			}
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.INT_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readInt());
			}
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.FLOAT_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readFloat());
			}
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.SHORT_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readShort());
			}
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.LONG_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readLong());
			}
		}
		else if(type == AbstractBasisSetTcpOutputAdapter.BYTE_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readByte());
			}
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.CHAR_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readChar());
			}
		}
		else	// Type must be Object or subclass
		{
			for (int i = 0; i < size; ++i)
			{
				try
				{
					buffer.put(i, inputStream.readObject());
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Discards the DataBuffer from the given input stream.
	 * 
	 * @param inputStream the stream to read from
	 * @param samples the size in samples of the data buffer to discard.
	 * @throws IOException if there is an exception reading from the stream.
	 */
	private void discardDataBuffer(ObjectInputStream inputStream, int samples)
			throws IOException
	{
		byte type = inputStream.readByte();

		if (type == AbstractBasisSetTcpOutputAdapter.DOUBLE_TYPE)
		{
			inputStream.skipBytes(8 * samples);
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.INT_TYPE)
		{
			inputStream.skipBytes(4 * samples);
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.FLOAT_TYPE)
		{
			inputStream.skipBytes(4 * samples);
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.SHORT_TYPE)
		{
			inputStream.skipBytes(2 * samples);
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.LONG_TYPE)
		{
			inputStream.skipBytes(8 * samples);
		}
		else if(type == AbstractBasisSetTcpOutputAdapter.BYTE_TYPE)
		{
			inputStream.skipBytes(1 * samples);
		}
		else if (type == AbstractBasisSetTcpOutputAdapter.CHAR_TYPE)
		{
			inputStream.skipBytes(2 * samples);
		}
		else	// Type must be Object or subclass
		{
			for (int i = 0; i < samples; ++i)
			{
				try
				{
					// TODO should this be readUnshared
					inputStream.readObject();
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
		fInputStream = Channels.newInputStream(fSocket);
		fObjectInputStream = new ObjectInputStream(fInputStream);							

		while (!isKilled())
		{
			try 
			{
				// Read header
				Header header = (Header) fObjectInputStream.readUnshared();
				
				switch (header.fPacketType) {
				case AbstractBasisSetTcpOutputAdapter.ERROR_PACKET:
					String mesg = "Error returned from server: " + header.fMessage;
					System.out.println(mesg);
					sLogger.severe(mesg);
					break;
				case AbstractBasisSetTcpOutputAdapter.DESCRIPTOR_PACKET:
					readDescriptorPacket(fObjectInputStream, header);
					break;
				case AbstractBasisSetTcpOutputAdapter.DATA_PACKET:
					readBasisSetPacket(fObjectInputStream, header);
					break;
				default:
					String mesg2 = "Unknown header type: " + header.fPacketType;
					System.out.println(mesg2);
					sLogger.severe(mesg2);
					break;			
				}
				
				
			} 
			catch (ClassNotFoundException e)
			{
				String message = e.getLocalizedMessage() + " "
						+ getFullyQualifiedName()
						+ " did not recognize class";
				sLogger.logp(Level.WARNING, CLASS_NAME, "serviceConnection",
					message);
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
				fSocket.close();
				fInputStream.close();
			}
			catch (IOException e)
			{
				String message = getFullyQualifiedName() + " could not close ";
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"closeConnection", message, e);
			}
			finally 
			{
				fSocket = null;
				fInputStream = null;
			}
		}
	}

}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetTcpJavaSerializationInputAdapter.java,v $
//  Revision 1.3  2006/05/23 16:07:03  smaher_cvs
//  Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//
//  Revision 1.2  2006/05/18 14:05:19  smaher_cvs
//  Checkpointing working generic TCP basis set adapters.
//
//  Revision 1.1  2006/05/16 12:43:18  smaher_cvs
//  Renamed BasisSet input and output adapters.
//
//  Revision 1.11  2006/04/17 18:51:30  tames_cvs
//  Reverted back the serialization of DataBuffers to be the writing and reading
//  of each element of the buffer instead of the array returned by the method
//  getAs<Type>Array(). This array method call results in the DataBuffer data
//  being copied into the array on the sender side. The client side then must
//  deserialize the array which allocates the array before writing it to the buffer.
//  It appears at least on a PC that this extra memory allocation on both sides,
//  plus the data copy on the source side has higher cost then iterating over
//  the contents of the DataBuffer. I did not test what influence the size of the
//  DataBuffer has on the performance.
//
//  Revision 1.10  2006/04/14 13:12:51  smaher_cvs
//  Added some logging.
//
//  Revision 1.9  2006/04/11 17:01:51  smaher_cvs
//  Incorporated experimental serialization change.  The change serializes entire arrays of primitives so the "looping" through the primitives is done down in the object stream.  Also, I added a reset() call in the output stream to keep the object cache in the input stream on the client clean.  Performance improved over 20% in high-performance tests (30-40MBps).
//
//  Revision 1.1  2006/04/11 13:52:12  smaher_cvs
//  Experimenting with higher performance object stream use.
//
//  Revision 1.8  2006/03/31 16:27:23  smaher_cvs
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