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

/**
 * Pausable is an indicator that the implementor can be paused and resumed.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/16 03:56:18 $
 * @author 	Troy Ames
 */
public interface Pausable
{
	/**
	 * Pauses this Object.
	 */	 
	public void pause();
		
	/**
	 * Returns true if this Object is paused, false otherwise. 
	 *
	 * @return True if this Object is paused, false otherwise
	 */
	public boolean isPaused();

	/**
	 * Resume this Object.
	 */	
	public void resume();
}


//--- Development History  ---------------------------------------------------
//
//  $Log: Pausable.java,v $
//  Revision 1.1  2005/04/16 03:56:18  tames
//  Refactored activity package.
//
//