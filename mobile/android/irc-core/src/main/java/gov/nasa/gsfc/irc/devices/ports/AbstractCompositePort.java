//=== File Prolog ============================================================
//
//	$Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/AbstractCompositePort.java,v 1.21 2006/08/01 19:55:48 chostetter_cvs Exp $
//
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.ports;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.AbstractManagedCompositeComponent;
import gov.nasa.gsfc.irc.components.ComponentFactory;
import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleFactory;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataElementDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.description.DataInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.events.OutputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.OutputMessageListener;
import gov.nasa.gsfc.irc.devices.events.OutputMessageSource;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.PortAdapter;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectListener;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectSource;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferListener;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferSource;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferListener;
import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferSource;

/**
 * Implements a composite port that builds child adapters and connections based
 * on a PortDescriptor. This abstract implementation will filter 
 * OutputMessageEvents and only pass on events that contain this port in 
 * the event path. OutputMessageEvent objects are passed on to
 * an OutputAdapter. InputMessageEvent objects received from a child 
 * InputAdapter are passed on to all listeners of this port.
 * 
 * @version	$Date: 2006/08/01 19:55:48 $
 * @author		T. Ames
 */
public abstract class AbstractCompositePort extends 
	AbstractManagedCompositeComponent implements Port, InputMessageListener, 
		OutputMessageSource, BasisBundleSource
{
	private static final String CLASS_NAME = AbstractCompositePort.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Composite Port";
	
	public static final String DESCRIPTOR_PROPERTY = "Descriptor";

	// InputMessageEvent listeners
	private transient List fInputListeners = new CopyOnWriteArrayList();
	
	// OutputMessageEvent listeners
	private transient List fOutputListeners = new CopyOnWriteArrayList();
	
	private Path fPath;
	
	
	/**
	 * Constructs a new CompositePort having a default name.
	 */	
	public AbstractCompositePort()
	{
		this(DEFAULT_NAME);
	}
		
	/**
	 * Constructs a new CompositePort having the given base name.
	 * 
	 * @param name The base name of the new CompositePort
	 */	
	public AbstractCompositePort(String name)
	{
		super(name);
	}
		
	/**
	 * Constructs a new CompositePort configured according to the given
	 * PortDescriptor.
	 * 
	 * @param descriptor The PortDescriptor of the new CompositePort
	 */
	public AbstractCompositePort(PortDescriptor descriptor)
	{
		super(descriptor);
	}

	//----------------------------------------------------------------------
	//	Descriptor-related methods
	//----------------------------------------------------------------------
	
	/**
	 * Sets the Descriptor of this CompositePort to the given Descriptor. The
	 * CompositePort will in turn be (re)configured in accordance with the given
	 * Descriptor.
	 * 
	 * @param descriptor A Descriptor
	 */	
	public void setDescriptor(Descriptor descriptor)
	{
		if (descriptor instanceof PortDescriptor)
		{
			super.setDescriptor(descriptor);
			
			configureFromDescriptor();
		}
	}
		
	/**
	 * Configures the port in accordance with the given Descriptor.
	 * 
	 * @param descriptor A PortDescriptor
	 */
	private void configureFromDescriptor()
	{
		PortDescriptor descriptor = (PortDescriptor) getDescriptor();
		
		if (descriptor == null)
		{
			return;
		}
		
		fPath = descriptor.getPath();
		
		try
		{
			setName(descriptor.getName());
		}
		catch (PropertyVetoException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Unable to set name from descriptor name = " + 
					descriptor.getFullyQualifiedName();
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"configureFromDescriptor", message, ex);
			}
		}
		
		// Instantiate any DataElements specified in the port's DataInterfaces
		
		DataSpace dataSpace = Irc.getDataSpace();
		
		BasisBundleFactory basisBundleFactory = Irc.getBasisBundleFactory();
		
		Iterator dataInterfaces = descriptor.getDataInterfaces();
		
		while (dataInterfaces.hasNext())
		{
			DataInterfaceDescriptor dataInterface = (DataInterfaceDescriptor) 
				dataInterfaces.next();
			
			Iterator dataElements = dataInterface.getDataElements(); 
			
			while (dataElements.hasNext())
			{
				DataElementDescriptor dataElement = (DataElementDescriptor) 
					dataElements.next();
				
				if (dataElement instanceof BasisBundleDescriptor)
				{
					BasisBundleDescriptor basisBundleDescriptor = 
						(BasisBundleDescriptor) dataElement;
					
					int size = basisBundleDescriptor.getSize();
					
					BasisBundle basisBundle = 
						basisBundleFactory.createBasisBundle
							(basisBundleDescriptor, this, size);
					
					if (basisBundle != null)
					{
						dataSpace.addBasisBundle(basisBundle);
					}
				}
			}
		}
		
		ComponentFactory factory = Irc.getComponentFactory();
				
		// Get ConnectionDescriptors from PortDescriptor and examine them
		Iterator connectionsIter = descriptor.getConnections().iterator();

		while (connectionsIter.hasNext())
		{
			ConnectionDescriptor connDescriptor = 
				(ConnectionDescriptor) connectionsIter.next();
			Connection connection = 
				(Connection) factory.createComponent(connDescriptor.getClassName());
			connection.setDescriptor(connDescriptor);
			
			// Add connection as child component
			addComponent(connection);
		}

		// Get the OutputAdapterDescriptor from the PortDescriptor
		OutputAdapterDescriptor outputDescriptor = descriptor.getOutputAdapter();
		
		// Create and connect up OutputAdapter if one is specified
		if (outputDescriptor != null)
		{
			PortAdapter adapter = 
				(PortAdapter) factory.createComponent(outputDescriptor.getClassName());
			adapter.setDescriptor(outputDescriptor);
			
			// Add formatter as child component
			addComponent(adapter);
		}
		
		// Get the InputAdapterDescriptor from the PortDescriptor
		InputAdapterDescriptor inputDescriptor = descriptor.getInputAdapter();
		
		// Create and connect up parser if one is specified
		if (inputDescriptor != null)
		{
			PortAdapter adapter = 
				(PortAdapter) factory.createComponent(inputDescriptor.getClassName());
			adapter.setDescriptor(inputDescriptor);

			// Add parser as child component
			addComponent(adapter);
		}
	}
	
	/**
	 * Fire a InputMessageEvent to any registered listeners.
	 * 
	 * @param event  The InputMessageEvent object.
	 */
	protected void fireInputMessageEvent(InputMessageEvent event) 
	{
		for (Iterator iter = fInputListeners.iterator(); iter.hasNext();)
		{
			((InputMessageListener) iter.next()).handleInputMessageEvent(event);
		}
	}

	/**
	 * Fire an OutputMessageEvent to any child registered listeners.
	 * 
	 * @param event  The OutputMessageEvent object.
	 */
	protected void fireOutputMessageEvent(OutputMessageEvent event) 
	{
		for (Iterator iter = fOutputListeners.iterator(); iter.hasNext();)
		{
			((OutputMessageListener) iter.next()).handleOutputMessageEvent(event);
		}
	}

	/**
	 * Adds the given Component to this device. This method calls 
	 * {@link #registerComponent(MinimalComponent) registerComponent}
	 * to set up the default event registration and then calls 
	 * <code>super.addComponent</code>.
	 * 
	 * @param component The Component to be added
	 * @return True if the given Component was actually added
	 **/
	public boolean addComponent(MinimalComponent component)
	{
		boolean result = super.addComponent(component);
		
		if (result == true)
		{
			registerComponent(component);
		}
		
		return (result);
	}

	/**
	 * Initializes the event registration for the given component. 
	 * If the component is an
	 * {@link OutputMessageListener} then the component registered as a 
	 * listener of this port. If the component is an 
	 * {@link InputMessageSource} then this port is registered as a listener.
	 * If the component is a PortAdapter it is registered with all existing 
	 * child Connections. If the component is a Connection it is registered to 
	 * all existing OutputAdapters and all InputAdapters are registered to it.
	 * <p>Subclasses can override this method and 
	 * <code>unregisterComponent</code> to change the default 
	 * registration architecture. 
	 * 
	 * @param component The Component to be added
	 * @see #unregisterComponent(MinimalComponent)
	 **/
	protected void registerComponent(MinimalComponent component)
	{
		// Handle new PortAdapters
		
		if (component instanceof OutputMessageListener)
		{
			addOutputMessageListener((OutputMessageListener) component);
		}
		
		if (component instanceof InputMessageSource)
		{
			((InputMessageSource) component).addInputMessageListener(this);
		}
		
		// Register with existing Connections
		if (component instanceof InputBufferListener)
		{
			// Register new component as a listener to all existing 
			// Connections (InputBufferSource)
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputBufferSource)
				{
					((InputBufferSource) child).addInputBufferListener(
						(InputBufferListener) component);
				}
			}
		}

		// Register with existing ConnectEvent sources
		if (component instanceof ConnectListener)
		{
			// Register new component as a listener to all existing 
			// Connections (ConnectSource)
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof ConnectSource)
				{
					((ConnectSource) child).addConnectListener(
						(ConnectListener) component);
				}
			}
		}
		
		if (component instanceof OutputBufferSource)
		{
			// Register existing Connections to the new component
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof OutputBufferListener)
				{
					((OutputBufferSource) component).addOutputBufferListener(
						(OutputBufferListener) child);
				}
			}
		}
		
		// Handle new Connections
		
		// If the component is an OutputBufferListener then register it as a 
		// listener to all existing child components that are sources of 
		// OutputBufferEvents.
		if (component instanceof OutputBufferListener)
		{
			// Add this component as a listener to any sources if they exists
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof OutputBufferSource)
				{
					((OutputBufferSource) child).addOutputBufferListener(
						(OutputBufferListener) component);
				}
			}
		}
		
		// If the component is an InputBufferSource then register 
		// all existing child components that are listeners 
		// of InputBufferEvents.
		if (component instanceof InputBufferSource)
		{
			// Add any listeners to this source if they exists
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputBufferListener)
				{
					((InputBufferSource) component).addInputBufferListener(
						(InputBufferListener) child);
				}
			}
		}

		// If the component is an ConnectSource then register 
		// all existing child components that are listeners 
		// of ConnectEvents.
		if (component instanceof ConnectSource)
		{
			// Add any listeners to this source if they exists
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof ConnectListener)
				{
					((ConnectSource) component).addConnectListener(
						(ConnectListener) child);
				}
			}
		}
	}

	/**
	 * Removes the given Component from the Set of Components of this Port. This
	 * method calls <code>super.removeComponent(MinimalComponent</code> and
	 * then calls
	 * {@link #unregisterComponent(MinimalComponent) unregisterComponent} to
	 * remove all event registrations associated with this component.
	 * 
	 * @param component The Component to be removed
	 * @return True if the given Component was actually removed
	 */
	public boolean removeComponent(MinimalComponent component)
	{
		boolean result = super.removeComponent(component);
		
		if (result == true)
		{
			unregisterComponent(component);
		}
		
		return (result);
	}

	/**
	 * Removes event registrations for the given component that were
	 * created by the <code>registerComponent</code> method. 
	 * 
	 * @param component The Component to be added
	 * @see #registerComponent(MinimalComponent)
	 **/
	protected void unregisterComponent(MinimalComponent component)
	{
		// Handle removal of PortAdapters
		
		if (component instanceof OutputMessageListener)
		{
			removeOutputMessageListener((OutputMessageListener) component);
		}
		
		if (component instanceof InputMessageSource)
		{
			((InputMessageSource) component).removeInputMessageListener(this);
		}
		
		// Unregister with existing Connections
		if (component instanceof InputBufferListener)
		{
			// Unregister new component as a listener to all existing 
			// Connections (InputBufferSource)
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputBufferSource)
				{
					((InputBufferSource) child).removeInputBufferListener(
						(InputBufferListener) component);
				}
			}
		}

		if (component instanceof OutputBufferSource)
		{
			// Unregister existing Connections to the new component
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof OutputBufferListener)
				{
					((OutputBufferSource) component).removeOutputBufferListener(
						(OutputBufferListener) child);
				}
			}
		}
		
		// Handle remove Connections
		
		// If the component is an OutputBufferListener then unregister it as a 
		// listener to all existing child components that are sources of 
		// OutputBufferEvents.
		if (component instanceof OutputBufferListener)
		{
			// Remove this component as a listener to any sources if they exists
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof OutputBufferSource)
				{
					((OutputBufferSource) child).removeOutputBufferListener(
						(OutputBufferListener) component);
				}
			}
		}
		
		// If the component is an InputBufferSource then unregister 
		// all existing child components that are listeners 
		// of InputBufferEvents.
		if (component instanceof InputBufferSource)
		{
			// Remove this component as a listener to any sources if they exists
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputBufferListener)
				{
					((InputBufferSource) component).removeInputBufferListener(
						(InputBufferListener) child);
				}
			}
		}
	}

	// Event handler Methods ------------------------------------

	/**
	 * Handle a message event by first checking that this device is in the
	 * event path and then forward it to all listeners. The event
	 * is discarded if it does not include this device in the path.
	 * 
	 * @param event an OutputMessageEvent 
	 */
	public void handleOutputMessageEvent(OutputMessageEvent event)
	{
		Path messagePath = event.getSendContext();
		
		// Verify that we are the correct destination for this message.
		if (messagePath != null && messagePath.startsWith(fPath))
		{
			fireOutputMessageEvent(new OutputMessageEvent(this, event));
		}
	}

	/**
	 * Handles a received InputMessageEvent by adding this ports name to the 
	 * end of the path and publishing a new event to all registered 
	 * InputMessageListeners.
	 *
	 * @param event An InputMessageEvent
	**/
	public void handleInputMessageEvent(InputMessageEvent event)
	{
		Path path = new DefaultPath(getName(), event.getReplyContext());
		
		fireInputMessageEvent(
			new InputMessageEvent(this, event.getMessage(), path));
	}

	// Event Listener Registration Methods ------------------------------------
	
	/**
	 * Registers a listener for a {@link InputMessageEvent} from this source.
	 * 
	 * @param listener the listener to register
	 */
	public void addInputMessageListener(InputMessageListener listener)
	{
		fInputListeners.add(listener);
	}

	/**
	 * Unregisters a listener for a {@link InputMessageEvent} from this source.
	 * 
	 * @param listener the listener to unregister
	 */
	public void removeInputMessageListener(InputMessageListener listener)
	{
		fInputListeners.remove(listener);
	}
	
	/**
	 * Returns all the registered listeners to this source
	 * 
	 * @return an array of InputListeners
	 */
	public InputMessageListener[] getInputMessageListeners()
	{
		return (InputMessageListener[])(
				fInputListeners.toArray(
					new InputMessageListener[fInputListeners.size()]));
	}
	
	/** 
	 * Adds the given listener to the set of registered OutputMessageEvent 
	 * listeners.
	 * 
	 * @param listener the event listener to register
	 */
	public void addOutputMessageListener(OutputMessageListener listener)
	{
		fOutputListeners.add(listener);
	}

	/** 
	 * Removes the given listener from the set of registered OutputMessageEvent 
	 * listeners.
	 * 
	 * @param listener the event listener to unregister
	 */
	public void removeOutputMessageListener(OutputMessageListener listener)
	{
		fOutputListeners.remove(listener);
	}

	/**
	 * Returns the set of OutputMessageEvent listeners.
	 * 
	 * @return an array of registered listeners
	 */
	public OutputMessageListener[] getOutputMessageListeners()
	{
		return (OutputMessageListener[])(
				fOutputListeners.toArray(
					new OutputMessageListener[fOutputListeners.size()]));
	}
		
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in) 
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		// InputMessageEvent listeners
		fInputListeners = Collections.synchronizedList(new ArrayList(0));
		
		// OutputMessageEvent listeners
		fOutputListeners = Collections.synchronizedList(new ArrayList(0));
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractCompositePort.java,v $
//  Revision 1.21  2006/08/01 19:55:48  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.20  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.19  2006/04/18 04:08:02  tames
//  Changed to reflect refactored Input and Output messages.
//
//  Revision 1.18  2006/03/07 23:32:42  chostetter_cvs
//  NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
//  Revision 1.17  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.16  2005/11/28 23:17:51  tames
//  Changed cast for input and output adapters to just PortAdapter for greater
//  flexibility.
//
//  Revision 1.15  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.14  2005/09/20 21:42:45  tames_cvs
//  Changed the configureFromDescriptor method to set the name based on the
//  descriptor name instead of the descriptor fully qualified name. This was
//  breaking response message paths to reply to a specific client.
//
//  Revision 1.13  2005/09/14 21:31:18  chostetter_cvs
//  Fixed BasisBundle name issue in DataSpace
//
//  Revision 1.12  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.11  2005/07/12 17:13:21  tames
//  Small modification to how events are published.
//
//  Revision 1.10  2005/05/04 17:12:01  tames_cvs
//  Added support for ConnectEvent listeners and sources.
//
//  Revision 1.9  2005/04/19 20:36:56  chostetter_cvs
//  Organized imports
//
//  Revision 1.8  2005/04/16 04:05:17  tames
//  Changes to reflect refactored state and activity packages.
//
//  Revision 1.7  2005/02/07 04:55:56  tames
//  Added addComponent and removeComponent methods to make the
//  necessary event connections when components are added or removed.
//
//  Revision 1.6  2005/02/04 21:43:05  tames_cvs
//  Changes to reflect modifications to how Message Events are
//  created and sent.
//
//  Revision 1.5  2005/02/01 18:14:52  tames
//  Reflects changes to MessageEvent handling.
//
//  Revision 1.4  2005/01/20 21:03:11  tames
//  Fixed ClassCastException when getting the array of listeners.
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
//  Revision 1.4  2004/10/05 13:50:16  tames_cvs
//  Reflects changes made to the OutputAdapter interface and abstract
//  implementations.
//
//  Revision 1.3  2004/09/28 21:53:29  tames_cvs
//  InputAdapters now do not have to be sources of InputMessageEvents.
//
//  Revision 1.2  2004/09/27 21:47:02  tames
//  Reflects a refactoring of port descriptors.
//
//  Revision 1.1  2004/09/27 20:31:18  tames
//  Reflects a refactoring of port architecture.
//
//  Revision 1.16  2004/08/06 14:33:19  tames_cvs
//  parser and formatter descriptor changes
//
//  Revision 1.15  2004/08/03 20:33:05  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.14  2004/07/28 18:24:16  tames_cvs
//  Changed interactions between ports and formatters.
//
//  Revision 1.13  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.12  2004/07/15 17:48:55  chostetter_cvs
//  ComponentManager, property change work
//
//  Revision 1.11  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.10  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.9  2004/07/06 14:53:20  tames_cvs
//  Changed to pass ByteBuffers
//
//  Revision 1.8  2004/07/06 14:45:20  tames_cvs
//  Updated to reflect message changes
//
//  Revision 1.7  2004/06/07 14:31:44  tames_cvs
//  Changed to use the component factory to construct components.
//
//  Revision 1.6  2004/05/29 03:07:35  chostetter_cvs
//  Organized imports
//
//  Revision 1.5  2004/05/27 16:10:34  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.4  2004/05/12 21:58:23  chostetter_cvs
//  Revisions for Descriptor changes (primarily).
//
//  Revision 1.3  2004/05/03 15:17:24  tames_cvs
//  Added set and get methods for Connect, OutputAdapter, and InputAdapter 
//  components.
//
//  Revision 1.2  2004/04/30 20:40:33  tames_cvs
//  removed methods using obsolete PortMessage
//
//  Revision 1.1  2004/04/30 20:32:19  tames_cvs
//  Initial Version
//
