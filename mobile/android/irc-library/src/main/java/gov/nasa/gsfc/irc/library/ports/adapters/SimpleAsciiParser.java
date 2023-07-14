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
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractMessageInputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputException;

/**
 * A simple input adapter to put the bytes received into a Message.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/03 18:42:13 $
 * @author	tjames
**/
public class SimpleAsciiParser extends AbstractMessageInputAdapter
{
	public static final String DEFAULT_NAME = "ASCII Input Adapter";

	/**
	 * Constructs a new SimpleAsciiParser having a default name.
	 */
	public SimpleAsciiParser()
	{
		super(DEFAULT_NAME);
	}

	/**
	 * Constructs a new SimpleAsciiParser having the given name.
	 * 
	 * @param name The name of the new SimpleAsciiParser
	 */
	public SimpleAsciiParser(String name)
	{
		super(name);
	}

	/**
	 * Constructs a new SimpleAsciiParser configured according to the given
	 * InputAdapterDescriptor.
	 * 
	 * @param descriptor The InputAdapterDescriptor of the new SimpleAsciiParser
	 */
	public SimpleAsciiParser(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapter#process(gov.nasa.gsfc.commons.types.buffers.BufferHandle)
	 */
	public Object process(BufferHandle handle) throws InputException
	{
		Buffer buffer = handle.getBuffer();
		Message message = Irc.getMessageFactory().createMessage();

		CharBuffer charBuffer = Charset.forName("US-ASCII").decode(
			((ByteBuffer) buffer));

		message.put("Message", charBuffer.toString());

		return message;
	}
}


// --- Development History ---------------------------------------------------
//
//  $Log: SimpleAsciiParser.java,v $
//  Revision 1.1  2006/05/03 18:42:13  tames
//  Initial version.
//
//