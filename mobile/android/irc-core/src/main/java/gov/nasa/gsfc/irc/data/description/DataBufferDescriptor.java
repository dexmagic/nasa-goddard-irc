//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/description/DataBufferDescriptor.java,v 1.14 2006/07/28 16:57:45 smaher_cvs Exp $
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

package gov.nasa.gsfc.irc.data.description;

import org.jdom.Element;
import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataBufferDescriptor describes a DataBuffer.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/07/28 16:57:45 $
 * @author Carl F. Hostetter
 */

public class DataBufferDescriptor extends DataElementDescriptor
{
	private String fName;
	private Pixel fPixel;
	private Class fType;
	private Unit fUnit = Unit.ONE;
	private String fCoaddHint;
		
	
	/**
	 * Constructs a new DataBufferDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataBufferDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataBufferDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataBufferDescriptor		
	**/
	
	public DataBufferDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DataBufferDescriptor having the given base name.
	 * 
	 * @param name The base name of the new DataBufferDescriptor
	**/
	
	public DataBufferDescriptor(String name)
	{
		super(name);
		
		fName = name;
	}

	
	/**
	 *	Constructs a DataBufferDescriptor describing a DataBuffer having the given 
	 *  base name, Pixel, type, and Unit.
	 *
	 *  @param name The name of the described DataBuffer
	 *  @param pixel The Pixel of the described DataBuffer (if any)
	 *  @param type The content type of the described DataBuffer
	 *  @param unit The Unit of the described DataBuffer
	 */
	
	public DataBufferDescriptor(String name, Pixel pixel, Class type, Unit unit)
	{
		super(name);
		
		fName = name;
		fPixel = pixel;
		fType = type;
		fUnit = unit;
		
		if (fPixel != null)
		{
			super.setName(getName() + " " + fPixel);
		}
	}
	
	
	/**
	 *	Constructs a DataBufferDescriptor describing a Unitless DataBuffer 
	 *  having the given name, content type, and Pixel.
	 *
	 *  @param name The name of the described DataBuffer
	 *  @param pixel The Pixel of the described DataBuffer (if any)
	 *  @param type The content type of the described DataBuffer
	 */
	
	public DataBufferDescriptor(String name, Pixel pixel, Class type)
	{
		this(name, pixel, type, null);
	}
	
	
	/**
	 *	Constructs a DataBufferDescriptor describing a DataBuffer having 
	 *  the given name, content type, and Unit.
	 *
	 *  @param name The name of the described DataBuffer
	 *  @param type The content type of the described DataBuffer
	 *  @param unit The Unit of the described DataBuffer
	 */
	
	public DataBufferDescriptor(String name, Class type, Unit unit)
	{
		this(name, null, type, unit);
	}
	
	
	/**
	 *	Constructs a DataBufferDescriptor describing a Unitless DataBuffer 
	 *  having the given name and content type.
	 *
	 *  @param name The name of the described DataBuffer
	 *  @param type The content type of the described DataBuffer
	 */
	
	public DataBufferDescriptor(String name, Class type)
	{
		this(name, null, type, null);
	}
	
	
	/**
	 *	Constructs a DataBufferDescriptor having all the same attributes as the 
	 *  given DataBufferDescriptor, but with the given Pixel.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 *  @param pixel The Pixel of the new DataBufferDescriptor
	 */
	
	public DataBufferDescriptor(DataBufferDescriptor descriptor, Pixel pixel)
	{
		this(descriptor.getName(), pixel, descriptor.getDataType(), 
			descriptor.getUnit());
	}
	
	
	/**
	 *	Default constructor of a new DataBufferDescriptor.
	 *
	 */
	
	public DataBufferDescriptor()
	{
		this(null, null, null, null);
	}
	
	
	/**
	 *	Returns a modifiable copy of this DataBufferDescriptor.
	 *
	 *  @return A modifiable copy of this DataBufferDescriptor
	 */
	
	public DataElementDescriptor getModifiableCopy()
	{
		return (new ModifiableDataBufferDescriptor(this));
	}	
	

	/**
	 *	Returns the base name of this DataBufferDescriptor, which excludes its Pixel 
	 *  tag (if any).
	 *
	 *  @return The base name of this DataBufferDescriptor
	 */
	
	protected String getBaseName()
	{
		return (fName);	
	}
	

	/**
	 *  Sets the name of this DataBufferDescriptor to the given name.
	 *
	 *  @param The desired new name of this DataBufferDescriptor
	 **/

	public void setName(String name) 
	{
		fName = name;
		
		if (fName != null && fPixel != null)
		{
			super.setName(fName + " " + fPixel);
		}
		else
		{
			super.setName(fName);
		}
	}

	/**
	 *	Sets the content type of this DataBufferDescriptor to the given 
	 *  type.
	 *
	 *  @param type A type
	 */
	
	protected void setDataType(Class type)
	{
		fType = type;	
	}
	

	/**
	 *	Returns the content type of the DataBuffer described by this 
	 *  DataBufferDescriptor.
	 *
	 *  @return The content type of the DataBuffer described by this 
	 *  		DataBufferDescriptor
	 */
	
	public Class getDataType()
	{
		return (fType);	
	}
	

	/**
	 *	Returns true if the DataBuffer described by this 
	 *  DataBufferDescriptor is a Pixel, false otherwise.
	 *
	 *  @return True if the DataBuffer described by this 
	 *  DataBufferDescriptor is a Pixel, false otherwise
	 */
	
	public boolean isPixel()
	{
		return (fPixel != null);	
	}
	
	
	/**
	 *	Sets the Pixel of this DataBufferDescriptor to the given Pixel.
	 *
	 *  @param pixel A Pixel
	 */
	
	protected void setPixel(Pixel pixel)
	{
		fPixel = pixel;
		
		if (fPixel != null)
		{
			super.setName(fName + " " + fPixel);
		}
		else
		{
			super.setName(fName);
		}
	}
	

	/**
	 *	Returns the Pixel of the DataBuffer described by this 
	 *  DataBufferDescriptor (if any).
	 *
	 *  @return The Pixel of of the DataBuffer described by this 
	 *  		DataBufferDescriptor
	 */
	
	public Pixel getPixel()
	{
		return (fPixel);	
	}
	

	/**
	 *	Sets the Unit of this DataBufferDescriptor to the given Unit.
	 *
	 *  @param unit A Unit
	 */
	
	protected void setUnit(Unit unit)
	{
		fUnit = unit;
	}
	

	/**
	 *	Returns the Unit of the DataBuffer described by this DataBufferDescriptor.
	 *
	 *  @return The Unit of the DataBuffer described by this DataBufferDescriptor
	 */
	
	public Unit getUnit()
	{
		return (fUnit);	
	}
	

	/**
	 * Sets the aggregate "hint" for coadding.  The hint
	 * can tell an IRC coadder whether to aggregate or average
	 * the values in this data buffer.
	 * 
	 * @param coaddHint The aggregate to set.
	 */
	public void setCoaddHint(String coaddHint)
	{
		fCoaddHint = coaddHint;
	}


	/**
	 * Return the aggregate "hint" for coadding.
	 * 
	 * @return Returns the aggregate.
	 */
	public String getCoaddHint()
	{
		return fCoaddHint;
	}


	/**
	 *	Returns true if this DataBufferDescriptor equals the given 
	 *  DataBufferDescriptor, false otherwise.
	 *
	 *  @return True if this DataBufferDescriptor equals the given 
	 *  		DataBufferDescriptor, false otherwise
	 */
	
	public boolean equals(Object descriptor)
	{
		return (this.toString().equals(descriptor.toString()));
	}	
	

	/**
	 *	Returns a String representation of this DataBufferDescriptor.
	 *
	 *  @return A String representation of this DataBufferDescriptor
	 */
	
	public String toString()
	{
		return ("Name: " + getName() + " Type: " + fType + " Unit: " + fUnit);
	}	
	

	/**
	 * Unmarshall this DataBufferDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		//--- Load the content type		
		fType = getTypeClass();

		//--- Load the Units
		
		String unitName = fSerializer.loadStringAttribute
			(Dataml.A_UNITS, Unit.ONE.toString(), fElement);
		
		if (unitName != null)
		{
			fUnit = Unit.searchSymbol(unitName);
			
			if (fUnit == null)
			{
				fUnit = Unit.ONE;
			}
		}
		
		//--- Load the aggregate hint		
		fCoaddHint = fSerializer.loadStringAttribute
			(Dataml.A_COADD_HINT, null, fElement);		
	}

	
	/**
	 * Marshall this DataBufferDescriptor to the given XML Element.  
	 *
	 * @param element The Element to marshall into
	**/
	
	public void xmlMarshall(Element element)
	{
		fSerializer.storeDescriptorElement(Dataml.E_DATA_BUFFER, this, 
			Dataml.C_DATA_BUFFER, element, fDirectory);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataBufferDescriptor.java,v $
//  Revision 1.14  2006/07/28 16:57:45  smaher_cvs
//  Comments
//
//  Revision 1.13  2006/06/26 20:10:19  smaher_cvs
//  Added coadd hints
//
//  Revision 1.12  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.11  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.10  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.9  2005/07/19 18:00:18  tames_cvs
//  Changed DataBuffer type to Class instead of DataBufferType. Removed
//  all reference to obsolete DataBufferType class.
//
//  Revision 1.8  2005/03/15 00:36:03  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.7  2005/03/10 21:40:19  tames
//  Changes to getName() for performance reasons.
//
//  Revision 1.6  2005/02/10 21:03:56  chostetter_cvs
//  Fixed duplicat Pixel tag problem with ModifiableDataBufferDescriptors
//
//  Revision 1.5  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/09/14 20:12:13  chostetter_cvs
//  Units now specified as Strings in lieu of better scheme later
//
//  Revision 1.2  2004/09/11 19:22:44  chostetter_cvs
//  BasisBundle creation from XML now works, except for DataBuffer units
//
//  Revision 1.1  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.10  2004/07/17 18:39:02  chostetter_cvs
//  Name, descriptor modification work
//
//  Revision 1.9  2004/07/14 03:09:55  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.8  2004/07/11 18:05:41  chostetter_cvs
//  More data request work
//
//  Revision 1.7  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.6  2004/07/02 02:38:53  chostetter_cvs
//  Moved Pixel from data to commons package
//
//  Revision 1.5  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.4  2004/05/29 03:47:16  chostetter_cvs
//  Renamed DataValueType to DataBufferType
//
//  Revision 1.3  2004/05/28 05:58:19  chostetter_cvs
//  More Namespace, DataSpace, Descriptor worl
//
//  Revision 1.2  2004/05/20 21:28:10  chostetter_cvs
//  Checking in for the weekend
//
//  Revision 1.1  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.1  2004/05/16 21:54:27  chostetter_cvs
//  More work
//
