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
 * A ComponentNamespaceManager is a bean that contains and manages a Namespace 
 * of Components.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:51 $
 * @author	Carl F. Hostetter
 **/

public interface ComponentNamespaceManager 
	extends ComponentManager, ComponentNamespace
{
	/**
	 *	Starts the Component managed by this ComponentNamespaceManager that 
	 *  has the given sequenced (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name 
	 *  		of some Component managed by this ComponentNamespaceManager
	 */
	
	public void startComponent(String sequencedName);
	

	/**
	 *	Stops the Component managed by this ComponentNamespaceManager that 
	 *  has the given sequenced (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name 
	 *  		of some Component managed by this ComponentNamespaceManager
	 */
	
	public void stopComponent(String sequencedName);
	

	/**
	 *	Kills the Component managed by this ComponentNamespaceManager that 
	 *  has the given sequenced (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name 
	 *  		of some Component managed by this ComponentNamespaceManager
	 */
	
	public void killComponent(String sequencedName);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentNamespaceManager.java,v $
//  Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//