//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/ComponentSet.java,v 1.1 2006/03/14 14:56:24 chostetter_cvs Exp $
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


/**
 *	A ComponentSet is a Set of Components.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/03/14 14:56:24 $
 *  @author Carl F. Hostetter
 */

public interface ComponentSet extends HasComponents
{
	/**
	 *  Adds the given Component to the Set of Components of this ComponentSet. 
	 *
	 *  @param component The Component to be added to this ComponentSet
	 *  @return True if the given Component was actually added to this 
	 *  		ComponentSet
	 **/

	public boolean addComponent(MinimalComponent component);
	

	/**
	 *  Removes the given Component from the Set of Components of this 
	 *  ComponentSet. 
	 *
	 *  @param component The Component to be removed from this ComponentSet
	 *  @return True if the given Component was actually removed from this 
	 *  		ComponentSet
	 **/

	public boolean removeComponent(MinimalComponent component);
	
	
	/**
	 *  Removes the Component having the given ComponentId from the Set of 
	 *  Components of this ComponentManager.
	 *
	 *  @param id The ComponentId of the Component to be removed from this 
	 *  		ComponentManager
	 *  @return The Component that was removed
	 **/

	public MinimalComponent removeComponent(ComponentId id);
	
	
	/**
	 *  Removes the Component having the given fully-qualified (and thus globally 
	 *  unique) name from the Set of Components of this ComponentManager.
	 *
	 *  @param fullyQualifiedName The fully-qualified name of the Component to be 
	 *  		removed from this ComponentManager
	 *  @return The Component that was removed
	 **/

	public MinimalComponent removeComponent(String fullyQualifiedName);
}


//--- Development History  ---------------------------------------------------
//
//	$Log: ComponentSet.java,v $
//	Revision 1.1  2006/03/14 14:56:24  chostetter_cvs
//	Replaced Composite with ComponentSet
//	
//	
//	
