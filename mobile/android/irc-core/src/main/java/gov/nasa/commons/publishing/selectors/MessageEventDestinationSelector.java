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
import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;

/**
 * An EventSelector that matches only events where
 * <code>(event instanceof MessageEvent)</code> and
 * <code>event.getDestination() == destination</code> or
 * <code>event.getDestination().startsWith(destination)</code>is true. The 
 * value of <code>destination</code> is an argument of the constructor.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/21 15:55:20 $
 * @author Troy Ames
 */
public class MessageEventDestinationSelector implements EventSelector
{
	Path fDestination = null;
	
	/**
	 * Construct a selector with an empty path.
	 */
	public MessageEventDestinationSelector()
	{
		fDestination = new DefaultPath();
	}
	
	/**
	 * Construct a selector to match the given destination.
	 */
	public MessageEventDestinationSelector(Path destination)
	{
		fDestination = destination;
	}
	
	/**
	 * Matches instances of MessageEvents starting with a specified destination
	 * Path.
	 * 
	 * @param event A event to match.
	 * @return True if event matches constrains of this selector, false otherwise.
	 */
	public boolean matches(EventObject event)
	{
		boolean result = false;
		
		if (event instanceof MessageEvent)
		{
			Path destination = ((MessageEvent) event).getDestination();
			
			if (destination == fDestination)
			{
				result = true;
			}
			else if (destination != null && destination.startsWith(fDestination))
			{
				result = true;
			}
		}
		
		return result;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageEventDestinationSelector.java,v $
//  Revision 1.3  2006/04/21 15:55:20  tames_cvs
//  Added no arg constructor that sefaults to an empty path for comparisons. Also
//  added support for specifing a null path.
//
//  Revision 1.2  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 03:57:31  tames
//  Initial implementation.
//
//