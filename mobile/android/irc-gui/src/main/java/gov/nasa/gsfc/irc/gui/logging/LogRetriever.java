// === File Prolog ============================================================
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.logging;

import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisRequestAmount;
import gov.nasa.gsfc.irc.data.DataSpace;

/**
 * LogRetriever adds input and the processor to the algorithm.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 30, 2005 1:36:21 PM
 * @author Peyush Jain
 */

public class LogRetriever extends DefaultAlgorithm
{
    /**
     * Default constructor of a LogRetriever.
     */
    public LogRetriever()
    {
        this("LogRetriever");
    }

    /**
     * Constructs a new log retriever having the given name.
     * 
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
     * @param name The name of the new log retriever
     */
    public LogRetriever(String name)
    {
        super(name);
    }

    /**
     * Constructs a new log retriever configured according to the given Descriptor.
     * 
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
     * @param descriptor A Descriptor describing the desired configuration of
     * 		the new log retriever
     */
    public LogRetriever(ComponentDescriptor descriptor)
    {
        super(descriptor);
        
        DataSpace dataSpace = Irc.getDataSpace();

        // add input to the algorithm
        Input input = new DefaultInput("Input");
        this.addInput(input);

        // add processor to the algorithm
        Processor processor = new LogRetrieverProcessor("Processor");
        this.addProcessor(processor);

        // add processor as the listener to input
        input.addInputListener(processor);

        // input
        BasisBundleId basisBundleId1 = 
        		dataSpace.getBasisBundleId("LogData from Log Handler");

        BasisRequestAmount requestAmount1 = new BasisRequestAmount();
        requestAmount1.setAmount(1);
        BasisRequest basisRequest1 = new BasisRequest(basisBundleId1,
                requestAmount1);
        input.addBasisRequest(basisRequest1);
     }
}

// --- Development History ---------------------------------------------------
//
// $Log: LogRetriever.java,v $
// Revision 1.4  2006/03/14 16:13:18  chostetter_cvs
// Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
// Revision 1.3  2006/03/14 14:57:16  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.2  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.1  2005/09/01 17:30:56  pjain_cvs
// Adding to CVS.
//
//