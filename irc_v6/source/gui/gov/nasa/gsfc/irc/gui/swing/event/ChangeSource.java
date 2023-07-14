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

package gov.nasa.gsfc.irc.gui.swing.event;

import javax.swing.event.ChangeListener;

/**
 * Implementors are sources of ChangeEvents or subclasses.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/16 23:00:36 $
 * @author 	Troy Ames
 */
public interface ChangeSource
{
	/**
	 * Adds a ChangeListener to this source.
	 * 
	 * @param listener the ChangeListener to add
	 * @see #fireStateChanged
	 * @see #removeChangeListener
	 */
	public void addChangeListener(ChangeListener listener);

	/**
	 * Removes a ChangeListener from this source.
	 * 
	 * @param listener the ChangeListener to remove
	 * @see #fireStateChanged
	 * @see #addChangeListener
	 */
	public void removeChangeListener(ChangeListener listener);

	/**
	 * Returns an array of all the <code>ChangeListener</code>s added to this
	 * source with addChangeListener().
	 * 
	 * @return all of the <code>ChangeListener</code>s added or an empty
	 *         array if no listeners have been added
	 */
	public ChangeListener[] getChangeListeners();
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ChangeSource.java,v $
//  Revision 1.1  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//