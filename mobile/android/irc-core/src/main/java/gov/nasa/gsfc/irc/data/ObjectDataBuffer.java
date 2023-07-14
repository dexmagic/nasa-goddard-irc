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
 * An ObjectDataBuffer is a linear, finite sequence of <code>Object</code> 
 * elements. Although this class can be used to hold Object representations of 
 * primitive types it is much less efficient for this purpose then the 
 * primitive DataBuffer types.
 *
 * <p> Methods in this class that do not otherwise have a value to return are
 * specified to return the buffer upon which they are invoked.  This allows
 * method invocations to be chained.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/02/07 15:03:46 $
 * @author 	Troy Ames
 */
class ObjectDataBuffer extends AbstractDataBuffer
{
	/**
	 * The length of each element in this DataBuffer in bytes.
	 */
	public static final int BYTES_PER_ELEMENT = 4;
	
	private Object [] fNativeArray = null;

	/**
	 *	Constructs an ObjectDataBuffer having the given array of data and as 
	 *  described by the given DataBufferDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 *  @param data An array of data
	 *  @param capacity The capacity of this DataBuffer
	 */
	public ObjectDataBuffer(DataBufferDescriptor descriptor, int capacity)
	{
		this(descriptor, new Object[capacity], 0, capacity);
	}

	/**
	 * Constructs an ObjectDataBuffer having the given array of data with the 
	 * given range and as described by the given DataBufferDescriptor.
	 * 
	 * @param descriptor A DataBufferDescriptor
	 * @param data An array of data
	 * @param offset The offset in the array for the first element of this
	 *            DataBuffer
	 * @param capacity The length of this DataBuffer
	 */
	ObjectDataBuffer(
		DataBufferDescriptor descriptor, 
		Object [] array, 
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

		Object[] arrayCopy = new Object[sourceCapacity];

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

		return (new ObjectDataBuffer(
			getDescriptor(), arrayCopy, 0, arrayCopy.length));
	}
	

	/**
	 * Clears the values in this DataBuffer to null. 
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
				fNativeArray[resolveIndex(i)] = null;
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
	 * Initializes all the values in this DataBuffer to the given value. 
	 * If this DataBuffer is read-only, this call has no effect, and throws an 
	 * UnsupportedOperationException
	 * 
	 * @param value The value with which to initialize all the values of this 
	 * 		DataBuffer
	 * @return This DataBuffer
	 * @throws UnsupportedOperationException if this is a read-only DataBuffer
	 */
	
	public DataBuffer initialize(Object value)
		throws UnsupportedOperationException
	{
		if (! isReadOnly())
		{
			int size = getSize();
			
			for (int i = 0; i < size; i++)
			{
				fNativeArray[resolveIndex(i)] = value;
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
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>byte</code>. The element at the specified location
	 * must be either a subclass of <code>Number</code> or a valid 
	 * <code>String</code> representation of a byte, otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a byte
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>byte</code>
	 */
	public byte getAsByte(int index)
	{
		byte value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Number)
		{
			value = ((Number) valueObject).byteValue();
		}
		else if (valueObject instanceof String)
		{
			try
			{
				value = Byte.decode((String) valueObject).byteValue();
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedOperationException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}
	
    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>char</code>. The element at the specified location
	 * must be either a <code>Character</code> or a <code>String</code>, 
	 * otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a char
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>char</code>
	 */
	public char getAsChar(int index)
	{
		char value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Character)
		{
			value = ((Character) valueObject).charValue();
		}
		else if (valueObject instanceof String)
		{
			value = ((String) valueObject).charAt(0);
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}
	
    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>short</code>. The element at the specified location
	 * must be either a subclass of <code>Number</code> or a valid 
	 * <code>String</code> representation of a short, otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a short
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>short</code>
	 */
	public short getAsShort(int index)
	{
		short value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Number)
		{
			value = ((Number) valueObject).shortValue();
		}
		else if (valueObject instanceof String)
		{
			try
			{
				value = Short.decode((String) valueObject).shortValue();
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedOperationException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}

    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>int</code>. The element at the specified location
	 * must be either a subclass of <code>Number</code> or a valid 
	 * <code>String</code> representation of a int, otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a int
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>int</code>
	 */
	public int getAsInt(int index)
	{
		int value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Number)
		{
			value = ((Number) valueObject).intValue();
		}
		else if (valueObject instanceof String)
		{
			try
			{
				value = Integer.decode((String) valueObject).intValue();
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedOperationException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}
	
    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>long</code>. The element at the specified location
	 * must be either a subclass of <code>Number</code> or a valid 
	 * <code>String</code> representation of a long, otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a long
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>long</code>
	 */
	public long getAsLong(int index)
	{
		long value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Number)
		{
			value = ((Number) valueObject).longValue();
		}
		else if (valueObject instanceof String)
		{
			try
			{
				value = Long.decode((String) valueObject).longValue();
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedOperationException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}
	
    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>float</code>. The element at the specified location
	 * must be either a subclass of <code>Number</code> or a valid 
	 * <code>String</code> representation of a float, otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a float
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>float</code>
	 */
	public float getAsFloat(int index)
	{
		float value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Number)
		{
			value = ((Number) valueObject).floatValue();
		}
		else if (valueObject instanceof String)
		{
			try
			{
				value = Float.parseFloat((String) valueObject);
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedOperationException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}
	
    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * converted to a <code>double</code>. The element at the specified location
	 * must be either a subclass of <code>Number</code> or a valid 
	 * <code>String</code> representation of a double, otherwise this method will
	 * throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index converted to a double
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 * @throws UnsupportedOperationException if the value cannot be converted
	 *             into a <code>double</code>
	 */
	public double getAsDouble(int index)
	{
		double value;
		Object valueObject = fNativeArray[resolveIndex(index)];
		
		if (valueObject instanceof Number)
		{
			value = ((Number) valueObject).doubleValue();
		}
		else if (valueObject instanceof String)
		{
			try
			{
				value = Double.parseDouble((String) valueObject);
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedOperationException(e.getLocalizedMessage());
			}
		}
		else
		{
			throw new UnsupportedOperationException(
				"Buffer element cannot be converted to requested type");
		}
		
 		return value;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsObject(int)
	 */
	public Object getAsObject(int index)
	{
		return fNativeArray[resolveIndex(index)];
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsByteBuffer()
	 */
	public ByteBuffer getAsByteBuffer()
	{
		throw new UnsupportedOperationException(
		 	"Buffer cannot be converted to a ByteBuffer");
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
			arrayCopy[i] = getAsByte(i);
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
			arrayCopy[i] = getAsChar(i);
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
			arrayCopy[i] = getAsShort(i);
		}
		
		return arrayCopy;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsIntArray()
	 */
	public int[] getAsIntArray()
	{
		int size = getSize();
		int [] arrayCopy = new int[size];		
		
		for (int i = 0; i < size; i++)
		{
			arrayCopy[i] = getAsInt(i);
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
			arrayCopy[i] = getAsLong(i);
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
			arrayCopy[i] = getAsFloat(i);
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
			arrayCopy[i] = getAsDouble(i);
		}
		
		return arrayCopy;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getAsObjectArray()
	 */
	public Object[] getAsObjectArray()
	{
		int sourceCapacity = fBufferLength;

		Object[] arrayCopy = new Object[sourceCapacity];

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
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, byte)
	 */
	public DataBuffer put(int index, byte value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = new Byte(value);
    	
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

		fNativeArray[resolveIndex(index)] = new Character(value);
    	
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

		fNativeArray[resolveIndex(index)] = new Short(value);
    	
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

		fNativeArray[resolveIndex(index)] = new Integer(value);
    	
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

		fNativeArray[resolveIndex(index)] = new Long(value);
    	
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

		fNativeArray[resolveIndex(index)] = new Float(value);
    	
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

		fNativeArray[resolveIndex(index)] = new Double(value);
    	
    	return this;
    }
        
    /**
     * Absolute <i>put</i> method.
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
     */
	public DataBuffer put(int index, Object value)
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		fNativeArray[resolveIndex(index)] = value;
    	
    	return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, Object[], int, int)
     */
    public DataBuffer put(int index, Object[] source, int sourceIndex, int length)
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
			put(index + i, source.getAsObject(sourceIndex + i));
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
				Object [] sampledArray = new Object[numSubsamples];
				int element = 0;
				
				for (int i = 0; i < numSubsamples; i++, element += downsamplingRate)
				{
					sampledArray[i] = fNativeArray[resolveIndex(element)];
				}

				// create downsampled DataBuffer
				result = new ObjectDataBuffer(
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
			stringRep.append(", " + fNativeArray[resolveIndex(i)].toString());
		}

		return (stringRep.toString());
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#duplicate()
	 */
	public DataBuffer duplicate()
	{
		return (new ObjectDataBuffer(
			getDescriptor(), fNativeArray, fArrayOffset, fBufferLength));
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ObjectDataBuffer.java,v $
//  Revision 1.7  2006/02/07 15:03:46  chostetter_cvs
//  Added clear() and initialize(value) methods
//
//  Revision 1.6  2005/07/21 15:27:11  tames_cvs
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
//  Revision 1.2  2005/07/15 19:22:46  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/07/14 21:48:50  tames
//  Initial version. Needed for data model refactoring.
//
//