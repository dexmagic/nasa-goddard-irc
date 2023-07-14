//=== File Prolog ============================================================
//
// $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/processors/DataToTextWriterProcessor.java,v 1.5 2006/07/24 13:25:23 smaher_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: DataToTextWriterProcessor.java,v $
// Revision 1.5  2006/07/24 13:25:23  smaher_cvs
// Fixed bug where failure occurs if actual number of samples isn't the same
// as the setNumberSamples value.
//
// Revision 1.4  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.3  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.2  2005/07/15 19:21:35  chostetter_cvs
// Adjusted for changes in DataBuffer
//
// Revision 1.1  2004/07/24 02:46:11  chostetter_cvs
// Added statistics calculations to DataBuffers, renamed some classes
//
// Revision 1.3  2004/07/22 22:15:11  chostetter_cvs
// Created Algorithm versions of library Processors
//
// Revision 1.2  2004/07/22 16:28:03  chostetter_cvs
// Various tweaks
//
// Revision 1.1  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.4  2004/07/15 18:28:07  chostetter_cvs
// private property name to public property name. D'oh!
//
// Revision 1.3  2004/07/15 18:27:11  chostetter_cvs
// Add rich set of firePropertyChange event methods  to Components
//
// Revision 1.2  2004/07/15 17:48:55  chostetter_cvs
// ComponentManager, property change work
//
// Revision 1.1  2004/07/14 03:09:55  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.3  2004/07/13 18:52:50  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.1  2004/07/12 13:49:16  chostetter_cvs
// More Algorithm testing
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
import gov.nasa.gsfc.irc.data.DataSet;


/**
 * A DataToTextWriterProcessor prints out a specified number of values 
 * from each DataBuffer of each BasisSet it receives.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/07/24 13:25:23 $
 * @author carlwork
 */

public class DataToTextWriterProcessor extends BasisSetProcessor
{
	private static final String CLASS_NAME = 
		DataToTextWriterProcessor.class.getName();
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Data to Text Writer Processor";
	
	public static final String NUM_SAMPLES_PROP_NAME = "numSamples";
	
	private static final int DEFAULT_NUM_SAMPLES = 10;
	
	private int fNumSamples = DEFAULT_NUM_SAMPLES;
	
	
	
	/**
	 * Constructs a new DataToTextWriterProcessor having a default name.
	 * 
	 */
	
	public DataToTextWriterProcessor()
	{
		this(DEFAULT_NAME);
	}

	
	/**
	 *	Constructs a new DataToTextWriterProcessor having the given name.
	 *
	 *	@param name The name of the new Processor
	 */
	
	public DataToTextWriterProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new DataToTextWriterProcessor configured according to the 
	 *  given ComponentDescriptor.
	 *
	 *  @param descriptor A ComponentDescriptor describing the desired 
	 * 		configuration of the new Processor
	 */
	
	public DataToTextWriterProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Constructs a new DataToTextWriterProcessor having a default name that will 
	 * print the given number of samples for each DataBuffer of each DataSet it 
	 * receives. If the given value is 0, all samples will be printed (WARNING: 
	 * This could be a very large number of samples for some DataSets!)
	 * 
	 * @param numSamples The number of samples of each DataBuffer that will be 
	 * 		printed
	 */
	
	public DataToTextWriterProcessor(int numSamples)
	{
		this(DEFAULT_NAME);
		
		fNumSamples = numSamples;
	}
	
	
	/**
	 * Causes this DataToTextWriterProcessor to process the given 
	 * DataSet.
	 * 
	 * @param dataSet A DataSet
	 */
	
	public void processDataSet(DataSet dataSet)
	{
		System.out.println("DataSet: " + dataSet);
		
		super.processDataSet(dataSet);
	}
	
	
	/**
	 * Causes this DataToTextWriterProcessor to process the given 
	 * BasisSet.
	 * 
	 * @param basisSet A BasisSet
	 */
	
	protected void processBasisSet(BasisSet basisSet)
	{
		System.out.println("BasisSet: " + basisSet);
		
		DataBuffer basisBuffer = basisSet.getBasisBuffer();
		
		System.out.println("Basis buffer " + basisBuffer);
		
		int actualNumberOfSamples = basisSet.getSize();
		if (actualNumberOfSamples > 0)
		{
			System.out.println(basisBuffer.dataToString(actualNumberOfSamples));
		}
		else
		{
			System.out.println(basisBuffer.dataToString());
		}
		
		Iterator dataBuffers = basisSet.getDataBuffers();
		
		while (dataBuffers.hasNext())
		{
			DataBuffer dataBuffer = (DataBuffer) dataBuffers.next();
			
			System.out.println("Data buffer " + dataBuffer);
			
			if (actualNumberOfSamples > 0)
			{
				System.out.println(dataBuffer.dataToString(actualNumberOfSamples));
			}
			else
			{
				System.out.println(dataBuffer.dataToString());
			}
		}
	}

	
	/**
	 * Sets the number of samples to print from each input BasisSet.
	 * 
	 * @param numSamples The number of samples to print from each input BasisSet
	 */
	
	public void setNumSamples(int numSamples)
	{
		if (numSamples > 0)
		{
			synchronized (getConfigurationChangeLock())
			{
				int oldNumSamples = fNumSamples;
				
				fNumSamples = numSamples;
				
				firePropertyChange(NUM_SAMPLES_PROP_NAME, 
					oldNumSamples, fNumSamples);
			}
		}
		else
		{
			String message = "Number of samples must be greater than 0";
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"setNumSamples", message);
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 * Returns the current number of samples to print from each input BasisSet.
	 * 
	 * @return The current number of samples to print from each input BasisSet
	 */
	
	public int getNumSamples()
	{
		return (fNumSamples);
	}
	
	
	/**
	 * Returns a String representation of this DataToTextWriterProcessor.
	 * 
	 * @return A String representation of this DataToTextWriterProcessor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString() + "\n");
		
		stringRep.append("Prints " + fNumSamples + " samples\n");
		
		return (stringRep.toString());
	}
}
