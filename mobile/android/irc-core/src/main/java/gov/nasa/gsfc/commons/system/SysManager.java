//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.commons.system;

import java.lang.reflect.InvocationTargetException;

import gov.nasa.gsfc.commons.system.resources.ResourceManager;


/**
 * A SysManager as a source of system configuration and environment 
 * information, as well as of system services.
 *
 * @version $Date: 2005/12/01 20:55:35 $	
 * @author Carl F. Hostetter
 * @author Troy Ames
**/
public interface SysManager
{

	/**
	 * Singleton method name used by various class instantiators
	 */
	public static final String GETINSTANCE = "getInstance";
	
	/**
	 * Returns the default Class Loader. Some environments require specific
	 * instances of a class loader in order to find classes and resources.
	 * This method returns the default class loader defined by the 
	 * implementing class.
	 *
	 * @return the default ClassLoader
	 */
	public ClassLoader getDefaultLoader();

	/**
     * Constructs an instance of a class using a <code>getInstance()</code>
     * method if it exists for a singleton class, otherwise it instantiates the
     * class using the default constructor.
	 * 
	 * @param className the name of the class to construct an instance of
	 * @return an instance of the class or null if unsuccessful
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Object instantiateClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	/**
     * Constructs an instance of a class using the given parameters. If the
     * parameters array argument is null or empty this method will attempt to
     * create an instance using a no argument constructor. Although the 
     * parameters array argument cannot contain any <code>null</code> values.
     * 
     * @param aClass the class to construct an instance of
     * @param parameters the parameter array, in order or null
     * @return an instance of the class or null if unsuccessful
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     */
	public Object instantiateClass(Class aClass, Object[] parameters) throws
            InstantiationException, InvocationTargetException,
            IllegalArgumentException, IllegalAccessException;
	
	/**
	 * Loads a class with the given name.
	 * 
	 * @param className a String representation of the class to load
	 * @return the Class
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String className) throws ClassNotFoundException;

	/**
	 * Get the ResourceManager specified by this SysManager.
	 * @return a resource manager
	**/
	public ResourceManager getResourceManager();
}

//--- Development History  ---------------------------------------------------
//
// $Log: SysManager.java,v $
// Revision 1.11  2005/12/01 20:55:35  tames_cvs
// Merged ASF changes into IRC. Changes were with respect to
// the instantiateClass method.
//
// Revision 1.10  2005/05/23 19:55:22  tames_cvs
// Relocated the default class loader to the DefaultSysManager and
// added get methods to Sys and SysManager.
//
// Revision 1.9  2005/01/07 21:32:00  tames
// Added instantiateClass(Class aClass, Object[] parameters) method.
//
// Revision 1.8  2005/01/07 20:14:59  tames
// Updated loadClass method to handle more diverse String representations
// of Class types.
//
// Revision 1.7  2004/12/03 13:28:00  smaher_cvs
// Added support for instantiating a singleton in SysMgr.instantiateClass().
//
// Revision 1.6  2004/09/07 19:57:44  tames
// Added loadClass capability and instantiation from a singleton defined in
// the typemap file.
//
// Revision 1.5  2004/08/03 20:24:00  tames_cvs
// Changes to reflect instantiateClass exception handling changes
//
// Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.3  2004/06/01 15:40:14  tames_cvs
// Added instatiateClass method
//
// Revision 1.2  2004/05/28 22:02:30  tames_cvs
// Bug fixes and testing
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
//
