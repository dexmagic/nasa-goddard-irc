//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/cell/TwinkleCellRenderer.java,v 1.9 2006/04/27 18:51:52 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.library.gui.vis.cell;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.commons.numerics.types.Range;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.gui.util.ColorMap;
import gov.nasa.gsfc.irc.gui.vis.AbstractVisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.DefaultAxisModel;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;


/**
 * This class provides basic two dimensional gridded cell render capability.
 * This class can be used in a Decorator pattern for a parent 
 * component that delegates the actual drawing to this renderer. 
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/27 18:51:52 $
 * @author Troy Ames
 */
public class TwinkleCellRenderer extends AbstractVisRenderer
{
	private static final String CLASS_NAME = TwinkleCellRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Stroke definitions
	private static final float LINE_WIDTH = 1.0f;
	private static final BasicStroke BASIC_STROKE = new BasicStroke(LINE_WIDTH);

	/**
	 *    Use the range of values that exist within the current range
	 */
	public static final int AUTO_RANGE = 0;

	/**
	 *    Number of colors in any returned color map.
	 */
	public static final int NUM_COLORS = 256;

	//  Defaults
	private static final Range DEF_MAPPED_RANGE = new Range(0, 10000);
	private static final String RAMP_LUT_NAME = "Ramp";
	private static final String DEF_COLOR_LUT = RAMP_LUT_NAME;
	private static final String DEF_INTENSITY_LUT = RAMP_LUT_NAME;

	//  Default property values.
	private static final ColorMap DEF_COLOR_MAP = new ColorMap();
	private static final int DEF_LOW_CUT = 0;
	private static final int DEF_HIGH_CUT = 100;

	private static final int MAX_COLOR_INDEX = ColorMap.NUM_COLORS - 1;

	//  Property values
	private boolean fCellGridVisible = false;

	//  The diffenence between max and min.
	private double fRange = 0d;

	// Reused graphics objects.
	private Rectangle2D.Double fCellRect = new Rectangle2D.Double();
	//  The actual colors of the current color map in use. 

	//  Properties of the mapped range
	private int fMappedRangeType = AUTO_RANGE;
	private Range fMappedRange = DEF_MAPPED_RANGE;
	private double fMax = 0.0;
	private double fMin = 0.0;

	//  Look up tables 
	private String fColorLutName = DEF_COLOR_LUT;
	private String fIntensityLutName = DEF_INTENSITY_LUT;

	//  Color map.  Note that it is initially dirty because we don't want
	//  to create it until it is necessary.
//	private Color[] fColorMap = new Color[NUM_COLORS];
	private boolean fDirtyColorMap = true;

	private boolean fStructureChanged = true;
	private LinkedHashMap fBasisInfo = new LinkedHashMap(5);

	// Loop constants that define the screen ratios and margins.
	private int fCellRowOffset = 0;
	private int fCellColumnOffset = 0;
	private int fCellRows = 12;
	private int fCellColumns = 32;
	private boolean fXLeftOrigin = true;
	private boolean fYTopOrigin = true;

	private AxisModel fAxisModel = null;

	//  Property values
	//  The actual colors of the current color map in use. 
	private Color[] fColors;
	private ColorMap fColorMap = DEF_COLOR_MAP;
	private int fLowCut = DEF_LOW_CUT;
	private int fHighCut = DEF_HIGH_CUT;
	private String fBufferBaseName;


	/**
	 * Create a new renderer with default axis models.
	 */
	public TwinkleCellRenderer()
	{
		this(null);
	}

	/**
	 * Create a new renderer with the given axis models.
	 * 
	 * @param xAxis the X axis model
	 * @param yAxis the Y axis model
	 */
	public TwinkleCellRenderer(AxisModel axis)
	{
		super();

		fAxisModel = axis;
		
		if (fAxisModel == null)
		{
			fAxisModel = new DefaultAxisModel();
		}
		
		fAxisModel.addChangeListener(this);
	}

	/**
	 * Causes this renderer to plot new data from all the DataBuffers of the given
	 * BasisSet.
	 * 
	 * @param g2d the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 */
	private synchronized void drawBasisSet(Graphics2D g2d, Rectangle2D rect,
			BasisSet basisSet)
	{
		int numSamplesIn = basisSet.getSize();
		BasisBundleId bundleId = basisSet.getBasisBundleId();

		if (numSamplesIn > 0)
		{
			Color savedColor = g2d.getColor();
			//System.out.println("BasisSet desc:" + basisSet.getDescriptor());
			//JComponent parent = getParent();
			DataBuffer basisBuffer = basisSet.getBasisBuffer();

			int numBuffers = basisSet.getNumberOfDataBuffers();

			double basisValue = 0;

			//  Min and max axis values
			double min = fAxisModel.getViewMinimum();
			double max = min + fAxisModel.getViewExtent();
			fMin = min;
			fMax = max;

			//  Update the difference between max and min.
			fRange = max - min;

			// Drawing canvas bounds information
			double canvasLeft = rect.getMinX();
			double canvasRight = rect.getMaxX();
			double canvasTop = rect.getMinY();
			double canvasBottom = rect.getMaxY();

			//  Canvas width and height
			double canvasHeight = rect.getHeight() - 1;
			double canvasWidth = rect.getWidth() - 1;

			// Cell sizes
			double cellWidth = canvasWidth / fCellColumns;
			double cellHeight = canvasHeight / fCellRows;

			//  Collect color information.
			ColorMap colorMap = fColorMap;
			fColors = colorMap.getColorMap();
			int sampleIndex = numSamplesIn - 1;
			basisValue = basisBuffer.getAsDouble(sampleIndex);
			double dataValue = 0.0d;
			double cellXOrigin = 0.0d;
			
			// Determine which corner the [0, 0] cell will be located in.
			if (fXLeftOrigin)
			{
				cellXOrigin = canvasLeft + (fCellColumnOffset * cellWidth);
			}
			else
			{
				cellXOrigin = canvasRight - (fCellColumnOffset * cellWidth);				
			}
			
			double cellYOrigin = 0.0d;

			if (fYTopOrigin)
			{
				cellYOrigin = canvasTop + (fCellRowOffset * cellHeight);
			}
			else
			{
				cellYOrigin = canvasBottom - (fCellRowOffset * cellHeight);
			}

			for (int i = 0; i < numBuffers; i++)
			{
				double cellX = cellXOrigin;
				double cellY = cellYOrigin;

				DataBuffer dataBuffer = basisSet.getDataBuffer(i);
				
				if (dataBuffer.getName().startsWith(fBufferBaseName))
				{
					DataBufferDescriptor bufferInfo = dataBuffer.getDescriptor();
					
					Pixel pixel = bufferInfo.getPixel();
					
					if (pixel != null)
					{
						int[] coordinate = pixel.getCoordinates();
						int dimensions = coordinate.length;
						
						if (dimensions > 0)
						{
							if (fYTopOrigin)
							{
								cellY += cellHeight * coordinate[0];
							}
							else
							{
								cellY -= cellHeight * coordinate[0] + cellHeight;
							}
							
							if (dimensions == 2)
							{
								if (fXLeftOrigin)
								{
									cellX += cellWidth * coordinate[1];
								}
								else
								{
									cellX -= cellWidth * coordinate[1] + cellWidth;
								}
								
								dataValue = dataBuffer.getAsDouble(sampleIndex);
	
								//  Reset the square
								fCellRect.setRect(cellX, cellY, cellWidth,
									cellHeight);
	
								//  Draw the cell using the proper color.
								g2d.setColor(getCellColor(dataValue));
								//System.out.println("dataValue:" + dataValue
								//	+ " getCellColor():" + getCellColor(dataValue));
								g2d.fill(fCellRect);
	
								//  Draw the grid using the proper color.
								if (fCellGridVisible)
								{
									g2d.setColor(Color.GRAY);
									g2d.draw(fCellRect);
								}
							}
						}
					}
				}
			}

			// Restore graphics settings
			g2d.setColor(savedColor);
		}	
	}

	/**
	 * Get the color of the cell for the given value.
	 * 
	 * @param value the value that needs to be "normalized" to the color
	 */
	public Color getCellColor(double value)
	{
		Color cellColor = null;

		//		System.out.println("Value = " + value);

		//  Not a number is a special case that occurs when the input is bad.
		if (Double.isNaN(value))
		{
			//cellColor = getBadInputColor();
		}
		
		//  See if it missed the high cut
		else if (value >= fMax)
		{
			cellColor = fColors[MAX_COLOR_INDEX];
		}
		
		//  See if it missed the low cut.
		else if (value <= fMin)
		{
			cellColor = fColors[0];
		}
		
		//  Otherwise, normalize the value within the current cut range and
		//  find the proper color to display.
		else
		{
			double normToRange = (value - fMin) / fRange;
			cellColor = 
				fColors[(int) (normToRange * MAX_COLOR_INDEX + .5D)];
		}

		return cellColor;
	}

	/**
	 * Draw any needed selection indicator for the cell at the given coordinates
	 * and canvas location.
	 * 
	 * @param g2d the graphics context on which to draw
	 * @param row the row of the cell
	 * @param col the column of the cell
	 * @param cellX the x position of the cell within the canvas
	 * @param cellY the y position of the cell within the canvas
	 * @param cellWidth the width of each cell in the grid
	 * @param cellHeight the height of each cell in the grid
	 */
	protected void drawCell(Graphics2D g2d, int row, int col, 
					   		double cellX, double cellY, 
							double cellWidth, double cellHeight)
	{
		//  Reset the square
		fCellRect.setRect(cellX, cellY, cellWidth, cellHeight);

		//  Draw the square using the proper color.
		//g2d.setColor(getCellColor(fGridValues[row][col]));
		g2d.draw(fCellRect);
		g2d.fill(fCellRect);
	}

	/**
	 * Draw the data on the given graphics context.
	 * 
	 * @param g2d the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 * 
	 * @return the same rectangle instance received.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		if (dataSet != null)
		{
			if (fStructureChanged)
			{
				updateBasisSetInfo(dataSet);
				fStructureChanged = false;
			}

			Iterator basisSets = dataSet.getBasisSets().iterator();

			while (basisSets.hasNext())
			{
				BasisSet basisSet = (BasisSet) basisSets.next();

				drawBasisSet(g2d, rect, basisSet);
			}
		}

		return rect;
	}

	/**
	 * Update Basis set information.
	 * 
	 * @param dataSet the changed dataset
	 */
	private void updateBasisSetInfo(DataSet dataSet)
	{
		int numBasisSets = dataSet.getBasisSets().size();
		Iterator basisSets = dataSet.getBasisSets().iterator();
		LinkedHashMap tempBasisInfo = new LinkedHashMap(numBasisSets);

		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			BasisBundleId bundleId = basisSet.getBasisBundleId();

//			tempBasisInfo.put(bundleId, updateTraceInfo(basisSet,
//				(LinkedHashMap) fBasisInfo.get(bundleId)));

			if (sLogger.isLoggable(Level.FINE))
			{
				String message = CLASS_NAME + " updating BasisSetInfo:\n"
						+ basisSet;

				sLogger.logp(Level.FINE, CLASS_NAME, "updateBasisSetInfo",
					message);
			}
		}

		fBasisInfo = tempBasisInfo;
	}
	
	private HashMap updateTraceInfo(BasisSet basisSet, Map oldTraces)
	{
		LinkedHashMap newInfo = 
			new LinkedHashMap(basisSet.getDataBufferNames().size());

		System.out.println("basisSet.getDataBufferDescriptors():" + basisSet.getDataBufferDescriptors());
		Iterator bufferNames = basisSet.getDataBufferNames().iterator();

		while (bufferNames.hasNext())
		{
			String bufferName = (String) bufferNames.next();
			ChannelRenderInfo traceInfo = null;

			if (oldTraces != null && oldTraces.containsKey(bufferName))
			{
				// Reuse old trace info
				traceInfo = (ChannelRenderInfo) oldTraces.remove(bufferName);
			}
			else
			{
				// Create trace info for new buffer
				traceInfo = new ChannelRenderInfo();
			}

			newInfo.put(bufferName, traceInfo);
		}

		// TODO handle obsolete trace infos in oldTraces.
		return newInfo;
	}

	/**
	 * @return Returns the cellGridVisible.
	 */
	public boolean isCellGridVisible()
	{
		return fCellGridVisible;
	}
	
	/**
	 * 
	 * @param cellGridVisible The cellGridVisible to set.
	 */
	public void setCellGridVisible(boolean cellGridVisible)
	{
		fCellGridVisible = cellGridVisible;
	}
	
	/**
	 * Get the number of displayed columns.
	 * 
	 * @return Returns the cellColumns.
	 */
	public int getColumns()
	{
		return fCellColumns;
	}
	
	/**
	 * Set the number of columns that will be in the display.
	 * 
	 * @param columns The number of columns.
	 */
	public void setColumns(int columns)
	{
		fCellColumns = columns;
	}
	
	/**
	 * Get the base name of the buffer for this label renderer.
	 * @return Returns the base name of the buffer.
	 */
	public String getBaseName()
	{
		return fBufferBaseName;
	}

	/**
	 * Set the base name of the buffer to render a label for.
	 * @param baseName The base name of the buffer to display label for.
	 */
	public void setBaseName(String baseName)
	{
		fBufferBaseName = baseName;
	}

	/**
	 * Get the number of rows in the display.
	 * 
	 * @return Returns the number of rows.
	 */
	public int getRows()
	{
		return fCellRows;
	}
	
	/**
	 * Set the number of rows that will be in the display.
	 * 
	 * @param rows The number of rows to display.
	 */
	public void setRows(int rows)
	{
		fCellRows = rows;
	}
	
	/**
	 * Get the relative column offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @return Returns the cell column offset.
	 */
	public int getColumnOffset()
	{
		return fCellColumnOffset;
	}
	
	/**
	 * Set the relative column offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @param columnOffset The cell column offset to set.
	 */
	public void setColumnOffset(int columnOffset)
	{
		fCellColumnOffset = columnOffset;
	}
	
	/**
	 * Get the relative row offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @return Returns the cell row offset.
	 */
	public int getRowOffset()
	{
		return fCellRowOffset;
	}
	
	/**
	 * Set the relative row offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @param rowOffset The cell row offset to set.
	 */
	public void setRowOffset(int rowOffset)
	{
		fCellRowOffset = rowOffset;
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: TwinkleCellRenderer.java,v $
//  Revision 1.9  2006/04/27 18:51:52  chostetter_cvs
//  Organized imports
//
//  Revision 1.8  2006/02/14 20:08:01  tames
//  Added support for specifying a specific buffer base name to visualize. This is
//  needed if the BasisSet has more than one pixel bundle in it.
//
//  Revision 1.7  2005/10/11 03:03:30  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.6  2005/07/15 17:57:07  mn2g
//  updated to support the data buffer changes
//
//  Revision 1.5  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.4  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.3  2005/01/11 21:35:47  chostetter_cvs
//  Initial version
//
//  Revision 1.2  2004/12/18 06:39:03  tames
//  Fixed bug where renderer was not registering for change events from
//  the axis model.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.2  2004/12/14 21:45:30  tames
//  Updates to reflect using an external axis scale along with
//  some refactoring of interfaces.
//
//  Revision 1.1  2004/11/15 19:43:19  tames
//  Initial Version
//
//