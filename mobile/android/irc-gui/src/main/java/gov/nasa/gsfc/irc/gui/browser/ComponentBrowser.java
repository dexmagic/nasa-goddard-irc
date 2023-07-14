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

package gov.nasa.gsfc.irc.gui.browser;


import java.awt.Component;
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
 *  Launches a Component Browser.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Oct 01, 2004 8:30:15 AM
 *  @author Peyush Jain
 */

public class ComponentBrowser extends AbstractAction
{
    private static final String CLASS_NAME = ComponentBrowser.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    private String fCompBrowserFile;  
    private URL fUrl;
    private static final String DEFAULT_BROWSER_URL = "resources/xml/core/gui/BrowserGUI.xml";   
    
    /** The component used for the ComponentBrowser.  This can be used, for example, to
     * act as a parent Component to display warning dialogs down in classes that don't
     * have ready access to a Component.  A bit of a hack, but I couldn't find a better 
     * alternative to handle PropertyVetoExceptions in PropertyTableModel.setValueAt().  S. Maher
     */
    private static Component sComponent;
    
    /**
     * Default Constructor for Component Browser. <p>
     */        
    public ComponentBrowser()
    {
        super();
        fCompBrowserFile = Irc.getPreference(IrcPrefKeys.COMPONENT_BROWSER_GUI_FRAME);
        if (fCompBrowserFile == null || fCompBrowserFile.length() < 1)
        {
            fCompBrowserFile = DEFAULT_BROWSER_URL;
        }
        fUrl = Sys.getResourceManager().getResource(fCompBrowserFile);
    }

    /**
     *  Called when Browser action occur.  
     *
     *  @param e  the action event
     *
     *  @see ComponentTreeController
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
                sComponent = frame;
            }
            catch (Exception e)
            {
                String message = "Exception: " + e.getLocalizedMessage();
                
                System.out.println(message);
                sLogger.logp(Level.WARNING, CLASS_NAME,
                        "actionPerformed", message, e);
            }
        }
        else
        {
            System.out.println("Could not find Component Browser file:" + fCompBrowserFile);
        }        
    }
//    public static void main(String[] args)
//    {
//        //Irc.main(args);
//        new CompBrowser();
//    }    
    public static Component getComponent()
    {
        return sComponent;
    }
    public static void setComponent(Component component)
    {
        sComponent = component;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentBrowser.java,v $
//  Revision 1.6  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.5  2005/01/10 23:09:37  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.4  2005/01/10 14:12:10  smaher_cvs
//  Added dialog box for PropertyVetoExceptions (if the ComponentBrowser is being used).
//
//  Revision 1.3  2004/12/05 14:25:38  smaher_cvs
//  Optional uses a url specific in a property
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//