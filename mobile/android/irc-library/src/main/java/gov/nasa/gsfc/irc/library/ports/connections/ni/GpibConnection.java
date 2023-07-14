/*
 * Created on Aug 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.nasa.gsfc.irc.library.ports.connections.ni;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.library.ports.connections.ni.gpib.GPIB;
import gov.nasa.gsfc.irc.library.ports.connections.ni.gpib.GPIBException;

/**
 * @author root
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GpibConnection extends AbstractConnection
	implements Connection
{
	public static final String DEFAULT_NAME = "GPIB Connection";
	
	
	/**
	 *  Constructs a new GpibConnection having a default name.
	 * 
	 **/

	public GpibConnection()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new GpibConnection having the given base name.
	 * 
	 *  @param name The base name of the new GpibConnection
	 **/

	public GpibConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new GpibConnection configured according to the given 
	 *  ConnectionDescriptor.
	 *
	 *  @param descriptor The ConnectionDescriptor of the new GpibConnection
	 */
	
	public GpibConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection#write(java.nio.ByteBuffer)
	 */
	public void process(ByteBuffer buffer) throws IOException 
	{
		// TODO Auto-generated method stub
		CharBuffer charBuf = buffer.asCharBuffer();
		
        int ud = 0;
		try 
		{
			ud = GPIB.ibfind("dev1");
			System.out.println("completed command 1, ud = " + ud); 
			String message = charBuf.toString();
	         
		    System.out.println("Sent:\"" + message + "\"" 
		    		+ " of length:" + message.length());  
		    GPIB.ibwrt(ud, message, message.length());
		    System.out.flush();
		} 
		catch ( GPIBException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}