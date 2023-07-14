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

import java.util.Date;
import java.util.Vector;

/**
 *  ArchiveSession stores a vector of ArchiveFileInfo objects that belong
 *  to this session. Also, it stores sources' BasisBundleId from the which 
 *  data is received and archived. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 2, 2005 2:52:01 PM
 *  @author Peyush Jain
 */

public class ArchiveSession
{
    private BasisBundleId fBasisBundleId;
    private long startTime; //start time for this session
    private long stopTime; //stop time for this session
    private Vector fFiles; //file infos stored under this session
    
    /**
     * Constructor
     */
    public ArchiveSession()
    {
        fFiles = new Vector();
    }    

    /**
     * @return Returns the basisBundleId.
     */
    public BasisBundleId getBasisBundleId()
    {
        return fBasisBundleId;
    }
    
    /**
     * @param basisBundleId The basisBundleId to set.
     */
    public void setBasisBundleId(BasisBundleId basisBundleId)
    {
        fBasisBundleId = basisBundleId;
    }
    
    /**
     * Gets all the ArchiveFileInfo objects in this session.
     * 
     * @return
     */
    public Vector getFiles()
    {
        return fFiles;
    }
    
    /**
     * @return Returns the startTime.
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime The startTime to set.
     */
    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    /**
     * @return Returns the stopTime.
     */
    public long getStopTime()
    {
        return stopTime;
    }

    /**
     * @param stopTime The stopTime to set.
     */
    public void setStopTime(long stopTime)
    {
        this.stopTime = stopTime;
    }

    /**
     * Add the given ArchiveFileInfo to the session.
     * 
     * @param file
     */
    public void addFile(ArchiveFileInfo file)
    {
        fFiles.add(file);
    }
        
    /**
     * Remove the given ArchiveFileInfo from the session.
     * 
     * @param file
     */
    public void removeFile(ArchiveFileInfo file)
    {
        fFiles.remove(file);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return fBasisBundleId.toString()
            + ": "
            + (new Date(startTime)).toString()
            + " to "
            + (new Date(stopTime)).toString();
    }
    
    
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ArchiveSession.java,v $
//  Revision 1.2  2005/10/14 13:49:01  pjain_cvs
//  Added fields for startTime and stopTime. Also added corresponding getters
//  and setter methods.
//
//  Revision 1.1  2005/06/14 19:00:38  pjain_cvs
//  Adding to CVS
//
//