//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataValueFormatType.java,v $
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.2  2005/03/15 20:01:00  mn2g
//  Fixed binary formatting
//
//  Revision 1.1  2005/02/14 22:03:20  chostetter_cvs
//  Revised data formatting, includes binary formatting with mask
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.transformation;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamedObject;
import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.data.transformation.description.Datatransml;


/**
 * A DataValueFormatType enumerates the possible types of data formatter.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter 
**/

public class DataValueFormatType extends AbstractNamedObject implements Serializable
{
	/**
	 * All known DataValueFormats, keyed by name.
	**/
	// NB: For initialization sequence, this has to come before any 
	// DataValueFormatType constants.
	private static Map sFormats = new HashMap();
	
	public static final DataValueFormatType NONE = new DataValueFormatType(Datatransml.NONE);
	public static final DataValueFormatType TEXT = new DataValueFormatType(Datatransml.TEXT);
	public static final DataValueFormatType PRINTF = new DataValueFormatType(Datatransml.PRINTF);
	public static final DataValueFormatType TIME = new DataValueFormatType(Datatransml.TIME);
	public static final DataValueFormatType BINARY = new DataValueFormatType(Datatransml.BINARY);
	public static final DataValueFormatType DECIMAL = new DataValueFormatType(Datatransml.DECIMAL);
	public static final DataValueFormatType REAL = new DataValueFormatType(Datatransml.REAL);
	public static final DataValueFormatType DATE = new DataValueFormatType(Dataml.DATE);
	public static final DataValueFormatType BIT_ARRAY = new DataValueFormatType(Dataml.BIT_ARRAY);
	public static final DataValueFormatType _BOOLEAN = new DataValueFormatType(Dataml._BOOLEAN);
	public static final DataValueFormatType _BYTE = new DataValueFormatType(Dataml._BYTE);
	public static final DataValueFormatType _CHAR = new DataValueFormatType(Dataml._CHAR);
	public static final DataValueFormatType _SHORT = new DataValueFormatType(Dataml._SHORT);
	public static final DataValueFormatType _INT = new DataValueFormatType(Dataml._INT	);
	public static final DataValueFormatType _LONG = new DataValueFormatType(Dataml._LONG);
	public static final DataValueFormatType _FLOAT = new DataValueFormatType(Dataml._FLOAT	);
	public static final DataValueFormatType _DOUBLE = new DataValueFormatType(Dataml._DOUBLE);
	public static final DataValueFormatType BOOLEAN = new DataValueFormatType(Dataml.BOOLEAN);
	public static final DataValueFormatType BYTE = new DataValueFormatType(Dataml.BYTE	);
	public static final DataValueFormatType CHARACTER = new DataValueFormatType(Dataml.CHARACTER);
	public static final DataValueFormatType SHORT = new DataValueFormatType(Dataml.SHORT);
	public static final DataValueFormatType INTEGER = new DataValueFormatType(Dataml.INTEGER);
	public static final DataValueFormatType LONG = new DataValueFormatType(Dataml.LONG	);
	public static final DataValueFormatType FLOAT = new DataValueFormatType(Dataml.FLOAT);
	public static final DataValueFormatType DOUBLE = new DataValueFormatType(Dataml.DOUBLE	);
	public static final DataValueFormatType STRING = new DataValueFormatType(Dataml.STRING	);
//	public static final DataValueFormatType BIG_DECIMAL = new DataValueFormatType(Dataml.BIG_DECIMAL);
//	public static final DataValueFormatType BIG_INTEGER = new DataValueFormatType(Dataml.BIG_INTEGER);
	

	/**
	 * Constructs a new DataValueFormatType having the given name.
	 *
	 * @param name The name of the new DataValueFormatType
	**/
	
	protected DataValueFormatType(String name)
	{
		super(name);
		
		sFormats.put(name, this);
	}
	

	/**
	 * Returns the DataValueFormatType corresponding to the given DataValueFormatType name.
	 *
	 * @param The name of the desired DataValueFormatType
	 * @return The DataValueFormatType that has the given name (if any)
	**/
	
	public static DataValueFormatType forName(String name)
	{
		DataValueFormatType result = (DataValueFormatType) sFormats.get(name);
		
		return (result);
	}
	

	/**
	 * This method supports serialization.
	 *
	**/
	
	public Object readResolve() throws ObjectStreamException
	{
		String name = getName();
		
		if (! sFormats.containsKey(name))
		{
			sFormats.put(name, this);
		}
		
		return ((DataValueFormatType) sFormats.get(name));
	}
}
