
//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/ports/adapters/MessageDeserializer.java,v 1.7 2006/04/18 04:24:23 tames Exp $
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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.channels.Channels;
import java.nio.channels.ScatteringByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractPipeInputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;

/**
 * Message deserialization parser component that parses received bytes into
 * Message objects. This is done using Java's object serialization feature.
 * An InputMessageEvent is sent to all registered listeners.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:24:23 $
 * @author	Troy Ames
 */
public class MessageDeserializer extends AbstractPipeInputAdapter
	implements InputMessageSource 
{
	private static final String CLASS_NAME = MessageDeserializer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Message Deserializer";

	private InputStream fInputStream = null;
	
	
	/**
	 *  Constructs a new MessageSerializer having a default name.
	 * 
	 **/

	public MessageDeserializer()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new MessageSerializer having the given name.
	 * 
	 *  @param name The name of the new MessageDeserializer
	 **/

	public MessageDeserializer(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new MessageDeserializer configured according to the given 
	 *  InputAdapterDescriptor.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new MessageDeserializer
	 */
	
	public MessageDeserializer(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * This method parses the Channel for Messages.
	 *
	 * @return a Message or null
	**/
	public Object processChannel(ScatteringByteChannel channel) throws IOException
	{
		Message message = null;
		ObjectInputStream objectInputStream = null;
		
		if (fInputStream == null)
		{
			// Set up the bridge between a ByteBuffer and the InputStream
			// required by the Java object serialization architecture
			fInputStream = Channels.newInputStream(channel);	
		}
		
        try
        {
        	objectInputStream = new ObjectInputStream(fInputStream);
            message = (Message) objectInputStream.readUnshared();
            
            if (sLogger.isLoggable(Level.FINER))
            {
    			sLogger.logp(Level.FINER, CLASS_NAME, 
    					"processChannel", 
						"MessageDeserializer received message:"
						+ message);
            }
        }
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return message;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageDeserializer.java,v $
//  Revision 1.7  2006/04/18 04:24:23  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.6  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.5  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/11/16 21:07:48  tames
//  Revised stop behavior as an attempt to avoid a deadlock on Mac OS.
//  Removed use of a nonclosing input stream class that was impacting
//  performance.
//
//  Revision 1.3  2005/05/13 18:53:02  tames_cvs
//  Added log message for logging the message just deserialized.
//
//  Revision 1.2  2005/05/13 04:08:11  tames
//  Changes to reflect super class changes. Removed debug print statements.
//
//  Revision 1.1  2005/04/25 15:51:00  tames
//  Name change as well as class hierarchy change.
//
//  Revision 1.7  2005/02/08 15:37:01  tames_cvs
//  Removed unnecessary finally clause in process method
//
//  Revision 1.6  2005/02/04 21:53:52  tames_cvs
//  Changed the process method to comply with Interface. The process
//  method still needs more work to have the correct behavior.
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
//  Revision 1.2  2004/10/06 15:30:54  tames_cvs
//  Reflects a refactoring of the abstract InputAdapter classes.
//
//  Revision 1.1  2004/09/27 21:48:14  tames
//  Reflects relocation of port adapters.
//
//  Revision 1.5  2004/09/27 20:44:57  tames
//  Reflects a refactoring of port architecture.
//
//  Revision 1.4  2004/08/09 17:30:10  tames_cvs
//  added debug statements
//
//  Revision 1.3  2004/08/06 14:36:33  tames_cvs
//  InputAdapter descriptor changes, added zero argument constructor
//
//  Revision 1.2  2004/08/03 20:37:55  tames_cvs
//  Javadoc changes
//
//  Revision 1.1  2004/07/28 22:43:04  tames_cvs
//  Initial version
//
