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

package gov.nasa.gsfc.irc.gui.properties;

import gov.nasa.gsfc.irc.gui.logging.LoggerLevelModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Editor for choosing a Logger Level.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 16, 2005 3:31:53 PM
 * @author Peyush Jain
 */

public class LoggerLevelEditor extends SwingEditorSupport implements
        ActionListener
{
    /**
     * Set if this editor's descriptor contains an enumerated choice constraint.
     * Subclasses are responsible for recognizing and handling this condition.
     */
    protected boolean fIsChoice = true;

    private JComboBox fComboBox;
    private LoggerLevelModel fComboBoxModel = new LoggerLevelModel();
    private ArrayList fChoiceName = new ArrayList(5);
    private ArrayList fChoiceValue = new ArrayList(5);

    /**
     * 
     */
    public LoggerLevelEditor()
    {
        super();
        fComboBox = new JComboBox(fComboBoxModel);

        fPanel = new JPanel();
        fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
        fPanel.add(fComboBox);
        fComboBox.addActionListener(this);
    }

    /**
     * Set (or change) the object that is to be edited.
     * 
     * @param value The new target object to be edited.
     * @see java.beans.PropertyEditor#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
        // See if the combo box selection needs to be updated
        int currentSelection = fComboBox.getSelectedIndex();

        if (fComboBox.getItemAt(currentSelection) != value)
        {
            // The value does not match the current selected item in the
            // Combo Box
            fComboBox.setSelectedItem(value);
        }
        setValue0(value);
    }

    /**
     * Set the value. Since this can only be called by this class it bypasses
     * validation and updating the ComboBox since by definition they will be
     * correct.
     * 
     * @param value the value to set
     */
    private void setValue0(Object value)
    {
        super.setValue(value);
    }

    /**
     * Handle action event by setting the value of the property to the content
     * of the text field.
     * 
     * @param event the ActionEvent performed.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event)
    {
        // System.out.println("Choice actionPerformed:" +
        // fComboBox.getSelectedItem());

        int selectedIndex = fComboBox.getSelectedIndex();

        // Guard against action events caused by clearing the combo box.
        if (selectedIndex >= 0)
        {
            setValue0(fComboBox.getSelectedItem());
            // System.out.println("Choice actionPerformed value:" +
            // fChoiceValue.get(selectedIndex));
        }
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: LoggerLevelEditor.java,v $
// Revision 1.1  2005/09/01 16:22:31  pjain_cvs
// Adding to CVS.
//
// Revision 1.3 2005/01/26 19:38:13 tames
// More code cleanup and additional support for properties with choice
// constraints.
//
// Revision 1.2 2005/01/25 23:40:17 tames
// General editor cleanup and bug fixes.
//
// Revision 1.1 2005/01/20 08:08:14 tames
// Changes to support choice descriptors and message editing bug fixes.
//
//