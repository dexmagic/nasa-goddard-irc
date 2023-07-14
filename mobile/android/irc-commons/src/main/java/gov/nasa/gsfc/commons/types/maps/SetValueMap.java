//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SetValueMap.java,v $
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
// 
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A SetValueMap is a Map whose entries contain Object keys but Set values. 
 * The <code>put()</code> and <code>remove()</code> methods have been overridden 
 * to manage a Set of values associated with each given key.
 * 
 * <P>NOTE: The value associated with a key, being a Set, does not contain 
 * duplicate entries.
 * 
 * <P>NOTE: The backing Sets are synchronized.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2004/07/12 14:26:24 $
 * @author Carl F. Hostetter
**/

public class SetValueMap extends HashMap
{
	/**
	 * Adds the given value to the Set of values associated with the given 
	 * key. Note that if the value is already in the Set, no new Set entry 
	 * will be added.
	 *
	 * @param key The key with which the given value is to be associated 
	 * @param value The value to add to the Set of values associated with the 
	 *  	given key
	 * @param The given key
	**/
	
	public Object put(Object key, Object value)
	{
		Set values = (Set) super.get(key); 
		
		if (values == null)
		{
			// Create a new Set of values and associate it with the given 
			// key.
			
			values = Collections.synchronizedSet(new HashSet());
			
			super.put(key, values);
		}
		
		values.add(value);
		
		return (key);
	}
	

	/**
	 *  Returns an Iterator over the Set of values associated with the 
	 *  given key. 
	 *
	 * @param key The key whose Set of values is to be returned 
	 * @return An Iterator over the Set of values associated with the 
	 *  	given key
	 *			
	**/
	
	public Object get(Object key)
	{
		Iterator result = null;
		
		Set values = (Set) super.get(key);
		
		if (values != null)
		{
			result = values.iterator();
		}
		
		return (result);
	}
	

	/**
	 * Removes the entry having the given key from this SetValueMap. 
	 *
	 * @param key The key of the entry to be removed 
	 * @return The given key
	**/
	
	public Object remove(Object key)
	{
		return (super.remove(key));
	}
	

	/**
	 * Removes the given value from the List of values associated with the 
	 * entry of this SetValueMap having the given key. If the value is the 
	 * only value currently in the list, the entry will be removed entirely. 
	 *
	 * @param key The key of the entry from which to remove the given value 
	 * @param value The value to be removed
	 * @return The given key
	**/
	
	public Object remove(Object key, Object value)
	{
		Set values = (Set) super.get(key);
		
		if (values != null)
		{
			values.remove(value);
			
			if (values.isEmpty())
			{
				remove(key);
			}
		}
		
		return (key);
	}
}
