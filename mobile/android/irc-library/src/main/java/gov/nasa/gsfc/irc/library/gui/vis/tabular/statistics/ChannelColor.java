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

package gov.nasa.gsfc.irc.library.gui.vis.tabular.statistics;

import java.awt.Color;

import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.gui.swing.table.GenericColorCellRenderer;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;
import gov.nasa.gsfc.irc.gui.vis.tabular.AbstractStatisticsColumn;

/**
 * The ChannelColor class returns the color a channel is being drawn on the 
 * screen as a key to visually associate a channel in the table with the 
 * same channel in another visualization.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/16 23:01:15 $
 * @author 	Troy Ames
 */
public class ChannelColor extends AbstractStatisticsColumn
{
	private static final String NAME = "Color";
	private static final GenericColorCellRenderer sCellRenderer =
		new GenericColorCellRenderer();

	/**
	 * Constructs a ChannelColor with a result class of <code>Color</code>
	 * and a <code>GenericColorCellRenderer</code> as the cell renderer.
	 */
	public ChannelColor()
	{
		super();
		setName(NAME);
		setResultClass(Color.class);
		setCellRenderer(sCellRenderer);
	}

	/**
	 * Returns the color representation of the channel.
	 * 
	 * @param dataBuffer the data buffer for the channel. Not used by this class.
	 * @param channel the channel information for this channel
	 */
	public Object getStatistic(DataBuffer dataBuffer, ChannelRenderInfo channel)
	{
		Color color = null;
		
		if (channel != null)
		{
			color = channel.getColor();
		}
		
		return color;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ChannelColor.java,v $
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.2  2004/12/10 16:41:42  tames
//  Added check for null channel argument in getStatistic method.
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//