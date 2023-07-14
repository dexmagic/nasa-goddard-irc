//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ListUtil.java,v $
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
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

package gov.nasa.gsfc.irc.gui.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

/**
 *  Collection of list utility methods.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/16 21:12:51 $
 *  @author	    John Higinbotham 
**/
public class ListUtil 
{
	/**
	 * Helper to populate a sorted JList. 
	 *
	 * @param src Source of data (Object[])
	 * @param dest Destination of data (JList)
	 *
	**/
	public static void populateSortedList(Object[] src, JList dest)
	{
		Arrays.sort(src);
		dest.setListData(src);
	}

	/**
	 * Helper to clear a JList. 
	 *
	 * @param dest Destination of data (JList)
	 *
	**/
	public static void clearList(JList dest)
	{
		String[]  clear = new String[0];
		dest.setListData(clear);
	}

	/**
	 *    Return a list of the selected items within the given selection model,
	 *  where the items themselves are contained within the given list model.
	 *
	 *  @param selectionModel  the selection model for the list
	 *  @param listModel  the model for the list
	 *
	 *  @return  a list (which may be empty) of the selected items
	 */
	public static List getSelectedItems(ListSelectionModel selectionModel,
								 		ListModel listModel)
	{
		//  Get the range of indices we need to check.
		int minSelIndex = selectionModel.getMinSelectionIndex();
		int maxSelIndex = selectionModel.getMaxSelectionIndex();

		//  Collect the list of selected items from this range.
		LinkedList selectedList = new LinkedList();
		for (int i = minSelIndex; i <= maxSelIndex; i++)
		{
			if (selectionModel.isSelectedIndex(i))
			{
				selectedList.add(listModel.getElementAt(i));
			}
		}
		
		return selectedList;
	}

	/**
	 *    Return a list of the selected items in the given column,
	 *  where the items themselves are contained within the given table model.
	 *
	 *  @param selectionModel  the selection model for the table
	 *  @param tableModel  the model for the table
	 *  @param column  the column of interest within the table
	 *
	 *  @return  a list (which may be empty) of the selected items
	 */
	public static List getSelectedColumn(ListSelectionModel selectionModel,
								 		 TableModel tableModel,
										 int column)
	{
		//  Get the range of indices we need to check.
		int minSelIndex = selectionModel.getMinSelectionIndex();
		int maxSelIndex = selectionModel.getMaxSelectionIndex();

		//  Collect the list of selected items from this range.
		LinkedList selectedList = new LinkedList();
		for (int i = minSelIndex; i <= maxSelIndex; i++)
		{
			if (selectionModel.isSelectedIndex(i))
			{
				selectedList.add(tableModel.getValueAt(i, column));
			}
		}
		
		return selectedList;
	}

	/**
	 *    Select the given values in the given list.  This is provided because
	 *  this method seems to be missing from the 'JList' API.
	 *
	 *  @param list  list to select the values from
	 *  @param values  the values to select
	 */
	public static void setSelectedValues(JList list, List values)
	{
		ListModel listModel = list.getModel();
		int numModelItems = listModel.getSize();
		int numValues = values.size();

		//  Selected indices
		LinkedList selectedIndices = new LinkedList();
		int[] selectedIndicesArr = null;

		//  Figure out the indices we need to select.  Note that we don't
		//  assume that the values actually exist in the list.
		for (int i = 0; i < numModelItems; i++)
		{
			if (values.contains(listModel.getElementAt(i)))
			{
				selectedIndices.add(new Integer(i));
			}
		}

		//  Translate the list of integer objects into an integer array.
		selectedIndicesArr = new int[selectedIndices.size()];
		for (int i = 0; i < selectedIndicesArr.length; i++)
		{
			selectedIndicesArr[i] = 
				((Integer) selectedIndices.get(i)).intValue();
		}

		//  Finally, make the API call.
		list.setSelectedIndices(selectedIndicesArr);
	}
}
