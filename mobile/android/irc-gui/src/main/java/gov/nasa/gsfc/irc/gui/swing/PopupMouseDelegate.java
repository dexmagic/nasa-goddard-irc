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

package gov.nasa.gsfc.irc.gui.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPopupMenu;


/**
 * This delegate provides a mouse listener that will pop up the given popup menu
 * upon the proper mouse requests. Note that it is assumed that the invoker of
 * the mouse events is a component because the source of the events is used to
 * position the actual menu.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/11/08 23:06:38 $
 * @author Troy Ames
 */
public class PopupMouseDelegate extends MouseAdapter
{
	private static final String CLASS_NAME = 
			PopupMouseDelegate.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private JPopupMenu fPopupMenu = null;

	/**
	 * Constructor. Note that this does NOT setup the mouse listener.
	 * 
	 * @param popupMenu the popup menu to display
	 */
	public PopupMouseDelegate(JPopupMenu popupMenu)
	{
		fPopupMenu = popupMenu;
	}

	/**
	 * Invoked when the mouse is clicked in the component
	 * 
	 * @param e the mouse event
	 */
	public void mouseClicked(MouseEvent e)
	{
		checkForPopup(e);
	}

	/**
	 * Invoked when a mouse button is pressed while the mouse is in a component.
	 * 
	 * @param e the mouse event
	 */
	public void mousePressed(MouseEvent e)
	{
		checkForPopup(e);
	}

	/**
	 * Invoked when a mouse button is released while the mouse is in a
	 * component.
	 * 
	 * @param e the mouse event
	 */
	public void mouseReleased(MouseEvent e)
	{
		checkForPopup(e);
	}

	/**
	 * Determine if the given mouse event should cause a popup menu to appear.
	 */
	protected void checkForPopup(MouseEvent e)
	{
		//  If needed, popup the menu at the place where the mouse
		//  was pressed.
		if (e != null && e.isPopupTrigger())
		{
			showPopup(e);
		}
	}

	/**
	 * Display the popup menu at the coordinates given by the mouse event.
	 * 
	 * @param e the mouse event that caused the popup
	 */
	protected void showPopup(MouseEvent e)
	{
		try
		{
			if (e != null && fPopupMenu != null)
			{
				fPopupMenu.show((Component) e.getSource(), e.getPoint().x, e
						.getPoint().y);
			}
		}
		//  If there is a problem, just log it.
		catch (ClassCastException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Sources of mouse events must be components";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, "showPopup", message, ex);
			}
		}
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: PopupMouseDelegate.java,v $
//  Revision 1.2  2004/11/08 23:06:38  tames
//  Logging and Javadoc changes
//
// 
