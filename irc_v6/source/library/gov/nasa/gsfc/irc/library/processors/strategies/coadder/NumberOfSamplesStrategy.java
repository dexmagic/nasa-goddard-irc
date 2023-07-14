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
 * Coadd after a certain number of samples have been read.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: NumberOfSamplesStrategy.java,v 1.3 2006/05/10 17:29:11 smaher_cvs Exp $
 * @author	$Author: smaher_cvs $
 */
public class NumberOfSamplesStrategy implements CoadderStrategy {
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger
			.getLogger(NumberOfSamplesStrategy.class.getName());


	private static final int DEFAULT_NUMBER_OF_SAMPLES = 100;
	
	private int fNumberOfSamplesInCoadds = DEFAULT_NUMBER_OF_SAMPLES;  // default
    
    /**
     * 
     */
    public NumberOfSamplesStrategy()
    {
    	fNumberOfSamplesInCoadds = DEFAULT_NUMBER_OF_SAMPLES;
    }
    
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy#getCoaddIndexPoints(gov.nasa.gsfc.irc.data.BasisSet, int)
     */
    public int[] getCoaddIndexPoints(BasisSet basisSet, int numberSamplesSinceLastCoadd)
    {
//        ArrayList indexPoints = new ArrayList();
        // Why "+ 1" below? We may need space for 
        // CoadderStrategy.INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS token at the beginning    	
        int[] indexPoints = new int[basisSet.getSize() + 1];
        int numberOfIndexPoints = 0;
        
        int nextCoaddIndex = -1;
        
        // The "- 1" is to work with indices ..
        if (numberSamplesSinceLastCoadd  >= fNumberOfSamplesInCoadds - 1)
        {
            // Do an immediate coadd
            nextCoaddIndex = 0;
        }
        else
        {
            nextCoaddIndex = fNumberOfSamplesInCoadds - numberSamplesSinceLastCoadd - 1;
        }
        int basisSetSize = basisSet.getSize();
        while (nextCoaddIndex < basisSetSize)
        {
//            indexPoints.add(new Integer(nextCoaddIndex));
            indexPoints[numberOfIndexPoints++] = nextCoaddIndex;
            nextCoaddIndex += fNumberOfSamplesInCoadds;
        }
        
        // Trim the array to the right size
        int[] trimmedIndexPoints = new int[numberOfIndexPoints];
        System.arraycopy(indexPoints, 0, trimmedIndexPoints, 0, numberOfIndexPoints);
        return trimmedIndexPoints;
    }

    /**
     * @return number of samples in coadds
     */
    public int getNumberOfSamplesInCoadds() {
        return fNumberOfSamplesInCoadds;
    }

    /**
     * Set number of samples in coadds.  If 0 or 1 samples is specified, no coadding
     * is done.  If the number of samples is less than 0, an IllegalArgumentException is thrown.
     * @param numberOfSamplesInCoadds
     * @throws PropertyVetoException 
     * @throws IllegalArgumentException
     */
    public void setNumberOfSamplesInCoadds(int numberOfSamplesInCoadds) throws PropertyVetoException {
    	if (numberOfSamplesInCoadds > 1)
        {
    		fNumberOfSamplesInCoadds = numberOfSamplesInCoadds;
        }
    	else if (numberOfSamplesInCoadds == 0 || numberOfSamplesInCoadds == 1) 
    	{
    		// This is more to guide the user; if they don't want coadding this algorithm should
    		// be removed since it introduces overhead
    		String mesg = "Number of samples for coadding should be 2 or greater";
			sLogger.warning(mesg);
    		fNumberOfSamplesInCoadds = 1;    		
    	}
    	else
    	{
    		// This is more to guide the user; if they don't want coadding this algorithm should
    		// be removed since it introduces overhead
    		throw new PropertyVetoException("Illegal number of samples for coadding: " + numberOfSamplesInCoadds, new PropertyChangeEvent(this, "NumberOfSamplesInCoadds", new Integer(fNumberOfSamplesInCoadds), new Integer(numberOfSamplesInCoadds)));
    	}    	
    }
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: NumberOfSamplesStrategy.java,v $
//	Revision 1.3  2006/05/10 17:29:11  smaher_cvs
//	Changed coadd index array to be a primitive array; fixed some bugs; have a working chop coadder with this checkin.
//	
//	Revision 1.2  2005/09/25 02:22:29  smaher_cvs
//	Checkpointing DataThresholdStrategy
//	
//	Revision 1.1  2005/09/23 20:56:45  smaher_cvs
//	Implemented and tested new Coadder with strategy pattern
//	
//	Revision 1.3  2005/09/19 14:57:42  smaher_cvs
//	Remove need for 1.5 autoboxing.
//	
//	Revision 1.2  2005/09/16 20:58:43  smaher_cvs
//	Checkpointing Coadder Strategy design.
//	
//	Revision 1.1  2005/09/16 20:41:52  smaher_cvs
//	Checkpointing Coadder performance improvements.
//	