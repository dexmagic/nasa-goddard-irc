//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/AbstractAutoScaleRenderer.java,v 1.2 2005/01/11 21:35:46 chostetter_cvs Exp $
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.DefaultAxisModel;


/**
 * This class auto scales an axis model if the model has auto scaling enabled. 
 * Instances of this class should be placed in the render chain before any 
 * renderer that is dependent on the same axis model. 
 * This class does not have any visual components and does not draw to the 
 * graphics context.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/11 21:35:46 $
 * @author	Troy Ames
 */
public abstract class AbstractAutoScaleRenderer extends AbstractVisRenderer
{
	private static final String CLASS_NAME = AbstractAutoScaleRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
    private AxisModel fAxisModel = null;

	/**
	 * Create a new auto scaler for the given axis model.
	 * 
	 * @param model the axis model to autoscale.
	 */
	public AbstractAutoScaleRenderer(AxisModel model)
	{
		super();
		
		fAxisModel = model;		
		
		if (fAxisModel == null)
		{
			fAxisModel = new DefaultAxisModel();
		}
	}

	/**
	 * Update the axis model if needed based on the given dataset.
	 *  
	 * @param g2d  the graphics context.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 *
	 * @return the same rectangle instance received.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		if (fAxisModel.isAutoScale())
		{
			if (dataSet != null)
			{
				autoScaleAxis(fAxisModel, dataSet);
			}
		}
		
		return rect;
	}
	
	/**
	 * Update the axis based on the data in the given DataSet.
	 * 
	 * @param model	the model to update.
	 * @param dataSet the current DataSet.
	 */
	public abstract void autoScaleAxis(AxisModel model, DataSet dataSet);
	

	/**
	 * Get the current AxisModel.
	 * 
	 * @return Returns the axisModel.
	 */
	public AxisModel getAxisModel()
	{
		return fAxisModel;
	}
	
	/**
	 * Set the AxisModel for this auto scaler.
	 * 
	 * @param axisModel The axisModel to set.
	 */
	public void setAxisModel(AxisModel model)
	{
		fAxisModel = model;
	}
	
	/**
	 * Get the current auto scale setting.
	 * 
	 * @return Returns true if auto scale is enabled, false otherwise.
	 */
	public boolean isAutoScale()
	{
		return fAxisModel.isAutoScale();
	}
	
	/**
	 * Set the current auto scale setting. 
	 * 
	 * @param enabled True to enable auto scaling, false to disable.
	 */
	public void setAutoScale(boolean enabled)
	{
		if (fAxisModel != null)
		{
			fAxisModel.setAutoScale(enabled);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractAutoScaleRenderer.java,v $
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.1  2004/11/15 19:42:43  tames
//  Initial Version
//
//