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
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test for TcpServerConnection.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/05/17 14:48:55 $
 * @author 	Troy Ames
 */
public class TcpConnectionTest extends TestCase
{
	private TcpServerConnection fServerConnection;
	private TcpClientConnection fClientConnection;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		fServerConnection = new TcpServerConnection();
		fServerConnection.setPort(9999);
		fClientConnection = new TcpClientConnection();
		fClientConnection.setPort(9999);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		fServerConnection.kill();
		fClientConnection.kill();
	}

	public void testStartStopClient()
	{
		fServerConnection.start();
		assertTrue(fServerConnection.isStarted());
		
		fClientConnection.start();
		assertTrue(fClientConnection.isStarted());

		fClientConnection.stop();
		assertTrue(fClientConnection.isStopped());
		
		//fClientConnection.setPort(9998);
		fClientConnection.start();
		assertTrue(fClientConnection.isStarted());
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite()
	{
		return new TestSuite(TcpConnectionTest.class);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: TcpConnectionTest.java,v $
//  Revision 1.2  2005/05/17 14:48:55  tames
//  *** empty log message ***
//
//  Revision 1.1  2005/05/17 14:20:36  tames
//  Initial version
//
//