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

package gov.nasa.gsfc.irc.gui.logging;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;

import gov.nasa.gsfc.irc.gui.controller.SwixController;
import gov.nasa.gsfc.irc.gui.properties.PropertyTableModel;

/**
 * LoggerTreeController monitors tree selection events from log viewer tree.
 * 
 * <P> This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 10, 2005 11:20:45 AM
 * @author Peyush Jain
 */

public class LoggerTreeController implements SwixController, TreeSelectionListener
{
    private static final String CLASS_NAME = LogViewer.class.getName();
    private static final Logger sLogger = Logger.getLogger("");

    private JTree fTree;
    private JTable fTable;
    
    protected PropertyTableModel fTableModel;
    private LoggerTreeModel fTreeModel;

    public static Logger sSelectedLogger;

    private JTextPane fTextPane;
    private TextPaneHandler fTextPaneHandler;
//  private JComboBox fLevelsComboBox;

    public void setView(Component component)
    {
        if (component instanceof JTextPane)
        {
            fTextPane = (JTextPane)component;
            fTextPaneHandler = new TextPaneHandler(fTextPane);
            sLogger.addHandler(fTextPaneHandler);

            for (int i = 0; i < sLogger.getHandlers().length; i++)
            {
                System.out.println(sLogger.getHandlers()[i].getClass());
            }
        }
        else if (component instanceof JTree)
        {
            if (fTree != null)
            {
                fTree.removeTreeSelectionListener(this);
            }

            fTree = (JTree)component;

            // Keep track of the current selection so we can update
            // controls and the editor.
            fTree.addTreeSelectionListener(this);

            fTreeModel = (LoggerTreeModel)fTree.getModel();
        }
        else if (component instanceof JTable)
        {
            fTable = (JTable)component;

            fTableModel = (PropertyTableModel)fTable.getModel();
        }
//        else if (component instanceof JComboBox)
//        {
//            fLevelsComboBox = (JComboBox)component;
//            fLevelsComboBox.addActionListener(this);
//        }
    }

//    /**
//     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//     */
//    public void actionPerformed(ActionEvent e)
//    {
//        Object source = e.getSource();
//
//        if (source instanceof JComboBox)
//        {
//            if (source == fLevelsComboBox && fTextPaneHandler != null)
//            {
//                sLogger.setLevel((Level)fLevelsComboBox.getSelectedItem());
//                fTextPaneHandler.setLevel((Level)fLevelsComboBox
//                                                    .getSelectedItem());
//            }
//        }
//    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        Logger logger = null;

        if (fTable != null)
        {
            // Clear current edits to make sure any user changes are
            // complete.
            TableCellEditor editor = fTable.getCellEditor();

            if (editor != null)
            {
                editor.stopCellEditing();
            }
        }

        // show the properties of selected node in the table
        if (fTree.getLastSelectedPathComponent() != null)
        {
            logger = (Logger)((LoggerNode)fTree.getLastSelectedPathComponent())
                                                                .getUserObject();
            sSelectedLogger = logger;
            IrcLogger fIrcLogger = new IrcLogger(logger);
            fTableModel.setObject(fIrcLogger);
        }
    }

}

// --- Development History ---------------------------------------------------
//
// $Log: LoggerTreeController.java,v $
// Revision 1.2  2005/09/14 21:57:55  chostetter_cvs
// Organized imports
//
// Revision 1.1  2005/09/01 19:12:38  pjain_cvs
// Adding to CVS.
//
//