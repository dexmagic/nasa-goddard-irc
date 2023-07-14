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

package gov.nasa.gsfc.commons.processing.tasks;

import gov.nasa.gsfc.commons.processing.activity.HasActive;
import gov.nasa.gsfc.commons.processing.activity.HasException;
import gov.nasa.gsfc.commons.processing.activity.HasFinish;
import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.properties.state.HasState;
import gov.nasa.gsfc.commons.types.namespaces.HasName;

/**
 * A task is a process that can be started by calling the 
 * <code>start</code> method. A task can 
 * optionally perform all of it's functionality in the <code>start</code>
 * method or trigger an internal thread for processing asynchronously. 
 * In either case a task will not be considered finished and discarded 
 * until it is in the finished state. 
 * <P>
 * User's may call the <code>stop</code> method to halt the task process or 
 * <code>kill</code> to terminate the process. Tasks should document what 
 * the meaning of stop is.
 * <P>
 * Although not required by this interface a Task should implement the 
 * {@link gov.nasa.gsfc.commons.progress.HasProgress HasProgress} and
 * {@link gov.nasa.gsfc.commons.processing.activity.Pausable Pausable} 
 * interfaces if applicable.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/16 03:57:56 $
 * @author	tames
 **/
public interface Task extends 
	Startable, HasFinish, HasException, HasActive, HasState, HasName
{
}


//--- Development History  ---------------------------------------------------
//
//  $Log: Task.java,v $
//  Revision 1.2  2005/04/16 03:57:56  tames
//  Changes to reflect activity package refactoring.
//
//  Revision 1.1  2005/04/06 21:02:18  tames_cvs
//  Initial Version
//
//