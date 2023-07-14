//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/algorithms/DefaultOutput.java,v 1.41 2006/08/01 19:55:48 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultOutput.java,v $
//	Revision 1.41  2006/08/01 19:55:48  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.40  2006/06/02 19:20:04  smaher_cvs
//	Name change: UniformSampleRate->UniformSampleInterval
//	
//	Revision 1.39  2006/05/31 13:05:22  smaher_cvs
//	Name change: UniformSampleRate->UniformSampleInterval
//	
//	Revision 1.38  2006/03/20 16:43:26  tames
//	Added an addBasisBundle(BasisBundleDescriptor, BasisBundleSource, int)
//	method so that users can designate an other source for a BasisBundle.
//	
//	Revision 1.37  2006/03/17 22:51:17  tames_cvs
//	Some simplification of the makeAvailable methods to use BasisSet funtionality.
//	
//	Revision 1.36  2006/03/14 16:13:18  chostetter_cvs
//	Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//	
//	Revision 1.35  2006/03/14 14:57:15  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.34  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.33  2006/01/24 16:19:16  chostetter_cvs
//	Changed default ComponentManager behavior, default is now none
//	
//	Revision 1.32  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.31  2005/09/14 21:53:37  chostetter_cvs
//	Added ability to find BasisBundles by regex pattern matching on names
//	
//	Revision 1.30  2005/09/14 21:31:18  chostetter_cvs
//	Fixed BasisBundle name issue in DataSpace
//	
//	Revision 1.29  2005/09/08 22:18:32  chostetter_cvs
//	Massive Data Transformation-related changes
//	
//	Revision 1.28  2005/04/04 15:40:58  chostetter_cvs
//	Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//	
//	Revision 1.27  2005/02/25 01:05:46  chostetter_cvs
//	REALLY really fixed start/stop behavior problem vis a vis unreleased BasisSets due to Thread interruption
//	
//	Revision 1.26  2005/01/27 21:38:02  chostetter_cvs
//	Implemented new exception state and default exception behavior for Objects having ActivityState
//	
//	Revision 1.25  2004/09/02 19:39:57  chostetter_cvs
//	Initial data-description redesign work
//	
//	Revision 1.24  2004/07/22 20:14:58  chostetter_cvs
//	Data, Component namespace work
//	
//	Revision 1.23  2004/07/22 16:28:03  chostetter_cvs
//	Various tweaks
//	
//	Revision 1.22  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.21  2004/07/17 01:25:58  chostetter_cvs
//	Refactored test algorithms
//	
//	Revision 1.20  2004/07/16 21:35:24  chostetter_cvs
//	Work on declaring uniform sample rate of data
//	
//	Revision 1.19  2004/07/16 00:23:20  chostetter_cvs
//	Refactoring of DataSpace, Output wrt BasisBundle collections
//	
//	Revision 1.18  2004/07/15 19:07:47  chostetter_cvs
//	Added ability to block while waiting for a BasisBundle to have listeners
//	
//	Revision 1.17  2004/07/14 03:43:56  chostetter_cvs
//	Added ability to detect number of data listeners, stop when goes to 0
//	
//	Revision 1.16  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.15  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.14  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.13  2004/07/11 21:24:54  chostetter_cvs
//	Organized imports
//	
//	Revision 1.12  2004/07/11 07:30:35  chostetter_cvs
//	More data request work
//	
//	Revision 1.11  2004/07/08 20:26:17  chostetter_cvs
//	BasisSet allocation, blocking changes
//	
//	Revision 1.10  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.9  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.8  2004/06/30 20:56:20  chostetter_cvs
//	BasisSet is now an interface
//	
//	Revision 1.7  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.6  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Output, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.5  2004/06/09 03:28:49  chostetter_cvs
//	Output-related modifications
//	
//	Revision 1.4  2004/06/08 14:21:53  chostetter_cvs
//	Added child/parent relationship to Components
//	
//	Revision 1.3  2004/06/04 05:34:42  chostetter_cvs
//	Further data, Algorithm, and Component work
//	
//	Revision 1.2  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.2  2004/05/27 23:09:26  chostetter_cvs
//	More Namespace related changes
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//	
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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberSet;
import gov.nasa.gsfc.commons.types.namespaces.MemberSet;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.AbstractManagedComponent;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleFactory;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;


/**
 *  An Output is a Component that manages the creation and production of 
 *  BasisBundles within the Dataspace.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/01 19:55:48 $
 *  @author	Carl F. Hostetter
**/

public class DefaultOutput extends AbstractManagedComponent implements Output 
{
	private static final String CLASS_NAME = DefaultOutput.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final DataSpace sDataSpace = Irc.getDataSpace();
	private static final BasisBundleFactory sBasisBundleFactory = 
		Irc.getBasisBundleFactory();
	
	private MemberSet fBasisBundles = new DefaultMemberSet();
	
	
	/**
	 *	Constructs a new Output having a default name. Note that the new Output 
	 *  will need to have its ComponentManager (typically an Algorithm) set (if any 
	 *  is desired).
	 *
	 */
	
	public DefaultOutput()
	{
		this(Output.DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new Output having the given base name. Note that the new Output 
	 *  will need to have its ComponentManager (typically an Algorithm) set (if any 
	 *  is desired).
	 * 
	 *  @param name The base name of the new Output
	 **/

	public DefaultOutput(String name)
	{
		super(name);
	}

	
	/**
	 *	Constructs a new Output configured according to the given 
	 *  ComponentDescriptor. Note that the new Output will need to have its 
	 *  ComponentManager (typically an Algorithm) set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new Output
	 */
	
	public DefaultOutput(ComponentDescriptor descriptor)
	{
		super(descriptor.getName());
	}
	
	
//----------------------------------------------------------------------
//	BasisBundle-related methods
//----------------------------------------------------------------------
	
	/**
	 *  Returns true if there are currently BasisBundles associated with this 
	 *  Output, false otherwise.
	 *  
	 *  @return	True if there are currently BasisBundles associated with this 
	 *  		Output, false otherwise
	 **/

	public boolean hasBasisBundles()
	{
		return (fBasisBundles.size() > 0);
	}
	
	
	/**
	 *  Returns the current number of BasisBundles associated with this Output.
	 *  
	 *  @return	The current number of BasisBundles associated with this Output
	 **/

	public int getNumBasisBundles()
	{
		return (fBasisBundles.size());
	}
	
	
	/**
	 *  Returns the Set of BasisBundles associated with this Output.
	 *
	 *  @return The Set of BasisBundles associated with this Output
	 */

	public Set getBasisBundles()
	{
		return (Collections.unmodifiableSet(fBasisBundles.getMembers()));
	}


	/**
	 *  Returns the Set of BasisBundles associated with this Output whose 
	 *  fully-qualifed names match the given regular expression pattern.
	 *
	 *  @param regExPattern A regular expression pattern
	 *  @return The Set of BasisBundles associated with this Output whose 
	 *  		fully-qualifed names match the given regular expression pattern
	 */

	public Set getBasisBundles(String regExPattern)
	{
		return (fBasisBundles.getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of fully-qualified names of the BasisBundles associated 
	 *  with this Output.
	 *  
	 *  @return	The Set of fully-qualified names of the BasisBundles associated 
	 * 		with this Output
	 **/

	public Set getBasisBundleNames()
	{
		return (Collections.unmodifiableSet
			(fBasisBundles.getFullyQualifiedNames()));
	}
	

	/**
	 *  Returns the BasisBundle in the Set of BasisBundles associated with this 
	 *  Output that has the given fully-qualified name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified name of a BasisBundle 
	 *  		associated with this Output
	 *  @return The BasisBundle in the Set of BasisBundles associated with this 
	 *  		Output that has the given fully-qualified name
	 */

	public BasisBundle getBasisBundle(String fullyQualifiedName)
	{
		return ((BasisBundle) fBasisBundles.get(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the BasisBundleId of the BasisBundle associated with this Output 
	 *  (if any) that has the given fully-qualified name (i.e., of the form
	 *  "<BasisBundle name> of <output name> of <algorithm name>". If no such 
	 *  BasisBundle is associated with this Output, the result will be null.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the 
	 * 		desired BasisBundle (i.e., of the form 
	 * 		"<BasisBundle name> of <output name> of <algorithm name>"
	 *  @return The BasisBundleId of the BasisBundle associated with this 
	 * 		Output (if any) that has the given fully-qualified name
	 */
	
	public BasisBundleId getBasisBundleId(String fullyQualifiedName)
	{
		return ((BasisBundleId) fBasisBundles.getMemberId(fullyQualifiedName));
	}
	
	
	
	/**
	 *  Returns the BasisBundle associated with this Output (if any) that has 
	 *  the given BasisBundleId.
	 * 
	 *  @param basisBundleId The BasisBundleId of the desired BasisBundle
	 *  @return The BasisBundle associated with this Output (if any) that has 
	 * 		the given basisBundleId
	 */
	
	public BasisBundle getBasisBundle(BasisBundleId basisBundleId)
	{
		return ((BasisBundle) fBasisBundles.get(basisBundleId));
	}
	
	
	/**
	 * Causes this Output to create a new BasisBundle as described in 
	 * the given BasisBundleDescriptor, and then to add the new BasisBundle 
	 * to the current DataSpace.
	 * 
	 * @param descriptor A BasisBundleDescriptor describing the requested 
	 * 		new BasisBundle
	 * @return The BasisBundleId of the new BasisBundle
	 */
	
	public BasisBundleId addBasisBundle(BasisBundleDescriptor descriptor)
	{
		return (addBasisBundle(descriptor, this, 0));
	}
	
	
	/**
	 * Causes this Output to create a new BasisBundle of the given capacity 
	 * and as described in the given BasisBundleDescriptor, and then to add 
	 * the new BasisBundle to the current DataSpace.
	 * 
	 * @param descriptor A BasisBundleDescriptor described the requested 
	 * 		new BasisBundle
	 * @param capacity The capacity of the new BasisBundle
	 * @return The BasisBundleId of the new BasisBundle
	 */
	
	public BasisBundleId addBasisBundle
		(BasisBundleDescriptor descriptor, int capacity)
	{
		return (addBasisBundle(descriptor, this, capacity));
	}
	
	
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
		(BasisBundleDescriptor descriptor, BasisBundleSource source, int capacity)
	{
		BasisBundleId basisBundleId = null;
		
		try
		{
			BasisBundle basisBundle = sBasisBundleFactory.
				createBasisBundle(descriptor, source, capacity);
			
			sDataSpace.addBasisBundle(basisBundle);
			
			basisBundleId = basisBundle.getBasisBundleId();
			
			fBasisBundles.add(basisBundle);
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
		
		return (basisBundleId);
	}
	
	/**
	 * Returns the current number of listeners to the BasisBundle indicated by the 
	 * given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the desired BasisBundle
	 * @return The current number of listeners to the BasisBundle 
	 * 		indicated by the given BasisBundleId
	 */
	
	public int getNumListeners(BasisBundleId basisBundleId)
	{
		BasisBundle basisBundle = sDataSpace.getBasisBundle(basisBundleId);
		
		return (basisBundle.getNumListeners());
	}
	
	
	/**
	 * Blocks on the the BasisBundle of this Output indicated by the given 
	 * BasisBundleId until it has at least one BasisBundleListener.
	 * 
	 * @param basisBundleId The BasisBundleId of some BasisBundle of this Output
	 */
	
	public void waitForListeners(BasisBundleId basisBundleId)
	{
		try
		{
			BasisBundle basisBundle = sDataSpace.getBasisBundle(basisBundleId);
			
			basisBundle.waitForListeners();
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}
	
	
	/**
	 * Causes this Output to resize the BasisBundle indicated by the given 
	 * BasisBundleId to the given new capacity.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle to resize
	 * @param capacity The new capacity of the indicated BasisBundle
	 */
	
	public void resizeBasisBundle(BasisBundleId basisBundleId, int capacity)
	{
		try
		{
			BasisBundle basisBundle = (BasisBundle)
				fBasisBundles.get(basisBundleId);
			
			basisBundle.resize(capacity);
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}
	
	
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
		BasisBundleDescriptor descriptor)
	{
		try
		{
			BasisBundle basisBundle = (BasisBundle)
				fBasisBundles.get(basisBundleId);
		
			basisBundle.setDescriptor(descriptor);
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}
	
	
	/**
	 * Removes the BasisBundle of this Output that has the given 
	 * BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle to remove
	 */
	
	public void removeBasisBundle(BasisBundleId basisBundleId)
	{
		sDataSpace.removeBasisBundle(basisBundleId);
		
		fBasisBundles.remove(basisBundleId);
	}
	
	
	/**
	 * Sets the declared uniform sample interval of the BasidBundle (and thus of 
	 * all BasisSets subsequently allocated from the BasisBundle) indicated 
	 * by the given BasisBundleId to the given sample interval, in terms of 
	 * the basis units of the BasisBundle. It is the responsibility of the 
	 * caller to guarantee that basis values are written into subsequently 
	 * allocated BasisSets of the indicated BasisBundle with the corresponding 
	 * uniform spacing. To subsequently declare that the BasisBundle is not 
	 * uniformly sampled (the default assumption), set the rate to 0 or to 
	 * Double.NaN.
	 * 
	 * @param basisBundleId The BasisBundleId of the indicated BasisBundle
	 * @param The declared uniform sample interval, in terms of the basis units 
	 * 		of the indicated BasisBundle
	 */
	
	public void setUniformSampleInterval(BasisBundleId basisBundleId, 
		double sampleInterval)
	{
		BasisBundle basisBundle = getBasisBundle(basisBundleId);
		
		if (basisBundle != null)
		{
			basisBundle.setUniformSampleInterval(sampleInterval);
		}
	}
	
	
	/**
	 * Allocates and returns a writeable BasisSet of the indicated size from
	 * the indicated BasisBundle of this Output. 
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which 
	 * 		to allocate the desired writeable BasisSet
	 * @param size The size of the desired BasisSet
	 */
	
	public BasisSet allocateBasisSet(BasisBundleId basisBundleId, int size)
	{
		BasisSet basisSet = null;
		
		try
		{
			BasisBundle basisBundle = getBasisBundle(basisBundleId);
			
			if (basisBundle != null)
			{
				basisSet = basisBundle.allocateBasisSet(size);
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
		
		return (basisSet);
	}
	
	
	/**
	 *  Informs this Output that the next BasisSet it makes available to 
	 *  the BasisBundle indicated by the given BasisBundleId will begin a
	 *  new, coherent sequence of basis values and their correlated data 
	 *  on that BasisBUndle
	 *  
	 *  @param basisBundleId A BasisBundleId of some BasisBundle of this 
	 * 		Output
	 */
	
	public void startNewBasisSequence(BasisBundleId basisBundleId)
	{
		try
		{
			BasisBundle basisBundle = getBasisBundle(basisBundleId);
			
			if (basisBundle != null)
			{
				basisBundle.startNewBasisSequence();
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}
	
	
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
	
	public void makeAvailable(BasisSet basisSet, int numValidSamples)
	{
		try
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = getFullyQualifiedName() + 
					" making BasisSet available:\n" + basisSet;
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"makeAvailable", message);
			}
			
			if (basisSet != null)
			{
				basisSet.makeAvailable(numValidSamples);
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}
	

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
	
	public void makeAvailable(BasisSet basisSet)
	{
		if (basisSet != null)
		{
			basisSet.makeAvailable();
		}
	}
	
	
	/**
	 *  Causes this Output to immediately cease operation and release any 
	 *  allocated resources, including its BasisBundles. A killed Ouput cannot 
	 *  subsequently be started or otherwise reused.
	 * 
	 */
	
	public void kill()
	{
		super.kill();
		
		Iterator basisBundles = fBasisBundles.iterator();
		
		while (basisBundles.hasNext())
		{
			BasisBundle basisBundle = (BasisBundle) basisBundles.next();
			
			sDataSpace.remove(basisBundle);
		}
		
		fBasisBundles.clear();
		fBasisBundles = null;
	}
}
