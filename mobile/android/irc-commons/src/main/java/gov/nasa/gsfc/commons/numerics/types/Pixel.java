//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Pixel.java,v $
//  Revision 1.4  2005/08/17 18:41:48  chostetter_cvs
//  Fixed typo in docs
//
//  Revision 1.3  2004/09/11 19:22:44  chostetter_cvs
//  BasisBundle creation from XML now works, except for DataBuffer units
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/07/02 02:38:53  chostetter_cvs
//  Moved Pixel from data to commons package
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
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
import java.util.LinkedList;

import gov.nasa.gsfc.commons.types.strings.FullStringTokenizer;

/**
 * This class represents an artibrary-dimensioned integral point in
 * space.  Pixels are used to further qualify channels by associating
 * them with a position in some grid (e.g., a detector grid).  Pixels
 * are immutable.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/08/17 18:41:48 $
 * @author Steve Clark
 */

public class Pixel implements Comparable, Serializable
{
	//  String representation constants
	private static final String PIXEL_PREFIX = "[";
	private static final String COORD_SEP = ",";
	private static final String PIXEL_SUFFIX = "]";

	//  Exception messages.
	private static final String BAD_FORMAT_MSG = 
		"Pixel is not formatted correctly.";

	/**
	 * Coordinates of the pixel
	 */
	private int[] fCoordinates;

	/**
	 *  Create a new pixel using its string representation.
	 *
	 *  @param str  string representation of the pixel, presumably retrieved
	 *			  from the @see #toString method of this class
	 *
	 *  @throws IllegalArgumentException  if the given string cannot be
	 *									parsed
	 */
	public Pixel(String str)
		throws IllegalArgumentException
	{
		//  Get the indices of the prefix and suffix.
		int prefixIndex = str.indexOf(PIXEL_PREFIX);
		int suffixIndex = str.indexOf(PIXEL_SUFFIX);

		//  Make sure prefix and suffix exist.
		if (prefixIndex >= 0 && suffixIndex >= 0)
		{
			LinkedList coordList = new LinkedList();
			//  Tokenize the string between the prefix and suffix.
			FullStringTokenizer coordTok = new FullStringTokenizer(
				str.substring(prefixIndex + 1, suffixIndex), COORD_SEP);

			//  Walk through the coordinates in the string, adding each to 
			//  the list.
			while (coordTok.hasMoreTokens())
			{
				coordList.add(new Integer(coordTok.nextToken().trim()));
			}

			//  Convert the coordinate list to array of ints. 
			fCoordinates = new int[coordList.size()];
			for (int i = 0; i < fCoordinates.length; i++)
			{
				fCoordinates[i] = ((Integer) coordList.get(i)).intValue();
			}
		}

		//  Otherwise, there is a formatting problem.
		else
		{
			throw new IllegalArgumentException(BAD_FORMAT_MSG);
		}
	}

	/**
	 * Create a new pixel with the specified coordinates.
	 */
	public Pixel(int coords[])
	{
		fCoordinates = (int[]) coords.clone();
	}

	/**
	 * Create a new one-dimensional pixel with the specified coordinate.
	 */
	public Pixel(int c1)
	{
		fCoordinates = new int[] { c1 };
	}

	/**
	 * Create a new two-dimensional pixel with the specified coordinates.
	 */
	public Pixel(int c1, int c2)
	{
		fCoordinates = new int[] { c1, c2 };
	}

	/**
	 * Create a new three-dimensional pixel with the specified coordinates.
	 */
	public Pixel(int c1, int c2, int c3)
	{
		fCoordinates = new int[] { c1, c2, c3 };
	}

	/**
	 * Retrieve the coordinates of the pixel.
	 */
	public int[] getCoordinates()
	{
		return fCoordinates;
	}

	/**
	 * Retrieve the dimensionality of the pixel (i.e., how many
	 * coordinates it has).
	 */
	public int getDimension()
	{
		return fCoordinates.length;
	}

	/**
	 * Compare two pixels for equality
	 */
	public boolean equals(Object cmp)
	{
		if ((cmp == null) || (getClass() != cmp.getClass()))
		{
			return false;
		}

		int me[] = getCoordinates();
		int you[] = ((Pixel) cmp).getCoordinates();

		if (me.length == you.length)
		{
			for (int i = me.length; --i >= 0; )
			{
				if (me[i] != you[i])
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Compare the specified Pixel to the receiver.  Comparator
	 * proceeds from the first to the last coordinate; if a difference
	 * is found, the return value is the receiver's value minus the
	 * argument's value.  If no difference is found before one or both
	 * sets of coordinates is exhausted, then the return value is the
	 * receiver's dimensionality minus the argument's dimensionality.
	 *
	 * @param cmp	The object to compare against
	 *
	 * @return	The integer difference between the two Pixels
	 *
	 * @exception ClassCastException	The argument was not a Pixel
	 */
	public int compareTo(Object cmp) throws ClassCastException
	{
		int me[] = getCoordinates();
		if (cmp == null)
		{
			return me[0];
		}

		int you[] = ((Pixel) cmp).getCoordinates();
		int bound = Math.min(me.length, you.length);

		for (int i = 0; i < bound; ++i)
		{
			int diff = me[i] - you[i];

			if (diff != 0)
			{
				return diff;
			}
		}

		return me.length - you.length;
	}

	/**
	 *	Provide a string representation of the pixel.
	 *
	 *  @return  the string representation
	 */
	public String toString()
	{
		String res = PIXEL_PREFIX + fCoordinates[0];

		// List the coordinates.
		for (int i = 1; i < fCoordinates.length; ++i)
		{
			res += COORD_SEP + fCoordinates[i];
		}

		return res + PIXEL_SUFFIX;
	}
}
