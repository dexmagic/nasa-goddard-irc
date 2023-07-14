//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/AbstractInputAdapter.java,v 1.11 2006/03/14 14:57:16 chostetter_cvs Exp $
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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.components.ComponentId;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

/**
 * Abstract implementation of the InputAdapter component interface. 
 * Implementers are responsible for transforming raw input data to an
 * internal representation. Received InputBufferEvent objects 
 * are handled by calling the <code>process</code> method.
 * Subclasses will at a minimum need to implement the <code>process</code> 
 * method.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	Troy Ames
 */
public abstract class AbstractInputAdapter extends AbstractPortAdapter
		implements InputAdapter 
{
	private static final String CLASS_NAME = AbstractInputAdapter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Input Adapter";
	

	/**
	 *  Constructs a new InputAdapter whose ComponentId is the given ComponentId 
	 *  and that is managed by the default ComponentManager.
	 *  
	 *  @param componentId The ComponentId of the new InputAdapter
	 **/

	protected AbstractInputAdapter(ComponentId componentId)
	{
		super(componentId);
	}
		
	
	/**
	 *  Constructs a new InputAdapter having a default name and managed by 
	 *  the default ComponentManager.
	 * 
	 **/

	public AbstractInputAdapter()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 * Constructs a new InputAdapter having the given name and managed by the 
	 * default ComponentManager.
	 * 
	 * @param name The name of the new InputAdapter
	 */
	
	public AbstractInputAdapter(String name)
	{
		super(name);
	}
	

	/**
	 *	Constructs a new InputAdapter configured according to the given 
	 *  InputAdapterDescriptor and managed by the global ComponentManager.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new InputAdapter
	 */
	
	public AbstractInputAdapter(InputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Causes this InputAdapter to process the specified buffer. 
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
	public abstract Object process(BufferHandle handle) throws InputException;

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
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractInputAdapter.java,v $
//  Revision 1.11  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.10  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.8  2005/05/13 04:02:12  tames
//  Added code to handle event to lock and release the data handle.
//
//  Revision 1.7  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.6  2005/03/01 00:02:43  tames
//  Moved the locking and unlocking of the BufferHandle contained in the
//  InputBufferEvent out of the handleInputBufferEvent method.
//
//  Revision 1.5  2005/02/08 15:35:49  tames_cvs
//  Added finally clause to handleInputBuffer method to properly release
//  the buffer even if the process method throws an exception.
//
//  Revision 1.4  2005/02/04 21:45:46  tames_cvs
//  Changes to reflect modifications to how Message and Buffer Events are
//  created and sent.
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
//  Revision 1.4  2004/10/06 15:30:54  tames_cvs
//  Reflects a refactoring of the abstract InputAdapter classes.
//
//  Revision 1.3  2004/09/28 21:53:29  tames_cvs
//  InputAdapters now do not have to be sources of InputMessageEvents.
//
//  Revision 1.2  2004/09/27 21:47:20  tames
//  Reflects a refactoring of port descriptors.
//
//  Revision 1.1  2004/09/27 20:32:45  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//  Revision 1.4  2004/09/13 20:06:12  tames_cvs
//  Fixed missing Buffer handle release.
//
//  Revision 1.3  2004/08/06 14:35:05  tames_cvs
//  Added Format descriptors
//
//  Revision 1.2  2004/08/03 20:34:35  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.1  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
