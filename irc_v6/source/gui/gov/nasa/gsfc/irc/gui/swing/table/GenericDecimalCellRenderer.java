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
import java.text.NumberFormat;

/**
 *
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/04/04 19:17:06 $
 *  @author	    Bob Loewenstein
 */
public class GenericDecimalCellRenderer extends DefaultTableCellRenderer
{

	//  Properties of the renderer when a value is 'null'.
	private static final String DEFAULT_TEXT = "--";
	private int precision = 1;
	NumberFormat fFormatter =  NumberFormat.getInstance();

	/**
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

		//  Set it up.
		labelComp.setHorizontalAlignment(SwingConstants.RIGHT);

		//  If we have a value, use it.
		if (value != null)
		{
			Double myValue = (Double)value;
			fFormatter.setMaximumFractionDigits(precision);
			fFormatter.setMinimumFractionDigits(precision);
			String myString = fFormatter.format(myValue);
			
			labelComp.setText(myString);
		}

		//  Otherwise, use a default.
		else
		{
			labelComp.setText(DEFAULT_TEXT);
		}

		return labelComp;
	}
	
	public void setPrecision(int decimalPlaces) {
		
		precision = decimalPlaces;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: GenericDecimalCellRenderer.java,v $
//  Revision 1.2  2006/04/04 19:17:06  rfl
//  change in doc only
//
//  Revision 1.1  2006/03/07 20:39:47  rfl
//  Provides support for printing N decimal places precision
//
