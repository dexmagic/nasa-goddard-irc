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
//   * Neither the author, their corporation, nor NASA is responsible for
//     any consequence of the use of this software.
//   * The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//   * Altered versions of this software must be plainly marked as such.
//   * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.properties;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.gui.data.BasisRequestEditorController;

/**
 * Editor for adding, removing and changing an array of Basis requests.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 19, 2005 3:22:37 PM
 * @author Troy Ames
 */

public class BasisRequestArrayEditor extends SwingEditorSupport implements ActionListener
{
    private static final String CLASS_NAME = BasisRequestArrayEditor.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    private JButton fButton;    
    private JList fList;
    private DefaultListModel fListModel;
    private JScrollPane fScrollPane;
    private BasisRequest[] fRequests = null;
    
    private String fEditorDescription = "resources/xml/core/gui/BasisRequestArrayEditor.xml";
    private URL fEditorUrl = Sys.getResourceManager().getResource(fEditorDescription);
    
    /**
     * Default Constructor
     */
    public BasisRequestArrayEditor()
    {
        //create a button to pop up a window to add, remove, and edit requests
        fButton = new JButton("Edit...");
        
        //create a list to display a list of requests
        fList = new JList();
        
        //disable user inputs on the list
        fList.setEnabled(false);
        
        //create a list model for adding/removing elements
        fListModel = new DefaultListModel();
        fList.setModel(fListModel);
        fList.setVisibleRowCount(-1);

        fScrollPane = new JScrollPane(fList);
        
        //use gridbag layout
        fPanel = new JPanel(new GridBagLayout());
        fPanel.setPreferredSize(new Dimension(250,70));
        
        GridBagConstraints constraints = new GridBagConstraints();
        
        //-- fill, weightx, weighty comes into play when window is resized
        
        //fills component's grid in both x and y directions
        constraints.fill = GridBagConstraints.BOTH;
        
        //gives max. space to the component's grid in both x and y directions
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        
        //place the component in grid(x,y)
        constraints.gridx = 0;
        constraints.gridy = 0;
        
        //add component with the constraints to the panel
        fPanel.add(fScrollPane, constraints);
        
        //-- change certain constraints for next component
        
        //don't fill (uses its own size) component's grid in both directions
        constraints.fill = GridBagConstraints.NONE;

        //gives min. space to the component's grid in both x and y directions
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        
        //place the component in grid(x,y)
        constraints.gridx = 1;
        constraints.gridy = 0;

        //add component with the constraints to the panel
        fPanel.add(fButton, constraints);

        fButton.addActionListener(this);
    }

    /** 
     * @see java.beans.PropertyEditorSupport#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
        // Add the requests to the model
        if(value instanceof BasisRequest[])
        {   
	        // Since editor instance is shared, clear out the model before using it 
	        fListModel.clear();
	        
			fRequests = (BasisRequest[])value;

            for(int i = 0; i < fRequests.length; i++)
            {
                fListModel.addElement(fRequests[i]);
            }

            super.setValue(value);
        }
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JButton)
        {
            if (fEditorUrl != null)
            {
                try
                {
                    //Container frame = Irc.getGuiFactory().render(fEditorUrl, this);
                    Container frame = Irc.getGuiFactory().render(fEditorUrl);
                    // WindowUtil.centerFrame(frame);
                    BasisRequestEditorController controller = 
                    	(BasisRequestEditorController) Irc.getGuiFactory().find("Selected_Request_Controller");
                    
                    controller.setCallbackClient(this);
                    controller.setBasisRequest(fRequests);
                    frame.setVisible(true);
                }
                catch (Exception ex)
                {
                    String msg = "Exception performing select action: "
                            + ex.getLocalizedMessage();
                    sLogger.logp(Level.WARNING, CLASS_NAME, "actionPerformed",
                            msg, ex);
                }
            }
            else
            {
                if (sLogger.isLoggable(Level.INFO))
                {
                    String msg = "Could not find Editor description file:"
                            + fEditorDescription;
                    sLogger.logp(Level.INFO, CLASS_NAME, "actionPerformed", msg);
                }
            }
        }
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: BasisRequestArrayEditor.java,v $
// Revision 1.1  2006/01/02 03:53:06  tames
// Initial version.
//
//