//=== File Prolog ============================================================
//
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

package gov.nasa.gsfc.irc.gui.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import gov.nasa.gsfc.commons.types.collections.ObjectPair;

/**
 * This class is a simple table model for System properties or 
 * preferences.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/03/22 20:19:11 $
 * @author 	Troy Ames
 */
public class SystemPreferencesTableModel extends AbstractTableModel
{
    private List fProperties;
    private String fNameColumnLabel = "Name";
    private String fValueColumnLabel = "Value";


	/**
	 * Default constructor that uses the current System properties.
	 */
	public SystemPreferencesTableModel()
	{
		super();
		fProperties = new ArrayList();
		Properties properties = System.getProperties();
		
        for (Iterator iterator = properties.keySet().iterator();
        	iterator.hasNext(); ) 
        {
            String name = (String) iterator.next();
            String value = System.getProperty(name);
            ObjectPair nameValuePair = new ObjectPair(name, value);
            fProperties.add(nameValuePair);
        }
        
        // Sort
        Collections.sort(fProperties, new PreferenceComparator(true));
	}

    /**
	 * Returns the name of the specified column.
	 * 
	 * @param column the column index.
	 * @return the column name.
	 */
	public String getColumnName(final int column)
	{
		if (column == 0)
		{
			return fNameColumnLabel;
		}
		else
		{
			return fValueColumnLabel;
		}
	}

    /**
     * Returns the number of columns in the table model.  In this case, there 
     * are two columns: one for the preference name, and one for the value.
     *
     * @return the column count (always 2 in this case).
     */
	public int getColumnCount()
	{
		return 2;
	}

	/**
	 * Returns the number of rows in the table model.
	 * 
	 * @return the row count.
	 */
	public int getRowCount()
	{
        return fProperties.size();
	}

    /**
     * Returns the value at the specified row and column.
     *
     * @param rowIndex  the row index.
     * @param columnIndex  the column index.
     * @return the value.
     */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object result = null;
        ObjectPair property = (ObjectPair) fProperties.get(rowIndex);
        
        if (columnIndex == 0)
		{
			result = property.first;
		}
		else if (columnIndex == 1)
		{
			result = property.second;
		}
         
        return result;
	}
	
	/**
	 * A class for comparing ObjectPair preference objects.
	 */
	private class PreferenceComparator implements Comparator
	{
		/** Indicates the sort order. */
		protected boolean fAscending;

		/**
		 * Standard constructor.
		 *
		 * @param ascending  true for ascending order, false for descending.
		 */
		public PreferenceComparator(final boolean ascending)
		{
			fAscending = ascending;
		}

		/**
		 * Compares two objects.
		 *
		 * @param obj1  the first object.
		 * @param obj2  the second object.
		 *
		 * @return an integer that indicates the relative order of the objects.
		 */
		public int compare(final Object obj1, final Object obj2)
		{
			int result = 0;

				String name1 = (String) ((ObjectPair) obj1).first;
				String name2 = (String) ((ObjectPair) obj2).first;
				
				if (fAscending)
				{
					result = name1.compareTo(name2);
				}
				else
				{
					result = name2.compareTo(name1);
				}
				
				return result;
		}

		/**
		 * Returns <code>true</code> if this object is equal to the specified 
		 * object, and <code>false</code> otherwise.
		 *
		 * @param obj  the other object.
		 * @return A boolean.
		 */
		public boolean equals(final Object obj)
		{
			boolean result = false;
			
			if (this == obj)
			{
				result = true;
			}
			else if ((obj instanceof PreferenceComparator))
			{
				PreferenceComparator comparator = (PreferenceComparator) obj;

				if (fAscending == comparator.fAscending)
				{
					result = true;
				}
			}

			return result;
		}

		/**
		 * Returns a hash code value for the object.
		 *
		 * @return the hashcode
		 */
		public int hashCode()
		{
			return (fAscending ? 1 : 0);
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: SystemPreferencesTableModel.java,v $
//  Revision 1.1  2005/03/22 20:19:11  tames
//  Initial version.
//
//