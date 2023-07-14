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
//   1	IRC	   1.0		 1/21/2002 5:44:49 PM Ken Wootton	 
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

package gov.nasa.gsfc.commons.types.iterators;

import java.util.Enumeration;
import java.util.Iterator;

/**
 *	This class provides an iterator for traversing the elements of an
 *  enumeration.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Ken Wootton
 */
public class EnumerationIterator implements Iterator
{
	private static final String UNSUPPORTED_MSG = 
		"Remove is not supported within this iterator.";

	private Enumeration fEnumeration = null;

	/**
	 *	Create the iterator.
	 *
	 *  @param enumeration  the enumeration that contains the elements
	 *					  of the iteration
	 */
	public EnumerationIterator(Enumeration enumeration)
	{
		fEnumeration = enumeration;
	}
	
	/**
	 *	Determine if the iteration has more elements.
	 *
	 *  @return  true if the iteration returns more elements
	 */
	public boolean hasNext()
	{
		return fEnumeration.hasMoreElements();
	}

	/**
	 *	Get the next object in the iteration.
	 *
	 *  @return  the next object in the iteration
	 */
	public Object next()
	{
		return fEnumeration.nextElement();
	}

	/**
	 *	Unsupported.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException(UNSUPPORTED_MSG);
	}
}
