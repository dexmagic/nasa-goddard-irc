//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: MemberSetBean.java,v $
// Revision 1.2  2006/03/14 17:33:15  chostetter_cvs
// Added getFullyQualifiedName() to support MemberSetBean polymorphism
//
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


/**
 *  A MemberSetBean is a MemberSet that has Membership bean events.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/14 17:33:15 $
 *  @author Carl F. Hostetter
**/

public interface MemberSetBean extends HasName, MemberSet
{
	/**
	 * Returns the fully-qualifed name of this MemberSetBean.
	 * 
	 * @return The fully-qualifed name of this MemberSetBean
	 **/

	public String getFullyQualifiedName();


	/**
	 * Adds the given MembershipListener as a listener for changes in the 
	 * membership of this MemberSetBean.
	 * 
	 * @param listener A MembershipListener
	 **/

	public void addMembershipListener(MembershipListener listener);


	/**
	 * Remove the given MembershipListener as a listener for changes in the 
	 * membership of this MemberSetBean.
	 *  
	 * @param listener A MembershipListener
	 **/

	public void removeMembershipListener(MembershipListener listener);
}
