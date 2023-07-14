//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataRequestSatisfactionRuleType.java,v $
//  Revision 1.3  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/03/15 00:36:02  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.1  2005/03/04 18:46:25  chostetter_cvs
//  Can now choose among three request satisfaction rules: all, any, and first
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

package gov.nasa.gsfc.irc.data;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamedObject;


/**
 * A DataRequestSatisfactionRuleType enumerates the possible types of data 
 * request satisfaction rule.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:51 $ 
 * @author Carl F. Hostetter 
**/

public class DataRequestSatisfactionRuleType extends AbstractNamedObject 
	implements Serializable
{
	/**
	 * All known DataRequestSatisfactionRuleTypes, keyed by name.
	**/
	// NB: For initialization sequence, this has to come before any 
	// DataRequestSatisfactionRuleType constants.
	private static Map sRules = new HashMap();
	
	public static final DataRequestSatisfactionRuleType ALL = 
		new DataRequestSatisfactionRuleType("all");
	public static final DataRequestSatisfactionRuleType ANY = 
		new DataRequestSatisfactionRuleType("any");
	public static final DataRequestSatisfactionRuleType FIRST = 
		new DataRequestSatisfactionRuleType("first");
	

	/**
	 * Constructs a new DataRequestSatisfactionRuleType having the given name.
	 *
	 * @param name The name of the new DataRequestSatisfactionRuleType
	**/
	
	protected DataRequestSatisfactionRuleType(String name)
	{
		super(name);
		
		sRules.put(name, this);
	}
	

	/**
	 *  Returns a String representation of this DataRequestSatisfactionRuleType.
	 *
	 *  @return A String representation of this DataRequestSatisfactionRuleType
	**/
	
	public String toString()
	{
		return (getName());
	}
	

	/**
	 * Returns the DataRequestSatisfactionRuleType corresponding to the given 
	 * DataRequestSatisfactionRuleType name.
	 *
	 * @param The name of the desired DataRequestSatisfactionRuleType
	 * @return The DataRequestSatisfactionRuleType that has the given name 
	 * 		(if any)
	**/
	
	public static DataRequestSatisfactionRuleType forName(String name)
	{
		DataRequestSatisfactionRuleType result = 
			(DataRequestSatisfactionRuleType) sRules.get(name);
		
		return (result);
	}
	

	/**
	 * This method supports serialization.
	 *
	**/
	
	public Object readResolve() throws ObjectStreamException
	{
		String name = getName();
		
		if (! sRules.containsKey(name))
		{
			sRules.put(name, this);
		}
		
		return ((DataRequestSatisfactionRuleType) sRules.get(name));
	}
}
