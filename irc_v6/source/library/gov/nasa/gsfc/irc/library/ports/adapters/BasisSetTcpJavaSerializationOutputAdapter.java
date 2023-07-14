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

import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.BasisSetTcpEncoder;
import gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.JavaSerializationTcpEncoder;

import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * A BasisSetTcpJavaSerializationInputAdapter is an OutputAdapter that converts a specified
 * BasisSet to a serialized form and publishes it over a TCP socket to one or
 * more client listeners. This component listens for and accepts connections
 * from clients based on the parameters in the descriptor. The format of the
 * serialized BasisSet is subject to change.
 * <p>
 * This OutputAdapter paired with a
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpJavaSerializationInputAdapter BasisSetTcpJavaSerializationInputAdapter}
 * on the remote receiving end allows a BasisSet to be published across a TCP
 * socket. Note since this adapter manages it's own TCP socket a Connection
 * component is not needed or used.
 * <P>
 * The configuration of this adapter is specified by an OutputAdapterDescriptor.
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
 * <td>basisRequest</td>
 * <td>-</td>
 * <td align="left">The name of the BasisBundle to publish.</td>
 * </tr>
 * <tr align="center">
 * <td>downsamplingRate</td>
 * <td>0</td>
 * <td align="left">The rate to downsample the BasisBundle.</td>
 * </tr>
 * <tr align="center">
 * <td>requestAmount</td>
 * <td>1</td>
 * <td align="left">The requested amount to use for each publication.</td>
 * </tr>
 * <tr align="center">
 * <td>requestUnit</td>
 * <td>samples</td>
 * <td align="left">The unit of the requested amount for each publication</td>
 * </tr>
 * <tr align="center">
 * <td>port</td>
 * <td>-</td>
 * <td align="left">The local port to listen on for new client connections</td>
 * </tr>
 * <tr align="center">
 * <td>connectionsAllowed</td>
 * <td>1</td>
 * <td align="left">The maximum number of simultaneous client connections
 * allowed. </td>
 * </tr>
 * <tr align="center">
 * <td>queueKeepMode</td>
 * <td>keepAll</td>
 * <td align="left">The queue mode for DataSetEvents. The options are "keepAll"
 * which will queue and potentially block the publisher of DataSetEvents when
 * the queue capacity is reached, "keepLatest" which will start discarding the
 * events from the head of the queue when the capacity is reached, and
 * "keepEarliest" which will discard new events if there is not room on the
 * queue.</td>
 * </tr>
 * <tr align="center">
 * <td>queueCapacity</td>
 * <td>10</td>
 * <td align="left">The maximum number of DataSetEvents that will be queued up.
 * </td>
 * </tr>
 * </table> </center>
 * <P>
 * A partial example IML Output Adapter description for this adapter: <BR>
 * 
 * <pre>
 *      &lt;OutputAdapter name=&quot;dataFormatter&quot; type=&quot;BasisSet&quot;&gt;
 *          &lt;Parameter name=&quot;basisRequest&quot; value=&quot;DetectorData from Anonymous&quot; /&gt;
 *          &lt;Parameter name=&quot;requestAmount&quot; value=&quot;0.5&quot; /&gt;
 *          &lt;Parameter name=&quot;requestUnit&quot; value=&quot;s&quot; /&gt;
 *          &lt;Parameter name=&quot;queueKeepMode&quot; value=&quot;keepLatest&quot; /&gt;
 *          &lt;Parameter name=&quot;queueCapacity&quot; value=&quot;1&quot; /&gt;
 *      &lt;/OutputAdapter&gt;
 * </pre>
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/05/22 14:07:49 $
 * @author Troy Ames
 */
public class BasisSetTcpJavaSerializationOutputAdapter extends AbstractBasisSetTcpOutputAdapter
{
	private static final String CLASS_NAME = BasisSetTcpJavaSerializationOutputAdapter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String DEFAULT_NAME = "BasisSet TCP Serializer";

	
	/**
	 * Constructs a new BasisSetTcpJavaSerializationOutputAdapter having a default name.
	 *
	 */
	
	public BasisSetTcpJavaSerializationOutputAdapter()
	{
		this(DEFAULT_NAME);
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationOutputAdapter having the given name.
	 *
	 */
	
	public BasisSetTcpJavaSerializationOutputAdapter(String name)
	{
		super(name);
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationOutputAdapter configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new OutputAdapter
	 */
	public BasisSetTcpJavaSerializationOutputAdapter(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpJavaSerializationOutputAdapter#allocateDataSetWriter(java.net.Socket, java.lang.String, int)
	 */
	protected BasisSetTcpEncoder allocateDataSetWriter(Socket socket, String keepMode, 
			int capacity, int bufferSizeBytes, String errorMesg, List inputs)
	{
		return new JavaSerializationTcpEncoder(this, socket, keepMode, capacity, bufferSizeBytes, errorMesg, inputs);
	}	

}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetTcpJavaSerializationOutputAdapter.java,v $
//  Revision 1.3  2006/05/22 14:07:49  smaher_cvs
//  Added passing through the Inputs that are used in the generic output adapters so that they can be stopped when the client disconnects.
//
//  Revision 1.2  2006/05/18 14:05:19  smaher_cvs
//  Checkpointing working generic TCP basis set adapters.
//
//  Revision 1.1  2006/05/16 12:43:18  smaher_cvs
//  Renamed BasisSet input and output adapters.
//
//  Revision 1.9  2006/04/11 17:01:51  smaher_cvs
//  Incorporated experimental serialization change.  The change serializes entire arrays of primitives so the "looping" through the primitives is done down in the object stream.  Also, I added a reset() call in the output stream to keep the object cache in the input stream on the client clean.  Performance improved over 20% in high-performance tests (30-40MBps).
//
//  Revision 1.1  2006/04/11 13:52:12  smaher_cvs
//  Experimenting with higher performance object stream use.
//
//  Revision 1.8  2006/04/07 15:06:13  smaher_cvs
//  Comments and support for underlying buffer size
//
//  Revision 1.7  2006/03/31 16:27:23  smaher_cvs
//  Added XDR basis set encoding, which included pulling out some common functionality from BasisSetTcpJavaSerializationOutputAdapter.
//
