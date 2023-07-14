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

package gov.nasa.gsfc.irc.gui.logging;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;
//import informatrix.logGui.logger.LoggerSelector;
//import informatrix.logGui.logger.LoggerView;

/**
 * Launches a Log Viewer.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version Aug 10, 2005 11:20:09 AM
 * @author Peyush Jain
 */

public class LogViewer extends AbstractAction
{
    private static final String CLASS_NAME = LogViewer.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
    private String fLogViewerFile;  
    private URL fUrl;
    private static final String DEFAULT_LOGVIEWER_URL = "resources/xml/core/gui/LogViewer.xml";   
    
    /**
     * Default Constructor for Log Viewer. <p>
     */        
    public LogViewer()
    {
        super();
        
        fLogViewerFile = Irc.getPreference(IrcPrefKeys.LOGVIEWER_GUI_FRAME);
        
        if (fLogViewerFile == null || fLogViewerFile.length() < 1)
        {
            fLogViewerFile = DEFAULT_LOGVIEWER_URL;
        }
        
        fUrl = Sys.getResourceManager().getResource(fLogViewerFile);
    }

    /**
     *  Called when LogViewer action occur.  
     *
     *  @param e  the action event
     */    
    public void actionPerformed(ActionEvent event)
    {
        if (fUrl != null)
        {
            try
            {
                Container frame = Irc.getGuiFactory().render(fUrl);
                WindowUtil.centerFrame(frame);
                frame.setVisible(true);
            }
            catch (Exception e)
            {
                String message = "Exception: " + e.getLocalizedMessage();
                sLogger.logp(Level.WARNING, CLASS_NAME,
                        "actionPerformed", message, e);
            }
        }
        else
        {
            String message = "Could not find Log Viewer file:" + fLogViewerFile;
            sLogger.logp(Level.WARNING, CLASS_NAME, "actionPerformed", message);
        }        
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: LogViewer.java,v $
//  Revision 1.2  2005/09/14 21:57:55  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/09/01 18:28:05  pjain_cvs
//  Adding to CVS.
//
//