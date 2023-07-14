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
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.HasDescriptor;
import gov.nasa.gsfc.irc.gui.browser.BeanInfoFactory;
import gov.nasa.gsfc.irc.messages.MessageKeys;
import gov.nasa.gsfc.irc.messages.description.AbstractMessageDescriptor;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.scripts.Script;

/**
 * Table model used to obtain property names and values. This model encapsulates 
 * an array of FieldDescriptorss.
 *
 * @version	$Date: 2006/04/18 04:21:55 $
 */
public class MessageTableModel extends AbstractTableModel
{
    private FieldDescriptor[] fFieldDescriptors;
    private BeanDescriptor fBeanDescriptor;
    private Descriptor fMessageDescriptor;
    private BeanInfo fBeanInfo;
    private Object fBean;

    // Cached property editors.
    private static Hashtable fPropertyEditors;

    // Shared instance of a comparator
    private static Comparator fComparator = new FieldDescriptorComparator();

    private static final int NUM_COLUMNS = 3;

    public static final int COL_NAME = 0;
    public static final int COL_VALUE = 1;
    public static final int COL_UNITS = 2;

    private Object fCurrentFilter = ArgumentFilterModel.STANDARD;

    // Sort options
    public static final int SORT_OFF = 0;
    public static final int SORT_ALPHA = 1;

    private int fSortOrder = SORT_OFF;

    /**
     * Default constructor for a MessageTableModel. The target Object can be 
     * set with the <code>setObject</code> method.
     */
    public MessageTableModel()
    {
        if (fPropertyEditors == null)
        {
            fPropertyEditors = new Hashtable();
        }
    }

    /**
     * Construct a model with the specified target Object or bean.
     * 
     * @param bean the target bean.
     */
    public MessageTableModel(Object bean)
    {
        this();
        setObject(bean);
    }

    /**
     * Sets the current filter of the arguments. If the filter has 
     * changed this method will call <code>resetTableRows</code>.
     * 
     * @param filter the new filter
     */
    public void setFilter(Object filter)
    {
    	if (!fCurrentFilter.equals(filter))
    	{
            fCurrentFilter = filter;
    		resetTableRows();
		}
    }

    /**
     * Returns the current filter type
     * 
     * @return the current filter type
     */
    public Object getFilter()
    {
        return fCurrentFilter;
    }

    /**
     * Sets the current sort order for the arguments. If the sort order has 
     * changed this method will call <code>resetTableRows</code>.
     * 
     * @param sort one of the SORT_ constants
     */
    public void setSortOrder(int sort)
    {
    	if (sort != fSortOrder)
    	{
    		fSortOrder = sort;
    		resetTableRows();
		}
    }

    /**
     * Gets the sort order for this table.
     * 
     * @return the sort order.
     */
    public int getSortOrder()
    {
        return fSortOrder;
    }

    /**
     * Set the target Object.
     * 
     * @param bean the target Object
     */
    public void setObject(Object bean)
    {
        fBean = bean;

        fBeanInfo = BeanInfoFactory.getBeanInfo(bean.getClass());
        
        if (bean instanceof HasDescriptor)
        {
        	fMessageDescriptor = ((HasDescriptor) bean).getDescriptor();
        }
        else if (bean instanceof Message)
        {
        	fMessageDescriptor = 
        		(Descriptor) ((Message) bean).getProperty(MessageKeys.DESCRIPTOR);
        }

        if (fBeanInfo != null)
        {
            fBeanDescriptor = fBeanInfo.getBeanDescriptor();
        }
        
        resetTableRows();
    }

    /**
     * Return the current object that is represented by this model.
     * 
     * @return the current Object
     */
    public Object getObject()
    {
        return fBean;
    }

    /**
     * Get row count (total number of properties shown)
     * 
     * @return the row count
     */
    public int getRowCount()
    {
    	int count = 0;
    	
        if (fFieldDescriptors != null)
        {
            count = fFieldDescriptors.length;
        }
        
        return count;
    }

    /**
     * Get column count.
     * 
     * @return the current column count
     */
    public int getColumnCount()
    {
        return NUM_COLUMNS;
    }

    /**
     * Check if given cell is editable.
     * 
     * @param row table row
     * @param col table column
     */
    public boolean isCellEditable(int row, int col)
    {
    	boolean isEditable = false;
    	
        if (col == COL_VALUE && fFieldDescriptors != null)
        {
        	isEditable = !fFieldDescriptors[row].isReadOnly();
        }

        return isEditable;
    }

    /**
	 * Get text value for cell of table
	 * 
	 * @param row table row
	 * @param col table column
	 */
	public Object getValueAt(int row, int col)
	{
		Object value = null;

		if (col == COL_NAME)
		{
			value = fFieldDescriptors[row].getDisplayName();
		}
		else if (col == COL_UNITS)
		{
			value = fFieldDescriptors[row].getUnits();
		}
		else
		{
            // Handle value column
			if (fBean instanceof Message)
			{
				value = ((Message) fBean).get(fFieldDescriptors[row].getName());
			}
			else if (fBean instanceof Script)
			{
				value = ((Script) fBean).get(fFieldDescriptors[row].getName());
			}
		}

		return value;
	}

    /**
	 * Set the value of the Values column for the specified row and column.
	 * 
	 * @param value the value to set
	 * @param row the row for the value
	 * @param column the column for the value
	 */
    public void setValueAt(Object value, int row, int column)
    {
        if (column != COL_VALUE || fFieldDescriptors == null
                || row > fFieldDescriptors.length)
        {
            return;
        }
        
        if(value == null)
        {
            return;
        }
        
        //System.out.println("PropertyTableModel (setValueAt): "+value.getClass());
        //System.out.println("Model setValueAt:" + fFieldDescriptors[row].getName() + " " + value);
        ((Map) fBean).put(fFieldDescriptors[row].getName(), value);
        fireTableCellUpdated(row, column);
    }

    /**
     * Returns the Java type info for the property at the given row.
     * 
     * @param row the row 
     * @return the Class for the given row
     */
    public Class getPropertyType(int row)
    {
        return fFieldDescriptors[row].getValueClass();
    }

    /**
     * Returns the FieldDescriptor for the row.
     * 
     * @param row the row 
     * @return the descriptor for the given row
     */
    public FieldDescriptor getFieldDescriptor(int row)
    {
        return fFieldDescriptors[row];
    }

    /**
     * Returns a new instance of the property editor for a given class. If an
     * editor is not specified in the property descriptor then it is looked up
     * in the PropertyEditorManager.
     * 
     * @param row the row to get the editor for
     */
    public PropertyEditor getPropertyEditor(int row)
    {
        Class cls = fFieldDescriptors[row].getPropertyEditorClass();

        PropertyEditor editor = null;

        if (cls != null)
        {
            try
            {
                editor = (PropertyEditor)cls.newInstance();
            }
            catch (Exception ex)
            {
                System.out.println("PropertyTableModel: Instantiation exception creating PropertyEditor");
            }
        }
        else
        {
            // Look for a registered editor for this type.
            Class type = getPropertyType(row);

            if (type != null)
            {
                editor = (PropertyEditor)fPropertyEditors.get(type);

                if (editor == null)
                {
                    // Load a shared instance of the property editor.
                    editor = PropertyEditorManager.findEditor(type);

                    if (editor != null)
                    {
                        fPropertyEditors.put(type, editor);
                    }
                }

                if (editor == null)
                {
                   // Use the editor for Object.class
                    editor = (PropertyEditor)fPropertyEditors.get(Object.class);
 
                    if (editor == null)
                    {
                        editor = PropertyEditorManager.findEditor(Object.class);
                        
                        if (editor != null)
                        {
                            fPropertyEditors.put(Object.class, editor);
                        }
                    }
                }
            }
        }
        
        return editor;
    }

    /**
     * Returns a flag indicating if the encapsulated object has a customizer.
     */
    public boolean hasCustomizer()
    {
        if (fBeanDescriptor != null)
        {
            Class cls = fBeanDescriptor.getCustomizerClass();
            return (cls != null);
        }

        return false;
    }

    /**
     * Gets the customizer for the current object.
     * 
     * @return New instance of the customizer or null if there isn't a
     * 		customizer.
     */
    public Component getCustomizer()
    {
        Component customizer = null;

        if (fBeanDescriptor != null)
        {
            Class cls = fBeanDescriptor.getCustomizerClass();

            if (cls != null)
            {
                try
                {
                    customizer = (Component)cls.newInstance();
                }
                catch (Exception ex)
                {
                    // XXX - debug
                    System.out
                            .println("PropertyTableModel: Instantiation exception creating Customizer");
                }
            }
        }

        return customizer;
    }

    /**
	 * Forces a sort and filter on the table rows. Fires a 
	 * TableDataChangedEvent.
	 */
	protected void resetTableRows()
	{
		filterTable(fCurrentFilter);
        sortTable(fSortOrder);
		
		// Redraw table rows
		fireTableDataChanged();
	}

	/**
	 * Sorts the table according to the sort type.
	 * 
	 * @param sort the sort order to apply to the table.
	 */
	protected void sortTable(int sort)
	{
		if (fFieldDescriptors == null)
		{
			// nothing to sort
			return;
		}
		else if (sort == SORT_ALPHA)
		{
			Arrays.sort(fFieldDescriptors, fComparator);
		}
	}

    /**
	 * Filters the table to display only arguments with specific attributes.
	 * The table will likely need to be sorted also.
	 * 
	 * @param filter The arguments to display.
	 * @see #sortTable(int)
	 */
    protected void filterTable(Object filter)
	{
		if (fBeanInfo == null)
			return;

		// Use collections to filter out unwanted properties
		ArrayList list = new ArrayList();
		Collection fields = 
			((AbstractMessageDescriptor) fMessageDescriptor).getFields();
		
		list.addAll(fields);

		ListIterator iterator = list.listIterator();
		FieldDescriptor field;
		

		while (iterator.hasNext())
		{
			field = (FieldDescriptor) iterator.next();

			if (ArgumentFilterModel.ALL == filter)
			{
				// Nothing to remove
			}
			else if (ArgumentFilterModel.STANDARD == filter)
			{
				if (field.isExpert() || field.isHidden())
				{
					iterator.remove();
				}
			}
			else if (ArgumentFilterModel.EXPERT == filter)
			{
				if (!field.isExpert() || field.isHidden())
				{
					iterator.remove();
				}
			}
			else if (ArgumentFilterModel.READ_ONLY == filter)
			{
				if (!field.isReadOnly() || field.isHidden())
				{
					iterator.remove();
				}
			}
			else if (ArgumentFilterModel.HIDDEN == filter)
			{
				if (!field.isHidden())
				{
					iterator.remove();
				}
			}
			else if (ArgumentFilterModel.PREFERRED == filter)
			{
				if (!field.isPreferred() || field.isHidden())
				{
					iterator.remove();
				}
			}
			else
			{
				System.out.println("Didn't understand filter:" + filter);
			}
		}

		fFieldDescriptors = 
			(FieldDescriptor[]) list.toArray(new FieldDescriptor[list.size()]);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageTableModel.java,v $
//  Revision 1.7  2006/04/18 04:21:55  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.6  2005/06/08 22:03:19  chostetter_cvs
//  Organized imports
//
//  Revision 1.5  2005/05/23 15:31:53  tames_cvs
//  Updated to reflect change to AbstractMessageDescriptor.
//
//  Revision 1.4  2005/01/27 22:00:46  tames
//  Misc coding style changes and removal of obsolete code.
//
//  Revision 1.3  2005/01/25 23:42:38  tames
//  Fixed sorting and filter bugs.
//
//  Revision 1.2  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//