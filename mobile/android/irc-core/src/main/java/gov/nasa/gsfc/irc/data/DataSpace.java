//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.commons.types.namespaces.MemberSetBean;
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

public interface DataSpace extends HasName, MemberSetBean, HasBasisBundles
{
	/**
	 *  Returns true if the given BasisBundle is in this DataSpace, false  
	 *  otherwise.
	 *  
	 *  @param basisBundle A BasisBundle
	 *  @return	True if the given BasisBundle is in this DataSpace, false 
	 *  		otherwise
	 **/

	public boolean contains(BasisBundle basisBundle);
	
	
	/**
	 *  Adds the given BasisBundle to this DataSpace.
	 *  
	 *  @param basisBundle The BasisBundle to be added to this DataSpace
	 *  @return True if the given BasisBundle was actually added
	 **/

	public void addBasisBundle(BasisBundle basisBundle);
	
	
	/**
	 *  Removes the given BasisBundle from this DataSpace.
	 * 
	 *  @param basisBundle The BasisBundle to remove
	 */
	
	public void remove(BasisBundle basisBundle);
	
	
	/**
	 *  Removes the BasisBundle having the given BasisBundleId from 
	 *  this DataSpace.
	 * 
	 *  @param basisBundleId The BasisBundleId of the BasisBundle to 
	 * 		remove
	 */
	
	public void removeBasisBundle(BasisBundleId basisBundleId);
	
	
	/**
	 *  Removes all of the BasisBundles that are owned by the 
	 *  BasisBundleSource having the given MemberId from this 
	 *  DataSpace.
	 * 
	 *  @param sourceId A BasisBundleSourceId
	 */
	
	public void removeBasisBundles(MemberId sourceId);


	/**
	 *  Removes all of the BasisBundles that are owned by the given 
	 *  BasisBundleSource from this DataSpace.
	 * 
	 *  @param source A BasisBundleSource
	 */
	
	public void removeBasisBundles(BasisBundleSource source);
	
	
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
	
	public Map getDataBufferNames(String regEx);
		
	
	/**
	 *  Adds the given DataSpaceLisener to this DataSpace as a listener for 
	 *  DataSpaceEvents.
	 * 
	 *  @param listener A DataSpaceLisener
	 */
	
	public void addDataSpaceListener(DataSpaceListener listener);


	/**
	 *  Removes the given DataSpaceLisener from this DataSpace as a listener 
	 *  for BasisBundleEvents.
	 * 
	 *  @param listener A DataSpaceLisener
	 */
	
	public void removeDataSpaceListener(DataSpaceListener listener);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DataSpace.java,v $
//	Revision 1.19  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.18  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.17  2005/08/31 16:11:34  tames_cvs
//	Removed toArray method since it was causing a ClassCastException.
//	
//	Revision 1.16  2004/11/30 20:07:36  chostetter_cvs
//	Added ability to find DataBuffer names across all BasisBundles that match a given regular expression
//	
//	Revision 1.15  2004/07/21 14:26:14  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.14  2004/07/16 00:23:20  chostetter_cvs
//	Refactoring of DataSpace, Output wrt BasisBundle collections
//	
//	Revision 1.13  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.12  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.11  2004/06/09 03:28:49  chostetter_cvs
//	Output-related modifications
//	
//	Revision 1.10  2004/06/04 21:14:30  chostetter_cvs
//	Further tweaks in support of data testing
//	
//	Revision 1.9  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.8  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.7  2004/05/29 04:30:00  chostetter_cvs
//	Further data-related refinements
//	
//	Revision 1.6  2004/05/29 03:07:35  chostetter_cvs
//	Organized imports
//	
//	Revision 1.5  2004/05/28 05:58:19  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.4  2004/05/27 19:47:45  chostetter_cvs
//	More Namespace, DataSpace changes
//	
