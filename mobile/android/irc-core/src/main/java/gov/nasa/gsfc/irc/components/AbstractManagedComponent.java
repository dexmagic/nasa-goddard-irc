//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/components/AbstractManagedComponent.java,v 1.7 2006/06/07 16:22:21 smaher_cvs Exp $
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

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;


/**
 *  A ManagedComponent is a Component that has a ComponentManager.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/06/07 16:22:21 $
 *  @author	Carl F. Hostetter
**/

public abstract class AbstractManagedComponent extends AbstractIrcComponent 
	implements ManagedComponent
{
	public static final String DEFAULT_NAME = "Managed Component";
	
	private ComponentManager fManager;
	
	
	/**
	 *  Constructs a new ManagedComponent whose ComponentId is the given ComponentId.
	 *  Note that the new ManagedComponent will need to have its ComponentManager 
	 *  set (if any is desired).
	 *  
	 *  @param componentId The ComponentId of the new ManagedComponent
	 **/

	protected AbstractManagedComponent(ComponentId componentId)
	{
		super(componentId);
	}
		
	
	/**
	 *	Constructs a new ManagedComponent having a default name. Note that the new 
	 *  ManagedComponent will need to have its ComponentManager set (if any is 
	 *  desired).
	 *
	 */
	
	public AbstractManagedComponent()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new ManagedComponent having the given base name. Note that the 
	 *  new ManagedComponent will need to have its ComponentManager set (if any is 
	 *  desired).
	 * 
	 *  @param name The base name of the new ManagedComponent
	 **/

	public AbstractManagedComponent(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new ManagedComponent configured according to the given 
	 *  ComponentDescriptor. Note that the new ManagedComponent will need to have 
	 *  its ComponentManager set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new ManagedComponent
	 */
	
	public AbstractManagedComponent(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 *  Sets the manager of this ManagedComponent to the given ComponentManager. 
	 *  NOTE that this ManagedComponent is not added to the given ComponentManager; 
	 *  that should be done by the caller. However, if this ManagedComponent 
	 *  already has a ComponentManager, it is first removed from that 
	 *  ComponentManager before the new ComponentManager is set.
	 *  
	 *  @param manager The new ComponentManager of this ManagedComponent
	**/

	public void setManager(ComponentManager manager)	
	{
		if (fManager != manager)
		{
			if (fManager != null)
			{
				fManager.remove(this);
			}
		
			fManager = manager;
		}
	}
	
	
	/**
	 *  Returns the ComponentManager of this ManagedComponent.
	 *  
	**/

	public ComponentManager getManager()
	{
		return (fManager);
	}
	
	
	/**
	 *  Returns a String representation of this ManagedComponent.
	 * 
	 *  @return A String representation of this ManagedComponent
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
//	$Log: AbstractManagedComponent.java,v $
//	Revision 1.7  2006/06/07 16:22:21  smaher_cvs
//	Undid making some fields transient as they are needed (at least when serializing a BasisBundleDescriptor).
//	
//	Revision 1.6  2006/06/02 19:19:40  smaher_cvs
//	Made some fields transient to avoid circular dependencies when serializing.
//	
//	Revision 1.5  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
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
//	Revision 1.10  2004/12/01 22:37:48  tames_cvs
//	Updated to reflect changes to the Composite interface. This allows the
//	ComponentManager and Composite components to use the same
//	interface.
//	
//	Revision 1.9  2004/11/10 23:09:56  chostetter_cvs
//	Initial debugging of Message formatting. Mostly works except for C-style formatting.
//	
//	Revision 1.8  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.7  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.6  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.4  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.3  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.2  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.1  2004/06/08 14:21:53  chostetter_cvs
//	Added child/parent relationship to Components
//	
