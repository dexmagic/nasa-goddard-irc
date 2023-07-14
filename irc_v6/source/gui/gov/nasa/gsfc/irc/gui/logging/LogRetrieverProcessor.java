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

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisSet;

/**
 * LogRetrieverProcessor processes the incoming basisSet, i.e., logs the message
 * stored in the basisSet.
 * 
 * <P> This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 30, 2005 1:38:37 PM
 * @author Peyush Jain
 */

public class LogRetrieverProcessor extends BasisSetProcessor
{
    private static final String CLASS_NAME = LogViewer.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
    public static final String DEFAULT_NAME = "Log Retriever Processor";


	/**
	 *  Constructs a new LogRetrieverProcessor having a default name.
	 * 
	 **/

	public LogRetrieverProcessor()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new LogRetrieverProcessor having the given base name.
	 * 
	 *  @param name The base name of the new LogRetrieverProcessor
	 **/

	public LogRetrieverProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new LogRetrieverProcessor configured according to the given 
	 *  ComponentDescriptor.
	 *
	 *  @param descriptor The ComponentDescriptor of the new LogRetrieverProcessor
	 */
	
	public LogRetrieverProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
    /**
     * @see gov.nasa.gsfc.irc.algorithms.BasisSetProcessor#processBasisSet(gov.nasa.gsfc.irc.data.BasisSet)
     */
    protected void processBasisSet(BasisSet basisSet)
    {
        //retrieve the log record from the basisSet
        LogRecord logRecord = (LogRecord)(basisSet.getDataBuffer(0).getAsObject(0));
        
        //log the record
        if(sLogger.isLoggable(logRecord.getLevel()))
        {
            sLogger.log(logRecord.getLevel(), logRecord.getMessage());
        }
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: LogRetrieverProcessor.java,v $
// Revision 1.3  2006/03/14 14:57:16  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.2  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.1  2005/09/01 17:31:09  pjain_cvs
// Adding to CVS.
//
//