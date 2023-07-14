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

package gov.nasa.gsfc.commons.system.io;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import gov.nasa.gsfc.commons.system.io.FileUtil;


/**
 * JUnit tests
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/11 15:46:19 $
 * @author	smaher
 */
public class FileUtilTest extends TestCase  {

//    private final static String TEST_PATH = FileUtilTest.class.getResource("/classes" + 
//    		File.separator + FileUtil.getClassNameAsFileLocation( FileUtilTest.class )).getFile() ;
	
    private final static String TEST_PATH = "resources/testing/" ;
    private final static String TEST_SOURCE_FILE = TEST_PATH + "TestFileSource.dat";
    private final static String TEST_SHORT_FILE = TEST_PATH + "TestFileShort.dat";
    private final static String TEST_LONG_FILE = TEST_PATH + "TestFileLong.dat";
    private final static String TEST_DIFF_FILE = TEST_PATH + "TestFileDiff.dat";
    private final static String TEST_SOURCE_COPY_FILE = TEST_PATH + "TestFileSourceCopy.dat";

    /**
     * Tests FileUtil.areFilesIdentical
     */
    public void testFileCompare() {
    	System.out.println("Path to files = " + TEST_PATH);
        assertFalse( FileUtil.areFilesIdentical( TEST_SOURCE_FILE , TEST_SHORT_FILE));
        assertFalse( FileUtil.areFilesIdentical( TEST_SOURCE_FILE , TEST_LONG_FILE));
        assertFalse( FileUtil.areFilesIdentical( TEST_SOURCE_FILE , TEST_DIFF_FILE));
        assertTrue( FileUtil.areFilesIdentical( TEST_SOURCE_FILE , TEST_SOURCE_COPY_FILE));
    }
    
    
    public static void main(String[] args)
    {
        //junit.textui.TestRunner.run(FileUtilTest.suite());
    	TestRunner.run(FileUtilTest.class);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(FileUtilTest.class);             
        //suite.addTest(someclass.suite());           
        return suite;
    }
}

//--- Development History  ---------------------------------------------------
//
//	$Log: FileUtilTest.java,v $
//	Revision 1.1  2006/01/11 15:46:19  smaher_cvs
//	Moved JUnit test classes into same package as classes under test.
//	
//	Revision 1.7  2005/05/23 22:12:32  tames
//	Relocated data files.
//	
//	Revision 1.6  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.5  2004/12/07 22:33:05  jhiginbotham_cvs
//	Support for properly finding test file.
//	
//	Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//	Organized imports
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
//	
