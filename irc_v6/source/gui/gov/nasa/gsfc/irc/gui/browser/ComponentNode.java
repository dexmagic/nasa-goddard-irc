//=== File Prolog ============================================================
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.gui.browser;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nasa.gsfc.irc.components.MinimalComponent;

/**
 *  ComponentNode represents a node in Component Browser tree.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Oct 08, 2004 9:33:43 AM
 *  @author Peyush Jain
 */

public class ComponentNode extends DefaultMutableTreeNode
{
    /**
     * Default Constructor
     */
    public ComponentNode()
    {
        super();
    }
    
    /**
     * Constructor
     * 
     * @param userObject sets the user object as a node
     */
    public ComponentNode(Object userObject)
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
        if(getUserObject() instanceof MinimalComponent)
        {
            return ((MinimalComponent)getUserObject()).getName();
        }
        else
        {
            //return super.toString();
            //getUserObject() instanceof ComponentManager
            return "IRC Component Manager";
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentNode.java,v $
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//