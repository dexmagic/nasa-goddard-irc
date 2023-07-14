//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/plot/XyzPlotRenderer.java,v 1.4 2006/08/10 16:13:51 smaher_cvs Exp $
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

package gov.nasa.gsfc.irc.library.gui.vis.plot;

import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.AbstractVisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;
import gov.nasa.gsfc.irc.gui.vis.axis.DefaultAxisModel;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelModel;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;
import gov.nasa.gsfc.irc.gui.vis.channel.DefaultChannelModel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * Experimental  
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/10 16:13:51 $
 * @author Steve Maher
 */
public class XyzPlotRenderer extends AbstractVisRenderer
{
	private static final String CLASS_NAME = XyzPlotRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Stroke definitions
	private static final float LINE_WIDTH = 1.0f;
	private static final BasicStroke BASIC_STROKE = new BasicStroke(LINE_WIDTH);

	//  Property values
	private boolean fPointsVisible = false;
	private boolean fLinesVisible = true;
	private float fPointRadius = 2f;

	// Reused graphics objects.
	private Ellipse2D.Float fDrawnPoint = new Ellipse2D.Float();
	private GeneralPath fDrawnPath = new GeneralPath();

	//  Point position information, including the actual position of the
	//  drawn point on the canvas.
	private float fXDrawnArray[]=null;
	private float fXDrawn = 0;
	private float fYDrawn = 0;
	private float fYDrawnLast = 0;
	private boolean fFirstTracePoint = true;

	//  Trace policies
	private boolean fIsDiscrete = false;
	private boolean fIsSynthesized = false;

	private boolean fStructureChanged = true;

	// Axis info.
	private AxisModel fXAxisModel = null;
	private AxisModel fYAxisModel = null;
	
	private double fPreviousFirstBasisBufferValue;
	private double fPreviousLastBasisBufferValue;	
    
	private int fNumBuffers = 0;
	
	// Data selection fields
	private ChannelModel fChannelModel;
	private String fXAxisChannel = "time";
	private LinkedHashSet fYAxisChannels = new LinkedHashSet(5);

	/**
	 * Holds regular expressions used to specify Y axis channels
	 */
	private ArrayList fYAxisChannelRegularExpressions = new ArrayList();
	
	private boolean fRenderHintsSet=false;
    private OpenGlPlot fXyzPlotRenderer;
	
	/**
	 * Create a new renderer with default axis models.
	 */
	public XyzPlotRenderer()
	{
		this(null, null);
	}

	/**
	 * Create a new renderer with the given axis models.
	 * 
	 * @param xAxis the X axis model
	 * @param yAxis the Y axis model
	 */
	public XyzPlotRenderer(AxisModel xAxis, AxisModel yAxis)
	{
		this(xAxis, yAxis, null);
	}

	/**
	 * Create a new renderer with the given axis and channel model.
	 * 
	 * @param xAxis the X axis model
	 * @param yAxis the Y axis model
	 * @param channels the channel model for this renderer
	 */
	public XyzPlotRenderer(AxisModel xAxis, AxisModel yAxis, ChannelModel channels)
	{
		super();

		fXAxisModel = xAxis;
		fYAxisModel = yAxis;
		fChannelModel = channels;
		
		if (fXAxisModel == null)
		{
			fXAxisModel = new DefaultAxisModel();
		}
		
		
		if (fYAxisModel == null)
		{
			fYAxisModel = new DefaultAxisModel();
		}

		if (fChannelModel == null)
		{
			fChannelModel = new DefaultChannelModel();
		}

		fXAxisModel.addChangeListener(this);
		fYAxisModel.addChangeListener(this);
        
        fXyzPlotRenderer = new OpenGlPlot("3D Plots");
	}

	private void printGraphics2DInfo(Graphics2D g2d) {
		System.out.println("Antialiasing key: "+g2d.getRenderingHints());
	}
	
	private void setRenderingHints(Graphics2D g2d) {
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderHints.add(new RenderingHints(RenderingHints.KEY_RENDERING,			  		
			    RenderingHints.VALUE_RENDER_SPEED));

		g2d.setRenderingHints(renderHints);
		
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
		int firstSampleIndex = 0;
		int lastSampleIndex=0;

		if(!fRenderHintsSet) {
			setRenderingHints(g2d);
			fRenderHintsSet=true;
			printGraphics2DInfo(g2d);
		}
		
		if (numSamplesIn > 0)
		{
			Color savedColor = g2d.getColor();

			BasisBundleId bundleId = basisSet.getBasisBundleId();
			//JComponent parent = getParent();
			
			DataBuffer xBuffer = basisSet.getDataBuffer(fXAxisChannel);
			
			if (xBuffer == null)
			{
				xBuffer = basisSet.getBasisBuffer();
			}
			
			//System.out.println("Basis: " + basisSet.getBasisBufferDescriptor());
			//System.out.println("Basis2: " + basisBuffer.getName());
			


			double xValue = 0;
			double oldXValue = 0;

			//  Axis model info
			final double minX = fXAxisModel.getViewMinimum();
			final double maxX = minX + fXAxisModel.getViewExtent();
			final double minY = fYAxisModel.getViewMinimum();
			final double maxY = minY + fYAxisModel.getViewExtent();

			AxisScale xScale = fXAxisModel.getAxisScale();
			AxisScale yScale = fYAxisModel.getAxisScale();
			
			// Drawing canvas bounds information
			final double canvasLeft = rect.getMinX();
			final double canvasRight = rect.getMaxX();
			final double canvasTop = rect.getMinY();
			final double canvasBottom = rect.getMaxY();

			//  Canvas width and height
			final double canvasHeight = rect.getHeight();
			final double canvasWidth = rect.getWidth();

			//  Set dimensions of circular point.
			fDrawnPoint.width = fPointRadius * 2;
			fDrawnPoint.height = fDrawnPoint.width;

			// find what, if any, data from this basis set is visible on 
			// the graph - note this just checks the time axis as that's easy
			
			// is any data visible?
			if(xBuffer.getMinValue()>maxX || (xBuffer.getMaxValue()<minX)) {
				return;
			}
			
			// some data is available if we get here.
			firstSampleIndex=-1;
			lastSampleIndex=-1;
			for(int loop=0; loop<numSamplesIn; loop++) {
				double timePoint = xBuffer.getAsDouble(loop);
				if( (firstSampleIndex==-1) &&
					timePoint>=minX) {
					firstSampleIndex=loop;
					
					// only bother looking at the rest of the time points if 
					// the dataSet has more data than the graph is viewing..
					if(xBuffer.getMaxValue()>maxX) {
						lastSampleIndex=numSamplesIn;
						break;
					}
				}
				if( (lastSampleIndex==-1) &&
						timePoint>maxX) {
					lastSampleIndex=loop-1;
					// no need to check the rest of the time points
					break;
				}
			}
			// if there wasn't a max point, just use all of the data.
			if(lastSampleIndex==-1) {
				lastSampleIndex=numSamplesIn;
			}
			
			// if we get here then there is at least some data to plot.
			// by default below all channels use the SAME X point over and over again
			// the transformation should only be done ONCE..
			if(fXDrawnArray==null || fXDrawnArray.length<numSamplesIn) {
				fXDrawnArray=new float[numSamplesIn];
			}
			

            

			
			ArrayList dataBufferNames = new ArrayList(fYAxisChannels);
			for (Iterator regexes = fYAxisChannelRegularExpressions.iterator(); regexes.hasNext();)
			{
				String regex = (String) regexes.next();
				Set regexDataBufferNames = basisSet.getDataBufferNames(regex);
				dataBufferNames.addAll(regexDataBufferNames);
				for (Iterator bufferName = regexDataBufferNames.iterator(); bufferName
						.hasNext();)
				{
					String dataBufferName = (String) bufferName.next();
					// This checks for duplicates, so we can just keep throwing the 
					// new data buffers in.  TODO Performance problem?
					fChannelModel.addChannel(dataBufferName);
				}
			}

            OpenGlPlot.XyPlotData plotData = 
                new OpenGlPlot.XyPlotData(lastSampleIndex - firstSampleIndex, 
                        dataBufferNames.size(),
                        xBuffer.getAsDouble(xBuffer.getSize() - 1) - xBuffer.getAsDouble(0),
                        basisSet.getBasisBuffer().getUnit());
            
            int xCoordCardinal = 0;
            // precalculate all of the interesting X points
            for(int loop=firstSampleIndex;loop<lastSampleIndex;loop++, xCoordCardinal++) {
                xValue = xBuffer.getAsDouble(loop);
                
                plotData.fBasisBufferValues[xCoordCardinal] = xValue;
                
                double xRatio = xScale.getScaleRatio(xValue, minX, maxX-minX);
                //  Translate and scale the point to the view coordinates.
                // TODO should we check if the results are INF or NaN?
                fXDrawn = (float) ((canvasWidth * xRatio) + canvasLeft);
                fXDrawnArray[loop]=fXDrawn;
                plotData.fXCoords[xCoordCardinal] = fXDrawn;
            }
            
            
            int bufferCardinal = 0;
			for (Iterator iter = dataBufferNames.iterator(); iter.hasNext(); bufferCardinal++)
			{
				String dataBufferName = (String) iter.next();
					
				oldXValue = 0;

				DataBuffer dataBuffer = 
					basisSet.getDataBuffer(dataBufferName);
				
				if (dataBuffer != null)
				{
					ChannelRenderInfo channelInfo = 
						(ChannelRenderInfo) fChannelModel.getRenderInfo(
							dataBuffer.getName());
	
					//  Set the drawing color for the trace. Reset the path.
					//  First point of the trace.
					fFirstTracePoint = true;
					g2d.setColor(channelInfo.getColor());
                    plotData.fTraces[bufferCardinal].fColor = channelInfo.getColor();
                    plotData.fTraces[bufferCardinal].fName = new String(dataBufferName);
					fDrawnPath.reset();
	
                    int yCoordCardinal = 0;
					for (int j = firstSampleIndex; j < lastSampleIndex; j++, yCoordCardinal++)
					{						
						double yValue = dataBuffer.getAsDouble(j);
						double yRatio = yScale.getScaleRatio(yValue, minY, maxY-minY);
						
						//  Translate and scale the point to the view coordinates.
						// TODO should we check if the results are INF or NaN?
						fXDrawn = fXDrawnArray[j];
						fYDrawn = (float) (canvasBottom - (canvasHeight * yRatio));
                        plotData.fTraces[bufferCardinal].fYCoords[yCoordCardinal] = fYDrawn;
                        
						//  If needed, draw the point, adjusting for the size
						//  of the point itself.
						if (fPointsVisible)
						{
							fDrawnPoint.x = fXDrawn - fPointRadius;
							fDrawnPoint.y = fYDrawn - fPointRadius;
							g2d.fill(fDrawnPoint);
						}

						//  The first point is the starting point in the path.
						if (fFirstTracePoint)
						{
							fDrawnPath.moveTo(fXDrawn, fYDrawn);
							fFirstTracePoint = false;
						}
						else //  Others are just pieces of the path
						{
							//  For discrete traces, we need to drawn a
							//  line aligned vertically with new point. This
							//  creates what seem like vertical transitions
							//  between the values.
							if (fIsDiscrete)
							{
								fDrawnPath.lineTo(fXDrawn, fYDrawnLast);
							}

							//  Draw the line.
							fDrawnPath.lineTo(fXDrawn, fYDrawn);
						}

						//  Save this in case we need it for discrete values.
						fYDrawnLast = fYDrawn;
	
						oldXValue = xValue;
					}
	
					//  Draw the line/path, if needed.
					if (fLinesVisible)
					{
						//g2d.setStroke(fStrokes[i]); // TBD.
						//g2d.setStroke(BASIC_STROKE);
						g2d.draw(fDrawnPath);
					}
				}
			}

            if (fXyzPlotRenderer != null)
            {
                plotData.fXAxisModel = fXAxisModel;
                plotData.fYAxisModel = fYAxisModel;   
                plotData.fRect2D = rect;
                
                double firstBasisValue = basisSet.getBasisBuffer().getAsDouble(0);
                if (true || firstBasisValue != fPreviousFirstBasisBufferValue)
                {   
                    fXyzPlotRenderer.receiveNewPlotData(plotData);                    
                }
                else
                {
                    System.out.println("Skipping duplicated basis set");
                }
                fPreviousFirstBasisBufferValue = firstBasisValue;     
                double lastBasisValue = basisSet.getBasisBuffer().getAsDouble(numSamplesIn - 1);
              
                System.out.println("PPPPP  timeDurationInBasisSet=" + (lastBasisValue - firstBasisValue) + 
                		" timeSinceLastBasisSet=" + (firstBasisValue - fPreviousLastBasisBufferValue) +
                		" basisSetSize=" + numSamplesIn + ", firstTime=" + firstBasisValue);
                
                fPreviousLastBasisBufferValue = lastBasisValue;                  

                
            }
			// Restore graphics settings
			g2d.setColor(savedColor);
		}		
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
	 * Get the X axis channel to use as the basis. This method may be replaced 
	 * in the future when channel selection is formalized.
	 * 
	 * @return the X axis channel.
	 */
	public String getXAxisChannel()
	{
		return fXAxisChannel;
	}
	
	/**
	 * Set the X axis channel to use as the basis. This method may be replaced 
	 * in the future when channel selection is formalized.
	 * 
	 * @param channelName The X axis channel.
	 */
	public void setXAxisChannel(String channelName)
	{
		fXAxisChannel = channelName;
	}
	
	/**
	 * Get the Y axis channels to plot. This method may be replaced in the 
	 * future when channel selection is formalized.
	 * 
	 * @return Returns the selected channels.
	 */
	public LinkedHashSet getYAxisChannels()
	{
		return fYAxisChannels;
	}
	
	/**
	 * Set the Y axis channels to plot. This can be a "|" separated list
	 * of channel names. This method may be replaced in the future when
	 * channel selection is formalized.  Names may be regular expressions
	 * surrounded by "{" and "}".
	 * 
	 * @param channelNames The channels to plot.
	 */
	public void setYAxisChannels(String channelNames)
	{
		fYAxisChannels.clear();
		fYAxisChannelRegularExpressions.clear();
		
		//fChannelModel.clear();
		
		if (channelNames != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(channelNames, "|");
			
			int numNames = tokenizer.countTokens();
			
			for (int i = 0; i < numNames; i++)
			{
				String bufferName = tokenizer.nextToken().trim();
				
				if (bufferName.startsWith(BasisRequest.REGEX_DELIMETER_START) && 
						bufferName.endsWith(BasisRequest.REGEX_DELIMETER_END))
				{
//					fYAxisChannelRegularExpressions.add(Pattern.compile(bufferName.substring(1, bufferName.length() - 1)));
					fYAxisChannelRegularExpressions.add(bufferName.substring(1, bufferName.length() - 1));					
				}
				else
				{
					fYAxisChannels.add(bufferName);
					fChannelModel.addChannel(bufferName);
				}
			}
		}
	}
	
	/**
	 * Set whether or not the points are displayed on the chart.
	 * 
	 * @param pointsVisible whether or not points are visible
	 */
	public final void setPointsVisible(boolean pointsVisible)
	{
		fPointsVisible = pointsVisible;
	}

	/**
	 * Get whether or not the points are displayed on the chart.
	 * 
	 * @return whether or not points are visible
	 */
	public boolean getPointsVisible()
	{
		return fPointsVisible;
	}

	/**
	 * Set whether or not the line segments between points are displayed on the
	 * chart.
	 * 
	 * @param linesVisible whether or not lines are visible
	 */
	public final void setLinesVisible(boolean linesVisible)
	{
		fLinesVisible = linesVisible;
	}

	/**
	 * Get whether or not the lines segments between points are displayed on the
	 * chart.
	 * 
	 * @return whether or not lines are visible
	 */
	public boolean getLinesVisible()
	{
		return fLinesVisible;
	}

	/**
	 * Set the radius of any points drawn to the chart.
	 * 
	 * @param pointRadius the point radius
	 */
	public final void setPointRadius(float pointRadius)
	{
		fPointRadius = pointRadius;
	}

	/**
	 * Get the radius of any points drawn to the chart.
	 * 
	 * @return the point radius
	 */
	public float getPointRadius()
	{
		return fPointRadius;
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: XyzPlotRenderer.java,v $
//  Revision 1.4  2006/08/10 16:13:51  smaher_cvs
//  Debug for "missing" data
//
//  Revision 1.3  2006/08/01 01:19:46  smaher_cvs
//  *** empty log message ***
//
//  Revision 1.2  2006/07/28 20:06:25  smaher_cvs
//  *** empty log message ***
//
//  Revision 1.1  2006/07/28 11:03:30  smaher_cvs
//  Checkpointing 3D plotting
//
//  Revision 1.12  2006/05/23 16:00:35  smaher_cvs
//  Added ability to specify channels using regular expressions.
//
//  Revision 1.11  2005/10/11 03:13:05  tames
//  Reflects changes to the AxisModel interface.
//
//  Revision 1.10  2005/08/19 16:09:18  tames_cvs
//  Commented out the clear when adding a new channel.
//
//  Revision 1.9  2005/08/12 21:19:47  tames_cvs
//  Modified the addChannels method to clear any existing channels.
//
//  Revision 1.8  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.7  2005/03/15 17:19:32  chostetter_cvs
//  Made DataBuffer an interface, organized imports
//
//  Revision 1.6  2005/03/07 20:08:02  mn2g
//  Removed timing printout
//
//  Revision 1.5  2005/02/23 22:59:46  mn2g
//  a possibly useless test to see if setting rendering hints helps with CPU utiliziation
//
//  Revision 1.4  2005/02/22 11:19:45  mn2g
//  made a few of the obvious variables final in a vain attempt to get bloody
//  java to pretent to optimize this code.
//
//  Revision 1.3  2005/02/22 10:50:23  mn2g
//  some optimizations for drawing stripcharts.
//
//  namely don't bother processing points that are off scale in the X axis
//
//  plus all channels share the same set of X points, so this precalculates those values and caches them
//
//  Revision 1.2  2004/12/18 03:56:17  tames
//  Fixed bug where renderer was not registering for change events from
//  the Y axis model.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.4  2004/12/14 21:45:30  tames
//  Updates to reflect using an external axis scale along with
//  some refactoring of interfaces.
//
//  Revision 1.3  2004/11/19 04:26:19  tames
//  Now restores the state of the graphics context to be tha same as received.
//
//  Revision 1.2  2004/11/15 19:45:44  tames
//  Removed obsolete autoscale code.
//
//  Revision 1.1  2004/11/08 23:14:35  tames
//  Initial Version
//
//