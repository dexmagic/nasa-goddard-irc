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

package gov.nasa.gsfc.irc.gui.svg;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGUserAgent;

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
import gov.nasa.gsfc.irc.gui.vis.AbstractDataVisComponent;
import gov.nasa.gsfc.irc.gui.vis.VisInput;


/**
 * This class provides a data driven SVG visualization component that 
 * listens for data events as an {@link InputListener}. When new data arrives
 * an update request will go on the Canvas event queue. This class delegates 
 * the actual modification to one or more 
 * {@link gov.nasa.gsfc.irc.gui.svg.SvgUpdater SvgUpdaters}.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author 	Troy Ames
 */
public class DefaultDataSvgComponent extends SvgComponent
	implements DataSvgComponent, InputListener, Pausable
{
	private static final String CLASS_NAME = 
		AbstractDataVisComponent.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Input related fields
	private Input fInput;
	private DataSet fCurrentDataSet;
	private boolean fPaused = false;
    private UpdateManager fUpdateManager;
    
	// List of SvgUpdaters
	private ArrayList fUpdaterList = new ArrayList();

    /**
     * Creates a new DefaultDataSvgComponent.
     */
    public DefaultDataSvgComponent() 
    {
		this(new VisInput("DataSvg Input"));
    }

	/**
	 * Create a new DefaultDataSvgComponent component with the given Input. An
	 * Input with the given name must be registered with the component manager.
	 * 
	 * @param inputName the name of the Input to listen to.
	 */
	public DefaultDataSvgComponent(String inputName)
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
	 * Create a new DefaultDataSvgComponent component.
	 */
	public DefaultDataSvgComponent(Input input)
	{
		super(null, true, false);
		fInput = input;

		if (fInput == null)
		{
			fInput = new VisInput("DataSvg Input");
		}

		fInput.addInputListener(this);
		
		// Force the canvas to always be dynamic even if the current
        // document does not contain scripting or animation.
        setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
	}

	/**
     * Creates a new DefaultDataSvgComponent.
     *
     * @param ua a SVGUserAgent instance or null.
     * @param eventsEnabled Whether the GVT tree should be reactive to mouse
     *                      and key events.
     * @param selectableText Whether the text should be selectable.
     */
    public DefaultDataSvgComponent(Input input,
    		SVGUserAgent ua,
    		boolean eventsEnabled,
    		boolean selectableText) 
    {
    	super(ua, eventsEnabled, selectableText);
		fInput = input;

		if (fInput == null)
		{
			fInput = new VisInput("DataSvg Input");
		}

		fInput.addInputListener(this);
		
		// Force the canvas to always be dynamic even if the current
        // document does not contain scripting or animation.
        setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
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
		
		if (!fPaused)
		{
			// Release any previously held DataSet
			if (fCurrentDataSet != null)
			{
				fCurrentDataSet.release();
			}
			
			fCurrentDataSet = dataSet;
			fCurrentDataSet.hold();
			
			if (fUpdateManager == null)
			{
				// The update manager only becomes available after the first 
				// rendering completes so we need to keep retrying until
				// the SVG is ready to be updated.
			    fUpdateManager = getUpdateManager();
			}
			
			if (fUpdateManager != null)
			{
				fUpdateManager.getUpdateRunnableQueue().invokeLater(
					new Runnable()
					{
						public void run()
						{
							updateSvg(DefaultDataSvgComponent.this, fCurrentDataSet);
						}
					});
			}
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

    /**
	 * Adds an updater to this Component. When this component receives new data, 
	 * each updater added will have their <code>update</code> method
	 * called in the order they were added.
	 * 
	 * @param updater The updater to add to this component.
	 * @see SvgUpdater#update(SvgComponent, DataSet)
	 */
	public synchronized void addUpdater(SvgUpdater updater)
	{
		fUpdaterList.add(updater);		
	}

    /**
	 * Removes an updater from this Component.
	 * 
	 * @param updater The updater to remove from this component.
	 */
	public synchronized void removeUpdater(SvgUpdater updater)
	{
		fUpdaterList.remove(updater);		
	}

	/**
	 * Get the updaters that have been added to this component.
	 * 
	 * @return an array of known updaters
	 */
	public synchronized SvgUpdater[] getUpdaters()
	{
		SvgUpdater[] updaterArray = new SvgUpdater[fUpdaterList.size()];
		
		return (SvgUpdater[]) fUpdaterList.toArray(updaterArray);
	}

	/**
	 * Allow all updaters to modify the given SVG context in the order 
	 * they were added to this component. This method
	 * is called from a <code>Runnable</code> on the canvas update thread.
	 *  
	 * @param svg  the svg context on which to update.
	 * @param dataSet the data to utilize if applicable, may be null.
	 */
	protected synchronized void updateSvg(SvgComponent svg, DataSet dataSet)
	{
		int size = fUpdaterList.size();

		// Let all updaters modify the SVG object
	    for (int i=0; i < size; i++)
		{
			((SvgUpdater) fUpdaterList.get(i)).update(svg, dataSet);
		}		
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataSvgComponent.java,v $
//  Revision 1.3  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.2  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/11/07 22:12:02  tames
//  SVG support
//
//