//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/AbstractManagedCompositeComponent.java,v 1.5 2006/03/14 17:33:33 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;


/**
 *  A ManagedCompositeComponent is a CompositeComponent that has a Manager.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 17:33:33 $
 *  @author Carl F. Hostetter
 */

public abstract class AbstractManagedCompositeComponent 
	extends AbstractCompositeComponent implements ManagedCompositeComponent
{
	public static final String DEFAULT_NAME = "Managed Composite Component";
	
	private ComponentManager fManager;
	
	
	/**
	 *  Constructs a new ManagedCompositeComponent whose ComponentId is the given 
	 *  ComponentId. Note that the new ManagedCompositeComponent will need to have 
	 *  its ComponentManager set (if any is desired).
	 *  
	 *  @param componentId The Component of the new ManagedCompositeComponent
	 **/

	protected AbstractManagedCompositeComponent(ComponentId componentId)
	{
		super(componentId);
	}
		
	
	/**
	 *	Constructs a new ManagedCompositeComponent having a default name. Note that 
	 *  the new ManagedCompositeComponent will need to have its ComponentManager 
	 *  set (if any is desired).
	 *
	 */
	
	public AbstractManagedCompositeComponent()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new ManagedCompositeComponent having the given base name. Note 
	 *  that the new ManagedCompositeComponent will need to have its 
	 *  ComponentManager set (if any is desired).
	 * 
	 *  @param name The base name of the new ManagedCompositeComponent
	 **/

	public AbstractManagedCompositeComponent(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new ManagedCompositeComponent configured according to the given 
	 *  ComponentDescriptor. Note that the new ManagedCompositeComponent will need 
	 *  to have its ComponentManager set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new 
	 *  		ManagedCompositeComponent
	 */
	
	public AbstractManagedCompositeComponent(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 *  Sets the manager of this ManagedCompositeComponent to the given 
	 *  ComponentManager. NOTE that this ManagedCompositeComponent is not added to 
	 *  the given ComponentManager; that should be done by the caller. However, if 
	 *  this ManagedCompositeComponent already has a ComponentManager, it is first 
	 *  removed from that ComponentManager before the new ComponentManager is set.
	 *  
	 *  @param manager The new ComponentManager of this ManagedComponent
	**/

	public void setManager(ComponentManager manager)	
	{
		if ((fManager != null) && (fManager != manager) && 
			! fManager.equals(manager))
		{
			fManager.remove(this);
		}
		
		fManager = manager;
	}
	
	
	/**
	 *  Returns the parent CompositeComponent of this ManagedCompositeComponent.
	 *  
	**/

	public ComponentManager getManager()
	{
		return (fManager);
	}
	
	
	/**
	 *  Returns a String representation of this ManagedCompositeComponent.
	 * 
	 *  @return A String representation of this ManagedCompositeComponent
	 */
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(super.toString());
		
		if (fManager != null)
		{
			buffer.append("\nCurrently managed by: " + 
				fManager.getFullyQualifiedName());
		}
		else
		{
			buffer.append("\nCurrently unmanaged");
		}
		
		return (buffer.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractManagedCompositeComponent.java,v $
//	Revision 1.5  2006/03/14 17:33:33  chostetter_cvs
//	Beefed up toString() method
//	
//	Revision 1.4  2006/03/14 14:57:16  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.3  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.2  2006/01/24 15:55:14  chostetter_cvs
//	Changed default ComponentManager behavior, default is now none
//	
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	
