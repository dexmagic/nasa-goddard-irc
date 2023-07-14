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

package gov.nasa.gsfc.irc.gui.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

import gov.nasa.gsfc.irc.logging.BasisSetHandler;

/**
 *  Handler List Model populates the list with available handlers in
 *  Add Handler GUI.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Aug 22, 2005 1:55:42 PM
 *  @author Peyush Jain
 */

public class HandlerListModel extends DefaultListModel
{
    private static final String CLASS_NAME = HandlerListModel.class.getName();
    private final Logger sLogger = Logger.getLogger(CLASS_NAME);

    /**
     * Constructor
     */
    public HandlerListModel()
    {
        super();
        
        //add console handler
        addElement(new ConsoleHandler());
        
        //add file handler
        try
        {            
            addElement(new FileHandler());
        }
        catch (SecurityException se)
        {
            String msg = "Security Exception";
            sLogger.logp(Level.WARNING, CLASS_NAME, "readFromFile", msg, se);
        }
        catch (IOException ioe)
        {
            String msg = "Security Exception";
            sLogger.logp(Level.WARNING, CLASS_NAME, "readFromFile", msg, ioe);
        }
        
        //add basisSet handler
        addElement(new BasisSetHandler());
    }
    
}


//--- Development History  ---------------------------------------------------
//
//  $Log: HandlerListModel.java,v $
//  Revision 1.3  2006/02/01 23:01:51  tames
//  Changed import for relocated BasisSetHandler class.
//
//  Revision 1.2  2005/09/14 21:57:55  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/09/01 16:10:28  pjain_cvs
//  Adding to CVS.
//
//