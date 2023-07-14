//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/DataBuffer.java,v 1.62 2006/02/08 20:36:28 chostetter_cvs Exp $
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

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * A DataBuffer is a linear, finite sequence of elements of a specific
 * type.  
 *
 * <p> There is one subclass of this class for each non-boolean primitive type
 * and one for the Object type.
 *
 * <p> Methods in this class that do not otherwise have a value to return are
 * specified to return the buffer upon which they are invoked.  This allows
 * method invocations to be chained.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/02/08 20:36:28 $
 * @author Carl F. Hostetter
 * @author Troy J. Ames
 */

public interface DataBuffer extends HasName, Serializable
{
	/**
	 * Returns the array that backs this buffer <i>(optional operation)</i>. 
	 * The returned array will be of the same type as this DataBuffer.
	 *
	 * <p> Modifications to this buffer's content will cause the returned
	 * array's content to be modified, and vice versa.
	 *
	 * <p>Invoke the {@link #hasArray hasArray} method before invoking this
	 * method in order to ensure that this buffer has an accessible backing
	 * array.</p>
	 *
	 * @return The array that backs this buffer
	 * @throws ReadOnlyBufferException if this buffer is backed by an array 
	 * 		but is read-only
	 * @throws UnsupportedOperationException if this buffer is not backed by an 
	 * 		accessible array
	 */
	public Object array();	

	/**
	 * Returns true if this buffer is backed by an accessible array, false 
	 * otherwise.
	 *
	 * <p>If this method returns <tt>true</tt> then the {@link #array() array}
	 * and {@link #arrayOffset() arrayOffset} methods may safely be invoked.</p>
	 *
	 * @return True if this buffer is backed by an accessible array, false 
	 * 		otherwise
	 */
	public boolean hasArray();

	/**
	 * Returns the offset within this buffer's backing array of the first
	 * element of the buffer <i>(optional operation)</i>.
	 *
	 * <p> If this buffer is backed by an array then buffer position <i>p</i>
	 * corresponds to array index <i>p</i><tt>arrayOffset()</tt>.
	 *
	 * <p>Invoke the {@link #hasArray hasArray} method before invoking this
	 * method in order to ensure that this buffer has an accessible backing
	 * array.</p>
	 *
	 * @return The offset within this DataBuffer's backing array of the first 
	 * 		element of the buffer
	 * @throws ReadOnlyBufferException if this buffer is read-only
	 * @throws UnsupportedOperationException if this buffer is not backed by an 
	 * 		accessible array
	 */
	public int arrayOffset();
	
	/**
	 * Returns true if this DataBuffer is read-only, false otherwise.
	 * 
	 * @return True if this DataBuffer is read-only, false otherwise
	 */
	public boolean isReadOnly();

	/**
	 * Returns a new DataBuffer having the same characteristics as this
	 * DataBuffer. The new DataBuffer will be read-only if, and
	 * only if, this buffer is read-only.
	 * 
	 * <p>
	 * The content of the new DataBuffer will be that of this DataBuffer.
	 * Changes to this DataBuffer's content will be visible in the new
	 * DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A new DataBuffer having the same characteristics as this
	 *         DataBuffer
	 */
	public DataBuffer duplicate();
	
	/**
	 * Returns the DataBufferDescriptor that describes this DataBuffer.
	 * 
	 * @return The DataBufferDescriptor that describes this DataBuffer
	 */
	public DataBufferDescriptor getDescriptor();

	/**
	 * Returns true if this DataBuffer is a Pixel, false otherwise.
	 * 
	 * @return True if this DataBuffer is a Pixel, false otherwise
	 */
	public boolean isPixel();

	/**
	 * Returns the Pixel of this DataBuffer (if any).
	 * 
	 * @return The Pixel of this DataBuffer (if any)
	 */
	public Pixel getPixel();

	/**
	 * Returns the fully-qualified name of this DataBuffer, i.e., including the
	 * Pixel tag if it is has a Pixel.
	 * 
	 * @return The fully-qualified name of this DataBuffer, i.e., including the
	 *         Pixel tag if it is has a Pixel
	 */
	public String getFullyQualifiedName();

	/**
	 * Returns the data type of the content of this DataBuffer.
	 * 
	 * @return The data type of this DataBuffer
	 */
	public Class getDataBufferType();

	/**
	 * Returns the Unit of this DataBuffer.
	 * 
	 * @return The Unit of this DataBuffer
	 */
	public Unit getUnit();

	/**
	 * Returns true if the data of this DataBuffer is real-valued (i.e. float or
	 * double), false otherwise.
	 * 
	 * @return True if the data of this DataBuffer is real-valued (i.e. float or
	 *         double), false otherwise
	 */
	public boolean isRealValued();

	/**
	 * Clears the values in this DataBuffer to an appropriate value for its type. 
	 * If this DataBuffer is read-only, this call has no effect, and throws an 
	 * UnsupportedOperationException
	 * 
	 * @return This DataBuffer
	 * @throws UnsupportedOperationException if this is a read-only DataBuffer
	 */
	public DataBuffer clear()
		throws UnsupportedOperationException;

	/**
	 * Initializes all the values in this DataBuffer to the given value, which must 
	 * be appropriate for its type. If this DataBuffer is read-only, this call has 
	 * no effect, and throws an UnsupportedOperationException
	 * 
	 * @param value The value with which to initialize all the values of this 
	 * 		DataBuffer
	 * @return This DataBuffer
	 * @throws UnsupportedOperationException if this is a read-only DataBuffer
	 * @throws IllegalArgumentException if the given value is not of an appropriate 
	 * 		type
	 */
	
	public DataBuffer initialize(Object value)
		throws UnsupportedOperationException, IllegalArgumentException;
	
	/**
	 * Returns a new DataBuffer having the same characteristics as this
	 * DataBuffer but containing a copy of the data of this DataBuffer. The
	 * resulting new DataBuffer will contain all the data of this DataBuffer.
	 * 
	 * @return A new DataBuffer having the same characteristics as this
	 *         DataBuffer but containing a copy of the data of this DataBuffer
	 */
	public DataBuffer copy();

	/**
	 * Makes this DataBuffer read-only.
	 * 
	 * @return This DataBuffer
	 */
	public DataBuffer makeReadOnly();

	/**
	 * Returns the size of this DataBuffer in number of samples.
	 * 
	 * @return The size of this DataBuffer in number of samples
	 */
	public int getSize();

	/**
	 * Returns a new DataBuffer wrapping a slice of the underlying data of
	 * this DataBuffer, spanning the given range.
	 * 
	 * @param offset starting position of the desired slice (inclusive) relative
	 * 		to this DataBuffer.
	 * @param length the length of the DataBuffer slice.
	 * @return A new DataBuffer wrapping a slice of the underlying data of
	 *         this DataBuffer, spanning the given range
	 * @throws IndexOutOfBoundsException if the given range is negative or
	 *             are larger than this DataBuffer
	 */
	public DataBuffer slice(int offset, int length);

    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * DataBuffer into this buffer.  If there are more elements in the
     * source buffer than in this buffer, that is, if
     * <tt>src.size()</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size() - index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <i>n</i>&nbsp;=&nbsp;<tt>src.size()</tt> elements from the given
     * DataBuffer into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source buffer from which elements are to be read;
     *         must not be this buffer
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the remaining elements in the source buffer
     *
     * @throws  IllegalArgumentException
     *          If the source buffer is this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, DataBuffer source);

    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * DataBuffer into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * DataBuffer into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source buffer from which elements are to be read;
     *         must not be this buffer
     * @param  sourceIndex The index in the source buffer to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the remaining elements in the source buffer
     *
     * @throws  IllegalArgumentException
     *          If the source buffer is this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, DataBuffer source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, byte[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, char[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, short[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, int[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, long[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, float[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, double[] source, int sourceIndex, int length);
    
    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * array into this buffer.  If the specified <tt>length</tt> is 
     * larger than this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * array into this buffer, starting at <tt>index</tt>.
     * 
     * @param  index The index into this buffer to start copying the source data
     * @param  source
     *         The source array from which elements are to be read;
     *         must not be null
     * @param  sourceIndex The index in the source array to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the specified elements in the source array
     *
     * @throws  IllegalArgumentException
     *          If the sourceIndex or length are not valid for the source array.
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public DataBuffer put(int index, Object[] source, int sourceIndex, int length);
    
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
    public DataBuffer put(int index, byte value);

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
    public DataBuffer put(int index, char value);

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
    public DataBuffer put(int index, short value);

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
    public DataBuffer put(int index, int value);

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
    public DataBuffer put(int index, long value);

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
    public DataBuffer put(int index, float value);

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
    public DataBuffer put(int index, double value);

    /**
     * Absolute <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
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
     *          If the value cannot be converted into the type of this buffer.
     */
    public DataBuffer put(int index, Object value);

    /**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * cast to a <code>byte</code>. This may involve rounding or truncation.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to a byte
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public byte getAsByte(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given absolute
	 * index, cast to a <code>char</code>. This may involve rounding or 
	 * truncation.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to a <code>char</code>
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public char getAsChar(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given absolute
	 * index, cast to a <code>double</code>.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to a <code>double</code>
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public double getAsDouble(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given absolute
	 * index, cast to a <code>float</code>. This may result in a
	 * loss of precision.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to a <code>float</code>
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public float getAsFloat(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given absolute
	 * index, cast to an <code>int</code>. This may involve rounding or truncation.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to an <code>int</code>
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's length
	 */
	public int getAsInt(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given absolute
	 * index, cast to a <code>long</code>. This may involve rounding or 
	 * truncation.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to a <code>long</code>
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public long getAsLong(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * cast to a <code>short</code>. This may involve rounding or truncation.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index cast to a <code>short</code>
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public short getAsShort(int index);

	/**
	 * Returns the value of the element of this DataBuffer at the given index,
	 * as an Object. If the type of the buffer is a primitive this method will
	 * return the corresponding wrapper Object.
	 * 
	 * @param index The index of the element to get
	 * @return The value of the element at the given index as an Object
	 * @throws IndexOutOfBoundsException if the given index is negative or is
	 *             not less than this DataBuffer's limit minus one
	 */
	public Object getAsObject(int index);

	/**
	 * Creates a copy of this DataBuffer as a NIO ByteBuffer. 
	 *
	 * <p>Changes to this DataBuffer's content will NOT be visible in the new
	 * ByteBuffer, and vice versa.</p>
	 *
	 * @return This DataBuffer as a ByteBuffer
	 * @see ByteBuffer
	 */	
	public ByteBuffer getAsByteBuffer();
	
	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * bytes.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         bytes
	 */
	public byte[] getAsByteArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * chars.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         chars
	 */
	public char[] getAsCharArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * doubles.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         doubles
	 */
	public double[] getAsDoubleArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * floats.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         floats
	 */
	public float[] getAsFloatArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * ints.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n ints
	 */
	public int[] getAsIntArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * longs.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         longs
	 */
	public long[] getAsLongArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * shorts.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         shorts
	 */
	public short[] getAsShortArray();

	/**
	 * Returns a copy of the n elements of this DataBuffer as an array of n
	 * Objects.
	 * <p>
	 * Since the result is a copy, changes in the result will not be reflected
	 * in this DataBuffer, and vice versa.
	 * </p>
	 * 
	 * @return A copy of the n elements of this DataBuffer as an array of n
	 *         chars
	 */
	public Object[] getAsObjectArray();

	/**
	 * Causes this DataBuffer to update its set of data statistics. This would
	 * only be called if a writeable DataBuffer is updated.
	 */
	public void recalculateStatistics();

	/**
	 * Returns the number of samples used to calculate the statistics for this
	 * DataBuffer. This will be less than the size of this DataBuffer in
	 * the event that any of the samples have invalid values (NaN).
	 * 
	 * @return The number of samples used to calculate the statistics for this
	 *         DataBuffer
	 */
	public int getNumSamplesInStatistics();

	/**
	 * Returns the minimum value found in this DataBuffer.
	 * 
	 * @return The minimum value found in this DataBuffer
	 */
	public double getMinValue();

	/**
	 * Returns the index of the minimum value found in this DataBuffer. If more
	 * than one element has the minimum value, the index of the first of these
	 * is returned.
	 * 
	 * @return The index of the minimum value found in this DataBuffer
	 */
	public int getMinValueIndex();

	/**
	 * Returns the maximum value found in this DataBuffer.
	 * 
	 * @return The maximum value found in this DataBuffer
	 */
	public double getMaxValue();

	/**
	 * Returns the index of the minimum value found in this DataBuffer. If more
	 * than one element has the minimum value, the index of the first of these
	 * is returned.
	 * 
	 * @return The index of the minimum value found in this DataBuffer
	 */
	public int getMaxValueIndex();

	/**
	 * Returns the arithmetic mean of the values in this DataBuffer.
	 * 
	 * @return The arithmetic mean of the values in this DataBuffer
	 */
	public double getArithmeticMean();

	/**
	 * Returns the root mean square (RMS) of the values in this DataBuffer.
	 * 
	 * @return The root mean square (RMS) of the values in this DataBuffer
	 */
	public double getRootMeanSquare();

	/**
	 * Returns a new DataBuffer with the same characteristics as this DataBuffer
	 * but containing only every nth element of this original, where n is the
	 * given sampling rate. This has the effect of reducing the amount of data,
	 * the size of the result being 1/n (with integer rounding).
	 * <p>
	 * If the given rate is 1 or less, the result will be a copy of the entire
	 * buffer; if it is larger than the limit of this DataBuffer, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param sampleRate The subsampling rate
	 * @return A new DataBuffer with the same characteristics as this DataBuffer
	 *         but containing only every nth element of this original, where n
	 *         is the given sampling rate
	 * @throws IllegalArgumentException if the given sample rate is greater than
	 *             the current limit of this DataBuffer
	 */
	public DataBuffer downsample(int downsamplingRate);

	/**
	 * Returns a String representation of the specified range of data in this
	 * DataBuffer.
	 * 
	 * @param startPosition The start of the data range
	 * @param length The length of the data range
	 * @return A String representation of the specified range of data in this
	 *         DataBuffer
	 */
	public String dataToString(int startPosition, int length);

	/**
	 * Returns a String representation of the specified amount of data in this
	 * DataBuffer, starting at the beginning of the Buffer.
	 * 
	 * @param amount The amount of data
	 * @return A String representation of the specified amount of data in this
	 *         DataBuffer, starting at the beginning of the Buffer
	 */
	public String dataToString(int amount);

	/**
	 * Returns a String representation of the data in this DataBuffer.
	 * <p>
	 * Note that for a typical DataBuffer this could produce a <em>very</em>
	 * long String!
	 * 
	 * @return A String representation of the data in this DataBuffer
	 */
	public String dataToString();

}

//--- Development History ---------------------------------------------------
//
//  $Log: DataBuffer.java,v $
//  Revision 1.62  2006/02/08 20:36:28  chostetter_cvs
//  Added initialize(Object value) method
//
//  Revision 1.61  2006/02/07 15:03:46  chostetter_cvs
//  Added clear() and initialize(value) methods
//
//  Revision 1.60  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.59  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.58  2005/07/21 15:27:12  tames_cvs
//  Removed the getSizeAsByte method since the definition was
//  somewhat ambiguous and not valid for ObjectDataBuffers.
//
//  Revision 1.57  2005/07/20 22:07:26  tames_cvs
//  Added bulk put methods of the form: put(index, type[], start, length)
//
//  Revision 1.56  2005/07/19 17:59:48  tames_cvs
//  Changed DataBuffer type to Class instead of DataBufferType. Removed
//  all reference to obsolete DataBufferType class.
//
//  Revision 1.55  2005/07/15 22:03:48  tames
//  Added getSizeInBytes() method to support archivers and other users that
//  that need to know the physical size of the underlying data.
//
//  Revision 1.54  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.53 2005/05/18 14:02:40 smaher_cvs
//  Added put(double). This is convenient for algorithms to stored numbers
//  in databuffers without having to know the data buffer type (as long as
//  the data buffer type is compatible with the provided double).
//
//  Revision 1.52  2005/04/27 21:40:18  chostetter_cvs
//  Added getByte(int) method for symmetry
//
//  Revision 1.51  2005/04/18 15:06:13  pjain_cvs
//  Added getByteScale()
//
//  Revision 1.50  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.49  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.48  2005/03/22 17:00:48  chostetter_cvs
//  Fixed determination of number of valid samples in statistics
//
//  Revision 1.47  2005/03/22 16:46:28  chostetter_cvs
//  Fixed RMS description, added getSize()
//
//  Revision 1.46  2005/03/21 20:00:17  chostetter_cvs
//  Removed byte-position dependent get methods
//
//  Revision 1.45  2005/03/18 20:52:33  tames_cvs
//  Changed castAndCopy... method names to match the pattern established
//  for the single element cast and copy equivalent.
//
//  Revision 1.44  2005/03/17 01:17:25  chostetter_cvs
//  Renamed getByte to get to match ByteBuffer methods
//
//  Revision 1.43  2005/03/17 01:13:53  chostetter_cvs
//  Documentation
//
//  Revision 1.42  2005/03/17 01:07:58  chostetter_cvs
//  Documentation
//
//  Revision 1.41  2005/03/17 00:49:28  chostetter_cvs
//  Documentation
//
//  Revision 1.40  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.39  2005/03/15 17:19:32  chostetter_cvs
//  Made DataBuffer an interface, organized imports
//
//
