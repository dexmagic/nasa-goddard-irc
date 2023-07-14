//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/CompositeAllocation.java,v 1.4 2005/10/19 15:06:55 chostetter_cvs Exp $
//
// This code was developed by NASA Goddard Space Flight Center, Code 588 for the 
// Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// *  Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
// *  The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
// *  Altered versions of this software must be plainly marked as such.
// *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.system.memory;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A CompositeAllocation is a concrete Allocation that can represent a root
 * allocation from a MemoryModel or an Allocation linked to a set of other
 * Allocations in a parent/child association. As a root Allocation this class
 * will release itself from the MemoryModel when there are no more "holders" of
 * the Allocation. As a child Allocation this class will release its hold on one
 * or more parent Allocations that it is linked to. 
 * <p>
 * The CompositeAllocation can maintain a link to many parent allocations. As 
 * allocations are appended and sliced the effect is a sliding window of 
 * allocated space from a MemoryModel.
 * <p>
 * This code was developed for
 * NASA, Goddard Space Flight Center, Code 588 for the Instrument Remote Control
 * (IRC) project.
 * 
 * @version $Date: 2005/10/19 15:06:55 $
 * @author Troy Ames
 */
public class CompositeAllocation extends AbstractAllocation
{
	private LinkedList fAllocations = new LinkedList();
	private int fAllocationStart = 0;
	
	/**
	 * Constructs a new Allocation spanning the specified range of
	 * positions in a memory model, and having an external reference count of 1.
	 * 
	 * @param model The MemoryModel of the new Allocation
	 * @param start The start of the new Allocation within the memory model
	 * @param size The size of the new Allocation within the memory model
	 */	
	public CompositeAllocation(MemoryModel model, int start, int size)
	{
		super(model, size);
		
		fStart = start;
		fAllocationStart = start;
	}	
		
	/**
	 * Constructs a new Allocation spanning the specified range of positions 
	 * in a memory model linked to the specified parent allocations,
	 * and having an external reference count of 1. The parent allocations are
	 * added in the order they are returned by the specified collection's 
	 * iterator and their reference count incremented. The order of the parents
	 * Collection must be in the same order they represent in the MemoryModel 
	 * and contiguous with the oldest allocation first and the most recent last.
	 * 
	 * @param parents The parent Allocations to this new Allocation.
	 * @param start The start of the new Allocation within the memory model
	 * @param size The size of the new Allocation within the memory model
	 */	
	public CompositeAllocation(Collection parents, int start, int size)
	{
		super(size);
		
		fStart = start;
		
		if(parents != null)
		{
			for (Iterator allocations = parents.iterator(); allocations.hasNext();)
			{
				Allocation allocation = (Allocation) allocations.next();
				allocation.hold();
				fAllocations.add(allocation);
			}
		}
		
		Allocation firstAllocation = (Allocation) fAllocations.getFirst();
		
		if (firstAllocation != null)
		{
			fAllocationStart = firstAllocation.getStart();
		}
		else
		{
			fAllocationStart = start;
		}
	}
	
	/**
	 * Constructs a new Allocation linked to a list of parent
	 * Allocations spanning the specified range of positions in a memory model,
	 * and having an external reference count of 1.
	 * 
	 * @param parent The parent Allocation to this new Allocation.
	 * @param start The start of the new Allocation within the memory model
	 * @param size The size of the new Allocation within the memory model
	 */	
	public CompositeAllocation(Allocation parent, int start, int size)
	{
		super(size);
		
		fStart = start;
		
		if(parent != null)
		{
			fAllocationStart = parent.getStart();
			parent.hold();
			fAllocations.add(parent);
		}
		else
		{
			fAllocationStart = start;
		}
	}
	
	/**
	 *  Decrements the current reference count of this Allocation. If this
	 *  Allocation has any parents and the reference count is zero as a result
	 *  of this call the parents <code>release</code> methods will be called.
	 * 
	 *  @return The decremented reference count of this Allocation
	 */
	protected synchronized int decrementReferenceCount()
	{
		int result = super.decrementReferenceCount();
		
		if (result <= 0 && fAllocations.size() > 0)
		{
			Object [] allocations = fAllocations.toArray();
			
			for (int i = 0; i < allocations.length; i++)
			{
				((Allocation) allocations[i]).release();
			}
		}
		
		return (result);
	}

	/**
	 * Returns a new Allocation that represents a subrange of this Allocation.
	 * As a result of this call the original Allocation's reference count is
	 * incremented so the <code>release</code> method should be called if it
	 * is no longer needed.
	 * 
	 * @param index The index of the slice relative to the start of this
	 *            Allocation
	 * @param length the length of the new slice.
	 * @return The new Allocation that covers a subrange of this Allocation.
	 * @throws IllegalArgumentException if the parameters are negative or do not
	 *             specifiy a valid subrange of this Allocation.
	 */
	public synchronized Allocation slice(int index, int length)
	{
		AbstractAllocation remainder = null;
		
		if ((index >= 0) && (length >= 0) && (index + length <= fSize))
		{
			// Make a copy of the parents and remove any not within the
			// specified range.
			LinkedList parents = (LinkedList) fAllocations.clone();
			
			if (parents.size() == 0)
			{
				// This allocation is the root parent so add it.
				parents.add(this);
			}
			else
			{
				// Remove any allocations not in the slice.
				filterRegionAllocations(parents, index, length);
			}
			
			// Pass the parent allocations of this to the new allocation.
			remainder = new CompositeAllocation
				(parents, resolveIndex(this.fStart + index), length);	
		}
		else
		{
			String message = "The given split index (" + index + ", " + length + 
				") is illegal for " + this;
			
			throw (new IllegalArgumentException(message));
		}
		
		return (remainder);
	}
	
	/**
	 * Removes the unnecessary allocations from the parents list that are not
	 * in the specified region.
	 * 
	 * @param parents the parent allocations
	 * @param start the start position of interest
	 * @param length the length of the region
	 */
	private final void filterRegionAllocations(
			LinkedList parents, int start, int length)
	{
		int relativeStartOffset = start;

		// Calculate the start offset relative to the first allocation so
		// that we can simply use the length of allocations to determine if
		// they should be included in the slice region.
		if (fAllocationStart <= fStart)
		{
			relativeStartOffset += fStart - fAllocationStart;
		}
		else
		{
			// Allocation wraps around the end of the memory model
			int capacity = this.getMemoryModel().getSize();
			relativeStartOffset += (capacity - fAllocationStart) + fStart;
		}
		
		int relativeEndOffset = length + relativeStartOffset;
		
		//int modelOffset = resolveIndex(this.fStart + index);
		Iterator allocations = parents.iterator();
		
		while (allocations.hasNext())
		{
			Allocation allocation = (Allocation) allocations.next();
			int allocationSize = allocation.getSize();
			
			// Update relative start position in the allocation
			relativeStartOffset -= allocationSize;
			
			// Remove the allocation if it is not in the slice region
			if (relativeStartOffset >= 0 || relativeEndOffset <= 0)
			{
				allocations.remove();
			}

			// Update relative end position in the allocation
			relativeEndOffset -= allocationSize;
		}
	}
	
	/**
	 * Returns a new Allocation that represents appending the given Allocation
	 * to the end of this Allocation. The two source Allocations will have their
	 * reference counts incremented as a result so if the original Allocations
	 * are no longer needed they should be released.
	 * 
	 * @param allocation The source Allocation to append.
	 * @return The new Allocation that covers a combined range of the source
	 *         Allocations.
	 */
	public synchronized Allocation append(Allocation allocation)
	{
		AbstractAllocation result = null;
		
		// Make a copy of the parents 
		LinkedList parents = (LinkedList) fAllocations.clone();
		if (parents.size() == 0)
		{
			// This allocation is the root parent so add it.
			parents.add(this);
		}

		// Append the specified allocation.
		parents.add(allocation);
		
		result = new CompositeAllocation
			(parents, this.fStart, this.fSize + allocation.getSize());	
		
		return (result);
	}
	
	/**
	 * Returns a new Allocation that represents appending the specifed
	 * allocation followed by a slice. The result is the same as calling the
	 * <code>append</code> method and then the <code>slice</code> method
	 * with the advantage that the extra allocation is avoided. The two source Allocations will
	 * have their reference counts incremented as a result so if the original
	 * Allocations are no longer needed they should be released.
	 * 
	 * @param allocation The source Allocation to append.
	 * @param index The index of the slice relative to the start of this
	 *            Allocation
	 * @param length the length of the new slice.
	 * @return The new Allocation that covers a subrange of this Allocation.
	 * @throws IllegalArgumentException if the parameters are negative or do not
	 *             specifiy a valid subrange of this Allocation.
	 */
	public synchronized Allocation appendAndSlice(
			Allocation allocation, int index, int length)
	{
		AbstractAllocation remainder = null;
		int newSize = fSize + allocation.getSize();
		
		if ((index >= 0) && (length >= 0) && (index + length <= newSize))
		{
			// Make a copy of the parents and remove any not within the
			// specified range.
			LinkedList parentsClone = (LinkedList) fAllocations.clone();
			if (parentsClone.size() == 0)
			{
				// This allocation is the root parent so add it.
				parentsClone.add(this);
			}

			parentsClone.add(allocation);
			
			// Remove any allocations not in the slice.
			filterRegionAllocations(parentsClone, index, length);
			
			// Pass the parent allocations of this to the new allocation.
			remainder = new CompositeAllocation
				(parentsClone, resolveIndex(this.fStart + index), length);	
		}
		else
		{
			String message = "The given split index (" + index + ", " + length + 
				") is illegal for " + this;
			
			throw (new IllegalArgumentException(message));
		}
		
		return (remainder);
	}
	
	/**
	 * Returns the MemoryModel of this Allocation.
	 * 
	 * @return The MemoryModel of this Allocation or null if not defined.
	 */
	public MemoryModel getMemoryModel()
	{
		MemoryModel result = null;
		
		if (fAllocations.size() > 0)
		{
			result = ((Allocation) fAllocations.getFirst()).getMemoryModel();
		}
		else
		{
			result = super.getMemoryModel();
		}
		
		return result;
	}

	/**
	 * Returns the absolute index in the MemoryModel of the given 
	 * relative index in the Allocation.
	 * 
	 * @return The absolute MemoryModel index of the relative allocation index.
	 */
	public int resolveIndex(int index)
	{
		int result = index;
		
		if (fAllocations.size() > 0)
		{
			result = ((Allocation) fAllocations.getFirst()).resolveIndex(index);
		}
		else 
		{
			result = super.resolveIndex(index);
		}
		
		return (result);
	}

	/**
	 *  Returns a String representation of this Allocation.
	 * 
	 *  @return A String representation of this Allocation
	 */
	public synchronized String toString()
	{
		StringBuffer message = new StringBuffer(
				"[Start: " + fStart 
				+ " Size: " + fSize 
				+ " Refs: " + getReferenceCount() 
				+ " parents:");
		
		for (Iterator allocations = fAllocations.iterator(); allocations.hasNext();)
		{
			Allocation allocation = (Allocation) allocations.next();
			message.append(allocation.toString());
		}
		
		message.append("]");
		
		return message.toString();
	}
	
//	Map fmap = new HashMap();
//	/**
//	 * Holds the Allocation from being reclaimed by incrementing the current 
//	 * reference count of this Allocation.
//	 * 
//	 * @return The incremented reference count of this Allocation
//	 */
//	public synchronized int hold()
//	{
//		int result = super.hold();
//		Integer count = (Integer) fmap.get(Thread.currentThread());
//		if (count != null)
//		{
//			new Integer(count.intValue()+1);
//		}
//		else
//		{
//			count = new Integer(1);
//		}
//		fmap.put(Thread.currentThread(), count);
//		System.out.println("Hold " + Integer.toHexString(hashCode()) + " : " + this.getReferenceCount() + " pair:" + count + " from: " + Thread.currentThread());
//		//(new Exception()).fillInStackTrace().printStackTrace();
//		return (result);
//	}
//
//	/**
//	 * Releases this Allocation by decrements the reference count of this
//	 * Allocation. If the resulting reference count is 0 then the Allocation can
//	 * be reclaimed. This implementation calls the
//	 * {@link #decrementReferenceCount() decrementReferenceCount} method.
//	 * 
//	 * @return The decremented reference count of this Allocation
//	 */
//	public synchronized int release()
//	{
//		int result = super.release();
//		Integer count = (Integer) fmap.get(Thread.currentThread());
//		if (count != null)
//		{
//			new Integer(count.intValue()-1);
//		}
//		else
//		{
//			count = new Integer(-1);
//		}
//		fmap.put(Thread.currentThread(), count);
//		System.out.println("Release " + Integer.toHexString(hashCode()) + " : " + this.getReferenceCount() + " pair:" + count + " from: " + Thread.currentThread());
//		if (this.getReferenceCount() <= 0 )
//		{
//			System.out.println("Dealloc " + Integer.toHexString(hashCode()) + " from: " + Thread.currentThread());
//			//(new Exception()).fillInStackTrace().printStackTrace();
//		}
//		return result;
//	}

}

//--- Development History  ---------------------------------------------------
//
// $Log: CompositeAllocation.java,v $
// Revision 1.4  2005/10/19 15:06:55  chostetter_cvs
// Organized imports
//
// Revision 1.3  2005/09/22 18:36:38  tames
// Fixed the case where the allocations wrap around the end of the
// Memory Model.
//
// Revision 1.2  2005/09/09 21:31:18  tames
// Full implementation.
//
// Revision 1.1  2005/08/26 22:05:03  tames_cvs
// Partial initial implementation.
//
//
