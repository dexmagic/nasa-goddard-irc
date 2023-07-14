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

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.commons.types.namespaces.Member;
import gov.nasa.gsfc.commons.types.namespaces.MembershipEvent;
import gov.nasa.gsfc.commons.types.namespaces.MembershipListener;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.ComponentSet;
import gov.nasa.gsfc.irc.components.HasComponents;
import gov.nasa.gsfc.irc.components.ManagedComponent;
import gov.nasa.gsfc.irc.components.MinimalComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;


/**
 *  Component Tree Model creates a tree with components.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Oct 07, 2004 11:05:21 AM
 *  @author Peyush Jain
 */

public class ComponentTreeModel extends DefaultTreeModel 
	implements MembershipListener 
{   
    ComponentNode fRoot;
    
    /**
     * Default Constructor
     */ 
    public ComponentTreeModel()
    {
        super(new ComponentNode());
        
        //remove this container test
        //ContainerTest ct = new ContainerTest();
        
        fRoot = new ComponentNode(Irc.getComponentManager());
        Irc.getComponentManager().addMembershipListener(this);
        
        setRoot(fRoot);
        buildTree(fRoot);
    }
    
    /**
     * Recursively builds a visual tree based on IRC components structure
     */
    private void buildTree(ComponentNode parent)
    {
    	
        if(parent.getUserObject() instanceof HasComponents)
        {

        	Set unsortedSet = ((HasComponents)parent.getUserObject()).getComponents(); 
        	
        	//
        	// Order the components in the tree alphabetically by component name
        	//
            List c = new ArrayList(unsortedSet);
            Collections.sort(c, new Comparator() {

				public int compare(Object o1, Object o2)
				{
					if (o1 instanceof HasName && o2 instanceof HasName)
					{
						return ((HasName) o1).getName().compareTo(((HasName) o2).getName());
					}
					return 0;
				}
            });            

            
            Object list[] = c.toArray();
            for(int i = 0; i < list.length; i++)
            {
                ComponentNode tempNode = new ComponentNode(list[i]);
                parent.add(tempNode);
                buildTree(tempNode);                
            }
        }        
    }
    
    /**
     * Invoke this to insert newChild at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create
     * the appropriate event.
     */    
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index)
    {
        Object c = ((ComponentNode)newChild).getUserObject();
        Object p = ((ComponentNode)parent).getUserObject();
        
        if(c instanceof MinimalComponent)
        {
            if(p instanceof ComponentSet)
            {
                //insert in model (IRC)
                
                //This fires an event which is received by 
                //receiveMembershipEvent(). It then inserts
                //the component in the tree (GUI)
                ((ComponentSet)p).addComponent((MinimalComponent)c);
            }
            else
            {
                System.out.print("ERROR: PARENT OBJECT IS NOT COMPONENT SET");
                System.out.println("IT IS OF TYPE: "+p.getClass());
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
     */
    public void removeNodeFromParent(MutableTreeNode node)
    {
        if(node.getParent() != null)
        {
            Object c = ((ComponentNode)node).getUserObject();
            Object p = ((ComponentNode)(node.getParent())).getUserObject();
                        
            if(c instanceof MinimalComponent)
            {
                if(p instanceof ComponentSet)
                {             
                    //remove from model (IRC)
                    
                    //This fires an event which is received by 
                    //receiveComponentManagerEvent(). It then removes
                    //the component from the tree (GUI)
                    ((ComponentSet)p).removeComponent((MinimalComponent)c);                    
                }
           }
            else
            {
                System.out.println("ERROR: Selected component is not an instance of MinimalComponent.");
            }  
        }
    }

	/**
	 * Causes this ComponentTreeModel to receive the given MembershipEvent.
	 *
	 * @param event A MembershipEvent
	 **/
	
	public void receiveMembershipEvent(MembershipEvent event)
	{
		Object source = event.getSource();
		
		if (source instanceof HasComponents)
		{
			Member member = event.getMember();
			
			if ((member != null) && (member instanceof ManagedComponent))
			{
				ManagedComponent component = (ManagedComponent) member;
			
				if (event.wasAdded())
				{
					addComponent(fRoot, component);
				}
				else if (event.wasRemoved())
				{
					removeComponent(fRoot, component);
				}
			}
		}
	}
	
	
    /**
     * Recursive function to add a Component from a MembershipEvent
     * @param currentNode
     * @param component
     */
    private void addComponent(ComponentNode currentNode, ManagedComponent component)
    {
        if (currentNode.getUserObject() == component.getManager())
        {
            ComponentNode child = new ComponentNode(component);
            
            //insert in tree (automaticaly refreshes the tree)
            super.insertNodeInto(child, currentNode, 0);
            
            //adds any sub-components of "child" to the tree
            buildTree(child);
        }
        else if (currentNode.getChildCount() > 0)
        {
            Enumeration e = currentNode.children();
            
            while (e.hasMoreElements())
            {
                ComponentNode node = (ComponentNode) e.nextElement();
                addComponent(node, component);
            }
        }
    }
    

    /**
     * Recursive function to remove a Component from any part of the tree.
     * 
     * @param currentNode
     * @param nodeToRemove
     */
    private void removeComponent(ComponentNode currentNode, Object nodeToRemove)
    {
        if (currentNode.getUserObject() == nodeToRemove)
        {
            super.removeNodeFromParent(currentNode);
        }
        else if (currentNode.getChildCount() > 0)
        {
            Enumeration e = currentNode.children();
            
            while (e.hasMoreElements())
            {
                ComponentNode node = (ComponentNode) e.nextElement();
                removeComponent(node, nodeToRemove);
            }
        }
    }
}


//--- Development History ---------------------------------------------------
//
//  $Log: ComponentTreeModel.java,v $
//  Revision 1.11  2006/06/16 19:28:37  smaher_cvs
//  Sort the components by name in buildTree.  Previously, layout was non-deterministic.
//
//  Revision 1.10  2006/03/14 14:56:24  chostetter_cvs
//  Replaced Composite with ComponentSet
//
//  Revision 1.9  2006/01/23 17:59:53  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.8  2005/01/21 02:54:40  smaher_cvs
//  Made the componentAdded and componentRemoved work with any level
//  of the tree - not just the root.
//
//  Revision 1.7  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.6  2004/12/01 22:43:27  tames_cvs
//  Updated to reflect changes to the Composite interface. This allows the
//  ComponentManager and Composite components to use the same
//  interface.
//
//  Revision 1.4 2004/12/01 19:26:28 pjain_cvs
//  Added support for adding components with sub-components.
//
//  Revision 1.3 2004/11/22 18:44:23 pjain_cvs
//  Listens and Processes ComponentManager Events.
//
//  Revision 1.2  2004/11/19 15:17:56  pjain_cvs
//  *** empty log message ***
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//