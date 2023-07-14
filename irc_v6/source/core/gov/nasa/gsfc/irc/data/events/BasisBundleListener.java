//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/BasisBundleListener.java,v 1.3 2005/07/13 20:09:09 tames Exp $
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


/**
 * A BasisBundleListener can listen for and receive BasisBundleEvents from a 
 * BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/07/13 20:09:09 $
 * @author Carl F. Hostetter
 */

public interface BasisBundleListener
{
	/**
	 * Causes this BasisBundleListener to receive the given BasisBundleEvent.
	 * 
	 * @param event A BasisBundleEvent
	 */
	
	public void receiveBasisBundleEvent(BasisBundleEvent event);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisBundleListener.java,v $
//  Revision 1.3  2005/07/13 20:09:09  tames
//  File header change only.
//
//  Revision 1.2  2005/04/04 15:40:59  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.1  2004/07/21 14:21:41  chostetter_cvs
//  Moved into subpackage
//
//  Revision 1.2  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
