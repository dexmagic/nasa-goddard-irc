//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/time/AbstractClock.java,v 1.2 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log:
//   1	IRC	   1.4		 03/19/2002 11:22:25 AM  Carl Hostetter
//  $
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
 *	An AbstractClock returns various representations of time elapsed from some
 *  origin.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author	 Carl Hostetter
 */

package gov.nasa.gsfc.commons.numerics.time;



public abstract class AbstractClock implements Clock
{
	/**
	 *  Returns the current time of this Clock in seconds.
	 *
	 *  @return The current time of this Clock in seconds
	**/

	public double getTimeInSeconds()
	{
		return (getTimeInMilliseconds() / 1000);
	}


	/**
	 *  Resets the time of this Clock.
	 *
	**/

	public void reset()
	{
		throw (new UnsupportedOperationException
			   ("This Clock cannot be reset"));
	}


	/**
	 *  Sets the time of this Clock to the given time, and returns the time at
	 *  which it was set.
	 *
	 *  @param time The time to which to set this Clock
	 *  @return The time at which the Clock was set
	**/

	public double set(double time)
	{
		throw (new UnsupportedOperationException
			   ("This Clock cannot be set"));
	}


	/**
	 *  Sets the time of this Clock to the given time, and returns the time at
	 *  which it was set in milliseconds.
	 *
	 *  @param timeInMilliseconds The time to which to set this Clock in
	 *  	milliseconds
	 *  @return The time at which the Clock was set in milliseconds
	**/

	public long set(long timeInMilliseconds)
	{
		throw (new UnsupportedOperationException
			   ("This Clock cannot be set"));
	}
}
