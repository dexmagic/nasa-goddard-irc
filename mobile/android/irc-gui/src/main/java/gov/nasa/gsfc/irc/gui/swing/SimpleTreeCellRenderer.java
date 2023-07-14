//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is given at the end of the file.
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

package gov.nasa.gsfc.irc.gui.swing;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 *    This class provides a very simple tree cell renderer that is based
 *  upon another specified renderer.  This renderer on which this renderer
 *  is based is called the original renderer within this class.
 *    Why the heck would you need something like this (I heard you)?
 *  Well, when you want to make simple changes to the output of the
 *  tree cell renderer already provided by the Java language (say to just
 *  change the icon) it is quite tempting to just subclass
 *  DefaultTreeCellRenderer.  However, this may not work properly with
 *  the Motif look and feel, which uses its own default tree cell renderer
 *	that is also derived from DefaultTreeCellRenderer.
 *    This class attempts to aleviate this problem by providing a
 *  class that is based off the original tree cell renderer, no matter
 *  what look and feel is used.  Simply query the tree in question for
 *  the original renderer and provide it to this class.  Subclasses
 *  of this class can then just act as if they were subclassing a normal
 *  renderer.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/22 18:13:31 $
 *  @author	    Ken Wootton
 */
public abstract class SimpleTreeCellRenderer implements TreeCellRenderer
{
	private TreeCellRenderer fOriginalRenderer = null;

	/**
	 *    Create the simple renderer with the given base renderer.
	 *
	 *  @param originalRenderer  the tree cell renderer on which this renderer
	 *                           is based
	 */
	public SimpleTreeCellRenderer(TreeCellRenderer originalRenderer)
	{
		fOriginalRenderer = originalRenderer;
	}

	/**
	 *    Set the renderer originally used for the tree.  This renderer
	 *  will be used to create the renderer component.
	 *
	 *  @param originalRenderer  the tree cell renderer on which this renderer
	 *                           is based
	 */
	public void setOriginalRenderer(TreeCellRenderer originalRenderer)
	{
		fOriginalRenderer = originalRenderer;
	}

	/**
	 *    Get the renderer that was originally used for the tree.  This
	 *  renderer is used to create the renderer component.
	 *
	 *  @return  the original tree cell renderer on which this renderer is
	 *           based.
	 */
	public TreeCellRenderer getOrginalRenderer()
	{
		return fOriginalRenderer;
	}

	/**
	 *    Get the component used drawing the given value of the specified
	 *  tree cell.  This will use the original renderer to provide this
	 *  component, assuming it exists.
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
		Component comp = null;

		if (fOriginalRenderer != null)
		{
			comp = fOriginalRenderer.getTreeCellRendererComponent(
					tree, value, selected, expanded, leaf, row, hasFocus);
		}

		return comp;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: SimpleTreeCellRenderer.java,v $
//  Revision 1.1  2004/09/22 18:13:31  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
