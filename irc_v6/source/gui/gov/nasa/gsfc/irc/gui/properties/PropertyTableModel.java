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
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import gov.nasa.gsfc.commons.properties.beans.DescriptorNameComparator;
import gov.nasa.gsfc.commons.properties.beans.DescriptorTypeComparator;
import gov.nasa.gsfc.irc.gui.browser.BeanInfoFactory;
import gov.nasa.gsfc.irc.gui.browser.ComponentBrowser;
import gov.nasa.gsfc.irc.gui.browser.PropertyFilterModel;

/**
 * Table model used to obtain property names and values. This model encapsulates 
 * an array of PropertyDescriptors.
 *
 * @version	$Date: 2005/12/31 20:38:52 $
 */
public class PropertyTableModel extends AbstractTableModel
{
    /**
     * Logger for this class
     */
    private static final Logger sLogger = Logger
            .getLogger(PropertyTableModel.class.getName());

    private PropertyDescriptor[] fDescriptors;
    private BeanDescriptor fBeanDescriptor;
    private BeanInfo fBeanInfo;
    private Object fBean;

    // Cached property editors.
    private static Hashtable fPropertyEditors;

    // Shared instance of a comparator
    private static DescriptorNameComparator fNameComparator = 
    	new DescriptorNameComparator();
    private static DescriptorTypeComparator fTypeComparator = 
    	new DescriptorTypeComparator();

    private static final int NUM_COLUMNS = 2;

    public static final int COL_NAME = 0;
    public static final int COL_VALUE = 1;

    private Object fCurrentFilter = PropertyFilterModel.STANDARD;

    // Sort options
    public static final int SORT_OFF = 0;
    public static final int SORT_ALPHA = 1;
    public static final int SORT_TYPE = 2;

    private int fSortOrder = SORT_ALPHA;

    public PropertyTableModel()
    {
        if (fPropertyEditors == null)
        {
            fPropertyEditors = new Hashtable();
        }
    }

    /**
     * Default constructor for a PropertyTableModel. The target Object can be 
     * set with the <code>setObject</code> method.
     */
    public PropertyTableModel(Object bean)
    {
        this();
        setObject(bean);
    }

    /**
     * Sets the current filter of the properties. If the filter has 
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
     * Sets the current sort order for the properties. If the sort order has 
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
     * Set the table model to represents the properties of the object.
     * 
     * @param bean the target Object
     */
    public void setObject(Object bean)
    {
    	if (bean == null)
    	{
    		// Use a dummy Object
    		bean = new Object();
    	}
    	
        fBean = bean;

        fBeanInfo = BeanInfoFactory.getBeanInfo(bean.getClass());

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

    	if (fDescriptors != null)
        {
            count = fDescriptors.length;
        }
    	
        return count;
    }

    /**
     * Get column count (2: name, value)
     * 
     * @return the current column count
     */
    public int getColumnCount()
    {
        return NUM_COLUMNS;
    }

    /**
     * Check if given cell is editable
     * @param row table row
     * @param col table column
     */
    public boolean isCellEditable(int row, int col)
    {
    	boolean isEditable = false;

    	if (col == COL_VALUE && fDescriptors != null)
        {
    		isEditable = (fDescriptors[row].getWriteMethod() == null) ? false : true;
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
            value = fDescriptors[row].getDisplayName();
        }
        else
        {
            // Handle value column
            Method getter = fDescriptors[row].getReadMethod();

            if (getter != null)
            {
                Class[] paramTypes = getter.getParameterTypes();
                Object[] args = new Object[paramTypes.length];

                try
                {
                    for (int i = 0; i < paramTypes.length; i++)
                    {
                        // XXX - debug
                        System.out.println("\tShouldn't happen! getValueAt getter = "
                                        + getter
                                        + " parameter = "
                                        + paramTypes[i]);
                        args[i] = paramTypes[i].newInstance();
                    }
                }
                catch (Exception ex)
                {
                    // XXX - handle better
                    ex.printStackTrace();
                }

                try
                {
                    value = getter.invoke(fBean, args);
                }
                catch (IllegalArgumentException ex)
                {
                    // XXX - handle better
                    ex.printStackTrace();
                }
                catch (IllegalAccessException ex2)
                {
                    // XXX - handle better
                    ex2.printStackTrace();
                }
                catch (InvocationTargetException ex3)
                {
                    // XXX - handle better
                    ex3.printStackTrace();
                }
                catch (NoSuchMethodError ex4)
                {
                    // XXX - handle better
                    System.out.println("NoSuchMethodError for method invocation");
                    System.out.println("Bean: " + fBean.toString());
                    System.out.println("Getter: " + getter.getName());
                    System.out.println("Getter args: ");
                    for (int i = 0; i < args.length; i++)
                    {
                        System.out.println("\t" + "type: " + paramTypes[i]
                                + " value: " + args[i]);
                    }
                    ex4.printStackTrace();
                }
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
        if (column != COL_VALUE || fDescriptors == null
                || row > fDescriptors.length)
        {
            return;
        }
        
        if(value == null)
        {
            return;
        }
        
        //System.out.println("PropertyTableModel (setValueAt): "+value.getClass());
        
        Method setter = fDescriptors[row].getWriteMethod();
        
        if (setter != null)
        {
            try
            {
                setter.invoke(fBean, new Object[] { value });
            }
            catch (IllegalArgumentException ex)
            {
                // XXX - handle better
                //System.out.println("Setter: " + setter + "\nArgument: "
                //        + value.getClass().toString());
                System.out.println("Row: " + row + " Column: " + column);
                ex.printStackTrace();
                System.out.println("\n");
            }
            catch (IllegalAccessException ex2)
            {
                // XXX - handle better
                System.out.println("Setter: " + setter + "\nArgument: "
                        + value.getClass().toString());
                System.out.println("Row: " + row + " Column: " + column);
                ex2.printStackTrace();
                System.out.println("\n");
            }
            catch (InvocationTargetException ex3)
            {
                
                // Unfortunately I'm mixing some "view" into this "model" to
                // display messages from PropertyVetos.  I can't propagate an
                // exception because setValueAt is inherited.  I can't find 
                // a reference to another appropriate class within this class
                // to send some sort of veto event (and display the message),
                // so therefore I'm displaying the message here, and borrowing
                // a component from the component browser (if it exists).  If
                // the component browser isn't being used, the dialog box is
                // not generated.  Can someone show me a better way?   S. Maher
               
                if (ex3.getTargetException() instanceof PropertyVetoException)
                {
                    if (ComponentBrowser.getComponent() != null)
                    {
                        JOptionPane.showMessageDialog(ComponentBrowser.getComponent(),
                                ex3.getCause().getMessage(),
                                "Invalid Property Value",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    sLogger.logp(Level.CONFIG, "PropertyTableModel",
                            "setValueAt()", "PropertyVetoException: " + ex3.getCause().getMessage());

                }
                else 
                {
                    // XXX - handle better
                    System.out.println("Setter: " + setter + "\nArgument: "
                            + value.getClass().toString());
                    System.out.println("Row: " + row + " Column: " + column);
                    ex3.printStackTrace();
                    System.out.println("\n");
                }
            }

        }
    }

    /**
     * Returns the Java type info for the property at the given row.
     * 
     * @param row the row 
     * @return the Class for the given row
     */
    public Class getPropertyType(int row)
    {
        return fDescriptors[row].getPropertyType();
    }

    /**
     * Returns the PropertyDescriptor for the row.
     * 
     * @param row the row 
     * @return the descriptor for the given row
     */
    public PropertyDescriptor getPropertyDescriptor(int row)
    {
        return fDescriptors[row];
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
        Class cls = fDescriptors[row].getPropertyEditorClass();

        PropertyEditor editor = null;

        if (cls != null)
        {
            try
            {
                editor = (PropertyEditor)cls.newInstance();
            }
            catch (Exception ex)
            {
                // XXX - debug
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
	 * Sorts the table according to the sort type.
	 * 
	 * @param sort the sort order to apply to the table.
	 */
    public void sortTable(int sort)
    {
		if (fDescriptors == null)
		{
			// Nothing to sort
			return;
		}
		else if (sort == SORT_ALPHA)
		{
			Arrays.sort(fDescriptors, fNameComparator);
		}
		else if (sort == SORT_TYPE)
		{
			Arrays.sort(fDescriptors, fTypeComparator);
		}
    }

    /**
     * Filters the table to display only properties with specific attributes.
	 * The table will likely need to be sorted also.
	 * 
	 * @param filter The properties to display.
	 * @see #sortTable(int)
     */
    public void filterTable(Object filter)
	{
		if (fBeanInfo == null)
			return;

		fDescriptors = fBeanInfo.getPropertyDescriptors();

		// Use collections to filter out unwanted properties
		ArrayList list = new ArrayList();
		list.addAll(Arrays.asList(fDescriptors));

		ListIterator iterator = list.listIterator();
		PropertyDescriptor desc;

		while (iterator.hasNext())
		{
			desc = (PropertyDescriptor) iterator.next();

			if (PropertyFilterModel.ALL == filter)
			{
				// Nothing to remove
			}
			else if (PropertyFilterModel.STANDARD == filter)
			{
				if (desc.getWriteMethod() == null || desc.isExpert()
						|| desc.isHidden())
				{
					iterator.remove();
				}
			}
			else if (PropertyFilterModel.EXPERT == filter)
			{
				if (desc.getWriteMethod() == null || !desc.isExpert()
						|| desc.isHidden())
				{
					iterator.remove();
				}
			}
			else if (PropertyFilterModel.READ_ONLY == filter)
			{
				if (desc.getWriteMethod() != null || desc.isHidden())
				{
					iterator.remove();
				}
			}
			else if (PropertyFilterModel.HIDDEN == filter)
			{
				if (!desc.isHidden())
				{
					iterator.remove();
				}
			}
			else if (PropertyFilterModel.BOUND == filter)
			{
				if (!desc.isBound() || desc.isHidden())
				{
					iterator.remove();
				}
			}
			else if (PropertyFilterModel.CONSTRAINED == filter)
			{
				if (!desc.isConstrained() || desc.isHidden())
				{
					iterator.remove();
				}
			}
			else if (PropertyFilterModel.PREFERRED == filter)
			{
				if (!desc.isPreferred() || desc.isHidden())
				{
					iterator.remove();
				}
			}
		}

		fDescriptors = (PropertyDescriptor[]) list
				.toArray(new PropertyDescriptor[list.size()]);
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
}

//--- Development History ---------------------------------------------------
//
//  $Log: PropertyTableModel.java,v $
//  Revision 1.7  2005/12/31 20:38:52  tames
//  Modified setObject method to handle a null argument.
//
//  Revision 1.6  2005/01/27 22:04:06  tames
//  Fixed numerous bugs including: correct row size for specific property
//  editor, correct ending of edit session when editor focus changes.
//
//  Revision 1.5 2005/01/11 21:35:46 chostetter_cvs
//  Initial version
//
//  Revision 1.4 2005/01/10 14:12:10 smaher_cvs
//  Added dialog box for PropertyVetoExceptions (if the ComponentBrowser is being
// used).
//
//  Revision 1.3 2005/01/09 05:21:04 smaher_cvs
//  *** empty log message ***
//
//  Revision 1.2  2005/01/07 21:39:43  tames
//  Relocation and numerous other changes.
//
//  Revision 1.2  2004/12/17 21:09:50  smaher_cvs
//  Added BitArray editor
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
 * @version 1.16 02/27/02
 * @author  Mark Davidson
 */
