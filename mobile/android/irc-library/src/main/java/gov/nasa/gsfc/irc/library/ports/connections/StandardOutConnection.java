//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: StandardOutConnection.java,v $
//	Revision 1.9  2006/05/04 18:51:36  rfl
//	Removed print out of bitArray
//	
//	Revision 1.8  2006/03/14 14:57:16  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.7  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.6  2006/01/13 03:26:37  tames
//	Added debug println
//	
//	Revision 1.5  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.4  2004/10/14 15:22:16  chostetter_cvs
//	Port descriptor-oriented refactoring
//	
//	Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.2  2004/09/27 20:39:30  tames
//	Reflects a refactoring of Connection input and output events.
//	
//	Revision 1.1  2004/08/03 20:35:52  tames_cvs
//	Initial Version
//	
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;

/**
 * A StandardOutConnection writes data to Standard Out.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/04 18:51:36 $
 * @author	Troy Ames
 */
public class StandardOutConnection extends AbstractConnection
{
	private WritableByteChannel fOutChannel = Channels.newChannel(System.out);
	
	public static final String DEFAULT_NAME = "Standard Out Connection";
	
	
	/**
	 *  Constructs a new StandardOutConnection having a default name.
	 * 
	 **/

	public StandardOutConnection()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new StandardOutConnection having the given base name.
	 * 
	 *  @param name The base name of the new StandardOutConnection
	 **/

	public StandardOutConnection(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new StandardOutConnection configured according to the given 
	 *  ConnectionDescriptor.
	 *
	 *  @param descriptor The ConnectionDescriptor of the new StandardOutConnection
	 */
	
	public StandardOutConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Writes the contents of the Buffer to Standard Out.
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public void process(ByteBuffer data) throws IOException
	{
		System.out.println("Standard Out Connection writing buffer:" + data);
		int byteWritten = fOutChannel.write(data);
		data.flip();
//		System.out.println(new BitArray((ByteBuffer) data).toString(8));
	}
}
