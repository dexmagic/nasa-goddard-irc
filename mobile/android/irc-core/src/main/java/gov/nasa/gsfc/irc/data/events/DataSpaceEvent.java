//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/DataSpaceEvent.java,v 1.4 2006/01/24 15:22:21 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.data.events;

import gov.nasa.gsfc.commons.types.namespaces.MembershipEvent;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.DataSpace;


/**
 * A DataSpaceEvent contains information about a change in the state of a 
 * DataSpace.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/24 15:22:21 $
 * @author Carl F. Hostetter
 */

public class DataSpaceEvent extends MembershipEvent
{
	/**
	 * Constructs a new DataSpaceEvent indicating whether the given BasisBundle 
	 * has just joined or just left the given source DataSpace.
	 * 
	 * @param source The DataSpace source of the new DataSpaceEvent
	 * @param basisBundle A BasisBundle
	 * @param isMember Indicates whether the given BasisBundle is now a member of 
	 * 		the source DataSpace
	 */
	
	public DataSpaceEvent(DataSpace source, BasisBundle basisBundle, 
		boolean isMember)
	{
		super(source, basisBundle, isMember);
	}
	
	
	/**
	 * Returns the DataSpace source of this DataSpaceEvent.
	 * 
	 * @return The DataSpace source of this DataSpaceEvent
	 */
	
	public DataSpace getDataSpace()
	{
		return ((DataSpace) getSource());
	}
	
	
	/**
	 * Returns the BasisBundleId of the BasisBundle that has either joined or left 
	 * the source DataSpace (if any).
	 * 
	 * @return The BasisBundleId of the BasisBundle that has either joined or left 
	 * 		the source DataSpace
	 */
	
	public BasisBundleId getBasisBundleId()
	{
		BasisBundleId result = null;
		
		BasisBundle basisBundle = (BasisBundle) getMember();
		
		if (basisBundle != null)
		{
			result = basisBundle.getBasisBundleId();
		}
		
		return (result);
	}
	
	
	/**
	 * Returns a String representation of this DataSpaceEvent.
	 * 
	 * @return A String representation of this DataSpaceEvent
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer("DataSpaceEvent:\n");
		
		BasisBundleId id = getBasisBundleId();
		
		stringRep.append("BasisBundleId: " + id);
		
		if (id != null)
		{
			if (wasAdded())
			{
				stringRep.append(" has been added to ");
			}
			else if (wasRemoved())
			{
				stringRep.append(" has been removed from ");
			}
		
			stringRep.append(getDataSpace().getName());
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpaceEvent.java,v $
//  Revision 1.4  2006/01/24 15:22:21  chostetter_cvs
//  Fixed possible null-pointer exception
//
//  Revision 1.3  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/07/13 20:11:31  tames
//  File header change only.
//
//  Revision 1.1  2004/07/21 14:21:41  chostetter_cvs
//  Moved into subpackage
//
//  Revision 1.3  2004/07/16 00:55:26  chostetter_cvs
//  Tweaks
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
