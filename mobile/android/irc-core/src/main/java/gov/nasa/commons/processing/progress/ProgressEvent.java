//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ProgressEvent.java,v $
//  Revision 1.3  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:01  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.processing.progress;


/**
 * Event to notify others about the progress of some processing.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/16 15:18:31 $
 *  @author John Higinbotham
 */

public class ProgressEvent 
{
	//---Processor States
	public final static int NOMINAL = 0;
	public final static int ERROR   = 1;

	//---Vars	
	private int fState;			 // ActivityState of processor
	private float fPercentComplete; // Percent complete 
	private String fMessage = null; // Message

	/**
	 * Create a new ProgressEvent. 
	 *  
	 * @param percentComplete  Percent complete.
	 * @param state	 Processor state. 
	**/
	public ProgressEvent(float percentComplete, int state, String message)
	{
		fPercentComplete = percentComplete; 
		fState		   = state;
		fMessage		 = message;
	}

	/**
	 * Get percent complete.
	 *  
	 * @return float Percent complete. 
	**/
	public float getPercentComplete()
	{
		return fPercentComplete;
	}

	/**
	 * Get processor state. 
	 *  
	 * @return Processor state.
	**/
	public int getState()
	{
		return fState;
	}

	/**
	 * Get message. 
	 *  
	 * @return String message. 
	**/
	public String getMessage()
	{
		return fMessage;
	}
}
