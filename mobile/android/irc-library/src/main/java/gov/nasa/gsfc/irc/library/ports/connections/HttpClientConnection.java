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

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.SingleUseBufferHandle;
import gov.nasa.gsfc.commons.types.queues.KeepOptionalBoundedQueue;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractDualThreadedConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A HttpClientConnection is a connection component that connects to and 
 * performs HTTP requests to an HTTP server.
 * 
 * <P>HTTP connections are established by the client at the time of the request,
 * and the server handles disconnects after a session timeout. Therefore, this
 * class behaves differently than the standard TCP client connection
 * (see {@link gov.nasa.gsfc.irc.library.ports.connections.TcpClientConnection 
 * TcpClientConnection}) which establishes a connection, and maintains connectivity.
 * 
 * <P>Additionally, server responses typically occur as a result of a client
 * request.  There is no "pushing" of data from the server.  Therefore, our
 * writing thread will block on each request, and wait for a response. Once the
 * response is received, it is placed onto a response queue for processing by
 * the reading thread.
 *
 * <P>Writing to a connection is done by the calling thread of the
 * <code>process</code> method. Reading data is done by a Thread owned by this
 * instance (see {@link gov.nasa.gsfc.irc.devices.ports.connections.AbstractDualThreadedConnection 
 * AbstractDualThreadedConnection}).
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
 *      <td>port</td><td>80</td>
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
 *     &lt;Connection name="Test Connection" type="HTTP Client"&gt;
 *         &lt;Parameter name="hostname" value="localhost" /&gt;
 *         &lt;Parameter name="port" value="9999" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/06/07 15:54:14 $
 * @author	Jeffrey C. Hosler
 */
public class HttpClientConnection extends AbstractDualThreadedConnection 
	implements Connection
{
	//============================================================================
	// CONSTANTS
	//============================================================================

	//---Logging support
	private final static String CLASS_NAME = HttpClientConnection.class.getName();
	private final static Logger sLogger    = Logger.getLogger(CLASS_NAME);

	//---Descriptor keys
    public final static String HOST_KEY                     = "hostname";
    public final static String PORT_KEY                     = "port";
    public final static String HOST_FILE_KEY                = "hostFile";
    public final static String REQUEST_TYPE_KEY             = "requestType";
    public static final String RESPONSE_KEEP_KEY            = "responseQueueKeepMode";
    public static final String RESPONSE_KEEP_ALL_STR        = "responseQueueKeepAll";
    public static final String RESPONSE_KEEP_LATEST_STR     = "responseQueueKeepLatest";
    public static final String RESPONSE_KEEP_EARLIEST_STR   = "responseQueueKeepEarliest";
    public static final String RESPONSE_KEEP_CAPACITY_KEY   = "responseQueueCapacity";

    // HTTP request type (must start at 0, and increment sequentially)
    // TODO: Migrate to enumeration with Java 5
    public static final transient int REQUEST_TYPE_GET = 0;
    public static final transient int REQUEST_TYPE_POST = 1;
    public static final transient int REQUEST_TYPE_HEAD = 2;
    public static final transient int REQUEST_TYPE_OPTIONS = 3;
    public static final transient int REQUEST_TYPE_PUT = 4;
    public static final transient int REQUEST_TYPE_DELETE = 5;
    public static final transient int REQUEST_TYPE_TRACE = 6;

    // Array of strings corresponding to the request type (must match the
    // ordering of the REQUEST_TYPE_* constants above
    public static final transient String[] REQUEST_TYPE_STRINGS =
                                    {
                                        "GET",
                                        "POST",
                                        "HEAD",
                                        "OPTIONS",
                                        "PUT",
                                        "DELETE",
                                        "TRACE"
                                    };
    
    protected static final String DEFAULT_PROTOCOL = "http";
    protected static final int    DEFAULT_PORT = 80;
    protected static final String DEFAULT_HOSTNAME = "localhost";
    protected static final String DEFAULT_HOSTFILE = "";
    protected static final String DEFAULT_REQUEST_TYPE = "GET";

	//============================================================================
	// VARS
	//============================================================================

	//---Configuration
	private int fPort;
    private String fProtocol;
	private String fHostName;
    private String fHostFile;
    private String fRequestType;
	
	//---Default name
	private final static String DEFAULT_NAME = "HTTP Client Connection";

    //---Response queue
    private KeepOptionalBoundedQueue fResponseQueue = 
        new KeepOptionalBoundedQueue(10);

	//----------------------------------------------------------------------------
	
	//============================================================================
	// CONSTRUCTION
	//============================================================================

	/**
	 *	Constructs a new HttpClientConnection having a default name.
	 *
	 */
	public HttpClientConnection()
	{
		super(DEFAULT_NAME);
        
        initialize();
	}
	
	
	/**
	 *  Constructs a new HttpClientConnection having the given base name.
	 * 
	 *  @param name The base name of the new HttpClientConnection
	 **/

	public HttpClientConnection(String name)
	{
		super(name);
        
        initialize();
	}
	
	
	/**
	 * Constructs a new HttpClientConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new HttpClientConnection
	 */
	public HttpClientConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
        
        initialize();
		
		configureFromDescriptor(descriptor);
	}
	
	/**
     * Initializes the default settings for this connection. 
	 */
    protected void initialize()
    {
        // Initialize the defaults
        fProtocol    = getDefaultProtocol();
        fPort        = getDefaultPort();
        fHostName    = getDefaultHostName();
        fHostFile    = getDefaultHostFile();
        fRequestType = getDefaultRequestType();

        // Initialize the queue settings
        fResponseQueue.setKeepAll();
    }
    
	/**
	 *  Sets the Descriptor of this Component to the given Descriptor. The 
	 *  Component will in turn be (re)configured in accordance with the given 
	 *  Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/
	public void setDescriptor(Descriptor descriptor)
	{
	    super.setDescriptor(descriptor);
	    
		if (descriptor instanceof ConnectionDescriptor)
		{
			configureFromDescriptor((ConnectionDescriptor) descriptor);
		}
	}	

	/**
	 * Causes this HttpClientConnection to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a ConnectionDescriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		String METHOD = "configureFromDescriptor()";
		
		if (descriptor == null)
		{
			return;
		}
		
		//---Extract port number from descriptor
		String strPortNumber = descriptor.getParameter(PORT_KEY);
		if (strPortNumber != null)
		{
			try
			{
				int port = Integer.parseInt(strPortNumber);
				
				if (port < 0)
				{
					String message = 
						"Attempt to build HttpClientConnection with invalid port number "
						+ strPortNumber;
	
					sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message);
				}
				else 
				{
					//---Set the port number
					fPort = port;			
				}
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build HttpClientConnection with invalid port number "
					+ strPortNumber;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
			}
		}
		
		//---Extract hostname from descriptor
		String strHostName = descriptor.getParameter(HOST_KEY);
		if (strHostName != null)
		{		
			fHostName = strHostName;
		}
		else
		{
			String message = HOST_KEY
				+ " property not specified, using " + fHostName;

			sLogger.logp(Level.FINE, CLASS_NAME, METHOD, message);
		}
		
        //---Extract the host file from descriptor
        String strHostFile = descriptor.getParameter(HOST_FILE_KEY);
        if (strHostFile != null)
        {       
            fHostFile = strHostFile;
        }
        else
        {
            String message = HOST_FILE_KEY
                + " property not specified, using " + fHostFile;

            sLogger.logp(Level.FINE, CLASS_NAME, METHOD, message);
        }
        
        //---Extract the request type from descriptor
        String strRequestType = descriptor.getParameter(REQUEST_TYPE_KEY);
        if (strRequestType != null)
        {       
            fRequestType = strRequestType;
            
            // Validate the request type
            
        }
        else
        {
            String message = REQUEST_TYPE_KEY
                + " property not specified, using " + fRequestType;

            sLogger.logp(Level.FINE, CLASS_NAME, METHOD, message);
        }
        
        //---Extract the queue configuration from the desciptor
        String keepMode = descriptor.getParameter(RESPONSE_KEEP_KEY);
        if (keepMode != null)
        {
            if (keepMode.equals(RESPONSE_KEEP_ALL_STR))
            {
                fResponseQueue.setKeepAll();
            }
            else if (keepMode.equals(RESPONSE_KEEP_LATEST_STR))
            {
                fResponseQueue.setKeepLatest();
            }
            else if (keepMode.equals(RESPONSE_KEEP_EARLIEST_STR))
            {
                fResponseQueue.setKeepEarliest();
            }
        }

        String keepCapacity = descriptor.getParameter(RESPONSE_KEEP_CAPACITY_KEY);
        if (keepCapacity != null)
        {
            try
            {
                int capacity = Integer.parseInt(keepCapacity);
                
                if (capacity < 1)
                {
                    String message = 
                        "Response Queue capacity must be greater than 0 instead of "
                        + capacity;
    
                    sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message);
                }
                else 
                {
                    //---Set the capacity 
                    fResponseQueue.setCapacity(capacity);         
                }
            }
            catch (NumberFormatException e)
            {
                String message = 
                    "Attempt to set response queue capacity with invalid number "
                    + keepCapacity;
    
                sLogger.logp(Level.WARNING, CLASS_NAME, METHOD, message, e);
            }
        }
	}

	//============================================================================
	// CONNECTION SUPPORT
	//============================================================================

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection#openConnection()
     */
    protected void openConnection()
    {
        // HTTP connections are performed when the message is sent.
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection#closeConnection()
     */
    protected void closeConnection()
    {
        // HTTP connections are closed by the server.
    }
    
    /**
     * Listens for resposes from the server. In this case, we will simply
     * monitor the response queue.
     */
    protected void serviceConnection()
    {
        String response;
        try
        {
            // This call will block until something is in the queue
            response = (String) fResponseQueue.blockingRemove();

            // Fire the response
            if (response != null)
            {
                BufferHandle handle = new SingleUseBufferHandle(ByteBuffer.wrap(response.getBytes()));

                fireInputBufferEvent(new InputBufferEvent(this, handle));
            }
        }
        catch (InterruptedException e)
        {
            sLogger.logp(Level.WARNING, CLASS_NAME, "serviceConnection", 
                    getFullyQualifiedName() + " exception servicing connection ",
                    e);

            stop();
        }
    }

	//============================================================================
	// READ/WRITE
	//============================================================================

    /**
     * Gets the response from the server if an error occurred, but useful
     * information was returned from the server. The underlying logic currently
     * has no implementation, so this method always returns null.
     * 
     * @return  String  The response from the server, or null if there was no
     *                  error response
     */
    public String getError(HttpURLConnection urlConnection) throws IOException
    {
        // Process the error if any
        String result = null;
        InputStream errorStream = urlConnection.getErrorStream();
        if (errorStream != null)
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
            String response = bufferedReader.readLine();
            while (response != null)
            {
                result = result + response;
                response = bufferedReader.readLine();
            }
        }
        errorStream.close();
        
        return result;
    }
    
    /**
     * Gets the response from the server.
     * 
     * @return  String  The response from the server, or null if there was no
     *                  response
     */
    public String getResponse(URLConnection urlConnection) throws IOException
    {
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String result = null;
        String response = bufferedReader.readLine();
        while (response != null)
        {
            if (result == null)
            {
                result = "";
            }
            result = result + response;
            response = bufferedReader.readLine();
        }
        inputStream.close();
        
        return result;
    }

    /**
     * Performs a post request to the server
     */
    protected String httpPost(String parameters) throws
            IOException, MalformedURLException 
    { 
        DataOutputStream URLWriter;
        URL url;
        URLConnection urlConnection;
        String baseUrl = getBaseUrl();

        // Perform a post request to get a new connection map id
        url = new URL(baseUrl);
        urlConnection = url.openConnection();

        // Set to have input and output and no cache
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);

        // Work around a Netscape bug
        urlConnection.setRequestProperty(
            "Content-Type",
            "application/x-www-form-urlencoded");

        // Perform a Post request to the server
        URLWriter = new DataOutputStream(urlConnection.getOutputStream());
        URLWriter.writeBytes(parameters);
        URLWriter.flush();
        URLWriter.close();

        // Return the response
        return getResponse(urlConnection);
    } 

    /** 
     * Performs an HTTP get request to the server.
     * 
     * @param   parameters  The key/value parameters to send in the request
     * 
     * @throws  IOException If there was a problem writing to the server
     * @throws  MalformedURLException If there was a problem creating the URL
     *          object
     */ 
    protected String httpGet(String parameters) throws
            IOException, MalformedURLException 
    { 
        URL url;
        URLConnection urlConnection;
        String baseUrl = getBaseUrl();
        
        // Construct the required URL
        if ((parameters.equals("")) || (parameters == null))
        {
            url = new URL(baseUrl);
        }
        else
        {
            url = new URL(baseUrl + "?" + parameters);
        }

        // Perform a post request to get a new connection map id
        urlConnection = url.openConnection();

        // Set no cache
        urlConnection.setUseCaches(false);

        // Return the response
        return getResponse(urlConnection);
    } 

    /** 
     * Performs an HTTP trace request to the server.
     * 
     * @param   parameters  The key/value parameters to send in the request
     */ 
    protected String httpTrace(String parameters) throws
            IOException, MalformedURLException 
    {
        // TODO: Not currently implemented
        String message = "Request type \"" + REQUEST_TYPE_STRINGS[REQUEST_TYPE_TRACE] + "\"" +
        " is not currently supported";
        sLogger.logp(Level.WARNING, CLASS_NAME, "process()", message);
        
        return null;
    } 

    /** 
     * Performs an HTTP put request to the server.
     * 
     * @param   parameters  The key/value parameters to send in the request
     */ 
    protected String httpPut(String parameters) throws
            IOException, MalformedURLException 
    { 
        // TODO: Not currently implemented
        String message = "Request type \"" + REQUEST_TYPE_STRINGS[REQUEST_TYPE_PUT] + "\"" +
        " is not currently supported";
        sLogger.logp(Level.WARNING, CLASS_NAME, "process()", message);
        
        return null;
    } 

    /** 
     * Performs an HTTP delete request to the server.
     * 
     * @param   parameters  The key/value parameters to send in the request
     */ 
    protected String httpDelete(String parameters) throws
            IOException, MalformedURLException 
    { 
        // TODO: Not currently implemented
        String message = "Request type \"" + REQUEST_TYPE_STRINGS[REQUEST_TYPE_DELETE] + "\"" +
        " is not currently supported";
        sLogger.logp(Level.WARNING, CLASS_NAME, "process()", message);
        
        return null;
    } 

    /** 
     * Performs an HTTP head request to the server.
     * 
     * @param   parameters  The key/value parameters to send in the request
     */ 
    protected String httpHead(String parameters) throws
            IOException, MalformedURLException 
    { 
        // TODO: Not currently implemented
        String message = "Request type \"" + REQUEST_TYPE_STRINGS[REQUEST_TYPE_HEAD] + "\"" +
        " is not currently supported";
        sLogger.logp(Level.WARNING, CLASS_NAME, "process()", message);
        
        return null;
    }

    /** 
     * Performs an HTTP options request to the server.
     * 
     * @param   parameters  The key/value parameters to send in the request
     */ 
    protected String httpOptions(String parameters) throws
            IOException, MalformedURLException 
    { 
        // TODO: Not currently implemented
        String message = "Request type \"" + REQUEST_TYPE_STRINGS[REQUEST_TYPE_OPTIONS] + "\"" +
        " is not currently supported";
        sLogger.logp(Level.WARNING, CLASS_NAME, "process()", message);
        
        return null;
    } 


	/**
	 * Writes the contents of the Buffer to the connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public synchronized void process(ByteBuffer buffer)
	{
		if (isStarted())
		{
            String response = null;
            String requestTypeStr = getRequestType();
            int requestType = stringToRequestType(requestTypeStr);
            
            try
            {
                String parameters = new String(buffer.array());
                switch (requestType)
                {
                    case REQUEST_TYPE_GET:
                        response = httpGet(parameters);
                        break;
                    case REQUEST_TYPE_POST:
                        response = httpPost(parameters);
                        break;
                    case REQUEST_TYPE_OPTIONS:
                        response = httpOptions(parameters);
                        break;
                    case REQUEST_TYPE_TRACE:
                        response = httpTrace(parameters);
                        break;
                    case REQUEST_TYPE_PUT:
                        response = httpPut(parameters);
                        break;
                    case REQUEST_TYPE_HEAD:
                        response = httpHead(parameters);
                        break;
                    case REQUEST_TYPE_DELETE:
                        response = httpDelete(parameters);
                        break;
                    default:
                        String message = "Request type \"" + requestTypeStr + "\"" +
                        " is not understood";
                        sLogger.logp(Level.WARNING, CLASS_NAME, "process()", message);
                }
                
                if (response != null)
                {
                    // Place response onto the response queue
                    try
                    {
                        fResponseQueue.blockingAdd(response);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
		}
	}
	
	//============================================================================
	// Defaults
	//============================================================================

    /**
     * Get the default port used for this HTTP server connection. This
     * method should be overridden by subclasses if a different port is
     * required.
     * 
     * @return Returns the default server port
     */
    protected int getDefaultPort()
    {
        return DEFAULT_PORT;
    }
    
    /**
     * Get the default protocol used for this HTTP server connection. This
     * method should be overridden by subclasses if a different default
     * protocol is required.
     * 
     * @return Returns the default server protocol
     */
    protected String getDefaultProtocol()
    {
        return DEFAULT_PROTOCOL;
    }
    
    /**
     * Get the default hostname used for this HTTP server connection. This
     * method should be overridden by subclasses if a different default
     * hostname is required. Example: protocol://hostname:port/hostfile
     * 
     * @return Returns the default server hostname
     */
    protected String getDefaultHostName()
    {
        return DEFAULT_HOSTNAME;
    }
    
    /**
     * Get the default hostfile used for this HTTP server connection. This
     * method should be overridden by subclasses if a different default
     * hostfile is required. Example: protocol://hostname:port/hostfile
     * 
     * @return Returns the default server hostfile
     */
    protected String getDefaultHostFile()
    {
        return DEFAULT_HOSTFILE;
    }
    
    /**
     * Get the default request type used for this HTTP server connection. This
     * method should be overridden by subclasses if a different default
     * request type is required.
     * 
     * @return Returns the default request type
     * 
     * @see     HttpURLConnection#getRequestMethod()
     */
    protected String getDefaultRequestType()
    {
        return DEFAULT_REQUEST_TYPE;
    }
    
    //============================================================================
    // SET/GET
    //============================================================================

	/**
	 * Get the remote server port number.
	 * @return Returns the serverPort.
	 */
	public int getPort()
	{
		return fPort;
	}
	
    /**
     * Get the remote server protocol.
     * @return Returns the server protocol.
     */
    public String getProtocol()
    {
        return fProtocol;
    }
    
	/**
	 * Set the port number listening for new client connections.
	 * @param serverPort The serverPort to set.
	 */
	public void setPort(int serverPort)
	{
		if (serverPort > 0)
		{
			int oldValue = fPort;
			fPort = serverPort;
			firePropertyChange(PORT_KEY, oldValue, fPort);
		}
	}
	
	/**
	 * Get the remote server hostname.
	 * @return Returns the remote server hostname
	 */
	public String getHostName()
	{
		return fHostName;
	}
	
    /**
     * Get the remote server host file.
     * @return Returns the remote server host file
     */
    public String getHostFile()
    {
        return fHostFile;
    }
    
    /**
     * Get the request type.
     * @return Returns the HTTP request type
     */
    public String getRequestType()
    {
        return fRequestType;
    }
    
    /**
     * Set the HTTP request type.
     * @param requestType The HTTP request type, one of
     *        <UL>
     *          <LI>GET
     *          <LI>POST
     *          <LI>HEAD
     *          <LI>OPTIONS
     *          <LI>PUT
     *          <LI>DELETE
     *          <LI>TRACE
     *        </UL>
     */
    public void setRequestType(String requestType)
    {
        boolean valid = false;
        if (requestType != null)
        {
            for (int i=0; i<REQUEST_TYPE_STRINGS.length; i++)
            {
                if (requestType.toUpperCase().equals(REQUEST_TYPE_STRINGS[i]))
                {
                    valid = true;
                    break;
                }
            }
        }

        // Make sure we received valid input
        if (!valid)
        {
            throw new IllegalArgumentException(
                    "The specified request type is not valid.");
        }
        String oldValue = fRequestType;
        fRequestType = requestType;
        firePropertyChange(REQUEST_TYPE_KEY, oldValue, fRequestType);
    }
    
    /**
     * Sets the maximum number of response messages that this connection will
     * queue up for reading. If the new capacity is less then the number of
     * events currently in the queue they will be dicarded.
     * 
     * @param capacity the new capacity of the event Queue
     */
    public void setCapacity(int capacity) throws IllegalArgumentException
    {
        try
        {
            fResponseQueue.setCapacity(capacity);
        }
        catch (IllegalArgumentException e)
        {
            fResponseQueue.clear();
            fResponseQueue.setCapacity(capacity);
        }
    }

    /**
     * Returns the maximum number of response messages that this 
     * connection will queue up for reading.
     *
     * @return maximum capacity
     **/
    public int getCapacity()
    {
        return (fResponseQueue.getCapacity());
    }
    
    /**
     * Returns true if the current keep mode is to keep all the contents of the 
     * Queue.
     * 
     * @return Returns true if the keep mode is keep all.
     */
    public boolean isKeepAll()
    {
        return (fResponseQueue.isKeepAll());
    }

    /**
     * Sets the keep mode for the Queue to keep all.
     */
    public void setKeepAll()
    {
        fResponseQueue.setKeepAll();
    }

    /**
     * Returns true if the current keep mode is to keep the latest objects
     * added to the Queue if full and discarding the earliest if needed.
     * 
     * @return Returns true if the keep mode is keep latest.
     */
    public boolean isKeepLatest()
    {
        return (fResponseQueue.isKeepLatest());
    }

    /**
     * Sets the keep mode for the Queue to keep latest.
     */
    public void setKeepLatest()
    {
        fResponseQueue.setKeepLatest();
    }

    /**
     * Returns true if the current keep mode is to keep the earliest objects
     * added to the Queue if full and discarding the latest if needed.
     * 
     * @return Returns true if the keep mode is keep earliest.
     */
    public boolean isKeepEarliest()
    {
        return (fResponseQueue.isKeepEarliest());
    }

    /**
     * Sets the keep mode for the Queue to keep earliest.
     */
    public void setKeepEarliest()
    {
        fResponseQueue.setKeepEarliest();
    }

	//============================================================================
	// MISC SUPPORT
	//============================================================================

    /**
     * Returns the request type from a supplied request type string.
     * 
     * @param   String  The request type string to map to a request type
     * 
     * @return  int The request type. This value will be one of the
     *              constants defined in this class definition
    **/
    public static int stringToRequestType(String modeString)
    {
        int type = REQUEST_TYPE_GET; // Default
        for (int i=0; i<REQUEST_TYPE_STRINGS.length; i++)
        {
            if (modeString.toUpperCase().equals(REQUEST_TYPE_STRINGS[i]))
            {
                type = i;
                break;
            }
        }
        return type;
    }
    
    /**
     * Returns the base URL string that should be used to reference the remote
     * server file.<br>
     * 
     * @return  String  The base URL string
     */
    protected String getBaseUrl()
    {
        return getProtocol() + "://" + getHostName() + ":" + getPort() + getHostFile();
    }
    
	/**
	 *  Returns a String representation of this Connection.
	 * 
	 *  @return A String representation of this Connection
	 */	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString());	
		
		stringRep.append("\nParameters ");
		stringRep.append("[RemoteHost=" + getHostName());
		stringRep.append(" RemoteFile=" + getHostFile());
        stringRep.append(" RemotePort=" + getPort());
        stringRep.append(" RequestType=" + getRequestType());
		stringRep.append("]");
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: HttpClientConnection.java,v $
// Revision 1.1  2006/06/07 15:54:14  jhosler_cvs
// Initial version with some unimplemented HTTP support. Current version only supports GET and POST.
//
//
