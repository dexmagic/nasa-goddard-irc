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

package gov.nasa.gsfc.irc.components;

import gov.nasa.gsfc.commons.processing.activity.HasActive;
import gov.nasa.gsfc.commons.processing.activity.HasException;
import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.properties.state.HasState;

/**
 * Defines the interface for setting, quering, and listening for component
 * states. Although not required by this interface, components should consider
 * also implementing the 
 * {@link gov.nasa.gsfc.commons.processing.activity.Pausable Pausable}, 
 * {@link gov.nasa.gsfc.commons.processing.activity.HasFinish HasFinish}, and
 * {@link gov.nasa.gsfc.commons.processing.progress.HasProgress} 
 * interfaces if applicable.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/16 04:03:55 $
 * @author 	Troy Ames
 */
public interface HasComponentState extends 
		HasState, Startable, HasException, HasActive
{
}


//--- Development History  ---------------------------------------------------
//
//  $Log: HasComponentState.java,v $
//  Revision 1.1  2005/04/16 04:03:55  tames
//  Changes to reflect refactored state and activity packages.
//
//