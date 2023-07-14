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

package gov.nasa.gsfc.irc.gui.vis.tabular;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import gov.nasa.gsfc.commons.processing.activity.ActivityStateModel;
import gov.nasa.gsfc.commons.processing.activity.DefaultActivityStateModel;
import gov.nasa.gsfc.commons.processing.activity.Pausable;
import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.InputListener;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelModel;

/**
 * A StatisticsTable is an InputListener that receives data and passes it to
 * a <@link StatisticsTableModel>. There are also convenience methods to add 
 * StatisticsColumns to the table model.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/21 22:03:42 $
 * @author 	Troy Ames
 */
public class StatisticsTable extends JTable 
	implements InputListener, Startable, Pausable
{
	private static final String CLASS_NAME = StatisticsTable.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

// Input related fields
	private Input fInput;
	private DataSet fCurrentDataSet;

	// Channel data fields
	private ChannelModel fChannelModel;
	
	// Statistics Model
	private StatisticsTableModel fTableModel = null;
	private ActivityStateModel fStateModel = new DefaultActivityStateModel();

	/**
	 * Create a table with the given table model.
	 * 
	 * @param tableModel the table model
	 */
	public StatisticsTable(StatisticsTableModel tableModel)
	{
		super(tableModel);
			
		fTableModel = (StatisticsTableModel) tableModel;
	}

	/**
	 * Create a table with the given <code>Input</code>.
	 * 
	 * @param input the InputListener
	 */
	public StatisticsTable(Input input)
	{
		this(new StatisticsTableModel());
		
		fInput = input;

		if (fInput == null)
		{
			fInput = new DefaultInput("StatisticsTable Input");
		}

		fInput.addInputListener(this);

		if (!fInput.isStarted())
		{
			fInput.start();
		}
	}

	/**
	 * Create a table with the given <code>Input</code> and 
	 * <code>ChannelModel</code>.
	 * 
	 * @param input the InputListener
	 * @param channels the channels for this table to track
	 */
	public StatisticsTable(Input input, ChannelModel channels)
	{
		this(new StatisticsTableModel(channels));
		
		fInput = input;

		if (fInput == null)
		{
			fInput = new DefaultInput("StatisticsTable Input");
		}

		fInput.addInputListener(this);

		if (!fInput.isStarted())
		{
			fInput.start();
		}
	}

	/**
	 * Set the Statistics Columns for this table. The column list is 
	 * a "|" deliminated list of column names. The names must map to a 
	 * class listed in a typemap.
	 * 
	 * @param columnList a "|" delimited list of column names
	 */
	public void setStatisticsColumns(String columnList)
	{
		StatisticsTableModel model = (StatisticsTableModel) getModel();

		if (columnList != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(columnList, "|");
			
			int numNames = tokenizer.countTokens();
			
			for (int i = 0; i < numNames; i++)
			{
				String columnName = tokenizer.nextToken();
				StatisticsColumn column = 
					(StatisticsColumn) Irc.instantiateFromTypemap(
						"StatisticsTableTypes", columnName);
				
				model.addStatisticsColumn(column);
			}
		}
	}
	
    /**
	 * Creates default columns for the table from the data model using the
	 * <code>getColumnCount</code> method defined in the
	 * <code>TableModel</code> interface.
	 * <p>
	 * Clears any existing columns before creating the new columns based on
	 * information from the model.
	 * 
	 * @see #getAutoCreateColumnsFromModel
	 */
	public void createDefaultColumnsFromModel()
	{
		StatisticsTableModel model = (StatisticsTableModel) getModel();
		
		if (model != null)
		{
			// Remove any current columns
			TableColumnModel colModel = getColumnModel();
			while (colModel.getColumnCount() > 0)
			{
				colModel.removeColumn(colModel.getColumn(0));
			}

			// Create new columns from the data model info
			for (int i = 0; i < model.getColumnCount(); i++)
			{
				TableColumn newColumn = new TableColumn(i);
				
				newColumn.setCellRenderer(
					model.getStatisticsColumn(i).getCellRenderer());

				addColumn(newColumn);
			}
		}
	}

    // -- InputListener methods ------------------------------------------------
	
	/**
	 * Causes this InputListener to receive the given BasisBundleEvent.
	 * 
	 * @param event A BasisBundleEvent
	 */
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		fTableModel.receiveBasisBundleEvent(event);
	}
			
	/**
	 * Causes this InputListener to receive the given DataSetEvent. Calls the 
	 * <code>processDataSet</code> method with the DataSet contained
	 * in the event.
	 * 
	 * @param event A DataSetEvent
	 */
	public synchronized void receiveDataSetEvent(DataSetEvent event)
	{
		fTableModel.receiveDataSetEvent(event);
	}

	/**
	 *  Sets this table to the exception state, due to the given 
	 *  Exception. This table is first stopped, and then put into the 
	 *  exception state.
	 *  
	 *  @param exception The Exception resulting in the exception state
	 */
	public void declareException(Exception exception)
	{
		if (sLogger.isLoggable(Level.SEVERE))
		{
			String message = getName() + 
				" encountered Exception: stopping and entering exception state";
			
			sLogger.logp
				(Level.SEVERE, CLASS_NAME, "declareException", message, exception);
		}
		
		stop();
		
		fStateModel.declareException(exception);
	}
	
	/**
	 * Causes this Chart Component to start if the component has not
	 * been previously killed.
	 */
	public void start()
	{
		// If we are killed we cannot be started so just return
		if (fStateModel.isKilled())
		{
			return;
		}
		
		try
		{
			fStateModel.start();
			Input input = fInput;
			
			if (!input.isStarted())
			{
				input.start();
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}

	/**
	 * Returns true if this Component is currently started, false otherwise. 
	 *  
	 * @return True if this Component is currently started, false otherwise
	 */
	public boolean isStarted()
	{
		return (fStateModel.isStarted());
	}
	
	/**
	 * Causes this Component to stop.
	 */
	public void stop()
	{
		// If we are killed we cannot be stopped so just return
		if (fStateModel.isKilled())
		{
			return;
		}
		
		fStateModel.stop();
		fInput.stop();
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = getName() + " has stopped";
			
			sLogger.logp(Level.FINE, CLASS_NAME, "stop", message);
		}
	}	
	
	/**
	 * Returns true if this Component is stopped (i.e., not started), false
	 * otherwise.
	 * 
	 * @return True if this Component is stopped (i.e., not started), false
	 *         otherwise
	 */
	public boolean isStopped()
	{
		return (fStateModel.isStopped());
	}	
	
	/**
	 * Causes this Component to immediately cease operation and release 
	 * any allocated resources. A killed Component cannot subsequently be 
	 * started or otherwise reused.
	 */
	public void kill()
	{
		fStateModel.kill();
		fInput.kill();
				
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = getName() + " has been killed";
			
			sLogger.logp(Level.FINE, CLASS_NAME, "kill", message);
		}
	}
	
	/**
	 * Returns true if this Component is killed, false otherwise.
	 *
	 * @return True if this Component is killed, false otherwise
	 */
	public boolean isKilled()
	{
		return (fStateModel.isKilled());
	}
	
	/**
	 * Returns true if this visualization is paused, false otherwise. 
	 *
	 * @return True if this visualization is paused, false otherwise
	 */
	public synchronized boolean isPaused()
	{
		return fTableModel.isPaused();
	}

	/**
	 * Pauses this visualization.
	 */	 
	public synchronized void pause()
	{
		fTableModel.pause();
	}

	/**
	 * Resume this visualization.
	 */	
	public synchronized void resume()
	{
		fTableModel.resume();
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: StatisticsTable.java,v $
//  Revision 1.8  2006/03/21 22:03:42  tames_cvs
//  Made Pausable and added support for interface.
//
//  Revision 1.7  2006/03/21 18:59:33  tames_cvs
//  Changed constructor so that Input is started automatically.
//
//  Revision 1.6  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.5  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/11/03 22:34:36  tames
//  Added StatisticsTable(Input) constructor to support tables that do not need
//  a ChannelModel.
//
//  Revision 1.3  2005/08/31 20:55:08  tames_cvs
//  Made this class implement Startable so that it can be started from the
//  generic popup menu.
//
//  Revision 1.2  2005/04/04 15:40:59  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//