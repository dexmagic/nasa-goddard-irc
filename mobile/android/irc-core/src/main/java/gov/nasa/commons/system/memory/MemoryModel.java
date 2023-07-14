//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/MemoryModel.java,v 1.10 2005/07/12 17:05:47 tames Exp $
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
 * A MemoryModel maintains statistics describing the state of a pool of 
 * memory, from within which one or more Allocations can be requested, and the 
 * resulting dynamic Collection of Allocations can be accessed. 
 * <p>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/07/12 17:05:47 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public interface MemoryModel
{
	/**
	 * Adds the given MemoryModelListener as a listener for MemoryModelEvents
	 * (i.e., all events) from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public void addMemoryModelListener(MemoryModelListener listener);

	/**
	 * Removes the given MemoryModelListener as a listener for MemoryModelEvents
	 * (i.e., all events) from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public void removeMemoryModelListener(MemoryModelListener listener);

	/**
	 * Returns the MemoryModelListeners on this MemoryModel as an array
	 * of MemoryModelListeners.
	 * 
	 * @return The MemoryModelListeners on this MemoryModel as an array
	 *         of MemoryModelListeners
	 */
	public MemoryModelListener[] getMemoryModelListeners();

	/**
	 * Adds the given MemoryModelListener as a listener for (only)
	 * ReleaseRequestEvents from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public void addReleaseRequestListener(MemoryModelListener listener);

	/**
	 * Removes the given MemoryModelListener as a listener for (only)
	 * ReleaseRequestEvents from this MemoryModel.
	 * 
	 * @param listener A MemoryModelListener
	 */
	public void removeReleaseRequestListener(MemoryModelListener listener);

	/**
	 * Returns the Set of ReleaseRequestEvent listeners on this MemoryModel as
	 * an array of MemoryModelListeners.
	 * 
	 * @return The Set of ReleaseRequestEvent listeners on this MemoryModel as
	 *         an array of MemoryModelListeners
	 */
	public MemoryModelListener[] getReleaseRequestListeners();

	/**
	 * Returns true if this MemoryModel is currently able to allocate the given
	 * amount, false otherwise.
	 * 
	 * @param amount The allocation amount requested
	 * @return True if this MemoryModel is currently able to allocate the given
	 *         amount, false otherwise
	 */
	public boolean isAvailable(int amount);

	/**
	 * Attempts to allocate the given amount from within this MemoryModel. If
	 * successful, an Allocation representing the allocated amount is returned.
	 * Otherwise, the result is null.
	 * 
	 * @param amount The desired amount to allocate
	 * @return An Allocation representing the allocated amount, or null if the
	 *         Allocation attempt failed
	 */
	public Allocation allocate(int amount);

	/**
	 * Attempts to allocate the given amount from within this MemoryModel,
	 * blocking until the request can be filled.
	 * 
	 * @param amount The desired amount to allocate
	 * @return An Allocation representing the allocated amount
	 * @throws InterruptedException if the call blocks but is interrupted before
	 *             the allocation can be filled
	 */
	public Allocation blockingAllocate(int amount) throws InterruptedException;

	/**
	 * Releases the given Allocation from this MemoryModel.
	 * 
	 * @param allocation An Allocation from this MemoryModel
	 * @throws IllegalArgumentException if the given Allocation was not made on
	 *             this MemoryModel
	 */
	public void release(Allocation allocation);

	/**
	 * Returns the total size of this MemoryModel.
	 * 
	 * @return The total size of this MemoryModel
	 */
	public int getSize();

	/**
	 * Returns the current amount of available memory in this
	 * MemoryModel.
	 * 
	 * @return The current amount of available memory in this MemoryModel
	 */
	public int getAmountAvailable();
	
    /**
	 * Checks and resolves the given index relative to this memory model.
	 * 
	 * @returns an absolute index into the memory model.
	 */
	public int resolveIndex(int index);
}

//--- Development History  ---------------------------------------------------
//
// $Log: MemoryModel.java,v $
// Revision 1.10  2005/07/12 17:05:47  tames
// Refactored memory package to improve performance and to
// further encapsulate the MemoryModel from users.
//
// Revision 1.9  2005/04/04 15:40:58  chostetter_cvs
// Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
// Revision 1.8  2005/03/24 22:55:01  chostetter_cvs
// Changed synchronization on wrapping of memory model, removed extraneous methods
//
// Revision 1.7  2005/03/23 00:09:06  chostetter_cvs
// Tweaks to BasisSet allocation, release
//
// Revision 1.6  2004/09/15 20:06:17  chostetter_cvs
// Fixed issues with freeing of java.nio duplicate memory block freeing
//
// Revision 1.5  2004/07/15 21:28:16  chostetter_cvs
// Fixed error in maintaining wrap state
//
// Revision 1.4  2004/07/13 18:52:50  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.2  2004/07/08 19:31:34  chostetter_cvs
// MemoryModel changes
//
// Revision 1.1  2004/06/04 05:34:42  chostetter_cvs
// Further data, Algorithm, and Component work
//
