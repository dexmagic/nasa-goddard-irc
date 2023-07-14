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

package gov.nasa.gsfc.irc.gui.data;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorSupport;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.properties.PropertyTableModel;

/**
 * Controller for the BasisRequestEditor dialog.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/13 03:23:24 $
 * @author Troy Ames
 */
public class BasisRequestEditorController extends WindowAdapter 
	implements SwixController, ActionListener, ListSelectionListener
{
    private static final String CLASS_NAME = BasisRequestEditorController.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    private String fAddFile = "resources/xml/core/gui/AddBasisRequest.xml";
    private URL fUrl = Sys.getResourceManager().getResource(fAddFile);

    private JList fList;
    private JTable fTable;    
    protected PropertyTableModel fTableModel;    
    private DefaultListModel fListModel;
    private LinkedHashMap fRequestsMap = new LinkedHashMap();

    private JButton fRemoveButton;
    private JDialog fDialog;
    private PropertyEditorSupport fCallbackClient = null;

    /**
     * Set the call back Object for the results of this dialog.
     * 
     * @param client the client of this dialog.
     */
    public void setCallbackClient(PropertyEditorSupport client)
    {
    	fCallbackClient = client;
    }
    
    /**
     * Set the current requests that can be edited.
     * 
     * @param requests the current requests
     */
    public void setBasisRequest(BasisRequest[] requests)
    {
    	fRequestsMap.clear();

        for (int i = 0; i < requests.length; i++)
        {
            // TODO need to clone requests before adding to map
            fRequestsMap.put(
            	requests[i].getBasisBundleId(), requests[i]);
        }

        updateListModel();
    }
    
    /**
     * Add the given requests to the current set of requests.
     * 
     * @param requests the requests to add.
     */
    public void addBasisRequests(BasisRequest[] requests)
    {
    	for (int i = 0; i < requests.length; i++)
    	{
            fRequestsMap.put(
            	requests[i].getBasisBundleId(), requests[i]);
    	}

        updateListModel();
    }
    
	/**
	 * Called with the view or component to control or monitor.
	 * 
	 * @param component the component to control.
	 */
    public void setView(Component component)
    {
        if (component instanceof JList)
        {
            if (fList != null)
            {
                fList.removeListSelectionListener(this);
            }
            
            fList = (JList) component;
            fList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fList.setModel(new DefaultListModel());
            fListModel = (DefaultListModel) fList.getModel();
            
            //  Keep track of the current selection so we can update
            //  controls and the editor.
            fList.addListSelectionListener(this);
        }
        else if(component instanceof JTable)
        {
            fTable = (JTable)component;           
            fTableModel = (PropertyTableModel)fTable.getModel();
        } 
        else if(component instanceof JButton)
        {
            if(((JButton)component).getName().equals("REMOVE"))
            {
                fRemoveButton = (JButton)component;
                fRemoveButton.setEnabled(false);
            }
            
            ((JButton)component).addActionListener(this);
        }
        else if(component instanceof JDialog)
        {
            fDialog = (JDialog) component;
            fDialog.addWindowListener(this);
        }
    }

    /**
     * Called when the Add, Remove, or OK buttons are clicked on.
     * 
     * @param e the action event
     */
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
                    AddBasisRequestController controller = 
                    	(AddBasisRequestController) Irc.getGuiFactory().find("Add_Request_Controller");

                    controller.setCallbackClient(this);
                    frame.setVisible(true);
                }
                catch (Exception ex)
                {
                    String message = "Exception performing add action: " 
                        + ex.getLocalizedMessage();
                    sLogger.logp(Level.WARNING, CLASS_NAME, 
                            "actionPerformed", message, ex);
                }              
            }
            else
            {
                System.out.println("Could not find About file:" + fAddFile);
            }        
        }
        else if(e.getActionCommand().equals("REMOVE"))
        {
            // Remove request from the list
            fListModel.removeElement(fList.getSelectedValue());
            fRemoveButton.setEnabled(false);
            fTableModel.setObject(null);
        }    
        else if(e.getActionCommand().equals("OK"))
        {
        	updateClient();
        	fDialog.dispose();
        }    
    }
    
    /** 
     * Called whenever the value of the list selection changes.
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

        // Show the properties of the selected node in the table
        if(fList.getSelectedValue() != null)
        {
            fTableModel.setObject(fList.getSelectedValue());
            fRemoveButton.setEnabled(true);
        }
    }
    
    /**
     * Called to update the BasisRequest List in the dialog
     */
    private void updateListModel()
    {
        if (fListModel != null)
        {
        	Collection requests = fRequestsMap.values();
            fListModel.clear();
            fListModel.ensureCapacity(requests.size());

            for (Iterator iter = requests.iterator(); iter.hasNext();)
            {
                fListModel.addElement(iter.next());
            }
        }       
    }

    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu. Forces the client to be updated.
     */
    public void windowClosing(WindowEvent e)
    {
    	updateClient();
    }
    
    /**
     * Updates the registered client of this class.
     */
    protected void updateClient()
    {
    	fCallbackClient.setValue(fRequestsMap.values().toArray(new BasisRequest[fRequestsMap.size()]));
    }
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisRequestEditorController.java,v $
//  Revision 1.2  2006/01/13 03:23:24  tames
//  Comment change only.
//
//  Revision 1.1  2006/01/02 03:52:39  tames
//  Initial version.
//
//