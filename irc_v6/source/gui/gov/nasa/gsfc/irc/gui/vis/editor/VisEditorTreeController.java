//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui.vis.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.properties.PropertyTableModel;
import gov.nasa.gsfc.irc.gui.swing.PopupMouseDelegate;

/**
 * Tree Controller for the default visualization editor monitors action and 
 * tree selection events from the tree. 
 * Also, it generates a pop up menu for the selected tree node.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/01/10 23:11:08 $
 * @author Troy Ames
 */
public class VisEditorTreeController extends MouseAdapter 
            implements SwixController, ActionListener, TreeSelectionListener
{
    private static final String CLASS_NAME = VisEditorTreeController.class.getName();
    private final Logger sLogger = Logger.getLogger(CLASS_NAME);

    protected JTree fTree;
    protected JPopupMenu fPopupMenu;
    private JTable fTable;    
    private VisEditorTreeModel fTreeModel;
    private PropertyTableModel fTableModel;    
    private TreePath fPathToSelectedNode;
    private PopupMouseDelegate fPopupDelegate = null;

    private String fAddFile = "resources/xml/core/gui/AddComponent.xml";
    private URL fUrl = Sys.getResourceManager().getResource(fAddFile);

    /**
     * Default Constructor
     */
    public VisEditorTreeController()
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
            fTree = (JTree)comp;
            
            //create a pop up menu
            createPopupMenu();

            fTree.addMouseListener(this);
            fTree.addTreeSelectionListener(this);

            fTreeModel = (VisEditorTreeModel)fTree.getModel();
//            ToolTipManager.sharedInstance().registerComponent(fTree);
        }
        else if(comp instanceof JTable)
        {
            fTable = (JTable)comp;
            
            fTableModel = (PropertyTableModel)fTable.getModel();
        }       
    }
    
    /**
     * Creates a popup menu for tree components 
     */    
    protected void createPopupMenu()
    {
        JMenuItem menuItem;

        //Create the popup menu.
        fPopupMenu = new JPopupMenu();
//        menuItem = new JMenuItem("Start");
//        menuItem.setActionCommand("START");
//        menuItem.addActionListener(this);
//        fPopupMenu.add(menuItem);
//        menuItem = new JMenuItem("Stop");
//        menuItem.setActionCommand("STOP");
//        menuItem.addActionListener(this);
//        fPopupMenu.add(menuItem);
//
//        fPopupMenu.addSeparator();

        menuItem = new JMenuItem("Add");
        menuItem.setActionCommand("ADD");
        menuItem.setEnabled(false);
        menuItem.addActionListener(this);
        fPopupMenu.add(menuItem);
        menuItem = new JMenuItem("Remove");
        menuItem.setActionCommand("REMOVE");
        menuItem.setEnabled(false);
        menuItem.addActionListener(this);
        fPopupMenu.add(menuItem);
    }
    
    /**
     * Invoked when the mouse is clicked in the component
     * 
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e)
    {
        checkForPopup(e);
    }

    /**
     * Invoked when a mouse button is pressed while the mouse is in a component.
     * 
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e)
    {
        checkForPopup(e);
    }

    /**
     * Invoked when a mouse button is released while the mouse is in a
     * component.
     * 
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e)
    {
        checkForPopup(e);
    }

    /**
     * Determine if the given mouse event should cause a popup menu to appear.
     */
    private void checkForPopup(MouseEvent e)
    {
        //  If needed, popup the menu at the place where the mouse
        //  was pressed.
        if (e != null && e.isPopupTrigger())
        {
            showPopup(e);
        }
    }

    /**
     * Display the popup menu at the coordinates given by the mouse event.
     * 
     * @param e the mouse event that caused the popup
     */
    private void showPopup(MouseEvent e)
    {
        try
        {
            disableOptions();
            
            if (e != null && fPopupMenu != null)
            {
                fPopupMenu.show((Component) e.getSource(), e.getPoint().x, e
                        .getPoint().y);
            }
        }
        //  If there is a problem, just log it.
        catch (ClassCastException ex)
        {
            if (sLogger.isLoggable(Level.WARNING))
            {
                String message = "Sources of mouse events must be components";
                
                sLogger.logp(Level.WARNING, CLASS_NAME, "showPopup", message, ex);
            }
        }
    }
    
    /**
     * Disables options in the popup menu based on the clicked component.
     */ 
    protected void disableOptions()
    {
//        if(fPopupMenu != null && !fTree.isSelectionEmpty())
//        {
//            Object obj = ((ComponentNode)fTree.getLastSelectedPathComponent()).getUserObject();
//            MenuElement[] items = fPopupMenu.getSubElements();
//    
//            if(obj instanceof HasActivityState)
//            {
//                if(((HasActivityState)obj).isStarted())
//                {
//                    //Disable Start
//                    for (int i = 0; i < items.length; i++)
//                    {
//                        String actionCommand = ((JMenuItem)items[i]).getActionCommand();
//                        if(actionCommand.equals("START"))
//                        {
//                            ((JMenuItem)items[i]).setEnabled(false);
//                        }                        
//                        else
//                        {
//                            ((JMenuItem)items[i]).setEnabled(true);
//                        }
//                    }
//                }
//                else
//                {
//                    if(((HasActivityState)obj).isKilled())
//                    {
//                        //Disable Start and Stop
//                        for (int i = 0; i < items.length; i++)
//                        {
//                            String actionCommand = ((JMenuItem)items[i]).getActionCommand();
//                            if(actionCommand.equals("START") || actionCommand.equals("STOP"))
//                            {
//                                ((JMenuItem)items[i]).setEnabled(false);
//                            }                        
//                            else
//                            {
//                                ((JMenuItem)items[i]).setEnabled(true);
//                            }
//                        }                                            
//                    }
//                    else //Not killed and not started means it is stopped
//                    {
//                        //Disable Stop
//                        for (int i = 0; i < items.length; i++)
//                        {
//                            String actionCommand = ((JMenuItem)items[i]).getActionCommand();
//                            if(actionCommand.equals("STOP"))
//                            {
//                                ((JMenuItem)items[i]).setEnabled(false);
//                            }                        
//                            else
//                            {
//                                ((JMenuItem)items[i]).setEnabled(true);
//                            }
//                        }                                                                    
//                    }
//                }
//            }
//            else
//            {
//                //If it is not an instance of HasActivityState and is an 
//                //instance of ComponentManager then Start, Stop, and Remove 
//                //should be disabled otherwise it is a MinimalComponent 
//                //and therefore Start, Stop, and Add should be disabled
//                if(obj instanceof ComponentManager)
//                {
//                    //Disable Start, Stop, Remove
//                    for (int i = 0; i < items.length; i++)
//                    {
//                        String actionCommand = ((JMenuItem)items[i]).getActionCommand();
//                        if(actionCommand.equals("START") || 
//                                actionCommand.equals("STOP") ||
//                                actionCommand.equals("REMOVE"))
//                                
//                        {
//                            ((JMenuItem)items[i]).setEnabled(false);
//                        }                        
//                        else
//                        {
//                            ((JMenuItem)items[i]).setEnabled(true);
//                        }
//                    }                       
//                }
//                else
//                {
//                    //Disable Start, Stop, Add
//                    for (int i = 0; i < items.length; i++)
//                    {
//                        String actionCommand = ((JMenuItem)items[i]).getActionCommand();
//                        if(actionCommand.equals("START") || 
//                                actionCommand.equals("STOP") ||
//                                actionCommand.equals("ADD"))
//                                
//                        {
//                            ((JMenuItem)items[i]).setEnabled(false);
//                        }                        
//                        else
//                        {
//                            ((JMenuItem)items[i]).setEnabled(true);
//                        }
//                    }    
//                }
//            }        
//        }
    }
    
    /**
     *  Called when various actions occur.  These actions come from Start,
     *  Stop, Add, and Remove items of the popup menu.
     *
     *  @param e  the action event
     */
    public void actionPerformed(ActionEvent e)
    {
        Object obj = ((DefaultMutableTreeNode)fTree.getLastSelectedPathComponent()).getUserObject();

        if(e.getActionCommand().equals("START"))
        {
            if(obj instanceof Startable)
            {
                ((Startable)obj).start();
            }
            else
            {
                System.out.println("ERROR: COMPONENT TYPE IS NOT STARTABLE");
            }
        }
        else if(e.getActionCommand().equals("STOP"))
        {
            if(obj instanceof Startable)
            {
                ((Startable)obj).stop();
            }
            else
            {
                System.out.println("ERROR: COMPONENT TYPE IS NOT STARTABLE");
            }            
        }
        else if(e.getActionCommand().equals("ADD"))
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
                }
                
                //when user chooses a component from add component GUI,
                //its name and type are stored in sSelObjName and sSelObjType
                //resp. If sSelObjName is not null then create an object
                //of that type and set the name. Then, insert this new object
                //in the tree.
//                if(ComponentListController.sSelObjName != null)
//                {
//                    Object newChild = Irc.getDescriptorFramework()
//                        .instantiateFromGlobalMap("ComponentLibraryType", 
//                                ComponentListController.sSelObjType);
//                    
//                    ((MinimalComponent)newChild)
//                                .setName(ComponentListController.sSelObjName);
//
//                    fTreeModel.insertNodeInto(new ComponentNode(newChild), 
//                        (ComponentNode)fTree.getLastSelectedPathComponent(),0);
//                }
            }
            else
            {
                System.out.println("Could not find About file:" + fAddFile);
            }        
        }
        else if(e.getActionCommand().equals("REMOVE"))
        {
            fTreeModel.removeNodeFromParent(
                    (DefaultMutableTreeNode)fTree.getLastSelectedPathComponent());        
        }        
    }       
    
    /** 
     * Called whenever the value of the selection changes.
     * 
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        Object obj = null;
        
        //If a node is selected in the tree then show its properties in the
        //table otherwise show the properties of the parent of the last
        //selected node
        if(fTree.getLastSelectedPathComponent() != null)
        {
            fPathToSelectedNode = fTree.getSelectionPath();
            obj = ((DefaultMutableTreeNode)fTree.getLastSelectedPathComponent())
                                                            .getUserObject();
            fTableModel.setObject(obj);        
        }
        else
        {
            if(fPathToSelectedNode.getParentPath() != null)
            {
                fTree.setSelectionPath(fPathToSelectedNode.getParentPath());
                obj = ((DefaultMutableTreeNode)fTree.getLastSelectedPathComponent())
                                                            .getUserObject();
                fTableModel.setObject(obj);                
            }
        }       
    }
    
//  /**
//   * Invoked when a mouse button has been pressed on a component.
//   * @param e the mouse event
//   */
//    public void mousePressed(MouseEvent e)
//    {
//        if (SwingUtilities.isRightMouseButton(e))
//        {
//            try
//            {
//                Robot robot = new java.awt.Robot();
//                robot.mousePress(InputEvent.BUTTON1_MASK);
//                robot.mouseRelease(InputEvent.BUTTON1_MASK);
//            }
//            catch (AWTException ae)
//            {
//            }
//        }
//    }
//
//    /**
//     * Invoked when a mouse button has been released on a component.
//     * @param e the mouse event
//     */
//    public void mouseReleased(MouseEvent e)
//    {
//        if (fTree != null)
//        {
//            Rectangle rect = fTree.getPathBounds(fTree.getLeadSelectionPath());
//
//            disableOptions();
//
//            if (e.isPopupTrigger() && !fTree.isSelectionEmpty()
//                    && rect.contains(e.getX(), e.getY()))
//            {
//                fPopupMenu.show(e.getComponent(), e.getX(), e.getY());
//            }
//        }
//    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: VisEditorTreeController.java,v $
//  Revision 1.3  2005/01/10 23:11:08  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.2  2005/01/07 21:36:13  tames
//  Updated to reflect relocation of property classes.
//
//  Revision 1.1  2004/12/20 22:41:55  tames
//  Visualization property editing support added.
//
//