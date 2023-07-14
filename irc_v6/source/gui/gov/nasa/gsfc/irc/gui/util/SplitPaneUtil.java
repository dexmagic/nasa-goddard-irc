//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: SplitPaneUtil.java,v $
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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *    This class provides convenience methods that work on JSplitPanes.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/16 21:12:51 $
 *  @author	    Ken Wootton
 */
public class SplitPaneUtil
{
	//  IDs for the two panes.
	public static final int LEFT_PANE = 0;
	public static final int RIGHT_PANE = 1;
	public static final int TOP_PANE = LEFT_PANE;
	public static final int BOTTOM_PANE = RIGHT_PANE;

	//  This is the divider size of a hidden, i.e. not shown, divider.
	public static final int HIDDEN_DIVIDER_SIZE = 0;

	//  Using this divider location will tell a JSplitPane to set its
	//  divider to a position that honors the resize weight of the split pane.
	public static final int DIV_HONOR_RESIZE = -1;

	//  Proportional divider locations.
	private static final double LEFT_TOP_DIV_LOC = 0.0;
	private static final double RIGHT_BOTTOM_DIV_LOC = 1.0;

	//  Resize weights
	private static final double LEFT_TOP_HIDDEN_WEIGHT = 0.0;
	private static final double RIGHT_BOTTOM_HIDDEN_WEIGHT = 1.0;

	//  Used to free up components in a split pane.
	private static final Dimension ZERO_DIM = new Dimension(0, 0);

	/**
	 *    Set the specified pane of the split pane to either be displayed
	 *  or hidden.  If asked to hide a pane, this method will hide the pane
	 *  and its divider and adjust the split pane's wieght so that it
	 *  can no longer be shown.  If asked to display a pane, it will restore
	 *  the divider, bring the divider to its last known position, and
	 *  restore the split pane's weight.
	 *
	 *  @param splitPane  the split pane that will have one of its panes 
	 *                    hidden/shown
	 *  @param paneId  either LEFT_TOP_PANE or RIGHT_BOTTOM_PANE
	 *  @param isDisplayed  whether or not to display the specified pane
	 *  @param dividerSize  the normal size of the split pane divider
	 *  @param displayedWeight  the weight for the split pane the given
	 *                          pane is displayed
	 */
	public static void setPaneDisplayed(JSplitPane splitPane, int paneId, 
										boolean isDisplayed, int dividerSize, 
										double displayedWeight)
	{
		//  Display the pane.  Reset its divider size, the last 
		//  location,  pane weight.
		if (isDisplayed)
		{
			splitPane.setDividerSize(dividerSize);
			splitPane.setDividerLocation(
				splitPane.getLastDividerLocation());
			splitPane.setResizeWeight(displayedWeight);
		}

		//  Hide the pane.  Hide the divider and shift the resize weight
		//  so that the pane can no longer be displayed.
		else
		{
			splitPane.setDividerSize(HIDDEN_DIVIDER_SIZE);

			//  Push the divider to the left/up and all weight to the 
			//  right/bottom pane.
			if (paneId == LEFT_PANE)
			{
				splitPane.setDividerLocation(LEFT_TOP_DIV_LOC);
				splitPane.setResizeWeight(LEFT_TOP_HIDDEN_WEIGHT);
			}

			//  Push the divider to the right/bottom and all the weight
			//  to the left/upper pane.
			else if (paneId == RIGHT_PANE)
			{
				splitPane.setDividerLocation(RIGHT_BOTTOM_DIV_LOC);
				splitPane.setResizeWeight(RIGHT_BOTTOM_HIDDEN_WEIGHT);
			}
		}

		splitPane.setEnabled(isDisplayed);
	}

	/**
	 *    A little noticed "feature" of a split pane causes it to not let
	 *  any of its components get smaller than their specified minimum size.
	 *  It also will force the divider to adjust to its component's 
	 *  preferred size on occasion.  This method fixes those 
	 *  problems (if that isn't desired).
	 *
	 *  @param c  the JComponent that will be placed in a JSplitPane
	 */
	public static void freeSplitPaneJComponent(JComponent c)
	{
		c.setMinimumSize(ZERO_DIM);
		c.setPreferredSize(ZERO_DIM);
	}
}
