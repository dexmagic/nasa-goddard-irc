//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/AbstractBasisBundleSource.java,v 1.4 2006/08/03 20:20:55 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractBasisBundleSource.java,v $
//	Revision 1.4  2006/08/03 20:20:55  chostetter_cvs
//	Fixed proxy BasisBundle name creation bug
//	
//	Revision 1.3  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.2  2006/01/24 20:18:07  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.commons.processing.creation.AbstractCreator;
import gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName;


/**
 *  A BasisBundleSource is a source (creator, owner, writer) of BasisBundles.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/03 20:20:55 $
 *  @author	Carl F. Hostetter
**/

public abstract class AbstractBasisBundleSource extends AbstractCreator 
	implements BasisBundleSource
{
	/**
	 * Constructs a new BasisBundleSource having the given fully-qualified name.
	 * 
	 * @param fullyQualifiedName The fully-qualified name of the new 
	 * 		BasisBundleSource
	 */
	 
	public AbstractBasisBundleSource(String fullyQualifiedName)
	{
		super(fullyQualifiedName);
	}
		
		
	/**
	 * Constructs a new BasisBundleSource having the given base name and name 
	 * qualifier.
	 * 
	 * @param name The base name of the new BasisBundleSource
	 * @param nameQualifier The name qualifier of the new BasisBundleSource
	 */
	 
	public AbstractBasisBundleSource(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
		
		
	/**
	 *  Constructs a new BasisBundleSource having the given base name, and whose 
	 *  name qualifier is set to the fully-qualified name of the given Object. If 
	 *  the given Object has a fully-qualified name property, the name qualifier of 
	 *  this BasisBundleSource will be updated as needed to reflect any subsequent 
	 *  changes in the fully-qualified name of the given Object.
	 * 
	 *  @param name The base name of the new BasisBundleSource
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new BasisBundleSource
	 **/

	public AbstractBasisBundleSource(String name, 
		HasFullyQualifiedName nameQualifier)
	{
		super(name, nameQualifier);
	}
}

