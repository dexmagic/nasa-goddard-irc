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

import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * LoggerNode represents a node in Log viewer tree.
 * 
 * <P> This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 12, 2005 1:27:42 PM
 * @author Peyush Jain
 */

public class LoggerNode extends DefaultMutableTreeNode
{
    /**
     * Default Constructor
     */
    public LoggerNode()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param userObject sets the user object as a node
     */
    public LoggerNode(Object userObject)
    {
        super(userObject);
    }

    /**
     * Returns the name of the user object
     * 
     * @return the name of the user object
     */
    public String toString()
    {
        String name = "";
        String parentName = "";
        Object obj = getUserObject();

        if (obj instanceof Logger)
        {
            name = ((Logger)obj).getName();

            if (name.equals(""))
            {
                return "Root Logger";
            }
            else
            {
                parentName = ((Logger)obj).getParent().getName();
                String str = name.replaceFirst(parentName, "");
                if (str.startsWith("."))
                {
                    str = str.substring(1);
                }
                return str;
            }
        }
        return name;
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: LoggerNode.java,v $
// Revision 1.1  2005/09/01 18:02:28  pjain_cvs
// Adding to CVS.
//
//