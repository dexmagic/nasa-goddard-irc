//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/HasBasisBundles.java,v 1.6 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//  $Log: HasBasisBundles.java,v $
//  Revision 1.6  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/09/14 21:49:46  chostetter_cvs
//  Added ability to find BasisBundles by regex pattern matching on names
//
//  Revision 1.4  2005/09/14 21:31:18  chostetter_cvs
//  Fixed BasisBundle name issue in DataSpace
//
//  Revision 1.3  2005/09/14 20:14:48  chostetter_cvs
//  Added ability to use context information to disambiguate (yes, Bob, disambiguate) BasisSet data selection
//
//  Revision 1.2  2004/07/22 20:14:58  chostetter_cvs
//  Data, Component namespace work
//
//  Revision 1.1  2004/07/16 00:23:20  chostetter_cvs
//  Refactoring of DataSpace, Output wrt BasisBundle collections
//
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data;

import java.util.Set;


/**
 * Any Object that has BasisBundles can return the Set of its BasisBundles, or 
 * retrieve them by name.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:51 $
 * @author Carl F. Hostetter
*/

public interface HasBasisBundles
{
	/**
	 *  Returns true if there are currently BasisBundles associated with this 
	 *  Object, false otherwise.
	 *  
	 *  @return	True if there are currently BasisBundles associated with this 
	 *  		Object, false otherwise
	 **/

	public boolean hasBasisBundles();
	
	
	/**
	 *  Returns the current number of BasisBundles associated with this Object.
	 *  
	 *  @return	The current number of BasisBundles associated with this Object
	 **/

	public int getNumBasisBundles();
	
	
	/**
	 *  Returns the Set of BasisBundles associated with this Object.
	 *
	 *  @return The Set of BasisBundles associated with this Object
	 */

	public Set getBasisBundles();


	/**
	 *  Returns the Set of BasisBundles associated with this Object whose 
	 *  fully-qualifed names match the given regular-expression.
	 *
	 *  @param regEx A regular expression
	 *  @return The Set of BasisBundles associated with this Object whose 
	 *  		fully-qualifed names match the given regular-expression
	 */

	public Set getBasisBundles(String regEx);


	/**
	 *  Returns the Set of fully-qualified names of the BasisBundles associated 
	 *  with this Object.
	 *  
	 *  @return	The Set of fully-qualified names of the BasisBundles associated 
	 * 		with this Object
	 **/

	public Set getBasisBundleNames();
	

	/**
	 *  Returns the BasisBundle in the Set of BasisBundles associated with this 
	 *  Object that has the given fully-qualified name. If no such BasisBundle is 
	 *  associated with this Object, the result will be null.
	 *
	 *  @param fullyQualifiedName The fully-qualified name of the desired 
	 *  		BasisBundle
	 *  @return The BasisBundle in the Set of BasisBundles associated with this 
	 *  		Object that has the given fully-qualified name
	 */

	public BasisBundle getBasisBundle(String fullyQualifiedName);
	
	
	/**
	 *  Returns the BasisBundleId of the BasisBundle associated with this Object 
	 *  (if any) that has the given fully-qualified name. If no such BasisBundle is 
	 *  associated with this Object, the result will be null.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the desired 
	 *  		BasisBundle
	 *  @return The BasisBundleId of the BasisBundle associated with this 
	 * 		Object (if any) that has the given fully-qualified name
	 */
	
	public BasisBundleId getBasisBundleId(String fullyQualifiedName);
	
	
	/**
	 *  Returns the BasisBundle associated with this Object (if any) that has 
	 *  the given BasisBundleId.
	 * 
	 *  @param basisBundleId The BasisBundleId of the desired BasisBundle
	 *  @return The BasisBundle associated with this Object (if any) that has 
	 * 		the given basisBundleId
	 */
	
	public BasisBundle getBasisBundle(BasisBundleId basisBundleId);
}
