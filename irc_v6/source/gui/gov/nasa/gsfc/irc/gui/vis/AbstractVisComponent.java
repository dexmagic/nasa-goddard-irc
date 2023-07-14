//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/AbstractVisComponent.java,v 1.7 2005/11/07 22:14:48 tames Exp $
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.swing.event.ChangeSource;


/**
 * This class provides a component that uses one or more renderers to draw its
 * image to the screen.  This class will prompt a renderer to draw an image
 * as necessary by calling the <code>draw</code> method on the renderer.
 * <p>
 * Requests to update the component come from either direct requests,
 * through the <code>repaint</code> method, or from AWT, through the 
 * <code>paintComponent</code> method.  
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/11/07 22:14:48 $
 * @author	Troy Ames
 */
public class AbstractVisComponent extends JComponent 
	implements VisComponent, ChangeListener
{
	// List of VisRenderers
	private ArrayList fRenderList = new ArrayList();
	
	/**
	 * Create a new visualization component.
	 */
	public AbstractVisComponent()
	{
		super();
	}

	/**
	 * Create a new visualization component with the specified model. This 
	 * instance will be registered as a <code>ChangeListener</code> to the 
	 * model.
	 * 
	 * @param model the model for this visualization component
	 */
	public AbstractVisComponent(ChangeSource model)
	{
		super();

		model.addChangeListener(this);
	}

	/**
	 * Paint the component by asking all renderers to draw on the Graphics
	 * context. If this component is opaque this method will first draw the 
	 * background and then call the <code>rendererDraw</code> method.
	 *
	 * @param g  the graphics context
	 */
	public synchronized void paintComponent(Graphics g)
	{
	    Insets inset = getInsets();
	    int drawWidth = getWidth() - inset.left - inset.right;
	    int drawHeight = getHeight() - inset.top - inset.bottom;
		
	    if (isOpaque())
		{ 
	    	Color originalColor = g.getColor();
	    	
	    	// Paint background for component
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(originalColor);
		}
		
	    Rectangle2D rect = new Rectangle(
	    	inset.left, inset.top, drawWidth, drawHeight);

	    // Let all renderers draw on the graphics object
	    rendererDraw((Graphics2D) g, rect, null);
	}
	
	/**
	 * Allow all renderers to draw on the given graphics context in the order 
	 * they were added to this component. This method
	 * is called from the <code>paintComponent</code> method.
	 *  
	 * @param g2d  the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render if applicable, may be null.
	 */
	protected synchronized void rendererDraw(
			Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		int size = fRenderList.size();

		// Let all renderers draw on the graphics object
	    for (int i=0; i < size; i++)
		{
			rect = ((VisRenderer) fRenderList.get(i)).draw(g2d, rect, dataSet);
		}		
	}

	/**
     * Returns a size based on the preferred size of added 
     * renderers.
     *
     * @return the value of the <code>preferredSize</code> property
     * @see #setPreferredSize
     * @see ComponentUI
     */
    public Dimension getPreferredSize()
	{
    	
    	Dimension size = null;
    	VisRenderer[] renderers = getRenderers();
    	
    	for (int i = 0; i < renderers.length; i++)
    	{
    		size = renderers[i].getPreferredSize();
    	}
    	
    	// if renderers don't care defer to the component
    	// TODO should this be reversed and only use the size returned by a 
    	// renderer if the component does not care?
    	if (size == null)
    	{
        	size = super.getPreferredSize();
    	}
    	     	
		return size;
	}

	/**
	 * Invoked when a model of interest has changed its state.
	 *
	 * @param event  a ChangeEvent object
	 */
	public void stateChanged(ChangeEvent event)
	{
		// We don't really care what changed, but assume we need to redraw to
		// reflect the change.
		repaint();
	}
	
    /**
	 * Adds a renderer to this Chart. When this component is asked to repaint 
	 * itself, each renderer added will have their <code>draw</code> method
	 * called in the order they were added.
	 * 
	 * @param renderer The renderer to add to this component.
	 * @see VisRenderer#draw(Graphics2D, DataSet)
	 */
	public synchronized void addRenderer(VisRenderer renderer)
	{
		renderer.setParent(this);
		fRenderList.add(renderer);		
	}

    /**
	 * Removes a renderer from this Chart.
	 * 
	 * @param renderer The renderer to remove from this component.
	 */
	public synchronized void removeRenderer(VisRenderer renderer)
	{
		//renderer.setParent(null);
		fRenderList.remove(renderer);		
	}

	/**
	 * Get the renderers that have been added to this component.
	 * 
	 * @return an array of known renderers
	 */
	public synchronized VisRenderer[] getRenderers()
	{
		VisRenderer[] rendererArray = new VisRenderer[fRenderList.size()];
		
		return (VisRenderer[]) fRenderList.toArray(rendererArray);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractVisComponent.java,v $
//  Revision 1.7  2005/11/07 22:14:48  tames
//  Synchronized methods that modify Renderers collection.
//
//  Revision 1.6  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.5  2004/12/20 22:41:29  tames
//  Visualization property editing support added.
//
//  Revision 1.4  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//