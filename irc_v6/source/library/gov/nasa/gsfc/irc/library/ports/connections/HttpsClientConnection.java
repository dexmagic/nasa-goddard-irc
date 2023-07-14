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

package gov.nasa.gsfc.irc.library.ports.connections;

import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;


/**
 * A HttpsClientConnection is a connection component that connects to and 
 * reads/writes data from a secure HTTP server (HTTPS). This class is based
 * upon the HTTP client connection (@see
 * {@link gov.nasa.gsfc.irc.library.ports.connections.HttpClientConnection
 * HttpClientConnection}).
 *
 * TODO: This class will likely need additional parameters for authentication
 * and handshaking. The implementation for this class has not been tested against
 * an HTTPS server.
 * 
 * <P>The configuration of this connection is specified by a ConnectionDescriptor.
 * The table below gives the configuration parameters that this connection uses.
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
 *      <td>hostname</td><td>localhost</td>
 *      <td align="left">The remote host name to connect to</td>
 *  </tr>
 *  <tr align="center">
 *      <td>port</td><td>443</td>
 *      <td align="left">The remote port to connect to</td>
 *  </tr>
 *  <tr align="center">
 *      <td>hostFile</td><td>N/A</td>
 *      <td align="left">The file on the remote host</td>
 *  </tr>
 *  <tr align="center">
 *      <td>requestType</td><td>GET</td>
 *      <td align="left">The HTTP request type, one of
 *        <UL>
 *          <LI>GET
 *          <LI>POST
 *          <LI>HEAD
 *          <LI>OPTIONS
 *          <LI>PUT
 *          <LI>DELETE
 *          <LI>TRACE
 *        </UL>
 *      </td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="HTTPS Client"&gt;
 *         &lt;Parameter name="hostname" value="localhost" /&gt;
 *         &lt;Parameter name="port" value="9999" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/06/07 15:56:52 $
 * @author	Jeffrey C. Hosler
 */
public class HttpsClientConnection extends HttpClientConnection
{
	//============================================================================
	// CONSTANTS
	//============================================================================

    protected static final String DEFAULT_PROTOCOL = "https";
    protected static final int DEFAULT_PORT = 443;

	//============================================================================
	// VARS
	//============================================================================

	//---Configuration
	//---Default name
	private final static String DEFAULT_NAME = "HTTPS Client Connection";

	//----------------------------------------------------------------------------
	
	//============================================================================
	// CONSTRUCTION
	//============================================================================

	/**
	 *	Constructs a new HttpClientConnection having a default name.
	 *
	 */
	public HttpsClientConnection()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new HttpClientConnection having the given base name.
	 * 
	 *  @param name The base name of the new HttpClientConnection
	 **/

	public HttpsClientConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 * Constructs a new HttpClientConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new HttpClientConnection
	 */
	public HttpsClientConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
	}
    
    //============================================================================
    // Defaults
    //============================================================================

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.library.ports.connections.HttpClientConnection#getDefaultProtocol()
     */
    protected String getDefaultProtocol()
    {
        return DEFAULT_PROTOCOL;
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.library.ports.connections.HttpClientConnection#getDefaultPort()
     */
    protected int getDefaultPort()
    {
        return DEFAULT_PORT;
    }
    
}

//--- Development History  ---------------------------------------------------
//
// $Log: HttpsClientConnection.java,v $
// Revision 1.1  2006/06/07 15:56:52  jhosler_cvs
// Initial untested version.
//
//
