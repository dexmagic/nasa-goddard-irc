//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: MenuUtil.java,v $
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
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

package gov.nasa.gsfc.irc.gui.util;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

/**
 *    This class provides utility methods that assist in building menus.
 *  Note that the methods below may take the menu itself as a 'JComponent'
 *  to allow this to be used for both popup and regular menus.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/16 21:12:51 $
 *  @author	    Ken Wootton
 */
public class MenuUtil
{
	private static final String DEF_TEXT = "";

    /**
     *    Create a menu item with the given properties.  Note that arguments
	 *  that are given 'null' as their value are ignored, i.e. the 
	 *  corresponding property of the menu item is left unaffected.
     *
	 *  @param menu  the menu in which to add the menu item
     *  @param text  text of the menu item
	 *  @param actionCommand  if not 'null', the action command for the item
	 *  @param actionListener  if not 'null', the action listener for the item
     *  @param isEnabled  whether or not the item is initially enabled
     */
    public static JMenuItem createMenuItem(JComponent menu, String text,
										   String actionCommand,
										   ActionListener actionListener,
										   boolean isEnabled)
    {
		return createMenuItem(menu, text, actionCommand, actionListener,
							  isEnabled, KeyEvent.VK_UNDEFINED);
    }

    /**
     *    Create a menu item with the given properties.  Note that arguments
	 *  that are given 'null' as their value are ignored, i.e. the 
	 *  corresponding property of the menu item is left unaffected.
     *
	 *  @param menu  the menu in which to add the menu item
     *  @param text  text of the menu item
	 *  @param actionCommand  if not 'null', the action command for the item
	 *  @param actionListener  if not 'null', the action listener for the item
     *  @param isEnabled  whether or not the item is initially enabled
     *  @param mnemonic  if not KeyEvent.VK_UNDEFINED, the nnemonic used to 
	 *                   access the item
     */
    public static JMenuItem createMenuItem(JComponent menu, String text,
										   String actionCommand,
										   ActionListener actionListener,
										   boolean isEnabled, int mnemonic)
    {
        JMenuItem menuItem = new JMenuItem(text);
		setupMenuItem(menu, menuItem, text, actionCommand, actionListener, 
					  isEnabled, mnemonic);

		return menuItem;
    }

	/**
	 *    Create a check box menu item with the given properties.  Note that
	 *  arguments that are given 'null' as their value are ignored, i.e. the 
	 *  corresponding property of the menu item is left unaffected.
	 *
	 *  @param menu  the menu in which to add the menu item
     *  @param text  text of the menu item
	 *  @param actionCommand  if not 'null', the action command for the item
	 *  @param actionListener  if not 'null', the action listener for the item
     *  @param isEnabled  whether or not the item is initially enabled
     *  @param mnemonic  if not KeyEvent.VK_UNDEFINED, the nnemonic used to 
	 *                   access the item
	 *  @param isSelected  whether or not the item is initially selected
	 */
	public static JCheckBoxMenuItem createCheckBoxMenuItem(
		JComponent menu, String text, String actionCommand,
		ActionListener actionListener, boolean isEnabled, boolean isSelected,
		int mnemonic)
	{
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(text);

		setupMenuItem(menu, menuItem, text, actionCommand, actionListener, 
					  isEnabled, mnemonic);
		menuItem.setSelected(isSelected);

		return menuItem;
	}

	/**
	 *    Set up the given menu item with the given properties and add it
	 *  to the given menu.  Note that arguments that are given 'null' as their 
	 *  value are ignored, i.e. the corresponding property of the menu item is 
	 *  left unaffected.
	 *
	 *  @param menu  the menu in which to add the menu item
	 *  @param menuItem  the menu item to change
     *  @param text  text of the menu item
	 *  @param actionCommand  if not 'null', the action command for the item
	 *  @param actionListener  if not 'null', the action listener for the item
     *  @param isEnabled  whether or not the item is initially enabled
     *  @param mnemonic  if not KeyEvent.VK_UNDEFINED, the nnemonic used to 
	 *                   access the item
	 */
	public static void setupMenuItem(JComponent menu, JMenuItem menuItem, 
									 String text, String actionCommand,
								   	 ActionListener actionListener,
									 boolean isEnabled, int mnemonic)
	{
		//  Fill in the text, using a default if it is missing.
		if (text != null)
		{
			menuItem.setText(text);
		}
		else
		{
			menuItem.setText(DEF_TEXT);
		}

		//  Action commands, action listeners, and mnemonics are added 
		//  conditionally.
		if (actionCommand != null)
		{
			menuItem.setActionCommand(actionCommand);
		}
		if (actionListener != null)
		{
			menuItem.addActionListener(actionListener);
		}
		if (mnemonic != KeyEvent.VK_UNDEFINED)
		{
			menuItem.setMnemonic(mnemonic);
		}

        menuItem.setEnabled(isEnabled);

		//  Add the item to the menu.
		if (menu != null)
		{
			menu.add(menuItem);
		}
	}
}
