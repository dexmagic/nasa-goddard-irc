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

package gov.nasa.gsfc.irc.gui.messages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.swing.PopupMouseDelegate;
import gov.nasa.gsfc.irc.messages.InvalidMessageException;
import gov.nasa.gsfc.irc.messages.MessageFactory;
import gov.nasa.gsfc.irc.messages.MessageKeys;
import gov.nasa.gsfc.irc.messages.Messages;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;
import gov.nasa.gsfc.irc.scripts.Script;
import gov.nasa.gsfc.irc.scripts.ScriptException;
import gov.nasa.gsfc.irc.scripts.ScriptFactory;
import gov.nasa.gsfc.irc.scripts.Scripts;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;

/**
 *  This class is the controller for both the message catalog and message
 *  customizer portions of the GUI and, in doing so, monitors much of the 
 *  interaction between the pieces of the messaging GUI.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/04/18 04:21:55 $
 *  @author		John Higinbotham (Emergent Space Technologies)
 *  @author		Troy Ames
 */
public class MessagePanelController 
	implements TreeSelectionListener, ActionListener, SwixController
{
	private static final String CLASS_NAME = 
		MessagePanelController.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String RUN_ACTION = "RUN_ACTION";
	public static final String SAVE_ACTION = "SAVE";
	public static final String RESTORE_ACTION = "RESTORE";
	public static final String RESTORE_DEFAULT_ACTION = "RESTORE_DEFAULTS";
	
	//---Error strings for command execution problems.
	private static final String RUN_ERROR_TITLE = "Error";
	private static final String[] RUN_ERROR_MSG = {
		"Error attempting to execute the selected command.",
		"The given arguments are invalid or the command timed out."};

	//---Component is used as the parent for any dialogs that are displayed.
	private JComponent fParentComponent = null;
	
	private Object fCurrentObject = null;

	private JTree fTreeView = null;
	private MessageEditorView fCustomizerView = null;
	private JPopupMenu fPopupMenu = null;
	private JMenuItem fRunMenuItem = null;
	private JButton fRunButton = null;
	private MessageFactory fMessageFactory = Irc.getMessageFactory();
	private ScriptFactory fScriptFactory = Irc.getScriptFactory();
	private PopupMouseDelegate fPopupDelegate;
	
	// Local cache
	private Map fLocalScriptCache = new HashMap();
	private Map fLocalMessageCache = new HashMap();

	/**
	 *  Set the component used for displaying dialogs.  Dialogs
	 *  are centered on this component.  By default, dialogs are
	 *  centered on the screen.
	 *
	 *  @param parent  the parent component for dialogs
	 */
	public void setParentComponent(JComponent parent)
	{
		fParentComponent = parent;
	}

	/**
	 *  Called when various actions occur.
	 *
	 *  @param action  the action event
	 */
	public void actionPerformed(ActionEvent action)
	{
		String actCommand = action.getActionCommand();

		if (RUN_ACTION.equals(actCommand))
		{
			runSelection();
		}
		else if (SAVE_ACTION.equals(actCommand))
		{
			stopCurrentEdit();
			
			if (fCurrentObject instanceof Message)
			{
				Message msgClone = 
					fMessageFactory.createMessage((Message) fCurrentObject);
				fMessageFactory.storeMessage(msgClone);
			}
			else if (fCurrentObject instanceof Script)
			{
				Script scriptClone = 
					fScriptFactory.createScript((Script) fCurrentObject);
				fScriptFactory.storeScript(scriptClone);
			}
		}
		else if (RESTORE_ACTION.equals(actCommand))
		{
			Object restoredObject = null;
			stopCurrentEdit();

			if (fCurrentObject instanceof Message)
			{
				restoredObject = 
					fMessageFactory.retrieveMessage(
						(MessageDescriptor)((Message) fCurrentObject).getProperty(
						MessageKeys.DESCRIPTOR));
			}
			else if (fCurrentObject instanceof Script)
			{
				restoredObject = fScriptFactory.retrieveScript(
					(ScriptDescriptor)((Script) fCurrentObject).getDescriptor());
			}
			
			if (fCustomizerView != null && restoredObject != null)
			{
				fCurrentObject = restoredObject;
				fCustomizerView.setObject(restoredObject);
			}
		}
		else if (RESTORE_DEFAULT_ACTION.equals(actCommand))
		{
			stopCurrentEdit();

			if (fCurrentObject instanceof Message)
			{
				fCurrentObject = fMessageFactory.createMessage(
					(MessageDescriptor)((Message) fCurrentObject).getProperty(
						MessageKeys.DESCRIPTOR));
			}
			else if (fCurrentObject instanceof Script)
			{
				fCurrentObject = fScriptFactory.createScript(
					(ScriptDescriptor)((Script) fCurrentObject).getDescriptor());
			}
			
			if (fCustomizerView != null)
			{
				fCustomizerView.setObject(fCurrentObject);
			}
		}
	}

	/**
	 * Run the currently selected script or command.
	**/
	protected void runSelection()
	{
		try
		{
			//  Clear any current edits to make sure any user changes are complete.
			if (fCustomizerView != null)
			{
				stopCurrentEdit();
			}

			if (fCurrentObject != null)
			{
				//---Found script
				if (fCurrentObject instanceof Script)
				{
					Script scriptClone = 
						fScriptFactory.createScript((Script) fCurrentObject);
					Scripts.validateScript((Script) scriptClone);
					Scripts.callScript((Script) scriptClone);
				}
				//---Found message
				else if (fCurrentObject instanceof Message)
				{
					Message msgClone = 
						fMessageFactory.createMessage((Message) fCurrentObject);
					Messages.validateMessage((Message) msgClone);
					Messages.publishMessage((Message) msgClone);
				}
				//---Found something unexpected
				else
				{
					String message = "Unknown Object type: "
						+ fCurrentObject;
					
					sLogger.logp(
							Level.WARNING, CLASS_NAME, "runSelection", 
							message);
				}
			}
		}
		catch (InvalidMessageException e)
		{
			String message = "Exception running message: "
				+ ((Message) fCurrentObject).getName();
			
			sLogger.logp(
					Level.WARNING, CLASS_NAME, "runSelection", 
					message, e);

			JOptionPane.showMessageDialog(
					fParentComponent, RUN_ERROR_MSG,
					RUN_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
		}
		catch (ScriptException e)
		{
			String message = "Exception running script: "
				+ ((Script) fCurrentObject).getName();
			
			sLogger.logp(
					Level.WARNING, CLASS_NAME, "runSelection", 
					message, e);
		}
	}
	
	/**
	 *  Tell the customizer to stop the current editing session.
	 */
	protected void stopCurrentEdit()
	{
		if (fCustomizerView != null)
		{
			fCustomizerView.stopCurrentEdit();

			Object editedObject = fCustomizerView.getObject();
			
			if (editedObject instanceof Message)
			{
				fLocalMessageCache.put(
					((Message) editedObject).getFullyQualifiedName(),
					editedObject);
			}
			else if (editedObject instanceof Script)
			{
				fLocalScriptCache.put(
					((Script) editedObject).getFullyQualifiedName(),
					editedObject);
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
			JPopupMenu menu = (JPopupMenu) view;
			
			if (fPopupMenu == null)
			{
				fPopupMenu = menu;
				//  We use a delegate to determine when to popup the menu.		
				fPopupDelegate = new PopupMouseDelegate(fPopupMenu);
			}			

			if (fPopupDelegate != null && fTreeView != null)
			{
				// Establish new connections
				((JComponent) fTreeView).addMouseListener(fPopupDelegate);
			}
		}
		else if (view instanceof JTree)
		{
			if (fTreeView != null)
			{
				fTreeView.removeTreeSelectionListener(this);
			}
			
			fTreeView = (JTree) view;

			if (fPopupDelegate != null)
			{
				//  We use a delegate to determine when to popup the menu.		
				fTreeView.addMouseListener(fPopupDelegate);
			}

			//  Keep track of the current selection so we can update
			//  controls and the editor.
			fTreeView.addTreeSelectionListener(this);
		}
		else if (view instanceof MessageEditorView)
		{
			fCustomizerView = (MessageEditorView) view;
			setParentComponent(fCustomizerView);
		}
		else if (view instanceof JButton)
		{
			if (RUN_ACTION.equals(((JButton) view).getActionCommand()))
			{
				fRunButton = (JButton) view;
			}
			
			((JButton) view).addActionListener(this);
		}
		else if (view instanceof JMenuItem)
		{
			if (RUN_ACTION.equals(((JMenuItem) view).getActionCommand()))
			{
				fRunMenuItem = (JMenuItem) view;
			}
			
			((JMenuItem) view).addActionListener(this);
		}
	}

	/**
	 *  Signals that the selection has changed.  Note that
	 *  this may mean that nothing is selected.
	 *
	 *  @param e  the selection event
	 */
	public void valueChanged(TreeSelectionEvent event)
	{
		if (event == null)
		{
			return;
		}
		
		if (fCustomizerView != null)
		{
			//---Clear current edits to make sure any user changes are complete.
			stopCurrentEdit();
		}

		DescriptorNode descNode = 
			DescriptorNode.getNodeFromPath(event.getNewLeadSelectionPath());
		
		//---Enable/disable command button based on the selected item.
		if (descNode != null)
		{
			Descriptor descriptor = descNode.getDescriptor();
			
			//---Found script
			if (descriptor != null 
				&& descriptor instanceof ScriptDescriptor)
			{
				//fEditCommandItem.setEnabled(true);
				fRunMenuItem.setEnabled(true);
				fRunButton.setEnabled(true);

				// Check local cache first
				Script script = 
					(Script) fLocalScriptCache.get(descriptor.getFullyQualifiedName());
				
				// Get cached script from factory if not found locally
				if (script == null)
				{
					script = fScriptFactory.retrieveScript(
						(ScriptDescriptor) descriptor);
				}
				
				// Create new one if not found yet
				if (script == null)
				{
					//System.out.println("script not in cache");
					// Create a new script
					script = 
						fScriptFactory.createScript((ScriptDescriptor) descriptor);
				}

				fCurrentObject = script;
			}
			//---Found message
			else if (descriptor != null 
					&& descriptor instanceof MessageDescriptor)
			{
				//fEditCommandItem.setEnabled(cmdDesc instanceof ScriptDescriptor);
				fRunMenuItem.setEnabled(true);
				fRunButton.setEnabled(true);
				
				//---Tell the table model and other components listening that the
				//   selection has changed.  
				// Check local cache first
				Message msg = 
					(Message) fLocalMessageCache.get(
						descriptor.getFullyQualifiedName());
				
				//System.out.println("descriptor.getFullyQualifiedName():" + descriptor.getFullyQualifiedName());
				// Get cached message from factory if not found locally
				if (msg == null)
				{
					msg = fMessageFactory.retrieveMessage(
						(MessageDescriptor) descriptor);
				}
				
				// Create new one if not found yet
				if (msg == null)
				{
					//System.out.println("Message not in cache");
					// Create a new message
					msg = fMessageFactory.createMessage(
							(MessageDescriptor) descriptor);
				}

				fCurrentObject = msg;
			}
			//---Found something unexpected
			else
			{
				//fEditCommandItem.setEnabled(false);
				fRunMenuItem.setEnabled(false);
				fRunButton.setEnabled(false);
				fCurrentObject = null;
			}
		}
		else
		{
			//fEditCommandItem.setEnabled(false);
			fRunMenuItem.setEnabled(false);
			fRunButton.setEnabled(false);
			fCurrentObject = null;
		}
		
		// Notify the editor of the change
		if (fCustomizerView != null && fCurrentObject != null)
		{
			fCustomizerView.setObject(fCurrentObject);
		}
	}

}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessagePanelController.java,v $
//  Revision 1.7  2006/04/18 04:21:55  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.6  2006/02/07 21:55:55  tames
//  Modified Controller to run or send a clone of the current script or message.
//
//  Revision 1.5  2005/04/12 15:36:40  tames_cvs
//  Updated catch clause for ScriptExceptions.
//
//  Revision 1.4  2005/02/01 18:48:49  tames
//  Added actions for handling saving and restoring arguments.
//
//  Revision 1.3  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//  Revision 1.2  2005/01/10 23:10:25  tames_cvs
//  Clean up of message and script error handling.
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//  Revision 1.6  2004/12/16 22:58:57  tames
//  Updated to reflect swix package restructuring.
//
//  Revision 1.5  2004/10/15 15:51:00  tames_cvs
//  Fixed the check for the run button in setView.
//
//  Revision 1.4  2004/10/14 21:07:01  tames_cvs
//  Reverted to previous version since the check for the run button did not work.
//
//  Revision 1.3  2004/10/14 18:43:36  tames_cvs
//  Added check for run command button by action name.
//
//  Revision 1.2  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/22 18:10:47  tames_cvs
//  Renamed class.
//
//  Revision 1.4  2004/09/21 21:19:23  tames_cvs
//  Changes to use the MessageCatalogView class.
//
//  Revision 1.3  2004/09/21 15:10:28  tames
//  Added functionality to support a destination path for messages.
//
//  Revision 1.2  2004/09/20 21:11:16  tames
//  Refactoring of classes to get layout in XML, control in controllers,
//  and simplified views.
//
//  Revision 1.1  2004/09/16 21:10:38  jhiginbotham_cvs
//  Port from IRC v5.
//
