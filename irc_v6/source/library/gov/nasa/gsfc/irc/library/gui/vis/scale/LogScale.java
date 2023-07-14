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

package gov.nasa.gsfc.irc.library.gui.vis.scale;

import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;

/**
 * This AxisScale provides the means to convert a value to a log scale and 
 * back. This is independent from screen resolution or axis size because 
 * the scaled result is in terms of a ratio which the client then translates
 * into screen coordinates. The log base is by default 10.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 03:15:00 $
 * @author 	Troy Ames
 */
public class LogScale implements AxisScale
{
	// Cached values to reduce unnecessary calculations.
	private double fLastMinimum = 0.0;
	private double fLastLogMinimim = 0.0;
	private double fLastMaximum = 0.0;
	private double fLastLogMaximum = 0.0;
	
	// the log base for this scale
	private double fBase = 10.0;
	private double fLogBase = Math.log(fBase);
	
	/**
	 * Construct a LogScale with base 10.
	 */
	public LogScale()
	{
		super();
	}

	/**
	 * Construct a LogScale with the given base. Base must be greater than 0.
	 */
	public LogScale(double base)
	{
		super();
		setBase(base);
	}

	/**
	 * Applies a log scale to the value and returns a ratio of where the
	 * given value is relative to the view minimum and in units the size of
	 * the view extent.
	 * 
	 * @param value the value to scale
	 * @param viewMin the minimum visible view value on the axis
	 * @param extent the extent of the visible view on the axis
	 * @return a ratio relative to the view value and extent
	 */
	public double getScaleRatio(double value, double viewMin, double extent)
	{
		double logMin = fLastLogMinimim;

		// Convert all the variables to a log base before calculating the ratio.
		
		if (viewMin != fLastMinimum)
		{
			// Convert minimum to a log based minimum.
			logMin = log(viewMin);
			
			// Cache values for next call
			fLastMinimum = viewMin;
			fLastLogMinimim = logMin;
		}

		double logMax = fLastLogMaximum;
		double maximum = viewMin + extent;

		if (maximum != fLastMaximum)
		{
			// Convert maximum to a log based maximum.
			logMax = log(maximum);
			
			// Cache values for next call
			fLastMaximum = maximum;
			fLastLogMaximum = logMax;
		}

		// Convert value to a log based value.
		double logValue = log(value);		
		double logRange = logMax - logMin;
		double scaledRatio = 0.0;
		
		// This is now a linear calculation.
		if (logRange > 0.0)
		{
			scaledRatio = (logValue - logMin) / logRange;
		}
		
		return scaledRatio;
	}

	/**
	 * Determines for a log scale what value would result in the given ratio
	 * relative to the view minimum and in units the size of
	 * the view extent. 
	 * This is the inverse of <code>getScaleRatio</code> and is necessary when 
	 * translating from a screen relative coordinate to an axis domain value.
	 * 
	 * @param ratio a ratio relative to the view value and extent
	 * @param viewMin the minimum visible view value on the axis
	 * @param extent the extent of the visible view on the axis
	 * @return a value when scaled results in the given ratio
	 */
	public double getScaleValue(double ratio, double viewMin, double extent)
	{
		double logMin = fLastLogMinimim;

		// Convert all the variables to a log base before calculating the value.
		
		if (viewMin != fLastMinimum)
		{
			// Convert minimum to a log based minimum.
			logMin = log(viewMin);
			
			// Cache values for next call
			fLastMinimum = viewMin;
			fLastLogMinimim = logMin;
		}

		double logMax = fLastLogMaximum;
		double maximum = viewMin + extent;
		
		if (maximum != fLastMaximum)
		{
			// Convert maximum to a log based maximum.
			logMax = log(maximum);
			
			// Cache values for next call
			fLastMaximum = maximum;
			fLastLogMaximum = logMax;
		}

		double logRange = logMax - logMin;

		// This is now a linear calculation.
		double logValue = (logRange * ratio) + logMin;
		double value = 0.0;
		
		// Convert log value back to a normal value.
		if (logValue > 0.0)
		{
			value = Math.pow(fBase, logValue);
		}
		else
		{
			value = Math.pow(fBase, Math.sqrt(logValue * logValue)) * -1.0;
		}
		
		return value;
	}
	
	/**
	 * Calculates the log of the given value.
	 * 
	 * @param value the value to take the log of.
	 * @return the log result
	 */
	private double log(double value)
	{
		double result = 0.0;
		
		// Convert value to a log based value.
		if (value > 1.0)
		{
			result = Math.log(value) / fLogBase;
		}
		else if (value < -1.0)
		{
			result = Math.log(Math.sqrt(value * value)) / fLogBase * -1.0;
		}
		
		return result;
	}
	
	/**
	 * Get the base for this log scale.
	 * @return Returns the base.
	 */
	public double getBase()
	{
		return fBase;
	}
	
	/**
	 * Set the base for this log scale. Base must be greater than 0;
	 * 
	 * @param base The base to set.
	 */
	public void setBase(double base)
	{
		if (base > 0.0)
		{
			fBase = base;
			fLogBase = Math.log(fBase);	
		}
		else
		{
			throw new IllegalArgumentException("Base must be greater than 0");
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: LogScale.java,v $
//  Revision 1.3  2005/10/11 03:15:00  tames
//  Reflects changes to the AxisScale interface.
//
//  Revision 1.2  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/14 21:47:34  tames
//  Initial version.
//
//