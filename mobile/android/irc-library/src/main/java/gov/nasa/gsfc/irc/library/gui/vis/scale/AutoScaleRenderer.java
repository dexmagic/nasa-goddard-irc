//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/scale/AutoScaleRenderer.java,v 1.7 2005/10/11 03:14:05 tames Exp $
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

package gov.nasa.gsfc.irc.library.gui.vis.scale;

import java.util.Iterator;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.AbstractAutoScaleRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;


/**
 * This class auto scales an axis to the absolute minimum and maximum value 
 * based on a specific channel or over all buffers contained in a 
 * DataSet. 
 * 
 * Instances of this class should be placed in the render chain before any 
 * renderer that is dependent on the same axis model. 
 * This class does not have any visual components and does not draw to the 
 * graphics context.
 * 
 * <P>
 * The table below gives the auto scaling modes supported by this class.
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Mode</th>
 *      <th>Default</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>One Time Scale</td><td>disabled</td>
 *      <td align="left">Forces a one time auto scale. This may be necessary 
 *      since the preferred model settings may not be known until data is
 *      received for the first time. This class will auto scale based on
 *      the data received and then disable auto scaling ignoring all mode 
 *      settings below.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>Reset Scale</td><td>disabled</td>
 *      <td align="left">Same as One Time Scale above except auto scaling
 *      is not disabled. The mode settings below will be used in future
 *      auto scaling.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>Range Pinned</td><td>disabled</td>
 *      <td align="left">Maintains the same range size, but updates the 
 *      range position. This results in a fixed size window on the data</td>
 *  </tr>
 *  <tr align="center">
 *      <td>Range Contraction</td><td>enabled</td>
 *      <td align="left">The range size will expand or contract based on the 
 *      data. This will show all the current data adjusting the scale to fill the 
 *      available space.
 *      </td>
 *  </tr>
 *  <tr align="center">
 *      <td>Default</td><td>-</td>
 *      <td align="left">The range size will expand if needed based on the 
 *      data. This will show all the current data scaled to reflect the largest 
 *      samples even if they are not in the current set.
 *      </td>
 *  </tr>
 *  </table>
 *  </center>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 03:14:05 $
 * @author	Troy Ames
 */
public class AutoScaleRenderer extends AbstractAutoScaleRenderer
{
	private static final String CLASS_NAME = AutoScaleRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
    private String fScaleChannel = null;
 
    private boolean fRangePinned = false;
    private boolean fRangeContractionEnabled = true;
    private boolean fResetScaleEnabled = false;
    private boolean fOneTimeScaleEnabled = false;

    /**
	 * Create a new auto scaler for the given axis model.
	 * 
	 * @param model the axis model to autoscale.
	 */
	public AutoScaleRenderer(AxisModel model)
	{
		super(model);
	}

	/**
	 * Update the axis based on the data in the given DataSet.
	 * 
	 * @param model	the model to update.
	 * @param dataSet the current DataSet.
	 */
	public void autoScaleAxis(AxisModel model, DataSet dataSet)
	{	
		Iterator basisSets = dataSet.getBasisSets().iterator();
		double maxInBundle = Double.NEGATIVE_INFINITY;
		double minInBundle = Double.POSITIVE_INFINITY;
		int maxValueIndex = 0;
		int minValueIndex = 0;
		boolean axisUpdateNeeded = false;
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			if (fScaleChannel != null)
			{
				// Scale based on the provided channel.
				String basisName = basisSet.getBasisBufferDescriptor().getName();
				
				// First check if channel is the basis
				if (fScaleChannel.equals(basisName))
				{
					double first = basisSet.getFirstBasisValue();
					double last = basisSet.getLastBasisValue();
					
					if (first > last)
					{
						maxInBundle = first;
						minInBundle = last;
						maxValueIndex = 0;
						minValueIndex = basisSet.getSize() - 1;
					}
					else
					{
						maxInBundle = last;
						minInBundle = first;
						maxValueIndex = basisSet.getSize() - 1;
						minValueIndex = 0;
					}
					
					axisUpdateNeeded = true;
				}
				else 
				{
					// See if channel is a data buffer
					DataBuffer dataBuffer = basisSet.getDataBuffer(fScaleChannel);
					
					if (dataBuffer != null)
					{
						double maxInBuffer = dataBuffer.getMaxValue();

						if (maxInBuffer > maxInBundle)
						{
							maxInBundle = maxInBuffer;
							maxValueIndex = dataBuffer.getMaxValueIndex();
						}

						double minInBuffer = dataBuffer.getMinValue();

						if (minInBuffer < minInBundle)
						{
							minInBundle = minInBuffer;
							minValueIndex = dataBuffer.getMinValueIndex();
						}

						axisUpdateNeeded = true;
					}
				}
			}
			else
			{
				// Scale based on all the data channels
				// Find the min and max of all the buffers.
						
				for (Iterator dataBuffers = basisSet.getDataBuffers(); 
					dataBuffers.hasNext();)
				{
					DataBuffer dataBuffer = (DataBuffer) dataBuffers.next();
					double maxInBuffer = dataBuffer.getMaxValue();
	
					if (maxInBuffer > maxInBundle)
					{
						maxInBundle = maxInBuffer;
						maxValueIndex = dataBuffer.getMaxValueIndex();
					}
	
					double minInBuffer = dataBuffer.getMinValue();
	
					if (minInBuffer < minInBundle)
					{
						minInBundle = minInBuffer;
						minValueIndex = dataBuffer.getMinValueIndex();
					}
					
					axisUpdateNeeded = true;
				}		
			}
		}
		
		if (axisUpdateNeeded)
		{
			// TODO synchronize this on the model
			double viewMin = model.getViewMinimum();
			double viewExtent = model.getViewExtent();
			//double viewMax = viewMin + viewExtent;
			double axisMax = model.getMaximum();
			double axisMin = model.getMinimum();
			double axisExtent = axisMax - axisMin;
			
			if (fOneTimeScaleEnabled)
			{
				// Force a one time rescale of the axis.
				fOneTimeScaleEnabled = false;
				viewMin = minInBundle;
				viewExtent = maxInBundle - minInBundle;
				axisMax = maxInBundle;
				axisMin = minInBundle;
				getAxisModel().setAutoScale(false);
			}
			else if (fResetScaleEnabled)
			{
				// Force an initial rescale of the axis.
				fResetScaleEnabled = false;
				viewExtent = maxInBundle - minInBundle;
				viewMin = minInBundle;
				axisMax = maxInBundle;
				axisMin = minInBundle;
			}
			else if (fRangePinned)
			{
				double viewOffset = axisMax - viewMin;
				viewMin = maxInBundle - viewOffset;
//				// Bias the auto scale toward the newest samples
//				if (maxValueIndex > minValueIndex)
//				{
//					viewMax = maxInBundle;
//					viewMin = viewMax - viewExtent;
//				}
//				else
//				{
//					viewMin = minInBundle;
//					viewMax = viewMin + viewExtent;
//				}

				axisMax = maxInBundle;
				axisMin = minInBundle;
			}
			else if (fRangeContractionEnabled)
			{
				// Absolute axis rescale
				viewExtent = maxInBundle - minInBundle;
				viewMin = minInBundle;
			}
			else
			{
				// Only expand the range of the axis
				
				// Change min if it is to high
				if (minInBundle < viewMin)
				{
					viewMin = minInBundle;
					axisMin = minInBundle;
				}

				// Change max if it is to low
				if (maxInBundle > viewMin + viewExtent)
				{
					axisMax = maxInBundle;
					viewExtent = maxInBundle - minInBundle;
				}

			}
			
			model.setRangeProperties(
				viewMin, viewExtent, axisMin, axisMax, false);
		}
	}
	
	/**
	 * Get the channel that is currently used to scale the axis.
	 * 
	 * @return Returns the channel name.
	 */
	public String getScaleChannel()
	{
		return fScaleChannel;
	}
	
	/**
	 * Set the channel to scale the axis by.
	 * 
	 * @param channelName The channel name.
	 */
	public void setScaleChannel(String channelName)
	{
		fScaleChannel = channelName;
	}
	
	/**
	 * @return Returns the rangePinned.
	 */
	public boolean isRangePinned()
	{
		return fRangePinned;
	}
	
	/**
	 * @param rangePinned The rangePinned to set.
	 */
	public void setRangePinned(boolean rangePinned)
	{
		fRangePinned = rangePinned;
	}
	
	/**
	 * @return Returns the rangeContractionEnabled.
	 */
	public boolean isRangeContractionEnabled()
	{
		return fRangeContractionEnabled;
	}
	
	/**
	 * @param rangeContractionEnabled The rangeContractionEnabled to set.
	 */
	public void setRangeContractionEnabled(boolean rangeContractionEnabled)
	{
		fRangeContractionEnabled = rangeContractionEnabled;
	}
	
	/**
	 * @return Returns the resetScaleEnabled.
	 */
	public boolean isResetScaleEnabled()
	{
		return fResetScaleEnabled;
	}
	
	/**
	 * @param resetScaleEnabled The resetScaleEnabled to set.
	 */
	public void setResetScaleEnabled(boolean resetScaleEnabled)
	{
		fResetScaleEnabled = resetScaleEnabled;
	}

	/**
	 * @return Returns the oneTimeScaleEnabled.
	 */
	public boolean isOneTimeScaleEnabled()
	{
		return fOneTimeScaleEnabled;
	}
	
	/**
	 * @param oneTimeScaleEnabled The oneTimeScaleEnabled to set.
	 */
	public void setOneTimeScaleEnabled(boolean oneTimeScaleEnabled)
	{
		fOneTimeScaleEnabled = oneTimeScaleEnabled;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AutoScaleRenderer.java,v $
//  Revision 1.7  2005/10/11 03:14:05  tames
//  Reflects changes to the AxisModel interface.
//
//  Revision 1.6  2005/09/23 20:42:41  tames
//  JavaDoc changes only.
//
//  Revision 1.5  2005/07/15 19:44:11  tames
//  Updated to reflect DataBuffer changes.
//
//  Revision 1.4  2005/03/15 16:13:07  tames
//  Added a one time rescale option to this class.
//
//  Revision 1.3  2005/03/15 07:52:13  tames
//  Fixed one time rescale to disable future rescaling.
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
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