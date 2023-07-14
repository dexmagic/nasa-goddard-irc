//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/processing/activity/Startable.java,v 1.2 2005/04/16 03:56:18 tames Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// 	$Log: Startable.java,v $
// 	Revision 1.2  2005/04/16 03:56:18  tames
// 	Refactored activity package.
// 	
// 	Revision 1.1  2004/08/23 13:50:24  tames
// 	Initial Version
// 	
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

/**
 *	Startable is an indicator that the implementor can be started, stopped
 *	or killed.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version $Date: 2005/04/16 03:56:18 $
 *	@author Troy Ames
 */
public interface Startable
{
	/**
	 * Starts this Object.
	 */	 
	public void start();
		
	/**
	 * Returns true if this Object is started, false otherwise. 
	 *
	 * @return True if this Object is started, false otherwise
	 */
	public boolean isStarted();

	/**
	 * Stops this Object.
	 */	
	public void stop();
	
	/**
	 * Returns true if this Object is stopped, false otherwise.
	 *
	 * @return True if this Object is stopped, false otherwise
	 */	
	public boolean isStopped();
	
	/**
	 * Kills this Object. Killed instances release all their resources and 
	 * cannot be started or subsequently reused.
	 */
	public void kill();
	
	/**
	 * Returns true if this Object has been killed, false otherwise. A killed 
	 * Object releases all of its resources and cannot be restarted or subsequently 
	 * reused.
	 *
	 * @return True if this Object has been killed, false otherwise
	 */	
	public boolean isKilled();
}
