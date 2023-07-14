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

import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  BasisSetFileWriteProcessor creates BasisSetArchive (.bsa) files in user
 *  specified directory. It creates 3 maps: WritersMap, SessionMap, and TimeMap.
 *  In all these maps BasisBundleId is kept as a key. WritersMap stores
 *  ArchiveFileWriters (ObjectOutputStream and FileChannel) to write data to a
 *  file, SessionMap stores a session which contains a list of files that 
 *  belong to that session, and TimeMap stores the last time basisSet was 
 *  received as values.
 *  
 *  Other implementation details:
 *  There are 3 cases for writing the data to a file:
 *  (1) If we get a basisSet with a new BasisBundleId (i.e., BasisBundleId is
 *      not in the map), then create a ArchiveFileWriters object and 
 *      store BasisBundleId and ArchiveFileWriters objects in WritersMap. Also,
 *      create a new ArchiveSession object and add a ArchiveFileInfo object in 
 *      it. Then, set the startTime and fileName for this ArchiveFileInfo
 *      object and write to the file using the new ArchiveFileWriters object. 
 *  (2) If by adding more data to the current file, the file size limit exceeds
 *      then close the current file (set the stopTime for the ArchiveFileInfo
 *      using TimeMap) and create new ArchiveFileWriters object (for the new 
 *      file). Then, overwrite the old ArchiveFileWriters object in the 
 *      WritersMap with this new one and use it to write to the new file. In 
 *      this case, retreive the current session from SessionMap and add 
 *      ArchiveFileInfo (set startTime, fileName) for this new file to it. 
 *  (3) If none of the above cases apply, then get the ArchiveFileWriters
 *      object (corresponding to the given basisBundleId) and write to the 
 *      current file.
 * 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 3, 2005 11:14:53 AM
 *  @author Peyush Jain
 */

public class BasisSetFileWriteProcessor extends BasisSetProcessor
{
    private static final String CLASS_NAME = BasisSetFileWriteProcessor.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
    public static final String DEFAULT_NAME = "BasisSet File Write Processor";
    
    public static final byte BYTE_TYPE = 0;
    public static final byte CHAR_TYPE = 1;
    public static final byte SHORT_TYPE = 2;
    public static final byte INT_TYPE = 3;
    public static final byte LONG_TYPE = 4;
    public static final byte FLOAT_TYPE = 5;
    public static final byte DOUBLE_TYPE = 6;
    public static final byte OBJECT_TYPE = 7;
    
    //property that specifies directory where data files should be stored
    private final static String PROP_ARCHIVE_DIRECTORY = "irc.archive.rootDirectory";

    //directory where data files should be stored
    private File fRootDirectory = null;
    
    private BasisSetFileNameGenerator fFileNameGenerator = BasisSetFileNameGenerator.getInstance();
    
    private ArchiveCatalog fCatalog; 
        
    private int fFileSize = 30000000; //file size in bytes (30MB)
    
    private Map fWritersMap;//mapping between basisBundleId and writers for a file
    private Map fSessionMap;//mapping between basisBundleId and sessionId
    private Map fTimeMap; //mapping between basisBundleId and time for the last basisSet
    
    private int fSessionId;
    
    
    /**
     * Constructor
     */
    public BasisSetFileWriteProcessor()
    {
        super(DEFAULT_NAME);

        fWritersMap = new HashMap();
        fTimeMap = new HashMap();
        fSessionMap = new HashMap();
                
        //setup data directory
        String dir = Irc.getPreference(PROP_ARCHIVE_DIRECTORY);        
        setRootDirectory(new File(dir));        
    }

    /**
     * Causes this processor to process the given BasisSet.
     * 
     * @param basisSet   contains data to be written
     */
    protected synchronized void processBasisSet(BasisSet basisSet)
    {        
        long time = System.currentTimeMillis();
        BasisBundleId basisBundleId = basisSet.getBasisBundleId();
        
        try
        {
            int type = newWritersNeeded(basisSet);
            
            if(type == 0)
            {
                //write basisSet and time to already opened (existing) file
                writeToFile(basisSet, time);
            }
            else                
            {
                String fileName = fRootDirectory.toString() + File.separator 
                                            + fFileNameGenerator.getFileName();
                
                if (sLogger.isLoggable(Level.FINE))
                {
                    String msg = "Creating file: "+fileName;
                    sLogger.logp(Level.FINE, CLASS_NAME, "processBasisSet", msg);
                }
                
                if(type == 1)
                {
                    //new basisSet structure
                    fSessionMap.put(basisBundleId, new Integer(fSessionId));

                    createWriters(basisSet, fileName);                    
                    
                    //create a new session and a new file (with start time)
                    ArchiveSession session = new ArchiveSession();
                    session.setBasisBundleId(basisBundleId);
                    fCatalog.addSession(session);
                    startFileInSession(session, fileName, time);
                    
                    //increase count for the next session or source
                    fSessionId++; 
                }
                else if(type == 2)
                {
                    //write the stop time for the file in the existing session
                    endFileInSession(basisBundleId);
                    
                    createWriters(basisSet, fileName);
                    
                    //setting up for a new file in existing session and
                    //set start time for the new file
                    int sessionId = ((Integer)fSessionMap.get(basisBundleId)).intValue();
                    ArchiveSession session = 
                        (ArchiveSession)fCatalog.getSessions().elementAt(sessionId);
                    
                    startFileInSession(session, fileName, time); 
                }
                
                //write descriptors to the new file
                ArchiveFileWriters w = (ArchiveFileWriters)fWritersMap.get(basisBundleId);
                w.getObjectOutputStream().writeObject(basisSet.getDescriptor());
                
                //write basisSet and time to the file
                writeToFile(basisSet, time);
            }
        }
        catch (IOException ioe)
        {
            String msg = "IOException";
            sLogger.logp(Level.WARNING, CLASS_NAME, "processBasisSet", msg, ioe);
        }

        //store time with bundle id (used as prevTime...)
        fTimeMap.put(basisBundleId, new Long(time));
    }
    
    /**
     * It returns 1 or 2 if new writers are needed and 0 otherwise.
     * 1 is returned if data is received from a new basisSet  
     * structure. 2 is returned if data is received from an existing
     * basisSet structure and file limit is reached.
     * 
     * @return type
     */
    private int newWritersNeeded(BasisSet basisSet) throws IOException
    {
        BasisBundleId basisBundleId = basisSet.getBasisBundleId();
        
        //return 1, if basisBundleId does not exist in the Hashmap
        if (!fWritersMap.containsKey(basisBundleId))
        {
            return 1;
        }
        else if (fWritersMap.get(basisBundleId) != null)
        {
            FileChannel tempFileChannel = 
                ((ArchiveFileWriters)fWritersMap.get(basisBundleId)).getFileChannel();
            long newSize = tempFileChannel.position();
            
            //return 2, if adding more data will exceed the limit
            //for the file size
            if (newSize > fFileSize)
            {
                System.out.println("newSize: "+newSize);
                System.out.println("fFileSize: "+fFileSize);
                //close the file
                tempFileChannel.close();
                return 2;
            }
        }
        
        //return 0, if new writers are not needed
        return 0;
    } 
    
    /**
     * Creates writers (ObjectOutputStream and FileChannel) for the given file
     * 
     * @param basisSet    used to get basisBundleId which is used as a key to
     *                    access writers for the given file
     * @param fileName    file for which writers are created
     * @throws IOException
     */
    private void createWriters(BasisSet basisSet, String fileName) throws IOException
    {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName));

        //to write descriptors and metadata to the file
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        //to write data (ByteBuffers) to the file
        FileChannel fileChannel = fileOutputStream.getChannel();

        //create an object for storing FileChannel, ObjectOutputStream 
        ArchiveFileWriters w = new ArchiveFileWriters(fileChannel, objectOutputStream);

        //store the above object with a unique basisBundleId
        ArchiveFileWriters tmp = 
            (ArchiveFileWriters)fWritersMap.put(basisSet.getBasisBundleId(), w);
        
        //close file if a new writers object replaces an old one
        if(tmp != null)
        {
            tmp.getFileChannel().close();
        }
    }
    
    /**
     * Creates a ArchiveFileInfo object with the start time and fileName. Then,
     * it adds this object tot the given session.
     * 
     * @param session   ArchiveFileInfo is added to this session
     * @param fileName   used to store it in ArchiveFileInfo
     * @param time   used to store it in ArchiveFileInfo as startTime for
     *               the given fileName
     */
    private void startFileInSession(ArchiveSession session, String fileName, long time)
    {
        ArchiveFileInfo fileInfo = new ArchiveFileInfo();
        
        fileInfo.setStartTime(time);
        fileInfo.setFileName(fileName);
        session.addFile(fileInfo);

        //set the start time for this session
        session.setStartTime(time);
    }

    /**
     * Sets the stoptime for the file archiving data from the source
     * with the given basisBundleId. 
     * 
     * @param basisBundleId
     */
    private void endFileInSession(BasisBundleId basisBundleId)
    {
        long prevTime = ((Long)fTimeMap.get(basisBundleId)).longValue();
        int sessionId = ((Integer)fSessionMap.get(basisBundleId)).intValue();
        
        ArchiveSession session = 
            (ArchiveSession)fCatalog.getSessions().elementAt(sessionId);
        ArchiveFileInfo fileInfo = 
            (ArchiveFileInfo)session.getFiles().lastElement();
        
        fileInfo.setStopTime(prevTime);
        
        //every time a file is closed in a session, its stop time is updated
        session.setStopTime(prevTime);
    }       
    
    /**
     * Writes the basisSet (metadata and data) to the file.
     * 
     * @param basisSet   contains data to be written
     */
    private void writeToFile(BasisSet basisSet, long time) throws IOException
    {
        //get objectOutputStream to write to the file
        ObjectOutputStream objectOutputStream = 
            ((ArchiveFileWriters)fWritersMap.get(basisSet.getBasisBundleId()))
            .getObjectOutputStream();
        
        //get basis and data buffers
        DataBuffer basisBuffer = basisSet.getBasisBuffer();
        BasisBundleId basisBundleId = basisSet.getBasisBundleId();
        int samples = basisBuffer.getSize();
        
        BasisSetFileMetadata metadata = 
            new BasisSetFileMetadata(samples, time, basisBundleId);
        
        //write metadata
        objectOutputStream.writeObject(metadata);
        
        if(sLogger.isLoggable(Level.INFO))
        {
            String msg = "Writing Data... Samples: "+samples;
            sLogger.logp(Level.INFO, CLASS_NAME, "writeToFile", msg);
        }
        
        //write data
        writeDataBuffer(objectOutputStream, basisSet.getBasisBuffer());        
        for (Iterator buffers = basisSet.getDataBuffers(); buffers.hasNext();)
        {
            writeDataBuffer(objectOutputStream, (DataBuffer) buffers.next());
        }

        objectOutputStream.flush();
    }

    /**
     * Writes the DataBuffer to the given output stream.
     * 
     * @param outputStream the stream to write to
     * @param buffer the DataBuffer to write out.
     * @throws IOException if there is an exception writing to the stream.
     */
    private void writeDataBuffer(
            ObjectOutputStream outputStream, DataBuffer buffer) throws IOException
    {
        int size = buffer.getSize();
        Class type = buffer.getDataBufferType();
        
        if (type == double.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeDouble(buffer.getAsDouble(i));
            }
        }
        else if (type == int.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeInt(buffer.getAsInt(i));
            }
        }
        else if (type == float.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeFloat(buffer.getAsFloat(i));
            }
        }
        else if (type == short.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeShort(buffer.getAsShort(i));
            }
        }
        else if (type == long.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeLong(buffer.getAsLong(i));
            }
        }
        else if(type == byte.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeByte(buffer.getAsByte(i));
            }
        }
        else if (type == char.class)
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeChar(buffer.getAsChar(i));
            }
        }
        else    // Type must be Object or subclass
        {
            for (int i = 0; i < size; ++i)
            {
                outputStream.writeObject(buffer.getAsObject(i));
            }
        }
    }
    
    /**
     * @return Returns the fileSize.
     */
    public int getFileSizeInMB()
    {
        return fFileSize / 1000000;
    }
    
    /**
     * @param fileSize The fileSize to set.
     */
    public void setFileSizeInMB(int fileSize)
    {
        fFileSize = fileSize * 1000000;
    }
    
    /**
     *  Causes this Component to start.
     */
    public void start()
    {
        if (!isStarted())
        {
            super.start();

            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = getFullyQualifiedName() + " has started";
                sLogger.logp(Level.INFO, CLASS_NAME, "start", msg);
                
                msg = "Archiver: STARTED ...";
                sLogger.logp(Level.INFO, CLASS_NAME, "start", msg);
            }
            
            //get the catalog
            fCatalog = ArchiveCatalog.getArchiveCatalog();
            
            //initialize session id
            fSessionId = fCatalog.getSessions().size();
        }
    }
    
    /**
     *  Causes this Component to stop.
     */
    public synchronized void stop()
    {
        super.stop();

        try
        {
            closeAllFiles();
        }
        catch (RuntimeException e)
        {
            String msg = "Exception while closing file ";
            sLogger.logp(Level.WARNING, CLASS_NAME, "stop", msg, e);
        }

        if (sLogger.isLoggable(Level.INFO))
        {
            String msg = getFullyQualifiedName() + " has stopped";
            sLogger.logp(Level.INFO, CLASS_NAME, "stop", msg);
            
            msg = "Archiver: STOPPED ...";
            sLogger.logp(Level.INFO, CLASS_NAME, "stop", msg);
        }
    }

    /** 
     * Removes the entry for the given BasisBundleId from the map and
     * closes the corresponding file.
     * 
     * @see gov.nasa.gsfc.irc.algorithms.DefaultProcessor#handleInputBasisBundleClosing(gov.nasa.gsfc.irc.data.DefaultBasisBundleId)
     */
    protected void handleInputBasisBundleClosing(BasisBundleId inputBasisBundleId)
    {
        super.handleInputBasisBundleClosing(inputBasisBundleId);
        
        //remove inputBasisBundleId from the map
        if(fWritersMap.containsKey(inputBasisBundleId))
        {
            fWritersMap.remove(inputBasisBundleId);
            
            //close the file corresponding to inputBasisBundleId 
            closeFile(inputBasisBundleId);
        }
//        else
//        {
//            String msg = inputBasisBundleId.getFullyQualifiedName()
//                            +" NOT FOUND.";
//            sLogger.logp(Level.WARNING, CLASS_NAME, 
//                    "handleInputBasisBundleClosing", msg);
//        }        
    }
    
    /**
     * Removes the entry for the given BasisBundleId from the map and
     * closes the corresponding file. 
     * 
     * @see gov.nasa.gsfc.irc.algorithms.DefaultProcessor#handleNewInputBasisBundleStructure(gov.nasa.gsfc.irc.data.DefaultBasisBundleId, gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor)
     */
    protected void handleNewInputBasisBundleStructure
    		(BasisBundleId inputBasisBundleId, BasisBundleDescriptor inputDescriptor)
    {
        super.handleNewInputBasisBundleStructure(inputBasisBundleId,
                inputDescriptor);
        
        //Note: When the first time a basisSet with this new descriptor is 
        //received (in processBasisSet()), the basisBundleId is added to the map 
        //and the BasisSetFileName class gives its files a new "_bbb_". 
        //Therefore, it is safe to delete inputBasisBundleId from the map.
        
        //remove inputBasisBundleId from the map
        if(fWritersMap.containsKey(inputBasisBundleId))
        {
            fWritersMap.remove(inputBasisBundleId);
            
            //close the file corresponding to inputBasisBundleId 
            closeFile(inputBasisBundleId);
        }
//        else
//        {
//            String msg = inputBasisBundleId.getFullyQualifiedName()
//                            +" NOT FOUND.";
//            sLogger.logp(Level.WARNING, CLASS_NAME, 
//                    "handleInputBasisBundleClosing", msg);
//        }        
    }
    
    /**
     * Close output files.
     */
    private void closeFile(BasisBundleId basisBundleId)
    {
        if(fWritersMap.containsKey(basisBundleId))
        {
            try
            {
                endFileInSession(basisBundleId);                
                
                ((ArchiveFileWriters)fWritersMap.get(basisBundleId))
                    .getFileChannel().close();
            }
            catch (IOException ioe)
            {
                String msg = "IOException";
                sLogger.logp(Level.WARNING, CLASS_NAME, "closeFile", msg, ioe);
            }
        }
        else
        {
            // TODO Start/Stop component. Then, start component again to 
            //get this error.
            String msg = "BasisBundleId = "+basisBundleId.getFullyQualifiedName()
                    + " NOT FOUND. Unable to close corresponding file.";
            sLogger.logp(Level.WARNING, CLASS_NAME, "closeFile", msg);
        }
    }

    /**
     * Close all output files.
     */
    private void closeAllFiles()
    {
        //TODO should this delete all the entries from all the maps???
        
        //close all file channels in the map
        Iterator i = fWritersMap.keySet().iterator();
        while(i.hasNext())
        {
            closeFile((BasisBundleId)i.next());
        }
        
        ArchiveCatalog.writeToCatalogFile();
    }

    /**
     * Set the directory that data files will be placed in. If the given
     * directory does not exist or cannot be created then the current property
     * setting will not be changed.
     * 
     * @param directory The root directory to set.
     */
    private void setRootDirectory(File directory)
    {
        File tempDirectory = directory;

        // Check that the file represented by the property change
        // is non null and not an empty string
        if ((tempDirectory != null)
                & (tempDirectory.getPath().trim().length() > 0))
        {
            // Verify that the path can be created
            if (!FileUtil.checkDirectory(tempDirectory))
            {
                String msg = "Could not create: " + tempDirectory;
                sLogger.logp(Level.WARNING, CLASS_NAME, "setRootDirectory", msg);
            }
            else
            {
                fRootDirectory = tempDirectory;
            }
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileWriteProcessor.java,v $
//  Revision 1.9  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.8  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.7  2005/10/14 21:46:23  pjain_cvs
//  Removed processArchiveCatalog method and added some logging.
//
//  Revision 1.6  2005/10/14 13:56:56  pjain_cvs
//  Added startTime and stopTime to the session. Also, simplified start method.
//
//  Revision 1.5  2005/07/27 18:58:06  pjain_cvs
//  Updated for new DataBuffer architecture
//
//  Revision 1.4  2005/07/22 19:44:17  pjain_cvs
//  Changed newWritersNeeded() such that new archive file is created
//  after user's file size limit is passed
//
//  Revision 1.3  2005/07/18 14:24:23  pjain_cvs
//  Updated to reflect changes to BasisSet and DataBuffer classes
//
//  Revision 1.2  2005/06/16 20:33:50  pjain_cvs
//  Adding to CVS
//
//