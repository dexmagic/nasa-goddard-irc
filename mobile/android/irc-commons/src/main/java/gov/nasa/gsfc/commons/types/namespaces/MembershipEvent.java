//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: MembershipEvent.java,v $
//	Revision 1.2  2006/01/24 16:21:19  tames_cvs
//	Fixed wasRemoved method.
//	
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

import java.util.EventObject;


/**
 *  A MembershipEvent is an Event that indicates a change in the membership of 
 *  a MemberSet.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/24 16:21:19 $
 *  @author Carl F. Hostetter
**/

public class MembershipEvent extends EventObject
{
	private Member fMember = null;
	private boolean fIsMember = false;
	
	/**
	 * Constructs a new MembershipEvent indicating a change in the membership of 
	 * the given MemberSet.
	 *
	 * @param memberSet The MemberSet to which the event applies
	 **/
	
	public MembershipEvent(MemberSet memberSet)
	{
		super(memberSet);
	}
	
	
	/**
	 * Constructs a new MembershipEvent indicating that the Member indicated by 
	 * the given MemberId was either added to or removed from membership in the 
	 * given MemberSet, according to the value of the given flag.
	 *
	 * @param memberSet The MemberSet to which the event applies
	 * @param member The Member that has either been added to or removed from the 
	 * 		givem MemberSet
	 * @param isMember True if the given Member was added to the given MemberSet, 
	 * 		false if it was removed
	 **/
	
	public MembershipEvent(MemberSet memberSet, Member member, boolean isMember)
	{
		super(memberSet);
		
		fMember = member;
		fIsMember = isMember;
	}
	
	
	/**
	 * Returns the MemberId associated with this MembershipEvent (if any).
	 *
	 * @return The MemberId associated with this MembershipEvent (if any)
	 **/
	
	public Member getMember()
	{
		return (fMember);
	}
	
	
	/**
	 * Returns true if the Member indicated by the associated MemberId of this 
	 * MembershipEvent (if any) was added to the associated MemberSet, false if the 
	 * Member (if any) was removed. Note that if there is no such associated 
	 * MemberId, the value will always be false, and will indicate nothing.
	 *
	 * @return True if the Member indicated by the associated MemberId of this 
	 * 		MembershipEvent (if any) was added to the associated MemberSet, false 
	 * 		if the Member (if any) was removed
	 **/
	
	public boolean wasAdded()
	{
		return (fMember != null && fIsMember);
	}
	
	
	/**
	 * Returns true if the Member indicated by the associated MemberId of this 
	 * MembershipEvent (if any) was removed from the associated MemberSet, false if 
	 * the Member (if any) was added. Note that if there is no such associated 
	 * MemberId, the value will always be false, and will indicate nothing.
	 *
	 * @return True if the Member indicated by the associated MemberId of this 
	 * 		MembershipEvent (if any) was removed from the associated MemberSet, 
	 * 		false if the Member (if any) was added
	 **/
	
	public boolean wasRemoved()
	{
		return (fMember != null && !fIsMember);
	}
}
