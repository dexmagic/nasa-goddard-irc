//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataValueType.java,v $
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

package gov.nasa.gsfc.irc.data.description;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamedObject;


/**
 * A DataValueType enumerates the possible data value types.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:50 $ 
 * @author Carl F. Hostetter 
**/

public class DataValueType extends AbstractNamedObject 
	implements Serializable
{
	/**
	 * All known DataTypes, keyed by name.
	**/
	// NB: For initialization sequence, this has to come before any 
	// DataValueType constants.
	private static Map sTypes = new HashMap();
	
	public static final DataValueType _BOOLEAN = new DataValueType(Dataml._BOOLEAN);
	public static final DataValueType _BYTE = new DataValueType(Dataml._BYTE);
	public static final DataValueType _CHAR = new DataValueType(Dataml._CHAR);
	public static final DataValueType _SHORT = new DataValueType(Dataml._SHORT);
	public static final DataValueType _INT = new DataValueType(Dataml._INT);
	public static final DataValueType _LONG = new DataValueType(Dataml._LONG);
	public static final DataValueType _FLOAT = new DataValueType(Dataml._FLOAT);
	public static final DataValueType _DOUBLE = new DataValueType(Dataml._DOUBLE);
	public static final DataValueType BOOLEAN = new DataValueType(Dataml.BOOLEAN);
	public static final DataValueType BYTE = new DataValueType(Dataml.BYTE);
	public static final DataValueType CHARACTER = new DataValueType(Dataml.CHARACTER);
	public static final DataValueType SHORT = new DataValueType(Dataml.SHORT);
	public static final DataValueType INTEGER = new DataValueType(Dataml.INTEGER);
	public static final DataValueType LONG = new DataValueType(Dataml.LONG);
	public static final DataValueType FLOAT = new DataValueType(Dataml.FLOAT);
	public static final DataValueType DOUBLE = new DataValueType(Dataml.DOUBLE);
	public static final DataValueType STRING = new DataValueType(Dataml.STRING);
	public static final DataValueType COMPARABLE = new DataValueType(Dataml.COMPARABLE);
	public static final DataValueType BIG_DECIMAL = new DataValueType(Dataml.BIG_DECIMAL);
	public static final DataValueType BIG_INTEGER = new DataValueType(Dataml.BIG_INTEGER);
	public static final DataValueType BIT_ARRAY = new DataValueType(Dataml.BIT_ARRAY);
	public static final DataValueType DATE = new DataValueType(Dataml.DATE);
	

	/**
	 * Constructs a new DataValueType having the given name.
	 *
	 * @param name The name of the new DataValueType
	**/
	
	protected DataValueType(String name)
	{
		super (name);
		
		sTypes.put(name, this);
	}
	

	/**
	 * Returns the DataValueType corresponding to the given DataValueType name.
	 *
	 * @param The name of the desired DataValueType
	 * @return The DataValueType that has the given name (if any)
	**/
	
	public static DataValueType forName(String name)
	{
		DataValueType result = 
			(DataValueType) sTypes.get(name);
		
		return (result);
	}
	

	/**
	 * This method supports serialization.
	 *
	**/
	
	public Object readResolve() throws ObjectStreamException
	{
		String name = getName();
		
		if (! sTypes.containsKey(name))
		{
			sTypes.put(name, this);
		}
		
		return ((DataValueType) sTypes.get(name));
	}
}
