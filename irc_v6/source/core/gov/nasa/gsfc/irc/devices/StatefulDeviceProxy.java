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

import java.util.Iterator;

import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.events.OutputMessageListener;
import gov.nasa.gsfc.irc.devices.events.OutputMessageSource;
import gov.nasa.gsfc.irc.devices.models.DeviceStateModel;


/**
 * The StatefulDeviceProxy extends the <code>DefaultDeviceProxy</code> by
 * changing how the
 * {@link gov.nasa.gsfc.irc.devices.models.DeviceStateModel DeviceStateModel} is
 * connected. In this device proxy all messages (both input and output) are
 * passed to the state model. The state model can therefore modify or filter
 * them or generate new messages based on the state. Which state model to use is
 * specified by the device descriptor.
 * 
 * @version $Date: 2006/04/25 19:51:40 $
 * @author Troy Ames
 */
public class StatefulDeviceProxy extends DefaultDeviceProxy
{	
	/**
	 * Constructs an uninitialized instrument proxy instance.
	 */
	public StatefulDeviceProxy()
	{
		super();
	}
	
	/**
	 * Creates an instrument proxy initialized with the specified descriptor. 
	 * Causes all subsystems specified by the descriptor to be recursively 
	 * created.
	 *
	 * @param   descriptor description of an external device and subsystems.
	**/
	public StatefulDeviceProxy(DeviceDescriptor descriptor)
	{
		super(descriptor);
	}
	
	/**
	 * Initializes the event registration for the given component. If the
	 * component is an {@link OutputMessageListener} and a
	 * {@link gov.nasa.gsfc.irc.devices.models.DeviceStateModel DeviceStateModel} then
	 * the component is registered as a listener of this device for messages. If
	 * the component is an <code>InputMessageSource</code> and a
	 * <code>DeviceStateModel</code> then this device is registered as a
	 * listener. If the component is not <code>DeviceStateModel</code> then 
	 * the necessary connections to the state model are made.
	 * <p>
	 * Subclasses can override this method and <code>unregisterComponent</code>
	 * to change the default registration architecture.
	 * 
	 * @param component The Component to be added
	 * @see #unregisterComponent(MinimalComponent)
	 */
	protected void registerComponent(MinimalComponent component)
	{
		if (component instanceof DeviceStateModel)
		{
			// Component is a state model so we need to connect it to the 
			// Device proxy and any existing components 
			// (except other state components).
			
			if (component instanceof OutputMessageListener)
			{
				// Connect the state model to this device proxy
				addOutputMessageListener((OutputMessageListener) component);
			}
			
			if (component instanceof OutputMessageSource)
			{
				// Add all existing child listeners to this source
				for (Iterator i = iterator(); i.hasNext();)
				{
					Object child = i.next();
					
					if (child instanceof OutputMessageListener
							&& !(child instanceof DeviceStateModel))
					{
						((OutputMessageSource) component).addOutputMessageListener(
							(OutputMessageListener) child);
					}
				}
			}
			
			if (component instanceof InputMessageListener)
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
			if (component instanceof InputMessageSource)
			{
				// Connect this device proxy to the state model
				((InputMessageSource) component).addInputMessageListener(this);			
			}
		}
		else
		{
			// Component is not a state component so we need to make
			// the connections to any existing state model.
			
			if (component instanceof OutputMessageListener)
			{
				// Add component as a listener of any existing state component
				for (Iterator i = iterator(); i.hasNext();)
				{
					Object child = i.next();
					
					if (child instanceof DeviceStateModel
							&& child instanceof OutputMessageSource)
					{
						((OutputMessageSource) child).addOutputMessageListener(
							(OutputMessageListener) component);
					}
				}
			}
			
			if (component instanceof InputMessageSource)
			{
				// Add an existing state component as a listener to this 
				// component if any exists.
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
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: StatefulDeviceProxy.java,v $
//  Revision 1.5  2006/04/25 19:51:40  tames_cvs
//  Changed to reflect relocation of the DeviceStateModel interface.
//
//  Revision 1.4  2006/04/18 04:03:35  tames
//  Changes to support sending and receiving messages via the EventBus.
//
//  Revision 1.3  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/30 16:02:07  tames_cvs
//  Fixed the register method. It was not making all the connections.
//
//  Revision 1.1  2005/09/02 18:20:23  tames
//  Initial implementation.
//
//
