//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui.messages;

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.DescriptorLibraryUpdateListenerAdapter;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;
import gov.nasa.gsfc.irc.devices.DeviceChangeListener;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.description.ScriptInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.ports.PortDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptGroupDescriptor;


/**
 *  This class acts as a model adapter. It makes use of the descriptor library
 *  to build up a tree based on contained instruments, sub-instruments, command
 *  messages and scripts.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/07/14 15:11:03 $
 *  @author		John Higinbotham (Emergent Space Technologies)
**/
public class DescriptorModelAdapter extends DefaultTreeModel 
	implements DeviceChangeListener
{
	//---Listener for changes to the descriptor library.
	private DescriptorLibraryUpdateListenerAdapter fLibraryListener =
		new DescriptorLibraryUpdateListenerAdapter()
		{
			/**
			 *    Indicate an update to the library's instruments.
			 */
			public void handleInstrumentUpdate()
			{
				handleDeviceChange();
			}
			
			/**
			 *    Indicate an update to the library's command procedure
			 *  groups.
			 */
			public void handleCommandProcedureGroupUpdate()
			{
				handleDeviceChange();
			}
		};
		
	//-------------------------------------------------------------------------------------
		
	/**
	 * Create a new DescriptorModelAdapter
	 *
	 **/
	public DescriptorModelAdapter()
	{
		//---The TreeModel won't instantiate without a root node, but
		//   we don't know where the root is yet.
		super(new DefaultMutableTreeNode());
		init();
		Irc.getDescriptorLibrary().addLibraryUpdateListener(fLibraryListener);
	}

	/**
	 * Handle instrument changes that occur on the descriptor library.
	 *
	**/
	public void handleDeviceChange()
	{
		init();
		reload();
	}

	/**
	 * Initialize the tree from the information in the descriptors.
	 *
	**/
	public void init()
	{
		DescriptorNode fRoot = new DescriptorNode("IRC");
		setRoot(fRoot);

		//---Get the instruments and add them to the tree
        Iterator deviceIterator = Irc.getDescriptorLibrary().getDevices();
		DeviceDescriptor device = null;
		
        while(deviceIterator.hasNext())
        {
            device = (DeviceDescriptor) deviceIterator.next();
            DescriptorNode instrumentRoot = new DescriptorNode(device);
		    fRoot.add(instrumentRoot);
            buildTreeFromNode(instrumentRoot, device);
        }

		//---Get seperate scripts (external to the instruments) and add them to the tree
        Iterator sgdIterator = Irc.getDescriptorLibrary().getScriptGroups();
		ScriptGroupDescriptor sgd = null;
		
        if(sgdIterator.hasNext())
        {
            sgd = (ScriptGroupDescriptor) sgdIterator.next();
            DescriptorNode scriptGroupRoot = new DescriptorNode(sgd);
		    fRoot.add(scriptGroupRoot);
		    DescriptorNode.buildScriptTree(scriptGroupRoot, sgd);
        }
	}
	
	/**
	 * Build a tree from an instrument description node.
	 * 
	 * @param startNode DescriptorNode to start working with
	 * @param startInst DeviceDescriptor to start working with
	**/
	public void buildTreeFromNode(DescriptorNode startNode, 
								  DeviceDescriptor startInst)
	{
		//--- Instruments can have subinstruments / subsystems.  We need
		//    to process all of the instruments in the list.  We do that
		//    by making a recursive calls to this method with each instrument
		//    in the list.
		Iterator deviceList = startInst.getSubdevices();
		
		while (deviceList.hasNext())
		{
			DeviceDescriptor nextInst =	(DeviceDescriptor) deviceList.next();
			DescriptorNode instNode = new DescriptorNode(nextInst);
			startNode.add(instNode);
			buildTreeFromNode(instNode, nextInst);
		}

		// Process script interfaces
		Iterator scriptInterfaces = startInst.getScriptInterfaces();
		int scriptInterfaceCount = startInst.getScriptInterfaceSize();
		
		while (scriptInterfaces.hasNext())
		{
			ScriptInterfaceDescriptor sid = 
				(ScriptInterfaceDescriptor) scriptInterfaces.next();

			//--- Only show script interface nodes if there is more than one
			if (scriptInterfaceCount > 1)
			{
				DescriptorNode parent = new DescriptorNode(sid);
				startNode.add(parent);
				DescriptorNode.createNodes(parent, sid);
			}
			else
			{
				DescriptorNode.createNodes(startNode, sid);
			}
		}
		
		//---Process all message interface nodes
        buildMessageInterfaceTree(startNode, startInst);

		// Process ports
		Iterator ports = startInst.getPorts();
		int portCount = startInst.getPortSize();
		
		while (ports.hasNext())
		{
			DescriptorNode parent = startNode;
			PortDescriptor portDescr = (PortDescriptor) ports.next();
			
			//--- Only show port nodes if there is more than one
			if (portCount > 1)
			{
				parent = new DescriptorNode(portDescr);
				startNode.add(parent);				
			}
			
            //---Process all message interface nodes inside ports
            buildMessageInterfaceTree(parent, portDescr);            
		}
	}
    
    /**
     * Build a tree for Message Interface
     * 
     * @param node Node to start working with
     * @param descriptor Descriptor to start working with
    **/
    private void buildMessageInterfaceTree(DescriptorNode node, IrcElementDescriptor descriptor)
    {
        Iterator msgInterfaces = null;
        int msgInterfaceCount = 0;
        
        if(descriptor instanceof DeviceDescriptor)
        {
            msgInterfaces = ((DeviceDescriptor)descriptor).getMessageInterfaces();
            msgInterfaceCount = ((DeviceDescriptor)descriptor).getMessageInterfaceSize();
        }
        else if(descriptor instanceof MessageInterfaceDescriptor)
        {
            msgInterfaces = ((MessageInterfaceDescriptor)descriptor).getMessageInterfaces();
            msgInterfaceCount = ((MessageInterfaceDescriptor)descriptor).getMessageInterfaceSize();
        }
        else if(descriptor instanceof PortDescriptor)
        {
            msgInterfaces = ((PortDescriptor)descriptor).getMessageInterfaces();
            msgInterfaceCount = ((PortDescriptor)descriptor).getMessageInterfaceSize();            
        }
        
        while (msgInterfaces.hasNext())
        {
            MessageInterfaceDescriptor mid = 
                (MessageInterfaceDescriptor) msgInterfaces.next();

            DescriptorNode parent = null;
            
            //--- Only show message interface nodes if there is more than one
            if (msgInterfaceCount > 1)
            {
                parent = new DescriptorNode(mid);
                node.add(parent);
                DescriptorNode.createNodes(parent, mid);
            }
            else
            {
                parent = node;
                DescriptorNode.createNodes(node, mid);
            }
            
            buildMessageInterfaceTree(parent, mid);
        }
    }
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorModelAdapter.java,v $
//  Revision 1.5  2005/07/14 15:11:03  pjain_cvs
//  Added support for nested Message Interfaces
//
//  Revision 1.4  2005/02/03 07:03:10  tames
//  Updated to reflect changes in DeviceDescriptor.
//
//  Revision 1.3  2005/02/02 22:55:06  tames_cvs
//  Fixed bug where multiple MessageInterfaces was not being properly
//  handled.
//
//  Revision 1.2  2005/02/01 18:47:31  tames
//  Updated to reflect the relocation of messages to ports.
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//  Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.3  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.2  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.1  2004/09/16 21:10:38  jhiginbotham_cvs
//  Port from IRC v5.
// 
