//=== File Prolog ============================================================
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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractMessageOutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputException;
import gov.nasa.gsfc.irc.messages.MessageKeys;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;


/**
 *  The SimpleAsciiFormatter class is used convert Messages into a 
 *  string representation. Primarily used for debugging.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/05/03 18:41:56 $
 *  @author	    Troy Ames
 */
public class SimpleAsciiFormatter extends AbstractMessageOutputAdapter 
	implements OutputAdapter
{
	private static final String CLASS_NAME = SimpleAsciiFormatter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Simple ASCII Formatter";


	/**
	 * Constructs a new SimpleAsciiFormatter having a default name.
	 */
	public SimpleAsciiFormatter()
	{
		this(DEFAULT_NAME);
	}	
	
	/**
	 * Constructs a new SimpleAsciiFormatter having the given name.
	 * 
	 * @param name The name of the new SimpleAsciiFormatter
	 */
	public SimpleAsciiFormatter(String name)
	{
		super(name);
	}	
	
	/**
	 * Constructs a new SimpleAsciiFormatter configured according to the given
	 * OutputAdapterDescriptor.
	 * 
	 * @param descriptor The OutputAdapterDescriptor of the new
	 *            SimpleAsciiFormatter
	 */	
	public SimpleAsciiFormatter(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}	
	
	/**
	 * The format method takes a Message and creates a string representation
	 * into a ByteBuffer.
	 *
	 * @param	message   The Message to be formatted
	 * @return	ByteBuffer containing the formatted data
	 * @throws OutputException
	 */
	public ByteBuffer process(Message message) throws OutputException
	{	
		String msgString = message.toString();
		byte[] messageArray;
		
		try
		{
			messageArray = msgString.getBytes("US-ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			messageArray = new byte[0];
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    return ByteBuffer.wrap(messageArray);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: SimpleAsciiFormatter.java,v $
//  Revision 1.12  2006/05/03 18:41:56  tames
//  Removed some debugging println statements and changed format to
//  output ASCII instead of Unicode.
//
//  Revision 1.11  2006/04/18 04:24:23  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.10  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.9  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.8  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.7  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.6  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.5  2005/01/07 21:37:18  tames
//  Updated to reflect change of LocalizedName property name change to
//  displayName.
//
//  Revision 1.4  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/05 13:50:16  tames_cvs
//  Reflects changes made to the OutputAdapter interface and abstract
//  implementations.
//
//  Revision 1.1  2004/09/27 21:48:14  tames
//  Reflects relocation of port adapters.
//
//  Revision 1.3  2004/09/27 20:44:40  tames
//  Reflects a refactoring of port architecture.
//
//  Revision 1.2  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.1  2004/09/02 15:31:11  smaher_cvs
//  Created during GPIB testing during IRC/GBT workshop (so maybe not quite polished yet)
//
