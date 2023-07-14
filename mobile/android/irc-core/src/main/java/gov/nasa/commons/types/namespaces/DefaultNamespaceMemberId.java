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
 *  A NamespaceMemberId serves as a globally unique and persistent identifier 
 *  of a NamespaceMember.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public class DefaultNamespaceMemberId extends DefaultNamespaceMemberInfo 
	implements NamespaceMemberId
{
	/**
	 *  Constructs a new NamespaceMemberId that identifies the given NamespaceMember.
	 * 
	 *  @param member The NamespaceMember identified by the new NamespaceMemberId
	 **/

	public DefaultNamespaceMemberId(NamespaceMember member)
	{
		super(member.getFullyQualifiedName());
	}
		
	
	/**
	 *  Constructs a new NamespaceMemberId that identifies a NamespaceMember 
	 *  having the given sequenced name within the Namespace having the given 
	 *  fully-qualified name.
	 * 
	 *  @param sequencedName The sequenced name of the identified NamespaceMember
	 *  @param namespaceName The fully-qualified name of the Namespace of the 
	 *  		identified NamespaceMember
	 **/

	public DefaultNamespaceMemberId(String sequencedName, String namespaceName)
	{
		super(Namespaces.formFullyQualifiedName(sequencedName, namespaceName));
	}
		
	
	/**
	 *  Constructs a new NamespaceMemberId that identifies a NamespaceMember 
	 *  having the given fully-qualified name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the identified 
	 *  		NamespaceMember
	 **/

	public DefaultNamespaceMemberId(String fullyQualifiedName)
	{
		super(fullyQualifiedName);
	}
		
	
	/**
	 *  Constructs a new NamespaceMemberId that identifies a NamespaceMember 
	 *  having the given sequenced name (which must be identical to its base name) 
	 *  and belonging to the given Namespace.
	 * 
	 *  @param name The sequenced name of the identified NamespaceMember
	 *  @param namespace The Namespace of the identified NamespaceMember
	 **/

	protected DefaultNamespaceMemberId(String name, Namespace namespace)
	{
		super(name, namespace);
	}
}

//--- Development History ----------------------------------------------------
//
//	$Log: DefaultNamespaceMemberId.java,v $
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//
