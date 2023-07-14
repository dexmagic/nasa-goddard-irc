//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   2	IRC	   1.1		 11/13/2001 10:31:26 AMJohn Higinbotham Javadoc
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

package gov.nasa.gsfc.commons.types.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides basic functionality similar to a HashMap with the 
 * exception that a key may have more than one value associated with it.
 * 
 * <P>NOTE: A value can be added, and if so will appear, <em>more than once</em> 
 * in the List of values associated with a key. To avoid this behavior, see 
 * the SetValueMap class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2004/07/12 14:26:24 $
 * @author		John Higinbotham
**/
public class MultivalueMap 
{

	private HashMap fMap = null; // Map of keys to lists of values

	/**
	 * Construct a new MultivalueMap 
	 *
	**/
	public MultivalueMap()
	{
		fMap = new HashMap();
	}

	/**
	 * Store the value in the map and associate it with the specified key.
	 * If the key already exists, the value will be added to the list
	 * of values associated with that key.
	 * 
	 * <P>NOTE: A value can be added, and if so will appear, <em>more than once</em> 
	 * in the List of values associated with a key. To avoid this behavior, see 
	 * the SetValueMap class.
	 *
	 * @param  key 	Key to associate with value. 
	 * @param  value Value to be stored. 
	 *			
	**/
	public void put(Object key, Object value)
	{
		ArrayList list = (ArrayList) fMap.get(key); 
		if (list != null)
		{
			//---Add item to existing list for key
			list.add(value);
		}
		else
		{
			//---Create new list for key and add item to it
			list = new ArrayList();
			list.add(value);
			fMap.put(key, list);
		}
	}

	/**
	 * Retrieve the list of all values associated with the specified key. 
	 * 
	 * <P>NOTE: A value can be added, and if so will appear, <em>more than once</em> 
	 * in the List of values associated with a key. To avoid this behavior, see 
	 * the SetValueMap class.
	 *
	 * @param  key 	Key to associate with value. 
	 * @return Iterator of items associated with key or null, if none.
	 *			
	**/
	public Iterator get(Object key)
	{
		Iterator rval = null;
		List	 list = (List) fMap.get(key);
		if (list != null)
		{
			rval = list.iterator();
		}
		return rval;
	}

	/**
	 * Retrieve the collection of keys used in the map. 
	 *
	 * @return Iterator of keys used in the map. 
	 *			
	**/
	public Iterator keys()
	{
		return fMap.keySet().iterator();
	}

	// DEV: Other's may wish to add functionality to support removes.
}
