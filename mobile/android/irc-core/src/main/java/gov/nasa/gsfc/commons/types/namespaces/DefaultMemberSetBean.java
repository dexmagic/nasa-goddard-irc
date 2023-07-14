//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: DefaultMemberSetBean.java,v $
// Revision 1.4  2006/03/14 17:33:33  chostetter_cvs
// Beefed up toString() method
//
// Revision 1.3  2006/03/14 14:53:43  chostetter_cvs
// Now relays MembershipEvents from Members that are themselves MemberSetBeans
//
// Revision 1.2  2006/03/13 15:45:09  chostetter_cvs
// Allows for proxy MemberSetBeans
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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*

*/


/**
 *  A MemberSetBean is a MemberSet that has Membership bean events.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/14 17:33:33 $
 *  @author Carl F. Hostetter
**/

public class DefaultMemberSetBean extends DefaultMemberSet implements MemberSetBean, 
	MembershipListener
{
	private String fName = HasName.DEFAULT_NAME;
	MemberSetBean fSource = this;
	
	private transient List fMembershipListeners = new CopyOnWriteArrayList();
	

	/**
	 *	Default constructor of a new MemberSetBean.
	 *
	 */
	
	public DefaultMemberSetBean()
	{

	}
	

	/**
	 *	Constructs a new MemberSetBean having the given name.
	 *
	 *	@param name The name of the new MemberSetBean
	 */
	
	public DefaultMemberSetBean(String name)
	{
		if (name != null)
		{
			fName = name;
		}
		else
		{
			fName = HasName.DEFAULT_NAME;
		}
	}


	/**
	 *	Constructs a new MemberSetBean having a default name and acting as a proxy 
	 *  for the given MemberSetBean.
	 *
	 *	@param source The source of the MembershipEvents fired by this MemberSetBean
	 */
	
	public DefaultMemberSetBean(MemberSetBean source)
	{
		if (source != null)
		{
			fSource = source;
		}
		else
		{
			fSource = this;
		}
	}


	/**
	 *	Constructs a new MemberSetBean having the given name and acting as a proxy 
	 *  for the given MemberSetBean.
	 *
	 *	@param name The name of the new MemberSetBean
	 *	@param source The source of the MembershipEvents fired by this MemberSetBean
	 */
	
	public DefaultMemberSetBean(String name, MemberSetBean source)
	{
		this(name);
		
		if (source != null)
		{
			fSource = source;
		}
		else
		{
			fSource = this;
		}
	}


	/**
	 *	Returns the name of this MemberSetBean.
	 *
	 */
	
	public final String getName()
	{
		return (fName);
	}
	

	/**
	 *	Returns the fully-qualfied name of this MemberSetBean. Here, this is the 
	 *  same as its (base) name.
	 *
	 */
	
	public final String getFullyQualifiedName()
	{
		return (fName);
	}
	

	/**
	 * Adds the given MembershipListener as a listener for changes in the 
	 * membership of this ComponentManager.
	 * 
	 * @param listener A MembershipListener
	 **/

	public final void addMembershipListener(MembershipListener listener)
	{
		if (listener != null)
		{
			fMembershipListeners.add(listener);
		}
	}


	/**
	 * Remove the given MembershipListener as a listener for changes in the 
	 * membership of this ComponentManager.
	 *  
	 * @param listener A MembershipListener
	 **/

	public final void removeMembershipListener(MembershipListener listener)
	{
		if (listener != null)
		{
			fMembershipListeners.remove(listener);
		}
	}
	
	
	/**
	 *  Sends the given MembershipEvent to all of the MembershipListeners of this 
	 *  ComponentManager.
	 *  
	 *  @param event A MembershipEvent
	 **/

	protected final void fireMembershipEvent(MembershipEvent event)
	{
		synchronized (getConfigurationChangeLock())
		{
			Iterator listeners = fMembershipListeners.iterator();
			
			while (listeners.hasNext())
			{
				MembershipListener listener = (MembershipListener) listeners.next();
				
				listener.receiveMembershipEvent(event);
			}
		}
	}
	
	
	/**
	 *  Adds the given Member to this MemberSet.
	 *  
	 *  @param member The Member to add to this MemberSet
	 *  @return True if the given Member was actually added to this MemberSet
	 **/

	public boolean add(Member member)
	{
		boolean added = super.add(member);
		
		if (added == true)
		{
			fireMembershipEvent(new MembershipEvent(fSource, member, true));
			
			if ((member instanceof MemberSetBean) && (member != this))
			{
				((MemberSetBean) member).addMembershipListener(this);
			}
		}
		
		return (added);
	}
	
	
	/**
	 *  Removes the given Member from this MemberSet.
	 * 
	 *  @param member The Member to be removed
	 *  @return True if the given Member was actually removed
	 */

	public boolean remove(Member member)
	{
		boolean result = super.remove(member);
		
		if (result == true)
		{
			fireMembershipEvent(new MembershipEvent(fSource, member, false));
		}
		
		return (result);
	}


	/**
	 * Causes this MemberSetBean to receive the given MembershipEvent from one of 
	 * it Members.
	 * 
	 * <p>Here, simply refires the given event to the MembershipListeners of this 
	 * MemberSetBean
	 *
	 * @param event A MembershipEvent
	 **/
	
	public void receiveMembershipEvent(MembershipEvent event)
	{
		fireMembershipEvent(event);
	}
	
	
	/**
	 *	Returns a String representation of this MemberSetBean. 
	 *  
	 *  @return A String representation of this MemberSetBean
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(getFullyQualifiedName() + ":\n" + 
			super.toString());
		
		return (stringRep.toString());
	}
}
