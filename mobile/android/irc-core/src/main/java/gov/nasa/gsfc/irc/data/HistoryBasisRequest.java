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

import java.util.Set;

import gov.nasa.gsfc.commons.numerics.types.Amount;

/**
 * A HistoryBasisRequest defines and represents a request for some amount of
 * data on some subset (possibly all) of the DataBuffers of a BasisBundle.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/23 17:59:51 $
 * @author tjames
 */
public class HistoryBasisRequest extends BasisRequest
{
	private double fUpdateInterval = 1.0;
	
	/**
	 * Constructs a HistoryBasisRequest for the given Amount of
	 * data, from the Set of DataBuffers specified by the given Set of
	 * DataBuffer names, all of which are members of the BasisBundle specified
	 * by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferNames The Set of names of the subset of DataBuffers of
	 *            the specified BasisBundle on which the new BasisRequest is
	 *            made
	 * @param amount A request Amount defining the amount of data from the
	 *            specified BasisBundle that the new BasisRequest will request
	 */
	public HistoryBasisRequest(BasisBundleId basisBundleId,
			Set dataBufferNames, Amount amount)
	{
		super(basisBundleId, dataBufferNames, amount);
	}

	/**
	 * Constructs a HistoryBasisRequest for the given Amount of
	 * data, from the DataBuffer that has the given name, which is a member of
	 * the BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferName The name of the DataBuffer of the specified
	 *            BasisBundle on which the new BasisRequest is made
	 * @param amount A request Amount defining the amount of data from the
	 *            specified BasisBundle that the new BasisRequest will request
	 */
	public HistoryBasisRequest(BasisBundleId basisBundleId,
			String dataBufferName, Amount amount)
	{
		super(basisBundleId, dataBufferName, amount);
	}

	/**
	 * Constructs a HistoryBasisRequest for all available data from the Set of
	 * DataBuffers specified by the given Set of DataBuffer names, which are all
	 * members of the BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferNames The Set of names of the subset of DataBuffers of
	 *            the specified BasisBundle on which the new BasisRequest is
	 *            made
	 */
	public HistoryBasisRequest(BasisBundleId basisBundleId, Set dataBufferNames)
	{
		super(basisBundleId, dataBufferNames);
	}

	/**
	 * Constructs a HistoryBasisRequest for all available data from the
	 * DataBuffer having the given name that is a member of the BasisBundle
	 * specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferName The name of the DataBuffer of the specified
	 *            BasisBundle on which the new BasisRequest is made
	 */
	public HistoryBasisRequest(BasisBundleId basisBundleId, String dataBufferName)
	{
		super(basisBundleId, dataBufferName);
	}

	/**
	 * Constructs a HistoryBasisRequest for the given BasisRequestAmount of data
	 * from all of the DataBuffers of the BasisBundle specified by the given
	 * BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param basisInterval A request Amount defining the amount of data
	 *            from the specified BasisBundle that the new BasisRequest will
	 *            request
	 */
	public HistoryBasisRequest(BasisBundleId basisBundleId, Amount basisInterval)
	{
		super(basisBundleId, basisInterval);
	}

	/**
	 * Constructs a HistoryBasisRequest for all available data from all of the
	 * DataBuffers of the specified BasisBundle to the current set of data
	 * requests on this DataRequester.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which the
	 *            requested data is to be taken
	 */
	public HistoryBasisRequest(BasisBundleId basisBundleId)
	{
		super(basisBundleId);
	}

	/**
	 * Constructs a HistoryBasisRequest template.
	 */
	public HistoryBasisRequest()
	{
		super();
	}

	/**
	 * Returns the update interval.
	 * 
	 * @return Returns the update interval.
	 */
	public double getUpdateInterval()
	{
		return fUpdateInterval;
	}

	/**
	 * Sets the update interval
	 * @param interval The update Interval to set.
	 */
	public void setUpdateInterval(double interval)
	{
		fUpdateInterval = interval;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: HistoryBasisRequest.java,v $
//  Revision 1.5  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2006/01/02 18:30:31  tames
//  Updated to reflect depricated BasisRequestAmount class. Uses Amount instead.
//
//  Revision 1.3  2006/01/02 03:50:45  tames
//  Added default constructor to support BasisRequest property editors.
//
//  Revision 1.2  2005/09/09 21:34:18  tames
//  BasisRequester framework refactoring.
//
//  Revision 1.1  2005/08/26 22:10:49  tames_cvs
//  Partial initial implementation.
//
//