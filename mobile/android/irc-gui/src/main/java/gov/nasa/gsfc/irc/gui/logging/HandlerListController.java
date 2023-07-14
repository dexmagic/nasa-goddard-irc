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

import gov.nasa.gsfc.irc.gui.controller.SwixController;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Handler;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;

/**
 * Handler List Controller monitors action events from Add Handler GUI.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 22, 2005 2:33:24 PM
 * @author Peyush Jain
 */

public class HandlerListController implements SwixController, ActionListener
{
    private JDialog fDialog;
    private JList fList;
    private JButton fOkButton;
    private JButton fCancelButton;

    public static Handler sSelObj;

    /**
     * Set the views that this controller will monitor.
     * 
     * @param comp the view or component to control or monitor.
     * @see gov.nasa.gsfc.irc.gui.controller.SwixController#setView(java.awt.Component)
     */
    public void setView(Component comp)
    {
        if (comp instanceof JDialog)
        {
            fDialog = (JDialog)comp;
        }
        else if (comp instanceof JList)
        {
            fList = (JList)comp;
        }
        else if (comp instanceof JButton)
        {
            if (((JButton)comp).getName().equals("okButton"))
            {
                fOkButton = (JButton)comp;
            }
            else if (((JButton)comp).getName().equals("cancelButton"))
            {
                fCancelButton = (JButton)comp;
            }
            ((JButton)comp).addActionListener(this);
        }
    }

    /**
     * Called when various actions occur. These actions come from OK and Cancel
     * buttons in Add Handler GUI.
     * 
     * @param e the action event
     * @see HandlerTreeController
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JButton)
        {
            if (((JButton)e.getSource()).getActionCommand().equals("OK_ACTION"))
            {
                // if OK button is clicked, save object in a static variable
                sSelObj = (Handler)fList.getSelectedValue();
                fDialog.dispose();
            }
            else if (((JButton)e.getSource()).getActionCommand().equals(
                    "CANCEL_ACTION"))
            {
                sSelObj = null;
                fDialog.dispose();
            }
        }
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: HandlerListController.java,v $
// Revision 1.1  2005/09/01 16:11:11  pjain_cvs
// Adding to CVS.
//
//