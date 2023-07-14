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

package gov.nasa.gsfc.commons.publishing.selectors;

import java.util.EventObject;

/**
 * An EventSelector that matches all events.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/05 18:47:05 $
 * @author 	Troy Ames
 */
public class AllEventSelector implements EventSelector
{
	private static final EventSelector sInstance = new AllEventSelector();
	
	/**
	 * Private constructor to prevent other instances from getting created.
	 */
	private AllEventSelector()
	{	
	}
	
	/**
	 * Returns the same singleton instance of an AllEventSelector.
	 * 
	 * @return an instance of AllEventSelector
	 */
	public static EventSelector getInstance()
	{
		return sInstance;
	}
	
	/**
	 * Matches all events.
	 * 
	 * @param event A event to match.
	 * @return True in all cases.
	 */
	public boolean matches(EventObject event)
	{
		return true;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AllEventSelector.java,v $
//  Revision 1.1  2006/04/05 18:47:05  tames
//  Refactoring and implementation of a Publish/Subscribe pattern for EventObjects.
//
//