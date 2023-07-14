//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	$Log: ShortArrayRegion.java,v $
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
//	Revision 1.1.2.2  2004/03/24 20:31:33  chostetter_cvs
//	New package structure baseline
//	
//	Revision 1.2  2003/10/24 08:42:29  mnewcomb_cvs
//	Added $Log: ShortArrayRegion.java,v $
//	Added Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Added Initial version
//	Added
//	Added Revision 1.1.2.2  2004/03/24 20:31:33  chostetter_cvs
//	Added New package structure baseline
//	Added flag to the header of these files..
//	
//  ---------------------------------------------------
//
//	08/1/99	T. Ames/588
//
//		Initial version.
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

package gov.nasa.gsfc.commons.types.arrays;

import java.io.Serializable;


/**
 *  A ShortArrayRegion is a class that defines a region of a larger array that is
 *	available for use by other classes. Users can get the larger array and the
 *	start index, end index, and the size of the array region.  Users must notify
 *	this object when the array region is no longer needed by releasing the
 *	array. Multiple calls of <code>getArray</code> must be paired with an equal
 *	number of <code>releaseArray</code> in order for the array region to be
 *	reclaimed.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	08/1/99
 *  @author		T. Ames/588
**/

public class ShortArrayRegion implements Serializable
{
	private short[] fArray;
	protected int fNumberOfUsers = 0;
	public int fStart = 0;
	public int fLength = 0;

	/**
	 *  Default constructor of ArrayRegion.
	**/
	public ShortArrayRegion()
	{
	}

	/**
	 *  Constructs an ArrayRegion with the given array, start index, and
	 *	region length.
	 *
	 *  @param start	The index that is the start of the region.
	 *  @param length 	The length of the region.
	 *	@exception InvalidArgumentException The given argument is invalid.
	**/
	public ShortArrayRegion(short[] array, int start, int length)
		throws IllegalArgumentException
	{
		if (array == null)
		{
			throw new IllegalArgumentException("array argument is null");
		}
		if (start > (array.length - 1))
		{
			throw new IllegalArgumentException(
				"start index is greater than length of array");
		}

		fArray = array;
		fStart = start;
		if ((start + length) > array.length)
		{
			fLength = array.length - start;
		}
		else
		{
			fLength = length;
		}
	}

	/**
	 *  Gets the source array and marks the array region as in use.
	 *	Should be paired with calls to <code>release</code>.
	**/
	public synchronized short[] getArray()
	{
		fNumberOfUsers++;
		return fArray;
	}

	/**
	 *  Releases array region from use of the caller.
	**/
	public synchronized void release()
	{
		fNumberOfUsers--;
		// This method should throw an exception if release
		// is called to many times and fNumberOfUsers is less than zero
		if (fNumberOfUsers < 0) 
		{
			System.out.println(
				"Warning ShortArrayRegion release() was called to many times. "
				+ "Number of users is " + fNumberOfUsers
				+ " from thread: " + Thread.currentThread());
			fNumberOfUsers = 0;
		}
		if (fNumberOfUsers == 0)
		{
//  		System.out.println("ShortArrayRegion released: "
// 				+ fStart + " " + fLength + " " + fNumberOfUsers
// 				+ " " + Thread.currentThread());
			notifyAll();
		}
	}

	/**
	 *  Returns the start index of the region in the source array.
	 *
	 *  @return The start index.
	**/
	public int getStart()
	{
		return (fStart);
	}

	/**
	 *  Returns the length of the region.
	 *
	 *  @return The length.
	**/
	public int getLength()
	{
		return (fLength);
	}

	/**
	 *  Returns true if the region is in use by one or more users.
	 *
	 *  @return true if in use.
	**/
	public boolean isInUse()
	{
		if (fNumberOfUsers > 0)
			return(true);
		else
			return(false);
	}
}
