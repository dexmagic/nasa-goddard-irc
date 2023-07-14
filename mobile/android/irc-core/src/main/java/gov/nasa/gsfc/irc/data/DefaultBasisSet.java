//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/DefaultBasisSet.java,v 1.71 2006/08/10 16:13:22 smaher_cvs Exp $
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

import java.nio.BufferOverflowException;
import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.commons.system.memory.AbstractAllocation;
import gov.nasa.gsfc.commons.system.memory.Allocation;
import gov.nasa.gsfc.commons.types.namespaces.AbstractCreatedMember;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * A BasisSet is constrained view on the basis Buffer and on some set of the
 * data Buffers of a BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/10 16:13:22 $
 * @author Carl F. Hostetter
 */

public class DefaultBasisSet extends AbstractCreatedMember implements BasisSet
{
	private BasisBundleDescriptor fDescriptor;
	private BasisBundleId fBasisBundleId;
	private transient BasisBundle fBasisBundle;
	private MemberId fBasisBundleSourceId;
	
	protected int fSetOffset = -1;
	protected int fSetLength = 0;
	protected double fBasisBase = 0.0d;

	//MemoryModel fMemoryModel;
    Allocation fAllocation;	
	
	private static final transient DataBufferFactory fDataBufferFactory = 
		DataBufferFactory.getInstance();
	
	private DataBuffer fBasisBuffer;
	private LinkedHashMap fDataBuffersByName;
	private ArrayList fDataBuffers;
	
	private double fSampleRate = Double.NaN;
	private boolean fIsReadOnly = false;
	private boolean fIsCopy = false;
	
	/**
	 *  Constructs a new BasisSet in accordance with the structure of the 
	 *  given BasisBundle and having the given capacity.
	 *
	 *  @param basisBundle The BasisBundle of the new BasisSet
	 *  @param capacity The capacity of each of the Buffers of the new 
	 * 		BasisSet
	 **/
	public DefaultBasisSet(BasisBundle basisBundle, int capacity)
	{
		super(basisBundle.getName(), basisBundle.getNameQualifier(), 
			basisBundle.getBasisBundleId());
		
		fBasisBundle = basisBundle;
		fBasisBundleId = basisBundle.getBasisBundleId();
		fBasisBundleSourceId = basisBundle.getBasisBundleSourceId();
		fDescriptor = basisBundle.getDescriptor();
		fDataBuffers = new ArrayList();
		fDataBuffersByName = new LinkedHashMap();
		fBasisBase = basisBundle.getBasisBase();
		
		if (capacity > 0)
		{
			makeNewDataBuffers(capacity);
		}
	}
	
	/**
	 *  Constructs a new BasisSet in accordance with the structure 
	 *  of the given BasisBundle, but without allocating any DataBuffers.
	 *
	 *  @param basisBundle The BasisBundle of the new BasisSet
	 **/
	public DefaultBasisSet(BasisBundle basisBundle)
	{
		this(basisBundle, 0);
	}
	
	/**
	 *  Constructs a new BasisSet in accordance with the structure of the 
	 *  BasisBundle indicated by the given BasisBundleId, and having the given 
	 *  capacity.
	 *
	 *  @param basisBundleId The BasisBundleId of the BasisBundle of the new 
	 * 		BasisSet
	 *  @param capacity The capacity of each of the Buffers of the new 
	 * 		BasisSet
	 **/

	public DefaultBasisSet(BasisBundleId basisBundleId, int capacity)
	{
		this(Irc.getDataSpace().getBasisBundle(basisBundleId), capacity);
	}
	
	
	/**
	 * Constructs a new BasisSet in accordance with the structure of the
	 * BasisBundle indicated by the given BasisBundleId, but without allocating
	 * any DataBuffers.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle of the new
	 *            BasisSet
	 */
	public DefaultBasisSet(BasisBundleId basisBundleId)
	{
		this(basisBundleId, 0);
	}
	
	/**
	 * Constructs a new BasisSet having the same structure and pointing to the
	 * same set of basis and data buffers as the given BasisSet. This is useful
	 * for making copies, slices, duplicates, and filtered subsets of the data
	 * and structure of the given BasisSet without altering it.
	 * <p>
	 * If the original BasisSet has an associated Allocation it is shared with
	 * the result and incremented. Therefore you should always release the new
	 * BasisSet when it is no longer needed.
	 * 
	 * @param basisSet The DefaultBasisSet to clone
	 */
	public DefaultBasisSet(DefaultBasisSet basisSet)
	{
		this(basisSet.fBasisBundleId);
		
		fSetOffset = basisSet.fSetOffset;
		fSetLength = basisSet.fSetLength;
		
		fIsCopy = basisSet.fIsCopy;
		fIsReadOnly = basisSet.fIsReadOnly;
		fSampleRate = basisSet.fSampleRate;
		fBasisBase = basisSet.fBasisBase;
		
		fBasisBuffer = basisSet.fBasisBuffer;
				
		fDataBuffersByName = (LinkedHashMap) basisSet.fDataBuffersByName.clone();
		fDataBuffers = (ArrayList) basisSet.fDataBuffers.clone();
		
		if (basisSet.fAllocation != null)
		{
			fAllocation = basisSet.fAllocation;
			fAllocation.hold();
		}
	}
    
	/**
	 *  Causes this BasisSet to create a new set of DataBuffers, according 
	 *  to its current List of DataBufferDescriptors and the given capacity.
	 *
	 * @param capacity the size of the new DataBuffers
	 */
	private void makeNewDataBuffers(int capacity)
	{
		DataBufferDescriptor basisBufferDescriptor = 
			fDescriptor.getBasisBufferDescriptor();
		
		fSetOffset = 0;
		fSetLength = capacity;
		fBasisBuffer = fDataBufferFactory.createDataBuffer
			(basisBufferDescriptor, capacity);
		
		for (Iterator dataBufferDescriptors = 
			fDescriptor.getDataBufferDescriptors().iterator(); 
			dataBufferDescriptors.hasNext();)
		{
			DataBufferDescriptor descriptor = (DataBufferDescriptor) 
				dataBufferDescriptors.next();
			
			DataBuffer buffer = 
				fDataBufferFactory.createDataBuffer(descriptor, capacity);
			
			fDataBuffersByName.put(buffer.getName(), buffer);
			fDataBuffers.add(buffer);
		}
	}
	
	/**
	 * Returns a new BasisSet having the same structure and pointing to the same
	 * set of basis and data buffers as this given BasisSet. This is useful for
	 * making copies, slices, duplicates, and filtered subsets of the data and
	 * structure of the given BasisSet without altering it. If the original
	 * BasisSet has an associated Allocation it is shared with the result and
	 * incremented.
	 * 
	 * @return A shallow clone of this BasisSet
	 */
	protected BasisSet cloneBasisSet()
	{
		return (new DefaultBasisSet(this));
	}
	
	/**
	 * Returns a new BasisSet having the field values as this given BasisSet. 
	 * This is useful for making copies, slices, duplicates, and filtered 
	 * subsets of the data when the structure of the new BasisSet will be changed
	 * by the caller. 
	 * 
	 * @return A shallow clone of this BasisSet without the DataBuffers.
	 */
	private DefaultBasisSet shallowCloneBasisSet()
	{
		DefaultBasisSet result = new DefaultBasisSet(fBasisBundle, 0);
		
		// Overide the standard BasisBundle descriptor with
		// the descriptor for this basis set, which may
		// be filtered.  S. Maher
		// FIXME Separate basis request descriptors from "parent" basis bundle descriptors.
		result.fDescriptor = fDescriptor;
		
		result.fSetOffset = fSetOffset;
		result.fSetLength = fSetLength;
		
		result.fIsCopy = fIsCopy;
		result.fIsReadOnly = fIsReadOnly;
		result.fSampleRate = fSampleRate;
		result.fBasisBase = fBasisBase;
		
		return result;
	}
	
	
	/**
	 * Clears each of the values in each of the DataBuffers of this BasisSet 
	 * (including the BasisBuffer) to an appropriate value for its type. If this 
	 * BasisSet is read-only, this call has no effect, and throws an 
	 * UnsupportedOperationException
	 * 
	 * @return This BasisSet
	 * @throws UnsupportedOperationException if this is a read-only BasisSet
	 */
	
	public BasisSet clear()
		throws UnsupportedOperationException
	{
		if (! isReadOnly())
		{
			if (fBasisBuffer != null)
			{
				fBasisBuffer.clear();
				
				if (fDataBuffers != null)
				{
					Iterator dataBuffers = fDataBuffers.iterator();
					
					while (dataBuffers.hasNext())
					{
						DataBuffer dataBuffer = (DataBuffer) dataBuffers.next();
						
						dataBuffer.clear();
					}
				}
			}
		}
		else
		{
			String message = getFullyQualifiedName() + " is read-only";
			
			throw (new UnsupportedOperationException(message));
		}
		
		return (this);
	}
	

	/**
	 * Returns a copy of this BasisSet, having exactly the same structure as
	 * this BasisSet but containing a copy of the underlying basis and data
	 * buffers. Note because the result is a copy, modifying either the original
	 * or the copy will have no effect on the other.
	 * 
	 * @return A copy of this BasisSet
	 */
	public BasisSet copy()
	{
		DefaultBasisSet result = shallowCloneBasisSet();
		
		result.fIsCopy = true;				
		result.fAllocation = null;
		result.fBasisBuffer = fBasisBuffer.copy();
		
		int numBuffers = fDataBuffers.size();
		result.fDataBuffers.ensureCapacity(numBuffers);
		
		for (int i=0; i < numBuffers; i++)
		{
			DataBuffer bufferCopy = ((DataBuffer) fDataBuffers.get(i)).copy();
			result.fDataBuffers.add(bufferCopy);
			result.fDataBuffersByName.put(bufferCopy.getName(), bufferCopy);
		}
				
		return (result);
	}
	
	/**
	 *  Returns true if this BasisSet is a copy of some other BasisSet (i.e., not 
	 *  actually an allocation on some portion of its source BasisBundle), false 
	 *  otherwise.
	 *
	 *  @return True if this BasisSet is a copy of some other BasisSet (i.e., not 
	 *  		actually an allocation on some portion of its source BasisBundle), 
	 *  		false otherwise
	 **/
	public boolean isCopy()
	{
		return (fIsCopy);
	}
	
	/**
	 * Returns a new BasisSet having the same structure and pointing to the same
	 * set of basis and data buffers as this given BasisSet. If the original
	 * BasisSet has an associated Allocation it is shared with the result and
	 * incremented. The caller should always release the returned BasisSet when
	 * it is no longer needed.
	 * 
	 * @return A duplicate of this BasisSet
	 */
	public BasisSet duplicate()
	{
		DefaultBasisSet result = (DefaultBasisSet) cloneBasisSet();
		
		return (result);
	}
	
	/**
	 *  Returns a new BasisSet with the same structure as this BasisSet but 
	 * 	whose basis and data buffers contain only every nth sample of the 
	 *  originals, where n is the given sampling rate. This has the effect 
	 *  of reducing the amount of data, the size of the result being 
	 *  1/n (with integer rounding).
	 * 
	 *  <p> If the given rate is 1 or less, the result will be a copy of the 
	 *  entire buffer; if it is larger than the capacity of this DataBuffer, 
	 *  an IllegalArgumentException is thrown.
	 *
	 *  @param sampleRate The subsampling rate
	 *  @return A new BasisSet with the same structure as this BasisSet but 
	 * 		whose basis and data buffers contain only every nth sample of 
	 *		the originals, where n is the given sampling rate
	 *  @throws IllegalArgumentException if the given sample rate is greater 
	 * 		than the current capacity of this BasisSet
	 **/
	public BasisSet downsample(int sampleRate)
	{
		DefaultBasisSet result = null;
		
		if (sampleRate > 1)
		{
			if (sampleRate <= fBasisBuffer.getSize())
			{
				result = shallowCloneBasisSet();
				
				result.fIsCopy = true;				
				result.fBasisBuffer = fBasisBuffer.downsample(sampleRate);
				
				int numBuffers = fDataBuffers.size();
				result.fDataBuffers.ensureCapacity(numBuffers);
				
				for (int i=0; i < numBuffers; i++)
				{
					DataBuffer downsampledBuffer = 
						((DataBuffer) fDataBuffers.get(i)).downsample(sampleRate);
					result.fDataBuffers.add(downsampledBuffer);
					result.fDataBuffersByName.put(
						downsampledBuffer.getName(), downsampledBuffer);
				}
				
				if (this.isUniformlySampled())
				{
					result.fSampleRate = this.fSampleRate * sampleRate;
				}
				
				// Set new downsampled size of result
				result.fSetLength = result.fBasisBuffer.getSize();
			}
			else
			{
				String message = "The given sample rate (" + sampleRate + 
					" exceeds the capacity of this BasisSet:\n" + this;
				
				throw (new IllegalArgumentException(message));
			}
		}
		else
		{
			result = (DefaultBasisSet) this.copy();
		}

		return (result);
	}
	
	/**
	 * Returns a slice from this BasisSet, having exactly the same structure as
	 * this BasisSet but spanning only the given subrange of samples. The
	 * underlying basis and data buffers are shared with the original.
	 * <p>
	 * If the original BasisSet has an associated Allocation it is shared with
	 * the result and incremented. The caller should always release the new
	 * BasisSet slice when it is no longer needed.
	 * 
	 * @param startIndex The index of the start position of the desired slice
	 *            from this BasisSet (inclusive)
	 * @param length the number of samples in the slice.
	 * @return A slice from this BasisSet, having exactly the same structure as
	 *         this BasisSet but spanning the given subrange of samples.
	 */
	public BasisSet slice(int startIndex, int length)
	{
		// TODO check arguments
		DefaultBasisSet result = this.shallowCloneBasisSet();

		if (fAllocation != null)
		{
			result.fAllocation = (AbstractAllocation) fAllocation.slice(startIndex, length);
			result.fSetOffset = result.fAllocation.getStart();
		}
		else
		{
			//System.out.println("DBS slice fSetOffset:" + fSetOffset + " startIndex:" + startIndex + " resolve:" + fBasisBundle.resolveIndex(fSetOffset + startIndex));
			result.fSetOffset = fBasisBundle.resolveIndex(fSetOffset + startIndex);
			//System.out.println("DBS slice new fSetOffset:" + result.fSetOffset + " startIndex:" + result.getBasisBundleStartPosition() + " resolve:" + fBasisBundle.resolveIndex(fSetOffset + startIndex));
		}
		
		result.fSetLength = length;		
		result.fBasisBuffer = fBasisBuffer.slice(startIndex, length);
		
		int numBuffers = fDataBuffers.size();
		result.fDataBuffers.ensureCapacity(numBuffers);
		
		for (int i=0; i < numBuffers; i++)
		{
			DataBuffer dataBuffer = 
				((DataBuffer) fDataBuffers.get(i)).slice(startIndex, length);
			result.fDataBuffers.add(dataBuffer);
			result.fDataBuffersByName.put(dataBuffer.getName(), dataBuffer);
		}

		return (result);
	}
	
	/**
	 * Returns a new BasisSet that includes the samples from the specified
	 * BasisSet appended to this BasisSet. Both BasisSets must have the same
	 * structure. This may result in copying the data from the two source
	 * BasisSets to the result. If copying the data is not necessary then the
	 * underlying basis and data buffers are shared. Use the <code>isCopy</code>
	 * method to determine if the result is a copy.
	 * <p>
	 * If either BasisSets have an associated Allocation it is shared with the
	 * result and incremented. The caller should always release the returned
	 * BasisSet when it is no longer needed.
	 * 
	 * @param source The BasisSet to be appended.
	 * @return A BasisSet that represents the two BasisSets.
	 */
	public BasisSet append(BasisSet source)
	{
		//TODO need to handle the case where the two basis sets have different BasisBase values.
		BasisSet result = null;
		//System.out.println("DBSet Append ");
		
		if ((!isCopy() && !source.isCopy()) && isContiguousWith(source))
		{
			//System.out.println("DBSet Append isContiguous");
			int newStart = fSetOffset;
			int newLength = getSize() + source.getSize();
			
			result = fBasisBundle.slice(newStart, newLength);
			
			// If this basis set is filtered, then filter the new slice.
			// TODO is there a better way to determine filtering?
			// This won't hurt if it's not filtered - it just is a performance
			// boost.
			if (result.getDataBufferNames().size() != getDataBufferNames().size())
			{
				result = filterByTemplate((DefaultBasisSet) result, this);
			}
			
			
			// If an allocation exist they must also be appended
			if (fAllocation != null)
			{
				result.setAllocation(fAllocation.append(source.getAllocation()));
			}
		}
		else
		{
			//System.out.println("DBSet Append is NOT Contiguous");
			int newCapacity = getSize() + source.getSize();
			
			result = new DefaultBasisSet(fBasisBundle, newCapacity);
			
			// If this basis set is filtered, then filter the new slice.
			// TODO is there a better way to determine filtering?
			// This won't hurt if it's not filtered - it just is a performance
			// boost.			
			if (result.getDataBufferNames().size() != getDataBufferNames().size())
			{
				result = filterByTemplate((DefaultBasisSet) result, this);
			}			

			DataBuffer newBasisBuffer = result.getBasisBuffer();
			DataBuffer firstBasisBuffer = getBasisBuffer();
			DataBuffer secondBasisBuffer = source.getBasisBuffer();
			newBasisBuffer.put(0, firstBasisBuffer);
			newBasisBuffer.put(firstBasisBuffer.getSize(), secondBasisBuffer);
			
			Iterator newDataBuffers = result.getDataBuffers();
			Iterator firstDataBuffers = getDataBuffers();
			Iterator secondDataBuffers = source.getDataBuffers();
			
			while (newDataBuffers.hasNext())
			{
				DataBuffer newDataBuffer = 
					((DataBuffer) newDataBuffers.next());
				DataBuffer firstDataBuffer = 
					((DataBuffer) firstDataBuffers.next());
				DataBuffer secondDataBuffer = 
					((DataBuffer) secondDataBuffers.next());
				
				newDataBuffer.put(0, firstDataBuffer);
				newDataBuffer.put(firstDataBuffer.getSize(), secondDataBuffer);
			}
			
			result.makeReadOnly();
		}
		
		// If both BasisSets are uniformly sampled at the same rate, the 
		// result is uniformly sampled at the same rate.
		
		if (getUniformSampleInterval() == source.getUniformSampleInterval());
		{
			result.setUniformSampleInterval(getUniformSampleInterval());
		}
		
		return (result);
	}
	
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.BasisSet#put(int, gov.nasa.gsfc.irc.data.BasisSet)
     */
    public BasisSet put(int index, BasisSet source)
    {
    	return put(index, source, 0, source.getSize());
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.BasisSet#put(int, gov.nasa.gsfc.irc.data.BasisSet, int, int)
     */
    public BasisSet put(int index, BasisSet source, int sourceIndex, int length)
	{
		if (source == this)
		{
			throw new IllegalArgumentException();
		}
		
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}
		
		if (length + index > getSize())
		{
			throw new BufferOverflowException();
		}
		
		// Copy the basis data
		this.getBasisBuffer().put(
				index, source.getBasisBuffer(), sourceIndex, length);
		
		// Copy the data for each data buffer
		for (Iterator buffers = this.getDataBuffers(); buffers.hasNext();)
		{
			DataBuffer buffer = (DataBuffer) buffers.next();
			DataBuffer sourceBuffer = source.getDataBuffer(buffer.getName());
			
			buffer.put(index, sourceBuffer, sourceIndex, length);
		}
		
		return this;
	}

    /**
	 * Returns a new BasisSet that includes only those of the DataBuffers of
	 * this BasisSet whose names are included in the given Set of names. Note
	 * that if a DataBuffer has a Pixel, its name includes the Pixel tag. The
	 * underlying basis and included data buffers are shared with the original.
	 * <p>
	 * If the original BasisSet has an associated Allocation it is shared with
	 * the result and incremented. The caller should always release the filtered
	 * BasisSet when it is no longer needed.
	 * 
	 * @param dataBufferNames The names of the DataBuffers to retain in the
	 *            filtered BasisSet
	 * @return A new BasisSet that includes only those of the DataBuffers of
	 *         this BasisSet whose DataBufferIds are included in the given Set
	 *         of DataBufferIds
	 */
	public BasisSet filterIn(Set dataBufferNames)
	{
		DefaultBasisSet result = shallowCloneBasisSet();
		
		result.fBasisBuffer = fBasisBuffer;
		
		if ((dataBufferNames != null) && (dataBufferNames.size() > 0))
		{
			Set newDataBufferDescriptors = 
				fDescriptor.getDataBufferDescriptors();
			
			Set expandedDataBufferNames = expandDataBufferRegularExpressions(dataBufferNames);
			
			Iterator dataBufferDescriptors = newDataBufferDescriptors.iterator();
			
			while (dataBufferDescriptors.hasNext())
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor) 
					dataBufferDescriptors.next();
			
				String name = (String) descriptor.getName();
				
				if (expandedDataBufferNames.contains(name))
				{
					// Add buffer to the result
					DataBuffer buffer = (DataBuffer) fDataBuffersByName.get(name);
					
					result.fDataBuffersByName.put(name, buffer);
					result.fDataBuffers.add(buffer);
				}
				else
				{
					// Remove descriptor from the result
					dataBufferDescriptors.remove();
				}
			}
			
			// Update allocation since the filtered result shares DataBuffers
			if (fAllocation != null)
			{
				fAllocation.hold();
				result.setAllocation(fAllocation);
			}
			
			result.fDescriptor = new BasisBundleDescriptor
				(result.fDescriptor.getName(), 
					result.getBasisBufferDescriptor(), 
						newDataBufferDescriptors);
		}
		
		return (result);
	}

	/**
	 * @param dataBufferNames
	 * @return
	 */
	private Set expandDataBufferRegularExpressions(Set dataBufferNames)
	{
		Set expandedDataBufferNames = new HashSet();
		for (Iterator iter = dataBufferNames.iterator(); iter
				.hasNext();)
		{
			String possibleRegEx = (String) iter.next();
			if (possibleRegEx.startsWith(BasisRequest.REGEX_DELIMETER_START) && 
					possibleRegEx.endsWith(BasisRequest.REGEX_DELIMETER_END))
			{
				// Add all buffer names matching regex to results
				expandedDataBufferNames.addAll(getDataBufferNames(possibleRegEx.substring(1, possibleRegEx.length() - 1)));
			}
			else
			{
				// Add name as-is to results
				expandedDataBufferNames.add(possibleRegEx);
			}
			
		}
		return expandedDataBufferNames;
	}

	/**
	 * Returns a new BasisSet that includes only those of the DataBuffers of
	 * the provided source BasisSet whose names are included in the given template.
	 * <p>
	 * If the original BasisSet has an associated Allocation it is shared with
	 * the result and incremented. The caller should always release the filtered
	 * BasisSet when it is no longer needed.
	 * 
	 * @param source BasisSet that will be filtered
	 * @param template BasisSet from which the descriptor and databuffer list will be copied
	 * @return A new BasisSet that includes only those of the DataBuffers of
	 *         the source BasisSet whose DataBufferIds are included in the given template BasisSet
	 */
	private static BasisSet filterByTemplate(DefaultBasisSet source, DefaultBasisSet template)
	{
		DefaultBasisSet result = source.shallowCloneBasisSet();
		
		// Override descriptor with the template's (may reflect "filtered" data buffers)
		source.fDescriptor = template.fDescriptor;
		
		result.fBasisBuffer = source.fBasisBuffer;
		
		// Make defensive copy since we're going to change the list
		Set dataBufferNames = new HashSet(template.getDataBufferNames());
		
		if ((dataBufferNames != null) && (dataBufferNames.size() > 0))
		{
			Set newDataBufferDescriptors = 
				source.getDataBufferDescriptors();
			
			Iterator dataBufferDescriptors = newDataBufferDescriptors.iterator();
			
			while (dataBufferDescriptors.hasNext())
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor) 
					dataBufferDescriptors.next();
			
				String name = (String) descriptor.getName();
				
				if (dataBufferNames.contains(name))
				{
					// Add buffer to the result
					DataBuffer buffer = (DataBuffer) source.fDataBuffersByName.get(name);
					
					result.fDataBuffersByName.put(name, buffer);
					result.fDataBuffers.add(buffer);

					// Remove name so we can check for extras at the end
					dataBufferNames.remove(name);
				}
				else
				{
					// Remove descriptor from the result
					dataBufferDescriptors.remove();
				}
			}
			
			if (dataBufferNames.size() > 0)
			{
				throw new IllegalArgumentException("Data buffer(s) were not found in basis bundle: " + dataBufferNames);
			}
			
			// Update allocation since the filtered result shares DataBuffers
			if (source.fAllocation != null)
			{
				source.fAllocation.hold();
				result.setAllocation(source.fAllocation);
			}
			
			result.fDescriptor = new BasisBundleDescriptor
				(result.fDescriptor.getName(), 
					result.getBasisBufferDescriptor(), 
						newDataBufferDescriptors);
		}
		
		return (result);
	}
	
	/**
	 * Returns a new BasisSet that includes only those of the DataBuffers of
	 * this BasisSet whose DataBufferIds are <em>not</em> included in the
	 * given Set of DataBufferIds. The underlying basis and included data
	 * buffers are shared with the original.
	 * <p>
	 * If the original BasisSet has an associated Allocation it is shared with
	 * the result and incremented. The caller should always release the
	 * filtered BasisSet when it is no longer needed.
	 * 
	 * @param dataBufferIds The DataBufferIds of the DataBuffers to exclude from
	 *            the filtered BasisSet
	 * @return A new BasisSet that includes only those of the DataBuffers of
	 *         this BasisSet whose DataBufferIds are <em>not</em> included in
	 *         the given Set of DataBufferIds
	 */
	public BasisSet filterOut(Set dataBufferNames)
	{
		DefaultBasisSet result = shallowCloneBasisSet();
		
		result.fBasisBuffer = fBasisBuffer;
		
		if ((dataBufferNames != null) && (dataBufferNames.size() > 0))
		{
			Set expandedDataBufferNames = expandDataBufferRegularExpressions(dataBufferNames);
			
			Set newDataBufferDescriptors = 
				fDescriptor.getDataBufferDescriptors();
			
			Iterator dataBufferDescriptors = 
				newDataBufferDescriptors.iterator();
			
			while (dataBufferDescriptors.hasNext())
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor) 
					dataBufferDescriptors.next();
			
				String name = (String) descriptor.getName();
				
				if (expandedDataBufferNames.contains(name))
				{
					// Remove descriptor from the result
					dataBufferDescriptors.remove();
				}
				else
				{
					// Add buffer to the result
					DataBuffer buffer = (DataBuffer) fDataBuffersByName.get(name);
					
					result.fDataBuffersByName.put(name, buffer);
					result.fDataBuffers.add(buffer);
				}
			}
			
			// Update allocation since the filtered result shares DataBuffers
			if (fAllocation != null)
			{
				fAllocation.hold();
				result.setAllocation(fAllocation);
			}

			result.fDescriptor = new BasisBundleDescriptor
				(result.fDescriptor.getName(), 
					result.getBasisBufferDescriptor(), 
						newDataBufferDescriptors);
		}

		return (result);
	}
	
	/**
	 *  Returns the BasisBundleId of this BasisSet.
	 *
	 *  @return The BasisBundleId of this BasisSet
	 **/
	public BasisBundleId getBasisBundleId()
	{
		return (fBasisBundleId);
	}	
	
	/**
	 *  Returns the MemberId of the BasisBundleSource of this BasisSet.
	 *
	 *  @return The MemberId of the BasisBundleSource of this BasisSet
	 **/
	public MemberId getBasisBundleSourceId()
	{
		return (fBasisBundleSourceId);
	}
		
	/**
	 *  Returns the index of the start position of this BasisSet within the 
	 *  BasisBundle it was allocated from (if any).
	 *
	 *  @return The index of the start position of this BasisSet within the 
	 *  		BasisBundle it was allocated from (if any). 
	 *  @throws UnsupportedOperationException if this BasisSet has no defined 
	 *  		BasisBundle position
	 **/
	public int getBasisBundleStartPosition()
	{
		int result = fSetOffset;
		
		if (result < 0)
		{
			String message = "This BasisSet has no defined BasisBundle position";
			
			throw (new UnsupportedOperationException(message));
		}
		
		return (result);
	}
	
	/**
	 *  Returns true if the end of this BasisSet immediately precedes the start of 
	 *  the given BasisSet in its BasisBundle, false otherwise.
	 *  
	 *  @param second The second of two BasisSets to test for contiguity with this 
	 *  		BasisSet, and which must have been allocated from the same BasisBundle 
	 *  		as this BasisSet
	 *  @return True if the end of this BasisSet immediately precedes the start of 
	 *  		the given BasisSet in its BasisBundle, false otherwise 
	 **/
	protected boolean isContiguousWith(BasisSet second)
	{
		boolean result = false;	
		int myEnd = fSetOffset + fSetLength;

		if (fAllocation != null)
		{
			result = fAllocation.isContiguousWith(second.getAllocation());
		}
		else if (myEnd == second.getBasisBundleStartPosition())
		{
			result = true;
		}
		
		return (result);
	}
	
	/**
	 *  Returns the size of this BasisSet in number of basis samples.
	 *
	 *  @return The size of this BasisSet in number of basis samples
	 **/
	public int getSize()
	{
		return (fSetLength);
	}

	/**
	 *  Returns the first basis value of this BasisSet.
	 *
	 *  @return The first basis value of this BasisSet
	 **/
	public double getFirstBasisValue()
	{
		return (fBasisBuffer.getAsDouble(0));
	}

	/**
	 *  Returns the last basis value of this BasisSet.
	 *
	 *  @return The last basis value of this BasisSet
	 **/
	public double getLastBasisValue()
	{
		return (fBasisBuffer.getAsDouble(fBasisBuffer.getSize() - 1));
	}

	/**
	 *  Returns the size of this BasisSet in absolute amount of basis values 
	 *  spanned. This method assumes that the values in the BasisBuffer of this 
	 *  BasisSet are numeric and monotonic (either increasing or decreasing).
	 *
	 *  @return The size of this BasisSet in absolute amount of basis values 
	 * 		spanned.
	 **/	
	public double getBasisAmount()
	{
		double firstBasisValue = fBasisBuffer.getAsDouble(0);
		double lastBasisValue = fBasisBuffer.getAsDouble(fBasisBuffer.getSize() - 1);
		
		double basisAmount = Math.abs(lastBasisValue - firstBasisValue);
		
		return (basisAmount);
	}

	/**
	 * Sets the base or epoch that all BasisBuffer values are relative 
	 * to in this BasisSet.
	 * 
	 * @param base The base or epoch of the BasisBuffer.
	 * @throws UnsupportedOperationException if the BasisSet is read only
	 */
	protected void setBasisBase(double base) throws UnsupportedOperationException
	{
		//TODO should this method be public? 
		if (!fIsReadOnly)
		{
			fBasisBase = base;
		}
		else
		{
			throw new UnsupportedOperationException(
				"Cannot set base on a read only basis set");
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisSet#getBasisBase()
	 */
	public double getBasisBase()
	{
		return fBasisBase;
	}
	
	/**
	 *	Returns the index at which the given value occurs in the basis buffer 
	 *  of this BasisSet, as an upper bound on the basis value. If the given 
	 *  value does not occur, the index returned will be that of the greatest 
	 *  basis value that does not exceed the given value. If the first basis 
	 *  buffer value exceeds the given value, the result will be negative. If 
	 *  the given value is greater than the last basis value, the result will 
	 *  the capacity of this BasisSet (i.e., one more than the last index of 
	 *  the basis buffer).
	 * 
	 *  @param value The absolute basis value to find in this BasisSet
	 *  @return The index at which the given value, or the greatest basis value 
	 * 		that does not exceed the given value, occurs in the basis buffer of 
	 * 		this BasisSet
	 */
	public int getIndexOfBasisValueAsUpperBound(double upperBound)
	{
		int result = -1;
		
		int limit = fBasisBuffer.getSize();
		boolean done = false;
		
		for (int i = 0; (i < limit) && ! done; i++)
		{
			double value = fBasisBuffer.getAsDouble(i);
			
			if (value > upperBound)
			{
				done = true;
				
				result = i - 1;
			}
		}
		
		if (! done)
		{
			result = limit;
		}
		
		return (result);
	}
	
	/**
	 *	Returns the index at which the given value occurs in the basis buffer 
	 *  of this BasisSet, as a lower bound on the basis value. If the given 
	 *  value does not occur, the index returned will be that of the least 
	 *  basis value that exceeds the given value. If no such basis value occurs, 
	 *  the result will be the capacity of this BasisSet (i.e., one more than the 
	 * 	last index of the basis buffer)
	 * 
	 *  @param value The absolute basis value to find in this BasisSet
	 *  @return The index at which the given value, or the least basis value that  
	 * 		exceeds the given value, occurs in the basis buffer of this BasisSet
	 */
	public int getIndexOfBasisValueAsLowerBound(double lowerBound)
	{
		int result = 0;
		
		int limit = fBasisBuffer.getSize();
		boolean done = false;
		
		for (int i = 0; (i < limit) && ! done; i++)
		{
			double value = fBasisBuffer.getAsDouble(i);
			
			if (value >= lowerBound)
			{
				done = true;
				
				result = i;
			}
		}
		
		if (! done)
		{
			result = limit;
		}
		
		return (result);
	}
	
	/**
	 *	Returns the index at which the given absolute basis amount, as an offset 
	 *  	from the asbolute value of the first basis value of this BasisSet, is 
	 *  	spanned; or if, the given absolute offset amount is not precisely spanned 
	 *  at any index, then the greatest index at which the given absolute offset 
	 *  amount is not exceeded. If the given amount exceeds the absolute basis 
	 *  amount spanned by the entire basis buffer, the result will be the size of 
	 *  this BasisSet (i.e., one greater than the last index of the basis buffer).
	 * 
	 *  @param amount The absolute basis amount, as an offset from the absolute 
	 * 		value of the first basis value of this BasisSet, to find
	 *  @return The index at which the given absolute basis amount, as an offset 
	 *  		from the asbolute value of the first basis value of this BasisSet, is 
	 *  		spanned; or if, the given absolute offset amount is not precisely 
	 * 		spanned at any index, then the greatest index at which the given 
	 * 		absolute offset amount is not exceeded
	 */
	public int getIndexOfBasisAmountAsUpperBound(double amount)
	{
		int result = 0;
		
		double absFirstBasisValue = Math.abs(fBasisBuffer.getAsDouble(0));
		
		int limit = fBasisBuffer.getSize();
		boolean done = false;
		
		for (int i = 0; (i < limit) && ! done; i++)
		{
			double absCurrentBasisValue = Math.abs(fBasisBuffer.getAsDouble(i));
			double absBasisDifference = 
				Math.abs(absCurrentBasisValue - absFirstBasisValue);
			
			if (absBasisDifference > amount)
			{
				done = true;
				
				result = i - 1;
			}
		}
		
		if (! done)
		{
			result = limit;
		}
			
		return (result);
	}
	
	
	/**
	 *	Returns the first index at which the given absolute basis amount, as an 
	 *  offset from the asbolute value of the first basis value of this BasisSet, 
	 *  is spanned or exceeded. If the given amount exceeds the absolute basis 
	 *  amount spanned by the entire basis buffer, the result will be the size of 
	 *  this BasisSet (i.e., one greater than the last index of the basis buffer).
	 * 
	 *  @param amount The absolute basis amount, as an offset from the absolute 
	 * 		value of the first basis value of this BasisSet, to find
	 *  @return The first index at which the given absolute basis amount, as an 
	 *  		offset from the asbolute value of the first basis value of this 
	 * 		BasisSet, is spanned or exceeded
	 */
	public int getIndexOfBasisAmountAsLowerBound(double amount)
	{
		int result = 0;
		
		double absFirstBasisValue = Math.abs(fBasisBuffer.getAsDouble(0));
		
		int limit = fBasisBuffer.getSize();
		boolean done = false;
		
		for (int i = 0; (i < limit) && ! done; i++)
		{
			double absCurrentBasisValue = Math.abs(fBasisBuffer.getAsDouble(i));
			double absBasisDifference = 
				Math.abs(absCurrentBasisValue - absFirstBasisValue);
			
			if (absBasisDifference >= amount)
			{
				done = true;
				
				result = i;
			}
		}
		
		if (! done)
		{
			result = limit;
		}
			
		return (result);
	}
	
	/**
	 *  Returns true if this BasisSet has been declared to be uniformly sampled, 
	 *  false otherwise.
	 *
	 *  @return True if this BasisSet has been declared to be uniformly sampled, 
	 *  		false otherwise
	 **/
	public boolean isUniformlySampled()
	{
		return (fSampleRate > 0);
	}
	
	/**
	 *  Sets the declared uniform sample rate of the data in this BasisSet to 
	 *  the given value, in terms of the units of its basis buffer. It is up to 
	 *  the caller to guarantee that the given rate is accurate, and that all 
	 *  basis values in this BasisSet are in fact uniformly spaced at the declared 
	 *  rate. To indicate that the BasisSet is not uniformly sampled (the default 
	 *  assumption, unless the BasisBundle from which the BasisSet was allocated 
	 *  was also declared to be uniformly sampled at allocation time), set the 
	 *  value to 0 or less, or to Double.Nan.
	 *
	 *  @param sampleRate The declared sample rate of the data in this BasisSet 
	 * 		in terms of the units of its basis buffer
	 **/
	public void setUniformSampleInterval(double sampleRate)
	{
		if (sampleRate <= 0)
		{
			fSampleRate = Double.NaN;
		}
		else
		{
			fSampleRate = sampleRate;
		}
	}
	
	/**
	 *  Returns the declared uniform sample rate of the data in this BasisSet, in 
	 *  terms of the units of its basis buffer. If this BasisSet is not uniformly 
	 *  sampled, the result will be Double.Nan.
	 *
	 *  @return The declared sample rate of the data in this BasisSet in terms of 
	 * 		the units of its basis buffer
	 **/
	public double getUniformSampleInterval()
	{
		return (fSampleRate);
	}
	


	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisSet#setUniformSampleIntervalImplicitly()
	 */
	public void setUniformSampleIntervalImplicitly()
	{
		if (fSetLength < 2)
		{
			throw new IllegalStateException("Not enough samples to calculate sampling rate");
		}
		setUniformSampleInterval(fBasisBuffer.getAsDouble(1) - fBasisBuffer.getAsDouble(0));
	}
	
	/**
	 *  Returns true if this BasisSet is read-only, false otherwise.
	 *
	 *  @return True if this BasisSet is read-only, false otherwise
	 **/
	public boolean isReadOnly()
	{
		return (fIsReadOnly);
	}

	/**
	 *  Makes all of the Buffers (Basis and Data) in the BasisSet 
	 *  read-only. 
	 **/
	public void makeReadOnly()
	{
		fBasisBuffer.makeReadOnly();
		
		int numBuffers = fDataBuffers.size();
		
		for (int i=0; i < numBuffers; i++)
		{
			((DataBuffer) fDataBuffers.get(i)).makeReadOnly();
		}

		fIsReadOnly = true;
	}

	/**
	 * Makes the contents of this BasisSet available on its BasisBundle of origin.
	 * 
	 * @throws UnsupportedOperationException if the BasisSet is read only
	 */
	public void makeAvailable() throws UnsupportedOperationException
	{
		if (!fIsReadOnly)
		{
			fBasisBundle.makeAvailable(this);
		}
		else
		{
			throw new UnsupportedOperationException(
				"Cannot make available on a read only basis set");
		}
	}
	
	/**
	 * Makes the given number of valid samples of this BasisSet available on its 
	 * BasisBundle of origin.
	 * 
	 * @param The number of valid samples contained in the BasisSet
	 * @throws UnsupportedOperationException if the BasisSet is read only
	 */
	public void makeAvailable(int numValidSamples) 
		throws UnsupportedOperationException
	{
		if (!fIsReadOnly)
		{
			fBasisBundle.makeAvailable(this, numValidSamples);
		}
		else
		{
			throw new UnsupportedOperationException(
				"Cannot make available on a read only basis set");
		}
	}
	
	/**
	 * Returns the BasisBundleDescriptor that describes the structure of this
	 * BasisSet.
	 * 
	 * @return The BasisBundleDescriptor that describes the structure of this
	 *         BasisSet
	 */
	public BasisBundleDescriptor getDescriptor()
	{
		return (fDescriptor);
	}

	/**
	 *  Returns the basis Buffer of this BasisSet.
	 *
	 *  @return The basis Buffer of this BasisSet
	 **/
	public DataBuffer getBasisBuffer()
	{
		return (fBasisBuffer);
	}

	/**
	 *  Returns the DataBufferDescriptor that describes the basis Buffer of 
	 *  this BasisSet.
	 *
	 *  @return The DataBufferDescriptor that describes the basis Buffer of 
	 *  		this BasisSet
	 **/
	public DataBufferDescriptor getBasisBufferDescriptor()
	{
		return (fDescriptor.getBasisBufferDescriptor());
	}

	/**
	 *  Returns the List of DataBufferDescriptors describing the DataBuffers 
	 *  contained in this BasisSet.
	 *
	 *  @return List Set of DataBufferDescriptors describing the DataBuffers 
	 *  		contained in this BasisSet
	 **/
	public Set getDataBufferDescriptors()
	{
		return (Collections.unmodifiableSet
			(fDescriptor.getDataBufferDescriptors()));
	}

//	/**
//	 *  Returns the Collection of DataBuffers contained in this BasisSet.
//	 *
//	 *  @return The Collection of DataBuffers contained in this BasisSet
//	 **/
//
//	public Collection getDataBuffers()
//	{
//		return (Collections.unmodifiableCollection
//			(fDataBuffersByName.values()));
//	}
//
//
//	/**
//	 *  Returns the Set of DataBuffers contained in this BasisSet as an 
//	 *  an Array of DataBuffers.
//	 *
//	 *  @return The Set of DataBuffers contained in this BasisSet
//	 **/
//
//	public DataBuffer[] getDataBuffersAsArray()
//	{
//		int numBuffers = fDataBuffersByName.size();
//		
//		DataBuffer[] buffersArray = new DataBuffer[numBuffers];
//		
//		Iterator buffers = fDataBuffersByName.values().iterator();
//		
//		for (int i = 0; buffers.hasNext(); i++)
//		{
//			DataBuffer buffer = (DataBuffer) buffers.next();
//			
//			buffersArray[i] = buffer;
//		}
//		
//		return (buffersArray);
//	}


	/**
	 *  Returns the Set of the names of the DataBuffers contained in this 
	 *  BasisSet. Note that if a DataBuffer has a Pixel, its name includes the 
	 *  Pixel tag.
	 *
	 *  @return The Set of the names of the DataBuffers contained in this 
	 * 		BasisSet
	 **/
	public Set getDataBufferNames()
	{
		return (Collections.unmodifiableSet(fDataBuffersByName.keySet()));
	}

	/**
	 *  Returns the Set of names of the DataBuffers of this BasisSet that match 
	 *  the given regular expression. Note that if a DataBuffer has a Pixel, the 
	 *  Pixel tag is part of its name.
	 *  
	 * @param A regular expression
	 *  @return The Set of names of the DataBuffers of this BasisSet that match 
	 *  		the given regular expression
	 */
	public Set getDataBufferNames(String regEx)
	{
		Set result = null;
			
		Map matchingBuffers = getDataBuffers(regEx);
		
		if (matchingBuffers != null)
		{
			Set names = matchingBuffers.keySet();
			
			result = new LinkedHashSet(names);
		}
		
		return (result);
	}
		
	/**
	 *  Returns the DataBuffer of this BasisSet that has the given name. 
	 *  Note that if the indicated DataBuffer is a Pixel, its name includes 
	 *  the Pixel tag. If no such DataBuffer is present, the result will be 
	 *  null.
	 *
	 *  @param name The name of a DataBuffer of this BasisSet 
	 *  @return The DataBuffer of this BasisSet that has the given name
	 **/
	public DataBuffer getDataBuffer(String name)
	{
		return ((DataBuffer) fDataBuffersByName.get(name));
	}

	/**
	 *  Returns the DataBuffer of this BasisSet that has the given (base) name and 
	 *  the given Pixel. If no such DataBuffer is present, the result will be 
	 *  null.
	 *
	 *  @param name The (base) name of a DataBuffer of this BasisSet 
	 *  @param pixel The Pixel of the indicated DataBuffer
	 *  @return The DataBuffer of this BasisSet that has the given (base) name and 
	 *  		Pixel
	 **/
	public DataBuffer getDataBuffer(String name, Pixel pixel)
	{
		DataBuffer result = null;
		
		if (pixel != null)
		{
			result = getDataBuffer(name + " " + pixel);
		}
		else
		{
			result = getDataBuffer(name);
		}
		
		return (result);
	}

	/**
	 *  Returns the DataBuffer of this BasisSet that has the given (base) name and 
	 *  the Pixel represented by the given array of indices. If no such DataBuffer 
	 *  is present, the result will be null.
	 *
	 *  @param name The (base) name of a DataBuffer of this BasisSet 
	 *  @param pixel An integer array representing the Pixel of the indicated 
	 *  		DataBuffer
	 *  @return The DataBuffer of this BasisSet that has the given (base) name and 
	 *  		Pixel
	 **/
	public DataBuffer getDataBuffer(String name, int[] pixel)
	{
		DataBuffer result = null;
		
		if (pixel != null)
		{
			result = getDataBuffer(name, new Pixel(pixel));
		}
		else
		{
			result = getDataBuffer(name);
		}
		
		return (result);
	}

	/**
	 *  Returns the ordered Map of the DataBuffers (by name) of this BasisSet whose 
	 *  names match the given regular expression.
	 *
	 *  @param regEx A regular expression 
	 *  @return The ordered Map of the DataBuffers (by name) of this BasisSet whose 
	 * 		names match the given regular expression
	 **/
	public Map getDataBuffers(String regEx)
	{
		Map result = null;
		
		if (fDataBuffersByName != null)
		{
			result = new LinkedHashMap();
			
			Iterator dataBuffers = fDataBuffersByName.entrySet().iterator();
			
			while (dataBuffers.hasNext())
			{
				Map.Entry entry = (Map.Entry) dataBuffers.next();
				
				String name = (String) entry.getKey();
				
				if (Pattern.matches(regEx, name))
				{
					result.put(name, entry.getValue());
				}
			}
		}
		
		return (result);
	}

	/**
	 *  Releases this ReleasableBasisSet from further use by the caller.
	 **/
	public void release()
	{
		if (fAllocation != null)
		{
			fAllocation.release();
		}
	}

	/**
	 *	Returns a String representation of the specified range of data in 
	 *  this BasisSet, starting with the BasisBuffer and across each 
	 *  DataBuffer.
	 * 
	 *  @param startPosition The start of the data range
	 *  @param length The length of the data range
	 *  @return A String representation of the specified range of data in 
	 *  		this BasisSet
	 */
	public String dataToString(int startPosition, int length)
	{
		StringBuffer stringRep = new StringBuffer("BasisSet data (pos. " + 
			startPosition + " for " + length + ")\n");
		
		stringRep.append("Basis buffer: " + fBasisBuffer.toString() + "\n");
		stringRep.append(fBasisBuffer.dataToString
			(startPosition, length) + "\n");
		
		Iterator dataBuffers = fDataBuffers.iterator();
		
		while (dataBuffers.hasNext())
		{
			DataBuffer buffer = (DataBuffer) dataBuffers.next();
			
			stringRep.append("Data buffer: " + buffer.toString() + "\n");
			stringRep.append(buffer.dataToString
				(startPosition, length) + "\n");
		}
		
		return (stringRep.toString());
	}	
	
	/**
	 *	Returns a String representation of the specified amount of data in 
	 *  this BasisSet, starting with the BasisBuffer and across each 
	 *  DataBuffer, and starting at the beginning of each Buffer.
	 * 
	 *  @param amount The amount of data
	 *  @return A String representation of the specified amount of data in 
	 *  		this BasisSet, starting with the BasisBuffer and across each 
	 *  		DataBuffer, and starting at the beginning of each Buffer
	 */
	public String dataToString(int amount)
	{
		return (dataToString(0, amount - 1));
	}
		
	/**
	 *	Returns a String representation of the data in this BasisSet, 
	 *  starting with the BasisBuffer and across each DataBuffer.
	 *
	 *  <p>Note that for a typical BasisSet this could produce a 
	 *  <em>very</em> long String!
	 * 
	 *  @return A String representation of the data in this BasisSet
	 */
	public String dataToString()
	{
		return (dataToString(fBasisBuffer.getSize()));
	}

	/**
	 *  Returns a String representation of this BasisSet.
	 * 
	 *  @return A String representation of this BasisSet
	 */
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer("BasisSet " + 
			super.toString());
		
		stringRep.append("\nBasisBundle: " + fBasisBundleId);
		
//		stringRep.append("\nBasisBundle start index: " + fBasisBundleStartIndex);
//		stringRep.append("\nBasisBundle end index: " + fBasisBundleEndIndex);
		stringRep.append("\nIs a copy: " + isCopy());
		
		stringRep.append("\nCapacity of buffers: " + fBasisBuffer.getSize());

		stringRep.append("\nIs read only: " + fIsReadOnly);
		
		stringRep.append("\nBasis buffer: " + 
			getBasisBufferDescriptor());
		
		if (isUniformlySampled())
		{
			stringRep.append("\nUniform sample rate: " + fSampleRate);
		}
		
		stringRep.append("\nFirst basis value: " + 
				getFirstBasisValue());
			
		stringRep.append("\nLast basis value: " + 
				getLastBasisValue());
			
		stringRep.append("\nData buffers:");
		
		Iterator dataBufferDescriptors = 
			getDataBufferDescriptors().iterator();
		
		for (int i = 0; dataBufferDescriptors.hasNext(); i++)
		{
			DataBufferDescriptor descriptor = 
				(DataBufferDescriptor) dataBufferDescriptors.next();
			
			stringRep.append("\n" + i + ": " + descriptor);
		}
		
		return (stringRep.toString());
	}
	
	/**
	 * @return Returns the allocation.
	 */
	public Allocation getAllocation()
	{
		return fAllocation;
	}
	
	/**
	 * @param allocation The allocation to set.
	 */
	public void setAllocation(Allocation allocation)
	{
		fAllocation = allocation;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisSet#hold()
	 */
	public void hold()
	{
		if (fAllocation != null)
		{
			fAllocation.hold();
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisSet#getDataBuffers()
	 */
	public Iterator getDataBuffers()
	{
		return fDataBuffers.iterator();
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisSet#getDataBuffer(int)
	 */
	public DataBuffer getDataBuffer(int index)
	{
		DataBuffer result = null;
		
		if (index >= 0 && index < fDataBuffers.size())
		{
			result = (DataBuffer) fDataBuffers.get(index);
		}
//		else
//		{
//			throw new IllegalArgumentException(
//				"Argument index must be greater than 0 and less" 
//				+ " than the number of DataBuffers in this BasisSet");
//		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisSet#getNumberOfDataBuffers()
	 */
	public int getNumberOfDataBuffers()
	{
		return fDataBuffers.size();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultBasisSet.java,v $
//  Revision 1.71  2006/08/10 16:13:22  smaher_cvs
//  Made fDataBuffersByName a LinkedHashMap so that getDataBuffers(regex) will return the data buffers "in order".
//
//  Revision 1.70  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.69  2006/06/02 19:20:04  smaher_cvs
//  Name change: UniformSampleRate->UniformSampleInterval
//
//  Revision 1.68  2006/05/31 13:05:21  smaher_cvs
//  Name change: UniformSampleRate->UniformSampleInterval
//
//  Revision 1.67  2006/05/23 15:59:02  smaher_cvs
//  Added ability to specify data buffer names using regular expressions.
//
//  Revision 1.66  2006/05/17 13:28:04  smaher_cvs
//  Fixed so that filtered basis requests propagate through the descriptors and content of basis sets.
//
//  Revision 1.65  2006/05/12 18:09:30  smaher_cvs
//  Added setUniformSampleRate().
//
//  Revision 1.64  2006/02/07 15:27:44  chostetter_cvs
//  Fixed Javadoc
//
//  Revision 1.63  2006/02/07 15:26:37  chostetter_cvs
//  Added clear() method
//
//  Revision 1.62  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.61  2005/12/05 04:13:50  tames
//  Added support for a BasisBuffer base to handle conditions where a double
//  does not have enough percision to represent a BasisBuffer value.
//
//  Revision 1.60  2005/11/15 20:56:31  chostetter_cvs
//  Fixed failure to preserve the BasisBuffer in filtering operations
//
//  Revision 1.59  2005/09/13 22:28:58  tames
//  Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.58  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.57  2005/08/26 22:13:30  tames_cvs
//  Changes that are an incomplete refactoring. Also added initial support for history.
//
//  Revision 1.56  2005/08/26 02:59:46  tames
//  Documentation change only.
//
//  Revision 1.55  2005/07/21 15:27:12  tames_cvs
//  Removed the getSizeAsByte method since the definition was
//  somewhat ambiguous and not valid for ObjectDataBuffers.
//
//  Revision 1.54  2005/07/20 19:56:25  tames_cvs
//  Fixed bug in the downsample method that resulted in BasisSets returning
//  the incorrect size.
//
//  Revision 1.53  2005/07/18 21:12:03  tames_cvs
//  Changed getSizeInBytes method to include the size of the basis buffer
//  in the returned size.
//
//  Revision 1.52  2005/07/15 22:03:48  tames
//  Added getSizeInBytes() method to support archivers and other users that
//  that need to know the physical size of the underlying data.
//
//  Revision 1.51  2005/07/15 19:22:46  chostetter_cvs
//  Organized imports
//
//  Revision 1.50  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.49  2005/04/27 15:18:03  chostetter_cvs
//  Added methods to get DataBuffer by base name + Pixel
//
//  Revision 1.48  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.47  2005/03/24 21:54:28  chostetter_cvs
//  Fixed problem with shrinking BasisSets having fewer valid samples than requested, and with calculating spanned Basis values for BasisSets that decrease monotonically (rather than increase).
//
//  Revision 1.46  2005/03/23 21:05:13  chostetter_cvs
//  Fixed error with marking concatenated copied BasisSets as copies
//
//  Revision 1.45  2005/03/23 00:09:06  chostetter_cvs
//  Tweaks to BasisSet allocation, release
//
//  Revision 1.44  2005/03/22 22:47:06  chostetter_cvs
//  Refactoring of BasisSet allocation, release
//
//  Revision 1.43  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.42  2005/02/02 21:17:27  chostetter_cvs
//  DataSets (and their BasisSets) are now duplicated (in the java.nio sense) when sent to more than one DataListener
//
//  Revision 1.41  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
//  Revision 1.40  2005/01/28 23:57:34  chostetter_cvs
//  Added ability to integrate DataBuffers and requests, renamed average value
//
//  Revision 1.39  2005/01/26 20:46:05  chostetter_cvs
//  Added method to clear the contents of a BasisSet.
//
//  Revision 1.38  2004/11/30 16:53:36  chostetter_cvs
//  Added ability to obtain Set of names that match a given regular expression
//
//  Revision 1.37  2004/11/30 15:50:03  chostetter_cvs
//  Added ability to select a set of DataBuffers by matching names to a given regular expression
//
//  Revision 1.36  2004/09/15 20:06:17  chostetter_cvs
//  Fixed issues with freeing of java.nio duplicate memory block freeing
//
//  Revision 1.35  2004/09/13 20:46:02  chostetter_cvs
//  More buffer-name related fixes
//
//  Revision 1.34  2004/09/13 19:05:05  chostetter_cvs
//  Fixed name test for filterIn, filterOut
//
//  Revision 1.33  2004/09/13 17:34:15  chostetter_cvs
//  DetectorParser now working (except for Units)
//
//  Revision 1.32  2004/09/10 14:49:03  chostetter_cvs
//  More data description work
//
//  Revision 1.31  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.30  2004/08/19 18:05:39  chostetter_cvs
//  Tweaks addressing copied new data
//
//  Revision 1.29  2004/07/29 16:55:19  chostetter_cvs
//  Doc tweaks
//
//  Revision 1.28  2004/07/28 19:11:25  chostetter_cvs
//  BasisBundle now alerts of impending discontinuity, BasisRequester copies and releases pending data at discontinuity
//
//  Revision 1.27  2004/07/21 14:26:15  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.26  2004/07/20 14:53:00  chostetter_cvs
//  Made BasisSets stative with regard to being released
//
//  Revision 1.25  2004/07/20 02:37:35  chostetter_cvs
//  More real-valued boundary condition work
//
//  Revision 1.23  2004/07/19 19:00:38  chostetter_cvs
//  Implemented requests by basis amount
//
//  Revision 1.22  2004/07/19 14:16:14  chostetter_cvs
//  Added ability to subsample data in requests
//
//  Revision 1.21  2004/07/18 05:58:22  chostetter_cvs
//  More tweaks
//
//  Revision 1.20  2004/07/18 05:51:37  chostetter_cvs
//  Tweaks
//
//  Revision 1.19  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.18  2004/07/16 21:35:24  chostetter_cvs
//  Work on declaring uniform sample rate of data
//
//  Revision 1.17  2004/07/15 05:44:38  chostetter_cvs
//  Mods to determining new BasisSet structure
//
//  Revision 1.16  2004/07/14 22:24:53  chostetter_cvs
//  More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//
//  Revision 1.15  2004/07/14 00:33:49  chostetter_cvs
//  More Algorithm, data testing. Fixed slice bug.
//
//  Revision 1.14  2004/07/13 18:52:50  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.13  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.12  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.11  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.10  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.9  2004/07/09 22:29:11  chostetter_cvs
//  Extensive testing of Input/Output interaction, supports simple BasisRequests
//
//  Revision 1.8  2004/07/06 21:57:12  chostetter_cvs
//  More BasisRequester, DataRequester work
//
//  Revision 1.7  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.6  2004/07/06 11:55:26  chostetter_cvs
//  Check-in after weekend
//
//  Revision 1.5  2004/07/02 15:10:05  chostetter_cvs
//  Further CompositeBasisSet work
//
//  Revision 1.4  2004/07/02 02:33:30  chostetter_cvs
//  Renamed DataRequest to BasisRequest
//
//  Revision 1.3  2004/07/01 23:41:03  chostetter_cvs
//  MemoryModel work
//
//  Revision 1.2  2004/06/30 21:02:53  chostetter_cvs
//  Made constructors protected
//
//  Revision 1.1  2004/06/30 20:56:20  chostetter_cvs
//  BasisSet is now an interface
//
//  Revision 1.13  2004/06/29 22:46:13  chostetter_cvs
//  Fixed broken CVS-generated comments. Grrr.
//
//  Revision 1.12  2004/06/29 22:39:39  chostetter_cvs
//  Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//
//  Revision 1.11  2004/06/15 22:21:12  chostetter_cvs
//  More DataSetRequester work
//
//  Revision 1.10  2004/06/09 03:28:49  chostetter_cvs
//  Output-related modifications
//
//  Revision 1.9  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.8  2004/06/04 23:10:27  chostetter_cvs
//  Added data printing support to various Buffer classes
//
//  Revision 1.7  2004/06/04 17:28:32  chostetter_cvs
//  More data tweaks. Ready for testing.
//
//  Revision 1.6  2004/06/02 23:59:41  chostetter_cvs
//  More Namespace, DataSpace tweaks, created alogirthms package
//
//  Revision 1.5  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.4  2004/05/27 23:09:26  chostetter_cvs
//  More Namespace related changes
//
//  Revision 1.3  2004/05/20 21:28:10  chostetter_cvs
//  Checking in for the weekend
//
//  Revision 1.2  2004/05/16 15:44:36  chostetter_cvs
//  Further data-handling definition
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
