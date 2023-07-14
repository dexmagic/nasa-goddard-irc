//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/IntervalBasisRequester.java,v 1.4 2006/01/02 18:30:31 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.Converter;
import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.processing.activity.ActivityStateModel;
import gov.nasa.gsfc.commons.processing.activity.DefaultActivityStateModel;
import gov.nasa.gsfc.commons.properties.state.State;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.data.description.ModifiableBasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.BasisBundleListener;
import gov.nasa.gsfc.irc.data.events.BasisSetEvent;


/**
 *  A BasisRequester is a receiver of BasisBundleEvents, all from the same 
 *  BasisBundle, that is associated with exactly one BasisRequest on that 
 *  BasisBundle, and that knows how to satisfy its BasisRequest from the 
 *  BasisSets contained in the stream of BasisBundleEvents it receives. 
 *  
 *  <p>Note that a BasisRequester must be started before it will begin 
 *  listening for BasisBundleEvents and satisifying its associated 
 *  BasisRequest.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/02 18:30:31 $
 *  @author	Carl F. Hostetter
**/
public class IntervalBasisRequester implements BasisRequester
{
    /**
     * Logger for this class
     */
	private static final String CLASS_NAME = DefaultDataRequester.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private ActivityStateModel fStateModel = new DefaultActivityStateModel();
	
	private BasisRequest fBasisRequest = null;
	private BasisBundle fBasisBundle = null;
	
	private Queue fPendingData = new FifoQueue();
	private FifoQueue fSatisfiedRequests = new FifoQueue();
	
	private List fBasisBundleListeners = 
		Collections.synchronizedList(new ArrayList(0));
	private BasisBundleEvent fCurrentBasisBundleStructureEvent;
	
	private boolean fSelectsAllAvailableData = false;
	private Set fDataBufferSubset;
	
	private int fDownsamplingRate = 0;
	
	private Unit fInputBasisUnit;
	
	private Unit fInitialSkipUnit;
	private Unit fKeepUnit;
	private Unit fFinalSkipUnit;
	
	private double fInitialSkipAmount = 0;
	private double fKeepAmount = 0;
	private double fFinalSkipAmount = 0;
	
	private boolean fStartsNewBasisSequence = false;

	private double fAmountSkippedInitially = 0;
	private double fAmountKept = 0;
	private double fAmountSkippedFinally = 0;
	
	/**
	 * Constructs a new BasisRequester that will attempt to satisfy the 
	 * given BasisRequest.
	 * 
	 * @param basisRequest A BasisRequest
	 */	
	public IntervalBasisRequester(BasisRequest basisRequest)
	{
		setBasisRequest(basisRequest);
	}
	
	//----------------------------------------------------------------------
	//	State-related methods
	//----------------------------------------------------------------------
	
	/**
	 * Returns the current State of this BasisRequester.
	 * 
	 * @return The current State of this BasisRequester
	 */
	public State getState()
	{
		return (fStateModel.getState());
	}
	
	/**
	 *  Causes this BasisRequester to start issuing and satisfying its 
	 *  associated BasisRequest.
	 */
	public void start()
	{
		if (! isKilled() && ! isStarted())
		{
			try
			{
				fStateModel.start();
			
				fBasisBundle.addBasisBundleListener(this);
				fBasisBundle.addBasisSetListener(this);

			}
			catch (Exception ex)
			{
				declareException(ex);
			}
		}
	}
	
	/**
	 *  Returns true if this BasisRequester is currently started, false 
	 *  otherwise. A BasisRequester is also considered started if it is 
	 *  active or waiting.
	 *  
	 *  @return True if this BasisRequester is currently started, false 
	 * 		otherwise
	 */
	 
	public boolean isStarted()
	{
		return (fStateModel.isStarted());
	}
	

	/**
	 *  Sets this BasisRequester's State to active. A BasisRequester cannot 
	 *  become active unless it is currently started.
	 *  
	 */
	
	protected void declareActive()
	{
		fStateModel.declareActive();
	}
	
	
	/**
	 * Returns true if this BasisRequester is active, false otherwise.
	 *
	 * @return True if this BasisRequester is active, false otherwise
	 **/
	
	public boolean isActive()
	{
		return (fStateModel.isActive());
	}
	

	/**
	 *  Sets this BasisRequester's State to waiting. A BasisRequester cannot 
	 *  become waiting unless it is currently active.
	 *  
	 */
	
	protected void declareWaiting()
	{
		fStateModel.declareWaiting();
	}
	
	
	/**
	 * Returns true if this BasisRequester is waiting, false otherwise.
	 *
	 * @return True if this BasisRequester is waiting, false otherwise
	 **/
	
	public boolean isWaiting()
	{
		return (fStateModel.isWaiting());
	}
	

	/**
	 *  Causes this BasisRequester to stop, if it is currently started.
	 *  
	 */
	
	public synchronized void stop()
	{
		if (isStarted())
		{
			fStateModel.stop();
			
			fBasisBundle.removeBasisBundleListener(this);
			fBasisBundle.removeBasisSetListener(this);
			
			if (fBasisRequest.includesPendingDataOnStop())
			{
				promotePendingData();
				
				try
				{
					fSatisfiedRequests.blockUntilEmpty();
				}
				catch (InterruptedException ex)
				{
					
				}
			}
			
			// We need to release any currently queued input data
			
			clear();
			
			fSatisfiedRequests.unblock();
			
			fAmountSkippedInitially = 0;
			fAmountKept = 0;
			fAmountSkippedFinally = 0;
		}
	}
	
	
	/**
	 *  Returns true if this BasisRequester is stopped, false otherwise.
	 *
	 *  @return True if this BasisRequester is stopped, false otherwise
	 */
	
	public boolean isStopped()
	{
		return (fStateModel.isStopped());
	}
	
	
	/**
	 *  Sets this BasisRequester to the exception state, due to the given Exception. 
	 *  This BasisRequester is first stopped, and then enters the exception state.
	 *  
	 *  @param exception The Exception resulting in the exception state
	 */
	
	public void declareException(Exception exception)
	{
		if (sLogger.isLoggable(Level.SEVERE))
		{
			String message = 
				"Encountered Exception: stopping and entering exception state";
			
			sLogger.logp
				(Level.SEVERE, CLASS_NAME, "declareException", message, exception);
		}
		
		stop();
		
		fStateModel.declareException(exception);
	}
	
	
	/**
	 *  Clears the current Exception (if any) from this BasisRequester.
	 *  
	 */
	
	public void clearException()
	{
		fStateModel.clearException();
	}
	
	
	/**
	 *  Returns true if this BasisRequester is in an exception state, false 
	 *  otherwise.
	 *
	 *  @return True if this BasisRequester is in an exception state, false 
	 * 		otherwise
	 */
	
	public boolean isException()
	{
		return (fStateModel.isException());
	}
	
	
	/**
	 *  If this BasisRequester is in an exception state, this method will return the 
	 *  Exception that caused it. Otherwise, it returns null.
	 *  
	 *  @return The Exception that caused the exception state
	 */
	
	public Exception getException()
	{
		return (fStateModel.getException());
	}
	
	
	/**
	 *  Causes this BasisRequester to immediately cease operation and release 
	 *  any allocated resources. A killed BasisRequester cannot subsequently be 
	 *  started or otherwise reused.
	 * 
	 */
	
	public void kill()
	{
		if (! isKilled())
		{
			fBasisBundle.removeBasisBundleListener(this);
			
			// We need to release any currently queued input data
			
			clear();
			
			fStateModel.kill();
			
			fPendingData = null;
			fSatisfiedRequests = null;
		}
	}

	
	/**
	 *  Returns true if this BasisRequester is killed, false otherwise.
	 *
	 *  @return True if this BasisRequester is killed, false otherwise
	 */
	
	public boolean isKilled()
	{
		return (fStateModel.isKilled());
	}
	
	
	/**
	 * Adds the given StateChangeListener as a listener for changes in 
	 * the ActivityState of this BasisRequester.
	 *
	 * @param listener A StateChangeListener
	 **/
	
	public void addStateListener(PropertyChangeListener listener)
		throws IllegalArgumentException
	{
		fStateModel.addStateListener(listener);
	}


	/**
	 *	Returns the set of StateChangeListeners on this BasisRequester as an 
	 *  array of PropertyChangeListeners.
	 *
	 *  @return the Set of StateChangeListeners on this BasisRequester as an 
	 *  		array of PropertyChangeListeners
	 */
	
	public PropertyChangeListener[] getStateListeners()
	{
		return (fStateModel.getStateListeners());
	}
	
	
	/**
	 * Removes the given StateChangeListener as a listener for changes in 
	 * the ActivityState of this BasisRequester.
	 *
	 * @param listener A StateChangeListener
	 **/
	
	public void removeStateListener(PropertyChangeListener listener)
		throws IllegalArgumentException
	{
		fStateModel.removeStateListener(listener);
	}	
	
	
	/**
	 * Returns a filtered version of the given input BasisBundleDescriptor, 
	 * according to the current DataBuffer subset filter, if any. 
	 * 
	 * @param basisRequest A BasisRequest
	 */
	
	private BasisBundleDescriptor filterDescriptor
		(BasisBundleDescriptor descriptor)
	{
		BasisBundleDescriptor result = descriptor;
		
		if (fDataBufferSubset != null)
		{
			ModifiableBasisBundleDescriptor newDescriptor = 
				(ModifiableBasisBundleDescriptor) 
					descriptor.getModifiableCopy();
			
			Iterator dataBufferDescriptors = 
				newDescriptor.getDataBufferDescriptors().iterator();
			
			while (dataBufferDescriptors.hasNext())
			{
				DataBufferDescriptor dataBufferDescriptor = 
					(DataBufferDescriptor) dataBufferDescriptors.next();
				
				String descriptorName = 
					dataBufferDescriptor.getFullyQualifiedName();
				
				if (! fDataBufferSubset.contains(descriptorName))
				{
					newDescriptor.removeDataEntryDescriptor
						(dataBufferDescriptor);
				}
			}
			
			result = newDescriptor;
		}
		
		return (result);
	}
	
	/**
	 * Sets the BasisRequest to be satisfied by this BasisRequester to 
	 * the given BasisRequest. 
	 * 
	 * @param basisRequest A BasisRequest
	 * @throws IllegalArgumentException if the given BasisRequest is not valid in 
	 * 		some way
	 */
	public void setBasisRequest(BasisRequest basisRequest)
	{
		fBasisRequest = basisRequest;
		
		BasisBundleId basisBundleId = basisRequest.getBasisBundleId();
		
		fBasisBundle = 
			Irc.getDataSpace().getBasisBundle(basisBundleId);
		
		fInputBasisUnit = fBasisBundle.getBasisBufferDescriptor().getUnit();
		
		fSelectsAllAvailableData = 
			basisRequest.selectsAllAvailableData();
		
		BasisRequestAmount requestAmount = 
			(BasisRequestAmount) fBasisRequest.getRequestAmount();
		
		if (requestAmount != null)
		{
			fInitialSkipAmount = requestAmount.getInitialSkip().getAmount();
			fInitialSkipUnit = requestAmount.getInitialSkip().getUnit();
			
			if ((fInitialSkipAmount != 0) && (fInitialSkipUnit != null) &&  
					(fInitialSkipUnit != Unit.ONE) && 
					(fInitialSkipUnit != fInputBasisUnit))
			{
				Converter converter = 
					fInitialSkipUnit.getConverterTo(fInputBasisUnit);
				
				if (converter != null)
				{
					fInitialSkipAmount = converter.convert(fInitialSkipAmount);
				}
				else
				{
					String message = "Could not convert from initial skip Unit " + 
						fInitialSkipUnit + " to input basis Unit " + 
						fInputBasisUnit + " in request " + basisRequest;
					
					if (sLogger.isLoggable(Level.SEVERE))
					{
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"setBasisRequest", message);
					}
					
					throw (new IllegalArgumentException(message));
				}
			}
			
			fKeepAmount = requestAmount.getKeep().getAmount();
			fKeepUnit = requestAmount.getKeep().getUnit();
			
			if ((fKeepAmount != 0) && (fKeepUnit != null) && 
				(fKeepUnit != Unit.ONE) && (fKeepUnit != fInputBasisUnit))
			{
				Converter converter = fKeepUnit.getConverterTo(fInputBasisUnit);
				
				if (converter != null)
				{
					fKeepAmount = converter.convert(fKeepAmount);
				}
				else
				{
					String message = "Could not convert from keep Unit " + 
						fKeepUnit + " to input basis Unit " + 
						fInputBasisUnit + " in request " + basisRequest;
					
					if (sLogger.isLoggable(Level.SEVERE))
					{
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"setBasisRequest", message);
					}
					
					throw (new IllegalArgumentException(message));
				}
			}
			
			fFinalSkipAmount = requestAmount.getFinalSkip().getAmount();
			fFinalSkipUnit = requestAmount.getFinalSkip().getUnit();
			
			if ((fFinalSkipAmount != 0) && (fFinalSkipUnit != null) && 
				(fFinalSkipUnit != Unit.ONE) && 
				(fFinalSkipUnit != fInputBasisUnit))
			{
				Converter converter = 
					fFinalSkipUnit.getConverterTo(fInputBasisUnit);
				
				if (converter != null)
				{
					fFinalSkipAmount = converter.convert(fFinalSkipAmount);
				}
				else
				{
					String message = "Could not convert from final skip Unit " + 
						fFinalSkipUnit + " to input basis Unit " + 
						fInputBasisUnit + " in request " + basisRequest;
					
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
			fInitialSkipAmount = 0;
			fKeepAmount = 0;
			fFinalSkipAmount = 0;
			
			fInitialSkipUnit = null;
			fKeepUnit = null;
			fFinalSkipUnit = null;
		}
		
		fDownsamplingRate = basisRequest.getDownsamplingRate();
		
		BasisBundleDescriptor descriptor = fBasisBundle.getDescriptor();
		
		if (basisRequest.selectsAllDataBuffers())
		{
			fDataBufferSubset = null;
		}
		else
		{
			fDataBufferSubset = basisRequest.getDataBufferNames();

			descriptor = filterDescriptor(descriptor);
		}
		
		fCurrentBasisBundleStructureEvent = new BasisBundleEvent
			(fBasisBundle, descriptor);
	}
	
	
	/**
	 * Sets the downsampling rate of all of this BasisRequester to the given rate. 
	 * To indicate no downsampling, set this rate to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of this BasisRequester
	 */
	
	public void setDownsamplingRate(int downsamplingRate)
	{
		fBasisRequest.setDownsamplingRate(downsamplingRate);
		fDownsamplingRate = downsamplingRate;
	}
	
	
	/**
	 * Gets the downsampling rate of this BasisRequester. 
	 * 
	 * @return The downsampling rate of this BasisRequester
	 */
	
	public int getDownsamplingRate()
	{
		return (fDownsamplingRate);
	}
	
	
	/**
	 * Returns the BasisRequest currently associated with this 
	 * BasisRequester.
	 * 
	 * @return The BasisRequest currently associated with this 
	 * 		BasisRequester
	 */
	
	public BasisRequest getBasisRequest()
	{
		return (fBasisRequest);
	}
	
	
	/**
	 * Adds the given BasisSet to the queue of satisfited BasisRequests.
	 * 
	 */
	private void addToSatisfiedRequestsQueue(BasisSet basisSet)
	{
		if (fDownsamplingRate > 1)
		{
			if (fDownsamplingRate < basisSet.getSize())
			{
				BasisSet subsampledData = 
					basisSet.downsample(fDownsamplingRate);
				
				// We no longer need the original so release it
				basisSet.release();
				basisSet = subsampledData;
			}
		}
		
		fSatisfiedRequests.add(basisSet);
	}
	
	
	/**
	 * Causes this BasisRequester to copy its satisfying BasisSet(s) of data, 
	 * release the uncopied BasisSet(s), and then add the copied data back into 
	 * the queue of satisfied requests.
	 * 
	 */
	
	protected void copyAndReleaseSatisfyingData()
	{
		synchronized (fSatisfiedRequests)
		{
			int numSatisfiedRequests = fSatisfiedRequests.size();
			
			if (numSatisfiedRequests > 0)
			{
				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "Copying and releasing satisfying data (" + 
						numSatisfiedRequests + " requests)";
					
					sLogger.logp(Level.FINE, CLASS_NAME, 
						"copyAndReleaseSatisfyingData", message);
				}
				
				BasisSet[] copiedData = new BasisSet[numSatisfiedRequests];
				
				for (int i = 0; i < numSatisfiedRequests; i++)
				{
					BasisSet satisfiedRequest = 
						(BasisSet) fSatisfiedRequests.remove();
					
					if (satisfiedRequest.isCopy())
					{
						copiedData[i] = satisfiedRequest;
					}
					else
					{
						BasisSet copy = satisfiedRequest.copy();
						
						copiedData[i] = copy;
						
						satisfiedRequest.release();
					}
				}
				
				for (int i = 0; i < numSatisfiedRequests; i++)
				{
					fSatisfiedRequests.add(copiedData[i]);
				}
			}
		}
	}
	
	
	/**
	 * Causes this BasisRequester to copy its pending BasisSet of data, 
	 * release the uncopied BasisSet, and then add the copied data back into 
	 * the queue of pending data.
	 * 
	 */
	
	protected void copyAndReleasePendingData()
	{
		synchronized (fPendingData)
		{
			if (! fPendingData.isEmpty())
			{
				BasisSet pendingData = (BasisSet) fPendingData.get();
				
				if (! pendingData.isCopy())
				{
					if (sLogger.isLoggable(Level.FINE))
					{
						String message = "Copying and releasing pending data:\n" + 
							pendingData;
						
						sLogger.logp(Level.FINE, CLASS_NAME, 
							"copyAndReleasePendingData", message);
					}
					
					fPendingData.remove();
					
					BasisSet copiedData = pendingData.copy();
					
					fPendingData.add(copiedData);
					
					pendingData.release();
				}
			}
		}
	}
	
	
	/**
	 * Returns a BasisSet that represents the concatenation of the two 
	 * given BasisSets, which must have the same structure.
	 * 
	 * @param first The first of the two BasisSets to be concatenated
	 * @param second The second of the two BasisSets to be concatenated
	 * @return A BasisSet that represents the concatenation of the two given 
	 * 		BasisSets
	 */
	protected BasisSet concatenate(BasisSet first, BasisSet second)
	{
		//System.out.println("DBR concatenate first " + first.getBasisBundleStartPosition() + " size " + first.getSize());
		//System.out.println("DBR concatenate second " + second.getBasisBundleStartPosition() + " size " + second.getSize());
		
		BasisSet result = first.append(second);
		
		//System.out.println("DBR concatenate result " + result.getBasisBundleStartPosition() + " size " + result.getSize());
		
		// If only a subset of BasisBuffers is selected by the current 
		// BasisRequest, we filter the new BasisSet to include only those 
		// selected DataBuffers.
		
		if (fDataBufferSubset != null)
		{
			result = result.filterIn(fDataBufferSubset);
		}

		result.makeReadOnly();
		
		// We can now release the two given BasisSets.
		
		first.release();
		second.release();
		
		return (result);
	}
	
	
	/**
	 * Extracts any still-needed initial skip amount from the given BasisSet, 
	 * and returns whatever is left over.
	 * 
	 * @param basisSet A BasisSet
	 * @return Whatever part of the given BasisSet was not extracted for the 
	 * 		currently-needed initial skip amount
	 */
	
	protected BasisSet extractInitialSkipAmount(BasisSet basisSet)
	{
		BasisSet remainder = null;
		
		if ((fInitialSkipAmount > 0) && 
			(fAmountSkippedInitially < fInitialSkipAmount))
		{
			double basisAmount = 0;
			
			if ((fInitialSkipUnit == null) || (fInitialSkipUnit == Unit.ONE))
			{
				fAmountSkippedInitially += basisSet.getSize();
			}
			else
			{
				basisAmount = basisSet.getBasisAmount();
				
				fAmountSkippedInitially += basisAmount;
			}
			
			double remainderAmount = 0;
			boolean hasRemainder = false;
			
			if (fAmountSkippedInitially > fInitialSkipAmount)
			{
				remainderAmount = fAmountSkippedInitially - fInitialSkipAmount;
				hasRemainder = true;
				
				fAmountSkippedInitially = fInitialSkipAmount;
			}
			
			if (hasRemainder)
			{
				int remainderStartIndex = 0;
				int remainderLength = basisSet.getSize();
				
				if ((fInitialSkipUnit == null) || (fInitialSkipUnit == Unit.ONE))
				{
					remainderStartIndex = 
						(remainderLength - (int) remainderAmount);
				}
				else
				{
					double skippedAmount = basisAmount - remainderAmount;
					
					remainderStartIndex = basisSet.
						getIndexOfBasisAmountAsUpperBound(skippedAmount) + 1;
				}
				
				if (remainderStartIndex > 0)
				{
					remainder = 
						basisSet.slice(remainderStartIndex, (int) remainderAmount);
					
					basisSet.release();
				}
				else
				{
					// This can happen in real-valued basis-amount requests, 
					// when the value needed falls between the last sample 
					// of the previous BasisSet and the first sample of this 
					// BasisSet. In which case, we've in fact skipped the 
					// requisite amount, and so can send this BasisSet on intact.
					
					remainder = basisSet;
					fAmountSkippedInitially = fInitialSkipAmount;
				}
			}
			else
			{
				basisSet.release();
			}
		}
		else
		{
			remainder = basisSet;
		}			
		
		return (remainder);
	}
	
	
	/**
	 * Extracts any still-needed keep amount from the given BasisSet, and 
	 * returns whatever is left over.
	 * 
	 * @param basisSet A BasisSet
	 * @return Whatever part of the given BasisSet was not extracted for the 
	 * 		currently-needed keep amount
	 */
	
	protected BasisSet extractKeepAmount(BasisSet basisSet)
	{
		//System.out.println("DBR extractKeepAmount start:" + basisSet.getBasisBundleStartPosition() + " size:" +basisSet.getSize());
		BasisSet remainder = null;
		
		if ((fKeepAmount > 0) && (fAmountKept < fKeepAmount))
		{
			double basisAmount = 0;
			
			if ((fKeepUnit == null) || (fKeepUnit == Unit.ONE))
			{
				fAmountKept += basisSet.getSize();
			}
			else
			{
				basisAmount = basisSet.getBasisAmount();
				
				fAmountKept += basisAmount;
			}
			
			double remainderAmount = 0;
			
			if (fAmountKept > fKeepAmount)
			{
				remainderAmount = fAmountKept - fKeepAmount;
				
				fAmountKept = fKeepAmount;
			}
			
			BasisSet keptData = basisSet;
			
			if (remainderAmount > 0)
			{
				int remainderStartIndex = 0;
				int remainderLength = basisSet.getSize();
			
				if ((fKeepUnit == null) || (fKeepUnit == Unit.ONE))
				{
					remainderStartIndex = 
						(remainderLength - (int) remainderAmount);
				}
				else
				{
					double keptAmount = basisAmount - remainderAmount;
					
					remainderStartIndex = basisSet.
						getIndexOfBasisAmountAsUpperBound(keptAmount) + 1;
					
					// I added the following check because it would have
					// saved me a ton of time tracking down a (not so unusual, I believe)
					// problem with my basis buffer.  The problem is when the buffer
					// doesn't contain monotonically increasing (or decreasing)
					// values.  (Yes, it should, but during development that's not
					// always the case :)
					//
					// There will be an exception later on if the condition is
					// true, but its message is not very helpful; this will help.
					//
					// S. Maher
					//
					if (remainderStartIndex >= basisSet.getSize())
					{
                        sLogger.logp(
                                        Level.SEVERE,
                                        "DefaultBasisRequester",
                                        "extractKeepAmount()",
                                        "getIndexOfBasisAmountAsUpperBound returned the end of the BasisSet; are the basis buffer values monotonically increasing or decreasing?");
					}
				}
				
				if (remainderStartIndex > 0)
				{
					keptData = basisSet.slice(0, remainderStartIndex);
					
					remainder = 
						basisSet.slice(
							remainderStartIndex, 
							remainderLength - remainderStartIndex);
					
					// The original is no longer needed
					basisSet.release();
				}
				else
				{
					// This can happen in real-valued basis-amount requests, 
					// when the value needed falls between the last sample 
					// of the previous BasisSet and the first sample of this 
					// BasisSet. In which case, the currently pending data 
					// is promoted to satisfying status, and this BasisSet 
					// sent on intact.
					
					remainder = basisSet;
					fAmountKept = 0;
					
					if (fPendingData.size() > 0)
					{
						BasisSet pendingData = (BasisSet) fPendingData.remove();
						
						addToSatisfiedRequestsQueue(pendingData);
					}
				}
			}
			
			if (fPendingData.size() > 0)
			{
				BasisSet pendingData = (BasisSet) fPendingData.remove();
				keptData = concatenate(pendingData, keptData);
			}
			
			if (fAmountKept >= fKeepAmount)
			{
				addToSatisfiedRequestsQueue(keptData);
			}
			else
			{
				fPendingData.add(keptData);
			}
		}
		else
		{
			remainder = basisSet;
		}			
		
		return (remainder);
	}
	
	
	/**
	 * Extracts any still-needed final skip amount from the given BasisSet, 
	 * and returns whatever is left over.
	 * 
	 * @param basisSet A BasisSet
	 * @return Whatever part of the given BasisSet was not extracted for the 
	 * 		currently-needed final skip amount
	 */
	
	protected BasisSet extractFinalSkipAmount(BasisSet basisSet)
	{
		BasisSet remainder = null;
		
		if ((fFinalSkipAmount > 0) && 
			(fAmountSkippedFinally < fFinalSkipAmount))
		{
			double basisAmount = 0;
			
			if ((fFinalSkipUnit == null) || (fFinalSkipUnit == Unit.ONE))
			{
				fAmountSkippedFinally += basisSet.getSize();
			}
			else
			{
				basisAmount = basisSet.getBasisAmount();
				
				fAmountSkippedFinally += basisAmount;
			}
			
			double remainderAmount = 0;
			boolean hasRemainder = false;
			
			if (fAmountSkippedFinally > fFinalSkipAmount)
			{
				remainderAmount = fAmountSkippedFinally - fFinalSkipAmount;
				hasRemainder = true;
				
				fAmountSkippedFinally = fFinalSkipAmount;
			}
			
			if (hasRemainder)
			{
				int remainderStartIndex = 0;
				int remainderLength = basisSet.getSize();
				
				if ((fFinalSkipUnit == null) || (fFinalSkipUnit == Unit.ONE))
				{
					remainderStartIndex = 
						(remainderLength - (int) remainderAmount);
				}
				else
				{
					double skippedAmount = basisAmount - remainderAmount;
					
					remainderStartIndex = basisSet.
						getIndexOfBasisAmountAsUpperBound(skippedAmount) + 1;
				}
				
				if (remainderStartIndex > 0)
				{
					remainder = 
						basisSet.slice(remainderStartIndex, (int) remainderAmount);
					
					// Original BasisSet is no longer needed
					basisSet.release();
				}
				else
				{
					// This can happen in real-valued basis-amount requests, 
					// when the value needed falls between the last sample 
					// of the previous BasisSet and the first sample of this 
					// BasisSet. In which case, we've in fact skipped the 
					// requisite amount, and so can send this BasisSet on intact.
					
					remainder = basisSet;
					fAmountSkippedFinally = fFinalSkipAmount;
				}
			}
			else
			{
				// BasisSet is skipped so release it
				basisSet.release();
			}
		}
		else
		{
			remainder = basisSet;
		}
		
		return (remainder);
	}
	
	
	/**
	 * Causes this BasisRequester to process the given new BasisSet. If the BasisSet 
	 * contained in the BasisSetEvent completes a current open BasisRequest of this 
	 * BasisRequester, it will be incorporated into a new satisfying BasisSet. 
	 * Otherwise, the BasisSet will be queued against the still-open BasisRequest.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected synchronized void processBasisSet(BasisSet basisSet)
	{
		//System.out.println("DBR processBasisSet start:" + basisSet.getFirstBasisValue());
		if (isStarted() && (basisSet != null))
		{
			try
			{
				// If only a subset of BasisBuffers is selected by the 
				// current BasisRequest, we filter the BasisSet to include 
				// only those selected DataBuffers.
				
				if (fDataBufferSubset != null)
				{
					BasisSet filteredBasisSet = 
						basisSet.filterIn(fDataBufferSubset);
					
					// We no longer need the original so release it
					basisSet.release();
					basisSet = filteredBasisSet;
				}
				
				if (fSelectsAllAvailableData)
				{
					// If we're requesting all data as it becomes 
					// available ... 
	
					synchronized (fSatisfiedRequests)
					{
						if (fSatisfiedRequests.size() == 0)
						{
							// ... and if there is nothing currently on the 
							// satisfied queue, then we can just add the new 
							// BasisSet to the satisfied request queue.
							
							addToSatisfiedRequestsQueue(basisSet);
						}
						else
						{
							// ... and if there is already something on the 
							// satisfied queue, then we need to concatenate 
							// the new BasisSet to the end of the last of 
							// the already satisfied requests.
							
							BasisSet satisfiedRequest = (BasisSet)
								fSatisfiedRequests.removeLast();
							
							addToSatisfiedRequestsQueue(
								concatenate(satisfiedRequest, basisSet));
						}
					}
				}
				else
				{
					if (fStartsNewBasisSequence)
					{
						if (! fBasisRequest.ignoresBasisSequenceBoundaries())
						{
							fAmountSkippedInitially = 0;
							fAmountKept = 0;
							fAmountSkippedFinally = 0;
							
							clearPendingData();
							
							fStartsNewBasisSequence = false;
						}
					}
					
					while (basisSet != null)
					{
						basisSet = extractInitialSkipAmount(basisSet);
						
						if (basisSet != null)
						{
							basisSet = extractKeepAmount(basisSet);
						}
						
						if (basisSet != null)
						{
							basisSet = extractFinalSkipAmount(basisSet);
						}
						
						if ((fAmountSkippedInitially == fInitialSkipAmount) && 
							(fAmountKept == fKeepAmount) && 
							(fAmountSkippedFinally == fFinalSkipAmount))
						{
							fAmountSkippedInitially = 0;
							fAmountKept = 0;
							fAmountSkippedFinally = 0;
						}
					}
				}
			}
			catch (IllegalArgumentException ex)
			{
				basisSet.release();				
				throw ex;
			}
		}
		else
		{
			if (basisSet != null)
			{
				basisSet.release();
			}
			else
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Received null BasisSet";

					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"processBasisSet", message);
				}
			}
		}
	}
	
	
	/**
	 * Alerts each of the current BasisBundleListeners of this BasisRequester of the 
	 * given BasisBundleEvent.
	 * 
	 * @param event A BasisBundleEvent
	 */
	
	protected void alertBasisBundleListeners(BasisBundleEvent event)
	{
		Object[] listeners = null;
		
		synchronized (fBasisBundleListeners)
		{
			listeners = fBasisBundleListeners.toArray();
		}

		for (int i = 0; i < listeners.length; i++)
		{
			((BasisBundleListener) listeners[i]).receiveBasisBundleEvent(event);
		}
	}


	/**
	 * Adds the given BasisBundleListener as a listener for BasisBundleEvents 
	 * from this BasisRequester.
	 *
	 * @param listener A BasisBundleListener
	 **/
	
	public void addBasisBundleListener(BasisBundleListener listener)
	{
		if (! isKilled())
		{
			fBasisBundleListeners.add(listener);
			
			// Alert the given BasisBundleListener of the current structure of the 
			// BasisBundle associated with this BasisRequester.
			
			listener.receiveBasisBundleEvent(fCurrentBasisBundleStructureEvent);
		}
	}
	
	
	/**
	 * Removes the given BasisBundleListener as a listener for BasisBundleEvents 
	 * from this BasisRequester.
	 *
	 * @param listener A BasisBundleListener
	 **/
	
	public void removeBasisBundleListener(BasisBundleListener listener)
	{
		if (! isKilled())
		{
			fBasisBundleListeners.remove(listener);
		}
	}
	
	
	/**
	 * Causes this BasisRequester to receive the given BasisSetEvent. 
	 * If the BasisSet contained in the BasisSetEvent completes a 
	 * current open BasisRequest of this BasisRequester, it will be 
	 * incorporated into a new satisfying BasisSet. Otherwise, the BasisSet 
	 * will be queued against the still-open BasisRequest.
	 * 
	 * @param event A BasisSetEvent
	 */
	
	public synchronized void receiveBasisSetEvent(BasisSetEvent event)
	{
		if (((BasisSetEvent) event).isStartOfNewBasisSequence())
		{
			fStartsNewBasisSequence = true;
		}
		
		BasisSet basisSet = ((BasisSetEvent) event).getBasisSet();
		
		if (basisSet != null)
		{
			processBasisSet(basisSet);
		}
	}
	
	/**
	 * Causes this BasisRequester to receive the given BasisBundleEvent. 
	 * If the BasisSet contained in the BasisSetEvent completes a 
	 * current open BasisRequest of this BasisRequester, it will be 
	 * incorporated into a new satisfying BasisSet. Otherwise, the BasisSet 
	 * will be queued against the still-open BasisRequest.
	 * 
	 * @param event A BasisBundleEvent
	 */
	
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		if (event.isClosed())
		{
			clearPendingData();
			
			try
			{
				fSatisfiedRequests.blockUntilEmpty();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			
			stop();
		}
		else if (event.hasNewStructure())
		{
			clearPendingData();
			
			try
			{
				fSatisfiedRequests.blockUntilEmpty();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		else if (event.isRequestingRelease())
		{
			//copyAndReleaseSatisfyingData();
			//copyAndReleasePendingData();
		}
		
		alertBasisBundleListeners(event);
	}
	
	
	/**
	 * Returns a BasisSet spanning precisely the amount of queued data that 
 	 * satisfies the BasisRequest associated with this BasisRequester, or null if 
 	 * there is not yet sufficient data available to satisfy the BasisRequest.
	 * 
	 * @return A BasisSet spanning precisely the amount of queued data that 
 	 * 		satisfies the BasisRequest associated with this BasisRequester, or null
 	 * 		if there is not yet sufficient data available to satisfy the 
 	 * 		BasisRequest
 	 */
	
	public BasisSet satisfyRequest()
	{
		BasisSet result = null;
		
		if (isStarted())
		{
			result = (BasisSet) fSatisfiedRequests.remove();				
		}
		
		return (result);
	}
		

	/**
	 * Returns a BasisSet spanning precisely the amount of queued data that 
 	 * satisfies the BasisRequest associated with this BasisRequester. If 
 	 * the BasisSets contained in the BasisBundleEvents received by this 
 	 * BasisRequester since the last request was satisfied do not yet 
 	 * satisfy the current request, the call will block until the request is 
 	 * satisfied by further BasisBundleEvents or until this BasisRequester 
 	 * is cleared or stopped (in which case the result will be null).
	 * 
	 * @return A BasisSet spanning precisely the amount of queued data that 
 	 * 		satisfies the BasisRequest associated with this BasisRequester. 
 	 * 		If the BasisSets pending since the last request was satisfied do 
 	 * 		not yet satisfy the current request, the call will block until the 
 	 * 		request is satisfied by additional BasisSets or until this 
 	 * 		BasisRequester is cleared or stopped (in which case the result 
 	 * 		may be null)
	 * @throws InterruptedException if the block is interrupted
	 */
	
	public BasisSet blockingSatisfyRequest()
		throws InterruptedException
	{
		BasisSet result = null;
		
		if (isStarted())
		{
			result = (BasisSet) fSatisfiedRequests.blockingRemove();				
		}
		
		return (result);
	}
	
	
	/**
	 * Clears (and releases) all queued pending data.
	 * 
	 */
	
	protected void clearPendingData()
	{
		synchronized (fPendingData)
		{
			while (fPendingData.size() > 0)
			{
				BasisSet basisSet = (BasisSet) fPendingData.remove();

				basisSet.release();
			}
			
			fAmountSkippedInitially = 0;
			fAmountKept = 0;
			fAmountSkippedFinally = 0;
		}
	}
	

	/**
	 * Promotes any currently pending data to satisfying data status.
	 * 
	 */
	
	protected void promotePendingData()
	{
		synchronized (fPendingData)
		{
			while (fPendingData.size() > 0)
			{
				BasisSet basisSet = (BasisSet) fPendingData.remove();

				fSatisfiedRequests.add(basisSet);
			}
		}
	}
	

	/**
	 * Clears (and releases) all queued satisfying data.
	 * 
	 */
	
	protected void clearSatisfyingData()
	{
		synchronized (fSatisfiedRequests)
		{
			while (fSatisfiedRequests.size() > 0)
			{
				BasisSet basisSet = (BasisSet) fSatisfiedRequests.remove();
				
				basisSet.release();
			}
			
			fSatisfiedRequests.clear();
		}
	}
	

	/**
	 * Clears (and releases) all queued data (both pending and satisfying) 
	 * from this BasisRequester. Any currently blocked request will be 
	 * unblocked.
	 * 
	 */
	
	public void clear()
	{
		if (! isKilled())
		{
			synchronized (fPendingData)
			{
				clearPendingData();
				clearSatisfyingData();
			}
		}
	}
	
	
	/**
	 * Returns a String representation of this BasisRequester.
	 * 
	 * @return A String representation of this BasisRequester
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer();
		
		if (! isKilled())
		{
			stringRep.append(fBasisRequest);
			
			if (fAmountSkippedInitially > 0)
			{
				stringRep.append("\nAmount currently skipped initially: " + 
					fAmountSkippedInitially);
			}
			
			if (fAmountKept > 0)
			{
				stringRep.append("\nAmount currently kept: " + fAmountKept);
			}
			
			if (fAmountSkippedFinally > 0)
			{
				stringRep.append("\nAmount currently skipped finally: " + 
					fAmountSkippedFinally);
			}
			
			stringRep.append("\nBasisRequester state: " + fStateModel);
			
			if (! fSatisfiedRequests.isEmpty())
			{
				stringRep.append("\nHas satisfied request:\n" + 
					fSatisfiedRequests);
			}
			
			if (! fPendingData.isEmpty())
			{
				stringRep.append("\nHas pending data:\n" + 
					fPendingData);
			}
		}
		else
		{
			stringRep.append("BasisRequester " + super.toString() + 
				" has been killed");
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: IntervalBasisRequester.java,v $
//	Revision 1.4  2006/01/02 18:30:31  tames
//	Updated to reflect depricated BasisRequestAmount class. Uses Amount instead.
//	
//	Revision 1.3  2005/09/20 19:59:50  mn2g
//	a fix for the .setInitialSkipAmount bug
//	
//	Revision 1.2  2005/09/13 22:28:58  tames
//	Changes to refect BasisBundleEvent refactoring.
//	
//	Revision 1.1  2005/09/09 21:37:10  tames
//	Renamed this class. Was the old DefaultBasisRequester that is probably obsolete with the refactoring of BasisRequester classes.
//	
//	Revision 1.63  2005/08/26 22:13:30  tames_cvs
//	Changes that are an incomplete refactoring. Also added initial support for history.
//	
//	Revision 1.62  2005/07/15 19:22:45  chostetter_cvs
//	Organized imports
//	
//	Revision 1.61  2005/07/14 22:01:40  tames
//	Refactored data package for performance.
//	
//	Revision 1.60  2005/05/24 21:12:23  chostetter_cvs
//	Fixed some issues involving changing BasisRequests at run time
//	
//	Revision 1.59  2005/04/16 04:04:21  tames
//	Changes to reflect refactored state and activity packages.
//	
//	Revision 1.58  2005/04/12 22:15:16  chostetter_cvs
//	Fixed ordering, synchronization issues with including pending data on stop
//	
//	Revision 1.57  2005/04/12 20:36:06  chostetter_cvs
//	Added option to promote pending data to satisfying data on stop
//	
//	Revision 1.56  2005/04/05 21:22:09  chostetter_cvs
//	Fixed problem with DataRequester passing on BasisSetEvents to its listeners.
//	
//	Revision 1.55  2005/04/05 20:35:35  chostetter_cvs
//	Fixed problem with release status of BasisSets from which a copy was made; fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//	
//	Revision 1.54  2005/04/05 19:07:36  chostetter_cvs
//	Releases any BasisSets received while not started
//	
//	Revision 1.53  2005/04/04 15:40:58  chostetter_cvs
//	Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//	
//	Revision 1.52  2005/03/24 21:54:28  chostetter_cvs
//	Fixed problem with shrinking BasisSets having fewer valid samples than requested, and with calculating spanned Basis values for BasisSets that decrease monotonically (rather than increase).
//	
//	Revision 1.51  2005/03/24 18:16:34  chostetter_cvs
//	Fixed problem with releasing unused portions of BasisSets, changed waiting on DataRequestSatisfier repetitions to reach zero to waiting for stop
//	
//	Revision 1.50  2005/03/23 21:05:13  chostetter_cvs
//	Fixed error with marking concatenated copied BasisSets as copies
//	
//	Revision 1.49  2005/03/22 22:47:06  chostetter_cvs
//	Refactoring of BasisSet allocation, release
//	
//	Revision 1.48  2005/03/17 00:43:02  tames
//	Changed the receiveDataEvent method not to set a
//	component exception state. The caller will have to handle any
//	exceptions.
//	
//	Revision 1.47  2005/03/17 00:23:38  chostetter_cvs
//	Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//	
//	Revision 1.46  2005/03/16 18:54:51  chostetter_cvs
//	Refactored BasisRequestAmount
//	
//	Revision 1.45  2005/03/15 00:36:02  chostetter_cvs
//	Implemented covertible Units compliments of jscience.org packages
//	
//	Revision 1.44  2005/03/04 18:45:40  chostetter_cvs
//	Can now make both blocking and non-blocking satisfaction requests
//	
//	Revision 1.43  2005/03/01 23:25:15  chostetter_cvs
//	Revised Queue design and package for better blocking behavior
//	
//	Revision 1.42  2005/02/25 23:26:50  tames_cvs
//	Checkins for Carl related to a Thread deadlock condition when starting
//	and stopping Requesters.
//	
//	Revision 1.41  2005/02/24 23:46:40  chostetter_cvs
//	Really fixed starting/stopping/synchronization behavior
//	
//	Revision 1.40  2005/02/03 01:05:47  chostetter_cvs
//	Fixed inconsistent use of null to indicate no input data buffer filtering
//	
//	Revision 1.39  2005/02/01 20:53:54  chostetter_cvs
//	Revised releasable BasisSet design, release policy
//	
//	Revision 1.38  2005/01/31 21:35:30  chostetter_cvs
//	Fix for data request sequencing, documentation
//	
//	Revision 1.37  2005/01/29 00:06:54  chostetter_cvs
//	Downsampling, integration no longer mutually exclusive
//	
//	Revision 1.36  2005/01/28 23:59:43  chostetter_cvs
//	Tweak to name
//	
//	Revision 1.35  2005/01/28 23:57:34  chostetter_cvs
//	Added ability to integrate DataBuffers and requests, renamed average value
//	
//	Revision 1.34  2005/01/27 21:38:02  chostetter_cvs
//	Implemented new exception state and default exception behavior for Objects having ActivityState
//	
//	Revision 1.33  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.32  2004/11/10 19:31:28  tames
//	Added a getDownsamplingRate method
//	
//	Revision 1.31  2004/11/09 20:04:47  smaher_cvs
//	Added check for monotonically in(de)creasing basis buffer values in certain cases.
//	If this is not the case, a message is generated (to help with debugging).
//	
//	Revision 1.30  2004/11/08 18:37:52  chostetter_cvs
//	Basis sequence boundary behavior now configurable
//	
//	Revision 1.29  2004/09/15 15:12:53  chostetter_cvs
//	Now allows for intelligent copying and/or concatenation of satisfied requests in approrpiate circumstances
//	
//	Revision 1.27  2004/09/13 20:46:02  chostetter_cvs
//	More buffer-name related fixes
//	
//	Revision 1.26  2004/09/02 19:39:57  chostetter_cvs
//	Initial data-description redesign work
//	
//	Revision 1.25  2004/08/19 18:05:39  chostetter_cvs
//	Tweaks addressing copied new data
//	
//	Revision 1.24  2004/07/29 16:55:49  chostetter_cvs
//	Pending data no longer re-copied
//	
//	Revision 1.23  2004/07/28 22:14:29  chostetter_cvs
//	Fixed data copying bug (needed to flip Buffer)
//	
//	Revision 1.22  2004/07/28 19:11:25  chostetter_cvs
//	BasisBundle now alerts of impending discontinuity, BasisRequester copies and releases pending data at discontinuity
//	
//	Revision 1.21  2004/07/24 02:46:11  chostetter_cvs
//	Added statistics calculations to DataBuffers, renamed some classes
//	
//	Revision 1.20  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.19  2004/07/20 02:37:35  chostetter_cvs
//	More real-valued boundary condition work
//	
//	Revision 1.18  2004/07/19 23:47:50  chostetter_cvs
//	Real-valued boundary conidition work
//	
//	Revision 1.17  2004/07/19 19:00:38  chostetter_cvs
//	Implemented requests by basis amount
//	
//	Revision 1.16  2004/07/19 14:16:14  chostetter_cvs
//	Added ability to subsample data in requests
//	
//	Revision 1.15  2004/07/18 05:14:02  chostetter_cvs
//	Refactoring of data classes
//	
//	Revision 1.14  2004/07/16 21:35:24  chostetter_cvs
//	Work on declaring uniform sample rate of data
//	
//	Revision 1.13  2004/07/16 15:18:31  chostetter_cvs
//	Revised, refactored Component activity state
//	
//	Revision 1.12  2004/07/16 00:23:20  chostetter_cvs
//	Refactoring of DataSpace, Output wrt BasisBundle collections
//	
//	Revision 1.11  2004/07/15 21:30:00  chostetter_cvs
//	Debugged final skip, maintenance of skip-keep-skip statistics
//	
//	Revision 1.10  2004/07/15 05:44:38  chostetter_cvs
//	Mods to determining new BasisSet structure
//	
//	Revision 1.9  2004/07/15 04:26:43  chostetter_cvs
//	Debugged initial skip, boundary condition on free
//	
//	Revision 1.8  2004/07/14 22:24:53  chostetter_cvs
//	More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//	
//	Revision 1.7  2004/07/14 14:36:40  chostetter_cvs
//	More tweaks
//	
//	Revision 1.6  2004/07/14 00:33:49  chostetter_cvs
//	More Algorithm, data testing. Fixed slice bug.
//	
//	Revision 1.5  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.3  2004/07/11 07:30:35  chostetter_cvs
//	More data request work
//	
//	Revision 1.2  2004/07/09 22:29:11  chostetter_cvs
//	Extensive testing of Input/Output interaction, supports simple BasisRequests
//	
//	Revision 1.1  2004/07/06 21:57:12  chostetter_cvs
//	More BasisRequester, DataRequester work
//	
//	Revision 1.1  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.2  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.1  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
