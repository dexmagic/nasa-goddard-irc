//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/AbstractMessageOutputAdapter.java,v 1.8 2006/04/18 04:09:52 tames Exp $
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

package gov.nasa.gsfc.irc.devices.ports.adapters;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.devices.events.OutputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.OutputMessageListener;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferEvent;

/**
 * Abstract implementation of the OutputAdapter component interface
 * in addidtion to an OutputMessageListener. This abstract 
 * OutputAdapter transforms messages based on a description. Subclasses 
 * will at a minimum need to implement the <code>process</code> method and 
 * generate OutputBufferEvent objects. Received OutputMessageEvent objects
 * are handled by calling the <code>process</code> method.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:09:52 $
 * @author	Troy Ames
 */
public abstract class AbstractMessageOutputAdapter extends AbstractOutputAdapter 
	implements OutputAdapter, OutputMessageListener
{
	private static final String CLASS_NAME = 
		AbstractMessageOutputAdapter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Message Output Adapter";


	/**
	 *  Constructs a new MessageOutputAdapter having a default name and 
	 *  managed by the default ComponentManager.
	 * 
	 **/

	public AbstractMessageOutputAdapter()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new MessageOutputAdapter having the given name and 
	 *  managed by the default ComponentManager.
	 *  
	 *  @param name The name of the new MessageOutputAdapter
	 **/

	public AbstractMessageOutputAdapter(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new MessageOutputAdapter, configured according to the given 
	 *  OutputAdapterDescriptor.
	 *
	 *  @param descriptor The OutputAdapterDescriptor of the new MessageOutputAdapter
	 */
	
	public AbstractMessageOutputAdapter(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * The format method takes a Message and the appropriate
	 * descriptor to use.  It then formats the message.
	 *
	 * @param	message   The Message to be formatted
	 * @return	ByteBuffer containing the formatted data
	 * @throws	OutputException   Formatting failed.
	 */
	
	public abstract ByteBuffer process(Message message) throws OutputException;

	/**
	 * Gets the message from the event and calls the 
	 * <code>process</code> method. The result of the process method is 
	 * inserted into a new <code>OutputBufferEvent</code> along with
	 * the Path information from the original event. The 
	 * <code>fireOutputBufferEvent</code> is called with the new event.
	 *
	 * @param event event containing message data
	 * @see #process(Message)
	 */
	public void handleOutputMessageEvent(OutputMessageEvent event)
	{
		Message message = event.getMessage();
		
		try
		{
			ByteBuffer buffer = process(message);
			
			if (buffer != null)
			{
				fireOutputBufferEvent(
						new OutputBufferEvent(
							this, buffer, event.getSendContext()));
			}
		}
		catch (OutputException e)
		{
			String logMessage = "Exception when processing event";

			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"handleOutputMessageEvent", logMessage, e);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractMessageOutputAdapter.java,v $
//  Revision 1.8  2006/04/18 04:09:52  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.7  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.6  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/02/04 21:45:46  tames_cvs
//  Changes to reflect modifications to how Message and Buffer Events are
//  created and sent.
//
//  Revision 1.4  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.3  2004/11/10 23:09:56  chostetter_cvs
//  Initial debugging of Message formatting. Mostly works except for C-style formatting.
//
//  Revision 1.2  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/05 13:50:16  tames_cvs
//  Reflects changes made to the OutputAdapter interface and abstract
//  implementations.
//
//
