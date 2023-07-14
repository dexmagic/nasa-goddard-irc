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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Logger;

/**
 * Coadd after a certain interval (for example, time) passes in the basis buffer.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: BasisBufferIntervalStrategy.java,v 1.1 2006/06/26 20:09:42 smaher_cvs Exp $
 * @author	$Author: smaher_cvs $
 */
public class BasisBufferIntervalStrategy implements CoadderStrategy 
{
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger
			.getLogger(BasisBufferIntervalStrategy.class.getName());


	private static final double DEFAULT_INTERVAL = .01;
	
	private double fIntervalInBasisBuffer = DEFAULT_INTERVAL;  // default
    
	private double fNextCoaddTime;
    /**
     * 
     */
    public BasisBufferIntervalStrategy()
    {
    	fIntervalInBasisBuffer = DEFAULT_INTERVAL;
    }
    
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy#getCoaddIndexPoints(gov.nasa.gsfc.irc.data.BasisSet,
	 *      int)
	 */
	public int[] getCoaddIndexPoints(BasisSet basisSet,
			int numberSamplesSinceLastCoadd)
	{
		int[] indexPoints;

		// Initialize next coadd time if first time
		if (fNextCoaddTime <= 0)
		{
			fNextCoaddTime = basisSet.getBasisBuffer().getAsDouble(0)
					+ fIntervalInBasisBuffer;
		}

		indexPoints = getCoaddIndexPointsUsingTimestamps(basisSet);

		// If the basis set is uniformly sampled, using the sample interval
		// would be a faster method,
		// but care would need to be taken with roundoff error (e.g., how
		// exactly does the specified interval align with actual timestamps
		// in the basis sets).

		return indexPoints;
	}


    /**
	 * Get the coadd index points for any basis set (uniformly sampled or not)
	 * 
	 * @param basisSet
	 * @return
	 */
	private int[] getCoaddIndexPointsUsingTimestamps(BasisSet basisSet)
	{
        int[] indexPoints = new int[basisSet.getSize() + 1];
        int numberOfIndexPoints = 0;
          
        double[] basisArray = basisSet.getBasisBuffer().getAsDoubleArray();
        for (int i = 0; i < basisArray.length; i++)
		{
        	// If the current basis buffer value exceeds the interval, then
        	// coadd the values up to, but not including, the current sample.
			if (basisArray[i] > fNextCoaddTime)
			{
				if (i == 0)
				{
					// Contents from previous basis set(s) should be coadded and published
					indexPoints[numberOfIndexPoints] = CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS;
				}
				else
				{
					indexPoints[numberOfIndexPoints] = i - 1;
				}
				
				// Get the next coadd time.  Account for large gaps in time.				
				while (basisArray[i] > fNextCoaddTime)				
				{
					fNextCoaddTime += fIntervalInBasisBuffer;
				}
				
				numberOfIndexPoints++;
			}
		}
        
        // Trim the array to the right size
        int[] trimmedIndexPoints = new int[numberOfIndexPoints];
        System.arraycopy(indexPoints, 0, trimmedIndexPoints, 0, numberOfIndexPoints);
        return trimmedIndexPoints;
	}	

	/**
     * @return number of samples in coadds
     */
    public double getIntervalInBasisBuffer() {
        return fIntervalInBasisBuffer;
    }

    /**
     * Set the interval over which to coadd.  The units are the same as used by the
     * basis buffer.
     * @param interval
     * @throws PropertyVetoException if interval is less than or equal to 0 
     */
    public void setIntervalInBasisBuffer(double interval) throws PropertyVetoException {
    	if (interval <= 0)
        {
    		throw new PropertyVetoException("Basis buffer interval must be greater than 0: " + interval, new PropertyChangeEvent(this, "IntervalInBasisBuffer", new Double(fIntervalInBasisBuffer), new Double(interval)));
        }
    	fIntervalInBasisBuffer = interval;    	
    }
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisBufferIntervalStrategy.java,v $
//	Revision 1.1  2006/06/26 20:09:42  smaher_cvs
//	Initial
//	
//	Revision 1.3  2006/05/10 17:29:11  smaher_cvs
