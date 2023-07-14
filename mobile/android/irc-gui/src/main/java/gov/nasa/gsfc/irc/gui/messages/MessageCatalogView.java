//=== File Prolog ============================================================
//
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


import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * This class provides the view for the message and script catalog. It specifies
 * a custom cell renderer.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/02/01 18:47:57 $
 * @author		John Higinbotham (Emergent Space Technologies)
 * @author		Troy Ames
 */
public class MessageCatalogView extends JTree 
{
    /**
     * Returns an instance of <code>MessageCatalogView</code> which  
     * is created using the specified data model.
     *
     * @param newModel  the <code>TreeModel</code> to use as the data model
     */
    public MessageCatalogView(TreeModel newModel) 
    {
        super(newModel);
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new MessageTreeCellRenderer(getCellRenderer()));
			
		ToolTipManager.sharedInstance().registerComponent(this);
		this.showsRootHandles = true;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageCatalogView.java,v $
//  Revision 1.3  2005/02/01 18:47:57  tames
//  Turned on root handles.
//
//  Revision 1.2  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//  Revision 1.1  2005/01/07 20:56:01  tames
//  Relocated and refactored from a former command package. They now
//  utilize bean PropertyEditors.
//
//  Revision 1.3  2004/09/21 21:18:39  tames_cvs
//  Relocated all layout and control functionality.
//
//  Revision 1.1  2004/09/16 21:10:38  jhiginbotham_cvs
//  Port from IRC v5.
//
