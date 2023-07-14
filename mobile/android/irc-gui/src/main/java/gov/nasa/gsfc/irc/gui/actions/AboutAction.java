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

package gov.nasa.gsfc.irc.gui.actions;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;

/**
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/10 23:09:23 $
 * @author 	Troy Ames
 */
public class AboutAction extends AbstractAction
{
	private static final String CLASS_NAME = AboutAction.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	private String fAboutFile = "resources/xml/core/gui/AboutFrame.xml";
	private URL fUrl = Sys.getResourceManager().getResource(fAboutFile);

	/**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
	 */
	public AboutAction()
	{
		super();
	}

    /**
     * Invoked when an About action occurs.
     */
	public void actionPerformed(ActionEvent event)
	{
		System.out.println("about called with " + event.toString());
		// Build GUI

		if (fUrl != null)
		{
			try
			{
				Container frame = Irc.getGuiFactory().render(fUrl);
				WindowUtil.centerFrame(frame);
				frame.setVisible(true);
			}
			catch (Exception e)
			{
				String message = "Exception performing about action: " 
					+ e.getLocalizedMessage();

				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"actionPerformed", message, e);
			}
		}
		else
		{
			System.out.println("Could not find About file:" + fAboutFile);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AboutAction.java,v $
//  Revision 1.3  2005/01/10 23:09:23  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/08/29 19:08:36  tames
//  Initial version
//
