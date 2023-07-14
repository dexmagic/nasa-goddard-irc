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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import gov.nasa.gsfc.irc.gui.controller.SwixController;

/**
 *  Component List Controller monitors action events from Add Component GUI.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Oct 06, 2004 10:17:29 PM
 *  @author Peyush Jain
 */

public class ComponentListController implements SwixController, ActionListener
{
    private JDialog fDialog;
    private JList fList;
    private JTextField fTextField;
    private JButton fOkButton;
    private JButton fCancelButton;
    
    public static String sSelObjType;
    public static String sSelObjName;
    
    private Document document;
    
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
        else if(comp instanceof JTextField)
        {
            fTextField = (JTextField)comp;
            document = fTextField.getDocument();
            
            //disable the OK button if there is no text in
            //the textfield
            document.addDocumentListener(new DocumentListener()
                    {
                        public void insertUpdate(DocumentEvent e)
                        {
                            disableIfEmpty(e);
                        }
        
                        public void removeUpdate(DocumentEvent e)
                        {
                            disableIfEmpty(e);
                        }
        
                        public void changedUpdate(DocumentEvent e)
                        {
                            disableIfEmpty(e);
                        }
                        
                        public void disableIfEmpty(DocumentEvent e)
                        {
                            if (e.getDocument() == document)
                            {
                                fOkButton.setEnabled(document.getLength() > 0);
                            }                             
                        }
                    });
        }
        else if(comp instanceof JButton)
        {
            if(((JButton)comp).getName().equals("okButton"))
            {
                fOkButton = (JButton)comp;
            }
            else if(((JButton)comp).getName().equals("cancelButton"))
            {
                fCancelButton = (JButton)comp;
            }
            ((JButton)comp).addActionListener(this);
        }
    }
    
    /**
     *  Called when various actions occur.  These actions come from OK and 
     *  Cancel buttons in Add Component GUI.
     *
     *  @param e  the action event
     *
     *  @see ComponentTreeController
     */
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() instanceof JButton)
        {
            if(((JButton)e.getSource()).getActionCommand().equals("OK_ACTION"))
            {
                //if OK button is clicked, save object name and type in
                //a static variable
                sSelObjType = (String)fList.getSelectedValue();
                sSelObjName = fTextField.getText();
                fDialog.dispose();               
            }
            else if(((JButton)e.getSource()).getActionCommand().equals("CANCEL_ACTION"))
            {
                sSelObjType = null;
                sSelObjName = null;
                fDialog.dispose();                
            }
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentListController.java,v $
//  Revision 1.2  2004/12/16 22:58:38  tames
//  Updated to reflect swix package restructuring.
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//