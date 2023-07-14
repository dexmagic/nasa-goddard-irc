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

package gov.nasa.gsfc.irc.gui.vis;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import gov.nasa.gsfc.irc.data.DataSet;

/**
 * This interface defines the methods used by a visualization component 
 * to render on a graphics context. Implementing classes can be used
 * in a Decorator pattern for a component that delegates the actual drawing
 * to one or more VisRenderers. The implementing class can modify the bounding 
 * rectangle to influence other renderers that might follow.
 * 
 * <p>Note the parent component may not be a listener for data so implementors
 * of this interface must handle the case where the 
 * <code>draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)</code> 
 * method is passed null for the dataSet argument.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/14 21:47:11 $
 * @author 	Troy Ames
 */
public interface VisRenderer
{

	/**
	 * Draw on the given graphics context.  This method
	 * is used by a parent visualization component to draw its image.<p>
	 *  
	 * @param g2d  the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render if applicable, may be null.
	 *
	 * @return a rectangle that can be passed to other renderers. Typically this
	 * 		is identical to the rectangle passed in.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet);

	/**
	 * Set the parent component that will use this renderer to draw its image.
	 *
	 * @param parent  the parent component
	 */
	public void setParent(JComponent parent);
	
    /**
     * Get the amount of space needed by this renderer. 
     *
     * @return the preferred size of this renderer.
     */
	public Dimension getPreferredSize();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: VisRenderer.java,v $
//  Revision 1.2  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//