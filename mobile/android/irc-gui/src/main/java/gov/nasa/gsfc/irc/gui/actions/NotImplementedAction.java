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

package gov.nasa.gsfc.irc.gui.actions;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import gov.nasa.gsfc.irc.app.Irc;

/**
 * Displays a dialog box indicating the action has not been implemented yet.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/10 23:09:23 $
 * @author 	Troy Ames
 */
public class NotImplementedAction extends AbstractAction
{
	/**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
	 */
	public NotImplementedAction()
	{
		super();
	}

    /**
     * Invoked when a NotImplementedAction occurs.
     */
	public void actionPerformed(ActionEvent e)
	{
		Container root = Irc.getGuiFactory().getRootComponent();
		
		JOptionPane.showMessageDialog(root, "Sorry, not implemented yet.");
	}
}
