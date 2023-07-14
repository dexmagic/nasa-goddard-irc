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
//   3	IRC	   1.2		 3/19/2002 3:49:40 PM Ken Wootton	 Added a couple
//		of constructors and a couple of static colors.
//   2	IRC	   1.1		 7/20/2001 9:19:20 AM Ken Wootton	 Added
//		documentation.
//   1	IRC	   1.0		 7/17/2001 4:29:07 PM Ken Wootton	 
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

package gov.nasa.gsfc.commons.types.colors;

import java.awt.Color;

/**
 *	This class provides a simple extension of the @see Color class to 
 *  allow a user to easily store and restore the color as string.<p>
 *	Note that this class defines some constants for often used colors.
 *  The names of these constants are modelled after those supplied by the
 *  java.awt.Color class, not the NASA style guide.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Ken Wootton
 */
public class StringableColor extends Color
{
	/**
	 *  The color white
	 */
	public static final StringableColor white = 
		new StringableColor(Color.white);

	/**
	 *  The color black
	 */
	public static final StringableColor black = 
		new StringableColor(Color.black);

	//  Radix for the string
	private static final int HEX_RADIX = 16;

	/**
	 *	Get the string representation of the given color.  This 
	 *  representation can be used to create a color using the
	 *  @see StringableColor(String) constructor.
	 *
	 *  @param color  the color to get the string representation of
	 *
	 *  @return  the string representation as a 32-bit hexadecimal (sRGB)
	 */
	public static String toString(Color color)
	{
		return Integer.toHexString(color.getRGB()).toUpperCase();
	}

	/**
	 *	Create a new color from a string representation.  Note that
	 *  the @see #toString method will return the appropriate string
	 *  representation required by this constructor.
	 *	<p>The expected string is 32-bit hexidecimal in sRGB format
	 *  (bits 24-31 are alpha, 16-23 are red, 8-15 are green, and 0-7 are blue).
	 *
	 *  @param str  the string representation of the color
	 */
	public StringableColor(String str)
	{
		super((int) Long.parseLong(str, HEX_RADIX));
	}

	/**
	 *	Create a new stringable color from the given color.
	 *
	 *  @param color  color
	 */
	public StringableColor(Color color)
	{
		this(color.getRed(), color.getGreen(), color.getBlue(), 
			 color.getAlpha());
	}

	/**
	 *	Creates an opaque sRGB color with the specified red, green, and blue 
	 *  values in the range (0 - 255).
	 *
	 *  @param red  the red component
	 *  @param green the green component
	 *  @param blue  the blue component
	 */
	public StringableColor(int red, int green, int blue)
	{
		super(red, green, blue);
	}

	/**
	 *	Creates an sRGB color with the specified red, green, blue, and 
	 *  alpha values in the range (0 - 255).
	 *
	 *  @param red  the red component
	 *  @param green the green component
	 *  @param blue  the blue component
	 *  @param alpha  the alpha component
	 */
	public StringableColor(int red, int green, int blue, int alpha)
	{
		super(red, green, blue);
	}

	/**
	 *	Get the string representation of the color.  This 
	 *  representation can be used to create a color using the
	 *  @see StringableColor(String) constructor.
	 *
	 *  @return  the string representation as a 32-bit hexadecimal (sRGB)
	 */
	public String toString()
	{
		return toString(this);
	}
}

