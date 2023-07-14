//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/DataSet.java,v 1.18 2006/01/23 17:59:51 chostetter_cvs Exp $
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
import java.util.Collection;

import gov.nasa.gsfc.commons.processing.creation.HasCreator;


/**
 * A DataSet is a collection of BasisSets.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:51 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public interface DataSet extends Serializable, HasCreator
{
	/**
	 *  Returns the Collection of BasisSets contained in this DataSet.
	 *
	 *  @return The Collection of BasisSets contained in this DataSet
	 **/
	public Collection getBasisSets();

	/**
	 *  Returns the BasisSet contained in this DataSet that has the 
	 *  given BasisBundleId.
	 *
	 *  @return The BasisSet contained in this DataSet that has the 
	 *  		given BasisBundleId
	 **/
	public BasisSet getBasisSet(BasisBundleId basisBundleId);

	/**
	 *  Returns a copy of this DataSet, having exactly the same structure as this 
	 *  DataSet but containing a copy of each of the underlying BasisSets. Note that 
	 *  because this data is a copy, releasing, modifying, or accessing this 
	 *  DataSet will have no effect on the returned copy.
	 *
	 *  @return A copy of this DataSet
	 **/
	public DataSet copy();

	/**
	 *  Returns a duplicate of this DataSet, having exactly the same structure as 
	 *  this DataSet but containing a duplicate of each of 
	 *  the underlying BasisSets. Note that the underlying data is not copied.
	 *
	 *  @return A duplicate of this DataSet
	 **/
	public DataSet duplicate();

	/**
	 *  Releases the BasisSets of this DataSet from further use 
	 *  by the caller.
	 **/
	public void release();

	/**
	 * Holds the BasisSet of this DataSet for further use by the caller. 
	 * This prevents the BasisSets from being reclaimed by the provider.
	 */
	public void hold();

	/**
	 *	Returns a String representation of the specified range of data in 
	 *  this DataSet, across all of its BasisSets.
	 * 
	 *  @param startPosition The start of the data range
	 *  @param endPosition The end of the data range
	 *  @return A String representation of the specified range of data in 
	 *  		this DataSet, across all of its BasisSets.
	 */
	public String dataToString(int startPosition, int endPosition);

	/**
	 *	Returns a String representation of the specified amount of data in 
	 *  this DataSet, across all of its BasisSets.
	 * 
	 *  @param amount The amount of data
	 *  @return A String representation of the specified amount of data in 
	 *  		this DataSet, across all of its BasisSets
	 */
	public String dataToString(int amount);

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
	public String dataToString();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataSet.java,v $
//  Revision 1.18  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.17  2005/09/13 22:28:58  tames
//  Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.16  2005/07/18 21:12:39  tames_cvs
//  Added hold method.
//
//  Revision 1.15  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.14  2005/02/02 21:17:27  chostetter_cvs
//  DataSets (and their BasisSets) are now duplicated (in the java.nio sense) when sent to more than one DataListener
//
//  Revision 1.13  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
