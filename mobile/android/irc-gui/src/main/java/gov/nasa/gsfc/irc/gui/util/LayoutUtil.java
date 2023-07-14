//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: LayoutUtil.java,v $
// Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
// Port from IRC v5.
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
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 *    This class contains general routines for laying
 *  out GUI elements.  In particular, it contains those routines
 *  that assist in the monotony of setting up a grid bag layout.
 *
 *  @version (01/25/98) Initial creation
 *  @author Kenneth Wootton
 */

public class LayoutUtil
{
    public static final int STANDARD_INSET = 4;

    /**
     *    This is a convenience function for setting up constraints
     *  in a grid bag layout.  The insets are set to 'STANDARD_INSET'.
     *
     *  @param container  Container using a grid bag layout
     *  @param component  Component to place within the container
     *  @param gridX  X position of the component
     *  @param gridY  Y position of the component
     *  @param weightX  extra space in the X direction
     *  @param weightY  extra space in the Y direction
     *  @param gridWidth  width of the component 
     *  @param gridHeight  height of the component
     *  @param anchor  position within the grid cell
     *  @param fill  dimension that will grow when there is extra space
     */
    public static void gbConstrain(Container container, Component component,
                                   int gridX, int gridY, 
                                   double weightX, double weightY,
                                   int gridWidth, int gridHeight, 
                                   int anchor, int fill)
    {
		gbConstrain(container, component, gridX, gridY, weightX, weightY,
					gridWidth, gridHeight, anchor, fill, STANDARD_INSET);
    }

    /**
     *    This is a convenience function for setting up constraints
     *  in a grid bag layout. 
     *
     *  @param container  Container using a grid bag layout
     *  @param component  Component to place within the container
     *  @param gridX  X position of the component
     *  @param gridY  Y position of the component
     *  @param weightX  extra space in the X direction
     *  @param weightY  extra space in the Y direction
     *  @param gridWidth  width of the component 
     *  @param gridHeight  height of the component
     *  @param anchor  position within the grid cell
     *  @param fill  dimension that will grow when there is extra space
     *  @param inset  inset for all sides of the given component
     */
    public static void gbConstrain(Container container, Component component,
                                   int gridX, int gridY, 
                                   double weightX, double weightY,
                                   int gridWidth, int gridHeight, 
                                   int anchor, int fill, int inset)
    {
		gbConstrain(container, component, gridX, gridY, weightX, weightY,
					gridWidth, gridHeight, anchor, fill, inset, inset,
					inset, inset); 
    }

    /**
     *    This is a convenience function for setting up constraints
     *  in a grid bag layout. 
     *
     *  @param container  Container using a grid bag layout
     *  @param component  Component to place within the container
     *  @param gridX  X position of the component
     *  @param gridY  Y position of the component
     *  @param weightX  extra space in the X direction
     *  @param weightY  extra space in the Y direction
     *  @param gridWidth  width of the component 
     *  @param gridHeight  height of the component
     *  @param anchor  position within the grid cell
     *  @param fill  dimension that will grow when there is extra space
     *  @param insetTop  inset for the top of the given component
     *  @param insetLeft  inset for the left of the given component
     *  @param insetBottom  inset for the bottom of the given component
     *  @param insetRight  inset for the right of the given component
     */
    public static void gbConstrain(Container container, Component component,
                                   int gridX, int gridY, 
                                   double weightX, double weightY,
                                   int gridWidth, int gridHeight, 
                                   int anchor, int fill,
                                   int insetTop, int insetLeft,
                                   int insetBottom, int insetRight)
    {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.weightx = weightX;
        constraints.weighty = weightY;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.anchor = anchor;
        constraints.fill = fill;
        constraints.insets = new Insets(insetTop, insetLeft,
                                        insetBottom, insetRight);
        container.add(component, constraints);
    }

    /**
     *    This is a convenience function for setting up constraints
     *  in a grid bag layout.  This variation allows you to set the 
	 *  horizontal insets.  The other insets are set to 'STANDARD_INSET'.
     *
     *  @param container  Container using a grid bag layout
     *  @param component  Component to place within the container
     *  @param gridX  X position of the component
     *  @param gridY  Y position of the component
     *  @param weightX  extra space in the X direction
     *  @param weightY  extra space in the Y direction
     *  @param gridWidth  width of the component 
     *  @param gridHeight  height of the component
     *  @param anchor  position within the grid cell
     *  @param fill  dimension that will grow when there is extra space
     *  @param insetHor  inset in the horizontal direction (left and right) 
	 *                   of the given component
     */
    public static void gbConstrainHor(Container container, Component component,
									  int gridX, int gridY, 
									  double weightX, double weightY,
									  int gridWidth, int gridHeight, 
									  int anchor, int fill, int insetHor)
    {
		gbConstrain(container, component, gridX, gridY, weightX, weightY,
					gridWidth, gridHeight, anchor, fill, 
					STANDARD_INSET, insetHor, STANDARD_INSET, insetHor);
    }

    /**
     *    This is a convenience function for setting up constraints
     *  in a grid bag layout.  This variation allows you to set the 
	 *  vertical insets.  The insets are set to 'STANDARD_INSET'.
     *
     *  @param container  Container using a grid bag layout
     *  @param component  Component to place within the container
     *  @param gridX  X position of the component
     *  @param gridY  Y position of the component
     *  @param weightX  extra space in the X direction
     *  @param weightY  extra space in the Y direction
     *  @param gridWidth  width of the component 
     *  @param gridHeight  height of the component
     *  @param anchor  position within the grid cell
     *  @param fill  dimension that will grow when there is extra space
     *  @param insetVert  inset in the vertical direction (top and bottom)
	 *                    of the given component
     */
    public static void gbConstrainVert(Container container, 
									   Component component,
									   int gridX, int gridY, 
									   double weightX, double weightY,
									   int gridWidth, int gridHeight, 
									   int anchor, int fill, int insetVert)
    {
		gbConstrain(container, component, gridX, gridY, weightX, weightY,
					gridWidth, gridHeight, anchor, fill, 
					insetVert, STANDARD_INSET, insetVert, STANDARD_INSET);
    }
}
