//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Counter.java,v $
//  Revision 1.4  2004/06/11 17:26:40  chostetter_cvs
//  Added getCount()
//
//  Revision 1.3  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.2  2004/06/04 05:34:42  chostetter_cvs
//  Further data, Algorithm, and Component work
//
//  Revision 1.1  2004/06/02 22:33:27  chostetter_cvs
//  Namespace revisions
//
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

package gov.nasa.gsfc.commons.numerics.types;

import java.io.Serializable;


/**
 *  A Counter maintains an int count value.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/06/11 17:26:40 $
 *  @author Carl F. Hostetter
**/

public class Counter implements Serializable
{
	private int fCount = 0;	

	/**
	 *  Default constructor of a Counter. The initial count is 0.
	 *
	**/
	
	public Counter()
	{
		
	}
	

	/**
	 *  Constructs a new Counter having the given initial count.
	 *
	 *  @param count The initial count value
	**/
	
	public Counter(int count)
	{
		fCount = count;
	}
	

	/**
	 *  Sets the current count to the given count.
	 *
	 *  @param The new count
	 *  @return The new count
	**/
	
	public int setCount(int count)
	{
		fCount = count;
		
		return (fCount);
	}
	

	/**
	 *  Returns the current count of this Counter.
	 *
	 *  @return The current count of this Counter
	**/
	
	public int getCount()
	{
		return (fCount);
	}
	

	/**
	 *  Increments the current count value, and returns the result.
	 *
	 *  @return The incremented count value
	**/
	
	public int increment()
	{
		fCount++;
		
		return (fCount);
	}
	

	/**
	 *  Increments the current count value by the given amount, and returns the result.
	 *
	 *  @param The amount to add to the current count
	 *  @return The incremented count value
	**/
	
	public int increment(int amount)
	{
		fCount += amount;
		
		return (fCount);
	}
	

	/**
	 *  Decrements the current count value, and returns the result.
	 *
	 *  @return The decremented count value
	**/
	
	public int decrement()
	{
		fCount--;
		
		return (fCount);
	}
	

	/**
	 *  Decrements the current count value by the given amount, and returns the result.
	 *
	 *  @param The amount to subtract from the current count
	 *  @return The decremented count value
	**/
	
	public int decrement(int amount)
	{
		fCount -= amount;
		
		return (fCount);
	}
	
	
	/**
	 *  Returns a String representation of this Counter.
	 *
	 *  @return A String representation of this Counter
	**/
	
	public String toString()
	{
		return (new Integer(fCount).toString());
	}
}
