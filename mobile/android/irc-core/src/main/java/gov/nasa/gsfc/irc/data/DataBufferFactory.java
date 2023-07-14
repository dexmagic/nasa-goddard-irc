//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/DataBufferFactory.java,v 1.19 2005/09/30 20:55:48 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.data;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * A DataBufferFactory creates new DataBuffers according to a given 
 * DataBufferDescriptor.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */

public class DataBufferFactory
{
	private static final String CLASS_NAME = DataBufferFactory.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
    
	private static final DataBufferFactory fInstance = new DataBufferFactory();
	
	
	/**
	 * Default constructor of a DataBufferFactory.
	 */
	private DataBufferFactory()
	{
	}

	/**
	 * Returns the singleton instance of a DataBufferFactory.
	 * 
	 * @return The singleon instance of a DataBufferFactory
	 */
	public static DataBufferFactory getInstance()
	{
		return (fInstance);
	}
	
	/**
	 * Creates and returns a new DataBuffer according to the given
	 * DataBufferDescriptor and having the given capacity.
	 * 
	 * @param descriptor A DataBufferDescriptor describing the desired
	 *            attributes of the new DataBuffer
	 * @param capacity The capacity of the new DataBuffer
	 */
	public DataBuffer createDataBuffer(
			DataBufferDescriptor descriptor, int capacity)
	{
		DataBuffer newDataBuffer = null;
		Class dataType = descriptor.getDataType();
		
		if (dataType == char.class)
		{
			newDataBuffer = new CharDataBuffer(descriptor, capacity);
		}
		else if (dataType == double.class)
		{
			newDataBuffer = new DoubleDataBuffer(descriptor, capacity);
		}
		else if (dataType == int.class)
		{
			newDataBuffer = new IntegerDataBuffer(descriptor, capacity);
		}
		else if (dataType == float.class)
		{
			newDataBuffer = new FloatDataBuffer(descriptor, capacity);
		}
		else if (dataType == long.class)
		{
			newDataBuffer = new LongDataBuffer(descriptor, capacity);
		}
		else if (dataType == short.class)
		{
			newDataBuffer = new ShortDataBuffer(descriptor, capacity);
		}
		else if (dataType == byte.class)
		{
			newDataBuffer = new ByteDataBuffer(descriptor, capacity);
		}
		else
		{
			newDataBuffer = new ObjectDataBuffer(descriptor, capacity);
		}
		
		return newDataBuffer;
	}
	
	/**
	 * Creates and returns a new direct DataBuffer of the indicated
	 * content type and having the given name, Pixel, Unit, and capacity.
	 * 
	 * @param name The name of the new DataBuffer
	 * @param type The content type of the new DataBuffer
	 * @param pixel The Pixel of the new DataBuffer (if any)
	 * @param unit The Unit of the new DataBuffer
	 * @param capacity The capacity of the new DataBuffer
	 */
	public DataBuffer createDataBuffer(String name, Class type,
			Pixel pixel, Unit unit, int capacity)
	{
		DataBufferDescriptor descriptor = new DataBufferDescriptor(name, pixel,
				type, unit);

		return (createDataBuffer(descriptor, capacity));
	}

	/**
	 * Creates and returns a new DataBuffer of the indicated content type and
	 * having the given name, Pixel, Unit name, and capacity.
	 * 
	 * @param name The name of the new DataBuffer
	 * @param type The content type of the new DataBuffer
	 * @param pixel The Pixel of the new DataBuffer (if any)
	 * @param unitName The name of the Unit of the new DataBuffer
	 * @param capacity The capacity of the new DataBuffer
	 * @throws IllegalArgumentException if the given Unit name does not
	 *             correspond to a known Unit
	 */
	public DataBuffer createDataBuffer(String name, Class type,
			Pixel pixel, String unitName, int capacity)
	{
		DataBuffer result = null;

		Unit unit = Unit.searchSymbol(unitName);

		if (unit != null)
		{
			DataBufferDescriptor descriptor = new DataBufferDescriptor(name,
					pixel, type, unit);

			result = createDataBuffer(descriptor, capacity);
		}
		else
		{
			String message = "Unit name " + unitName + " not recognized";

			if (sLogger.isLoggable(Level.SEVERE))
			{
				sLogger.logp(Level.SEVERE, CLASS_NAME, "createDataBuffer",
					message);
			}

			throw (new IllegalArgumentException(message));
		}

		return (result);
	}

	/**
	 * Creates and returns a new Unitless direct DataBuffer of the indicated
	 * content type and having the given name, Pixel, and capacity.
	 * 
	 * @param name The name of the new DataBuffer
	 * @param type The content type of the new DataBuffer
	 * @param pixel The Pixel of the new DataBuffer (if any)
	 * @param capacity The capacity of the new DataBuffer
	 */
	public DataBuffer createDataBuffer(String name, Class type,
			Pixel pixel, int capacity)
	{
		return (createDataBuffer(name, type, pixel, (Unit) null, capacity));
	}

	/**
	 * Creates and returns a new direct DataBuffer of the indicated
	 * content type and having the given name, Unit name, and capacity.
	 * <p>
	 * NOTE that the given capacity is in terms of the elements corresonding to
	 * the given BufferDataType, <em>not</em> in terms of bytes (unless of
	 * course the BufferDataType is DataValueType.BYTE).
	 * 
	 * @param name The name of the new DataBuffer
	 * @param type The content type of the new DataBuffer
	 * @param unit The Unit of the new DataBuffer
	 * @param capacity The capacity of the new DataBuffer
	 * @throws IllegalArgumentException if the given Unit name does not
	 *             correspond to a known Unit
	 */
	public DataBuffer createDataBuffer(String name, Class type,
			String unitName, int capacity)
	{
		return (createDataBuffer(name, type, null, unitName, capacity));
	}

	/**
	 * Creates and returns a new direct DataBuffer of the indicated
	 * content type and having the given name, Unit, and capacity.
	 * 
	 * @param name The name of the new DataBuffer
	 * @param type The content type of the new DataBuffer
	 * @param unitName The name of the Unit of the new DataBuffer
	 * @param capacity The capacity of the new DataBuffer
	 */
	public DataBuffer createDataBuffer(String name, Class type,
			Unit unit, int capacity)
	{
		return (createDataBuffer(name, type, null, unit, capacity));
	}

	/**
	 * Creates and returns a new Unitless DataBuffer of the indicated
	 * content type and having the given name and capacity.
	 * 
	 * @param name The name of the new DataBuffer
	 * @param type The content type of the new DataBuffer
	 * @param capacity The capacity of the new DataBuffer
	 */
	public DataBuffer createDataBuffer(String name, Class type,
			int capacity)
	{
		return (createDataBuffer(name, type, (Pixel) null, (Unit) null,
			capacity));
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: DataBufferFactory.java,v $
//  Revision 1.19  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.18  2005/07/29 20:37:28  tames_cvs
//  Fixed a bug in the createDataBuffer method that did not handle
//  byte primitive types of Buffers. It was returning an Object buffer instead.
//
//  Revision 1.17  2005/07/19 17:59:48  tames_cvs
//  Changed DataBuffer type to Class instead of DataBufferType. Removed
//  all reference to obsolete DataBufferType class.
//
//  Revision 1.16  2005/07/14 22:01:40  tames
//  Refactored data package for performance.
//
//  Revision 1.15  2005/03/15 18:57:15  tames
//  Changed the createDataBuffer method so by default it does not create
//  a direct buffer.
//
//  Revision 1.14  2005/03/15 17:19:32  chostetter_cvs
//  Made DataBuffer an interface, organized imports
//
//  Revision 1.13  2005/03/15 00:36:02  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.12  2004/09/14 20:12:13  chostetter_cvs
//  Units now specified as Strings in lieu of better scheme later
//
//  Revision 1.11  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.10  2004/07/17 18:39:02  chostetter_cvs
//  Name, descriptor modification work
//
//  Revision 1.9  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.8  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.7  2004/07/02 02:38:53  chostetter_cvs
//  Moved Pixel from data to commons package
//
//  Revision 1.6  2004/06/05 19:13:00  chostetter_cvs
//  AbstractDataBuffer now wraps a ByteBuffer (instead of Buffer) and provides typed views of its ByteBuffer for reading and writing
//
//  Revision 1.5  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.4  2004/05/29 03:47:16  chostetter_cvs
//  Renamed DataValueType to DataBufferType
//
//  Revision 1.3  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.2  2004/05/27 15:57:16  chostetter_cvs
//  Data-related changes
//
//  Revision 1.1  2004/05/20 21:28:10  chostetter_cvs
//  Checking in for the weekend
//
//  Revision 1.2  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.1  2004/05/16 21:54:27  chostetter_cvs
//  More work
//
