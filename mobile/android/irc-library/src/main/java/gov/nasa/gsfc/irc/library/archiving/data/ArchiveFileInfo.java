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

/**
 *  ArchiveFileInfo stores information about the archived file, e.g.,
 *  file name and start/stop time.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 3, 2005 9:41:26 AM
 *  @author Peyush Jain
 */

public class ArchiveFileInfo
{
    private long fStartTime;
    private long fStopTime;        
    private String fFileName;
    
    /**
     * @return Returns the fileName.
     */
    public String getFileName()
    {
        return fFileName;
    }
    
    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName)
    {
        fFileName = fileName;
    }
    
    /**
     * @return Returns the startTime.
     */
    public long getStartTime()
    {
        return fStartTime;
    }
    
    /**
     * @param startTime The startTime to set.
     */
    public void setStartTime(long startTime)
    {
        fStartTime = startTime;
    }
    
    /**
     * @return Returns the stopTime.
     */
    public long getStopTime()
    {
        return fStopTime;
    }
    
    /**
     * @param stopTime The stopTime to set.
     */
    public void setStopTime(long stopTime)
    {
        fStopTime = stopTime;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ArchiveFileInfo.java,v $
//  Revision 1.1  2005/06/14 18:31:06  pjain_cvs
//  Adding to CVS
//
//