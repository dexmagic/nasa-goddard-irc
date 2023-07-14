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

package gov.nasa.gsfc.irc.devices;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*

*/

import gov.nasa.gsfc.commons.publishing.BusEventListener;
import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.messages.MessageEvent;
import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.AbstractManagedCompositeComponent;
import gov.nasa.gsfc.irc.components.ComponentFactory;
import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.description.StateModelDescriptor;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.events.OutputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.OutputMessageListener;
import gov.nasa.gsfc.irc.devices.events.OutputMessageSource;
import gov.nasa.gsfc.irc.devices.models.DeviceStateModel;
import gov.nasa.gsfc.irc.devices.ports.Port;
import gov.nasa.gsfc.irc.devices.ports.PortDescriptor;


/**
 * The DeviceProxy represents an external device. This class is responsible
 * for constructing all subsystems associated with this device. The ports which 
 * are constructing are ports that are used to communicate to the external 
 * device. This DeviceProxy implementation can be nested with another
 * DeviceProxy forming a component to subcomponent relationship.
 *  
 * @version	$Date: 2006/04/25 19:51:40 $
 * @author	Troy Ames
**/
public class DefaultDeviceProxy extends AbstractManagedCompositeComponent
	implements DeviceProxy, InputMessageListener, OutputMessageSource,
	OutputMessageListener, InputMessageSource
{
	private static final String CLASS_NAME = DefaultDeviceProxy.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	/**
	 * Property name for desciptor PropertyChangeEvents.
	 */
	public static final String DESCRIPTOR_PROPERTY = "Descriptor";	
	public static final String DEFAULT_NAME = "Device Proxy";

	// OutputMessageEvent listeners
	private transient List fOutputListeners = new CopyOnWriteArrayList();

	// BusEvent listeners
	private transient List fBusEventListeners = new CopyOnWriteArrayList();

	// InputMessageEvent listeners
	private transient List fInputListeners = new CopyOnWriteArrayList();

	private DeviceDescriptor fDescriptor;
	private Path fPath;
	
	/**
	 * Constructs an uninitialized instrument proxy instance.
	 */
	public DefaultDeviceProxy()
	{
		super(DEFAULT_NAME);
	}
	
	/**
	 * Creates an instrument proxy initialized with the specified descriptor. 
	 * Causes all subsystems specified by the descriptor to be recursively 
	 * created.
	 *
	 * @param   descriptor description of an external device and subsystems.
	**/
	public DefaultDeviceProxy(DeviceDescriptor descriptor)
	{
		super(descriptor.getName());
		
		fDescriptor = descriptor;		
		configureFromDescriptor(descriptor);
	}

	/**
	 * Sets the descriptor and initializes this instrument proxy only if
	 * it has not been previously initialized. This method 
	 * calls overridable mthods <code>buildModel</code>, <code>buildPorts</code>, 
	 * <code>requestPeers</code>, and <code>buildSubsystems</code> 
	 * in that order to initialize this proxy.
	 *
	 * @param descriptor description of an external device and subsystems.
	 * @see #buildModel(StateModelDescriptor)
	 * @see #requestPeers(DeviceDescriptor)
	 * @see #buildSubsystems(DeviceDescriptor)
	**/
	public synchronized void setDescriptor(Descriptor descriptor)
	{
		if (fDescriptor != null | descriptor == null)
		{
			// The descriptor has already been set or the parameter is null.
			return;
		}
		
		super.setDescriptor(descriptor);
		
		DeviceDescriptor oldDescriptor = fDescriptor;
		
		// Clean up components from a previous descriptor
		
		// Apply a new descriptor
		if (descriptor instanceof DeviceDescriptor)
		{
			fDescriptor = (DeviceDescriptor) descriptor;
			configureFromDescriptor((DeviceDescriptor) descriptor);
		}
		
		// Notify property listeners of the change
		firePropertyChange(
			DESCRIPTOR_PROPERTY,
			oldDescriptor,
			descriptor);
	}
	
	/**
	 * Add subsystem instrument.
	 *
	 * @param client DeviceDescriptor for new client
	**/
	public synchronized void addSubsystem(DeviceDescriptor descriptor)
	{
		if (descriptor != null)
		{
			ComponentFactory factory = Irc.getComponentFactory();
			String className = descriptor.getClassName();
		
			DeviceProxy proxy =
				(DeviceProxy) factory.createComponent(className);
			proxy.setDescriptor(descriptor);
			addComponent(proxy);
		}
	}

	// Event Handling Methods -----------------------------------------

	/**
	 * Handle a message event by first checking that this device is in the
	 * event path and then forward it to all listeners. The event
	 * is discarded if it does not include this device in the path.
	 * 
	 * @param event A published EventObject
	 */
	public void receiveBusEvent(EventObject event)
	{
		if (event instanceof MessageEvent)
		{
			Message message = ((MessageEvent) event).getMessage();
			Path messagePath = ((MessageEvent) event).getDestination();
			
			// Verify that we are the correct destination for this message.
			if (messagePath != null && messagePath.startsWith(fPath))
			{
				if (sLogger.isLoggable(Level.FINE))
				{
					String logMessage = "DeviceProxy handling bus message: " 
						+ message;
					
					sLogger.logp(Level.FINE, CLASS_NAME, 
						"receiveBusEvent", logMessage);
				}
				
				fireOutputMessageEvent(
					new OutputMessageEvent(this, message, messagePath));
			}
		}
	}

	/**
	 * Fire a <code>EventObject</code> to any registered listeners.
	 * 
	 * @param event  The EventObject.
	 */
	protected void fireBusEvent(EventObject event) 
	{
		for (Iterator iter = fBusEventListeners.iterator(); iter.hasNext();)
		{
			((BusEventListener) iter.next()).receiveBusEvent(event);
		}
	}

	/**
	 * Handle a message event by first checking that this device is in the
	 * event path and then forward it to all listeners. The event
	 * is discarded if it does not include this device in the path.
	 * 
	 * @param event a OutputMessageEvent 
	 */
	public void handleOutputMessageEvent(OutputMessageEvent event)
	{
		Path messagePath = event.getSendContext();
		
		// Verify that we are the correct destination for this message.
		if (messagePath != null && messagePath.startsWith(fPath))
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String logMessage = "DeviceProxy handling output message: " 
					+ event.getMessage();
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"handleOutputMessageEvent", logMessage);
			}

			fireOutputMessageEvent(new OutputMessageEvent(this, event));
		}
	}

	/**
	 * Fire an <code>OutputMessageEvent</code> to any registered listeners.
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
	 * Handles a received InputMessageEvent by adding this devices name to the 
	 * end of the path and publishing a new event to all registered 
	 * InputMessageListeners. Calls <code>fireInputMessageEvent</code>.
	 *
	 * @param event An InputMessageEvent
	**/
	public void handleInputMessageEvent(InputMessageEvent event)
	{
		Path messagePath = event.getReplyContext();	
		Path path = new DefaultPath(getName(), messagePath);

		if (sLogger.isLoggable(Level.FINE))
		{
			String logMessage = "DeviceProxy handling input message: " 
				+ event.getMessage();
			
			sLogger.logp(Level.FINE, CLASS_NAME, 
				"handleInputMessageEvent", logMessage);
		}

		// Check if there are any input listeners to fire an event to
		if (fInputListeners.size() > 0)
		{
			fireInputMessageEvent(
				new InputMessageEvent(this, event.getMessage(), path));			
		}

		// Check if there are any event bus listeners to fire an event to
		if (fBusEventListeners.size() > 0)
		{
			fireBusEvent(new MessageEvent(this, event.getMessage(), null, path));
		}
	}

	/**
	 * Fire an <code>InputMessageEvent</code> to any registered listeners.
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

	// Event Listener Registration Methods ----------------------------

	/**
	 * Subscribes the given listener to all events published by this
	 * BusEventPublisher.
	 * 
	 * @param listener the listener to register.
	 */
	public void addBusEventListener(BusEventListener listener)
	{
		fBusEventListeners.add(listener);
	}

	/**
	 * Unsubscribes the given listener to all events published by this
	 * BusEventPublisher.
	 * 
	 * @param listener the listener to unregister.
	 */
	public void removeBusEventListener(BusEventListener listener)
	{
		fBusEventListeners.remove(listener);
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
	 * Returns the set of MessageEvent listeners.
	 * 
	 * @return an array of registered listeners
	 */
	public OutputMessageListener[] getOutputMessageListeners()
	{
		return (OutputMessageListener[])(
				fOutputListeners.toArray(
					new OutputMessageListener[fOutputListeners.size()]));
	}
	
	/** 
	 * Adds the given listener to the set of registered InputMessageEvent 
	 * listeners.
	 * 
	 * @param listener the event listener to register
	 */
	public void addInputMessageListener(InputMessageListener listener)
	{
		fInputListeners.add(listener);
	}

	/** 
	 * Removes the given listener from the set of registered InputMessageEvent 
	 * listeners.
	 * 
	 * @param listener the event listener to unregister
	 */
	public void removeInputMessageListener(InputMessageListener listener)
	{
		fInputListeners.remove(listener);
	}

	/**
	 * Returns the set of ExternalMessageEvent listeners.
	 * 
	 * @return an array of registered listeners
	 */
	public InputMessageListener[] getInputMessageListeners()
	{
		return (InputMessageListener[])(
				fInputListeners.toArray(
					new InputMessageListener[fInputListeners.size()]));
	}

	// Initialization Methods ------------------------------------------------
	
	/**
	 * Initializes this proxy based on the specified descriptor. This method 
	 * calls overridable mthods <code>buildModel</code>, <code>buildPorts</code>, 
	 * <code>requestPeers</code>, and <code>buildSubsystems</code> 
	 * in that order to initialize this proxy.
	 *
	 * @param descriptor description of an external device and subsystems.
	 * @see #buildModel(StateModelDescriptor)
	 * @see #requestPeers(DeviceDescriptor)
	 * @see #buildSubsystems(DeviceDescriptor)
	**/
	private synchronized void configureFromDescriptor(DeviceDescriptor descriptor)
	{
		if (descriptor == null)
		{
			return;
		}
		
		fPath = descriptor.getPath();
		
		// try to create the instrument model for this instrument.
		buildModel(descriptor.getStateModel());

		// create any ports
		buildPorts(descriptor);

		// try to discover remote instrument peer descriptions
		requestPeers(descriptor);

		// build the subsystems described in the description.
		buildSubsystems(descriptor);
	}

	/**
	 * Build subsystems if they exist. This method calls overridable method
	 * <code>addSubsystem</code>.
	 * 
	 * @param descriptor description of an external device and subsystems.
	 * @see #addSubsystem(DeviceDescriptor)
	 **/
	protected synchronized void buildSubsystems(DeviceDescriptor descriptor)
	{
		Iterator subInstruments = descriptor.getSubdevices();

		while(subInstruments.hasNext())
		{
			addSubsystem((DeviceDescriptor) subInstruments.next());
		}
	}

	/**
	 * Initiates a discovery request for remote instrument peers
	 * specified in the descriptor.
	 * 
	 * @param descriptor description of an external device and subsystems.
	 **/
	protected synchronized void requestPeers(DeviceDescriptor descriptor)
	{
//		Iterator peerDescriptors = descriptor.getInstrumentPeers();
//		PeerNetworkManager netMgr = null;
//
//		// Don't start the PeerNetworkManager unless it is needed.
//		if (peerDescriptors.hasNext())
//		{
//			netMgr = PeerNetworkManager.getInstance();
//		}
//
//		while(peerDescriptors.hasNext())
//		{
//			DevicePeerDescriptor newDescriptor =
//				(DevicePeerDescriptor)peerDescriptors.next();
//
//			// Initiate a discovery request for the instrument peer
//			netMgr.findDescriptions(newDescriptor);
//		}
	}

	/**
	 * Builds all of the ports described within the specified descriptor. 
	 * 
	 * @param descriptor description of an external device and subsystems.
	 **/
	protected synchronized void buildPorts(DeviceDescriptor descriptor)
	{
		ComponentFactory factory = Irc.getComponentFactory();
		Iterator portsIter = descriptor.getPorts();
		
		while (portsIter.hasNext())
		{
			PortDescriptor portDesc = (PortDescriptor) portsIter.next();
			String className = portDesc.getClassName();
			
			Port port = (Port) factory.createComponent(className);
			port.setDescriptor(portDesc);
			addComponent(port);
		}
	}

	/**
	 * Build the instrument model.
	 *
	 * @param descriptor description of a state model.
	 **/
	protected synchronized void buildModel(StateModelDescriptor descriptor)
	{
		// if ActivityStateModel exists in IML under instrument tag, use that
		if ( descriptor != null )
		{
			ComponentFactory factory = Irc.getComponentFactory();
			String className = descriptor.getClassName();
		
			DeviceStateModel model =
				(DeviceStateModel) factory.createComponent(className);
			
			if (model != null)
			{
				model.setDescriptor(descriptor);
				addComponent(model);
			}
		}
	}
	
	/**
	 * Adds the given Component to this device. This method calls 
	 * {@link #registerComponent(MinimalComponent) registerComponent}
	 * to set up the event registration and then calls 
	 * <code>super.addComponent</code>.
	 * 
	 * @param component The Component to be added
	 * @return True if the given Component was actually added
	 **/
	public boolean addComponent(MinimalComponent component)
	{
		registerComponent(component);
		
		return (super.addComponent(component));
	}

	/**
	 * Initializes the event registration for the given component. If the
	 * component is an {@link OutputMessageListener} then the component
	 * registered as a listener of this device. If the component is an
	 * {@link InputMessageSource} then this device is registered as a listener.
	 * If the component is a {@link DeviceStateModel} then it is registered to
	 * this device as an OutputMessageListener and an InputMessageListener to
	 * all existing child sources.
	 * <p>
	 * Subclasses can override this method and <code>unregisterComponent</code>
	 * to change the default registration architecture.
	 * 
	 * @param component The Component to be added
	 * @see #unregisterComponent(MinimalComponent)
	 */
	protected void registerComponent(MinimalComponent component)
	{
		if (component instanceof OutputMessageListener)
		{
			addOutputMessageListener((OutputMessageListener) component);
		}
		
		if (component instanceof InputMessageSource)
		{
			((InputMessageSource) component).addInputMessageListener(this);
			
			// Add any models as a listener if they exists and are listeners
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof DeviceStateModel
						&& child instanceof InputMessageListener)
				{
					((InputMessageSource) component).addInputMessageListener(
						(InputMessageListener) child);
				}
			}
		}
		
		// If the component is a new state model then if necessary add it as a 
		// listener to all existing child components that are sources of 
		// InputMessages.
		if (component instanceof DeviceStateModel 
				&& component instanceof InputMessageListener)
		{
			// Add this model as a listener to any sources if they exists
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputMessageSource
						&& !(child instanceof DeviceStateModel))
				{
					((InputMessageSource) child).addInputMessageListener(
						(InputMessageListener) component);
				}
			}
		}
	}
	
	/**
	 * Removes the given Component from the Set of Components of this 
	 * Device. This method calls <code>super.removeComponent(MinimalComponent</code>
	 * and then calls 
	 * {@link #unregisterComponent(MinimalComponent) unregisterComponent}
	 * to remove all event registrations associated with this component. 
	 *
	 *  @param component The Component to be removed
	 *  @return True if the given Component was actually removed
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
		// Remove this component as a listener to all other potential sources
		
		if (component instanceof OutputMessageListener)
		{
			// Remove the component as a listener on this device proxy
			removeOutputMessageListener((OutputMessageListener) component);

			// Remove the component as a listener of any other child components
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof OutputMessageSource)
				{
					((OutputMessageSource) child).removeOutputMessageListener(
						(OutputMessageListener) component);
				}
			}
		}
		
		if (component instanceof InputMessageListener)
		{
			// Remove the component as a listener of any other child components
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputMessageSource)
				{
					((InputMessageSource) child).removeInputMessageListener(
						(InputMessageListener) component);
				}
			}
		}
		
		// Remove all potential listeners to this component
		
		if (component instanceof OutputMessageSource)
		{
			// Remove any other listeners
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof OutputMessageListener)
				{
					((OutputMessageSource) component).removeOutputMessageListener(
						(OutputMessageListener) child);
				}
			}
		}

		if (component instanceof InputMessageSource)
		{
			// Remove this device proxy as a listener
			((InputMessageSource) component).removeInputMessageListener(this);
			
			// Remove any other listeners
			for (Iterator i = iterator(); i.hasNext();)
			{
				Object child = i.next();
				
				if (child instanceof InputMessageListener)
				{
					((InputMessageSource) component).removeInputMessageListener(
						(InputMessageListener) child);
				}
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDeviceProxy.java,v $
//  Revision 1.19  2006/04/25 19:51:40  tames_cvs
//  Changed to reflect relocation of the DeviceStateModel interface.
//
//  Revision 1.18  2006/04/18 14:02:48  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.17  2006/04/18 04:03:35  tames
//  Changes to support sending and receiving messages via the EventBus.
//
//  Revision 1.16  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.15  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.14  2005/09/30 16:01:03  tames_cvs
//  Updated the unregister method to work in more configurations.
//
//  Revision 1.13  2005/09/02 18:21:03  tames
//  Documentation changes only.
//
//  Revision 1.12  2005/08/16 17:23:28  tames_cvs
//  Fixed ClassCast exception in fireInputMessageEvent method.
//
//  Revision 1.11  2005/07/12 17:12:57  tames
//  Small modification to how events are published.
//
//  Revision 1.10  2005/02/07 04:55:56  tames
//  Added addComponent and removeComponent methods to make the
//  necessary event connections when components are added or removed.
//
//  Revision 1.9  2005/02/04 21:41:03  tames_cvs
//  Changes to reflect modifications to how Message Events are
//  created and sent.
//
//  Revision 1.8  2005/02/03 07:00:35  tames
//  Updated to reflect changes in DeviceDescriptor.
//
//  Revision 1.7  2005/02/01 16:51:09  tames
//  Reflects changes to MessageEvent handling.
//
//  Revision 1.6  2005/01/21 20:16:51  tames
//  Reflects a refactoring of Message paths to support a new Path interface
//  and implementation.
//
//  Revision 1.5  2005/01/20 21:03:11  tames
//  Fixed ClassCastException when getting the array of listeners.
//
//  Revision 1.4  2004/12/17 22:09:22  tames_cvs
//  Updated the message path inteface and added the capability to set
//  the path of a message directly.
//
//  Revision 1.3  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.1  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.9  2004/09/27 20:09:16  tames
//  Reflect changes to message handling and method signaturre changes.
//
//  Revision 1.8  2004/08/10 13:33:37  tames
//  *** empty log message ***
//
//  Revision 1.7  2004/08/03 20:31:41  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.6  2004/07/27 21:09:34  tames_cvs
//  Message Interface changes
//
//  Revision 1.5  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//  Revision 1.4  2004/07/15 17:48:55  chostetter_cvs
//  ComponentManager, property change work
//
//  Revision 1.3  2004/06/28 17:21:16  chostetter_cvs
//  And here, days later, you _still_ can't cast a Set to a Collection. Go figure.
//
//  Revision 1.2  2004/06/24 19:43:24  chostetter_cvs
//  Turns out you can't cast a Set to a Collection. Grrrr.
//
//  Revision 1.1  2004/06/07 14:18:50  tames_cvs
//  Initial Version
//
//  Revision 1.2  2004/06/04 14:38:18  tames_cvs
//  Complete rewrite with only partial functionality implemented
//
