//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/AllocateEvent.java,v 1.1 2005/07/12 17:05:47 tames Exp $
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
 * An AllocateEvent conveys information about a new Allocation on a MemoryModel.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/07/12 17:05:47 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public class AllocateEvent extends AllocationEvent
{
	/**
	 * Constructs a new AllocateEvent for the given MemoryModel source,
	 * indicating that the given Allocation has just been made on the
	 * MemoryModel.
	 * 
	 * @param source The source MemoryModel of the new AllocateEvent
	 * @param allocation A new Allocation from within the given MemoryModel
	 */
	public AllocateEvent(MemoryModel source, Allocation allocation)
	{
		super(source, allocation);
	}

	/**
	 * Returns a String representation of this AllocateEvent.
	 * 
	 * @return A String representation of this AllocateEvent
	 */
	public String toString()
	{
		String result = new String("Allocation " + getAllocation());
		
		return (result);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AllocateEvent.java,v $
//  Revision 1.1  2005/07/12 17:05:47  tames
//  Refactored memory package to improve performance and to
//  further encapsulate the MemoryModel from users.
//
//  Revision 1.1  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
