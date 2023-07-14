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

package gov.nasa.gsfc.commons.types.namespaces;


/**
 *  A MemberId serves as a globally unique and persistent identifier of a 
 *  Member.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public class DefaultMemberId extends DefaultMemberInfo implements MemberId
{
	/**
	 *  Constructs a new MemberId that identifies a Member having the given name 
	 *  and given name qualifier.
	 * 
	 *  @param name The base name of the identified Member
	 *  @param nameQualifier The name qualifier of the identified Member
	 **/

	public DefaultMemberId(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
		
	
	/**
	 *  Constructs a new MemberId identifying a Member having the given base name, 
	 *  and whose name qualifier is set to the fully-qualified name of the given 
	 *  Object. If the given Object has a fully-qualified name property, the name 
	 *  qualifier of this MemberId will be updated as needed to reflect any 
	 *  subsequent changes in the fully-qualified name of the given Object.
	 * 
	 *  @param name The base name of the new MemberId
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new MemberId
	 **/

	public DefaultMemberId(String name, HasFullyQualifiedName nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 *  Constructs a new MemberId that identifies a Member having the given fully-
	 *  qualified name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the identified Member
	 **/

	public DefaultMemberId(String fullyQualifiedName)
	{
		super(Namespaces.getSequencedName(fullyQualifiedName), 
			Namespaces.getNameQualifier(fullyQualifiedName));
	}
}

//--- Development History ----------------------------------------------------
//
//	$Log: DefaultMemberId.java,v $
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.4  2005/12/06 19:55:10  tames_cvs
//	Added equals and hashCode methods.
//	
//	Revision 1.3  2005/05/02 18:44:51  chostetter_cvs
//	Removed equals() method from (by definition) unique ID objects (since they can use == reliably)
//	
//	Revision 1.2  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.1  2004/06/02 22:33:27  chostetter_cvs
//	Namespace revisions
//	
//
