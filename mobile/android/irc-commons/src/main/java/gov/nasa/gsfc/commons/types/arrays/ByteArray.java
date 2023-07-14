//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
// $Log: 
//  7	IRC	   1.6		 11/12/2001 5:00:59 PMJohn Higinbotham Javadoc
//	   update.
//  6	IRC	   1.5		 7/27/1999 9:25:24 AM Ken Wootton	 This branch is
//	   from the Version 2.0 version of the tree to version 2.1 and beyond.
//  5	IRC	   1.4		 6/29/1999 11:49:38 AMKen Wootton	   The current
//	   VSS tree needs to be "branched" to allow for development of
//	   two simultaneous versions of the software, namely 2.0 (July release) and
//	   2.1
//	   (August release).  The idea is that there will be changes made for the
//	   July
//	   release that will not be placed in the August release, such as retrofited
//	   changes to meet performance.  Also, there needs to be a place where people
//	   can work on changes for the August release and not alter the software that
//	   will be release in July, such as the new GUI.  The soon to come branching
//	   will hopefully solve this problem by providing two separate development
//	   trees until the July release.  I assume that at that time some type of
//	   merging of the trees must occur.
// 
//  4	IRC	   1.3		 5/20/1999 12:09:22 PMCarl F. Hostetter Solved
//	   sign-bit preservation problem in toInt(), toLong() methods.
//  3	IRC	   1.2		 5/20/1999 10:06:18 AMCarl F. Hostetter Tweak
//  2	IRC	   1.1		 5/19/1999 2:42:52 PM Carl F. Hostetter Added
//	   toShort(byte[]), toInt(byte[]), toLong(byte[]), and
//	   printByteArray() methods.
//  1	IRC	   1.0		 5/18/1999 10:06:32 PMCarl F. Hostetter 
// $
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

import java.io.PrintStream;


/**
 *  The ByteArray class can be used to convert primitive numeric
 *  types into byte arrays, and vice versa.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	05/19/99
 *  @author		C. Hostetter/588
**/
public class ByteArray
{
	/**
	 * Constants used for byte array conversion.
	 *
	**/
	public interface Constants
	{
		public static final int NUM_BITS_PER_BYTE   = 8;
		public static final int BYTE_MASK		   = 0xFF;
		public static final long BYTE_MASK_LONG	 = 0xFFL;
		public static final int NUM_BYTES_PER_SHORT = 2;
		public static final int NUM_BITS_PER_SHORT  = NUM_BITS_PER_BYTE * NUM_BYTES_PER_SHORT;
		public static final int NUM_BYTES_PER_INT   = 4;
		public static final int NUM_BITS_PER_INT	= NUM_BITS_PER_BYTE * NUM_BYTES_PER_INT;
		public static final int NUM_BYTES_PER_LONG  = 8;
		public static final int NUM_BITS_PER_LONG   = NUM_BITS_PER_BYTE * NUM_BYTES_PER_LONG;
	}

	/**
	 *  Returns a new byte array that represents the concatenation of the two given 
	 *  byte arrays.
	 *
	 *  @param first A byte array
	 *  @param second A byte array
	 *  @return A new byte array that represents the concatenation of the two given 
	 *  		byte arrays
	**/
	
	public static byte[] concatenate(byte[] first, byte[] second)
	{
		if (first == null)
		{
			first = new byte[0];
		}
		
		if (second == null)
		{
			second = new byte[0];
		}
		
		byte[] result = new byte[first.length + second.length];
		
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		
		return (result);
	}
	
	
	/**
	 *  Returns a byte array representation of the given short value.
	 *
	 *  @param value An short value
	 *  @return A byte array representation of the given short value
	**/
	public static byte[] toByteArray(short value)
	{
		byte[] result = new byte[Constants.NUM_BYTES_PER_SHORT];

		for (int byteIndex = 0, shift = Constants.NUM_BITS_PER_SHORT -
			Constants.NUM_BITS_PER_BYTE;
			byteIndex < Constants.NUM_BYTES_PER_SHORT;
			byteIndex++, shift -= Constants.NUM_BITS_PER_BYTE)
		{
			byte byteValue = (byte) ((value >>> shift) & Constants.BYTE_MASK);
			result[byteIndex] = byteValue;
		}

		return (result);
	}

	/**
	 *  Converts the given byte array into a short value.
	 *
	 *  @param byteArray A byte array
	 *  @return The short value of the given byte array
	 *  @throws IllegalArgumentException if the given byteArray has more bytes
	 *	  than a short.
	**/
	public static short toShort(byte[] byteArray)
	{
		short result = 0;

		if (byteArray.length <= Constants.NUM_BYTES_PER_SHORT)
		{
			for (int byteIndex = 0, shift = Constants.NUM_BITS_PER_SHORT -
				Constants.NUM_BITS_PER_BYTE;
				byteIndex < Constants.NUM_BYTES_PER_SHORT;
				byteIndex++, shift -= Constants.NUM_BITS_PER_BYTE)
			{
				byte byteValue = byteArray[byteIndex];
				short byteContribution = (short) (byteValue << shift);
				result += byteContribution;
			}
		}
		else
		{
			String detail = "ByteArray toShort(byte[]) method was " +
				"passed a byte array having more than " +
				Constants.NUM_BYTES_PER_SHORT + " bytes";

			throw (new IllegalArgumentException(detail));
		}

		return (result);
	}

	/**
	 *  Returns a byte array representation of the given int value.
	 *
	 *  @param value An int value
	 *  @return A byte array representation of the given int value
	**/
	public static byte[] toByteArray(int value)
	{
		byte[] result = new byte[Constants.NUM_BYTES_PER_INT];

		for (int byteIndex = 0, shift = Constants.NUM_BITS_PER_INT -
			Constants.NUM_BITS_PER_BYTE;
			byteIndex < Constants.NUM_BYTES_PER_INT;
			byteIndex++, shift -= Constants.NUM_BITS_PER_BYTE)
		{
			byte byteValue = (byte) ((value >>> shift) & Constants.BYTE_MASK);
			result[byteIndex] = byteValue;
		}
		return (result);
	}

	/**
	 *  Converts the given byte array into an int value.
	 *
	 *  @param byteArray A byte array
	 *  @return The int value of the given byte array
	 *  @throws IllegalArgumentException if the given byteArray has more bytes
	 *	  than an int.
	**/
	public static int toInt(byte[] byteArray)
	{
		int result = 0;

		if (byteArray.length <= Constants.NUM_BYTES_PER_INT)
		{
			for (int byteIndex = 0, shift = Constants.NUM_BITS_PER_INT -
				Constants.NUM_BITS_PER_BYTE;
				byteIndex < Constants.NUM_BYTES_PER_INT;
				byteIndex++, shift -= Constants.NUM_BITS_PER_BYTE)
			{
				// We go through the following gesticulations to preserve the
				// sign bit.

				byte byteValue = byteArray[byteIndex];
				int unsignedByteValue = byteValue & Constants.BYTE_MASK;
				int byteContribution = unsignedByteValue << shift;
				result += byteContribution;
			}
		}
		else
		{
			String detail = "ByteArray toInt(byte[]) method was " +
				"passed a byte array having more than " +
				Constants.NUM_BYTES_PER_INT + " bytes";

			throw (new IllegalArgumentException(detail));
		}

		return (result);
	}

	/**
	 *  Returns a byte array representation of the given long value.
	 *
	 *  @param value A long value
	 *  @return A byte array representation of the given long value
	**/
	public static byte[] toByteArray(long value)
	{
		byte[] result = new byte[Constants.NUM_BYTES_PER_LONG];

		for (int byteIndex = 0, shift = Constants.NUM_BITS_PER_LONG -
			Constants.NUM_BITS_PER_BYTE;
			byteIndex < Constants.NUM_BYTES_PER_LONG;
			byteIndex++, shift -= Constants.NUM_BITS_PER_BYTE)
		{
			byte byteValue = (byte) ((value >>> shift) & Constants.BYTE_MASK);
			result[byteIndex] = byteValue;
		}

		return (result);
	}


	/**
	 *  Converts the given byte array into a long value.
	 *
	 *  @param byteArray A byte array
	 *  @return The long value of the given byte array
	 *  @throws IllegalArgumentException if the given byteArray has more bytes
	 *	  than a long.
	**/
	public static long toLong(byte[] byteArray)
	{
		long result = 0;

		if (byteArray.length <= Constants.NUM_BYTES_PER_LONG)
		{
			for (int byteIndex = 0, shift = Constants.NUM_BITS_PER_LONG -
				Constants.NUM_BITS_PER_BYTE;
				byteIndex < Constants.NUM_BYTES_PER_LONG;
				byteIndex++, shift -= Constants.NUM_BITS_PER_BYTE)
			{
				// We go through the following gesticulations to preserve the
				// sign bit.

				byte byteValue = byteArray[byteIndex];
				long unsignedByteValue = byteValue & Constants.BYTE_MASK_LONG;
				long byteContribution = unsignedByteValue << shift;
				result += byteContribution;
			}
		}
		else
		{
			String detail = "ByteArray toLong(byte[]) method was " +
				"passed a byte array having more than " +
				Constants.NUM_BYTES_PER_LONG + " bytes";

			throw (new IllegalArgumentException(detail));
		}

		return (result);
	}

	/**
	 *  Prints the given byte array to the given PrintStream.
	 *
	 *  @param byteArray A byte array
	 *  @param printStream The PrintStream to which to print the give ByteArray
	 *  @throws IllegalArgumentException if the given PrintStream is null
	**/
	public static void printByteArray(byte[] byteArray, PrintStream printStream)
	{
		if (printStream != null)
		{
			printStream.print("Array of " + byteArray.length +
				" bytes (hex): ");

			for (int byteIndex = 0; byteIndex < byteArray.length; byteIndex++)
			{
				byte byteValue = byteArray[byteIndex];
				int unsignedByteValue = byteValue & Constants.BYTE_MASK;
				printStream.print(unsignedByteValue);
			}

			printStream.println();
		}
		else
		{
			String detail = "ByteArray printByteArray(byte[], " +
				"PrintStream) method was passed a null PrintStream.";

			throw (new IllegalArgumentException(detail));
		}
	}

	/**
	 *  Prints the given byte array to System.out.
	 *
	 *  <P>Simply calls <code>printByteArray(byte[], PrintStream)</code> with
	 *  the given byte array and System.out.
	 *
	 *  @param byteArray A byte array
	**/
	public static void printByteArray(byte[] byteArray)
	{
		printByteArray(byteArray, System.out);
	}
}
