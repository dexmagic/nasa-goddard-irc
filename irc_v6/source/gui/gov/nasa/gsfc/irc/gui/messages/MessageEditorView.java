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
import java.beans.Customizer;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * MessageEditorView is a visual table editor for a message or script.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/25 23:42:38 $
 * @author 	Troy Ames
 */
public class MessageEditorView extends JTable implements Customizer
{

	/**
	 * Creates a MessageEditorView.
	 */
	public MessageEditorView()
	{
		super(new MessageTableModel(), new MessageColumnModel());
	}

    /**
     * Set the message or script to be customized.  
     * 
     * @param message  The object to be customized.
     */
	public void setObject(Object message)
	{
		Object oldMessage = getObject();
		((MessageTableModel) getModel()).setObject(message);
		
		int rows = getRowCount();
		
		// Select the first editable row for input
		for (int i = 0; i < rows; i++)
		{
			if (isCellEditable(i, 1))
			{
				//TableCellEditor editor = getCellEditor(i, 1);	
				changeSelection(i, 1, false, false);
				editCellAt(i, 1);
				break;
			}
		}
		
		firePropertyChange("object", oldMessage, message);
	}
	
    /**
     * Return the current message or script that is represented by 
     * this customizer.
     * 
     * @return the current message or script
     */
    public Object getObject()
    {
        return ((MessageTableModel) getModel()).getObject();
    }

    /**
     * Sets the current filter of the Properties.
     * 
     * @param filter 
     */
    public void setFilter(Object filter)
    {
    	stopCurrentEdit();
        ((MessageTableModel) getModel()).setFilter(filter);
    }

    /**
     * Sets the current sort order for the arguments.
     * 
     * @param sort one of the SORT_ constants
     */
    public void setSortOrder(int sort)
    {
    	stopCurrentEdit();
        ((MessageTableModel) getModel()).setSortOrder(sort);
    }

	/**
	 *  Tell the customizer to stop the current editing session.
	 */
	public void stopCurrentEdit()
	{
		TableCellEditor editor = getCellEditor();
		
		if (editor != null)
		{
			editor.stopCellEditing();
		}
	}

    /**
	 * Prepares the editor by querying the data model for the value and
	 * selection state of the cell at <code>row</code>,<code>column</code>.
	 * <p>
	 * <b>Note: </b> Throughout the table package, the internal implementations
	 * always use this method to prepare editors so that this default behavior
	 * can be safely overridden by a subclass.
	 * 
	 * @param editor the <code>TableCellEditor</code> to set up
	 * @param row the row of the cell to edit, where 0 is the first row
	 * @param column the column of the cell to edit, where 0 is the first column
	 * @return the <code>Component</code> being edited
	 */
	public Component prepareEditor(TableCellEditor editor, int row, int column)
	{
		Component component = super.prepareEditor(editor, row, column);

		if (component instanceof JComponent)
		{
			JComponent jComp = (JComponent) component;
			//			if (column == 1 && jComp instanceof JTextField)
			//			{
			//				System.out.println("Table prepareEditor instanceof");
			//				((JTextField) jComp).requestFocusInWindow();
			//				((JTextField) jComp).selectAll();
			//			}
		}

		return component;
	}

    /**
	 * Register a listener for the PropertyChange event. The customizer should
	 * fire a PropertyChange event whenever it changes the target bean in a way
	 * that might require the displayed properties to be refreshed.
	 * 
	 * @param listener An object to be invoked when a PropertyChange event is
	 *            fired.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		super.addPropertyChangeListener(listener);
	}

    /**
     * Remove a listener for the PropertyChange event.
     *
     * @param listener  The PropertyChange listener to be removed.
     */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		super.removePropertyChangeListener(listener);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageEditorView.java,v $
//  Revision 1.2  2005/01/25 23:42:38  tames
//  Fixed sorting and filter bugs.
//
//  Revision 1.1  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//