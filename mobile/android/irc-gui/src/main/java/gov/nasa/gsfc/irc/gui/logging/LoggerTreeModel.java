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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.tree.DefaultTreeModel;

/**
 * LoggerTreeModel creates a visual tree based on logging namespace
 * 
 * <P> This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 11, 2005 3:18:43 PM
 * @author Peyush Jain
 */

public class LoggerTreeModel extends DefaultTreeModel
{
    private static final Logger logger1 = Logger.getLogger("gov.nasa.gsfc.irclib");
    private static final Logger logger2 = Logger.getLogger("gov.nasa.gsfc.commons");
    private static final Logger logger3 = Logger.getLogger("gov.nasa.gsfc.irc");

    private LoggerNode fRoot;

    /**
     * Default Constructor
     */
    public LoggerTreeModel()
    {
        super(new LoggerNode());

        buildTree();
    }

    /**
     * Builds a visual tree based on logging namespace
     */
    private void buildTree()
    {
        LoggerNode root = new LoggerNode("root");
        Map nodes = new HashMap();

        nodes.put(null, root);

        Enumeration eUnsorted = LogManager.getLogManager().getLoggerNames();
        
        // Sort the loggers alphabetically
        List keyList = Collections.list(eUnsorted);
        Collections.sort(keyList);
        Enumeration e = Collections.enumeration(keyList);
        
        while (e.hasMoreElements())
        {
            String name = (String)e.nextElement();
            Logger logger = Logger.getLogger(name);
            LoggerNode node = (LoggerNode)nodes.get(name);
            if (node == null)
            {
                node = new LoggerNode(logger);// name);
                nodes.put(name, node);
//                System.out.println("logger name: " + logger.getName()
//                        + ", level: " + logger.getLevel());
            }

            String parentName;
            if (logger.getParent() != null)
            {
                parentName = logger.getParent().getName();
            }
            else
            {
                parentName = null;
            }

            LoggerNode parentNode = (LoggerNode)nodes.get(parentName);
            if (parentNode == null)
            {
                logger = Logger.getLogger(parentName);
                parentNode = new LoggerNode(logger);// parentName);
                nodes.put(parentName, parentNode);
            }
            parentNode.add(node);
        }

        fRoot = (LoggerNode)nodes.get("");

        setRoot(fRoot);
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: LoggerTreeModel.java,v $
// Revision 1.3  2006/07/11 16:35:24  smaher_cvs
// Remove sysout diagnostics
//
// Revision 1.2  2006/06/15 16:31:19  smaher_cvs
// Loggers are now sorted alphabetically in the tree model.
//
// Revision 1.1  2005/09/01 18:35:04  pjain_cvs
// Adding to CVS.
//
//