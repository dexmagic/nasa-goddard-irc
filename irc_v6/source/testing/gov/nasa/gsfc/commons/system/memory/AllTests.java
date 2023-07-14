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

package gov.nasa.gsfc.commons.system.memory;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/17 18:37:47 $
 * @author	Troy Ames
 */
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for gov.nasa.gsfc.irc.data.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(AbstractMemoryModelTest.class);
		suite.addTestSuite(CompositeAllocationTest.class);
		suite.addTestSuite(ContiguousMemoryModelTest.class);
		suite.addTestSuite(DefaultLinkedAllocationTest.class);
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
//	Revision 1.1  2006/01/17 18:37:47  tames_cvs
//	Initial Version
//	
//	
//	
