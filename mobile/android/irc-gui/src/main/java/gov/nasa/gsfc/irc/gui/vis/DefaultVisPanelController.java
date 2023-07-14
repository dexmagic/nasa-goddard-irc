//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui.vis;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;

import gov.nasa.gsfc.commons.processing.activity.Pausable;
import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.GuiFactory;
import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.properties.PropertyTableModel;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;
import gov.nasa.gsfc.irc.gui.vis.editor.VisEditorTreeModel;
import gov.nasa.gsfc.irc.gui.vis.editor.VisEditorTreeNode;

/**
 * This class is the default concrete controller for visualization components.
 * Currently this only has minimal pause and resume capabilities.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/03 15:53:01 $
 * @author Troy Ames
 */
public class DefaultVisPanelController extends MouseAdapter
	implements ActionListener, SwixController
{
	private static final String CLASS_NAME = 
			DefaultVisPanelController.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Last View passed to this controller.
	private Component fLastComponent = null;
	
	// Component is used as the parent for any dialogs that are displayed.
	private JComponent fParentComponent = null;

	// Popup menus for components
	private JPopupMenu fDefaultPopupMenu = null;
	private Map fPopupMap = new HashMap();
	private GuiFactory fGuiBuilder = Irc.getGuiFactory();
	private MouseEvent fLastPopupEvent;

	/**
	 * Set the component used for displaying dialogs. Dialogs are centered on
	 * this component. By default, dialogs are centered on the screen.
	 * 
	 * @param parent the parent component for dialogs
	 */
	public void setParentComponent(JComponent parent)
	{
		fParentComponent = parent;
	}

	/**
	 * Called when menu actions are selected. 
	 * 
	 * @param e the action event
	 */
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("ActionEvent received:" + e.toString());
		String actCommand = e.getActionCommand();

		if ("Pause".equals(actCommand))
		{
			if (fLastPopupEvent != null 
					&& fLastPopupEvent.getSource() instanceof Startable)
			{
				((Pausable) fLastPopupEvent.getSource()).pause();
			}
		}
		else if ("Resume".equals(actCommand))
		{
			if (fLastPopupEvent != null 
					&& fLastPopupEvent.getSource() instanceof Startable)
			{
				((Startable) fLastPopupEvent.getSource()).start();
				((Pausable) fLastPopupEvent.getSource()).resume();
			}
		}
		else if ("Properties".equals(actCommand))
		{
			Container frame;
			try
			{
				if (fLastPopupEvent != null)
				{
					frame = fGuiBuilder.render("resources/xml/core/gui/VisPropertyEditor.xml");
	
					// Set up property table
					JTable table = (JTable) fGuiBuilder.find("Property_Table");
					((PropertyTableModel) table.getModel()).setObject(fLastPopupEvent.getSource());
					
					// Set up Visualization tree
					JTree tree = (JTree) fGuiBuilder.find("Vis_Tree");
					VisEditorTreeModel treeModel = (VisEditorTreeModel) tree.getModel();
					treeModel.setRoot(new VisEditorTreeNode(fLastPopupEvent.getSource()));
					tree.setSelectionRow(0);
					tree.expandRow(0);
					
					// Display Property frame
					//((JFrame) frame).pack();
					WindowUtil.centerFrame(frame);
					frame.setVisible(true);
				}
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	/**
	 * Set the views that this controller will monitor.
	 * 
	 * @param component the view or component to control or monitor.
	 * @see gov.nasa.gsfc.irc.gui.controller.SwixController#setView(java.awt.Component)
	 */
	public void setView(Component view)
	{
		if (view instanceof JPopupMenu)
		{
			// TODO should connecting a popup menu be done generically in the Swixml parser?
			JPopupMenu menu = (JPopupMenu) view;
			
			if (fDefaultPopupMenu == null)
			{
				fDefaultPopupMenu = menu;
			}
			
			//  We use a delegate to determine when to popup the menu.		
			//fPopupDelegate = new PopupMouseDelegate(fPopupMenu);

			if (fLastComponent != null)
			{
				System.out.println("Menu parent:" + menu.getParent());
				fPopupMap.put(fLastComponent, menu);
				// Establish new connections
				((JComponent) fLastComponent).addMouseListener(this);
			}
		}
		//if (view instanceof VisComponent)
		else if (view instanceof JPanel || view instanceof VisComponent)
		{
			if (fDefaultPopupMenu != null && view != null)
			{
				// Establish new connections
				fPopupMap.put(view, fDefaultPopupMenu);
				// Establish new connections
				view.addMouseListener(this);
			}

			if (fParentComponent == null)
			{
				setParentComponent((JComponent) view);
			}
			
			fLastComponent = view;
		}
		else if (view instanceof JTable)
		{
			if (fDefaultPopupMenu != null && view != null)
			{
				// Establish new connections
				fPopupMap.put(view, fDefaultPopupMenu);
				// Establish new connections
				view.addMouseListener(this);
			}

			if (fParentComponent == null)
			{
				setParentComponent((JComponent) view);
			}
			
			fLastComponent = view;
		}
		else if (view instanceof AbstractButton)
		{
			// Add this controller as a action listener to all buttons and menu
			// items.
			((AbstractButton) view).addActionListener(this);
		}
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
	protected void checkForPopup(MouseEvent e)
	{
		//  If needed, popup the menu at the place where the mouse
		//  was pressed.
		if (e != null && e.isPopupTrigger())
		{
			fLastPopupEvent = e;
			showPopup(e);
		}
	}

	/**
	 * Display the popup menu at the coordinates given by the mouse event.
	 * 
	 * @param e the mouse event that caused the popup
	 */
	protected void showPopup(MouseEvent e)
	{
		try
		{
			if (e != null && e.getSource() != null)
			{
				JPopupMenu menu = (JPopupMenu) fPopupMap.get(e.getSource());
				
				if (menu != null)
				{
					menu.show((Component) e.getSource(), e.getPoint().x, e
						.getPoint().y);
				}
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
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultVisPanelController.java,v $
//  Revision 1.11  2006/01/03 15:53:01  tames
//  Changed default to expand root node when property editor is created.
//
//  Revision 1.10  2005/09/22 18:50:43  tames
//  Modified pause capability.
//
//  Revision 1.9  2005/01/10 23:10:46  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.8  2005/01/07 21:35:55  tames
//  Updated to reflect relocation of property classes.
//
//  Revision 1.7  2004/12/23 14:39:39  tames
//  Added property browser support for JTable types.
//
//  Revision 1.6  2004/12/20 22:41:29  tames
//  Visualization property editing support added.
//
//  Revision 1.5  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.4  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.3  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/11/30 21:11:14  tames_cvs
//  Removed unnecessary if statement condition.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//
