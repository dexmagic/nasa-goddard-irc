//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/HasComponents.java,v 1.7 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//  $Log: HasComponents.java,v $
//  Revision 1.7  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2004/06/28 17:21:16  chostetter_cvs
//  And here, days later, you _still_ can't cast a Set to a Collection. Go figure.
//
//  Revision 1.5  2004/06/24 19:43:24  chostetter_cvs
//  Turns out you can't cast a Set to a Collection. Grrrr.
//
//  Revision 1.4  2004/06/03 04:39:22  chostetter_cvs
//  More DefaultComponentNamespace, Manager changes
//
//  Revision 1.3  2004/06/02 23:59:41  chostetter_cvs
//  More Namespace, DataSpace tweaks, created alogirthms package
//
//  Revision 1.2  2004/05/27 15:57:16  chostetter_cvs
//  Data-related changes
//
//  Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//  Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.components;

import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.types.namespaces.HasMembers;


/**
 * Any Object that has Components can return the Set of its Components, or 
 * retrieve them by fully-qualified name or by ComponentId.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:51 $
 * @author Carl F. Hostetter
*/

public interface HasComponents extends HasMembers
{
	/**
	 *  Returns the Set of Components associated with this Object.
	 *  
	 *  @return The Set of Components associated with this Object
	 */

	public Set getComponents();
	
	
	/**
	 *  Returns the Component in the Set of Components associated with this 
	 *  Object that has the given fully-qualified (and thus globally unique) name. 
	 *  If there is no such Component, the result is null.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component
	 *  @return The Component in the Set of Components associated with this 
	 *  		Object that has the given fully-qualified (and thus globally unique) 
	 *  		name
	 */

	public MinimalComponent getComponent(String fullyQualifiedName);


	/**
	 *  Returns the Component in the Set of Components associated with this 
	 *  Object that has the given ComponentId (if any). If there is no such 
	 *  Component, the result is null.
	 *
	 *  @param id The ComponentId of some Component associated with this Object
	 *  @return The Component in the Set of Components associated with this 
	 *  		Object that has the given ComponentId (if any)
	 **/

	public MinimalComponent getComponent(ComponentId id);
	
	
	/**
	 *	Returns the ComponentId of the Component in the Set of Components 
	 *  associated with this Object that has the given fully-qualified (and thus 
	 *  globally unique) name. If there is no such Component, the result 
	 *  will be null.
	 *
	 *	@param fullyQualifiedName The fully qualified (and thus globally unique) 
	 *		name of some Component
	 *	@return	The ComponentId of the Component in the Set of Components 
	 *  		associated with this Object that has the given fully-qualified (and 
	 *  		thus globally unique) name
	 */

	public ComponentId getComponentId(String fullyQualifiedName);
	

	/**
	 *	Returns the ComponentId of the Component in the Set of Components 
	 *  associated with this Object that has the given fully-qualified (and thus 
	 *  globally unique) name. If there is no such Component, the result 
	 *  will be null.
	 *
	 *	@param fullyQualifiedName The fully qualified (and thus globally unique) 
	 *		name of some Component
	 *	@return	The ComponentId of the Component in the Set of Components 
	 *  		associated with this Object that has the given fully-qualified (and 
	 *  		thus globally unique) name
	 */

	public ComponentId getComponentIdByFullyQualifiedName
		(String fullyQualifiedName);
	
	
	/**
	 *  Returns the Set of ComponentIds of the Components associated with this 
	 *  Object.
	 *  
	 *  @return The Set of ComponentIds of the Components associated with this 
	 *  		Object
	 **/

	public Set getComponentIds();
	
	
	/**
	 *  Returns a Map of the Components associated with this Object keyed by their 
	 *  ComponentIds.
	 *
	 *  @return A Map of the Components associated with this Object keyed by their 
	 *  		ComponentIds
	 */

	public Map getComponentsByComponentId();
	
	
	/**
	 *  Returns the Set of Components associated with this Object that have the 
	 *  given base (and thus potentially shared) name.
	 *
	 *  @param baseName The base (and thus potentially shared) name of some 
	 *  		Component(s) associated with this Object
	 *  @return The Set of Components associated with this Object that have the 
	 *  		given base (and thus potentially shared) name
	 **/

	public Set getByBaseName(String baseName);
	
	
	/**
	 *  Returns the Set of Components associated with this Object whose base names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Components associated with this Object
	 *  @return The Set of Components associated with this Object whose base names 
	 *  		match the given regular expression pattern
	 **/

	public Set getComponentByBaseNamePatternMatching(String regExPattern);
	
	
	/**
	 *  Returns the Set of fully-qualified (and thus globally-unique) names of the 
	 *  Members associated with this Object.
	 *  
	 *  @return The Set of fully-qualified (and thus globally-unique) names of the 
	 *  		Members associated with this Object
	 **/

	public Set getFullyQualifiedNames();
	
	
	/**
	 *  Returns a Map of the Components associated with this Object keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Components associated with this Object keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public Map getComponentsByFullyQualifiedName();
	
	
	/**
	 *  Returns the Component associated with this Object that has the given 
	 *  fully-qualified (and thus globally unique) name. If no such Component exists, 
	 *  the result is null.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component
	 *  @return The Component associated with this Object that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public MinimalComponent getComponentByFullyQualifiedName
		(String fullyQualifiedName);
	
	
	/**
	 *  Returns the Set of Components associated with this Object whose 
	 *  fully-qualified names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Components associated with this Object
	 *  @return The Set of Components associated with this Object whose 
	 *  		fully-qualified names match the given regular expression pattern
	 **/

	public Set getComponentsByFullyQualifiedNamePatternMatching(String regExPattern);
}
