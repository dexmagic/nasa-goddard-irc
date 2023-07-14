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

import javax.swing.tree.DefaultTreeModel;

import gov.nasa.gsfc.commons.types.namespaces.MembershipEvent;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.data.events.DataSpaceEvent;
import gov.nasa.gsfc.irc.data.events.DataSpaceListener;


/**
 * DataSpaceTreeModel creates a tree view of a DataSpace.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/23 17:59:51 $
 * @author Troy Ames
 */
public class DataSpaceTreeModel extends DefaultTreeModel 
implements DataSpaceListener 
{   
    DataNode fRoot;
    
    /**
     * Default Constructor
     */ 
    public DataSpaceTreeModel()
    {
        super(new DataNode());
        
        //remove this container test
        //ContainerTest ct = new ContainerTest();
        
        fRoot = new DataNode(Irc.getDataSpace());
        Irc.getDataSpace().addDataSpaceListener(this);
        
        setRoot(fRoot);
        buildTree(fRoot);
    }
    
    /**
     * Recursively builds a visual subtree based on the given parent structure
     * @param parent the current parent node
     */
    private void buildTree(DataNode parent)
    {
        if (parent.getUserObject() instanceof DataSpace)
        {
        	DataSpace dataSpace = (DataSpace) parent.getUserObject();
        	Object list[] = dataSpace.getBasisBundles().toArray();
            
            for(int i = 0; i < list.length; i++)
            {
                DataNode childNode = new DataNode(list[i]);
                parent.add(childNode);
                buildTree(childNode);                
            }
        }
        else if (parent.getUserObject() instanceof BasisBundle)
        {
        	BasisBundle bundle = (BasisBundle) parent.getUserObject();
        	
        	Object descriptor = bundle.getBasisBufferDescriptor();        	
            parent.add(new DataNode(descriptor));

        	Object list[] = bundle.getDataBufferDescriptors().toArray();

            for(int i = 0; i < list.length; i++)
            {
                DataNode childNode = new DataNode(list[i]);
                parent.add(childNode);
            }
        }
    }
    
	/**
	 * Forces the tree to be updated if the given MembershipEvent is a 
	 * DataSpaceEvent.
	 * 
	 * @param event A MembershipEvent
	 */
	public void receiveMembershipEvent(MembershipEvent event)
	{
		if (event instanceof DataSpaceEvent)
		{
			receiveDataSpaceEvent((DataSpaceEvent) event);
		}
	}
    
	/**
	 * Forces the tree to be updated.
	 * 
	 * @param event A DataSpaceEvent
	 */
	public void receiveDataSpaceEvent(DataSpaceEvent event)
	{
        buildTree(fRoot);
	}
}


//--- Development History ---------------------------------------------------
//
//  $Log: DataSpaceTreeModel.java,v $
//  Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/12/15 15:23:02  tames
//  Relocated out of library package.
//
//  Revision 1.1  2005/08/31 16:12:03  tames_cvs
//  Inital simple implementation.
//
//