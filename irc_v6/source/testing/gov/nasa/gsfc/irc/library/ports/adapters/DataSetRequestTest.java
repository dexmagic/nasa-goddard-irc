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

package gov.nasa.gsfc.irc.library.ports.adapters;

import junit.framework.TestCase;

/**
 * JUnit test for parsing DataSetRequest XML
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/11 17:16:48 $
 * @author	smaher
 */
public class DataSetRequestTest extends TestCase {
    
    //private final static String TEST_PATH = "resources/testing/" + File.separator + FileUtil.getClassNameAsFileLocation( FileInConnectionTest.class ) ;
    private final static String TEST_PATH = "resources/testing/" ;
    private final static String TEST_IN_FILE = TEST_PATH + "DataSetRequest.xml";
    
//    public void testParse() throws Exception
//	{
//		Document document = XmlUtil.processXmlFile(new FileInputStream(TEST_IN_FILE),false);
//		String string = XmlUtil.domToString(document, null);
//		System.out.println(string);
//		DataSetRequest request = BasisSetTcpGenericOutputAdapter.DataSetRequest.fromXml(document);
//		assertEquals(BasisSetTcpGenericOutputAdapter.DataSetRequest.FORMAT_XDR, request.getFormat());
//		List basisBundleRequests = request.getBasisBundleRequests();
//		assertEquals(3, basisBundleRequests.size());
//		System.out.println(basisBundleRequests);
//		int a = 5;
//		
//	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DataSetRequestTest.java,v $
//	Revision 1.2  2006/05/11 17:16:48  smaher_cvs
//	Commented out until rest of generic basis set adapter is checked in.
//	
//	Revision 1.1  2006/05/10 17:30:22  smaher_cvs
//	Added test for index array "scrub" in CoaddProcessor
//	
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
