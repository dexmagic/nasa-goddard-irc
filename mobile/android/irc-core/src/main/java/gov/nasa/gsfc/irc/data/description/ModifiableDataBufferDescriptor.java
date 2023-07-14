//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/description/ModifiableDataBufferDescriptor.java,v 1.7 2006/06/27 22:39:52 smaher_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ModifiableDataBufferDescriptor.java,v $
//  Revision 1.7  2006/06/27 22:39:52  smaher_cvs
//  Support for coaddHint
//
//  Revision 1.6  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.5  2005/07/19 18:00:18  tames_cvs
//  Changed DataBuffer type to Class instead of DataBufferType. Removed
//  all reference to obsolete DataBufferType class.
//
//  Revision 1.4  2005/03/15 00:36:03  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.3  2005/02/10 21:03:56  chostetter_cvs
//  Fixed duplicat Pixel tag problem with ModifiableDataBufferDescriptors
//
//  Revision 1.2  2004/09/14 20:12:13  chostetter_cvs
//  Units now specified as Strings in lieu of better scheme later
//
//  Revision 1.1  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.1  2004/07/17 18:39:02  chostetter_cvs
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

import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Pixel;


/**
 * A ModifiableDataBufferDescriptor is a modifiable copy of a 
 * DataBufferDescriptor.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/27 22:39:52 $
 * @author Carl F. Hostetter
 */

public class ModifiableDataBufferDescriptor extends DataBufferDescriptor
{
	/**
	 *	Constructs a new modifiable copy of the given DataBufferDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 */
	
	public ModifiableDataBufferDescriptor(DataBufferDescriptor descriptor)
	{
		super(descriptor.getBaseName(), descriptor.getPixel(), 
			descriptor.getDataType(), descriptor.getUnit());
        if (descriptor.getCoaddHint() != null)
        {
            setCoaddHint(new String(descriptor.getCoaddHint()));
        }
	}
	
	
	/**
	 *	Sets the name of this ModifiableDataBufferDescriptor to the given 
	 *	name.
	 *
	 *  @param name The new name of this ModifiableDataBufferDescriptor
	 */
	
	public void setName(String name)
	{
		super.setName(name);
	}
	

	/**
	 *	Sets the content type of this ModifiableDataBufferDescriptor to 
	 *  the given type.
	 *
	 *  @param type A content type
	 */
	
	public void setDataType(Class type)
	{
		super.setDataType(type);	
	}
	
	/**
	 *	Sets the Pixel of this ModifiableDataBufferDescriptor to the given 
	 *	Pixel.
	 *
	 *  @param pixel A Pixel
	 */
	
	public void setPixel(Pixel pixel)
	{
		super.setPixel(pixel);
	}
	

	/**
	 *	Sets the Unit of this ModifiableDataBufferDescriptor to the given Unit.
	 *
	 *  @param unit A Unit
	 */
	
	public void setUnit(Unit unit)
	{
		super.setUnit(unit);
	}
}
