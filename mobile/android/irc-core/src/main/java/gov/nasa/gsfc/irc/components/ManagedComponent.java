//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/ManagedComponent.java,v 1.1 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.components;


/**
 *  A ManagedComponent is a Component that has a Manager.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:51 $
 *  @author	Carl F. Hostetter
**/

public interface ManagedComponent extends IrcComponent, HasManager
{
	/**
	 *  Sets the manager of this ManagedComponent to the given ComponentManager. 
	 *  NOTE that this ManagedComponent is not added to the given ComponentManager; 
	 *  that should be done by the caller. However, if this ManagedComponent 
	 *  already has a ComponentManager, it is first removed from that 
	 *  ComponentManager before the new ComponentManager is set.
	 *  
	 *  @param manager The new ComponentManager of this ManagedComponent
	**/

	public void setManager(ComponentManager manager);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: ManagedComponent.java,v $
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.4  2004/11/28 16:54:09  tames
//	Changed interface so that a ManagedComponent does not have to be an
//	IrcComponent.
//	
//	Revision 1.3  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.2  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.1  2004/06/08 14:21:53  chostetter_cvs
//	Added child/parent relationship to Components
//	
