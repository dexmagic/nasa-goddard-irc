//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
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

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;


/**
 * Interface for components that can save their properties to a file. Typically,
 * a component will subclass {@link AbstractSaveableComponent}in order to
 * access the functionality. This allows classes to automatically load any save
 * property values. This can be done by calling {@link loadProperties} or,
 * perhaps more conveniently, {@link processPropertyLoading} from a constructor.
 * <P>
 * For example:
 * <pre>
 * public MyClass extends AbstractSaveableComponent
 * {
 *     public MyClass()
 *     {
 *         setName("My Class");
 *         processPropertyLoading(true, false);
 *     }
 * 
 *     public MyClass(String name)
 *     {
 *         super(name);
 *     }
 * 
 * ...
 * }
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/03/07 20:05:01 $
 * @author smaher
 */
public interface Saveable extends Serializable {

    /**
     * Save properties to a file.
     * @param f File to save to
     * @throws FileNotFoundException
     */
    void saveProperties(File f) throws FileNotFoundException;

    /**
     * Load properties from a file.
     * @param f File to load from
     * @throws FileNotFoundException
     */
    void loadProperties(File f) throws Exception;

    /**
     * Save properties to some predefined location
     */
    void saveProperties() throws FileNotFoundException;

    /**
     * Load properties from some predefined location 
     * @throws FileNotFoundException
     */
    void loadProperties() throws Exception;
    
    
    /**
     * Conditionally load properties depending on the IRC property {@link IrcPrefKeys.IRC_COMPONENT_AUTOLOAD_PROPERTIES}
     * and/or the provided argument.
     * <p>
     * This is the typical method called to load a class' persisted properties.
     * It can be called within a constructor of the class - except it shouldn't
     * be called in the constructor which takes a <code>String name</code> arguement.
     * (Refer to the class description for more information).
     * 
     * @param useSystemDefaultForPropertyLoading if true, property loading is
     * only performed if the IRC property is set to true.
     * @param loadFlagIfSystemDefaultNotUsed if the parameter <code>useSystemDefaultForPropertyLoading</code> is false,
     * then property loading is performed if this parameter is true; otherwise property loading is not performed.
     */
    public void processPropertyLoading(boolean useSystemDefaultForPropertyLoading, boolean loadFlagIfSystemDefaultNotUsed);

    /**
     * Restore properties of the calling instance and the default saved property file
     * to the values after a standard instance initialization.
     * In other words, any saved properties are discarded.
     * The following steps are performed in a typical implementation of this method:
     * <ul>
     * <li>Create new instance of this component 
     * <li>Copy property values to this component instance
     * <li>Save property values to default property file
     * </ul>
     * @throws FileNotFoundException if error with saving and loading defaults
     * @throws PropertyVetoException if default values are invalid
     */
    public void restoreAndSaveDefaultProperties() throws Exception;
    
    
}