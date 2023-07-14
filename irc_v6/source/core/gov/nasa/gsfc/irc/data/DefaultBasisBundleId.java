//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/DefaultBasisBundleId.java,v 1.3 2006/08/01 19:55:47 chostetter_cvs Exp $
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

import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberId;


/**
 * A BasisBundleId serves as a unique and persistent identifier of a 
 * BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Carl F. Hostetter
 */

public class DefaultBasisBundleId extends DefaultMemberId 
	implements BasisBundleId
{
	/**
	 * Constructs a BasisBundleId for the BasisBundle having the given 
	 * fully-qualified (and thus globally unique) name.
	 * 
	 * @param fullyQualifiedName The fully-qualified name of a BasisBundle
	 */
	
	public DefaultBasisBundleId(String fullyQualifiedName)
	{
		super(fullyQualifiedName);
	}
	

	/**
	 * Constructs a BasisBundleId for the BasisBundle having the given base name 
	 * and the given fully-qualified name of that BasisBundle's source.
	 * 
	 * @param baseName The base name of the BasisBundle described by this 
	 * 		BasisBundleId
	 */
	
	public DefaultBasisBundleId(String baseName, String sourceName)
	{
		super(baseName, sourceName);
	}
		
	
	/**
	 *  Constructs a new BasisBundleId identifying a BasisBundle having the given 
	 *  base name and BasisBundleSource.
	 * 
	 *  @param name The base name of the new BasisBundleId
	 *  @param source The BasisBundleId of the BasisBundle identified by the new 
	 *  		BasisBundleId
	 **/

	public DefaultBasisBundleId(String name, BasisBundleSource source)
	{
		super(name, source.getMemberId());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultBasisBundleId.java,v $
//  Revision 1.3  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.2  2006/03/14 16:13:18  chostetter_cvs
//  Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
//  Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.13  2006/01/02 03:44:47  tames
//  Javadoc change only.
//
//  Revision 1.12  2005/12/06 22:42:22  tames_cvs
//  Added constructors to support creating an instance when the BasisBundle or
//  source id is not available. This is required to implement the late binding of a
//  BasisRequest. This class will likely need to change again when the
//  name space modifications are completed.
//
//  Revision 1.11  2005/09/13 22:28:58  tames
//  Changes to refect BasisBundleEvent refactoring.
//
//  Revision 1.10  2005/05/02 18:44:51  chostetter_cvs
//  Removed equals() method from (by definition) unique ID objects (since they can use == reliably)
//
//  Revision 1.9  2004/07/12 19:04:45  chostetter_cvs
//  Added ability to find BasisBundleId, Components by their fully-qualified name
//
//  Revision 1.8  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.7  2004/06/09 03:28:49  chostetter_cvs
//  Output-related modifications
//
//  Revision 1.6  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.5  2004/05/28 05:58:19  chostetter_cvs
//  More Namespace, DataSpace, Descriptor worl
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
