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

package gov.nasa.gsfc.irc.gui.data;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * DataNode represents a node in the Data Space Browser tree.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/12/15 15:23:02 $
 * @author Troy Ames
 */
public class DataNode extends DefaultMutableTreeNode
{
    /**
     * Default Constructor
     */
    public DataNode()
    {
        super();
    }
    
    /**
     * DataNode constructor
     * 
     * @param userObject sets the user object as a node
     */
    public DataNode(Object userObject)
    {
        super(userObject);
    }
    
    /**
     * Returns the name of the user object. Handles DataSpace, BasisBundle, and
     * DataBufferDescriptor types.
     *
     * @return the name of the user object
     */
    public String toString()
    {
    	String result = null;
    	Object userObject = getUserObject();
    	
        if (userObject instanceof DataSpace)
        {
            result = ((DataSpace) userObject).getName();
        }
        else if (userObject instanceof BasisBundle)
        {
            result = ((BasisBundle) userObject).getFullyQualifiedName();
        }
        else if (userObject instanceof DataBufferDescriptor)
        {
            result = ((DataBufferDescriptor) userObject).getFullyQualifiedName();
            result = result + " [" 
            	+ ((DataBufferDescriptor) userObject).toString() + "]";
        }
        
        return result;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DataNode.java,v $
//  Revision 1.1  2005/12/15 15:23:02  tames
//  Relocated out of library package.
//
//  Revision 1.1  2005/08/31 16:12:03  tames_cvs
//  Inital simple implementation.
//
//