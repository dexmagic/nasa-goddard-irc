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

package gov.nasa.gsfc.irc.gui.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *    This simple cell renderer provides a tool tip which is the same
 *  as the string representation of the value.  It is intended to assist
 *  in the display of the entire value when the cell may be too small to
 *  do so itself.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/22 18:13:31 $
 *  @author	    Ken Wootton
 */
public class ToolTipCellRenderer extends DefaultTableCellRenderer
{
	/**
	 *    Get the component used for drawing the specified cell in 
	 *  the table.
	 *
	 *  @param table  the table that is asking the renderer to draw
	 *  @param value  the property inspector for the value to be rendered
	 *  @param isSelected  whether or not the cell is selected
	 *  @param hasFocus  whether or not the cell has focus
	 *  @param row  row of the cell to draw
	 *  @param col  column of the cell to draw
	 */
	public Component getTableCellRendererComponent(JTable table,
												   Object value, 
												   boolean isSelected, 
												   boolean hasFocus, 
												   int row, int column)
	{
		JLabel labelComp = (JLabel)
			super.getTableCellRendererComponent(table, value,
												isSelected, hasFocus, 
												row, column);

		//  Add the value itself as a tooltip.
		if (labelComp != null && value != null)
		{
			labelComp.setToolTipText(value.toString());
		}

		return labelComp;
	}	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ToolTipCellRenderer.java,v $
//  Revision 1.1  2004/09/22 18:13:31  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
