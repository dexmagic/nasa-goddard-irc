package gov.nasa.gsfc.irc.gui.browser;
//import gov.nasa.gsfc.irc.gui.components.browser.algorithms.*;
import gov.nasa.gsfc.irc.algorithms.Algorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.components.DefaultComponentFactory;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisRequestAmount;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.library.algorithms.FrequencySpectrum;
import gov.nasa.gsfc.irc.library.algorithms.SignalGenerator;
import gov.nasa.gsfc.irc.library.algorithms.TerminalPlotter;
import gov.nasa.gsfc.irc.library.processors.SignalGeneratorProcessor;
import gov.nasa.gsfc.irc.testing.algorithms.AlgorithmTest;

/*
 * Created on Jun 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author hhollis
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ContainerTest {
	private ComponentManager c;
	private ComponentManager d;
	private static Algorithm fSignalGenerator;
	private static Algorithm fTextDataViewer;
	private static Algorithm fPlotter;
	
	public ContainerTest(){

		//Irc.main(null);
		setupAlgorithms(null);
	
		DefaultComponentFactory dcf = new DefaultComponentFactory();
		
		//Algorithm algorithm1 = new BigAlgorithm();
		//Irc.getComponentManager().addComponent(algorithm1);

	}
	
	private static Algorithm configureSignalGenerator(String name)
	{
		System.out.println("Configuring SignalGenerator Algorithm...");
		
		DefaultAlgorithm algorithm = new DefaultAlgorithm(name); 
			
		Processor processor = new SignalGeneratorProcessor(60, 1, 1000, 60);
		
		Output output = new DefaultOutput("Signals Output");
		
		algorithm.addOutput(output);
		algorithm.addProcessor(processor);
		processor.addOutput(output);
		
		Irc.getComponentManager().addComponent(algorithm);
		
		return (algorithm);
	}

	public void setupAlgorithms(String[] args)
	{		
		//Irc.main(args);
		
		AlgorithmTest tester = new AlgorithmTest();
		
		System.out.println("Configuring Algorithms...\n");
		
		Algorithm signalGenerator = new SignalGenerator();

		Algorithm frequencySpectrum = new FrequencySpectrum();
		
		Algorithm plotter = new TerminalPlotter();
		
		System.out.println("Connecting Frequency Spectrum to Signal Generator...\n");
		
		Input input = frequencySpectrum.getInputBySequencedName("Input");

		DataSpace dataSpace = Irc.getDataSpace();
		BasisBundleId basisBundleId = dataSpace.getBasisBundleId
			("Signals from Output Signals of Algorithm Signal Generator");
		
		BasisRequestAmount requestInterval = new BasisRequestAmount();
		requestInterval.setAmount(4096);
		BasisRequest basisRequest = new BasisRequest(basisBundleId, 
			SignalGeneratorProcessor.SIN_BUFFER_NAME, requestInterval);
		
		input.addBasisRequest(basisRequest);	
		
		input.setRepetition(1);
		
		System.out.println(input + "\n");
		
		System.out.println("Connecting Terminal Plotter to Frequency Spectrum...\n");
		
		input = plotter.getInput("Input");

		basisBundleId = dataSpace.getBasisBundleId
			("Frequency Spectra of Signals from Output Frequency Spectra of Algorithm Frequency Spectrum");
		
		basisRequest = new BasisRequest(basisBundleId);
		
		input.addBasisRequest(basisRequest);
		input.setDownsamplingRate(10);
		
		System.out.println(input + "\n");
		
		System.out.println("Starting Algorithms...\n");
		
		//Irc.getComponentManager().startAllComponents();
	}
	/**
	 * @return Returns the c.
	 */
	public ComponentManager getC() {
		return c;
	}
	/**
	 * @param c The c to set.
	 */
	public void setC(ComponentManager c) {
		this.c = c;
	}
	/**
	 * @return Returns the d.
	 */
	public ComponentManager getD() {
		return d;
	}
	/**
	 * @param d The d to set.
	 */
	public void setD(ComponentManager d) {
		this.d = d;
	}
}

