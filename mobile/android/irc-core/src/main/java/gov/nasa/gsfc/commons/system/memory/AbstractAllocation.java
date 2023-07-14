//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/AbstractAllocation.java,v 1.2 2005/09/22 15:37:06 tames Exp $
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


/**
 * An AbstractAllocation represents an allocation from within a MemoryModel. An
 * Allocation has a size, a start position, a reference count that can be
 * incremented and decremented, and an associated MemoryModel. Allocations can
 * also be compared, here by their relative positions in a MemoryModel.
 * <p>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/09/22 15:37:06 $
 * @author Carl F. Hostetter
 * @author Troy Ames
 */

public abstract class AbstractAllocation implements Comparable, Allocation
{
	private MemoryModel fMemoryModel;
	int fSize = 0;
	int fStart;
	private int fReferenceCount = 1;
	
	/**
	 * Constructs a new Allocation of the given size. The reference count of the
	 * new Allocation is set to 1.
	 * 
	 * @param size The size of the new Allocation
	 */
	public AbstractAllocation(int size)
	{
		fSize = size;
	}

	/**
	 * Constructs a new Allocation of the given size from the given MemoryModel.
	 * The reference count of the new Allocation is set to 1.
	 * 
	 * @param model The MemoryModel of the new Allocation
	 * @param size The size of the new Allocation
	 */
	public AbstractAllocation(MemoryModel model, int size)
	{
		fMemoryModel = model;
		fSize = size;
	}

	/**
	 * Returns a new Allocation that represents appending the given Allocation
	 * to the end of this Allocation. The two source Allocations may have their
	 * reference counts incremented as a result so if the original Allocations
	 * are no longer needed they should be released.
	 * 
	 * @param allocation The source Allocation to append.
	 * @return The new Allocation that covers a combined range of the source
	 *         Allocations.
	 */
	public abstract Allocation append(Allocation allocation);

	/**
	 * Returns the MemoryModel of this Allocation.
	 * 
	 * @return The MemoryModel of this Allocation or null if not defined.
	 */
	public MemoryModel getMemoryModel()
	{
		return (fMemoryModel);
	}

	/**
	 * Returns the size of this Allocation.
	 * 
	 * @return The size of this Allocation
	 */
	public synchronized int getSize()
	{
		return (fSize);
	}

	/**
	 * Returns the start of this Allocation within its MemoryModel.
	 * 
	 * @return The start of this Allocation within its MemoryModel
	 */
	public synchronized int getStart()
	{
		return (fStart);
	}

	/**
	 * Sets the reference count of this Allocation to the given value.
	 * 
	 * @param count The new reference count
	 */
	protected synchronized void setReferenceCount(int count)
	{
		fReferenceCount = count;
	}

	/**
	 * Returns the current reference count of this Allocation. The reference
	 * count is the number of users of the Allocation. The reference count is 
	 * incremented by the <code>hold</code> method and decremented by the 
	 * <code>release</code> method.
	 * 
	 * @return The current reference count of this Allocation
	 */
	public synchronized int getReferenceCount()
	{
		return (fReferenceCount);
	}

	/**
	 * Returns true if the end of this Allocation immediately precedes the start
	 * of the given Allocation in the same MemoryModel, false otherwise.
	 * 
	 * @param second The Allocation to test for contiguity with this Allocation.
	 * @return True if the end of this Allocation immediately precedes the start
	 *         of the given Allocation in the same MemoryModel, false otherwise.
	 */
	public boolean isContiguousWith(Allocation second)
	{
		boolean result = false;

		if (getMemoryModel() == second.getMemoryModel())
		{
			if (resolveIndex(fStart + fSize) == second.getStart())
			{
				result = true;
			}
		}

		return result;
	}

	/**
	 * Holds the Allocation from being reclaimed by incrementing the current 
	 * reference count of this Allocation.
	 * 
	 * @return The incremented reference count of this Allocation
	 */
	public synchronized int hold()
	{
		return (++fReferenceCount);
	}

	/**
	 * Releases this Allocation by decrements the reference count of this
	 * Allocation. If the resulting reference count is 0 then the Allocation can
	 * be reclaimed. This implementation calls the
	 * {@link #decrementReferenceCount() decrementReferenceCount} method.
	 * 
	 * @return The decremented reference count of this Allocation
	 */
	public synchronized int release()
	{
		return decrementReferenceCount();
	}

	/**
	 * Returns the absolute index in the MemoryModel of the given 
	 * relative index in the Allocation. This implementation calls the 
	 * <code>resolveIndex</code> method of the MemoryModel if defined.
	 * 
	 * @return The absolute MemoryModel index of the relative allocation index.
	 */
	public int resolveIndex(int index)
	{
		int result = index;

		if (fMemoryModel != null)
		{
			result = fMemoryModel.resolveIndex(index);
		}

		return (result);
	}

	/**
	 * Returns a new Allocation that represents a subrange of this Allocation.
	 * This Allocation is not changed. The <code>release</code> method
	 * should be called on the original Allocation if it is no longer needed.
	 * 
	 * @param index The index of the slice relative to the start of this
	 *            Allocation
	 * @param length the length of the new slice.
	 * @return The new Allocation that covers a subrange of this Allocation.
	 * @throws IllegalArgumentException if the parameters are negative or do not
	 *             specifiy a valid subrange of this Allocation.
	 */
	public abstract Allocation slice(int index, int length);

	/**
	 * Decrements the current reference count of this Allocation and if the
	 * result is 0 the {@link MemoryModel#release(Allocation)} method is called
	 * with this Allocation as the argument.
	 * 
	 * @return The decremented reference count of this Allocation
	 */
	protected synchronized int decrementReferenceCount()
	{
		--fReferenceCount;

		if (fReferenceCount <= 0 && fMemoryModel != null)
		{
			fMemoryModel.release(this);
		}

		return (fReferenceCount);
	}

	/**
	 * Returns 0 if the start of this Allocation equals the start of the given
	 * Allocation, -1 if it is less, 1 if it is greater.
	 * 
	 * @param block An Allocation
	 * @return 0 if this start of this Allocation equals the given Allocation,
	 *         -1 if it is less, 1 if it is greater
	 * @throws ClassCastException if the given Object is not an Allocation
	 */
	public synchronized int compareTo(Object block) throws ClassCastException
	{
		int result = 0;

		int comparand = ((Allocation) block).getStart();

		if (fStart > comparand)
		{
			result = 1;
		}
		else if (fStart < comparand)
		{
			result = -1;
		}

		return (result);
	}

	/**
	 * Returns a String representation of this Allocation.
	 * 
	 * @return A String representation of this Allocation
	 */
	public synchronized String toString()
	{
		return ("Allocation of size = " + fSize + " from\n" + fMemoryModel);
	}
}

// --- Development History ---------------------------------------------------
//
// $Log: AbstractAllocation.java,v $
// Revision 1.2  2005/09/22 15:37:06  tames
// Synchronized release method.
//
// Revision 1.1  2005/07/12 17:05:47  tames
// Refactored memory package to improve performance and to
// further encapsulate the MemoryModel from users.
//
// Revision 1.1  2005/04/04 15:40:58  chostetter_cvs
// Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
