//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/DefaultComponentNamespaceManager.java,v 1.4 2006/03/14 21:26:11 chostetter_cvs Exp $
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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.types.namespaces.NamespaceMember;


/**
 *	A ComponentNamespaceManager is a manager of the Components in a 
 *	ComponentNamespace.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 21:26:11 $
 *  @author Carl F. Hostetter
 */

public class DefaultComponentNamespaceManager 
	extends DefaultComponentNamespace implements ComponentNamespaceManager
{
	private static final String CLASS_NAME = 
		DefaultComponentNamespaceManager.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Component Namespace Manager";
	
	private ComponentManager fProxiedManager = this;
	

	/**
	 *	Default constructor of a ComponentNamespaceManager. 
	 *
	 *	@param id The NamespaceMemberId of the new ComponentNamespaceManager
	 */
	
	public DefaultComponentNamespaceManager()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a ComponentNamespaceManager having the given name.
	 *
	 *	@param name The name of the new ComponentNamespaceManager
	 */
	
	public DefaultComponentNamespaceManager(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new ComponentNamespaceManager that acts as a proxy for the 
	 *  given ComponentNamespaceManager.
	 *
	 *  @param manager The ComponentNamespaceManager for which the new 
	 *  		ComponentNamespaceManager will serve as a proxy
	 */
	
	protected DefaultComponentNamespaceManager(ComponentNamespaceManager manager)
	{
		super(manager);
		
		fProxiedManager = manager;
	}
	

	/**
	 *  Adds the given NamespaceMember, which must be a ManagedComponent, to the 
	 *  Set of ManagedComponents in and managed by this ComponentNamespaceManager. 
	 *  If the base name of the given ManagedComponent already occurs in this 
	 *  ComponentNamespaceManager, it will be sequenced as needed to 
	 *  make it unique within this ComponentNamespaceManager.
	 *
	 *  @param component The ManagedComponent to be added to this 
	 *  		ComponentNamespaceManager
	 *  @return True if the given ManagedComponent was actually added to this 
	 *  		ComponentNamespaceManager
	 **/

	public final boolean add(NamespaceMember component)
	{
		boolean result = false;
		
		if (component instanceof ManagedComponent)
		{
			result = add((ManagedComponent) component);
		}
		
		return (result);
	}


	/**
	 *  Adds the given ManagedComponent to the Set of ManagedComponents in and 
	 *  managed by this ComponentNamespaceManager. If the base name of the given 
	 *  ManagedComponent already occurs in this ComponentNamespaceManager, it will 
	 *  be sequenced as needed to make it unique within this 
	 *  ComponentNamespaceManager.
	 *
	 *  @param component The ManagedComponent to be added to this 
	 *  		ComponentNamespaceManager
	 *  @return True if the given ManagedComponent was actually added to this 
	 *  		ComponentNamespaceManager
	 **/

	public boolean add(ManagedComponent component)
	{
		ComponentManager previousManager = component.getManager();
		
		if (fProxiedManager != null)
		{
			component.setManager(fProxiedManager);
		}
		else
		{
			component.setManager(this);
		}
		
		boolean result = super.add(component);
		
		if (result != true)
		{
			component.setManager(previousManager);
		}
		
		return (result);
	}


	/**
	 *  Removes the given NamespaceMember, which must be a ManagedComponent, from 
	 *  the Set of ManagedComponents in and managed by this 
	 *  ComponentNamespaceManager.
	 *
	 *  @param component The ManagedComponent to be removed from this 
	 *  		ComponentNamespaceManager
	 *  @return True if the given ManagedComponent was actually removed from this 
	 *  		ComponentNamespaceManager
	 **/

	public final boolean remove(NamespaceMember component)
	{
		boolean result = false;
		
		if (component instanceof ManagedComponent)
		{
			result = remove((ManagedComponent) component);
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes the given NamespaceMember, which must be a ManagedComponent, from 
	 *  the Set of ManagedComponents in and managed by this 
	 *  ComponentNamespaceManager.
	 *
	 *  @param component The ManagedComponent to be removed from this 
	 *  		ComponentNamespaceManager
	 *  @return True if the given ManagedComponent was actually removed from this 
	 *  		ComponentNamespaceManager
	 **/

	public boolean remove(ManagedComponent component)
	{
		boolean result = super.remove(component);
		
		if (result == true)
		{
			component.setManager(null);
		}
		
		return (result);
	}
	
	
	/**
	 *	Starts the Component managed by this ComponentNamespaceManager that has the 
	 *	given ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this 
	 *  		ComponentNamespaceManager
	 */
	
	public void startComponent(ComponentId id)
	{
		try
		{
			Startable component = (Startable) get(id);
			
			if (component != null)
			{
				component.start();
			}
			else
			{
				String message = "The Component with ComponentId = " + id +
					" is not currently managed by " + this;
				
				if (sLogger.isLoggable(Level.WARNING))
				{
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"startComponent", message);
				}
				
				throw (new IllegalArgumentException(message));
			}
		}
		catch (ClassCastException ex)
		{
			
		}
	}
	
	
	/**
	 *	Starts the Component managed by this ComponentNamespaceManager that has the 
	 *	given fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentNamespaceManager
	 *  @throws IllegalArgumentException if no Component having the given fully-
	 *  		qualified name is managed by this ComponentNamespaceManager
	 */
	
	public final void startComponent(String fullyQualifiedName)
	{
		ComponentId id = getComponentId(fullyQualifiedName);
		
		if (id != null)
		{
			startComponent(id);
		}
		else
		{
			String message = "No Component with fully-qualified name = " + 
				fullyQualifiedName + " is currently managed by " + this;
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "startComponent", message);
			}
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 *	Starts all of the Components managed by this ComponentNamespaceManager.
	 *
	 */
	
	public void startAllComponents()
	{
		synchronized (getConfigurationChangeLock())
		{
			Iterator components = iterator();
			
			while (components.hasNext())
			{
				try
				{
					Startable component = (Startable) components.next();
					
					component.start();
				}
				catch (ClassCastException ex)
				{
					
				}
			}
		}
	}


	/**
	 *	Stops the Component managed by this ComponentNamespaceManager that has the 
	 *  given ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this 
	 *  		ComponentNamespaceManager
	 *  @throws IllegalArgumentException if no Component having the given fully-
	 *  		qualified name is managed by this ComponentNamespaceManager
	 */
	
	public void stopComponent(ComponentId id)
	{
		try
		{
			Startable component = (Startable) get(id);
			
			if (component != null)
			{
				component.stop();
			}
			else
			{
				String message = "The Component with ComponentId = " + id +
					" is not currently managed by " + this;
				
				if (sLogger.isLoggable(Level.WARNING))
				{
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"stopComponent", message);
				}
				
				throw (new IllegalArgumentException(message));
			}
		}
		catch (ClassCastException ex)
		{
			
		}
	}
	
	
	/**
	 *	Stops the Component managed by this ComponentNamespaceManager that has the 
	 *  given fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentNamespaceManager
	 */
	
	public final void stopComponent(String fullyQualifiedName)
	{
		ComponentId id = getComponentId(fullyQualifiedName);
		
		if (id != null)
		{
			stopComponent(id);
		}
		else
		{
			String message = "No Component with fully-qualified name = " + 
				fullyQualifiedName + " is currently managed by " + this;
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "stopComponent", message);
			}
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 *	Stops all of the Components managed by this ComponentNamespaceManager.
	 *
	 */
	
	public void stopAllComponents()
	{
		synchronized (getConfigurationChangeLock())
		{
			Iterator components = iterator();
			
			while (components.hasNext())
			{
				try
				{
					Startable component = (Startable) components.next();
					
					component.stop();
				}
				catch (ClassCastException ex)
				{
					
				}
			}
		}
	}


	/**
	 *	Kills the Component managed by this ComponentNamespaceManager that has the 
	 *  given ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this 
	 *  		ComponentNamespaceManager
	 */
	
	public void killComponent(ComponentId id)
	{
		try
		{
			Startable component = (Startable) get(id);
			
			if (component != null)
			{
				component.kill();
			}
			else
			{
				String message = "The Component with ComponentId = " + id +
					" is not currently managed by " + this;
				
				if (sLogger.isLoggable(Level.WARNING))
				{
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"killComponent", message);
				}
				
				throw (new IllegalArgumentException(message));
			}
		}
		catch (ClassCastException ex)
		{
			
		}
	}
	
	
	/**
	 *	Kills the Component managed by this ComponentNamespaceManager that has the 
	 *	given fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentNamespaceManager
	 */
	
	public final void killComponent(String fullyQualifiedName)
	{
		ComponentId id = getComponentId(fullyQualifiedName);
		
		if (id != null)
		{
			killComponent(id);
		}
		else
		{
			String message = "No Component with fully-qualified name = " + 
				fullyQualifiedName + " is currently managed by " + this;
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "killComponent", message);
			}
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 *	Kills all of the Components managed by this ComponentNamespaceManager.
	 *
	 */
	
	public void killAllComponents()
	{
		synchronized (getConfigurationChangeLock())
		{
			Iterator components = iterator();
			
			while (components.hasNext())
			{
				try
				{
					Startable component = (Startable) components.next();
					
					component.kill();
				}
				catch (ClassCastException ex)
				{
					
				}
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultComponentNamespaceManager.java,v $
//	Revision 1.4  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
//	
//	Revision 1.3  2006/03/14 20:28:38  tames_cvs
//	Changed order the manager was set.
//	
//	Revision 1.2  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	
