//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/testing/gov/nasa/gsfc/irc/testing/algorithms/AlgorithmTest.java,v 1.37 2006/03/02 22:21:35 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AlgorithmTest.java,v $
//  Revision 1.37  2006/03/02 22:21:35  chostetter_cvs
//  Organized imports
//
//  Revision 1.36  2006/03/02 22:20:40  chostetter_cvs
//  Fixed bug in NamespaceMember name sequencing
//
//  Revision 1.35  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.34  2005/03/15 00:36:03  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.33  2004/09/08 18:02:56  smaher_cvs
//  Moved testing/source back to source/testing and testing/resources to resources/testing
//
//  Revision 1.1  2004/09/08 14:56:09  smaher_cvs
//  Moved these files from source/testing to testing/source.  This was so we could have a clean spot to put test-related XML files (testing/resources)
//
//  Revision 1.31  2004/07/28 22:14:29  chostetter_cvs
//  Fixed data copying bug (needed to flip Buffer)
//
//  Revision 1.30  2004/07/24 02:46:11  chostetter_cvs
//  Added statistics calculations to DataBuffers, renamed some classes
//
//  Revision 1.29  2004/07/22 22:28:10  chostetter_cvs
//  Tweaks
//
//  Revision 1.28  2004/07/22 22:15:11  chostetter_cvs
//  Created Algorithm versions of library Processors
//
//  Revision 1.27  2004/07/22 21:29:47  chostetter_cvs
//  BasisBundle name, access by name changes
//
//  Revision 1.26  2004/07/22 20:14:58  chostetter_cvs
//  Data, Component namespace work
//
//  Revision 1.25  2004/07/22 16:28:03  chostetter_cvs
//  Various tweaks
//
//  Revision 1.24  2004/07/21 14:26:15  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.23  2004/07/20 02:37:35  chostetter_cvs
//  More real-valued boundary condition work
//
//  Revision 1.22  2004/07/19 23:47:50  chostetter_cvs
//  Real-valued boundary conidition work
//
//  Revision 1.21  2004/07/19 19:00:38  chostetter_cvs
//  Implemented requests by basis amount
//
//  Revision 1.20  2004/07/19 14:16:14  chostetter_cvs
//  Added ability to subsample data in requests
//
//  Revision 1.19  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.18  2004/07/17 01:25:58  chostetter_cvs
//  Refactored test algorithms
//
//  Revision 1.17  2004/07/16 04:51:20  chostetter_cvs
//  Tweaks
//
//  Revision 1.16  2004/07/16 00:23:20  chostetter_cvs
//  Refactoring of DataSpace, Output wrt BasisBundle collections
//
//  Revision 1.15  2004/07/15 21:33:35  chostetter_cvs
//  More testing tweaks
//
//  Revision 1.14  2004/07/15 19:07:47  chostetter_cvs
//  Added ability to block while waiting for a BasisBundle to have listeners
//
//  Revision 1.13  2004/07/15 17:48:55  chostetter_cvs
//  ComponentManager, property change work
//
//  Revision 1.12  2004/07/15 04:26:43  chostetter_cvs
//  Debugged initial skip, boundary condition on free
//
//  Revision 1.11  2004/07/14 22:24:53  chostetter_cvs
//  More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//
//  Revision 1.10  2004/07/14 14:36:40  chostetter_cvs
//  More tweaks
//
//  Revision 1.9  2004/07/14 00:33:49  chostetter_cvs
//  More Algorithm, data testing. Fixed slice bug.
//
//  Revision 1.8  2004/07/13 18:52:50  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.7  2004/07/12 19:09:16  chostetter_cvs
//  Tweak
//
//  Revision 1.6  2004/07/12 19:04:45  chostetter_cvs
//  Added ability to find BasisBundleId, Components by their fully-qualified name
//
//  Revision 1.5  2004/07/12 14:31:12  chostetter_cvs
//  TerminalPlot tweaks
//
//  Revision 1.4  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/07/12 13:59:49  chostetter_cvs
//  Renamed AsciiPlotter to TerminalPlotter
//
//  Revision 1.2  2004/07/12 13:49:16  chostetter_cvs
//  More Algorithm testing
//
//  Revision 1.1  2004/07/11 21:39:03  chostetter_cvs
//  AlgorithmTest tweaks
//
//  Revision 1.12  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.11  2004/07/11 21:19:44  chostetter_cvs
//  More Algorithm work
//
//  Revision 1.10  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.9  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.8  2004/07/09 23:47:30  chostetter_cvs
//  More Input/Output testing
//
//  Revision 1.7  2004/07/09 22:29:11  chostetter_cvs
//  Extensive testing of Input/Output interaction, supports simple BasisRequests
//
//  Revision 1.6  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.5  2004/07/02 02:33:30  chostetter_cvs
//  Renamed DataRequest to BasisRequest
//
//  Revision 1.4  2004/06/30 20:56:20  chostetter_cvs
//  BasisSet is now an interface
//
//  Revision 1.3  2004/06/30 17:33:54  chostetter_cvs
//  Output tweaks
//
//  Revision 1.2  2004/06/29 22:46:13  chostetter_cvs
//  Fixed broken CVS-generated comments. Grrr.
//
//  Revision 1.1  2004/06/29 22:39:39  chostetter_cvs
//  Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
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

package gov.nasa.gsfc.irc.testing.algorithms;

import gov.nasa.gsfc.irc.algorithms.Algorithm;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisRequestAmount;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.library.algorithms.FrequencySpectrum;
import gov.nasa.gsfc.irc.library.algorithms.SignalGenerator;
import gov.nasa.gsfc.irc.library.algorithms.TerminalPlotter;
import gov.nasa.gsfc.irc.library.processors.SignalGeneratorProcessor;


/**
 * An AlgorithmTest performs a test of IRC Algorithms.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/02 22:21:35 $
 * @author Carl F. Hostetter
 */

public class AlgorithmTest
{
	public static void main(String[] args)
	{
		Irc.main(args);
		
		AlgorithmTest tester = new AlgorithmTest();
		
		System.out.println("Configuring Algorithms...\n");
		
		Algorithm signalGenerator = new SignalGenerator();

		Algorithm frequencySpectrum = new FrequencySpectrum();
		
		Algorithm plotter = new TerminalPlotter();
		
		System.out.println("Connecting Frequency Spectrum to Signal Generator...\n");
		
		Input input = frequencySpectrum.getInputBySequencedName("Input");

		DataSpace dataSpace = Irc.getDataSpace();
		BasisBundleId basisBundleId = dataSpace.getBasisBundleId
			("Signals.Signals Output.Signal Generator.IRC");
		
		BasisRequestAmount requestAmount = new BasisRequestAmount();
		requestAmount.setAmount(10000);
		BasisRequest basisRequest = new BasisRequest(basisBundleId, 
			SignalGeneratorProcessor.SIN_BUFFER_NAME, requestAmount);
		
		input.addBasisRequest(basisRequest);	
		
		input.setRepetition(1);
		
		System.out.println(input + "\n");
		
		System.out.println("Connecting Terminal Plotter to Frequency Spectrum...\n");
		
		input = plotter.getInputBySequencedName("Input");

		basisBundleId = dataSpace.getBasisBundleId
			("Frequency Spectra of Signals.Frequency Spectrum Output.Frequency Spectrum.IRC");
		
		basisRequest = new BasisRequest(basisBundleId);
		
		input.addBasisRequest(basisRequest);
		input.setDownsamplingRate(100);
		
		System.out.println(input + "\n");
		
		System.out.println("Starting Algorithms...\n");
		
		Irc.getComponentManager().startAllComponents();
	}
}
