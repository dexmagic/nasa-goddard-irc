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
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import gov.nasa.gsfc.irc.gui.properties.SwingEditorSupport;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * Column model for the Message editor table. This class also bridges 
 * the CellEditor interface which a table requires for a PropertyEditor.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/27 22:00:46 $
 * @author 	Troy Ames
 */
public class MessageColumnModel extends DefaultTableColumnModel
{
	private final static String COL_LABEL_NAME = "Argument";
	private final static String COL_LABEL_VALUE = "Value";
	private final static String COL_LABEL_UNITS = "Units";

	private static final int fMinColWidth = 150;

	/**
	 * Default constructor adds three columns to the table for argument,
	 * value, and units. This class also sets up the cell editors and renderers.
	 */
	public MessageColumnModel()
	{
		// Configure the columns and add them to the model
		TableColumn column;

		// Argument name
		column = new TableColumn(0);
		column.setHeaderValue(COL_LABEL_NAME);
		column.setPreferredWidth(fMinColWidth);
		column.setCellRenderer(new PropertyNameRenderer());
		addColumn(column);

		// Argument value
		column = new TableColumn(1);
		column.setHeaderValue(COL_LABEL_VALUE);
		column.setPreferredWidth(fMinColWidth * 2);
		column.setCellEditor(new PropertyValueEditor());
		column.setCellRenderer(new PropertyValueRenderer());
		addColumn(column);

		// Units
		column = new TableColumn(2);
		column.setHeaderValue(COL_LABEL_UNITS);
		column.setPreferredWidth(fMinColWidth / 2);
		column.setCellRenderer(new PropertyUnitRenderer());
		addColumn(column);
	}

	/**
	 * Renders the name of the Argument. Sets the short description of the
	 * argument as the tooltip text.
	 */
	protected class PropertyNameRenderer extends DefaultTableCellRenderer
	{
		/**
		 * Get UI for current name renderer.
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			MessageTableModel model = (MessageTableModel) table.getModel();
			FieldDescriptor desc = model.getFieldDescriptor(row);

			setToolTipText(desc.getShortDescription());
			setBackground(UIManager.getColor("Button.background"));
			setFocusable(false);

			return super.getTableCellRendererComponent(table, value,
				isSelected, false, row, column);
		}
	}

	/**
	 * Renders the units of the argument.
	 */
	protected class PropertyUnitRenderer extends DefaultTableCellRenderer
	{
		/**
		 * Get UI for current unit renderer.
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			MessageTableModel model = (MessageTableModel) table.getModel();
			FieldDescriptor desc = model.getFieldDescriptor(row);

			setBackground(UIManager.getColor("Button.background"));
			setFocusable(false);

			return super.getTableCellRendererComponent(table, value,
				isSelected, false, row, column);
		}
	}

	/**
	 * Renderer for a value with a property editor or installs the default cell
	 * rendererer.
	 */
	protected class PropertyValueRenderer implements TableCellRenderer
	{
		private DefaultTableCellRenderer renderer;
		private PropertyEditor editor;
		private Hashtable editors;
		private Class type;
		private Border selectedBorder;
		private Border emptyBorder;

		/**
		 * Construct a new PropertyValueRenderer.
		 */
		public PropertyValueRenderer()
		{
			renderer = new DefaultTableCellRenderer();
			editors = new Hashtable();
		}

		/**
		 * Get UI for current editor.
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			MessageTableModel model = (MessageTableModel) table.getModel();
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
					FieldDescriptor descriptor = model.getFieldDescriptor(row);
					((SwingEditorSupport) editor).setDescriptor(descriptor);
				}

				editor.setValue(value);

				component = editor.getCustomEditor();

				if (component != null)
				{
					component.setEnabled(isSelected);

					//System.out.println("getTableCellRendererComponent custom:" + comp);
					if (component instanceof JComponent)
					{
						if (isSelected)
						{
							if (selectedBorder == null)
							{
								selectedBorder = BorderFactory
										.createLineBorder(table
												.getSelectionBackground(), 1);
							}

							((JComponent) component).setBorder(selectedBorder);
						}
						else
						{
							if (emptyBorder == null)
							{
								emptyBorder = 
									BorderFactory.createEmptyBorder(1, 1, 1, 1);
							}

							((JComponent) component).setBorder(emptyBorder);
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
	protected class PropertyValueEditor extends AbstractCellEditor implements
			TableCellEditor, PropertyChangeListener
	{
		private PropertyEditor editor;
		private DefaultCellEditor cellEditor;
		private Class type;
		private Border selectedBorder;
		private Border emptyBorder;
		private Hashtable editors;
		private boolean fActive = false;

		/**
		 * Construct a new PropertyValueEditor.
		 */
		public PropertyValueEditor()
		{
			editors = new Hashtable();
			cellEditor = new DefaultCellEditor(new JTextField());
		}

	    /**
		 * Get UI for current editor, including custom editor if applicable.
		 */
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column)
		{
			MessageTableModel model = (MessageTableModel) table.getModel();
			type = model.getPropertyType(row);
			Component component = null;

			if (type != null)
			{
				editor = (PropertyEditor) editors.get(type);
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
							editor = (PropertyEditor) editorClass.newInstance();
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
				// Special case for properties with field descriptors.
				if (editor instanceof SwingEditorSupport)
				{
					((SwingEditorSupport) editor).setDescriptor(model
							.getFieldDescriptor(row));
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
								selectedBorder = BorderFactory
										.createLineBorder(table
												.getSelectionBackground(), 1);

							((JComponent) component).setBorder(selectedBorder);
						}
						else
						{
							if (emptyBorder == null)
							{
								emptyBorder = BorderFactory.createEmptyBorder(
									1, 1, 1, 1);
							}

							((JComponent) component).setBorder(emptyBorder);
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
//  $Log: MessageColumnModel.java,v $
//  Revision 1.4  2005/01/27 22:00:46  tames
//  Misc coding style changes and removal of obsolete code.
//
//  Revision 1.3  2005/01/25 23:39:29  tames
//  Removed debug print statements.
//
//  Revision 1.2  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//
