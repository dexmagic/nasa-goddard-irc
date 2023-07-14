//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: IntPermutationCalc.java,v $
//  Revision 1.3  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.numerics.sets;

import java.util.LinkedList;
import java.util.List;

/** 
 *  This class provides a simple way to calculate all the permutations of 
 *  1 - n sets of numbers, while preserving the ordering within those sets.
 *  For example, given the two sets of numbers {2, 3} and {4, 5, 6, 7}, 
 *  the returned permutations would be:
 *  {2, 4}, {3, 4}, {2, 5}, {3, 5}, {2, 6}, {3, 6}, {2, 7}, and {3, 7}.
 *  The permutation results can be retrieved individually, through
 *  get(int), or all at once, through getAll.<p>
 *	You can argue whether these results reflect a permutation or combination
 *  because you are only taking one item (and only one item) from each set but
 *  I'm tired of thinking about it.<p>
 *	Note that this class will allow the user to request the permutations of
 *  just a single set of numbers.  For example, the single set {2, 3} would 
 *  yield {2} and {3} as its permutations.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/01/23 17:59:54 $
 *  @author Ken Wootton
 */
public class IntPermutationCalc
{
	private Object[] fPermArrs = null;
	
	/**
	 *  Create a new permutation calculator with the given sets of numbers
	 *  to use in the permutation.
	 *
	 *  @param permArrs  This accepts a set of integer arrays, where each
	 *				   integer array contains a set of integers to be
	 *				   be processed in the permutation.
	 */
	public IntPermutationCalc(Object[] permArrs)
	{
		fPermArrs = permArrs;
	}
	
	/**
	 *  Get the "nth" permutation in the set of possible permutations.	
	 *
	 *  @param n  index into the set possible of permutations
	 *  @return  This will return an integer array consisting of a 
	 *		   single permutation.  The length of this array is equal
	 *		   to the number of permutations provided by the constructor.
	 */
	public int[] get(int n)
	{
		int permArrSubIndex = 0;
		int[] intValues = new int[fPermArrs.length];

		//  We need a value for each provided array.
		for (int i = 0; i < fPermArrs.length; i++)
		{
			permArrSubIndex = n % ((int[]) fPermArrs[i]).length;
			intValues[i] = ((int[]) fPermArrs[i])[permArrSubIndex];
			n = n / ((int[]) fPermArrs[i]).length;
		}

		return intValues;
	}

	/**
	 *  Get the set of all possible permutations.
	 *
	 *  @return  all possible permutations as an array of integer arrays, 
	 *		   where each integer array contains a single possible 
	 *		   permutation
	 */
	public Object[] getAll()
	{
		int numPermutations = getNumPermutations();
		Object[] permutations = new Object[numPermutations];

		//  Just collect each separate permutation.
		for (int i = 0; i < numPermutations; i++)
		{
			permutations[i] = get(i);
		}

		return permutations;
	}

	/**
	 *  Get the set of all possible permutations.
	 *
	 *  @return  all possible permutations as a list of integer arrays, 
	 *		   where each integer array contains a single possible 
	 *		   permutation
	 */
	public List getAllAsList()
	{
		int numPermutations = getNumPermutations();
		LinkedList permutations = new LinkedList();

		//  Just collect each separate permutation.
		for (int i = 0; i < numPermutations; i++)
		{
			permutations.add(get(i));
		}

		return permutations;
	}

	/**
	 *  Get the number of possible permutations.
	 *
	 *  @return  the number of possible permutations
	 */
	public int getNumPermutations()
	{
		int numPerms = 1;

		//  Just multiply the number of elements in each permutation array.
		for (int i = 0; i < fPermArrs.length; i++)
		{
			numPerms *= ((int[]) fPermArrs[i]).length;
		}

		return numPerms;
	}

	/**
	 *  Prints a simple dump of the given permutations to the screen.  This
	 *  is only used for testing.
	 *
	 *  @param permutations  the permutations to print
	 */
	private static void dumpPerms(Object[] permutations)
	{
		int[] valueArr = null;
		String s = null;

		//  Go through each permutation, printing a new line for each.
		for (int i = 0; i < permutations.length; i++)
		{
			valueArr = (int[]) permutations[i];
			s = "{";

			//  Add each value in the permutation to the string.
			for (int j = 0; j < valueArr.length; j++)
			{
				if (j > 0)
				{
					s += ", ";
				}

				s += valueArr[j];
			}

			s += "}";
			System.out.println(s);
		}
	}
	
	/**
	 *  Main test method.
	 *
	 *  @param args  ignored
	 */
	public static void main(String[] args)
	{
		int[] arr1 = {2, 3};
		int[] arr2 = {4, 5, 6, 7};
		int[] arr3 = {10, 20, 30};
		
		Object[] permArrsOne = {arr1};
		Object[] permArrsTwo = {arr1, arr2};
		Object[] permArrsThree = {arr1, arr2, arr3};
		IntPermutationCalc calc = null;

		//  Single array test
		System.out.println("\nTest with one array");		
		calc = new IntPermutationCalc(permArrsOne);
		IntPermutationCalc.dumpPerms(calc.getAll());

		//  Two array test.
		System.out.println("\nTest with two arrays");
		calc = new IntPermutationCalc(permArrsTwo);
		IntPermutationCalc.dumpPerms(calc.getAll());

		//  Three array test.
		System.out.println("\nTest with three arrays");
		calc = new IntPermutationCalc(permArrsThree);
		IntPermutationCalc.dumpPerms(calc.getAll());
	}
}

