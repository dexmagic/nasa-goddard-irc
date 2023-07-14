//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/channel/ChannelRenderInfo.java,v 1.1 2004/12/16 23:01:15 tames Exp $
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
import java.awt.Stroke;

/**
 * This channel information class provides rendering information that one or
 * more Renderers can use. 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/12/16 23:01:15 $
 * @author	Troy Ames
 */
public class ChannelRenderInfo
{
	public static final Stroke DEFAULT_STROKE = new java.awt.BasicStroke(1.0f);

	//  Added properties.
	private String fName = null;
	private boolean fDiscrete = false;
	private boolean fSynthesized = false;
	private Color fColor = null;
	private Stroke fStroke = null;

	/**
	 *  Create an empty channel information object.
	 */
	public ChannelRenderInfo()
	{
	}


	/**
	 * Create a new channel information object from the given information.
	 *
	 * @param name  the name of the channel
	 * @param discrete  whether or not to treat this channel as discrete
	 * @param synthesized  whether or not the end points of this channel are
	 *                      synthesized
	 * @param color  the color used to display the channel
	 * @param stroke  the stroke used to display the channel
	 */
	public ChannelRenderInfo(String name, boolean discrete,
					   boolean synthesized, Color color, Stroke stroke)
	{
		setDiscrete(discrete);
		setSynthesized(synthesized);
		setColor(color);
		setStroke(stroke);
	}

	/**
	 * Set name of channel.
	 *
	 * @param name Name of channel.
	 */
	public void setName(String name)
	{
		fName = name;
	}
	
	/**
	 * Get channel name. 
	 *
	 * @return channel name.
	**/
	public String getName()
	{
		return fName;
	}
	
	/**
	 * Set whether or not the channel should be treated as discrete.
	 * Discrete values transition instantaneously from one value to the
	 * next, displaying vertical lines between these transitions. 
	 *
	 * @param discrete  whether or not to treat this channel as discrete
	 */
	public void setDiscrete(boolean discrete)
	{
		fDiscrete = discrete;
	}

	/**
	 * Determine if this channel is treated as discrete.
	 *
	 * @return  whether or not this channel is treated as discrete
	 */
	public boolean isDiscrete()
	{
		return fDiscrete;
	}

	/**
	 * Set whether or not the channel should have its end points synthesized. If
	 * so, the visualization will attempt to retrieve the last value of the
	 * Y-input that occured before the displayed interval. This value is used as
	 * the initial value for the Y-input within this interval. In addition, the
	 * last value found within the displayed interval will be extended as the
	 * final value of that interval. This feature is useful for slow moving,
	 * discrete inputs as it allows the user to view the current value of an
	 * input even if no data points for the Y-input exist within the currently
	 * displayed interval of history.
	 * 
	 * @param synthesized whether or not the end points of this channel are
	 *            synthesized
	 */
	public void setSynthesized(boolean synthesized)
	{
		fSynthesized = synthesized;
	}

	/**
	 * Determine if this channel should have its end points synthesized.
	 *
	 * @return  whether or not the end points of this channel are
	 *           synthesized
	 *
	 * @see #setSynthesized
	 */
	public boolean isSynthesized()
	{
		return fSynthesized;
	}

	/**
	 * Set the color of this channel.
	 *
	 * @param color  the color used to display the channel
	 */
	public void setColor(Color color)
	{
		fColor = color;
	}

	/**
	 * Get the color of this channel.
	 *
	 * @return  the color used to display the channel	
	 */
	public Color getColor()
	{
		return fColor;
	}

	/**
	 * Set the stroke used to display this channel.
	 *
	 * @param stroke  the stroke used to display the channel
	 */
	public void setStroke(Stroke stroke)
	{
		fStroke = stroke;
	}

	/**
	 * Get the stroke used to display this channel.
	 *
	 * @return  the stroke used to display the channel
	 */
	public Stroke getStroke()
	{
		return fStroke;
	}

	/**
	 * Provide a string representation of the channel information.
	 *
	 * @return  the string represntation
	 */
	public String toString()
	{
		String str = super.toString();

		//  TODO Add the 2d specific fields.
		return str;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ChannelRenderInfo.java,v $
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//
