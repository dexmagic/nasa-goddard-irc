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

package gov.nasa.gsfc.irc.devices.models;

import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.components.AbstractManagedComponent;
import gov.nasa.gsfc.irc.components.ComponentId;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.description.StateModelDescriptor;
import gov.nasa.gsfc.irc.devices.events.InputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.InputMessageListener;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.events.OutputMessageEvent;
import gov.nasa.gsfc.irc.devices.events.OutputMessageListener;
import gov.nasa.gsfc.irc.devices.events.OutputMessageSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This abstract class implements the default behavior for a device state
 * model. The default behavior is to act as a pass-through for all messages
 * to and from the device. Therefore, this class provides input and output
 * message listener support (input and output message source), and declares
 * itself as an input and output message listener.
 * 
 * @version $Date: 2006/04/25 19:52:24 $
 * @author  Jeffrey C. Hosler
 */
public abstract class AbstractDeviceStateModel extends AbstractManagedComponent 
    implements DeviceStateModel,
               InputMessageListener, InputMessageSource,
               OutputMessageListener, OutputMessageSource
{
    private static final String CLASS_NAME = AbstractDeviceStateModel.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
    public static final String DEFAULT_NAME = "Device State Model";
    
    // InputMessageEvent listeners
    private transient List fInputListeners =
        Collections.synchronizedList(new ArrayList(0));
    
    // OutputMessageEvent listeners
    private transient List fOutputListeners =
        Collections.synchronizedList(new ArrayList(0));

    /**
     * Constructs a new DeviceStateModel whose ComponentId is the given
     * ComponentId. Note that the new ManagedComponent will need to have its
     * ComponentManager set (if any is desired).
     *
     * @param componentId The ComponentId of the new ManagedComponent
     */
    protected AbstractDeviceStateModel(ComponentId componentId)
    {
        super(componentId);
    }
        
    /**
     * Constructs a new CompositePort having a default name.
     */ 
    public AbstractDeviceStateModel()
    {
        super(DEFAULT_NAME);
    }
        
    /**
     * Constructs a new CompositePort having the given base name.
     * 
     * @param name The base name of the new CompositePort
     */ 
    public AbstractDeviceStateModel(String name)
    {
        super(name);
    }
        
    /**
     * Constructs a new CompositePort configured according to the given
     * PortDescriptor.
     * 
     * @param descriptor The PortDescriptor of the new CompositePort
     */
    public AbstractDeviceStateModel(StateModelDescriptor descriptor)
    {
        super(descriptor);
    }
            
    //----------------------------------------------------------------------
    //  Descriptor-related methods
    //----------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.components.AbstractIrcComponent#setDescriptor(gov.nasa.gsfc.irc.description.Descriptor)
     */
    public void setDescriptor(Descriptor descriptor)
    {
        if (descriptor instanceof StateModelDescriptor)
        {
            super.setDescriptor(descriptor);
        }
        else
        {
            if (sLogger.isLoggable(Level.WARNING))
            {
                String message = "Supplied descriptor is not a valid " +
                        "StateModelDescriptor";
                    
                sLogger.logp(Level.WARNING, CLASS_NAME, "setDescriptor", 
                    message);
            }
        }
    }
    
    //----------------------------------------------------------------------
    //  Event firing methods
    //----------------------------------------------------------------------
    
    /**
     * Fire a InputMessageEvent to any registered listeners.
     * 
     * @param event  The InputMessageEvent object.
     */
    protected void fireInputMessageEvent(InputMessageEvent event) 
    {
        Object[] targets = null;

        synchronized (fInputListeners)
        {
            targets = fInputListeners.toArray();
        }

        for (int i = 0; i < targets.length; i++)
        {
            ((InputMessageListener) targets[i]).handleInputMessageEvent(event);
        }
    }

    /**
     * Fire an OutputMessageEvent to any child registered listeners.
     * 
     * @param event  The OutputMessageEvent object.
     */
    protected void fireOutputMessageEvent(OutputMessageEvent event) 
    {
        Object[] targets = null;

        synchronized (fOutputListeners)
        {
            targets = fOutputListeners.toArray();
        }

        for (int i = 0; i < targets.length; i++)
        {
            ((OutputMessageListener) targets[i]).handleOutputMessageEvent(event);
        }
    }

    //----------------------------------------------------------------------
    //  Event handler methods
    //----------------------------------------------------------------------

    /**
     * Handle a message event by first checking that this device is in the
     * event path and then forward it to all listeners. The event
     * is discarded if it does not include this device in the path.
     * 
     * @param event an OutputMessageEvent 
     */
    public void handleOutputMessageEvent(OutputMessageEvent event)
    {
        Path eventPath = event.getSendContext();
        
        if (eventPath != null)
        {
            fireOutputMessageEvent(event);
        }
    }

    /**
     * Handles a received InputMessageEvent by adding this ports name to the 
     * end of the path and publishing a new event to all registered 
     * InputMessageListeners.
     *
     * @param event An InputMessageEvent
     */
    public void handleInputMessageEvent(InputMessageEvent event)
    {
        if (event != null)
        {
            Path path = new DefaultPath(getName(), event.getReplyContext());
            
            fireInputMessageEvent(
                new InputMessageEvent(this, event.getMessage(), path));
        }       
    }

    //----------------------------------------------------------------------
    //  Event Listener Registration Methods
    //----------------------------------------------------------------------
    
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
    
    /**
     * Defines the default behavior for streaming this object.
     * 
     * @param out   The output stream
     * 
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    /**
     * Defines the default behavior for reading the object state from a stream.
     * 
     * @param in    The input stream
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
// $Log: AbstractDeviceStateModel.java,v $
// Revision 1.1  2006/04/25 19:52:24  tames_cvs
// Added from the ASF project.
//
//
// ASF project history
// Revision 1.5  2006/04/25 19:33:07  jhosler_cvs
// Updated class implementation to reflect latest namespace changes in IRC
//
// Revision 1.4  2006/04/25 13:34:54  jhosler_cvs
// Changed super class to AbstractManagedComponent to reflect recent changes
// in the IRC class hierarchy. Latest changes require this device state model to
// implement the ManagedComponent interface so that other components can
// register as listeners to this classes message events.
//
// Revision 1.3  2006/04/21 18:46:11  tames_cvs
// Updated constructors to match IRC constructor changes.
//
// Revision 1.2  2006/04/20 22:16:41  jhosler_cvs
// Updated to reflect refactoring of IRC
//
// Revision 1.1  2005/11/14 16:45:10  jhosler_cvs
// Moved from the gov.nasa.gsfc.asf.controlsystem.device package
//
// Revision 1.2  2005/10/24 14:13:11  jhosler_cvs
// Code cleanup
//
// Revision 1.1  2005/09/12 20:37:12  jhosler_cvs
// Initial version
//
//