//=== File Prolog ============================================================
//
//
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ExitAction.java,v $
//  Revision 1.3  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/08/29 22:46:21  tames
//  *** empty log message ***
//
//  Revision 1.1  2004/08/29 19:08:36  tames
//  Initial version
//
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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gov.nasa.gsfc.irc.app.Irc;

/**
 * Exits the application by calling <code>Irc.shutdown()</code>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/01 21:50:58 $
 * @author 	Troy Ames
 */
public class ExitAction extends AbstractAction
{
	/**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
	 */
	public ExitAction()
	{
		super();
	}

    /**
     * Invoked when an Exit action occurs.
     */
	public void actionPerformed(ActionEvent e)
	{
		Irc.shutdown();
	}
}
