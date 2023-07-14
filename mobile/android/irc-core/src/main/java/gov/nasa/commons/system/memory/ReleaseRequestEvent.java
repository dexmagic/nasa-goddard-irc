//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/ReleaseRequestEvent.java,v 1.2 2005/07/12 17:05:47 tames Exp $
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
 * A ReleaseRequestEvent is an event sent by a MemoryModel to request that 
 * its listeners release any of its Allocations that are currently held by them.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/07/12 17:05:47 $
 * @author Carl F. Hostetter
 */

public class ReleaseRequestEvent extends MemoryModelEvent
{
	/**
	 * Constructs a new ReleaseRequestEvent for the given MemoryModel source.
	 * 
	 * @param source The MemoryModel source of the new ReleaseRequestEvent
	 */
	public ReleaseRequestEvent(MemoryModel source)
	{
		super(source);
	}
	
	
	/**
	 * Returns a String representation of this ReleaseRequestEvent.
	 * 
	 * @return A String representation of this ReleaseRequestEvent
	 */
	public String toString()
	{
		String result = null;
		
		MemoryModel model = getMemoryModel();
		
		if (model != null)
		{
			result = "Release request for " + model;
		}
		
		return (result);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ReleaseRequestEvent.java,v $
//  Revision 1.2  2005/07/12 17:05:47  tames
//  Refactored memory package to improve performance and to
//  further encapsulate the MemoryModel from users.
//
//  Revision 1.1  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
