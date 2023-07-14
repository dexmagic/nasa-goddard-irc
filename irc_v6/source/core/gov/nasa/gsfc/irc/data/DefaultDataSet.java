//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/DefaultDataSet.java,v 1.11 2006/08/01 19:55:47 chostetter_cvs Exp $
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gov.nasa.gsfc.commons.processing.creation.AbstractCreatedObject;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;


/**
 * A DataSet is a collection of BasisSets.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Carl F. Hostetter
 */

public class DefaultDataSet extends AbstractCreatedObject implements DataSet
{
	private Map fBasisSetsByBasisBundleId = new HashMap();
	
	/**
	 *  Constructs a new DefaultDataSet whose Creator has the given MemberId 
	 *  and containing (only) the given BasisSet.
	 *
	 *  @param creatorId The CreatorId of the new DataSet
	 *  @param basisSet A BasisSet
	 **/
	public DefaultDataSet(MemberId creatorId, BasisSet basisSet)
	{
		super(creatorId);
		
		fBasisSetsByBasisBundleId.put
			(basisSet.getBasisBundleId(), basisSet);
	}
	
	/**
	 *  Constructs a new DefaultDataSet whose Creator has the given MemberId 
	 *  and containing the given Collection of BasisSets.
	 *
	 *  @param creatorId The CreatorId of the new DataSet
	 *  @param basisSets A Collection of BasisSets
	 **/
	public DefaultDataSet(MemberId creatorId, Collection basisSets)
	{
		super(creatorId);
		
		Iterator basisSetIterator = basisSets.iterator();
		
		while (basisSetIterator.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSetIterator.next();
			
			fBasisSetsByBasisBundleId.put
				(basisSet.getBasisBundleId(), basisSet);
		}
	}
	
	/**
	 *  Constructs a new DefaultDataSet having the same structure and referring to 
	 *  the same set of BasisSets as the given DataSet. This is useful for making 
	 *  copies and duplicates of the data and structure of the given DataSet without 
	 *  altering it.
	 *
	 *  @param dataSet The DataSet to clone
	 **/
	protected DefaultDataSet(DataSet dataSet)
	{
		super(dataSet.getCreatorId());
		
		Iterator basisSets = dataSet.getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			fBasisSetsByBasisBundleId.put
				(basisSet.getBasisBundleId(), basisSet);
		}
	}
	
	/**
	 *  Returns a new DataSet having the same structure and referring to the same 
	 *  set of BasisSets as the given DataSet. This is useful for making copies 
	 *  and duplicates of the data and structure of the given DataSet without 
	 *  altering it.
	 * 
	 *  @return A clone of this BasisSet
	 **/
	protected Object clone()
	{
		return (new DefaultDataSet(this));
	}
	
	/**
	 * Returns a copy of this DataSet having exactly the same structure as this
	 * DataSet but containing a copy of each of the underlying BasisSets. Note
	 * that because this data is a copy, releasing, modifying, or accessing this
	 * DataSet will have no effect on the returned copy.
	 * 
	 * @return A copy of this DataSet
	 */
	public DataSet copy()
	{
		DefaultDataSet result = (DefaultDataSet) clone();
		
		result.fBasisSetsByBasisBundleId.clear();
		
		Iterator basisSets = this.getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			BasisSet copy = basisSet.copy();
			
			result.fBasisSetsByBasisBundleId.put
				(basisSet.getBasisBundleId(), copy);
		}
		
		return (result);
	}
	
	/**
	 * Returns a duplicate of this DataSet, having exactly the same structure as
	 * this DataSet but containing a duplicate of each of the underlying
	 * BasisSets. Note that the underlying data is not copied. The caller should
	 * always release the new DataSet when it is no longer needed.
	 * 
	 * @return A duplicate of this DataSet
	 */
	public DataSet duplicate()
	{
		DefaultDataSet result = (DefaultDataSet) clone();
		
		result.fBasisSetsByBasisBundleId.clear();
		
		Iterator basisSets = this.getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			BasisSet duplicate = basisSet.duplicate();
			
			result.fBasisSetsByBasisBundleId.put
				(basisSet.getBasisBundleId(), duplicate);
		}
		
		return (result);
	}
	
	/**
	 *  Returns the Collection of BasisSets contained in this DataSet.
	 *
	 *  @return The Collection of BasisSets contained in this DataSet
	 **/
	public Collection getBasisSets()
	{
		return (fBasisSetsByBasisBundleId.values());
	}
	
	/**
	 *  Returns the BasisSet contained in this DataSet that has the 
	 *  given BasisBundleId.
	 *
	 *  @return The BasisSet contained in this DataSet that has the 
	 *  		given BasisBundleId
	 **/
	public BasisSet getBasisSet(BasisBundleId basisBundleId)
	{
		return ((BasisSet) fBasisSetsByBasisBundleId.get(basisBundleId));
	}

	/**
	 *  Releases this DataSet from further use by the caller.
	 *
	 **/
	public void release()
	{
		Iterator basisSets = getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			basisSet.release();
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.DataSet#hold()
	 */
	public void hold()
	{
		Iterator basisSets = getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			basisSet.hold();
		}
	}

	/**
	 *	Returns a String representation of the specified range of data in 
	 *  this DataSet, across all of its BasisSets.
	 * 
	 *  @param startPosition The start of the data range
	 *  @param length The length data range
	 *  @return A String representation of the specified range of data in 
	 *  		this DataSet, across all of its BasisSets.
	 */
	public String dataToString(int startPosition, int length)
	{
		StringBuffer stringRep = new StringBuffer
			("DataSet " + super.toString() + "\n");
		
		Iterator basisSets = getBasisSets().iterator();
		
		for (int i = 0; basisSets.hasNext(); i++)
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			stringRep.append(basisSet.dataToString
				(startPosition, length) + "\n");
		}
		
		return (stringRep.toString());
	}	
	
	/**
	 *	Returns a String representation of the specified amount of data in 
	 *  this DataSet, across all of its BasisSets.
	 * 
	 *  @param amount The amount of data
	 *  @return A String representation of the specified amount of data in 
	 *  		this DataSet, across all of its BasisSets
	 */
	public String dataToString(int amount)
	{
		return (dataToString(0, amount));
	}
		
	/**
	 *	Returns a String representation of the data in this DataSet, across 
	 *  all of its BasisSets.
	 *
	 *  <p>Note that for a typical BasisBundle this could produce a 
	 *  <em>very</em> long String!
	 * 
	 *  @return A String representation of the data in this DataSet, across 
	 *  		all of its BasisSets
	 */
	public String dataToString()
	{
		StringBuffer stringRep = new StringBuffer
			("DataSet " + super.toString() + "\n");
	
		Iterator basisSets = getBasisSets().iterator();
	
		for (int i = 0; basisSets.hasNext(); i++)
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
		
			stringRep.append(basisSet.dataToString() + "\n");
		}
	
		return (stringRep.toString());
	}

	/**
	 *  Returns a String representation of this DataSet.
	 * 
	 *  @return A String representation of this DataSet
	 */
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer("DataSet " + 
			super.toString());	
		
		stringRep.append("\nBasisSets:");
		
		Iterator basisSets = getBasisSets().iterator();
		
		for (int i = 1; basisSets.hasNext(); i++)
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			stringRep.append("\n" + i + ": " + basisSet);
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataSet.java,v $
//  Revision 1.11  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.10  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/09/22 18:42:43  tames
//  Fixed dataToString(amount) method to handle amount = 1.
//
//  Revision 1.8  2005/09/13 22:28:58  tames
//  Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.7  2005/08/26 03:02:26  tames
//  Documentation change only.
//
//  Revision 1.6  2005/07/18 21:12:39  tames_cvs
//  Added hold method.
//
//  Revision 1.5  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.4  2005/04/04 15:40:58  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.3  2005/02/02 22:34:03  chostetter_cvs
//  Fixed errors in copy(), duplicate()
//
//  Revision 1.2  2005/02/02 21:17:27  chostetter_cvs
//  DataSets (and their BasisSets) are now duplicated (in the java.nio sense) when sent to more than one DataListener
//
//  Revision 1.1  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
//  Revision 1.12  2004/07/22 16:28:03  chostetter_cvs
//  Various tweaks
//
//  Revision 1.11  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.10  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.9  2004/07/02 02:33:30  chostetter_cvs
//  Renamed DataRequest to BasisRequest
//
//  Revision 1.8  2004/06/29 22:46:13  chostetter_cvs
//  Fixed broken CVS-generated comments. Grrr.
//
//  Revision 1.7  2004/06/29 22:39:39  chostetter_cvs
//  Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//
//  Revision 1.6  2004/06/15 22:21:12  chostetter_cvs
//  More DataSetRequester work
//
//  Revision 1.5  2004/06/04 23:10:27  chostetter_cvs
//  Added data printing support to various Buffer classes
//
//  Revision 1.4  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.3  2004/05/27 23:09:26  chostetter_cvs
//  More Namespace related changes
//
//  Revision 1.2  2004/05/16 15:44:36  chostetter_cvs
//  Further data-handling definition
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
