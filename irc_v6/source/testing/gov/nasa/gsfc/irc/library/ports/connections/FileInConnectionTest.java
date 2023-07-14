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

package gov.nasa.gsfc.irc.library.ports.connections;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import junit.framework.TestCase;

import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferListener;

/**
 * JUnit test for FileInConnection.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author	smaher
 */
public class FileInConnectionTest extends TestCase implements InputBufferListener  {
    
    //private final static String TEST_PATH = "resources/testing/" + File.separator + FileUtil.getClassNameAsFileLocation( FileInConnectionTest.class ) ;
    private final static String TEST_PATH = "shared/resources/testing/" ;
    private final static String TEST_IN_FILE = TEST_PATH + "fileInConnectionTest.dat";
    private final static String TEST_OUT_FILE = 
    	System.getProperty("java.io.tmpdir", TEST_PATH) + "fileInConnectionTestOut.dat";
    private FileChannel fc = null;
    private int eventCounts = 0;
	
    /**
	 * Test getters/setters 
	 */
	public void testBasicParams() {
	    
	    FileInConnection fic = new FileInConnection();
	    fic.setFileCheckSleepMillis( 5000L );
	    assertEquals( 5000L, fic.getFileCheckSleepMillis() );
	    fic.setFileName("abc");
	    assertEquals( "abc", fic.getFileName());
	    fic.setReadBlockSize( 500 );
	    assertEquals(500, fic.getReadBlockSize());
	}
	
	/**
	 * Main test.  Sets up a test file to read (which is 10KB of MarkIII data),
	 * listens for ConnectionData events, writes the data to a file, and then
	 * compares the source file to the destination file and fails if they're
	 * not identical.
	 */
	public void testFileIn()
	{
	    FileOutputStream fos = null;
        FileInConnection fic = new FileInConnection();
        try {
            fc = (fos = new FileOutputStream( TEST_OUT_FILE)).getChannel();
            fic.setFileName( TEST_IN_FILE);
            fic.addInputBufferListener(this);
            fic.start();
            Thread.sleep( 2000L);
            assertTrue(FileUtil.areFilesIdentical(TEST_IN_FILE, TEST_OUT_FILE));
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            fic.stop();
            fic.kill();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
	    
	}
	


    /* 
     * Listener method when that writes ConnectionData
     * to file.
     */
    public void handleInputBufferEvent(InputBufferEvent event) {
        assertNotNull(event);
        BufferHandle bufferHandle = event.getHandle();
        assertNotNull(bufferHandle);
        
        ByteBuffer buffer = (ByteBuffer) bufferHandle.getBuffer();
        assertNotNull(buffer);
        //System.out.println("event: " + buffer.limit());
        
        try {
            fc.write( buffer );
        } catch (IOException e) {
            fail(e.getMessage());
        }
        //fail("received data");
        eventCounts++;
        
    }
}

//--- Development History  ---------------------------------------------------
//
//	$Log: FileInConnectionTest.java,v $
//	Revision 1.4  2006/01/23 17:59:55  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.3  2005/06/08 22:03:19  chostetter_cvs
//	Organized imports
//	
//	Revision 1.2  2005/05/23 22:13:47  tames
//	Relocated output file location to the temp directory.
//	
//	Revision 1.1  2005/05/17 14:20:14  tames
//	relocated.
//	
//	Revision 1.6  2004/12/01 21:50:58  chostetter_cvs
//	Organized imports
//	
//	Revision 1.5  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.4  2004/09/27 20:41:54  tames
//	Reflects a refactoring of Connection input and output events.
//	
//	Revision 1.3  2004/09/08 18:02:56  smaher_cvs
//	Moved testing/source back to source/testing and testing/resources to resources/testing
//	
//	Revision 1.1  2004/09/08 14:56:09  smaher_cvs
//	Moved these files from source/testing to testing/source.  This was so we could have a clean spot to put test-related XML files (testing/resources)
//	
//	Revision 1.1  2004/08/23 18:24:13  smaher_cvs
//	Added FileUtilTest and FileInConnectionTest with overall test harness
//	
//	Revision 1.1  2004/08/13 22:07:55  smaher_cvs
//	Initial source and tests for a connection that reads a file.  This will initially be used to read MarkIII test files.
//	
