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

package gov.nasa.gsfc.irc.gui.vis.tabular;

import javax.swing.table.TableCellRenderer;

import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;

/**
 * The interface of statistics calculators for a column in a StatisticsTable.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/06 19:31:53 $
 * @author 	Troy Ames
 */
public interface StatisticsColumn
{
	/** 
	 * Get the statistic for the given buffer and channel information.
	 * 
	 * @param	buffer the data buffer to calculate statistics on
	 * @param	channel the specific channel information object that cooresponds
	 * 			to this buffer.
	 */
	public Object getStatistic(DataBuffer buffer, ChannelRenderInfo channel);
	
	/**
	 * Get the class that the <code>getStatistic</code> method will return. 
	 * 
	 * @return the result <code>Class</code> of this statistics column.
	 */
	public Class getResultClass();
	
	/** 
	 * Get the name of this statistics column.
	 * 
	 * @return the name of this column
	 */
	public String getName();
	
    /**
     * Returns the <code>TableCellRenderer</code> used by the
     * <code>JTable</code> to draw values for this type of column.  
     * The <code>cellRenderer</code> of the column
     * not only controls the visual look for the column, but is also used to
     * interpret the value object supplied by the <code>TableModel</code>.
     * When the <code>cellRenderer</code> is <code>null</code>,
     * the <code>JTable</code> uses a default renderer based on the 
     * class of the cells in that column. The default value for a 
     * <code>cellRenderer</code> is <code>null</code>.  
     *
     * @return	the <code>cellRenderer</code> property
     * @see	JTable#setDefaultRenderer
     */
    public TableCellRenderer getCellRenderer();
}


//--- Development History  ---------------------------------------------------
//
//  $Log: StatisticsColumn.java,v $
//  Revision 1.3  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//