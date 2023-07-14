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

package gov.nasa.gsfc.irc.gui.swing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 *    This class provides a simple panel meant to display a group of buttons.
 *  These buttons, added using the 'add' method, are centered within 
 *  panel in a grid layout.  This class is particularly useful for laying
 *  out buttons to be used in a dialog.
 *    The idea for this panel originally came from Sam Lieber.  In fact,
 *  this is really just a cleaned up version of something VERY similar
 *  that he did.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/22 18:13:31 $
 *  @author	    Ken Wootton
 */
public class ButtonPanel extends JPanel
{
	//  Spacing between added components.
    private static final int HORIZONTAL_GAP = 4;

	private JPanel fCenteredPanel = null;

	/**
	 *    Constructor.  Lay out the panel.
	 */
	public ButtonPanel()
	{
		GridLayout butLayout = null;

		setLayout(new FlowLayout());

		//  Create the centered panel.  Be careful to not use our overridden
		//  add method.
		butLayout = new GridLayout();
		butLayout.setHgap(HORIZONTAL_GAP);
		fCenteredPanel = new JPanel(butLayout);
		super.add(fCenteredPanel);
	}

	/**
	 *    Add a component to the panel.  This component along with any other
	 *  components are centered in this panel as a group.  Note that this
	 *  method allows the user to add any component to the panel even though 
	 *  the name of the class implies that its main use is to only add buttons.
	 * 
	 *  @param comp  component to add
	 *
	 *  @return  the added component
	 */
    public Component add(Component comp)
    {
		fCenteredPanel.add(comp);

		return comp;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ButtonPanel.java,v $
//  Revision 1.1  2004/09/22 18:13:31  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
