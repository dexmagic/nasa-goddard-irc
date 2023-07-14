// === File Prolog ============================================================
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.logging;

import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Utility class for custom log handlers.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 25, 2005 10:29:18 AM
 * @author Peyush Jain
 */

public class HandlerUtil
{
    private static LogManager fLogManager = LogManager.getLogManager();

    /**
     * If the property is not defined, return the given default value.
     */
    public static String getStringProperty(String name, String defaultValue)
    {
        String val = fLogManager.getProperty(name);
        if (val == null)
        {
            return defaultValue;
        }
        return val.trim();
    }

    /**
     * If the property is not defined or cannot be parsed, return the given
     * default value.
     */
    public static Level getLevelProperty(String name, Level defaultValue)
    {
        String val = fLogManager.getProperty(name);
        if (val == null)
        {
            return defaultValue;
        }
        try
        {
            return Level.parse(val.trim());
        }
        catch (Exception ex)
        {
            return defaultValue;
        }
    }

    /**
     * Return an instance of the class named by the "name" property. If the
     * property is not defined or has problems, return the defaultValue.
     */
    public static Filter getFilterProperty(String name, Filter defaultValue)
    {
        String val = fLogManager.getProperty(name);
        try
        {
            if (val != null)
            {
                Class clz = ClassLoader.getSystemClassLoader().loadClass(val);
                return (Filter)clz.newInstance();
            }
        }
        catch (Exception ex)
        {
            // We got one of a variety of exceptions in creating the
            // class or creating an instance.
            // Drop through.
        }
        // We got an exception. Return the defaultValue.
        return defaultValue;
    }

    /**
     * Return an instance of the class named by the "name" property. If the
     * property is not defined or has problems, return the defaultValue.
     */
    public static Formatter getFormatterProperty(String name,
            Formatter defaultValue)
    {
        String val = fLogManager.getProperty(name);
        try
        {
            if (val != null)
            {
                Class clz = ClassLoader.getSystemClassLoader().loadClass(val);
                return (Formatter)clz.newInstance();
            }
        }
        catch (Exception ex)
        {
            // We got one of a variety of exceptions in creating the
            // class or creating an instance.
            // Drop through.
        }
        // We got an exception. Return the defaultValue.
        return defaultValue;
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: HandlerUtil.java,v $
// Revision 1.1  2005/09/01 17:15:45  pjain_cvs
// Adding to CVS.
//
//