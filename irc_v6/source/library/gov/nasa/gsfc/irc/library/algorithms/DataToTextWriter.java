//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/algorithms/DataToTextWriter.java,v 1.5 2006/03/14 16:13:18 chostetter_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: DataToTextWriter.java,v $
// Revision 1.5  2006/03/14 16:13:18  chostetter_cvs
// Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
// Revision 1.4  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.3  2006/01/24 15:55:14  chostetter_cvs
// Changed default ComponentManager behavior, default is now none
//
// Revision 1.2  2006/01/23 17:59:54  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.1  2004/07/24 02:46:11  chostetter_cvs
// Added statistics calculations to DataBuffers, renamed some classes
//
// Revision 1.6  2004/07/22 22:15:11  chostetter_cvs
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
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.library.processors.DataToTextWriterProcessor;


/**
 * A DataToTextWriter prints out a specified number of values from each 
 * DataBuffer of each BasisSet it receives through its single Input.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/14 16:13:18 $
 * @author Carl F. Hostetter
 */

public class DataToTextWriter extends DefaultAlgorithm
{
	public static final String DEFAULT_NAME = "Data to Text Writer";
	
	private static final String CLASS_NAME = DataToTextWriter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
		
	
	/**
	 *	Default constructor of a DataToTextWriter.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 */
	
	public DataToTextWriter()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new DataToTextWriter having the given name.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 *	@param name The name of the new DataToTextWriter
	 */
	
	public DataToTextWriter(String name)
	{
		this(name, 10);
	}
	
	
	/**
	 *	Constructs a new DataToTextWriter having the given name and 
	 *  printing the given number of sampels from each DataBuffer.
	 *
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
	 *	@param name The name of the new DataToTextWriter
	 *  @param numSamples The number of samples of each DataBuffer that will be 
	 * 		printed
	 */
	
	public DataToTextWriter(String name, int numSamples)
	{
		super(name);
		
		Input input = new DefaultInput();
		this.addInput(input);
		
		Processor processor = new DataToTextWriterProcessor(10);
		
		this.addProcessor(processor);
		input.addInputListener(processor);
	}
}

