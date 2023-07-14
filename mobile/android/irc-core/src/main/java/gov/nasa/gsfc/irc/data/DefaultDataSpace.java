//=== File Prolog ============================================================
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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.maps.SetValueMap;
import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberSetBean;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.commons.types.namespaces.NamespaceMember;
import gov.nasa.gsfc.irc.data.events.DataSpaceEvent;
import gov.nasa.gsfc.irc.data.events.DataSpaceListener;


/**
 *  A DataSpace is a named container and manager of a Set of BasisBundles.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/01 19:55:47 $
 *  @author Carl F. Hostetter
**/

public class DefaultDataSpace extends DefaultMemberSetBean implements DataSpace
{
	private static final String CLASS_NAME = DefaultDataSpace.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private SetValueMap fBasisBundlesByBasisBundleSourceIdValueMap = 
		new SetValueMap();
	
	private Map fBasisBundlesByBasisBundleSourceId = 
		Collections.synchronizedMap(fBasisBundlesByBasisBundleSourceIdValueMap);
	
	
	/**
	 *	Default constructor of a DataSpace.
	 *
	 */
	
	public DefaultDataSpace()
	{
		this("DataSpace");
	}
	
	
	/**
	 *	Constructs a new DataSpace having the given name.
	 * 
	 *  @param name The name of the new DataSpace
	 *  @return A new DataSpace having the given name
	 */
	
	public DefaultDataSpace(String name)
	{
		super(name);
	}
	
	
	/**
	 *  Returns true if the given BasisBundle is in this DataSpace, false otherwise.
	 *  
	 *  @param basisBundle A BasisBundle
	 *  @return	True if the given BasisBundle is in this DataSpace, false otherwise
	 **/

	public boolean contains(BasisBundle basisBundle)
	{
		return (super.contains(basisBundle));
	}
	
	
	/**
	 *  Adds the given NamespaceMember, which must be a BasisBundle, to this DataSpace. 
	 * 
	 *  @param basisBundle A BasisBundle
	 *  @return True if the given NamespaceMember was actually added
	 */
	
	public boolean add(NamespaceMember basisBundle)
	{
		boolean result = false;
		
		if (basisBundle instanceof BasisBundle)
		{
			addBasisBundle((BasisBundle) basisBundle);
			
			result = true;
		}
		else
		{
			
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given BasisBundle to this DataSpace.
	 * 
	 *  @param basisBundle A BasisBundle
	 *  @return True if the given BasisBundle was actually added
	 */
	
	public void addBasisBundle(BasisBundle basisBundle)
	{
		boolean result = super.add(basisBundle);
		
		if (result == true)
		{
			DataSpaceEvent event = new DataSpaceEvent(this, basisBundle, true);
			
			fireMembershipEvent(event);
			
			basisBundle.addFullyQualifiedNameListener(this);
			String fullyQualifiedName = basisBundle.getFullyQualifiedName();
			
			fBasisBundlesByBasisBundleSourceId.put
				(basisBundle.getBasisBundleSourceId(), basisBundle);
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = 
					"Added new BasisBundle (" + fullyQualifiedName + ") " + 
					basisBundle.getFullyQualifiedName() + "\n with capacity = " +
					basisBundle.getSize();
				
				sLogger.logp(Level.FINE, CLASS_NAME, "addBasisBundle", message);
			}
		}
	}
	

	/**
	 *  Returns true if this DataSpace currently has BasisBundles, false 
	 *  otherwise.
	 *  
	 *  @return	True if this DataSpace currently has BasisBundles, false
	 *  		otherwise
	 **/

	public boolean hasBasisBundles()
	{
		return (! isEmpty());
	}


	/**
	 *  Returns the current number of BasisBundles that belong to this 
	 *  BasisBundleNamespace.
	 *  
	 *  @return	The current number of BasisBundles that belong to this 
	 * 		BasisBundleNamespace
	 **/

	public int getNumBasisBundles()
	{
		return (size());
	}
	
	
	/**
	 *  Returns the Set of BasisBundles of this DataSpace.
	 * 
	 *  @return The Set of BasisBundles of this DataSpace
	 */
	
	public Set getBasisBundles()
	{
		return (super.getMembers());
	}
	

	/**
	 *  Returns the Set of BasisBundles of this DataSpace whose fully-qualifed names 
	 *  match the given regular-expression.
	 *
	 *  @param regExPattern A regular expression
	 *  @return The Set of BasisBundles of this DataSpace whose fully-qualifed names 
	 *  		match the given regular-expression
	 */

	public Set getBasisBundles(String regExPattern)
	{
		return (super.getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of fully-qualified names of the BasisBundles of this 
	 *  DataSpace.
	 *  
	 *  @return	The Set of fully-qualified names of the BasisBundles of this 
	 *  		DataSpace
	 **/

	public Set getBasisBundleNames()
	{
		return (super.getFullyQualifiedNames());
	}
	
	
	/**
	 *  Returns the BasisBundle in the Set of BasisBundles associated with this 
	 *  DataSpace that has the given fully-qualified name (i.e., of the form
	 *  "<BasisBundle name> of <output name> of <algorithm name>". If no such 
	 *  BasisBundle is associated with this DataSpace, the result will be null.
	 *
	 *  @param fullyQualifiedName The fully-qualified name of the desired 
	 *  		BasisBundle (i.e., of the form "<BasisBundle name> of <Output name> of 
	 *  		<Algorithm name>"
	 *  @return The BasisBundle in the Set of BasisBundles associated with this 
	 *  		DataSpace that has the given fully-qualified name
	 */
	
	public BasisBundle getBasisBundle(String fullyQualifiedName)
	{
		return ((BasisBundle) super.get(fullyQualifiedName));
	}
	

	/**
	 *  Returns the BasisBundleId of the BasisBundle associated with this DataSpace 
	 *  (if any) that has the given fully-qualified name (i.e., of the form
	 *  "<BasisBundle name> from <output name> of <algorithm name>". If no such 
	 *  BasisBundle is associated with this DataSpace, the result will be null.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the 
	 * 		desired BasisBundle (i.e., of the form 
	 * 		"<BasisBundle name> from <output name> of <algorithm name>"
	 *  @return The BasisBundleId of the BasisBundle associated with this 
	 * 		DataSpace (if any) that has the given fully-qualified name
	 */
	
	public BasisBundleId getBasisBundleId(String fullyQualifiedName)
	{
		return ((DefaultBasisBundleId) super.getMemberId(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the BasisBundle in this DataSpace (if any) that has the 
	 *  given BasisBundleId.
	 * 
	 *  @param basisBundleId The BasisBundleId of the desired BasisBundle
	 *  @return The BasisBundle in this DataSpace (if any) that has the 
	 * 		given basisBundleId
	 */
	
	public BasisBundle getBasisBundle(BasisBundleId basisBundleId)
	{
		return ((BasisBundle) super.get(basisBundleId));
	}
	

	/**
	 *  Returns the Collection of BasisBundles in this DataSpace (if any) 
	 *  that are owned by the BasisBundleSource having the given MemberId.
	 * 
	 *  @param sourceId A BasisBundleSourceId
	 *  @return The Collection of BasisBundles in this DataSpace (if any) 
	 * 		that are owned by the BasisBundleSource having the given MemberId
	 */
	
	public Collection getBasisBundles(MemberId sourceId)
	{
		Collection result = (Collection) 
			fBasisBundlesByBasisBundleSourceId.get(sourceId);
		
		return (result);
	}
	

	/**
	 *  Returns the Collection of BasisBundles of this DataSpace (if any) 
	 *  that are owned by the given BasisBundleSource.
	 * 
	 *  @param source A BasisBundleSource
	 *  @return The Collection of BasisBundles of this DataSpace (if any) 
	 *  		that are owned by the given BasisBundleSource
	 */
	
	public Collection getBasisBundles(BasisBundleSource source)
	{
		return (getBasisBundles(source.getMemberId()));
	}
	

	/**
	 *  Removes the given BasisBundle from this DataSpace.
	 * 
	 *  @param basisBundle The BasisBundle to remove
	 */
	
	public void remove(BasisBundle basisBundle)
	{
		boolean removed = super.remove(basisBundle);
		
		if (removed)
		{
			basisBundle.removeFullyQualifiedNameListener(this);
			DataSpaceEvent event = new DataSpaceEvent(this, basisBundle, false);
			
			fireMembershipEvent(event);
			
			fBasisBundlesByBasisBundleSourceIdValueMap.remove
				(basisBundle.getBasisBundleSourceId(), basisBundle);
		}
	}
	

	/**
	 *  Removes the BasisBundle having the given BasisBundleId from 
	 *  this DataSpace.
	 * 
	 *  @param basisBundleId The BasisBundleId of the BasisBundle to 
	 * 		remove
	 */
	
	public void removeBasisBundle(BasisBundleId basisBundleId)
	{
		BasisBundle basisBundle = this.getBasisBundle(basisBundleId);
		
		if (basisBundle != null)
		{
			remove(basisBundle);
		}
	}
	

	/**
	 *  Removes all of the BasisBundles that are owned by the BasisBundleSource 
	 *  having the given MemberId from this DataSpace.
	 * 
	 *  @param sourceId The MemberId of some BasisBundleSource
	 */
	
	public void removeBasisBundles(MemberId sourceId)
	{
		synchronized (fBasisBundlesByBasisBundleSourceId)
		{

			// To be able to put common functionality in remove()
			// and to avoid a ConcurrentModificationException,
			// build up our own copy of the the BasisBundle
			// list for the sourceId, then call remove.
						
			Iterator bundlesToRemoveI = (Iterator) 
			fBasisBundlesByBasisBundleSourceId.get(sourceId);
			
			ArrayList bundlesToRemove = new ArrayList();
			
			while (bundlesToRemoveI.hasNext())
			{
				BasisBundle basisBundle = 
					(BasisBundle) bundlesToRemoveI.next();
				
				 bundlesToRemove.add(basisBundle);
			}
			
			bundlesToRemoveI = bundlesToRemove.iterator();
			
			while (bundlesToRemoveI.hasNext())
			{
				BasisBundle basisBundle = 
					(BasisBundle) bundlesToRemoveI.next();
				
				 remove(basisBundle);
			}
			
			// Now remove entire SetValueMap entry
			fBasisBundlesByBasisBundleSourceId.remove(sourceId);
		}
	}
	

	/**
	 *  Removes all of the BasisBundles that are owned by the given 
	 *  BasisBundleSource from this DataSpace.
	 * 
	 *  @param source A BasisBundleSource
	 */
	
	public void removeBasisBundles(BasisBundleSource source)
	{
		removeBasisBundles(source.getMemberId());
	}
	
	
	/**
	 *  Returns the Map (organized by BasisBundleId) of DataBuffer names across all 
	 *  the BasisBundles in this DataSpace that match the given regular expression. 
	 *  Note that the value of each entry of the returned Map is a Set of such names.
	 *  Also note that if a DataBuffer has a Pixel, the Pixel tag is part of its 
	 *  name.
	 *  
	 *  @param A regular expression
	 *  @return The Map (organized by BasisBundleId) of DataBuffer names across all 
	 *  		the BasisBundles in this DataSpace that match the given regular 
	 * 		expression
	 */
	
	public Map getDataBufferNames(String regExPattern)
	{
		Map result = new HashMap();
			
		Iterator basisBundles = super.iterator();
		
		while (basisBundles.hasNext())
		{
			BasisBundle bundle = (BasisBundle) basisBundles.next();
			
			BasisBundleId id = bundle.getBasisBundleId();
			
			Set names = bundle.getDataBufferNames(regExPattern);
			
			if ((names != null) && (names.size() > 0))
			{
				result.put(id, names);
			}
		}
		
		return (result);
	}
		
	
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
    	super.propertyChange(evt);

    	BasisBundle basisBundle = getBasisBundle((String) evt.getNewValue());
    	
    	if (basisBundle != null)
    	{
			DataSpaceEvent event = new DataSpaceEvent(
				this, basisBundle, true);
			
			fireMembershipEvent(event);
	    }
    }
    
	/**
	 *  Adds the given DataSpaceLisener to this DataSpace as a listener for 
	 *  DataSpaceEvents.
	 * 
	 *  @param listener A DataSpaceLisener
	 */
	
	public void addDataSpaceListener(DataSpaceListener listener)
	{
		super.addMembershipListener(listener);
	}


	/**
	 *  Removes the given DataSpaceLisener from this DataSpace as a listener 
	 *  for BasisBundleEvents.
	 * 
	 *  @param listener A DataSpaceLisener
	 */
	
	public void removeDataSpaceListener(DataSpaceListener listener)
	{
		super.removeMembershipListener(listener);
	}


	/**
	 *  Returns a String representation of this DataSpace.
	 * 
	 *  @return A String representation of this DataSpace
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer(getName() + " [" + getClass() + "]\n");	
		
		stringRep.append("Contains " + super.size() + " BasisBundle(s)\n");
		
		Iterator basisBundles = super.iterator();
			
		for (int i = 1; basisBundles.hasNext(); i++)
		{
			BasisBundle basisBundle = (BasisBundle) basisBundles.next();
			
			stringRep.append(i + ": " + basisBundle + "\n");
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultDataSpace.java,v $
//	Revision 1.33  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.32  2006/05/30 18:50:14  smaher_cvs
//	Fixed bug in removeBasisBundles.  It was incorrectly casting SetValueMap.get() to Collection, but when changed to Iterator, there was a ConcurrentModificationException b/c a remove() called outside of the iterator's remove().  I added the workaround of copying the list of BasisBundles and using an iterator on this (independent) Collection.
//	
//	Revision 1.31  2006/02/01 23:00:06  tames
//	Changed so that name changes of members causes a DataSpaceEvent.
//	
//	Revision 1.30  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.29  2005/11/09 18:43:56  tames_cvs
//	Modified event publishing to use the CopyOnWriteArrayList class to
//	hold listeners. This reduces the overhead when publishing events.
//	
//	Revision 1.28  2005/09/15 13:36:47  chostetter_cvs
//	Now actually returns BasisBundles in regex match; d'oh!
//	
//	Revision 1.27  2005/09/14 21:49:46  chostetter_cvs
//	Added ability to find BasisBundles by regex pattern matching on names
//	
//	Revision 1.26  2005/09/14 21:31:18  chostetter_cvs
//	Fixed BasisBundle name issue in DataSpace
//	
//	Revision 1.25  2005/08/31 16:11:34  tames_cvs
//	Removed toArray method since it was causing a ClassCastException.
//	
//	Revision 1.24  2005/07/14 22:01:40  tames
//	Refactored data package for performance.
//	
//	Revision 1.23  2005/04/06 14:59:46  chostetter_cvs
//	Adjusted logging levels
//	
//	Revision 1.22  2004/11/30 20:07:36  chostetter_cvs
//	Added ability to find DataBuffer names across all BasisBundles that match a given regular expression
//	
//	Revision 1.21  2004/10/14 21:26:00  chostetter_cvs
//	Changed BasisBundle/DataSpace default namespace behavior
//	
//	Revision 1.20  2004/07/22 20:14:58  chostetter_cvs
//	Data, Component namespace work
//	
//	Revision 1.19  2004/07/21 14:26:14  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.18  2004/07/16 00:23:20  chostetter_cvs
//	Refactoring of DataSpace, Output wrt BasisBundle collections
//	
//	Revision 1.17  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.16  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.15  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.14  2004/06/05 06:49:20  chostetter_cvs
//	Debugged BasisBundle stuff. It works!
//	
//	Revision 1.13  2004/06/04 21:14:30  chostetter_cvs
//	Further tweaks in support of data testing
//	
//	Revision 1.12  2004/06/04 17:28:32  chostetter_cvs
//	More data tweaks. Ready for testing.
//	
//	Revision 1.11  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.10  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.9  2004/05/29 04:30:00  chostetter_cvs
//	Further data-related refinements
//	
//	Revision 1.8  2004/05/29 03:39:03  chostetter_cvs
//	Added collection synchronization
//	
//	Revision 1.7  2004/05/29 03:27:24  chostetter_cvs
//	Added toString() method
//	
//	Revision 1.6  2004/05/29 03:07:35  chostetter_cvs
//	Organized imports
//	
//	Revision 1.5  2004/05/28 05:58:19  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.4  2004/05/27 23:09:26  chostetter_cvs
//	More Namespace related changes
//	
//	Revision 1.3  2004/05/27 19:47:45  chostetter_cvs
//	More Namespace, DataSpace changes
//	
//	Revision 1.3  2004/05/27 18:23:49  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.2  2004/05/27 15:57:16  chostetter_cvs
//	Data-related changes
//	
//	Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//	Initial version, checked in to support initial version of new components package
//	
