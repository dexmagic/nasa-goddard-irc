//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.commons.types.queues;


/**
 * Defines a class that can process queue entries.  Currently used with
 * Dequeuer.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/19 19:03:33 $
 * @author	smaher
 */

public interface QueueProcessor
{
    void processQueueEntry(Object queueEntry);
}

//--- Development History  ---------------------------------------------------
//
//$Log: QueueProcessor.java,v $
//Revision 1.1  2004/12/19 19:03:33  smaher_cvs
//Used to decouple queue processing threads from the queue consumers.
//
//Revision 1.2  2004/12/10 22:04:43  smaher_cvs
//Made public so I could unit test it.
//
//Revision 1.1  2004/11/12 20:01:39  smaher_cvs
//*** empty log message ***
//