//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/AbstractDataVisComponent.java,v 1.19 2006/03/14 14:57:16 chostetter_cvs Exp $
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeListener;

import gov.nasa.gsfc.commons.processing.activity.Pausable;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.InputListener;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.gui.swing.event.ChangeSource;


/**
 * This abstract class provides a data driven visualization component that 
 * listens for data events as an {@link InputListener}. When new data arrives
 * a repaint request will go on the AWT event queue.
 * 
 * <p>Requests to update the component come from either direct requests,
 * through the <code>repaint</code> method, or from AWT, through the 
 * <code>paintComponent</code> method.  
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author	Troy Ames
 */
public class AbstractDataVisComponent extends AbstractVisComponent 
	implements VisComponent, InputListener, ChangeListener, Pausable
{
	private static final String CLASS_NAME = 
		AbstractDataVisComponent.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Input related fields
	private Input fInput;
	private DataSet fCurrentDataSet;
	private boolean fPaused = false;

	/**
	 * Create a new chart component.
	 */
	public AbstractDataVisComponent()
	{
		this(new VisInput("DataVis Input"));
	}

	/**
	 * Create a new chart component with the given Input. An Input with the 
	 * given name must be registered with the component manager.
	 * 
	 * @param inputName the name of the Input to listen to.
	 */
	public AbstractDataVisComponent(String inputName)
	{
		super();
		
		MinimalComponent component = 
			Irc.getComponentManager().getComponent(inputName);
		
		if (component != null && component instanceof Input)
		{
			setInput((Input) component);
		}
	}
	
	/**
	 * Create a new chart component with the given Input.
	 * @param input the Input to listen to
	 */
	public AbstractDataVisComponent(Input input)
	{
		super();
		fInput = input;

		if (fInput == null)
		{
			fInput = new VisInput("DataVis Input");
		}

		fInput.addInputListener(this);
		
		if (!fInput.isStarted())
		{
			fInput.start();
		}
	}

	/**
	 * Create a new chart component.
	 * 
	 * @param input	Input source for data
	 * @param model	optional state change source
	 */
	public AbstractDataVisComponent(Input input, ChangeSource model)
	{
		this(input);
		
		if (model != null)
		{
			model.addChangeListener(this);
		}
	}

	/**
	 * Paint the component by asking all renderers to draw on the Graphics
	 * context. If this component is opaque this method will first draw the 
	 * background and then call the <code>rendererDraw</code> method.
	 *
	 * @param g  the graphics context
	 */
	public synchronized void paintComponent(Graphics g)
	{
	    Insets inset = getInsets();
	    int drawWidth = getWidth() - inset.left - inset.right;
	    int drawHeight = getHeight() - inset.top - inset.bottom;
		
	    if (isOpaque())
		{ 
	    	Color originalColor = g.getColor();
	    	
	    	// Paint background for component
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(originalColor);
		}
		
	    Rectangle2D rect = 
	    	new Rectangle(inset.left, inset.top, drawWidth, drawHeight);

	    // Let all renderers draw on the graphics object
	    rendererDraw((Graphics2D) g, rect, fCurrentDataSet);
	}
	
	/**
	 * Causes this InputListener to receive the given BasisBundleEvent.
	 * 
	 * @param event A DataListener
	 */
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		if (event != null)
		{
			if (sLogger.isLoggable(Level.FINEST)) {
				sLogger.fine("inputDescriptor: " + event.getDescriptor());
			}
			if (event.hasNewStructure())
			{
				BasisBundleDescriptor inputDescriptor = event.getDescriptor();
				BasisBundleId inputBasisBundleId = event.getBasisBundleId();
				// TODO what should we do
			}
			else if (event.isClosed())
			{
				BasisBundleId inputBasisBundleId = event.getBasisBundleId();
				// TODO what should we do
			}
		}
	}
			
	/**
	 * Causes this InputListener to receive the given DataSetEvent. Calls the 
	 * <code>repaint</code> method.
	 * 
	 * @param event A DatasetEvent
	 */
	public synchronized void receiveDataSetEvent(DataSetEvent event)
	{
		DataSet dataSet = event.getDataSet();
		//System.out.println("Dataset time:" + dataSet.getTimeStamp());
		
		if (!fPaused)
		{
			// Release any previously held DataSet
			if (fCurrentDataSet != null)
			{
				fCurrentDataSet.release();
			}
			
			fCurrentDataSet = dataSet;
			fCurrentDataSet.hold();
			
			repaint();
		}
	}
	
	/**
	 * Get the input component used for this visualization.
	 * 
	 * @return Returns the input.
	 */
	public Input getInput()
	{
		return fInput;
	}
	
	/**
	 * Set the Input component used for this visualization.
	 * 
	 * @param input The input to set.
	 */
	public void setInput(Input input)
	{
		if (fInput != null)
		{
			fInput.removeInputListener(this);
		}

		if (input != null)
		{
			input.addInputListener(this);
		}
		
		fInput = input;

		if (!fInput.isStarted())
		{
			fInput.start();
		}
	}

	/**
	 * Returns true if this visualization is paused, false otherwise. 
	 *
	 * @return True if this visualization is paused, false otherwise
	 */
	public synchronized boolean isPaused()
	{
		return fPaused;
	}

	/**
	 * Pauses this visualization.
	 */	 
	public synchronized void pause()
	{
		// Check if not currently paused
		if (!fPaused)
		{
			if (fCurrentDataSet != null)
			{
				// Copy the current data set so that we do not block
				// the data source and we have data for future repaints.
				DataSet dataSetCopy = fCurrentDataSet.copy();
				
				// Release original
				fCurrentDataSet.release();
				fCurrentDataSet = dataSetCopy;
			}
		}
		
		fPaused = true;
	}

	/**
	 * Resume this visualization.
	 */	
	public synchronized void resume()
	{
		fPaused = false;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataVisComponent.java,v $
//  Revision 1.19  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.18  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.17  2006/01/02 03:54:17  tames
//  Changed code to start Input.
//
//  Revision 1.16  2005/11/14 19:52:59  chostetter_cvs
//  Organized imports
//
//  Revision 1.15  2005/11/07 22:14:07  tames
//  Added constructor and better support for setting Input.
//
//  Revision 1.14  2005/10/19 19:19:38  chostetter_cvs
//  Organized imports
//
//  Revision 1.13  2005/09/22 18:46:54  tames
//  Added pause capability.
//
//  Revision 1.12  2005/09/14 21:57:55  chostetter_cvs
//  Organized imports
//
//  Revision 1.11  2005/09/13 22:31:45  tames
//  Partial Triple buffering implementation.
//
//  Revision 1.10  2005/07/18 21:14:29  tames_cvs
//  The receiveDataSet method now releases an old DataSet and
//  holds the new one.
//
//  Revision 1.9  2005/05/23 15:32:41  tames_cvs
//  Removed debug println.
//
//  Revision 1.8  2005/04/04 15:40:59  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.7  2005/03/15 17:24:50  chostetter_cvs
//  Organized imports
//
//  Revision 1.6  2005/03/12 12:43:34  smaher_cvs
//  Reduced System.out use that printed out entire DataSpace.
//
//  Revision 1.5  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
//  Revision 1.4  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//