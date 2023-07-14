//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/BasisBundle.java,v 1.40 2006/08/01 19:55:47 chostetter_cvs Exp $
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

import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.types.namespaces.MemberBean;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleListener;
import gov.nasa.gsfc.irc.data.events.BasisSetListener;


/**
 * A BasisBundle is a set of DataBuffers all sharing a single, common 
 * BasisBuffer.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public interface BasisBundle extends MemberBean
{
	/**
	 * Sets the BasisBundleDescriptor that describes the structure of this
	 * BasisBundle to the given BasisBundleDescriptor. In turn, this BasisBundle
	 * will subsequently create BasisSets having the new structure.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param descriptor The BasisBundleDescriptor that describes the desired
	 *            structure of this BasisBundle
	 */	
	public void setDescriptor(BasisBundleDescriptor descriptor);
		
	/**
	 * Returns the BasisBundleDescriptor that describes the structure of this
	 * BasisBundle.
	 * 
	 * @return The BasisBundleDescriptor that describes the structure of this
	 *         BasisBundle
	 */	
	public BasisBundleDescriptor getDescriptor();
	
	/**
	 * Returns the unique BasisBundleId for this BasisBundle.
	 * 
	 * @return The unique BasisBundleId for this BasisBundle
	 */	
	public BasisBundleId getBasisBundleId();
		
	/**
	 * Returns the MemberId of the BasisBundleSource that
	 * created/owns/is writing data to this BasisBundle.
	 * 
	 * @return The MemberId of the BasisBundleSource that
	 *         created/owns/is writing data to this BasisBundle
	 */
	public MemberId getBasisBundleSourceId();

	/**
	 * Returns the DataBufferDescriptor that describes the BasisBuffer of this
	 * BasisBundle.
	 * 
	 * @return The DataBufferDescriptor that describes the BasisBuffer of this
	 *         BasisBundle
	 */
	public DataBufferDescriptor getBasisBufferDescriptor();

	/**
	 * Returns the Set of DataBufferDescriptors that describe the DataBuffers of
	 * this BasisBundle.
	 * 
	 * @return The Set of DataBufferDescriptors that describe the DataBuffers of
	 *         this BasisBundle
	 */
	public Set getDataBufferDescriptors();

	/**
	 * Returns the Set of names of the DataBuffers of this BasisBundle. Note
	 * that if a DataBuffer has a Pixel, the Pixel tag is part of its name.
	 * 
	 * @return The Set of names of the DataBuffers of this BasisBundle
	 */
	public Set getDataBufferNames();

	/**
	 * Returns the Set of names of the DataBuffers of this BasisBundle that
	 * match the given regular expression. Note that if a DataBuffer has a
	 * Pixel, the Pixel tag is part of its name.
	 * 
	 * @param A regular expression
	 * @return The Set of names of the DataBuffers of this BasisBundle that
	 *         match the given regular expression
	 */
	public Set getDataBufferNames(String regEx);

	/**
	 * Returns the ordered Map of the DataBufferDescriptors (by name) of this
	 * BasisBundle whose names match the given regular expression.
	 * 
	 * @param regEx A regular expression
	 * @return The ordered Map of the DataBufferDescriptors (by name) of this
	 *         BasisBundle whose names match the given regular expression
	 */
	public Map getDataBufferDescriptors(String regEx);

	/**
	 * Returns true if this BasisBundle has been declared to be uniformly
	 * sampled, false otherwise.
	 * 
	 * @return True if this BasisSet has been declared to be uniformly sampled,
	 *         false otherwise
	 */
	public boolean isUniformlySampled();

	/**
	 * Sets the declared uniform sample interval of the data in all BasisSets
	 * subsequently allocated from this BasisBundle to the given value, in terms
	 * of the units of its basis buffer. It is up to the caller to guarantee
	 * that the given interval is accurate, and that all basis values in subsequent
	 * BasisSets are in fact uniformly spaced at the declared interval. To indicate
	 * that the BasisBundle is not uniformly sampled (the default assumption),
	 * set the value to 0 or less, or to Double.Nan.
	 * 
	 * @param sampleInterval The declared uniform sample interval of the data in alll
	 *            BasisSets subsequently allocated from this BasisBundle to the
	 *            given value, in terms of the units of its basis buffer
	 */
	public void setUniformSampleInterval(double sampleInterval);

	/**
	 * Sets the declared uniform sample interval of the data in all BasisSets
	 * subsequently allocated from this BasisBundle to the difference of the
	 * first two samples in the basis buffer of this BasisSet.
	 * 
	 * @throws IllegalStateException
	 *             if less than two samples are available in this BasisSet
	 */
	public void setUniformSampleIntervalImplicitly();
	
	/**
	 * Returns the declared uniform sample interval of the data in all BasisSets
	 * allocated from this BasisBundle, in terms of the Units of the basis
	 * buffer. If this BasisBundle is not uniformly sampled, the result will be
	 * Double.Nan.
	 * 
	 * @return The declared uniform sample interval of the data in all BasisSets
	 *         allocated from this BasisBundle, in terms of the Units of the
	 *         basis buffer
	 */
	public double getUniformSampleInterval();

	/**
	 * Sets the base or epoch that all future BasisBuffer values are relative 
	 * to in this BasisBundle.
	 * 
	 * @param base The base or epoch of future BasisBuffers.
	 */
	public void setBasisBase(double base);

	/**
	 * Returns the base or epoch that all future BasisBuffer values are relative 
	 * to in this BasisBundle.
	 * 
	 * @return The base or epoch of the BasisBuffer.
	 */
	public double getBasisBase();

	/**
	 * Returns the current size of this BasisBundle (i.e., the uniform size of
	 * each of its Buffers).
	 * 
	 * @return The current size of this BasisBundle
	 */
	public int getSize();

	/**
	 * Resizes this BasisBundle to the given new size. This method should only
	 * be called by the creator/owner of this BasisBundle, and only if
	 * necessary.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param The desired new size of this BasisBundle
	 */
	public void resize(int size);

	/**
	 * Clears each of the values in each of the DataBuffers of this BasisBundle 
	 * (including the BasisBuffer) to an appropriate value for its type.
	 * 
	 * @return This BasisBundle
	 */
	public BasisBundle clear()
		throws UnsupportedOperationException;

	/**
	 * Allocates and returns a new, writeable BasisSet of the given size.
	 * <p>
	 * If the requested amount of free space is currently unavailable, this
	 * method may block until the size of the requested allocation can be
	 * accomodated.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param size The size of the desired BasisSet
	 * @return A new, writeable BasisSet of the indicated size
	 */
	public BasisSet allocateBasisSet(int numSamples);

	/**
	 * Makes the data contained in the given BasisSet (which must have been
	 * previously allocated from this BasisBundle) available for reading. 
	 * There may be a performance or memory overhead cost if the number 
	 * of valid samples to make available is less then the size of the 
	 * given BasisSet. See the documentation for the implementing class.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param basisSet A BasisSet of newly-available data. The BasisSet must
	 *            have been previously allocated from this BasisBundle
	 * @param numValidSamples The number of valid data samples in the given
	 *            BasisSet. This must be provided in the event that the number
	 *            is less than the size of the BasisSet as requested on
	 *            allocation
	 * @throws IllegalArgumentException if the given BasisSet was not allocated
	 *             from this BasisBundle, or if this BasisBundle has been
	 *             resized subsequent to its allocation
	 */
	public void makeAvailable(BasisSet basisSet, int numValidSamples)
			throws IllegalArgumentException;

	/**
	 * Makes the data contained in the given BasisSet (which must have been
	 * previously allocated from this BasisBundle) available for reading.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 * 
	 * @param basisSet A BasisSet of newly-available data. The BasisSet must
	 *            have been previously allocated from this BasisBundle
	 * @throws IllegalArgumentException if the given BasisSet was not allocated
	 *             from this BasisBundle, or if this BasisBundle has been
	 *             resized subsequent to its allocation
	 */
	public void makeAvailable(BasisSet basisSet)
			throws IllegalArgumentException;

	/**
	 * Informs this BasisBundle that the next BasisSet its BasisBundleSource
	 * makes available to it will begin a new, coherent sequence of basis values
	 * and their correlated data.
	 * <p>
	 * This method should only be called by the BasisBundleSource that is
	 * writing data into this BasisBundle.
	 */
	public void startNewBasisSequence();

	/**
	 * Returns a read-only BasisSet of the indicated size from within this
	 * BasisBundle, spanning the given range of positions in the BasisBundle.
	 * 
	 * @param startPosition The start position of the desired range
	 * @param startPosition The end position of the desired range
	 * @return A read-only BasisSet of the indicated size from within this
	 *         BasisBundle, spanning the given range of positions in this
	 *         BasisBundle
	 */
	public BasisSet slice(int offset, int length);

	/**
	 * Resolves the given index relative to this BasisBundle.
	 * 
	 * @returns an absolute index into the BasisBundle.
	 */
	public int resolveIndex(int index);

	/**
	 * Adds the given BasisBundleLisener to this BasisBundle as a listener for
	 * BasisBundleEvents.
	 * 
	 * @param listener A BasisBundleLisener
	 */
	public void addBasisBundleListener(BasisBundleListener listener);

	/**
	 * Removes the given BasisBundleLisener from this BasisBundle as a listener
	 * for BasisBundleEvents.
	 * 
	 * @param listener A BasisBundleLisener
	 */

	public void removeBasisBundleListener(BasisBundleListener listener);

	/**
	 * Returns the BasisBundleEvent listeners on this BasisBundle as an array of
	 * BasisBundleListeners.
	 * 
	 * @return The BasisBundleEvent listeners on this BasisBundle as an array of
	 *         BasisBundleListeners
	 */
	public BasisBundleListener[] getBasisBundleListeners();

	/**
	 * Adds the given BasisSetLisener to this BasisBundle as a listener for
	 * BasisSetEvents.
	 * 
	 * @param listener A BasisSetListener
	 */
	public void addBasisSetListener(BasisSetListener listener);

	/**
	 * Removes the given BasisSetLisener from this BasisBundle as a listener for
	 * BasisSetEvent.
	 * 
	 * @param listener A BasisSetListener
	 */

	public void removeBasisSetListener(BasisSetListener listener);

	/**
	 * Returns the BasisSetEvent listeners on this BasisBundle as an array of
	 * BasisSetListeners.
	 * 
	 * @return The BasisSetEvent listeners on this BasisBundle as an array of
	 *         BasisSetListener
	 */
	public BasisSetListener[] getBasisSetListeners();

	/**
	 * Returns the current number of listeners to this BasisBundle
	 * 
	 * @return The current number of listeners to this BasisBundle
	 */
	public int getNumListeners();

	/**
	 * Blocks on this BasisBundle until it has at least one BasisBundleListener.
	 */
	public void waitForListeners();

	/**
	 * Returns a String representation of the specified range of data in this
	 * BasisBundle, starting with the BasisBuffer and across each DataBuffer.
	 * 
	 * @param startPosition The start of the data range
	 * @param endPosition The end of the data range
	 * @return A String representation of the specified range of data in this
	 *         BasisBundle
	 */
	public String dataToString(int startPosition, int endPosition);

	/**
	 * Returns a String representation of the specified amount of data in this
	 * BasisBundle, starting with the BasisBuffer and across each DataBuffer,
	 * and starting at the beginning of each Buffer.
	 * 
	 * @param amount The amount of data
	 * @return A String representation of the specified amount of data in this
	 *         BasisBundle, starting with the BasisBuffer and across each
	 *         DataBuffer, and starting at the beginning of each Buffer
	 */
	public String dataToString(int amount);

	/**
	 * Returns a String representation of the data in this BasisBundle, starting
	 * with the BasisBuffer and across each DataBuffer.
	 * <p>
	 * Note that for a typical BasisBundle this could produce a <em>very</em>
	 * long String!
	 * 
	 * @return A String representation of the data in this BasisBundle
	 */
	public String dataToString();
}

// --- Development History ---------------------------------------------------
//
// $Log: BasisBundle.java,v $
// Revision 1.40  2006/08/01 19:55:47  chostetter_cvs
// Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
// Revision 1.39  2006/05/31 13:05:21  smaher_cvs
// Name change: UniformSampleRate->UniformSampleInterval
//
// Revision 1.38  2006/02/07 18:09:25  chostetter_cvs
// Added clear() method
//
// Revision 1.37  2006/01/23 17:59:51  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.36  2005/12/05 04:13:50  tames
// Added support for a BasisBuffer base to handle conditions where a double
// does not have enough percision to represent a BasisBuffer value.
//
// Revision 1.35  2005/10/28 19:10:02  tames
// Javadoc change only.
//
// Revision 1.34  2005/09/13 22:28:58  tames
// Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.33  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.32  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.31  2004/11/30 16:53:36  chostetter_cvs
//  Added ability to obtain Set of names that match a given regular expression
//
//  Revision 1.30  2004/11/30 15:50:03  chostetter_cvs
//  Added ability to select a set of DataBuffers by matching names to a given regular expression
//
//  Revision 1.29  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.28  2004/07/21 14:26:15  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.27  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.26  2004/07/16 21:35:24  chostetter_cvs
//  Work on declaring uniform sample rate of data
//
//  Revision 1.25  2004/07/15 19:07:47  chostetter_cvs
//  Added ability to block while waiting for a BasisBundle to have listeners
//
//  Revision 1.24  2004/07/14 17:14:09  chostetter_cvs
//  Added access to set of DataBufferIds
//
//  Revision 1.23  2004/07/14 03:43:56  chostetter_cvs
//  Added ability to detect number of data listeners, stop when goes to 0
//
//  Revision 1.22  2004/07/12 19:04:45  chostetter_cvs
//  Added ability to find BasisBundleId, Components by their fully-qualified name
//
//  Revision 1.21  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.20  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.19  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.18  2004/07/08 20:26:17  chostetter_cvs
//  BasisSet allocation, blocking changes
//
//  Revision 1.17  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.16  2004/06/30 20:56:20  chostetter_cvs
//  BasisSet is now an interface
//
//  Revision 1.15  2004/06/09 03:28:49  chostetter_cvs
//  Output-related modifications
//
//  Revision 1.14  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.13  2004/06/04 23:10:27  chostetter_cvs
//  Added data printing support to various Buffer classes
//
//  Revision 1.12  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
//  Revision 1.11  2004/06/04 17:28:32  chostetter_cvs
//  More data tweaks. Ready for testing.
//
//  Revision 1.10  2004/06/04 05:34:42  chostetter_cvs
//  Further data, Algorithm, and Component work
//
//  Revision 1.9  2004/06/02 23:59:41  chostetter_cvs
//  More Namespace, DataSpace tweaks, created alogirthms package
//
//  Revision 1.8  2004/05/29 04:30:00  chostetter_cvs
//  Further data-related refinements
//
//  Revision 1.7  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.6  2004/05/27 23:09:26  chostetter_cvs
//  More Namespace related changes
//
//  Revision 1.5  2004/05/27 19:47:45  chostetter_cvs
//  More Namespace, DataSpace changes
//
//  Revision 1.4  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.3  2004/05/16 21:54:27  chostetter_cvs
//  More work
//
//  Revision 1.2  2004/05/16 15:44:36  chostetter_cvs
//  Further data-handling definition
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
