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

import java.beans.PropertyChangeListener;

import gov.nasa.gsfc.commons.properties.state.DefaultStateModel;
import gov.nasa.gsfc.commons.properties.state.State;


/**
 * This Activity State Model maintains a ActivityState for a custom Object 
 * with three primary states: Stopped, Started, and Killed. The Stopped state
 * indicates the Object is ready to be started for the first time, restarted 
 * or killed. Stopping a started Object typically releases any resources and
 * enters a Stopped state.
 * The Started state indicates the Object has been started and can be stopped 
 * or killed. The Killed state is an end state and indicates the Object has 
 * been killed. The state of a killed Object cannot be changed. 
 * <p>
 * The Started and Stopped states are
 * composite states with several substates. Users can optionally use the 
 * substates for higher fidelity if the Object supports them.
 * <p>
 * Stoppped has three substates: Ready (the default stopped state), Exception, 
 * and Finished. The Ready state indicates the Object is ready to be started or
 * restarted.
 * The Exception state indicates an exception has occured in a Started Object.
 * The exception can be queried and cleared. The Finished state indicates the
 * Object has completed the task. An Object can be restarted from a finished
 * state.
 * <p>
 * Started also has three substates: Active (the default started state), Paused,
 * and Waiting. The Active state indicates the Object is Started and actively
 * performing some function or task. The Waiting state indicates the Object is
 * started but waiting for some external or internal event to happen before 
 * becoming active again. The Paused state indicates the Object is Paused waiting
 * for an external event (typically from the user) to resume the Object. When
 * resumed the Object should continue from where it was paused. Pausing is not 
 * equivalent to a stop and then a start.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center,
 * Code 580 for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/04/18 18:53:58 $
 * @author Carl F. Hostetter
 * @author Troy Ames
 */
public class DefaultActivityStateModel extends DefaultStateModel 
	implements ActivityStateModel
{	
	Exception fException;
	
	
	/**
	 * Sets the State of this ActivityStateModel to the given State, unless this
	 * ActivityStateModel has been killed.
	 * 
	 * @param state The new State of this ActivityStateModel
	 */
	public void setState(State state)
	{
		if (! isKilled())
		{
			super.setState(state);
		}
	}
	
	/**
	 * Sets this ActivityStateModel to started and substate active, unless it 
	 * has been killed or is already started.
	 *  
	 */
	public void start()
	{
		if (! isKilled() && ! isStarted())
		{
			setState(ActivityState.ACTIVE);
		}
	}
	
	/**
	 * Returns true if this ActivityStateModel is currently started, false
	 * otherwise. An Object is also considered started if it is either active or
	 * waiting.
	 * 
	 * @return True if this ActivityStateModel is currently started, false
	 *         otherwise
	 */
	public boolean isStarted()
	{
		boolean result = false;
		
		State state = getState();
		
		if ((state == ActivityState.STARTED) || 
			(state == ActivityState.ACTIVE) || 
			(state == ActivityState.PAUSED) || 
			(state == ActivityState.WAITING))
		{
			result = true;
		}
		
		return (result);
	}
	
	/**
	 * Sets this ActivityStateModel to active, if it is currently started.
	 *  
	 */
	public void declareActive()
	{
		if (isStarted())
		{
			setState(ActivityState.ACTIVE);
		}
	}
	
	/**
	 * Returns true if this ActivityStateModel is active, false otherwise.
	 *
	 * @return True if this ActivityStateModel is active, false otherwise
	 **/
	public boolean isActive()
	{
		return (getState() == ActivityState.ACTIVE);
	}
	
	/**
	 * Sets this ActivityStateModel to waiting, if it is currently active.
	 *  
	 */
	public void declareWaiting()
	{
		if (isStarted())
		{
			setState(ActivityState.WAITING);
		}
	}
	
	/**
	 * Returns true if this ActivityStateModel is waiting, false otherwise.
	 *
	 * @return True if this ActivityStateModel is waiting, false otherwise
	 **/
	public boolean isWaiting()
	{
		return (getState() == ActivityState.WAITING);
	}
	
	/**
	 * Pauses this Object if it is currently started.
	 */	 
	public void pause()
	{
		if (isStarted())
		{
			setState(ActivityState.PAUSED);
		}
	}
		
	/**
	 * Returns true if this Object is paused, false otherwise. 
	 *
	 * @return True if this Object is paused, false otherwise
	 */
	public boolean isPaused()
	{
		return (getState() == ActivityState.PAUSED);
	}

	/**
	 * Resume this Object if it is currently paused.
	 */	
	public void resume()
	{
		if (getState() == ActivityState.PAUSED)
		{
			setState(ActivityState.ACTIVE);
		}
	}

	/**
	 * Sets this ActivityStateModel to stopped and ready, if it is currently 
	 * started.
	 *  
	 */
	public void stop()
	{
		if (isStarted())
		{
			setState(ActivityState.READY);
		}
	}
	
	/**
	 * Returns true if this ActivityStateModel is stopped, false otherwise.
	 * 
	 * @return True if this ActivityStateModel is stopped, false otherwise
	 */
	public boolean isStopped()
	{
		boolean result = false;
		
		State state = getState();
		
		if ((state == ActivityState.STOPPED) || 
			(state == ActivityState.READY) || 
			(state == ActivityState.EXCEPTION) || 
			(state == ActivityState.FINISHED))
		{
			result = true;
		}
		
		return (result);
	}
	
	/**
	 * Returns true if this Object is in a finished state, false otherwise.
	 *
	 * @return True if this Object is in a finished state, false otherwise
	 */
	public boolean isFinished()
	{
		return (getState() == ActivityState.FINISHED);
	}
	
	/**
	 * Sets this ActivityStateModel to finished if a valid state transition. 
	 * Callers can use the {@link HasFinish#isFinished() isFinished} method to 
	 * verify the result.
	 */
	public void declareFinished()
	{
		setState(ActivityState.FINISHED);
	}

	/**
	 * Sets this ActivityStateModel to the exception state, due to the given
	 * Exception. The ActivityStateModel is first stopped, and then put into the
	 * exception state.
	 * 
	 * @param exception The Exception resulting in the exception state
	 */
	public void declareException(Exception exception)
	{
		fException = exception;
		
		stop();
		
		setState(ActivityState.EXCEPTION);
	}
	
	/**
	 * Clears the current Exception (if any) from this ActivityStateModel.
	 *  
	 */
	public void clearException()
	{
		fException = null;
	}
	
	/**
	 * Returns true if this ActivityStateModel is in an exception state, false
	 * otherwise.
	 * 
	 * @return True if this ActivityStateModel is in an exception state, false
	 *         otherwise
	 */
	public boolean isException()
	{
		return (getState() == ActivityState.EXCEPTION);
	}
	
	/**
	 * If this ActivityStateModel is in an exception state, this method will
	 * return the Exception that caused it. Otherwise, it returns null.
	 * 
	 * @return The Exception that caused the exception state
	 */
	public Exception getException()
	{
		return (fException);
	}
	
	/**
	 * Sets this ActivityStateModel to killed. Objects in the killed state
	 * cannot further alter their States.
	 *  
	 */
	public void kill()
	{
		setState(ActivityState.KILLED);
		
		fStateListeners = null;
	}
	
	/**
	 * Returns true if this ActivityStateModel is killed, false otherwise.
	 * 
	 * @return True if this ActivityStateModel is killed, false otherwise
	 */
	public boolean isKilled()
	{
		return (getState() == ActivityState.KILLED);
	}
	
	/**
	 * Adds the given StateChangeListener as a listener for changes in 
	 * any State of this ActivityStateModel, unless it is killed.
	 *
	 * @param listener A StateChangeListener
	 **/
	public void addStateListener(PropertyChangeListener listener)
	{
		if (! isKilled())
		{
			super.addStateListener(listener);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultActivityStateModel.java,v $
//  Revision 1.4  2005/04/18 18:53:58  tames
//  Updated Javadoc.
//
//  Revision 1.3  2005/04/16 03:56:18  tames
//  Refactored activity package.
//
//  Revision 1.2  2005/01/27 21:38:02  chostetter_cvs
//  Implemented new exception state and default exception behavior for Objects having ActivityState
//
//  Revision 1.1  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//  Revision 1.2  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.5  2004/07/02 02:33:30  chostetter_cvs
//  Renamed DataRequest to BasisRequest
//
//  Revision 1.4  2004/06/29 22:46:13  chostetter_cvs
//  Fixed broken CVS-generated comments. Grrr.
//
//  Revision 1.3  2004/06/29 22:39:39  chostetter_cvs
//  Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//
//  Revision 1.2  2004/06/15 23:22:19  chostetter_cvs
//  Removed pause ActivityState
//
//  Revision 1.1  2004/06/15 20:04:03  chostetter_cvs
//  Added ActivityStateModel, use for stative Objects
//
