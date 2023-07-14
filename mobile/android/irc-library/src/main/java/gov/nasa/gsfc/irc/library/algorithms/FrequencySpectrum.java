//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/algorithms/FrequencySpectrum.java,v 1.10 2006/03/14 16:13:18 chostetter_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: FrequencySpectrum.java,v $
// Revision 1.10  2006/03/14 16:13:18  chostetter_cvs
// Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
// Revision 1.9  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.8  2006/01/24 15:55:14  chostetter_cvs
// Changed default ComponentManager behavior, default is now none
//
// Revision 1.7  2006/01/23 17:59:54  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.6  2005/11/15 18:37:39  chostetter_cvs
// Further FFT-related clean-up
//
// Revision 1.5  2005/07/19 18:24:27  chostetter_cvs
// Renamed certain processors, algorithms
//
// Revision 1.2  2005/03/15 22:50:36  mn2g
// fft algorithm that uses fft code that caches it's twiddle factors
//
// Revision 1.1  2005/03/15 17:34:56  mn2g
// uses a different fft algorithm
//
// Revision 1.4  2004/07/22 22:15:11  chostetter_cvs
// Created Algorithm versions of library Processors
//
//
//--- Warning ----------------------------------------------------------------
//
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// *  Neither the author, their corporation, nor NASA is responsible for
//	any consequence of the use of this software.
// *  The origin of this software must not be misrepresented either by
//	explicit claim or by omission.
// *  Altered versions of this software must be plainly marked as such.
// *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.algorithms;

import java.util.logging.Logger;

import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.library.processors.FrequencySpectrumProcessor;


/**
 * A FrequencySpectrum produces a frequency spectrum for each 
 * uniformly-sampled BasisSet of each DataBuffer of each DataSet it 
 * receives from its single Input.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/14 16:13:18 $
 * @author Carl F. Hostetter
 */

public class FrequencySpectrum extends DefaultAlgorithm
{
	public static final String DEFAULT_NAME = "Frequency Spectrum";
	
	private static final String CLASS_NAME = FrequencySpectrum.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
		
	
	/**
	 *	Default constructor of a FrequencySpectrum.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 */
	
	public FrequencySpectrum()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new FrequencySpectrum having the given name.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 *	@param name The name of the new FrequencySpectrum
	 */
	
	public FrequencySpectrum(String name)
	{
		super(name);
		
		Input input = new DefaultInput("Data Input");
		addInput(input);
		
		Processor processor = new FrequencySpectrumProcessor();
		addProcessor(processor);
		
		input.addInputListener(processor);

		Output output = new DefaultOutput("Frequency Spectrum Output");
		addOutput(output);
		
		processor.addOutput(output);
	}
}

