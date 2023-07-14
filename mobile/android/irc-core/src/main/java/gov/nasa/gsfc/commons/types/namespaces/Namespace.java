//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: Namespace.java,v $
//	Revision 1.2  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.8  2004/06/28 17:21:16  chostetter_cvs
//	And here, days later, you _still_ can't cast a Set to a Collection. Go figure.
//	
//	Revision 1.7  2004/06/24 19:43:24  chostetter_cvs
//	Turns out you can't cast a Set to a Collection. Grrrr.
//	
//	Revision 1.6  2004/06/02 22:33:27  chostetter_cvs
//	Namespace revisions
//	
//	Revision 1.5  2004/05/27 23:29:26  chostetter_cvs
//	More Namespace related changes
//	
//	Revision 1.4  2004/05/27 19:47:45  chostetter_cvs
//	More Namespace, DataSpace changes
//	
//	Revision 1.3  2004/05/27 15:57:16  chostetter_cvs
//	Data-related changes
//	
//	Revision 1.2  2004/05/14 19:55:59  chostetter_cvs
//	Namespaces can now themselves have/occupy Namespaces
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
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
 *  A Namespace is a set of Members within which the base name of each Member is 
 *  guaranteed to be unique. A Namespace provides for automatic sequencing of the 
 *  names of Members as they are added, if necessary to generate a unique sequenced 
 *  name of the Member within the Namespace.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface Namespace extends NamespaceMember, MemberSetBean
{
	/**
	 *  Adds the given NamespaceMember to the Set of Members of this Namespace. If 
	 *  the base name of the given NamespaceMember already occurs in this Namespace, 
	 *  it will be sequenced as needed to make it unique within this Namespace.
	 *
	 *  @param member The NamespaceMember to be added to this Namespace
	 *  @return True if the given NamespaceMember was actually added to this 
	 *  		Namespace
	 **/

	public boolean add(NamespaceMember member)
		throws MembershipException;


	/**
	 *  Reserves and returns the next sequence number available for the given 
	 *  base name in this Namespace. If the given base name does not yet occur in 
	 *  this Namespace, the result will be 1. Note that this method is only 
	 *  useful for the self-updating of its sequence number by a NamespaceMember of 
	 *  this Namespace (as otherwise the call will result in that sequence number 
	 *  being unavailable for use by a new NamespaceMember of this Namespace having 
	 *  the same given base name when it is added).
	 *  
	 *  @param name The name of a NamespaceMember of this Namespace 
	 **/

	public int getNextSequenceNumber(String name);
	
	
	/**
	 *  Returns the Set of the sequenced (and thus Namespace-unique) names of all 
	 *  the Members of this Namespace.
	 *  
	 *  @return	The Set of the sequenced (and thus Namespace-unique) names of all 
	 *  		the Members of this Namespace
	 **/

	public Set getSequencedNames();
	
	
	/**
	 *  Returns the NamespaceMember of this Namespace that has the given sequenced 
	 *  (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of the NamespaceMember in this 
	 *  		Namespace to get
	 *  @return The NamespaceMember of this Namespace that has the given sequenced 
	 *  		(and thus Namespace-unique) name
	 **/

	public NamespaceMember getBySequencedName(String sequencedName);
	
	
	/**
	 *  Returns the Set of Members of this Namespace whose sequenced names match 
	 *  the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the sequenced names of the Members of this Namespace
	 *  @return The Set of Members of this Namespace whose sequenced names match 
	 *  		the given regular expression pattern
	 **/

	public Set getBySequencedNamePatternMatching(String regExPattern);
	
	
	/**
	 *  Removes the NamespaceMember having the given sequenced (and thus 
	 *  Namespace-unique) name from this Namespace.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name of 
	 *  		some NamespaceMember of this Namespace
	 *  @return The NamespaceMember that was removed (if any)
	 **/

	public NamespaceMember removeBySequencedName(String sequencedName);
}
