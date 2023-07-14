//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.library.processors;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Test the <code>scrubCoaddIndexArray</code> method in CoaddProcessor.  The
 * more extensive tests are currently done in MarkIII, which requires a large,
 * hairy multi-threaded algorithm tester.
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: CoaddProcessorIndexArrayTest.java,v 1.1 2006/05/10 17:30:22 smaher_cvs Exp $
 * @author	$Author: smaher_cvs $
 */
public class CoaddProcessorIndexArrayTest extends TestCase
{
	public void testScrubGoodNormal() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,1,2,3};
		assertEquals(a, cp.scrubCoaddIndexArray(a.length, a));
	}
	
	public void testScrubGoodEmpty() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[0];
		assertEquals(a, cp.scrubCoaddIndexArray(a.length, a));
	}	
	
	public void testScrubGoodDupe1() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,0};
		int[] is = cp.scrubCoaddIndexArray(a.length, a);
		assertTrue(Arrays.equals(new int[] {0},is));
	}

	public void testScrubGoodDupe2() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,0, 1, 2};
		int[] is = cp.scrubCoaddIndexArray(a.length, a);
		assertTrue(Arrays.equals(new int[] {0, 1 ,2},is));
	}

	public void testScrubGoodDupe3() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,0, 1 ,2, 3, 3};
		int[] is = cp.scrubCoaddIndexArray(a.length, a);
		assertTrue(Arrays.equals(new int[] {0, 1, 2, 3},is));
	}

	public void testScrubGoodDupe4() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,0, 0, 1 ,2, 3, 3, 3};
		int[] is = cp.scrubCoaddIndexArray(a.length, a);
		assertTrue(Arrays.equals(new int[] {0, 1, 2, 3},is));
	}

	public void testScrubGoodDupe5() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0, 1 ,2, 2, 2, 3};
		int[] is = cp.scrubCoaddIndexArray(a.length, a);
		assertTrue(Arrays.equals(new int[] {0, 1, 2, 3},is));
	}

	public void testScrubGoodDupe6() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,0, 1 ,1, 2, 2, 3};
		int[] is = cp.scrubCoaddIndexArray(a.length, a);
		assertTrue(Arrays.equals(new int[] {0, 1, 2, 3},is));
	}
	
	public void testScrubBadBig() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {0,1,2,4};
		try
		{
			cp.scrubCoaddIndexArray(a.length, a);
			fail("Didn't throw exception");
		} catch (IllegalArgumentException e)
		{
		}
	}	
	
	public void testScrubBadIllegalValue() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {-2,1,2,3};
		try
		{
			cp.scrubCoaddIndexArray(a.length, a);
			fail("Didn't throw exception");
		} catch (IllegalArgumentException e)
		{
		}
	}		
	
	public void testScrubBadNotMono1() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {2,1,3};
		try
		{
			cp.scrubCoaddIndexArray(a.length, a);
			fail("Didn't throw exception");
		} catch (IllegalArgumentException e)
		{
		}
	}		
	
	public void testScrubBadNotMono2() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {1,3,2};
		try
		{
			cp.scrubCoaddIndexArray(a.length, a);
			fail("Didn't throw exception");
		} catch (IllegalArgumentException e)
		{
		}
	}		
	
	public void testScrubBadNotMono3() throws Exception
	{
		CoaddProcessor cp = new CoaddProcessor();
		int[] a = new int[] {3,2};
		try
		{
			cp.scrubCoaddIndexArray(a.length, a);
			fail("Didn't throw exception");
		} catch (IllegalArgumentException e)
		{
		}
	}		
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: CoaddProcessorIndexArrayTest.java,v $
//	Revision 1.1  2006/05/10 17:30:22  smaher_cvs
//	Added test for index array "scrub" in CoaddProcessor
//	