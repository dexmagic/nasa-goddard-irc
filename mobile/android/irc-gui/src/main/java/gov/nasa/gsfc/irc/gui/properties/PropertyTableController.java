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

package gov.nasa.gsfc.irc.gui.properties;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import gov.nasa.gsfc.irc.gui.controller.SwixController;


/**
 *  Property Table Controller monitors action events from filter options
 *  combox and tablemodel events from properties table.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/27 22:04:06 $
 * @author Peyush Jain
 */

public class PropertyTableController implements SwixController, 
                                        ActionListener, TableModelListener
{
    private JTable fTable;
    private PropertyTableModel fTableModel;
    private JComboBox fFilterOptions;
	private Object fFilterSelected;
    
	public static final String SORT_ALPHA_ACTION = "SORT_ALPHA";
	public static final String SORT_TYPE_ACTION = "SORT_TYPE";
	public static final String SORT_NONE_ACTION = "SORT_NONE";
    
    /** 
     * Default Constructor
     */
    public PropertyTableController()
    {
        
    }
    
    /**
     * Set the views that this controller will monitor.
     * 
     * @param comp the view or component to control or monitor.
     * @see gov.nasa.gsfc.irc.gui.controller.SwixController#setView(java.awt.Component)
     */ 
    public void setView(Component component)
    {
        if(component instanceof JTable)
        {
            fTable = (JTable)component;
            fTableModel = (PropertyTableModel)fTable.getModel();
            fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }
		else if (component instanceof JComboBox)
		{
			fFilterOptions = (JComboBox) component;
			fFilterSelected = fFilterOptions.getSelectedItem();
			fFilterOptions.addActionListener(this);
			
			if (fFilterSelected != null && fTableModel != null)
			{
				fTableModel.setFilter(fFilterSelected);
			}
		}
		else if (component instanceof JToggleButton)
		{
			((JToggleButton)component).addActionListener(this);
		}
    }

    /**
     *  Called when combobox actions occur.  
     *
     *  @param e  the action event
     */
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();

        if (source instanceof JComboBox)
        {
            if (source == fFilterOptions && fTableModel != null)
            {
            	fFilterSelected = fFilterOptions.getSelectedItem();
            	fTableModel.setFilter(fFilterSelected);
            }
        }
        else if (source instanceof JToggleButton)
        {
            if (fTableModel != null)
            {
        		String actCommand = evt.getActionCommand();

        		if (SORT_ALPHA_ACTION.equals(actCommand))
        		{
        			fTableModel.setSortOrder(1);
        		}
        		else if (SORT_TYPE_ACTION.equals(actCommand))
        		{
        			fTableModel.setSortOrder(2);
        		}
        		else if (SORT_NONE_ACTION.equals(actCommand))
        		{
        			fTableModel.setSortOrder(0);
        		}
            }
        }
   }
    
    /**
     *  This fine grain notification tells listeners the exact range
     *  of cells, rows, or columns that changed.
     *
     *  @param e  the action event
     */
    public void tableChanged(TableModelEvent evt)
    {
        // Adjust the preferred height of the row to the the same as
        // the property editor.
        // This seems to be necessary or the # rows doesn't change
        //fTable.setRowHeight(ROW_HEIGHT);

        PropertyEditor editor;
        Component comp;
        Dimension prefSize;

        for (int i = 0; i < fTable.getRowCount(); i++)
        {
            editor = fTableModel.getPropertyEditor(i);
            if (editor != null)
            {
                comp = editor.getCustomEditor();
                if (comp != null)
                {
                    prefSize = comp.getPreferredSize();
                    if (prefSize.height != fTable.getRowHeight(i))
                    {
                        fTable.setRowHeight(i, prefSize.height);
                    }
                }
            }
        }
    }    

}


//--- Development History  ---------------------------------------------------
//
//  $Log: PropertyTableController.java,v $
//  Revision 1.2  2005/01/27 22:04:06  tames
//  Fixed numerous bugs including: correct row size for specific property
//  editor, correct ending of edit session when editor focus changes.
//
//  Revision 1.1  2005/01/07 21:01:09  tames
//  Relocated.
//
//  Revision 1.2  2004/12/16 22:58:39  tames
//  Updated to reflect swix package restructuring.
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//