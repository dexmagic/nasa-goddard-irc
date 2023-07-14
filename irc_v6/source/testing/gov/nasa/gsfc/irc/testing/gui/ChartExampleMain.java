//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/testing/gov/nasa/gsfc/irc/testing/gui/ChartExampleMain.java,v 1.14 2006/03/15 20:41:40 tames_cvs Exp $
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

package gov.nasa.gsfc.irc.testing.gui;

import java.awt.Container;
import java.net.URL;

import javax.swing.JFrame;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.algorithms.Algorithm;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;
import gov.nasa.gsfc.irc.library.algorithms.SignalGenerator;
import gov.nasa.gsfc.irc.library.processors.SignalGeneratorProcessor;


/**
 * A chart example.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/15 20:41:40 $
 * @author Troy Ames
 */

public class ChartExampleMain
{
	public static void main(String[] args)
	{
		Irc.main(args);
		DataSpace dataSpace = Irc.getDataSpace();
				
		System.out.println("Configuring Algorithms...\n");
		
		Algorithm signalGenerator = new SignalGenerator();
		
		// Set initial properties
		SignalGeneratorProcessor processor = (SignalGeneratorProcessor) 
			signalGenerator.getComponentBySequencedName
				(SignalGeneratorProcessor.DEFAULT_NAME);
		
		processor.setAmplitude(100.0);
		processor.setFrequency(10);
		processor.setCyclesPerBasisSet(2.2);
			
		Irc.getComponentManager().add(signalGenerator);
		
		// Build GUI
		String guiFile = "resources/xml/examples/ChartExample.xml";
		URL url = Sys.getResourceManager().getResource(guiFile);

		if (url != null)
		{
			try
			{
				Container frame = Irc.getGuiFactory().render(url);
				((JFrame) frame).pack();
				WindowUtil.centerFrame(frame);
				frame.setVisible(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Could not find file:" + guiFile);
		}
		
		System.out.println("Starting Algorithms...\n");
		
		Irc.getComponentManager().startAllComponents();

		System.out.println(Irc.getComponentManager().toString());
		
//		Startable component = (Startable) 
//			Irc.getComponentManager().getComponent("Anonymous.HAWC");
//		try
//		{
//			Thread.sleep(10000);
//			component.stop();
//			Thread.sleep(10000);
//			component.start();
//		}
//		catch (InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ChartExampleMain.java,v $
//  Revision 1.14  2006/03/15 20:41:40  tames_cvs
//  Changed main to add algorithm to the ComponentManager.
//
//  Revision 1.7  2006/01/25 17:03:11  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/10/11 03:36:53  tames
//  Updated to reflect new AxisScrollBar and history.
//
//  Revision 1.4  2005/01/10 23:11:21  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.3  2004/12/14 21:48:15  tames
//  Updates for new tests.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/08 23:14:55  tames
//  Initial Version
//
//  Revision 1.4  2004/09/08 18:02:56  smaher_cvs
//  Moved testing/source back to source/testing and testing/resources to resources/testing
//
//  Revision 1.1  2004/09/08 14:56:09  smaher_cvs
//  Moved these files from source/testing to testing/source.  This was so we could have a clean spot to put test-related XML files (testing/resources)
//
//  Revision 1.2  2004/08/26 14:36:49  tames
//  *** empty log message ***
//
//  Revision 1.1  2004/08/23 14:02:19  tames
//  initial version
//
