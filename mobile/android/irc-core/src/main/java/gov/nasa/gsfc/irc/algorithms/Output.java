//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/algorithms/Output.java,v 1.22 2006/06/02 19:20:04 smaher_cvs Exp $
//
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

package gov.nasa.gsfc.irc.algorithms;

import gov.nasa.gsfc.irc.components.IrcComponent;
import gov.nasa.gsfc.irc.components.ManagedComponent;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.HasBasisBundles;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;


/**
 *  An Output is a Component that manages the creation and production of 
 *  BasisBundles within the Dataspace.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/06/02 19:20:04 $
 *  @author	Carl F. Hostetter
**/

public interface Output extends ManagedComponent, IrcComponent, HasBasisBundles, 
	BasisBundleSource
{
	public static final String DEFAULT_NAME = "Output";
	
	/**
	 * Causes this Output to create a new BasisBundle as described in 
	 * the given BasisBundleDescriptor, and then to add the new BasisBundle 
	 * to the current DataSpace.
	 * 
	 * @param descriptor A BasisBundleDescriptor describing the requested 
	 * 		new BasisBundle
	 * @return The BasisBundleId of the new BasisBundle
	 */
	
	public BasisBundleId addBasisBundle(BasisBundleDescriptor descriptor);
	
	
	/**
	 * Causes this Output to create a new BasisBundle of the given capacity 
	 * and as described in the given BasisBundleDescriptor, and then to add 
	 * the new BasisBundle to the current DataSpace.
	 * 
	 * @param descriptor A BasisBundleDescriptor describing the requested 
	 * 		new BasisBundle
	 * @param capacity The capacity of the new BasisBundle
	 * @return The BasisBundleId of the new BasisBundle
	 */
	
	public BasisBundleId addBasisBundle
		(BasisBundleDescriptor descriptor, int capacity);
	
	
	/**
	 * Causes this Output to create a new BasisBundle of the given capacity 
	 * and as described in the given BasisBundleDescriptor, and then to add 
	 * the new BasisBundle to the current DataSpace.
	 * 
	 * @param descriptor A BasisBundleDescriptor described the requested 
	 * 		new BasisBundle
	 * @param source the BasisBundleSource to use for this BasisBundle
	 * @param capacity The capacity of the new BasisBundle
	 * @return The BasisBundleId of the new BasisBundle
	 */
	
	public BasisBundleId addBasisBundle
		(BasisBundleDescriptor descriptor, BasisBundleSource source, int capacity);

	/**
	 * Sets the declared uniform sample interval of the BasidBundle (and thus of 
	 * all BasisSets subsequently allocated from the BasisBundle) indicated 
	 * by the given BasisBundleId to the given sample interval, in terms of 
	 * the basis units of the BasisBundle. It is the responsibility of the 
	 * caller to guarantee that basis values are written into subsequently 
	 * allocated BasisSets of the indicated BasisBundle with the corresponding 
	 * uniform spacing. To subsequently declare that the BasisBundle is not 
	 * uniformly sampled (the default assumption), set the interval to 0 or to 
	 * Double.NaN.
	 * 
	 * @param basisBundleId The BasisBundleId of the indicated BasisBundle
	 * @param The declared uniform sample interval, in terms of the basis units 
	 * 		of the indicated BasisBundle
	 */
	
	public void setUniformSampleInterval(BasisBundleId basisBundleId, double sampleInterval);
	
	
	/**
	 * Returns the current number of listeners to the BasisBundle 
	 * indicated by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the desired BasisBundle
	 * @return The current number of listeners to the BasisBundle 
	 * 		indicated by the given BasisBundleId
	 */
	
	public int getNumListeners(BasisBundleId basisBundleId);
	
	
	/**
	 * Blocks on the the BasisBundle of this Output indicated by the given 
	 * BasisBundleId until it has at least one BasisBundleListener.
	 * 
	 * @param basisBundleId The BasisBundleId of some BasisBundle of this Output
	 */
	
	public void waitForListeners(BasisBundleId basisBundleId);
	
	
	/**
	 * Causes this Output to resize the BasisBundle indicated by the given 
	 * BasisBundleId to the given new capacity.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle to resize
	 * @param capacity The new capacity of the indicated BasisBundle
	 */
	
	public void resizeBasisBundle(BasisBundleId basisBundleId, int capacity);
	
	
	/**
	 * Causes this Output to restructure the BasisBundle indicated by the given 
	 * BasisBundleId according to the structure specified by the given 
	 * BasisBundleDescriptor.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle to restructure
	 * @param descriptor A BasisBundleDescriptor describing the desired new 
	 * 		strcuture
	 */
	
	public void restructureBasisBundle(BasisBundleId basisBundleId, 
		BasisBundleDescriptor descriptor);
	
	
	/**
	 * Removes the BasisBundle of this Output that has the given 
	 * BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle to remove
	 */
	
	public void removeBasisBundle(BasisBundleId basisBundleId);
	
	
	/**
	 * Allocates and returns a writeable BasisSet of the indicated size from
	 * the indicated BasisBundle of this Output. 
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which 
	 * 		to allocate the desired writeable BasisSet
	 * @param size The size of the desired BasisSet
	 */
	
	public BasisSet allocateBasisSet(BasisBundleId basisBundleId, int size);
	
	
	/**
	 *  Informs this Output that the next BasisSet it makes available to 
	 *  the BasisBundle indicated by the given BasisBundleId will begin a
	 *  new, coherent sequence of basis values and their correlated data 
	 *  on that BasisBUndle
	 *  
	 *  @param basisBundleId A BasisBundleId of some BasisBundle of this 
	 * 		Output
	 */
	
	public void startNewBasisSequence(BasisBundleId basisBundleId);
	
	
	/**
	 * Outputs the given writeable BasisSet, which was previously allocated 
	 * from some BasisBundle of this Output and filled with current output 
	 * data. This results in the BasisSet being closed from any further 
	 * writing and in making it available for reading by any interested 
	 * readers.
	 * 
	 * @param basisSet An open, writeable BasisSet previously allocated from 
	 * 		some BasisBundle of this Output
	 * @param numValidSamples The number of valid samples written into the 
	 * 		given BasisSet, which must be provided if said number is less 
	 * 		than the full capacity of the given BasisSet
	 */
	
	public void makeAvailable(BasisSet basisSet, int numValidSamples);
	

	/**
	 * Outputs the given writeable BasisSet, which was previously allocated 
	 * from some BasisBundle of this Output and filled with current output 
	 * data. This results in the BasisSet being closed from any further 
	 * writing and in making it available for reading by any interested 
	 * readers.
	 * 
	 * @param basisSet An open, writeable BasisSet previously allocated from 
	 * 		some BasisBundle of this Output
	 */
	
	public void makeAvailable(BasisSet basisSet);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: Output.java,v $
//	Revision 1.22  2006/06/02 19:20:04  smaher_cvs
//	Name change: UniformSampleRate->UniformSampleInterval
//	
//	Revision 1.21  2006/03/20 16:43:26  tames
//	Added an addBasisBundle(BasisBundleDescriptor, BasisBundleSource, int)
//	method so that users can designate an other source for a BasisBundle.
//	
//	Revision 1.20  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.19  2004/11/28 17:06:17  tames
//	Updated to reflect change in the ManagedComponent interface.
//	
//	Revision 1.18  2004/09/02 19:39:57  chostetter_cvs
//	Initial data-description redesign work
//	
//	Revision 1.17  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.16  2004/07/17 01:25:58  chostetter_cvs
//	Refactored test algorithms
//	
//	Revision 1.15  2004/07/16 21:35:24  chostetter_cvs
//	Work on declaring uniform sample rate of data
//	
//	Revision 1.14  2004/07/16 00:23:20  chostetter_cvs
//	Refactoring of DataSpace, Output wrt BasisBundle collections
//	
//	Revision 1.13  2004/07/15 19:07:47  chostetter_cvs
//	Added ability to block while waiting for a BasisBundle to have listeners
//	
//	Revision 1.12  2004/07/14 03:43:56  chostetter_cvs
//	Added ability to detect number of data listeners, stop when goes to 0
//	
//	Revision 1.11  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.10  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.9  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.8  2004/07/08 20:26:17  chostetter_cvs
//	BasisSet allocation, blocking changes
//	
//	Revision 1.7  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.6  2004/06/30 20:56:20  chostetter_cvs
//	BasisSet is now an interface
//	
//	Revision 1.5  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.4  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.3  2004/06/09 03:28:49  chostetter_cvs
//	Output-related modifications
//	
//	Revision 1.2  2004/06/08 14:21:53  chostetter_cvs
//	Added child/parent relationship to Components
//	
//	Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//	
