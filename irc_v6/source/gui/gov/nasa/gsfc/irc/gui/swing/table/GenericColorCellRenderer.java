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

package gov.nasa.gsfc.irc.gui.swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/22 18:13:44 $
 *  @author	    Ken Wootton
 */
public class GenericColorCellRenderer extends DefaultTableCellRenderer
{
	private static final String DEFAULT_TEXT = "";

	//  Properties of the renderer when a value is 'null'.
	private static final String MISSING_TEXT = "Missing Color";
	private static final Color MISSING_BG_COLOR = Color.white;

	/**
	 *    Get the 'JLabel' used for drawing the colored cell within 
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
			super.getTableCellRendererComponent(table, 
												value,
												isSelected, hasFocus, 
												row, column);

		//  This must be opaque to properly redraw its background.
		labelComp.setOpaque(true);

		//  Set it up.
		labelComp.setHorizontalAlignment(SwingConstants.RIGHT);

		//  If we have a color, use it.
		if (value != null)
		{
			labelComp.setText(DEFAULT_TEXT);
			labelComp.setBackground((Color) value);
		}

		//  Otherwise, use a default.
		else
		{
			labelComp.setText(MISSING_TEXT);
			labelComp.setBackground(MISSING_BG_COLOR);
		}

		return labelComp;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: GenericColorCellRenderer.java,v $
//  Revision 1.1  2004/09/22 18:13:44  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.1  2004/09/16 21:13:08  jhiginbotham_cvs
//  Port from IRC v5.
// 
