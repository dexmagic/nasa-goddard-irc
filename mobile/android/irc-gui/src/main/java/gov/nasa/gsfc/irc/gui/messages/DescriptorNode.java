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
import javax.swing.tree.TreePath;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;
import gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.description.ScriptInterfaceDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptGroupDescriptor;

/**
 *  This class provides the tree with nodes given a descriptor.  It is
 *  required so that the tree can have a sensible name to display using the
 *  the toString method -- which currently provides the name of the 
 *  descriptor.
 *    This class also provides some convenience methods for creating a 
 *  tree of messages and scripts.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/09/13 20:30:12 $
 *  @author	    Lynne Case
 *  @author		John Higinbotham (Emergent Space Technologies)
 */
public class DescriptorNode extends DefaultMutableTreeNode
{
	/**
	 * The descriptor that relates to the node. 
	 */
	private Descriptor fDescriptor = null;

	/**
	 * This is used when we want a root node that is not based on a descriptor.
	 */
	private String fNoDescriptorName = null;
	
	
	/**
	 * Constructor that takes a descriptor.
	 */
	public DescriptorNode(String name)
	{
		super(name);
		fNoDescriptorName = name;
	}

	/**
	 * Constructor that takes a descriptor.
	 */
	public DescriptorNode(Descriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
	}

	/**
	 * Constructor that takes a descriptor and a destination.
	 */
	public DescriptorNode(Descriptor descriptor, String destination)
	{
		super(descriptor);
		fDescriptor = descriptor;
	}
	
	/**
	 *  Given a tree path, return the descriptor found as the last
	 *  component of the path, if it exists.  This is primarily provided 
	 *  as a convenience method for listeners of tree selection.
	 *
	 *  @param nodePath  Path that may or may not include a descriptor
	 *                   node at its end
	 *  @return  Descriptor node in the path or, if one is not found,
	 *           'null'
	 */
	public static DescriptorNode getNodeFromPath(TreePath nodePath)
	{
		DescriptorNode descNode = null;

		//---Get the node if it is of the correct type.
		if (nodePath != null)
		{
			Object pathComp = nodePath.getLastPathComponent();
			if (pathComp != null && pathComp instanceof DescriptorNode)
			{
				descNode = (DescriptorNode) pathComp;
			}
		}
		return descNode;
	} 

	/**
	 *  Build a tree from a script group description.
	 *    
	 *  @param startNode Root node of the tree
	 *  @param group  Group of commands to add to the root 
	 */
	public static void buildScriptTree(DescriptorNode root,
			ScriptGroupDescriptor group)
	{
		//--- Script Groups can have subgroups.  We need
		//    to process all of the subgroups in the list.  We do that
		//    by making a recursive call to this method with each subgroup
		//    in the list.
		Iterator list = group.getSubGroups();
		while (list.hasNext())
		{
			ScriptGroupDescriptor next = (ScriptGroupDescriptor) list.next();
			DescriptorNode node = new DescriptorNode(next);
			root.add(node);
			buildScriptTree(node, next);
		}

		//---Process the scripts
		createScriptNodes(root, group);
	}

	/**
	 * Create script nodes from the given group description.
	 *
	 * @param parent  parent node of the newly created nodes
	 * @param cmdInterface  group of commands to create node for
	 */
	public static void createScriptNodes(DescriptorNode parent, ScriptGroupDescriptor group)
	{
		Iterator iterator = group.getScripts();
		while (iterator.hasNext())
		{
			ScriptDescriptor cpd = (ScriptDescriptor) iterator.next();
			DescriptorNode node = new DescriptorNode(cpd);
			parent.add(node);
		}
	}
	
	//TODO: we probably need to update this method to support both messages and scripts now that 
	//there is a seperate script interface.
	
	/**
	 *  Create message nodes in the tree given a message interface.
	 *
	 * @param parent  Parent node of the newly created nodes
	 * @param msgInterface  Message interface from which commands will come
	 */
	public static void createNodes(DescriptorNode parent, MessageInterfaceDescriptor msgInterface)
	{
		Iterator items = msgInterface.getMessages();
		Descriptor des = null;

		while (items.hasNext())
		{	
			//TODO: What is the destination for a command now? Is it up to the 
			//proxy to determine what ports to pass it to?
			//DescriptorNode cmdNode = new DescriptorNode(cmd, dest);
			des = (Descriptor) items.next();
			DescriptorNode cmdNode = new DescriptorNode(des);			
			parent.add(cmdNode);
		}
	}
	
	/**
	 *  Create script nodes in the tree given a script interface.
	 *
	 * @param parent  Parent node of the newly created nodes
	 * @param msgInterface  Message interface from which commands will come
	 */
	public static void createNodes(DescriptorNode parent, ScriptInterfaceDescriptor scriptInterface)
	{
		Iterator items = scriptInterface.getScripts();
		Descriptor des = null;

		while (items.hasNext())
		{	
			des = (Descriptor) items.next();
			DescriptorNode cmdNode = new DescriptorNode(des);			
			parent.add(cmdNode);
		}
	}
	
	/**
	 * Get the sensible name for this node.
	 */
	public String toString()
	{
		String result = null;
		
		if (fDescriptor != null)
		{
			if (fDescriptor instanceof AbstractIrcElementDescriptor)
			{
				result = ((IrcElementDescriptor) fDescriptor).getDisplayName();
			}
			else
			{
				result = fDescriptor.getName();
			}
		}
		else
		{
			result = fNoDescriptorName;
		}
		
		return result;
	}

	/**
	 * Set the descriptor
	 */
	public void setDescriptor(Descriptor descriptor)
	{
		fDescriptor = descriptor;
	}

	/**
	 * Get the descriptor
	 */
	public Descriptor getDescriptor()
	{
		return fDescriptor;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorNode.java,v $
//  Revision 1.4  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.3  2005/04/08 20:59:11  tames_cvs
//  Removed unused references to a destination.
//
//  Revision 1.2  2005/01/11 21:51:59  tames
//  Changed the label to be "displayName"
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//  Revision 1.5  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.4  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.3  2004/09/21 15:10:27  tames
//  Added functionality to support a destination path for messages.
//
//  Revision 1.2  2004/09/20 21:11:16  tames
//  Refactoring of classes to get layout in XML, control in controllers,
//  and simplified views.
//
//  Revision 1.1  2004/09/16 21:10:38  jhiginbotham_cvs
//  Port from IRC v5.
// 
