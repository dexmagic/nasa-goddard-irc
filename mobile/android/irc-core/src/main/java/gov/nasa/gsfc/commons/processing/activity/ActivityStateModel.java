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

package gov.nasa.gsfc.commons.processing.activity;

import gov.nasa.gsfc.commons.properties.state.HasState;
import gov.nasa.gsfc.commons.properties.state.StateModel;


/**
 * An ActivityStateModel maintains an activity state that is applicable to
 * many components, tasks, and processes. Implementations are responsible for 
 * enforcing state change policies and transitions. 
 * Users of an ActivityStateModel may
 * not need to use or expose all of the states this interface provides.
 * 
 * <p>Implementations of this interface define the valid states and substates as 
 * well as transitions between these states.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center,
 * Code 580 for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/04/16 03:56:18 $
 * @author Carl F. Hostetter
 * @author Troy Ames
 */
public interface ActivityStateModel extends 
	StateModel, HasState, HasActivityState
{	
	/**
	 * Sets this ActivityStateModel to active if a valid state transition. 
	 * Callers can use the {@link HasActive#isActive() isActive} method to 
	 * verify the result.
	 */	
	public void declareActive();	
	
	/**
	 * Sets this ActivityStateModel to waiting if a valid state transition. 
	 * Callers can use the {@link HasActive#isWaiting() isWaiting} method to 
	 * verify the result.
	 */
	public void declareWaiting();
		
	/**
	 * Sets this ActivityStateModel to the exception state, due to the given 
	 * Exception. 
	 *  
	 * @param exception The Exception resulting in the exception state
	 */
	public void declareException(Exception exception);
	
	
	/**
	 * Sets this ActivityStateModel to finished if a valid state transition. 
	 * Callers can use the {@link HasFinish#isFinished() isFinished} method to 
	 * verify the result.
	 */
	public void declareFinished();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ActivityStateModel.java,v $
//  Revision 1.3  2005/04/16 03:56:18  tames
//  Refactored activity package.
//
//  Revision 1.2  2005/01/27 21:38:02  chostetter_cvs
//  Implemented new exception state and default exception behavior for Objects having ActivityState
//
//  Revision 1.1  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.2  2004/06/15 23:22:19  chostetter_cvs
//  Removed pause ActivityState
//
//  Revision 1.1  2004/06/15 20:04:03  chostetter_cvs
//  Added ActivityStateModel, use for stative Objects
//
