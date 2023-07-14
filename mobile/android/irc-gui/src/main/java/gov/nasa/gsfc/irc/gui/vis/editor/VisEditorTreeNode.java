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

package gov.nasa.gsfc.irc.gui.vis.editor;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.gui.vis.VisRenderer;

/**
 *  Represents a node in visualization editor tree.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/02 03:56:14 $
 *  @author Troy Ames
 */

public class VisEditorTreeNode extends DefaultMutableTreeNode
{
    /**
     * Default Constructor
     */
    public VisEditorTreeNode()
    {
        super();
    }
    
    /**
     * Constructs a node with the given user object.
     * 
     * @param userObject sets the user object as a node
     */
    public VisEditorTreeNode(Object userObject)
    {
        super(userObject);
    }
    
    /**
     * Returns a string representation of the user object
     *
     * @return a string
     */
    public String toString()
    {
    	Object userObject = getUserObject();
    	
        if(userObject instanceof JComponent)
        {
        	String label = ((JComponent) userObject).getName();
        	
        	if (label == null)
        	{
        		String[] className = userObject.getClass().getName().split("\\.", 0);
        		label = className[className.length - 1];
        	}
            return label;
        }
        else if (userObject instanceof VisRenderer)
        {
    		String[] className = userObject.getClass().getName().split("\\.", 0);
    		return className[className.length - 1];
        }
        else if (userObject instanceof HasName)
        {
    		return ((HasName) userObject).getName();
        }
        else
        {
    		return userObject.toString();
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: VisEditorTreeNode.java,v $
//  Revision 1.2  2006/01/02 03:56:14  tames
//  Added support for editing an Input.
//
//  Revision 1.1  2004/12/20 22:41:55  tames
//  Visualization property editing support added.
//
//