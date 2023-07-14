//=== File Prolog ============================================================
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *   Neither the author, their corporation, nor NASA is responsible for
//      any consequence of the use of this software.
//  *   The origin of this software must not be misrepresented either by
//      explicit claim or by omission.
//  *   Altered versions of this software must be plainly marked as such.
//  *   This notice may not be removed or altered.
//
//=== End File Prolog ========================================================
package gov.nasa.gsfc.irc.library.algorithms;

import java.util.Iterator;

import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.library.processors.CoaddProcessor;
import gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy;


/**
 * Generic Coadder that will average a certain set of samples within each
 * data buffer. It is only compatible with single Basis Bundle In other words,
 * it cannot support coadding basis requests from multiple basis bundles. Use
 * separate coadders for each basis bundle.
 * 
 * The Coadder uses a pluggable "strategy" pattern to define how many samples
 * constitute a coadd. 
 * 
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/06/27 22:40:35 $
 * @author Steve Maher
 * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.CoadderStrategy
 */
public class Coadder extends DefaultAlgorithm
{
	public static final String DEFAULT_NAME = "Coadder";
	
	public Coadder()
	{
		this(DEFAULT_NAME);
	}

	
	public Coadder(String name)
	{
		super(name);

		Input input = new DefaultInput();
		addInput(input);

		CoaddProcessor processor = new CoaddProcessor(); 
		addProcessor(processor);
		
		input.addInputListener(processor);
		
		Output output = new DefaultOutput();
		addOutput(output);
		
		processor.addOutput(output);
	}
	
	public void buildOutputDescriptor(BasisBundleDescriptor basisBundleDescriptor)
	{
	    for (Iterator iter = this.getProcessors().iterator(); iter.hasNext();)
        {
            CoaddProcessor processor = (CoaddProcessor) iter.next();
            processor.buildOutputDescriptor(basisBundleDescriptor);
        }
	}
	
	/**
	 * Set the strategy
	 * @param strategy
	 */
	public void setStrategy(CoadderStrategy strategy)
	{
		for (Iterator iter = this.getProcessors().iterator(); iter.hasNext();)
		{
			CoaddProcessor processor = (CoaddProcessor) iter.next();
			processor.setStrategy(strategy);
		}
	}
	
	/**
	 * Get the strategy
	 * @return
	 */
	public CoadderStrategy getStrategy()
	{
		CoadderStrategy strategy = null;
		for (Iterator iter = this.getProcessors().iterator(); iter.hasNext();)
		{
			// just get the one from the first processor because
			// they'll all be the same
			CoaddProcessor processor = (CoaddProcessor) iter.next();
			strategy = processor.getStrategy();
			break;
		}		
		return strategy;
	}
    
    public void setOuputBasisSetSize(int size)
    {
        for (Iterator iter = this.getProcessors().iterator(); iter.hasNext();)
        {
            CoaddProcessor processor = (CoaddProcessor) iter.next();
            processor.setOutputBasisSetSize(size);
        }
    }
    
    public int getOuputBasisSetSize()
    {
        int size = -1;
        for (Iterator iter = this.getProcessors().iterator(); iter.hasNext();)
        {
            // just get the one from the first processor because
            // they'll all be the same
            CoaddProcessor processor = (CoaddProcessor) iter.next();
            size = processor.getOutputBasisSetSize();
            break;
        }       
        return size;        
    }
}
//--- Development History  ---------------------------------------------------
//
//$Log: Coadder.java,v $
//Revision 1.11  2006/06/27 22:40:35  smaher_cvs
//Major rework for performance; use constant basis set size and cache data buffer references.
//
//Revision 1.10  2006/05/10 17:29:11  smaher_cvs
//Changed coadd index array to be a primitive array; fixed some bugs; have a working chop coadder with this checkin.
//
//Revision 1.9  2006/03/14 16:13:18  chostetter_cvs
//Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
//Revision 1.8  2006/03/14 14:57:15  chostetter_cvs
//Simplified Namespace, Manager, Component-related constructors
//
//Revision 1.7  2006/01/24 15:55:14  chostetter_cvs
//Changed default ComponentManager behavior, default is now none
//
//Revision 1.6  2006/01/23 17:59:54  chostetter_cvs
//Massive Namespace-related changes
//
//Revision 1.5  2005/10/07 21:08:30  smaher_cvs
//Finished testing IntraBasisSetDataThresholdStrategy
//
//Revision 1.4  2005/09/23 20:56:45  smaher_cvs
//Implemented and tested new Coadder with strategy pattern
//
//Revision 1.3  2005/09/16 20:41:52  smaher_cvs
//Checkpointing Coadder performance improvements.
//
//Revision 1.2  2005/07/22 13:36:55  smaher_cvs
//Comments.
//
