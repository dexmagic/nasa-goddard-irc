//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.library.processors;

import gov.nasa.gsfc.commons.types.arrays.ArrayHelpers;
import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.ModifiableBasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.ModifiableDataBufferDescriptor;
import gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.SI;


/**
 * Generic Coadder that will average a certain set samples within each
 * data buffer. It is only compatible with single Basis Bundle In other words,
 * it cannot support coadding basis requests from multiple basis bundles. Use
 * separate coadders for each basis bundle.
 * 
 * The Coadder uses a pluggable "strategy" pattern where the strategy is used
 * with each incoming basis set to define the coadd points.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: CoaddProcessor.java,v 1.23 2006/07/31 19:11:49 smaher_cvs Exp $
 * @author	Steve Maher
 * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy
 */

final public class CoaddProcessor extends BasisSetProcessor
{
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger.getLogger(CoaddProcessor.class
			.getName());

    public static final String DEFAULT_NAME = "Coadd Processor";
    
    public static final String COADD_HINT_AGGREGATE = "aggregate";
    public static final String COADD_HINT_BINARY = "binary";    
	
	/**
	 * The "strategy" to use for coadding
	 */
	private CoadderStrategy fStrategy;
    
    /**
     * Size of the output basis sets 
     */
    private int fOutputBasisSetSize = -1;
    
    
    //-- Leftovers to carry over to next basis set
	/**
	 * Number of samples leftover from previous basis set(s) 
	 */
	private int fNumberOfLeftoverSamples = 0;
	
    /**
     * Contains the running coadds for each data buffer
     */
    private double fDataBufferLeftovers[];
    
    /**
     * Contains the running coadds for basis buffer
     */
    private double fBasisBufferLeftovers;
    
    private int fOutputBasisSetIndex = -1;    
    

    //--- Some cached values for performance reasons
    private BasisSet fOutputBasisSet;

    private double[] fOutputBasisBufferArrayArray;    

    private double[][] fOutputDataBufferArrays;

    private boolean[] fPerBufferCoaddHintAggregateFlags;

    private boolean[] fPerBufferCoaddHintBinaryFlags;

    
    //--- Some diagnostic information
	/**
	 * The number of coadds performed in the last
	 * basis set.
	 */
	private int fLastNumberOfCoaddsInBasisSet;
	
	
	/**
	 */
	public CoaddProcessor()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 * @param name
	 * @param parent
	 */
	public CoaddProcessor(String name)
	{
		super(name);
	}
	

	/**
	 * @param descriptor
	 * @param parent
	 */
	public CoaddProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	


	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.algorithms.BasisSetProcessor#processBasisSet(gov.nasa.gsfc.irc.data.BasisSet)
	 */
	protected void processBasisSet(BasisSet basisSet) 
	{
        if (fStrategy == null)
        {
        	throw new IllegalStateException("No strategy set for coadder.");
        }
        
        // Get the indices where coadding should occur.  A zero-lengthed array is allowed.
        int[] coaddIndexPoints = fStrategy.getCoaddIndexPoints(basisSet, fNumberOfLeftoverSamples);
        
        // Make sure the indices are well formed
        coaddIndexPoints = scrubCoaddIndexArray(basisSet.getSize(), coaddIndexPoints);        
        
		if (sLogger.isLoggable(Level.CONFIG))
		{
			sLogger.config("Coadd indices in basis set: " + ArrayHelpers.arrayToString(coaddIndexPoints));
		}

        // A diagnostic
        fLastNumberOfCoaddsInBasisSet = (coaddIndexPoints != null) ? coaddIndexPoints.length: 0;
        
        Object[] dataBufferArrays = new Object[basisSet.getNumberOfDataBuffers()];

        for (int bufferNumber = 0; bufferNumber < basisSet.getNumberOfDataBuffers(); bufferNumber++)
		{
			DataBuffer inputDataBuffer = basisSet.getDataBuffer(bufferNumber);
            if (inputDataBuffer.getDataBufferType() == double.class)
            {
            	dataBufferArrays[bufferNumber] = inputDataBuffer.getAsDoubleArray();     	                
            }
            else if (inputDataBuffer.getDataBufferType() == float.class)
            {
            	dataBufferArrays[bufferNumber] = inputDataBuffer.getAsFloatArray();              	
            }
            else if (inputDataBuffer.getDataBufferType() == long.class)
            {
            	dataBufferArrays[bufferNumber] = inputDataBuffer.getAsLongArray();	            	
            }            
            else if (inputDataBuffer.getDataBufferType() == int.class)
            {
            	dataBufferArrays[bufferNumber] = inputDataBuffer.getAsIntArray();	            	
            }
            else if (inputDataBuffer.getDataBufferType() == short.class)
            {
            	dataBufferArrays[bufferNumber] = inputDataBuffer.getAsShortArray();	            	
            }          
            else if (inputDataBuffer.getDataBufferType() == byte.class)
            {
            	dataBufferArrays[bufferNumber] = inputDataBuffer.getAsByteArray();	            	
            }  	 
		}


        boolean firstCoadd = true;	
		int lastIndexToCoaddAfter = -1;		
		boolean done = false;
		
        // Go through each coaddIndexPoint and perform a coadd
        // for basis and data buffers
		int coaddIndexPointIndex = 0;
        
        double[] inputBasisArray = basisSet.getBasisBuffer().getAsDoubleArray();
        double[] dataSums = new double[basisSet.getNumberOfDataBuffers()];
        
		while (!done)
        {
			boolean addingLeftovers = false;
			int indexToCoaddAfter;
			if (coaddIndexPointIndex < coaddIndexPoints.length)
			{
				indexToCoaddAfter = coaddIndexPoints[coaddIndexPointIndex++];
	        	addingLeftovers = false;
	        	done = false;
			}
			else
			{
				// Last index
				indexToCoaddAfter = basisSet.getSize() - 1;
				addingLeftovers = true;
				done = true;
			}

            if (indexToCoaddAfter == CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS)
            {               	
                //--- Special Case: Output the leftovers from previous basis set without including
                // any samples from this basis set

                storeCoadd(basisSet, fBasisBufferLeftovers, fDataBufferLeftovers, fNumberOfLeftoverSamples);
            }
            else
            {
                //--- Normal coadd
                
                
                ////////////////////////////            
                //
                // Coadd the basis buffer
                //
                ////////////////////////////
                
                double basisSum = 0.0;    
                
                // How many new samples will be coadded?
                int numberOfSamplesInThisCoadd = indexToCoaddAfter - lastIndexToCoaddAfter;

                // Get appropriate fields for direct access to basis array
                
                if (firstCoadd == true)
                {
                    // Include leftovers from previous basis set
                    basisSum = fBasisBufferLeftovers;
                    numberOfSamplesInThisCoadd += fNumberOfLeftoverSamples;
                }
                
                // The coaddIndex is INCLUDED IN THE COADD
                for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                {
                    basisSum += inputBasisArray[i];  
                }
                
                
                if (addingLeftovers == true)
                {
                    fBasisBufferLeftovers = basisSum;
                    fNumberOfLeftoverSamples = numberOfSamplesInThisCoadd;
                }

                
                
                ////////////////////////////
                //
                // Coadd the data buffers
                //
                ////////////////////////////
                
                for (int bufferNumber = 0; bufferNumber < basisSet.getNumberOfDataBuffers(); bufferNumber++)
                {
                    double dataSum = 0.0;
                    if (firstCoadd == true)
                    {
                        // Include leftovers from previous basis set
                        dataSum = fDataBufferLeftovers[bufferNumber];
                    }
                    Object bufferArray = dataBufferArrays[bufferNumber];
                    
                    if (bufferArray instanceof double[])
                    {
                        double[] inputDataArrayD =  (double[]) bufferArray;     
                        for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                        {
                            dataSum += inputDataArrayD[i];   						
                        }	                
                    }
                    else if (bufferArray instanceof float[])
                    {
                        float[] inputDataArrayF = (float[]) bufferArray;  
                        for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                        {
                            dataSum += inputDataArrayF[i];   						
                        }	            	
                    }
                    else if (bufferArray instanceof long[])
                    {
                        long[] inputDataArrayL = (long[]) bufferArray;
                        for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                        {
                            dataSum += inputDataArrayL[i];   						
                        }	            	
                    }            
                    else if (bufferArray instanceof int[])
                    {
                        int[] inputDataArrayI = (int[]) bufferArray;
                        for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                        {
                            dataSum += inputDataArrayI[i];   						
                        }		            	
                    }
                    else if (bufferArray instanceof short[])
                    {
                        short[] inputDataArrayS = (short[]) bufferArray;
                        for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                        {
                            dataSum += inputDataArrayS[i];   						
                        }		            	
                    }          
                    else if (bufferArray instanceof byte[])
                    {
                        byte[] inputDataArrayB = (byte[]) bufferArray;
                        for (int i = lastIndexToCoaddAfter + 1; i <= indexToCoaddAfter; i++)
                        {
                            dataSum += inputDataArrayB[i];   						
                        }		            	
                    }  	           
                    else
                    {
                        throw new AssertionError("Illegal buffer array type: " + bufferArray.getClass());
                    }
                    
                    if (addingLeftovers == false)
                    {
                        // Store temporarily for later insertion in basis set
                        dataSums[bufferNumber] = dataSum;
                    }
                    else
                    {
                        // Store leftovers for next call to processBasisSet
                        fDataBufferLeftovers[bufferNumber]= dataSum;
                    }
                }
                
                if (addingLeftovers == false)
                {
                    storeCoadd(basisSet, basisSum, dataSums, numberOfSamplesInThisCoadd);
                }
                
            } // else PREVIOUS_LEFTOVERS
            
            firstCoadd = false;
            lastIndexToCoaddAfter = indexToCoaddAfter;
            
        } // while !done
	}

    /**
     * Store a single coadd point in the output basis set, taking into account the
     * coadd hints of each data buffer.
     * <p>
     * The storage mechanism delivers evenly sized basis sets so as to not trigger
     * the more expensive basis set chopping/concatenating code.
     * @param inputBasisSet
     * @param basisSum
     * @param dataSums
     * @param numberOfSamples
     */
    private void storeCoadd(BasisSet inputBasisSet, double basisSum, double[] dataSums,
	            int numberOfSamples)
		{
	
	        //--- Check for basis set allocation and publishing
	        
	        // If the output basis set size isn't set, take a reasonable guess
	        if (fOutputBasisSetSize < 0)
	        {
	            fOutputBasisSetSize = inputBasisSet.getSize()/numberOfSamples;
	            if (fOutputBasisSetSize == 0)
	            {
	                fOutputBasisSetSize = 1;
	            }
	        }
	        
	        // Allocate basis set first time through
	        if (fOutputBasisSetIndex < 0)
	        {
	            allocateNewBasisSet(inputBasisSet);
	        }
	            
	        // Perform AbstractDataBuffer.resolveIndex()-type adjustment to buffer
	        int arrayIndex = fOutputBasisSet.getBasisBuffer().arrayOffset() + fOutputBasisSetIndex;
	        
	        if (arrayIndex >= fOutputDataBufferArrays[0].length)
	        {
	        	arrayIndex -= fOutputDataBufferArrays[0].length;		
	        }

	        
	        //---  Perform desired coadd computation and store result in basis set
	        
	        fOutputBasisBufferArrayArray[arrayIndex] = basisSum/numberOfSamples;
	        
	        for (int bufferNumber = 0; bufferNumber < fOutputBasisSet.getNumberOfDataBuffers(); bufferNumber++)
	        {            

	            if (fPerBufferCoaddHintBinaryFlags[bufferNumber] == true)
	            {
	                // Treat data as "binary"; any values greater than 0 are 1; the
	                // rest are 0
	                if (dataSums[bufferNumber] > 0)
	                {
	                    fOutputDataBufferArrays[bufferNumber][arrayIndex] = 1;
	                }
	                else
	                {
	                    fOutputDataBufferArrays[bufferNumber][arrayIndex] = 0;                    
	                }
	            }
	            else if (fPerBufferCoaddHintAggregateFlags[bufferNumber] == true)
	            {
	                // Store the aggregate value
	                fOutputDataBufferArrays[bufferNumber][arrayIndex] = dataSums[bufferNumber];                       
	            }
	            else
	            {
	                // "Standard" (averaging) coadd
	                fOutputDataBufferArrays[bufferNumber][arrayIndex] = dataSums[bufferNumber] / numberOfSamples;
	            }            
	        }
	        
	        
	        fOutputBasisSetIndex++;
	
	        // Check if we should publish data
	        // fOutputBasisSetIndex holds the number of
	        // samples now ..
	        if (fOutputBasisSetIndex >= fOutputBasisSetSize)
	        {
	        	if (inputBasisSet.isUniformlySampled())
				{
					// Set the new sampling rate
					fOutputBasisSet.setUniformSampleIntervalImplicitly();
				}
	        	
	            // Make basis set available
	            getOutput().makeAvailable(fOutputBasisSet, fOutputBasisSetIndex);
	            allocateNewBasisSet(inputBasisSet);
	        }        
	
		}

    
	/**
	 * Allocate a new basis set, given some current state information
	 * and the provided input basis set.  Also cache some information
	 * such as the coadd hints for each data buffer.
	 * @param inputBasisSet
	 */
	private void allocateNewBasisSet(BasisSet inputBasisSet)
    {
        // allocate new basis set
        fOutputBasisSet = getOutput().allocateBasisSet(
                getAssociatedOutputBasisBundleId(inputBasisSet.getBasisBundleId()),
                fOutputBasisSetSize);  
        
        fOutputBasisSetIndex = 0;
        
        
        //--- Get references to the basis and data buffer arrays
        
        fOutputBasisBufferArrayArray = (double[]) fOutputBasisSet.getBasisBuffer().array();
        
        // Check for initialization
        if (fOutputDataBufferArrays == null)
        {
        	fOutputDataBufferArrays = new double[fOutputBasisSet.getNumberOfDataBuffers()][];
        }
        
        fPerBufferCoaddHintAggregateFlags = new boolean[fOutputBasisSet.getNumberOfDataBuffers()];
        fPerBufferCoaddHintBinaryFlags = new boolean[fOutputBasisSet.getNumberOfDataBuffers()];
        
        // Get data buffer arrays for higher performance
        int arrayOffsetCheck = fOutputBasisSet.getBasisBuffer().arrayOffset();
        int arrayLengthCheck = ((double []) fOutputBasisSet.getBasisBuffer().array()).length;

        
        for (int bufferNumber = 0; bufferNumber < fOutputBasisSet.getNumberOfDataBuffers(); bufferNumber++)
        {
        	// Not POSITIVE that all data buffers will share array offsets, etc., so 
        	// double check.
            DataBuffer buffer = fOutputBasisSet.getDataBuffer(bufferNumber);
            
            if (buffer.arrayOffset() != arrayOffsetCheck)
            {
            	throw new IllegalStateException("Data buffers not using same array offset!");    	
            }
            
            if (((double [])buffer.array()).length != arrayLengthCheck)
            {
            	throw new IllegalStateException("Data buffers not using same array length!");    	
            }            
            
            fOutputDataBufferArrays[bufferNumber] = (double[]) buffer.array();
            
            // Get the coaddHint from the input basis set because JUnit tests
            // can only change the input, not the output data buffer descriptors :)
            String coaddHint = inputBasisSet.getDataBuffer(bufferNumber).getDescriptor().getCoaddHint();
            if (coaddHint != null && coaddHint.length() > 0)
            {
                coaddHint = coaddHint.toLowerCase();
                if (COADD_HINT_AGGREGATE.equals(coaddHint))
                {
                    fPerBufferCoaddHintAggregateFlags[bufferNumber] = true;
                }
                else if (COADD_HINT_BINARY.equals(coaddHint))
                {
                    if (SI.BIT.equals(buffer.getUnit()) == false)
                    {
                        throw new IllegalArgumentException("DataBuffer " + buffer.getName()
                                + " has coadd hint \"" + COADD_HINT_BINARY + "\" but unit of data buffer is not \"bit\"");
                    }
                    fPerBufferCoaddHintBinaryFlags[bufferNumber] = true;
                }
            }            
        }
    }


    /**
	 * Make sure the strategy returned an array that has increasing values
	 * that are in range of the basis set.  If (in order) duplicates are
	 * encountered they are removed.
	 * 
	 * @param basisSetSize
	 * @param coaddIndexPoints
	 * @return the scrubbed index array
	 */
	int[] scrubCoaddIndexArray(int basisSetSize, int[] coaddIndexPoints)
	{
		int lastIndexPoint = CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS - 1;
		
		int arraySize = coaddIndexPoints.length;
		
		// arraySize may change if a duplicate is found
		for (int i = 0; i < arraySize; i++)
		{
			int indexPoint = coaddIndexPoints[i];
		
			if (indexPoint < lastIndexPoint)
			{
				throw new IllegalArgumentException("Coadd index point not monotonically increasing. Last, Current =  " + lastIndexPoint + ", " + indexPoint);
			}
			else if (indexPoint >= basisSetSize)
			{
				throw new IllegalArgumentException("Coadd index point (" + indexPoint + ") is beyond basis set size: " + basisSetSize);
			}
			else if (indexPoint < CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS)
			{
				throw new IllegalArgumentException("Illegal coadd index point: " + indexPoint);
			}
			//
			// Check for and remove duplicate
			//
			else if (indexPoint == lastIndexPoint)			
			{
				// Shift array left
				System.arraycopy(coaddIndexPoints, i, coaddIndexPoints, i - 1, coaddIndexPoints.length - i);
				i--;   // need to account for shifting
				arraySize--; // array size has shrunk by one
//				System.out.println("XXX removed duplicate");
			}				
			lastIndexPoint = indexPoint;
			

		}
		
		int[] returnedArray = coaddIndexPoints;
		
		// Trim to new array if we removed duplicates
		if (arraySize != coaddIndexPoints.length)
		{
			returnedArray = new int[arraySize];
			System.arraycopy(coaddIndexPoints, 0, returnedArray, 0, arraySize);
		}
		
		return returnedArray;
	}


	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.algorithms.DefaultProcessor#buildOutputDescriptor(gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor)
	 */
	public BasisBundleDescriptor buildOutputDescriptor(BasisBundleDescriptor inputDescriptor)
	{
		
		fNumberOfLeftoverSamples = 0;
		
		ModifiableBasisBundleDescriptor result = (ModifiableBasisBundleDescriptor)
			inputDescriptor.getModifiableCopy();
	
		result.setName("Coadded " + inputDescriptor.getName());
	
		Iterator dataBufferDescriptors = 
			result.getDataBufferDescriptors().iterator();
		
		fDataBufferLeftovers = new double[result.getDataBufferDescriptors().size() + 1];
		fBasisBufferLeftovers = 0.0d;
	
		 int i = 1;  // 0 is reserved for basis buffer
		while (dataBufferDescriptors.hasNext())
		{
			ModifiableDataBufferDescriptor descriptor = 
				(ModifiableDataBufferDescriptor) 
					dataBufferDescriptors.next();
	
			fDataBufferLeftovers[i++] = 0.0d;
			
			// the output is an average, so just make everything a double
			descriptor.setDataType(double.class);
		}
		
		return result;
	}


	public CoadderStrategy getStrategy()
	{
		return fStrategy;
	}


	public void setStrategy(CoadderStrategy coadderStrategy)
	{
		fStrategy = coadderStrategy;
	}


	/**
	 * @return Returns the lastNumberOfCoaddsInBasisSet.
	 */
	public int getLastNumberOfCoaddsInBasisSet()
	{
		return fLastNumberOfCoaddsInBasisSet;
	}


    public int getOutputBasisSetSize()
    {
        return fOutputBasisSetSize;
    }


    public void setOutputBasisSetSize(int outputBasisSetSize)
    {
        fOutputBasisSetSize = outputBasisSetSize;
    }


//    @Override
    public void start()
    {
        super.start();
    }


//    @Override
    public void stop()
    {
        super.stop();
        if (fOutputBasisSet != null) {
            fOutputBasisSet.release();
            fOutputBasisSet = null;
        }
        // Indicate a basis set allocation needs to occur
        fOutputBasisSetIndex = -1;
    }
}

//--- Development History  ---------------------------------------------------
//
//$Log: CoaddProcessor.java,v $
//Revision 1.23  2006/07/31 19:11:49  smaher_cvs
//Added processing of uniformly spaced basis data.
//
//Revision 1.22  2006/06/30 15:09:27  smaher_cvs
//Slight performance gain by writing directly to basis set array.
//
//Revision 1.21  2006/06/27 22:40:35  smaher_cvs
//Major rework for performance; use constant basis set size and cache data buffer references.
//
//Revision 1.20  2006/06/26 20:06:07  smaher_cvs
//Support for coaddHints; name changes
//
//Revision 1.19  2006/06/05 18:13:01  smaher_cvs
//Comments.
//
//Revision 1.18  2006/06/02 19:20:36  smaher_cvs
//Removed unused field.
//
//Revision 1.17  2006/05/11 17:17:29  smaher_cvs
//Removed 1.5ism.
//
//Revision 1.16  2006/05/10 17:29:24  smaher_cvs
//Changed coadd index array to be a primitive array; fixed some bugs; have a working chop coadder with this checkin.
//
//Revision 1.15  2006/03/14 14:57:15  chostetter_cvs
//Simplified Namespace, Manager, Component-related constructors
//
//Revision 1.14  2006/01/23 17:59:53  chostetter_cvs
//Massive Namespace-related changes
//
//Revision 1.13  2005/10/03 01:27:04  smaher_cvs
//Fixed bug with saving last basis set value
//
//Revision 1.12  2005/09/27 07:46:41  smaher_cvs
//Implemented IntraBasisSetDataThresholdStrategy (not tested yet)
//
//Revision 1.11  2005/09/23 20:56:45  smaher_cvs
//Implemented and tested new Coadder with strategy pattern
//

