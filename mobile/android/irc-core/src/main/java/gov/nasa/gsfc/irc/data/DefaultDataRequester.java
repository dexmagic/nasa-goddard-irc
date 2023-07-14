//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/DefaultDataRequester.java,v 1.79 2006/08/01 19:55:47 chostetter_cvs Exp $
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**/

import gov.nasa.gsfc.commons.processing.activity.ActivityStateModel;
import gov.nasa.gsfc.commons.processing.activity.DefaultActivityStateModel;
import gov.nasa.gsfc.commons.processing.creation.AbstractCreator;
import gov.nasa.gsfc.commons.properties.state.State;
import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.commons.types.namespaces.MembershipEvent;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.BasisBundleListener;
import gov.nasa.gsfc.irc.data.events.DataListener;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.data.events.DataSpaceEvent;
import gov.nasa.gsfc.irc.data.events.DataSpaceListener;


/**
 * A DataRequester contains and manages a Set of BasisRequests representing a
 * coordinated request for data across one or more BasisBundles, and which is to
 * be satisfied jointly in all its individual requests. A DataRequester can
 * further be configured to issue and satisfy its current composite request
 * continuously or only a specified number of times; and reports the result of
 * each such request to any interested listeners packaged in a DataSet.
 * <p>
 * Note that A DataRequester must be started before it will issue and satisfy
 * its Set of BasisRequests.
 * <p>
 * A DefaultDataRequester permits only one BasisRequest per BasisBundle. Adding
 * a BasisRequest for a BasisBundle will replace whatever BasisRequest may
 * previously have been associated with that BasisBundle.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Carl F. Hostetter
 */
public class DefaultDataRequester extends AbstractCreator 
	implements DataRequester, BasisBundleListener
{
	private static final String CLASS_NAME = DefaultDataRequester.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private ActivityStateModel fStateModel = new DefaultActivityStateModel();
	
	private HasName fSource;
	
	private Map fBasisRequestsByBasisBundleId = 
		Collections.synchronizedMap(new HashMap());
	private Map fBasisRequestersByBasisBundleId = 
		Collections.synchronizedMap(new HashMap());
	
	private List fDataRequesterListeners = new CopyOnWriteArrayList();
	private List fBasisRequesters = new CopyOnWriteArrayList();
	
	private DataRequestSatisfactionRuleType fSatisfactionRule = 
		DataRequestSatisfactionRuleType.ALL;
	
	private boolean fIsContinuous = true;
	private int fRepetition = 0;
	private int fRemainingRepetitions = 0;
	private int fDownsamplingRate = 0;
	
	private Collection fSatisfyingBasisSets;
	private DefaultDataSet fDataSet;
	
	private DataRequestSatisfier fDataRequestSatisfier = new DataRequestSatisfier();
	private Thread fDataRequestSatisfierThread;
	private PendingRequestHandler fPendingRequestHandler = new PendingRequestHandler();

	/**
	 * A DataRequestSatisfier is a Runnable that manages the satisfaction of the
	 * composite set of BasisRequests for a DataRequester.
	 * 
	 * @version $Date: 2006/08/01 19:55:47 $
	 * @author Carl F. Hostetter
	 */
	private class DataRequestSatisfier implements Runnable 
	{
		/**
		 * Causes this DataRequestSatisfier to satisfy the composite set of
		 * BasisRequests of this DataRequester.
		 */
		public void run()
		{
			if (! fIsContinuous)
			{
				fRemainingRepetitions = fRepetition;
			}
			
			if (isStarted())
			{
				declareActive();
			}
			
			boolean interrupted = false;
			
			while (! interrupted && isStarted() && 
				(fIsContinuous || fRemainingRepetitions > 0))
			{
				// Iterate through the current set of BasisRequesters, blocking on 
				// each until it returns a BasisSet that satisfies its individual 
				// request.
				
				if (fBasisRequestersByBasisBundleId.isEmpty())
				{
					// Wait until we have a request
					synchronized (fBasisRequestersByBasisBundleId)
					{
						if (fBasisRequestersByBasisBundleId.isEmpty())
						{
							try
							{
								fBasisRequestersByBasisBundleId.wait();
							}
							catch (InterruptedException ex)
							{
								interrupted = true;
							}
						}
					}
				}
				
				if (! interrupted)
				{
					int numBasisRequesters = 
						fBasisRequestersByBasisBundleId.size();
					
					if (fSatisfyingBasisSets == null)
					{
						fSatisfyingBasisSets = 
							new ArrayList(numBasisRequesters);
					}
					else
					{
						fSatisfyingBasisSets.clear();
					}
					
					Object[] basisRequesters = 
						fBasisRequestersByBasisBundleId.values().toArray();
					
					int numRequests = basisRequesters.length;
					
					boolean block = false;
					boolean done = false;
					
					if ((fSatisfactionRule == 
							DataRequestSatisfactionRuleType.ALL) || 
						((fSatisfactionRule == 
							DataRequestSatisfactionRuleType.FIRST)))
					{
						block = true;
					}
					
					while (!interrupted && !done && isStarted())
					{
						for (int i=0; i < numRequests && isStarted(); i++)
						{
							BasisSet satisfyingBasisSet = null;
							try
							{
								satisfyingBasisSet = collectBasisSet(
										(BasisRequester) basisRequesters[i], 
										block);
								
								if (getDataRequestSatisfactionRule() == 
									DataRequestSatisfactionRuleType.FIRST)
								{
									block = false;
								}
							}
							catch (InterruptedException e1)
							{
								// TODO Auto-generated catch block
								interrupted = true;
							}

							// The result should only be null in the case that
							// a BasisRequester is stopped.

							if (satisfyingBasisSet != null)
							{
								fSatisfyingBasisSets.add(satisfyingBasisSet);
							}
						}
						
						if (! fSatisfyingBasisSets.isEmpty())
						{
							done = true;
						}
						else
						{
							try
							{
								Thread.sleep(10);
							}
							catch (InterruptedException e)
							{
								interrupted = true;
							}
						}
					}
				}
					
				// If we weren't interrupted or stopped while waiting for the 
				// BasisRequests to be satisfied, then at this point they have all 
				// been satisfied, so we package up the satisfying BasisSets into a 
				// DataSet and ship it out.
				
				if (! interrupted && isStarted())
				{
					fDataSet = new DefaultDataSet
						(getMemberId(), fSatisfyingBasisSets);
					
					fSatisfyingBasisSets.clear();
					
					if (! fIsContinuous)
					{
						fRemainingRepetitions--;
					}
					
					sendToListeners(fDataSet);
					
					// After our listeners have received the data, we can 
					// release its memory for reuse.
					
					if (fDataSet != null)
					{
						// The DataSet can become null if we are stopped 
						fDataSet.release();
						fDataSet = null;
					}
				}
			}
			
			// If we reach this point and our set of satisfying BasisSets is not 
			// empty, then we were interrupted or stopped while satisfying requests, 
			// and so need to release the BasisSets we did get for reuse.
			
			if ((fSatisfyingBasisSets != null) && 
				(fSatisfyingBasisSets.size() > 0))
			{
				Iterator satisfyingBasisSets = 
					((Collection) ((ArrayList) fSatisfyingBasisSets).clone()).iterator();
				fSatisfyingBasisSets.clear();
				
				while (satisfyingBasisSets.hasNext())
				{
					BasisSet satisfyingBasisSet = 
						(BasisSet) satisfyingBasisSets.next();
					
					satisfyingBasisSet.release();
				}
				
			}
			
			if (fDataSet != null)
			{
				fDataSet.release();
				fDataSet = null;
			}
			
			stop();
		}
	}
	
	/**
	 * Default constructor of a DefaultDatasetRequester.
	 **/
	public DefaultDataRequester()
	{
		this("Anonymous");
	}
		
	/**
	 * Constructs a new DefaultDataRequester that has the given name.
	 * 
	 * @param name The desired name of the new DefaultDataRequester
	 */
	public DefaultDataRequester(String name)
	{
		super(name);
	}
		
	/**
	 * Constructs a new DefaultDataRequester that acts as a delegate for the
	 * given source.
	 * 
	 * @param source The DataSetSource for which the new DefaultDataRequester
	 *            will act as a proxy
	 */
	public DefaultDataRequester(HasName source)
	{
		this(source.getName());
		
		fSource = source;
	}
		
	//----------------------------------------------------------------------
	//		Object-related methods
	//----------------------------------------------------------------------
		
	/**
	 * Returns a String representation of this DataRequester.
	 * 
	 * @return A String representation of this DataRequester
	 */
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer
			("DataRequester State: " + fStateModel);
		
		if (! isKilled())
		{
			int numBasisRequests = fBasisRequestersByBasisBundleId.size();
			
			if (numBasisRequests == 0)
			{
				stringRep.append("\nNo BasisRequests");
			}
			else
			{
				stringRep.append("\n" + numBasisRequests + 
					" BasisRequest(s):");
				
				Object[] basisRequesters = 
					fBasisRequestersByBasisBundleId.values().toArray();
				
				for (int i = 0; i < basisRequesters.length; i++)
				{
					stringRep.append("\n> " + basisRequesters[i]);
				}
			}
			
			stringRep.append("\nSatisfaction rule: " + fSatisfactionRule);
		}
		
		return (stringRep.toString());
	}
	
	//----------------------------------------------------------------------
	//	State-related methods
	//----------------------------------------------------------------------
	
	/**
	 * Returns the current State of this DataRequester.
	 * 
	 * @return The current State of this DataRequester
	 */ 
	public State getState()
	{
		return (fStateModel.getState());
	}	
	
	/**
	 * Causes this DataRequester to start issuing and satisfying its Set of
	 * BasisRequests.
	 */
	public synchronized void start()
	{
		if (! isKilled() && ! isStarted())
		{
			try
			{
				fStateModel.start();
				
				startBasisRequesters();
				
				if (fDataRequestSatisfierThread == null)
				{
					fDataRequestSatisfierThread = 
						new Thread(fDataRequestSatisfier, 
							"DataRequestSatisfier for " + getName());
				}
				
				fDataRequestSatisfierThread.start();
			}
			catch (Exception ex)
			{
				declareException(ex);
			}
		}
	}
	
	/**
	 * Returns true if this DataRequester is currently started, false otherwise.
	 * A BasisRequester is also considered started if it is active or waiting.
	 * 
	 * @return True if this DataRequester is currently started, false otherwise
	 */	 
	public boolean isStarted()
	{
		return (fStateModel.isStarted());
	}

	/**
	 * Sets this DataRequester's State to active. A DataRequester cannot become
	 * active unless it is currently started.
	 */
	protected void declareActive()
	{
		fStateModel.declareActive();
	}
	
	/**
	 * Returns true if this DataRequester is active, false otherwise.
	 *
	 * @return True if this DataRequester is active, false otherwise
	 **/
	public boolean isActive()
	{
		return (fStateModel.isActive());
	}

	/**
	 * Sets this DataRequester's State to waiting. A DataRequester cannot become
	 * waiting unless it is currently active.
	 */
	protected void declareWaiting()
	{
		fStateModel.declareWaiting();
	}
	
	/**
	 * Returns true if this DataRequester is waiting, false otherwise.
	 *
	 * @return True if this DataRequester is waiting, false otherwise
	 **/
	public boolean isWaiting()
	{
		return (fStateModel.isWaiting());
	}
	
	/**
	 * Causes this DataRequester to stop, if it is currently started.
	 */
	public synchronized void stop()
	{
		if (isStarted())
		{
			stopBasisRequesters();
			
			synchronized (fDataRequestSatisfierThread)
			{
				fStateModel.stop();
				
				if (! fDataRequestSatisfierThread.isInterrupted())
				{
					fDataRequestSatisfierThread.interrupt();
				}
				
				if ((fSatisfyingBasisSets != null) && 
					(fSatisfyingBasisSets.size() > 0))
				{
					Iterator remainingBasisSets = fSatisfyingBasisSets.iterator();
					
					while (remainingBasisSets.hasNext())
					{
						BasisSet basisSet = (BasisSet) remainingBasisSets.next();
						
						basisSet.release();
					}
					
					fSatisfyingBasisSets.clear();
				}
				else if (fDataSet != null)
				{
					fDataSet.release();
					fDataSet = null;
				}
			}
			
			// TODO investigate how to restart an interrupted thread instead
			// of discarding the old one.
			fDataRequestSatisfierThread = null;
			
			synchronized (fStateModel)
			{
				fStateModel.notifyAll();
			}
		}
	}
	
	/**
	 * Returns true if this DataRequester is stopped (i.e., not started), false
	 * otherwise.
	 * 
	 * @return True if this DataRequester is stopped (i.e., not started), false
	 *         otherwise
	 */
	public boolean isStopped()
	{
		return (fStateModel.isStopped());
	}
	
	/**
	 * Sets this DataRequester to the exception state, due to the given
	 * Exception. This DataRequester is first stopped, and then enters the
	 * exceptio state.
	 * 
	 * @param exception The Exception resulting in the exception state
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
	 * Clears the current Exception (if any) from this DataRequester.
	 */
	public void clearException()
	{
		fStateModel.clearException();
	}
	
	/**
	 * Returns true if this DataRequester is in an exception state, false
	 * otherwise.
	 * 
	 * @return True if this DataRequester is in an exception state, false
	 *         otherwise
	 */
	public boolean isException()
	{
		return (fStateModel.isException());
	}
	
	/**
	 * If this DataRequester is in an exception state, this method will return
	 * the Exception that caused it. Otherwise, it returns null.
	 * 
	 * @return The Exception that caused the exception state
	 */
	public Exception getException()
	{
		return (fStateModel.getException());
	}
	
	/**
	 * Causes this DataRequester to immediately cease operation and release any
	 * allocated resources. A killed DataRequester cannot subsequently be
	 * started or otherwise reused.
	 */
	public synchronized void kill()
	{
		if (! isKilled())
		{
			if (fDataRequestSatisfierThread != null)
			{
				// Kill the thread now to release the lock below
				fDataRequestSatisfierThread.interrupt();
				fDataRequestSatisfierThread = null;
			}

			stopBasisRequesters();
			removeBasisRequests();
			
			fStateModel.kill();
			
			fIsContinuous = false;
			fRepetition = 0;
			fRemainingRepetitions = 0;
			
			fDataRequestSatisfier = null;
			
			fBasisRequestsByBasisBundleId = null;
			
			fDataRequesterListeners.clear();
			
			fSource = null;
			
			fBasisRequestersByBasisBundleId = null;
		}
	}
	
	/**
	 * Returns true if this DataRequester is killed, false otherwise.
	 * 
	 * @return True if this DataRequester is killed, false otherwise
	 */	
	public boolean isKilled()
	{
		return (fStateModel.isKilled());
	}
		
	/**
	 * Adds the given StateChangeListener as a listener for changes in 
	 * any ActivityState of this DataRequester.
	 *
	 * @param listener A StateChangeListener
	 **/	
	public void addStateListener(PropertyChangeListener listener)
		throws IllegalArgumentException
	{
		fStateModel.addStateListener(listener);
	}

	/**
	 * Returns the set of StateChangeListeners on this DataRequester as an array
	 * of PropertyChangeListeners.
	 * 
	 * @return the Set of StateChangeListeners on this DataRequester as an array
	 *         of PropertyChangeListeners
	 */
	public PropertyChangeListener[] getStateListeners()
	{
		return (fStateModel.getStateListeners());
	}
	
	/**
	 * Removes the given StateChangeListener as a listener for changes in 
	 * any ActivityState of this DataRequester.
	 *
	 * @param listener A StateChangeListener
	 **/
	public void removeStateListener(PropertyChangeListener listener)
		throws IllegalArgumentException
	{
		fStateModel.removeStateListener(listener);
	}	
	
	//----------------------------------------------------------------------
	//	BasisRequest-related methods
	//----------------------------------------------------------------------
	
	protected BasisSet collectBasisSet(BasisRequester requester, boolean block)
	throws InterruptedException
	{
		BasisSet satisfyingBasisSet = null;

		if (block)
		{
			satisfyingBasisSet = 
				requester.blockingSatisfyRequest();			
		}
		else
		{
			satisfyingBasisSet = 
				requester.satisfyRequest();
		}

		return satisfyingBasisSet;
	}
	
	/**
	 * Releases all currently queued input data.
	 */
	protected void releaseQueuedData()
	{
		for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
		{
			((BasisRequester) iter.next()).clear();
		}
	}
		
	/**
	 * Returns the Set of BasisRequests currently managed by this DataRequester.
	 * 
	 * @return The Set of BasisRequests currently managed by this DataRequester
	 */	 
	public BasisRequest[]  getBasisRequests()
	{
		BasisRequest[] requests = new BasisRequest[0];
		return ((BasisRequest[]) fBasisRequestsByBasisBundleId.values()
				.toArray(requests));
	}	
	
	/**
	 * Returns the (at most one) BasisRequest currently managed by this
	 * DataRequester that is made on the BasisBundle specified by the given
	 * BasisBundleId. If there is no such BasisRequest, the result is null.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            BasisRequests to get are made
	 * @return The Set of BasisRequests currently managed by this DataRequester
	 *         that are made on the BasisBundle specified by the given
	 *         BasisBundleId
	 */	 
	public BasisRequest getBasisRequest(BasisBundleId basisBundleId)
	{
		return ((BasisRequest) fBasisRequestsByBasisBundleId.get(basisBundleId));
	}
	
	/**
	 * Returns the Set of BasisRequests associated with this Object that are
	 * made on the BasisBundle specified by the given BasisBundleId. For a
	 * DefaultDataRequester, there will be at most one BasisRequest in the
	 * resulting Set.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            BasisRequests to get are made
	 * @return The Set of BasisRequests associated with this Object that are
	 *         made on the BasisBundle specified by the given BasisBundleId
	 */
	public Collection getBasisRequests(BasisBundleId basisBundleId)
	{
		Collection result = new ArrayList();
		
		BasisRequest request = (BasisRequest) 
			fBasisRequestsByBasisBundleId.get(basisBundleId);
		
		result.add(request);
		
		return (result);
	}
	
	/**
	 * Adds the given BasisRequest to the Set of BasisRequests managed by this
	 * DataRequester. If this DataRequester is not already a listener on the
	 * BasisBundle indicated in the given BasisRequest, it will add itself to
	 * the BasisBundle as a listener.
	 * 
	 * @param basisRequest A BasisRequest
	 */	 
	public void addBasisRequest(BasisRequest basisRequest)
	{
		if (! isKilled())
		{
			if (basisRequest != null)
			{
				BasisBundleId basisBundleId = basisRequest.getBasisBundleId();
				
				BasisBundle basisBundle = 
					Irc.getDataSpace().getBasisBundle(basisBundleId);
				
				if (basisBundle != null)
				{
					// Request is for an existing BasisBundle so add it.
					addBasisRequest0(basisRequest);
				}
				else
				{
					// If the BasisBundle is not available then set up a timer to 
					// repeatedly try again until the BasisBundle shows up in the 
					// DataSpace.
					String message = 
						"Basis bundle " + basisRequest.getBasisBundleId() 
						+ " is not available";
					
					sLogger.logp
						(Level.FINE, CLASS_NAME, "setBasisRequest", message);
					
					fPendingRequestHandler.addPendingRequest(basisRequest);
				}
			}
		}
	}
	
	/**
	 * Adds the BasisRequest for a BasisBundle that is known to exist.
	 * 
	 * @param basisRequest A BasisRequest
	 */	 
	private synchronized void addBasisRequest0(BasisRequest basisRequest)
	{
		BasisBundleId basisBundleId = basisRequest.getBasisBundleId();

		if (fBasisRequestsByBasisBundleId.get(basisBundleId) != null)
		{
			throw new IllegalArgumentException("There is already a basis request registered "
					+ "with this data requester for basis bundle id " + basisBundleId);
		}
			
		// First, cache the given BasisRequest for quick retrieval.
		fBasisRequestsByBasisBundleId.put(basisBundleId, basisRequest);
		
		BasisRequester requester = 
			(BasisRequester) fBasisRequestersByBasisBundleId.get(basisBundleId);
		
		if (requester != null)
		{
			// If we already have a BasisRequester for the
			// BasisBundle indicated by the given BasisRequest,
			// then we need to set the BasisRequest of that
			// BasisRequester to the given BasisRequest.
			
			requester.setBasisRequest(basisRequest);
		}
		else
		{
			// Otherwise, we create a new BasisRequester for the
			// given BasisRequest, and add it to our BasisRequesters.
			
			requester = createBasisRequester(basisRequest);
			
			synchronized (fBasisRequestersByBasisBundleId)
			{
				fBasisRequestersByBasisBundleId.put(basisBundleId, requester);
				
				if (fBasisRequestersByBasisBundleId.size() == 1)
				{
					fBasisRequestersByBasisBundleId.notify();
				}
			}
			
			// Add any current DataRequesterListeners as
			// BasisBundleEventListeners on the new BasisRequester,
			// so that they can receive BasisBundleEvents
			// indicating the structure of the BasisRequester's
			// BasisBundle.
			
			for (Iterator iter = fDataRequesterListeners.iterator(); iter.hasNext();)
			{
				requester.addBasisBundleListener((DataListener) iter.next());
			}
			
			fBasisRequesters.add(requester);
			
			// If DataRequester is started thwn we need to start the new
			// requester.
			
			if (isStarted())
			{
				requester.start();
			}
		}
	}
	
	/**
	 * Creates a new BasisRequester based on the BasisRequest. This
	 * implementation utilizes the <code>BasisRequesterFactory</code> returned
	 * by the {@link Irc#getBasisRequesterFactory()} method.
	 * 
	 * @param basisRequest the BasisRequest to satisfy.
	 * @return a new BasisRequester.
	 */
	protected BasisRequester createBasisRequester(BasisRequest basisRequest)
	{
		return Irc.getBasisRequesterFactory().createBasisRequester(basisRequest);
	}

	/**
	 * Sets the given array of BasisRequests managed
	 * by this DataRequester.
	 * 
	 * @param basisRequests the array of BasisRequests
	 */
	public void setBasisRequests(BasisRequest[] basisRequests)
	{
		if (! isKilled())
		{
			removeBasisRequests();
			
			if (basisRequests != null)
			{
				for (int i = 0; i < basisRequests.length; i++)
				{
					addBasisRequest(basisRequests[i]);
				}
			}
		}
	}
	
	/**
	 * Removes all BasisRequests from this DataRequester.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which all
	 *            BasisRequests are to be removed
	 */
	public void removeBasisRequests()
	{
		for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
		{
			((BasisRequester) iter.next()).stop();
		}
		
		fBasisRequesters.clear();
		fBasisRequestsByBasisBundleId.clear();
		fBasisRequestersByBasisBundleId.clear();
		fPendingRequestHandler.clearPending();
	}
	
	/**
	 * Removes all BasisRequests from this DataRequester that are made on the
	 * BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which all
	 *            BasisRequests are to be removed
	 */
	public synchronized void removeBasisRequests(BasisBundleId basisBundleId)
	{
		if (! isKilled())
		{
			if ((basisBundleId != null) && 
				fBasisRequestsByBasisBundleId.containsKey(basisBundleId))
			{
				boolean wasStarted = isStarted();
				
				if (isStarted())
				{
					stop();
				}
				
				fBasisRequestsByBasisBundleId.remove(basisBundleId);

				BasisRequester requester = (BasisRequester)
					fBasisRequestersByBasisBundleId.get(basisBundleId);
				
				if (requester != null)
				{
					requester.stop();
					fBasisRequesters.remove(requester);
				}
				
				fBasisRequestersByBasisBundleId.remove(basisBundleId);
				
				if (wasStarted)
				{
					start();
				}
			}
			
			fPendingRequestHandler.removePending(basisBundleId);
		}
	}
	
	/**
	 * Removes the given BasisRequest from the Set of BasisRequests managed by
	 * this DataRequester.
	 * 
	 * @param basisRequest The BasisRequest to remove
	 */
	public void removeBasisRequest(BasisRequest basisRequest)
	{
		if (! isKilled())
		{
			if (basisRequest != null)
			{
				BasisBundleId basisBundleId = basisRequest.getBasisBundleId();
				
				if (basisBundleId != null)
				{
					removeBasisRequests(basisBundleId);
				}
			}
		}
	}
	
	/**
	 * Starts all of the BasisRequesters of this DataRequester.
	 */	 
	private void startBasisRequesters()
	{
		for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
		{
			((BasisRequester) iter.next()).start();
		}
	}
	
	/**
	 * Stops all of the BasisRequesters of this DataRequester.
	 */
	private void stopBasisRequesters()
	{
		for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
		{
			((BasisRequester) iter.next()).stop();
		}
	}
	
	/**
	 * Sets the DataRequestSatisfactionRuleType indicator according to which
	 * this DataRequester is to satisfy its current Set of BasisRequests to the
	 * given DataRequestSatisfactionRuleType.
	 * 
	 * @param rule The DataRequestSatisfactionRuleType indicator according to
	 *            which this DataRequester is to satisfy its current Set of
	 *            BasisRequests
	 */
	public void setDataRequestSatisfactionRule(DataRequestSatisfactionRuleType rule)
	{
		fSatisfactionRule = rule;
	}
	
	/**
	 * Returns the DataRequestSatisfactionRuleType indicator according to which
	 * this DataRequester is currently configured to satisfy its current Set of
	 * BasisRequests.
	 * 
	 * @return The DataRequestSatisfactionRuleType indicator according to which
	 *         this DataRequester is currently configured to satisfy its current
	 *         Set of BasisRequests
	 */
	public DataRequestSatisfactionRuleType getDataRequestSatisfactionRule()
	{
		return (fSatisfactionRule);
	}
	
	/**
	 * Sets the repetition of this DataRequester to the given value. If this
	 * DataRequester is not currently continuous, then the repetition defines
	 * the number of requests that the DataRequester will issue when it is
	 * started.
	 * 
	 * @param numRepetitions The number of repetitions this DataRequester should
	 *            make when started
	 */
	public void setRepetition(int numRepetitions)
	{
		if (! isKilled())
		{
			fRepetition = numRepetitions;
			
			if (fRepetition < 0)
			{
				fRepetition = 0;
			}
			else
			{
				fIsContinuous = false;
				fRemainingRepetitions = fRepetition;
			}
		}
	}
		
	/**
	 * Returns the number of request repetitions this DataRequester will make
	 * when it is next started. If this DataRequester is currently continuous,
	 * then the result will be 0.
	 * 
	 * @param numRepetitions The number of repetitions this DataRequester will
	 *            make when started
	 */
	public int getRepetition()
	{
		return (fRepetition);
	}
	
	/**
	 * Sets the downsampling rate of all of the BasisRequesters of this 
	 * DataRequester to the given rate. To indicate no downsampling, set this rate 
	 * to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of the BasisRequesters of this 
	 * 		DataRequester
	 */
	public void setDownsamplingRate(int downsamplingRate)
	{
		for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
		{
			((BasisRequester) iter.next()).setDownsamplingRate(downsamplingRate);
		}

		fDownsamplingRate = downsamplingRate;
	}
	
	/**
	 * Gets the downsampling rate of the BasisRequester of this 
	 * DataRequester.
	 * 
	 * @return The downsampling rate of the BasisRequesters of this 
	 * 		DataRequester
	 */
	public int getDownsamplingRate()
	{
		return fDownsamplingRate;
	}

	/**
	 * After this DataRequester has been started, this will return the current
	 * number of unsatisfied request repetitions remaining for this
	 * DataRequester to satisfy. If this DataRequester has not yet been started,
	 * the result is the same as the current repetition value. In either case,
	 * if this DataRequester is currently continuous, then the result will
	 * always be 0.
	 * 
	 * @return The current number of unsatisfied request repetitions remaining
	 *         for this DataRequester to satisfy
	 */
	public int getRemainingRepetitions()
	{
	   return (fRemainingRepetitions);
	}
	
	/**
	 * Blocks on the calling Thread until this DataRequester has stopped.
	 * 
	 * @throws InterruptedException if the wait is interrupted by another Thread
	 */
	public void waitUntilStopped()
		throws InterruptedException
	{
		synchronized (fStateModel)
		{
			while (! fStateModel.isStopped())
			{
				fStateModel.wait();
			}
		}
	}
		
	/**
	 * Causes this DataRequester to repeat its current Set of BasisRequests
	 * indefinitely, once it is started and while not paused. To put this
	 * DataRequester back out of continuous mode, use
	 * <code>setRepeitition</code> to set a finite number of repetitions.
	 */ 
	public void setContinuous()
	{
		if (! isKilled())
		{
			fIsContinuous = true;
			fRepetition = 0;
			fRemainingRepetitions = 0;
		}
	}	
	
	/**
	 * Returns true if this DataRequester is currently in continuous mode, false
	 * otherwise.
	 * 
	 * @return True if this DataRequester is currently in continuous mode, false
	 *         otherwise
	 */ 
	public boolean isContinuous()
	{
		return (fIsContinuous);
	}
	
	//----------------------------------------------------------------------
	//	BasisBundleEvent-related methods
	//----------------------------------------------------------------------
	
	/**
	 * Causes this BasisBundleListener to receive the given BasisBundleEvent.
	 * 
	 * @param event A BasisBundleEvent
	 */
	public void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		// This implementation does not listen to BasisBundles directly (it uses 
		// BasisRequesters instead), so we won't receive any BasisBundleEvents.
	}
	
	//----------------------------------------------------------------------
	//	DataSet-related methods
	//----------------------------------------------------------------------
	
	/**
	 * Sends the given DataSet to each of the current DataRequesterListeners of 
	 * this DataRequester.
	 * 
	 * @param dataSet A DataSet
	 */
	protected void sendToListeners(DataSet dataSet)
	{
		DataSetEvent event = new DataSetEvent(fSource, dataSet);

		for (Iterator iter = fDataRequesterListeners.iterator(); iter.hasNext();)
		{
			((DataListener) iter.next()).receiveDataSetEvent(event);
		}
	}

	/**
	 * Adds the given DataListener as a listener for DataEvents 
	 * from this DataRequester.
	 *
	 * @param listener A DataListener
	 **/
	public void addDataListener(DataListener listener)
	{
		if (! isKilled())
		{
			// Add the given DataListener as a
			// BasisBundleEventListener to each of the current 
			// BasisRequesters, so that it can receive
			// events from the BasisBundle associated with each
			// BasisRequester indicating the structure of the BasisBundle 
			// (and changes therein).
			for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
			{
				((BasisRequester) iter.next()).addBasisBundleListener(listener);
			}

			fDataRequesterListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given DataListener as a listener for DataEvents 
	 * from this DataRequester.
	 *
	 * @param listener A DataListener
	 **/	
	public void removeDataListener(DataListener listener)
	{
		for (Iterator iter = fBasisRequesters.iterator(); iter.hasNext();)
		{
			((BasisRequester) iter.next()).removeBasisBundleListener(listener);
		}

		fDataRequesterListeners.remove(listener);
	}
	
	//--- Utility class ------------------------------------------------------
	
	/**
	 * This utility class manages a pending BasisRequest. When the target 
	 * BasisBundle becomes available in the DataSpace the 
	 * <code>setBasisRequest0</code> method will be called.
	 * 
	 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
	 * for the Instrument Remote Control (IRC) project.
	 *
	 * @author	tjames
	**/
	private final class PendingRequestHandler implements DataSpaceListener
	{
		private Map fPendingRequests = new HashMap();	
		private boolean fRegisteredListener = false;

		/**
		 * Adds a BasisRequest to manage until the target BasisBundle is 
		 * available.
		 * 
		 * @param request the BasisRequest to manage.
		 */
		public synchronized void addPendingRequest(BasisRequest request)
		{
			if (request == null)
			{
				return;
			}

			BasisBundleId basisBundleId = request.getBasisBundleId();
			
			BasisBundle basisBundle = 
				Irc.getDataSpace().getBasisBundle(basisBundleId);
			
			if (basisBundle != null)
			{
				// Request is for an existing BasisBundle so handle it.
				handlePendingRequest(request);
			}
			else
			{
				// If the BasisBundle is not available then set up as a listener
				// and wait to be notified of new BasisBundles.
				
				if (sLogger.isLoggable(Level.FINE))
				{
					String message = 
						"Basis bundle " + request.getBasisBundleId() 
						+ " is not available";
					
					sLogger.logp
						(Level.FINE, CLASS_NAME, "addPendingRequest", message);
				}
				
				fPendingRequests.put(basisBundleId, request);
				
				if (!fRegisteredListener)
				{
					Irc.getDataSpace().addDataSpaceListener(this);
					fRegisteredListener = true;

					// Check again just in case it was added before registering as a 
					// listener.
					basisBundle = 
						Irc.getDataSpace().getBasisBundle(basisBundleId);
					
					if (basisBundle != null)
					{
						fPendingRequests.remove(basisBundleId);
						handlePendingRequest(request);
					}	
				}
			}
		}
		
		/**
		 * Clears all pending requests.
		 */
		public synchronized void clearPending()
		{
			fPendingRequests.clear();
			
			if (fRegisteredListener)
			{
				Irc.getDataSpace().removeDataSpaceListener(this);
				fRegisteredListener = false;
			}
		}
		
		/**
		 * Removes the pending BasisRequest on the given Basis bundle.
		 * 
		 * @param bundleId The target BasisBundle id to remove.
		 */
		public synchronized void removePending(BasisBundleId bundleId)
		{
			fPendingRequests.remove(bundleId);

			if (fRegisteredListener && fPendingRequests.isEmpty())
			{
				Irc.getDataSpace().removeDataSpaceListener(this);
				fRegisteredListener = false;
			}
		}
		
		/**
		 * Handles the BasisRequest by calling the <code>addBasisRequest0</code>
		 * method.
		 * 
		 * @param request the BasisRequest to handle
		 */
		private synchronized void handlePendingRequest(BasisRequest request)
		{
			addBasisRequest0(request);

			if (fRegisteredListener && fPendingRequests.isEmpty())
			{
				Irc.getDataSpace().removeDataSpaceListener(this);
				fRegisteredListener = false;
			}
		}
		
		/**
		 * Causes this DataSpaceListener to receive the given MembershipEvent.
		 * If the event is a DataSpaceEvent for a new BasisBundle that a pending 
		 * request needs then this method will handle the pending case.
		 * 
		 * @param event A MembershipEvent
		 */
		public synchronized void receiveMembershipEvent(MembershipEvent event)
		{
			if (event instanceof DataSpaceEvent)
			{
				DataSpaceEvent dataSpaceEvent = (DataSpaceEvent) event;

				// Check if the event is for a new BasisBundle
				if (dataSpaceEvent.wasRemoved())
				{
					// The event was for the removal of a BasisBundle so there is 
					// nothing more we can do here.
					return;
				}

				BasisBundleId bundleId = dataSpaceEvent.getBasisBundleId();				
				BasisRequest pendingRequest = 
					(BasisRequest) fPendingRequests.remove(bundleId);
				
				if (pendingRequest != null)
				{
					if (sLogger.isLoggable(Level.FINE))
					{
						String message = 
							"Basis bundle " + pendingRequest.getBasisBundleId() 
							+ " is now available";
						
						sLogger.logp
							(Level.FINE, CLASS_NAME, "receiveDataSpaceEvent", message);
					}
					
					handlePendingRequest(pendingRequest);
				}			
			}
		}		
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultDataRequester.java,v $
//	Revision 1.79  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.78  2006/05/17 13:26:53  smaher_cvs
//	Added check for (illegal) duplicate requests for same basisbundle in addBasisRequest0
//	
//	Revision 1.77  2006/02/01 22:57:52  tames
//	Removed debug statements.
//	
//	Revision 1.76  2006/01/24 22:32:47  tames_cvs
//	Fixed late binding of a pending request.
//	
//	Revision 1.75  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.74  2006/01/20 05:28:15  tames
//	Fixed bug where a new BasisRequester is not started if the DataRequester is
//	already started.
//	
//	Revision 1.73  2006/01/17 05:52:07  tames
//	Removed nested synchronized blocks and other Thread related fixes to remove
//	overhead, potential Thread deadlocks, and to simplify class.
//	
//	Revision 1.72  2006/01/13 03:21:53  tames
//	JavaDoc change only.
//	
//	Revision 1.71  2006/01/04 16:54:31  tames
//	Removed characteristic behavior of requiring a manual restart if a basis request is added or removed.
//	
//	Revision 1.70  2006/01/02 03:49:09  tames
//	Updated to reflect changes to the HasBasisRequests interface.
//	
//	Revision 1.69  2005/12/08 16:59:09  tames_cvs
//	Revised implementation of pending BasisRequests to listen for
//	DataSpaceEvents instead of polling the DataSpace.
//	
//	Revision 1.68  2005/12/06 22:47:00  tames_cvs
//	Added a utility class to monitor the DataSpace for new BasisBundles. This is an
//	initial implementation to support the late binding of a BasisRequest when
//	the source BasisBundle does not exist yet.
//	
//	Revision 1.67  2005/11/09 18:43:23  tames_cvs
//	Modified event publishing to use the CopyOnWriteArrayList class to
//	hold listeners. This reduces the overhead when publishing events.
//	
//	Revision 1.66  2005/09/13 22:28:58  tames
//	Changes to refect BasisBundleEvent refactoring.
//	
//	Revision 1.65  2005/08/26 22:13:30  tames_cvs
//	Changes that are an incomplete refactoring. Also added initial support for history.
//	
//	Revision 1.64  2005/08/12 04:47:13  tames
//	Changed to eliminate the ConcurrentModification exception.
//	
//	Revision 1.62  2005/07/15 19:22:45  chostetter_cvs
//	Organized imports
//	
//	Revision 1.61  2005/07/14 22:01:40  tames
//	Refactored data package for performance.
//	
//	Revision 1.60  2005/05/27 15:42:08  tames_cvs
//	Fixed a potential thread deadlock when starting and stopping this
//	component.
//	
//	Revision 1.59  2005/05/24 21:12:23  chostetter_cvs
//	Fixed some issues involving changing BasisRequests at run time
//	
//	Revision 1.58  2005/05/02 15:26:37  tames
//	Changed the stop method not to interrupt the thread.
//	
//	Revision 1.57  2005/04/16 04:04:21  tames
//	Changes to reflect refactored state and activity packages.
//	
//	Revision 1.56  2005/04/12 22:15:16  chostetter_cvs
//	Fixed ordering, synchronization issues with including pending data on stop
//	
//	Revision 1.55  2005/04/05 21:22:09  chostetter_cvs
//	Fixed problem with DataRequester passing on BasisSetEvents to its listeners.
//	
//	Revision 1.54  2005/04/04 22:35:39  chostetter_cvs
//	Adjustments to synchronization scheme
//	
//	Revision 1.53  2005/04/04 15:40:58  chostetter_cvs
//	Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//	
//	Revision 1.52  2005/03/29 22:19:51  chostetter_cvs
//	Fixed redundant initial alert of BasisBundle structure
//	
//	Revision 1.51  2005/03/24 18:16:34  chostetter_cvs
//	Fixed problem with releasing unused portions of BasisSets, changed waiting on DataRequestSatisfier repetitions to reach zero to waiting for stop
//	
//	Revision 1.50  2005/03/22 23:00:14  chostetter_cvs
//	Fixed repetition wait/notify scheme
//	
//	Revision 1.49  2005/03/22 20:32:19  chostetter_cvs
//	Added ability to wait for all repetitions to complete
//	
//	Revision 1.48  2005/03/04 20:34:01  chostetter_cvs
//	Guard against potential null pointer exception in kill()
//	
//	Revision 1.47  2005/03/04 19:33:46  tames_cvs
//	Added a sleep to prevent CPU runaway if no data is available. Changed
//	default satisfaction rule to ALL to avoid breaking existing algorithms.
//	Both issues need to be revisited in the future.
//	
//	Revision 1.46  2005/03/04 18:46:25  chostetter_cvs
//	Can now choose among three request satisfaction rules: all, any, and first
//	
//	Revision 1.45  2005/03/02 22:43:09  tames_cvs
//	Fixed potential thread deadlock if data requester is waiting for a request to
//	be satisfied.
//	
//	Revision 1.44  2005/03/01 23:25:15  chostetter_cvs
//	Revised Queue design and package for better blocking behavior
//	
//	Revision 1.43  2005/02/25 23:26:50  tames_cvs
//	Checkins for Carl related to a Thread deadlock condition when starting
//	and stopping Requesters.
//	
//	Revision 1.41  2005/02/25 04:02:06  chostetter_cvs
//	See previous comment. Grrrrr.
//	
//	Revision 1.40  2005/02/25 01:05:46  chostetter_cvs
//	REALLY really fixed start/stop behavior problem vis a vis unreleased BasisSets due to Thread interruption
//	
//	Revision 1.39  2005/02/24 23:57:08  chostetter_cvs
//	Undid last change, which was logically insufficient
//	
//	Revision 1.38  2005/02/24 23:47:56  chostetter_cvs
//	Made a bit more efficient it testing for leftover satisfying BasisSets
//	
//	Revision 1.37  2005/02/24 23:46:40  chostetter_cvs
//	Really fixed starting/stopping/synchronization behavior
//	
//	Revision 1.36  2005/02/24 22:57:08  chostetter_cvs
//	Fixed broken start/stop/synchronization behavior
//	
//	Revision 1.35  2005/02/23 16:03:47  chostetter_cvs
//	Fixed synchronization bug in addBasisRequest
//	
//	Revision 1.34  2005/02/22 23:08:05  chostetter_cvs
//	DataRequester now waits for at least one BasisRequester to be formed
//	
//	Revision 1.33  2005/02/02 21:17:27  chostetter_cvs
//	DataSets (and their BasisSets) are now duplicated (in the java.nio sense) when sent to more than one DataListener
//	
//	Revision 1.32  2005/02/01 20:53:54  chostetter_cvs
//	Revised releasable BasisSet design, release policy
//	
//	Revision 1.31  2005/01/31 21:35:30  chostetter_cvs
//	Fix for data request sequencing, documentation
//	
//	Revision 1.30  2005/01/27 21:38:02  chostetter_cvs
//	Implemented new exception state and default exception behavior for Objects having ActivityState
//	
//	Revision 1.29  2004/11/19 21:39:36  smaher_cvs
//	Added check for null listener.
//	
//	Revision 1.28  2004/11/10 19:31:28  tames
//	Added a getDownsamplingRate method
//	
//	Revision 1.27  2004/10/24 23:21:28  tames
//	Changes to reduce potential thread deadlocks related to starting and
//	stopping the requester and synchronization on the
//	fBasisRequestersByBasisBundle Map.
//	
//	Revision 1.26  2004/10/22 13:49:26  tames
//	Restarting an interrupted thread does not appear to work correctly. I
//	changed the starting and stopping behavior to create a new thread.
//	In the future this should be investigated to determine how to restart
//	a thread to avoid the overhead of creating a new one.
//	
//	Revision 1.25  2004/10/08 14:42:12  chostetter_cvs
//	Clear the interrupted state of the DataRequesterThread so that it can be restarted
//	
//	Revision 1.24  2004/10/07 21:37:27  chostetter_cvs
//	Removed stopping of running requester when setting repetition level
//	
//	Revision 1.23  2004/07/24 02:46:11  chostetter_cvs
//	Added statistics calculations to DataBuffers, renamed some classes
//	
//	Revision 1.22  2004/07/22 17:15:00  chostetter_cvs
//	DataRequester clears its BasisRequesters when it stops
//	
//	Revision 1.21  2004/07/21 14:26:14  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.20  2004/07/17 01:25:58  chostetter_cvs
//	Refactored test algorithms
//	
//	Revision 1.19  2004/07/16 15:18:31  chostetter_cvs
//	Revised, refactored Component activity state
//	
//	Revision 1.18  2004/07/16 04:15:44  chostetter_cvs
//	More Algorithm work, primarily on properties
//	
//	Revision 1.17  2004/07/14 22:24:53  chostetter_cvs
//	More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//	
//	Revision 1.16  2004/07/14 00:33:49  chostetter_cvs
//	More Algorithm, data testing. Fixed slice bug.
//	
//	Revision 1.15  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.14  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.13  2004/07/11 07:30:35  chostetter_cvs
//	More data request work
//	
//	Revision 1.12  2004/07/09 22:29:11  chostetter_cvs
//	Extensive testing of Input/Output interaction, supports simple BasisRequests
//	
//	Revision 1.11  2004/07/06 21:57:12  chostetter_cvs
//	More BasisRequester, DataRequester work
//	
//	Revision 1.10  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.9  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.8  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.7  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.6  2004/06/16 22:17:05  chostetter_cvs
//	Check in for long weekend
//	
//	Revision 1.5  2004/06/15 23:28:04  chostetter_cvs
//	More BasisRequest work
//	
//	Revision 1.4  2004/06/15 22:21:12  chostetter_cvs
//	More DataSetRequester work
//	
//	Revision 1.3  2004/06/15 20:04:03  chostetter_cvs
//	Added ActivityStateModel, use for stative Objects
//	
//	Revision 1.2  2004/06/14 21:23:50  chostetter_cvs
//	More BasisRequest work
//	
//	Revision 1.1  2004/06/11 17:27:56  chostetter_cvs
//	Further Input-related work
//	
