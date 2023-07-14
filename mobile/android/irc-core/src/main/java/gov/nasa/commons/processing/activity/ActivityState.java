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

import gov.nasa.gsfc.commons.properties.state.State;


/**
 * An ActivityState is a named indicator of activity state. This class has
 * static fields for all the states and substates represented by the 
 * {@link gov.nasa.gsfc.commons.processing.activity.HasActivityState HasActivityState}
 * interface.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center,
 * Code 580 for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/04/16 03:56:18 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */
public class ActivityState implements State
{	
	/** Started composite state **/
	public static final ActivityState STARTED = new ActivityState("started");

	/** Active substate of the Started state **/
	public static final ActivityState ACTIVE = new ActivityState("active");

	/** Waiting substate of the Started state **/
	public static final ActivityState WAITING = new ActivityState("waiting");

	/** Paused substate of the Started state **/
	public static final ActivityState PAUSED = new ActivityState("paused");

	/** Stopped composite state **/
	public static final ActivityState STOPPED = new ActivityState("stopped");
	
	/** Ready substate of the Stopped state **/
	public static final ActivityState READY = new ActivityState("ready");
	
	/** Finished substate of the Stopped state **/
	public static final ActivityState FINISHED = new ActivityState("finished");
	
	/** Exception substate of the Stopped state **/
	public static final ActivityState EXCEPTION = new ActivityState("exception");

	/** Killed state **/
	public static final ActivityState KILLED = new ActivityState("killed");

	/** The name of this State. **/
	private String fName = "initial";

	/**
	 * Default constructor of a ActivityState.
	 *
	**/
	protected ActivityState()
	{
		super();
	}
	
	/**
	 * Constructs a new ActivityState having the given name.
	 *
	 * @param name The name of the new ActivityState
	**/
	
	protected ActivityState(String name)
	{
		fName = name;
	}

	/**
	 * Returns the name of this State.
	 *
	 * @return The name of this State
	**/
	public String getName()
	{
		return (fName);
	}
	
	/**
	 * Returns the String representation of this State.
	 *
	 * @return The String representation of this State
	**/
	public String toString()
	{
		return (fName);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ActivityState.java,v $
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
