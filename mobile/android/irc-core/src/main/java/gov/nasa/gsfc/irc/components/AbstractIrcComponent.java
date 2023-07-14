//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/AbstractIrcComponent.java,v 1.23 2006/03/14 14:57:16 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
// $Log: AbstractIrcComponent.java,v $
// Revision 1.23  2006/03/14 14:57:16  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.22  2006/03/07 23:32:42  chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
// Revision 1.21  2006/01/23 17:59:51  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.20  2005/04/16 04:03:55  tames
// Changes to reflect refactored state and activity packages.
//
// Revision 1.19  2005/04/06 14:59:46  chostetter_cvs
// Adjusted logging levels
//
// Revision 1.18  2005/01/27 21:38:02  chostetter_cvs
// Implemented new exception state and default exception behavior for Objects having ActivityState
//
// Revision 1.17  2004/11/10 23:09:56  chostetter_cvs
// Initial debugging of Message formatting. Mostly works except for C-style formatting.
//
// Revision 1.16  2004/08/03 20:28:39  tames_cvs
// Changed descriptor configuration handling
//
// Revision 1.15  2004/07/22 20:42:30  chostetter_cvs
// Sets Descriptor on construction
//
// Revision 1.14  2004/07/22 17:06:55  chostetter_cvs
// Namespace-related changes
//
// Revision 1.13  2004/07/21 14:26:15  chostetter_cvs
// Various architectural and event-passing revisions
//
// Revision 1.12  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.11  2004/07/16 15:18:31  chostetter_cvs
// Revised, refactored Component activity state
//
// Revision 1.10  2004/07/16 04:15:44  chostetter_cvs
// More Algorithm work, primarily on properties
//
// Revision 1.9  2004/07/13 18:52:50  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.8  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.7  2004/06/30 13:02:48  chostetter_cvs
// Made setStartedState() protected
//
// Revision 1.6  2004/06/15 23:23:57  chostetter_cvs
// Removed pause ActivityState
//
// Revision 1.5  2004/06/15 20:04:03  chostetter_cvs
// Added ActivityStateModel, use for stative Objects
//
// Revision 1.4  2004/06/11 17:27:56  chostetter_cvs
// Further Input-related work
//
// Revision 1.3  2004/06/04 05:34:42  chostetter_cvs
// Further data, Algorithm, and Component work
//
// Revision 1.2  2004/06/03 00:19:59  chostetter_cvs
// Organized imports
//
// Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
// More Namespace, DataSpace tweaks, created alogirthms package
//
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.ActivityStateModel;
import gov.nasa.gsfc.commons.processing.activity.DefaultActivityStateModel;
import gov.nasa.gsfc.commons.properties.state.State;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;


/**
 *	An IrcComponent is a MinimalComponent that has a dynamic AcitivityState 
 *  and a Descriptor.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2006/03/14 14:57:16 $
 *	@author Carl F. Hostetter
 */
public abstract class AbstractIrcComponent extends AbstractMinimalComponent 
	implements IrcComponent
{
	private static final String CLASS_NAME = AbstractIrcComponent.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final String DEFAULT_NAME = "Component";
	
	private ComponentDescriptor fDescriptor;
	
	private ActivityStateModel fStateModel = new DefaultActivityStateModel();

	//----------------------------------------------------------------------
	//	Construction-related methods
	//----------------------------------------------------------------------

	/**
	 *  Constructs a new Component whose ComponentId is the given ComponentId.
	 *  
	 *  @param componentId The ComponentId of the new Component
	 **/

	protected AbstractIrcComponent(ComponentId componentId)
	{
		super(componentId);
	}
		
	
	/**
	 *	Constructs a new IrcComponent having a default name.
	 *
	 */
	public AbstractIrcComponent()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new IrcComponent having the given base name.
	 *
	 *	@param name The bse name of the new IrcComponent
	 */
	public AbstractIrcComponent(String name)
	{
		super(name);
	}
	
	
	/**
	 *  Constructs a new IrcComponent configured according to the given 
	 *  ComponentDescriptor.
	 * 
	 *  @param descriptor The ComponentDescriptor of the new IrcComponent
	 **/

	public AbstractIrcComponent(ComponentDescriptor descriptor)
	{
		super(descriptor.getName());
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	//----------------------------------------------------------------------
	//	Descriptor-related methods
	//----------------------------------------------------------------------
	
	/**
	 *  Causes this Component to (re)configure itself in accordance with 
	 *  the descriptor.
	 *  
	 *  @param descriptor A ComponentDescriptor
	**/
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			try
			{
				setName(fDescriptor.getName());
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set base name of " + 
						getFullyQualifiedName() + " to " + fDescriptor.getName();
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, ex);
				}
			}
		}
	}
	
	
	/**
	 *  Sets the Descriptor of this Component to the given Descriptor. The 
	 *  Component will in turn be (re)configured in accordance with the given 
	 *  Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/
	
	public void setDescriptor(Descriptor descriptor)
	{
		if (descriptor instanceof ComponentDescriptor)
		{
			fDescriptor = (ComponentDescriptor) descriptor;
			
			configureFromDescriptor();
		}
	}
	
	
	/**
	 *  Returns the Descriptor associated with this Component.
	 *  
	 *  @return	The Descriptor associated with this Component
	**/
	public Descriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	//----------------------------------------------------------------------
	//	State-related methods
	//----------------------------------------------------------------------
	
	/**
	 *	Returns the current State of this Component.
	 *
	 *  @return The current State of this Component
	 */
	public State getState()
	{
		return (fStateModel.getState());
	}
	
	
	/**
	 *  Causes this Component to start.
	 * 
	 */
	public void start()
	{
		try
		{
			fStateModel.start();
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = getFullyQualifiedName() + " has started";
				
				sLogger.logp(Level.FINE, CLASS_NAME, "start", message);
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}
	}
	
	
	/**
	 *  Returns true if this Component is currently started, false otherwise. 
	 *  A Component is also considered started if it is active or waiting.
	 *  
	 *  @return True if this Component is currently started, false otherwise
	 */
	public boolean isStarted()
	{
		return (fStateModel.isStarted());
	}
	

	/**
	 *  Sets this Component's State to active. A Component cannot become active 
	 *  unless it is currently started.
	 *  
	 */
	protected void declareActive()
	{
		if (! isActive())
		{
			fStateModel.declareActive();
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = getFullyQualifiedName() + " is active";
				
				sLogger.logp(Level.FINE, CLASS_NAME, "declareActive", message);
			}
		}
	}
	
	
	/**
	 * Returns true if this Component is active, false otherwise.
	 *
	 * @return True if this Component is active, false otherwise
	 **/
	public boolean isActive()
	{
		return (fStateModel.isActive());
	}
	

	/**
	 *  Sets this Component's State to waiting. A Component cannot become 
	 *  waiting unless it is currently active.
	 */
	
	protected void declareWaiting()
	{
		fStateModel.declareWaiting();
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = getFullyQualifiedName() + " is waiting";
			
			sLogger.logp(Level.FINE, CLASS_NAME, "declareWaiting", message);
		}
	}
	
	
	/**
	 * Returns true if this Component is waiting, false otherwise.
	 *
	 * @return True if this Component is waiting, false otherwise
	 **/
	
	public boolean isWaiting()
	{
		return (fStateModel.isWaiting());
	}
	

	/**
	 *  Causes this Component to stop.
	 *  
	 */
	
	public void stop()
	{
		fStateModel.stop();
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = getFullyQualifiedName() + " has stopped";
			
			sLogger.logp(Level.FINE, CLASS_NAME, "stop", message);
		}
	}
	
	
	/**
	 *  Returns true if this Component is stopped (i.e., not started), 
	 *  false otherwise.
	 *
	 *  @return True if this Component is stopped (i.e., not started), 
	 * 		false otherwise
	 */
	
	public boolean isStopped()
	{
		return (fStateModel.isStopped());
	}
	
	
	/**
	 *  Sets this Component to the exception state, due to the given Exception. 
	 *  This Component is first stopped, and then enters the exception state.
	 *  
	 *  @param exception The Exception resulting in the exception state
	 */
	
	public void declareException(Exception exception)
	{
		if (sLogger.isLoggable(Level.SEVERE))
		{
			String message = getFullyQualifiedName() + 
				" encountered Exception: stopping and entering exception state";
			
			sLogger.logp
				(Level.SEVERE, CLASS_NAME, "declareException", message, exception);
		}
		
		stop();
		
		fStateModel.declareException(exception);
	}
	
	
	/**
	 *  Clears the current Exception (if any) from this Component.
	 *  
	 */
	
	public void clearException()
	{
		fStateModel.clearException();
	}
	
	
	/**
	 *  Returns true if this Component is in an exception state, false otherwise.
	 *
	 *  @return True if this Component is in an exception state, false otherwise
	 */
	
	public boolean isException()
	{
		return (fStateModel.isException());
	}
	
	
	/**
	 *  If this Component is in an exception state, this method will return the 
	 *  Exception that caused it. Otherwise, it returns null.
	 *  
	 *  @return The Exception that caused the exception state
	 */
	
	public Exception getException()
	{
		return (fStateModel.getException());
	}
	
	
	/**
	 *  Causes this Component to immediately cease operation and release 
	 *  any allocated resources. A killed Component cannot subsequently be 
	 *  started or otherwise reused.
	 * 
	 */
	public void kill()
	{
		fStateModel.kill();
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = getFullyQualifiedName() + " has been killed";
			
			sLogger.logp(Level.FINE, CLASS_NAME, "kill", message);
		}
	}

	
	/**
	 *  Returns true if this Component is killed, false otherwise.
	 *
	 *  @return True if this Component is killed, false otherwise
	 */
	public boolean isKilled()
	{
		return (fStateModel.isKilled());
	}
	
	
	/**
	 * Adds the given StateChangeListener as a listener for changes in 
	 * the ActivityState of this Component.
	 *
	 * @param listener A StateChangeListener
	 */
	public void addStateListener(PropertyChangeListener listener)
		throws IllegalArgumentException
	{
		if (fStateModel == null)
		{
			fStateModel = new DefaultActivityStateModel();
		}
		
		fStateModel.addStateListener(listener);
	}


	/**
	 *	Returns the set of StateChangeListeners on this Component as an 
	 *  array of PropertyChangeListeners.
	 *
	 *  @return the Set of StateChangeListeners on this Component as an 
	 *  	array of PropertyChangeListeners
	 */
	public PropertyChangeListener[] getStateListeners()
	{
		return (fStateModel.getStateListeners());
	}
	
	
	/**
	 * Removes the given StateChangeListener as a listener for changes in 
	 * the ActivityState of this Component.
	 *
	 * @param listener A StateChangeListener
	 */
	public void removeStateListener(PropertyChangeListener listener)
		throws IllegalArgumentException
	{
		fStateModel.removeStateListener(listener);
	}


	/**
	 *  Returns a String representation of this Component.
	 * 
	 *  @return A String representation of this Component
	 */
	public String toString()
	{
		String stringRep = getFullyQualifiedName() + " [" + getClass() + 
			"]\nState: " + fStateModel;
		
		return (stringRep);
	}
}
