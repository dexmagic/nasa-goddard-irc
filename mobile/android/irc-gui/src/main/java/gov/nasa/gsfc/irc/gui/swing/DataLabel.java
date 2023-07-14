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

import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.JLabel;

import gov.nasa.gsfc.commons.numerics.formats.SciDecimalFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.InputListener;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.MinimalComponent;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;
import gov.nasa.gsfc.irc.gui.vis.VisInput;

/**
 * The DataLabel class is a simple data driven visual component. This class is
 * an {@link gov.nasa.gsfc.irc.algorithms.InputListener InputListener} that 
 * displays the most recent value for a specified buffer of an optionally
 * specified BasisBundle. The buffer value to display is specified by the
 * <code>setBufferName</code> method. The BasisSet containing the buffer
 * is specified by the <code>setBasisBundleId</code> method. If the BasisSet
 * is not specified then the first occurance of the buffer in any BasisSet
 * received will be used.
 * 
 * <p>The default format pattern is "<code>###0.0##</code>".
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author 	Troy Ames
 */
public class DataLabel extends JLabel implements InputListener
{
	//  Default format pattern strings
	private static final String DECIMAL_PATTERN = "###0.0##";

	private Input fInput;
	private BasisBundleId fBasisBundleId;
	private String fBufferName;
	private ValueFormat fFormatter;

	/**
	 * Constructs a DataLabel with a default Input.
	 */
	public DataLabel()
	{
		this(new VisInput("DataLabel Input"));
	}
	
	/**
	 * Create a new DataLabel component with the given Input. An Input with the 
	 * given name must be registered with the component manager.
	 * 
	 * @param inputName the name of the Input to listen to.
	 */
	public DataLabel(String inputName)
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
	 * Create a new DataLabel with the given input. If
	 * input is null then a default one will be created. A default 
	 * LabelFormatter is also created.
	 * 
	 * @param input the data input for this DataLabel.
	 */
	public DataLabel(Input input)
	{
		super();
		fInput = input;

		if (fInput == null)
		{
			fInput = new VisInput("DataLabel Input");
		}

		if (fFormatter == null)
		{
			DecimalFormat format = new DecimalFormat(DECIMAL_PATTERN);
			fFormatter = new SciDecimalFormat(format, format);
		}

		fInput.addInputListener(this);
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
	 * <code>setText</code> method.
	 * 
	 * @param event A DatasetEvent
	 */
	public synchronized void receiveDataSetEvent(DataSetEvent event)
	{
		DataSet dataSet = event.getDataSet();
		BasisSet basisSet = null;
		DataBuffer dataBuffer = null;

		if (fBasisBundleId != null)
		{
			basisSet = dataSet.getBasisSet(fBasisBundleId);
			
			if (basisSet != null)
			{
				dataBuffer = basisSet.getDataBuffer(fBufferName);
			}
		}
		else
		{
			for (Iterator iter = dataSet.getBasisSets().iterator();
				iter.hasNext() && dataBuffer == null;)
			{
				dataBuffer =((BasisSet) iter.next()).getDataBuffer(fBufferName);
			}
		}
		
		if (dataBuffer != null)
		{
			double value = dataBuffer.getAsDouble(dataBuffer.getSize() - 1);
			setText(fFormatter.format(value));
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
	 * Get the formatter for this DataLabel.
	 * 
	 * @return Returns the formatter.
	 */
	public ValueFormat getLabelFormatter()
	{
		return fFormatter;
	}
	
	/**
	 * Set the formatter for this DataLabel.
	 * 
	 * @param formatter The formatter to set.
	 */
	public void setLabelFormatter(ValueFormat formatter)
	{
		fFormatter = formatter;
	}

	/**
	 * Set the label format pattern for the DataLabel.
	 * 
	 * @param pattern The format pattern.
	 */
	public void setLabelFormat(String pattern)
	{
		fFormatter.setPattern(pattern);
	}

	/**
	 * Get the label format pattern for the DataLabel.
	 * 
	 * @return the pattern
	 */
	public String getLabelFormat()
	{
		return fFormatter.getPattern();
	}
	
	/**
	 * Set the name of the buffer to display in this label.
	 * 
	 * @param name the name of the buffer
	 */
	public void setBufferName(String name)
	{
		fBufferName = name;
	}
	
	/**
	 * Get the name of the buffer displayed in this label.
	 * 
	 * @return the name of the buffer
	 */
	public String getBufferName()
	{
		return fBufferName;
	}
	
	/**
	 * Set the bundle Id of the bundle containing the buffer to display.
	 * 
	 * @param id the bundle id to find the buffer in.
	 */
	public void setBasisBundleId(BasisBundleId id)
	{
		fBasisBundleId = id;
	}

	/**
	 * Get the bundle Id of the bundle containing the buffer to display.
	 * 
	 * @return the bundle id to find the buffer in.
	 */
	public BasisBundleId getBasisBundleId()
	{
		return fBasisBundleId;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DataLabel.java,v $
//  Revision 1.3  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.2  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/11/07 22:12:19  tames
//  Initial version
//
//