//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ButtonUtil.java,v $
//  Revision 1.2  2004/10/15 20:42:37  jhiginbotham_cvs
//  Look for button icon in resources path.
//
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
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import gov.nasa.gsfc.commons.app.App;

/**
 *    This class provides utility methods that assist in creating simple
 *  buttons.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/10/15 20:42:37 $
 *  @author	    Ken Wootton
 */
public class ButtonUtil
{
	//  Tool tip to use when no tool tip is specified.
	private static final String DEFAULT_TOOL_TIP = "";

	/**
	 *    Create a button with the given parameters.
	 *
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
	public static JButton createButton(Class resourceClass,
									   String relIconLocation,
									   String actionCommand,
									   ActionListener actionListener,
									   boolean isEnabled,
									   String toolTip)
	{
		JButton but = null;

		//---Check for icon relative to specified class
		URL resource = resourceClass.getResource(relIconLocation);
		
		//---Check for icon in resources path
		if (resource == null)
		{
			resource = App.getResource(relIconLocation);
		}
				
		if (resource != null)
		{
			but = new JButton(new ImageIcon(resource));
		}

		//  If we didn't find the resource, just use a generic placeholder
		//  button.
		else
		{
			but = new JButton();
		}

		setupButton(but, actionCommand, actionListener, isEnabled, toolTip);

		return but;
	}

	/**
	 *    Create a toggle button with the given parameters.
	 *
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
	 *  @return  the created tool bar button
	 */
	public static JToggleButton createToggleButton(
		Class resourceClass, 
		String relIconLocation, 
		String actionCommand,
		ActionListener actionListener,
		boolean isEnabled,
		boolean isSelected,
		String toolTip)
	{
		JToggleButton but = null;

		URL resource = resourceClass.getResource(relIconLocation);
		if (resource != null)
		{
			but = new JToggleButton(new ImageIcon(resource));
		}

		//  If we didn't find the resource, just use a generic placeholder
		//  button.
		else
		{
			but = new JToggleButton();
		}

		//  Set up the button
		setupButton(but, actionCommand, actionListener, isEnabled, toolTip);
		but.setSelected(isSelected);

		return but;
	}

	/**
	 *    Set up the given button with the specified parameters.
	 *
	 *  @param but  the button
	 *  @param actionCommand  if not null, the action command for the button
	 *  @param actionListener  if not null, the listener to add to button
	 *  @param isEnabled  whether or not the button is initially enabled
	 *  @param toolTip  This is the text to use for the tool tip.  'null'
	 *                  and blank strings are ignored.
	 */
	public static void setupButton(AbstractButton but,
								   String actionCommand,
								   ActionListener actionListener,
								   boolean isEnabled,
								   String toolTip)
	{
		//  Action commands, listeners, and tool tips are added conditionally.
		if (actionCommand != null)
		{
			but.setActionCommand(actionCommand);
		}
		if (actionListener != null)
		{
			but.addActionListener(actionListener);
		}
		if (toolTip != null && !toolTip.equals(DEFAULT_TOOL_TIP))
		{
			but.setToolTipText(toolTip);
		}

		but.setEnabled(isEnabled);
	}
}









