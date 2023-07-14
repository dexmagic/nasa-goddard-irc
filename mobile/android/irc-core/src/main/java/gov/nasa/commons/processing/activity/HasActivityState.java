//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/processing/activity/HasActivityState.java,v 1.3 2005/04/16 03:56:18 tames Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//  This class requires JDK 1.1 or later.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  * Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//  * The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//  * Altered versions of this software must be plainly marked as such.
//  * This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.processing.activity;

import gov.nasa.gsfc.commons.properties.state.HasState;


/**
 * This interface defines methods for states applicable to many components, tasks, 
 * and processes such as started, stopped, and killed and their substates. 
 * Changes in state can be listened for.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/16 03:56:18 $
 * @author Troy Ames
 */
public interface HasActivityState extends 
	HasState, Startable, Pausable, HasException, HasActive, HasFinish
{
}

//--- Development History  ---------------------------------------------------
//
//  $Log: HasActivityState.java,v $
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
//  Revision 1.3  2004/06/15 23:22:19  chostetter_cvs
//  Removed pause ActivityState
//
//  Revision 1.2  2004/06/15 20:04:03  chostetter_cvs
//  Added ActivityStateModel, use for stative Objects
//
//  Revision 1.1  2004/06/11 17:26:21  chostetter_cvs
//  Moved out from IRC package
//
//  Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//  Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//
//
