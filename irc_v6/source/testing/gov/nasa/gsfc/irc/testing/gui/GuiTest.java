//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/testing/gov/nasa/gsfc/irc/testing/gui/GuiTest.java,v 1.6 2005/01/10 23:11:21 tames_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: GuiTest.java,v $
//  Revision 1.6  2005/01/10 23:11:21  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.5  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.4  2004/09/08 18:02:56  smaher_cvs
//  Moved testing/source back to source/testing and testing/resources to resources/testing
//
//  Revision 1.1  2004/09/08 14:56:09  smaher_cvs
//  Moved these files from source/testing to testing/source.  This was so we could have a clean spot to put test-related XML files (testing/resources)
//
//  Revision 1.2  2004/08/26 14:37:00  tames
//  *** empty log message ***
//
//  Revision 1.1  2004/08/23 14:02:19  tames
//  initial version
//
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

package gov.nasa.gsfc.irc.testing.gui;

import java.net.URL;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.GuiFactory;


/**
 * An AlgorithmTest performs a test of IRC Algorithms.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/01/10 23:11:21 $
 * @author Carl F. Hostetter
 */

public class GuiTest
{
	public static void main(String[] args)
	{
		Irc.main(args);

		System.out.println("Configuring GUI...\n");
		//String guiFile = "resources/xml/examples/gui.xml";
		String guiFile = "resources/xml/core/gui/AboutFrame.xml";
		//String guiFile = "resources/xml/examples/Chart.xml";
		//String guiFile = "resources/xml/examples/ComponentManagerGUI.xml";

		URL url = Sys.getResourceManager().getResource(guiFile);

//		JFreeChart chart = new JFreeChart;
//		ChartPanel panel = null;
//		TimeSeriesChart time = null;
			
		if (url != null)
		{
			GuiFactory builder = Irc.getGuiFactory();

			try
			{
				builder.render(url).setVisible(true);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Could not find file:" + guiFile);
		}

		System.out.println("GUI setup completed...\n");
	}
}