//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/HasBasisRequests.java,v 1.3 2006/01/02 03:50:16 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: HasBasisRequests.java,v $
//	Revision 1.3  2006/01/02 03:50:16  tames
//	Updated to use typed BasisRequest arrays instead of untyped collections.
//	
//	Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.3  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.2  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.1  2004/06/11 17:27:56  chostetter_cvs
//	Further Input-related work
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

package gov.nasa.gsfc.irc.data;

import java.util.Collection;


/**
 *  Defines the interface that all Objects having a Set of BasisRequests must 
 *  implement.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/02 03:50:16 $
 *  @author	Carl F. Hostetter
**/

public interface HasBasisRequests
{
	/**
	 * Returns an array of BasisRequests currently managed by this Input.
	 * 
	 * @return The array of BasisRequests currently managed by this Input
	 */
	public BasisRequest[] getBasisRequests();
	
	
	/**
	 *  Returns the Set of BasisRequests associated with this Object that 
	 *  are made on the BasisBundle specified by the given BasisBundleId.
	 *  
	 *  @param basisBundleId The BasisBundleId of the BasisBundle on which 
	 * 		the BasisRequests to get are made
	 *  @return The Set of BasisRequests associated with this Object that 
	 *  		are made on the BasisBundle specified by the given BasisBundleId
	 */
	 
	public Collection getBasisRequests(BasisBundleId basisBundleId);
}
