//=== File Prolog ============================================================
//
//
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  
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

package gov.nasa.gsfc.irc.testing.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import gov.nasa.gsfc.irc.gui.controller.SwixController;


/**
 * @author tames
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChartController implements SwixController
{
	private Component fView = null;

    /** The most recent value added. */
    private double fLastValue = 100.0;

	
	public Action testAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("ChartController.actionPerformed()" + e.toString());
			System.out.println("Event Source" + e.getSource().toString());
		}
	};
	
	/**
	 * 
	 */
	public ChartController()
	{
		super();
		System.out.println("ChartController cotr():");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 */
	public ChartController(String s)
	{
		System.out.println("ChartController cotr(String):" + s.toString());
		// TODO Auto-generated constructor stub
	}

	public void setView(Component comp)
	{
		System.out.println("setView called on " + this.toString() + ":" + comp.toString());
		
		if (fView == null)
		{
			fView = comp;

		}
	}
}
