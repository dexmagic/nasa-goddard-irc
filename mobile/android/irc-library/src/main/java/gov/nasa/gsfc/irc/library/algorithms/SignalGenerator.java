//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/algorithms/SignalGenerator.java,v 1.26 2006/03/14 16:13:18 chostetter_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: SignalGenerator.java,v $
// Revision 1.26  2006/03/14 16:13:18  chostetter_cvs
// Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
// Revision 1.25  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.24  2006/01/24 15:55:14  chostetter_cvs
// Changed default ComponentManager behavior, default is now none
//
// Revision 1.23  2006/01/23 17:59:54  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.22  2004/07/22 22:15:11  chostetter_cvs
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
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.library.processors.SignalGeneratorProcessor;


/**
 * A SignalGenerator produces a set of time-varying signals from its 
 * single Output.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/14 16:13:18 $
 * @author Carl F. Hostetter
 */

public class SignalGenerator extends DefaultAlgorithm
{
	private static final String CLASS_NAME = SignalGenerator.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Signal Generator";
	
	/**
	 *	Default constructor of a SignalGenerator.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 */
	
	public SignalGenerator()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new SignalGenerator having the given name.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 *	@param name The name of the new SignalGenerator
	 */
	
	public SignalGenerator(String name)
	{
		this(name, 60, 1, 1000, 60);
	}
	
	
	/**
	 *	Constructs a new SignalGenerator having the given name and set of signal 
	 *  parameters.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 * @param name The name of the new SignalGenerator
	 * @param frequency The desired signal frequency
	 * @param amplitude The desired signal amplitude
	 * @param samplesPerSecond The desired sample rate in samples per 
	 * 		second
	 * @param cyclesPerBasisSet The number of signal cycles to include in 
	 * 		each output BasisSet
	 */
	
	public SignalGenerator(String name, 
		double frequency, double amplitude, double samplesPerSecond, 
		double cyclesPerBasisSet)
	{
		super(name);
		
		Processor processor = new SignalGeneratorProcessor
			(frequency, amplitude, samplesPerSecond, cyclesPerBasisSet);
		
		Output output = new DefaultOutput("Signals Output");
		
		addOutput(output);
		addProcessor(processor);
		
		processor.addOutput(output);
	}
}

