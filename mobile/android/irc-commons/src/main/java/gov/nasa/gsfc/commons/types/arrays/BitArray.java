//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//  Development history is located at the end of the file.
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

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.BitSet;


/**
 *  The BitArray class is a Number that encloses a BitSet and implements the
 *  methods of BitSets in a rational manner. Like BitSets, BitArrays are
 *  growable. Unlike BitSets, the size() and length() methods return the number
 *  of bits in the bit pattern, whether set or not, not the index of the last
 *  set bit. E.g., the <code>length()</code> and <code>size()</code> of a
 *  BitArray whose bit pattern is 0000000 is 7.
 *
 *  <P>As a Number, the numeric values of BitArray are signed. To determine
 *  sign, the bit pattern of a BitArray is interpreted as a two's complement
 *  representation. If an unsigned value is needed, use the method
 *  <code>unsignedNumericValue()</code>.
 *
 *  <P>The methods of BitArray that can cause the BitArray to grow (see Javadoc
 *  comments) correctly maintain the resultant number of bits.
 *
 *  <P>Note that bit indices begin with 0. Also note that in a BitArray (as in
 *  a BitSet) with n bits, the bit at index 0 is the low-order (2^0) bit, that
 *  at index n-1 is the high-order (2^n-1) bit.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	11/08/99
 *  @author		C. Hostetter/588
**/


public class BitArray extends Number implements Cloneable, Comparable,
  Serializable
{
	public static final int MAX_NUM_BITS_BYTE	= 8;
	public static final int MAX_NUM_BITS_SHORT	= 16;
	public static final int MAX_NUM_BITS_INT	= 32;
	public static final int MAX_NUM_BITS_LONG	= 64;
	public static final int MAX_NUM_BITS_FLOAT	= MAX_NUM_BITS_LONG;
	public static final int MAX_NUM_BITS_DOUBLE	= MAX_NUM_BITS_LONG;

	private BitSet fBitSet;
	private int fNumBits = 0;


	/**
	 *  Default constructor of a BitArray.
	 *
	**/

	public BitArray()
	{
		fBitSet = new BitSet();
		fNumBits = 0;
	}


	/**
	 *  Constructs a BitArray having the given number of bits.
	 *
	 *  @param numBits The number of bits in the new BitArray
	 *  @throws NegativeArraySizeException if the specified number of bits is
	 *  	negative
	**/

	public BitArray(int numBits)
	{
		fBitSet = new BitSet(numBits);
		fNumBits = numBits;
	}


	/**
	 *  Constructs a BitArray having the same number of bits and bit pattern
	 *  as the given BitArray.
	 *
	 *  @param source A BitArray
	**/

	public BitArray(BitArray source)
	{
		fBitSet = (BitSet) source.fBitSet.clone();
		fNumBits = source.size();
	}


	/**
	 *  Constructs a BitArray whose bits are configured according to the binary
	 *  values of the given array of bytes.
	 *
	 *  @param byteArray An array of bytes
	**/

	public BitArray(byte[] byteArray)
	{
	    initWithBytes( byteArray );

	}

	/**
     * @param byteArray
     */
    private void initWithBytes(byte[] byteArray)
    {
	    
		for (int byteIndex = 0; byteIndex < byteArray.length; byteIndex++)
		{
			byte currentByte = byteArray[byteIndex];
			Byte byteObject = new Byte(currentByte);
			BitArray bitArrayObject = new BitArray(byteObject, MAX_NUM_BITS_BYTE);

			prepend(bitArrayObject);
		}
        
    }


    /**
     * @param byteBuffer Buffer to get bits from
     */
    public BitArray(ByteBuffer byteBuffer)
	{
        int origPosition = byteBuffer.position();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        byteBuffer.position(origPosition); // leave buffer in original state
        initWithBytes(bytes);
	}
    
	/**
	 *  Constructs a BitArray whose bits are configured to represent the binary
	 *  value of the given Number, and which will occupy at least the given
	 *  number of bits (but possibly more, if the given value requires it).
	 *
	 *  @param value A Number
	 *  @param numBits The minimum number of bits the new BitArray should have
	 *  @throws NegativeArraySizeException if the specified number of bits is
	 *  	negative
	 *  @see #parseNumber
	**/

	public BitArray(Number number, int numBits)
	{
		this(numBits);

		parseNumber(number);

		if (numBits > fNumBits)
		{
			fNumBits = numBits;
		}
	}


	/**
	 *  Constructs a BitArray whose bits are configured to represent the binary
	 *  value of the given non-negative Number. For instance, invoking this
	 *  constructor with a Number whose value is 18 will result in a BitArray
	 *  whose String representation is "10010".
	 *
	 *  <P>Note: the given Number will be parsed based on its
	 *  <code>longValue()</code> value and so will be "truncated" to 64 bits.
	 *
	 *  @param value A non-negative Number
	**/

	public BitArray(Number number)
	{
		this(number, 0);
	}


	/**
	 *  Constructs a BitArray whose bits are configured according to the given
	 *  binary String, the characters of which must all be 0s or 1s, and which
	 *  will occupy at least the given number of bits (but possibly more, if
	 *  the given binary String requires it).
	 *
	 *  <P>The bit pattern will be read from right to left: i.e., the 2^0 bit
	 *  is assumed to be the last character in the String, the 2^1 bit is the
	 *  penultimate character, and so on; so that, e.g. a valid binary String
	 *  representation of 18 is "10010", which will result in a BitArray whose
	 *  2nd (bit index 1) and 5th (bit index 4) bits (only) will be set.
	 *
	 *  @param binaryString A String whose characters are all 0s or 1s
	 *  @param numBits The minimum number of bits the new BitArray should have
	 *  @throws NumberFormatException if the given String is not a parsable
	 *  	binary String
	 *  @throws NegativeArraySizeException if the specified number of bits is
	 *  	negative
	**/

	public BitArray(String binaryString, int numBits)
		throws NumberFormatException
	{
		this(numBits);

		parseString(binaryString);

		if (numBits > fNumBits)
		{
			fNumBits = numBits;
		}
	}


	/**
	 *  Constructs a BitArray whose bits are configured according to the given
	 *  binary String, the characters of which must all be 0s or 1s or spaces.
	 *  The spaces are allowed because there are cases when the input string
	 *  contains spaces every 8 bits, for example, for clarity.
	 *
	 *  <P>The bit pattern will be read from right to left: i.e., the 2^0 bit
	 *  is assumed to be the last character in the String, the 2^1 bit is the
	 *  penultimate character, and so on; so that, e.g. a valid binary String
	 *  representation of 18 is "10010", which will result in a BitArray whose
	 *  2nd (bit index 1) and 5th (bit index 4) bits (only) will be set.
	 *
	 *  @param binaryString A String whose characters are all 0s or 1s or spaces
	 *  @throws NumberFormatException if the given String is not a parsable
	 *  	binary String
	**/

	public BitArray(String binaryString) throws NumberFormatException
	{
		this(binaryString, 0);
	}


	/**
	 *  Parses the given binary String into this BitArray.
	 *
	 *  @param binaryString A String whose characters are all 0s or 1s or spaces
	 *  @throws NumberFormatException if the given String is not a parsable
	 *  	binary String
	**/

	protected void parseString(String binaryString)
	{
		int numBits = binaryString.length();

		for (int stringIndex = numBits - 1, bitIndex = 0; stringIndex >= 0;
			stringIndex--)
		{
			if (binaryString.charAt(stringIndex) == '1')
			{
				fBitSet.set(bitIndex++);
			}
			else if (binaryString.charAt(stringIndex) == '0')
			{
				fBitSet.clear(bitIndex++);
			}
			else if (binaryString.charAt(stringIndex) == ' ')
			{
			    // Skip space; reduce number of expected bits
			    // because the space wasn't a bit
			    numBits--;
			}
			else
			{
				String detail = "BitArray(String): charAt(" + stringIndex +
					") in String " + binaryString + " is invalid: " +
					binaryString.charAt(stringIndex) +" != 0 or 1";

				throw (new NumberFormatException(detail));
			}
		}

		fNumBits = numBits;
	}


	/**
	 *  Parses the given Number into this BitArray.
	 *
	 *  <P>Note: if the given Number is a Float or a Double, the resultant
	 *  BitArray will have the same bit pattern as the internal (and native)
	 *  bit representation of the Float or Double value, respectively. See the
	 *  methods Float.floatToIntBits() and Double.doubleToLongBits() for
	 *  further details.
	 *
	 *  @param value A Number
	**/

	protected void parseNumber(Number value)
	{
		int numBits = 0;

		if (value instanceof Float)
		{
			Float floatValue = (Float) value;
			int intBits = Float.floatToIntBits(floatValue.floatValue());
			Integer integerValue = new Integer(intBits);
			parseNumber(integerValue);
		}
		else if (value instanceof Double)
		{
			Double doubleValue = (Double) value;
			long longBits = Double.doubleToLongBits(doubleValue.doubleValue());
			Long longValue = new Long(longBits);
			parseNumber(longValue);
		}
		else
		{
			if (value instanceof Byte)
			{
				numBits = MAX_NUM_BITS_BYTE;
			}
			else if (value instanceof Integer)
			{
				numBits = MAX_NUM_BITS_INT;
			}
			else if (value instanceof Short)
			{
				numBits = MAX_NUM_BITS_SHORT;
			}
			else // Treat any other Number type as a Long
			{
				numBits = MAX_NUM_BITS_LONG;
			}

			String binaryString = Long.toBinaryString(value.longValue());

			int binaryStringLength = binaryString.length();

			if (binaryStringLength > numBits)
			{
				binaryString = binaryString.substring
					(binaryStringLength - numBits);
				binaryStringLength = numBits;
			}

			fNumBits = binaryStringLength;
			parseString(binaryString);
		}

	}


	/**
	 *  Returns a (deep) clone of this BitArray.
	 *
	 *  @return A clone of this BitArray
	**/

	public Object clone()
	{
		BitArray result = new BitArray();

		result.fBitSet = (BitSet) fBitSet.clone();
		result.fNumBits = fNumBits;

		return (result);
	}


	/**
	 *  Compares this BitArray to the given value Object, which must be a
	 *  Number.
	 *
	 *  <P>Here, simply converts the given Object into a Number, and then
	 *  returns the result of calling <code>compareTo(Number)</code> with it.
	 *
	 *  <P>If the value is null or is not a Number, this method throws an
	 *  IllegalArgumentException.
	 *
	 *  @param value A Number
	 *  @return The result of the comparison
	**/

	public int compareTo(Object value)
	{
		if (value != null)
		{
			if (value instanceof BitArray)
			{
				return (compareTo((BitArray) value));
			}
			else if (value instanceof Number)
			{
				return (compareTo((Number) value));
			}
			else if (value instanceof String)
			{
				return (compareTo((String) value));
			}
			else
			{
				throw (new IllegalArgumentException());
			}
		}
		else
		{
			throw (new IllegalArgumentException());
		}
	}


	/**
	 *  Compares this BitArray to the given Number numerically.
	 *
	 *  <P>If the numeric value of this BitArray is greater than
	 *  that of the given Number, this method returns 1; if less, it returns
	 *  -1; if the same, it returns 0.
	 *
	 *  <P>N.B.: Because this BitArray is compared by either its
	 *  <code>longValue()</code> or its <code>doubleValue()</code> (depending
	 *  on the type of the given Number), it must have 64 bits or less.
	 *
	 *  <P>If the given Number is null or of a non-numeric type (i.e., Integer,
	 *  Long, Double, etc.), this method throws an IllegalArgumentException.
	 *
	 *  @param target A Number
	 *  @return The result of the comparison
	**/

	public int compareTo(Number target)
	{
		int result = 0;

		try
		{
			if ((target instanceof Integer) || (target instanceof Long) ||
				(target instanceof Byte) || (target instanceof Short))
			{
				long thisLongValue = this.longValue();
				long targetLongValue = target.longValue();

				if (thisLongValue > targetLongValue)
				{
					result = 1;
				}
				else if (thisLongValue < targetLongValue)
				{
					result = -1;
				}
			}
			else if ((target instanceof Double) || (target instanceof Float))
			{
				double thisDoubleValue = this.doubleValue();
				double targetDoubleValue = target.doubleValue();

				if (thisDoubleValue > targetDoubleValue)
				{
					result = 1;
				}
				else if (thisDoubleValue < targetDoubleValue)
				{
					result = -1;
				}
			}
			else
			{
				throw (new IllegalArgumentException());
			}
		}
		catch (Exception e)
		{
			throw (new IllegalArgumentException());
		}

		return (result);
	}


	/**
	 *  Compares the String representation of this BitArray to the given
	 *  String lexicographically.
	 *
	 *  <P>This BitArray is converted into a String representation, which is
	 *  then compared lexicographically to the given String via the String
	 *  <code>compareTo(String)</code> method, and the result is returned.
	 *
	 *  @param target A String
	 *  @return The result of the comparison
	**/

	public int compareTo(String target)
	{
	   return (toString().compareTo(target));
	}


	/**
	 *  Compares this BitArray to the given target BitArray.
	 *
	 *  <P>If the numeric value of this BitArray is greater than that of the
	 *  given BitArray, this method returns 1. If it is less, this method
	 *  returns -1. If the values are equal, then this method returns 1 if
	 *  this BitArray has a greater number of bits than the target, -1 if it
	 *  has fewer, and 0 if has the same.
	 *
	 *  <P>N.B.: If either this or the given target BitArray has more than 64
	 *  significant bits, then if the two differ only in the low-order bits,
	 *  this will be a particularly expensive method, as it must perform a
	 *  bit-wise comparison starting at the highest order bit.
	 *
	 *  <P>If the given BitArray is null, this method throws an
	 *  IllegalArgumentException.
	 *
	 *  @param target A BitArray
	 *  @return The result of the comparison
	**/

	public int compareTo(BitArray target)
	{
		int result = 0;

		if (target != null)
		{
			// Optimization: If this BitArray has more significant bits than
			// the target, its value is greater than that of the target, and
			// the result is 1. If fewer, it is less than the target, and the
			// result is -1.

			int thisNumSigBits = this.getNumberOfSignificantBits();
			int targetNumSigBits = target.getNumberOfSignificantBits();

			if (thisNumSigBits > targetNumSigBits)
			{
				result = 1;
			}
			else if (thisNumSigBits < targetNumSigBits)
			{
				result = -1;
			}
			else
			{
				// If we reach this point, we know that the two BitArrays
				// involved in this comparison have the same number of
				// significant bits, and therefore are potentially equal.

				// First, we compare the numeric values of the two BitArrrays.

				// Optimization: If the number of significant bits is
				// MAX_NUM_BITS_LONG or less, we can just compare their numeric
				// values as longs. If the long value of this BitArray is
				// greater than that of the target BitArray, the result is 1.
				// If it is less, the result is -1. Otherwise, the result
				// remains 0.

				if (thisNumSigBits <= MAX_NUM_BITS_LONG)
				{
					long thisLongValue = this.longValue();
					long targetLongValue = target.longValue();

					if (thisLongValue > targetLongValue)
					{
						result = 1;
					}
					else if (thisLongValue < targetLongValue)
					{
						result = -1;
					}
				}
				else
				{
					// Otherwise, we need to do a bitwise comparison, starting
					// with the highest order bit and working down. When we
					// first encounter a pair of bits that are not equal,
					// if the bit of this BitArray is set, its value is greater,
					// and the result is 1. If it is clear, its value is lesser,
					// and the result is -1. If we find that all the bits are
					// equal, the values are equal, and the result remains 0.

					boolean stop = false;

					// Optimization (barely): Since we know that the two
					// BitArrays have the same number of significant bits, we
					// can skip comparing the highest order bit (which must
					// both be set).

					for (int bit = thisNumSigBits - 2;
						!stop && (bit >= 0); bit--)
					{
						boolean thisBit = this.get(bit);
						boolean targetBit = target.get(bit);

						if (thisBit != targetBit)
						{
							stop = true;

							if (thisBit == true)
							{
								result = 1;
							}
							else
							{
								result = -1;
							}
						}
					}
				}

				// If at this point result is still 0, then the values of the
				// two BitArrays involved in this comparison are equal. The
				// result is then determined by comparing their sizes. If this
				// BitArray has a greater size than the target BitArray, the
				// result is 1; if lesser, the result is -1; if the same, the
				// result remains 0.

				if (result == 0)
				{
					int thisSize = this.size();
					int targetSize = target.size();

					if (thisSize > targetSize)
					{
						result = 1;
					}
					else if (thisSize < targetSize)
					{
						result = -1;
					}
				}
			}

			return (result);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
	}


	/**
	 *  Determines whether the numeric value of BitArray equals the given
	 *  Number.
	 *
	 *  <P>Simply returns true if the result of calling
	 *  <code>compareTo(Number)</code> with the given Number is 0, false
	 *  otherwise.
	 *
	 *  <P>If the given Number is null or of a non-numeric type (i.e., Integer,
	 *  Long, Double, etc.), this method throws an IllegalArgumentException.
	 *
	 *  @param target A Number
	 *  @return True if the numeric value of this BitArray equals that of the
	 *	  given Number, false otherwise
	**/

	public boolean equals(Number target)
	{
		return (this.compareTo(target) == 0);
	}


	/**
	 *  Determines whether the String representation of this BitArray is
	 *  lexicographically equal to given String.
	 *
	 *  <P>Simply returns true if the result of calling
	 *  <code>compareTo(String)</code> with the given String is 0, false
	 *  otherwise.
	 *
	 *  <P>If the given BitArray is null, this method throws an
	 *  IllegalArgumentException.
	 *
	 *  @param target A String
	 *  @return True if the String representation of this BitArray equals the
	 *	  given String, false otherwise
	**/

	public boolean equals(String target)
	{
		return (this.compareTo(target) == 0);
	}


	/**
	 *  Determines whether this BitArray equals the given Object.
	 *
	 *  <P>Simply returns true if the result of calling
	 *  <code>compareTo(BitArray)</code> with the given BitArray is 0, false
	 *  otherwise.
	 *
	 *  @param target A BitArray
	 *  @return True if this BitArray equals the given BitArray, false
	 *  	otherwise
	**/

	public boolean equals(Object target)
	{
		if (target == this)
		{
			return true;
		}
		else
		{
			return (this.compareTo(target) == 0);
		}
	}


	/**
	 *  Returns the hashcode of this BitArray.
	 *
	 *  @return The hashcode of this BitArray
	**/

	public int hashcode()
	{
		return (fBitSet.hashCode());
	}


	public String toString()
	{
	    return toString(0);
	}
	
	/**
     * Returns a String representation of this BitArray with occassional spaces.
     * The bit pattern will be read from right to left: i.e., the 2^0 bit will
     * be the last character in the String, the 2^1 bit will be the previous
     * character, and so on; so that, e.g. the resulting String representation
     * of a BitArray representing binary 18 will be "10010".
     * 
     * @param spacePeriod How often (in terms of bits) a space should be inserted
     * @return A String representation of this BitArray
     */
	public String toString(int spacePeriod)
	{
		StringBuffer resultBuffer = new StringBuffer(fNumBits);

		for (int i = fNumBits - 1; i >= 0; i--)
		{
			if (fBitSet.get(i))
			{
				resultBuffer.append('1');
			}
			else
			{
				resultBuffer.append('0');
			}
			if (i > 0  && spacePeriod > 0 && i % spacePeriod == 0)
			{
			    resultBuffer.append(' ');
			}
		}

		return (resultBuffer.toString());
	}


	/**
	 *  Returns an array of bytes representing this BitAray partioned into
	 *  8-bit bytes. The most significant byte of the resulting byte array
	 *  will be at index 0.
	 *
	 *  @return  An array of bytes representing this BitAray partioned into
	 *	  8-bit bytes
	**/

	public byte[] getBytes()
	{
		byte [] result = null;

		if (fNumBits > 0)
		{
			int numBytes = fNumBits / MAX_NUM_BITS_BYTE;

			if ((fNumBits % MAX_NUM_BITS_BYTE) > 0)
			{
				numBytes++;
			}

			result = new byte[numBytes];

			for (int i = numBytes-1, start = 0; i >= 0; i--,
				start += MAX_NUM_BITS_BYTE)
			{
				result[i] = (copyNumBitsFrom(start, MAX_NUM_BITS_BYTE)).
					byteValue();
			}
		}

		return (result);
	}


	/**
	 *  Returns a Gray Code representation of this BitArray.
	 *
	 *  <P>For any sequence of n bits, the Gray Code is obtained by:
	 *  <OL>
	 *  <LI>Adding a 0 to before the leftmost bit; then
	 *  <LI>For each successive bit i, if bit i and bit i + 1 are the same,
	 *	  then bit i of the Gray Code is 0; otherwise, the bit is 1.
	 *  </OL>
	 *  <P>Ex: For bit sequence 10011, the Gray Code is generated as:
	 *  <OL>
	 *  <LI> -> 010011
	 *  <LI> Gray Code: 11010
	 *  </OL>
	 *
	 *  @return  A Gray Code representation of this BitArray
	**/

	public BitArray toGrayCode()
	{
		BitArray result = new BitArray(fNumBits);

		if (fNumBits > 0)
		{
			BitArray temp = new BitArray(this);

			temp.clear(fNumBits);

			for (int bitIndex = 0; bitIndex < fNumBits; bitIndex++)
			{
				if (temp.get(bitIndex) != temp.get(bitIndex + 1))
				{
					result.set(bitIndex);
				}
			}
		}

		return (result);
	}


	/**
	 *  Returns the Gray Code value corresponding to the given value.
	 *
	 *  @return  The Gray Code value corresponding to the given value
	**/

	public static int toGrayCode(int value)
	{
		int result = 0;

		Integer valueInteger = new Integer(value);

		BitArray binaryBits = new BitArray(valueInteger);

		BitArray grayCodeBits = binaryBits.toGrayCode();

		result = grayCodeBits.intValue();

		return (result);
	}


	/**
	 *  Returns the binary code representation corresponding to the given Gray
	 *  Code representation BitArray.
	 *
	 *  For an explanation of Gray Code, see the <code>toGrayCode()</code>
	 *  method.
	 *
	 *  @return The binary code representation corresponding to the given Gray
	 *	  Code representation BitArray
	**/

	public BitArray toBinaryCodeFromGrayCode()
	{
		BitArray result = new BitArray(fNumBits);

		if (fNumBits > 0)
		{
			boolean lastBit = false;

			if (get(fNumBits - 1))
			{
				result.set(fNumBits - 1);
				lastBit = true;
			}

			for (int bitIndex = fNumBits - 2; bitIndex >= 0; bitIndex--)
			{
				if (get(bitIndex))
				{
					if (lastBit == false)
					{
						result.set(bitIndex);
						lastBit = true;
					}
					else
					{
						lastBit = false;
					}
				}
				else
				{
					if (lastBit == true)
					{
						result.set(bitIndex);
					}
				}
			}
		}

		return (result);
	}


	/**
	 *   Invert every bit in this BitArray. I.e., all set bits are cleared, all
	 *   cleared bits are set. Note that leading 0s are significant.
	 *
	 *   <P>E.g., 00101001 -> 11010110; 11111111 -> 00000000;
	 *   00000000 -> 11111111; 01111111 -> 10000000
	 *
	**/

	public void invert()
	{
		for (int bitIndex =  this.size() - 1; bitIndex >= 0; bitIndex--)
		{
			if (get(bitIndex) == true)
			{
				clear(bitIndex);
			}
			else
			{
				set(bitIndex);
			}
		}
	}


	/**
	 *   Adds 1 to this BitArray, ingoring overflow.
	 *
	 *   <P>E.g., 00101001 -> 00101010; 11111111 -> 00000000;
	 *   00000000 -> 00000001; 01111111 -> 10000000
	 *
	**/

	public void addOneIgnoringOverflow()
	{
		int highBitIndex = this.size() - 1;
		boolean done = false;

		for (int bitIndex =  0; bitIndex <= highBitIndex && !done; bitIndex++)
		{
			if (get(bitIndex) == true)
			{
				clear(bitIndex);

				done = false;
			}
			else
			{
				set(bitIndex);

				done = true;
			}
		}
	}


	/**
	 *   Applies the one's complement operation to this BitArray. I.e., it
	 *   flips all the bits. Note that leading 0s are significant.
	 *
	 *   <P>Simply calls <code>invert()</code>.
	 *
	**/

	public void applyOnesComplement()
	{
		invert();
	}


	/**
	 *   Applies the two's complement operation to this BitArray. I.e., it
	 *   flips all the bits and then adds 1 to the result (ignoring overflow).
	 *	Note that leading 0s are significant.
	 *
	 *   <P>E.g., 00101001 -> 11010111; 11111111 -> 00000001;
	 *   00000000 -> 00000000; 01111111 -> 10000001
	 *
	**/

	public void applyTwosComplement()
	{
		// First, flip all the bits

		invert();

		// Then add 1, ignoring overflow

		addOneIgnoringOverflow();
	}


	/**
	 *   Returns true if the range of bits of this BitArray from the lowest-
	 *   order bit to that at the given bit index represents a negative number
	 *   (i.e., if the highest-order bit of the specified range is set), false
	 *   otherwise.
	 *
	 *   <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *   (as in a BitSet) with n bits, the bit at index 0 is the low-order
	 *   (2^0) bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *   @return True if the range of bits of this BitArray from the lowest-
	 *	  order bit to that at the given bit index represents a negative
	 *	  number (i.e., if the highest-order bit of the specified range is
	 *	  set), false otherwise
	**/

	public boolean isNegative(int highOrderBitIndex)
	{
		boolean result = false;

		result = this.get(highOrderBitIndex);

		return (result);
	}


	/**
	 *   Returns true if this BitArray represents a negative number (i.e., if
	 *   its highest-order bit is set), false otherwise.
	 *
	 *   @return True if this BitArray represents a negarive number (i.e., if
	 *	  its highest-order bit is set), false otherwise
	**/

	public boolean isNegative()
	{
		return (this.isNegative(this.size()));
	}


	/**
	 *  Returns the signed numeric value of the range of bits of this BitArray
	 *  between the indices startIndex and endIndex (inclusive). To determine
	 *  the sign, the bit pattern of the specified range of bits is interpreted
	 *  as a signed two's complement representation. For example, the signed
	 *  numeric value of the first 6 bits (indices 0, 5) of a BitArray whose
	 *  String representation is "000101111" will be -18.
	 *
	 *  <P>If endIndex is beyond the range of this BitArray, the range is
	 *  treated as if it the BitArray were padded out with zeros to that index.
	 *  Note that such a range is always positive (since the resulting virtual
	 *  high-order bit will be 0).
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  @param startIndex The index of the start (low-order) bit of the range
	 *	  of this BitArray whose signed numeric value we wish to determine
	 *  @param startIndex The index of the end (high-order) bit of the range
	 *	  of this BitArray whose signed numeric value we wish to determine
	 *  @return The signed numeric value of the range of bits of this BitArray
	 *	  between the indices startIndex and endIndex (inclusive)
	 *  @throws IndexOutOfBoundsException if either of the given indices is
	 *  	negative
	 *  @throws NumberFormatException if the number of bits indicated by the
	 *	  given indices is greater than the number of bits in a long (i.e.,
	 *	  (endIndex - startIndex) + 1 > 64)
	**/

	public long signedNumericValue(int startIndex, int endIndex)
		throws IndexOutOfBoundsException, NumberFormatException
	{
		long result = 0L;

		int size = this.size();

		if (size > 0)
		{
			if ((startIndex >= 0) && (endIndex >= startIndex))
			{
				boolean negative = false;

				if (endIndex < size)
				{
					negative = this.get(endIndex);
				}

				BitArray bitArray = this;

				if ((startIndex > 0) || (endIndex < size - 1))
				{
					bitArray = copyBitsBetween(startIndex, endIndex);
				}
				else
				{
					if (negative)
					{
						bitArray =  (BitArray) this.clone();
					}
				}

				if (negative)
				{
					bitArray.applyTwosComplement();
				}

				result = bitArray.unsignedNumericValue();

				if (negative)
				{
					result = 0 - result;
				}
			}
			else
			{
				throw (new IndexOutOfBoundsException());
			}
		}

		return (result);
	}


	/**
	 *  Returns the signed numeric value of this BitArray. To determine the
	 *  sign, the bit pattern of this BitArray is interpreted as a signed
	 *  two's complement representation. For example, the signed numeric value
	 *  of a BitArray whose String representation is "101111" will be -18.
	 *
	 *  @return The signed numeric value of this BitArray
	 *  @throws NumberFormatException if the <code>size()</code> of this
	 *	  BitArray is greater than the number of bits in a long (i.e., > 64)
	**/

	public long signedNumericValue()
		throws NumberFormatException
	{
		return (signedNumericValue(0, size() - 1));
	}


	/**
	 *  Returns the unsigned numeric value of the range of bits of this
	 *  BitArray between the indices startIndex and endIndex (inclusive). For
	 *  example, the numeric value of the first 5 bits (indices 0, 4) of a
	 *  BitArray whose String representation is "1010010010" will be 18.
	 *
	 *  <P>If endIndex is beyond the range of this BitArray, the range
	 *  selection stops at the end of the BitArray.
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  @param startIndex The index of the start (low-order) bit of the range
	 *	  of this BitArray whose unsigned numeric value we wish to determine
	 *  @param startIndex The index of the end (high-order) bit of the range
	 *	  of this BitArray whose unsigned numeric value we wish to determine
	 *  @return  The unsigned numeric value of the range of bits of this
	 *	  BitArray between the indices startIndex and endIndex (inclusive)
	 *  @throws IndexOutOfBoundsException if either of the given indices is
	 *  	negative
	 *  @throws NumberFormatException if the number of bits indicated by the
	 *	  given indices is greater than the number of bits in a long minus 1
	 *	  (to allow for the sign bit; i.e., (endIndex - startIndex) + 1 > 63)
	**/

	public long unsignedNumericValue(int startIndex, int endIndex)
		throws IndexOutOfBoundsException, NumberFormatException
	{
		long result = 0L;

		BitArray bitArray = this;
		int size = bitArray.size();

		if (size > 0)
		{
			if ((startIndex >= 0) && (endIndex >= startIndex))
			{
				if ((startIndex > 0) || (endIndex < size - 1))
				{
					bitArray = copyBitsBetween(startIndex, endIndex);
				}

				result = Long.parseLong(bitArray.toString(), 2);
			}
			else
			{
				throw (new IndexOutOfBoundsException());
			}
		}

		return (result);
	}


	/**
	 *  Returns the unsigned numeric value of this BitArray. For example, the
	 *  unsigned numeric value of a BitArray whose String representation is
	 *  "10010" will be 18.
	 *
	 *  @return  The unsigned numeric value of this BitArray
	 *  @throws NumberFormatException if the <code>size()</code> of this
	 *	  BitArray is greater than the number of bits in a long (i.e., > 64)
	**/

	public long unsignedNumericValue()
		throws NumberFormatException
	{
		return (unsignedNumericValue(0, size() - 1));
	}


	/**
	 *  Returns the byte value of this BitArray. For example, the byte value
	 *  of a BitArray whose String representation is "10010" will be 18.
	 *
	 *  <P>This BitArray must have 8 or fewer significant bits. Any high-order
	 *  0s will be disregarded in determining whether this BitArray will "fit"
	 *  into a byte.
	 *
	 *  <P>N.B.: The returned value is signed. To determine the sign, the bit
	 *  pattern of this BitArray is interpreted as a signed two's complement
	 *  representation.
	 *
	 *  @return  The byte value of this BitArray
	 *  @throws NumberFormatException if the value of this BitArray contains
	 *  	too many significant bits (i.e. > 8) to "fit" into a byte.
	**/

	public byte byteValue()
		throws NumberFormatException
	{
		byte result = 0;

		// This works because the length() of a BitSet is the index of its
		// highest-order set bit plus one.

		int numSignificantBits = fBitSet.length();

		if (numSignificantBits > 0)
		{
			if (numSignificantBits <= MAX_NUM_BITS_BYTE)
			{
				result = (byte) signedNumericValue(0, MAX_NUM_BITS_BYTE - 1);
			}
			else
			{
				String detail = "BitArray.byteValue() invoked on a BitArray " +
					"having " + numSignificantBits + " significant bits, " +
					"which is too many to fit into a byte (max " +
					MAX_NUM_BITS_BYTE + "). " + "BitArray: " + toString();

				throw (new NumberFormatException(detail));
			}
		}

		return (result);
	}


	/**
	 *  Returns the short value of this BitArray. For example, the short value
	 *  of a BitArray whose String representation is "10010" will be 18.
	 *
	 *  <P>N.B.: The returned value is signed. To determine the sign, the bit
	 *  pattern of this BitArray is interpreted as a signed two's complement
	 *  representation. If the unsigned value is needed, instead call
	 *  <code>unsignedNumericValue()</code>.
	 *
	 *  <P>This BitArray must have 16 or fewer significant bits. Any high-order
	 *  0s will be disregarded in determining whether this BitArray will "fit"
	 *  into a short.
	 *
	 *  @return  The short value of this BitArray
	 *  @throws NumberFormatException if the value of this BitArray contains
	 *  	too many significant bits (i.e. > 16) to "fit" into a short.
	**/

	public short shortValue()
		throws NumberFormatException
	{
		short result = 0;

		// This works because the length() of a BitSet is the index of its
		// highest-order set bit plus one.

		int numSignificantBits = fBitSet.length();

		if (numSignificantBits > 0)
		{
			if (numSignificantBits <= MAX_NUM_BITS_SHORT)
			{
				result = (short) signedNumericValue(0, MAX_NUM_BITS_SHORT - 1);
			}
			else
			{
				String detail = "BitArray.shortValue() invoked on a BitArray " +
					"having " + numSignificantBits + " significant bits, " +
					"which is too many to fit into a short (max " +
					MAX_NUM_BITS_SHORT + "). " + "BitArray: " + toString();

				throw (new NumberFormatException(detail));
			}
		}

		return (result);
	}


	/**
	 *  Returns the int value of this BitArray. For example, the int value
	 *  of a BitArray whose String representation is "10010" will be 18.
	 *
	 *  <P>N.B.: The returned value is signed. To determine the sign, the bit
	 *  pattern of this BitArray is interpreted as a signed two's complement
	 *  representation.
	 *
	 *  <P>N.B.: The returned value is signed. To determine the sign, the bit
	 *  pattern of this BitArray is interpreted as a signed two's complement
	 *  representation. If the unsigned value is needed, instead call
	 *  <code>unsignedNumericValue()</code>.
	 *
	 *  @return  The int value of this BitArray
	 *  @throws NumberFormatException if the value of this BitArray contains
	 *  	too many significant bits (i.e. > 32) to "fit" into an int.
	**/

	public int intValue()
		throws NumberFormatException
	{
		int result = 0;

		// This works because the length() of a BitSet is the index of its
		// highest-order set bit plus one.

		int numSignificantBits = fBitSet.length();

		if (numSignificantBits > 0)
		{
			if (numSignificantBits <= MAX_NUM_BITS_INT)
			{
				result = (int) signedNumericValue(0, MAX_NUM_BITS_INT - 1);
			}
			else
			{
				String detail = "BitArray.intValue() invoked on a BitArray " +
					"having " + numSignificantBits + " significant bits, " +
					"which is too many to fit into an int (max " +
					MAX_NUM_BITS_INT + "). " + "BitArray: " + toString();

				throw (new NumberFormatException(detail));
			}
		}

		return (result);
	}


	/**
	 *  Returns the long value of this BitArray. For example, the long value
	 *  of a BitArray whose String representation is "10010" will be 18.
	 *
	 *  <P>N.B.: The returned value is signed. To determine the sign, the bit
	 *  pattern of this BitArray is interpreted as a signed two's complement
	 *  representation. If the unsigned value is needed, instead call
	 *  <code>unsignedNumericValue()</code>.
	 *
	 *  <P>This BitArray must have 64 or fewer significant bits. Any high-order
	 *  0s will be disregarded in determining whether this BitArray will "fit"
	 *  into a long.
	 *
	 *  @return  The long value of this BitArray
	 *  @throws NumberFormatException if the value of this BitArray contains
	 *  	too many significant bits (i.e. > 64) to "fit" into a long.
	**/

	public long longValue()
		throws NumberFormatException
	{
		long result = 0;

		int numSignificantBits = getNumberOfSignificantBits();

		if (numSignificantBits > 0)
		{
			if (numSignificantBits <= MAX_NUM_BITS_LONG)
			{
				result = signedNumericValue(0, MAX_NUM_BITS_LONG - 1);
			}
			else
			{
				String detail = "BitArray.longValue() invoked on a BitArray " +
					"having " + numSignificantBits + " significant bits, " +
					"which is too many to fit into a long (max " +
					MAX_NUM_BITS_LONG + "). " + "BitArray: " + toString();

				throw (new NumberFormatException(detail));
			}
		}

		return (result);
	}


	/**
	 *  Returns the float value of this BitArray.
	 *
	 *  <P>This method simply returns the calling
	 *  <code>Float.intBitsToFloat(long bits)</code> with the result of
	 *  <code>intValue()</code>. Therefore, this BitArray must have 32 or
	 *  fewer significant bits.
	 *
	 *  @return  The float value of this BitArray
	 *  @throws NumberFormatException if the value of this BitArray contains
	 *  	too many significant bits (i.e. > 32) to "fit" into a long.
	 *  @see #intValue
	**/

	public float floatValue() throws NumberFormatException
	{
		return (Float.intBitsToFloat(intValue()));
	}


	/**
	 *  Returns the double value of this BitArray.
	 *
	 *  <P>This method simply returns the calling
	 *  <code>Double.longBitsToDouble(long bits)</code> with the result of
	 *  <code>longValue()</code>. Therefore, this BitArray must have 64 or
	 *  fewer significant bits.
	 *
	 *  @return  The double value of this BitArray
	 *  @throws NumberFormatException if the value of this BitArray contains
	 *  	too many significant bits (i.e. > 64) to "fit" into a long.
	 *  @see #longValue
	**/

	public double doubleValue() throws NumberFormatException
	{
		return (Double.longBitsToDouble(longValue()));
	}


	/**
	 *  Returns the number of bits in this BitArray. Note that leading 0s are
	 *  significant, so that, e.g., the <code>length()</code> of a BitArray
	 *  whose bit pattern is 0000000 is 7.
	 *
	 *  @return The number of bits in the this BitArray
	**/

	public int length()
	{
		return (fNumBits);
	}


	/**
	 *  Returns the number of bits in this BitArray.  Note that leading 0s are
	 *  significant, so that, e.g., the <code>size()</code> of a BitArray
	 *  whose bit pattern is 0000000 is 7.
	 *
	 *  @return The number of bits in the this BitArray
	**/

	public int size()
	{
		return (fNumBits);
	}


	/**
	 *  Returns the number of significant bits in this BitArray (i.e., the
	 *  number of bits up to and including the highest-indexed set bit).
	 *
	 *  @return The number of significant bits in this BitArray
	**/

	public int getNumberOfSignificantBits()
	{
		// This works because the length() of a BitSet is the index of its
		// highest-order set bit plus one.

		return (fBitSet.length());
	}


	/**
	 *  Returns the number of set bits in this BitArray.
	 *
	 *  @return The number of set bits in this BitArray
	**/

	public int getNumberOfSetBits()
	{
		int result = 0;

		int numSignificantBits = getNumberOfSignificantBits();

		for (int bitIndex = 0; bitIndex < numSignificantBits; bitIndex++)
		{
			if (get(bitIndex))
			{
				result++;
			}
		}

		return (result);
	}


	/**
	 *  Sets the bit of this BitArray at the given index.
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: if the given bit index indicates a bit beyond the current
	 *  end this BitArray, the BitArray will grow to accomodate that bit index.
	 *
	 *  @param An index into this BitArray
	 *  @throws IndexOutOfBoundsException if the given index is negative
	**/

	public void set(int bitIndex)
	{
		fBitSet.set(bitIndex);

		if (bitIndex + 1 > fNumBits)
		{
			fNumBits = bitIndex + 1;
		}
	}


	/**
	 *  Clears the bit of this BitArray at the given index.
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: if the given bit index indicates a bit beyond the current
	 *  end this BitArray, the BitArray will grow to accomodate that bit index.
	 *
	 *  @param An index into this BitArray
	 *  @throws IndexOutOfBoundsException if the given index is negative
	**/

	public void clear(int bitIndex)
	{
		fBitSet.clear(bitIndex);

		if (bitIndex + 1 > fNumBits)
		{
			fNumBits = bitIndex + 1;
		}
	}


	/**
	 *  Returns the bit of this BitArray at the given index.
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  @param An index into this BitArray
	 *  @return True if the bit at the given index is set, false otherwise
	 *  @throws IndexOutOfBoundsException if the given index is negative
	**/

	public boolean get(int bitIndex)
	{
		return (fBitSet.get(bitIndex));
	}


	/**
	 *  Returns a copy of the bits of this BitArray between the given start
	 *  and end bit indices (inclusive).
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>If the end index is greater than n-1, or less than start, this
	 *  method will copy the bits from the start index through the last (nth)
	 *  bit of this BitArray (inclusive).
	 *
	 *  @param start The index of this BitArray at which to start copying
	 *  @param end The index of this BitArray at which to end copying
	 *  @throws IndexOutOfBoundsException if either of the given indices is
	 *  	negative
	**/

	public BitArray copyBitsBetween(int start, int end)
	{
		BitArray result = null;

		if ((start >= 0) && (start < fNumBits))
		{
			if ((end < start) || (end > fNumBits - 1))
			{
				end = fNumBits - 1;
			}

			result = new BitArray(end - start + 1);

			for (int thisIndex = start, resultIndex = 0; thisIndex <= end;
				thisIndex++, resultIndex++)
			{
				if (get(thisIndex))
				{
					result.set(resultIndex);
				}
			}
		}

		return (result);
	}


	/**
	 *  Returns a copy of the given number of bits of this BitArray starting
	 *  at the given start index.
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>If the given number of bits to copy would extend past the end of
	 *  this BitArray, the copy will be from the start index to the end of
	 *  this BitArray.
	 *
	 *  @param start The index of this BitArray at which to start copying
	 *  @param numBits The number of bits to copy
	 *  @throws IndexOutOfBoundsException if the given index is negative
	**/

	public BitArray copyNumBitsFrom(int start, int numBits)
	{
		BitArray result = null;

		if ((start >= 0) && (start < fNumBits) && (numBits > 0))
		{
			result = copyBitsBetween(start, start + numBits - 1);
		}

		return (result);
	}


	/**
	 *  Prepends the given BitArray before the first (low-order) bit of this
	 *  BitArray.
	 *
	 *  <P>That is, a BitArray representing the the binary value of 18, which
	 *  would have a String representation "10010", would after a call of
	 *  <code>prepend(new BitArray("0011"))</code> represent the binary value
	 *  of 291, and have a String representation "100100011".
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param source A BitArray
	**/

	public void prepend(BitArray source)
	{
		int sourceSize = source.size();

		if (sourceSize > 0)
		{
			BitSet oldBitSet = fBitSet;
			int oldSize = fNumBits;

			fBitSet = new BitSet(oldSize + sourceSize);
			fNumBits += sourceSize;

			for (int i = 0; i < sourceSize; i++)
			{
				if (source.get(i))
				{
					fBitSet.set(i);
				}
			}

			for (int i = 0; i < oldSize; i++)
			{
				if (oldBitSet.get(i))
				{
					fBitSet.set(sourceSize + i);
				}
			}
		}
	}


	/**
	 *  Prepends the given binary String before the first (low-order) bit of
	 *  this BitArray.
	 *
	 *  <P>Simply constructs a new BitArray with the given String using the
	 *  <code>BitArray(String)</code> constructor (q.v.), and then calls
	 *  <code>prepend(BitArray)</code> with the result.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param binaryString A String whose characters are all 0s or 1s
	 *  @throws NumberFormatException if the given String is not a parsable
	 *  	binary String
	**/

	public void prepend(String binaryString)
	{
		prepend(new BitArray(binaryString));
	}


	/**
	 *  Prepends the given Number before the first (low-order) bit of this
	 *  BitArray.
	 *
	 *  <P>Simply constructs a new BitArray with the given Number using the
	 *  <code>BitArray(Number)</code> constructor (q.v.), and then calls
	 *  <code>prepend(BitArray)</code> with the result.
	 *
	 *  <P>Note: the given Number will be parsed based on its
	 *  <code>longValue()</code> value and so will be "truncated" to 64 bits.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param number A Number
	**/

	public void prepend(Number number)
	{
		prepend(new BitArray(number));
	}


	/**
	 *  Appends the given BitArray after the last (high-order) bit of this
	 *  BitArray.
	 *
	 *  <P>That is, a BitArray representing the the binary value of 18, which
	 *  would have a String representation "10010", would after a call of
	 *  <code>append(new BitArray("0011"))</code> represent the binary value
	 *  of 114, and have a String representation "001110010".
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param source A BitArray
	**/

	public void append(BitArray source)
	{
		int sourceSize = source.size();

		if (sourceSize > 0)
		{
			BitSet oldBitSet = fBitSet;
			int oldSize = fNumBits;

			fBitSet = new BitSet(oldSize + sourceSize);
			fNumBits += sourceSize;

			for (int i = 0; i < oldSize; i++)
			{
				if (oldBitSet.get(i))
				{
					fBitSet.set(i);
				}
			}

			for (int i = 0; i < sourceSize; i++)
			{
				if (source.get(i))
				{
					fBitSet.set(oldSize + i);
				}
			}
		}
	}


	/**
	 *  Appends the given binary String after the last (high-order) bit of this
	 *  BitArray.
	 *
	 *  <P>Simply constructs a new BitArray with the given String using the
	 *  <code>BitArray(String)</code> constructor (q.v.), and then calls
	 *  <code>append(BitArray)</code> with the result.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param binaryString A String whose characters are all 0s or 1s
	 *  @throws NumberFormatException if the given String is not a parsable
	 *  	binary String
	**/

	public void append(String binaryString)
	{
		append(new BitArray(binaryString));
	}


	/**
	 *  Appends the given Number after the last (high-order) bit of this
	 *  BitArray.
	 *
	 *  <P>Simply constructs a new BitArray with the given Number, and which
	 *  will occupy at least the given number of bits (but possibly more, if
	 *  the given value requires it). using the
	 *  <code>BitArray(Number, int)</code> constructor (q.v.) with the given
	 *  Number and number or bits, and then calls <code>append(BitArray)</code>
	 *  with the result.
	 *
	 *  <P>Note: the given Number will be parsed based on its
	 *  <code>longValue()</code> value and so will be "truncated" to 64 bits.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param number A Number
	 *  @param numBits The minimum number of bits the new BitArray should have
	**/

	public void append(Number number, int numBits)
	{
		append(new BitArray(number, numBits));
	}


	/**
	 *  Appends the given Number after the last (high-order) bit of this
	 *  BitArray.
	 *
	 *  <P>Simply constructs a new BitArray with the given Number using the
	 *  <code>BitArray(Number)</code> constructor (q.v.), and then calls
	 *  <code>append(BitArray)</code> with the result.
	 *
	 *  <P>Note: the given Number will be parsed based on its
	 *  <code>longValue()</code> value and so will be "truncated" to 64 bits.
	 *
	 *  <P>GROWABLE: this method will aways cause this BitArray to grow.
	 *
	 *  @param number A Number
	**/

	public void append(Number number)
	{
		append(new BitArray(number));
	}


	/**
	 *  Copies the bit pattern of the given BitArray into this BitArray,
	 *  starting at the given bit index of this BitArray, overwriting the
	 *  current pattern starting at the given index of this BitArray, and
	 *  growing as needed to accomodate the imposed pattern. That is, a
	 *  BitArray representing the the binary value of 18, which would have a
	 *  String representation "10010", would after a call of
	 *  <code>copyInto(new BitArray("0011"), 3)</code> represent the binary
	 *  value of 26, and have a String representation "0011010".
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: if the given BitArray spills beyond the current end of
	 *  this BitArray, this BitArray will grow to accomodate it.
	 *
	 *  @param source A BitArray
	 *  @param bitIndex The index of the bit of this BitArray at which to
	 *  	start copying
	 *  @throws IndexOutOfBoundsException if the given index is negative
	**/

	public void copyInto(BitArray source, int bitIndex)
	{
		int sourceSize = source.size();

		for (int i = 0; i < sourceSize; i++)
		{
			if (source.get(i))
			{
				fBitSet.set(bitIndex + i);
			}
			else
			{
			    fBitSet.clear(bitIndex + i);
			}
		}

		fNumBits = Math.max(fNumBits, bitIndex + sourceSize);
	}


	/**
	 *  Copies the bit pattern of the given BitArray into this BitArray,
	 *  starting at the beginning of this BitArray and overwriting the
	 *  current pattern, and growing as needed to accomodate the imposed
	 *  pattern.
	 *
	 *  <P>Simply calls <code>copyInto(source, 0)</code>.
	 *
	 *  <P>GROWABLE: if the given BitArray spills beyond the current end of
	 *  this BitArray, this BitArray will grow to accomodate it.
	 *
	 *  @param source A BitArray
	**/

	public void copyInto(BitArray source)
	{
		copyInto(source, 0);
	}


	/**
	 *  Shifts the current bits of this BitArray higher by the given number of
	 *  bits, padding the lower bits with 0s. That is, a BitArray representing
	 *  the the binary value of 18, which would have a String representation
	 *  "10010", would after a call of <code>shiftHigher(2)</code> represent
	 *  the binary value of 72, and have a String representation "1001000".
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: this method will always cause this BitArray to grow.
	 *
	 *  @param An index into this BitArray
	 *  @throws IllegalArgumentException if the specified number of bits is
	 *  	negative
	**/

	public void shiftHigher(int numBits)
	{
		if (numBits > 0)
		{
			BitArray padding = new BitArray(numBits);

			prepend(padding);
		}
		else if (numBits < 0)
		{
			String detail = "BitArray.shiftHigher(int) was passed a negative " +
				"number of bits.";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  This method simply renames (and calls) <code>shiftHigher(int)</code>,
	 *  q.v.
	 *
	 *  @param An index into this BitArray
	 *  @see #shiftHigher
	 *  @throws IllegalArgumentException if the specified number of bits is
	 *  	negative
	**/

	public void shiftLeft(int numBits)
	{
		shiftHigher(numBits);
	}


	/**
	 *  Shifts the current bits of this BitArray lower by the given number of
	 *  bits, and decreases its number of bits accordingly. That is, a BitArray
	 *  representing the the binary value of 18, which would have a String
	 *  representation "10010", would after a call of
	 *  <code>shiftLower(2)</code> represent the binary value of 4, and have a
	 *  String representation "100".
	 *
	 *  <P>Note that bit indices begin with 0.  Also note that in a BitArray
	 *  (as in a BitSet) with n bits, the bit at index 0 is the low-order (2^0)
	 *  bit, that at index n-1 is the high-order (2^n-1) bit.
	 *
	 *  <P>GROWABLE: this method will always cause this BitArray to shrink.
	 *
	 *  @param An index into this BitArray
	 *  @throws IllegalArgumentException if the specified number of bits is
	 *  	negative
	**/

	public void shiftLower(int numBits)
	{
		if (numBits > 0)
		{
			fBitSet = copyBitsBetween(numBits, fNumBits).fBitSet;
			fNumBits -= numBits;
		}
		else if (numBits < 0)
		{
			String detail = "BitArray.shiftLower(int) was passed a negative " +
				"number of bits.";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  This method simply renames (and calls) <code>shiftLower(int)</code>,
	 *  q.v.
	 *
	 *  @param An index into this BitArray
	 *  @see #shiftLower
	 *  @throws IllegalArgumentException if the specified number of bits is
	 *  	negative
	**/

	public void shiftRight(int numBits)
	{
		shiftLower(numBits);
	}


	/**
	 *  Pads this BitArray out to have at least the given minimum number of
	 *  bits by adding high-order ("leading") 0s. Note that, if this BitArray
	 *  already has more than the given number of bits, this method will do
	 *  nothing.
	 *
	 *  <P>GROWABLE: this method may cause this BitArray to grow.
	 *
	 *  @param The minimum number of bits to which this BitArray should be
	 *  	padded.
	**/

	public void pad(int minNumBits)
	{
		if (fNumBits < minNumBits)
		{
			fBitSet.clear(minNumBits - 1);
			fNumBits = minNumBits;
		}
	}


	/**
	 *  Trims any high-order ("leading") 0s from this BitArray. If this
	 *  BitArray contains only 0s, it will "shrink" to have only one bit, a 0.
	 *
	 *  <P>GROWABLE: this method may cause this BitArray to shrink.
	**/

	public void trim()
	{
		// The following works because the length() of a BitSet is the index
		// of the highest-order set bit plus one.

		fNumBits = fBitSet.length();
	}


	/**
	 *  Trims any high-order ("leading") 0s from this BitArray, shrinking it
	 *  to have at least the given minimum number of bits.
	 *
	 *  <P>GROWABLE: this method may cause this BitArray to shrink.
	 *
	 *  @param The minimum number of bits to which this BitArray should be
	 *  	trimmed.
	**/

	public void trim(int minNumBits)
	{
		// The following works because the length() of a BitSet is the index
		// of the highest-order set bit plus one.

		fNumBits = Math.max(fBitSet.length(), minNumBits);
	}


	/**
	 *  Reverses the order of the bits in this BitArray. That is, a BitArray
	 *  representing the the binary value of 18, which would have a String
	 *  representation "10010", would after a call of <code>reverse()</code>
	 *  represent the binary value of 7, and have a String representation
	 *  "01001".
	**/

	public void reverse()
	{
		if (fNumBits > 1)
		{
			BitSet oldBitSet = fBitSet;
			fBitSet = new BitSet(fNumBits);

			for (int i = 0, j = fNumBits - 1; i < fNumBits; i++, j--)
			{
				if (oldBitSet.get(i))
				{
					fBitSet.set(j);
				}
			}
		}
	}


	/**
	 *  Performs a logical AND of this BitArray with the given BitArray.
	 *
	 *  <P>GROWABLE: if the given BitArray is larger than this BitArray, this
	 *  BitArray will grow to accomodate it. If the given BitArray is shorter than 
	 *  this BitArray, this BitArray will shrink to the size of the given BitArray.
	 *
	 *  @param A BitArray
	**/

	public void and(BitArray bitArray)
	{
		BitSet source = bitArray.fBitSet;
		int sourceNumBits = bitArray.size();

		if (sourceNumBits != fNumBits)
		{
			source = fBitSet;
			fBitSet = (BitSet) bitArray.fBitSet.clone();
			fNumBits = sourceNumBits;
		}

		fBitSet.and(source);
	}


	/**
	 *  Performs a logical ~AND of this BitArray with the given BitArray.
	 *
	 *  <P>GROWABLE: if the given BitArray is larger than this BitArray, this
	 *  BitArray will grow to accomodate it. If the given BitArray is shorter than 
	 *  this BitArray, this BitArray will shrink to the size of the given BitArray.
	 *
	 *  @param A BitArray
	**/

	public void andNot(BitArray bitArray)
	{
		BitSet source = bitArray.fBitSet;
		int sourceNumBits = bitArray.size();

		if (sourceNumBits != fNumBits)
		{
			source = fBitSet;
			fBitSet = (BitSet) bitArray.fBitSet.clone();
			fNumBits = sourceNumBits;
		}

		fBitSet.andNot(source);
	}


	/**
	 *  Performs a logical OR of this BitArray with the given BitArray.
	 *
	 *  <P>GROWABLE: if the given BitArray is larger than this BitArray, this
	 *  BitArray will grow to accomodate it. If the given BitArray is shorter than 
	 *  this BitArray, this BitArray will shrink to the size of the given BitArray.
	 *
	 *  @param A BitArray
	**/

	public void or(BitArray bitArray)
	{
		BitSet source = bitArray.fBitSet;
		int sourceNumBits = bitArray.size();

		if (sourceNumBits != fNumBits)
		{
			source = fBitSet;
			fBitSet = (BitSet) bitArray.fBitSet.clone();
			fNumBits = sourceNumBits;
		}

		fBitSet.or(source);
	}


	/**
	 *  Performs a logical XOR of this BitArray with the given BitArray.
	 *
	 *  <P>GROWABLE: if the given BitArray is larger than this BitArray, this
	 *  BitArray will grow to accomodate it. If the given BitArray is shorter than 
	 *  this BitArray, this BitArray will shrink to the size of the given BitArray.
	 *
	 *  @param A BitArray
	**/

	public void xor(BitArray bitArray)
	{
		BitSet source = bitArray.fBitSet;
		int sourceNumBits = bitArray.size();

		if (sourceNumBits != fNumBits)
		{
			source = fBitSet;
			fBitSet = (BitSet) bitArray.fBitSet.clone();
			fNumBits = sourceNumBits;
		}

		fBitSet.xor(source);
	}

	/**
	 * Converts the BitArray to an array of integer positions of all
	 * set bits.  For example, if the BitArray is "01001", this will
	 * return an array {0,3}
	 * @return the array containing the positions of all set bits
	 */
	public int[] asIntArray()
	{
	    int i2 = 0;
	    int [] ar = new int[fBitSet.cardinality()];
	    for (int i = 0; i < fBitSet.length(); i++)
	    {
	        if (get(i))
	        {
	            ar[i2++] = i;
	        }
	    }
	    return ar;
	}
	
	/**
	 * Returns the number of bits set to true in this BitArray.
	 * 
	 * @return the number of bits set to true in this BitArray.
	 */
	public int cardinality()
	{
	    return fBitSet.cardinality();
	}

	/**
	 *  Main provided for testing purposes.
	 *
	**/

	public static void main(String[] args)
	{
		String bitString;
		BitArray bitArray;
		Long longValue;

		longValue = new Long(3);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		long value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		longValue = new Long(2);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		longValue = new Long(1);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		longValue = new Long(0);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		longValue = new Long(-1);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		longValue = new Long(-2);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		longValue = new Long(-3);
		bitArray = new BitArray(longValue);
		bitString = bitArray.toString();
		System.out.println("Value: " + longValue + " = BitArray: " + bitString);

		value = bitArray.longValue();
		System.out.println("longValue() = " + value);

		System.out.println();

		bitString = "10011";
		bitArray = new BitArray(bitString);
		System.out.println(bitArray);
		BitArray grayCode = bitArray.toGrayCode();
		System.out.println("Gray Code: " + grayCode);
		System.out.println("Back to Binary Code: " +
			grayCode.toBinaryCodeFromGrayCode());

		for (int i = 0; i <= 32; i++)
		{
			Integer row = new Integer(i);
			BitArray rowBits = new BitArray(row);
			rowBits.trim();
			BitArray rowGrayCode = rowBits.toGrayCode();
			rowGrayCode.trim();

			System.out.println("Row: " + row + " Binary Code: " + rowBits +
				" Gray Code: " + rowGrayCode + " Number: " +
				rowGrayCode.intValue());
		}

		bitString = "0011";
		bitArray = new BitArray(bitString);
		System.out.println(bitArray);
		byte[] bytes = bitArray.getBytes();
		ByteArray.printByteArray(bytes);
		bitArray = new BitArray(bytes);
		System.out.println(bitArray);

		bitString = "10000000";
		bitArray = new BitArray(bitString);
		System.out.println(bitArray);
		bytes = bitArray.getBytes();
		ByteArray.printByteArray(bytes);
		bitArray = new BitArray(bytes);
		System.out.println(bitArray);

		bitString = "11100000";
		bitArray = new BitArray(bitString);
		System.out.println(bitArray);
		bytes = bitArray.getBytes();
		ByteArray.printByteArray(bytes);
		bitArray = new BitArray(bytes);
		System.out.println(bitArray);

		bitString = "10001000";
		bitArray = new BitArray(bitString);
		System.out.println(bitArray);
		bytes = bitArray.getBytes();
		ByteArray.printByteArray(bytes);
		bitArray = new BitArray(bytes);
		System.out.println(bitArray);

		bitString = "10001000";
		bitArray = new BitArray(bitString);
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 5): ");
			System.out.println(bitArray.unsignedNumericValue(0, 5));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 5): ");
			System.out.println(bitArray.signedNumericValue(0, 5));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 7): ");
			System.out.println(bitArray.unsignedNumericValue(0, 7));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 7): ");
			System.out.println(bitArray.signedNumericValue(0, 7));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 8): ");
			System.out.println(bitArray.unsignedNumericValue(0, 8));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 8): ");
			System.out.println(bitArray.signedNumericValue(0, 8));
		}
		catch (Exception e) {
			System.out.println("failed");
		}

		// 64 bits
		bitString = "1000100010001000100010001000100010001000100010001000100010001000";
//		bitString = "1000000000000000000000000000000000000000000000000000000000000000";
//		bitString = "1111111111111111111111111111111111111111111111111111111111111111";
		bitArray = new BitArray(bitString);
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 7): ");
			System.out.println(bitArray.unsignedNumericValue(0, 7));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 7): ");
			System.out.println(bitArray.signedNumericValue(0, 7));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 62): ");
			System.out.println(bitArray.unsignedNumericValue(0, 62));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 62): ");
			System.out.println(bitArray.signedNumericValue(0, 62));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 63): ");
			System.out.println(bitArray.unsignedNumericValue(0, 63));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 63): ");
			System.out.println(bitArray.signedNumericValue(0, 63));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": unsignedNumericValue(0, 64): ");
			System.out.println(bitArray.unsignedNumericValue(0, 64));
		}
		catch (Exception e) {
			System.out.println("failed");
		}
		try {
			System.out.print(bitString + ": signedNumericValue(0, 64): ");
			System.out.println(bitArray.signedNumericValue(0, 64));
		}
		catch (Exception e) {
			System.out.println("failed");
		}

//		try{Thread.sleep(100000);}catch(Exception e){}

		System.out.println();
		System.out.println("BitArray.compareTo(BitArray) test: <= 64 bits, equality");

		// 64 bits.
		String bitString1 = "1000100010001000100010001000100010001000100010001000100010001000";
		BitArray bitArray1 = new BitArray(bitString1);

		// Same string of 64 bits.
		String bitString2 = "1000100010001000100010001000100010001000100010001000100010001000";
		BitArray bitArray2 = new BitArray(bitString2);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 1: " + bitString1 + " to:\n" +
			"BitArray 2: " + bitString2);
		int result = bitArray1.compareTo(bitArray2);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 2: " + bitString2 + " to\n" +
			"BitArray 1: " + bitString1);
		result = bitArray2.compareTo(bitArray1);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("BitArray.compareTo(BitArray) test: <= 64 bits, inequality");

		// 64 bits.
		bitString1 = "1000100010001000100010001000100010001000100010001000100010001000";
		bitArray1 = new BitArray(bitString1);

		// Same string of 64 bits, but with 1 instead of 0 in LSB position.
		bitString2 = "1000100010001000100010001000100010001000100010001000100010001001";
		bitArray2 = new BitArray(bitString2);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 1: " + bitString1 + " to:\n" +
			"BitArray 2: " + bitString2);
		result = bitArray1.compareTo(bitArray2);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 2: " + bitString2 + " to\n" +
			"BitArray 1: " + bitString1);
		result = bitArray2.compareTo(bitArray1);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("BitArray.compareTo(BitArray) test: > 64 bits, equality");

		// 66 bits.
		bitString1 = "100010001000100010001000100010001000100010001000100010001000100000";
		bitArray1 = new BitArray(bitString1);

		// Same string of 66 bits.
		bitString2 = "100010001000100010001000100010001000100010001000100010001000100000";
		bitArray2 = new BitArray(bitString2);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 1: " + bitString1 + " to:\n" +
			"BitArray 2: " + bitString2);
		result = bitArray1.compareTo(bitArray2);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 2: " + bitString2 + " to\n" +
			"BitArray 1: " + bitString1);
		result = bitArray2.compareTo(bitArray1);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("BitArray.compareTo(BitArray) test: > 64 bits, inequality");

		// 66 bits.
		bitString1 = "100010001000100010001000100010001000100010001000100010001000100000";
		bitArray1 = new BitArray(bitString1);

		// Same string of 66 bits, but with 1 instead of 0 in LSB position.
		bitString2 = "100010001000100010001000100010001000100010001000100010001000100001";
		bitArray2 = new BitArray(bitString2);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 1: " + bitString1 + " to:\n" +
			"BitArray 2: " + bitString2);
		result = bitArray1.compareTo(bitArray2);
		System.out.println("Result: " + result);

		System.out.println();
		System.out.println("Compare\n" +
			"BitArray 2: " + bitString2 + " to\n" +
			"BitArray 1: " + bitString1);
		result = bitArray2.compareTo(bitArray1);
		System.out.println("Result: " + result);
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: BitArray.java,v $
//	Revision 1.10  2006/01/23 17:59:54  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.9  2005/02/14 22:00:16  chostetter_cvs
//	Mask operations can now shrink as well as increase the number of valid bits in the result
//	
//	Revision 1.8  2005/02/03 15:04:23  tames
//	Changed method equals(BitArray) to equals(Object) to correctly compare
//	an Object argument that happens to be an instance of BitArray. Fixed
//	compareTo(Object) to handle Object arguments that are BitArrays. This
//	fixes the condition where a collection could not determine if it
//	contained a given BitArray unless they were the same "==" Object.
//	
//	Revision 1.7  2005/01/31 14:50:13  smaher_cvs
//	Allowing spaces in String constructor (e.g., to allow "10010001 00100111" as an argument)
//	
//	Revision 1.6  2004/12/21 21:26:08  smaher_cvs
//	Added cardinality() and asIntArray()
//	
//	Revision 1.5  2004/12/01 19:25:33  smaher_cvs
//	Commented parameter in toString(int)
//	
//	Revision 1.4  2004/11/22 16:44:40  smaher_cvs
//	Fixed bug where copyInto would only set bits on the copy, but not clear bits.
//	
//	Revision 1.3  2004/11/19 21:39:08  smaher_cvs
//	Added ByteBuffer constructore and toString(int)
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
//	Revision 1.1.2.2  2004/03/24 20:31:33  chostetter_cvs
//	New package structure baseline
//	
//	Revision 1.3  2003/06/12 15:23:38  tames_cvs
//	Changed the order that primitive byte arrays are created and interpreted.
//	
//
//	04/16/99	C. Hostetter/588
//
//		Initial version.
//
//	04/20/99	C. Hostetter/588
//		Clarified, documented relationship of bit index with bit order and bit
//		String ordering in relevant operations.
//
//	04/21/99	C. Hostetter/588
//		Now extends Number.
//		Added append(String), prepend(String) methods.
//		Added MAX_NUM_BITS constants.
//		Added BitArray(String, int) constructor.
//		Added append(Number), append(Object), prepend(Number), prepend(Object)
//			methods.
//		Revised trim() method.
//		Added trim(int), pad(int) methods.
//
//	04/22/99	C. Hostetter/588
//		Removed append(Object), prepend(Object) methods.
//
//	04/26/99	C. Hostetter/588
//		Fixed << problems.
//
//	04/29/99	C. Hostetter/588
//		Added toPrimitiveByteArray() method.
//	  Added BitArray(byte[]) constructor. This could be made more efficient.
//
//	05/12/99	C. Hostetter/588
//		Fixed bounds check error in parseNumber(Number) method.
//
//	05/13/99	C. Hostetter/588
//		Added append(Number, int) method, fixed problem with BitArray(byte[])
//	  constructor.
//
//	07/08/99	C. Hostetter/588
//		Now handles negative values.
//
//		Now handles negative zero. Also changed parseNumber(Number) to handle
//	  Float and Double (via the Float.floatToIntBits() and
//	  Double.doubleToLongBits() methods, respectively), and changed
//	  floatValue() and doubleValue() to utilize Float.intBitsToFloat and
//	  Double.longBitsToDouble, respectively.
//
//	07/09/99	C. Hostetter/588
//		Reordered instanceof tests in parseNumber to improve efficiency.
//
//		Fixed reordering goof. D'oh!
//
//	07/19/99	C. Hostetter/588
//		Changed signedNumericValue() method to use Long.parseLong() to
//	  correctly handle negative zeros.
//
//	07/23/99	C. Hostetter/588
//		Changed parseNumber(Number) to use Long.toBinaryString() to correctly
//	  parse negative numeric types (which, in Java, are signed twos-
//	  complement).
//
//	09/03/99	C. Hostetter/588
//		Added getNumberOfSignificantBits(), getNumberOfSetBits() methods.
//
//	10/07/99	S. Clark
//		Fixed shiftRight()/shiftLower().
//		Clarified incorrect documentation in signedNumericValue() re:
//		treatment of sign bit; added unsignedNumericValue().
//
//	10/27/99	C. Hostetter/588
//		Added toGrayCode(), toBinaryCodeFromGrayCode() methods.
//
//	11/08/99	C. Hostetter/588
//		Added static int toGrayCode(int) method.
//