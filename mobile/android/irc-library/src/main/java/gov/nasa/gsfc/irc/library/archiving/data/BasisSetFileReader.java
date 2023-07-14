//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.library.archiving.data;

import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;

import java.util.logging.Logger;

/**
 *  BasisSetFileReader adds processor and output to the algorithm.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Apr 19, 2005 1:28:10 PM
 *  @author Peyush Jain
 */

public class BasisSetFileReader extends DefaultAlgorithm
{
    private static final String CLASS_NAME = BasisSetFileReader.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
    public static final String DEFAULT_NAME = "BasisSet File Reader";
    
    private ArchiveSession[] fSessions;

    
    /**
     *  Default constructor of a BasisSetFileReader.
     *
     */    
    public BasisSetFileReader()
    {
        super(DEFAULT_NAME);
        init();
    }
    
    /**
     *  Constructs a new BasisSetFileReader having the given name. 
     *  
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
     *  @param name The name of the new BasisSetFileReader
     */    
    public BasisSetFileReader(String name)
    {
        super(name);
        
        init();
    }
    
    /**
     * Constructs a new BasisSetFileReader configured according to the given 
     * Descriptor.
     * 
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
     * @param descriptor A Descriptor describing the desired configuration 
     *      of the new BasisSetFileReader
     */    
    public BasisSetFileReader(ComponentDescriptor descriptor)
    {
        super(descriptor);

        init();    
    }

	/**
	 * 
	 */
	private void init()
	{
		//add output to the algorithm
        Output output = new DefaultOutput("Data");        
        this.addOutput(output);

        //add processor to the algorithm
        Processor processor = new BasisSetFileReadProcessor();
        this.addProcessor(processor);
        
        //add output to the processor
        processor.addOutput(output);
	}
    
    /**
     * @return Returns selected sessions to read.
     */
    public ArchiveSession[] getArchiveSessions()
    {
        return new ArchiveSession[0];
    }
    
    /**
     * @param sessions Sets the sessions to read.
     */
    public void setArchiveSessions(ArchiveSession[] sessions)
    {
        fSessions = sessions;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileReader.java,v $
//  Revision 1.7  2006/06/12 14:10:55  smaher_cvs
//  Made sure all constructors intialize processors, inputs, outputs, etc.
//
//  Revision 1.6  2006/03/14 16:13:18  chostetter_cvs
//  Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
//  Revision 1.5  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.4  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/10/14 13:54:23  pjain_cvs
//  Added get/set ArchiveSessions methods.
//
//  Revision 1.2  2005/06/15 18:43:38  pjain_cvs
//  Added BasisSetFileEditor
//
//  Revision 1.1  2005/05/02 15:09:00  pjain_cvs
//  Adding to CVS.
//
//