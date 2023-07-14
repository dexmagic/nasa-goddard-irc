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

package gov.nasa.gsfc.commons.processing.progress;


/**
 * A source of ProgressEvents to notify others about the progress 
 * of some processing.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/06 21:01:41 $
 * @author	tames
 **/
public interface HasProgress
{
	/**
	 * Adds the given ProgressListener to this Object as a 
	 * listener for changes in the progress of this Object.
	 *
	 * @param listener A ProgressListener
	 */
	public void addProgressListener(ProgressListener listener);


	/**
	 * Returns the set of ProgressListeners on this Object as an 
	 * array of ProgressListeners.
	 *
	 * @return the Set of ProgressListeners on this Object as an array.
	 */	
	public ProgressListener[] getProgressListeners();


	/**
	 * Removes the given ProgressListener from this Object as a  
	 * listener for changes in the progress of this Object.
	 *
	 * @param listener A ProgressListener
	 */
	public void removeProgressListener(ProgressListener listener);
}


//--- Development History  ---------------------------------------------------
//
//  $Log: HasProgress.java,v $
//  Revision 1.1  2005/04/06 21:01:41  tames_cvs
//  Initial Version
//
//