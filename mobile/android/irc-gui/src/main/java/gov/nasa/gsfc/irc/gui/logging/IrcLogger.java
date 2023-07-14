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

import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IrcLogger has a handle to logger object. It has all the public methods of
 * Logger along with an extra setHandlers method. This extra method has to be 
 * exposed so that the users can have access to LoggerHandlerEditor 
 * (to edit the handlers for the selected logger) in LogViewer GUI.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 19, 2005 2:29:54 PM
 * @author Peyush Jain
 */

public class IrcLogger
{
    private Logger fLogger;

    /**
     * Constructor
     */
    public IrcLogger(Logger logger)
    {
        super();
        fLogger = logger;
    }

    /**
     * @see Logger#getUseParentHandlers()
     */
    public synchronized boolean getUseParentHandlers()
    {
        return fLogger.getUseParentHandlers();
    }

    /**
     * @see Logger#setUseParentHandlers()
     */
    public synchronized void setUseParentHandlers(boolean useParentHandlers)
    {
        fLogger.setUseParentHandlers(useParentHandlers);
    }

    /**
     * @see Logger#getName()
     */
    public String getName()
    {
        return fLogger.getName();
    }

    /**
     * @see Logger#getHandlers()
     */
    public Handler[] getHandlers()
    {
        return fLogger.getHandlers();
    }
    
    /**
     * Set Handlers
     */
    public void setHandlers(Handler[] handlers)
    {
        //Do nothing
    }

    /**
     * @see Logger#getParent()
     */
    public Logger getParent()
    {
        return fLogger.getParent();
    }

    /**
     * @see Logger#setParent()
     */
    public void setParent(Logger parent)
    {
        fLogger.setParent(parent);
    }

    /**
     * @see Logger#setLevel()
     */
    public void setLevel(Level newLevel) throws SecurityException
    {
        fLogger.setLevel(newLevel);
    }

    /**
     * @see Logger#getLevel()
     */
    public Level getLevel()
    {
        return fLogger.getLevel();
    }

    /**
     * @see Logger#setFilter()
     */
    public void setFilter(Filter newFilter) throws SecurityException
    {
        fLogger.setFilter(newFilter);
    }

    /**
     * @see Logger#getFilter()
     */
    public Filter getFilter()
    {
        return fLogger.getFilter();
    }

    /**
     * @see Logger#getResourceBundle()
     */
    public ResourceBundle getResourceBundle()
    {
        return fLogger.getResourceBundle();
    }

    /**
     * @see Logger#getResourceBundleName()
     */
    public String getResourceBundleName()
    {
        return fLogger.getResourceBundleName();
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: IrcLogger.java,v $
// Revision 1.2  2005/10/07 13:38:25  pjain_cvs
// Added setHandlers method and removed getHandler & setHandler methods.
//
// Revision 1.1  2005/09/01 18:09:47  pjain_cvs
// Adding to CVS.
//
//