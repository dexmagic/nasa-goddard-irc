//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//  This class is a modified version of code originally developed by 
//  Sun Microsystems. The copyright notice for the original code is given at 
//  the end of the file.
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


/**
 * Column model for the PropertyTable
 * @version 1.6 02/27/02
 * @author Mark Davidson
 */
public class PropertyColumnModel extends DefaultTableColumnModel
{
    private final static String COL_LABEL_PROP = "Property";
    private final static String COL_LABEL_VALUE = "Value";

    private static final int fMinColWidth = 150;

    public PropertyColumnModel()
    {
        // Configure the columns and add them to the model
        TableColumn column;

        // Property
        column = new TableColumn(0);
        column.setHeaderValue(COL_LABEL_PROP);
        column.setPreferredWidth(fMinColWidth);
        column.setCellRenderer(new PropertyNameRenderer());
        addColumn(column);

        // Value
        column = new TableColumn(1);
        column.setHeaderValue(COL_LABEL_VALUE);
        column.setPreferredWidth(fMinColWidth * 2);
        column.setCellEditor(new PropertyValueEditor());
        column.setCellRenderer(new PropertyValueRenderer());
        addColumn(column);
    }

    /**
     * Renders the name of the property. Sets the short description of the
     * property as the tooltip text.
     */
    class PropertyNameRenderer extends DefaultTableCellRenderer
    {
        /**
         * Get UI for current editor, including custom editor button if
         * applicable.
         */
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column)
        {
            PropertyTableModel model = (PropertyTableModel)table.getModel();
            PropertyDescriptor desc = model.getPropertyDescriptor(row);

            setToolTipText(desc.getShortDescription());
            setBackground(UIManager.getColor("Button.background"));
			setFocusable(false);

            return super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
        }
    }

    /**
     * Renderer for a value with a property editor or installs the default cell
     * rendererer.
     */
    class PropertyValueRenderer implements TableCellRenderer
    {
        private DefaultTableCellRenderer renderer;
        private PropertyEditor editor;

        private Hashtable editors;
        private Class type;

        private Border selectedBorder;
        private Border emptyBorder;

        public PropertyValueRenderer()
        {
            renderer = new DefaultTableCellRenderer();
            editors = new Hashtable();
        }

        /**
         * Get UI for current editor, including custom editor button if
         * applicable. XXX - yuck! yuck! yuck!!!!
         */
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column)
        {
            PropertyTableModel model = (PropertyTableModel)table.getModel();
            type = model.getPropertyType(row);
			Component component = null;
            
            if (type != null)
            {
                editor = (PropertyEditor) editors.get(type);
                
                if (editor == null)
                {
                    editor = model.getPropertyEditor(row);

                    if (editor != null)
                    {
                        editors.put(type, editor);
                    }
                }
            }
            else
            {
                editor = null;
            }

            if (editor != null)
            {
                // Special case for the enumerated properties. Must reinitialize
                // to reset the combo box values.
                if (editor instanceof SwingEditorSupport)
                {
                    ((SwingEditorSupport) editor).init(
                    	model.getPropertyDescriptor(row));
                }

                editor.setValue(value);

                component = editor.getCustomEditor();
                
                if (component != null)
                {
                    component.setEnabled(isSelected);

                    if (component instanceof JComponent)
                    {
                        if (isSelected)
                        {
                            if (selectedBorder == null)
                                selectedBorder = BorderFactory.createLineBorder(
                                	table.getSelectionBackground(), 1);

                            ((JComponent)component).setBorder(selectedBorder);
                        }
                        else
                        {
                            if (emptyBorder == null)
                            {
                                emptyBorder = 
                                	BorderFactory.createEmptyBorder(1, 1, 1, 1);
                            }
                            
                            ((JComponent)component).setBorder(emptyBorder);
                        }
                    }
                    
					// Reset the row height for the component
					Dimension prefSize = component.getPreferredSize();

					if (prefSize.height != table.getRowHeight(row))
					{
						table.setRowHeight(row, prefSize.height);
					}
                }
            }
            
			if (component == null)
			{
				// Otherwise use default renderer
				component = renderer.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			}

			return component;
        }

        /**
         * Retrieves the property editor for this value.
         */
        public PropertyEditor getPropertyEditor()
        {
            return editor;
        }
    }
    
	/**
	 * An editor for types which have a property editor. This class bridges 
	 * the CellEditor interface which the table requires for a PropertyEditor.
	 * Installs a default cell editor if a property editor is not defined.
	 */
    class PropertyValueEditor extends AbstractCellEditor implements
            TableCellEditor, PropertyChangeListener
    {
        private PropertyEditor editor;
        private DefaultCellEditor cellEditor;
        private Class type;

        private Border selectedBorder;
        private Border emptyBorder;

        private Hashtable editors;
        private boolean fActive = false;

        public PropertyValueEditor()
        {
            editors = new Hashtable();
            cellEditor = new DefaultCellEditor(new JTextField());
        }

        /**
         * Get UI for current editor, including custom editor button if
         * applicable.
         */
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column)
        {
            PropertyTableModel model = (PropertyTableModel)table.getModel();
            type = model.getPropertyType(row);
			Component component = null;

            if (type != null)
            {
                editor = (PropertyEditor)editors.get(type);
                if (editor == null)
                {
                    PropertyEditor ed = model.getPropertyEditor(row);

                    // Make a copy of this prop editor and register this as a
                    // prop change listener.
                    // We have to do this since we want a unique PropertyEditor
                    // instance to be used for an editor vs. a renderer.
                    if (ed != null)
                    {
                        Class editorClass = ed.getClass();
                        try
                        {
                            editor = (PropertyEditor)editorClass.newInstance();
                            editor.addPropertyChangeListener(this);
                            editors.put(type, editor);

                        }
                        catch (Exception ex)
                        {
                            System.out.println("Couldn't instantiate type editor \""
                                            + editorClass.getName()
                                            + "\" : "
                                            + ex);
                        }
                    }
                }
            }
            else
            {
                editor = null;
            }

            if (editor != null)
            {
                // Special case for the enumerated properties. Must reinitialize
                // to reset the combo box values.
                if (editor instanceof SwingEditorSupport)
                {
                    ((SwingEditorSupport) editor).init(
                    	model.getPropertyDescriptor(row));
                }

                editor.setValue(value);
                component = editor.getCustomEditor();
                
                if (component != null)
                {
                    component.setEnabled(isSelected);

                    if (component instanceof JComponent)
                    {
                        if (isSelected)
                        {
                            if (selectedBorder == null)
                                selectedBorder = BorderFactory.createLineBorder(table.getSelectionBackground(), 1);

                            ((JComponent)component).setBorder(selectedBorder);
                        }
                        else
                        {
                            if (emptyBorder == null)
                            {
                                emptyBorder = 
                                	BorderFactory.createEmptyBorder(1, 1, 1, 1);
                            }

                            ((JComponent)component).setBorder(emptyBorder);
                        }
                    }

					// Reset the row height for the component
					Dimension prefSize = component.getPreferredSize();

					if (prefSize.height != table.getRowHeight(row))
					{
						table.setRowHeight(row, prefSize.height);
					}
                }
            }

			if (component == null)
			{
				// Otherwise use default editor
				component = cellEditor.getTableCellEditorComponent(table,
					value, isSelected, row, column);
			}

			return component;
        }


		/**
		 * Stop cell editing. Passes stop request to editor delegate.
		 */
		public boolean stopCellEditing()
		{
			boolean result = true;

			if (editor != null)
			{
				if (editor instanceof SwingEditorSupport)
				{
					result = ((SwingEditorSupport) editor).stopEditing();
				}
			}
			else if (cellEditor != null)
			{
				result = cellEditor.stopCellEditing();
			}

			if (result)
			{
				// Let super class fire appropriate event.
				super.stopCellEditing();
			}

			return result;
		}

        /**
         * Get cellEditorValue for current editor
         */
        public Object getCellEditorValue()
        {
            Object obj = null;

            if (editor != null)
            {
                obj = editor.getValue();
            }
            else
            {
                obj = cellEditor.getCellEditorValue();
            }

            if (type != null && obj != null && !type.isPrimitive()
                    && !type.isAssignableFrom(obj.getClass()))
            {
                // XXX - debug
                System.out.println("Type mismatch in getCellEditorValue() = "
                        + obj.getClass() + " type = " + type);

                try
                {
                    obj = type.newInstance();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            return obj;
        }

        //
        // Property Change handler.
        // 

        public void propertyChange(PropertyChangeEvent evt)
        {
			super.stopCellEditing();
        }
    }    
}

//--- Development History  ---------------------------------------------------
//
//  $Log: PropertyColumnModel.java,v $
//  Revision 1.2  2005/01/27 22:04:06  tames
//  Fixed numerous bugs including: correct row size for specific property
//  editor, correct ending of edit session when editor focus changes.
//
//  Revision 1.1  2005/01/07 21:01:09  tames
//  Relocated.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 * 
 * @author  Mark Davidson
 */
