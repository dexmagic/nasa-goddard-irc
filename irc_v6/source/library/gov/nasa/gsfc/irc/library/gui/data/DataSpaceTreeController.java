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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.data.DataNode;
import gov.nasa.gsfc.irc.gui.data.DataSpaceTreeModel;

/**
 * DataSpaceTreeController monitors action and tree selection events from the
 * tree.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/12/15 15:24:08 $
 * @author Troy Ames
 */
public class DataSpaceTreeController extends MouseAdapter 
            implements SwixController, ActionListener, TreeSelectionListener
{
    private static final String CLASS_NAME = DataSpaceTreeController.class.getName();
    private final Logger sLogger = Logger.getLogger(CLASS_NAME);

    protected JTree fTree;
    private DataSpaceTreeModel fTreeModel;
    private TreePath fPathToSelectedNode;

    /**
     * Default Constructor
     */
    public DataSpaceTreeController()
    {
    }
    
    /**
     * Set the views that this controller will monitor.
     * 
     * @param comp the view or component to control or monitor.
     * @see gov.nasa.gsfc.irc.gui.controller.SwixController#setView(java.awt.Component)
     */ 
    public void setView(Component comp)
    {
    	if (comp instanceof JTree)
        {
			if (fTree != null)
			{
				fTree.removeTreeSelectionListener(this);
			}
			
            fTree = (JTree) comp;
            
			//  Keep track of the current selection so we can update
			//  controls and the editor.
			fTree.addTreeSelectionListener(this);

            fTreeModel = (DataSpaceTreeModel)fTree.getModel();
        }
    }

    /**
     *  Called when various actions occur.  Currently does nothing.
     *
     *  @param e  the action event
     */
    public void actionPerformed(ActionEvent e)
    {
        Object obj = 
        	((DataNode) fTree.getLastSelectedPathComponent()).getUserObject();
    }       
    
    /** 
     * Called whenever the value of the selection changes.
     * 
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        Object obj = null;   
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpaceTreeController.java,v $
//  Revision 1.3  2005/12/15 15:24:08  tames
//  Updated to reflect relocation of the DataNode class.
//
//  Revision 1.2  2005/09/14 21:57:55  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/08/31 16:12:03  tames_cvs
//  Inital simple implementation.
//
//