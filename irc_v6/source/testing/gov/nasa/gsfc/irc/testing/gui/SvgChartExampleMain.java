//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/testing/gov/nasa/gsfc/irc/testing/gui/SvgChartExampleMain.java,v 1.4 2006/01/23 17:59:55 chostetter_cvs Exp $
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

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.algorithms.Algorithm;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.library.algorithms.SignalGenerator;
import gov.nasa.gsfc.irc.library.processors.SignalGeneratorProcessor;

/**
 * A SVG chart example.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:55 $
 * @author Troy Ames
 */

public class SvgChartExampleMain
{
	public static void main(String[] args)
	{
		Irc.main(args);
				
		System.out.println("Configuring Algorithms...\n");		
		Algorithm signalGenerator = new SignalGenerator();
		
		// Set initial properties
		SignalGeneratorProcessor processor = (SignalGeneratorProcessor) 
			signalGenerator.getComponentBySequencedName("Signal Generator Processor");
		processor.setAmplitude(100.0);
		processor.setCyclesPerBasisSet(5);
		processor.setFrequency(60.0);
		processor.setSampleRate(1000);
		processor.setOffset(100);
			
		Irc.getComponentManager().addComponent(signalGenerator);
				
		// Build GUI
		String guiFile = "resources/xml/examples/SvgDynamicExample.xml";
		URL url = Sys.getResourceManager().getResource(guiFile);

		if (url != null)
		{
			try
			{
				Container frame = Irc.getGuiFactory().render(url);
				((JFrame) frame).pack();
				//WindowUtil.centerFrame(frame);
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
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: SvgChartExampleMain.java,v $
//  Revision 1.4  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2006/01/13 03:30:55  tames
//  Removed unused test code.
//
//  Revision 1.2  2005/11/07 22:23:36  tames
//  Modified signal generator properties.
//
//  Revision 1.1  2005/10/26 14:02:53  tames
//  Initial version
//
//  Revision 1.4  2005/09/22 18:55:05  tames
//  Misc changes to support testing.
//
//  Revision 1.3  2005/09/14 21:57:55  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2005/09/09 22:44:16  tames
//  *** empty log message ***
//
//  Revision 1.1  2005/08/26 22:17:31  tames_cvs
//  Initial implementation.
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
