//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/time/SystemClock.java,v 1.3 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: SystemClock.java,v $
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/07/06 13:40:01  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
//  Revision 1.1.2.3  2004/03/24 20:31:33  chostetter_cvs
//  New package structure baseline
//
//
//   1	IRC	   1.4		 03/19/2002 11:22:25 AM  Carl Hostetter
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

/**
 *	A SystemClock returns various representations of time elapsed from the
 *  System clock.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author	 Carl Hostetter
 */

package gov.nasa.gsfc.commons.system.time;

import gov.nasa.gsfc.commons.numerics.time.AbstractClock;



public class SystemClock extends AbstractClock
{
	private static final long START_TIME = System.currentTimeMillis();
	
	/**
	 *  Returns the current time of the SystemClock (which is in milliseconds).
	 *
	 *  @return The current time of the SystemClock
	**/

	public double getTime()
	{
		return ((double) System.currentTimeMillis());
	}


	/**
	 *  Returns the current time of the SystemClock in seconds.
	 *
	 *  @return The current time of the SystemClock in seconds
	**/

	public double getTimeInSeconds()
	{
		return (System.currentTimeMillis() / (double) 1000);
	}


	/**
	 *  Returns the current time of the SystemClock in milliseconds.
	 *
	 *  @return The current time of the SystemClock in milliseconds
	**/

	public long getTimeInMilliseconds()
	{
		return (System.currentTimeMillis());
	}
	
	/**
	 *  Returns the time elapsed since the start of the SystemClock in 
	 *  milliseconds
	 *
	 *  @return The current time of the SystemClock
	**/

	public long getTimeSinceStartInMilliseconds()
	{
		return (System.currentTimeMillis() - START_TIME);
	}
}
