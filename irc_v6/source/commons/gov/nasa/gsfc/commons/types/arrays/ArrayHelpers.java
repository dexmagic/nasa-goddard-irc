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
//   1	IRC	   1.0		 1/30/01 1:57:13 PM   Steve Clark
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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class provides some basic array manipulation utilities which
 * didn't get into the java Array classes.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2005/01/05 16:07:14 $
 * @author		Steve Clark
**/
public class ArrayHelpers
{
	/**
	 * Value reported by indexOf() when requested object not found.
	**/
	public static final int ARR_NOT_FOUND_INDEX = -1;

	/**
	 * Return the highest index of some specified object in an array.
	 * Comparisons are performed using equals().
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	**/
	public static int indexOf(Object arr[], Object tst)
	{
		if (arr != null)
		{
			for (int i = arr.length; --i >= 0; )
			{
				Object obj = arr[i];
				if ((obj == tst) ||
					((obj != null) && (obj.equals(tst))))
				{
					return i;
				}
			}
		}
		return ARR_NOT_FOUND_INDEX;
	}

	/**
	 * Return the highest index of some specified value in an int[].
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	**/
	public static int indexOf(int arr[], int tst)
	{
		if (arr != null)
		{
			for (int i = arr.length; --i >= 0; )
			{
				if (arr[i] == tst)
				{
					return i;
				}
			}
		}
		return ARR_NOT_FOUND_INDEX;
	}

	/**
	 * Return the highest index of some specified value in an long[].
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static int indexOf(long arr[], long tst)
	{
		if (arr != null)
		{
			for (int i = arr.length; --i >= 0; )
			{
				if (arr[i] == tst)
				{
					return i;
				}
			}
		}
		return ARR_NOT_FOUND_INDEX;
	}

	/**
	 * Return the highest index of some specified value in a short[].
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static int indexOf(short arr[], short tst)
	{
		if (arr != null)
		{
			for (int i = arr.length; --i >= 0; )
			{
				if (arr[i] == tst)
				{
					return i;
				}
			}
		}
		return ARR_NOT_FOUND_INDEX;
	}

	/**
	 * Return the highest index of some specified value in a double[].
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static int indexOf(double arr[], double tst)
	{
		if (arr != null)
		{
			for (int i = arr.length; --i >= 0; )
			{
				if (arr[i] == tst)
				{
					return i;
				}
			}
		}
		return ARR_NOT_FOUND_INDEX;
	}

	/**
	 * Return the highest index of some specified value in a float[].
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static int indexOf(float arr[], float tst)
	{
		if (arr != null)
		{
			for (int i = arr.length; --i >= 0; )
			{
				if (arr[i] == tst)
				{
					return i;
				}
			}
		}
		return ARR_NOT_FOUND_INDEX;
	}

	/**
	 * Test whether an array contains a particular object.
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static boolean contains(Object arr[], Object tst)
	{
		return (indexOf(arr, tst) != ARR_NOT_FOUND_INDEX);
	}

	/**
	 * Test whether an int[] contains a particular value.
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static boolean contains(int arr[], int tst)
	{
		return (indexOf(arr, tst) != ARR_NOT_FOUND_INDEX);
	}

	/**
	 * Test whether a long[] contains a particular value.
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static boolean contains(long arr[], long tst)
	{
		return (indexOf(arr, tst) != ARR_NOT_FOUND_INDEX);
	}

	/**
	 * Test whether a short[] contains a particular value.
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static boolean contains(short arr[], short tst)
	{
		return (indexOf(arr, tst) != ARR_NOT_FOUND_INDEX);
	}

	/**
	 * Test whether a double[] contains a particular value.
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static boolean contains(double arr[], double tst)
	{
		return (indexOf(arr, tst) != ARR_NOT_FOUND_INDEX);
	}

	/**
	 * Test whether a float[] contains a particular value.
	 *
	 * @param arr[] array
	 * @param tst item being looked for
	 * @return index 
	 */
	public static boolean contains(float arr[], float tst)
	{
		return (indexOf(arr, tst) != ARR_NOT_FOUND_INDEX);
	}

	/**
	 * Verify that the size of the array is what we expect.
	 *
	 * @param array Array
	 * @param desired Desired size
	 * @throws IllegalArgumentException
	**/
	public static void VerifySize(Object array, int desired)
	{
		int actual = Array.getLength(array);
		if (actual != desired)
		{
			throw new IllegalArgumentException("Improper array size, desired="+
											   desired + " actual=" + actual);
		}
	}
 
	/**
	 * Verify that the sizes of the two arrays are the same. 
	 *
	 * @param array1 Array one
	 * @param array2 Array two 
	 * @throws IllegalArgumentException
	**/
	public static void VerifySizesEqual(Object array1, Object array2)
	{
		int len1 = Array.getLength(array1);
		int len2 = Array.getLength(array2);
		if (len1 != len2)
		{
			throw new IllegalArgumentException ("Array sizes not equal");
		}
	}

	/**
	 *  Return the objects in the specified iterator in an Object[].
	 *  Note that you cannot cast the return Object[] to a more specific array
	 *  or else a Cast Exception will occur. You need to cast the
	 *  and object in the array.
	 *
	 *  @param i an Iterator over a Collection of Objects
	 *  @return the objects in the Iterator are returned in the Object[]
	**/
	public static Object[] toArray(Iterator i)
	{
		java.util.ArrayList list = new java.util.ArrayList();
		while(i.hasNext())
		{
			list.add(i.next());
		}
		return list.toArray();
	}

	/**
	 *  Return an array containing the items in the given collection.
	 *
	 *  @param arrayClass  the class of each item in the array
	 *  @param collection  the collection of items
	 *  @return  This will return the array of items in the collection.
	 *		   The user should be able to cast this to the proper
	 *		   array type (arrayClass[]).
	**/
	public static Object[] collectionToArray(Class arrayClass, Collection collection)
	{
		//  Create an array of the proper type.
		int arrSize = collection.size();
		Object[] arr = (Object[]) Array.newInstance(arrayClass, arrSize);

		Iterator colIter = collection.iterator();
		int i = 0;

		//  Load the array from the iterator.
		while (colIter.hasNext())
		{
			arr[i++] = colIter.next();
		}
		return arr;
	}

   /**
	*  Return the given iterator as an array of the specified class. 
	*
	*  @param iterator  iterator to translate
	*  @param arrayClass  class of each item in the iterator/array
	*
	*  @return  array representation of the iterator
   **/
	public static Object[] iteratorToArray(Class arrayClass, Iterator iterator)
	{
		//  Load a list with the items in the iterator.
		LinkedList list = new LinkedList();
		while (iterator.hasNext())
		{
			list.add(iterator.next());
		}

		//  Translate the list to proper type of array.
		return list.toArray(
			(Object[]) Array.newInstance(arrayClass, list.size()));
	}

	/**
	 *  Cast the given object array to an array of the of objects of the
	 *  given class.  This method is handy for those instances where you
	 *  know the class of each object in the array but the objects themselves 
	 *  were not placed in the proper type of array.The result of this
	 *  method can now be cast to the proper array.
	 *
	 *  @param arrayClass  the class of each item in the array
	 *  @param objectArr  the array of objects which needs to be cast.
	 *
	 *  @return  the array cast to the proper type
	**/
	public static Object[] castObjectArr(Class arrayClass, Object[] objectArr)
	{
		Object[] arr = (Object[]) Array.newInstance(arrayClass, objectArr.length);

		//  Copy directly.  No need to cast the array contents.
		System.arraycopy(objectArr, 0, arr, 0, objectArr.length);
		return arr;
	}
	

	/**
	 * Convert an array to a String.
	 * <p>
	 * Snarfed from <a href="http://www.codeproject.com/useritems/ArrayToString.asp">this article</a>.
	 * @param array Object[], primitive array, Hashtable, HashSet, or Collection to convert to a String
	 * @return String representation of the array
	 */
	public static String arrayToString(Object array) {
	    if (array == null) {
	        return "[NULL]";
	    } else {
	        Object obj = null;
	        if (array instanceof Hashtable) {
	            array = ((Hashtable)array).entrySet().toArray();
	        } else if (array instanceof HashSet) {
	            array = ((HashSet)array).toArray();
	        } else if (array instanceof Collection) {
	            array = ((Collection)array).toArray();
	        }
	        int length = Array.getLength(array);
	        int lastItem = length - 1;
	        StringBuffer sb = new StringBuffer("[");
	        for (int i = 0; i < length; i++) {
	            obj = Array.get(array, i);
	            if (obj != null) {
	                sb.append(obj);
	            } else {
	                sb.append("[NULL]");
	            }
	            if (i < lastItem) {
	                sb.append(", ");
	            }
	        }
	        sb.append("]");
	        return sb.toString();
	    }
	}

}
