//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: NamespaceMember.java,v $
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

import java.beans.PropertyVetoException;


/**
 *  A NamespaceMember is a Member that can belong to a Namespace, and within 
 *  which it will have a unique, possibly sequenced, name.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface NamespaceMember extends Member, HasSequencedName, HasNamespace
{
	/**
	 *  Returns the NamespaceMemberId of this NamespaceMember.
	 *
	 *  @return The NamespaceMemberId of this NamespaceMember
	 **/

	public NamespaceMemberId getNamespaceMemberId();
	
	
	/**
	 *  Returns true if this NamespaceMember is currently a member of the given 
	 *  Namespace, false otherwise.
	 *
	 *  @param namespace A Namespace
	 *  @return True if this NamespaceMember is currently a member of the given 
	 *  	Namespace, false otherwise
	 **/

	public boolean isMember(Namespace namespace);
	
	
	/**
	 *  Sets the base name of this NamespaceMember to the given name. If the name is 
	 *  not unique in the Namespace this NamespaceMember occupies, a unique 
	 *  sequenced name will be generated for it. If the attempted name change is 
	 *  vetoed by some vetoable change listener on this NamespaceMember (if any), a 
	 *  PropertyVetoException is thrown and no change is made. Otherwise, the 
	 *  change is reported to any name property change listeners.
	 *
	 *  @param name The desired new base name of this NamespaceMember
	 *  @return The new, Namespace-unique (possibly sequenced) form of the name of 
	 *  		this NamespaceMember
	 *  @throws PropertyVetoException if the attempted name change is vetoed
	 **/

	public void setName(String name)
		throws PropertyVetoException;
}
