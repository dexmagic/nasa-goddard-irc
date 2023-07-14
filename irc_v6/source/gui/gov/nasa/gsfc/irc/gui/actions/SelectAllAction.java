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
//  $Log: SelectAllAction.java,v $
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/08/29 22:46:21  tames
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

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import gov.nasa.gsfc.commons.types.ObjectClippable;

/**
 * This defines a <code>SelectAllAction</code> that attempts to find the focus
 * owner by going up the component hierarchy starting at the source of the 
 * ActionEvent.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/01 21:50:58 $
 * @author 	Troy Ames
 */
public class SelectAllAction extends AbstractAction
{
	/**
     * Defines a <code>CutAction</code> object with a default
     * description string and default icon.
	 */
	public SelectAllAction()
	{
		super();
	}

    /**
     * Invoked when a select all action occurs.
     */
	public void actionPerformed(ActionEvent e)
	{
		Object target = e.getSource();
		
		if (target instanceof Component)
		{
			Component component = ((Component)target).getParent();

			// Look for a root Window component
			while (component != null)
			{
				if (component instanceof JPopupMenu)
				{
					component = ((JPopupMenu) component).getInvoker();
				}
				else if (component instanceof Window)
				{
					selectAll(((Window) component).getFocusOwner());
					component = null;
				}
				else
				{
					component = component.getParent();
				}
			}
		}
	}
	
	/**
	 * Selects all the text in the focused component if possible.
	 * @param focusedComponent component to apply copy operation on.
	 **/
	protected void selectAll(Component focusedComponent)
	{
		if (focusedComponent instanceof JTextComponent)
		{
			((JTextComponent) focusedComponent).selectAll();
		}
		else if (focusedComponent instanceof ObjectClippable)
		{
			((ObjectClippable) focusedComponent).selectAllObjects();
		}
		else
		{
			// Look for ObjectClippables in parents
			Component parent = focusedComponent.getParent();
			while (parent != null)
			{
				if (parent instanceof ObjectClippable)
				{
					((ObjectClippable) parent).selectAllObjects();
					parent = null;
				}
				else
				{
					parent = parent.getParent();
				}
			}
		}
	}

}
