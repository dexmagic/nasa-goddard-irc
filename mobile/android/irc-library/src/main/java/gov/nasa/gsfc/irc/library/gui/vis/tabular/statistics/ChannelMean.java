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
 * ChannelMean returns the mean value contained in the current 
 * buffer for the channel. The calculation does not span multiple buffers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/07 21:01:26 $
 * @author 	Troy Ames
 */
public class ChannelMean extends AbstractStatisticsColumn
{
	private static final String NAME = "Mean";
	private int decimalPrecision = 1;
	private static final GenericDecimalCellRenderer sCellRenderer =
		new GenericDecimalCellRenderer();

	/**
	 * Constructs a ChannelMean with a result class of <code>Double</code>.
	 */
	public ChannelMean()
	{
		super();
		setName(NAME);
		setResultClass(Double.class);
		setCellRenderer(sCellRenderer);
		
		// set the decimal precision for these data
		sCellRenderer.setPrecision(decimalPrecision);
	}

	/**
	 * Returns the mean value of the data buffer for this channel.
	 * 
	 * @param dataBuffer the data buffer for the channel
	 * @param channel the channel information for this channel
	 */
	public Object getStatistic(DataBuffer dataBuffer, ChannelRenderInfo channel)
	{
		double mean = dataBuffer.getArithmeticMean();
		
		return new Double(mean);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ChannelMean.java,v $
//  Revision 1.3  2006/03/07 21:01:26  rfl
//  modified to use GenericDecimalCellRenderer to set and display a double with decimal precision limited to a set number of  digits
//
//  Revision 1.2  2005/01/28 23:57:34  chostetter_cvs
//  Added ability to integrate DataBuffers and requests, renamed average value
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//