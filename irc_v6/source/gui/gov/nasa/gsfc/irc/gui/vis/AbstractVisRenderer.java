//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/AbstractVisRenderer.java,v 1.1 2004/12/14 21:47:11 tames Exp $
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gov.nasa.gsfc.irc.data.DataSet;


/**
 * This abstract class provides the basic functionality for a data
 * renderer.  Implementing classes can be used
 * in a Decorator pattern for a parent component that delegates the actual 
 * drawing to this renderer. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/14 21:47:11 $
 * @author	Troy Ames
 */
public abstract class AbstractVisRenderer 
	implements VisRenderer, ChangeListener
{
	// Component that is using this renderer.
	private JComponent fParent = null;
    
	/**
	 * Default constructor of a data renderer.
	**/
	public AbstractVisRenderer()
	{
	}

	/**
	 * Set the component that will use this renderer to draw its content.
	 *
	 * @param component  the chart component
	 */
	public void setParent(JComponent parent)
	{
		fParent = parent;
	}

	/**
	 * Get the component that is using this renderer to draw its content.
	 *
	 * @return  the chart component
	 */
	public JComponent getParent()
	{
		return fParent;
	}

	/**
	 * Draw the data on the given graphics context.  This method
	 * is used by a parent visualization component to draw its image.<p>
	 *  
	 * @param g2d  the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 *
	 * @return a rectangle that can be passed to other renderers. Typically this
	 * 		is identical to the rectangle passed in.
	 */
	public abstract Rectangle2D draw(
			Graphics2D g2d, Rectangle2D rect, DataSet dataSet);

    /**
     * Get the amount of space needed for this renderer. Returns null
     * by default.
     *
     * @return the preferred size of this renderer.
     */
    public Dimension getPreferredSize() 
    {
        return null;
    }

    /**
	 * Invoked when a model of interest has changed its state. Calls 
	 * <code>repaint</code> on the parent component.
	 *
	 * @param event  a ChangeEvent object
	 */
	public void stateChanged(ChangeEvent event)
	{
		// We don't really care what changed, but assume we need to redraw to
		// reflect the change.
		if (fParent != null)
		{
			fParent.repaint();
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractVisRenderer.java,v $
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//