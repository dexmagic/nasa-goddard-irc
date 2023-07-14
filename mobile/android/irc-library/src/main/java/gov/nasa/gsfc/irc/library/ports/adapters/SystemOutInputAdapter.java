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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractInputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputException;

/**
 * A simple input adapter to write the raw bytes received to system out.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	tjames
**/
public class SystemOutInputAdapter extends AbstractInputAdapter
{
	public static final String DEFAULT_NAME = "SystemOut Input Adapter";


	/**
	 *  Constructs a new SystemOutInputAdapter having a default name.
	 * 
	 **/

	public SystemOutInputAdapter()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new SystemOutInputAdapter having the given name.
	 *  
	 *  @param name The name of the new SystemOutInputAdapter
	 **/

	public SystemOutInputAdapter(String name)
	{
		super(name);
	}
	

	/**
	 *	Constructs a new SystemOutInputAdapter configured according to the given 
	 *  InputAdapterDescriptor.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new SystemOutInputAdapter
	 */
	
	public SystemOutInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapter#process(gov.nasa.gsfc.commons.types.buffers.BufferHandle)
	 */
	public Object process(BufferHandle handle) throws InputException
	{
		Buffer buffer = handle.getBuffer();
		
		System.out.println(buffer);
		System.out.println(Charset.forName("US-ASCII").decode(((ByteBuffer) buffer)));
		buffer.flip();
		System.out.println(new BitArray((ByteBuffer) buffer));
		
		return null;
	}

}


//--- Development History  ---------------------------------------------------
//
//  $Log: SystemOutInputAdapter.java,v $
//  Revision 1.5  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2006/01/13 03:25:34  tames
//  Added debug println
//
//  Revision 1.2  2005/08/29 17:24:58  tames_cvs
//  Added default constructor.
//
//  Revision 1.1  2005/08/29 15:46:21  tames_cvs
//  Initial implementation.
//
//