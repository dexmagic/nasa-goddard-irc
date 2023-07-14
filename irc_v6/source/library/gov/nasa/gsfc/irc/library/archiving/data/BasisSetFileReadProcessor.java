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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.irc.algorithms.DefaultProcessor;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;

/**
 *  BasisSetFileReadProcessor processes BasisSetArchive (.bsa) files. It
 *  gets the selected rows from the archived sessions table, reads the 
 *  corresponding files (with descriptor, metadata, and data), creates a
 *  basisSet with this information and makes it available to the interested
 *  readers. Also, it uses different threads to read sessions with
 *  different bundleIds.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 6, 2005 3:20:42 PM
 *  @author Peyush Jain
 */

public class BasisSetFileReadProcessor extends DefaultProcessor
{
    private static final String CLASS_NAME = BasisSetFileReadProcessor.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
    public static final String DEFAULT_NAME = "BasisSet File Read Processor";
        
    private Output fOutput;
    private Map fDescriptorMap;//key: descriptor's fullyQualifiedName, value: basisBundleId

    /**
     * Constructor
     * 
     * @param reader
     */
    public BasisSetFileReadProcessor()
    {
    		super(DEFAULT_NAME);
    		
        fDescriptorMap = new HashMap();
    }
    
    //RunnableFileReader reads files from the given sessions (Note: these 
    //sessions have the same bundleId). Descriptor, metadata, and data are read
    //from each file. Data is made available to the readers at the same rate
    //with which it was recorded. This is done by sleeping for the difference 
    //of the timestamps of the two sets of data. Note that these timestamps
    //were initially recorded when the data was archived and is part of
    //the metadata.
    private class RunnableFileReader implements Runnable
    {
        private long fPrevTime = 0;
        BasisBundleId fBasisBundleId = null;
        Vector fSessions;

        /**
         * Constructor
         */
        public RunnableFileReader(Vector sessions)
        {
            fSessions = sessions;
        }

        /** 
         * This method reads the files from all the given sessions.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            for(int i = 0; i < fSessions.size(); i++)
            {
                ArchiveSession session = (ArchiveSession)fSessions.elementAt(i);
                for (int j = 0; j < session.getFiles().size(); j++)
                {
                    String fileName = 
                        ((ArchiveFileInfo)session.getFiles().elementAt(j)).getFileName();
                    
                    readFromFile(fileName);
                }
            }
            
            //finished reading data... stop component
            if (getManager() instanceof Startable)
            {
                ((Startable)getManager()).stop();
            }
        }

        private void readFromFile(String fileName)
        {
            FileInputStream fileInputStream = null;
            ObjectInputStream objectInputStream = null;
            
            fOutput = getOutput();
            
            try
            {
                fileInputStream = new FileInputStream(new File(fileName));
                objectInputStream = new ObjectInputStream(fileInputStream);
                                
                BasisBundleDescriptor descriptor;
                BasisSetFileMetadata metadata;
                BasisSet outputBasisSet;
                long currTime;

                //read descriptor
                descriptor = readDescriptor(objectInputStream);
                
                //If descriptor's name exists in the map then use its value as the
                //the basisbundleId otherwise create a new one and add it to the map
                if(fDescriptorMap.containsKey(descriptor.getFullyQualifiedName()))
                {
                		fBasisBundleId = (BasisBundleId) 
                    		fDescriptorMap.get(descriptor.getFullyQualifiedName());                   
                    fOutput.startNewBasisSequence(fBasisBundleId);
                }
                else
                {
                		fBasisBundleId = fOutput.addBasisBundle(descriptor, 5000);
                    
                    fDescriptorMap.put(descriptor.getFullyQualifiedName(), fBasisBundleId);
                }
                
                while(fileInputStream.available() > 0)
                {
                    //read metadata
                    metadata = readMetadata(objectInputStream);
                    
                    currTime = metadata.getTime();
                    
                    try
                    {
                        //to avoid sleeping on the first chunk of data
                        if(fPrevTime != 0)
                        {
                            long t = currTime - fPrevTime;
                            
                            if (sLogger.isLoggable(Level.FINE))
                            {
                                String msg = "Sleeping for "+t;
                                sLogger.logp(Level.FINE, CLASS_NAME, "readFromFile", msg);
                            }
                            
                            Thread.sleep(currTime - fPrevTime);
                        }
                    }
                    catch (InterruptedException ie)
                    {
                        String msg = "Interrupted Exception";
                        sLogger.logp(Level.WARNING, CLASS_NAME, "readFromFile", msg, ie);
                    }
                    
                    //set fPrevTime for the next chunk of data
                    fPrevTime = currTime;
                    
                    //read data
                    outputBasisSet = readData(objectInputStream, metadata.getSamples());

                    if (sLogger.isLoggable(Level.FINE))
                    {
                        String msg = "Making data available... ";
                        sLogger.logp(Level.FINE, CLASS_NAME, "readFromFile", msg);
                    }
                    
                    fOutput.makeAvailable(outputBasisSet);
                }

                if(fileInputStream.available() == 0)
                {
                    if (sLogger.isLoggable(Level.FINE))
                    {
                        String msg = "Finished...";
                        sLogger.logp(Level.FINE, CLASS_NAME, "readFromFile", msg);
                    }
                }                
            }
            catch (FileNotFoundException fnfe)
            {
                String msg = "File Not Found";
                sLogger.logp(Level.WARNING, CLASS_NAME, "readFromFile", msg, fnfe);
            }
            catch (IOException ioe)
            {
                String msg = "IOException";
                sLogger.logp(Level.WARNING, CLASS_NAME, "readFromFile", msg, ioe);
            }
            catch (ClassNotFoundException cnfe)
            {
                String msg = "Class Not Found";
                sLogger.logp(Level.WARNING, CLASS_NAME, "readFromFile", msg, cnfe);
            }
        }
        
        /**
         * This function returns a descriptor from the given objectInputStream.
         * 
         * @param objectInputStream
         * @return BasisBundleDescriptor
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private BasisBundleDescriptor readDescriptor(ObjectInputStream objectInputStream) 
                    throws IOException, ClassNotFoundException
        {
            BasisBundleDescriptor descriptor = null;            

            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = "Reading descriptor...";
                sLogger.logp(Level.INFO, CLASS_NAME, "readDescriptor", msg);
            }
            Object obj = objectInputStream.readObject();            

            if(obj instanceof BasisBundleDescriptor)
            {
                descriptor = (BasisBundleDescriptor)obj;
            }
            else
            {
                String msg = "Object type is NOT BasisBundleDescriptor";
                sLogger.logp(Level.SEVERE, CLASS_NAME, "readFile", msg);
            }
            return descriptor;
        }
        
        /**
         * This function returns BasisSetFileMetadata from the given
         * objectInputStream.
         * 
         * @param objectInputStream
         * @return BasisSetFileMetadata
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private BasisSetFileMetadata readMetadata(ObjectInputStream objectInputStream)
                    throws IOException, ClassNotFoundException
        {
            BasisSetFileMetadata metadata = null;

            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = "Reading Metadata...";
                sLogger.logp(Level.INFO, CLASS_NAME, "readMetadata", msg);
            }
            Object obj = objectInputStream.readObject();

            if(obj instanceof BasisSetFileMetadata)
            {
                metadata = (BasisSetFileMetadata)obj;
            }
            else
            {
                String msg = "Object type is NOT BasisSetFileMetadata" + obj.getClass();
                sLogger.logp(Level.SEVERE, CLASS_NAME, "readFile", msg);
            }
            return metadata;
        }
        
        /**
         * This function reads data from the given fileInputStream and then
         * returns it in a basisSet.
         * 
         * @param fileInputStream
         * @param samples
         * @return
         * @throws IOException
         */
        private BasisSet readData(ObjectInputStream objectInputStream, 
                                            int samples) throws IOException
        {
            BasisSet outputBasisSet;
            
            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = "Reading Data... Samples: "+ samples;
                sLogger.logp(Level.INFO, CLASS_NAME, "readData", msg);
            }
            
            if (sLogger.isLoggable(Level.FINE))
            {
                String msg = "Bytes to be processed: "+ objectInputStream.available();
                sLogger.logp(Level.FINE, CLASS_NAME, "readData", msg);
            }            
            
            outputBasisSet = fOutput.allocateBasisSet(fBasisBundleId, samples);
            
            //read in the basis channel
            readDataBuffer(objectInputStream, outputBasisSet.getBasisBuffer());
            
            for (Iterator buffers = outputBasisSet.getDataBuffers(); 
                buffers.hasNext();)
            {
                //read in the data channel
                readDataBuffer(objectInputStream, (DataBuffer) buffers.next());
            }                     
            
            return outputBasisSet;            
        }
        
        /**
         * Reads the DataBuffer from the given input stream.
         * 
         * @param inputStream the stream to read from
         * @param buffer the DataBuffer to read into.
         * @throws IOException if there is an exception reading from the stream.
         */
        private void readDataBuffer(ObjectInputStream inputStream, DataBuffer buffer) 
                                                            throws IOException
        {
            int size = buffer.getSize();
            Class type = buffer.getDataBufferType();
            
            if (type == double.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readDouble());
                }
            }
            else if (type == int.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readInt());
                }
            }
            else if (type == float.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readFloat());
                }
            }
            else if (type == short.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readShort());
                }
            }
            else if (type == long.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readLong());
                }
            }
            else if(type == byte.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readByte());
                }
            }
            else if (type == char.class)
            {
                for (int i = 0; i < size; ++i)
                {
                    buffer.put(i, inputStream.readChar());
                }
            }
            else    // Type must be Object or subclass
            {
                for (int i = 0; i < size; ++i)
                {
                    try
                    {
                        buffer.put(i, inputStream.readObject());
                    }
                    catch (ClassNotFoundException cnfe)
                    {
                        String msg = "Class Not Found";
                        sLogger.logp(Level.WARNING, CLASS_NAME, "readDataBuffer", msg, cnfe);
                    }
                }
            }
        }
    }
    
    /**
     *  Causes this Component to start.
     */
    public void start()
    {        
        ArchiveSession[] selRows = BasisSetFileController.sSelectedRows;
        
        //if none of the session rows are selected then stop Reader
        if(selRows == null || selRows.length == 0)
        {
            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = "None of the sessions are selected. Please select "
                        + "one or more sessions to read from.";
                sLogger.logp(Level.INFO, CLASS_NAME, "start", msg);
            }

            if (getManager() instanceof Startable)
            {
                ((Startable)getManager()).stop();
            }
        }
        else
        {
            super.start();

            Map map = new HashMap();

            //BasisBundleId is mapped to a vector with sessions (having
            //the same basisBundleId). This is done so that we can create
            //a new thread (to process data) for every basisBundleId.
            for(int i = 0; i < selRows.length; i++)
            {
                ArchiveCatalog catalog = ArchiveCatalog.getArchiveCatalog();
                ArchiveSession selSession = selRows[i];

                for (int j = 0; j < catalog.getSessions().size(); j++)
                {
                    ArchiveSession session = 
                        (ArchiveSession)catalog.getSessions().elementAt(j);

                    if(selSession == session)
                    {
                        if(map.containsKey(session.getBasisBundleId().getName()))
                        {
                            Vector sessions = (Vector)map.get(session.
                                    getBasisBundleId().getName());
                            sessions.add(session);
                        }
                        else
                        {
                            Vector sessions = new Vector();
                            sessions.add(session);
                            map.put(session.getBasisBundleId().getName(), sessions);
                        }
                    }
                }
            }
            
            //create a new thread for every basisBundleId (i.e., key in the map)
            Iterator iter = map.values().iterator();

            while(iter.hasNext())
            {
                Vector v = (Vector)iter.next();
                
                if (sLogger.isLoggable(Level.FINE))
                {
                    String msg = "Creating a new Thread...";
                    sLogger.logp(Level.FINE, CLASS_NAME, "start", msg);
                    msg = ((ArchiveSession)v.elementAt(0)).getBasisBundleId().getName();
                    sLogger.logp(Level.FINE, CLASS_NAME, "start", msg);                    
                }
                Thread t = new Thread(new RunnableFileReader(v));

                t.start();                
            }
            if (sLogger.isLoggable(Level.INFO))
            {
                String msg = "Reader STARTED...";
                sLogger.logp(Level.INFO, CLASS_NAME, "start", msg);
            }
        }        
    }
    
    /**
     *  Causes this Component to stop.
     */
    public void stop()
    {
        super.stop();
        
        if (sLogger.isLoggable(Level.INFO))
        {
            String msg = "Reader STOPPED...";
            sLogger.logp(Level.INFO, CLASS_NAME, "start", msg);
        }
    }

    /**
     *  Causes this Processor to process the given Dataset.
     * 
     *  @param dataSet A DataSet
     */

    protected void processDataSet(DataSet dataSet)
    {
        //We don't receive input, so we don't process it.
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileReadProcessor.java,v $
//  Revision 1.11  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.10  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/10/14 21:57:39  pjain_cvs
//  Added a call to fOutput.startNewBasisSequence() and changed some logging.
//
//  Revision 1.8  2005/10/14 15:43:04  pjain_cvs
//  Modified readFromFile method to check if basisBundleId already exists in the
//  descriptor map before creating a new one.
//
//  Revision 1.7  2005/10/14 14:01:42  pjain_cvs
//  Uses getArchiveCatalog method instead of readFromCatalogFile method to
//  get a reference to the catalog object.
//
//  Revision 1.6  2005/08/29 14:42:04  pjain_cvs
//  Stops the component after data is read.
//
//  Revision 1.5  2005/08/29 13:55:39  pjain_cvs
//  Organized Imports
//
//  Revision 1.4  2005/07/27 18:57:56  pjain_cvs
//  Updated for new DataBuffer architecture
//
//  Revision 1.3  2005/07/18 14:24:23  pjain_cvs
//  Updated to reflect changes to BasisSet and DataBuffer classes
//
//  Revision 1.2  2005/06/16 20:32:35  pjain_cvs
//  Reads files from one or more sessions archived from one or more sources
//  at the same rate as it was recorded.
//
//