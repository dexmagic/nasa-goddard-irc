//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataComparatorType.java,v $
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.state;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamedObject;


/**
 * A DataComparatorType enumerates the possible types of data comparator rules.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/04/27 19:46:21 $ 
 * @author Carl F. Hostetter 
**/

public class DataComparatorType extends AbstractNamedObject 
	implements Serializable
{
	/**
	 * All known DataComparatorTypes, keyed by name.
	**/
	// NB: For initialization sequence, this has to come before any 
	// DataComparatorType constants.
	private static Map sComparators = new HashMap();
	
	public static final DataComparatorType TRUE = 
		new DataComparatorType(Datastateml.TRUE);
	public static final DataComparatorType FALSE = 
		new DataComparatorType(Datastateml.FALSE);
	public static final DataComparatorType NULL = 
		new DataComparatorType(Datastateml.NULL);
	public static final DataComparatorType NOT_NULL = 
		new DataComparatorType(Datastateml.NOT_NULL);
	public static final DataComparatorType INSTANCEOF = 
		new DataComparatorType(Datastateml.INSTANCEOF);
	public static final DataComparatorType EQUALS = 
		new DataComparatorType(Datastateml.EQUALS);
	public static final DataComparatorType NOT_EQUALS = 
		new DataComparatorType(Datastateml.NOT_EQUALS);
	public static final DataComparatorType MATCHES = 
		new DataComparatorType(Datastateml.MATCHES);
	public static final DataComparatorType EQ = 
		new DataComparatorType(Datastateml.EQ);
	public static final DataComparatorType NEQ = 
		new DataComparatorType(Datastateml.NEQ);
 	public static final DataComparatorType LT = 
		new DataComparatorType(Datastateml.LT);
	public static final DataComparatorType GT = 
		new DataComparatorType(Datastateml.GT);
	public static final DataComparatorType LTEQ = 
		new DataComparatorType(Datastateml.LTEQ);
	public static final DataComparatorType GTEQ = 
		new DataComparatorType(Datastateml.GTEQ);
	

	/**
	 * Constructs a new DataComparatorType having the given name.
	 *
	 * @param name The name of the new DataComparatorType
	**/
	
	protected DataComparatorType(String name)
	{
		super (name);
		
		sComparators.put(name, this);
	}
	

	/**
	 * Returns the DataComparatorType corresponding to the given 
	 * DataComparatorType name.
	 *
	 * @param The name of the desired DataComparatorType
	 * @return The DataComparatorType that has the given name (if any)
	**/
	
	public static DataComparatorType forName(String name)
	{
		DataComparatorType result = 
			(DataComparatorType) sComparators.get(name);
		
		return (result);
	}
	

	/**
	 * This method supports serialization.
	 *
	**/
	
	public Object readResolve() throws ObjectStreamException
	{
		String name = getName();
		
		if (! sComparators.containsKey(name))
		{
			sComparators.put(name, this);
		}
		
		return ((DataComparatorType) sComparators.get(name));
	}
}
