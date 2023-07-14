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


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/05/17 14:48:28 $
 * @author	smaher
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
            "Test for gov.nasa.gsfc.irc.library.ports.connections.tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(FileInConnectionTest.class);
        suite.addTestSuite(TcpConnectionTest.class);
        //$JUnit-END$
        return suite;
    }

    public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: AllTests.java,v $
//	Revision 1.2  2005/05/17 14:48:28  tames
//	Added main().
//	
//	Revision 1.1  2005/05/17 14:19:57  tames
//	Initial version
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
//	
