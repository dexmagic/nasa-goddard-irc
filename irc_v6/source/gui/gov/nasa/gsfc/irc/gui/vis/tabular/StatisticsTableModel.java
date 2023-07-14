//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/tabular/StatisticsTableModel.java,v 1.9 2006/03/21 22:03:42 tames_cvs Exp $
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

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import gov.nasa.gsfc.commons.processing.activity.Pausable;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.InputListener;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelModel;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelRenderInfo;

/**
 * This table model tracks and provides to all listeners a set
 * of given statistics. Statistic column calculators are added to the model 
 * using the <code>addStatisticsColumn</code> method. A StatisticsTableModel 
 * typically needs to receive data either 
 * directly as a listener to an {@link Input} or indirectly from 
 * a {@link StatisticsTable}. For convenience the input can be passed to 
 * the constructor. If this StatisticsTableModel is also being used as a legend
 * for a VisComponent then a {@link ChannelModel} is also needed. If provided 
 * then only the channels in the channel model will be included in the
 * table, otherwise all channels received by the input will be included.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/03/21 22:03:42 $
 * @author Troy Ames
 */
public class StatisticsTableModel extends AbstractTableModel
	implements InputListener, Pausable
{
	private Object[] fStatistics = null;

	// Statistics Calculators
	private ArrayList fCalculators = new ArrayList();
	
	//  Table Properties
	private String[] fColNames = null;
	private Class[] fColClasses = null;

	// Input related fields
	private Input fInput;
	private DataSet fCurrentDataSet;
	
	// Channel data fields
	private ChannelModel fChannelModel;
	private boolean fPaused = false;

	/**
	 * Create a new table model without an input or channel model.
	 */
	public StatisticsTableModel()
	{
		this(null, null);
	}

	/**
	 * Create a new table model using the given input.
	 * 
	 * @param input	Input source for data
	 */
	public StatisticsTableModel(Input input)
	{
		this(input, null);
	}

	/**
	 * Create a new table model using the given ChannelModel.
	 * 
	 * @param channels 	the model for all the channels to be included in the 
	 * 					table.
	 */
	public StatisticsTableModel(ChannelModel channels)
	{
		this(null, channels);
	}

	/**
	 * Create a new table model.
	 * 
	 * @param input		Input source for data or null
	 * @param channels 	the model for all the channels to be included in the 
	 * 					table or null.
	 */
	public StatisticsTableModel(Input input, ChannelModel channels)
	{
		fInput = input;

		fChannelModel = channels;
		
		if (fInput != null)
		{
			fInput.addInputListener(this);
		}
	}

	/**
	 * Add the given <code>StatisticsColumn</code> to this model.
	 * 
	 * @param statistic the statistics column calculator to add
	 */
	public synchronized void addStatisticsColumn(StatisticsColumn statistic)
	{
		fCalculators.add(statistic);
		fColNames = getColNames();
		fColClasses = getColClasses();
		fireTableStructureChanged();
	}
	
	/**
	 * Remove the given <code>StatisticsColumn</code> from this model.
	 * 
	 * @param statistic the statistics column calculator to add
	 */
	public synchronized void removeStatisticsColumn(StatisticsColumn statistic)
	{
		fCalculators.remove(statistic);
		fColNames = getColNames();
		fColClasses = getColClasses();
		fireTableStructureChanged();
	}
	
	/**
	 * @param i
	 * @return
	 */
	public StatisticsColumn getStatisticsColumn(int index)
	{
		return (StatisticsColumn) fCalculators.get(index);
	}
	
	//-- Support for the table model -------------------------------------------
	
	/**
	 * Get the number of columns in the table.
	 * 
	 * @return the number of columns in the table.
	 */
	public synchronized int getColumnCount()
	{
		int colCount = 0;

		if (fColNames != null)
		{
			colCount = fColNames.length;
		}

		return colCount;
	}

	/**
	 * Get the name of the column with the given index.
	 * 
	 * @param columnIndex the index of the column in question
	 * @return the name of that column
	 */
	public synchronized String getColumnName(int columnIndex)
	{
		String colName = null;

		if (fColNames != null)
		{
			colName = fColNames[columnIndex];
		}

		return colName;
	}

	/**
	 * Get the class of the column with the given index.
	 * 
	 * @param columnIndex the index of the column in question
	 * @return the class of that column
	 */
	public synchronized Class getColumnClass(int columnIndex)
	{
		Class colClass = Object.class;

		if (fColClasses != null)
		{
			colClass = fColClasses[columnIndex];
		}

		return colClass;
	}

	/**
	 * Get the number of rows in the table.
	 * 
	 * @return the number of rows in the table
	 */
	public synchronized int getRowCount()
	{
		int rowCount = 0;

		//  We have a row in the table for each channel.
		if (fStatistics != null)
		{
			rowCount = fStatistics.length;
		}

		return rowCount;
	}

	/**
	 * Report whether or not the table cell at the given coordinates is
	 * editable.
	 * 
	 * @param rowIndex row of the cell in question
	 * @param columnIndex column of the cell in question
	 * @return whether or not the cell is editable
	 */
	public synchronized boolean isCellEditable(int rowIndex, int colIndex)
	{
		//  This table is not editable at all.
		return false;
	}

	/**
	 * Set the value at the given coordinates to the given value.
	 * 
	 * @param aValue new value for the coordinate
	 * @param rowIndex the row of the cell to change
	 * @param columnIndex the column of the cell to change
	 */
	public synchronized void setValueAt(Object aValue, int rowIndex, 
									   int columnIndex)
	{
		//  No-op.  This table is not editable at all.
	}
 
	/**
	 * Get the value at the specified coordinates.
	 * 
	 * @param rowIndex the row of the cell of interest
	 * @param columnIndex the column of the cell of interest
	 */
	public synchronized Object getValueAt(int rowIndex, int columnIndex)
	{
		Object value = null;

		if (fStatistics != null)
		{
			//  Get the proper statistic, indicated by the column, from
			//  the proper input, indicated by the row.
			if(fStatistics.length<=rowIndex || fStatistics[rowIndex]==null ||
					((Object[])fStatistics[rowIndex]).length<=columnIndex) {
				return value;
			}
			
			value = ((Object[]) fStatistics[rowIndex])[columnIndex];
		}

		return value;
	}

	// -- InputListener methods ------------------------------------------------
	
	/**
	 * Causes this InputListener to receive the given BasisBundleEvent.
	 * 
	 * @param event A BasisBundleEvent
	 */
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		if (event != null)
		{
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
	 * <code>updateColumns</code> method with the DataSet contained
	 * in the event.
	 * 
	 * @param event A DataSetEvent
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
			
			// Update the cached table data
			if (fChannelModel != null)
			{
				updateColumns(dataSet, fChannelModel);
			}
			else
			{
				updateColumns(dataSet);
			}

			//  Update the table data.	
			fireTableDataChanged();
		}		
	}

	//-- Other -----------------------------------------------------------------

	/**
	 * Updates the cached table values based on the given data set and channels.
	 * Only the channels that are in both the channel model and data set
	 * will be added.
	 * 
	 * @param dataSet the data set for calculating statistics on
	 * @param channels the channels to include in the table
	 */
	protected synchronized void updateColumns(
			DataSet dataSet, ChannelModel channels)
	{
		Iterator basisSets = dataSet.getBasisSets().iterator();
		BasisSet basisSet = (BasisSet) basisSets.next();

		//  Channel information
		ChannelRenderInfo[] renderInfoArr = 
			(ChannelRenderInfo[]) channels.getRenderInfos();
		
		int rows = renderInfoArr.length;
		Object[] statsPerChannel = new Object[rows];
		Object[] calculators = fCalculators.toArray();

		//  Calculate statistics for each individual channel.
		for (int channel = 0; channel < rows; channel++)
		{
			DataBuffer dataBuffer = 
				basisSet.getDataBuffer(renderInfoArr[channel].getName());
			
			//  If the dataBuffer doesn't exist, usually caused by the data
			//  request associated with the input does not include the channel.
			if (dataBuffer == null)
			{
				continue;
			}
			
			int columns = calculators.length;
			Object[] statValues = new Object[columns];
			
			for (int stat = 0; stat < columns; stat++)
			{
				statValues[stat] = 
					((StatisticsColumn) calculators[stat]).getStatistic(
						dataBuffer, renderInfoArr[channel]);
			}
			
			statsPerChannel[channel] = statValues;
		}

		fStatistics = statsPerChannel;

		//  If needed, reset the column classes.  Note that these are
		//  collected only when needed for performance reasons.
		if (fColClasses == null)
		{
			fColClasses = getColClasses();
		}

		//  If needed, refresh the table itself.
//		if (tableStructureChanged)
//		{
//			fireTableStructureChanged();
//		}

		//  Update the table data.			
		fireTableDataChanged();
	}

	/**
	 * Updates the cached table values based on the given data set.
	 * All the channels in the data set will be added.
	 * 
	 * @param dataSet the data set for calculating statistics on
	 */
	protected synchronized void updateColumns(DataSet dataSet)
	{
		Iterator basisSets = dataSet.getBasisSets().iterator();
		ArrayList statsPerChannel = new ArrayList();
		Object[] calculators = fCalculators.toArray();

		while(basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
	
			// Calculate statistics for each individual channel.
			for (Iterator dataBuffers = basisSet.getDataBuffers();
				dataBuffers.hasNext();)
			{	
				DataBuffer buffer = (DataBuffer) dataBuffers.next();
				int columns = calculators.length;
				Object[] statValues = new Object[columns];
				
				for (int stat = 0; stat < columns; stat++)
				{
					statValues[stat] = 
						((StatisticsColumn) calculators[stat]).getStatistic(
							buffer, null);
				}
				
				statsPerChannel.add(statValues);
			}
		}

		fStatistics = statsPerChannel.toArray();

		//  If needed, reset the column classes.  Note that these are
		//  collected only when needed for performance reasons.
		if (fColClasses == null)
		{
			fColClasses = getColClasses();
		}

		//  If needed, refresh the table itself.
//		if (tableStructureChanged)
//		{
//			fireTableStructureChanged();
//		}

		//  Update the table data.			
		fireTableDataChanged();
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
	 * Set the input component used for this visualization. Removes this 
	 * table model as an input listener from any previously set input and adds
	 * it as a listener to the given input.
	 * 
	 * @param input The input to set.
	 */
	public void setInput(Input input)
	{
		if (fInput != null)
		{
			fInput.removeInputListener(this);
		}
		
		fInput = input;
		
		if (fInput != null)
		{
			fInput.addInputListener(this);
		}
	}

	/**
	 * Get an array of classes that matches the values of the given statistics.
	 * This array will typically serve as the column classes for the table.
	 * 
	 * @return an array of the classes for the given set of statistics
	 */
	private Class[] getColClasses()
	{
		Class[] classes = null;
		Object[] calculators = fCalculators.toArray();
		int arraySize = calculators.length;

		if (arraySize > 0)
		{
			classes = new Class[arraySize];
			
			for (int i = 0; i < arraySize; i++)
			{
				//  Try to get the class directly from the value.
				if (calculators[i] != null)
				{
					classes[i] = 
						((StatisticsColumn) calculators[i]).getResultClass();
				}
			}
		}

		return classes;
	}

	/**
	 * Get an array of classes that matches the values of the given statistics.
	 * This array will typically serve as the column classes for the table.
	 * 
	 * @return an array of the classes for the given set of statistics
	 */
	private String[] getColNames()
	{
		String[] names = null;
		Object[] calculators = fCalculators.toArray();
		int arraySize = calculators.length;

		if (arraySize > 0)
		{
			names = new String[arraySize];
			
			for (int i = 0; i < arraySize; i++)
			{
				//  Try to get the class directly from the value.
				if (calculators[i] != null)
				{
					names[i] = 
						((StatisticsColumn) calculators[i]).getName();
				}
			}
		}

		return names;
	}

	/**
	 * Returns true if this model is paused, false otherwise. 
	 *
	 * @return True if this model is paused, false otherwise
	 */
	public synchronized boolean isPaused()
	{
		return fPaused;
	}

	/**
	 * Pauses this model.
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
	 * Resume this model.
	 */	
	public synchronized void resume()
	{
		fPaused = false;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: StatisticsTableModel.java,v $
//  Revision 1.9  2006/03/21 22:03:42  tames_cvs
//  Made Pausable and added support for interface.
//
//  Revision 1.8  2006/03/02 20:14:10  tames_cvs
//  Fixed potential Index out of bounds exception in the getValueAt method.
//
//  Revision 1.7  2005/11/04 19:34:50  tames
//  Fixed updateColumns(DataSet) method to correctly iterate over all Buffers.
//
//  Revision 1.6  2005/11/03 22:35:54  tames
//  Added a default constructor to support instances where a ChannelModel
//  is not needed.
//
//  Revision 1.5  2005/08/23 20:52:57  mn2g
//  added some extra error checking
//
//  Revision 1.4  2005/07/15 17:44:41  tames
//  Removed obsolete call to getDataBuffersAsArray().
//
//  Revision 1.3  2005/04/04 15:40:59  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.2  2005/02/01 20:53:54  chostetter_cvs
//  Revised releasable BasisSet design, release policy
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.2  2004/12/14 21:43:33  tames
//  Javadoc updates only
//
//  Revision 1.1  2004/12/10 16:37:13  tames
//  Initial Version
//
//
