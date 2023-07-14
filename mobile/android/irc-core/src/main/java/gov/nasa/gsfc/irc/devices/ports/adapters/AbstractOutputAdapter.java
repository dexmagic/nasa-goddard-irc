//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/AbstractOutputAdapter.java,v 1.11 2006/03/14 14:57:16 chostetter_cvs Exp $
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

import java.nio.Buffer;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferListener;

/**
 * Abstract implementation of the OutputAdapter component interface. A 
 * OutputAdapter generates and publishes OutputBufferEvents. Subclasses can 
 * call the <code>fireOutputBufferEvent</code> method with a generated event.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	Troy Ames
 */
public abstract class AbstractOutputAdapter extends AbstractPortAdapter
		implements OutputAdapter
{
	public static final String DEFAULT_NAME = "Output Adapter";
	
	// OutputBufferEvent listeners
	private transient List fListeners = new CopyOnWriteArrayList();
	

	/**
	 *  Constructs a new OutputAdapter having a default name and managed by 
	 *  the default ComponentManager.
	 * 
	 **/

	public AbstractOutputAdapter()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	/**
	 * Constructs a new OutputAdapter having the given name and managed by the 
	 * default ComponentManager.
	 * 
	 * @param name The name of the new OutputAdapter
	 */
	
	public AbstractOutputAdapter(String name)
	{
		super(name);
	}
	

	/**
	 *	Constructs a new OutputAdapter, configured according to the given 
	 *  OutputAdapterDescriptor.
	 *
	 *  @param descriptor The OutputAdapterDescriptor of the new OutputAdapter
	 */
	
	public AbstractOutputAdapter(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Fire a OutputBufferEvent to any registered listeners.
	 * 
	 * @param event  The OutputBufferEvent object.
	 */
	protected void fireOutputBufferEvent(OutputBufferEvent event) 
	{
		Buffer dataBuffer = event.getData();
		int cachedPosition = dataBuffer.position();
		
		for (Iterator iter = fListeners.iterator(); iter.hasNext();)
		{
			((OutputBufferListener) iter.next()).handleOutputBufferEvent(event);
			dataBuffer.position(cachedPosition);
		}
	}

	/**
	 * Registers the given listener for OutputBufferEvent generated by this
	 * OutputAdapter.
	 * 
	 * @param listener a OutputBufferListener to register
	 */
	public void addOutputBufferListener(OutputBufferListener listener)
	{
		fListeners.add(listener);
	}

	/**
	 * Unregisters the given listener.
	 * 
	 * @param listener a OutputBufferListener to unregister
	 */
	public void removeOutputBufferListener(OutputBufferListener listener)
	{
		fListeners.remove(listener);
	}

	/**
	 * Gets an array of listeners registered to receive notification
	 * of new OutputBufferEvent.
	 * 
	 * @return an array of registered OutputBufferListeners
	 */
	public OutputBufferListener[] getOutputBufferListeners()
	{
		return (OutputBufferListener[])(
				fListeners.toArray(new OutputBufferListener[fListeners.size()]));
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractOutputAdapter.java,v $
//  Revision 1.11  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.10  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.8  2005/07/29 19:45:06  tames_cvs
//  Changed fireOutputBufferEvent to reset the buffer position between
//  sending to listeners. Should consider creating a duplicate of the buffer
//  instead.
//
//  Revision 1.7  2005/07/12 17:13:44  tames
//  Small modification to how events are published.
//
//  Revision 1.6  2005/05/04 17:10:57  tames_cvs
//  Removed unused import.
//
//  Revision 1.5  2005/05/02 15:27:17  tames
//  Removed some unused fields.
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
//  Revision 1.3  2004/10/05 13:50:16  tames_cvs
//  Reflects changes made to the OutputAdapter interface and abstract
//  implementations.
//
//  Revision 1.2  2004/09/27 21:47:20  tames
//  Reflects a refactoring of port descriptors.
//
//  Revision 1.1  2004/09/27 20:32:45  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//  Revision 1.5  2004/08/09 17:28:33  tames_cvs
//  Event propagation bug fix.
//
//  Revision 1.4  2004/08/06 14:34:44  tames_cvs
//  Added Format descriptor
//
//  Revision 1.3  2004/08/03 20:34:19  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.2  2004/07/28 18:24:16  tames_cvs
//  Changed interactions between ports and formatters.
//
//  Revision 1.1  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//