//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ProcessingEvent.java,v $
//  Revision 1.3  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
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

package gov.nasa.gsfc.commons.processing;


/**
 * Event to notify others about the state of processing.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/16 15:18:31 $
 *  @author John Higinbotham
 */

public class ProcessingEvent 
{
	private Object fToken  = null; // Unique token provided by listener
	private int fState;			// ActivityState of processing

	//---Processor States
	public final static int PROCESSING_FINISHED = 1;

	/**
	 * Create a new ProcessingEvent. 
	 *  
	 * @param token Unique token provided by a listener. 
	 * @param state Processor state. 
	**/
	public ProcessingEvent(Object token, int state)
	{
		fToken = token;
		fState = state;
	}

	/**
	 * Get token. 
	 *  
	 * @return Token. 
	**/
	public Object getToken()
	{
		return fToken;
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
}
