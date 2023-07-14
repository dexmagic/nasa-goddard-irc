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
 * An abstract implementation of the 
 * {@link StatisticsColumn}
 * interface. Subclasses must implement the <code>getStatistic</code> method
 * and set the properties that differ from the defaults defined by this
 * class. The name property should always be set by subclasses. The 
 * Cell Renderer is by default <code>null</code>. The default value for 
 * Result Class is <code>Object</code>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/15 20:42:22 $
 * @author 	Troy Ames
 */
public abstract class AbstractStatisticsColumn implements StatisticsColumn
{
	private Class fResultClass = Object.class;
	private String fName = "";
	private TableCellRenderer fCellRenderer = null;

	/** 
	 * Get the statistic for the given buffer and channel information.
	 * 
	 * @param	buffer the data buffer to calculate statistics on
	 * @param	channel the specific channel information object that cooresponds
	 * 			to this buffer.
	 */
	public abstract Object getStatistic(DataBuffer buffer, ChannelRenderInfo channel);

	/**
	 * Get the class that the <code>getStatistic</code> method will return. 
	 * The default value is <code>Object</code>.
	 * 
	 * @return the result <code>Class</code> of this statistics column.
	 */
	public Class getResultClass()
	{
		return fResultClass;
	}

	/**
	 * Set the result class that is returned by the <code>getStatistic</code>
	 * method for this instance.
	 * 
	 * @param resultClass the result class of this instance
	 */
	public void setResultClass(Class resultClass)
	{
		fResultClass = resultClass;
	}

	/** 
	 * Get the name of this statistics column.
	 * 
	 * @return the name of this column
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * Set the name of this statistics column.
	 * 
	 * @param name the name of this column
	 */
	public final void setName(String name)
	{
		fName = name;
	}

    /**
	 * Returns the <code>TableCellRenderer</code> used by the
	 * <code>JTable</code> to draw values for this type of column. The
	 * <code>cellRenderer</code> of the column not only controls the visual
	 * look for the column, but is also used to interpret the value object
	 * supplied by the <code>getStatistic</code> method. When the
	 * <code>cellRenderer</code> is <code>null</code>, the
	 * <code>JTable</code> uses a default renderer based on the class of the
	 * cells in that column. The default value is <code>null</code>.
	 * 
	 * @return the <code>cellRenderer</code> property
	 * @see javax.swing.JTable#setDefaultRenderer
	 */
    public TableCellRenderer getCellRenderer() 
    {
    	return fCellRenderer;
    }

    /**
     * Sets the <code>TableCellRenderer</code> used by <code>JTable</code>
     * to draw individual values for this statistic.  
     *
     * @param cellRenderer  the new cellRenderer
     * @see	#getCellRenderer
     */
    public void setCellRenderer(TableCellRenderer cellRenderer) 
    {
		fCellRenderer = cellRenderer;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractStatisticsColumn.java,v $
//  Revision 1.2  2006/03/15 20:42:22  tames_cvs
//  Javadoc change only.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//