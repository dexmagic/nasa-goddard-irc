//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/properties/state/HasState.java,v 1.2 2005/04/16 03:58:58 tames Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
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
//  * Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//  * The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//  * Altered versions of this software must be plainly marked as such.
//  * This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.properties.state;

import java.beans.PropertyChangeListener;


/**
 * Objects that have State can be queried for their State, and changes in 
 * their State can be listened for.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/16 03:58:58 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */
public interface HasState
{
	/** Property name used in the State PropertyChangeEvent */
	public static final String STATE_PROPERTY_NAME = "State";
	
	/**
	 * Returns the current State of this Object.
	 *
	 * @return The current State of this Object
	 */
	public State getState();
	
	/**
	 * Adds the given PropertyChangeListener to this Object as a 
	 * listener for changes in the state of this Object.
	 *
	 * @param listener A PropertyChangeListener
	 */
	public void addStateListener(PropertyChangeListener listener);

	/**
	 * Returns the set of PropertyChangeListeners on this Object as an 
	 * array of PropertyChangeListeners.
	 *
	 * @return the Set of PropertyChangeListeners on this Object as an 
	 *  	array of PropertyChangeListeners
	 */
	public PropertyChangeListener[] getStateListeners();

	/**
	 * Removes the given PropertyChangeListener from this Object as a  
	 * listener for changes in the state of this Object.
	 *
	 * @param listener A PropertyChangeListener
	 */
	public void removeStateListener(PropertyChangeListener listener);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: HasState.java,v $
//  Revision 1.2  2005/04/16 03:58:58  tames
//  Refactored state package
//
//  Revision 1.1  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//
