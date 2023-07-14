//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/DefaultDataVisComponent.java,v 1.11 2006/03/15 20:43:37 tames_cvs Exp $
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

package gov.nasa.gsfc.irc.gui.vis;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.gui.swing.event.ChangeSource;

/**
 * This class provides a concrete implementation of an AbstractDataVisComponent
 * a subclass of JComponent that uses one or more renderers to draw its two
 * dimensional data to the screen. This class will prompt a renderer to draw as
 * necessary by calling the <code>draw</code> method on the renderer. This
 * component must be started with the <code>start</code> method.
 * <p>
 * Requests to update the component come from either direct requests, through
 * the <code>repaint</code> method, or from AWT, through the
 * <code>paintComponent</code> method.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/03/15 20:43:37 $
 * @author Troy Ames
 */
public class DefaultDataVisComponent extends AbstractDataVisComponent 
	implements Startable
{
	private static final String CLASS_NAME = 
			DefaultDataVisComponent.class.getName();
	
	/**
	 * Default constructor of a DataVisComponent.
	 */
	public DefaultDataVisComponent()
	{
		this(null, null);
	}

	/**
	 * Constructs a new DataVisComponent on the Input having the given
	 * fully-qualified (and thus globally unique) name.
	 * 
	 * @param inputName The fully-qualified (and thus globally unique) name of
	 *            the Input to listen to
	 */
	public DefaultDataVisComponent(String inputName)
	{
		super(inputName);
	}
	
	/**
	 * Constructs a new DataVisComponent on the the given Input. If the given
	 * Input is null then a default one will be created.
	 * 
	 * @param input The Input for this DataVisComponent
	 */
	public DefaultDataVisComponent(Input input)
	{
		this(input, null);
	}

	/**
	 * Constructs a new DataVisComponent on the given Input and using the given 
	 * ChangeSource model
	 * 
	 * @param input The Input for this DataVisComponent
	 * @param model An optional state ChangeSource
	 */
	public DefaultDataVisComponent(Input input, ChangeSource model)
	{
		super(input, model);
	}
	
	/**
	 * Causes this Chart Component's Input to start if the component has not
	 * been previously killed. 
	 */
	public void start()
	{
		Input input = getInput();
		
		if (input != null && !input.isStarted())
		{
			input.start();
		}
	}
	
	/**
	 * Returns true if this Component's Input is currently started, false
	 * otherwise.
	 * 
	 * @return True if this Component's Input is currently started, false
	 *         otherwise
	 */
	public boolean isStarted()
	{
		boolean result = false;
		
		Input input = getInput();
		
		if (input != null)
		{
			result = input.isStarted();
		}

		return result;
	}
	
	/**
	 * Causes this Component's Input to stop.
	 */
	public void stop()
	{
		Input input = getInput();
		
		if (input != null)
		{
			input.stop();
		}
	}	
	
	/**
	 * Returns true if this Component's Input is stopped (i.e., not started),
	 * false otherwise.
	 * 
	 * @return True if this Component's Input is stopped (i.e., not started),
	 *         false otherwise
	 */
	public boolean isStopped()
	{
		boolean result = true;
		
		Input input = getInput();
		
		if (input != null)
		{
			result = input.isStopped();
		}

		return result;
	}	

	/**
	 * Causes this Component's Input to immediately cease operation and release
	 * any allocated resources. A killed Component cannot subsequently be
	 * started or otherwise reused.
	 */
	public void kill()
	{
		Input input = getInput();
		
		if (input != null)
		{
			input.kill();
		}
	}
	
	/**
	 * Returns true if this Component's Input is killed, false otherwise.
	 *
	 * @return True if this Component's Input is killed, false otherwise
	 */
	public boolean isKilled()
	{
		boolean result = false;
		
		Input input = getInput();
		
		if (input != null)
		{
			result = input.isKilled();
		}

		return result;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataVisComponent.java,v $
//  Revision 1.11  2006/03/15 20:43:37  tames_cvs
//  Remove namespace related methods and interface. This class is now a component only in the sense of a swing component.
//
//  Revision 1.10  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/11/07 22:15:42  tames
//  Added constructor with (String inputName)
//
//  Revision 1.8  2005/04/16 04:06:03  tames
//  Changes to reflect refactored state and activity packages.
//
//  Revision 1.7  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.6  2005/01/27 21:38:02  chostetter_cvs
//  Implemented new exception state and default exception behavior for Objects having ActivityState
//
//  Revision 1.5  2004/12/20 22:41:29  tames
//  Visualization property editing support added.
//
//  Revision 1.4  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//