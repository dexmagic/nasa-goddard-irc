//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: WindowUtil.java,v $
//  Revision 1.2  2004/08/31 22:04:29  tames
//  Added return to centerFrame method
//
//  Revision 1.1  2004/08/26 14:36:29  tames
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

package gov.nasa.gsfc.irc.gui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

/**
 *    This class contains simple window utility methods.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/08/31 22:04:29 $
 *  @author	    Ken Wootton
 *  @author	    Troy Ames
 */
public class WindowUtil
{
	/**
	 * Center the given frame on the desktop.
	 *
	 * @param frame  the container to be centered on the desktop
	 * @return the frame instance passed as the argument
	 */
	public static Container centerFrame(Container frame)
	{
		Dimension winSize = frame.getSize();
		Dimension screenSize = frame.getToolkit().getScreenSize();

		frame.setLocation((screenSize.width - winSize.width) / 2,
						(screenSize.height - winSize.height) / 2);
		return frame;
	}

	/**
	 *  Center the given component relative to the given relative component.
	 *
	 *  @param relativeComp  the component in which will be used to position
	 *                       the 'compToCenter'
	 *  @param compToCenter  the component to center
	 */
	public static void centerRelativeTo(Component relativeComp, 
										Component compToCenter)
	{
		Point relativeLoc = relativeComp.getLocationOnScreen();
		Dimension relativeSize = relativeComp.getSize();
		Dimension compToCenterSize = compToCenter.getSize();

		//  Determine the position as a delta from the relative component 
		//  and move it.
		Point2D.Double locFromRelative = new Point2D.Double(
			(relativeSize.getWidth() - compToCenter.getWidth()) / 2,
			(relativeSize.getHeight() - compToCenter.getHeight()) / 2);
		compToCenter.setLocation(
			(int) (relativeLoc.getX() + locFromRelative.getY()),
			(int) (relativeLoc.getY() + locFromRelative.getY()));
	}

	/**
	 *  Get the frame that contains this component, if there is one.
	 *
	 *  @param comp  the component to get the frame for
	 *
	 *  @return  the frame that contains the given component or 'null'
	 *           if one cannot be found
	 */
	public static Frame getTopLevelFrame(JComponent comp)
	{
		Frame topLevelFrame = null;
		Component topLevelComp = comp.getTopLevelAncestor();

		//  Ensure that the top level ancestor is a frame which should
		//  always be the case in a normal client, otherwise simply
		//  use null as this frame
		if(topLevelComp != null && topLevelComp instanceof Frame)
		{
			topLevelFrame = (Frame) topLevelComp;
		}

		return topLevelFrame;
	}
}

