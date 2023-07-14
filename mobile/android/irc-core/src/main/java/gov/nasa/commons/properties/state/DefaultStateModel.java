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

package gov.nasa.gsfc.commons.properties.state;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * A StateModel maintains a State and notifies listeners of state changes.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center,
 * Code 580 for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:55 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */
public class DefaultStateModel implements StateModel
{	
	private State fState = new State() 
	{ 
		public String getName()
		{ 
			return "default";
		}
		
		public String toString()
		{ 
			return (getName());
		}
	};
	
	protected transient PropertyChangeSupport fStateListeners = 
		new PropertyChangeSupport(this);
	
	/**
	 * Sets the State of this StateModel to the given State.
	 *
	 * @param state The new State of this StateModel
	 */
	public void setState(State state)
	{
		State oldState = fState;
		fState = state;

		fStateListeners.firePropertyChange
			(STATE_PROPERTY_NAME, oldState, state);
	}
	
	/**
	 * Returns the current State of this StateModel.
	 *
	 * @return The current State of this StateModel
	 */
	public State getState()
	{
		return (fState);
	}
	
	/**
	 * Adds the given StateChangeListener as a listener for changes in 
	 * any State of this StateModel.
	 *
	 * @param listener A StateChangeListener
	 **/
	public void addStateListener(PropertyChangeListener listener)
	{
		if (fStateListeners != null)
		{
			fStateListeners.addPropertyChangeListener(listener);
		}
	}

	/**
	 *	Returns the set of StateChangeListeners on this StateModel as an 
	 *  array of PropertyChangeListeners.
	 *
	 *  @return the Set of StateChangeListeners on this Object as an 
	 *  	array of PropertyChangeListeners
	 */
	public PropertyChangeListener[] getStateListeners()
	{
		PropertyChangeListener[] result = new PropertyChangeListener[0];
		
		if (fStateListeners != null)
		{
			result = fStateListeners.getPropertyChangeListeners();
		}
		
		return (result);
	}

	/**
	 * Removes the given StateChangeListener as a listener for changes in 
	 * any State of this StateModel.
	 *
	 * @param listener A StateChangeListener
	 **/
	public void removeStateListener(PropertyChangeListener listener)
	{
		if (fStateListeners != null)
		{
			fStateListeners.removePropertyChangeListener(listener);
		}
	}
	
	/**
	 *	Returns a String representation of this StateModel.
	 *
	 *  @return A String representation of this StateModel
	 */
	public String toString()
	{
		return (fState.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultStateModel.java,v $
//  Revision 1.4  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/04/16 03:58:58  tames
//  Refactored state package
//
//  Revision 1.2  2005/04/06 21:04:07  tames_cvs
//  Changed setState method to comply with property change guidelines.
//
//  Revision 1.1  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
