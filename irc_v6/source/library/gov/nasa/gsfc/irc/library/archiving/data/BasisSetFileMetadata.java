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

package gov.nasa.gsfc.irc.library.archiving.data;

import gov.nasa.gsfc.irc.data.BasisBundleId;

import java.io.Serializable;

/**
 *  BasisSetFileMetadata contains the elements of metadata in the 
 *  BasisSetArchive (.bsa) file.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Apr 22, 2005 10:13:28 AM
 *  @author Peyush Jain
 */

public class BasisSetFileMetadata implements Serializable
{
    private int fSamples;
    private long fTime;
    private BasisBundleId fBasisBundleId;    
    
    /**
     * Constructor
     * 
     * @param samples The number of samples
     */
    public BasisSetFileMetadata(int samples)
    {
        super();
        fSamples = samples;
    }
    
    /**
     * Constructor
     * 
     * @param samples The number of samples
     * @param time The time at which data was received
     */
    public BasisSetFileMetadata(int samples, long time)
    {
        super();
        fSamples = samples;
        fTime = time;
    }    
    
    /**
     * Constructor
     * 
     * @param samples The number of samples
     * @param basisBundleId The basisBundleId for the source of data
     */
    public BasisSetFileMetadata(int samples, BasisBundleId basisBundleId)
    {
        super();
        fSamples = samples;
        fBasisBundleId = basisBundleId;
    }
    
    /**
     * Constructor
     * 
     * @param samples The number of samples
     * @param time The time at which data was received
     * @param basisBundleId The basisBundleId for the source of data
     */
    public BasisSetFileMetadata(int samples, long time,
            BasisBundleId basisBundleId)
    {
        super();
        fSamples = samples;
        fTime = time;
        fBasisBundleId = basisBundleId;
    }
    
    /**
     * @return Returns the samples.
     */
    public int getSamples()
    {
        return fSamples;
    }
    
    /**
     * @return Returns the time.
     */
    public long getTime()
    {
        return fTime;
    }
    
    
    /**
     * @return Returns the basisBundleId.
     */
    public BasisBundleId getBasisBundleId()
    {
        return fBasisBundleId;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileMetadata.java,v $
//  Revision 1.1  2005/05/02 15:15:57  pjain_cvs
//  Adding to CVS.
//
//