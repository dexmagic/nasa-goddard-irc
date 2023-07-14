//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: AbstractCreator.java,v $
//	Revision 1.3  2006/08/03 20:20:55  chostetter_cvs
//	Fixed proxy BasisBundle name creation bug
//	
//	Revision 1.2  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.1  2006/01/23 17:59:54  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.3  2004/06/05 06:49:20  chostetter_cvs
//	Debugged BasisBundle stuff. It works!
//	
//	Revision 1.2  2004/05/28 05:58:19  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.1  2004/05/12 21:55:40  chostetter_cvs
//	Further tweaks for new structure, design
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
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

package gov.nasa.gsfc.commons.processing.creation;

import gov.nasa.gsfc.commons.types.namespaces.AbstractMember;
import gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName;


/**
 *  A Creator is a fully-qualfied-named Object that can create other Objects.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/03 20:20:55 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractCreator extends AbstractMember implements Creator
{
	/**
	 *  Constructs a new Creator having the given fully-qualified name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the new Creator
	 **/

	public AbstractCreator(String fullyQualifiedName)
	{
		super(fullyQualifiedName);
	}
		
	
	/**
	 *  Constructs a new Creator having the given base name and (fixed) name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new Creator
	 *  @param nameQualifier The name qualifier of the new Creator
	 **/

	public AbstractCreator(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
		
	
	/**
	 *  Constructs a new Creator having the given base name, and whose name qualifier 
	 *  is set to the fully-qualified name of the given Object. If the given Object 
	 *  has a fully-qualified name property, the name qualifier of this Creator will 
	 *  be updated as needed to reflect any subsequent changes in the fully-
	 *  qualified name of the given Object.
	 * 
	 *  @param name The base name of the new Creator
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new Creator
	 **/

	public AbstractCreator(String name, HasFullyQualifiedName nameQualifier)
	{
		super(name, nameQualifier);
	}
}
