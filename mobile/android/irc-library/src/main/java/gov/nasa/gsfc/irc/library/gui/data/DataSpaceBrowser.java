//=== File Prolog ============================================================
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *   Neither the author, their corporation, nor NASA is responsible for
//      any consequence of the use of this software.
//  *   The origin of this software must not be misrepresented either by
//      explicit claim or by omission.
//  *   Altered versions of this software must be plainly marked as such.
//  *   This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.gui.data;

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

/**
 * Launches a Data Space Browser.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/08/31 16:12:03 $
 * @author Troy Ames
 */
public class DataSpaceBrowser extends AbstractAction
{
    private static final String CLASS_NAME = DataSpaceBrowser.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    private String fBrowserFile;  
    private URL fUrl;
    private static final String DEFAULT_BROWSER_URL = 
    	"resources/xml/core/gui/DataSpaceBrowser.xml";   
    
    /**
     * Default Constructor for DataSpaceBrowser. 
     */        
    public DataSpaceBrowser()
    {
        super();
        fBrowserFile = Irc.getPreference(IrcPrefKeys.DATA_SPACE_BROWSER_GUI_FRAME);

        if (fBrowserFile == null || fBrowserFile.length() < 1)
        {
            fBrowserFile = DEFAULT_BROWSER_URL;
        }

        fUrl = Sys.getResourceManager().getResource(fBrowserFile);
    }

    /**
	 * Called when a Browser action occurs.
	 * 
	 * @param e the action event
	 * @see DataSpaceTreeController
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
            String message = "Could not find DataSpace Browser file:" + fBrowserFile;
            sLogger.logp(Level.WARNING, CLASS_NAME, "actionPerformed", message);
        }        
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpaceBrowser.java,v $
//  Revision 1.1  2005/08/31 16:12:03  tames_cvs
//  Inital simple implementation.
//
//