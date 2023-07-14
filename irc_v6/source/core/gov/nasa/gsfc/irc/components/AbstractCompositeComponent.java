//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/AbstractCompositeComponent.java,v 1.5 2006/03/14 21:26:11 chostetter_cvs Exp $
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.processing.activity.ActivityState;
import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.commons.types.namespaces.Member;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.commons.types.namespaces.MembershipListener;
import gov.nasa.gsfc.commons.types.namespaces.NamespaceMember;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;


/**
 *	A CompositeComponent is a Component that is itself composed of and manages 
 *  a Namespace of one or more ManagedComponents.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 21:26:11 $
 *  @author Carl F. Hostetter
 */

public abstract class AbstractCompositeComponent extends AbstractIrcComponent 
	implements CompositeComponent, PropertyChangeListener
{
	public static final String DEFAULT_NAME = "Composite Component";
	
	/**
	 *  The Namespace of Components of this Composite.
	 */
	private DefaultComponentNamespaceManager fComponents;

	
//----------------------------------------------------------------------
//	Construction-related methods
//----------------------------------------------------------------------

	/**
	 *  Constructs a new CompositeComponent whose ComponentId is the given 
	 *  ComponentId.
	 *  
	 *  @param componentId The Component of the new CompositeComponent
	 **/

	protected AbstractCompositeComponent(ComponentId componentId)
	{
		super(componentId);
		
		fComponents = new DefaultComponentNamespaceManager(this);
	}
		
	
	/**
	 *	Constructs a new CompositeComponent having a default name.
	 *
	 */
	
	public AbstractCompositeComponent()
	{
		super(DEFAULT_NAME);
		
		fComponents = new DefaultComponentNamespaceManager(this);
	}
	
	
	/**
	 *  Constructs a new CompositeComponent having the given base name.
	 * 
	 *  @param name The base name of the new CompositeComponent
	 **/

	public AbstractCompositeComponent(String name)
	{
		super(name);
		
		fComponents = new DefaultComponentNamespaceManager(this);
	}
	
	
	/**
	 *	Constructs a new CompositeComponent configured according to the given 
	 *  ComponentDescriptor.
	 *
	 *  @param descriptor The ComponentDescriptor of the new CompositeComponent
	 */
	
	public AbstractCompositeComponent(ComponentDescriptor descriptor)
	{
		super(descriptor);
		
		fComponents = new DefaultComponentNamespaceManager(this);
	}	
	

//----------------------------------------------------------------------
//	Object-related methods
//----------------------------------------------------------------------

	/**
	 *  Returns a String representation of this CompositeComponent.
	 * 
	 *  @return A String representation of this CompositeComponent
	 */
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(super.toString());
		
		if ((fComponents != null) && (fComponents.size() > 0))
		{
			buffer.append("\nHas " + fComponents.size() + " sub-components:");
			
			Iterator components = fComponents.iterator();
			
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
			buffer.append("\nHas no sub-components");
		}
		
		return (buffer.toString());
	}
	
	
//----------------------------------------------------------------------
//	State-related methods
//----------------------------------------------------------------------

	/**
	 *  Causes this Composite to start.
	 *  
	 *  <p>Here, simply starts all of the Components in the Composite.
	 */
	
	public void start()
	{
		try
		{
			super.start();
			
			Iterator components = fComponents.iterator();
			
			while (components.hasNext())
			{
				Object component = components.next();
				
				if (component instanceof IrcComponent)
				{
					((IrcComponent) component).start();
				}
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}
	}


	/**
	 *  Causes this Composite to stop. The Composite should gracefully 
	 *  cease activity and free any resources not needed should the 
	 *  Composite subsequently be started again.
	 *  
	 *  <p>Here, simply stops all of the Components in the Composite.
	 */
	
	public void stop()
	{
		stopAllComponents();
		
		super.stop();
	}


	/**
	 *  Clears the current Exception (if any) of this Composite and of all of its 
	 *  Components.
	 *  
	 */
	
	public void clearException()
	{
		Iterator components = fComponents.iterator();
		
		while (components.hasNext())
		{
			Object component = components.next();
			
			if (component instanceof IrcComponent)
			{
				((IrcComponent) component).clearException();
			}
		}
		
		super.clearException();
	}
	
	
	/**
	 *  Causes this Composite to immediately cease its activity and 
	 *  release its resources.
	 * 
	 */
	
	public void kill()
	{
		Iterator components = fComponents.iterator();
		
		while (components.hasNext())
		{
			Object component = components.next();
			
			if (component instanceof IrcComponent)
			{
				((IrcComponent) component).kill();
			}
		}
		
		fComponents.clear();
		
		super.kill();
	}
	
//----------------------------------------------------------------------
//	Namespace-related methods
//----------------------------------------------------------------------
	
	/**
	 *  Reserves and returns the next sequence number available for the given 
	 *  base name in this CompositeComponent. If the given base name does not yet 
	 *  occur in this CompositeComponent, the result will be 1. Note that this 
	 *  method is only useful for the self-updating of its sequence number by a 
	 *  NamespaceMember of this CompositeComponent (as otherwise the call will result in 
	 *  that sequence number being unavailable for use by a new NamespaceMember of this 
	 *  CompositeComponent having the same given base name when it is added).
	 *  
	 *  @param name The name of a NamespaceMember of this CompositeComponent 
	 **/

	public int getNextSequenceNumber(String name)
	{
		return (fComponents.getNextSequenceNumber(name));
	}
	
	
	/**
	 *  Adds the given Member, which must be a Component, to this CompositeComponent, 
	 *  replacing the existing Component having the same fully-qualified name 
	 *  (if any). If such Component was replaced, the replaced Component is returned.
	 *  
	 *  @param component The Component to add to this CompositeComponent
	 *  @return The previous Component with the same fully-qualified name (if any) 
	 *  		replaced by the given Component
	 **/

	public final Member replace(Member component)
	{
		MinimalComponent result = null;
		
		result = fComponents.getComponentByFullyQualifiedName
			(component.getFullyQualifiedName());
			
		if (result != null)
		{
			remove(result);
		}
		
		add(component);
		
		return (result);
	}
	
	
	/**
	 *  Returns true if the given named Object, which must be a Component, occurs 
	 *  among the named Components in this CompositeComponent, false otherwise.
	 *  
	 *  @param object A Component
	 *  @return	True if the given named Object, which must be a Component, occurs 
	 *  		among the named Components in this CompositeComponent, false otherwise
	 **/

	public final boolean contains(HasName component)
	{
		boolean result = false;
		
		if (component instanceof MinimalComponent)
		{
			result = fComponents.contains(component);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns true if there are no sub-Components currently in this 
	 *  CompositeComponent, false otherwise.
	 *  
	 *  @return True if there are no sub-Components currently in this 
	 *  		CompositeComponent, false otherwise
	 **/

	public final boolean isEmpty()
	{
		return (fComponents.isEmpty());
	}


	/**
	 *  Returns an Iterator over the Set of sub-Components currently in this  
	 *  CompositeComponent.
	 *  
	 *  @return An Iterator over the Set of sub-Components currently in this  
	 *  		CompositeComponent
	 **/

	public final Iterator iterator()
	{
		return (fComponents.iterator());
	}
	
	
	/**
	 *  Returns the number of sub-Components currently in this CompositeComponent.
	 *  
	 *  @return The number of sub-Components currently in this CompositeComponent
	 **/

	public final int size()
	{
		return (fComponents.size());
	}


	/**
	 *  Returns the set of sub-Components currently in this CompositeComponent as 
	 *  an array of Objects.
	 *  
	 *  @return The set of sub-Components currently in this CompositeComponent as 
	 *  		an array of Objects
	 **/

	public final Object[] toArray()
	{
		return (fComponents.toArray());
	}
	
	
	/**
	 *  Removes all sub-Components from this CompositeComponent.
	 *  
	 **/

	public final void clear()
	{
		fComponents.clear();
	}


	/**
	 * Adds the given MembershipListener as a listener for changes in the 
	 * membership of this CompositeComponent.
	 * 
	 * @param listener A MembershipListener
	 **/

	public final void addMembershipListener(MembershipListener listener)
	{
		fComponents.addMembershipListener(listener);
	}


	/**
	 * Remove the given MembershipListener as a listener for changes in the 
	 * membership of this CompositeComponent.
	 *  
	 * @param listener A MembershipListener
	 **/

	public final void removeMembershipListener(MembershipListener listener)
	{
		fComponents.removeMembershipListener(listener);
	}
	
	
	/**
	 *  Returns the Set of CoponentIds of the Components in this CompositeComponent.
	 *  
	 *  @return The Set of CoponentIds of the Components in this CompositeComponent
	 **/

	public final Set getMemberIds()
	{
		return (fComponents.getMemberIds());
	}
	
	
	/**
	 *  Returns the Set of sub-Components in this CompositeComponent.
	 *  
	 *  @return	The Set of sub-Components in this CompositeComponent
	 **/

	public final Set getMembers()
	{
		return (fComponents.getMembers());
	}
	
	
	/**
	 *  Returns a Map of the Components in this CompositeComponent keyed on their 
	 * 	(globally unique) ComponentIds.
	 *  
	 *  @return	A Map of the Components in this CompositeComponent keyed on their 
	 * 		(globally unique) CoponentIds
	 **/

	public final Map getMembersByMemberId()
	{
		return (fComponents.getMembersByMemberId());
	}
	
	
	/**
	 *  Returns a Map of the Components in this CompositeComponent, keyed on 
	 *  their fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Components in this CompositeComponent, keyed on 
	 *  		their fully-qualified (and thus globally unique) names
	 */

	public final Map getMembersByFullyQualifiedName()
	{
		return (fComponents.getMembersByFullyQualifiedName());
	}
	
	
	/**
	 *  Returns the CoponentId of the Component in this CompositeComponent that has 
	 *  the given fully-qualified (and thus globally unique) name. If no such 
	 *  Component exists, the result is null.
	 *
	 *  @param fullyQualifiedName A fully-qualified (and thus globally unique) name 
	 *  		of some Component in this CompositeComponent
	 *  @return The CoponentId of the Component in this CompositeComponent that has 
	 *  		the given fully-qualified (and thus globally unique) name
	 **/

	public final MemberId getMemberId(String fullyQualifiedName)
	{
		return (fComponents.getMemberId(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the sub-Component in this CompositeComponent that has the given 
	 *  ComponentId (if any). If no such Component exists, the result is null.
	 *
	 *  @param componentId The ComponentId of some sub-Component in this 
	 *  		CompositeComponent
	 *  @return The sub-Component in this CompositeComponent that has the given 
	 *  		ComponentId
	 **/

	public final Member get(MemberId componentId)
	{
		return (fComponents.get(componentId));
	}
	
	
	/**
	 *  Returns the sub-Component in this CompositeComponent that has the given 
	 *  sequenced name (if any). If no such Component exists, the result is null.
	 *
	 *  @param sequencedName The sequenced name of some sub-Component in this 
	 *  		CompositeComponent
	 *  @return The sub-Component in this CompositeComponent that has the given 
	 *  		sequenced name
	 **/

	public final Object get(String sequencedName)
	{
		return (fComponents.get(sequencedName));
	}
	
	
	/**
	 *  Returns the Set of Components in this CompositeComponent that have the given 
	 *  base (and thus potentially shared) name.
	 *
	 *  @param baseName The base (and thus potentially shared) name of some 
	 *  		NamespaceMember(s) associated with this Object
	 *  @return The Set of Components in this CompositeComponent that have the given 
	 *  		base (and thus potentially shared) name
	 **/

	public final Set getByBaseName(String baseName)
	{
		return (fComponents.getByBaseName(baseName));
	}
	
	
	/**
	 *  Returns the Set of Components in this CompositeComponent whose base names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Components in this CompositeComponent
	 *  @return The Set of Components in this CompositeComponent whose base names 
	 *  		match the given regular expression pattern
	 **/

	public final Set getByBaseNamePatternMatching(String regExPattern)
	{
		return (fComponents.getByBaseNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of the sequenced (and thus Namespace-unique) names of all 
	 *  the sub-Components of this CompositeComponent.
	 *  
	 *  @return	The Set of the sequenced (and this Namespace-unique) names of all 
	 *  		the sub-Components of this CompositeComponent
	 **/

	public final Set getSequencedNames()
	{
		return (fComponents.getSequencedNames());
	}
	
	
	/**
	 *  Returns the Component in this CompositeComponent that has the given 
	 *  sequenced (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of the Component in this 
	 *  		CompositeComponent to get
	 *  @return The Component in this CompositeComponent that has the given 
	 *  		sequenced (and thus Namespace-unique) name
	 **/

	public final NamespaceMember getBySequencedName(String sequencedName)
	{
		return (fComponents.getBySequencedName(sequencedName));
	}
	
	
	/**
	 *  Returns the Set of Components in the CompositeComponent whose sequenced 
	 *  (and thus Namespace-unique) name matches the given regular expression.
	 *  
	 *  @param regExPattern The regular expression to match the sequenced names 
	 *  		against
	 *  @return The Set of Components in the CompositeComponent whose sequenced 
	 *  		(and thus Namespace-unique) name matches the given regular expression
	 **/

	public final Set getBySequencedNamePatternMatching(String regExPattern)
	{
		return (fComponents.getBySequencedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Component in this CompositeComponent that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of a Component in this CompositeComponent
	 *  @return The Component in this CompositeComponent that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public final Member getByFullyQualifiedName(String fullyQualifiedName)
	{
		return (fComponents.getByFullyQualifiedName(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Components in this CompositeComponent whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Components in this CompositeComponent
	 *  @return The Set of Components in this CompositeComponent whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public final Set getByPatternMatching(String regExPattern)
	{
		return (fComponents.getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of Components in this CompositeComponent whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Components in this CompositeComponent
	 *  @return The Set of Components in this CompositeComponent whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public final Set getByFullyQualifiedNamePatternMatching(String regExPattern)
	{
		return (fComponents.getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Removes the given Member, which must be a Component, from the Set 
	 *  of Components in this CompositeComponent. 
	 *
	 *  @param component The Component to be removed from this CompositeComponent
	 *  @return True if the given Component was actually removed from this 
	 *  		CompositeComponent
	 **/

	public final boolean remove(Member component)
	{
		boolean result = false;
		
		if (component instanceof MinimalComponent)
		{
			result = removeComponent((MinimalComponent) component);
		}
		
		return (result);
	}


	/**
	 *  Removes the given NamespaceMember, which must be a Component, from the Set 
	 *  of Components in this CompositeComponent. 
	 *
	 *  @param component The Component to be removed from this CompositeComponent
	 *  @return True if the given Component was actually removed from this 
	 *  		CompositeComponent
	 **/

	public final boolean remove(NamespaceMember component)
	{
		boolean result = false;
		
		if (component instanceof MinimalComponent)
		{
			result = removeComponent((MinimalComponent) component);
		}
		
		return (result);
	}


	/**
	 *  Removes the Component having the given ComponentId from this 
	 *  CompositeComponent.
	 *  
	 *  @param componentId The ComponentId of the Component to be removed from this 
	 *  		CompositeComponent
	 *  @return The Component that was removed (if any)
	 **/

	public final Member remove(MemberId componentId)
	{
		MinimalComponent component = (MinimalComponent) fComponents.get(componentId);
		
		if (component != null)
		{
			removeComponent(component);
		}
		
		return (component);
	}


	/**
	 *  Removes the Component having the given fully-qualified (and thus 
	 *  globally unique) name from this CompositeComponent.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of the Component to be removed from this CompositeComponent
	 *  @return The Component that was removed (if any)
	 **/

	public final Member remove(String fullyQualifiedName)
	{
		MinimalComponent component = (MinimalComponent) 
			fComponents.get(fullyQualifiedName);
		
		if (component != null)
		{
			removeComponent(component);
		}
		
		return (component);
	}
	
//----------------------------------------------------------------------
//	ComponentNamespaceManager-related methods
//----------------------------------------------------------------------

	/**
	 *  Adds the given Component to the Set of Components in and managed by this 
	 *  CompositeComponent. If the base name of the given Component already occurs 
	 *  in this CompositeComponent, it will be sequenced as needed to make it 
	 *  unique within this CompositeComponent.
	 *
	 *  @param component The Component to be added to this CompositeComponent
	 *  @return True if the given Component was actually added to this 
	 *  		CompositeComponent
	 **/

	public boolean addComponent(MinimalComponent component)
	{
		boolean result = fComponents.add(component);
		
		if (result == true)
		{
			if (component instanceof IrcComponent)
			{
				((IrcComponent) component).addStateListener(this);
			}
				
			component.addPropertyChangeListener(this);
		}
		
		return (result);
	}
	

	/**
	 *  Adds the given Member, which must be a Component, to the Set of Components 
	 *  in and managed by this CompositeComponent. If the base name of the given 
	 *  Component already occurs in this CompositeComponent, it will be sequenced 
	 *  as needed to make it unique within this CompositeComponent.
	 *
	 *  @param component The Component to be added to this CompositeComponent
	 *  @return The (possibly updated) ComponentId of the given Component as a 
	 *  		member of this CompositeComponent
	 **/

	public final boolean add(Member component)
	{
		boolean result = false;
		
		if (component instanceof MinimalComponent)
		{
			result = addComponent((MinimalComponent) component);
		}
		
		return (result);
	}


	/**
	 *  Adds the given Collection of Components to the Set of Components in and 
	 *  managed by this CompositeComponent. If the base name of a given Component 
	 *  already occurs in this CompositeComponent, it will be sequenced as needed 
	 *  to make it unique within this CompositeComponent.
	 *  
	 *  @param The Components to add to this CompositeComponent
	 *  @return True if all of the given Components were actually added, false 
	 *  		otherwise
	 **/

	public final boolean addAll(Collection components)
	{
		boolean result = true;
		
		if (components != null)
		{
			Iterator componentIterator = components.iterator();
			
			while (componentIterator.hasNext())
			{
				MinimalComponent component = (MinimalComponent) 
					componentIterator.next();
				
				boolean added = addComponent(component);
				
				if (! added)
				{
					result = false;
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given NamepaceMember, which must be a Component, to the Set of 
	 *  Components of this CompositeComponent. If the base name of the given 
	 *  Component already occurs in this CompositeComponent, it will be sequenced 
	 *  as needed to make it unique within this CompositeComponent.
	 *
	 *  @param component The Component to be added to this CompositeComponent
	 *  @return The (possibly updated) ComponentId of the given Component as a 
	 *  		member of this CompositeComponent
	 **/

	public final boolean add(NamespaceMember component)
	{
		boolean result = false;
		
		if (component instanceof MinimalComponent)
		{
			result = addComponent((MinimalComponent) component);
		}
		
		return (result);
	}


	/**
	 *  Returns true if the given Component is in this CompositeComponent, 
	 *  false otherwise.
	 *  
	 *  @param component A Component
	 *  @return	True if the given Component is in this CompositeComponent, 
	 *  		false otherwise
	 **/

	public final boolean contains(MinimalComponent component)
	{
		return (fComponents.contains(component));
	}
	
	
	/**
	 *  Returns the Set of fully-qualified (and thus globally-unique) names of the 
	 *  Components in this CompositeComponent.
	 *  
	 *  @return The Set of fully-qualified (and thus globally-unique) names of the 
	 *  		Components in this CompositeComponent
	 **/

	public final Set getFullyQualifiedNames()
	{
		return (fComponents.getFullyQualifiedNames());
	}
	
	
	/**
	 * Returns the ComponentNamespaceManager used by this CompositeComponent to 
	 * manage its Namespace of subcomponents.
	 *
	 * @return The ComponentNamespaceManager used by this CompositeComponent to 
	 * 		manage its Namespace of subcomponents
	 */
	
	protected ComponentNamespaceManager getComponentNamespaceManager()
	{
		return (fComponents);
	}
	

	/**
	 * Returns the Set of Components that belong to this Composite.
	 *
	 * @return The Set of Components that belong to this Composite
	 */
	
	public final Set getComponents()
	{
		return (fComponents.getMembers());
	}
	

	/**
	 *  Returns a Map of the Components in this CompositeComponent keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Components in this CompositeComponent keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public final Map getComponentsByFullyQualifiedName()
	{
		return (fComponents.getComponentsByFullyQualifiedName());
	}
	

	/**
	 *	Returns the (at most) single Component in this CompositeComponent that has 
	 *  the given fully-qualified (and thus globally unique) name. If there is no 
	 *  such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this CompositeComponent
	 *	@return	The (at most) single Component in this CompositeComponent that 
	 *		has the given fully-qualified (and thus globally unique) name
	 */

	public final MinimalComponent getComponent(String fullyQualifiedName)
	{
		return (fComponents.getComponent(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Components in this CompositeComponent whose base names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Components in this CompositeComponent
	 *  @return The Set of Components in this CompositeComponent whose base names 
	 *  		match the given regular expression pattern
	 **/

	public final Set getComponentByBaseNamePatternMatching(String regExPattern)
	{
		return (fComponents.getComponentByBaseNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *	Returns the (at most) single sub-Component in this CompositeComponent that 
	 *  has the given sequenced (and thus Namespace-unique) name. If there is no 
	 *  such Component, the result will be null.
	 *
	 *	@param sequencedName The sequenced (and thus Namespace-unique) name of a 
	 *		sub-Component in this CompositeComponent
	 *	@return	The (at most) single sub-Component in this CompositeComponent that 
	 *		has the given sequenced (and thus Namespace-unique) name
	 */

	public final MinimalComponent getComponentBySequencedName(String sequencedName)
	{
		return (fComponents.getComponentBySequencedName(sequencedName));
	}
	
	
	/**
	 *	Returns the (at most) single sub-Component in this CompositeComponent that 
	 *  has the given fully-qualified (and thus globally unique) name. If there is 
	 *  no such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a sub-Component in this CompositeComponent
	 *	@return	The (at most) single sub-Component in this CompositeComponent that 
	 *		has the given fully-qualified (and thus globally unique) name
	 */

	public final MinimalComponent getComponentByFullyQualifiedName
		(String fullyQualifiedName)
	{
		return (fComponents.getComponentByFullyQualifiedName(fullyQualifiedName));
	}
	

	/**
	 *  Returns the Set of Components in this CompositeComponent whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Components in this CompositeComponent
	 *  @return The Set of Components in this CompositeComponent whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public final Set getComponentsByFullyQualifiedNamePatternMatching
		(String regExPattern)
	{
		return (fComponents.getComponentsByFullyQualifiedNamePatternMatching
			(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of ComponentIds of the Components in this CompositeComponent.
	 *  
	 *  @return The Set of ComponentIds of the Components in this CompositeComponent
	 **/

	public final Set getComponentIds()
	{
		return (fComponents.getComponentIds());
	}
	
	
	/**
	 *  Returns a Map of the Components in this CompositeComponent keyed by their 
	 *  ComponentIds.
	 *
	 *  @return A Map of the Components in this CompositeComponent keyed by their 
	 *  		ComponentIds
	 */

	public final Map getComponentsByComponentId()
	{
		return (fComponents.getComponentsByComponentId());
	}
	
	
	/**
	 *  Returns the Component in the Set of Components in this CompositeComponent 
	 *  that has the given ComponentId (if any). If there is no such Component, 
	 *  the result is null.
	 *
	 *  @param id The ComponentId of some Component in this CompositeComponent
	 *  @return The Component in the Set of Components in this CompositeComponent 
	 *  		that has the given ComponentId (if any)
	 **/

	public final MinimalComponent getComponent(ComponentId id)
	{
		return (fComponents.getComponent(id));
	}
	
	
	/**
	 *	Returns the ComponentId of the (at most) single sub-Component in this 
	 *  CompositeComponent that has the given sequenced (and thus Namespace-unique) 
	 *  name. If there is no such Component, the result will be null.
	 *
	 *	@param sequencedName The sequenced (and thus Namespace-unique) name of a 
	 *		sub-Component in this CompositeComponent
	 *	@return	The ComponentId of the (at most) single sub-Component in this 
	 *  		CompositeComponent that has the given sequenced (and thus 
	 *  		Namespace-unique) name
	 */

	public final ComponentId getComponentIdBySequencedName(String sequencedName)
	{
		return (fComponents.getComponentIdBySequencedName(sequencedName));
	}
	

	/**
	 *	Returns the ComponentId of the (at most) single Component in this 
	 *  CompositeComponent that has the given fully-qualified (and thus globally 
	 *  unique) name. If there is no such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this CompositeComponent
	 *	@return	The ComponentId of the (at most) single Component in this 
	 *  		CompositeComponent that has the given fully-qualified (and thus 
	 *  		globally unique) name
	 */

	public final ComponentId getComponentId(String fullyQualifiedName)
	{
		return (fComponents.getComponentId(fullyQualifiedName));
	}
	
	
	/**
	 *	Returns the ComponentId of the (at most) single Component in this 
	 *  CompositeComponent that has the given fully-qualified (and thus globally 
	 *  unique) name. If there is no such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this CompositeComponent
	 *	@return	The ComponentId of the (at most) single Component in this 
	 *  		CompositeComponent that has the given fully-qualified (and thus 
	 *  		globally unique) name
	 */

	public final ComponentId getComponentIdByFullyQualifiedName
		(String fullyQualifiedName)
	{
		return (fComponents.getComponentIdByFullyQualifiedName(fullyQualifiedName));
	}
	
	
	/**
	 *  Removes the given Component from the Set of Components of this 
	 *  Composite. Also removes this Composite from the Component as a listener 
	 *  for property changes and state changes.
	 * 
	 *  <p>The given Component will be stopped before it is removed.</p>
	 *
	 *  @param component The Component to be removed
	 *  @return True if the given Component was actually removed
	 */

	public boolean removeComponent(MinimalComponent component)
	{
		boolean result = fComponents.remove(component);
		
		if (result == true)
		{
			if (component instanceof IrcComponent)
			{
				((IrcComponent) component).stop();
					
				((IrcComponent) component).removeStateListener(this);
			}
				
			component.removePropertyChangeListener(this);
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes the Component having the given ComponentId from the Set of 
	 *  Components of this CompositeComponent.
	 *
	 *  @param id The ComponentId of the Component to be removed from this 
	 *  		CompositeComponent
	 *  @return The Component that was removed
	 **/

	public final MinimalComponent removeComponent(ComponentId id)
	{
		MinimalComponent result = fComponents.getComponent(id);
		
		if (result != null)
		{
			removeComponent(result);
		}

		return (result);
	}
	
	
	/**
	 *  Removes the sub-Component having the given sequenced (and thus 
	 *  Namespace-unique) name from this CompositeComponent.
	 * 
	 *  @param sequencedName The sequenced  (and thus Namespace-unique) name of the 
	 *  		sub-Component to be removed
	 *  @return The sub-Component that was removed (if any)
	 */

	public final MinimalComponent removeComponentBySequencedName
		(String sequencedName)
	{
		MinimalComponent result = 
			fComponents.getComponentBySequencedName(sequencedName);
	
		if (result != null)
		{
			removeComponent(result);
		}

		return (result);
	}
	
	
	/**
	 *  Removes the NamespaceMember having the given sequenced (and thus Namespace-unique) 
	 *  name from this Namespace.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name of 
	 *  		some NamespaceMember of this Namespace
	 *  @return The NamespaceMember that was removed (if any)
	 **/

	public final NamespaceMember removeBySequencedName(String sequencedName)
	{
		MinimalComponent result = 
			fComponents.getComponentBySequencedName(sequencedName);
		
		if (result != null)
		{
			removeComponent(result);
		}

		return (result);
	}
	
	
	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from this CompositeComponent.
	 * 
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of the Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public final MinimalComponent removeComponent(String fullyQualifiedName)
	{
		MinimalComponent result = fComponents.getComponent(fullyQualifiedName);
	
		if (result != null)
		{
			removeComponent(result);
		}

		return (result);
	}
	

	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from this CompositeComponent.
	 * 
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of the Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public final MinimalComponent removeComponentByFullyQualifiedName
		(String fullyQualifiedName)
	{
		MinimalComponent result = fComponents.getComponent(fullyQualifiedName);

		if (result != null)
		{
			removeComponent(result);
		}

		return (result);
	}
		
//----------------------------------------------------------------------
//	Component ActivityState-related methods
//----------------------------------------------------------------------
	
	/**
	 *	Starts the Component managed by this CompositeComponent that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this 
	 *  		CompositeComponent
	 */
	
	public final void startComponent(ComponentId id)
	{
		fComponents.startComponent(id);
	}
	

	/**
	 *	Starts the Component managed by this CompositeComponent that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this CompositeComponent
	 */
	
	public final void startComponent(String fullyQualifiedName)
	{
		fComponents.startComponent(fullyQualifiedName);
	}
	

	/**
	 *	Starts all of the Components managed by this CompositeComponent.
	 *
	 */
	
	public void startAllComponents()
	{
		fComponents.startAllComponents();
	}
	

	/**
	 *	Stops the Component managed by this CompositeComponent that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this 
	 *  		CompositeComponent
	 */
	
	public final void stopComponent(ComponentId id)
	{
		fComponents.stopComponent(id);
	}
	

	/**
	 *	Stops the Component managed by this CompositeComponent that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this CompositeComponent
	 */
	
	public final void stopComponent(String fullyQualifiedName)
	{
		fComponents.stopComponent(fullyQualifiedName);
	}
	

	/**
	 *	Stops all of the Components managed by this CompositeComponent.
	 *
	 */
	
	public void stopAllComponents()
	{
		fComponents.stopAllComponents();
	}
	

	/**
	 *	Kills the Component managed by this CompositeComponent that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this 
	 *  		CompositeComponent
	 */
	
	public final void killComponent(ComponentId id)
	{
		fComponents.killComponent(id);
	}
	

	/**
	 *	Kills the Component managed by this CompositeComponent that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this CompositeComponent
	 */
	
	public final void killComponent(String fullyQualifiedName)
	{
		fComponents.killComponent(fullyQualifiedName);
	}
	

	/**
	 *	Kills all of the Components managed by this CompositeComponent.
	 *
	 */
	
	public void killAllComponents()
	{
		fComponents.killAllComponents();
	}
	
	
	/**
	 *	Notifies this Composite that the given Component has started.
	 * 
	 *  <p>This method should only be called by a Component of this 
	 *  Composite.
	 * 
	 *  <p>Here, does nothing.
	 * 
	 *	@param component The Component of this Composite that has started
	 */
	
	protected void componentHasStarted(IrcComponent component)
	{
		
	}
	
	
	/**
	 *	Returns true if at least one of the Components of this Composite 
	 *  is active, false otherwise.
	 *
	 *	@return True if at least one of the Components of this Composite 
	 *  	is active, false otherwise
	 */
	
	protected boolean hasActiveComponents()
	{
		boolean aComponentIsActive = false;
		
		Iterator components = fComponents.iterator();
		
		while (components.hasNext() && ! aComponentIsActive)
		{
			Object component = components.next();
			
			if ((component instanceof IrcComponent) && 
				((IrcComponent) component).isActive())
			{
				aComponentIsActive = true;
			}
		}
		
		return (aComponentIsActive);
	}

	
	/**
	 *	Notifies this Composite that the given Component has become active. 
	 *  If this Composite is currently inactive, it will itself become active.
	 * 
	 *  <p>This method should only be called by a Component of this 
	 *  Composite.
	 * 
	 *	@param component The Component of this Composite that has become 
	 *		active
	 */
	
	protected void componentHasBecomeActive(IrcComponent component)
	{
		if (! isActive())
		{
			// This Composite is currently inactive, but one of its 
			// Components has become active; so the PipelineElement now 
			// itself becomes active.
			
			declareActive();
		}
	}
	
	
	/**
	 *	Notifies this Composite that the given Component has become inactive. 
	 *  If this Composite is currently active, and if no other Component is 
	 *  current active, it will itself become inactive.
	 * 
	 *  <p>This method should only be called by a Component of this 
	 *  Composite.
	 * 
	 *	@param component The Component of this Composite that has become 
	 *		inactive
	 */
	
	protected void componentIsWaiting(IrcComponent component)
	{
		if (isActive())
		{
			// This Composite is currently active, but one of its 
			// Components is waiting.
			
			if (! hasActiveComponents())
			{
				// None of the Components of this Composite are now
				// active, so the Composite itself now becomes waiting.
				
				declareWaiting();
			}
		}
	}
	
	
	/**
	 *	Notifies this Composite that the given Component is in an exception state. 
	 *  This Composite will first stop itself and all of its Components, and then 
	 *  enter the exception state.
	 * 
	 *  <p>This method should only be called by a Component of this 
	 *  Composite.
	 * 
	 *	@param component The Component of this Composite that has entered an 
	 *		exception state
	 */
	
	protected void componentIsInException(IrcComponent component)
	{
		if (! isKilled())
		{
			if (component.isException())
			{
				stop();
				
				declareException(component.getException());
			}
		}
	}
	
	
	/**
	 *	Notifies this Composite that the given Component has stopped.
	 * 
	 *  <p>This method should only be called by a Component of this 
	 *  Composite.
	 * 
	 *  <p>Here, does nothing.
	 * 
	 *	@param component The Component of this Composite that has stopped
	 */
	
	protected void componentHasStopped(IrcComponent component)
	{
		
	}
	
	
	/**
	 *	Notifies this Composite that the given Component has been killed.
	 * 
	 *  <p>This method should only be called by a Component of this 
	 *  Composite.
	 * 
	 *  <p>Here, removes the given Component from the Collection of 
	 *  Components of this Composite.
	 * 
	 *	@param component The Component of this Composite that has been 
	 *		killed
	 */
	
	protected void componentHasBeenKilled(IrcComponent component)
	{
		removeComponent(component);
	}
	
	
	/**
	 * Alerts this Composite that a change has occurred in some property of 
	 * some Component that it is listening to.
	 * 
	 * <p>This method should typically be called only by the beans to 
	 * which this Composite has registered as a listener, or by itself.
	 * 
	 * <p>Here, handles Component ActivityState change events. 
	 * 
	 * <p>Other events, including particular or additional properties of 
	 * interest, should be handled by overriding this method. Overriding 
	 * methods must call <code>super.propertyChange</code> unless they 
	 * intend to block this default handling of these properties.
	 */
	
	public void propertyChange(PropertyChangeEvent event)
	{
		String eventName = event.getPropertyName();
		Object eventSource = event.getSource();
		Object newValue = event.getNewValue();
		
		if (eventSource instanceof IrcComponent)
		{
			IrcComponent component = (IrcComponent) eventSource;

			if (eventName == IrcComponent.STATE_PROPERTY_NAME)
			{
				ActivityState newState = (ActivityState) newValue;
				
				if (newState.equals(ActivityState.STARTED))
				{
					componentHasStarted(component);
				}
				else if (newState.equals(ActivityState.STOPPED))
				{
					componentHasStopped(component);
				}
				else if (newState.equals(ActivityState.ACTIVE))
				{
					componentHasBecomeActive(component);
				}
				else if (newState.equals(ActivityState.WAITING))
				{
					componentIsWaiting(component);
				}
				else if (newState.equals(ActivityState.EXCEPTION))
				{
					componentIsInException(component);
				}
				else if (newState.equals(ActivityState.KILLED))
				{
					componentHasBeenKilled(component);
				}
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractCompositeComponent.java,v $
//	Revision 1.5  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
//	
//	Revision 1.4  2006/03/14 17:33:33  chostetter_cvs
//	Beefed up toString() method
//	
//	Revision 1.3  2006/03/14 14:57:16  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.2  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.38  2005/11/09 20:03:52  tames_cvs
//	Added missing getCompositeListeners method to match bean spec.
//	
//	Revision 1.37  2005/11/09 18:43:23  tames_cvs
//	Modified event publishing to use the CopyOnWriteArrayList class to
//	hold listeners. This reduces the overhead when publishing events.
//	
//	Revision 1.36  2005/04/20 19:47:55  chostetter_cvs
//	Will not start/stop component unless not started/stopped
//	
//	Revision 1.35  2005/04/16 04:03:55  tames
//	Changes to reflect refactored state and activity packages.
//	
//	Revision 1.34  2005/04/12 22:15:16  chostetter_cvs
//	Fixed ordering, synchronization issues with including pending data on stop
//	
//	Revision 1.33  2005/02/02 06:10:34  tames
//	Fixed null pointer exception if toString is called after component is killed.
//	
//	Revision 1.32  2005/01/27 21:38:02  chostetter_cvs
//	Implemented new exception state and default exception behavior for Objects having ActivityState
//	
//	Revision 1.31  2004/12/21 20:01:37  smaher_cvs
//	Set the parent of the child being added to the component, if applicable.
//	
//	Revision 1.30  2004/12/02 04:11:24  tames
//	Updated to implement the new CompositeComponent interface.
//	
//	Revision 1.29  2004/12/01 22:37:48  tames_cvs
//	Updated to reflect changes to the Composite interface. This allows the
//	ComponentManager and Composite components to use the same
//	interface.
//	
//	Revision 1.28  2004/11/23 22:36:02  tames_cvs
//	Removed overly restrictive class cast in toString method.
//	
//	Revision 1.27  2004/07/22 17:06:55  chostetter_cvs
//	Namespace-related changes
//	
//	Revision 1.26  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.25  2004/07/16 15:18:31  chostetter_cvs
//	Revised, refactored Component activity state
//	
//	Revision 1.24  2004/07/16 04:15:44  chostetter_cvs
//	More Algorithm work, primarily on properties
//	
//	Revision 1.23  2004/07/13 18:52:50  chostetter_cvs
//	More data, Algorithm work
//	
//	Revision 1.22  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.21  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.20  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.19  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.18  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.17  2004/06/28 17:21:16  chostetter_cvs
//	And here, days later, you _still_ can't cast a Set to a Collection. Go figure.
//	
//	Revision 1.16  2004/06/24 19:43:24  chostetter_cvs
//	Turns out you can't cast a Set to a Collection. Grrrr.
//	
//	Revision 1.15  2004/06/15 20:04:03  chostetter_cvs
//	Added ActivityStateModel, use for stative Objects
//	
//	Revision 1.14  2004/06/11 17:27:56  chostetter_cvs
//	Further Input-related work
//	
//	Revision 1.13  2004/06/03 04:39:22  chostetter_cvs
//	More AbstractCompositeComponent, Manager changes
//	
//	Revision 1.12  2004/06/03 04:17:09  chostetter_cvs
//	AbstractCompositeComponent, Manager changes
//	
//	Revision 1.11  2004/06/03 01:51:27  chostetter_cvs
//	More Namespace tweaks
//	
//	Revision 1.10  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.9  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.8  2004/05/29 03:42:43  chostetter_cvs
//	ActivityState, doc tweaks
//	
//	Revision 1.7  2004/05/29 02:40:06  chostetter_cvs
//	Lots of data-related changes
//	
//	Revision 1.6  2004/05/27 23:28:33  chostetter_cvs
//	Constructor tweaks
//	
//	Revision 1.5  2004/05/27 23:25:06  chostetter_cvs
//	Constructor tweaks
//	
//	Revision 1.4  2004/05/27 23:09:26  chostetter_cvs
//	More Namespace related changes
//	
//	Revision 1.3  2004/05/27 20:03:45  chostetter_cvs
//	More Namespace, DataSpace tweaks
//	
//	Revision 1.2  2004/05/27 15:57:16  chostetter_cvs
//	Data-related changes
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//	
