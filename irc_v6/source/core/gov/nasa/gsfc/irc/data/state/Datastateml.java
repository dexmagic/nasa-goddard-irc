//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Datastateml.java,v $
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.state;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the Data State XML (DataStateML) schema. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better 
 * understanding of the structure and content of the data used to build the 
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
**/

public interface Datastateml extends Xsd
{
	static final String V_SCHEMA = "datastateml.xsd";
	
	static final String TRUE		= "true";
	static final String FALSE		= "false";
	static final String NULL		= "null";
	static final String NOT_NULL	= "not null";
	static final String INSTANCEOF	= "instanceof";
	static final String EQUALS		= "equals";
	static final String NOT_EQUALS	= "not equals";
	static final String MATCHES		= "matches";
	static final String EQ			= "=";
	static final String NEQ			= "!=";
	static final String LT			= "<";
	static final String GT			= ">";
	static final String LTEQ		= "<=";
	static final String GTEQ		= ">=";
	
	//--- Attributes
	static final String A_SOURCE_TYPE	= "sourceType";
	static final String A_COMPARATOR	= "comparator";
	static final String A_TARGET_TYPE	= "targetType";
	static final String A_TARGET_VALUE	= "targetValue";
	
	//--- Elements 
	static final String E_SWITCH			= "Switch";
	static final String E_CASE_SELECTION	= "CaseSelection";
	static final String E_CASE				= "Case";
	static final String E_DETERMINANT		= "Determinant";
	static final String E_COMPARISON		= "Comparison";
	
	//--- Classes
	static final String C_DATA_STATE_DETERMINER = 
		"gov.nasa.gsfc.irc.data.state.DataStateDeterminerDescriptor";
	static final String C_DETERMINANT = 
		"gov.nasa.gsfc.irc.data.state.DataStateDeterminantDescriptor";
	static final String C_COMPARATOR = 
		"gov.nasa.gsfc.irc.data.state.DataComparatorDescriptor";
	
	// Namespaces
	static final String N_DETERMINERS	= "DeterminerType";
	static final String N_COMPARISONS	= "ComparisonType";
}
