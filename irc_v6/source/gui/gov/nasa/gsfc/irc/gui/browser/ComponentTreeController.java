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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.MenuElement;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import com.thoughtworks.xstream.XStream;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.properties.PropertyTableModel;
import gov.nasa.gsfc.irc.gui.swing.PopupMouseDelegate;

/**
 *  Component Tree Controller monitors action and tree selection events from 
 *  component tree. Also, it generates a pop up menu for the tree components.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Oct 05, 2004 11:23:25 AM
 *  @author Peyush Jain
 */
public class ComponentTreeController extends MouseAdapter 
            implements SwixController, ActionListener, TreeSelectionListener
{
    protected JTree fTree;
    protected JPopupMenu fPopupMenu;
    protected PropertyTableModel fTableModel;    
    private JTable fTable;    
    private ComponentTreeModel fTreeModel;
    private TreePath fPathToSelectedNode;
    private PopupMouseDelegate fPopupDelegate = null;


    private String fAddFile = "resources/xml/core/gui/AddComponent.xml";
    private URL fUrl = Sys.getResourceManager().getResource(fAddFile);
    private static final String CLASS_NAME = ComponentTreeController.class.getName();
    private final Logger sLogger = Logger.getLogger(CLASS_NAME);

    /**
     * Default Constructor
     */
    public ComponentTreeController()
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
		if (comp instanceof JPopupMenu)
		{
			JPopupMenu menu = (JPopupMenu) comp;
			
			if (fPopupMenu == null)
			{
				fPopupMenu = menu;
                
                //  We use a delegate to determine when to popup the menu.      
                fPopupDelegate = new PopupMouseDelegate(fPopupMenu)
                {
                    protected void showPopup(MouseEvent e)
                    {
                        resetMenuOptions();
                        super.showPopup(e);
                    }
                };
            }			

			if (fPopupDelegate != null && fTree != null)
			{
				// Establish new connections
				((JComponent) fTree).addMouseListener(fPopupDelegate);
			}
		}
		else if (comp instanceof JTree)
        {
			if (fTree != null)
			{
				fTree.removeTreeSelectionListener(this);
			}
			
            fTree = (JTree) comp;
            
			if (fPopupDelegate != null)
			{
				//  We use a delegate to determine when to popup the menu.		
				fTree.addMouseListener(fPopupDelegate);
			}
			
			//  Keep track of the current selection so we can update
			//  controls and the editor.
			fTree.addTreeSelectionListener(this);

            fTreeModel = (ComponentTreeModel)fTree.getModel();
//            ToolTipManager.sharedInstance().registerComponent(fTree);
        }
        else if(comp instanceof JTable)
        {
            fTable = (JTable)comp;
            
            fTableModel = (PropertyTableModel)fTable.getModel();
        }       
		else if (comp instanceof JMenuItem)
		{
			((JMenuItem) comp).addActionListener(this);
		}
    }

    
    /**
     * Resets options in the popup menu based on the clicked component.
     */ 
    protected void resetMenuOptions()
    {
        if(fPopupMenu != null && !fTree.isSelectionEmpty())
        {
            Object obj = ((ComponentNode)fTree.getLastSelectedPathComponent()).getUserObject();
            MenuElement[] items = fPopupMenu.getSubElements();
    
            if(obj instanceof Startable)
            {
                if(((Startable)obj).isStarted())
                {
                    //Disable Start
                    for (int i = 0; i < items.length; i++)
                    {
                        String actionCommand = ((JMenuItem)items[i]).getActionCommand();
                        if(actionCommand.equals("START"))
                        {
                            ((JMenuItem)items[i]).setEnabled(false);
                        }                        
                        else
                        {
                            ((JMenuItem)items[i]).setEnabled(true);
                        }
                    }
                }
                else
                {
                    if(((Startable)obj).isKilled())
                    {
                        //Disable Start and Stop
                        for (int i = 0; i < items.length; i++)
                        {
                            String actionCommand = ((JMenuItem)items[i]).getActionCommand();
                            if(actionCommand.equals("START") || actionCommand.equals("STOP"))
                            {
                                ((JMenuItem)items[i]).setEnabled(false);
                            }                        
                            else
                            {
                                ((JMenuItem)items[i]).setEnabled(true);
                            }
                        }                                            
                    }
                    else //Not killed and not started means it is stopped
                    {
                        //Disable Stop
                        for (int i = 0; i < items.length; i++)
                        {
                            String actionCommand = ((JMenuItem)items[i]).getActionCommand();
                            if(actionCommand.equals("STOP"))
                            {
                                ((JMenuItem)items[i]).setEnabled(false);
                            }                        
                            else
                            {
                                ((JMenuItem)items[i]).setEnabled(true);
                            }
                        }                                                                    
                    }
                }
            }
            else
            {
                //If it is not an instance of HasActivityState and is an 
                //instance of ComponentManager then Start, Stop, and Remove 
                //should be disabled otherwise it is a MinimalComponent 
                //and therefore Start, Stop, and Add should be disabled
                if(obj instanceof ComponentManager)
                {
                    //Disable Start, Stop, Remove
                    for (int i = 0; i < items.length; i++)
                    {
                        String actionCommand = ((JMenuItem)items[i]).getActionCommand();
                        if(actionCommand.equals("START") || 
                                actionCommand.equals("STOP") ||
                                actionCommand.equals("REMOVE"))
                                
                        {
                            ((JMenuItem)items[i]).setEnabled(false);
                        }                        
                        else
                        {
                            ((JMenuItem)items[i]).setEnabled(true);
                        }
                    }                       
                }
                else
                {
                    //Disable Start, Stop, Add
                    for (int i = 0; i < items.length; i++)
                    {
                        String actionCommand = ((JMenuItem)items[i]).getActionCommand();
                        if(actionCommand.equals("START") || 
                                actionCommand.equals("STOP") ||
                                actionCommand.equals("ADD"))
                                
                        {
                            ((JMenuItem)items[i]).setEnabled(false);
                        }                        
                        else
                        {
                            ((JMenuItem)items[i]).setEnabled(true);
                        }
                    }    
                }
            }        
        }
    }
    
    /**
     *  Called when various actions occur.  These actions come from Start,
     *  Stop, Add, and Remove items of the popup menu.
     *
     *  @param e  the action event
     */
    public void actionPerformed(ActionEvent e)
    {
        Object obj = ((ComponentNode)fTree.getLastSelectedPathComponent()).getUserObject();

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
                if(ComponentListController.sSelObjType != null)
                {
                    Object newChild = Irc.getDescriptorFramework()
                        .instantiateFromGlobalMap("ComponentLibraryType", 
                                ComponentListController.sSelObjType);
                    
                    if (ComponentListController.sSelObjName != null && ComponentListController.sSelObjName.length() > 0 )
					{
						try
						{
							((MinimalComponent) newChild)
									.setName(ComponentListController.sSelObjName);
						} catch (PropertyVetoException ex)
						{
							if (sLogger.isLoggable(Level.WARNING))
							{
								String message = "Unable to set base name of "
										+ ((MinimalComponent) newChild)
												.getFullyQualifiedName()
										+ " to "
										+ ComponentListController.sSelObjName;

								sLogger.logp(Level.WARNING, CLASS_NAME,
										"actionPerformed", message, ex);
							}
						}
					}

                    fTreeModel.insertNodeInto(new ComponentNode(newChild), 
                        (ComponentNode)fTree.getLastSelectedPathComponent(),0);
                }
            }
            else
            {
                System.out.println("Could not find About file:" + fAddFile);
            }        
        }
        else if(e.getActionCommand().equals("REMOVE"))
        {
            if(obj instanceof Startable)
            {
                ((Startable)obj).stop();
            }

            fTreeModel.removeNodeFromParent(
                    (ComponentNode)fTree.getLastSelectedPathComponent());        
        } 
        else if (e.getActionCommand().equals("SAVE"))
		{
			XStream xstream = new XStream();

			String xmlVersion = xstream.toXML(obj);
			System.out.println("Object:" + xmlVersion);
			MinimalComponent newObj = (MinimalComponent) xstream
					.fromXML(xmlVersion);
			Irc.getComponentManager().addComponent(newObj);

		} 
        else if (e.getActionCommand().equals("GUI"))
		{
			XStream xstream = new XStream();
			JFrame frame;
			try
			{
				frame = (JFrame)(Irc.getGuiFactory().render("resources/xml/core/gui/AboutFrame.xml"));
				//frame.show();
				String xmlVersion = xstream.toXML(frame);
				//System.out.println("Object:" + xmlVersion);
				JFrame newObj = (JFrame) xstream
						.fromXML(xmlVersion);
				newObj.show();
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

		} 
        
        // may need to reset menu options
        resetMenuOptions();
    }       
    
    /** 
     * Called whenever the value of the selection changes.
     * 
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        Object obj = null;
        
		if (fTable != null)
		{
			//---Clear current edits to make sure any user changes are complete.
			TableCellEditor editor = fTable.getCellEditor();
			
			if (editor != null)
			{
				editor.stopCellEditing();
			}
		}

        //If a node is selected in the tree then show its properties in the
        //table otherwise show the properties of the parent of the last
        //selected node
        if(fTree.getLastSelectedPathComponent() != null)
        {
            fPathToSelectedNode = fTree.getSelectionPath();
            obj = ((ComponentNode)fTree.getLastSelectedPathComponent())
                                                            .getUserObject();
            fTableModel.setObject(obj);        
        }
        else
        {
            if(fPathToSelectedNode.getParentPath() != null)
            {
                fTree.setSelectionPath(fPathToSelectedNode.getParentPath());
                obj = ((ComponentNode)fTree.getLastSelectedPathComponent())
                                                            .getUserObject();
                fTableModel.setObject(obj);                
            }
        }
        
        resetMenuOptions();
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentTreeController.java,v $
//  Revision 1.18  2006/06/02 19:18:37  smaher_cvs
//  Allow adding component without specifying name.
//
//  Revision 1.17  2006/01/23 17:59:53  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.16  2005/04/29 22:08:19  pjain_cvs
//  Fixed popupDelegate to resetMenuOptions each time popup menu is invoked.
//
//  Revision 1.15  2005/04/16 04:05:40  tames
//  *** empty log message ***
//
//  Revision 1.14  2005/03/01 19:57:11  tames_cvs
//  Removed the kill method call when removing a component.
//
//  Revision 1.13  2005/02/03 22:59:55  tames_cvs
//  Changed the behavior of remove to first stop and then
//  kill the component before removing it.
//
//  Revision 1.12  2005/01/27 21:57:52  tames
//  Moved definition of popup menu to xml description file. Fixed bug to force
//  an end of the current property edit if the tree selection changed. Added
//  toolbar button actions for sorting the properties.
//
//  Revision 1.11  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.10  2005/01/10 23:09:36  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.9  2005/01/07 20:52:54  tames
//  Updated to reflect relocation of the property related classes.
//
//  Revision 1.8  2004/12/21 20:07:03  smaher_cvs
//  Ability for ExtendedComponentTreeController to refresh current object.
//
//  Revision 1.7  2004/12/16 22:58:38  tames
//  Updated to reflect swix package restructuring.
//
//  Revision 1.6  2004/12/04 17:01:19  smaher_cvs
//  Gave some properties and methods protected access to allow subclassing.
//
//  Revision 1.5  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.4  2004/12/01 19:27:44  pjain_cvs
//  Changed the way mouse events are handled.
//
//  Revision 1.3  2004/11/19 15:12:17  pjain_cvs
//  Adding to CVS
//
//