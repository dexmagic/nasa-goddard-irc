//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   2	IRC	   1.1		 11/12/2001 5:01:00 PMJohn Higinbotham Javadoc
//		update.
//   1	IRC	   1.0		 12/8/2000 10:28:19 AMJohn Higinbotham 
//  $ 
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.types.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a common location to store methods that provide
 * additional support for working with collections in Java.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2004/07/12 14:26:24 $
 * @author		John Higinbotham
**/
public class CollectionUtil 
{

	/**
	 * Return an iterator as a list. 
	 *
	 * @param  iterator	Iterator to return a list.
	 * @return Resultant list.
	 *			
	**/
	public static List iteratorToList(Iterator iterator)
	{
		ArrayList list = new ArrayList();
		while (iterator.hasNext())
		{
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * Return an iterator resulting from the combination of items
	 * in a pair of iterators.
	 *
	 * @param  iterator1	First iterator of pair. 
	 * @param  iterator2	Second iterator of pair.
	 * @return Resultant iterator. 
	 *			
	**/
	public static Iterator combineIteratorPair(Iterator iterator1, Iterator iterator2)
	{
		ArrayList list = new ArrayList();
		while (iterator1.hasNext())
		{
			list.add(iterator1.next());
		}
		while (iterator2.hasNext())
		{
			list.add(iterator2.next());
		}
		return list.iterator();
	}
}
