// === File Prolog ============================================================
//
// $Header:
// /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/HistoryBasisRequester.java,v
// 1.1 2005/08/26 22:10:49 tames_cvs Exp $
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.data;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.ConversionException;
import org.jscience.physics.units.Converter;
import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.commons.system.memory.MemoryModel;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;

/**
 * A HistoryBasisRequester is a receiver of BasisBundleEvents, all from the same
 * BasisBundle, that is associated with exactly one BasisRequest on that
 * BasisBundle, and that knows how to satisfy its BasisRequest. This 
 * requester satisfies {@link HistoryBasisRequest HistoryBasisRequests}.
 * <p>
 * Note that a BasisRequester must be started before it will begin listening for
 * BasisBundleEvents and satisifying its associated BasisRequest.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/10 13:24:35 $
 * @author Troy Ames
 */
public class HistoryBasisRequester extends AbstractBasisRequester 
	implements BasisRequester
{
	/**
	 * Logger for this class
	 */
	private static final String CLASS_NAME = DefaultDataRequester.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private BasisRequest fBasisRequest = null;
	private HistoryBundle fHistoryBasisBundle = null;
	private BasisSet fPendingBasisSet = null;
	private MemoryModel fMemoryModel = null;

	private SatisfiedCache fSatisfiedRequest = new SatisfiedCache();

	private Unit fInputBasisUnit;

	private Unit fHistoryUnit;				// units of requested size
	private double fHistoryAmount = 0;		// requested size of history
	private int fHistorySamples = 100;		// size of history in samples
	private int fHistoryBundleCapacity = 200;
	private long fUpdateIntervalMs = 1000;	// interval in milliseconds
	private double fRequestedUpdateInterval = 0.5;	// interval in seconds
	private Unit fRequestedUpdateIntervalUnit = Amount.lookUpUnit("s");
	private long fLastUpdate = 0;
	private boolean fDownsamplingEnabled = false;

	private double fAmountKept = 0;
	private boolean fHistoryBundleInitialized = false;

	/**
	 * Constructs a new BasisRequester that will attempt to satisfy the given
	 * BasisRequest.
	 * 
	 * @param basisRequest A BasisRequest
	 */
	public HistoryBasisRequester(BasisRequest basisRequest)
	{
		super(basisRequest);
		setBasisRequest0(basisRequest);
	}

	/**
	 * Causes this BasisRequester to stop, if it is currently started. All
	 * pending data will be cleared.
	 */
	public synchronized void stop()
	{
		if (isStarted())
		{
			super.stop();

			// We need to release any currently queued input data
			clear();
		}
	}

	/**
	 * Causes this BasisRequester to immediately cease operation and release any
	 * allocated resources. A killed BasisRequester cannot subsequently be
	 * started or otherwise reused.
	 */
	public void kill()
	{
		if (!isKilled())
		{
			super.kill();

			// We need to release any currently queued input data
			clear();
			fSatisfiedRequest = null;
		}
	}

	/**
	 * Sets the BasisRequest to be satisfied by this BasisRequester to the given
	 * BasisRequest.
	 * 
	 * @param basisRequest A BasisRequest
	 * @throws IllegalArgumentException if the given BasisRequest is not valid
	 *             in some way
	 */
	public void setBasisRequest(BasisRequest basisRequest)
	{
		super.setBasisRequest(basisRequest);
		setBasisRequest0(basisRequest);
	}
	
	/*
	 * Sets the BasisRequest to be satisfied by this BasisRequester to 
	 * the given BasisRequest. Called from the constructor or the public 
	 * setBasisRequest method.
	 */
	private void setBasisRequest0(BasisRequest basisRequest)
	{
		super.setBasisRequest(basisRequest);

		fBasisRequest = basisRequest;
		fDownsamplingEnabled = isDownsamplingEnabled();

		BasisBundleDescriptor descriptor = getInputDescriptor();
		fInputBasisUnit = descriptor.getBasisBufferDescriptor().getUnit();

		Amount requestAmount = fBasisRequest.getRequestAmount();
		
		if (fBasisRequest instanceof HistoryBasisRequest)
		{
			// Get the update interval in seconds and convert it to ms
			fRequestedUpdateInterval = 
				((HistoryBasisRequest) fBasisRequest).getUpdateInterval();
			fUpdateIntervalMs = (long) (fRequestedUpdateInterval * 1000.0);
			
			// Convert the update interval to the BasisUnits if possible
			// This is needed to approximate how big the history buffer 
			// needs to be.
			if (fRequestedUpdateIntervalUnit != fInputBasisUnit)
			{
				Converter converter = null;
				
				try
				{
					converter = fRequestedUpdateIntervalUnit.getConverterTo(fInputBasisUnit);
				}
				catch (ConversionException e)
				{
					// Nothing to do here since a null converter will be handled
					// below.
				}

				if (converter != null)
				{
					fRequestedUpdateInterval = converter.convert(fRequestedUpdateInterval);
				}
				else
				{
					// The interval cannot be converted to BasisUnits
					fRequestedUpdateInterval = Double.NaN;
				}
			}
		}

		if (requestAmount != null)
		{
			fHistoryAmount = requestAmount.getAmount();
			fHistoryUnit = requestAmount.getUnit();

			if ((fHistoryAmount != 0) && (fHistoryUnit != null)
					&& (fHistoryUnit != Unit.ONE)
					&& (fHistoryUnit != fInputBasisUnit))
			{
				Converter converter = fHistoryUnit
						.getConverterTo(fInputBasisUnit);

				if (converter != null)
				{
					fHistoryAmount = converter.convert(fHistoryAmount);
				}
				else
				{
					String message = "Could not convert from request Unit "
							+ fHistoryUnit + " to input basis Unit "
							+ fInputBasisUnit + " in request " + basisRequest;

					if (sLogger.isLoggable(Level.SEVERE))
					{
						sLogger.logp(Level.SEVERE, CLASS_NAME,
							"setBasisRequest", message);
					}

					throw (new IllegalArgumentException(message));
				}
			}
		}
		else
		{
			fHistoryAmount = 0;
			fHistoryUnit = null;
		}

		if (sLogger.isLoggable(Level.FINE))
		{
			String message = "History units:" + fHistoryUnit
				+ " History amount:" + fHistoryAmount;

			sLogger.logp(Level.FINE, CLASS_NAME, "setBasisRequest0",
				message);
		}

		if (!basisRequest.selectsAllDataBuffers())
		{
			descriptor = descriptor.filterDescriptor(basisRequest
					.getDataBufferNames());
		}

		//BasisBundle basisBundle = Irc.getDataSpace().getBasisBundle(
		//	basisBundleId);
	}

	/**
	 * Adds the given BasisSet to the avaiable satisfied BasisRequests.
	 * 
	 * @param basisSet basis set to add
	 */
	private void addToSatisfiedRequestsQueue(BasisSet basisSet)
	{
		// If there is already something on the
		// satisfied queue, then we need to replace it.
		BasisSet outdatedBasisSet = (BasisSet) fSatisfiedRequest.remove();

		if (outdatedBasisSet != null)
		{
			outdatedBasisSet.release();
		}

		if (fDownsamplingEnabled)
		{
			basisSet = downsample(basisSet);
		}
		
		fSatisfiedRequest.add(basisSet);
	}

	/**
	 * Causes this BasisRequester to process the given new BasisSet. If the
	 * BasisSet contained in the BasisSetEvent completes a current open
	 * BasisRequest of this BasisRequester, it will be incorporated into a new
	 * satisfying BasisSet. Otherwise, the BasisSet will be queued against the
	 * still-open BasisRequest.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected synchronized void processBasisSet(BasisSet basisSet)
	{
		if (basisSet == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Received null BasisSet";

				sLogger.logp(Level.WARNING, CLASS_NAME, "processBasisSet",
					message);
			}

			// There is nothing more to do in this method
			
			return;
		}
		
		if (!fHistoryBundleInitialized)
		{
			// Initialize the history bundle
			// Note this may need to be called again if the given basis set
			// does not have sufficient information.
			
			fHistoryBundleInitialized = 
				initializeHistoryBundle(basisSet, fHistoryAmount, fHistoryUnit);
				
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "History Amount:" + fHistoryAmount
					+ " History Samples:" + fHistorySamples
					+ " History Capacity:" + fHistoryBundleCapacity;

				sLogger.logp(Level.FINE, CLASS_NAME, "processBasisSet",
					message);
			}
		}

		if (isStarted() && fHistoryBundleInitialized)
		{
			// Process the new basis set
			
			try
			{
				// Since appending the new data to the history buffer may block
				// we need to do some checks to minimize a deadlock. First check
				// to see if there is enough room on the history buffer,
				if (!fMemoryModel.isAvailable(basisSet.getSize()))
				{
					// By promoting pending data, requesters may release any
					// holds they have on the older data.
					promotePendingData();
					
					// TODO if there is not any pending data should we send a
					// release request?
				}
				
				// Copy data to history
				BasisSet historyBasisSet = 
					fHistoryBasisBundle.appendBasisSet(basisSet);

				// We no longer need the original so release it
				basisSet.release();

				// Handle the new history BasisSet
				handlePendingData(historyBasisSet);
			}
			catch (IllegalArgumentException ex)
			{
				basisSet.release();
				throw ex;
			}
		}
		else
		{
			basisSet.release();
		}
	}

	// Needed state information for the initializeHistoryBundle method below
	private double fFirstBasisValue = 0;
	private boolean fPartialCalculationResult = false;
	
	/**
	 * Initializes several fields and history bundle based on the basis request
	 * and the given basis set. Note this may need to be called more than once
	 * if the BasisSet only has one sample in it. In this case partial 
	 * results are saved until a new basis set is received.
	 * 
	 * @param basisSet the latest basis set received
	 * @param requestedAmount the requested history amount
	 * @param requestedUnit the unit of the requested history amount
	 * @return true if history bundle was initialized, false otherwise.
	 */
	private boolean initializeHistoryBundle(
			BasisSet basisSet, double requestedAmount, Unit requestedUnit)
	{
		boolean result = false;
		double basisRate = 0.0;
		
		// check if we have a partial result already
		if (fPartialCalculationResult)
		{
			// This is the second basis set so we should now be able to 
			// approximate the data rate
			double basisDuration = 
				Math.abs(basisSet.getLastBasisValue() 
					- fFirstBasisValue);
			basisRate = basisDuration / basisSet.getSize();
			
			fPartialCalculationResult = false;
		}
		else if (basisSet.getSize() > 1)
		{
			// we have enough info with this one basis set to
			// approximate the data rate
			double basisDuration = 
				Math.abs(basisSet.getLastBasisValue() 
					- basisSet.getFirstBasisValue());
			basisRate = basisDuration / (basisSet.getSize() - 1);
		}
		else
		{
			// Save partial result until the next basis set is received.
			fFirstBasisValue = basisSet.getFirstBasisValue();
			fPartialCalculationResult = true;
		}

		// Check if we now have enough information to complete the 
		// initialization.
		if (!fPartialCalculationResult)
		{
			// Finish initialization
			if (!(requestedAmount > 0))
			{
				// The request amount was not set so default to the size of 
				// the basis set.
				requestedAmount = basisSet.getSize();
			}
			
			// Calculate the history size in samples
			if (requestedUnit == null || requestedUnit == Unit.ONE)
			{
				// History amount is already specified in samples so assign it
				fHistorySamples = (int) requestedAmount;
			}
			else
			{
				// History amount was specified in basis units so we need to
				// approximate how many samples this will be.
				fHistorySamples = (int) (requestedAmount / basisRate);
			}
			
			// Calculate the needed history capacity
			fHistoryBundleCapacity = fHistorySamples;
			
			// Increase the size to hold all data before the next update
			if (fRequestedUpdateInterval != Double.NaN)
			{
				// This is the expected amount of data we will receive
				// between the update intervals
				fHistoryBundleCapacity += 
					(int) (basisRate * fRequestedUpdateInterval);
			}
			else
			{
				// We don't know the data rate so just add some extra
				fHistoryBundleCapacity += (int) (fHistorySamples * 0.5);
			}
			
			// Add some extra for variable size basisSets and boundaries
			fHistoryBundleCapacity += fHistoryBundleCapacity / 2;
			fHistoryBundleCapacity += basisSet.getSize() * 3;
			
			fHistoryBasisBundle = new HistoryBundle(
				basisSet.getBasisBundleSourceId(), 
				basisSet.getBasisBundleId(),
				getInputDescriptor(), 
				fHistoryBundleCapacity);
			
			fHistoryBasisBundle.setHistorySize(fHistorySamples);
			fMemoryModel = fHistoryBasisBundle.getMemoryModel();

			// Initialization is complete
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Causes this BasisRequester to handle this event by clearing any
	 * pending data.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected void handleBundleStructureChange(BasisBundleEvent event)
	{
		clear();
		fHistoryBundleInitialized = false;
	}

	/**
	 * Causes this BasisRequester to handle this event by clearing any
	 * pending data.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected void handleBundleClosed(BasisBundleEvent event)
	{
		promotePendingData();
	}

	/**
	 * Returns a BasisSet that satisfies the BasisRequest associated with this
	 * BasisRequester, or null if there is not yet sufficient data available to
	 * satisfy the BasisRequest.
	 * 
	 * @return A BasisSet that satisfies the BasisRequest, or null
	 */
	public BasisSet satisfyRequest()
	{
		BasisSet result = null;

		if (isStarted())
		{
			result = (BasisSet) fSatisfiedRequest.remove();
		}

		return (result);
	}

	/**
	 * Returns a BasisSet that satisfies the BasisRequest associated with this
	 * BasisRequester. This call will block until the request is satisfied by
	 * further BasisBundleEvents or until this BasisRequester is cleared or
	 * stopped (in which case the result will be null).
	 * 
	 * @return A BasisSet that satisfies the BasisRequest associated with this
	 *         BasisRequester or null.
	 * @throws InterruptedException if the block is interrupted
	 */
	public BasisSet blockingSatisfyRequest() throws InterruptedException
	{
		BasisSet result = null;

		if (isStarted())
		{
			result = (BasisSet) fSatisfiedRequest.blockingRemove();
		}

		return (result);
	}

	/**
	 * Handles pending data by promoting it to satisfied status if the requested
	 * update interval has passed.
	 * 
	 * @param basisSet a BasisSet containing new data.
	 */
	protected void handlePendingData(BasisSet basisSet)
	{
		if (fPendingBasisSet != null)
		{
			fPendingBasisSet.release();
			fPendingBasisSet = null;
		}

		long currentTime = System.currentTimeMillis();

		if ((currentTime - fLastUpdate) > fUpdateIntervalMs)
		{
			addToSatisfiedRequestsQueue(basisSet);
			fLastUpdate = currentTime;
		}
		else
		{
			fPendingBasisSet = basisSet;
		}
	}

	/**
	 * Clears (and releases) all pending data.
	 */
	protected void clearPendingData()
	{
		if (fPendingBasisSet != null)
		{
			fPendingBasisSet.release();
			fPendingBasisSet = null;
		}
	}

	/**
	 * Promotes any currently pending data to satisfying data status.
	 */
	protected void promotePendingData()
	{
		if (fPendingBasisSet != null)
		{
			fLastUpdate = System.currentTimeMillis();
			addToSatisfiedRequestsQueue(fPendingBasisSet);
			fPendingBasisSet = null;

			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "CurrentTime:" + fLastUpdate 
					+ " Update Interval:" + fUpdateIntervalMs;

				sLogger.logp(Level.FINE, CLASS_NAME, "promotePendingData",
					message);
			}
		}
	}

	/**
	 * Clears (and releases) all queued satisfying data.
	 */
	protected void clearSatisfyingData()
	{
		BasisSet outdatedBasisSet = (BasisSet) fSatisfiedRequest.remove();

		if (outdatedBasisSet != null)
		{
			outdatedBasisSet.release();
		}
	}

	/**
	 * Clears (and releases) all queued data (both pending and satisfying) from
	 * this BasisRequester.
	 */
	public void clear()
	{
		clearPendingData();
		clearSatisfyingData();
	}

	/**
	 * Returns a String representation of this BasisRequester.
	 * 
	 * @return A String representation of this BasisRequester
	 */
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer();

		if (!isKilled())
		{
			stringRep.append(fBasisRequest);

			if (fAmountKept > 0)
			{
				stringRep.append("\nAmount currently kept: " + fAmountKept);
			}

			stringRep.append("\nBasisRequester state: " + getState());

			if (!fSatisfiedRequest.isEmpty())
			{
				stringRep.append("\nHas satisfied request:\n"
						+ fSatisfiedRequest);
			}

			if (fPendingBasisSet != null)
			{
				stringRep.append("\nHas pending data:\n" + fPendingBasisSet);
			}
		}
		else
		{
			stringRep.append("BasisRequester " + super.toString()
					+ " has been killed");
		}

		return (stringRep.toString());
	}

	//--- Utility class ------------------------------------------------------
	
	static final class SatisfiedCache
	{
		private Object fCachedValue = null;
		
		public SatisfiedCache()
		{
		}
		
		/**
		 * Add the given Object.
		 *
		 * @param object The Object to add to this Queue
		 * @return False if the add attempt failed
		**/		
		public synchronized boolean add(Object object)
		{
			boolean result = true;
			
			fCachedValue = object;
			
			if (fCachedValue != null)
			{
				notifyAll();
			}
			
			return (result);
		}

		/**
		 * Removes the cached Object and returns it. If no Object 
		 * is availble, returns null.
		 *
		 * @return The cached Object, or null if no Object is 
		 * 		available
		**/	
		public synchronized Object remove()
		{
			Object result = fCachedValue;
			fCachedValue = null;
			
			return (result);
		}
		
		/**
		 * Removes the cached Object if available, and returns it. If no Object
		 * is available, then this method blocks until an Object becomes
		 * available.
		 * 
		 * @return The available Object, or null if no Object is available
		 * @throws InterruptedException if this method blocks and then is
		 *             interrupted
		 */
		public synchronized Object blockingRemove()
			throws InterruptedException
		{
			Object result = fCachedValue;
			
			while (result == null)
			{
				wait();
				result = fCachedValue;
			}
			
			fCachedValue = null;
			
			return (result);
		}

		/**
		 * Returns true if this Queue is currently empty, false otherwise. 
		 * 
		 * @return True if this Queue is currently empty, false otherwise 
		**/		
		public boolean isEmpty()
		{
			return (fCachedValue == null);
		}
		
		/**
		 * Returns a String representation of this Queue.
		 * 
		 * @return A String representation of this Queue
		**/
		public synchronized String toString()
		{
			StringBuffer stringRep = new StringBuffer();
			
			if (fCachedValue != null)
			{
				stringRep.append(fCachedValue + "\n");
			}
			else
			{
				stringRep.append("Empty");
			}
			
			return (stringRep.toString());
		}
	}
}

// --- Development History ---------------------------------------------------
//
// $Log: HistoryBasisRequester.java,v $
// Revision 1.10  2006/08/10 13:24:35  smaher_cvs
// Changed handleBundleStructureChange to do a more thorough reset.
//
// Revision 1.9  2006/04/18 03:59:38  tames
// Removed println.
//
// Revision 1.8  2006/01/23 17:59:51  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.7  2006/01/20 05:26:11  tames
// Removed debug printlns.
//
// Revision 1.6  2006/01/02 18:30:31  tames
// Updated to reflect depricated BasisRequestAmount class. Uses Amount instead.
//
// Revision 1.5  2006/01/02 03:51:33  tames
// Added code to handle the case when no amount is specified.
//
// Revision 1.4  2005/11/16 16:37:31  tames_cvs
// Handled the case when a converter throws an exception.
//
// Revision 1.3  2005/09/22 18:43:45  tames
// Added log messages and removed System println calls.
//
// Revision 1.2  2005/09/09 21:34:18  tames
// BasisRequester framework refactoring.
//
// Revision 1.1 2005/08/26 22:10:49 tames_cvs
// Partial initial implementation.
//	
//	
