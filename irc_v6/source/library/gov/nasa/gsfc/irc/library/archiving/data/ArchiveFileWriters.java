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

import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;

/**
 *  ArchiveFileWriters stores FileChannel and ObjectOutputStream to
 *  write data to a particular file.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 2, 2005 10:29:10 AM
 *  @author Peyush Jain
 */

public class ArchiveFileWriters
{
    private FileChannel fFileChannel;
    private ObjectOutputStream fObjectOutputStream;
    
    /**
     * Constructor
     */
    public ArchiveFileWriters(FileChannel fileChannel, ObjectOutputStream objectOutputStream)
    {
        super();
        fFileChannel = fileChannel;
        fObjectOutputStream = objectOutputStream;
    }

    /**
     * @return Returns the fileChannel.
     */
    public FileChannel getFileChannel()
    {
        return fFileChannel;
    }
    
    /**
     * @param fileChannel The fileChannel to set.
     */
    public void setFileChannel(FileChannel fileChannel)
    {
        fFileChannel = fileChannel;
    }

    /**
     * @return Returns the outputStreamWriter.
     */
    public ObjectOutputStream getObjectOutputStream()
    {
        return fObjectOutputStream;
    }
    
    /**
     * @param outputStreamWriter The outputStreamWriter to set.
     */
    public void setOutputStreamWriter(ObjectOutputStream objectOutputStream)
    {
        fObjectOutputStream = objectOutputStream;
    }        
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ArchiveFileWriters.java,v $
//  Revision 1.1  2005/06/14 18:54:16  pjain_cvs
//  Adding to CVS
//
//