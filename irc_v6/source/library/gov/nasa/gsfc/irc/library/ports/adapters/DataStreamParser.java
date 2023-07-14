
//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/ports/adapters/DataStreamParser.java,v 1.10 2006/04/27 19:46:21 chostetter_cvs Exp $
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
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.messages.MessageSender;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.transformation.DataTransformer;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractMessageInputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputException;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

/**
 * A DataStreamParser is used to parser an input data stream.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/27 19:46:21 $
 * @author	Troy Ames
 */

public class DataStreamParser extends AbstractMessageInputAdapter
{
	private static final String CLASS_NAME = DataStreamParser.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Data Stream Parser";
	
	private MessageSenderDelegate fSenderDelegate;
	private Map fContextMap = new HashMap();
	
	
	/**
	 * Constructs a new DataStreamParser having a default name.
	 *
	 */
	
	public DataStreamParser()
	{
		super(DEFAULT_NAME);
		
		fSenderDelegate = new MessageSenderDelegate(this);
		fContextMap.put(MessageSenderDelegate.MESSAGE_SENDER_KEY, fSenderDelegate);
	}
	

	/**
	 * Constructs a new DataStreamParser configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new DataStreamParser
	 *  @param manager The ComponentManager of the new DataStreamParser
	 */
	public DataStreamParser(InputAdapterDescriptor descriptor, 
		ComponentManager manager)
	{
		super(descriptor);
		
		fSenderDelegate = new MessageSenderDelegate(this);
		fContextMap.put(MessageSenderDelegate.MESSAGE_SENDER_KEY, fSenderDelegate);
	}
	

	/**
	 *  Sets the manager of this DataStreamParser to the given ComponentManager.
	 *  
	 *  @param manager The new ComponentManager of this DataStreamParser
	**/

	public void setManager(ComponentManager manager)
	{
		super.setManager(manager);
		
		if (manager instanceof BasisBundleSource)
		{
			fContextMap.put(BasisBundleSource.BASIS_BUNDLE_SOURCE_KEY, manager);
		}
	}
	
	
	/**
	 * Causes this DataStreamParser to process the given buffer. 
	 * This method should return null if a return value is not relevant to
	 * the implementing class.
	 *
	 * @param handle BufferHandle to a buffer to be parsed
	 * @return a result or null.
	 * 
	 * @throws InputException
	 * @throws IOException
	 * @see BufferHandle
	 */
	
	public Object process(BufferHandle handle) throws InputException
	{
		Object result = null;
		
		if (handle != null)
		{
			Buffer input = handle.getBuffer();
			
			DataTransformer transformer = getDataTransformer();
			
			if (transformer != null)
			{
				result = transformer.transform(input, fContextMap);
			}
		}
	    
	    return (result);
	}

	/**
	 * Handles a new InputBufferEvent by calling the <code>process</code>
	 * method with the BufferHandle contained in the InputBufferEvent.
	 * 
	 * @param event the InputBufferEvent
	 * @see #process(BufferHandle)
	 */
	public void handleInputBufferEvent(InputBufferEvent event)
	{
		BufferHandle handle = event.getHandle();
		
		// Update the context.
		fSenderDelegate.setContext(event.getReplyContext());

		try
		{
			// Lock the buffer
			handle.setInUse();
			process(handle);
		}
		catch (InputException e)
		{
			String message = "Exception when processing data";

			sLogger.logp(Level.WARNING, CLASS_NAME, 
					"handleInputBufferEvent", message, e);
		}
		finally
		{
			// Release the enclosing handle
			handle.release();
		}
	}

	// --- Utility classes ----------------------------------------------------
	
	private static class MessageSenderDelegate implements MessageSender
	{
		private DataStreamParser fParent;
		private Path fContext = null;

		/**
		 * Constructs a MessageSenderDelegate that can call 
		 * 
		 * @param parent
		 */
		public MessageSenderDelegate(DataStreamParser parent)
		{
			fParent = parent;
		}
		
		/**
		 * Sends a message to all listeners of the parent.
		 */
		public void sendMessage(Message message)
		{
			// Check if the result is a message
			if (message != null)
			{
				// Send the result to all registered listeners
				fParent.fireInputMessageEvent(
					new InputMessageEvent(this, message, fContext));
			}
		}
		
		/**
		 * Sets the current context to use for messages sent by this
		 * message sender.
		 * 
		 * @param context the context to use for messages
		 */
		public void setContext(Path context)
		{
			fContext = context;
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataStreamParser.java,v $
//  Revision 1.10  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.9  2006/04/18 14:01:07  tames
//  Reflects relocated MessageSender interface.
//
//  Revision 1.8  2006/04/18 04:24:23  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.7  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.6  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.5  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/09/14 21:31:18  chostetter_cvs
//  Fixed BasisBundle name issue in DataSpace
//
//  Revision 1.3  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.2  2005/09/14 18:04:18  tames_cvs
//  Added message sender support.
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//
