//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataFormatRuleType.java,v $
//  Revision 1.2  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
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
import gov.nasa.gsfc.irc.data.transformation.description.Datatransml;


/**
 * A DataFormatRuleType enumerates the possible types of data pattern 
 * rule.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:50 $ 
 * @author Carl F. Hostetter 
**/

public class DataFormatRuleType extends AbstractNamedObject 
	implements Serializable
{
	/**
	 * All known DataFormatPatternRuleTypes, keyed by name.
	**/
	// NB: For initialization sequence, this has to come before any 
	// DataFormatRuleType constants.
	private static Map sRules = new HashMap();
	
	public static final DataFormatRuleType AND = 
		new DataFormatRuleType(Datatransml.AND);
	public static final DataFormatRuleType NAND = 
		new DataFormatRuleType(Datatransml.NAND);
	public static final DataFormatRuleType OR = 
		new DataFormatRuleType(Datatransml.OR);
	public static final DataFormatRuleType XOR = 
		new DataFormatRuleType(Datatransml.XOR);
	

	/**
	 * Constructs a new DataFormatRuleType having the given name.
	 *
	 * @param name The name of the new DataFormatRuleType
	**/
	
	protected DataFormatRuleType(String name)
	{
		super (name);
		
		sRules.put(name, this);
	}
	

	/**
	 * Returns the DataFormatRuleType corresponding to the given 
	 * DataFormatRuleType name.
	 *
	 * @param The name of the desired DataFormatRuleType
	 * @return The DataFormatRuleType that has the given name (if any)
	**/
	
	public static DataFormatRuleType forName(String name)
	{
		DataFormatRuleType result = 
			(DataFormatRuleType) sRules.get(name);
		
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
		
		return ((DataFormatRuleType) sRules.get(name));
	}
}
