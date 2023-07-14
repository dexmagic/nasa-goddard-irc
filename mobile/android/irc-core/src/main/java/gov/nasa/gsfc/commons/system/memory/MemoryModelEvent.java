//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/memory/MemoryModelEvent.java,v 1.2 2005/07/12 17:05:47 tames Exp $
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

import java.util.EventObject;


/**
 * A MemoryModelEvent is an abstract class for conveying MemoryModel event 
 * information.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/07/12 17:05:47 $
 * @author Carl F. Hostetter
 */

public abstract class MemoryModelEvent extends EventObject
{
	/**
	 * Constructs a new MemoryModelEvent for the given MemoryModel source.
	 * 
	 * @param source The source of the new MemoryModelEvent
	 */
	public MemoryModelEvent(Object source)
	{
		super(source);
	}
	
	/**
	 * Returns the MemoryModel source of this MemoryModelEvent.
	 * 
	 * @param source The source of the new MemoryModelEvent
	 */
	public MemoryModel getMemoryModel()
	{
		return ((MemoryModel) super.getSource());
	}

	/**
	 * Returns a String representation of this MemoryModelEvent.
	 * 
	 * @return A String representation of this MemoryModelEvent
	 */
	public String toString()
	{
		String result = null;
		
		MemoryModel source = (MemoryModel) super.getSource();
		
		if (source != null)
		{
			result = source.toString();
		}
		
		return (result);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MemoryModelEvent.java,v $
//  Revision 1.2  2005/07/12 17:05:47  tames
//  Refactored memory package to improve performance and to
//  further encapsulate the MemoryModel from users.
//
//  Revision 1.1  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
