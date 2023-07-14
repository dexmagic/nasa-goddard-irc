//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
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

package gov.nasa.gsfc.irc.library.ports.connections;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.SingleUseBufferHandle;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;


/**
 * Connection to read data from a file.  An example use would be to use pre-recorded
 * instrument data from a file.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	 smaher
 */
public class FileInConnection extends AbstractConnection 
	implements Connection
{
	private static final String CLASS_NAME = FileInConnection.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String DEFAULT_NAME = "File In Connection";
	
	// Parameter Keys
	public final static String FILE_NAME_KEY = "inputFilename";
	public final static String BLOCK_SIZE_KEY = "blocksize";
	public final static String SWAP_ENDIAN_KEY = "enableEndianSwap";
	public final static String SWAP_SIZE_KEY = "swapSizeBytes";
	
	
	// Name of file
	private String fFileName = null;
	
	// Block size when reading from files
	private int fReadBlockSize = 1024;

	// How long to sleep between checking for file growth
    private long fFileCheckSleepMillis = 5000L;

    // Do we need to swap the byte order?
    // If set to true, then every getSwapSizeBytes() bytes 
    // will be reversed.
    private boolean fSwappingEndian = false;
    
    // If we're doing the endian swap, how many 
    // bytes that are in a swap "word"
    public static int fSwapSizeBytes = 4;
	
	/**
	 * The thread for reading from ports.
	 */
	private Thread fReaderThread;
	private ConnectionDescriptor fDescriptor;


	/**
	 *	Constructs a new FileInConnection having a default name.
	 *
	 */
	
	public FileInConnection()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new FileInConnection having the given base name.
	 *  
	 *  @param name The base name of the new FileInConnection
	 */
	
	public FileInConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new FileInConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new FileInConnection
	 */
	public FileInConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);

		fDescriptor = descriptor;
		
		configureFromDescriptor(descriptor);
	}
	
	
	public void setDescriptor(Descriptor descriptor)
	{
	    super.setDescriptor(descriptor);
		if (descriptor instanceof ConnectionDescriptor)
		{
			configureFromDescriptor((ConnectionDescriptor) descriptor);
		}
	}	
	/**
	 * Causes this FileInConnection to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a ConnectionDescriptor
	 */
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		if (descriptor == null)
		{
			return;
		}
		
		// Set FileName
		setFileName(descriptor.getParameter(FILE_NAME_KEY));
		
		// Set block size if set
		String strBlockSize = descriptor.getParameter(BLOCK_SIZE_KEY);
		
		if (strBlockSize != null) {
            try {

                int blockSize = Integer.parseInt(strBlockSize);

                if (blockSize < 0) {
                    String message = "Bad blocksize: " + blockSize;

                    sLogger.logp(Level.WARNING, CLASS_NAME,
                            "configureFromDescriptor", message);
                } else {
                    // Set the block size
                    setReadBlockSize(blockSize);
                }
            } catch (NumberFormatException e) {
                String message = "Bad blocksize: " + strBlockSize;

                sLogger.logp(Level.WARNING, CLASS_NAME,
                        "configureFromDescriptor", message, e);
            }
        }

		// Swap endian?
		String strEndianSwap = descriptor.getParameter(SWAP_ENDIAN_KEY);
		if (strEndianSwap != null)
		{
		    setSwappingEndian(Boolean.valueOf(strEndianSwap).booleanValue());
		}

	}

	/**
	 * Start reading data from the file. 
	 * A connection must be started before the 
	 * <code>write</code> method can be called and before data will be received
	 * from this ConnectionManager.
	 */
	public void start()
	{
		super.start();
		//declareWaiting();  TODO: is this needed?
		
		// Don't worry if file exists, yet.  If not, this class
		// will keep checking for a file (assuming fileCheckSleepMillis
		// is > 0)
		
		// Check for valid file name
//		if (getFileName() == null || (new File(getFileName())).exists() == false )
//		{
//            String message = getFullyQualifiedName()
//                    + " Empty filename or file doesn't exist: " + getFileName();
//
//            sLogger.logp(Level.WARNING, CLASS_NAME, "start", message);
//            // Create the initial connection thread
//            stop();
//        }
//		else
//		{
            startReaderThread();
//		}

	}

	/**
	 * Stops the connection. A connection that is closed will not generate 
	 * any ConnectionDataEvents.
	 */
	public synchronized void stop()
	{
		super.stop();
		stopReaderThread();
	}
		
	/**
	 *  Causes this Component to immediately cease operation and release 
	 *  any allocated resources. A killed Component cannot subsequently be 
	 *  started or otherwise reused.
	 */	
	public void kill()
	{
		stop();
		super.kill();
	}



	/**
	 * Starts the server thread to listen for new connections.
	 */
	private void startReaderThread()
	{
		// If the Thread is already running skip it
		if( fReaderThread != null && fReaderThread.isAlive() )
		{
			return;
		}

		// This is a server so start a SocketServer thread
		// that waits for client connections
		fReaderThread = new Thread(new FileReader(),
				(getFullyQualifiedName() + " Reader Thread"));
		fReaderThread.start();
	}

	/**
	 * Stops the server thread from listening for new connections.
	 */
	private void stopReaderThread()
	{
		// Interrupt the Server thread, if it's running
		if( fReaderThread != null && fReaderThread.isAlive() )
		{
			fReaderThread.interrupt();		// Sets a flag and interrupts
			fReaderThread = null;
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * Get the default block size for each socket read.
	 * @return Returns the readBlockSize.
	 */
	public int getReadBlockSize()
	{
		return fReadBlockSize;
	}
	
	/**
	 * Set the default block size for each socket read.
	 * @param readBlockSize The readBlockSize to set.
	 */
	public void setReadBlockSize(int readBlockSize)
	{
		if (readBlockSize > 0) {
            int oldValue = fReadBlockSize;
            fReadBlockSize = readBlockSize;
            firePropertyChange(BLOCK_SIZE_KEY, oldValue, fReadBlockSize);
        }
	}
	
	/**
	 * Get the port number listening for new client connections.
	 * @return Returns the serverPort.
	 */
	public String getFileName()
	{
		return fFileName;
	}
	

	/**
	 * @param filename The file to read from
	 */
	public void setFileName(String filename)
	{
        String oldValue = fFileName;
        fFileName = filename;
        firePropertyChange(FILE_NAME_KEY, oldValue, fFileName);
    }

    public long getFileCheckSleepMillis() {
        return fFileCheckSleepMillis;
    }
    
    /**
     * Set how long to sleep after reading the file and checking
     * if there's any new data in the file.
     * @param numMillis Number of milliseconds 
     */
    public void setFileCheckSleepMillis(long numMillis) {
        fFileCheckSleepMillis = numMillis;
    }
    
    /**
     * @return Returns the swappingEndian.
     */
    public boolean isSwappingEndian() {
        return fSwappingEndian;
    }
    /**
     * @param swappingEndian The swappingEndian to set.
     */
    public void setSwappingEndian(boolean swappingEndian) {
        fSwappingEndian = swappingEndian;
    }
    
    /**
     * @return Returns the fSwapSizeBytes.
     */
    public static int getSwapSizeBytes() {
        return fSwapSizeBytes;
    }
    /**
     * @param swapSizeBytes The fSwapSizeBytes to set.
     */
    public static void setSwapSizeBytes(int swapSizeBytes) {
        fSwapSizeBytes = swapSizeBytes;
    }
    
    
	// Utility class definition ------------------------------------------------
	
	/**
	 * The FileReader class reads data from a file and
	 * calls the <code>fireConnectionDataEvent</code> method on the Connection.
	 * <P>It will keep watching the file to see if the file has grown in size and
	 * processes any new data.
	**/
	protected class FileReader implements Runnable
	{
		private Selector fSelector = null;
		
		/**
		 * Creates a new Reader.
		 */
		public FileReader()
		{
		}

		public void run()
		{	
            long prevFileSize = 0;
    		long totalBytes = 0;
    		
    		
    		while(true)
			{
        		sLogger.log(Level.FINE, "FileInConnection: checking file  " + getFileName());

        		FileChannel fileChannel = null;
			    FileInputStream fileInputStream = null;
               
                try {

		    		// Open file and channel		    		    
                    fileInputStream = new FileInputStream(getFileName());
                    fileChannel = fileInputStream.getChannel();

                    // I'm not sure whether FileChannel.size() is updated
                    // when a file grows, so only grab one copy and save it.
                    long newSize = fileChannel.size();
                    
                    // If the file size has grown, then read the
                    // new data.
                    if (newSize > prevFileSize)
                    {
                        sLogger.log(Level.FINE, "FileInConnection: reading from " + getFileName());
                        
                        long currentPos = prevFileSize; // position is zero-based
                        
                        // used when swapping endian
        		        byte swapChunk[] = new byte[getSwapSizeBytes()];
        		        

                        
                        while (currentPos < newSize) {
                		    
            		        int bytesRead = 0;
                            boolean readSomeData = false;
                            
                            if (isSwappingEndian() == false)
                		    {
                                ByteBuffer byteBuffer = ByteBuffer.allocate(getReadBlockSize());

                                // Loop while data is available, filling the ByteBuffer
                                while ((bytesRead = fileChannel.read(byteBuffer, currentPos)) > 0
                                        && fileChannel.position() < getReadBlockSize()) {
                                    if (bytesRead > 0)
                                    {
                                        totalBytes += bytesRead;
                                        currentPos += bytesRead;
                                        readSomeData = true;
                                    }
                                }

                                if (readSomeData) {

                                    byteBuffer.flip();

                                    BufferHandle handle = new SingleUseBufferHandle(byteBuffer);
                                    fireInputBufferEvent(new InputBufferEvent(
                                            FileInConnection.this, handle));
                                }
                		    }
                		    else
                		    {
                		        // make sure read block size is divisible by getSwapSizeBytes()
                		        setReadBlockSize( getReadBlockSize() - getReadBlockSize() % getSwapSizeBytes());
                		        
                		        // buffer to receive swapped bytes and eventually be
                		        // wrapped in a ByteBuffer and sent out as an event
                		        byte byteArray[] = new byte[getReadBlockSize()];

                		        int bytesReadInBlock = 0;
                		        
                		        while (bytesReadInBlock + getSwapSizeBytes() <= getReadBlockSize() &&
                		                (bytesRead = fileInputStream.read(swapChunk)) > 0) 
                		        {
                		            if (bytesRead > 0)
                		            {
                                        totalBytes += bytesRead;
                                        currentPos += bytesRead;
                                        for (int i = bytesRead; i > 0; i--) {
                                            byteArray[bytesReadInBlock++] = swapChunk[i-1];
                                        }
                                        readSomeData = true;
                		            }
                		        }
                                
                		        if (readSomeData) {
                		            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
                		            	
                		            // Need to set limit to cover situation where
                		            // a full block wasn't read
                		            byteBuffer.limit(bytesReadInBlock);

                		            // Send InputBufferEvent with buffer
                		            BufferHandle handle = new SingleUseBufferHandle(byteBuffer);
                                    fireInputBufferEvent(new InputBufferEvent(
                                            FileInConnection.this, handle));
                                }                		        
                		    }
                        }
                		
                		prevFileSize = newSize;  
                		
                		if (sLogger.isLoggable(Level.FINER))
                		{
                		    sLogger.log(Level.FINER, CLASS_NAME + " - total read bytes: " + totalBytes);    
                		}
                    }

                } catch (FileNotFoundException e) {
                    String message = 
                		"Could not open file: "
                		+ getFileName();

                	sLogger.logp(Level.WARNING, CLASS_NAME, 
                			"run", message, e);                
                } catch (java.nio.channels.ClosedByInterruptException e) {
                    //NOP

                } catch (IOException e) {
                    String message = 
                		"IOException:"
                		+ getFileName();

                	sLogger.logp(Level.WARNING, CLASS_NAME, 
                			"run", message, e);                
                }
                
                finally
                {
                    // Make sure everything is closed
                    if (fileChannel != null) {
                        try {
                            fileChannel.close();
                        } catch (IOException e2) {
                            // NOP
                        }
                    }
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e2) {
                            // NOP
                        }
                    }
                }

                // If fileCheckSleepMillis is > 0, then
                // sleep and loop again, seeing if the file
                // grew.  Otherwise, exit.
                if (getFileCheckSleepMillis() > 0)
                {
                    // Sleep a bit before checking whether
                    // the file grew
                    try {
                        Thread.sleep( getFileCheckSleepMillis());
                    } catch (InterruptedException e1) {
                        // NOP
                    }
                }
                else
                {
                    break;
                }

			}
		}
	}


	/* 
	 * ALWAYS THROWS AN EXCEPTION because not implemented
	 * 
	 */
	public void process(ByteBuffer buffer) 
	{
	    throw new ReadOnlyBufferException();
	}
	
	/**
	 *  Returns a String representation of this Connection.
	 * 
	 *  @return A String representation of this Connection
	 */	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString());	
		
		stringRep.append("\nParameters ");
		stringRep.append("[FileName=" + getFileName() );
		stringRep.append(", " + BLOCK_SIZE_KEY + "=" + getReadBlockSize()  );
		stringRep.append(", " + SWAP_ENDIAN_KEY + "=" + isSwappingEndian()  );
		stringRep.append(", " + SWAP_SIZE_KEY + "=" + getSwapSizeBytes()  );
		stringRep.append("]");
		stringRep.append("\nConnection: ");
		
		return (stringRep.toString());
	}


}


//--- Development History  ---------------------------------------------------
//
//  $Log: FileInConnection.java,v $
//  Revision 1.16  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.15  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.14  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.13  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.12  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.11  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.10  2004/10/06 18:25:13  smaher_cvs
//  Changed key from filename to inputFilename to not collide with AbstractConnection's dump file name key.
//
//  Revision 1.9  2004/09/27 20:39:30  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.8  2004/09/08 21:05:36  smaher_cvs
//  Changed write so that it throws a ReadOnlyBufferException instead of a IOException
//
//  Revision 1.7  2004/09/08 15:05:03  smaher_cvs
//  Moved dev history to bottome of file
//
//  Revision 1.6  2004/09/08 14:56:35  smaher_cvs
//  Removed extra import statement
//
//  Revision 1.5  2004/08/27 17:42:53  smaher_cvs
//  Added support for endian swapping, not having the file to pre-exist, and not rechecking file growth if sleep is <=0.
//
//  Revision 1.4  2004/08/26 19:26:16  smaher_cvs
//  Set default sleep value for checking changed file size
//
//  Revision 1.3  2004/08/23 19:23:11  smaher_cvs
//  Added some comments
//
//  Revision 1.2  2004/08/23 18:27:45  smaher_cvs
//  Added check for ClosedByInterruptException which can be thrown innocuosly when the connector is stopped.
//
//  Revision 1.1  2004/08/13 22:07:55  smaher_cvs
//  Initial source and tests for a connection that reads a file.  This will initially be used to read MarkIII test files.
//
