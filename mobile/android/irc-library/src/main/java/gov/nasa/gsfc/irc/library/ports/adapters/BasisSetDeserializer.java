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
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.data.AbstractBasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractPipeInputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.BasisSetSerializer.Header;
import gov.nasa.gsfc.irc.library.ports.adapters.BasisSetSerializer.Trailer;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.Channels;
import java.nio.channels.ScatteringByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A BasisSetDeserializer receives
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent InputBufferEvents}
 * from a
 * {@link gov.nasa.gsfc.irc.devices.ports.connections.Connection Connection} and
 * recreates a {@link gov.nasa.gsfc.irc.data.BasisSet BasisSet} from the data.
 * The format of the data is specified by a
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetSerializer BasisSetSerializer}.
 * <p>
 * This InputAdapter paired with a <code>BasisSetDeserializer</code> on the
 * remote sending end allows a BasisSet to be published across a Connection.
 * <P>
 * <B>Note:</B> Early versions of the 1.4 JVM and even later versions on some
 * platforms have serious bugs with respect to NIO nonblocking channels. As a
 * result this class and any derived subclasses may not work reliably on all
 * platforms. Developers should test on the target platform before utilizing
 * this class.
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
 *       &lt;InputAdapter name=&quot;Test Input&quot; type=&quot;BasisSet&quot;&gt;
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
public class BasisSetDeserializer extends AbstractPipeInputAdapter
{
	private static final String CLASS_NAME = BasisSetDeserializer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "BasisSet Deserializer";
	
	private static final String BASIS_BUNDLE_SIZE_KEY ="basisBundleSize";;
	public final static String REMOTE_NAMES_KEY     = "useRemoteBundleNames";

	private Output fOutput;
	
	private InputStream fInputStream = null;
	
	private ObjectInputStream fObjectInputStream = null;
	private Map fDescriptorMap = new HashMap();
	private Map fIdMap = new HashMap();
	private int fBasisBundleSize = 5000;
	private boolean fUseRemoteSourceName = false;
	

	/**
	 *  Constructs a new BasisSetDeserializer having a default name.
	 * 
	 **/

	public BasisSetDeserializer()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new BasisSetDeserializer having the given name.
	 * 
	 *  @param name The name of the new BasisSetDeserializer
	 **/

	public BasisSetDeserializer(String name)
	{
		super(name);
		
		fOutput = new DefaultOutput(getFullyQualifiedName());
		fOutput.start();
	}
	
	
	/**
	 *  Constructs a new BasisSetDeserializer configured according to the given 
	 *  InputAdapterDescriptor.
	 * 
	 *  @param descriptor The InputAdapterDescriptor of the new BasisSetDeserializer
	 **/

	public BasisSetDeserializer(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
		
		fOutput = new DefaultOutput(getFullyQualifiedName());
		fOutput.start();
		
		configureFromDescriptor(descriptor);
	}
	
	
	/**
	 * Process the available data in the channel. This method will block 
	 * waiting for data on the stream. 
	 * 
	 * @return null.
	 * @throws IOException 
	**/
	public Object processChannel(ScatteringByteChannel channel) throws IOException
	{
		if (fInputStream == null)
		{
			fInputStream = Channels.newInputStream(channel);
		}

		while (!isKilled())
		{
			try 
			{
				fObjectInputStream = new ObjectInputStream(fInputStream);				
					
				// Read header
				Header header = (Header) fObjectInputStream.readUnshared();
				//System.out.println("Received Header:" + header.fSequenceNumber + " Samples:" + header.fSamples);
				
				if (header != null
						&& header.fPacketType == BasisSetSerializer.DESCRIPTOR_PACKET)
				{
					readDescriptorPacket(fObjectInputStream, header);
				}
				else if (header != null 
						&& header.fPacketType == BasisSetSerializer.DATA_PACKET)
				{
					readBasisSetPacket(fObjectInputStream, header);
				}
			} 
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (StreamCorruptedException e) 
			{
				// We have lost sync with the ObjectStream so try again.
				e.toString();
			}
	    }

		return null;
	}
	
	/**
	 *  Sets the Descriptor of this Adapter to the given Descriptor. 
	 *  
	 *  @param descriptor A Descriptor
	**/
	public void setDescriptor(Descriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
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
	}
	
	/**
	 * Causes this adapter to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a InputAdapterDescriptor
	 */
	private void configureFromDescriptor(InputAdapterDescriptor descriptor)
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
	 * Reads a Descriptor from the specified stream.
	 * 
	 * @param input the stream to read from
	 * @param header the associated header for this BasissetDescriptor
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
				if (fUseRemoteSourceName )
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
	 * @param header the associated header for this Basisset
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
        	//System.out.println("trailer:\n" + trailer);
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
		
		if (type == BasisSetSerializer.DOUBLE_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readDouble());
			}
		}
		else if (type == BasisSetSerializer.INT_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readInt());
			}
		}
		else if (type == BasisSetSerializer.FLOAT_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readFloat());
			}
		}
		else if (type == BasisSetSerializer.SHORT_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readShort());
			}
		}
		else if (type == BasisSetSerializer.LONG_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readLong());
			}
		}
		else if(type == BasisSetSerializer.BYTE_TYPE)
		{
			for (int i = 0; i < size; ++i)
			{
				buffer.put(i, inputStream.readByte());
			}
		}
		else if (type == BasisSetSerializer.CHAR_TYPE)
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


	private void discardDataBuffer(ObjectInputStream inputStream, int samples)
			throws IOException
	{
		byte type = inputStream.readByte();

		if (type == BasisSetSerializer.DOUBLE_TYPE)
		{
			inputStream.skipBytes(8 * samples);
		}
		else if (type == BasisSetSerializer.INT_TYPE)
		{
			inputStream.skipBytes(4 * samples);
		}
		else if (type == BasisSetSerializer.FLOAT_TYPE)
		{
			inputStream.skipBytes(4 * samples);
		}
		else if (type == BasisSetSerializer.SHORT_TYPE)
		{
			inputStream.skipBytes(2 * samples);
		}
		else if (type == BasisSetSerializer.LONG_TYPE)
		{
			inputStream.skipBytes(8 * samples);
		}
		else if(type == BasisSetSerializer.BYTE_TYPE)
		{
			inputStream.skipBytes(1 * samples);
		}
		else if (type == BasisSetSerializer.CHAR_TYPE)
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
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetDeserializer.java,v $
//  Revision 1.16  2006/05/23 16:07:03  smaher_cvs
//  Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//
//  Revision 1.15  2006/03/20 16:46:07  tames
//  Added the flag useRemoteBundleNames and capability to publish BasisBundles
//  locally by the same name as on the remote source side. With this the same
//  visualization description may be used on both sides of a connection.
//
//  Revision 1.14  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.13  2006/03/13 17:13:12  smaher_cvs
//  Trying to use descriptor to set basis bundle size.  Not working.
//
//  Revision 1.12  2006/03/10 19:30:29  tames
//  The BasisBundles created by this adapter now reflect the name of this adapter.
//
//  Revision 1.11  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.10  2005/11/21 16:13:08  tames
//  Updated Javadoc only.
//
//  Revision 1.9  2005/11/17 22:59:03  tames_cvs
//  Added StreamCorrupt Exception catch for Mac OS testing.
//
//  Revision 1.8  2005/11/16 22:46:31  chostetter_cvs
//  Organized imports
//
//  Revision 1.7  2005/11/16 21:07:48  tames
//  Revised stop behavior as an attempt to avoid a deadlock on Mac OS.
//  Removed use of a nonclosing input stream class that was impacting
//  performance.
//
//  Revision 1.6  2005/10/21 20:24:22  tames_cvs
//  Debug print statement changes only.
//
//  Revision 1.5  2005/07/20 19:59:23  tames_cvs
//  Updated for new DataBuffer architecture.
//
//  Revision 1.4  2005/07/18 21:16:20  tames_cvs
//  Parcial update to the new DataBuffer architecture. This version compiles
//  but is not complete.
//
//  Revision 1.3  2005/05/27 15:43:12  tames_cvs
//  Added a log statement.
//
//  Revision 1.2  2005/05/13 04:08:11  tames
//  Changes to reflect super class changes. Removed debug print statements.
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