//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/BasisSet.java,v 1.60 2006/08/01 19:55:47 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development histroy is located at the end of the file.
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
import java.nio.ReadOnlyBufferException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.commons.system.memory.Allocation;
import gov.nasa.gsfc.commons.types.namespaces.CreatedMember;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * A BasisSet is constrained view on the basis Buffer and on some set of the
 * data Buffers of a BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public interface BasisSet extends CreatedMember, Serializable
{
	/**
	 * Returns the BasisBundleId of this BasisSet.
	 * 
	 * @return The BasisBundleId of this BasisSet
	 */
	public BasisBundleId getBasisBundleId();

	/**
	 * Returns the MemberId of the BasisBundleSource of this BasisSet.
	 * 
	 * @return The BasisBundleSourceId of this BasisSet
	 */
	public MemberId getBasisBundleSourceId();

	/**
	 * Returns the index of the start position of this BasisSet within the
	 * BasisBundle it was allocated from (if any).
	 * 
	 * @return The index of the start position of this BasisSet within the
	 *         BasisBundle it was allocated from (if any).
	 * @throws UnsupportedOperationException if this BasisSet has no defined
	 *             BasisBundle position
	 */
	public int getBasisBundleStartPosition();

	/**
	 * Clears each of the values in each of the DataBuffers of this BasisSet 
	 * (including the BasisBuffer) to an appropriate value for its type. If this 
	 * BasisSet is read-only, this call has no effect, and throws an 
	 * UnsupportedOperationException
	 * 
	 * @return This BasisSet
	 * @throws UnsupportedOperationException if this is a read-only BasisSet
	 */
	public BasisSet clear()
		throws UnsupportedOperationException;

	/**
	 * Returns a copy of this BasisSet, having exactly the same structure as
	 * this BasisSet but containing a copy of the underlying basis and data
	 * buffers. Note that because this data is a copy, releasing, accessing, or
	 * modifying this BasisSet will have no effect on the returned copy.
	 * 
	 * @return A copy of this BasisSet
	 */
	public BasisSet copy();

	/**
	 * Returns true if this BasisSet is a copy of some other BasisSet (i.e., not
	 * actually an allocation on some portion of its source BasisBundle), false
	 * otherwise.
	 * 
	 * @return True if this BasisSet is a copy of some other BasisSet (i.e., not
	 *         actually an allocation on some portion of its source
	 *         BasisBundle), false otherwise
	 */
	public boolean isCopy();

	/**
	 * Returns a duplicate of this BasisSet, having exactly the same structure
	 * as as this BasisSet but containing a duplicate of the underlying basis 
	 * and data buffers. Note that the underlying data is not copied.
	 * 
	 * @return A duplicate of this BasisSet
	 */
	public BasisSet duplicate();

	/**
	 * Returns a slice from this BasisSet, having exactly the same structure as
	 * this BasisSet but spanning only the given subrange of basis samples.
	 * 
	 * @param startIndex The index of the start position of the desired slice
	 *            from this BasisSet (inclusive)
	 * @param length the length or number of samples in the slice.
	 * @return A slice from this BasisSet, having exactly the same structure as
	 *         this BasisSet but spanning the given subrange of basis samples.
	 */
	public BasisSet slice(int startIndex, int length);

	/**
	 * Returns a new BasisSet that includes the samples from the specified
	 * BasisSet appended to this BasisSet. Both BasisSets must have the same
	 * structure. This may result in copying the data from the two source
	 * BasisSets to the result. If copying the data is not necessary then the
	 * underlying basis and data buffers are shared. Use the <code>isCopy</code>
	 * method to determine if the result is a copy.
	 * <p>
	 * The caller should always release the new BasisSet when it is no longer needed.
	 * 
	 * @param source The BasisSet to be appended.
	 * @return A BasisSet that represents the two BasisSets.
	 */
	public BasisSet append(BasisSet source);

    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * BasisSet into this BasisSet.  If there are more elements in the
     * source BasisSet than in this BasisSet, that is, if
     * <tt>src.size()</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size() - index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <i>n</i>&nbsp;=&nbsp;<tt>src.size()</tt> elements from the given
     * BasisSet into this BasisSet, starting at <tt>index</tt>.
     * 
     * @param  index The index into this BasisSet to start copying the source data
     * @param  source
     *         The source BasisSet from which elements are to be read;
     *         must not be this BasisSet
     *
     * @return  This BasisSet
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this BasisSet
     *          for the remaining elements in the source BasisSet
     *
     * @throws  IllegalArgumentException
     *          If the source BasisSet is this BasisSet
     *
     * @throws  ReadOnlyBufferException
     *          If this BasisSet is read-only
     */
    public BasisSet put(int index, BasisSet source);

    /**
     * Bulk <i>put</i> method.
     *
     * <p> This method transfers the values in the given source
     * BasisSet into this buffer.  If the specified <tt>length</tt> is 
     * larger than this BasisSet, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>size()-index</tt>,
     * then no elements are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <tt>length</tt> elements from the given
     * BasisSet into this BasisSet, starting at <tt>index</tt>.
     * 
     * @param  index The index into this BasisSet to start copying the source data
     * @param  source
     *         The source BasisSet from which elements are to be read;
     *         must not be this buffer
     * @param  sourceIndex The index in the source BasisSet to start copying
     * @param  length The length of the source data to copy
     * 
     * @return  This BasisSet
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this BasisSet
     *          for the remaining elements in the source BasisSet
     *
     * @throws  IllegalArgumentException
     *          If the source BasisSet is this BasisSet
     *
     * @throws  ReadOnlyBufferException
     *          If this BasisSet is read-only
     */
    public BasisSet put(int index, BasisSet source, int sourceIndex, int length);
    
	/**
	 * Returns a new BasisSet that includes only those of the DataBuffers of
	 * this BasisSet whose names are included in the given Set of names. Note
	 * that if a DataBuffer has a Pixel, its name includes the Pixel tag.
	 * 
	 * @param dataBufferNames The names of the DataBuffers to retain in the
	 *            filtered BasisSet
	 * @return A new BasisSet that includes only those of the DataBuffers of
	 *         this BasisSet whose DataBufferIds are included in the given Set
	 *         of DataBufferIds
	 */
	public BasisSet filterIn(Set dataBufferIds);

	/**
	 * Returns a new BasisSet that includes only those of the DataBuffers of
	 * this BasisSet whose DataBufferIds are <em>not</em> included in the
	 * given Set of DataBufferIds.
	 * 
	 * @param dataBufferIds The DataBufferIds of the DataBuffers to exclude from
	 *            the filtered BasisSet
	 * @return A new BasisSet that includes only those of the DataBuffers of
	 *         this BasisSet whose DataBufferIds are <em>not</em> included in
	 *         the given Set of DataBufferIds
	 */
	public BasisSet filterOut(Set dataBufferIds);

	public Allocation getAllocation();
	
	public void setAllocation(Allocation allocation);
	
	/**
	 * Returns the size of this BasisSet in number of basis samples.
	 * 
	 * @return The size of this BasisSet in number of basis samples
	 */
	public int getSize();

	/**
	 * Returns the first basis value of this BasisSet.
	 * 
	 * @return The first basis value of this BasisSet
	 */
	public double getFirstBasisValue();

	/**
	 * Returns the last basis value of this BasisSet.
	 * 
	 * @return The last basis value of this BasisSet
	 */
	public double getLastBasisValue();

	/**
	 * Returns the size of this BasisSet in absolute amount of basis values
	 * spanned. This method assumes that the values in the BasisBuffer of this
	 * BasisSet are numeric and monotonic (either increasing or decreasing).
	 * 
	 * @return The size of this BasisSet in absolute amount of basis values
	 *         spanned.
	 */
	public double getBasisAmount();

	/**
	 * Returns the base or epoch that all BasisBuffer values are relative to
	 * in this BasisSet.
	 * 
	 * @return The base or epoch of the BasisBuffer.
	 */
	public double getBasisBase();

	/**
	 * Returns the index at which the given value occurs in the basis buffer of
	 * this BasisSet, as an upper bound on the basis value. If the given value
	 * does not occur, the index returned will be that of the greatest basis
	 * value that does not exceed the given value. If the first basis buffer
	 * value exceeds the given value, the result will be negative. If the given
	 * value is greater than the last basis value, the result will the capacity
	 * of this BasisSet (i.e., one more than the last index of the basis
	 * buffer).
	 * 
	 * @param value The absolute basis value to find in this BasisSet
	 * @return The index at which the given value, or the greatest basis value
	 *         that does not exceed the given value, occurs in the basis buffer
	 *         of this BasisSet
	 */
	public int getIndexOfBasisValueAsUpperBound(double value);

	/**
	 * Returns the index at which the given value occurs in the basis buffer of
	 * this BasisSet, as an lower bound on the basis value. If the given value
	 * does not occur, the index returned will be that of the least basis value
	 * that exceeds the given value. If no such basis value occurs, the result
	 * will be the capacity of this BasisSet (i.e., one more than the last index
	 * of the basis buffer)
	 * 
	 * @param value The absolute basis value to find in this BasisSet
	 * @return The index at which the given value, or the least basis value that
	 *         exceeds the given value, occurs in the basis buffer of this
	 *         BasisSet
	 */
	public int getIndexOfBasisValueAsLowerBound(double value);

	/**
	 * Returns the index at which the given absolute basis amount, as an offset
	 * from the asbolute value of the first basis value of this BasisSet, is
	 * spanned; or if, the given absolute offset amount is not precisely spanned
	 * at any index, then the greatest index at which the given absolute offset
	 * amount is not exceeded. If the given amount exceeds the absolute basis
	 * amount spanned by the entire basis buffer, the result will be the size of
	 * this BasisSet (i.e., one greater than the last index of the basis
	 * buffer).
	 * 
	 * @param amount The absolute basis amount, as an offset from the absolute
	 *            value of the first basis value of this BasisSet, to find
	 * @return The index at which the given absolute basis amount, as an offset
	 *         from the asbolute value of the first basis value of this
	 *         BasisSet, is spanned; or if, the given absolute offset amount is
	 *         not precisely spanned at any index, then the greatest index at
	 *         which the given absolute offset amount is not exceeded
	 */
	public int getIndexOfBasisAmountAsUpperBound(double amount);

	/**
	 * Returns the first index at which the given absolute basis amount, as an
	 * offset from the asbolute value of the first basis value of this BasisSet,
	 * is spanned or exceeded. If the given amount exceeds the absolute basis
	 * amount spanned by the entire basis buffer, the result will be the size of
	 * this BasisSet (i.e., one greater than the last index of the basis
	 * buffer).
	 * 
	 * @param amount The absolute basis amount, as an offset from the absolute
	 *            value of the first basis value of this BasisSet, to find
	 * @return The first index at which the given absolute basis amount, as an
	 *         offset from the asbolute value of the first basis value of this
	 *         BasisSet, is spanned or exceeded
	 */
	public int getIndexOfBasisAmountAsLowerBound(double amount);

	/**
	 * Returns true if this BasisSet has been declared to be uniformly sampled,
	 * false otherwise.
	 * 
	 * @return True if this BasisSet has been declared to be uniformly sampled,
	 *         false otherwise
	 */
	public boolean isUniformlySampled();

	/**
	 * Sets the declared uniform sample interval of the data in this BasisSet to the
	 * given value, in terms of the units of its basis buffer. It is up to the
	 * caller to guarantee that the given interval is accurate, and that all basis
	 * values in this BasisSet are in fact uniformly spaced at the declared
	 * interval. To indicate that the BasisSet is not uniformly sampled (the default
	 * assumption, unless the BasisBundle from which the BasisSet was allocated
	 * was also declared to be uniformly sampled at allocation time), set the
	 * value to 0 or less, or to Double.Nan.
	 * 
	 * @param sampleInterval The declared sample interval of the data in this BasisSet
	 *            in terms of the units of its basis buffer
	 */
	public void setUniformSampleInterval(double sampleInterval);

	/**
	 * Sets the declared uniform sample interval of the data in this BasisSet to
	 * the difference of the first two samples in the basis buffer of this
	 * BasisSet.
	 * 
	 * @throws IllegalStateException
	 *             if less than two samples are available in this BasisSet
	 */
	public void setUniformSampleIntervalImplicitly();
	
	/**
	 * Returns the declared uniform sample interval of the data in this BasisSet, in
	 * terms of the units of its basis buffer. If this BasisSet is not uniformly
	 * sampled, the result will be Double.Nan.
	 * 
	 * @return The declared sample interval of the data in this BasisSet in terms of
	 *         the units of its basis buffer
	 */
	public double getUniformSampleInterval();

	/**
	 * Returns a new BasisSet with the same structure as this BasisSet but whose
	 * basis and data buffers contain only every nth sample of the originals,
	 * where n is the given sampling rate. This has the effect of reducing the
	 * amount of data, the size of the result being 1/n (with integer rounding).
	 * <p>
	 * If the given rate is 1 or less, the result will be a copy of the entire
	 * buffer; if it is larger than the capacity of this DataBuffer, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param sampleRate The subsampling rate
	 * @return A new BasisSet with the same structure as this BasisSet but whose
	 *         basis and data buffers contain only every nth sample of the
	 *         originals, where n is the given sampling rate
	 * @throws IllegalArgumentException if the given sample rate is greater than
	 *             the current capacity of this BasisSet
	 */
	public BasisSet downsample(int sampleRate);

	/**
	 * Returns true if this BasisSet is read-only, false otherwise.
	 * 
	 * @return True if this BasisSet is read-only, false otherwise
	 */
	public boolean isReadOnly();

	/**
	 * Makes all of the Buffers (Basis and Data) in the BasisSet read-only. This
	 * will also flip each buffer (in the java.nio.Buffer sense)
	 */
	public void makeReadOnly();

	/**
	 * Makes the contents of this BasisSet available on its BasisBundle of origin.
	 * 
	 * @throws UnsupportedOperationException if the BasisSet is read only
	 */
	public void makeAvailable() throws UnsupportedOperationException;

	/**
	 * Makes the given number of valid samples of this BasisSet available on its 
	 * BasisBundle of origin.
	 * 
	 * @param The number of valid samples contained in the BasisSet
	 * @throws UnsupportedOperationException if the BasisSet is read only
	 */	
	public void makeAvailable(int numValidSamples) throws UnsupportedOperationException;
	
	/**
	 * Returns the BasisBundleDescriptor that describes the structure of this
	 * BasisSet.
	 * 
	 * @return The BasisBundleDescriptor that describes the structure of this
	 *         BasisSet
	 */
	public BasisBundleDescriptor getDescriptor();

	/**
	 * Returns the basis Buffer of this BasisSet.
	 * 
	 * @return The basis Buffer of this BasisSet
	 */
	public DataBuffer getBasisBuffer();

	/**
	 * Returns the DataBufferDescriptor that describes the basis Buffer of this
	 * BasisSet.
	 * 
	 * @return The DataBufferDescriptor that describes the basis Buffer of this
	 *         BasisSet
	 */
	public DataBufferDescriptor getBasisBufferDescriptor();

	/**
	 * Returns the Set of DataBufferDescriptors describing the DataBuffers
	 * contained in this BasisSet.
	 * 
	 * @return The Set of DataBufferDescriptors describing the DataBuffers
	 *         contained in this BasisSet
	 */
	public Set getDataBufferDescriptors();

    /**
     * Returns an iterator over the DataBuffers in this BasisSet. The order 
     * in which DataBuffers are returned are guaranteed to be the same for 
     * the same BasisSet.
     * 
     * @return an <tt>Iterator</tt> over the DataBuffers in this BasisSet
     */
    public Iterator getDataBuffers();

    /**
     * Returns the number of DataBuffers in this BasisSet.
     * 
     * @return The number of DataBuffers in this BasisSet
     */
    public int getNumberOfDataBuffers();

    /**
	 * Returns the ordered Map of the DataBuffers (by name) of this BasisSet
	 * whose names match the given regular expression.
	 * 
	 * @param regEx A regular expression
	 * @return The ordered Map of the DataBuffers (by name) of this BasisSet
	 *         whose names match the given regular expression
	 */
	public Map getDataBuffers(String regEx);

	/**
	 * Returns the Set of the names of the DataBuffers contained in this
	 * BasisSet. Note that if a DataBuffer has a Pixel, its name includes the
	 * Pixel tag.
	 * 
	 * @return The Set of the names of the DataBuffers contained in this
	 *         BasisSet
	 */
	public Set getDataBufferNames();

	/**
	 * Returns the Set of names of the DataBuffers of this BasisSet that match
	 * the given regular expression. Note that if a DataBuffer has a Pixel, the
	 * Pixel tag is part of its name.
	 * 
	 * @param A regular expression
	 * @return The Set of names of the DataBuffers of this BasisSet that match
	 *         the given regular expression
	 */
	public Set getDataBufferNames(String regEx);

	/**
	 * Returns the DataBuffer of this BasisSet associated with the given index.
	 * If no such DataBuffer is present, the result will be null.
	 * 
	 * @param index The index of a DataBuffer of this BasisSet
	 * @return The DataBuffer of this BasisSet at the given index
	 */
	public DataBuffer getDataBuffer(int index);

	/**
	 * Returns the DataBuffer of this BasisSet that has the given name. Note
	 * that if the indicated DataBuffer is a Pixel, its name includes the Pixel
	 * tag. If no such DataBuffer is present, the result will be null.
	 * 
	 * @param name The name of a DataBuffer of this BasisSet
	 * @return The DataBuffer of this BasisSet that has the given name
	 */
	public DataBuffer getDataBuffer(String name);

	/**
	 * Returns the DataBuffer of this BasisSet that has the given (base) name
	 * and the given Pixel. If no such DataBuffer is present, the result will be
	 * null.
	 * 
	 * @param name The (base) name of a DataBuffer of this BasisSet
	 * @param pixel The Pixel of the indicated DataBuffer
	 * @return The DataBuffer of this BasisSet that has the given (base) name
	 *         and Pixel
	 */
	public DataBuffer getDataBuffer(String name, Pixel pixel);

	/**
	 * Returns the DataBuffer of this BasisSet that has the given (base) name
	 * and the Pixel represented by the given array of indices. If no such
	 * DataBuffer is present, the result will be null.
	 * 
	 * @param name The (base) name of a DataBuffer of this BasisSet
	 * @param pixel An integer array representing the Pixel of the indicated
	 *            DataBuffer
	 * @return The DataBuffer of this BasisSet that has the given (base) name
	 *         and Pixel
	 */
	public DataBuffer getDataBuffer(String name, int[] pixel);

	/**
	 * Releases this BasisSet from further use by the caller. This allows the
	 * provider to reclaim the memory used by this BasisSet.
	 */
	public void release();

	/**
	 * Holds this BasisSet for further use by the caller. This prevents 
	 * the BasisSet from being reclaimed by the provider.
	 */
	public void hold();

	/**
	 * Returns a String representation of the specified range of data in this
	 * BasisSet, starting with the BasisBuffer and across each DataBuffer.
	 * 
	 * @param startPosition The start of the data range
	 * @param endPosition The end of the data range
	 * @return A String representation of the specified range of data in this
	 *         BasisSet
	 */
	public String dataToString(int startPosition, int endPosition);

	/**
	 * Returns a String representation of the specified amount of data in this
	 * BasisSet, starting with the BasisBuffer and across each DataBuffer, and
	 * starting at the beginning of each Buffer.
	 * 
	 * @param amount The amount of data
	 * @return A String representation of the specified amount of data in this
	 *         BasisSet, starting with the BasisBuffer and across each
	 *         DataBuffer, and starting at the beginning of each Buffer
	 */
	public String dataToString(int amount);

	/**
	 * Returns a String representation of the data in this BasisSet, starting
	 * with the BasisBuffer and across each DataBuffer.
	 * <p>
	 * Note that for a typical BasisSet this could produce a <em>very</em>
	 * long String!
	 * 
	 * @return A String representation of the data in this BasisSet
	 */
	public String dataToString();
}

//--- Development History ---------------------------------------------------
//
//  $Log: BasisSet.java,v $
//  Revision 1.60  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.59  2006/05/31 13:05:21  smaher_cvs
//  Name change: UniformSampleRate->UniformSampleInterval
//
//  Revision 1.58  2006/05/17 21:10:59  smaher_cvs
//  setUniformSampleRate() -> setUniformSampleRateImplicitly()
//
//  Revision 1.57  2006/05/12 18:09:30  smaher_cvs
//  Added setUniformSampleRate().
//
//  Revision 1.56  2006/02/07 15:27:44  chostetter_cvs
//  Fixed Javadoc
//
//  Revision 1.55  2006/02/07 15:26:37  chostetter_cvs
//  Added clear() method
//
//  Revision 1.54  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.53  2005/12/05 04:13:50  tames
//  Added support for a BasisBuffer base to handle conditions where a double
//  does not have enough percision to represent a BasisBuffer value.
//
//  Revision 1.52  2005/09/13 22:28:58  tames
//  Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.51  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.50  2005/08/26 22:13:30  tames_cvs
//  Changes that are an incomplete refactoring. Also added initial support for history.
//
//  Revision 1.49  2005/08/26 02:58:54  tames
//  Documentation change only.
//
//  Revision 1.48  2005/07/21 15:27:12  tames_cvs
//  Removed the getSizeAsByte method since the definition was
//  somewhat ambiguous and not valid for ObjectDataBuffers.
//
//  Revision 1.47  2005/07/18 21:12:03  tames_cvs
//  Changed getSizeInBytes method to include the size of the basis buffer
//  in the returned size.
//
//  Revision 1.46  2005/07/15 22:03:48  tames
//  Added getSizeInBytes() method to support archivers and other users that
//  that need to know the physical size of the underlying data.
//
//  Revision 1.45  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.44 2005/04/27 15:18:03 chostetter_cvs
//  Added methods to get DataBuffer by base name + Pixel
//
//  Revision 1.43  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.42  2005/03/24 21:54:28  chostetter_cvs
//  Fixed problem with shrinking BasisSets having fewer valid samples than requested, and with calculating spanned Basis values for BasisSets that decrease monotonically (rather than increase).
//
//  Revision 1.41  2005/03/22 22:47:06  chostetter_cvs
//  Refactoring of BasisSet allocation, release
//
//  Revision 1.40  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.39  2005/02/02 21:17:27  chostetter_cvs
//  DataSets (and their BasisSets) are now duplicated (in the java.nio sense) when sent to more than one DataListener
//
//  Revision 1.38  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
//  Revision 1.37  2005/01/28 23:57:34  chostetter_cvs
//  Added ability to integrate DataBuffers and requests, renamed average value
//
//  Revision 1.36  2005/01/26 20:46:05  chostetter_cvs
//  Added method to clear the contents of a BasisSet.
//
//  Revision 1.35  2004/11/30 16:53:36  chostetter_cvs
//  Added ability to obtain Set of names that match a given regular expression
//
//  Revision 1.34  2004/11/30 15:50:03  chostetter_cvs
//  Added ability to select a set of DataBuffers by matching names to a given regular expression
//
//  Revision 1.33  2004/09/15 20:06:17  chostetter_cvs
//  Fixed issues with freeing of java.nio duplicate memory block freeing
//
//  Revision 1.32  2004/09/10 14:49:03  chostetter_cvs
//  More data description work
//
//  Revision 1.31  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.30  2004/07/29 16:55:19  chostetter_cvs
//  Doc tweaks
//
//  Revision 1.29  2004/07/28 19:11:25  chostetter_cvs
//  BasisBundle now alerts of impending discontinuity, BasisRequester copies and releases pending data at discontinuity
//
//  Revision 1.28  2004/07/21 14:26:14  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.27  2004/07/20 14:53:00  chostetter_cvs
//  Made BasisSets stative with regard to being released
//
//  Revision 1.26  2004/07/19 19:00:38  chostetter_cvs
//  Implemented requests by basis amount
//
//  Revision 1.25  2004/07/19 14:16:14  chostetter_cvs
//  Added ability to subsample data in requests
//
//  Revision 1.24  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.23  2004/07/16 21:35:24  chostetter_cvs
//  Work on declaring uniform sample rate of data
//
//  Revision 1.22  2004/07/14 22:24:53  chostetter_cvs
//  More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//
//  Revision 1.21  2004/07/14 00:33:49  chostetter_cvs
//  More Algorithm, data testing. Fixed slice bug.
//
//  Revision 1.20  2004/07/13 18:52:50  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.19  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.18  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.17  2004/07/09 22:29:11  chostetter_cvs
//  Extensive testing of Input/Output interaction, supports simple BasisRequests
//
//  Revision 1.16  2004/07/06 21:57:12  chostetter_cvs
//  More BasisRequester, DataRequester work
//
//  Revision 1.15  2004/07/06 11:55:26  chostetter_cvs
//  Check-in after weekend
//
//  Revision 1.14  2004/06/30 20:56:20  chostetter_cvs
//  BasisSet is now an interface
//
