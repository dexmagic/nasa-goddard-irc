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

package gov.nasa.gsfc.irc.ui;


/**
 * This class implements a simple progress display. Progress is always written
 * to standard out. If an instance of ProgressDisplay is running in a
 * non headless environment with a graphic context then the progress will
 * also be reflected in a graphic progress frame.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/22 05:00:06 $
 * @author 	Troy Ames
 */
public class ProgressDisplay
{
	private boolean fGraphicsContext = false;
	private Object fProgressFrame = null;

	/**
	 * Construct a new ProgressDisplay.
	 * 
	 * @param graphicsContext true if environment has a graphics context
	 */
	public ProgressDisplay(boolean graphicsContext)
	{

	}
	
	/**
	 * Set the percent complete for the progress display. 
	 * Note that this number is between 0.0 and 1.0.
	 * 
	 * @param percent the percent complete
	 */
	public void setPercentComplete(float percent)
	{
		if (fGraphicsContext)
		{
			if (percent < 0.0f)
			{
				percent = 0.0f;
			}
			else if (percent > 1.0f)
			{
				percent = 1.0f;
			}

		}
	}
		
	/**
	 * Sets the value of the progress string and writes it to stdout. 
	 * If this instance has a graphic context the status display is also updated.
	 * 
	 * @param text the progress string or null
	 */
	public void setString(String text)
	{
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: ProgressDisplay.java,v $
//	Revision 1.3  2004/12/22 05:00:06  tames
//	Comment change only.
//	
//	Revision 1.2  2004/12/22 04:42:19  tames
//	Layout changes as well as changing the default behavior
//	to scale the startup image.
//	
//	Revision 1.1  2004/08/31 22:03:30  tames
//	Initial Version
//	
