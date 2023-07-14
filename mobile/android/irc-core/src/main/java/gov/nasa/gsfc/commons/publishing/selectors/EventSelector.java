//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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
 * An EventSelector has a <code>matches(EventObject)</code> method that
 * returns true if the given event matches the selector criteria, false
 * otherwise.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/05 18:47:04 $
 * @author Troy Ames
 */
public interface EventSelector 
{
	/**
	 * Determines whether this selector matches the given event.
	 * 
	 * @param event A event to match.
	 * @return True if this selector matches the given event, false otherwise.
	 */
	public boolean matches(EventObject event);
}

//--- Development History ----------------------------------------------------
//
//	$Log: EventSelector.java,v $
//	Revision 1.1  2006/04/05 18:47:04  tames
//	Refactoring and implementation of a Publish/Subscribe pattern for EventObjects.
//	
//
