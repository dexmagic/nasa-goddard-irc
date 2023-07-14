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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.commons.numerics.types.Amount;

/**
 *  A BasisRequesterFactory creates and configures new instances of 
 *  BasisRequesters upon request.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/02 18:30:31 $
 * @author	tjames
 **/
public class DefaultBasisRequesterFactory implements BasisRequesterFactory
{

	/**
	 * Creates and returns a new BasisRequester for the given BasisRequest. If
	 * the request is an instance of a {@link HistoryBasisRequest} then a
	 * {@link HistoryBasisRequester} will be returned otherwise a
	 * {@link DefaultBasisRequester} will be returned.
	 * 
	 * @param request The BasisRequest that this requester must satisfy
	 * @return A BasisRequester
	 */
	public BasisRequester createBasisRequester(BasisRequest request)
	{
		BasisRequester requester = null;
		Amount requestAmount = request.getRequestAmount();
		BasisRequestAmount basisRequestAmount = null;
		
		// handle for compatibility
		if (requestAmount instanceof BasisRequestAmount)
		{
			basisRequestAmount = (BasisRequestAmount) requestAmount;
		}
		
		if (request instanceof HistoryBasisRequest)
		{
			requester = new HistoryBasisRequester(request);
			System.out.println("Created a HistoryBasisRequester");
		}
		else if (basisRequestAmount != null 
				&& (basisRequestAmount.getFinalSkip().getAmount() > 0
						|| basisRequestAmount.getInitialSkip().getAmount() > 0))
		{
			requester = new IntervalBasisRequester(request);
			System.out.println("Created a IntervalBasisRequester");
		}
		else
		{
			requester = new DefaultBasisRequester(request);
			System.out.println("Created a DefaultBasisRequester");
		}
		
		return requester;	
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultBasisRequesterFactory.java,v $
//  Revision 1.5  2006/01/02 18:30:31  tames
//  Updated to reflect depricated BasisRequestAmount class. Uses Amount instead.
//
//  Revision 1.4  2005/09/20 18:41:14  tames
//  Added print debug lines.
//
//  Revision 1.3  2005/09/19 16:44:01  tames_cvs
//  Fixed potential null pointer exception if the request amount is null.
//
//  Revision 1.2  2005/09/14 21:48:44  tames
//  Added support for the IntervalBasisRequester.
//
//  Revision 1.1  2005/08/26 22:10:04  tames_cvs
//  Initial implementation.
//
//