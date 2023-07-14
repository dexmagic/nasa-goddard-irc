//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PropertyUtil.java,v $
//  Revision 1.6  2006/07/13 17:11:42  smaher_cvs
//  Better text on error.
//
//  Revision 1.5  2006/05/10 17:20:34  smaher_cvs
//  Added property check convenience routines.
//
//  Revision 1.4  2004/12/03 07:09:44  jhiginbotham_cvs
//  Enable support for custom cell editors.
//
//  Revision 1.3  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/09/21 22:09:32  tames_cvs
//  Removed references to obsolete methods or classes.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
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

package gov.nasa.gsfc.irc.gui.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;


/**
 *    This class contains useful methods for aquiring the values of
 *  various GUI properties.  Note that these methods will often contain
 *  logic to get the default value if this property is not supplied. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/07/13 17:11:42 $
 *  @author	    Ken Wootton
 */
public class PropertyUtil
{
	private static final String UNREADABLE_DIR_MSG = 
		"Cannot read default directory specified in plist.  Using alternate default."; 

	/**
	 *    Get the default directory for a GUI file dialog.  This will
	 *  be the user specified directory, specified in the plist file,
	 *  or the default, which is driven from the given parameters.
	 *
	 *   TBD:  Test various user settings.
	 *
	 *  @param resourceClass  the class that will be used as the base
	 *                        for the relative location link
	 *  @param relDirLocation  relative location of the default directory
	 *                         from the given resource class, given as a 
	 *                         string
	 */
	public static File getDefaultDir(Class resourceClass, 
									 String relDirLocation)
	{
		File defaultDir = null;

		//  Try to create a file for the user specified default directory.
		//  Note that having nothing for this property is fine.
		String systemPropDir = 
			Irc.getPreference(IrcPrefKeys.GUI_DIALOG_DEFAULT_DIRECTORY);
		if (systemPropDir != null)
		{
			defaultDir = new File(systemPropDir);
			
			//  If we can't read the directory, we will rely on the default.
			if (!defaultDir.canRead())
			{
				defaultDir = null;
				//Log.getInstance().log(
					//IrcLogEntryFactory.createErrorMsg(
				System.out.println(UNREADABLE_DIR_MSG);
				//, Log.GUI_CAT)); 
			}
		}

		//  If couldn't get a user specified default, just use the parameter
		//  driven default.
		if (defaultDir == null)
		{
			URL resource = resourceClass.getResource(relDirLocation);
			if (resource != null)
			{
				defaultDir = new File(resource.getFile());
			}
		}

		return defaultDir;
	}
	
	/**
	 * Convenience routine for constraint checks
	 * @param propertyValue
	 * @param min
	 * @param max
	 * @param name
	 * @throws PropertyVetoException
	 */
	public static void checkIntegerProperty(Integer propertyValue, int min, int max, String name) throws PropertyVetoException
	{
	    if (propertyValue != null && (propertyValue.intValue() < min || propertyValue.intValue() > max))
	    {
	        throw new PropertyVetoException(name + " must be between " + min + " and " + max  + " (provided value = " + propertyValue.intValue() + ").", null);
	    }
	}

	/**
     * Convenience routine for constraint checks
     * @param propertyValue
     * @param min
     * @param max
     * @param name
     * @throws PropertyVetoException
     */
    public static void checkIntegerProperty(int propertyValue, int min, int max, String name) throws PropertyVetoException
    {
        checkIntegerProperty(new Integer(propertyValue), min, max, name);
    }
    
    /**
     * Convenience routine for constraint checks
     * @param propertyValue
     * @param min
     * @param max
     * @param name
     * @throws PropertyVetoException
     */
    public static void checkFloatProperty(Float propertyValue, float min, float max, String name) throws PropertyVetoException
    {
        if (propertyValue != null && (propertyValue.floatValue() < min || propertyValue.floatValue() > max))
        {
            throw new PropertyVetoException(name + " must be between " + min + " and " + max  + " (provided value = " + propertyValue.floatValue() + ").", null);
        }
    }

}

