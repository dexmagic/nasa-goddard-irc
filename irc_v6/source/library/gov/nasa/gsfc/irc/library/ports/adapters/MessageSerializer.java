//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/ports/adapters/MessageSerializer.java,v 1.6 2006/04/18 04:24:23 tames Exp $
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractMessageOutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputException;


/**
 * The MessageSerializer class is used convert Message Objects into a ByteBuffer. 
 * This is done using Java's object serialization feature. An 
 * OutputBufferEvent containing the resulting ByteBuffer is sent to all 
 * registered listeners.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:24:23 $
 * @author	    Bhavana Singh
 */
public class MessageSerializer extends AbstractMessageOutputAdapter 
	implements OutputAdapter
{
	private static final String CLASS_NAME = MessageSerializer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String DEFAULT_NAME = "Message Serializer";
	

	/**
	 * Constructs a new MessageSerializer having a default name.
	 */
	public MessageSerializer()
	{
		this(DEFAULT_NAME);
	}
		
	/**
	 * Constructs a new MessageSerializer having the given name.
	 * 
	 * @param name The name of the new MessageSerializer
	 */
	public MessageSerializer(String name)
	{
		super(name);
	}	
	
	/**
	 * Constructs a new MessageSerializer configured according to the given
	 * OutputAdapterDescriptor.
	 * 
	 * @param descriptor The OutputAdapterDescriptor of the new
	 *            MessageSerializer
	 */	
	public MessageSerializer(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	/**
	 * The format method takes a Message and the appropriate
	 * AbstractDataFormatterDescriptor to use.  It then formats the DataObject
	 * into the ByteBuffer.
	 *
	 * @param	message   The Message to be formatted
	 * @return	ByteBuffer containing the formatted data
	 * @throws OutputException
	 */
	public ByteBuffer process(Message message) throws OutputException
	{
	    ByteArrayOutputStream byteStream = null;
		ObjectOutputStream objectStream ;
		ByteBuffer formattedBuffer;
	
		//System.out.println("format:" + message);
		
	    try
	    {
			// ObjectOutputStream is constructed on a ByteArrayOutputStream,
	    	byteStream = new ByteArrayOutputStream();
	    	objectStream = new ObjectOutputStream(byteStream);
	
	    	objectStream.writeUnshared(message);
	
	    	formattedBuffer =  ByteBuffer.wrap(byteStream.toByteArray());
	
	    	//close outputstream
	    	objectStream.close();
	    	byteStream.close();
	    }
	    catch (IOException e)
	    {
	    	OutputException exception = new OutputException();
	    	exception.initCause(e);
			throw (exception);
	    }
	
	    return formattedBuffer;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageSerializer.java,v $
//  Revision 1.6  2006/04/18 04:24:23  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.5  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.4  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.2  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.1  2005/04/25 15:51:43  tames
//  Name change.
//
//  Revision 1.7  2005/04/22 22:25:20  tames_cvs
//  Changed serialization from writeObject to writeUnshared.
//
//  Revision 1.6  2005/01/25 23:42:59  tames
//  Removed debug print statement.
//
//  Revision 1.5  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
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
//  Revision 1.4  2004/09/27 20:44:40  tames
//  Reflects a refactoring of port architecture.
//
//  Revision 1.3  2004/08/09 17:29:51  tames_cvs
//  *** empty log message ***
//
//  Revision 1.2  2004/08/06 14:36:12  tames_cvs
//  OutputAdapter descriptor changes, added zero argument constructor
//
//  Revision 1.1  2004/07/28 18:17:52  tames_cvs
//  Initial Version
//
