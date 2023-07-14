//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: StartAllAction.java,v $
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/09/04 13:31:06  tames
//  *** empty log message ***
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
 * Starts all components currently in the component manager.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/01 21:50:58 $
 * @author 	Troy Ames
 */
public class StartAllAction extends AbstractAction
{
	/**
     * Defines a Start All <code>Action</code> object with a default
     * description string and default icon.
	 */
	public StartAllAction()
	{
		super();
	}

    /**
     * Invoked when a StartAllAction occurs.
     */
	public void actionPerformed(ActionEvent e)
	{
		Irc.getComponentManager().startAllComponents();
	}
}
