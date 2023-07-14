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

package gov.nasa.gsfc.irc.gui.vis.channel;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import gov.nasa.gsfc.irc.gui.util.ColorWheel;
import gov.nasa.gsfc.irc.gui.util.ColorWheelUsage;

/**
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/21 22:03:13 $
 * @author 	Troy Ames
 */
public class DefaultChannelModel implements ChannelModel
{
	private ColorWheelUsage fColorWheel = null;

	private LinkedHashMap fRenderInfo = new LinkedHashMap(5);
	
	/**
	 * 
	 */
	public DefaultChannelModel()
	{
		this(null);
	}
	
	/**
	 * 
	 */
	public DefaultChannelModel(ColorWheel colorWheel)
	{
		super();
		
		fColorWheel = new ColorWheelUsage(colorWheel);
	}
	
	public void addChannel(String channelName)
	{
		if (channelName != null && !fRenderInfo.containsKey(channelName))
		{
			// Create trace info for new buffer
			ChannelRenderInfo renderInfo = new ChannelRenderInfo();
			
			renderInfo.setName(channelName);
			
			Color color = fColorWheel.getLeastUsed();
			fColorWheel.addUse(color);
			renderInfo.setColor(color);
			
			fRenderInfo.put(channelName, renderInfo);
		}
	}
	
	public void setChannel(String channelNames)
	{
		if (channelNames != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(channelNames, "|");
			
			int numNames = tokenizer.countTokens();
			
			for (int i = 0; i < numNames; i++)
			{
				String bufferName = tokenizer.nextToken().trim();
				
				addChannel(bufferName);
			}
		}
	}
	
	public Object[] getChannelNames()
	{
		return fRenderInfo.keySet().toArray();
	}
	
	public ChannelRenderInfo getRenderInfo(String traceName)
	{
		return (ChannelRenderInfo) fRenderInfo.get(traceName);
	}

	public ChannelRenderInfo[] getRenderInfos()
	{
		ChannelRenderInfo[] infos = new ChannelRenderInfo[fRenderInfo.size()];
		
		return (ChannelRenderInfo[]) fRenderInfo.values().toArray(infos);
	}
	
	public void clear()
	{
		fRenderInfo.clear();
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultChannelModel.java,v $
//  Revision 1.5  2006/03/21 22:03:13  tames_cvs
//  Changed setChannel to recognize a "|" deliminated list of channels.
//
//  Revision 1.4  2005/08/31 20:53:55  tames_cvs
//  Added a setChannel method as a temp fix so a channel can be added via
//  swiXml.
//
//  Revision 1.3  2005/08/12 21:18:49  tames_cvs
//  Added a clear method for removing current channels from the model.
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//