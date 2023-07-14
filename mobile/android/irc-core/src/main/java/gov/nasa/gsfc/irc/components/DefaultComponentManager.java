//=== File Prolog ============================================================
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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberSetBean;
import gov.nasa.gsfc.commons.types.namespaces.Member;
import gov.nasa.gsfc.commons.types.namespaces.NamespaceMember;


/**
 * A ComponentManager is a bean that contains and manages a Set of Components.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/14 21:26:11 $
 *  @author Carl F. Hostetter
 */

public class DefaultComponentManager extends DefaultMemberSetBean 
	implements ComponentManager
{
	private static final String CLASS_NAME = DefaultComponentManager.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private ComponentManager fProxiedManager = this;
	
	
	/**
	 *	Default constructor of a new ComponentManager.
	 *
	 */
	
	public DefaultComponentManager()
	{
		this("Default Component Manager");
	}
	

	/**
	 *	Constructs a new ComponentManager having the given name.
	 *
	 *	@param name The name of the new ComponentManager
	 */
	
	public DefaultComponentManager(String name)
	{
		super(name);
	}


	/**
	 *	Constructs a new ComponentManager that acts as a proxy for the given 
	 *  ComponentManager.
	 *
	 *  @param manager The ComponentManager for which the new ComponentManager will 
	 *  		serve as a proxy
	 */
	
	public DefaultComponentManager(ComponentManager manager)
	{
		fProxiedManager = manager;
	}
	

	/**
	 *  Adds the given ManagedComponent to this ComponentManager.
	 *  
	 *  @return True if the given Component was actually added
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
	 *  Adds the given Member, which must be a ManagedComponent, to this 
	 *  ComponentManager.
	 *  
	 *  @param component The ManagedComponent to add to this ComponentManager
	 *  @return True if the given ManagedComponent was actually added
	 **/

	public final boolean add(Member component)
	{
		boolean result = false;
		
		if (component instanceof ManagedComponent)
		{
			result = add((ManagedComponent) component);
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given Component, which must be a ManagedComponent, to this 
	 *  ComponentManager.
	 *  
	 *  @param component The ManagedComponent to add to this ComponentManager
	 *  @return True if the given ManagedComponent was actually added
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
	 *  Adds the given Component, which must be a ManagedComponent, to this 
	 *  ComponentManager.
	 *  
	 *  @return True if the given ManagedComponent was actually added
	 **/

	public final boolean addComponent(MinimalComponent component)
	{
		boolean result = false;
		
		if (component instanceof ManagedComponent)
		{
			result = add((ManagedComponent) component);
		}
		
		return (result);
	}


	/**
	 *  Returns the Set of Components managed by this ComponentManager.
	 *  
	 *  @return The Set of Components managed by this ComponentManager
	 */

	public final Set getComponents()
	{
		return (super.getMembers());
	}
	
	
	/**
	 *  Removes the given ManagedComponent from the Set of ManagedComponents in and 
	 *  managed by this ComponentManager.
	 *
	 *  @param component The ManagedComponent to remove from this ComponentManager
	 *  @return True if the given ManagedComponent was actually removed
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
	 *  Removes the given Member, which must be a ManagedComponent, from the Set of 
	 *  ManagedComponents in and managed by this ComponentManager.
	 *
	 *  @param component The ManagedComponent to remove from this ComponentManager
	 *  @return True if the given ManagedComponent was actually removed
	 **/

	public final boolean remove(Member component)
	{
		boolean result = false;
		
		if (component instanceof ManagedComponent)
		{
			remove((ManagedComponent) component);
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes the given Component, which must be a ManagedComponent, from the 
	 *  Set of ManagedComponents in and managed by this ComponentManager. The 
	 *  given ManagedComponent Component will also be stopped.
	 *
	 *  @param component The ManagedComponent to be removed from this 
	 *  		ComponentManager
	 *  @return True if the given ManagedComponent was actually removed
	 **/

	public final boolean removeComponent(MinimalComponent component)
	{
		boolean result = false;
		
		if (component instanceof ManagedComponent)
		{
			result = remove((ManagedComponent) component);
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes the Component having the given ComponentId from the Set of 
	 *  Components of this ComponentManager.
	 *
	 *  @param id The ComponentId of the Component to be removed from this 
	 *  		ComponentManager
	 *  @return The Component that was removed
	 **/

	public final MinimalComponent removeComponent(ComponentId id)
	{
		ManagedComponent result = (ManagedComponent) getComponent(id);
		
		if (result != null)
		{
			remove(result);
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from the Set of Components of this ComponentManager.
	 *
	 *  @param fullyQualifiedName The fully-qualified name of the Component to be 
	 *  		removed from this ComponentManager
	 *  @return The Component that was removed
	 **/

	public final MinimalComponent removeComponent(String fullyQualifiedName)
	{
		ManagedComponent result = (ManagedComponent) 
			getComponent(fullyQualifiedName);
		
		if (result != null)
		{
			remove(result);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the Component in the Set of Components managed by this 
	 *  ComponentManager that has the given fully-qualified (and thus globally 
	 *  unique) name. If there is no such Component, the result is null.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
	 *  @return The Component in the Set of Components managed by this 
	 *  		ComponentManager that has the given fully-qualified (and thus globally 
	 *  		unique) name
	 */

	public final MinimalComponent getComponent(String fullyQualifiedName)
	{
		return ((MinimalComponent) super.get(fullyQualifiedName));
	}


	/**
	 *  Returns the Component in the Set of Components managed by this 
	 *  ComponentManager that has the given ComponentId. If there is no such 
	 *  Component, the result is null.
	 *
	 *  @param id The ComponentId of some Component managed by this ComponentManager
	 *  @return The Component in the Set of Components managed by this 
	 *  		ComponentManager that has the given ComponentId
	 **/

	public final MinimalComponent getComponent(ComponentId id)
	{
		return ((MinimalComponent) super.get(id));
	}
	
	
	/**
	 *	Returns the ComponentId of the Component in the Set of Components managed 
	 *  by this ComponentManager that has the given fully-qualified (and thus 
	 *  globally unique) name. If there is no such Component, the result will be 
	 *  null.
	 *
	 *	@param fullyQualifiedName The fully qualified (and thus globally unique) 
	 *		name of some Component managed by this ComponentManager
	 *	@return	The ComponentId of the Component in the Set of Components managed 
	 *  		by this ComponentManager that has the given fully-qualified (and thus 
	 *  		globally unique) name
	 */

	public final ComponentId getComponentId(String fullyQualifiedName)
	{
		return ((DefaultComponentId) getMemberId(fullyQualifiedName));
	}
	
	
	/**
	 *	Returns the ComponentId of the Component in the Set of Components managed 
	 *  by this ComponentManager that has the given fully-qualified (and thus 
	 *  globally unique) name. If there is no such Component, the result 
	 *  will be null.
	 *
	 *	@param fullyQualifiedName The fully qualified (and thus globally unique) 
	 *		name of some Component
	 *	@return	The ComponentId of the Component in the Set of Components managed 
	 *		by this ComponentManager that has the given fully-qualified (and thus 
	 *		globally unique) name
	 */

	public final ComponentId getComponentIdByFullyQualifiedName
		(String fullyQualifiedName)
	{
		return ((DefaultComponentId) getMemberId(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of ComponentIds of the Components managed by this 
	 *  ComponentManager.
	 *  
	 *  @return The Set of ComponentIds of the Components managed by this 
	 *  		ComponentManager
	 **/

	public final Set getComponentIds()
	{
		return (getMemberIds());
	}
	
	
	/**
	 *  Returns a Map of the Components managed by this ComponentManager keyed by 
	 *  their ComponentIds.
	 *
	 *  @return A Map of the Components managed by this ComponentManager keyed by 
	 *  		their ComponentIds
	 */

	public final Map getComponentsByComponentId()
	{
		return (getMembersByMemberId());
	}
	
	
	/**
	 *  Returns the Set of Components managed by this ComponentManager whose base 
	 *  names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Components managed by this ComponentManager
	 *  @return The Set of Components managed by this ComponentManager whose base 
	 *  		names match the given regular expression pattern
	 **/

	public final Set getComponentByBaseNamePatternMatching(String regExPattern)
	{
		return (getByBaseNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns a Map of the Components managed by this ComponentManager keyed by 
	 *  their fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Components managed by this ComponentManager keyed by 
	 *  		their fully-qualified (and thus globally unique) names
	 */

	public final Map getComponentsByFullyQualifiedName()
	{
		return (getMembersByFullyQualifiedName());
	}
	
	
	/**
	 *  Returns the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name. If no such Component exists, 
	 *  the result is null.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component
	 *  @return The Component managed by this ComponentManager that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public final MinimalComponent getComponentByFullyQualifiedName
		(String fullyQualifiedName)
	{
		return ((MinimalComponent) getByFullyQualifiedName(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Components managed by this ComponentManager whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Components managed by this 
	 *  		ComponentManager
	 *  @return The Set of Components managed by this ComponentManager whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public final Set getComponentsByFullyQualifiedNamePatternMatching(String regExPattern)
	{
		return (getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *	Starts the Component managed by this ComponentManager that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this ComponentManager
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
	 *	Starts the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
	 *  @throws IllegalArgumentException if no Component having the given fully-
	 *  		qualified name is managed by this ComponentManager
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
	 *	Starts all of the Components managed by this ComponentManager.
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
	 *	Stops the Component managed by this ComponentManager that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this ComponentManager
	 *  @throws IllegalArgumentException if no Component having the given fully-
	 *  		qualified name is managed by this ComponentManager
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
	 *	Stops the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
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
	 *	Stops all of the Components managed by this ComponentManager.
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
	 *	Kills the Component managed by this ComponentManager that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this ComponentManager
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
	 *	Kills the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
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
	 *	Kills all of the Components managed by this ComponentManager.
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
	
	
	/**
	 *  Returns a String representation of this ComponentManager.
	 * 
	 *  @return A String representation of this ComponentManager
	 */
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		if (fProxiedManager != null)
		{
			buffer.append(fProxiedManager.getFullyQualifiedName());
		}
		else
		{
			buffer.append(getFullyQualifiedName());
		}
		
		if (size() > 0)
		{
			buffer.append(" currently manages " + size() + " components:");
			
			Iterator components = iterator();
			
			while (components.hasNext())
			{
				for (int i = 1; components.hasNext(); i++)
				{
					MinimalComponent component = (MinimalComponent) 
						components.next();
					
					String sequencedName = component.getSequencedName();
					
					buffer.append("\n" + i + ": " + sequencedName);
				}
			}
		}
		else
		{
			buffer.append(" currently manages no components");
		}
		
		return (buffer.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultComponentManager.java,v $
//	Revision 1.17  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
//	
//	Revision 1.16  2006/03/14 20:28:38  tames_cvs
//	Changed order the manager was set.
//	
//	Revision 1.15  2006/03/14 17:33:33  chostetter_cvs
//	Beefed up toString() method
//	
//	Revision 1.14  2006/03/14 14:57:16  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.13  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.12  2005/11/14 21:20:34  tames_cvs
//	Added getNestedComponent method place holder.
//	
//	Revision 1.11  2005/11/09 20:04:57  tames_cvs
//	Modified event publishing to use the CopyOnWriteArrayList class to
//	hold listeners. This reduces the overhead when publishing events.
//	Also added missing getListeners method.
//	
//	Revision 1.10  2005/07/12 17:08:56  tames
//	Fixed removeComponents method for potential exception state.
//	
//	Revision 1.9  2005/04/06 14:59:46  chostetter_cvs
//	Adjusted logging levels
//	
//	Revision 1.8  2005/02/07 15:33:04  tames_cvs
//	Made ComponentManager an interface and added a
//	DefaultComponentManager implementation.
//	
//	Revision 1.15  2005/01/21 02:56:42  smaher_cvs
//	Made fire[Add,Remove]ComponentEvent public to allow for
//	additions/removals of components that aren't necessarily
//	at the root level.
//	
//	Revision 1.14  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.13  2004/12/01 22:37:48  tames_cvs
//	Updated to reflect changes to the Composite interface. This allows the
//	ComponentManager and Composite components to use the same
//	interface.
//	
//	Revision 1.12  2004/11/22 18:38:48  tames
//	Reverted back the last modification since adding a component was
//	causing duplicate events.
//	
//	Revision 1.11  2004/11/19 16:25:02  tames_cvs
//	Fixed bug. Adding a component now fires an event.
//	
//	Revision 1.10  2004/08/23 13:54:57  tames
//	Added support for Startable components that are not IrcComponents.
//	
//	Revision 1.9  2004/07/22 20:14:58  chostetter_cvs
//	Data, Component namespace work
//	
//	Revision 1.8  2004/07/22 17:06:55  chostetter_cvs
//	Namespace-related changes
//	
//	Revision 1.7  2004/07/17 01:25:58  chostetter_cvs
//	Refactored test algorithms
//	
//	Revision 1.6  2004/07/16 00:55:15  chostetter_cvs
//	ComponentManager now issues membership events
//	
//	Revision 1.5  2004/07/16 00:23:20  chostetter_cvs
//	Refactoring of DataSpace, Output wrt BasisBundle collections
//	
//	Revision 1.4  2004/07/15 17:48:55  chostetter_cvs
//	ComponentManager, property change work
//	
//	Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.2  2004/06/03 04:17:09  chostetter_cvs
//	DefaultComponentNamespace, Manager changes
//	
//	Revision 1.6  2004/06/03 01:51:27  chostetter_cvs
//	More Namespace tweaks
//	
//	Revision 1.5  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.4  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.3  2004/05/29 03:56:19  chostetter_cvs
//	Added default name
//	
//	Revision 1.2  2004/05/27 16:10:02  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//		  
