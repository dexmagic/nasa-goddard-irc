//=== File Prolog ============================================================
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

import gov.nasa.gsfc.commons.processing.activity.ActivityStateModel;
import gov.nasa.gsfc.commons.processing.activity.DefaultActivityStateModel;
import gov.nasa.gsfc.commons.properties.state.State;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.BasisBundleListener;
import gov.nasa.gsfc.irc.data.events.BasisSetEvent;


/**
 * An AbstractBasisRequester is an abstract receiver of BasisBundleEvents from
 * the same BasisBundle, that is associated with one BasisRequest on
 * that BasisBundle. Subclasses must implement 
 * {@link #processBasisSet(BasisSet) processBasisSet},
 * {@link #handleBundleClosed(BasisBundleEvent) handleBundleClosed}, and
 * {@link #handleBundleStructureChange(BasisBundleEvent) handleBundleStructureChange}
 * methods. Subclasses should consider overriding the 
 * {@link #handleStartsNewBasisSequence()} method.
 * <p>
 * Note that a BasisRequester must be started before it will begin listening for
 * BasisBundleEvents.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Troy Ames
 */
public abstract class AbstractBasisRequester implements BasisRequester
{
    /**
     * Logger for this class
     */
	private static final String CLASS_NAME = DefaultDataRequester.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private ActivityStateModel fStateModel = new DefaultActivityStateModel();
	private BasisBundle fBasisBundle = null;
	private BasisRequest fBasisRequest = null;
	/**
	 * Contains a copy of the basis bundle descriptor associated with fBasisBundle that may have a reduced (filtered) set of data buffers in congruence with fBasisRequest.
	 */
	private BasisBundleDescriptor fBasisBundleDescriptor;
	private List fBasisBundleListeners = new CopyOnWriteArrayList();
	private Set fDataBufferSubset;
	private int fDownsamplingRate = 0;
	
	/**
	 * Default constructor for derivations.
	 */
	public AbstractBasisRequester()
	{
	}
	
	/**
	 * Constructs a new BasisRequester that will attempt to satisfy the 
	 * given BasisRequest.
	 * 
	 * @param basisRequest A BasisRequest
	 */	
	public AbstractBasisRequester(BasisRequest basisRequest)
	{
		setBasisRequest0(basisRequest);
	}

	//--- BasisBundle listener methods -----------------------------------------
	
	/**
	 * Causes this BasisRequester to receive the given BasisBundleEvent. If the
	 * event represents a basis bundle closed condition then the
	 * {@link #handleBundleClosed(BasisBundleEvent)} method will be called
	 * followed by {@link #stop()}. If the event represents a new basis bundle
	 * structure then the {@link #handleBundleStructureChange(BasisBundleEvent)}
	 * method will be called.
	 * 
	 * @param event A BasisBundleEvent
	 */
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		if (event.isClosed())
		{
			handleBundleClosed(event);
			stop();
		}
		else if (event.hasNewStructure())
		{
			
			// We need to propagate the correct (possibly filtered) version
			// of the descriptor relative to this basis request.  That may
			// "override" the information about the new structure of the basis bundle,
			// but currently no implementations use this information.
			// FIXME Need to separate basis request filtering from "parent" basis
			// bundle descriptions.
			
			// Create new event with the descriptor of *this basis set
			event = new BasisBundleEvent(fBasisBundle, getInputDescriptor());
			
			handleBundleStructureChange(event);
		}
		else if (event.isRequestingRelease())
		{
			//copyAndReleaseSatisfyingData();
			//copyAndReleasePendingData();
		}
		
		alertBasisBundleListeners(event);
	}

	/**
	 * Causes this BasisRequester to receive the given BasisSetEvent. This
	 * implementation will first call {@link #handleStartsNewBasisSequence()} if
	 * in fact the new basis set starts a new sequence, followed by
	 * {@link #processBasisSet(BasisSet)}.
	 * 
	 * @param event A BasisSetEvent
	 */
	public synchronized void receiveBasisSetEvent(BasisSetEvent event)
	{
		if (event.isStartOfNewBasisSequence())
		{
			handleStartsNewBasisSequence();
		}
		
		BasisSet basisSet = ((BasisSetEvent) event).getBasisSet();
		
		if (basisSet != null)
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

			processBasisSet(basisSet);
		}
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
		setBasisRequest0(basisRequest);
	}
	
	/*
	 * Sets the BasisRequest to be satisfied by this BasisRequester to 
	 * the given BasisRequest. Called from the constructor or the public 
	 * setBasisRequest method.
	 */
	private void setBasisRequest0(BasisRequest basisRequest)
	{
		fBasisRequest = basisRequest;
		
		BasisBundleId basisBundleId = basisRequest.getBasisBundleId();
		
		if (fBasisBundle != null)
		{
			fBasisBundle.removeBasisSetListener(this);
			fBasisBundle.removeBasisBundleListener(this);
		}

		if (basisRequest.selectsAllDataBuffers())
		{
			fDataBufferSubset = null;
		}
		else
		{
			fDataBufferSubset = basisRequest.getDataBufferNames();
		}

		fDownsamplingRate = basisRequest.getDownsamplingRate();

		fBasisBundle = 
			Irc.getDataSpace().getBasisBundle(basisBundleId);
		
		if (fBasisBundle == null)
		{
			String message = 
				"Basis bundle " + basisRequest.getBasisBundleId() 
				+ " is not available";
			
			sLogger.logp
				(Level.WARNING, CLASS_NAME, "setBasisRequest", message);
		}
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
	 * Downsample the given basis set if the BasisRequest requests it, otherwise
	 * returns the original basis set unmodified. The original basis set is
	 * released if downsampling is performed.
	 * 
	 * @param basisSet the basisSet to downsample.
	 * @return a downsampled basis set or the original basis set.
	 */
	public BasisSet downsample(BasisSet basisSet)
	{
		BasisSet result = basisSet;
		
		if (fDownsamplingRate > 1)
		{
			if (fDownsamplingRate < basisSet.getSize())
			{
				BasisSet subsampledData = 
					basisSet.downsample(fDownsamplingRate);
				
				// We no longer need the original so release it
				basisSet.release();
				result = subsampledData;
			}
		}
		
		return result;
	}
	
	/**
	 * Sets the downsampling rate of all of this BasisRequester to the given
	 * rate. To indicate no downsampling, set this rate to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of this BasisRequester
	 */
	public void setDownsamplingRate(int downsamplingRate)
	{
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
	 * Returns true if the basis request specifies downsampling, otherwise
	 * returns false.
	 * 
	 * @return true if downsampleing enabled, false otherwise.
	 * @see #downsample(BasisSet)
	 */
	public boolean isDownsamplingEnabled()
	{
		return (fDownsamplingRate > 1);
	}

	/**
	 * Gets the descriptor of the current requested BasisBundle if it exist.
	 * 
	 * @return the descriptor for the current bundle or null if there is not one.
	 */
	public BasisBundleDescriptor getInputDescriptor()
	{
		BasisBundleDescriptor result = null;
		
		if (fBasisBundle != null)
		{
			// No synchronize is needed here since
			// the assignment is repeatable.  Assumes
			// fBasisBundleDescriptor.equals() is not employed.
			if (fBasisBundleDescriptor == null)
			{
				fBasisBundleDescriptor = fBasisBundle.getDescriptor().filterDescriptor2(fDataBufferSubset);				
			}
			result = fBasisBundleDescriptor;
		}
		
		return result;
	}
	
	/**
	 * Causes this BasisRequester to process the given new BasisSet. This 
	 * method is called from the 
	 * {@link #receiveBasisBundleEvent(BasisBundleEvent) receiveBasisBundleEvent}
	 * method.
	 * 
	 * @param sourceBasisSet A new BasisSet.
	 */
	protected abstract void processBasisSet(BasisSet sourceBasisSet);
	
	/**
	 * Causes this BasisRequester to handle an event signifying a new
	 * Basis bundle structure.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected abstract void handleBundleStructureChange(BasisBundleEvent event);
	
	/**
	 * Causes this BasisRequester to handle an event signifying the basis
	 * bundle has closed.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected abstract void handleBundleClosed(BasisBundleEvent event);

	/**
	 * Causes this BasisRequester to handle an event signifying the next basis
	 * bundle has a new basis sequence. Since requesters may not care for this 
	 * condition this implementation does nothing.
	 */
	protected void handleStartsNewBasisSequence()
	{
		// Do nothing by default.
	}

	//--- State-related methods ------------------------------------------------
	
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
	 * Causes this BasisRequester to enter the started state if it is not
	 * curently started or killed. Adds this requester as a listener of the
	 * BasisBundle if there is one.
	 */ 
	public void start()
	{
		if (! isKilled() && ! isStarted())
		{
			fStateModel.start();
			
			if (fBasisBundle != null)
			{
				fBasisBundle.addBasisBundleListener(this);
				fBasisBundle.addBasisSetListener(this);
			}
		}
	}

	/**
	 * Returns true if this BasisRequester is currently started, false
	 * otherwise. 
	 * 
	 * @return True if this BasisRequester is currently started, false otherwise
	 */	 
	public boolean isStarted()
	{
		return (fStateModel.isStarted());
	}
	
	/**
	 * Sets this BasisRequester's State to active. A BasisRequester cannot
	 * become active unless it is currently started.
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
	 * Sets this BasisRequester's State to waiting. A BasisRequester cannot
	 * become waiting unless it is currently active.
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
	 * Causes this BasisRequester to enter the stopped state, if it is currently
	 * started. Removes this requester as a listener of the BasisBundle if there
	 * was one.
	 */
	public void stop()
	{
		if (isStarted())
		{
			fStateModel.stop();

			if (fBasisBundle != null)
			{
				fBasisBundle.removeBasisSetListener(this);
				fBasisBundle.removeBasisBundleListener(this);
			}
		}
	}
	
	/**
	 * Returns true if this BasisRequester is stopped, false otherwise.
	 * 
	 * @return True if this BasisRequester is stopped, false otherwise
	 */	
	public boolean isStopped()
	{
		return (fStateModel.isStopped());
	}
	
	/**
	 * Sets this BasisRequester to the exception state, due to the given
	 * Exception. This BasisRequester is first stopped, and then enters the
	 * exception state.
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
	 * Clears the current Exception (if any) from this BasisRequester.
	 */
	public void clearException()
	{
		fStateModel.clearException();
	}
	
	/**
	 * Returns true if this BasisRequester is in an exception state, false
	 * otherwise.
	 * 
	 * @return True if this BasisRequester is in an exception state, false
	 *         otherwise
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
	 * Causes this BasisRequester to enter the killed state. A killed
	 * BasisRequester cannot subsequently be started or otherwise reused.
	 * Removes this requester as a listener of the BasisBundle if there
	 * was one.
	 */
	public void kill()
	{
		if (! isKilled())
		{
			fStateModel.kill();

			if (fBasisBundle != null)
			{
				fBasisBundle.removeBasisSetListener(this);
				fBasisBundle.removeBasisBundleListener(this);
			}
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
	
	//--- Listener methods -----------------------------------------------------
	
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
	 * Alerts each of the current BasisBundleListeners of this BasisRequester of
	 * the given BasisBundleEvent.
	 * 
	 * @param event A BasisBundleEvent
	 */
	protected void alertBasisBundleListeners(BasisBundleEvent event)
	{
		for (Iterator iter = fBasisBundleListeners.iterator(); iter.hasNext();)
		{
			((BasisBundleListener) iter.next()).receiveBasisBundleEvent(event);
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
			
			if (fBasisBundle != null)
			{
				BasisBundleEvent event = 
					new BasisBundleEvent(
						fBasisBundle, getInputDescriptor() /*fBasisBundle.getDescriptor()*/);
				listener.receiveBasisBundleEvent(event);
			}
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
		fBasisBundleListeners.remove(listener);
	}	
}

//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractBasisRequester.java,v $
//	Revision 1.9  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.8  2006/05/17 15:28:24  smaher_cvs
//	Moved descriptor filter to BasisBundleDescriptor.
//	
//	Revision 1.7  2006/05/17 13:25:16  smaher_cvs
//	BasisBundleEvents that are published to listeners now have a descriptor that reflects any filtering done in the BasisRequest.
//	
//	Revision 1.6  2005/12/08 16:57:40  tames_cvs
//	Removed obsolete Timer references.
//	
//	Revision 1.5  2005/12/06 22:39:52  tames_cvs
//	Removed delayed binding for basis request since this was not working for
//	all subclasses. The caller must provide a BasisRequest for a BasisBundle that
//	actually exist in the DataSpace.
//	
//	Revision 1.4  2005/11/09 18:43:23  tames_cvs
//	Modified event publishing to use the CopyOnWriteArrayList class to
//	hold listeners. This reduces the overhead when publishing events.
//	
//	Revision 1.3  2005/09/22 18:38:50  tames
//	Minor style changes only.
//	
//	Revision 1.2  2005/09/13 22:28:58  tames
//	Changes to refect BasisBundleEvent refactoring.
//	
//	Revision 1.1  2005/09/09 21:31:44  tames
//	Initial implementation.
//	
//	
//	
