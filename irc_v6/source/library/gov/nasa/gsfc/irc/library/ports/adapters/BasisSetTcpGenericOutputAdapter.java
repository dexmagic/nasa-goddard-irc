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

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.commons.xml.XmlException;
import gov.nasa.gsfc.commons.xml.XmlUtil;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleIdFactory;
import gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.BasisSetTcpEncoder;
import gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.JavaSerializationTcpEncoder;
import gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.XdrTcpEncoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

/**
 * This is a server for BasisSets that receives BasisSet requests dynamically
 * from the client. After making the initial socket connection, the client sends
 * a DataSetRequest XML document (preceeded by an integer specifying the
 * document size in bytes). The DataSetRequest contains one or more BasisSet
 * requests and a format for the data (XDR or Java serialization). This class
 * will create and add BasisRequests to the dataspace and allocate, depending on
 * the format, either an XdrTcpEncoder or JavaSerializationTcpEncoder to perform
 * the data encoding. Any errors in the request will be sent back to the client
 * in the Header (message part). The DataSetRequest is immutable. To create a new request,
 * an new socket connection and request must be made.
 * <p>
 * 
 * This OutputAdapter paired with a
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpGenericInputAdapter BasisSetTcpGenericInputAdapter}
 * on the remote receiving end allows a BasisSet to be published across a TCP
 * socket. Note since this adapter manages it's own TCP socket a Connection
 * component is not needed or used. Of course, if the "xdr" format is selected,
 * any software that interprets XDR can be used as a client (an example Python
 * client is included in the IRC source code).
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset.XdrTcpEncoder XdrTcpEncoder}
 * documents the protocol format.
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
 * 
 *  
 *         &lt;OutputAdapter name=&quot;dataFormatter&quot; type=&quot;BasisSet&quot;&gt;
 *             &lt;Parameter name=&quot;queueKeepMode&quot; value=&quot;keepLatest&quot; /&gt;
 *             &lt;Parameter name=&quot;queueCapacity&quot; value=&quot;1&quot; /&gt;
 *         &lt;/OutputAdapter&gt;
 * </pre>
 * 
 * <P>
 * The format of the XML request <strong>that comes across the socket from the
 * client</strong> is not yet formalized in an XML Schema. Below is an
 * illustrative example. Note, this document must be preceeded on the socket by
 * an integer that gives the size in bytes of the XML document string.  The integer
 * should be sent in Java/XDR/network byte order (i.e., big endian).
 * 
 * <pre>
 * 
 *                 &lt;DataSetRequest&gt;
 *                    &lt;Format&gt;xdr&lt;/Format&gt;  &lt;!-- &quot;xdr&quot; or &quot;java&quot; --&gt;
 *                    &lt;BasisBundleRequest&gt;  &lt;!-- must contain one or more BasisBundleRequests --&gt;
 *                           &lt;BasisBundleName&gt;
 *                                 SensorData.MarkIII
 *                           &lt;/BasisBundleName&gt;
 *                                   
 *                           &lt;!-- All DAC and SAE pixels for column 0 --&gt;
 *                           &lt;DataBufferName&gt;
 *                                 {DAC \[.*,0\]}  &lt;!-- &quot;{ .. }&quot; is a regular expression --&gt;
 *                           &lt;/DataBufferName&gt;
 *                           &lt;DataBufferName&gt;
 *                                 {SAE \[.*,0\]}
 *                           &lt;/DataBufferName&gt;
 *                           &lt;RequestAmount&gt;1000&lt;/RequestAmount&gt;
 *                    &lt;/BasisBundleRequest&gt;
 *      				
 *                    &lt;BasisBundleRequest&gt;
 *                          &lt;BasisBundleName&gt;
 *                              StreamConfiguration.MarkIII
 *                          &lt;/BasisBundleName&gt;
 *                          &lt;!-- Specifying no DataBufferNames means send all DataBuffers --&gt;
 *      		            &lt;RequestAmount&gt;1&lt;/RequestAmount&gt;
 *      		            &lt;RequestUnit&gt;s&lt;/RequestUnit&gt;  &lt;!-- optional --&gt;
 *      					&lt;Downsampling&gt;2&lt;/Downsampling&gt;  &lt;!-- optional --&gt;
 *                    &lt;/BasisBundleRequest&gt;
 *                 &lt;/DataSetRequest&gt;
 *   
 *  
 * </pre>
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Id: BasisSetTcpGenericOutputAdapter.java,v 1.4 2006/05/22 14:07:49
 *          smaher_cvs Exp $
 * @author $Author: smaher_cvs $
 */
public class BasisSetTcpGenericOutputAdapter extends AbstractBasisSetTcpOutputAdapter
{
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger
			.getLogger(BasisSetTcpGenericOutputAdapter.class.getName());
	
	/** 
	 * Decorator method that reads a data set request from the socket and then allocates
	 * the appropriate encoder depending on the format in the request (xdr or java).
	 */
	protected BasisSetTcpEncoder allocateDataSetWriter(Socket socket, String keepMode, int capacity,
			int bufferSizeBytes, String errorMesg, List bogusinputs)
	{
		DataSetRequest dataSetRequest = null;
		StringBuffer errorMesgB = new StringBuffer(errorMesg == null ? "" : errorMesg);
		
		try
		{
			dataSetRequest = readDataSetRequest(socket.getInputStream());
		} catch (Exception e)
		{
			e.printStackTrace();
			sLogger.severe(e.getMessage());
			errorMesgB.append(e.getMessage());
		}
		
		// "Shutdown" the original input created in
		// AbstractBasisSetTcpOutputAdapter.  This
		// will be rerun for multiple client connections,
		// but that shouldn't be a problem.
		
		shutdownInputs(new ArrayList(Arrays.asList(new Input[] {fInput})));

		/*
		 * Each Basis Bundle Request is handled by a separate Input.  This was necessary
		 * to avoid contention/deadlocks while retrieving data from multiple basis bundles.
		 * We save the Inputs so that we can start() and stop() them.
		 */
		List inputs = new ArrayList();		

		
		if (dataSetRequest != null)
		{
			try
			{
				addBasisRequests(dataSetRequest, inputs);
			} catch (Exception e)
			{
				e.printStackTrace();
				sLogger.severe(e.getMessage());
				errorMesgB.append(e.getMessage());
			}
		}

		BasisSetTcpEncoder encoder = null;
		
		if (dataSetRequest != null && dataSetRequest.getFormat() != null)
		{
			if (dataSetRequest.getFormat().equals(DataSetRequest.FORMAT_XDR))
			{
				encoder = new XdrTcpEncoder(this, socket, keepMode, capacity, bufferSizeBytes, errorMesgB.length() > 0 ? errorMesgB.toString() : null, inputs);
			}
			else if (dataSetRequest.getFormat().equals(DataSetRequest.FORMAT_JAVA))
			{
				encoder = new JavaSerializationTcpEncoder(this, socket, keepMode, capacity, bufferSizeBytes, errorMesgB.length() > 0 ? errorMesgB.toString() : null, inputs);
			}
		}
		
		return encoder;
	}





	/**
	 * Process a DataSetRequest by creating Inputs and BasisRequests, assigning
	 * them to this OutputAdapter, and starting them.
	 * @param dataSetRequest
	 * @param inputList List to which the allocated Inputs will be added
	 * @throws Exception 
	 */
	private void addBasisRequests(DataSetRequest dataSetRequest, List inputList) throws Exception
	{
		List clientBasisBundleRequests = dataSetRequest.getBasisBundleRequests();
		for (Iterator iter = clientBasisBundleRequests.iterator(); iter.hasNext();)
		{
			BasisBundleRequest clientBasisBundleRequest = (BasisBundleRequest) iter.next();
			
			Amount basisRequestAmount = null;			
			if (clientBasisBundleRequest.getRequestAmount() > 0)
			{
				basisRequestAmount = new Amount();
				basisRequestAmount.setAmount(clientBasisBundleRequest
						.getRequestAmount());

				String units = clientBasisBundleRequest.getRequestUnits();
				if (units != null && units.length() > 0)
				{
					basisRequestAmount.setUnit(units);
				}
			}
			
			String bundleName = clientBasisBundleRequest.getBasisBundleName();
			
			if (bundleName != null && bundleName.length() > 0)
			{
				//
				// Create the basis bundle request ..
				//
				BasisBundleId basisBundleId = new DefaultBasisBundleIdFactory()
						.createBasisBundleId(bundleName);
				
				// BasisRequest constructor needs a null reference to indicate
				// all data buffers should be used.
				Set dataBufferNames = null;
				if (clientBasisBundleRequest.getDataBufferNames().size() > 0)
				{
					dataBufferNames = clientBasisBundleRequest.getDataBufferNames();
				}
				
				BasisRequest basisRequest;
				if (basisRequestAmount != null)
				{
					basisRequest = new BasisRequest(basisBundleId,
							dataBufferNames, basisRequestAmount);
				}
				else
				{
					// Get all data as soon as possible
					basisRequest = 
						new BasisRequest(basisBundleId, 
								dataBufferNames);				
				}
				
				// This was set in original server ..
				basisRequest.includePendingDataOnStop();
				
				basisRequest.setDownsamplingRate(clientBasisBundleRequest.getDownsample());

				Input input = new DefaultInput();
				input.addInputListener(this);
				inputList.add(input);
				input.addBasisRequest(basisRequest);
				
				// We want to propagate error messages back to the
				// client so check and rethrow any exceptions that
				// occurred.
				if (input.getException() != null)
				{
					throw input.getException();
				}
				input.start();
			}
			else
			{
				throw new IllegalArgumentException("Empty basis bundle name");
			}

			
		}
	}	
	
	
	
	
	
	/**
	 * Read the XML DataSetRequest from the socket.
	 * 
	 * @param socket
	 * @return
	 * @throws IOException 
	 * @throws XmlException
	 * @see BasisSetTcpGenericInputAdapter 
	 */
	private DataSetRequest readDataSetRequest(InputStream is) throws IOException, XmlException
	{
		DataSetRequest dataSetRequest = null;
	
		DataInputStream dis = new DataInputStream(is);
		
		// Read size of XML request
		int size = dis.readInt();
		// Read XML request
		byte[] xmlStringBytes = new byte[size];
		dis.readFully(xmlStringBytes);

		if (sLogger.isLoggable(Level.CONFIG))
		{
			sLogger.config("InputStream - Received data request from client: : xmlStringBytes="
							+ new String(xmlStringBytes));
		}
		
		// Parse XML request string into XML document
		Document document = XmlUtil.processXmlFile(new ByteArrayInputStream(xmlStringBytes), false);
		
//		XmlUtil.dumpDom(document);
		
		// Parse XML document into DataSetRequest
		dataSetRequest = DataSetRequest.fromXml(document);
		
		return dataSetRequest;
	}


	/**
	 * XML binding class to DataSetRequest element.
	 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
	 * for the Instrument Remote Control (IRC) project.
	 * 
	 * @version	$Id: BasisSetTcpGenericOutputAdapter.java,v 1.6 2006/06/14 19:01:18 smaher_cvs Exp $
	 * @author	$Author: smaher_cvs $
	 */
	public static class DataSetRequest
	{
		/**
		 * Logger for this class
		 */
		private static final Logger sLogger = Logger
				.getLogger(DataSetRequest.class.getName());

		public final static String E_DOWNSAMPLING = "Downsampling";
		public final static String E_DATA_SET_REQUEST = "DataSetRequest";
		public final static String E_FORMAT = "Format";
		public final static String E_BASIS_BUNDLE_REQUEST = "BasisBundleRequest";
		public final static String E_BASIS_BUNDLE_NAME = "BasisBundleName";
		public final static String E_DATA_BUFFER_NAME = "DataBufferName";
		public final static String E_REQUEST_AMOUNT = "RequestAmount";
		public final static String E_REQUEST_UNIT = "RequestUnit";

		
		// Can't use 1.5 enums yet ...
		public final static String FORMAT_XDR = "xdr";
		public final static String FORMAT_JAVA = "java";		
		private final String fFormat;
		
		private final List fBasisBundleRequests;
		
		

		
		/**
		 * @param format
		 */
		public DataSetRequest(String format, List basisBundleRequests)
		{
			fFormat = format;
			fBasisBundleRequests = basisBundleRequests;
		}


		public static DataSetRequest fromXml(Object jdomElement) throws XmlException
		{
			String namespacePrefix = "";
			Namespace namespace = null;			
			List dataSetRequests = XmlUtil.selectNodes(jdomElement, "//" + DataSetRequest.E_DATA_SET_REQUEST, null);						
//			if (dataSetRequests == null || dataSetRequests.size() < 1)
//			{
//				// Didn't find any DataSetRequest elements with null namespace, so
//				// try a common namespace used in Device iml files
//				namespacePrefix = "foo:";
//				namespace = Namespace.getNamespace(namespacePrefix.substring(0, namespacePrefix.indexOf(":")), "http://aaa.gsfc.nasa.gov/iml");
//				dataSetRequests = selectNodes(jdomElement, "//" + namespacePrefix + BasisSetTcpGenericOutputAdapter.E_DATA_SET_REQUEST, namespace);						
//			}

			if (dataSetRequests == null || dataSetRequests.size() < 1)
			{
				// Still didn't find any DataSetRequest elements
				throw new XmlException("No " + DataSetRequest.E_DATA_SET_REQUEST + 
				" element found in document");
			}
			else if (dataSetRequests.size() > 1)
			{
				// This should be caught during validation, but I'm not sure yet how I'm going to
				// validate the request over the socket
				throw new XmlException("More than one " + DataSetRequest.E_DATA_SET_REQUEST + 
				" element found in document");
			}


			// Get the format
			String format = ((Text) XmlUtil.selectNode(jdomElement, "//" + namespacePrefix + DataSetRequest.E_FORMAT + "/text()", namespace)).getTextTrim().toLowerCase();
			
			// Get the basis bundle requests
			
			List basisBundleRequests = new ArrayList();			
			List basisBundles = XmlUtil.selectNodes(jdomElement, "//" + namespacePrefix + DataSetRequest.E_BASIS_BUNDLE_REQUEST, namespace);
			for (Iterator iter = basisBundles.iterator(); iter.hasNext();)
			{
				Element basisBundleRequestE = (Element) iter.next();
				basisBundleRequests.add(BasisBundleRequest.fromXml(basisBundleRequestE));
			}
			
			DataSetRequest dsr = new DataSetRequest(format, basisBundleRequests);			
			return dsr;
				
		}


		public String getFormat()
		{
			return fFormat;
		}


		public List getBasisBundleRequests()
		{
			return fBasisBundleRequests;
		}
	}
	
	/**
	 * XML binding class to BasisBundleRequest.
	 * 
	 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
	 * for the Instrument Remote Control (IRC) project.
	 * 
	 * @version	$Id: BasisSetTcpGenericOutputAdapter.java,v 1.6 2006/06/14 19:01:18 smaher_cvs Exp $
	 * @author	$Author: smaher_cvs $
	 */
	public static class BasisBundleRequest
	{
		/**
		 * Logger for this class
		 */
		private static final Logger sLogger = Logger
				.getLogger(BasisBundleRequest.class.getName());

		/**
		 * Name of basis bundle
		 */
		private final String fBasisBundleName;

		/**
		 * Names of data buffers to retrieve.  Should
		 * be null or size() == 0 to retrieve all data 
		 */
		private final Set fDataBufferNames;  // Because BasisRequest uses Set
		
		/**
		 * Request amount
		 */
		private final int fRequestAmount;
		
		/**
		 * Request Units
		 */
		private final String fRequestUnits;
		
		/**
		 * How much to downsample
		 */
		private final int fDownsample;
		
		/**
		 * @param basisBundleName
		 * @param dataBufferNames
		 * @param requestAmount
		 * @param requestUnits
		 * @param downsample TODO
		 */
		public BasisBundleRequest(String basisBundleName, Set dataBufferNames, int requestAmount, String requestUnits, int downsample)
		{
			fBasisBundleName = basisBundleName;
			fDataBufferNames = dataBufferNames;
			fRequestAmount = requestAmount;
			fRequestUnits = requestUnits;
			fDownsample = downsample;
		}

		public String getBasisBundleName()
		{
			return fBasisBundleName;
		}

		public Set getDataBufferNames()
		{
			return fDataBufferNames;
		}

		public int getRequestAmount()
		{
			return fRequestAmount;
		}

		/**
		 * @return Returns the downsample.
		 */
		public int getDownsample()
		{
			return fDownsample;
		}

		public String getRequestUnits()
		{
			return fRequestUnits;
		}

		/**
		 * Create a request from an XML element
		 * @param jdomElement
		 * @return
		 * @throws XmlException
		 */
		public static BasisBundleRequest fromXml(Object jdomElement) throws XmlException
		{

			//
			// Parse required basis bundle name, and optional request amount
			//
			
			String basisBundleName = ((Text) XmlUtil.selectNode(jdomElement, "./" + DataSetRequest.E_BASIS_BUNDLE_NAME + "/text()", null)).getTextTrim();
			
			List requestAmountStrings =  XmlUtil.selectNodes(jdomElement, "./" + DataSetRequest.E_REQUEST_AMOUNT + "/text()", null);
			
			if (requestAmountStrings.size() > 1)
			{
				throw new XmlException("More than one " + DataSetRequest.E_REQUEST_AMOUNT + " element not allowed");
			}
			
			int requestAmount = -1;
			if (requestAmountStrings.size() == 1)
			{
				String requestAmountStr = ((Text) requestAmountStrings.get(0)).getTextTrim();
				try
				{
					requestAmount = Integer.parseInt(requestAmountStr);
				} catch (NumberFormatException e)
				{
					throw new XmlException("Couldn't parse request amount: " + requestAmountStr);
				}				
			}
			

			
			//
			// Get optional request unit
			//
			String requestUnit = ""; // TODO does this means "samples"?
			List requestUnits = XmlUtil.selectNodes(jdomElement, "./" + DataSetRequest.E_REQUEST_UNIT + "/text()", null);
			if (requestUnits.size() > 1)
			{
				throw new XmlException("More than one " + DataSetRequest.E_REQUEST_UNIT + " element not allowed");
			}
			if (requestUnits.size() == 1)
			{
				requestUnit = ((Text) requestUnits.get(0)).getTextTrim();
			}

			//
			// Get optional downsample
			//
			int downsample = 0;
			List downsamples = XmlUtil.selectNodes(jdomElement, "./" + DataSetRequest.E_DOWNSAMPLING + "/text()", null);
			if (downsamples.size() > 1)
			{
				throw new XmlException("More than one " + DataSetRequest.E_DOWNSAMPLING + " element not allowed");
			}
			if (downsamples.size() == 1)
			{
				String downsampleStr = ((Text) downsamples.get(0)).getTextTrim();
				try
				{
					downsample = Integer.parseInt(downsampleStr);
				} catch (NumberFormatException e)
				{
					throw new XmlException("Couldn't parse downsample amount: " + downsampleStr);
				}
			}			
			
			//
			// Get optional data buffers
			//
			List dataBufferNamesE = XmlUtil.selectNodes(jdomElement, "./" + DataSetRequest.E_DATA_BUFFER_NAME + "/text()", null);						
			HashSet dataBufferNames = new HashSet();
			for (Iterator iter = dataBufferNamesE.iterator(); iter.hasNext();)
			{
				Text element = ((Text) iter.next());
				dataBufferNames.add(element.getTextTrim());
			}

			return new BasisBundleRequest(basisBundleName, dataBufferNames, requestAmount, requestUnit, downsample);
		}
		
		
	}

}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisSetTcpGenericOutputAdapter.java,v $
//	Revision 1.6  2006/06/14 19:01:18  smaher_cvs
//	Allow no request amount to be specified for DataSetRequests.
//	
//	Revision 1.5  2006/05/23 16:07:03  smaher_cvs
//	Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//	
//	Revision 1.4  2006/05/22 14:07:49  smaher_cvs
//	Added passing through the Inputs that are used in the generic output adapters so that they can be stopped when the client disconnects.
//	
//	Revision 1.3  2006/05/18 14:04:20  smaher_cvs
//	Checkpointing working generic TCP basis set adapters.
//	
//	Revision 1.1  2006/05/17 21:08:23  smaher_cvs
//	Early snapshot to fix compilation errors (dependency in other class).
//	