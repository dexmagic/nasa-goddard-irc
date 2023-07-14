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

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.properties.PropertyTableModel;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

/**
 * Selected Handler Controller monitors action events from LogHandler GUI. Users
 * can change the selected handlers' properties using LogHandler GUI.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version Aug 22, 2005 11:44:05 AM
 * @author Peyush Jain
 */

public class SelectedHandlerController extends WindowAdapter implements SwixController, 
                            ActionListener, ListSelectionListener
{
    private static final String CLASS_NAME = LogViewer.class.getName();
    private static final Logger sLogger = Logger.getLogger("");

    private String fAddFile = "resources/xml/core/gui/AddHandler.xml";
    private URL fUrl = Sys.getResourceManager().getResource(fAddFile);

    private JList fList;
    private JTable fTable;    
    protected PropertyTableModel fTableModel;    
    private DefaultListModel fListModel;

    private JComboBox fPriorityOptions;
    private JButton fAddButton;
    private JButton fRemoveButton;
    private JDialog fDialog;

    public static String sHandlers;


    public void setView(Component component)
    {
        if (component instanceof JComboBox)
        {
            fPriorityOptions = (JComboBox) component;
            fPriorityOptions.addActionListener(this);
        }
        else if (component instanceof JList)
        {
            if (fList != null)
            {
                fList.removeListSelectionListener(this);
            }
            
            fList = (JList) component;            
            fList.setModel(new DefaultListModel());
            //  Keep track of the current selection so we can update
            //  controls and the editor.
            fList.addListSelectionListener(this);

            fListModel = (DefaultListModel)fList.getModel();
            
            Handler[] handlers = LoggerTreeController.sSelectedLogger.getHandlers(); 
            for (int i = 0; i < handlers.length; i++)
            {
                fListModel.addElement(handlers[i]);
            }
        }
        else if(component instanceof JTable)
        {
            fTable = (JTable)component;
            
            fTableModel = (PropertyTableModel)fTable.getModel();
        } 
        else if(component instanceof JButton)
        {
            if(((JButton)component).getName().equals("ADD"))
            {
                fAddButton = (JButton)component;
            }
            else if(((JButton)component).getName().equals("REMOVE"))
            {
                fRemoveButton = (JButton)component;
            }
            ((JButton)component).addActionListener(this);
        }
        else if(component instanceof JDialog)
        {
            fDialog = (JDialog) component;
            fDialog.addWindowListener(this);
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("ADD"))
        {
            if (fUrl != null)
            {
                try
                {
                    Container frame = Irc.getGuiFactory().render(fUrl);
                    //WindowUtil.centerFrame(frame);
                    frame.setVisible(true);
                }
                catch (Exception ex)
                {
                    String message = "Exception performing add action: " 
                        + ex.getLocalizedMessage();
                    sLogger.logp(Level.WARNING, CLASS_NAME, 
                            "actionPerformed", message, ex);
                }
                
                //when user chooses a handler from add handler GUI, it is stored
                //in sSelObj. If sSelObj is not null then add it to the logger
                //and insert it in the list
                if(HandlerListController.sSelObj != null)
                {
                    //add handler to the logger                    
                    LoggerTreeController.sSelectedLogger.addHandler(HandlerListController.sSelObj);

                    //add handler to the list
                    fListModel.addElement(HandlerListController.sSelObj);
                }
            }
            else
            {
                System.out.println("Could not find About file:" + fAddFile);
            }        
        }
        else if(e.getActionCommand().equals("REMOVE"))
        {
            //remove handler from the logger
            LoggerTreeController.sSelectedLogger.removeHandler(HandlerListController.sSelObj);
            
            //remove handler from the list
            fListModel.removeElement(fList.getSelectedValue());        
        }    
    }
    
    /** 
     * Called whenever the value of the selection changes.
     * 
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (fTable != null)
        {
            //---Clear current edits to make sure any user changes are complete.
            TableCellEditor editor = fTable.getCellEditor();
            
            if (editor != null)
            {
                editor.stopCellEditing();
            }
        }

        //show the properties of the selected node in the table
        if(fList.getSelectedValue() != null)
        {
            fTableModel.setObject((Handler)fList.getSelectedValue());
        }
    }

    public void windowClosing(WindowEvent e)
    {
        //initialize sHandlers
        sHandlers = "";
        for (int i = 0; i < fListModel.getSize(); i++)
        {
            sHandlers = sHandlers + fListModel.getElementAt(i) + ";";
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: SelectedHandlerController.java,v $
//  Revision 1.1  2005/09/01 19:17:04  pjain_cvs
//  Adding to CVS.
//
//