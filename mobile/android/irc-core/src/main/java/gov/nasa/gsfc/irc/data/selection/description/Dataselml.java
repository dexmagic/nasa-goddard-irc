//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.data.selection.description;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the Data Selection Markup Language (Dataselml) XML schema. <P>
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

public interface Dataselml extends Xsd
{
	static final String V_SCHEMA = "dataselml.xsd";
	
	//--- Attributes
	static final String A_INDEX			= "index";
	static final String A_LENGTH		= "length";
	static final String A_BUFFERED		= "buffered";
	static final String A_SIZE			= "size";
	static final String A_NAME			= "name";
	static final String A_KEY			= "key";
	static final String A_DELIMITER		= "delimiter";
	static final String A_REGEX			= "regex";
	static final String A_FLAGS			= "flags";
	static final String A_GROUP_NUMBER 	= "groupNumber";
	static final String A_SEPARATOR		= "separator";
	static final String A_INITIAL_COUNT	= "initialCount";

	//--- Elements 
	static final String E_VALUE				= "Value";
	static final String E_SELECTION			= "Selection";
	static final String E_CONCATENATION		= "Concatenation";
	static final String E_CLASS				= "Class";
	static final String E_DATA_NAME			= "DataName";
	static final String E_COUNTER			= "Counter";
	static final String E_BYTE_DELIMITED	= "ByteDelimited";
	static final String E_CHAR_DELIMITED	= "CharDelimited";
	static final String E_BYTE_RANGE		= "ByteRange";
	static final String E_CHAR_RANGE		= "CharRange";
	static final String E_BY_REG_EX_PATTERN	= "ByRegExPattern";
	static final String E_BY_NAME			= "ByName";
	static final String E_BY_FIELD_NAME		= "ByFieldName";
	static final String E_FIELD_VALUE		= "FieldValue";
	static final String E_BY_KEY			= "ByKey";
	
	//--- Classes
	static final String C_DATA_SELECTION = 
		"gov.nasa.gsfc.irc.data.selection.description.DefaultDataSelectionDescriptor";
	static final String C_DATA_FEATURE_SELECTION = 
		"gov.nasa.gsfc.irc.data.selection.description.DataFeatureSelectionDescriptor";
	static final String C_DATA_SPAN_SELECTION = 
		"gov.nasa.gsfc.irc.data.selection.description.DataSpanSelectionDescriptor";
	static final String C_DATA_CONTAINER_SELECTION = 
		"gov.nasa.gsfc.irc.data.selection.description.DataContainerSelectionDescriptor";
	static final String C_STRING_CONSTANT_VALUE_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.StringConstantValueSelectorDescriptor";
	static final String C_BYTE_DELIMITED_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.ByteDelimitedDataSelectorDescriptor";
	static final String C_BYTE_RANGE_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.ByteRangeDataSelectorDescriptor";
	static final String C_CHAR_DELIMITED_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.CharDelimitedDataSelectorDescriptor";
	static final String C_CHAR_RANGE_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.CharRangeDataSelectorDescriptor";
	static final String C_COUNTER_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.CounterDataSelectorDescriptor";
	static final String C_DATA_CONCATENATION = 
		"gov.nasa.gsfc.irc.data.selection.description.DataConcatenationDescriptor";
	static final String C_CLASS_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.ClassSelectorDescriptor";
	static final String C_DATA_NAME_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.DataNameSelectorDescriptor";
	static final String C_DATA_VALUE_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.DataValueSelectorDescriptor";
	static final String C_NUM_TOKENS_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.NumTokensSelectorDescriptor";
	static final String C_KEYED_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.KeyedDataSelectorDescriptor";
	static final String C_NAMED_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.NamedDataSelectorDescriptor";
	static final String C_REG_EX_PATTERN_DATA_SELECTOR = 
		"gov.nasa.gsfc.irc.data.selection.description.RegExPatternDataSelectorDescriptor";

	//--- Namespaces
	static final String N_SELECTORS = "SelectorType";
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Dataselml.java,v $
//  Revision 1.8  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.7  2006/04/27 23:31:09  chostetter_cvs
//  Added support for field value selection in determinants
//
//  Revision 1.6  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.5  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.4  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.3  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.2  2005/09/15 15:34:08  chostetter_cvs
//  Added support for command counter value selection
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//
