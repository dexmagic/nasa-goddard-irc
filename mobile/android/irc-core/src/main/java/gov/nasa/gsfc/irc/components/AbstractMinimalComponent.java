//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/AbstractMinimalComponent.java,v 1.20 2006/03/14 14:57:16 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
//  This class requires JDK 1.1 or later.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  * Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//  * The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//  * Altered versions of this software must be plainly marked as such.
//  * This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.components;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamespaceMemberBean;


/**
 *	A MinimalComponent is a Serializable Object that has a name and a set of 
 *  properties.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2006/03/14 14:57:16 $
 *	@author Carl F. Hostetter
 */

public abstract class AbstractMinimalComponent extends AbstractNamespaceMemberBean 
	implements MinimalComponent
{
	private static final String CLASS_NAME = 
		AbstractMinimalComponent.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Component";
	
	// The lock is serializable so that this class can be serialized 
	private Object fConfigurationChangeLock = new Serializable() {};
	
	
//----------------------------------------------------------------------
//	Construction-related methods
//----------------------------------------------------------------------

	/**
	 *  Constructs a new Component whose ComponentId is the given ComponentId. 
	 *  
	 *  @param componentId The ComponentId of the new Component
	 **/

	protected AbstractMinimalComponent(ComponentId componentId)
	{
		super(componentId);
	}
		
	
	/**
	 *	Constructs a new Component having a default name.
	 *
	 */
	
	public AbstractMinimalComponent()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *  Constructs a new Component having the given base name.
	 * 
	 *  @param name The base name of the new Component
	 **/

	public AbstractMinimalComponent(String name)
	{
		super(new DefaultComponentId(name));
	}
	
	
	/**
	 *  Returns the lock Object that this Component uses to synchronize 
	 *  configuration changes.
	 *  
	 *  @return	The lock Object that this Component uses to synchronize 
	 *  		configuration changes
	 **/

	protected Object getConfigurationChangeLock()
	{
		return (fConfigurationChangeLock);
	}
	

	/**
	 * Returns the ComponentId of this Component.
	 *
	 * @return The ComponentId of this Component
	 **/
	
	public ComponentId getComponentId()
	{
		return ((ComponentId) getMemberId());
	}


//----------------------------------------------------------------------
//	Serialization-related methods
//----------------------------------------------------------------------

	/**
	 * Serialization support for writing out an instance of this class.
	 * 
	 * @param out the stream to write an object out to.
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
	}

	/**
	 * Serialization support for reading in an instance of this class.
	 * 
	 * @param in the stream to read an object from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{
		in.defaultReadObject();
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: AbstractMinimalComponent.java,v $
// Revision 1.20  2006/03/14 14:57:16  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.19  2006/03/07 23:32:42  chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
// Revision 1.18  2006/01/23 17:59:51  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.17  2005/09/30 16:54:46  smaher_cvs
// Made fConfigurationChangeLock serializable so the entire class is serializable
//
// Revision 1.16  2005/04/20 14:39:00  tames_cvs
// Javadoc updates only.
//
// Revision 1.15  2005/04/16 04:03:55  tames
// Changes to reflect refactored state and activity packages.
//
// Revision 1.14  2005/01/21 20:02:39  smaher_cvs
// Added support for vetoable property changes.
//
// Revision 1.13  2004/07/22 20:14:58  chostetter_cvs
// Data, Component namespace work
//
// Revision 1.12  2004/07/17 18:39:02  chostetter_cvs
// Name, descriptor modification work
//
// Revision 1.11  2004/07/16 04:15:44  chostetter_cvs
// More Algorithm work, primarily on properties
//
// Revision 1.10  2004/07/15 18:27:11  chostetter_cvs
// Add rich set of firePropertyChange event methods  to Components
//
// Revision 1.9  2004/07/15 17:48:55  chostetter_cvs
// ComponentManager, property change work
//
// Revision 1.8  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.7  2004/07/11 21:19:44  chostetter_cvs
// More Algorithm work
//
// Revision 1.6  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.5  2004/06/04 18:54:29  tames_cvs
// Changed fPropertyChangeListeners from private to protected 
// so subclasses can fire property change events.
//
// Revision 1.4  2004/06/04 18:02:24  tames_cvs
// Added getPropertyChangeListeners() method
//
// Revision 1.3  2004/06/04 05:34:42  chostetter_cvs
// Further data, Algorithm, and Component work
//
// Revision 1.2  2004/06/03 00:19:59  chostetter_cvs
// Organized imports
//
// Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
// More Namespace, DataSpace tweaks, created alogirthms package
//
//
