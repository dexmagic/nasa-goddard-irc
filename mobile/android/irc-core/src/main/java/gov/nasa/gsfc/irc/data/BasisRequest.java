//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/BasisRequest.java,v 1.24 2006/05/23 15:58:19 smaher_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisRequest.java,v $
//	Revision 1.24  2006/05/23 15:58:19  smaher_cvs
//	Added delimiters for regular expressions.
//	
//	Revision 1.23  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.22  2006/01/02 18:30:31  tames
//	Updated to reflect depricated BasisRequestAmount class. Uses Amount instead.
//	
//	Revision 1.21  2006/01/02 03:47:25  tames
//	Added default constructor to support BasisRequest property editors.
//	
//	Revision 1.20  2005/04/12 20:36:06  chostetter_cvs
//	Added option to promote pending data to satisfying data on stop
//	
//	Revision 1.19  2005/03/15 00:36:02  chostetter_cvs
//	Implemented covertible Units compliments of jscience.org packages
//	
//	Revision 1.18  2005/02/03 01:05:47  chostetter_cvs
//	Fixed inconsistent use of null to indicate no input data buffer filtering
//	
//	Revision 1.17  2005/01/31 21:35:30  chostetter_cvs
//	Fix for data request sequencing, documentation
//	
//	Revision 1.16  2005/01/28 23:57:34  chostetter_cvs
//	Added ability to integrate DataBuffers and requests, renamed average value
//	
//	Revision 1.15  2004/11/08 18:37:52  chostetter_cvs
//	Basis sequence boundary behavior now configurable
//	
//	Revision 1.14  2004/09/15 15:10:09  chostetter_cvs
//	Tweaks to streamline logic
//	
//	Revision 1.13  2004/09/14 22:55:08  chostetter_cvs
//	Fixed setting flag indicating selection of data
//	
//	Revision 1.12  2004/09/14 22:45:10  chostetter_cvs
//	Fixed setting flag indicating selection of all available data
//	
//	Revision 1.11  2004/09/13 18:45:11  chostetter_cvs
//	Added ability to set DataBuffer names
//	
//	Revision 1.10  2004/09/02 19:39:57  chostetter_cvs
//	Initial data-description redesign work
//	
//	Revision 1.9  2004/08/19 16:59:32  chostetter_cvs
//	Fixed javadoc with regard to DataBuffer names
//	
//	Revision 1.8  2004/07/24 02:46:11  chostetter_cvs
//	Added statistics calculations to DataBuffers, renamed some classes
//	
//	Revision 1.7  2004/07/22 21:29:47  chostetter_cvs
//	BasisBundle name, access by name changes
//	
//	Revision 1.6  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.5  2004/07/18 05:14:02  chostetter_cvs
//	Refactoring of data classes
//	
//	Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.3  2004/07/11 18:05:41  chostetter_cvs
//	More data request work
//	
//	Revision 1.2  2004/07/09 22:29:11  chostetter_cvs
//	Extensive testing of Input/Output interaction, supports simple BasisRequests
//	
//	Revision 1.1  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.2  2004/06/14 21:23:50  chostetter_cvs
//	More BasisRequest work
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import gov.nasa.gsfc.commons.numerics.types.Amount;

/**
 *  A BasisRequest defines and represents a request for some amount (possibly 
 *  open-ended) of data on some subset (possibly all) of the DataBuffers of a 
 *  BasisBundle.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/05/23 15:58:19 $
 *  @author	Carl F. Hostetter
 **/

public class BasisRequest
{
	/**
	 * Used to indicate the beginning of a regular expression
	 * that is used to specify BasisRequest data buffers.
	 */
	public final static String REGEX_DELIMETER_START = "{";

	/**
	 * Used to indicate the end of a regular expression
	 * that is used to specify BasisRequest data buffers.
	 */	
	public final static String REGEX_DELIMETER_END = "}";
	
	private BasisBundleId fBasisBundleId;
	private Set fDataBufferNames;
	private Amount fRequestAmount;
	private int fDownsamplingRate = 0;
	private boolean fIntegrate = false;
	private boolean fIgnoreBasisSequenceBoundaries = false;
	private boolean fIncludePendingDataOnStop = false;

	/**
	 * Constructs a BasisRequest for the given Amount of data, from
	 * the Set of DataBuffers specified by the given Set of DataBuffer names,
	 * all of which are members of the BasisBundle specified by the given
	 * BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferNames The Set of names of the subset of DataBuffers of
	 *            the specified BasisBundle on which the new BasisRequest is
	 *            made
	 * @param amount A r Amount defining the amount of data from the
	 *            specified BasisBundle that the new BasisRequest will request
	 */
	public BasisRequest(BasisBundleId basisBundleId, Set dataBufferNames,
			Amount amount)
	{
		if (basisBundleId != null)
		{
			fBasisBundleId = basisBundleId;

			setDataBufferNames(dataBufferNames);

			fRequestAmount = amount;
		}
		else
		{
			String message = "The BasisBundleId of a BasisRequest cannot be null";

			throw (new IllegalArgumentException(message));
		}
	}

	/**
	 * Constructs a BasisRequest for the given Amount of data, from
	 * the DataBuffer that has the given name, which is a member of the
	 * BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferName The name of the DataBuffer of the specified
	 *            BasisBundle on which the new BasisRequest is made
	 * @param amount A request Amount defining the amount of data from the
	 *            specified BasisBundle that the new BasisRequest will request
	 */
	public BasisRequest(BasisBundleId basisBundleId, String dataBufferName,
			Amount amount)
	{
		this(basisBundleId, amount);

		if (dataBufferName != null)
		{
			Set names = new HashSet();

			names.add(dataBufferName);

			setDataBufferNames(names);
		}
		else
		{
			setDataBufferNames(null);
		}
	}

	/**
	 * Constructs a BasisRequest for all available data from the Set of
	 * DataBuffers specified by the given Set of DataBuffer names, which are all
	 * members of the BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferNames The Set of names of the subset of DataBuffers of
	 *            the specified BasisBundle on which the new BasisRequest is
	 *            made
	 */
	public BasisRequest(BasisBundleId basisBundleId, Set dataBufferNames)
	{
		this(basisBundleId, dataBufferNames, null);
	}

	/**
	 * Constructs a BasisRequest for all available data from the DataBuffer
	 * having the given name that is a member of the BasisBundle specified by
	 * the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param dataBufferName The name of the DataBuffer of the specified
	 *            BasisBundle on which the new BasisRequest is made
	 */
	public BasisRequest(BasisBundleId basisBundleId, String dataBufferName)
	{
		this(basisBundleId, dataBufferName, null);
	}

	/**
	 * Constructs a BasisRequest for the given BasisRequest Amount of data from
	 * all of the DataBuffers of the BasisBundle specified by the given
	 * BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            new BasisRequest is made
	 * @param basisInterval A BasisRequestAmount defining the amount of data
	 *            from the specified BasisBundle that the new BasisRequest will
	 *            request
	 */
	public BasisRequest(BasisBundleId basisBundleId,Amount basisInterval)
	{
		this(basisBundleId, (Set) null, basisInterval);
	}

	/**
	 * Constructs a BasisRequest for all available data from all of the
	 * DataBuffers of the specified BasisBundle to the current set of data
	 * requests on this DataRequester.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which the
	 *            requested data is to be taken
	 */
	public BasisRequest(BasisBundleId basisBundleId)
	{
		this(basisBundleId, (Set) null, null);
	}

	/**
	 * Constructs a BasisRequest template.
	 */
	public BasisRequest()
	{
		super();
	}

	/**
	 * Sets the BasisBundleId of the BasisBundle on which this BasisRequest 
	 * is made.
	 * 
	 * @param id the BasisBundleId of the BasisBundle on which this request
	 * 		is made.
	 */
	public void setBasisBundleId(BasisBundleId id)
	{
		fBasisBundleId = id;
	}
	
	/**
	 * Returns the BasisBundleId of the BasisBundle on which this BasisRequest 
	 * is made.
	 * 
	 * @return The BasisBundleId of the BasisBundle on which this BasisRequest 
	 * 		is made
	 */
	public BasisBundleId getBasisBundleId()
	{
		return (fBasisBundleId);
	}

	/**
	 * Sets the BasisRequest Amount, defining the amount of data which this
	 * BasisRequest requests, to the given BasisRequest Amount. If the given
	 * BasisRequest Amount is null, this BasisRequest will select all available
	 * data.
	 * 
	 * @param interval The BasisRequest Amount defining the amount of data which
	 *            this BasisRequest requests
	 */
	public void setRequestAmount(Amount amount)
	{
		fRequestAmount = amount;
	}

	/**
	 * Returns the BasisRequest Amount defining the amount of data which this 
	 * BasisRequest requests. If this BasisRequest is configured to select all 
	 * available data, the result will be null.
	 * 
	 * @return The BasisRequest Amount defining the amount of data which this 
	 * 		BasisRequest makes
	 */
	public Amount getRequestAmount()
	{
		return (fRequestAmount);
	}

	/**
	 * Configures this BasisRequest to ignore basis sequence boundaries (i.e., to 
	 * fill requests across such boundaries).
	 * 
	 */
	public void ignoreBasisSequenceBoundaries()
	{
		fIgnoreBasisSequenceBoundaries = true;
	}

	/**
	 * Configures this BasisRequest to respect basis sequence boundaries (i.e., to 
	 * not fill requests across such boundaries).
	 */
	public void respectBasisSequenceBoundaries()
	{
		fIgnoreBasisSequenceBoundaries = false;
	}

	/**
	 * Returns true of this BasisRequest is configured to ignore basis sequence 
	 * boundaries (i.e., to fill requests across such boundaries), false 
	 * otherwise.
	 * 
	 * @return True of this BasisRequest is configured to ignore basis sequence 
	 * 		boundaries (i.e., to fill requests across such boundaries), false 
	 * 		otherwise
	 */
	public boolean ignoresBasisSequenceBoundaries()
	{
		return (fIgnoreBasisSequenceBoundaries);
	}

	/**
	 * Configures this BasisRequest to include any pending data it has collected 
	 * (i.e., data received but not yet sufficient to fulfill the request) at the 
	 * time it is stopped as satisfying data.
	 */
	public void includePendingDataOnStop()
	{
		fIncludePendingDataOnStop = true;
	}

	/**
	 * Configures this BasisRequest to ignore any pending data it has collected 
	 * (i.e., data received but not yet sufficient to fulfill the request) at the 
	 * time it is stopped.
	 */
	public void ignorePendingDataOnStop()
	{
		fIncludePendingDataOnStop = false;
	}

	/**
	 * Returns true of this BasisRequest is configured to include any pending data 
	 * it has collected (i.e., data received but not yet sufficient to fulfill the 
	 * request) at the time it is stopped as satisfying data, false otherwise.
	 * 
	 * @return True of this BasisRequest is configured to include any pending data 
	 * 		it has collected (i.e., data received but not yet sufficient to fulfill 
	 * 		the request) at the time it is stopped as satisfying data, false 
	 * 		otherwise
	 */
	public boolean includesPendingDataOnStop()
	{
		return (fIncludePendingDataOnStop);
	}

	/**
	 * Sets the downsampling rate of this BasisRequest to the given rate. To 
	 * indicate no downsampling, set this rate to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of this BasisRequest
	 */
	public void setDownsamplingRate(int downsamplingRate)
	{
		fDownsamplingRate = downsamplingRate;
	}

	/**
	 * Returns the current downsampling rate of this BasisRequest.
	 * 
	 * @return The downsampling rate of this BasisRequest
	 */
	public int getDownsamplingRate()
	{
		return (fDownsamplingRate);
	}

	/**
	 * Sets the Set of DataBuffer names specifying the subset of the DataBuffers of 
	 * the BasisBundle on which this BasisRequest is made to the given Set of 
	 * names. If the given Set is null, this BasisRequest will be configured to 
	 * select all DataBuffers.
	 * 
	 * @param dataBufferNames The Set of DataBuffer names specifying the subset of 
	 * 		the DataBuffers of the BasisBundle on which this BasisRequest is made
	 */
	public void setDataBufferNames(Set dataBufferNames)
	{
		if (dataBufferNames == null)
		{
			fDataBufferNames = null;
		}
		else
		{
			if (fDataBufferNames == null)
			{
				fDataBufferNames = new HashSet();
			}
			else
			{
				fDataBufferNames.clear();
			}

			fDataBufferNames.addAll(dataBufferNames);
		}
	}

	public void addDataBuffer(String dataBufferName)
	{
		if (fDataBufferNames == null)
		{
			fDataBufferNames = new HashSet();
		}

		fDataBufferNames.add(dataBufferName);
	}

	/**
	 * Returns the Set of DataBuffer names specifying the subset of the 
	 * DataBuffers of the BasisBundle on which this BasisRequest is made. 
	 * If this BasisRequest is configured to select all DataBuffers, the 
	 * result will be null.
	 * 
	 * @return The Set of DataBuffer names specifying the subset of the 
	 * 		DataBuffers of the BasisBundle on which this BasisRequest is 
	 * 		made
	 */
	public Set getDataBufferNames()
	{
		Set result = null;

		if (fDataBufferNames != null)
		{
			result = Collections.unmodifiableSet(fDataBufferNames);
		}

		return (result);
	}

	/**
	 * Returns true if this BasisRequest is configured to select all available 
	 * data (i.e., if no BasisRequestAmount is specified), false otherwise.
	 * 
	 * @return True if this BasisRequest is configured to select all available 
	 * 		data (i.e., if no BasisRequestAmount is specified), false otherwise
	 */
	public final boolean selectsAllAvailableData()
	{
		boolean result = fRequestAmount == null;

		return (result);
	}

	/**
	 * Returns true if this BasisRequest is configured to select all of the 
	 * DataBuffers of the BasisBundle on which it is made (i.e., if no 
	 * BasisBundleIds are specified), false otherwise.
	 * 
	 * @return True if this BasisRequest is configured to select all of the 
	 * 		DataBuffers of the BasisBundle on which it is made (i.e., if no 
	 * 		BasisBundleIds are specified), false otherwise
	 */
	public final boolean selectsAllDataBuffers()
	{
		boolean result = (fDataBufferNames == null);

		return (result);
	}

	/**
	 * Returns a String representation of this BasisRequest.
	 * 
	 * @return A String representation of this BasisRequest
	 */
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer("BasisRequest on "
				+ fBasisBundleId + ":");

		if (selectsAllDataBuffers())
		{
			stringRep.append("\nSelects all DataBuffers");
		}
		else
		{
			stringRep.append("\nSelected DataBuffers:");

			Iterator names = fDataBufferNames.iterator();

			while (names.hasNext())
			{
				String name = (String) names.next();

				stringRep.append("\n" + name);
			}
		}

		if (selectsAllAvailableData())
		{
			stringRep.append("\nSelects all available data");
		}
		else
		{
			stringRep.append("\n" + fRequestAmount);
		}

		if (fDownsamplingRate > 0)
		{
			stringRep.append("\nDownsampling rate: " + fDownsamplingRate);
		}

		if (fIntegrate)
		{
			stringRep.append("\nIntegrates requested data");
		}

		return (stringRep.toString());
	}
}
