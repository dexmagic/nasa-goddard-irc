//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/AbstractDataBuffer.java,v 1.7 2006/01/23 17:59:51 chostetter_cvs Exp $
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

import java.lang.reflect.Array;
import java.nio.BufferOverflowException;
import java.nio.ReadOnlyBufferException;

import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
  * An abstract implementation of the DataBuffer interface.
  * 
  * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
  * for the Instrument Remote Control (IRC) project.
  *
  * @version $Date: 2006/01/23 17:59:51 $
  * @author	Troy Ames
  * @author Carl F. Hostetter
  */
public abstract class AbstractDataBuffer implements DataBuffer
{
	private String fName = DEFAULT_NAME;
	private DataBufferDescriptor fDescriptor;
	
	// Data related fields
	private Object fDataArray;
	protected int fArrayOffset = 0;
	protected int fArrayLength = 0;
	
	protected int fBufferLength = 0;

	private Class fDataType;
	
	private boolean fIsPixel = false;
	private Pixel fPixel;
	private Unit fUnit;
	
	boolean fIsReadOnly = false;
	
	// Statistics related fields
	private boolean fCalculatedStatistics = false;
	private int fNumSamplesInStatistics = 0;
	private double fMinValue = Double.POSITIVE_INFINITY;
	private int fMinValueIndex = -1;
	private double fMaxValue = Double.NEGATIVE_INFINITY;
	private int fMaxValueIndex = -1;
	private double fArithmeticMean = Double.NaN;
	private double fRootMeanSquare = Double.NaN;
	

	/**
	 *	Constructs a DataBuffer having the given array of data and as 
	 *  described by the given DataBufferDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 *  @param data An array of data
	 *  @param capacity The capacity of this DataBuffer
	 */
	public AbstractDataBuffer(DataBufferDescriptor descriptor, Object array, int capacity)
	{
		this(descriptor, array, 0, capacity);
	}
	
	/**
	 * Constructs a DataBuffer having the given array of data with the 
	 * given range and as described by the given DataBufferDescriptor.
	 * 
	 * @param descriptor A DataBufferDescriptor
	 * @param data An array of data
	 * @param offset The offset in the array for the first element of this
	 *            DataBuffer
	 * @param capacity The capacity of this DataBuffer
	 */
	AbstractDataBuffer(
		DataBufferDescriptor descriptor, 
		Object array,
		int offset, int capacity)
	{
		super();
		
		fName = descriptor.getName();

		if (fName == null)
		{
			fName = "AbstractDataBuffer";
		}
		
		//fArrayData = Array.newInstance(double.class, capacity);
		fDescriptor = descriptor;
		
		fDataType = descriptor.getDataType();
		fIsPixel = descriptor.isPixel();
		fPixel = descriptor.getPixel();
		fUnit = descriptor.getUnit();
		
		fDataArray = array;
		fArrayOffset = offset;
		fArrayLength = Array.getLength(fDataArray);
		fBufferLength = capacity;
		
		if (fBufferLength > fArrayLength)
		{
			throw new IllegalArgumentException(
				"Buffer length cannot be greater than source array length");
		}
	}
	
	/**
	 * Sets the name of this DataBuffer to the given name. If the given name is
	 * null, the name will be set to the DEFAULT_NAME.
	 * 
	 * @param The desired new name of this Object
	 */
	protected void setName(String name) 
	{
		if (name == null)
		{
			fName = DEFAULT_NAME;
		}
		else
		{
			fName = name;
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.types.namespaces.HasName#getName()
	 */
	public String getName()
	{
		return (fName);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#array()
	 */
	public final Object array()
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}

		return (fDataArray);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#hasArray()
	 */
	public final boolean hasArray()
	{
		return (!fIsReadOnly);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#arrayOffset()
	 */
	public final int arrayOffset()
	{
		if (fIsReadOnly)
		{
			throw new ReadOnlyBufferException();
		}
		
		if (!hasArray())
		{
			throw new UnsupportedOperationException();
		}

		return (fArrayOffset);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#isReadOnly()
	 */
	public final boolean isReadOnly()
	{
		return (fIsReadOnly);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#duplicate()
	 */
	public abstract DataBuffer duplicate();
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#slice(int, int)
	 */
	public DataBuffer slice(int offset, int length)
	{
		AbstractDataBuffer slice = null;

		// Verify arguments are valid
		if (offset >= fBufferLength)
		{
			String message = "The specified offset (" + offset 
				+ ") is greater than the original buffer length of (" 
				+ fBufferLength + ") ";
		
			throw (new IllegalArgumentException(message));
		}
		
		// TODO need to resolve how to do the following with the root buffer
//		if (offset + length > fBufferLength)
//		{
//			String message = "The specified arguments (" 
//				+ offset + ", " + length
//				+ ") is not rational for a buffer of length ("
//				+ fBufferLength + ") ";
//		
//			throw (new IllegalArgumentException(message));
//		}
		
		int newOffset = offset + fArrayOffset;
		
		// Test for wrap around of the offset
		if (newOffset >= fArrayLength)
		{
			newOffset -= fArrayLength;
		}
		
		slice = (AbstractDataBuffer) duplicate();
		slice.fArrayOffset = newOffset;
		slice.fBufferLength = length;
		
		return (slice);
	}

	/**
	 * Returns the absolute index in the underlying array.
	 * 
	 * @param index relative index in this DataBuffer
	 * @return the index in the underlying array.
	 */
	final int resolveIndex(int index)
	{
		if ((index < 0) || (index >= fBufferLength))
		{
			throw new IndexOutOfBoundsException();
		}
		
		index += fArrayOffset;
		
		if (index >= fArrayLength)
		{
			index -= fArrayLength;
		}
		
		return index;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getDescriptor()
	 */
	public DataBufferDescriptor getDescriptor()
	{
		return (fDescriptor);	
	}
	
	/**
	 * Returns the fully-qualified name of this DataBuffer, i.e., including the
	 * Pixel tag if it is has a Pixel.
	 * 
	 * @return The fully-qualified name of this DataBuffer, i.e., including the
	 *         Pixel tag if it is has a Pixel
	 */
	public String getFullyQualifiedName()
	{
		return (fDescriptor.getFullyQualifiedName());
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getDataBufferType()
	 */
	public Class getDataBufferType()
	{
		return (fDataType);	
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getUnit()
	 */
	public Unit getUnit()
	{
		return (fUnit);	
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#isRealValued()
	 */
	public boolean isRealValued()
	{
		boolean result = false;
		
		if ((fDataType == double.class) ||
			(fDataType == float.class))
		{
			result = true;
		}
		
		return (result);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#copy()
	 */
	public abstract DataBuffer copy();

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#makeReadOnly()
	 */
	public DataBuffer makeReadOnly()
	{
		fIsReadOnly = true;
		
		return (this);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#recalculateStatistics()
	 */
	public void recalculateStatistics()
	{
		int numSamples = fBufferLength;
		fNumSamplesInStatistics = numSamples;
		
		double sum = 0;
		
		for (int i = 0; i < numSamples; i++)
		{
			double value = getAsDouble(i);
			
			if (value != Double.NaN)
			{
				sum += value;
				
				if (value < fMinValue)
				{
					fMinValue = value;
					fMinValueIndex = i;
				}
				
				if (value > fMaxValue)
				{
					fMaxValue = value;
					fMaxValueIndex = i;
				}
			}
			else
			{
				fNumSamplesInStatistics--;
			}
		}
		
		if (fNumSamplesInStatistics > 0)
		{
			fArithmeticMean = sum / fNumSamplesInStatistics;
		
			fRootMeanSquare = Math.sqrt(fArithmeticMean * fArithmeticMean);
		}
		
		fCalculatedStatistics = true;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getNumSamplesInStatistics()
	 */
	public int getNumSamplesInStatistics()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fNumSamplesInStatistics);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getMinValue()
	 */
	public double getMinValue()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fMinValue);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getMinValueIndex()
	 */
	public int getMinValueIndex()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fMinValueIndex);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getMaxValue()
	 */
	public double getMaxValue()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fMaxValue);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getMaxValueIndex()
	 */
	public int getMaxValueIndex()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fMaxValueIndex);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getArithmeticMean()
	 */
	public double getArithmeticMean()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fArithmeticMean);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getRootMeanSquare()
	 */
	public double getRootMeanSquare()
	{
		if (! fCalculatedStatistics)
		{
			recalculateStatistics();
		}
		
		return (fRootMeanSquare);
	}
			
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#dataToString(int,int)
	 */
	public abstract String dataToString(int startPosition, int length);
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#dataToString(int)
	 */
	public String dataToString(int amount)
	{
		return (dataToString(0, amount));
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#dataToString()
	 */
	public String dataToString()
	{
		return (dataToString(fBufferLength));
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#isPixel()
	 */
	public boolean isPixel()
	{
		return (fIsPixel);	
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getPixel()
	 */
	public Pixel getPixel()
	{
		return (fPixel);	
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataBuffer#getSize()
	 */
	public int getSize()
	{
		return (fBufferLength);	
	}
	
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, byte[], int, int)
     */
    public DataBuffer put(int index, byte[] source, int sourceIndex, int length)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, char[], int, int)
     */
    public DataBuffer put(int index, char[] source, int sourceIndex, int length)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, short[], int, int)
     */
    public DataBuffer put(int index, short[] source, int sourceIndex, int length)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, long[], int, int)
     */
    public DataBuffer put(int index, long[] source, int sourceIndex, int length)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, float[], int, int)
     */
    public DataBuffer put(int index, float[] source, int sourceIndex, int length)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, double[], int, int)
     */
    public DataBuffer put(int index, double[] source, int sourceIndex, int length)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, java.lang.Object[], int, int)
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
		
		for (int i = 0; i < length; i++)
		{
			put(index + i, source[sourceIndex + i]);
		}
		
		return this;
	}

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, gov.nasa.gsfc.irc.data.DataBuffer)
     */
    public DataBuffer put(int index, DataBuffer source)
    {
    	return put(index, source, 0, source.getSize());
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.data.DataBuffer#put(int, gov.nasa.gsfc.irc.data.DataBuffer, int, int)
     */
	public abstract DataBuffer put(int i, DataBuffer source, int j, int size);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer();
		
		if (fDescriptor != null)
		{
			stringRep.append(fDescriptor.toString());
		}
		
		return (stringRep.toString());
	}	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataBuffer.java,v $
//  Revision 1.7  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.5  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.4  2005/08/26 02:57:21  tames
//  Reduced the inheritence depth by removing the NamedObject as super class.
//
//  Revision 1.3  2005/07/20 22:07:26  tames_cvs
//  Added bulk put methods of the form: put(index, type[], start, length)
//
//  Revision 1.2  2005/07/19 17:59:48  tames_cvs
//  Changed DataBuffer type to Class instead of DataBufferType. Removed
//  all reference to obsolete DataBufferType class.
//
//  Revision 1.1  2005/07/14 21:48:16  tames
//  Initial version. Needed for data model refactoring.
//
//  Revision 1.17  2005/05/18 14:02:40  smaher_cvs
//  Added put(double).  This is convenient for algorithms to stored numbers
//  in databuffers without having to know the data buffer type (as long as
//  the data buffer type is compatible with the provided double).
//
//  Revision 1.16  2005/04/27 21:40:18  chostetter_cvs
//  Added getByte(int) method for symmetry
//
//  Revision 1.15  2005/04/18 15:09:35  pjain_cvs
//  Added getByteScale()
//
//  Revision 1.14  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.13  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.12  2005/03/22 17:04:22  chostetter_cvs
//  Oops.
//
//  Revision 1.11  2005/03/22 17:00:48  chostetter_cvs
//  Fixed determination of number of valid samples in statistics
//
//  Revision 1.10  2005/03/22 16:46:28  chostetter_cvs
//  Fixed RMS description, added getSize()
//
//  Revision 1.9  2005/03/21 20:00:17  chostetter_cvs
//  Removed byte-position dependent get methods
//
//  Revision 1.8  2005/03/18 20:52:33  tames_cvs
//  Changed castAndCopy... method names to match the pattern established
//  for the single element cast and copy equivalent.
//
//  Revision 1.7  2005/03/17 01:17:25  chostetter_cvs
//  Renamed getByte to get to match ByteBuffer methods
//
//  Revision 1.6  2005/03/17 01:13:53  chostetter_cvs
//  Documentation
//
//  Revision 1.5  2005/03/17 01:07:58  chostetter_cvs
//  Documentation
//
//  Revision 1.4  2005/03/17 00:49:07  chostetter_cvs
//  Documentation
//
//  Revision 1.3  2005/03/17 00:38:08  chostetter_cvs
//  Makes sure relative element gets are at an element boundary
//
//  Revision 1.2  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.1  2005/03/15 17:19:32  chostetter_cvs
//  Made DataBuffer an interface, organized imports
//
//  Revision 1.38  2005/03/15 01:06:50  chostetter_cvs
//  Changed reset() to rewind() in copy()
//
//  Revision 1.37  2005/03/15 01:00:43  chostetter_cvs
//  Fixed copy() behavior to exactly preserve ByteBuffer state
//
//  Revision 1.36  2005/03/15 00:36:02  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.35  2005/03/14 15:16:53  tames
//  Modified the copy method to use bulk copy for potential performance
//  improvement.
//
//  Revision 1.34  2005/03/10 21:37:23  tames
//  Modifications for performance and to better match the ByteBuffer
//  API. Should the getDataAs_Buffer() methods be changed to
//  as_Buffer() to match ByteBuffer?
//
//  Revision 1.33  2005/01/28 23:57:34  chostetter_cvs
//  Added ability to integrate DataBuffers and requests, renamed average value
//
//  Revision 1.32  2004/10/08 14:13:34  chostetter_cvs
//  Fixed min, max value initializations
//
//  Revision 1.31  2004/09/14 20:12:13  chostetter_cvs
//  Units now specified as Strings in lieu of better scheme later
//
//  Revision 1.30  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.29  2004/07/28 22:14:29  chostetter_cvs
//  Fixed data copying bug (needed to flip Buffer)
//
//  Revision 1.28  2004/07/24 02:46:11  chostetter_cvs
//  Added statistics calculations to DataBuffers, renamed some classes
//
//  Revision 1.27  2004/07/21 14:26:15  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.26  2004/07/19 23:47:50  chostetter_cvs
//  Real-valued boundary conidition work
//
//  Revision 1.25  2004/07/19 14:16:14  chostetter_cvs
//  Added ability to subsample data in requests
//
//  Revision 1.24  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.23  2004/07/17 18:39:02  chostetter_cvs
//  Name, descriptor modification work
//
//  Revision 1.22  2004/07/14 00:33:48  chostetter_cvs
//  More Algorithm, data testing. Fixed slice bug.
//
//  Revision 1.21  2004/07/13 18:52:50  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.20  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.19  2004/07/11 21:19:44  chostetter_cvs
//  More Algorithm work
//
//  Revision 1.18  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.17  2004/07/09 22:29:11  chostetter_cvs
//  Extensive testing of Input/Output interaction, supports simple BasisRequests
//
//  Revision 1.16  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.15  2004/07/02 02:38:53  chostetter_cvs
//  Moved Pixel from data to commons package
//
//  Revision 1.14  2004/06/09 03:28:49  chostetter_cvs
//  Output-related modifications
//
//  Revision 1.13  2004/06/05 19:13:00  chostetter_cvs
//  DataBuffer now wraps a ByteBuffer (instead of Buffer) and provides typed views of its ByteBuffer for reading and writing
//
//  Revision 1.12  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.11  2004/06/04 23:10:27  chostetter_cvs
//  Added data printing support to various Buffer classes
//
//  Revision 1.10  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
//  Revision 1.9  2004/05/29 14:53:54  chostetter_cvs
//  Adjusted class definition
//
//  Revision 1.8  2004/05/29 14:52:03  chostetter_cvs
//  Added support for CharBuffers
//
//  Revision 1.7  2004/05/29 03:47:16  chostetter_cvs
//  Renamed DataValueType to DataBufferType
//
//  Revision 1.6  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.5  2004/05/27 23:09:26  chostetter_cvs
//  More Namespace related changes
//
//  Revision 1.4  2004/05/27 15:57:16  chostetter_cvs
//  Data-related changes
//
//  Revision 1.3  2004/05/20 21:28:10  chostetter_cvs
//  Checking in for the weekend
//
//  Revision 1.2  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.1  2004/05/16 21:54:27  chostetter_cvs
//  More work
//
