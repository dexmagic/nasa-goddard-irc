//=== File Prolog ============================================================
//
// $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/processors/TerminalPlotterProcessor.java,v 1.14 2006/05/31 13:05:22 smaher_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: TerminalPlotterProcessor.java,v $
// Revision 1.14  2006/05/31 13:05:22  smaher_cvs
// Name change: UniformSampleRate->UniformSampleInterval
//
// Revision 1.13  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.12  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.11  2005/07/15 19:21:35  chostetter_cvs
// Adjusted for changes in DataBuffer
//
// Revision 1.10  2005/03/17 00:23:38  chostetter_cvs
// Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
// Revision 1.9  2004/09/14 20:12:13  chostetter_cvs
// Units now specified as Strings in lieu of better scheme later
//
// Revision 1.8  2004/07/24 02:46:11  chostetter_cvs
// Added statistics calculations to DataBuffers, renamed some classes
//
// Revision 1.7  2004/07/22 22:30:36  chostetter_cvs
// Tweak
//
// Revision 1.6  2004/07/22 22:28:10  chostetter_cvs
// Tweaks
//
// Revision 1.5  2004/07/22 22:22:19  chostetter_cvs
// Plot position mins out at 0
//
// Revision 1.4  2004/07/22 22:15:11  chostetter_cvs
// Created Algorithm versions of library Processors
//
// Revision 1.3  2004/07/22 16:28:03  chostetter_cvs
// Various tweaks
//
// Revision 1.2  2004/07/21 14:26:15  chostetter_cvs
// Various architectural and event-passing revisions
//
// Revision 1.1  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.14  2004/07/16 21:35:24  chostetter_cvs
// Work on declaring uniform sample rate of data
//
// Revision 1.13  2004/07/16 04:48:50  chostetter_cvs
// Fixed BasisSet period calculation error
//
// Revision 1.12  2004/07/15 21:33:35  chostetter_cvs
// More testing tweaks
//
// Revision 1.11  2004/07/15 18:27:11  chostetter_cvs
// Add rich set of firePropertyChange event methods  to Components
//
// Revision 1.10  2004/07/15 17:48:55  chostetter_cvs
// ComponentManager, property change work
//
// Revision 1.9  2004/07/15 04:26:43  chostetter_cvs
// Debugged initial skip, boundary condition on free
//
// Revision 1.8  2004/07/14 22:24:53  chostetter_cvs
// More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//
// Revision 1.7  2004/07/14 00:33:49  chostetter_cvs
// More Algorithm, data testing. Fixed slice bug.
//
// Revision 1.6  2004/07/13 18:52:50  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.5  2004/07/12 19:31:01  chostetter_cvs
// Further tweaks
//
// Revision 1.4  2004/07/12 19:04:45  chostetter_cvs
// Added ability to find BasisBundleId, Components by their fully-qualified name
//
// Revision 1.3  2004/07/12 14:31:12  chostetter_cvs
// TerminalPlot tweaks
//
// Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.1  2004/07/12 13:59:49  chostetter_cvs
// Renamed AsciiPlotter to TerminalPlotterProcessor
//
// Revision 1.4  2004/07/12 13:49:16  chostetter_cvs
// More Algorithm testing
//
// Revision 1.3  2004/07/11 21:39:03  chostetter_cvs
// AlgorithmTest tweaks
//
// Revision 1.2  2004/07/11 21:24:54  chostetter_cvs
// Organized imports
//
// Revision 1.1  2004/07/11 21:19:44  chostetter_cvs
// More Algorithm work
//
//
//--- Warning ----------------------------------------------------------------
//
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// *  Neither the author, their corporation, nor NASA is responsible for
//	any consequence of the use of this software.
// *  The origin of this software must not be misrepresented either by
//	explicit claim or by omission.
// *  Altered versions of this software must be plainly marked as such.
// *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.processors;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;


/**
 * An TerminalPlotterProcessor presents a simple ASCII plot of the first 
 * DataBuffer of the first BasisSet of each DataSet it receives.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/31 13:05:22 $
 * @author carlwork
 */

public class TerminalPlotterProcessor extends BasisSetProcessor
{
	private static final String CLASS_NAME = 
		TerminalPlotterProcessor.class.getName();
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Terminal Plotter Processor";
	
	private static final String INITIAL_PAD = " ";
	private static final char BELOW_RANGE_CHAR = '<';
	private static final char DEFAULT_PLOT_POINT_CHAR = 'o';
	
	private char fPlotPointChar = DEFAULT_PLOT_POINT_CHAR;
	private int fPlotWidth;
	private boolean fLabelPlots = true;
	
	public static final String PLOT_POINT_CHAR_PROP_NAME = "plot point char";
	public static final String PLOT_WIDTH_PROP_NAME = "plot width";
	public static final String LABEL_PLOTS_PROP_NAME = "label plots";
	
	private double fMinDataValue;
	private double fMaxDataValue;

	private double fScaleFactor;
	
	
	/**
	 *	Constructs a new TerminalPlotterProcessor having a default name.
	 *
	 */
	
	public TerminalPlotterProcessor()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new TerminalPlotterProcessor having the given name.
	 *
	 *	@param name The name of the new TerminalPlotterProcessor
	 */
	
	public TerminalPlotterProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new TerminalPlotterProcessor configured according to the 
	 *  given ComponentDescriptor.
	 *
	 *  @param descriptor A ComponentDescriptor describing the desired 
	 * 		configuration of the new TerminalPlotterProcessor
	 */
	
	public TerminalPlotterProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Constructs a new TerminalPlotterProcessor having a default name and 
	 * configured with the given plot parameters.
	 * 
	 * @param plotWidth The width of the data plot
	 */
	
	public TerminalPlotterProcessor(int plotWidth)
	{
		this(DEFAULT_NAME, plotWidth);
	}
	
	
	/**
	 * Constructs a new TerminalPlotterProcessor having the given name and 
	 * configured with the given plot parameters.
	 * 
	 * @param name The name of the new TerminalPlotterProcessor
	 * @param plotWidth The width of the data plot
	 */
	
	public TerminalPlotterProcessor(String name, int plotWidth)
	{
		this(name);
				
		setPlotWidth(plotWidth);
	}
	
	
	/**
	 * Causes this TerminalPlotterProcessor to plot the first DataBuffer 
	 * of the given BasisSet
	 * 
	 * @param basisSet A BasisSet
	 */
	
	public void processBasisSet(BasisSet basisSet)
	{
		int numSamples = basisSet.getSize();
		
		DataBuffer basisBuffer = basisSet.getBasisBuffer();
		
		Iterator dataBuffers = basisSet.getDataBuffers();
		DataBuffer dataBuffer = (DataBuffer) dataBuffers.next();
		
		if (fLabelPlots)
		{
			System.out.println("----------------------------------------");
			System.out.println("BasisSet: " + basisSet.getBasisBundleId());
			System.out.println("Contains " + numSamples + " samples");
			
			if (basisSet.isUniformlySampled())
			{
				System.out.println("Uniform sample rate: " + 
					basisSet.getUniformSampleInterval() + " " + 
					basisSet.getBasisBufferDescriptor().getUnit());
			}
			
			System.out.println("Basis buffer: " + basisBuffer); 
			
			if (basisBuffer.isRealValued())
			{
				System.out.print("Basis range: " + basisBuffer.getAsDouble(0) + 
					" to " + basisBuffer.getAsDouble(numSamples - 1));
			}
			else
			{
				System.out.print("Basis range: " + basisBuffer.getAsLong(0) + 
					" to " + basisBuffer.getAsLong(numSamples - 1));
			}
			
			System.out.println(" " + basisBuffer.getUnit());
			
			System.out.println("Plotting Data buffer: " + dataBuffer);
			fMinDataValue = dataBuffer.getMinValue();
			int minValueIndex = dataBuffer.getMinValueIndex();
			fMaxDataValue = dataBuffer.getMaxValue();
			int maxValueIndex = dataBuffer.getMaxValueIndex();
			System.out.println("Min. value: " + fMinDataValue + " at basis value " + 
				basisBuffer.getAsDouble(minValueIndex));
			System.out.println("Max. value: " + fMaxDataValue + " at basis value " + 
					basisBuffer.getAsDouble(maxValueIndex));
			updateParameters();
			System.out.println("----------------------------------------");
		}
		
		synchronized (TerminalPlotterProcessor.this.getConfigurationChangeLock())
		{
			for (int x = 0; x < numSamples; x++)
			{
				double dataValue = dataBuffer.getAsDouble(x);
				
				int plotPosition = getPlotPosition(dataValue);
				
				if (plotPosition < 0)
				{
					System.out.println(BELOW_RANGE_CHAR);
				}
				else
				{
					StringBuffer pad = new StringBuffer(plotPosition);
					
					pad.append(INITIAL_PAD);
					
					for (int y = 0; y < plotPosition; y++)
					{
						pad.append(' ');
					}
					
					System.out.println(pad.toString() + fPlotPointChar);
				}
			}
		}
		
		System.out.println();
	}

	
	/**
	 * Updates the current set of calculated plot parameters.
	 * 
	 */
	
	private void updateParameters()
	{
		fScaleFactor = fPlotWidth / (fMaxDataValue - fMinDataValue);
	}
	
	
	/**
	 * Returns the plot position corresponding to the given data value.
	 * 
	 * @return The plot position corresponding to the given data value
	 */
	
	private int getPlotPosition(double dataValue)
	{
		int result = 0;
		
		double normalizedValue = dataValue - fMinDataValue;
		
		result = (int) (normalizedValue * fScaleFactor);
		
		return (result);
	}
	
	
	/**
	 * Sets the plot width to the given number of columns.
	 * 
	 * @param numColumns The desired plot width
	 */
	
	public void setPlotWidth(int numColumns)
	{
		if (numColumns > 0)
		{
			synchronized (getConfigurationChangeLock())
			{
				double oldPlotWidth = fPlotWidth;
				
				fPlotWidth = numColumns;
				
				firePropertyChange(PLOT_WIDTH_PROP_NAME, 
					oldPlotWidth, fPlotWidth);
					
				updateParameters();
			}
		}
		else
		{
			String message = "Plot width must be greater than 0";
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"setPlotWidth", message);
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 * Returns the current plot width.
	 * 
	 * @return The current plot width
	 */
	
	public int getPlotWidth()
	{
		return (fPlotWidth);
	}
	
	
	/**
	 * Sets the plot point character to the given character.
	 * 
	 * @param plotPointChar The desired plot point character
	 */
	
	public void setPlotPointCharacter(char plotPointChar)
	{
		synchronized (getConfigurationChangeLock())
		{
			double oldPlotPointChar = fPlotPointChar;
			
			fPlotPointChar = plotPointChar;
			
			firePropertyChange(PLOT_POINT_CHAR_PROP_NAME, 
				plotPointChar, fPlotPointChar);
		}
	}
	
	
	/**
	 * Returns the current plot point character.
	 * 
	 * @return The current plot point character
	 */
	
	public int getPlotPointChar()
	{
		return (fPlotPointChar);
	}
	
	
	/**
	 * Returns a String representation of this TerminalPlotterProcessor.
	 * 
	 * @return A String representation of this TerminalPlotterProcessor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString());
		
		stringRep.append("\nPlot width: " + fPlotWidth + " columns");
		
		return (stringRep.toString());
	}
}
