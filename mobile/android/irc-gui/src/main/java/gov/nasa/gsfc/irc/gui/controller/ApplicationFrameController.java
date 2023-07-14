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

package gov.nasa.gsfc.irc.gui.controller;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.logging.LogViewer;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;

/**
 * Application Frame Controller monitors window closing event from Application
 * Frame.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Oct 11, 2005 1:16:18 PM
 * @author Peyush Jain
 */

public class ApplicationFrameController extends WindowAdapter implements SwixController
{
    private static final String CLASS_NAME = LogViewer.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    private JFrame fFrame;

    /**
     * @see gov.nasa.gsfc.irc.gui.controller.SwixController#setView(java.awt.Component)
     */
    public void setView(Component component)
    {
        if (component instanceof JFrame)
        {
            fFrame = (JFrame)component;
            fFrame.addWindowListener(this);
        }
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e)
    {
        //shut down IRC application
        Irc.shutdown();
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ApplicationFrameController.java,v $
//  Revision 1.1  2005/10/14 13:42:41  pjain_cvs
//  Adding to CVS.
//
//