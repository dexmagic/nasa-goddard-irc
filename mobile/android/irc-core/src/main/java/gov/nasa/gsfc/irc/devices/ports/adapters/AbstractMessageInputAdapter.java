//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/AbstractMessageInputAdapter.java,v 1.14 2006/04/18 04:09:52 tames Exp $
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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

/**
 * Abstract implementation of the InputAdapter component interface for receiving
 * messages. Implementers are responsible for transforming raw input data to an
 * internal representation. Received InputBufferEvent objects 
 * are handled by calling the <code>process</code> method. Subclasses 
 * will at a minimum need to implement the <code>process</code> method. This 
 * implementation will send an InputMessageEvent to all listeners if the 
 * process method returns a Message object.
 * <p> 
 * Note if Message objects are expected to be large and might span many input
 * buffers from a Connection, then subclassing the 
 * {@link gov.nasa.gsfc.irc.devices.ports.adapters.AbstractPipeInputAdapter AbstractPipeInputAdapter} 
 * class might be more appropriate.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:09:52 $
 * @author	Troy Ames
 */
public abstract class AbstractMessageInputAdapter extends AbstractInputAdapter
		implements InputAdapter, InputMessageSource 
{
	private static final String CLASS_NAME = 
		AbstractMessageInputAdapter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Message Input Adapter";

	// InputMessageEvent listeners
	private transient List fListeners = new CopyOnWriteArrayList();


	/**
	 * Constructs a new MessageInputAdapter having a default name and managed by the 
	 * default ComponentManager.
	 */	
	public AbstractMessageInputAdapter()
	{
		super(DEFAULT_NAME);
	}
	
	/**
	 * Constructs a new MessageInputAdapter having the given name.
	 * 
	 * @param name the name of this adapter
	 */	
	public AbstractMessageInputAdapter(String name)
	{
		super(name);
	}	

	/**
	 * Constructs a new MessageInputAdapter, configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new MessageInputAdapter
	 */
	public AbstractMessageInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	/**
	 * Handles a new InputBufferEvent by first setting the BufferHandle
	 * as in use to lock it then calls the <code>process</code>
	 * method with the BufferHandle contained in the InputBufferEvent. If the
	 * process method returns a non null result of type <code>Message</code>
	 * then the Message is inserted into a new <code>InputMessageEvent</code> 
	 * along with the context information from the original event. The 
	 * <code>fireInputMessageEvent</code> is called with the new event.
	 * 
	 * @param event the InputBufferEvent
	 * @see #process(BufferHandle)
	 */
	public void handleInputBufferEvent(InputBufferEvent event)
	{
		BufferHandle handle = event.getHandle();

		try
		{
			// Lock the buffer
			handle.setInUse();
			Object result = process(handle);
			
			// Check if the result is a message
			if (result != null && result instanceof Message)
			{
				// Send the result to all registered listeners
				fireInputMessageEvent(
					new InputMessageEvent(
						this, (Message) result, event.getReplyContext()));
			}
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

	/**
	 * Fire an InputMessageEvent to any registered listeners.
	 * 
	 * @param event  The InputMessageEvent object.
	 */
	protected void fireInputMessageEvent(InputMessageEvent event) 
	{
		for (Iterator iter = fListeners.iterator(); iter.hasNext();)
		{
			((InputMessageListener) iter.next()).handleInputMessageEvent(event);
		}
	}

	/**
	 * Registers the given listener for a InputMessageEvent generated by this
	 * InputAdapter.
	 * 
	 * @param listener a InputMessageListener to register
	 */
	public void addInputMessageListener(InputMessageListener listener)
	{
		fListeners.add(listener);
	}

	/**
	 * Unregisters the given listener.
	 * 
	 * @param listener a InputMessageListener to unregister
	 */
	public void removeInputMessageListener(InputMessageListener listener)
	{
		fListeners.remove(listener);
	}

	/**
	 * Gets an array of listeners registered to receive notification
	 * of new InputMessageEvent objects.
	 * 
	 * @return an array of registered InputListeners
	 */
	public InputMessageListener[] getInputMessageListeners()
	{
		return (InputMessageListener[])(
				fListeners.toArray(new InputMessageListener[fListeners.size()]));
	}

}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractMessageInputAdapter.java,v $
//  Revision 1.14  2006/04/18 04:09:52  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.13  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.12  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.11  2005/11/14 19:52:59  chostetter_cvs
//  Organized imports
//
//  Revision 1.10  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.9  2005/07/12 17:13:44  tames
//  Small modification to how events are published.
//
//  Revision 1.8  2005/05/13 04:02:52  tames
//  Javadoc update only.
//
//  Revision 1.7  2005/04/25 15:49:16  tames
//  Javadoc updates only.
//
//  Revision 1.6  2005/02/08 15:35:49  tames_cvs
//  Added finally clause to handleInputBuffer method to properly release
//  the buffer even if the process method throws an exception.
//
//  Revision 1.5  2005/02/04 21:45:46  tames_cvs
//  Changes to reflect modifications to how Message and Buffer Events are
//  created and sent.
//
//  Revision 1.4  2005/01/20 21:03:11  tames
//  Fixed ClassCastException when getting the array of listeners.
//
//  Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.2  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/06 15:30:54  tames_cvs
//  Reflects a refactoring of the abstract InputAdapter classes.
//
//
