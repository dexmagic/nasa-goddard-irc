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

package gov.nasa.gsfc.irc.library.gui.svg;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;

import gov.nasa.gsfc.commons.numerics.formats.SciDecimalFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.svg.SvgComponent;
import gov.nasa.gsfc.irc.gui.svg.SvgUpdater;

/**
 * The VisibilityUpdater class can be used by an DataSvgComponent 
 * to update a SVG graphic by setting the visibility attribute of a SVG 
 * element to visible or hidden based on the value of a sample in a DataSet. 
 * Which element to update is determined by an element id naming pattern and 
 * the data value. 
 * 
 * <P>The element id naming pattern is based on the Id root property set by 
 * the <code>setIdRoot</code> method.
 * The table below gives the element names and descriptions that this updater 
 * uses.
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Element Name</th>
 *      <th>Example</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>&lt;root&gt;_Init</td><td>LHe_Init</td>
 *      <td align="left">The name used for an SVG element that represents the 
 *      initial state before any data has been received. If the root property is
 *      set to "LHe" then this element would have an Id of "LHe_Init". This 
 *      initial element, if it exists in the SVG description, will be hidden 
 *      when the <code>update</code> method is called for the first time.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>&lt;root&gt;_Under</td><td>LHe_Under</td>
 *      <td align="left">If this element is in the SVG description it will be 
 *      set to visible whenever the value from the data is less than the minimum 
 *      set by the <code>setMinimum</code> method.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>&lt;root&gt;_&lt;n&gt;</td><td>LHe_0<br>LHe_1<br>...<br>LHe_9</td>
 *      <td align="left">The element representing the current state when the 
 *      data value is between the minimum and maximum set for this updater. 
 *      The variable &lt;n&gt; is defined as <em>0&le;n&lt;states</em> where states is
 *      the number of states set by the <code>setStates</code> method. The
 *      current state &lt;n&gt; is determined by where the data value falls 
 *      within the range between minimum to maximum binned by the number of
 *      states. The element representing the current state will be set 
 *      to visible and the element representing the previous state will be set
 *      to hidden.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>&lt;root&gt;_Over</td><td>LHe_Over</td>
 *      <td align="left">If this element is in the SVG description it will be 
 *      set to visible whenever the value from the data is greater than the maximum 
 *      set by the <code>setMaximum</code> method.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>&lt;root&gt;_Value</td><td>LHe_Value</td>
 *      <td align="left">If this element is in the SVG description it will be 
 *      replaced by a text element representing the data value formatted using
 *      the pattern set by the <code>setTextFormat</code> method. If the
 *      text format pattern is not set this element will not be used.</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/12/01 19:34:45 $
 * @author 	Troy Ames
 */
public class VisibilityUpdater implements SvgUpdater
{
	private String fIdRoot = "State";
	private boolean fInitialized = false;
	private int fStates = 10;
	private int fPreviousState = 0;
	private double fMinimum = 0.0;
	private double fMaximum = 10.0;
	private double fRange = fMaximum - fMinimum;
	private BasisBundleId fBasisBundleId;
	private String fBufferName;
	private final int NO_DATA = Integer.MAX_VALUE;
	private String fPreviousStateId = null;
	private ValueFormat fFormatter = null;
	
	/**
	 * Default constructor for the VisibilityUpdater.
	 */
	public VisibilityUpdater()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.gui.svg.SvgUpdater#update(gov.nasa.gsfc.irc.gui.svg.SvgComponent, gov.nasa.gsfc.irc.data.DataSet)
	 */
	public void update(SvgComponent svg, DataSet dataSet)
	{
		SVGDocument document = svg.getSVGDocument();
		
		if (document != null)
		{
			Element element = null;
			
			if (!fInitialized)
			{
				fInitialized = initializeSvgDom(document);
			}
			
			int newState = fPreviousState;
			
			// Determine current state based on current data
			double value = getValue(dataSet);
			newState = getNewState(value);
			
			// Check if the state really changed 
			if (newState != fPreviousState && newState != NO_DATA)
			{
				String newStateId = null;
				
				if (newState == fStates)
				{
					newStateId = fIdRoot + "_Over";
				}
				else if (newState < 0)
				{
					newStateId = fIdRoot + "_Under";
				}
				else
				{
					newStateId = fIdRoot + "_" + newState;
				}

				// Show element representing new state
				element = document.getElementById(newStateId);
				
				if (element != null)
				{
					element.setAttribute("visibility", "visible");
				}
	
				// Hide element representing previous state
				element = document.getElementById(fPreviousStateId);
				
				if (element != null)
				{
					element.setAttribute("visibility", "hidden");
				}
				
				// Save current state
				fPreviousState = newState;
				fPreviousStateId = newStateId;
			}
			
			if (fFormatter != null)
			{
				// Update value element if it exists
				element = document.getElementById(fIdRoot + "_Value");
				
				// Set "Value" element if it exists
				if (element != null)
				{
					Text newNode = document.createTextNode(
						fFormatter.format(value));
					element.replaceChild(newNode, element.getFirstChild());
				}
			}
		}
	}

	/**
	 * Initializes the given SVG document by hiding all the SVG elements
	 * applicable to this updater.
	 * 
	 * @param document the SVG document to initialize
	 * @return true if initialization was successful, false otherwise
	 */
	private boolean initializeSvgDom(SVGDocument document)
	{
		boolean result = false;
		
		// Hide the optional init element if it exists
		Element element = document.getElementById(fIdRoot + "_Init");
		
		if (element != null)
		{
			element.setAttribute("visibility", "hidden");
		}
		
		// Hide all level elements
		for (int i = 0; i < fStates; i++)
		{
			element = document.getElementById(fIdRoot + "_" + i);
			
			if (element != null)
			{
				element.setAttribute("visibility", "hidden");
				
				// Initialization is considered finished as long as one level
				// Element was found and set.
				result = true;
			}
		}
		
		element = document.getElementById(fIdRoot + "_Under");
		
		// Hide "Under" element if it exists
		if (element != null)
		{
			element.setAttribute("visibility", "hidden");
		}
		
		element = document.getElementById(fIdRoot + "_Over");
		
		// Hide "Over" element if it exists
		if (element != null)
		{
			element.setAttribute("visibility", "hidden");
		}
		
		return result;
	}

	/**
	 * Calculates a new state where the returned value will be negative if the 
	 * value is less then minimum, or <em>0&le;n&lt;# of states</em> if the value
	 * is in the range between minimum and maximum, or <em>n=# of states</em> if 
	 * the value is greater than the maximum.
	 * 
	 * @param value the current data value
	 * @return the current state
	 */
	protected int getNewState(double value)
	{
		int result = NO_DATA;

		if (value != Double.NaN)
		{
			double partialResult = ((value - fMinimum) / fRange);
			
			if (partialResult < 0)
			{
				result = -1;
			}
			else if (partialResult > 1.0)
			{
				result = fStates;
			}
			else
			{
				result = (int) Math.round((fStates - 1) * partialResult);
			}
		}
		
		return result;
	}
	
	/**
	 * Gets the value from the given DataSet based on the BasisBundleId set by 
	 * the <code>setBasisBundleId</code> method and the buffer name set by the
	 * <code>setBufferName</code> method.
	 * 
	 * @param dataSet the data set to use for the data
	 * @return the current value as a double
	 */
	protected double getValue(DataSet dataSet)
	{
		double result = Double.NaN;
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
			result = dataBuffer.getAsDouble(dataBuffer.getSize() - 1);
		}
		
		return result;
	}
	
	/**
	 * Returns the root Id string to use for getting the applicable SVG elements.
	 * 
	 * @return Returns the idRoot.
	 */
	public String getIdRoot()
	{
		return fIdRoot;
	}

	/**
	 * Sets the root Id string to use for getting the applicable SVG elements.
	 * 
	 * @param idRoot The idRoot to set.
	 */
	public void setIdRoot(String idRoot)
	{
		fIdRoot = idRoot;
	}

	/**
	 * Returns the number of states that this updater will recognize.
	 * 
	 * @return Returns the number of states.
	 */
	public int getStates()
	{
		return fStates;
	}

	/**
	 * Setss the number of states that this updater will recognize.
	 * 
	 * @param states The states to set.
	 */
	public void setStates(int states)
	{
		fStates = states;
	}

	/**
	 * Returns the maximum for the range of values that will be used to 
	 * determine the current state.
	 * 
	 * @return Returns the maximum.
	 */
	public double getMaximum()
	{
		return fMaximum;
	}

	/**
	 * Sets the maximum for the range of values that will be used to 
	 * determine the current state.
	 * 
	 * @param maximum The maximum to set.
	 */
	public void setMaximum(double maximum)
	{
		fMaximum = maximum;
		fRange = fMaximum - fMinimum;
	}

	/**
	 * Returns the minimum for the range of values that will be used to 
	 * determine the current state.
	 * 
	 * @return Returns the minimum.
	 */
	public double getMinimum()
	{
		return fMinimum;
	}

	/**
	 * Sets the minimum for the range of values that will be used to 
	 * determine the current state.
	 * 
	 * @param minimum The minimum to set.
	 */
	public void setMinimum(double minimum)
	{
		fMinimum = minimum;
		fRange = fMaximum - fMinimum;
	}

	/**
	 * Returns the BasisBundleId that this updater will pull the data value 
	 * from.
	 * 
	 * @return Returns the basisBundleId.
	 */
	public BasisBundleId getBasisBundleId()
	{
		return fBasisBundleId;
	}

	/**
	 * Sets the BasisBundleId that this updater will pull the data value 
	 * from.
	 * 
	 * @param basisBundleId The basisBundleId to set.
	 */
	public void setBasisBundleId(BasisBundleId basisBundleId)
	{
		fBasisBundleId = basisBundleId;
	}

	/**
	 * Returns the Buffer name that this updater will pull the data value 
	 * from.
	 * 
	 * @return Returns the bufferName.
	 */
	public String getBufferName()
	{
		return fBufferName;
	}

	/**
	 * Sets the Buffer name that this updater will pull the data value 
	 * from.
	 * 
	 * @param bufferName The bufferName to set.
	 */
	public void setBufferName(String bufferName)
	{
		fBufferName = bufferName;
	}
	
	/**
	 * Set the text format pattern for the value.
	 * 
	 * @param pattern The format pattern.
	 */
	public void setTextFormat(String pattern)
	{
		if (fFormatter == null)
		{
			DecimalFormat format = new DecimalFormat(pattern);
			fFormatter = new SciDecimalFormat(format, format);
		}
		else
		{
			fFormatter.setPattern(pattern);
		}
	}

	/**
	 * Get the text format pattern for the value.
	 * 
	 * @return the pattern
	 */
	public String getTextFormat()
	{
		String result = null;
		
		if (fFormatter != null)
		{
			result = fFormatter.getPattern();
		}
		
		return result;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: VisibilityUpdater.java,v $
//  Revision 1.2  2005/12/01 19:34:45  tames
//  Updated JavaDoc only.
//
//  Revision 1.1  2005/11/07 22:17:42  tames
//  Initial version
//
//