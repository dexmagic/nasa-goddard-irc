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

import gov.nasa.gsfc.commons.publishing.messages.MessageEvent;

/**
 * An EventSelector that matches only events where 
 * <code>(event instanceof MessageEvent)</code> is true.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 03:57:31 $
 * @author 	Troy Ames
 */
public class MessageEventSelector implements EventSelector
{
	private static final EventSelector sInstance = new MessageEventSelector();
	
	/**
	 * Private constructor to prevent other instances from getting created.
	 */
	private MessageEventSelector()
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
	 * Matches instances of MessageEvents.
	 * 
	 * @param event A event to match.
	 * @return True if event is an instance of a MessageEvent, false otherwise.
	 */
	public boolean matches(EventObject event)
	{
		boolean result = false;
		
		if (event instanceof MessageEvent)
		{
			result = true;
		}
		
		return result;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageEventSelector.java,v $
//  Revision 1.1  2006/04/18 03:57:31  tames
//  Initial implementation.
//
//