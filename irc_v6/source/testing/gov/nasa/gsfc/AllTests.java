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

package gov.nasa.gsfc;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test Suite for all JUnit test cases in IRCv6
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/17 18:37:33 $
 * @author	smaher
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for gov.nasa.gsfc.testing");
        suite.addTest(gov.nasa.gsfc.commons.system.io.AllTests.suite());
        suite.addTest(gov.nasa.gsfc.commons.system.memory.AllTests.suite());
        suite.addTest(gov.nasa.gsfc.commons.types.queues.AllTests.suite());
        suite.addTest(gov.nasa.gsfc.irc.library.ports.connections.AllTests.suite());
        suite.addTest(gov.nasa.gsfc.irc.data.AllTests.suite());
        
        // The Eclipse Test Suite maintenance plugin only seems to recognize test
        // classes in the current package.
        
        //$JUnit-BEGIN$

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
//	Revision 1.3  2006/01/17 18:37:33  tames_cvs
//	Added missing test suites.
//	
//	Revision 1.2  2006/01/11 15:46:19  smaher_cvs
//	Moved JUnit test classes into same package as classes under test.
//	
//	Revision 1.1  2005/05/17 14:49:22  tames
//	Relocated and added main().
//	
//	Revision 1.4  2004/12/19 19:04:26  smaher_cvs
//	Added tests for Dequeuer
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
