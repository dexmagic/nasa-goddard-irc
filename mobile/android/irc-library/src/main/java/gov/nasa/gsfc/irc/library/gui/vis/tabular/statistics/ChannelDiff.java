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

import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.gui.swing.table.GenericDecimalCellRenderer;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;
import gov.nasa.gsfc.irc.gui.vis.tabular.AbstractStatisticsColumn;

/**
 * ChannelDiff returns the difference of Max & Min (Max-Min)  of the current
 * buffer for the channel. The calculation does not span multiple buffers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/07 21:01:26 $
 * @author 	Bob Loewenstein
 */
public class ChannelDiff extends AbstractStatisticsColumn
{
	private static final String NAME = "Diff";
	private int decimalPrecision = 0;
	private static final GenericDecimalCellRenderer sCellRenderer =
		new GenericDecimalCellRenderer();

	/**
	 * Constructs a ChannelDiff with a result class of <code>Double</code>.
	 */
	public ChannelDiff()
	{
		super();
		setName(NAME);
		setResultClass(Double.class);
		setCellRenderer(sCellRenderer);
		
		// set the decimal precision for these data
		sCellRenderer.setPrecision(decimalPrecision);
	}

	/**
	 * Returns the difference of Max & Min (Max-Min)  of the data buffer for this channel.
	 * 
	 * @param dataBuffer the data buffer for the channel. Not used by this class.
	 * @param channel the channel information for this channel
	 */
	public Object getStatistic(DataBuffer dataBuffer, ChannelRenderInfo channel)
	{
		Double maxValue = new Double(dataBuffer.getMaxValue());
		Double minValue = new Double(dataBuffer.getMinValue());
		double diff = maxValue.doubleValue() - minValue.doubleValue();
		Double diffValue = new Double(diff);
		
		return diffValue;
	}
}


//