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

import gov.nasa.gsfc.irc.components.Command;
import gov.nasa.gsfc.irc.components.RefreshableCommand;
import gov.nasa.gsfc.irc.components.Saveable;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.MenuElement;
import javax.swing.event.TreeSelectionEvent;

/**
 * Component Tree Controller that adds support for CommandCommands that
 * can execute commands and have their properties saved.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version 
 *  @author smaher
 */
public class ExtendedComponentTreeController extends ComponentTreeController
{

    private final static String RUN_ACTION = "RUN";
    
    private final static String SAVE_ACTION = "SAVE";
    private final static String RESTORE_ACTION = "RESTORE";
    private final static String REFRESH_ACTION = "REFRESH";    

    private static final String CLASS_NAME = ExtendedComponentTreeController.class.getName();
    private final Logger sLogger = Logger.getLogger(CLASS_NAME);

    /**
     * Default Constructor
     */
    public ExtendedComponentTreeController()
    {
    }
         
    /**
     * Disables options in the popup menu based on the clicked component.
     */ 
    protected void resetMenuOptions()
    {
        super.resetMenuOptions();
        if(fPopupMenu != null && !fTree.isSelectionEmpty())
        {
            Object obj = ((ComponentNode)fTree.getLastSelectedPathComponent()).getUserObject();
           
            // Enable "Run" if component is a command
            if (obj instanceof Command)
            {
                setMenuItemEnabled(RUN_ACTION, true);
            }
            else
            {
                setMenuItemEnabled(RUN_ACTION, false);
            }
            
            if (obj instanceof RefreshableCommand)
            {
                setMenuItemEnabled(REFRESH_ACTION, true);
            }
            else
            {
                setMenuItemEnabled(REFRESH_ACTION, false);
            }
            
            // Enable "Save" if component is saveable
            if (obj instanceof Saveable)
            {
                setMenuItemEnabled(SAVE_ACTION, true);
                setMenuItemEnabled(RESTORE_ACTION, true);
            }
            else
            {
                setMenuItemEnabled(SAVE_ACTION, false);
                setMenuItemEnabled(RESTORE_ACTION, false);
            }
        }
    }
    
    private void setMenuItemEnabled(String actionCommand, boolean enabled)
    {
        MenuElement[] items = fPopupMenu.getSubElements();
        for (int i = 0; i < items.length; i++)
        {
            String currentActionCommand = ((JMenuItem)items[i]).getActionCommand();
            if(currentActionCommand.equals(actionCommand))                   
            {
                ((JMenuItem)items[i]).setEnabled(enabled);
                break;
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

        if(e.getActionCommand().equals(RUN_ACTION))
        {
            if (obj instanceof Command)
            {
                ((Command)obj).execute();
            }
            else
            {
                // TODO: do something here
            }

        }
        else if(e.getActionCommand().equals(REFRESH_ACTION))
        {
            if (obj instanceof RefreshableCommand)
            {
                ((RefreshableCommand)obj).refreshParameters();
                fTableModel.setObject(obj);
            }
            else
            {
                // TODO: do something here
            }

        }        
        else if(e.getActionCommand().equals(SAVE_ACTION))
        {
            if (obj instanceof Saveable)
            {
                // If this is a folder, this action will cascade
                // to all (saveable) items in the folder.  Therefore,
                // confirm the action with the user.
                boolean doSave = true;
//                if (obj instanceof SaveableComposite && JOptionPane.showConfirmDialog(fTree,
//                        "Save properties for all components within this parent component?",
//                        "Folder Save Properties Check",
//                        JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.QUESTION_MESSAGE) !=
//                            JOptionPane.OK_OPTION)
//                {
//                    doSave = false;
//                }
                
                if (doSave)
                {
                    try
                    {
                        ((Saveable) obj).saveProperties();
                    } catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
            else
            {
                
            }
        }
        else if(e.getActionCommand().equals(RESTORE_ACTION))
        {
            if (obj instanceof Saveable)
            {
                // If this is a folder, this action will cascade
                // to all (saveable) items in the folder.  Therefore,
                // confirm the action with the user.
                boolean doSave = true;
//                if (obj instanceof SaveableComposite && JOptionPane.showConfirmDialog(fTree,
//                        "Restore defaults for all components within this parent component?",
//                        "Folder Restore Defaults Check",
//                        JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.QUESTION_MESSAGE) !=
//                            JOptionPane.OK_OPTION)
//                {
//                    doSave = false;
//                }
                
                if (doSave)
                {
                    try
                    {

                        ((Saveable) obj).restoreAndSaveDefaultProperties();

                        fTableModel.setObject(obj);

                    } catch (Exception e2)
                    {
                        JOptionPane.showMessageDialog(fTree,
                                e2.getMessage(),
                                "Exception while restoring defaults: " + e2.getMessage(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            else
            {
                
            }
        }
        else 
        {
            // Defer to processing a "standard" action
            super.actionPerformed(e);
        }
    }
    
    /* 
     * Add refreshing parameter values.  This is used to allow commands
     * to refresh their properties from, for example, a telemetry stream
     * after a new IRC session starts.
     */
    public void valueChanged(TreeSelectionEvent e)
    {
    	
        // If we want the commands to refresh automatically ...
        // (which obstructs persisted parameters)
//    	Object obj = ((ComponentNode)fTree.getLastSelectedPathComponent()).getUserObject();
//    	if (obj != null && obj instanceof RefreshableCommand)
//    	{
//    		((RefreshableCommand)obj).refreshParameters();
//    	}
    	// Set parameters before sending to super
    	super.valueChanged(e);
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ExtendedComponentTreeController.java,v $
//  Revision 1.16  2006/03/07 20:05:01  smaher_cvs
//  Moved AbstractSaveableComponent and SaveableComposite to MarkIII repository.
//
//  Revision 1.15  2006/01/11 15:45:39  smaher_cvs
//  Polished some bean property methods by changing to better name and exposing exceptions correctly.
//
//  Revision 1.14  2006/01/05 17:56:04  smaher_cvs
//  Added support for "Refresh" action
//
//  Revision 1.13  2005/05/27 15:22:55  smaher_cvs
//  Added stubbed code for automatically refreshing
//  command parameters.  Didn't activate because it
//  interferes with loading saved parameters.
//
//  Revision 1.12  2005/01/31 14:51:28  smaher_cvs
//  Removed unused pop menu string labels (they're now defined in (Extended)BrowserGui.xml
//
//  Revision 1.11  2005/01/27 21:57:52  tames
//  Moved definition of popup menu to xml description file. Fixed bug to force
//  an end of the current property edit if the tree selection changed. Added
//  toolbar button actions for sorting the properties.
//
//  Revision 1.10  2005/01/25 17:26:15  smaher_cvs
//  restoreDefaultProperties now throws PropertyVetoException; change propertyFileSuffix to instanceNumber.
//
//  Revision 1.9  2005/01/19 22:06:32  smaher_cvs
//  Changed some log messages.
//
//  Revision 1.8  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.7  2005/01/10 01:03:34  smaher_cvs
//  Changed folder dialog box to QUESTION, not ERROR.
//
//  Revision 1.6  2005/01/04 18:30:42  smaher_cvs
//  SaveableComposite->SaveableFolder
//
//  Revision 1.5  2004/12/30 21:42:31  smaher_cvs
//  CommandFolder -> SaveableComposite
//
//  Revision 1.4  2004/12/30 21:26:44  smaher_cvs
//  Added dialog boxes for saving/restoring SaveableFolders
//
//  Revision 1.3  2004/12/21 20:07:03  smaher_cvs
//  Ability for ExtendedComponentTreeController to refresh current object.
//
//  Revision 1.2  2004/12/06 20:22:30  smaher_cvs
//  Added default property restore action.
//
//  Revision 1.1  2004/12/05 14:25:06  smaher_cvs
//  Renamed to ExtendedComponentTreeController
//
//  Revision 1.1  2004/12/04 17:01:49  smaher_cvs
//  Support for commands and saveable components in the Component Browser.
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