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

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.xml.XmlException;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractPortAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpGenericOutputAdapter.DataSetRequest;

import java.beans.PropertyVetoException;
import java.util.logging.Logger;


/**
 * This class allows BasisSets to be received from a corresponding
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpGenericOutputAdapter BasisSetTcpGenericOutputAdapter}.
 * The XML description of this InputAdapter has a DataSetRequest element that
 * contains a description for the BasisSets that will be received by this
 * client. The definition of the element is described in
 * {@link gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpGenericOutputAdapter BasisSetTcpGenericOutputAdapter}.
 * <P>
 * The configuration of this adapter is specified by an InputAdapterDescriptor.
 * The table below gives the configuration parameters that this adapter uses. If
 * the parameter is missing then the default value will be used. If there is not
 * a default value specified then the parameter is required to be in the
 * descriptor except where noted
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
 * <td align="left">The default size of BasisBundles created by this source.
 * </td>
 * </tr>
 * <tr align="center">
 * <td>useRemoteBundleNames</td>
 * <td>false</td>
 * <td align="left">Flag to use the same remote Bundle names locally. </td>
 * </tr>
 * </table> </center>
 * <P>
 * A partial example IML port description for this type of adapter: <BR>
 * <pre>
 *           &lt;InputAdapter name=&quot;Test Input&quot; type=&quot;BasisSetTcp&quot;&gt;
 *               &lt;Parameter name=&quot;hostname&quot; value=&quot;localhost&quot; /&gt;
 *               &lt;Parameter name=&quot;port&quot; value=&quot;9999&quot; /&gt;
 *               &lt;Parameter name=&quot;retryDelay&quot; value=&quot;5000&quot; /&gt;
 *               &lt;Parameter name=&quot;basisBundleSize&quot; value=&quot;5000&quot; /&gt;          
 *               &lt;Parameter name=&quot;useRemoteBundleNames&quot; value=&quot;true&quot; /&gt;          
 *               
 *               &lt;DataSetRequest&gt;
 *                  &lt;Format&gt;xdr&lt;/Format&gt;  &lt;!-- &quot;xdr&quot; or &quot;java&quot; --&gt;
 *                  &lt;BasisBundleRequest&gt;
 *                         &lt;BasisBundleName&gt;
 *                               SensorData.MarkIII
 *                         &lt;/BasisBundleName&gt;
 *                                 
 *                         &lt;!-- All DAC and SAE pixels for column 0 --&gt;
 *                         &lt;DataBufferName&gt;
 *                               {DAC \[.*,0\]}  &lt;!-- &quot;{ .. }&quot; is a regular expression --&gt;
 *                         &lt;/DataBufferName&gt;
 *                         &lt;DataBufferName&gt;
 *                               {SAE \[.*,0\]}
 *                         &lt;/DataBufferName&gt;
 *                         &lt;RequestAmount&gt;1000&lt;/RequestAmount&gt;
 *                  &lt;/BasisBundleRequest&gt;
 *    				
 *                  &lt;BasisBundleRequest&gt;
 *                        &lt;BasisBundleName&gt;
 *                            StreamConfiguration.MarkIII
 *                        &lt;/BasisBundleName&gt;
 *                        &lt;!-- Specifying no DataBufferNames means send all DataBuffers --&gt;
 *    		            &lt;RequestAmount&gt;1&lt;/RequestAmount&gt;
 *    		            &lt;RequestUnit&gt;s&lt;/RequestUnit&gt;
 *    					&lt;Downsampling&gt;2&lt;/Downsampling&gt;			
 *                  &lt;/BasisBundleRequest&gt;
 *               &lt;/DataSetRequest&gt;
 *               
 *           &lt;/InputAdapter&gt;
 * </pre>
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/05/23 16:07:03 $
 * @author Steve Maher
 */
public class BasisSetTcpGenericInputAdapter extends AbstractPortAdapter
{
	private static final String CLASS_NAME = BasisSetTcpGenericInputAdapter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "BasisSet TCP Generic InputAdapter";
	private Startable fDecoder;

	/**
	 * Constructs a new BasisSetTcpJavaSerializationInputAdapter having a default name and managed by 
	 * the default ComponentManager.
	 *
	 */
	public BasisSetTcpGenericInputAdapter()
	{
		super(DEFAULT_NAME);	
	}


	/**
	 * Constructs a new BasisSetTcpJavaSerializationInputAdapter having the given name and managed by 
	 * the default ComponentManager.
	 *
	 */
	public BasisSetTcpGenericInputAdapter(String name)
	{
		super(name);	
	}

	/**
	 * Sets the descriptor and allocates the correct decoder, depending on
	 * the format requested in the DataSetRequest.
	 * @see gov.nasa.gsfc.irc.components.IrcComponent#setDescriptor(gov.nasa.gsfc.irc.description.Descriptor)
	 */
	public void setDescriptor(Descriptor descriptor)
	{
		InputAdapterDescriptor inputAdapterDescriptor = (InputAdapterDescriptor) descriptor;
		DataSetRequest dataSetRequest = null;
		try
		{
			dataSetRequest = BasisSetTcpGenericOutputAdapter.DataSetRequest.fromXml(inputAdapterDescriptor.getElement().getDocument());
		} catch (XmlException e1)
		{
			e1.printStackTrace();
			sLogger.warning("InputAdapterDescriptor - Invalid data set request: "
					+ e1.getMessage());			
		}		
			
		if (dataSetRequest != null)
		{
			String name = "BasisSetTcpGenericInputAdapter (undefined)";
			
			if (BasisSetTcpGenericOutputAdapter.DataSetRequest.FORMAT_JAVA.equals(dataSetRequest.getFormat()))
			{
				name = "BasisSetTcpGenericInputAdapter (Java)";

				fDecoder = new BasisSetTcpJavaSerializationInputAdapter(inputAdapterDescriptor);
			}
			else if (BasisSetTcpGenericOutputAdapter.DataSetRequest.FORMAT_XDR.equals(dataSetRequest.getFormat()))
			{
				name = "BasisSetTcpGenericInputAdapter (XDR)";
				fDecoder = new BasisSetTcpXdrInputAdapter(inputAdapterDescriptor);
			}
			else
			{
				sLogger.warning("InputAdapterDescriptor - Invalid data set request format: "
						+ dataSetRequest.getFormat());
			}
			
			try
			{
				setName(name);
			} catch (PropertyVetoException e)
			{
				e.printStackTrace();
			}				
		}
		else
		{
			sLogger.warning("InputAdapterDescriptor - Could not find valid " + 
					BasisSetTcpGenericOutputAdapter.DataSetRequest.E_DATA_SET_REQUEST + 
					" element in descriptor");
		}	
	}

	/**
	 *	Constructs a new BasisSetTcpJavaSerializationInputAdapter configured according to the given 
	 *  InputAdapterDescriptor.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new 
	 *  		BasisSetTcpJavaSerializationInputAdapter
	 */
	
	public BasisSetTcpGenericInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
		setDescriptor(descriptor);
	}


	/**
	 * Starts the adapter for receiving data. This 
	 * starts a reader thread.
	 */
	public synchronized void start()
	{
		if (fDecoder != null)
		{
			fDecoder.start();
		}
	}
	
	/**
	 * Stops the connection. This stops the reader thread.
	 */
	public synchronized void stop()
	{
		if (fDecoder != null)
		{
			fDecoder.stop();
		}
	}

	/**
	 * Causes this Component to immediately cease operation and release any
	 * allocated resources. A killed Component cannot subsequently be started or
	 * otherwise reused.
	 */	
	public synchronized void kill()
	{
		if (fDecoder != null)
		{
			fDecoder.kill();
		}
	}

}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetTcpGenericInputAdapter.java,v $
//  Revision 1.4  2006/05/23 16:07:03  smaher_cvs
//  Refactored BasisSetTcp*InputAdapters (adding AbstractBasisSetTcpInputAdapter); completed the generic basis set server functionality.
//
//  Revision 1.3  2006/05/22 14:04:39  smaher_cvs
//  Adds format type dynamically to name.
//
//  Revision 1.2  2006/05/18 14:05:19  smaher_cvs
//  Checkpointing working generic TCP basis set adapters.
//
//  Revision 1.1  2006/05/17 21:08:23  smaher_cvs
//  Early snapshot to fix compilation errors (dependency in other class).
//
