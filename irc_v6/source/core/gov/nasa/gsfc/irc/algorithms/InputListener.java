//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/InputListener.java,v 1.2 2005/09/13 22:27:47 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: InputListener.java,v $
//  Revision 1.2  2005/09/13 22:27:47  tames
//  Changes to reflect DataRequester refactoring
//
//  Revision 1.1  2004/07/21 14:26:15  chostetter_cvs
//  Various architectural and event-passing revisions
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

package gov.nasa.gsfc.irc.algorithms;

import gov.nasa.gsfc.irc.data.events.DataListener;


/**
 * An InputListener can listen for and receive InputEvents from Inputs.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/13 22:27:47 $
 * @author Carl F. Hostetter
 */

public interface InputListener extends DataListener
{

}
