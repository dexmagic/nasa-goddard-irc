//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   1	IRC	   1.0		 6/28/2002 4:14:19 PM Ken Wootton	 
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

package gov.nasa.gsfc.commons.types.arrays;

import java.util.Iterator;

/**
 *	This class provides a simple iterator for an array of values.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Ken Wootton
 */
public class ArrayIterator implements Iterator
{
	private static final String UNSUPPORTED_MSG = 
		"Remove is not supported within this iterator.";

	//  The actual array of information 
	private Object[] fArray = null;

	//  Index of the current element.
	private int fArrIndex = 0;

	/**
	 *	Create the iterator.  Note that the array is expected to be
	 *  fully populated.  Every element in the array will be in the 
	 *  iterator, regardless of its value (i.e. even if it is null).
	 *
	 *  @param array  the array that contains the elements of the iterator
	 */
	public ArrayIterator(Object[] array)
	{
		fArray = array;
	}

	/**
	 *	Determine if the iteration has more elements.
	 *
	 *  @return  true if the iteration returns more elements
	 */
	public boolean hasNext()
	{
		return (fArrIndex < fArray.length);
	}

	/**
	 *	Get the next object in the iteration.
	 *
	 *  @return  the next object in the iteration
	 */
	public Object next()
	{
		return fArray[fArrIndex++];
	}

	/**
	 *	Unsupported.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException(UNSUPPORTED_MSG);
	}	
}


