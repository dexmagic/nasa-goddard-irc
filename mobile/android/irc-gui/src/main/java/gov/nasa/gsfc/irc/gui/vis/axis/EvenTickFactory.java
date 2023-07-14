//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/axis/EvenTickFactory.java,v 1.3 2005/11/07 22:17:25 tames Exp $
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

package gov.nasa.gsfc.irc.gui.vis.axis;

import java.util.LinkedList;
import java.util.List;



/**
 * This class will attempt to create a set of evenly positioned ticks that 
 * are an even multiple of 1.0 or 0.5 within the range 5.0 x 10^5 to 1.0 x 10^-5.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580 
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Date: 2005/11/07 22:17:25 $
 * @author 	Troy Ames
 */
public class EvenTickFactory implements TickFactory
{
	//  Integer vales for the possible labeling techniques. These are
	//  also offsets into the LABELING_TECH array.
	public static final int OFFSET_FROM_ZERO_TECH = 0;

	public static final int ACTUAL_VALUES_TECH = 1;

	//  String values for labeling techniques.
	public static final String[] LABELING_TECH = { "Offset from zero",
			"Actual values" };

	private static final int DEF_NUM_TICKS = 5;

	//  Labeling techinque
	private int fLabelingTech = ACTUAL_VALUES_TECH;

	//  Log scale
	private boolean fLogScale = false;

	/**
	 * Create a new tick factory .
	 */
	public EvenTickFactory()
	{
	}

	/**
	 * Create a set of evenly spaced ticks with the given axis model.
	 * 
	 * @param axis the model of the axis for these ticks
	 * @return an array of ticks
	 */
	public Tick[] createTicks(AxisModel axis)
	{
		List tickList = new LinkedList();
		double min = axis.getViewMinimum();
		double max = min + axis.getViewExtent();
		int numTicks = axis.getNumberOfTicks();
		
		if (numTicks > 0)
		{
			double offset = 0;
			double tickValue = 0;

			//  Calculate the distance between the tick values.
			double axisRange = max - min;
			//double tickSize = (axisRange) / (numTicks - 1);
			double tickSize = axisRange / numTicks;
			double normalTickSize = normalizeTickUnit(tickSize, fLargeTickUnits);
			
			if (numTicks < 4 && (normalTickSize * numTicks) > axisRange)
			{
				// Try using a smaller unit so that we have at least 3 ticks
				normalTickSize = normalizeTickUnit(tickSize, fSmallTickUnits);
			}

			//  Make the ticks. Start at the minimum and work our way up.
			tickValue = min - (min % normalTickSize);
			double endValue = min + axisRange + (normalTickSize / 2);
			
			if (min < 0)
			{
				tickValue -= normalTickSize;
			}
			
			while (tickValue <= max)
			{
				if (tickValue >= min)
				{
					tickList.add(new Tick(tickValue, 0));
				}
				
				tickValue += normalTickSize;
			}
		}

		return (Tick[]) tickList.toArray(new Tick[tickList.size()]);
	}

	// Structure for normalizing tick label values to minimize roundoff
	// errors.
	private static double[] fLargeTickUnits = 
	{
			0.00001D, 0.00005D,
			0.0001D, 0.0005D,
			0.001D, 0.005D,
			0.01D, 0.05D,
			0.1D, 0.5D,
			1.0D, 5.0D,
			10.0D, 50.0D,
			100.0D, 500.0D,
			1000.0D, 5000.0D,
			10000.0D, 50000.0D,
			100000.0D, 500000.0D
	};
	
	// Structure for normalizing tick label values to minimize roundoff
	// errors.
	private static double[] fSmallTickUnits = 
	{
			0.00001D, 0.00002D, 0.000025D, 0.00003D, 0.00004D, 0.00005D,
			0.0001D, 0.0002D, 0.00025D, 0.0003D, 0.0004D, 0.0005D,
			0.001D, 0.002D, 0.0025D, 0.003D, 0.004D, 0.005D,
			0.01D, 0.02D, 0.025D, 0.03D, 0.04D, 0.05D,
			0.1D, 0.2D, 0.25D, 0.3D, 0.4D, 0.5D,
			1.0D, 2.0D, 2.5D, 3.0D, 4.0D, 5.0D,
			10.0D, 20.0D, 25.0D, 30.0D, 40.0D, 50.0D,
			100.0D, 150.0D, 200.0D, 250.0D, 300.0D, 400.0D, 500.0D, 
			1000.0D, 2000.0D, 2500.0D, 3000.0D, 4000.0D, 5000.0D,
			10000.0D, 20000.0D, 25000.0D, 30000.0D, 40000.0D, 50000.0D,
			100000.0D, 200000.0D, 250000.0D, 300000.0D, 400000.0D, 500000.0D,
	};
	
	/**
	 * Normalizes a tick unit based on the requested magnitude.
	 * 
	 * @param tickSize the size (magnitude) of a tick.
	 * @return a normalized tick value
	 */
	private double normalizeTickUnit(double tickSize, double[] tickUnits)
	{
		double normalizedTickSize = 1.0D;
		
		for (int i=0; i < tickUnits.length; i++)
		{
			normalizedTickSize = tickUnits[i];
			
			if (tickSize <= normalizedTickSize)
			{
				break;
			}
		}
		
		return normalizedTickSize;
	}

	/**
	 * Set whether or not the ticks should be scaled logarithmically.
	 * 
	 * @param logScale whether or not to scale the ticks logarithmicallly
	 */
	public void setLogScale(boolean logScale)
	{
		fLogScale = logScale;
	}

	/**
	 * Get whether or not the ticks are scaled logarithmically.
	 * 
	 * @return whether or not the ticks are scaled logarithmicallly
	 */
	public boolean isLogScale()
	{
		return fLogScale;
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: EvenTickFactory.java,v $
//  Revision 1.3  2005/11/07 22:17:25  tames
//  Added ability to create finer resolution ticks for small charts.
//
//  Revision 1.2  2005/10/11 03:00:17  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//
