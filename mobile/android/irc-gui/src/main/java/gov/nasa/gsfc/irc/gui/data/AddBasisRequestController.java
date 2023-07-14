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

package gov.nasa.gsfc.irc.gui.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.HistoryBasisRequest;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.gui.controller.SwixController;

/**
 * Controller for the Add Basis Request dialog.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Troy Ames
 */
public class AddBasisRequestController implements SwixController, ActionListener
{
    private JDialog fDialog;
    protected JTree fTree;
    private JComboBox fRequestTypeBox;
    private Object fCallbackClient = null;

    /**
     * Set the components that this controller will monitor.
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
        else if (comp instanceof JTree)
        {
			fTree = (JTree) comp;
        }
        else if (comp instanceof JButton)
        {
            ((JButton)comp).addActionListener(this);
        }
        else if (comp instanceof JComboBox)
        {
        	fRequestTypeBox = (JComboBox) comp;
            
        	// TODO remove the hardcoding of the valid choices
        	fRequestTypeBox.addItem("default");
        	fRequestTypeBox.addItem("history");
        }
    }

    /**
     * Called when the OK and Cancel buttons are clicked on.
     * 
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JButton)
        {
            if (((JButton)e.getSource()).getActionCommand().equals("OK_ACTION"))
            {
                // if OK button is clicked, call back to client the selection
                if (fCallbackClient != null)
                {
                	((BasisRequestEditorController) fCallbackClient).addBasisRequests(
                        getBasisRequests(fTree.getSelectionPaths()));
                }
                
                fDialog.dispose();
            }
            else if (((JButton)e.getSource()).getActionCommand().equals(
                    "CANCEL_ACTION"))
            {
                // Throw away any selections
                fDialog.dispose();
            }
        }
    }
    
    /**
     * Set the call back Object for the results of this dialog.
     * 
     * @param client the client of this dialog.
     */
    public void setCallbackClient(Object client)
    {
    	fCallbackClient = client;
    }
    
    /**
     * Converts the current selected paths into BasisRequests.
     * 
     * @param paths the selected tree paths.
     * @return the basis requests
     */
    private BasisRequest[] getBasisRequests(TreePath[] paths)
    {
        Map requestMap = new LinkedHashMap();

        for (int i = 0; paths != null && i < paths.length; i++)
        {
        	int pathLength = paths[i].getPathCount();
        	
        	BasisBundle bundle = 
        		(BasisBundle)((DataNode) paths[i].getPathComponent(1)).getUserObject();
        	BasisBundleId bundleId = bundle.getBasisBundleId();
        	
        	if (!requestMap.containsKey(bundleId))
        	{
        		// Create a new BasisRequest and add it to the Map
        		String type = (String) fRequestTypeBox.getSelectedItem();        		
        		BasisRequest request = null;

        		if (type.equals("default"))
        		{
        			request = new BasisRequest(bundleId);
        		}
        		else
        		{
        			request = new HistoryBasisRequest(bundleId);
        		}

        		requestMap.put(bundleId, request);	
        	}
        	
        	if (pathLength > 2)
        	{
        		// A data buffer was specified so add it to the requests
            	DataBufferDescriptor buffer = 
        			(DataBufferDescriptor)((DataNode) paths[i].getPathComponent(2)).getUserObject();
        		BasisRequest request = (BasisRequest) requestMap.get(bundleId);
        		request.addDataBuffer(buffer.getName());
        	}
        }
        
        return (BasisRequest[]) requestMap.values().toArray(new BasisRequest[0]);
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: AddBasisRequestController.java,v $
// Revision 1.3  2006/08/01 19:55:47  chostetter_cvs
// Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
// Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.1  2006/01/02 03:52:39  tames
// Initial version.
//
//