//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: HasMembers.java,v $
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

import java.util.Map;
import java.util.Set;


/**
 *  The HasMembers interface specifies the methods that all Objects having 
 *  Members must implement.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface HasMembers extends HasNamedObjects
{
	/**
	 *  Returns the Set of MemberIds of the Members associated with this Object.
	 *  
	 *  @return The Set of MemberIds of the Members associated with this Object
	 **/

	public Set getMemberIds();
	
	
	/**
	 *  Returns the Set of fully-qualified (and thus globally-unique) names of the 
	 *  Members associated with this Object.
	 *  
	 *  @return The Set of fully-qualified (and thus globally-unique) names of the 
	 *  		Members associated with this Object
	 **/

	public Set getFullyQualifiedNames();
	
	
	/**
	 *  Returns a Map of the Members associated with this Object keyed by their 
	 *  MemberIds.
	 *
	 *  @return A Map of the Members associated with this Object keyed by their 
	 *  		MemberIds
	 */

	public Map getMembersByMemberId();
	
	
	/**
	 *  Returns a Map of the Members associated with this Object keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Members associated with this Object keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public Map getMembersByFullyQualifiedName();
	
	
	/**
	 *  Returns the MemberId of the Member associated with this Object that has 
	 *  the given fully-qualified (and thus globally unique) name. If no such Object 
	 *  exists, the result is null.
	 *
	 *  @param fullyQualifiedName A fully-qualified (and thus globally unique) name 
	 *  		of some Member
	 *  @return The MemberId of the Member associated with this Object that has 
	 *  		the given fully-qualified (and thus globally unique) name
	 **/

	public MemberId getMemberId(String fullyQualifiedName);
	
	
	/**
	 *  Returns the Member associated with this Object that has the given 
	 *  MemberId. If no such Member exists, the result is null.
	 *
	 *  @param memberId The MemberId of some Member associated with this Object
	 *  @return The named Member associated with this Object that has the given 
	 *  		MemberId
	 **/

	public Member get(MemberId MemberId);
	
	
	/**
	 *  Returns the Member associated with this Object that has the given 
	 *  fully-qualified (and thus globally unique) name. If no such Object exists, 
	 *  the result is null.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member
	 *  @return The Member associated with this Object that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public Object get(String fullyQualifiedName);
	
	
	/**
	 *  Returns the Set of Members associated with this Object that have the given 
	 *  base (and thus potentially shared) name.
	 *
	 *  @param baseName The base (and thus potentially shared) name of some 
	 *  		Member(s) associated with this Object
	 *  @return The Set of Members associated with this Object that have the given 
	 *  		base (and thus potentially shared) name
	 **/

	public Set getByBaseName(String baseName);
	
	
	/**
	 *  Returns the Set of Members associated with this Object whose base names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Members associated with this Object
	 *  @return The Set of Members associated with this Object whose base names 
	 *  		match the given regular expression pattern
	 **/

	public Set getByBaseNamePatternMatching(String regExPattern);
	
	
	/**
	 *  Returns the Member associated with this Object that has the given 
	 *  fully-qualified (and thus globally unique) name. If no such Object exists, 
	 *  the result is null.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member
	 *  @return The Member associated with this Object that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public Member getByFullyQualifiedName(String fullyQualifiedName);
	
	
	/**
	 *  Returns the Set of Members associated with this Object whose fully-qualified 
	 *  names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Members associated with this Object
	 *  @return The Set of Members associated with this Object whose fully-qualified 
	 *  		names match the given regular expression pattern
	 **/

	public Set getByFullyQualifiedNamePatternMatching(String regExPattern);
}
