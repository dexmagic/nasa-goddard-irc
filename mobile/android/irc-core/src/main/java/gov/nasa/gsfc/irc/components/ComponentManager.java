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



/**
 * A ComponentManager is a bean that contains and manages a Set of Components.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	tames
 **/

public interface ComponentManager extends ComponentSetBean
{
	/**
	 *	Starts the Component managed by this ComponentManager that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this ComponentManager
	 */
	
	public void startComponent(ComponentId id);
	

	/**
	 *	Starts the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
	 */
	
	public void startComponent(String fullyQualifiedName);
	

	/**
	 *	Starts all of the Components managed by this ComponentManager.
	 *
	 */
	
	public void startAllComponents();
	

	/**
	 *	Stopes the Component managed by this ComponentManager that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this ComponentManager
	 */
	
	public void stopComponent(ComponentId id);
	

	/**
	 *	Stops the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
	 */
	
	public void stopComponent(String fullyQualifiedName);
	

	/**
	 *	Stops all of the Components managed by this ComponentManager.
	 *
	 */
	
	public void stopAllComponents();
	

	/**
	 *	Kills the Component managed by this ComponentManager that has the given 
	 *  ComponentId.
	 *  
	 *  @param id The ComponentId of some Component managed by this ComponentManager
	 */
	
	public void killComponent(ComponentId id);
	

	/**
	 *	Kills the Component managed by this ComponentManager that has the given 
	 *  fully-qualified (and thus globally unique) name.
	 *
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Component managed by this ComponentManager
	 */
	
	public void killComponent(String fullyQualifiedName);
	

	/**
	 *	Kills all of the Components managed by this ComponentManager.
	 *
	 */
	
	public void killAllComponents();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentManager.java,v $
//  Revision 1.18  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.17  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.16  2005/02/07 15:33:04  tames_cvs
//  Made ComponentManager an interface and added a
//  DefaultComponentManager implementation.
//
//