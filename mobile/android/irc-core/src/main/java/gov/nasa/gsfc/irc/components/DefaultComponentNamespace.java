//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/DefaultComponentNamespace.java,v 1.3 2006/03/14 21:26:11 chostetter_cvs Exp $
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

import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.types.namespaces.DefaultNamespace;


/**
 *	A ComponentNamespace is a Namespace of Components.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 21:26:11 $
 *  @author Carl F. Hostetter
 */

public class DefaultComponentNamespace extends DefaultNamespace 
	implements ComponentNamespace
{
	/**
	 *  Constructs a new ComponentNamespace having the given base name.
	 * 
	 *  @param name The base name of the new ComponentNamespace
	 **/

	public DefaultComponentNamespace(String name)
	{
		super(name);
	}
		
	
	/**
	 *  Constructs a new ComponentNamespace that acts as a proxy for the given 
	 *  ComponentNamespace.
	 *
	 *  @param namespace The ComponentNamespace for which the new ComponentNamespace 
	 *  		will act as a proxy
	 **/

	protected DefaultComponentNamespace(ComponentNamespace namespace)
	{
		super(namespace);
	}
	
	
	/**
	 *  Adds the given Component to the Set of Components of this ComponentNamespace. 
	 *  If the base name of the given Component already occurs in this 
	 *  ComponentNamespace, it will be sequenced as needed to make it unique within 
	 *  this ComponentNamespace.
	 *
	 *  @param component The Component to be added to this ComponentNamespace
	 *  @return True if the given Component was actually added to this 
	 *  		ComponentNamespace
	 **/

	public final boolean addComponent(MinimalComponent component)
	{
		return (add(component));
	}


	/**
	 *  Returns the Set of Components in this ComponentNamespace.
	 *  
	 *  @return The Set of Components in this ComponentNamespace
	 */

	public Set getComponents()
	{
		return (super.getMembers());
	}
	
	
	/**
	 *  Returns the Set of Components in this ComponentNamespace whose base names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Components in this ComponentNamespace
	 *  @return The Set of Components in this ComponentNamespace whose base names 
	 *  		match the given regular expression pattern
	 **/

	public Set getComponentByBaseNamePatternMatching(String regExPattern)
	{
		return (getByBaseNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *	Returns the (at most) single Component in this ComponentNamespace that 
	 *  has the given sequenced (and thus Namespace-unique) name. If there is no 
	 *  such Component, the result will be null.
	 *
	 *	@param sequencedName The sequenced (and thus Namespace-unique) name of a 
	 *		Component in this ComponentNamespace
	 *	@return	The (at most) single Component in this ComponentNamespace that 
	 *		has the given sequenced (and thus Namespace-unique) name
	 */

	public MinimalComponent getComponentBySequencedName(String sequencedName)
	{
		return ((MinimalComponent) super.getBySequencedName(sequencedName));
	}
	

	/**
	 *	Returns the (at most) single Component in this ComponentNamespace that has 
	 *  the given fully-qualified (and thus globally unique) name. If there is no 
	 *  such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this ComponentNamespace
	 *	@return	The (at most) single Component in this ComponentNamespace that 
	 *		has the given fully-qualified (and thus globally unique) name
	 */

	public MinimalComponent getComponentByFullyQualifiedName
		(String fullyQualifiedName)
	{
		return ((MinimalComponent) super.getByFullyQualifiedName
			(fullyQualifiedName));
	}
	

	/**
	 *	Returns the (at most) single Component in this ComponentNamespace that has 
	 *  the given fully-qualified (and thus globally unique) name. If there is no 
	 *  such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this ComponentNamespace
	 *	@return	The (at most) single Component in this ComponentNamespace that 
	 *		has the given fully-qualified (and thus globally unique) name
	 */

	public MinimalComponent getComponent(String fullyQualifiedName)
	{
		return (getComponentByFullyQualifiedName(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Components in this ComponentNamespace whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Components in this ComponentNamespace
	 *  @return The Set of Components in this ComponentNamespace whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public Set getComponentsByFullyQualifiedNamePatternMatching(String regExPattern)
	{
		return (getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Component in the Set of Components in this ComponentNamespace 
	 *  that has the given ComponentId (if any). If there is no such Component, 
	 *  the result is null.
	 *
	 *  @param id The ComponentId of some Component in this ComponentNamespace
	 *  @return The Component in the Set of Components in this ComponentNamespace 
	 *  		that has the given ComponentId (if any)
	 **/

	public MinimalComponent getComponent(ComponentId id)
	{
		return ((MinimalComponent) super.get(id));
	}
	
	
	/**
	 *  Returns a Map of the Components in this ComponentNamespace keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Components in this ComponentNamespace keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public Map getComponentsByFullyQualifiedName()
	{
		return (getMembersByFullyQualifiedName());
	}
	
	
	/**
	 *  Returns a Map of the Components in this ComponentNamespace keyed by their 
	 *  ComponentIds.
	 *
	 *  @return A Map of the Components in this ComponentNamespace keyed by their 
	 *  		ComponentIds
	 */

	public Map getComponentsByComponentId()
	{
		return (getMembersByMemberId());
	}
	
	
	/**
	 *	Returns the ComponentId of the (at most) single Component in this 
	 *  ComponentNamespace that has the given sequenced (and thus Namespace-unique) 
	 *  name. If there is no such Component, the result will be null.
	 *
	 *	@param sequencedName The sequenced (and thus Namespace-unique) name of a 
	 *		Component in this ComponentNamespace
	 *	@return	The ComponentId of the (at most) single Component in this 
	 *  		ComponentNamespace that has the given public name
	 */

	public ComponentId getComponentIdBySequencedName(String sequencedName)
	{
		ComponentId result = null;
		
		MinimalComponent component = (MinimalComponent) 
			super.getBySequencedName(sequencedName);
		
		if (component != null)
		{
			result = component.getComponentId();
		}
		
		return (result);
	}
	
	
	/**
	 *	Returns the ComponentId of the (at most) single Component in this 
	 *  ComponentNamespace that has the given fully-qualified (and thus globally 
	 *  unique) name. If there is no such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this ComponentNamespace
	 *	@return	The ComponentId of the (at most) single Component in this 
	 *  		ComponentNamespace that has the given fully-qualified (and thus 
	 *  		globally unique) name
	 */

	public ComponentId getComponentIdByFullyQualifiedName
		(String fullyQualifiedName)
	{
		ComponentId result = null;
		
		MinimalComponent component = (MinimalComponent) 
			super.getByFullyQualifiedName(fullyQualifiedName);
		
		if (component != null)
		{
			result = component.getComponentId();
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the Set of ComponentIds of the Components in this ComponentNamespace.
	 *  
	 *  @return The Set of ComponentIds of the Components in this ComponentNamespace
	 **/

	public Set getComponentIds()
	{
		return (getMemberIds());
	}
	
	
	/**
	 *	Returns the ComponentId of the (at most) single Component in this 
	 *  ComponentNamespace that has the given fully-qualified (and thus globally 
	 *  unique) name. If there is no such Component, the result will be null.
	 *
	 *	@param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *		name of a Component in this ComponentNamespace
	 *	@return	The ComponentId of the (at most) single Component in this 
	 *  		ComponentNamespace that has the given fully-qualified (and thus 
	 *  		globally unique) name
	 */

	public ComponentId getComponentId(String fullyQualifiedName)
	{
		return (getComponentIdByFullyQualifiedName(fullyQualifiedName));
	}
	

	/**
	 *  Removes the given Component from the Set of Components of this 
	 *  ComponentNamespace. 
	 *
	 *  @param component The Component to be removed from this ComponentNamespace
	 *  @return True if the given Component was actually removed
	 **/

	public final boolean removeComponent(MinimalComponent component)
	{
		return (remove(component));
	}


	/**
	 *  Removes the Component having the given ComponentId from this 
	 *  ComponentNamespace.
	 * 
	 *  @param id The ComponentId of the Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public final MinimalComponent removeComponent(ComponentId id)
	{
		return ((MinimalComponent) remove(id));
	}


	/**
	 *  Removes the Component having the given sequenced (and thus Namespace-unique) 
	 *  name from this ComponentNamespace.
	 * 
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name of the 
	 *  		Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public final MinimalComponent removeComponentBySequencedName
		(String sequencedName)
	{
		return ((MinimalComponent) removeBySequencedName(sequencedName));
	}
	

	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from this ComponentNamespace.
	 * 
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of the Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public final MinimalComponent removeComponentByFullyQualifiedName
		(String fullyQualifiedName)
	{
		return ((MinimalComponent) removeByFullyQualifiedName(fullyQualifiedName));
	}
	

	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from this ComponentNamespace.
	 * 
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of the Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public final MinimalComponent removeComponent(String fullyQualifiedName)
	{
		return (removeComponentByFullyQualifiedName(fullyQualifiedName));
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultComponentNamespace.java,v $
//	Revision 1.3  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
//	
//	Revision 1.2  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.10  2004/12/02 00:20:40  tames
//	Restored "implements HasComponents" which was removed in the
//	previous update.
//	
//	Revision 1.9  2004/12/01 22:37:48  tames_cvs
//	Updated to reflect changes to the Composite interface. This allows the
//	ComponentManager and Composite components to use the same
//	interface.
//	
//	Revision 1.8  2004/07/22 20:14:58  chostetter_cvs
//	Data, Component namespace work
//	
//	Revision 1.7  2004/07/15 17:48:55  chostetter_cvs
//	ComponentManager, property change work
//	
//	Revision 1.6  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.5  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.4  2004/06/28 17:21:16  chostetter_cvs
//	And here, days later, you _still_ can't cast a Set to a Collection. Go figure.
//	
//	Revision 1.3  2004/06/24 19:43:24  chostetter_cvs
//	Turns out you can't cast a Set to a Collection. Grrrr.
//	
//	Revision 1.2  2004/06/03 04:39:22  chostetter_cvs
//	More ComponentNamespace, Manager changes
//	
//	Revision 1.1  2004/06/03 04:17:09  chostetter_cvs
//	ComponentNamespace, Manager changes
//	
