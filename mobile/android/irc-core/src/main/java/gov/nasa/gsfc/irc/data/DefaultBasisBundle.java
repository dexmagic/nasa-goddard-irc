//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/DefaultBasisBundle.java,v 1.90 2006/08/03 20:20:55 chostetter_cvs Exp $
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**/

import gov.nasa.gsfc.commons.system.memory.Allocation;
import gov.nasa.gsfc.commons.system.memory.ContiguousMemoryModel;
import gov.nasa.gsfc.commons.system.memory.MemoryModel;
import gov.nasa.gsfc.commons.system.memory.MemoryModelEvent;
import gov.nasa.gsfc.commons.system.memory.MemoryModelListener;
import gov.nasa.gsfc.commons.system.memory.ReleaseRequestEvent;
import gov.nasa.gsfc.commons.types.namespaces.AbstractMemberBean;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.BasisBundleListener;
import gov.nasa.gsfc.irc.data.events.BasisSetEvent;
import gov.nasa.gsfc.irc.data.events.BasisSetListener;


/**
 * A BasisBundle is a set of DataBuffers all sharing and correlated with a 
 * single, common BasisBuffer. 
 * 
 * <p>This default implementation is as simple as possible, representing a 
 * dynamically-sized sliding window of contiguous allocated space within a 
 * fixed extent of free space. The position and size of the allocated space is 
 * defined by the start and end of the first and last of a dynamic set of 
 * variously-sized BasisSets, each of which has an associated reference count 
 * (namely, the number of BasisBundleListeners to which the BasisSet was sent 
 * when it was made available). Further, the BasisBundle is assumed to be used 
 * by exactly one BasisBundleSource that will, in strict sequence, allocate a 
 * BasisSet, write data into it, and then make it available for reading, before 
 * allocating the next BasisSet. 
 * 
 * <p>As each such BasisSet of new data is made available for reading, a 
 * BasisSetEvent containing a read-only version of the BasisSet is sent to 
 * all BasisBundleEventListeners. Each of these listeners is in turn responsible 
 * for alerting this BasisBundle when it is finished with any BasisSet received 
 * from it. When the reference count for each such BasisSet reaches zero, the 
 * BasisSet is freed, and its portion of this BasisBundle is then available 
 * for subsequent reallocation as part of another BasisSet.
 * 
 * <p>Note that a request by a BasisBundleSource to allocate some amount of 
 * data for writing will fail if there is currently insufficient free space 
 * in the BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/03 20:20:55 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public class DefaultBasisBundle extends AbstractMemberBean 
	implements BasisBundle, MemoryModelListener
{
	private static final String CLASS_NAME = DefaultBasisBundle.class.getName();	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private BasisBundleDescriptor fDescriptor;
	private BasisBundleId fBasisBundleId;
	private MemberId fBasisBundleSourceId;
	
	private int fCapacity = 0;
	private BasisSet fBackingBasisSet;
	private MemoryModel fMemoryModel;
	
	private boolean fStartedNewBasisSequence = false;
	
	private List fBasisBundleListeners = new CopyOnWriteArrayList();
	private List fBasisSetListeners = new CopyOnWriteArrayList();
	private double fBasisBase = 0.0d;
	
	/**
	 * Constructs a new BasisBundle for the given BasisBundleSource, configured as 
	 * described by the given BasisBundleDescriptor, but without creating any 
	 * backing DataBuffers.
	 * 
	 * @param descriptor The BasisBundleDescriptor that describes the structure
	 * 		of the desired new BasisBundle
	 * @param source The BasisBundleSource of the new BasisBundle
	 * @return A new BasisBundle from the given BasisBundleSource, as described
	 * 		by the given BasisBundleDescriptor
	 */
	public DefaultBasisBundle(BasisBundleDescriptor descriptor, 
		BasisBundleSource source)
	{
		this(descriptor, source, 0);
	}
		
	/**
	 * Constructs a new BasisBundle for the given BasisBundleSource, configured as 
	 * described by the given BasisBundleDescriptor, and whose backing DataBuffers 
	 * all have the given capacity.
	 * 
	 * @param descriptor The BasisBundleDescriptor that describes the structure
	 * 		of the desired new BasisBundle
	 * @param source The BasisBundleSource of the new BasisBundle
	 * @param capacity The uniform capacity of the backing data buffers of the
	 * 		new BasisBundle
	 * @return A new BasisBundle from the given BasisBundleSource, as described
	 * 		by the given BasisBundleDescriptor
	 */
	public DefaultBasisBundle(BasisBundleDescriptor descriptor, 
		BasisBundleSource source, int capacity)
	{
		super(new DefaultBasisBundleId(descriptor.getName(), source));
		
		fBasisBundleId = (BasisBundleId) getMemberId();
		fBasisBundleSourceId = source.getMemberId();
		
		fCapacity = capacity;
		
		setDescriptor(descriptor);
	}
		
	/**
	 * Creates a new set of backing data buffers according to the current
	 * BasisBundleDescriptor and capacity.
	 */
	protected synchronized final void createNewBackingBuffers()
	{
		if (fCapacity > 0)
		{
			if (fMemoryModel != null)
			{
				fMemoryModel.removeReleaseRequestListener(this);
			}
			
			fMemoryModel = new ContiguousMemoryModel(fCapacity);
			fMemoryModel.addReleaseRequestListener(this);
			
			fBackingBasisSet = new DefaultBasisSet(this, fCapacity);
		}
	}
		
	/**
	 * Causes this BasisBundle to receive the given MemoryModelEvent from its
	 * MemoryModel.
	 * 
	 * @param event A MemoryModelEvent
	 */
	public void receiveMemoryModelEvent(MemoryModelEvent event)
	{
		if (event instanceof ReleaseRequestEvent)
		{
			askListenersToReleaseBasisSets();
		}
	}
		
	/**
	 * Sets the BasisBundleDescriptor that describes the structure of this
	 * BasisBundle to the given BasisBundleDescriptor. In turn, this BasisBundle
	 * will subsequently create BasisSets having the new structure.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param descriptor The BasisBundleDescriptor that describes the desired
	 *            structure of this BasisBundle
	 */
	public synchronized final void setDescriptor(BasisBundleDescriptor descriptor)
	{
		if (fDescriptor != descriptor)
		{
			fDescriptor = descriptor;
			
			alertListenersOfNewStructure();
			
			createNewBackingBuffers();
		}
	}
	
	/**
	 * Send the given BasisBundleEvent to all current BasisBundleListeners on
	 * this BasisBundle.
	 * 
	 * @param event the event to publish.
	 */
	protected void alertListeners(BasisBundleEvent event)
	{
		for (Iterator iter = fBasisBundleListeners.iterator(); iter.hasNext();)
		{
			((BasisBundleListener) iter.next()).receiveBasisBundleEvent(event);
		}
	}
		
	/**
	 * Alerts all listeners on this BasisBundle that is has a new structure.
	 */
	protected void alertListenersOfNewStructure()
	{
		int numListeners = fBasisBundleListeners.size();
		
		if (numListeners > 0)
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = getName() + " alerting " + numListeners + 
					" listener(s) of new structure...";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"alertListenersOfNewStructure", message + 
					"\n" + fDescriptor);
			}
			
			BasisBundleEvent event = new BasisBundleEvent(this, fDescriptor);
			
			alertListeners(event);
		}
	}
		
	/**
	 * Asks all listeners on this BasisBundle to free any held BasisSets.
	 */
	protected void askListenersToReleaseBasisSets()
	{
		int numListeners = fBasisBundleListeners.size();
		
		if (numListeners > 0)
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Alerting " + numListeners + 
					" listener(s) of BasisSet release request...";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"askListenersToReleaseBasisSets", message + 
					"\n" + fMemoryModel);
			}
			
			BasisBundleEvent event = 
				new BasisBundleEvent(this, true, false);
			
			alertListeners(event);
		}
	}
	
	/**
	 * Alerts all listeners on this BasisBundle that it is closing.
	 */
	protected void alertListenersOfClosing()
	{
		int numListeners = fBasisBundleListeners.size();
		
		if (numListeners > 0)
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = getName() + " alerting " + numListeners + 
					" listener(s) of closing...";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"alertListenersOfClosing", message);
			}
			
			BasisBundleEvent event = 
				new BasisBundleEvent(this, false, true);
			
			alertListeners(event);
		}
	}
		
	/**
	 * Returns the BasisBundleDescriptor that describes the structure of this
	 * BasisBundle.
	 * 
	 * @return The BasisBundleDescriptor that describes the structure of this
	 *         BasisBundle
	 */
	public BasisBundleDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	

	/**
	 * Returns the BasisBundleId for this BasisBundle.
	 * 
	 * @return The BasisBundleId for this BasisBundle
	 */
	public BasisBundleId getBasisBundleId()
	{
		return (fBasisBundleId);
	}
		
	/**
	 * Returns the MemberId of the BasisBundleSource that created/owns/is 
	 * writing data to this BasisBundle.
	 * 
	 * @return The MemberId of the BasisBundleSource that created/owns/is 
	 * 		writing data to this BasisBundle
	 */
	public MemberId getBasisBundleSourceId()
	{
		return (fBasisBundleSourceId);
	}
	
	/**
	 * Returns the current size of this BasisBundle (i.e., the uniform size of
	 * each of its Buffers).
	 * 
	 * @return The current size of this BasisBundle
	 */	
	public int getSize()
	{
		return (fCapacity);
	}
	
	/**
	 * Returns the DataBufferDescriptor that describes the basis Buffer of this
	 * BasisBundle.
	 * 
	 * @return The DataBufferDescriptor that describes the basis Buffer of this
	 *         BasisBundle
	 */
	public DataBufferDescriptor getBasisBufferDescriptor()
	{
		return (fDescriptor.getBasisBufferDescriptor());
	}
		
	/**
	 * Returns the Set of DataBufferDescriptors that describe the structure of
	 * this BasisBundle.
	 * 
	 * @return The Set of DataBufferDescriptors that describe the structure of
	 *         this BasisBundle
	 */	
	public Set getDataBufferDescriptors()
	{
		return (fDescriptor.getDataBufferDescriptors());
	}
		
	/**
	 * Returns the ordered Map of the DataBufferDescriptors (by name) of this
	 * BasisBundle whose names match the given regular expression.
	 * 
	 * @param regEx A regular expression
	 * @return The ordered Map of the DataBufferDescriptors (by name) of this
	 *         BasisBundle whose names match the given regular expression
	 */
	public Map getDataBufferDescriptors(String regEx)
	{
		Map result = null;
		
		if (fDescriptor != null)
		{
			Set descriptorSet = fDescriptor.getDataBufferDescriptors(regEx);
			
			if (descriptorSet != null)
			{
				result = new LinkedHashMap();
				
				Iterator descriptors = descriptorSet.iterator();
				
				while (descriptors.hasNext())
				{
					DataBufferDescriptor descriptor = (DataBufferDescriptor)
						descriptors.next();
					
					result.put(descriptor.getName(), descriptor);
				}
			}
		}
		
		return (result);
	}
	
	/**
	 * Returns the Set of names of the DataBuffers of this BasisBundle. Note
	 * that a DataBuffer has a Pixel, the Pixel tag is part of its name.
	 * 
	 * @return The Set of names of the DataBuffers of this BasisBundle
	 */
	public Set getDataBufferNames()
	{
		return (fDescriptor.getDataEntryNames());
	}
	
	/**
	 * Returns the Set of names of the DataBuffers of this BasisBundle that
	 * match the given regular expression. Note that if a DataBuffer has a
	 * Pixel, the Pixel tag is part of its name.
	 * 
	 * @param A regular expression
	 * @return The Set of names of the DataBuffers of this BasisBundle that
	 *         match the given regular expression
	 */
	public Set getDataBufferNames(String regEx)
	{
		Set result = null;
			
		Map matchingDescriptors = getDataBufferDescriptors(regEx);
		
		if (matchingDescriptors != null)
		{
			Set names = matchingDescriptors.keySet();
			
			result = new LinkedHashSet(names);
		}
		
		return (result);
	}
		
	/**
	 * Returns true if this BasisBundle has been declared to be uniformly
	 * sampled, false otherwise.
	 * 
	 * @return True if this BasisSet has been declared to be uniformly sampled,
	 *         false otherwise
	 */
	public boolean isUniformlySampled()
	{
		return (fBackingBasisSet.isUniformlySampled());
	}
	
	/**
	 * Sets the declared uniform sample rate of the data in alll BasisSets
	 * subsequently allocated from this BasisBundle to the given value, in terms
	 * of the units of its basis buffer. It is up to the caller to guarantee
	 * that the given rate is accurate, and that all basis values in subsequent
	 * BasisSets are in fact uniformly spaced at the declared rate. To indicate
	 * that the BasisBundle is not uniformly sampled (the default assumption),
	 * set the value to 0 or less, or to Double.Nan.
	 * 
	 * @param sampleRate The declared uniform sample rate of the data in alll
	 *            BasisSets subsequently allocated from this BasisBundle to the
	 *            given value, in terms of the units of its basis buffer
	 */	
	public void setUniformSampleInterval(double sampleRate)
	{
		fBackingBasisSet.setUniformSampleInterval(sampleRate);
	}
	
	/**
	 * Sets the declared uniform sample interval of the data in all BasisSets
	 * subsequently allocated from this BasisBundle to the difference of the
	 * first two samples in the basis buffer of this BasisSet.
	 * 
	 * @throws IllegalStateException
	 *             if less than two samples are available in this BasisSet
	 */
	public void setUniformSampleIntervalImplicitly()
	{
		fBackingBasisSet.setUniformSampleIntervalImplicitly();
	}
	
	/**
	 * Returns the declared uniform sample rate of the data in all BasisSets
	 * allocated from this BasisBundle, in terms of the Units of the basis
	 * buffer. If this BasisBundle is not uniformly sampled, the result will be
	 * Double.Nan.
	 * 
	 * @return The declared uniform sample rate of the data in all BasisSets
	 *         allocated from this BasisBundle, in terms of the Units of the
	 *         basis buffer
	 */
	public double getUniformSampleInterval()
	{
		return (fBackingBasisSet.getUniformSampleInterval());
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisBundle#setBasisBase(double)
	 */
	public synchronized void setBasisBase(double base)
	{
		if (base != fBasisBase)
		{
			alertListenersOfNewStructure();
			fBasisBase = base;
			((DefaultBasisSet) fBackingBasisSet).setBasisBase(fBasisBase);
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisBundle#getBasisBase()
	 */
	public double getBasisBase()
	{
		return fBasisBase;
	}
	
	/**
	 * Resizes this BasisBundle to the given new capacity. This method should
	 * only be called by the creator/owner of this BasisBundle, and only if an
	 * allocation request fails.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param capacity The desired new capacity of this BasisBundle
	 */
	public synchronized void resize(int capacity)
	{
		fCapacity = capacity;
	
		createNewBackingBuffers();
	}
	
	/**
	 * Informs this BasisBundle that the next BasisSet its BasisBundleSource
	 * makes available to it will begin a new, coherent sequence of basis values
	 * and their correlated data.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 */	
	public void startNewBasisSequence()
	{
		fStartedNewBasisSequence = true;
	}
	
	
	/**
	 * Clears each of the values in each of the DataBuffers of this BasisBundle 
	 * (including the BasisBuffer) to an appropriate value for its type.
	 * 
	 * @return This BasisBundle
	 */
	
	public BasisBundle clear()
	{
		if (fBackingBasisSet != null)
		{
			fBackingBasisSet.clear();
		}
		
		return (this);
	}
	

	/**
	 * Allocates and returns a new, writeable BasisSet of the given size,
	 * blocking if necessary until sufficient space becomes available in this
	 * BasisBundle to satisfy the amount requested.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param size The size of the desired BasisSet
	 * @return A new, writeable BasisSet of the indicated size
	 */
	public synchronized BasisSet allocateBasisSet(int size)
	{
		DefaultBasisSet result = null;
		
		if (fMemoryModel != null)
		{
			try
			{
				Allocation allocation = fMemoryModel.blockingAllocate(size);
				
				result = (DefaultBasisSet) 
					fBackingBasisSet.slice(
						allocation.getStart(), allocation.getSize());
				
				result.fAllocation = allocation;
			}
			catch (InterruptedException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "The attempted allocation was interrupted";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"allocateBasisSet", message, ex);
				}
			}
		}
		else
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "This BasisBundle has not yet been sized";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"allocateBasisSet", message);
			}
		}
			
		return (result);
	}
	
	/**
	 * Makes the data contained in the given BasisSet (which must have been
	 * previously allocated from this BasisBundle) available for reading.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param basisSet A BasisSet of newly-available data. The BasisSet must
	 *            have been previously allocated from this BasisBundle
	 * @param numValidSamples The number of valid data samples in the given
	 *            BasisSet. This must be provided in the event that the number
	 *            is less than the size of the BasisSet as requested on
	 *            allocation
	 * @throws IllegalArgumentException if the given BasisSet was not allocated
	 *             from this BasisBundle, or if this BasisBundle has been
	 *             resized subsequent to its allocation
	 */	
	public synchronized void makeAvailable(BasisSet basisSet, int numValidSamples)
		throws IllegalArgumentException
	{
		Iterator listeners = fBasisSetListeners.iterator();		
		
		if (listeners.hasNext() && numValidSamples > 0)
		{
			//
			// Trim the basis bundle to the actual number of samples if necessary
			//
			if (basisSet.getSize() > numValidSamples)
			{
				BasisSet originalBasisSet = basisSet;
				basisSet = originalBasisSet.slice(0, numValidSamples);
				
				// We no longer need the original BasisSet
				originalBasisSet.release();
			}
			
			
			basisSet.makeReadOnly();
			BasisSetEvent event = 
				new BasisSetEvent(this, basisSet, fStartedNewBasisSequence);
			
			while (listeners.hasNext())
			{
				BasisSetListener listener = 
					(BasisSetListener) listeners.next();
				
				// Increment the allocation for each additional listener
				if (listeners.hasNext())
				{
					basisSet.hold();
				}
				
				listener.receiveBasisSetEvent(event);					
			}
			
			fStartedNewBasisSequence = false;
		}
		else
		{
			// If there are no listeners to this BasisBundle, then we 
			// can release the given BasisSet right away.
			
			basisSet.makeReadOnly();
			basisSet.release();
		}
	}


	/**
	 *  Makes the data contained in the given BasisSet (which must have been 
	 *  previously allocated from this BasisBundle) available for reading. 
	 * 
	 *  <p>This method should only be called by the BasisBundleSource that is 
	 *  writing data into this BasisBundle.
	 * 
	 *  @param basisSet A BasisSet of newly-available data. The BasisSet 
	 * 		must have been previously allocated from this BasisBundle
	 *  @throws IllegalArgumentException if the given BasisSet was not 
	 * 		allocated from this BasisBundle, or if this BasisBundle has been 
	 * 		resized subsequent to its allocation
	 */
	
	public void makeAvailable(BasisSet basisSet)
		throws IllegalArgumentException
	{
		makeAvailable(basisSet, basisSet.getSize());
	}
	
	
	/**
	 * Returns a read-only BasisSet of the indicated size from within this
	 * BasisBundle.
	 * 
	 * @param startPosition The start position of the desired range (inclusive).
	 * @param length the length of the slice.
	 * @return A read-only BasisSet slice from within this
	 *         BasisBundle, spanning the specified range of samples in the
	 *         BasisBundle
	 */	
	public BasisSet slice(int startPosition, int length)
	{
		BasisSet slice = fBackingBasisSet.slice(startPosition, length);
		
		slice.makeReadOnly();
		
		return (slice);
	}

    /**
	 * Checks and resolves the given index relative to this BasisBundle.
	 * 
	 * @returns an absolute index into the BasisBundle.
	 */
	public synchronized int resolveIndex(int index)
	{
		int result = index;
		
		if (fMemoryModel != null)
		{
			result = fMemoryModel.resolveIndex(index);
		}
		
		return (result);
	}	

	/**
	 * Adds the given BasisBundleLisener to this BasisBundle as a listener for
	 * BasisBundleEvents.
	 * 
	 * @param listener A BasisBundleLisener
	 */
	public void addBasisBundleListener(BasisBundleListener listener)
	{
		if (listener != null)
		{
			fBasisBundleListeners.add(listener);

			// Alert the given BasisBundleListener of the current structure of 
			// this BasisBundle.		
			BasisBundleEvent event = new BasisBundleEvent(this, fDescriptor);			
			listener.receiveBasisBundleEvent(event);

			synchronized (fBasisBundleListeners)
			{		
				// In the case that the given BasisBundleListener is the first one 
				// added since this BasisBundle was created or last cleared, alert 
				// any processes waiting for a listener to be added to this 
				// BasisBundle that there is one.
			
				if (fBasisBundleListeners.size() == 1)
				{
					fBasisBundleListeners.notifyAll();
				}
			}			
		}
		else
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Attempt to add a null BasisBundleListener";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"addBasisBundleListener", message);
			}
		}
	}


	/**
	 * Removes the given BasisBundleLisener from this BasisBundle as a listener
	 * for BasisBundleEvents.
	 * 
	 * @param listener A BasisBundleLisener
	 */
	public void removeBasisBundleListener(BasisBundleListener listener)
	{
		fBasisBundleListeners.remove(listener);
	}

	/**
	 * Returns the BasisBundleEvent listeners on this BasisBundle as
	 * an array of BasisBundleListeners.
	 * 
	 * @return The BasisBundleEvent listeners on this BasisBundle as
	 *         an array of BasisBundleListeners
	 */
	public final BasisBundleListener[] getBasisBundleListeners()
	{
		return (BasisBundleListener[]) 
			fBasisBundleListeners.toArray(
				new BasisBundleListener[fBasisBundleListeners.size()]);
	}

	/**
	 * Adds the given BasisSetLisener to this BasisBundle as a listener for
	 * BasisSetEvents.
	 * 
	 * @param listener A BasisSetLisener
	 */
	public void addBasisSetListener(BasisSetListener listener)
	{
		if (listener != null)
		{
			fBasisSetListeners.add(listener);

			synchronized (fBasisSetListeners)
			{		
				// In the case that the given BasisSetListener is the first one 
				// added since this BasisBundle was created or last cleared, 
				// alert any processes waiting for a listener to be added to 
				// this BasisBundle that there is one.
			
				if (fBasisSetListeners.size() == 1)
				{
					fBasisSetListeners.notifyAll();
				}
			}			
		}
		else
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Attempt to add a null BasisSetListener";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"addBasisSetListener", message);
			}
		}
	}

	/**
	 * Removes the given BasisSetLisener from this BasisBundle as a listener
	 * for BasisSetEvents.
	 * 
	 * @param listener A BasisSetLisener
	 */
	public void removeBasisSetListener(BasisSetListener listener)
	{
		fBasisSetListeners.remove(listener);
	}

	/**
	 * Returns the BasisSetEvent listeners on this BasisBundle as
	 * an array of BasisSetListeners.
	 * 
	 * @return The BasisSetEvent listeners on this BasisBundle as
	 *         an array of BasisSetListeners
	 */
	public BasisSetListener[] getBasisSetListeners()
	{
		return (BasisSetListener[]) 
			fBasisSetListeners.toArray(new BasisSetListener[fBasisSetListeners.size()]);
	}

	/**
	 * Returns the current number of listeners to this BasisBundle.
	 * 
	 * @return The current number of listeners to this BasisBundle
	 */
	public int getNumListeners()
	{
		return (fBasisSetListeners.size());
	}
	
	/**
	 * Blocks on this BasisBundle until it has at least one BasisSetListener.
	 */
	public void waitForListeners()
	{
		synchronized (fBasisSetListeners)
		{
			while (fBasisSetListeners.size() == 0)
			{
				try
				{
					fBasisSetListeners.wait();
				}
				catch (InterruptedException ex)
				{
					
				}
			}
		}
	}
	
	/**
	 * Returns a String representation of the specified range of data in this
	 * BasisBundle, starting with the BasisBuffer and across each DataBuffer.
	 * 
	 * @param startPosition The start of the data range
	 * @param endPosition The end of the data range
	 * @return A String representation of the specified range of data in this
	 *         BasisBundle
	 */
	public String dataToString(int startPosition, int endPosition)
	{
		return (fBackingBasisSet.dataToString(startPosition, endPosition));
	}

	/**
	 * Returns a String representation of the specified amount of data in this
	 * BasisBundle, starting with the BasisBuffer and across each DataBuffer,
	 * and starting at the beginning of each Buffer.
	 * 
	 * @param amount The amount of data
	 * @return A String representation of the specified amount of data in this
	 *         BasisBundle, starting with the BasisBuffer and across each
	 *         DataBuffer, and starting at the beginning of each Buffer
	 */
	public String dataToString(int amount)
	{
		return (dataToString(0, amount - 1));
	}
	
	/**
	 * Returns a String representation of the data in this BasisBundle, starting
	 * with the BasisBuffer and across each DataBuffer.
	 * <p>
	 * Note that for a typical BasisBundle this could produce a <em>very</em>
	 * long String!
	 * 
	 * @return A String representation of the data in this BasisBundle
	 */	
	public String dataToString()
	{
		return (dataToString(fCapacity));
	}

	/**
	 * Returns a String representation of this BasisBundle.
	 * 
	 * @return A String representation of this BasisBundle
	 */	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(getFullyQualifiedName());	
		
		if (fBackingBasisSet != null)
		{
			synchronized (fBackingBasisSet)
			{
				stringRep.append("\nBasis buffer: " + 
					getBasisBufferDescriptor());
				
				stringRep.append("\nData buffers:");
				
				Iterator dataBufferDescriptors = 
					getDataBufferDescriptors().iterator();
				
				for (int i = 1; dataBufferDescriptors.hasNext(); i++)
				{
					DataBufferDescriptor descriptor = 
						(DataBufferDescriptor) dataBufferDescriptors.next();
					
					stringRep.append("\n" + i + ": " + descriptor);
				}
				
				if (fMemoryModel != null)
				{
					stringRep.append("\n" + fMemoryModel);
				}
			}
		}
		
		return (stringRep.toString());
	}

	/**
	 * @return Returns the memoryModel.
	 */
	protected MemoryModel getMemoryModel()
	{
		return fMemoryModel;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultBasisBundle.java,v $
//  Revision 1.90  2006/08/03 20:20:55  chostetter_cvs
//  Fixed proxy BasisBundle name creation bug
//
//  Revision 1.89  2006/08/01 21:07:00  chostetter_cvs
//  Fixed return type of getBasisBundleId
//
//  Revision 1.88  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.87  2006/05/31 13:05:21  smaher_cvs
//  Name change: UniformSampleRate->UniformSampleInterval
//
//  Revision 1.86  2006/05/19 14:35:09  smaher_cvs
//  Removed use of synchronized(fBasisSetListeners) in makeAvailable(), eliminating a deadlock condition when makeAvailable() and DefaultBasisRequester.stop() executed simultaneously.
//
//  Revision 1.85  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.84  2006/03/14 16:13:18  chostetter_cvs
//  Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
//  Revision 1.83  2006/03/01 02:10:08  tames
//  Modified makeAvailable to guard against consumers releasing the BasisSet
//  before it has been published to all consumers.
//
//  Revision 1.82  2006/02/07 18:09:25  chostetter_cvs
//  Added clear() method
//
//  Revision 1.81  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.80  2005/12/05 04:13:49  tames
//  Added support for a BasisBuffer base to handle conditions where a double
//  does not have enough percision to represent a BasisBuffer value.
//
//  Revision 1.79  2005/11/09 18:43:23  tames_cvs
//  Modified event publishing to use the CopyOnWriteArrayList class to
//  hold listeners. This reduces the overhead when publishing events.
//
//  Revision 1.78  2005/11/07 22:11:46  tames
//  Fixed get listeners typecast bug.
//
//  Revision 1.77  2005/09/13 22:28:58  tames
//  Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.76  2005/08/26 22:13:30  tames_cvs
//  Changes that are an incomplete refactoring. Also added initial support for history.
//
//  Revision 1.75  2005/07/15 19:22:45  chostetter_cvs
//  Organized imports
//
//  Revision 1.74  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.73  2005/04/05 20:35:36  chostetter_cvs
//  Fixed problem with release status of BasisSets from which a copy was made; fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//
//  Revision 1.72  2005/04/04 22:35:39  chostetter_cvs
//  Adjustments to synchronization scheme
//
//  Revision 1.71  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.70  2005/03/24 22:55:01  chostetter_cvs
//  Changed synchronization on wrapping of memory model, removed extraneous methods
//
//  Revision 1.69  2005/03/24 21:54:28  chostetter_cvs
//  Fixed problem with shrinking BasisSets having fewer valid samples than requested, and with calculating spanned Basis values for BasisSets that decrease monotonically (rather than increase).
//
//  Revision 1.68  2005/03/24 18:16:34  chostetter_cvs
//  Fixed problem with releasing unused portions of BasisSets, changed waiting on DataRequestSatisfier repetitions to reach zero to waiting for stop
//
//  Revision 1.67  2005/03/23 00:09:06  chostetter_cvs
//  Tweaks to BasisSet allocation, release
//
//  Revision 1.66  2005/03/22 22:47:06  chostetter_cvs
//  Refactoring of BasisSet allocation, release
//
//  Revision 1.65  2005/02/03 01:05:02  chostetter_cvs
//  Avoid restructuring in setDescriptor when given descriptor is the same as the previous one
//
//  Revision 1.64  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
//  Revision 1.63  2005/01/31 21:35:30  chostetter_cvs
//  Fix for data request sequencing, documentation
//
//  Revision 1.62  2004/11/30 16:53:36  chostetter_cvs
//  Added ability to obtain Set of names that match a given regular expression
//
//  Revision 1.61  2004/11/30 15:50:03  chostetter_cvs
//  Added ability to select a set of DataBuffers by matching names to a given regular expression
//
//  Revision 1.60  2004/11/17 22:23:11  chostetter_cvs
//  Now throws away data allocated before resizing
//
//  Revision 1.59  2004/09/15 20:06:17  chostetter_cvs
//  Fixed issues with freeing of java.nio duplicate memory block freeing
//
//  Revision 1.58  2004/09/15 18:36:53  chostetter_cvs
//  Fixed DataEvent propagation error
//
//  Revision 1.57  2004/09/13 18:43:37  chostetter_cvs
//  Fixed bug with making less-than-full BasisSet available
//
//  Revision 1.56  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.55  2004/08/18 15:14:48  chostetter_cvs
//  toString now checks to see whether a backing BasisSet has been created
//
//  Revision 1.54  2004/08/18 15:04:21  tames
//  Modified startNewBasisSequence method to check if iterator has any
//   elements before calling next().
//
//  Revision 1.53  2004/07/28 20:17:02  chostetter_cvs
//  Implemented selectable and adaptive default output BasisBundle sizing
//
//  Revision 1.52  2004/07/28 19:11:25  chostetter_cvs
//  BasisBundle now alerts of impending discontinuity, BasisRequester copies and releases pending data at discontinuity
//
//  Revision 1.51  2004/07/22 21:29:47  chostetter_cvs
//  BasisBundle name, access by name changes
//
//  Revision 1.50  2004/07/22 20:14:58  chostetter_cvs
//  Data, Component namespace work
//
//  Revision 1.49  2004/07/22 16:28:03  chostetter_cvs
//  Various tweaks
//
//  Revision 1.48  2004/07/21 14:26:14  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.47  2004/07/20 14:53:00  chostetter_cvs
//  Made BasisSets stative with regard to being released
//
//  Revision 1.46  2004/07/19 14:16:14  chostetter_cvs
//  Added ability to subsample data in requests
//
//  Revision 1.45  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.44  2004/07/17 01:25:58  chostetter_cvs
//  Refactored test algorithms
//
//  Revision 1.43  2004/07/16 21:35:24  chostetter_cvs
//  Work on declaring uniform sample rate of data
//
//  Revision 1.42  2004/07/15 19:07:47  chostetter_cvs
//  Added ability to block while waiting for a BasisBundle to have listeners
//
//  Revision 1.41  2004/07/15 05:44:38  chostetter_cvs
//  Mods to determining new BasisSet structure
//
//  Revision 1.40  2004/07/14 22:24:53  chostetter_cvs
//  More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//
//  Revision 1.39  2004/07/14 17:14:09  chostetter_cvs
//  Added access to set of DataBufferIds
//
//  Revision 1.38  2004/07/14 03:43:56  chostetter_cvs
//  Added ability to detect number of data listeners, stop when goes to 0
//
//  Revision 1.37  2004/07/14 00:33:48  chostetter_cvs
//  More Algorithm, data testing. Fixed slice bug.
//
//  Revision 1.36  2004/07/13 18:52:50  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.35  2004/07/12 19:04:45  chostetter_cvs
//  Added ability to find BasisBundleId, Components by their fully-qualified name
//
//  Revision 1.34  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.33  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.32  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.31  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.30  2004/07/09 22:29:11  chostetter_cvs
//  Extensive testing of Input/Output interaction, supports simple BasisRequests
//
//  Revision 1.29  2004/07/08 20:26:17  chostetter_cvs
//  BasisSet allocation, blocking changes
//
//  Revision 1.28  2004/07/08 20:03:11  chostetter_cvs
//  MemoryModel changes
//
//  Revision 1.27  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.26  2004/07/02 15:10:05  chostetter_cvs
//  Further CompositeBasisSet work
//
//  Revision 1.25  2004/07/02 04:08:56  chostetter_cvs
//  Improvements to restructuring, synchronization
//
//  Revision 1.24  2004/07/02 03:26:08  chostetter_cvs
//  If there are no listeners, frees a newly-available BasisSet right away
//
//  Revision 1.23  2004/07/02 02:33:30  chostetter_cvs
//  Renamed DataRequest to BasisRequest
//
//  Revision 1.22  2004/06/30 20:56:20  chostetter_cvs
//  BasisSet is now an interface
//
//  Revision 1.21  2004/06/29 22:46:13  chostetter_cvs
//  Fixed broken CVS-generated comments. Grrr.
//
//  Revision 1.20  2004/06/29 22:39:39  chostetter_cvs
//  Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//
//  Revision 1.19  2004/06/09 03:28:49  chostetter_cvs
//  Output-related modifications
//
//  Revision 1.18  2004/06/05 19:13:00  chostetter_cvs
//  DataBuffer now wraps a ByteBuffer (instead of Buffer) and provides typed views of its ByteBuffer for reading and writing
//
//  Revision 1.17  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.16  2004/06/04 23:10:27  chostetter_cvs
//  Added data printing support to various Buffer classes
//
//  Revision 1.15  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
//  Revision 1.14  2004/06/04 17:28:32  chostetter_cvs
//  More data tweaks. Ready for testing.
//
//  Revision 1.13  2004/06/04 05:34:42  chostetter_cvs
//  Further data, Algorithm, and Component work
//
//  Revision 1.12  2004/06/03 00:19:59  chostetter_cvs
//  Organized imports
//
//  Revision 1.11  2004/06/02 23:59:41  chostetter_cvs
//  More Namespace, DataSpace tweaks, created alogirthms package
//
//  Revision 1.10  2004/05/29 04:30:00  chostetter_cvs
//  Further data-related refinements
//
//  Revision 1.9  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.8  2004/05/27 23:09:26  chostetter_cvs
//  More Namespace related changes
//
//  Revision 1.7  2004/05/27 19:47:45  chostetter_cvs
//  More Namespace, DataSpace changes
//
//  Revision 1.6  2004/05/27 18:23:49  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.5  2004/05/27 15:57:16  chostetter_cvs
//  Data-related changes
//
//  Revision 1.4  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.3  2004/05/16 21:54:27  chostetter_cvs
//  More work
//
//  Revision 1.2  2004/05/16 15:44:36  chostetter_cvs
//  Further data-handling definition
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
