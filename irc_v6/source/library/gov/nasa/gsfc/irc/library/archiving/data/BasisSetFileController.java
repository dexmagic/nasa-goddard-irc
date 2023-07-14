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

package gov.nasa.gsfc.irc.library.archiving.data;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.controller.SwixController;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  BasisSetFileController monitors events from BasisSetReader GUI.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version May 31, 2005 9:57:33 AM
 *  @author Peyush Jain
 */

public class BasisSetFileController implements SwixController, ActionListener, 
                                                        ListSelectionListener
{
    private static final String CLASS_NAME = BasisSetFileController.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    private JDialog fDialog;
    private JTable fTable;
    private JButton fOkButton;
    private JButton fCancelButton;
    private JButton fDelButton;
 
    private BasisSetFileModel fBasisSetModel;
    private ListSelectionModel fRowSelectionModel;
    public static ArchiveSession[] sSelectedRows;
    
    
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
        else if(comp instanceof JTable)
        {
            fTable = (JTable)comp;            
            fBasisSetModel = (BasisSetFileModel)fTable.getModel();
            
            fRowSelectionModel = fTable.getSelectionModel();
            fRowSelectionModel.addListSelectionListener(this);
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
            else if(((JButton)comp).getName().equals("delButton"))
            {
                fDelButton = (JButton)comp;
            }
            ((JButton)comp).addActionListener(this);
        }     
    }

    /**
     * Called when button (OK, Cancel, Delete) actions occur.  
     *
     * @param e  the action event
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() instanceof JButton)
        {
            if(((JButton)e.getSource()).getActionCommand().equals("OK_ACTION"))
            {
                int temp[] = fTable.getSelectedRows();
                sSelectedRows = new ArchiveSession[temp.length];
                
                for(int i = 0; i < temp.length; i++)
                {
                    //store selected rows
                    sSelectedRows[i] = fBasisSetModel.getArchiveSession(temp[i]);
                }
                fDialog.dispose();               
            }
            else if(((JButton)e.getSource()).getActionCommand().equals("CANCEL_ACTION"))
            {
//                sSelectedRows = null;
                fDialog.dispose();                
            }
            else if(((JButton)e.getSource()).getActionCommand().equals("DEL_ACTION"))
            {
                Container root = Irc.getGuiFactory().getRootComponent();
                
                int type = JOptionPane.showConfirmDialog(root, 
                        "Are you sure you want to delete ''?",
                        "Confirm File Delete", JOptionPane.OK_CANCEL_OPTION);
                
                if(type == JOptionPane.OK_OPTION)
                {
                    ArchiveCatalog catalog = ArchiveCatalog.getArchiveCatalog();
                    
                    int temp[] = fTable.getSelectedRows();
                    Object sessions[] = catalog.getSessions().toArray();
                    
                    for(int i = 0; i < temp.length; i++)
                    {                        
                        ArchiveSession session = (ArchiveSession)sessions[temp[i]];

                        //delete all the files in the session
                        for(int j = 0; j < session.getFiles().size(); j++)
                        {
                            String fileName = ((ArchiveFileInfo)session.getFiles().elementAt(j)).getFileName();                            
                            File file = new File(fileName);
                            
                            if(file.delete())
                            {
                                if (sLogger.isLoggable(Level.INFO))
                                {
                                    String msg = fileName+" deleted successfully.";
                                    sLogger.logp(Level.INFO, CLASS_NAME, "actionPerformed", msg);
                                }
                            }
                            else
                            {
                                if (sLogger.isLoggable(Level.INFO))
                                {
                                    String msg = fileName+" could not be deleted..";
                                    sLogger.logp(Level.INFO, CLASS_NAME, "actionPerformed", msg);
                                }
                            }
                        }
                        
                        //remove session from the catalog
                        catalog.removeSession(session);
                    }
                    
                    //remove the row from the table model
                    fBasisSetModel.removeRows(temp);
                }
                else if (type == JOptionPane.CANCEL_OPTION)
                {
                    //Ignore
                }
            }
        }        
    }

    /** 
     * Called when list selections occur.  
     *
     * @param e  the action event
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        //enable/disable OK and Del buttons based on selection in the list
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty())
        {
            fOkButton.setEnabled(false);
            fDelButton.setEnabled(false);
        } 
        else
        {
            fOkButton.setEnabled(true);
            fDelButton.setEnabled(true);
        }          
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileController.java,v $
//  Revision 1.2  2005/10/14 13:52:27  pjain_cvs
//  Modified the type of sSelectedRows from Vector to ArchiveSession[]. Also,
//  getArchiveCatalog method is used to get a reference to catalog object
//  (instead of readFromCatalogFile method).
//
//  Revision 1.1  2005/08/19 19:18:47  pjain_cvs
//  Relocated to gov.nasa.gsfc.irc.library.archiving.data package
//
//  Revision 1.2  2005/06/15 18:21:15  pjain_cvs
//  Changed BasisSetModel class to BasisSetFileModel class
//
//  Revision 1.1  2005/06/14 19:32:14  pjain_cvs
//  Adding to CVS
//
//