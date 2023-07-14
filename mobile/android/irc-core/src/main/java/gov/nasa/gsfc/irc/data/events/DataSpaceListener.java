//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/DataSpaceListener.java,v 1.3 2006/01/23 17:59:54 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.data.events;

import gov.nasa.gsfc.commons.types.namespaces.MembershipListener;


/**
 * A DataSpaceListener can listen for and receive DataSpaceEvents from a 
 * DataSpace.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:54 $
 * @author Carl F. Hostetter
 */

public interface DataSpaceListener extends MembershipListener
{

}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpaceListener.java,v $
//  Revision 1.3  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/07/13 20:11:46  tames
//  File header change only.
//
//  Revision 1.1  2004/07/21 14:21:41  chostetter_cvs
//  Moved into subpackage
//
//  Revision 1.2  2004/07/16 00:55:26  chostetter_cvs
//  Tweaks
//
//  Revision 1.1  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
