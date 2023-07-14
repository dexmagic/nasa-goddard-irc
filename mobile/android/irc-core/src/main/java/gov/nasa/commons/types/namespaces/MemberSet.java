//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: MemberSet.java,v $
// Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.io.Serializable;
import java.util.Collection;


/**
 *  A MemberSet contains an ordered Set of Members.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface MemberSet extends Serializable, HasMembers
{
	/**
	 *  Adds the given Member to this MemberSet. If a Member of this MemberSet 
	 *  already has the same fully-qualified name as the given Member, the given 
	 *  Member is not added, and the result is false. Otherwise, it is added and 
	 *  the result is true.
	 *  
	 *  @param The Member to add to this MemberSet
	 *  @return True if the given Member was actually added, false otherwise
	 **/

	public boolean add(Member member);
	
	
	/**
	 *  Adds the given Collection of Members to this MemberSet. If a Member of this 
	 *  MemberSet already has the same fully-qualified name as any of the given 
	 *  Members, that given Member is not added.
	 *  
	 *  @param The Members to add to this MemberSet
	 *  @return True if all of the given Members were actually added, false otherwise
	 **/

	public boolean addAll(Collection members);
	
	
	/**
	 *  Adds the given Member to this MemberSet. If a Member of this MemberSet 
	 *  already has the same fully-qualified name as the given Member, the given 
	 *  Member replaces the previous Member, which is returned. Otherwise, it is 
	 *  added and the result is null.
	 *  
	 *  @param The Member to add to this MemberSet
	 *  @return The previous Member with the same fully-qualified name (if any) 
	 *  		replaced by the given Member
	 **/

	public Member replace(Member member);
	
	
	/**
	 *  Removes the given Member from this MemberSet.
	 * 
	 *  @param member The Member to be removed
	 *  @return True if the given Member was actually removed
	 */

	public boolean remove(Member member);


	/**
	 *  Removes the Member in this MemberSet that has the given MemberId.
	 * 
	 *  @param memberId The MemberId of the Member to be removed
	 *  @return The Member that was removed
	 */

	public Member remove(MemberId memberId);


	/**
	 *  Removes the Member in this MemberSet that has the given fully-qualified 
	 *  (and thus globally unique) name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the Member to be 
	 *  		removed
	 *  @return The Member that was removed
	 */

	public Member remove(String fullyQualifiedName);


	/**
	 *  Removes all Members from this MemberSet.
	 *  
	 **/

	public void clear();
}
