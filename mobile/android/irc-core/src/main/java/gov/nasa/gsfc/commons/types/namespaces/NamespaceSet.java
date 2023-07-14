//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: NamespaceSet.java,v $
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.util.Set;


/**
 *  A NamespaceSet is a Set of Namespaces.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface NamespaceSet extends NamedBean
{
	/**
	 *  Adds the given Namespace to the Set of Namespaces in this NamespaceSet.
	 *
	 *  @param namespace The Namespace to be added to this NamespaceSet
	 *  @return True if the given Namespace was actually added to this 
	 *  		NamespaceSet
	 **/

	public boolean add(Namespace namespace);


	/**
	 *  Returns the Set of Namespaces in this NamespaceSet.
	 *  
	 *  @return	The Set of Namespaces in this NamespaceSet
	 **/

	public Set getNamespaces();


	/**
	 *  Returns the Set of the names of the Namespaces in this NamespaceSet.
	 *  
	 *  @return	The Set of the names of the Namespaces in this NamespaceSet
	 **/

	public Set getNamespaceNames();
	
	
	/**
	 *  Returns the Namespace in this NamespaceSet that has the given name.
	 *  
	 *  @param name The name of some Namespace in this Namespace
	 *  @return The Namespace in this NamespaceSet that has the given name
	 **/

	public Namespace get(String name);
	
	
	/**
	 *  Returns the Set of Namespaces in this NamespaceSet whose names match the 
	 *  given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the names of the Namespaces in this NamespaceSet
	 *  @return The Set of Namespaces in this NamespaceSet whose names match the 
	 *  		given regular expression pattern
	 **/

	public Set getNamespacesByPatternMatching(String regExPattern);
	
	
	/**
	 *  Returns the (at most) single NamespaceMember from among the Namespaces in this 
	 *  NamespaceSet that has the given fully-qualified name.
	 *
	 *  @param fullyQualifiedName The fully-qualified name of some NamespaceMember of some 
	 *  		Namespace in this NamespaceSet
	 *  @return The (at most) single NamespaceMember from among the Namespaces in this 
	 *  		NamespaceSet that has the given fully-qualified name
	 **/

	public NamespaceMember getMember(String fullyQualifiedName);
	
	
	/**
	 *  Returns the Set of Members from among the Namespaces in this NamespaceSet 
	 *  that have the given sequenced name.
	 *
	 *  @param sequencedName The sequenced name of some NamespaceMember(s) of some 
	 *  		Namespace(s) in this NamespaceSet
	 *  @return The Set of Members from among the Namespaces in this NamespaceSet 
	 *  		that have the given sequenced name
	 **/

	public NamespaceMember getMembers(String sequencedName);
	
	
	/**
	 *  Returns the Set of Members of the Namespaces in this NamespaceSet whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Members of the Namespaces in this 
	 *  		NamespaceSet
	 *  @return The Set of Members of the Namespaces in this NamespaceSet whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public Set getMembersByFullyQualifiedNamePatternMatching(String regExPattern);
	
	
	/**
	 *  Returns the Set of Members of the Namespaces in this NamespaceSet whose 
	 *  sequenced names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the sequenced names of the Members of the Namespaces in this 
	 *  		NamespaceSet
	 *  @return The Set of Members of the Namespaces in this NamespaceSet whose 
	 *  		sequenced names match the given regular expression pattern
	 **/

	public Set getMembersBySequencedNamePatternMatching(String regExPattern);
	
	
	/**
	 *  Removes the given Namespace from the Set of Namespaces in this NamespaceSet.
	 *  
	 *  @param namespace A Namespace in this NamespaceSet
	 *  @return True if the given Namespace was actually removed
	 **/

	public boolean remove(Namespace namespace);
	
	
	/**
	 *  Removes the Namespace having the given name from the Set of Namespaces in 
	 *  this NamespaceSet.
	 *  
	 *  @param name The name of some Namespace in this NamespaceSet
	 *  @return The Namespace that was removed (if any)
	 **/

	public Namespace remove(String name);
}
