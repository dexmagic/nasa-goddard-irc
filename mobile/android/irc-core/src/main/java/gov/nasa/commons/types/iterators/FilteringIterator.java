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
//   3	IRC	   1.2		 11/12/2001 5:01:03 PMJohn Higinbotham Javadoc
//		update.
//   2	IRC	   1.1		 10/26/2001 9:54:54 AMJohn Higinbotham Remove header.
//   1	IRC	   1.0		 3/5/2001 5:32:24 PM  Steve Clark	 
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

import java.util.Collection;
import java.util.Iterator;


/**
 * FilteringIterator is constructed as a wrapper around another
 * Iterator.  It implements a simple filter, returning only those
 * elements from the underlying Iterator which appear in a collection
 * given at construction time.
 */
public class FilteringIterator implements Iterator
{
	private Object fNext = null;
	private Iterator fBaseIterator;
	private Collection fFilter;
	private boolean fInvert;
	private boolean fAtEnd = false;

	/**
	 * Construct a FilteringIterator as specified.
	 *
	 * @param base		The Iterator to be filtered
	 * @param filter	Collection of elements acting as filter - any
	 *					element not in this collection will not be
	 *					returned by the FilteringIterator
	 * @param invert	If true, the effect of 'filter' is reversed: any
	 *					element which <em>is</em> in the filter will
	 *					<em>not</em> be returned by the FilteringIterator
	 */
	public FilteringIterator(Iterator base, Collection filter,
							boolean invert)
	{
		fBaseIterator = base;
		fFilter = filter;
		fInvert = invert;
		advance();
	}

	/**
	 * Construct a FilteringIterator as specified.
	 *
	 * @param base		The Iterator to be filtered
	 * @param filter	Collection of elements acting as filter - any
	 *					element not in this collection will not be
	 *					returned by the FilteringIterator
	 */
	public FilteringIterator(Iterator base, Collection filter)
	{
		this(base, filter, false);
	}

	/**
	 * Get the next item.
	 *
	 * @returns next available item
	**/
	public Object next()
	{
		Object result = fNext;
		advance();
		return result;
	}

	/**
	 * Determine if this iterator has a next item.
	 *
	 * @boolean true if there is a next item, false otherwise
	**/
	public boolean hasNext()
	{
		return !fAtEnd;
	}

	/**
	 * Operation is not supported by this Iterator.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Advance my internal pointers to the next entry which passes the
	 * filter.  Set fAtEnd to true if no acceptable element is found
	 * before the underlying Iterator runs out of elements.
	 */
	private void advance()
	{
		while (fBaseIterator.hasNext())
		{
			fNext = fBaseIterator.next();
			if (fFilter.contains(fNext) != fInvert)
			{
				// If we found an appropriate entry, return
				// immediately
				return;
			}
		}

		// If the loop terminated, we ran out of elements
		fAtEnd = true;
	}
}
