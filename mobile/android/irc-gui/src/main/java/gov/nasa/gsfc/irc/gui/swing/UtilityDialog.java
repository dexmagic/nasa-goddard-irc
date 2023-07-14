//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development histroy is located at the end of the file.
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gov.nasa.gsfc.irc.gui.util.ButtonUtil;

/**
 *    This class provides a utility superclass for simple dialogs.  It 
 *  provides a couple pieces of functionality to its subclasses which 
 *  include:
 *    - the ability to set the component with the initial focus
 *    - the ability to determine if the "OK" button may be pressed
 *    - the ability to allow an "enter" action in a text field go to the
 *      next component or activate the "OK" button
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/12/01 21:50:58 $
 *  @author	    Ken Wootton
 */
public abstract class UtilityDialog extends JDialog
{
	//---Possible reasons for the dialog being closed.
	public static final int OK_CLOSED_REASON     = 0;
	public static final int CANCEL_CLOSED_REASON = 1;

	//---Default button labels
	public static final String DEF_OK_LABEL     = "OK";
	public static final String DEF_APPLY_LABEL  = "Apply";
	public static final String DEF_CANCEL_LABEL = "Cancel";

	//---Button actions
	public static final String OK_ACT     = DEF_OK_LABEL;
	public static final String APPLY_ACT  = DEF_APPLY_LABEL;
	public static final String CANCEL_ACT = DEF_CANCEL_LABEL;

	//---Margin around the dialog
	private static final int MARGIN_PIXELS        = 4;
	private static final int BOTTOM_MARGIN_PIXELS = 0;

	//---Buttons for the dialog.
    protected JButton fOkButton     = null;
	protected JButton fApplyButton  = null;
	protected JButton fCancelButton = null;

	//---The actual reason the dialog was closed.	
	protected int fClosedReason = CANCEL_CLOSED_REASON;
    
    /**  
     *    The initial object that will have focus when the dialog
     *  is first displayed.
     */
    protected JComponent fInitialFocusObject = null;

    /**
     *  Listener for window events.
     */
    protected WindowListener fWinListener = new WindowAdapter()
        {
            /**
             *  Make it so that closing the window means cancel.
             */
            public void windowClosing(WindowEvent e) 
            {
				cancel();
            }
            
            /**
             *    The first time this dialog is displayed set the
             *  the focus to the requested 'fInitialFocusObject' and
			 *  set whether or not the ok and apply buttons should be enabled.
			 *
			 *  @param e  the window event
             */
            public void windowActivated(WindowEvent e)
            { 
				//  Update whether or not the ok and apply buttons are enabled.
				updateOkActionButtons();

				//  Set the initial focus.
                if (fInitialFocusObject != null)
                {
					requestFocus(fInitialFocusObject);
                }
            }
        };

    /**
     *    This action listener is used to track action events (e.g. an
	 *  enter keypress) in text fields.
     */
    protected ActionListener fEnterListener = new ActionListener()
        {
            /**
             *    Do the "proper" action for an enter press/action
             *  event.  If there is another field in the GUI in which
             *  to navigate, move the focus to that field.  Otherwise,
             *  activate the ok button.
             */
            public void actionPerformed(ActionEvent e)
            {
                if (e.getSource() instanceof Component)
                {
                    //  If we have another field to go to in the dialog
                    //  move the focus there.
                    Component nextField = 
                        getNextField((Component) e.getSource());
                    if (nextField != null)
                    {
						requestFocus(nextField);
                    }
                    
                    //  Otherwise, activate the ok button.
                    else
                    {
                        if (fOkButton != null)
                        {
                            fOkButton.doClick();
                        }
                    }
                }
            }
        };

    /**
     *    This action listener is used to track the use of the 
	 *  three action buttons (ok, apply, and cancel).
     */
    protected ActionListener fButtonListener = new ActionListener()
        {
            /**
			 *    Perform the action indicated by the user's button
			 *  press.
             */
            public void actionPerformed(ActionEvent e)
            {
				String actCommand = e.getActionCommand();

				if (actCommand.equals(OK_ACT))
				{
					ok();
				}
				else if (actCommand.equals(APPLY_ACT))
				{
					apply();
				}
				else if (actCommand.equals(CANCEL_ACT))
				{
					cancel();
				}
			}
		};

    /**
     *    Listener for document changes.  This listener will update
     *  whether or not the ok button is enabled any time the content of 
     *  the item in which this is listening changes.
     */
    protected DocumentListener fContentListener = new DocumentListener()
        {
			/**
			 *    Indicate that the document changed.
			 *
			 *  @param e  the document event
			 */
            public void changedUpdate(DocumentEvent e)
            {
				updateOkActionButtons();
            }

			/**
			 *    Indicate that something was inserted into the document.
			 *
			 *  @param e  the document event
			 */
            public void insertUpdate(DocumentEvent e)
            {
                updateOkActionButtons();
            }

			/**
			 *    Indicate that something was removed into the document.
			 *
			 *  @param e  the document event
			 */
            public void removeUpdate(DocumentEvent e)
            {
                updateOkActionButtons();
            }
        };

    /**
     *    Constructor.  This is a model dialog that is not destroyed upon
	 *  closing.
     *
     *  @param owner  the parent frame for which this dialog will be
     *                centered
     *  @param title  the title of the dialog
	 *  @param modal  whether or not the dialog should be modal
     */
    public UtilityDialog(Frame owner, String title, boolean modal)
    {
        super(owner, title, modal);
        
        //  Don't destroy the dialog on close.
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        addWindowListener(fWinListener);
    }

    /**
     *    Get the operation the user used to close the dialog.  The user
     *  may close the dialog by doing an ok operation (typing enter in
     *  a text field or by hitting the ok button) or by doing a cancel
     *  operation (closing the window or hitting the cancel button).
     * 
     *  @return  Either OK_CLOSED_REASON or CANCEL_CLOSED_REASON,
     *           depending on the operation used to close the dialog.
     */
    public int getClosedReason()
    {
        return fClosedReason;
    }

    /**
     *    Display or hide the dialog.
     *
     *  @param state  desired visibility of the dialog
     */
    public void setVisible(boolean state)
    {
        //  If we are making this visible, reinitialize the why this
		//  dialog was closed.
        if (state)
        {
            fClosedReason = CANCEL_CLOSED_REASON;
        }

        super.setVisible(state);
    }

	/**
	 *    Set the text for the 'ok' button.
	 *
	 *  @param text  new button text
	 */
	public void setOkButtonText(String text)
	{
		fOkButton.setText(text);
	}
	
	/**
	 *    Set the text for the 'apply' button.
	 *
	 *  @param text  new button text
	 */
	public void setApplyButtonText(String text)
	{
		fApplyButton.setText(text);
	}

	/**
	 *    Set the text for the 'cancel' button.
	 *
	 *  @param text  new button text
	 */
	public void setCancelButtonText(String text)
	{
		fCancelButton.setText(text);
	}

    //////////////////////////////////////////////////////////////////////
    //   Methods that may need to be overridden by subclasses.
    //////////////////////////////////////////////////////////////////////
    
	/**
	 *    Respond to a press of the "ok" button.  Note that the default 
	 *  implementation of this method should suffice for most subclasses of
	 *  of this class.
	 */
	public void ok()
	{
		apply();
		fClosedReason = OK_CLOSED_REASON;
		setVisible(false);
	}

	/**
	 *    Apply the information in the dialog.  This method should
	 *  be overridden by any dialog that uses the apply button.
	 *    Note that the default implementation of the 'ok' method
	 *  uses this method to do this portion of its work.
	 */
	public void apply()
	{
	}

	/**
	 *  Respond to a press of the cancel button.  Note that the default 
	 *  implementation of this method should suffice for most subclasses of
	 *  of this class.
	 */
	public void cancel()
	{
		fClosedReason = CANCEL_CLOSED_REASON;
		setVisible(false);
	}

    /**
     *    Get the next field to navigate to in the GUI when the given
     *  component has focus.  By default this simply returns
	 *  'null', meaning there is no "next component", i.e. press the
	 *  ok button.
     *
     *  @param comp  the component we are currently on (this probably
     *               received some type of action event)
     *
     *  @return the next component to navigate to within the GUI or
     *          null if this is the last component to which we
     *          should navigate
     */
    protected Component getNextField(Component comp)
    {
		//  We always just want to press "OK", assuming it is available.
		return null;
    }

    /**
     *    Determine if the ok and apply buttons are allowed to be enabled at
	 *  this point in time.  By default, the ok and apply buttons are always
	 *  enabled.
     *
     *  @return  whether or not the ok and apply buttons should be enabled
     */
    protected boolean areOkActionsAllowed()
	{
		return true;
	}

    //////////////////////////////////////////////////////////////////
    //  Other
    //////////////////////////////////////////////////////////////////

    /**
	 *    Create and layout the components for this dialog, centering
	 *  the given component.  Note that this will create the standard
	 *  dialog buttons (ok, cancel, and, optionally, apply).
	 *    This method should be called by all children of the utility dialog
	 *  to add/layout the content of the dialog.  Note that this will change
	 *  the size of the GUI so any centering should occur after this
     *  method is called.
     *
     *  @param comp  component to center in the dialog
	 *  @param includeApply  This denotes whether or not the dialog includes 
	 *                       an apply button.  Note that by including an
	 *                       apply button, the dialog should override the
	 *                       'apply' method so that it can do something
	 *                       useful when this button is pressed.
     */
    protected void createStandardGui(Component comp, boolean includeApply)
    {
		JPanel contentPane = new JPanel(new BorderLayout());

		//  Add a margin.  Note that the bottom is done differently because
		//  the buttons themselves add a small margin.
		contentPane.setBorder(BorderFactory.createEmptyBorder(
			MARGIN_PIXELS, MARGIN_PIXELS, BOTTOM_MARGIN_PIXELS,
			MARGIN_PIXELS));

		//  Add the components.
		contentPane.add(comp, BorderLayout.CENTER);
		contentPane.add(createButtons(includeApply), BorderLayout.SOUTH);

        setContentPane(contentPane);
        pack();
    }

    /** 
     *    Add the normal action listener, for enter events, and document
     *  listener, for content changes, to the given text field.
     *
     *  @param textField  the text field to track for action events and
     *                    content changes
     */
    protected void addTextFieldListeners(JTextField textField)
    {
        textField.addActionListener(fEnterListener);
        textField.getDocument().addDocumentListener(fContentListener);
    }

    /**
     *    Update whether or not the ok button is enabled based on
     *  the response from the child implemented 'okAllowed' method.
     */
    protected void updateOkActionButtons()
    {
        if (fOkButton != null)
        {
			//  Wrap this in a call to invokeLater to make sure it is
			//  done in the event queue. 
			Runnable doRequestFocus = new Runnable() 
			{
				public void run() 
				{
					boolean okAllowed = areOkActionsAllowed();

					fOkButton.setEnabled(okAllowed);
					if (fApplyButton != null)
					{
						fApplyButton.setEnabled(okAllowed);
					}
				}
			};
			SwingUtilities.invokeLater(doRequestFocus);		
        }
    }

	//////////////////////////////////////////////////////////////////////
	//  Other
	//////////////////////////////////////////////////////////////////////

	/**
	 *    Request the given component be given focus.  This request is 
	 *  wrapped into a call to SwingUtilities.invokeLater to make sure
	 *  it is done in the event queue.
	 *
	 *  @param comp  the component that will get focus
	 */
	private void requestFocus(final Component comp)
	{
		//  Wrap the request for focus in a call to invokeLater to make sure it
		//  is done in the event queue. 
		Runnable doRequestFocus = new Runnable() 
		{
			public void run() 
			{
				comp.requestFocus();
			}
		};
		SwingUtilities.invokeLater(doRequestFocus);		
	}

	/**
	 *    Create the standard buttons used for the controlling the dialog, i.e.
	 *  the ok, apply, and cancel buttons.
	 *
	 *  @param includeApply  whether or not to include the "apply" button
	 */
	public ButtonPanel createButtons(boolean includeApply)
	{
		ButtonPanel butPanel = new ButtonPanel();

		//  Ok
		fOkButton = new JButton(DEF_OK_LABEL);
		ButtonUtil.setupButton(fOkButton, OK_ACT, fButtonListener, true,
		 					   null);
		fOkButton.setMnemonic('O');
		butPanel.add(fOkButton);
		
		//  Apply
		if (includeApply)
		{
			fApplyButton = new JButton(DEF_APPLY_LABEL);
			ButtonUtil.setupButton(fApplyButton, APPLY_ACT, fButtonListener, true,
		 						   null);
			fApplyButton.setMnemonic('A');
			butPanel.add(fApplyButton);
		}

		//  Cancel
		fCancelButton = new JButton(DEF_CANCEL_LABEL);
		ButtonUtil.setupButton(fCancelButton, CANCEL_ACT, fButtonListener,
		 						true, null);
		fCancelButton.setMnemonic('C');
		butPanel.add(fCancelButton);

		return butPanel;
	 }
}

//--- Development History  ---------------------------------------------------
//
//  $Log: UtilityDialog.java,v $
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/09/22 18:13:31  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
