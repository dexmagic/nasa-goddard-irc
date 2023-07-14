//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.data;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * An IntegerDataBuffer is a linear, finite sequence of <code>int</code> 
 * primitive elements.  
 *
 * <p> Methods in this class that do not otherwise have a value to return are
 * specified to return the buffer upon which they are invoked.  This allows
 * method invocations to be chained.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/02/08 20:36:28 $
 * @author 	Troy Ames
 */
final class IntegerDataBuffer extends AbstractDataBuffer
{
	/**
	 * The length of each element in this DataBuffer in bytes.
	 */
	public static final int BYTES_PER_ELEMENT = 4;
	
	private int [] fNativeArray = null;

	/**
	 *	Constructs an IntegerDataBuffer having the given array of data and as 
	 *  described by the given DataBufferDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 *  @param data An array of data
	 *  @param capacity The capacity of this DataBuffer
	 */
	public IntegerDataBuffer(DataBufferDescriptor descriptor, int capacity)
	{
		this(descriptor, new int[capacity], 0, capacity);
	}

	/**
	 * Constructs an IntegerDataBuffer having the given array of data with the 
	 * given range and as described by the given DataBufferDescriptor.
	 * 
	 * @param descriptor A DataBufferDescriptor
	 * @param data An array of data
	 * @param offset The offset in the array for the first element of this
	 *            DataBuffer
	 * @param capacity The length of this DataBuffer
	 */
	IntegerDataBuffer(
		DataBufferDescriptor descriptor, 
		int [] array, 
		int offset, int capacity)
	{
		super(descriptor, array, offset, capacity);
		fNativeArray = array;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#copy()
	 */
	public DataBuffer copy()
	{
		int sourceCapacity = fBufferLength;

		int[] arrayCopy = new int[sourceCapacity];

		if (fArrayOffset + sourceCapacity > fArrayLength)
		{
			int tailLength = fArrayLength - fArrayOffset;
			System.arraycopy(fNativeArray, fArrayOffset, arrayCopy, 0, tailLength);
			System.arraycopy(fNativeArray, 0, arrayCopy, tailLength,
				arrayCopy.length - tailLength);
		}
		else
		{
			System.arraycopy(fNativeArray, fArrayOffset, arrayCopy, 0,
				arrayCopy.length);
		}

		return (new IntegerDataBuffer(
			getDescriptor(), arrayCopy, 0, arrayCopy.length));
	}
	

	/**
	 * Clears the values in this DataBuffer to 0. 
	 * If this DataBuffer is read-only, this call has no effect, and throws an 
	 * UnsupportedOperationException
	 * 
	 * @return This DataBuffer
	 * @throws UnsupportedOperationException if this is a read-only DataBuffer
	 */
	
	public DataBuffer clear()
		throws UnsupportedOperationException
	{
		if (! isReadOnly())
		{
			int size = getSize();
			
			for (int i = 0; i < size; i++)
			{
				fNativeArray[resolveIndex(i)] = 0;
			}
		}
		else
		{
			String message = getFullyQualifiedName() + " is read-only";
			
			throw (new UnsupportedOperationException(message));
		}
		
		return (this);
	}
	

	/**
	 * Initializes all the values in this DataBuffer to the given value, which must 
	 * be a Number. If this DataBuffer is read-only, this call has no effect, and 
	 * throws an UnsupportedOperationException
	 * 
	 * @param value The value with which to initialize all the values of this 
	 * 		DataBuffer
	 * @return This DataBuffer
	 * @throws UnsupportedOperationException if this is a read-only DataBuffer
	 * @throws IllegalArgumentException if the given value is not a Number
	 */
	
	public DataBuffer initialize(Object value)
		throws UnsupportedOperationException, IllegalArgumentException
	{
		if (! isReadOnly())
		{
			if ((value != null) && (value instanceof Number))
			{
				int intValue = ((Number) value).intValue();
				
				int size = getSize();
				
				for (int i = 0; i < size; i++)
				{
					fNativeArray[resolveIndex(i)] = intValue;
				}
			}
			else
			{
				String message = "Value " + value + " is not a Number";
				
				throw (new IllegalArgumentException(message));
			}
		}
		else
		{
			String message = getFullyQualifiedName() + " is read-only";
			
			throw (new UnsupportedOperationException(message));
		}
		
		return (this);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsByte(int)
	 */
	public byte getAsByte(int index)
	{
		return ((byte) fNativeArray[resolveIndex(index)]);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsChar(int)
	 */
	public char getAsChar(int index)
	{
		return ((char) fNativeArray[resolveIndex(index)]);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsShort(int)
	 */
	public short getAsShort(int index)
	{
		return ((short) fNativeArray[resolveIndex(index)]);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsInt(int)
	 */
	public int getAsInt(int index)
	{
		return ((int) fNativeArray[resolveIndex(index)]);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsLong(int)
	 */
	public long getAsLong(int index)
	{
		return ((long) fNativeArray[resolveIndex(index)]);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsFloat(int)
	 */
	public float getAsFloat(int index)
	{
		return ((float) fNativeArray[resolveIndex(index)]);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsDouble(int)
	 */
	public double getAsDouble(int index)
	{
		return ((double) fNativeArray[resolveIndex(index)]);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsObject(int)
	 */
	public Object getAsObject(int index)
	{
		return (new Integer(fNativeArray[resolveIndex(index)]));
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsByteBuffer()
	 */
	public ByteBuffer getAsByteBuffer()
	{
		int size = getSize();
		int sizeInBytes = size * BYTES_PER_ELEMENT;
		ByteBuffer buffer = ByteBuffer.allocate(sizeInBytes);
		
		for (int i = 0; i < size; i++)
		{
			buffer.putInt(getAsInt(i));
		}
		
		buffer.flip();
		
		return (buffer);	
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsByteArray()
	 */
	public byte[] getAsByteArray()
	{
		int size = getSize();
		byte [] arrayCopy = new byte[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = (byte) fNativeArray[resolveIndex(i)];
		}
		
		return arrayCopy;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsCharArray()
	 */
	public char[] getAsCharArray()
	{
		int size = getSize();
		char [] arrayCopy = new char[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = (char) fNativeArray[resolveIndex(i)];
		}
		
		return arrayCopy;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsShortArray()
	 */
	public short[] getAsShortArray()
	{
		int size = getSize();
		short [] arrayCopy = new short[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = (short) fNativeArray[resolveIndex(i)];
		}
		
		return arrayCopy;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsIntArray()
	 */
	public int[] getAsIntArray()
	{
		int sourceCapacity = fBufferLength;

		int[] arrayCopy = new int[sourceCapacity];

		if (fArrayOffset + sourceCapacity > fArrayLength)
		{
			int tailLength = fArrayLength - fArrayOffset;
			System.arraycopy(fNativeArray, fArrayOffset, arrayCopy, 0, tailLength);
			System.arraycopy(fNativeArray, 0, arrayCopy, tailLength,
				arrayCopy.length - tailLength);
		}
		else
		{
			System.arraycopy(fNativeArray, fArrayOffset, arrayCopy, 0,
				arrayCopy.length);
		}
		
		return arrayCopy;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsLongArray()
	 */
	public long[] getAsLongArray()
	{
		int size = getSize();
		long [] arrayCopy = new long[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = (long) fNativeArray[resolveIndex(i)];
		}
		
		return arrayCopy;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsFloatArray()
	 */
	public float[] getAsFloatArray()
	{
		int size = getSize();
		float [] arrayCopy = new float[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = (float) fNativeArray[resolveIndex(i)];
		}
		
		return arrayCopy;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsDoubleArray()
	 */
	public double[] getAsDoubleArray()
	{
		int size = getSize();
		double [] arrayCopy = new double[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = (double) fNativeArray[resolveIndex(i)];
		}
		
		return arrayCopy;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsObjectArray()
	 */
	public Object[] getAsObjectArray()
	{
		int size = getSize();
		Object [] arrayCopy = new Object[size];		
		
		for (int i= 0; i < size; i++)
		{
			arrayCopy[i] = new Integer(fNativeArray[resolveIndex(i)]);
		}
		
		return arrayCopy;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, byte)
	 */
	public DataBuffer put(int index, byte value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, char)
	 */
	public DataBuffer put(int index, char value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, short)
	 */
	public DataBuffer put(int index, short value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, int)
     */
    public DataBuffer put(int index, int value)
    {
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
    }

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, long)
	 */
	public DataBuffer put(int index, long value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, float)
	 */
	public DataBuffer put(int index, float value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, double)
     */
    public DataBuffer put(int index, double value)
    {
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = (int) value;
    	
    	return this;
    }
        
    /**
     * Absolute <i>put</i> method. The supported Object types for value are
     * <code>String</code> and subclasses of <code>Number</code>. The 
     * <code>{@link Integer#decode(java.lang.String) Integer.decode(String)}</code>
     * method is called for values of type <code>String</code> to convert to an
     * <code>int</code>.
     * 
     * <p> Writes the given value into this buffer at the given
     * index. </p>
     *
     * @param  index The index at which the value will be written
     * @param  value The value to be written
     *
     * @return  This DataBuffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's size
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     * @throws  IllegalArgumentException
     *          If the value cannot be converted into an <code>int</code>.
     * 
     * @see Number
     * @see Integer
     */
	public DataBuffer put(int index, Object value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		if (value instanceof Number)
		{
			fNativeArray[resolveIndex(index)] = ((Number) value).intValue();
		}
		else if (value instanceof String)
		{
			try
			{
				fNativeArray[resolveIndex(index)] = 
					Integer.decode((String) value).intValue();
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new IllegalArgumentException(
				"value paramerter must be of type Number for this DataBuffer");
		}
    	
    	return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, int[], int, int)
     */
    public DataBuffer put(int index, int[] source, int sourceIndex, int length)
	{
    	// TODO check arguments
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}
		
		if (length + index > getSize())
		{
			throw new BufferOverflowException();
		}
		
		int relativeOffset = resolveIndex(index);
		int limit = relativeOffset + length;
		
		if (limit > fArrayLength)
		{
			int tailLength = limit - fArrayLength;
			int headLength = length - tailLength;
			
			// Handle part that fits at the end of native array.
			System.arraycopy(
					source, sourceIndex, 
					fNativeArray, relativeOffset, headLength);
			
			// Handle part that wraps around to beginning of native array.
			System.arraycopy(
					source, sourceIndex + headLength, 
					fNativeArray, 0, tailLength);
		}
		else
		{
			System.arraycopy(
					source, sourceIndex, 
					fNativeArray, relativeOffset, length);
		}

		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, gov.nasa.gsfc.irc.data.DataBuffer, int, int)
     */
    public DataBuffer put(int index, DataBuffer source, int sourceIndex, int length)
	{
		if (source == this)
		{
			throw new IllegalArgumentException();
		}
		
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}
		
		if (length + index > getSize())
		{
			throw new BufferOverflowException();
		}
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source.getAsInt(sourceIndex + i));
		}
		
		return this;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#downsample(int)
	 */
	public DataBuffer downsample(int downsamplingRate)
	{
		DataBuffer result;
		
		if (downsamplingRate > 1)
		{
			int dataBufferSize = getSize();
			
			if (downsamplingRate <= dataBufferSize)
			{
				int numSubsamples = dataBufferSize / downsamplingRate;
				int [] sampledArray = new int[numSubsamples];
				int element = 0;
				
				for (int i = 0; i < numSubsamples; i++, element += downsamplingRate)
				{
					sampledArray[i] = fNativeArray[resolveIndex(element)];
				}

				// create downsampled DataBuffer
				result = new IntegerDataBuffer(
					getDescriptor(), sampledArray, 0, numSubsamples);
			}
			else
			{
				String message = "The given downsampling rate (" + 
					downsamplingRate + 
					" exceeds the capacity of this DataBuffer:\n" + this;
				
				throw (new IllegalArgumentException(message));
			}
		}
		else
		{
			result = this.copy();
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#dataToString(int, int)
	 */
	public String dataToString(int startPosition, int length)
	{
		//TODO check parameters
		StringBuffer stringRep = new StringBuffer();
		int size = startPosition + length;
		
		for (int i = startPosition; i < size; i++)
		{
			stringRep.append(", " + fNativeArray[resolveIndex(i)]);
		}

		return (stringRep.toString());
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#duplicate()
	 */
	public DataBuffer duplicate()
	{
		return (new IntegerDataBuffer(
			getDescriptor(), fNativeArray, fArrayOffset, fBufferLength));
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: IntegerDataBuffer.java,v $
//  Revision 1.8  2006/02/08 20:36:28  chostetter_cvs
//  Added initialize(Object value) method
//
//  Revision 1.7  2006/02/07 15:03:45  chostetter_cvs
//  Added clear() and initialize(value) methods
//
//  Revision 1.6  2005/07/21 15:27:12  tames_cvs
//  Removed the getSizeAsByte method since the definition was
//  somewhat ambiguous and not valid for ObjectDataBuffers.
//
//  Revision 1.5  2005/07/20 22:07:26  tames_cvs
//  Added bulk put methods of the form: put(index, type[], start, length)
//
//  Revision 1.4  2005/07/15 22:03:48  tames
//  Added getSizeInBytes() method to support archivers and other users that
//  that need to know the physical size of the underlying data.
//
//  Revision 1.3  2005/07/15 21:25:41  tames
//  Added buffer type specific optimization for the getAs_Type_Array() method.
//
//  Revision 1.2  2005/07/15 19:22:45  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/07/14 21:48:50  tames
//  Initial version. Needed for data model refactoring.
//
//