//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/BasisRequesterFactory.java,v 1.1 2005/08/26 22:09:50 tames_cvs Exp $
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

package gov.nasa.gsfc.irc.data;

/**
 *  A BasisRequesterFactory creates and configures new instances of 
 *  BasisRequesters upon request.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2005/08/26 22:09:50 $
 *  @author Troy Ames
 */
public interface BasisRequesterFactory
{
	/**
	 *	Creates and returns a new BasisRequester for the given BasisRequest.
	 *
	 *	@param request The BasisRequest that this requester must satisfy
	 *  @return A BasisRequester
	 */
	public BasisRequester createBasisRequester(BasisRequest request);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: BasisRequesterFactory.java,v $
//	Revision 1.1  2005/08/26 22:09:50  tames_cvs
//	Initial implementation.
//	
//		  
