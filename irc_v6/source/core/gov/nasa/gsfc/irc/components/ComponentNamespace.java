//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/ComponentNamespace.java,v 1.12 2006/03/14 14:57:16 chostetter_cvs Exp $
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

import gov.nasa.gsfc.commons.types.namespaces.Namespace;


/**
 *	A ComponentNamespace is a Namespace of Components.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 14:57:16 $
 *  @author Carl F. Hostetter
 */

public interface ComponentNamespace extends ComponentSetBean, Namespace
{
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

	public boolean addComponent(MinimalComponent component);
	

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

	public MinimalComponent getComponentBySequencedName(String sequencedName);
	

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

	public ComponentId getComponentIdBySequencedName(String sequencedName);
	

	/**
	 *  Removes the Component having the given sequenced (and thus Namespace-unique) 
	 *  name from this ComponentNamespace.
	 * 
	 *  @param sequencedName The sequenced  (and thus Namespace-unique) name of the 
	 *  		Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public MinimalComponent removeComponentBySequencedName(String sequencedName);
	

	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from this ComponentNamespace.
	 * 
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of the Component to be removed
	 *  @return The Component that was removed (if any)
	 */

	public MinimalComponent removeComponentByFullyQualifiedName
		(String fullyQualifiedName);
}


//--- Development History  ---------------------------------------------------
//
//	$Log: ComponentNamespace.java,v $
//	Revision 1.12  2006/03/14 14:57:16  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.11  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	
