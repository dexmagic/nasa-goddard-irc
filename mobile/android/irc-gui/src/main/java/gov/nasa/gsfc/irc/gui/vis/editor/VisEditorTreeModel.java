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

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import gov.nasa.gsfc.irc.gui.vis.AbstractDataVisComponent;
import gov.nasa.gsfc.irc.gui.vis.VisComponent;
import gov.nasa.gsfc.irc.gui.vis.VisRenderer;


/**
 * Tree Model for the default visualization editor.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/03 15:55:00 $
 * @author Troy Ames
 */
public class VisEditorTreeModel extends DefaultTreeModel 
{   
	VisEditorTreeNode fRoot;
    
    /**
     * Default Constructor
     */ 
    public VisEditorTreeModel()
    {
        super(null);
    }
    
    /**
     * Sets the root to <code>root</code>. A null <code>root</code> implies
     * the tree is to display nothing, and is legal.
     * 
     * @see javax.swing.tree.DefaultTreeModel#setRoot(javax.swing.tree.TreeNode)
     */
    public void setRoot(TreeNode root) 
    {
        super.setRoot(root);
        
        if (root != null)
        {
        	buildTree((VisEditorTreeNode) root);
        }
    }

    /**
     * Recursively builds a visual tree based on parent structure.
     * 
     * @param parent The parent node of the tree or subtree.
     */
    private void buildTree(VisEditorTreeNode parent)
    {
    	Object userObject = parent.getUserObject();
    	
        if(userObject instanceof VisComponent)
        {
        	// Add Input node
        	if (userObject instanceof AbstractDataVisComponent)
        	{
	        	VisEditorTreeNode inputNode = new VisEditorTreeNode(
	        		((AbstractDataVisComponent) userObject).getInput());
	        	parent.add(inputNode);
        	}
        	
        	// Add renderers
            VisRenderer[] renderers = 
            	((VisComponent) parent.getUserObject()).getRenderers();
            
        	VisEditorTreeNode rendererFolder = new VisEditorTreeNode("Renderers");
        	parent.add(rendererFolder);
        	
            for(int i = 0; i < renderers.length; i++)
            {
                createChildTree(rendererFolder, renderers[i]);
            }
        }
        else if (userObject instanceof JTable)
        {
            //createChildNode(parent, ((JTable) userObject).getModel());
            
        	Component[] children = ((JComponent) userObject).getComponents();

        	for(int i = 0; i < children.length; i++)
            {
                createChildTree(parent, children[i]);
            }
        }
        else if (userObject instanceof JComponent)
        {
        	Component[] children = ((JComponent) userObject).getComponents();

        	for(int i = 0; i < children.length; i++)
            {
                createChildTree(parent, children[i]);
            }
        }
    }
    
    private void createChildTree(VisEditorTreeNode parent, Object childUserObject)
    {
    	if (parent != null && childUserObject != null)
    	{
			VisEditorTreeNode tempNode = new VisEditorTreeNode(childUserObject);
	        parent.add(tempNode);
	        buildTree(tempNode);
    	}
    }
    
    /**
     * Invoke this to insert newChild at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create
     * the appropriate event.
     * 
     * @see javax.swing.tree.DefaultTreeModel#insertNodeInto(javax.swing.tree.MutableTreeNode, javax.swing.tree.MutableTreeNode, int)
     */
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index)
    {
        Object c = ((VisEditorTreeNode)newChild).getUserObject();
        Object p = ((VisEditorTreeNode)parent).getUserObject();
        
        if(c instanceof VisRenderer)
        {
            if(p instanceof VisComponent && c instanceof VisRenderer)
            {
                //insert in model (IRC)
                
                //This fires an event which is received by 
                //receiveComponentManagerEvent(). It then inserts
                //the component in the tree (GUI)
                ((VisComponent)p).addRenderer((VisRenderer)c);
            }
        }
        else
        {
            System.out.println("ERROR: NEW OBJECT IS NOT MINIMAL COMPONENT...");
        }        
    }
    
    /**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     * @see javax.swing.tree.DefaultTreeModel#removeNodeFromParent(javax.swing.tree.MutableTreeNode)
     */
    public void removeNodeFromParent(MutableTreeNode node)
    {
        if(node.getParent() != null)
        {
            Object child = ((VisEditorTreeNode)node).getUserObject();
            Object parent = ((VisEditorTreeNode)(node.getParent())).getUserObject();
                        
            if(child instanceof VisRenderer)
            {
            }
            else
            {
                System.out.println("ERROR: Selected component is not an instance of MinimalComponent.");
            }  
        }
    }
}


//--- Development History ---------------------------------------------------
//
//  $Log: VisEditorTreeModel.java,v $
//  Revision 1.5  2006/01/03 15:55:00  tames
//  Changed constructor to remove creation of temp tree node instance.
//
//  Revision 1.4  2006/01/02 03:56:14  tames
//  Added support for editing an Input.
//
//  Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.2  2004/12/23 14:41:13  tames
//  Support for JTable.
//
//  Revision 1.1  2004/12/20 22:41:55  tames
//  Visualization property editing support added.
//
//