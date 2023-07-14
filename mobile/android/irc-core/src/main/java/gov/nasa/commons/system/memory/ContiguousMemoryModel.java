//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/ContiguousMemoryModel.java,v 1.5 2006/01/23 17:59:55 chostetter_cvs Exp $
//
// This code was developed by NASA Goddard Space Flight Center, 
// Code 588 for the Instrument Remote Control (IRC) project.
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

import gov.nasa.gsfc.commons.types.queues.FifoQueue;


/**
 * A ContiguousMemoryModel is a one-dimensional MemoryModel representing a
 * circular, continuous, and limited range of memory, individual positions
 * within which can be located by index. This Memory model restricts all
 * allocated memory ranges to be contiguous, however they can wrap around the
 * end of the buffer to the beginning (circular). This class uses
 * {@link gov.nasa.gsfc.commons.system.memory.DefaultLinkedAllocation DefaultLinkedAllocation}
 * for Allocation objects.
 * <p>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/23 17:59:55 $
 * @author Troy Ames
 */
public class ContiguousMemoryModel extends AbstractMemoryModel 
	implements MemoryModel
{
	private int fFreeStartIndex = 0;
	private int fAvailable = 0;
	private FifoQueue fAllocations = new FifoQueue();

	/**
	 * Contructs a new ContiguousMemoryModel of the given size. The indices of
	 * the positions in the new MemoryModel will begin at 0.
	 * 
	 * @param size The size of the new MemoryModel
	 * @throws IllegalArgumentException if the given size is negative or zero
	 */
	public ContiguousMemoryModel(int size)
	{
		super(size);

		fAvailable = size;
	}

	/**
	 * Attempts to allocate the given amount of memory from within this
	 * MemoryModel. The Allocation will be returned if there is enough free
	 * space in the MemoryModel, null otherwise.
	 * 
	 * @param amount The desired amount to allocate
	 * @return The allocation or null if unsuccessful
	 */	
	public synchronized Allocation allocate(int amount)
	{
		Allocation result = null;
		
		if (fAvailable >= amount && amount > 0)
		{
			int start = fFreeStartIndex;
			
			result = new DefaultLinkedAllocation(this, start, amount);	
			fAvailable -= amount;
			fFreeStartIndex = resolveIndex(start + amount);
			fAllocations.add(result);
		}
		
		return (result);
	}
	
	/**
	 * Returns the current amount of available memory in this MemoryModel.
	 * 
	 * @return The current amount of available memory in this MemoryModel
	 */
	public int getAmountAvailable()
	{
		return fAvailable;
	}

	/**
	 *  Returns true if this MemoryModel is currently able to allocate the given 
	 *  amount, false otherwise.
	 *  
	 *  @param amount The allocation amount requested
	 *  @return True if this MemoryModel is currently able to allocate the given 
	 *  		amount, false otherwise
	 */
	public final boolean isAvailable(int amount)
	{
		boolean result = false;
		
		if (isRationalAllocationAmount(amount))
		{
			if (amount <= getAmountAvailable())
			{
				result = true;
			}
		}
		
		return (result);
	}
	
	/**
	 * Immediately releases the given Allocation from this MemoryModel
	 * (regardless of the value of its reference count).
	 * <p>
	 * The operation results in a ReleasedEvent being sent to all current
	 * MemoryModelListeners.
	 * </p>
	 * 
	 * @param allocation An Allocation from this MemoryModel
	 * @throws IllegalArgumentException if the given Allocation was not made on
	 *             this MemoryModel
	 */
	public synchronized void release(Allocation allocation)
	{
		if ((allocation != null) && (allocation.getMemoryModel() == this))
		{
			((AbstractAllocation) allocation).setReferenceCount(0);
			Allocation oldestAllocation = (Allocation) fAllocations.get();
			
			// Check if allocation is contiguous with the current free space
			if (allocation == oldestAllocation)
			{				
				// Since this allocation is first on the Queue it must be 
				// contiguous so update memory state. Also check for any
				// previously released but noncontiguous allocations to 
				// determine if they are now contiguous.
				
				while (oldestAllocation != null 
						&& oldestAllocation.getReferenceCount() == 0)
				{
					fAllocations.remove();
					
					// This is contiguous so add it on
					int size = oldestAllocation.getSize();
					fAvailable += size;
					
					oldestAllocation = (Allocation) fAllocations.get();
				}
				
				notifyAll();
			}
			else if (allocation == (Allocation) fAllocations.getLast())
			{
				// This allocation was the most recently allocated so we 
				// can maintain contiguous memory if we reuse the memory
				// represented by this allocation
				fAllocations.removeLast();
				
				// Add it back on to the front of the free space to be
				// reused by the next allocation request.
				fFreeStartIndex = allocation.getStart();
				fAvailable += allocation.getSize();
				
			}
			// Else this allocation is not contiguous meaning there are
			// allocations in front and behind it in the memory model so wait 
			// until allocations in front of it are released to reclaim.
		}
		else
		{
			String message = 
				"The given allocation is not from this MemoryModel:\n"
				+ allocation;

			throw (new IllegalArgumentException(message));
		}
	}
	
	/**
	 *  Returns a String representation of this MemoryModel.
	 * 
	 *  @return A String representation of this MemoryModel
	 */
	public final String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString());	
		
		stringRep.append("\nNext allocation position: " + fFreeStartIndex);
		stringRep.append("\nSpace available: " + fAvailable);
		
		return (stringRep.toString());
	}

    /**
	 * Checks and resolves the given index relative to this memory model.
	 * 
	 * @returns an absolute index into the memory model.
	 */
	public final int resolveIndex(int index)
	{
		int result = index;
		int size = fSize;
		
		if (result >= size)
		{
			result -= size;
		}
		
		return (result);
	}	
}

//--- Development History  ---------------------------------------------------
//
// $Log: ContiguousMemoryModel.java,v $
// Revision 1.5  2006/01/23 17:59:55  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.4  2005/11/14 19:52:59  chostetter_cvs
// Organized imports
//
// Revision 1.3  2005/10/28 18:41:27  tames
// Added the capability to reuse the most recently allocated memory when it is
// released. This prevents noncontiguous memory when the writer calls
// makeAvailable with zero samples.
//
// Revision 1.2  2005/07/15 13:26:31  tames
// Removed synchronization of the resolveIndex method.
//
// Revision 1.1  2005/07/12 17:05:47  tames
// Refactored memory package to improve performance and to
// further encapsulate the MemoryModel from users.
//
//
