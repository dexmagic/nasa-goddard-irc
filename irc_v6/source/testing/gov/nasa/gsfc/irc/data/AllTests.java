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

package gov.nasa.gsfc.irc.data;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/28 18:45:26 $
 * @author	Troy Ames
 */
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for gov.nasa.gsfc.irc.data.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(AbstractDataBufferTest.class);
		suite.addTestSuite(IntegerDataBufferTest.class);
		suite.addTestSuite(DefaultBasisSetTest.class);
		suite.addTestSuite(DataModelTest.class);
		suite.addTestSuite(HistoryBundleTest.class);
		suite.addTestSuite(BasisBundleTest.class);
		//$JUnit-END$
		return suite;
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: AllTests.java,v $
//	Revision 1.3  2005/10/28 18:45:26  tames
//	Added BasisBundle test
//	
//	Revision 1.2  2005/09/09 22:42:41  tames
//	added HistoryBundleTest suite.
//	
//	Revision 1.1  2005/07/14 21:46:20  tames
//	Initial version
//	
//	
