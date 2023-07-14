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

import gov.nasa.gsfc.irc.app.Irc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;

/**
 *  ArchiveCatalog maintains a list of user sessions. Each user session has
 *  a list of archived files along with their start and stop times.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 1, 2005 3:32:10 PM
 *  @author Peyush Jain
 */

public class ArchiveCatalog
{
    private static final String CLASS_NAME = ArchiveCatalog.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    private final static String PROP_ARCHIVE_DIRECTORY = "irc.archive.rootDirectory";
    public final static String CATALOG_FILE = "Catalog.xml";
    private static String fFileName;
    
    private static ArchiveCatalog fCatalog = null;

    private Vector fSessions;
    
    
    /**
     * Constructor
     */
    public ArchiveCatalog()
    {
        //setup data directory
        String dir = Irc.getPreference(PROP_ARCHIVE_DIRECTORY);        
        fFileName = dir + File.separator + CATALOG_FILE;

        fSessions = new Vector();
    }

    /**
     * Gets all the sessions stored in the catalog.
     * 
     * @return Returns the sessions.
     */
    public Vector getSessions()
    {
        return fSessions;
    }
    
    /**
     * Adds the given session to the catalog.
     * 
     * @param session
     */
    public void addSession(ArchiveSession session)
    {
        fSessions.add(session);
    }
    
    /**
     * Removes the given session from the catalog.
     * 
     * @param session
     */
    public void removeSession(ArchiveSession session)
    {
        fSessions.remove(session);
    }
    
    /**
     * Gets the ArchiveCatalog.
     * 
     * @return ArchiveCatalog
     */
    public static ArchiveCatalog getArchiveCatalog()
    {
        if(fCatalog == null)
        {
            fCatalog = new ArchiveCatalog();
            
            //no need to validate when we have just created the catalog
            readFromCatalogFile();
        }
        else
        {
            validate();
        }
        
        return fCatalog;
    }
    
    /**
     * Reads from the catalog file.
     */
    private static void readFromCatalogFile()
    {
        //create reader
        InputStreamReader inputStreamReader = null;
        
        try
        {
            inputStreamReader = new InputStreamReader(new FileInputStream(new File(fFileName)));
        }
        catch (FileNotFoundException fnfe)
        {
            String msg = fFileName + ": File Not Found";
            sLogger.logp(Level.WARNING, CLASS_NAME, "readFromCatalogFile", msg);
        }
        
        if(inputStreamReader != null)
        {
            //use xstream to deserialize xml to catalog
            XStream xstream = new XStream();
            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = "*** READING CATALOG ***";
                sLogger.logp(Level.INFO, CLASS_NAME, "readFromCatalogFile", msg);
            }

            fCatalog = (ArchiveCatalog)xstream.fromXML(inputStreamReader);            
            
            //close reader
            try
            {
                inputStreamReader.close();                
            }
            catch (IOException ioe)
            {
                String msg = "IOException";
                sLogger.logp(Level.WARNING, CLASS_NAME, "readFromCatalogFile", msg, ioe);
            }
        }
    }
    
    /**
     * Writes an ArchiveCatalog object to the catalog file.
     * 
     * @param catalog
     */
    public static void writeToCatalogFile()
    {
        //create writer
        OutputStreamWriter outputStreamWriter = null;
        try
        {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(fFileName)));
        }
        catch (FileNotFoundException fnfe)
        {
            String msg = fFileName + ": File Not Found";
            sLogger.logp(Level.WARNING, CLASS_NAME, "writeToCatalogFile", msg);
        }
        
        //use xstream to serialize catalog to xml
        XStream xstream = new XStream();
        if (sLogger.isLoggable(Level.INFO))
        {
            String msg = "*** WRITING CATALOG ***";
            sLogger.logp(Level.INFO, CLASS_NAME, "writeToCatalogFile", msg);
        }
        xstream.toXML(fCatalog, outputStreamWriter);
        
        //close writer
        try
        {
            outputStreamWriter.close();
        }
        catch (IOException ioe)
        {
            String msg = "IOException";
            sLogger.logp(Level.WARNING, CLASS_NAME, "writeToCatalogFile", msg, ioe);
        }
    }
    
    /**
     * Checks if the files referred to by the catalog are present, if not, the 
     * corresponding entries from the catalog are deleted. If after deleting 
     * the file entries, no files are left in a session then it is
     * also deleted from the catalog.
     */
    private static void validate()
    {
        Object tmpSessionsArray[] = fCatalog.getSessions().toArray();

        //loop through all the sessions in the catalog
        for (int i = 0; i < tmpSessionsArray.length; i++)
        {
            ArchiveSession session = (ArchiveSession)tmpSessionsArray[i];
            Object tmpFilesArray[] = session.getFiles().toArray();
            
            //loop through all the files in a session
            for (int j = 0; j < tmpFilesArray.length; j++)
            {
                ArchiveFileInfo file = (ArchiveFileInfo)tmpFilesArray[j];
                File f = new File(file.getFileName());
                
                //if the file does not exist then remove the entry from
                //the catalog
                if(!f.exists())
                {
                    String msg = "File: "+file.getFileName()+" does not exist."
                                 +" Removing it from catalog...";
                    sLogger.logp(Level.WARNING, CLASS_NAME, "processArchiveCatalog", msg);

                    session.removeFile(file);
                }                    
            }
            
            //if there are no files in the session then delete the session
            if(session.getFiles().size() == 0)
            {
                String msg = "No files in the session. Removing it from catalog...";
                sLogger.logp(Level.WARNING, CLASS_NAME, "processArchiveCatalog", msg);

                fCatalog.removeSession(session);
            }
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ArchiveCatalog.java,v $
//  Revision 1.4  2005/10/14 21:44:57  pjain_cvs
//  Added validate method.
//
//  Revision 1.3  2005/10/14 13:48:01  pjain_cvs
//  Added getArchiveCatalog method and moved the initialization of fFileName
//  to the constructor.
//
//  Revision 1.2  2005/06/15 15:16:10  pjain_cvs
//  Moved dir and catalog file setup from constructor to read and write fxns.
//
//  Revision 1.1  2005/06/14 18:28:13  pjain_cvs
//  Adding to CVS
//
//