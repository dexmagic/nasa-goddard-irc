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




/**
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/08/12 21:18:49 $
 * @author 	Troy Ames
 */
public interface ChannelModel
{
	
	public void addChannel(String channelName);
	public Object[] getChannelNames();
	
	public ChannelRenderInfo getRenderInfo(String channelName);
	public ChannelRenderInfo[] getRenderInfos();
	public void clear();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ChannelModel.java,v $
//  Revision 1.2  2005/08/12 21:18:49  tames_cvs
//  Added a clear method for removing current channels from the model.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//