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

package gov.nasa.gsfc.irc.library.processors.strategies.coadder;

import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Coadd when certain rising or falling data threshold(s) appear in the data.
 * <p>
 * Note, unit tests for this class are currently in the MarkIII project.
 * 
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/06/27 22:39:27 $
 * @author Steve Maher
 */
public class IntraBasisSetDataThresholdStrategy implements CoadderStrategy {

    /**
     * The thresholds to apply to the basis sets
     */
    private ArrayList fThresholds; 
    
    /**
     * Leftovers from previous basis set
     */
    private double[] fLastSampleValueFromPreviousBasisSet;
    
    /**
     * Initialization flag
     */
    private boolean fNeverProcessedBasisSet = true;
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy#getCoaddIndexPoints(gov.nasa.gsfc.irc.data.BasisSet, int)
     */
    public int[] getCoaddIndexPoints(BasisSet basisSet, int numberSamplesSinceLastCoadd)
    {
        int basisSetSize = basisSet.getSize();
       
        int numberOfIndexPoints = 0;        
        
        // Why "+ 1" below? We may need space for 
        // CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS token at the beginning
        int[] scratchIndexPointArray = new int[basisSetSize + 1];

        
        
        if (fNeverProcessedBasisSet == true)
        {
            fLastSampleValueFromPreviousBasisSet = new double[basisSet.getNumberOfDataBuffers()];
        }

        if (fThresholds == null)
        {
            throw new IllegalStateException("No thresholds set.");
        }
        
        // Go through each threshold and add coadd indices for each run.
        // We will sort all the indices after the loop.  Duplicates indices are 
        // okay and will be ignored in the CoaddProcessor.
        for (Iterator thresholdsI = fThresholds.iterator(); thresholdsI.hasNext();)
        {
            Threshold threshold = (Threshold) thresholdsI.next();
            threshold.checkInitialization(basisSet);
            
            int[] bufferIndices = threshold.getDataBufferIndices();
            
            // and each data buffer of each threshold ..
            for (int bufIndex = 0; bufIndex < bufferIndices.length; bufIndex++)
            {
                DataBuffer buffer = basisSet.getDataBuffer(bufferIndices[bufIndex]);
                double previousValue;
                
                //
                // Check last value of previous basis set
                //
                if (fNeverProcessedBasisSet == true)
                {
                    previousValue = buffer.getAsDouble(0);
                }
                else
                {
                    double firstValue = buffer.getAsDouble(0);
                    
                    //
                    // Check if threshold was triggered between the value from the 
                    // last basis set and the first value of this basis set
                    //
                    if (threshold.checkThreshold(fLastSampleValueFromPreviousBasisSet[bufIndex], firstValue))
                    {
                    	
                        if (threshold.getIncludeThresholdPointInCurrentCoadd() == true)
                        {
                        	//
                        	// Include the first sample in the coadd
                        	//
                        	scratchIndexPointArray[numberOfIndexPoints++] = 0;
                        }
                        else 
                        {
                        	//
                        	// Include token saying to just coadd leftovers
                        	//
                        	scratchIndexPointArray[numberOfIndexPoints++] = CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS;                	
                        }
                    }
                    previousValue = firstValue;                    
                }
                
                // Now check the data in the current basis set
                for (int sampleIndex = 1; sampleIndex < basisSetSize; sampleIndex++)
                {
                	double nextValue = buffer.getAsDouble(sampleIndex);
                    if (threshold.checkThreshold(previousValue, nextValue))
                    {
                        if (threshold.getIncludeThresholdPointInCurrentCoadd() == true)
                        {
                        	// Include threshold point in coadd
                        	scratchIndexPointArray[numberOfIndexPoints++] = sampleIndex;                      	
                        }
                        else 
                        {
                        	// Leave threshold point for next coadd group
                        	scratchIndexPointArray[numberOfIndexPoints++] = sampleIndex - 1;
                        }
                    }
                    previousValue = nextValue;
                }
                
                // If on last threshold, save last value from basis set
                if (thresholdsI.hasNext() == false)
                {
                    // Save last value of basis set
                    fLastSampleValueFromPreviousBasisSet[bufIndex] = buffer
                            .getAsDouble(basisSetSize - 1); 
                } 
            }
           
            
        }
        fNeverProcessedBasisSet = false;
        
        // Trim the array to the right size ...
        int[] trimmedAndSortedIndexPoints = new int[numberOfIndexPoints];
        System.arraycopy(scratchIndexPointArray, 0, trimmedAndSortedIndexPoints, 0, numberOfIndexPoints);
        
        // And sort them (required, and not provided, by the coadder processor)
        Arrays.sort(trimmedAndSortedIndexPoints);
        
        return trimmedAndSortedIndexPoints;        
    }


	/**
	 * Return the current set of thresholds for this coadder.
	 * @return array of array of {@link gov.nasa.gsfc.irc.library.processors.strategies.coadder.IntraBasisSetDataThresholdStrategy.Threshold}
	 */
	public ArrayList getThresholds()
	{
		return fThresholds;
	}


	/**
	 * Set the thresholds that should be used in this coadder.
	 * @param thresholds array of {@link gov.nasa.gsfc.irc.library.processors.strategies.coadder.IntraBasisSetDataThresholdStrategy.Threshold}
	 */
	public void setThresholds(ArrayList thresholds)
	{
		fThresholds = thresholds;
	}

    
    /**
     * Defines a single (immutable) threshold to determine when a coadding occurs.
     * 
     * Contains a value, direction, and whether the threshold value should
     * be included in the coadding.
     *
     * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
     * for the Instrument Remote Control (IRC) project.
     * 
     * @version	$Id: IntraBasisSetDataThresholdStrategy.java,v 1.6 2006/06/27 22:39:27 smaher_cvs Exp $
     * @author	$User:$
     */
    public class Threshold
    {
		/**
		 * Logger for this class
		 */
		private final Logger sLogger = Logger.getLogger(Threshold.class
				.getName());

        /**
         * Threshold direction that indicates a threshold should
         * trigger if the previous value before the threshold is 
         * less than the threshold value.
         */
        public final static int THRESHOLD_RISING = 1;
        /**
         * Threshold direction that indicates a threshold should
         * trigger if the previous value before the threshold is 
         * more than the threshold value.
         */        
        public final static int THRESHOLD_FALLING = -1;
        
        /**
         * Defines whether the values being coadded when a threshold is
         * triggered include the values at the trigger point, or
         * should those values be put in the <em>next</em> group
         * of coadded values.
         */
        private boolean fIncludeThresholdPointInCurrentCoadd = false;
        
        
        /**
         * Defines the direction of the threshold.  Must be
         * {@link #THRESHOLD_RISING} or {@link #THRESHOLD_FALLING}.
         */
        private int fThresholdDirection;
        
        /**
         * The threshold value.  The threshold will be triggered
         * when the data value "passes" this value in the appropriate
         * direction.
         */
        private double fThresholdValue;
       
        /**
         * Name prefix of all the data buffers to which the threshold
         * check should be applied.
         */
        private String fDataBufferPrefix;
        
        private int[] fDataBufferIndices;

		/**
		 * @param dataBufferPrefix
         * @param value
         * @param direction
         * @param includeThresholdInCurrentCoadd
		 */
		public Threshold(String dataBufferPrefix, 
				double value,
				int direction, boolean includeThresholdInCurrentCoadd)
		{
			fDataBufferPrefix = dataBufferPrefix;
			fIncludeThresholdPointInCurrentCoadd = includeThresholdInCurrentCoadd;
			fThresholdDirection = direction;
			fThresholdValue = value;
		}

		/**
         * Check whether we need to do some initial processing.
         * @param basisSet
         */
        private void checkInitialization(BasisSet basisSet)
        {
            if (fDataBufferIndices == null)
            {
                ArrayList bufferIndices = new ArrayList();
                int i = 0;
                boolean found = false;
                for (Iterator iter = basisSet.getDataBuffers(); iter.hasNext(); i++)
                {
                    DataBuffer buffer = (DataBuffer) iter.next();
                    if (buffer.getName().startsWith(fDataBufferPrefix))
                    {
                        bufferIndices.add(new Integer(i));
                        found = true;
                    }
                }
                if (found == false)
                {
                	throw new IllegalArgumentException("Could not find data buffer with prefix: " + fDataBufferPrefix);
                }

                fDataBufferIndices = new int[bufferIndices.size()];
                i = 0;
                for (Iterator iter = bufferIndices.iterator(); iter.hasNext();)
                {
                    fDataBufferIndices[i++] = ((Integer)iter.next()).intValue();
                }
            }

            if (fDataBufferIndices.length == 0)
            {
            	String mesg = "Could not find any data buffers using prefix: " + fDataBufferPrefix;
				sLogger.severe(mesg);
            }
            
            // Make sure the direction has been set.  I think
            // this is better than default the direction to something.
            if (fThresholdDirection != THRESHOLD_FALLING &&
            		fThresholdDirection != THRESHOLD_RISING)
            {
            	throw new IllegalStateException("Direction not set in Coadder threshold.");
            }
        }
        
        /**
         * Check if threshold was triggered given two values.
         * @param olderValue
         * @param newerValue
         * @return
         */
        private boolean checkThreshold(double olderValue, double newerValue)
        {
            
            if (fThresholdDirection == THRESHOLD_FALLING && 
                    olderValue > newerValue &&
                    olderValue > fThresholdValue &&
                    fThresholdValue >= newerValue)
            {
                return true;
            }
            else if (fThresholdDirection == THRESHOLD_RISING && 
                        olderValue < newerValue &&
                        olderValue < fThresholdValue &&
                        fThresholdValue <= newerValue)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        
        public int getThresholdDirection()
        {
            return fThresholdDirection;
        }
        public double getThresholdValue()
        {
            return fThresholdValue;
        }
        public String getDataBufferPrefix()
        {
            return fDataBufferPrefix;
        }

        /**
         * Return the indices of the data buffer(s) in the basis bundle
         * that match the data buffer prefix defined in {@link #setDataBufferPrefix(String)}.
         * @return the buffer indices that match the data buffer prefix
         */
        public int[] getDataBufferIndices()
        {
            return fDataBufferIndices;
        }

        public boolean getIncludeThresholdPointInCurrentCoadd()
        {
            return fIncludeThresholdPointInCurrentCoadd;
        }
        
    }



}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: IntraBasisSetDataThresholdStrategy.java,v $
//	Revision 1.6  2006/06/27 22:39:27  smaher_cvs
//	Comments
//	
//	Revision 1.5  2006/06/20 12:51:28  smaher_cvs
//	Comments.
//	
//	Revision 1.4  2006/05/10 17:29:11  smaher_cvs
//	Changed coadd index array to be a primitive array; fixed some bugs; have a working chop coadder with this checkin.
//	
//	Revision 1.3  2005/10/07 21:08:30  smaher_cvs
//	Finished testing IntraBasisSetDataThresholdStrategy
//	
//	Revision 1.2  2005/10/03 01:27:04  smaher_cvs
//	Fixed bug with saving last basis set value
//	
//	Revision 1.1  2005/09/27 07:46:41  smaher_cvs
//	Implemented IntraBasisSetDataThresholdStrategy (not tested yet)
//	
//	Revision 1.1  2005/09/25 02:22:29  smaher_cvs
//	Checkpointing DataThresholdStrategy
//	
//	Revision 1.1  2005/09/23 20:56:45  smaher_cvs
