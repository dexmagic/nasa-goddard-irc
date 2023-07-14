//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ToolBarUtil.java,v $
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

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
/**
 *    This class provides utility methods that assist in building toolbars.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/16 21:12:51 $
 *  @author	    Ken Wootton
 */
public class ToolBarUtil
{
	//  For some reason, buttons spread their horizontal margins
	//  too much when using just an icon (this is especially true since
	//  JDK 1.4) .  Using the below margin fixes this problem.
	private static final Insets BUTTON_MARGIN = new Insets(0, 0, 0, 0);

	/**
	 *    Create a button in a toolbar with the given parameters.
	 *
	 *  @param toolbar  the tool bar to which the button will be added
	 *  @param resourceClass  the class that will be used as the base
	 *                        for the relative location link
	 *  @param relIconLocation  relative location of the icon from the
	 *                          given resource class
	 *  @param actionCommand  if not null, the action command for the button
	 *  @param actionListener  if not null, the listener to add to button
	 *  @param isEnabled  whether or not the button is initially enabled
	 *  @param toolTip  This is the text to use for the tool tip.  'null'
	 *                  and blank strings are ignored.
	 *
	 *  @return  the created tool bar button
	 */
	public static JButton createToolBarButton(JToolBar toolBar,
											  Class resourceClass,
											  String relIconLocation,
											  String actionCommand,
											  ActionListener actionListener,
											  boolean isEnabled,
											  String toolTip)
	{
		JButton but = ButtonUtil.createButton(resourceClass, relIconLocation,
											  actionCommand, actionListener,
											  isEnabled, toolTip);

		//  Fix the margins.
		but.setMargin(BUTTON_MARGIN);

		toolBar.add(but);

		return but;
	}

	/**
	 *    Create a toggle button in a toolbar with the given parameters.
	 *
	 *  @param toolbar  the tool bar to which the button will be added
	 *  @param resourceClass  the class that will be used as the base
	 *                        for the relative location link
	 *  @param relIconLocation  relative location of the icon from the
	 *                          given resource class
	 *  @param actionCommand  if not null, the action command for the button
	 *  @param actionListener  if not null, the listener to add to button
	 *  @param isEnabled  whether or not the button is initially enabled
	 *  @param isSelected  the initial selection state of the button
	 *  @param toolTip  This is the text to use for the tool tip.  'null'
	 *                  and blank strings are ignored.
	 *
	 *  @return  the created toggle button
	 */
	public static JToggleButton createToolBarToggle(
		JToolBar toolBar, 
		Class resourceClass, 
		String relIconLocation,
		String actionCommand, 
		ActionListener actionListener, 
		boolean isEnabled,
		boolean isSelected, 
		String toolTip)
	{
		JToggleButton but = ButtonUtil.createToggleButton(resourceClass,
														  relIconLocation,
														  actionCommand,
														  actionListener,
														  isEnabled,
														  isSelected,
														  toolTip);

		//  Fix the margins.
		but.setMargin(BUTTON_MARGIN);

		toolBar.add(but);

		return but;		
	}
}
