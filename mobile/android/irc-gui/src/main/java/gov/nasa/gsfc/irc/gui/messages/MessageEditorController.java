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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import gov.nasa.gsfc.irc.gui.controller.SwixController;


/**
 *  MessageEditorController monitors action events from filter options
 *  combo box.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/27 22:00:46 $
 * @author Troy Ames
 */

public class MessageEditorController implements SwixController, ActionListener
{
    private MessageEditorView fCustomizer;
    private JComboBox fFilterOptions;
    
	private Object fFilterSelected;
	public static final String SORT_ALPHA_ACTION = "SORT_ALPHA";
	public static final String SORT_NONE_ACTION = "SORT_NONE";
    
    /** 
     * Default Constructor
     */
    public MessageEditorController()
    {
    }
    
    /**
	 * Set the views that this controller will monitor.
	 * 
	 * @param component the view or component to control or monitor.
	 * @see gov.nasa.gsfc.irc.gui.controller.SwixController#setView(java.awt.Component)
	 */
	public void setView(Component component)
	{
		if (component instanceof MessageEditorView)
		{
			fCustomizer = (MessageEditorView) component;
			fCustomizer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//fCustomizer.setRowHeight(ROW_HEIGHT);
		}
		else if (component instanceof JComboBox)
		{
			fFilterOptions = (JComboBox) component;
			fFilterSelected = fFilterOptions.getSelectedItem();
			fFilterOptions.addActionListener(this);
			
			if (fFilterSelected != null && fCustomizer != null)
			{
				fCustomizer.setFilter(fFilterSelected);
			}
		}
		else if (component instanceof JToggleButton)
		{
			((JToggleButton)component).addActionListener(this);
		}
	}
    
    /**
     *  Called when user actions occur.  
     *
     *  @param e  the action event
     */
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();

        if (source instanceof JComboBox)
        {
            if (source == fFilterOptions && fCustomizer != null)
            {
            	fFilterSelected = fFilterOptions.getSelectedItem();
            	fCustomizer.setFilter(fFilterSelected);
            }
        }
        else if (source instanceof JToggleButton)
        {
            if (fCustomizer != null)
            {
        		String actCommand = evt.getActionCommand();

        		if (SORT_ALPHA_ACTION.equals(actCommand))
        		{
                	fCustomizer.setSortOrder(1);
        		}
        		else if (SORT_NONE_ACTION.equals(actCommand))
        		{
                	fCustomizer.setSortOrder(0);
        		}
            }
        }
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageEditorController.java,v $
//  Revision 1.3  2005/01/27 22:00:46  tames
//  Misc coding style changes and removal of obsolete code.
//
//  Revision 1.2  2005/01/25 23:39:07  tames
//  Added sorting capability.
//
//  Revision 1.1  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//