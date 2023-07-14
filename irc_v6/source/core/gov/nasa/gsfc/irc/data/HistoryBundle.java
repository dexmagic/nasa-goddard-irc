//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/HistoryBundle.java,v 1.6 2006/08/01 19:55:47 chostetter_cvs Exp $
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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.system.memory.Allocation;
import gov.nasa.gsfc.commons.system.memory.CompositeAllocation;
import gov.nasa.gsfc.commons.system.memory.ContiguousMemoryModel;
import gov.nasa.gsfc.commons.system.memory.MemoryModel;
import gov.nasa.gsfc.commons.types.namespaces.AbstractCreatedMember;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * A HistoryBundle is a set of DataBuffers all sharing and correlated with a
 * single, common BasisBuffer. This is similar to a BasisBundle except it is
 * designed for a single owner for short term storage of data. It does not
 * broadcast any events.
 * <p>
 * The {@link #appendBasisSet(BasisSet)} method provides the means to add data
 * to this history. Note that appending data will block if there is currently
 * insufficient free space in the HistoryBundle.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Troy Ames
 */

public class HistoryBundle extends AbstractCreatedMember 
{
	private static final String CLASS_NAME = HistoryBundle.class.getName();	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private BasisBundleDescriptor fDescriptor;
	private BasisBundleId fBasisBundleId;
	private MemberId fBasisBundleSourceId;
	
	private int fCapacity = 0;
	private BasisSet fBackingBasisSet;
	private MemoryModel fMemoryModel;
	private boolean fStartedNewBasisSequence = false;
	
	private CompositeAllocation fHistoryAllocation = null;
	private int fHistorySize = 1000;
	
	/**
	 * Constructs a new HistoryBundle for the given BasisBundleSource, as
	 * described by the given BasisBundleDescriptor, and whose backing data
	 * buffers all have the given capacity.
	 * 
	 * @param sourceId The MemberId of the BasisBundleSource that will use the 
	 * 		new BasisBundle
	 * @param descriptor The BasisBundleDescriptor that describes the structure
	 *            of the desired new BasisBundle
	 * @param capacity The uniform capacity of the backing data buffers of the
	 *            new HistoryBundle
	 * @return A new HistoryBundle for the given BasisBundleSource, as described
	 *         by the given BasisBundleDescriptor
	 */
	public HistoryBundle(MemberId sourceId, BasisBundleId id,
		BasisBundleDescriptor descriptor, int capacity)
	{
		super(descriptor.getName(), descriptor.getNameQualifier(), sourceId);
		
		fBasisBundleSourceId = sourceId;
		fBasisBundleId = id;
		
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
			fMemoryModel = new ContiguousMemoryModel(fCapacity);
			
			fBackingBasisSet = new DefaultBasisSet(fBasisBundleId, fCapacity);
		}
	}
	
	/**
	 * Sets the BasisBundleDescriptor that describes the structure of this
	 * HistoryBundle to the given BasisBundleDescriptor. In turn, this
	 * HistoryBundle will subsequently create BasisSets having the new
	 * structure.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this HistoryBundle.
	 * 
	 * @param descriptor The BasisBundleDescriptor that describes the desired
	 *            structure of this HistoryBundle
	 */
	public synchronized final void setDescriptor(BasisBundleDescriptor descriptor)
	{
		if (fDescriptor != descriptor)
		{
			fDescriptor = descriptor;
			
			createNewBackingBuffers();
		}
	}
		
	/**
	 * Returns the BasisBundleDescriptor that describes the structure of this
	 * HistoryBundle.
	 * 
	 * @return The BasisBundleDescriptor that describes the structure of this
	 *         HistoryBundle
	 */	
	public BasisBundleDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	/**
	 * Returns the unique BasisBundleId for this HistoryBundle.
	 * 
	 * @return The unique BasisBundleId for this HistoryBundle
	 */	
	public BasisBundleId getBasisBundleId()
	{
		return (fBasisBundleId);
	}
	
	/**
	 * Returns the MemberId of the BasisBundleSource that created/owns/is 
	 * writing data to this HistoryBundle.
	 * 
	 * @return The MemberId of the BasisBundleSource that created/owns/is 
	 * 		writing data to this HistoryBundle
	 */	
	public MemberId getBasisBundleSourceId()
	{
		return (fBasisBundleSourceId);
	}
		
	/**
	 * Returns the fully-qualified name of this HistoryBundle within whatever
	 * Namespace it may occupy (i.e., of the form "<BasisBundle name> of
	 * <BasisBundleSource name>".
	 * 
	 * @return The fully-qualified name of this HistoryBundle within whatever
	 *         Namespace it may occupy (i.e., of the form "<Object name> of
	 *         <namespace name>"
	 */
	public String getFullyQualifiedName()
	{
		return (getName() + " from " + fBasisBundleSourceId);
	}
	
	/**
	 * Returns the current size of this HistoryBundle (i.e., the uniform size of
	 * each of its Buffers).
	 * 
	 * @return The current size of this HistoryBundle
	 */
	public int getSize()
	{
		return (fCapacity);
	}
	
	/**
	 * Returns the DataBufferDescriptor that describes the basis Buffer of this
	 * HistoryBundle.
	 * 
	 * @return The DataBufferDescriptor that describes the basis Buffer of this
	 *         HistoryBundle
	 */
	public DataBufferDescriptor getBasisBufferDescriptor()
	{
		return (fDescriptor.getBasisBufferDescriptor());
	}
	
	/**
	 * Returns the Set of DataBufferDescriptors that describe the structure of
	 * this HistoryBundle.
	 * 
	 * @return The Set of DataBufferDescriptors that describe the structure of
	 *         this HistoryBundle
	 */
	public Set getDataBufferDescriptors()
	{
		return (fDescriptor.getDataBufferDescriptors());
	}
	
	/**
	 * Returns the ordered Map of the DataBufferDescriptors (by name) of this
	 * HistoryBundle whose names match the given regular expression.
	 * 
	 * @param regEx A regular expression
	 * @return The ordered Map of the DataBufferDescriptors (by name) of this
	 *         HistoryBundle whose names match the given regular expression
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
	 * Returns the Set of names of the DataBuffers of this HistoryBundle. Note
	 * that if a DataBuffer has a Pixel, the Pixel tag is part of its name.
	 * 
	 * @return The Set of names of the DataBuffers of this HistoryBundle
	 */	
	public Set getDataBufferNames()
	{
		return (fDescriptor.getDataEntryNames());
	}
		
	/**
	 * Returns the Set of names of the DataBuffers of this HistoryBundle that
	 * match the given regular expression. Note that if a DataBuffer has a
	 * Pixel, the Pixel tag is part of its name.
	 * 
	 * @param A regular expression
	 * @return The Set of names of the DataBuffers of this HistoryBundle that
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
	 * Returns true if this HistoryBundle has been declared to be uniformly
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
	 * Sets the declared uniform sample interval of the data in all BasisSets
	 * subsequently allocated from this HistoryBundle to the given value, in terms
	 * of the units of its basis buffer. It is up to the caller to guarantee
	 * that the given interval is accurate, and that all basis values in subsequent
	 * BasisSets are in fact uniformly spaced at the declared interval. To indicate
	 * that the HistoryBundle is not uniformly sampled (the default assumption),
	 * set the value to 0 or less, or to Double.Nan.
	 * 
	 * @param sampleInterval The declared uniform sample rate of the data in alll
	 *            BasisSets subsequently allocated from this HistoryBundle to the
	 *            given value, in terms of the units of its basis buffer
	 */
	public void setUniformSampleInterval(double sampleInterval)
	{
		fBackingBasisSet.setUniformSampleInterval(sampleInterval);
	}

	/**
	 * Returns the declared uniform sample interval of the data in all BasisSets
	 * allocated from this HistoryBundle, in terms of the Units of the basis
	 * buffer. If this HistoryBundle is not uniformly sampled, the result will be
	 * Double.Nan.
	 * 
	 * @return The declared uniform sample interval of the data in all BasisSets
	 *         allocated from this HistoryBundle, in terms of the Units of the
	 *         basis buffer
	 */	
	public double getUniformSampleInterval()
	{
		return (fBackingBasisSet.getUniformSampleInterval());
	}
	
	/**
	 * Resizes this HistoryBundle to the given new capacity. This method should
	 * only be called by the creator/owner of this HistoryBundle, and only if an
	 * allocation request fails.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this HistoryBundle.
	 * 
	 * @param capacity The desired new capacity of this HistoryBundle
	 */	
	public synchronized void resize(int capacity)
	{
		fCapacity = capacity;
	
		createNewBackingBuffers();
	}
		
	/**
	 * Informs this HistoryBundle that the next BasisSet its BasisBundleSource
	 * makes available to it will begin a new, coherent sequence of basis values
	 * and their correlated data.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this HistoryBundle.
	 */	
	public void startNewBasisSequence()
	{
		fStartedNewBasisSequence = true;
	}
		
	/**
	 * Allocates and returns a new, BasisSet consisting of the given basisSet
	 * data appended to history data, blocking if necessary until sufficient
	 * space becomes available in this HistoryBundle to hold the new data.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this HistoryBundle.
	 * 
	 * @param size The size of the desired BasisSet
	 * @return A new, writeable BasisSet of the indicated size
	 */
	public synchronized BasisSet appendBasisSet(BasisSet basisSet)
	{
		DefaultBasisSet result = null;
		
		if (fMemoryModel != null)
		{
			Allocation allocation = null;
			
			try
			{
				allocation = fMemoryModel.blockingAllocate(basisSet.getSize());
				
				// TODO check for buffer overflow
				int startIndex = allocation.getStart();
				int bufferEnd = startIndex + basisSet.getSize();
				if (bufferEnd > fCapacity)			
				{
					fBackingBasisSet.put(
						startIndex, basisSet, 0, fCapacity - startIndex);
					fBackingBasisSet.put(
						0, basisSet, fCapacity - startIndex, bufferEnd - fCapacity);
				}
				else
				{
					fBackingBasisSet.put(startIndex, basisSet);
				}
				
				// Remember previous allocation
				CompositeAllocation previousHistoryAllocation = fHistoryAllocation;
				
				// Update history allocation with new allocation
				if (fHistoryAllocation == null)
				{
					// Initialize history allocation
					fHistoryAllocation = 
						new CompositeAllocation(
								allocation, 
								allocation.getStart(),
								allocation.getSize());
				}
				else
				{
					// Add the allocation and if necessary 
					// resize to size of history
					int newSize = 
						fHistoryAllocation.getSize() + allocation.getSize();
					CompositeAllocation newAllocation = null;
					
					if (newSize > fHistorySize)
					{
						newAllocation = 
							(CompositeAllocation) fHistoryAllocation.appendAndSlice(
								allocation, 
								newSize - fHistorySize, 
								fHistorySize);
					}
					else
					{
						newAllocation = 
							(CompositeAllocation) fHistoryAllocation.append(allocation);
					}

					fHistoryAllocation = newAllocation;
				}
				
				// The HistoryBundle needs more control of when a history 
				// allocation is released since it cannot be released to early 
				// before a complete history is created. The result is we need 
				// an additional hold here followed by a release of the previous
				// history allocation. We now don't care when receivers of the
				// result actually release their hold.
				fHistoryAllocation.hold();
				
				if (previousHistoryAllocation != null)
				{
					previousHistoryAllocation.release();
				}
				
				//System.out.println("HistoryAllocation:" + fHistoryAllocation);
				result = (DefaultBasisSet) 
					fBackingBasisSet.slice(
							fHistoryAllocation.getStart(), 
							fHistoryAllocation.getSize());
				
				result.setAllocation(fHistoryAllocation);
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
			finally
			{
				if (allocation != null)
				{
					allocation.release();
				}
			}
		}
		else
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "This HistoryBundle has not yet been sized";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"allocateBasisSet", message);
			}
		}
			
		return (result);
	}

	/**
	 * @return Returns the memoryModel.
	 */
	public MemoryModel getMemoryModel()
	{
		return fMemoryModel;
	}

	/**
	 * Checks and resolves the given index relative to this HistoryBundle.
	 * 
	 * @returns an absolute index into the HistoryBundle.
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
	 * Returns a String representation of the specified range of data in this
	 * HistoryBundle, starting with the BasisBuffer and across each DataBuffer.
	 * 
	 * @param startPosition The start of the data range
	 * @param endPosition The end of the data range
	 * @return A String representation of the specified range of data in this
	 *         HistoryBundle
	 */	
	public String dataToString(int startPosition, int endPosition)
	{
		return (fBackingBasisSet.dataToString(startPosition, endPosition));
	}	
	
	/**
	 * Returns a String representation of the specified amount of data in this
	 * HistoryBundle, starting with the BasisBuffer and across each DataBuffer,
	 * and starting at the beginning of each Buffer.
	 * 
	 * @param amount The amount of data
	 * @return A String representation of the specified amount of data in this
	 *         HistoryBundle, starting with the BasisBuffer and across each
	 *         DataBuffer, and starting at the beginning of each Buffer
	 */	
	public String dataToString(int amount)
	{
		return (dataToString(0, amount - 1));
	}
			
	/**
	 * Returns a String representation of the data in this HistoryBundle,
	 * starting with the BasisBuffer and across each DataBuffer.
	 * <p>
	 * Note that for a typical HistoryBundle this could produce a <em>very</em>
	 * long String!
	 * 
	 * @return A String representation of the data in this HistoryBundle
	 */	
	public String dataToString()
	{
		return (dataToString(fCapacity));
	}
	
	/**
	 * Returns a String representation of this HistoryBundle.
	 * 
	 * @return A String representation of this HistoryBundle
	 */	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer("HistoryBundle " + 
			getFullyQualifiedName());	
		
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
	 * @return Returns the historySize.
	 */
	public int getHistorySize()
	{
		return fHistorySize;
	}

	/**
	 * @param historySize The historySize to set.
	 */
	public void setHistorySize(int historySize)
	{
		fHistorySize = historySize;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: HistoryBundle.java,v $
//  Revision 1.6  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.5  2006/06/02 19:20:04  smaher_cvs
//  Name change: UniformSampleRate->UniformSampleInterval
//
//  Revision 1.4  2006/05/31 13:05:22  smaher_cvs
//  Name change: UniformSampleRate->UniformSampleInterval
//
//  Revision 1.3  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/22 18:45:02  tames
//  Fixed errors with releasing pending allocations.
//
//  Revision 1.1  2005/09/09 21:34:18  tames
//  BasisRequester framework refactoring.
//
//  Revision 1.1  2005/08/26 22:16:23  tames_cvs
//  Partial initial implementation.
//
//
