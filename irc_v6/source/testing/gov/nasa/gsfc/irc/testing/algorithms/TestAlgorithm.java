//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/testing/gov/nasa/gsfc/irc/testing/algorithms/TestAlgorithm.java,v 1.5 2006/03/14 14:57:16 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Initial version
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

import java.awt.Container;
import java.net.URL;

import javax.swing.JFrame;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.algorithms.Algorithm;
import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisRequestAmount;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;
import gov.nasa.gsfc.irc.library.algorithms.SignalGenerator;


/**
 * A TestAlgorithm performs a test of IRC Algorithms.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/14 14:57:16 $
 * @author Carl F. Hostetter
 */

public class TestAlgorithm extends DefaultAlgorithm
{
	public class TestProcessor extends BasisSetProcessor
	{
		public TestProcessor()
		{
			super("Test Processor");
		}
		
		public TestProcessor(String name)
		{
			super(name);
		}
		
		public void processBasisSet(BasisSet data)
		{
			System.out.println(getName() + " received BasisSet:\n" + data);
		}
	}
	
	public TestAlgorithm(String name)
	{
		super(name);
		
		DataSpace dataSpace = Irc.getDataSpace();
		BasisBundleId basisBundleId = dataSpace.getBasisBundleId
			("Signals.Signals Output.Signal Generator.IRC");
		BasisRequestAmount requestAmount = new BasisRequestAmount();
		requestAmount.setAmount(1000);
		BasisRequest basisRequest = new BasisRequest(basisBundleId, requestAmount);
		
		Input input = new DefaultInput();
		input.addBasisRequest(basisRequest);
		addInput(input);
		
		Processor processor1 = new TestProcessor();
		input.addInputListener(processor1);
		addProcessor(processor1);
		
		Processor processor2 = new TestProcessor();
		input.addInputListener(processor2);
		addProcessor(processor2);
	}


	public static void main(String[] args)
	{
		Irc.main(args);
		
		System.out.println("Configuring Algorithms...");
		
		Algorithm signalGenerator = new SignalGenerator();
		Irc.getComponentManager().addComponent(signalGenerator);
		
		Algorithm testAlgorithm = new TestAlgorithm("Test Algorithm");
		Irc.getComponentManager().addComponent(testAlgorithm);

		System.out.println("Building GUI...");
		
		String guiFile = "resources/xml/examples/TestAlgorithm.xml";
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
		
		System.out.println("Starting Algorithms...");
		
		Irc.getComponentManager().startAllComponents();
	}
}
