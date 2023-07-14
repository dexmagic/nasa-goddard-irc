//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui.util;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.numerics.types.Range;
import gov.nasa.gsfc.commons.types.strings.FullStringTokenizer;

/**
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/04/06 14:59:46 $
 * @author Ken Wootton
 */
public class ColorMap implements Cloneable
{
	private static final String CLASS_NAME = ColorMap.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	/**
	 * Property name for the type of range that will be used.
	 */
	public static final String RANGE_TYPE_PROP = "Range Type";

	/**
	 * Property name for range that will be used if the range type is
	 * 'SPECIFIED_RANGE'.
	 */
	public static final String RANGE_PROP = "Range";
	
	/**
	 * Property name for the color look up table. Note that the color map will
	 * change as a result of a change to this property.
	 * 
	 * @see #getColorMap()
	 */
	public static final String COLOR_LUT_PROP = "Color LUT";

	/**
	 * Property name for the intensity look up table. Note that the color map
	 * will change as a result of a change to this property.
	 * 
	 * @see #getColorMap()
	 */
	public static final String INTENSITY_LUT_PROP = "Intensity LUT";

	/**
	 * Use the range of values that exist within the current range
	 */
	public static final int AUTO_RANGE = 0;

	/**
	 * Use the specified range
	 */
	public static final int SPECIFIED_RANGE = 1;

	/**
	 * Number of colors in any returned color map.
	 */
	public static final int NUM_COLORS = 256;

	//  Color component constants
	public static final int NUM_COMPONENTS = 3;
	public static final int RED_COMP = 0;
	public static final int GREEN_COMP = 1;
	public static final int BLUE_COMP = 2;

	//  Save/restore constants
	private static final String FIELD_SEP = "COLOR_MAP_FIELD_SEP";

	//  Misc
	private static final String LUT_STR_SEP = " + ";

	//  Defaults
	private static final Range DEF_MAPPED_RANGE = new Range(0, 10000);
	private static final String RAMP_LUT_NAME = "Ramp";
	private static final String DEF_COLOR_LUT = RAMP_LUT_NAME;
	private static final String DEF_INTENSITY_LUT = RAMP_LUT_NAME;

	//  Properties of the mapped range
	private int fMappedRangeType = AUTO_RANGE;
	private Range fMappedRange = DEF_MAPPED_RANGE;

	//  Look up tables 
	private String fColorLutName = DEF_COLOR_LUT;
	private String fIntensityLutName = DEF_INTENSITY_LUT;

	//  Color map.  Note that it is initially dirty because we don't want
	//  to create it until it is necessary.
	private Color[] fColorMap = new Color[NUM_COLORS];
	private boolean fDirtyColorMap = true;

	//  Property change manager
	PropertyChangeSupport fPropSupport = new PropertyChangeSupport(this);

	/**
	 * Create a new color map.
	 */
	public ColorMap()
	{
	}

	/**
	 * Create a new color map using a string representation.
	 * 
	 * @param str string representation of the color map, presumably retrieved
	 *            from the
	 * @see #toString method of this class
	 * 
	 * @throws IllegalArgumentException if the given string cannot be parsed
	 */
	public ColorMap(String str)
		throws IllegalArgumentException
	{
		try
		{
			FullStringTokenizer fieldTok = new FullStringTokenizer(
				str.trim(), FIELD_SEP);
			
			//  Grab the fields/tokens.

			setMappedRangeType(fieldTok.nextInt());
			setMappedRange(new Range(fieldTok.nextToken()));
			setColorLut(fieldTok.nextToken().trim());
			setIntensityLut(fieldTok.nextToken().trim());

			//  Call this to test the color and intensity look up table
			//  settings.  If they are invalid, an exception is thrown.
			getColorMap();
		}

		//  Report any parsing errors (NoSuchElement, Number, or 
		//  IllegalArgument,Runtime exceptions).
		catch (Exception e)
		{
			String message = "Color map is not formatted correctly:  ";

			sLogger.logp(Level.WARNING, CLASS_NAME, "ColorMap", message, e);
		}
	}

	/**
	 * Set the type of range to use when mapping the colors of the color map.
	 * This is either 'AUTO_RANGE', which will use the range of values that
	 * exist in the current image, or 'SPECIFIED_RANGE' which will use the range
	 * specified with the 'setMappedRange' method.
	 * 
	 * @param mappedRangeType either 'AUTO_RANGE' or 'SPECIFIED_RANGE'
	 * 
	 * @see #AUTO_RANGE
	 * @see #SPECIFIED_RANGE
	 * @see @setMappedRange(Range)
	 */
	public void setMappedRangeType(int mappedRangeType)
	{
		if (fMappedRangeType != mappedRangeType)
		{
			Integer oldValue = new Integer(fMappedRangeType);

			fMappedRangeType = mappedRangeType;
			fPropSupport.firePropertyChange(RANGE_TYPE_PROP, oldValue,
											new Integer(fMappedRangeType));
		}
	}
	
	/**
	 * Get the type of range that will be used when mapping the colors of the
	 * color map.
	 * 
	 * @return either 'AUTO_RANGE' or 'SPECIFIED_RANGE'
	 * 
	 * @see #setMappedRangeType(int)
	 */
	public int getMappedRangeType()
	{
		return fMappedRangeType;
	}

	/**
	 * Set the range of values that the set of colors will be mapped over.
	 * 
	 * @param mappedRange the mapped range of values
	 */
	public void setMappedRange(Range mappedRange)
	{
		if (mappedRange != null && !fMappedRange.equals(mappedRange))
		{
			Range oldValue = fMappedRange;

			fMappedRange = mappedRange;
			fPropSupport.firePropertyChange(RANGE_PROP, oldValue, 
											fMappedRange);
		}
	}

	/**
	 * Get the range of values in which the set of colors will be mapped over.
	 * 
	 * @return the mapped range of values
	 */
	public Range getMappedRange()
	{
		return fMappedRange;
	}

	/**
	 * Set the name of the color look up table.
	 * 
	 * @param intensityLutName name of the color look up table
	 */
	public void setColorLut(String colorLutName)
	{
		if (colorLutName != null && !colorLutName.equals(fColorLutName))
		{
			String oldValue = fColorLutName;
			fColorLutName = colorLutName;
			fDirtyColorMap = true;

			fPropSupport.firePropertyChange(COLOR_LUT_PROP, oldValue, 
											fColorLutName);
		}
	}

	/**
	 * Get the name of the color look up table.
	 * 
	 * @return the name of the color look up table
	 */
	public String getColorLut()
	{
		return fColorLutName;
	}

	/**
	 * Set the name of the intensity look up table.
	 * 
	 * @param intensityLutName name of the intensity look up table
	 */
	public void setIntensityLut(String intensityLutName)
	{
		if (intensityLutName != null && 
			!intensityLutName.equals(fIntensityLutName))
		{
			String oldValue = fIntensityLutName;
			fIntensityLutName = intensityLutName;
			fDirtyColorMap = true;

			fPropSupport.firePropertyChange(INTENSITY_LUT_PROP, oldValue, 
											fIntensityLutName);
		}
	}

	/**
	 * Get the name of the intensity look up table.
	 * 
	 * @return the name of the intensity look up table
	 */
	public String getIntensityLut()
	{
		return fIntensityLutName;
	}

	/**
	 *    
	 *
	 *  @return  the color map for the currently selected color and
	 *           intensity look up tables
	 */
	public Color[] getColorMap()
	{
		//  If changes are waiting, change it.
		if (fDirtyColorMap)
		{
			setupColorMap();
		}
			
		return fColorMap;
	}

	/**
	 * Add a listener for one of the advertised properties of this class
	 * (RANGE_TYPE_PROP, RANGE_PROP, COLOR_MAP_PROP).
	 * 
	 * @param listener the property listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		fPropSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a listener for one of the advertised properties of this class
	 * (RANGE_TYPE_PROP, RANGE_PROP, COLOR_MAP_PROP).
	 * 
	 * @param listener the property listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		fPropSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Get the string used to represent this object as a property. This string
	 * should be human readable as it will be presented to the user within the
	 * property editor.
	 * 
	 * @return the string representation of this object, in human readable form
	 */
	public String getPropStr()
	{
		return getColorLut() + LUT_STR_SEP + getIntensityLut();
	}

	/**
	 * Provide a string representation of the color map. This string can be used
	 * by the string constructor to recreate this object.
	 * 
	 * @return a string represntation
	 */
	public String toString()
	{
		return getMappedRangeType() + FIELD_SEP + getMappedRange() +
			FIELD_SEP + getColorLut() + FIELD_SEP + getIntensityLut();
	}

	/**
	 * Make a deep copy of the object.
	 * 
	 * @return the copy
	 */
	public Object clone()
	{
		ColorMap clone = new ColorMap();

		//  Copy the fields.
		clone.setMappedRangeType(getMappedRangeType());
		clone.setMappedRange((Range) getMappedRange().clone());
		clone.setColorLut(new String(getColorLut()));
		clone.setIntensityLut(new String(getIntensityLut()));

		return clone;
	}
	
	/**
	 * Combine the color and intensity look up tables to create the desired
	 * color map.
	 */
	protected void setupColorMap()
	{
		int maxLut = NUM_COLORS - 1;

		//  Loop variables for the color components
		float[] compFloats = new float[NUM_COMPONENTS];
		int[] compInts = new int[NUM_COMPONENTS];
		int comp = 0;

		//  Get the color tables.  If have anything other than a ramp
		//  intensity LUT, we need to incorporate it.
		//float[][] colorLut = ImageColorLuts.getLut(fColorLutName);
		float[][] colorLut = ImageColorLuts.getLut("Real");
		float[] intensityLut = null;
		if (!fIntensityLutName.equals(RAMP_LUT_NAME))
		{
			intensityLut = ImageColorItts.getItt(fIntensityLutName);
		}

		//  We need a color LUT.
		if (colorLut == null)
		{
			return;
		}

		//  Create/recreate the color map.
		for(int i = 0; i < NUM_COLORS; i++) 
		{
			//  If needed, use the intensity LUT to determine which 
			//  values within the color LUT to use.
			if (intensityLut != null)
			{
				int index = 
					(int) ((intensityLut[i] * maxLut) + 0.5);
				compFloats[RED_COMP] = colorLut[index][RED_COMP];
				compFloats[GREEN_COMP] = colorLut[index][GREEN_COMP];
				compFloats[BLUE_COMP] = colorLut[index][BLUE_COMP];
			}

			//  Otherwise, just use the color LUT values directly.
			else
			{
				compFloats[RED_COMP] = colorLut[i][RED_COMP];
				compFloats[GREEN_COMP] = colorLut[i][GREEN_COMP];
				compFloats[BLUE_COMP] = colorLut[i][BLUE_COMP];
			}
			
			//  Convert the a bytes value between 0 and 1 to an RGB value and
			//  add the color to the map.
			for (comp = 0; comp < NUM_COMPONENTS; comp++)
			{
				compInts[comp] = ((byte) (compFloats[comp] * maxLut)) & 0xFF;
			}
			fColorMap[i] = new Color(compInts[RED_COMP],
									 compInts[GREEN_COMP],
									 compInts[BLUE_COMP]);
		}

		fDirtyColorMap = false;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ColorMap.java,v $
//  Revision 1.4  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.3  2005/01/07 21:34:07  tames
//  Removed references to obsolete property classes.
//
//
