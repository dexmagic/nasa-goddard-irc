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
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;
import gov.nasa.gsfc.irc.gui.vis.tabular.AbstractStatisticsColumn;

/**
 * ChannelMax returns the maximum channel value contained in the current 
 * buffer. The calculation does not span multiple buffers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/16 23:01:15 $
 * @author 	Troy Ames
 */
public class ChannelMax extends AbstractStatisticsColumn
{
	private static final String NAME = "Max";

	/**
	 * Constructs a ChannelMax with a result class of <code>Double</code>.
	 */
	public ChannelMax()
	{
		super();
		setName(NAME);
		setResultClass(Double.class);
	}

	/**
	 * Returns the maximum value of the data buffer for this channel.
	 * 
	 * @param dataBuffer the data buffer for the channel. Not used by this class.
	 * @param channel the channel information for this channel
	 */
	public Object getStatistic(DataBuffer dataBuffer, ChannelRenderInfo channel)
	{
		return new Double(dataBuffer.getMaxValue());
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ChannelMax.java,v $
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//