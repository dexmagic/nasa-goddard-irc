//=== File Prolog ============================================================
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

package gov.nasa.gsfc.commons.system.memory;

/**
 * An Allocation represents an allocation from within a MemoryModel. An
 * Allocation has a size, a reference count that can be incremented and
 * decremented, and an associated MemoryModel. Allocations can also be compared
 * by their relative start position within the MemoryModel.
 * <p>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/07/12 17:05:47 $
 * @author Troy Ames
 */
public interface Allocation extends Comparable
{

	/**
	 * Returns the MemoryModel of this Allocation.
	 * 
	 * @return The MemoryModel of this Allocation or null if not defined.
	 */
	public MemoryModel getMemoryModel();

	/**
	 * Returns the size of this Allocation.
	 * 
	 * @return The size of this Allocation
	 */
	public int getSize();

	/**
	 * Returns the start of this Allocation within its MemoryModel.
	 * 
	 * @return The start of this Allocation within its MemoryModel
	 */
	public int getStart();

	/**
	 * Returns the current reference count of this Allocation. The reference
	 * count is the number of users of the Allocation. The reference count is 
	 * incremented by the <code>hold</code> method and decremented by the 
	 * <code>release</code> method.
	 * 
	 * @return The current reference count of this Allocation
	 */
	public int getReferenceCount();

	/**
	 * Returns true if the end of this Allocation immediately precedes the start
	 * of the given Allocation in its MemoryModel, false otherwise.
	 * 
	 * @param second The Allocation to test for contiguity with this Allocation.
	 * @return True if the end of this Allocation immediately precedes the start
	 *         of the given Allocation in its MemoryModel, false otherwise.
	 */
	public boolean isContiguousWith(Allocation second);

	/**
	 * Holds the Allocation from being reclaimed by incrementing the current 
	 * reference count of this Allocation.
	 * 
	 * @return The incremented reference count of this Allocation
	 */
	public int hold();

	/**
	 * Releases this Allocation by decrements the reference count of this 
	 * Allocation. If the resulting reference count is 0 then the Allocation
	 * can be reclaimed.
	 * 
	 * @return The decremented reference count of this Allocation
	 */
	public int release();

	/**
	 * Returns the absolute index in the MemoryModel of the given 
	 * relative index in the Allocation.
	 * 
	 * @return The absolute MemoryModel index of the relative allocation index.
	 */
	public int resolveIndex(int index);

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
	public Allocation slice(int index, int length);

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
	public Allocation append(Allocation allocation);
}

// --- Development History ---------------------------------------------------
//
// $Log: Allocation.java,v $
// Revision 1.2  2005/07/12 17:05:47  tames
// Refactored memory package to improve performance and to
// further encapsulate the MemoryModel from users.
//
//