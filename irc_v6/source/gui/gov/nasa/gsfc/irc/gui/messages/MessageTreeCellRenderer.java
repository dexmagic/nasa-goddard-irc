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

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.gui.swing.SimpleTreeCellRenderer;
import gov.nasa.gsfc.irc.gui.util.IconUtil;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;

/**
 *  This class provides a simple tree cell renderer for displaying
 *  commands.  It will change the icon displayed next to a leaf
 *  if it is a script or command.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/01/07 20:56:01 $
 *  @author	    Ken Wootton
 *  @author		John Higinbotham (Emergent Space Technologies)
 */
public class MessageTreeCellRenderer extends SimpleTreeCellRenderer
{
	//  Icon locations
	private static final String COMMAND_LOC = "CommandSent13x12.gif";
	private static final String SCRIPT_LOC = "Script13x12.gif";
	private static final String DISABLE_COMMAND_LOC ="CommandSent13x12_off.gif";
	private static final String DISABLE_SCRIPT_LOC ="Script13x12_off.gif";
	public static final String IMAGE_DIR = "resources/images/";

	//  Icons
	private static final ImageIcon COMMAND_ICON =
		IconUtil.getIcon(IMAGE_DIR + COMMAND_LOC);
	private static final ImageIcon SCRIPT_ICON =
		IconUtil.getIcon(IMAGE_DIR + SCRIPT_LOC);
	private static final ImageIcon DISABLE_COMMAND_ICON =
		IconUtil.getIcon(IMAGE_DIR + DISABLE_COMMAND_LOC);
	private static final ImageIcon DISABLE_SCRIPT_ICON =
		IconUtil.getIcon(IMAGE_DIR + DISABLE_SCRIPT_LOC);

	/**
	 *    Create the tree cell renderer for commands.
	 *
	 *  @param originalRenderer  the tree cell renderer on which this renderer
	 *                           is based
	 */
	public MessageTreeCellRenderer(TreeCellRenderer originalRenderer)
	{
		super(originalRenderer);
	}

	/**
	 *    Get the component used drawing the given value of the specified
	 *  tree cell.  The rendered component will use a custom icon for
	 *  leaf nodes, depending on if the cell in question represents
	 *  a command or a script.
	 *
	 *  @param tree  the tree in which the cell will be rendered
	 *  @param value  the value of the node
	 *  @param selected  whether or not the cell is selected
	 *  @param expanded  whether or not the cell is expanded
	 *  @param leaf  whether or not this node is a leaf
	 *  @param row  the row within the tree
	 *  @param hasFocus  whether or not the tree cell has the focus
	 */
	public Component getTreeCellRendererComponent(JTree tree,
												  Object value,
												  boolean selected,
												  boolean expanded,
												  boolean leaf,
												  int row,
												  boolean hasFocus)
	{
		Component comp = super.getTreeCellRendererComponent(tree,
															value,
															selected,
															expanded,
															leaf,
															row,
															hasFocus);

		if (comp instanceof JLabel)
		{
			JLabel labelComp = (JLabel) comp;

			//  Add a tool tip to help the display the name when the tree
			//  is too small to display the whole thing.
			labelComp.setToolTipText(value.toString());

			//  Customize the icons for the leaves.
			if (leaf && value instanceof DescriptorNode)
			{
				Descriptor descriptor =
					(Descriptor) ((DescriptorNode) value).getDescriptor();

				//  Scripts.  Check this first because is it farther down in
				//  the inheritance hierarchy.
				if (descriptor instanceof ScriptDescriptor &&
					SCRIPT_ICON != null)
				{
					labelComp.setIcon(SCRIPT_ICON);
                    // needed to resolve a Java bug under certain platforms
                    // which throws an exception when an icon within
                    // a label is disabled.
					labelComp.setDisabledIcon(DISABLE_SCRIPT_ICON);
				}

				//  Commands
				else if (descriptor instanceof MessageDescriptor &&
						 COMMAND_ICON != null)
				{
					labelComp.setIcon(COMMAND_ICON);
                    // needed to resolve a Java bug - see comment above
					labelComp.setDisabledIcon(DISABLE_COMMAND_ICON);
				}
			}
		}

		return comp;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageTreeCellRenderer.java,v $
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//  Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.3  2004/09/22 18:11:14  tames_cvs
//  Removed references to obsolete methods or classes.
//
//  Revision 1.2  2004/09/21 22:08:58  tames_cvs
//  Removed references to obsolete methods or classes.
//
//  Revision 1.1  2004/09/16 21:10:38  jhiginbotham_cvs
//  Port from IRC v5.
// 
