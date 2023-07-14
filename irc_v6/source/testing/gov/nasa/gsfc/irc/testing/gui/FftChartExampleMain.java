//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/testing/gov/nasa/gsfc/irc/testing/gui/FftChartExampleMain.java,v 1.10 2006/03/14 16:13:18 chostetter_cvs Exp $
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
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.algorithms.Algorithm;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;
import gov.nasa.gsfc.irc.library.algorithms.FrequencySpectrum;
import gov.nasa.gsfc.irc.library.algorithms.SignalGenerator;
import gov.nasa.gsfc.irc.library.processors.SignalGeneratorProcessor;


/**
 * A chart example.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/14 16:13:18 $
 * @author Troy Ames
 */

public class FftChartExampleMain
{
	public static void main(String[] args)
	{
		Irc.main(args);
		
		DataSpace dataSpace = Irc.getDataSpace();
		ComponentManager componentManager = Irc.getComponentManager();
				
		System.out.println("Configuring Algorithms...\n");
		
		Algorithm signalGenerator = new SignalGenerator();
		componentManager.add(signalGenerator);
		
		// Set initial properties
		SignalGeneratorProcessor processor = (SignalGeneratorProcessor) 
			signalGenerator.getProcessorBySequencedName
				(SignalGeneratorProcessor.DEFAULT_NAME);
		processor.setAmplitude(10.0);
		processor.setFrequency(60.0);
		
		Algorithm frequencySpectrum = new FrequencySpectrum();
		componentManager.add(frequencySpectrum);
		
		System.out.println("Connecting Frequency Spectrum to Signal Generator...\n");
		
		Input input = frequencySpectrum.getInputBySequencedName("Data Input");
		
		BasisBundleId basisBundleId = dataSpace.getBasisBundleId
			("Signals.Signals Output.Signal Generator.IRC");
		
		Amount requestAmount = new Amount();
		requestAmount.setAmount(1000);
		Set requestedBuffers = new HashSet();
		requestedBuffers.add(SignalGeneratorProcessor.SIN_BUFFER_NAME);
		requestedBuffers.add(SignalGeneratorProcessor.RANDOM_BUFFER_NAME);
		BasisRequest basisRequest = new BasisRequest(basisBundleId, 
			requestedBuffers, requestAmount);
		
		input.addBasisRequest(basisRequest);	
		
		// Build GUI
		String guiFile = "resources/xml/examples/FftChartExample.xml";
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
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: FftChartExampleMain.java,v $
//  Revision 1.10  2006/03/14 16:13:18  chostetter_cvs
//  Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
//  Revision 1.9  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.8  2006/02/01 23:03:05  tames
//  Removed references to deprecated BasisRequestAmount class.
//
//  Revision 1.7  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/11/15 23:14:20  chostetter_cvs
//  Fixed issue with correct mapping of input DataBuffers to output DataBuffers when input is filtered
//
//  Revision 1.5  2005/11/15 18:37:39  chostetter_cvs
//  Further FFT-related clean-up
//
//  Revision 1.4  2005/07/19 18:24:27  chostetter_cvs
//  Renamed certain processors, algorithms
//
//  Revision 1.3  2005/03/15 22:51:43  mn2g
//  compare the old fft algorithm along side the new ( caching ) one.
//
//  Revision 1.2  2005/03/15 17:19:32  chostetter_cvs
//  Made DataBuffer an interface, organized imports
//
//  Revision 1.1  2005/03/15 14:28:42  tames
//  Renamed.
//
//  Revision 1.1  2005/03/15 07:52:50  tames
//  FFT visualization test
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
