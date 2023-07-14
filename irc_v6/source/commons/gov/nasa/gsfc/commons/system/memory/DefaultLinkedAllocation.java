//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/DefaultLinkedAllocation.java,v 1.2 2005/08/19 18:44:14 tames_cvs Exp $
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

/**
 * A DefaultLinkedAllocation is a concrete Allocation that can 
 * represent a root allocation from a MemoryModel or an Allocation linked to 
 * another Allocation in a parent/child association. As a root Allocation this
 * class will release itself from the MemoryModel when there are no more 
 * "holders" of the Allocation. As a child Allocation this class will release
 * its hold on the parent Allocation that it is linked to.
 * 
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/08/19 18:44:14 $
 * @author Troy Ames
 */
public class DefaultLinkedAllocation extends AbstractAllocation
{
	private Allocation fParentAllocation;
	
	/**
	 * Constructs a new DefaultLinkedAllocation spanning the specified range of
	 * positions in a memory model, and having an external reference count of 1.
	 * 
	 * @param model The MemoryModel of the new Allocation
	 * @param start The start of the new Allocation within the memory model
	 * @param size The size of the new Allocation within the memory model
	 */	
	public DefaultLinkedAllocation(MemoryModel model, int start, int size)
	{
		super(model, size);
		
		fStart = start;
	}	
		
	/**
	 * Constructs a new Allocation linked to another parent
	 * Allocation spanning the specified range of positions in a memory model,
	 * and having an external reference count of 1.
	 * 
	 * @param parent The parent Allocation to this new Allocation.
	 * @param start The start of the new Allocation within the memory model
	 * @param size The size of the new Allocation within the memory model
	 */	
	protected DefaultLinkedAllocation(Allocation parent, int start, int size)
	{
		super(size);
		
		fStart = start;
		fParentAllocation = parent;
		
		if(fParentAllocation != null)
		{
			fParentAllocation.hold();
		}
	}
	
	/**
	 *  Decrements the current reference count of this Allocation. If this
	 *  Allocation has a parent and the reference count is zero as a result
	 *  of this call the parents <code>release</code> method will be called.
	 * 
	 *  @return The decremented reference count of this Allocation
	 */
	protected synchronized int decrementReferenceCount()
	{
		int result = super.decrementReferenceCount();
		
		if (fParentAllocation != null && result <= 0)
		{
			fParentAllocation.release();
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
			remainder = new DefaultLinkedAllocation
				(this, resolveIndex(this.fStart + index), length);	
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
		
		result = new JoinedIndexAllocation(
				this, allocation, this.fStart,
				this.fSize + allocation.getSize());
		
		return (result);
	}
	
	/**
	 * Returns the MemoryModel of this Allocation.
	 * 
	 * @return The MemoryModel of this Allocation or null if not defined.
	 */
	public MemoryModel getMemoryModel()
	{
		MemoryModel result = null;
		
		if (fParentAllocation != null)
		{
			result = fParentAllocation.getMemoryModel();
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
		
		if (fParentAllocation != null)
		{
			result = fParentAllocation.resolveIndex(index);
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
		String message = "[Start: " + fStart 
			+ " Size: " + fSize 
			+ " Refs: " + getReferenceCount() 
			+ " parent:" + fParentAllocation + "]";
		
		return message;
	}
	
	// --- Utility classes ---------------------------------------------------
	
	/**
	 * Utility class that represents the concatination of two parent Allocations.
	 */
	private static class JoinedIndexAllocation extends DefaultLinkedAllocation
	{
		private Allocation fSecondParent;
		private boolean fInternallyContiguous = false;

		/**
		 * Constructs a new Allocation spanning the specified range of positions
		 * across two parent Allocations, and having an external reference count
		 * of 1.
		 * 
		 * @param firstParent The first parent Allocation
		 * @param secondParent The second parent Allocation
		 * @param start The start of the new Allocation within the memory model
		 * @param size The size of the new Allocation within the memory model
		 */	
		public JoinedIndexAllocation(
				Allocation firstParent, Allocation secondParent, int start, int size)
		{
			super(firstParent, start, size);
			fSecondParent = secondParent;

			if(fSecondParent != null)
			{
				fSecondParent.hold();
			}
			
			fInternallyContiguous = firstParent.isContiguousWith(fSecondParent);
		}

		/**
		 *  Decrements the current reference count of this Allocation. If the 
		 *  resulting reference count is 0 then the <code>release</code> 
		 *  method is called on both parents.
		 * 
		 *  @return The decremented reference count of this Allocation
		 */
		public synchronized int decrementReferenceCount()
		{
			int result = super.decrementReferenceCount();
			
			if (fSecondParent != null && result <= 0)
			{
				fSecondParent.release();
			}
			
			return (result);
		}

		/**
		 * Returns true if the end of this Allocation immediately precedes the
		 * start of the given Allocation in its MemoryModel, false otherwise.
		 * 
		 * @param second The Allocation to test for contiguity with this
		 *            Allocation, and which must have been allocated from the
		 *            same MemoryModel as this Allocation
		 * @return True if the end of this Allocation immediately precedes the
		 *         start of the given Allocation in its MemoryModel, false
		 *         otherwise
		 */
		public boolean isContiguousWith(Allocation second)
		{
			boolean result = false;
			
			if (fInternallyContiguous)
			{
					result = super.isContiguousWith(second);
			}

			return result;
		}

		/**
		 *  Returns a String representation of this Allocation.
		 * 
		 *  @return A String representation of this Allocation
		 */
		public synchronized String toString()
		{
			String message = "[Joined " + super.toString() 
				+ " parent2:" + fSecondParent + "]";
			
			return message;
		}
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: DefaultLinkedAllocation.java,v $
// Revision 1.2  2005/08/19 18:44:14  tames_cvs
// Documentation change only.
//
// Revision 1.1  2005/07/12 17:05:47  tames
// Refactored memory package to improve performance and to
// further encapsulate the MemoryModel from users.
//
// Revision 1.1  2005/04/04 15:40:58  chostetter_cvs
// Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
// Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.2  2004/07/11 07:30:35  chostetter_cvs
// More data request work
//
// Revision 1.1  2004/07/01 23:41:03  chostetter_cvs
// MemoryModel work
//
