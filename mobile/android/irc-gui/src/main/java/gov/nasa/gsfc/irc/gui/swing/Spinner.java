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

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import gov.nasa.gsfc.irc.gui.util.ButtonUtil;

/**
 *    This class provides a small, graphical control for increasing/
 *  decreasing values.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/12/01 21:50:58 $
 *  @author	    Ken Wootton
 */
public class Spinner extends JPanel implements ActionListener, MouseListener
{
	/**
	 *    Action command for the up button.
	 */
	public static final String UP_ACT = "UpActionCommand";

	/**
	 *    Action command for the down button.
	 */
	public static final String DOWN_ACT = "DownActionCommand";

	/**
	 *    This is the default period for additional spinner button
	 *  movements when a spinner button is held down by the user.
	 */
	public static final int DEF_UPDATE_PERIOD = 100;
	
	public static final String IMAGE_DIR = "resources/images/";
	//  When a spinner button is held down, start making changes
	//  immediately.
	private static final int INITIAL_TIMER_DELAY = 0;

	//  Icon locations
	private static final String UP_ICON = "SpinnerUp6x3.gif";
	private static final String DOWN_ICON = "SpinnerDown6x3.gif";

	//  Tool tips
	private static final String UP_TIP = "Increase the Value";
	private static final String DOWN_TIP = "Decrease the Value";

	//  Grid Layout parameters
	private static final int NUM_COLUMNS = 1;
	private static final int NUM_BUTTONS = 2;
	private static final int HORIZONTAL_GAP = 0;
	private static final int VERTICAL_GAP = 0;

	//  We need to ax the margin so that the buttons can be as
	//  small as possible.
	private static final Insets BUTTON_MARGIN = new Insets(0, 0, 0, 0);

	//  This timer is used to continuously send spinner updates when a 
	//  button is held down.
	private Timer fHeldButTimer = new Timer(DEF_UPDATE_PERIOD, this);

    private EventListenerList fListenerList = new EventListenerList();

	//  Buttons
	private JButton fUpBut = null;
	private JButton fDownBut = null;
	
	//  This is the button that is currently held down, if there is one.
	private JButton fHeldBut = null;

	/**
	 *    Constructor.  Create the GUI components.
	 */
	public Spinner()
	{
		super(new GridLayout(NUM_BUTTONS, NUM_COLUMNS, HORIZONTAL_GAP, 
							 VERTICAL_GAP));

		//  Up button
		fUpBut = ButtonUtil.createButton(this.getClass(), 
										 IMAGE_DIR + UP_ICON,
										 UP_ACT, null, true, UP_TIP);
		fUpBut.setMargin(BUTTON_MARGIN);
		fUpBut.addMouseListener(this);
		add(fUpBut);

		//  Down button
		fDownBut = ButtonUtil.createButton(this.getClass(), 
										   IMAGE_DIR + DOWN_ICON,
										   DOWN_ACT, null, true, DOWN_TIP);
		fDownBut.setMargin(BUTTON_MARGIN);
		fDownBut.addMouseListener(this);
		add(fDownBut);

		fHeldButTimer.setInitialDelay(INITIAL_TIMER_DELAY);
	}

	/**
	 *    Change the period used to create additional spinner events
	 *  that occur while a spinner button is held down.
	 *
	 *  @param updatePeriod  the time between spinner events when a button
	 *                       is held down
	 */
	public void setUpdatePeriod(int updatePeriod)
	{
		fHeldButTimer.setDelay(updatePeriod);
	}

	/**
	 *    Get the period used to create additional spinner events
	 *  that occur while a spinner button is held down.
	 *
	 *  @return  the time between spinner events when a button
	 *           is held down
	 */
	public int getUpdatePeriod()
	{
		return fHeldButTimer.getDelay();
	}

	/**
	 *    Cancel any updates pending or future updates that are generated
	 *  by the current button press.
	 *    This method is useful when errors occur during a spinner update
	 *  that may cause a modal dialog to appear.  The problem with this
	 *  is that the corresponding mouse released event will not trigger
	 *  and updates to the spinner will queue up while the user contemplates
	 *  their next action.
	 */
	public void cancelUpdate()
	{
		//  Reset the held button and stop any more spinner events.
		fHeldButTimer.stop();
		fHeldBut = null;

		//  Sometimes, particularly when spinning events are cancelled after
		//  a button press and the focus changes, the buttons may left in a 
		//  strange selected state because a mouse pressed event occurs 
		//  without a corresponding mouse released.  This fixes any problems 
		//  that may occur.
		fUpBut.setSelected(false);
		fDownBut.setSelected(false);
	}

	/**
	 *    Add a listener for changes to the spinner.
	 *
	 *  @param l  the listener
	 */
	public void addSpinnerListener(SpinnerListener l)
	{
		fListenerList.add(SpinnerListener.class, l);
	}

	/**
	 *    Remove a listener for changes to the spinner.
	 *
	 *  @param l  the listener
	 */
	public void removeSpinnerListener(SpinnerListener l)
	{
		fListenerList.remove(SpinnerListener.class, l);
	}

	/**
	 *    Invoked when the timer for a held button is activated.
	 *  This is used to move the spinner at the specified timer interval.
	 *
	 *  @param e  the action event
	 */
	public void actionPerformed(ActionEvent e)
	{
		moveSpinner();
	}

	/**
	 *    Invoked when a button on the spinner is pressed.
	 *
	 *  @param e  the mouse event
	 */
	public void mousePressed(MouseEvent e)
	{
		//  Use a timer to move the spinner more while the button is held.
		//  Note that the initial delay is zero to make sure this
		//  happens at least once for each button press.
		fHeldBut = (JButton) e.getSource();
		fHeldButTimer.start();
	}

	/**
	 *    Invoked when a button on the spinner is released.
	 *
	 *  @param e  the mouse event
	 */
	public void mouseReleased(MouseEvent e)
	{
		cancelUpdate();
	}

	/**
	 *    This is ignored.
	 */
	public void mouseClicked(MouseEvent e)
	{
	}

	/**
	 *    This is ignored.
	 */
	public void mouseEntered(MouseEvent e)
	{
	}

	/**
	 *    This is ignored.
	 */
	public void mouseExited(MouseEvent e)
	{
	}

	/**
	 *    Move the spinner in the direction of the currently held
	 *  button.
	 */
	protected void moveSpinner()
	{
		//  Fire the proper event to listeners depending on the button that is
		//  currently held down.
		if (fHeldBut == fUpBut)
		{
			fireSpinnerIncreased();
		}
		else if (fHeldBut == fDownBut)
		{
			fireSpinnerDecreased();
		}
	}

	/**
	 *    Fire an event to all listeners that the value used with this
	 *  spinner should be increased.
	 */
	protected void fireSpinnerIncreased()
	{
		Object [] listeners = fListenerList.getListenerList();
		SpinnerEvent spinnerEvent = new SpinnerEvent(this);
			
		//  Process the listeners last to first, notifying
		//  those that are interested in this event.  Note
		//  that the 'listeners' array is made up of 
		//  ListenerType - listener pairs, thereby requiring 
		//  the strange index change of 2.
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			//  Deliver the event
			((SpinnerListener) listeners[i + 1]).spinnerIncreased(
				spinnerEvent);
		}
	}

	/**
	 *    Fire an event to all listeners that the value used with this
	 *   spinner should be decreased.
	 */
	protected void fireSpinnerDecreased()
	{
		Object [] listeners = fListenerList.getListenerList();
		SpinnerEvent spinnerEvent = new SpinnerEvent(this);
			
		//  Process the listeners last to first, notifying
		//  those that are interested in this event.  Note
		//  that the 'listeners' array is made up of 
		//  ListenerType - listener pairs, thereby requiring 
		//  the strange index change of 2.
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			//  Deliver the event
			((SpinnerListener) listeners[i + 1]).spinnerDecreased(
				spinnerEvent);
		}
	}

	/**
	 *    Test the GUI.
	 *
	 *  @param args  ignored
	 */
    public static void main(String[] args)
    {
		javax.swing.JFrame testFrame = new javax.swing.JFrame();
		Spinner spinner = new Spinner();

		//  Set up frame.
		testFrame.setContentPane(spinner);
		testFrame.setSize(new java.awt.Dimension(60, 60));
		testFrame.setVisible(true);
    }
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Spinner.java,v $
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/09/22 18:13:31  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.2  2004/09/21 22:09:32  tames_cvs
//  Removed references to obsolete methods or classes.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
