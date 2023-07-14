//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Datatransml.java,v $
//  Revision 1.15  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.14  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.13  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.12  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.11  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.10  2006/02/07 21:07:50  chostetter_cvs
//  Keyed data selector now removes selected entry from input data map
//
//  Revision 1.9  2006/02/02 17:04:50  chostetter_cvs
//  Added support for Sequencer in Records
//
//  Revision 1.8  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.7  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.6  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.5  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.4  2005/09/14 19:32:07  chostetter_cvs
//  Added ability to publish parse results as a Message
//
//  Revision 1.3  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.2  2005/09/13 13:26:10  chostetter_cvs
//  Fixes for HAWC command formatting
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

package gov.nasa.gsfc.irc.data.transformation.description;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the Data Transformation XML (DTML) schema. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better 
 * understanding of the structure and content of the data used to build the 
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/06/01 22:22:43 $
 * @author Carl F. Hostetter
**/

public interface Datatransml extends Xsd
{
	static final String V_SCHEMA = "datatransml.xsd";
	
	static final String WILDCARD = "*";
	
	static final String NONE	= "none";
	static final String TIME	= "time";
	static final String PRINTF	= "printf";
	static final String TEXT	= "text";
	static final String BINARY	= "binary";
	static final String DECIMAL	= "decimal";
	static final String REAL	= "real";
	static final String AND		= "and";
	static final String NAND	= "nand";
	static final String OR		= "or";
	static final String XOR		= "xor";
	static final String FINE	= "fine";
	static final String INFO	= "INFO";
	static final String WARNING	= "WARNING";
	static final String SEVERE	= "SEVERE";
	
	//--- Attributes
	static final String A_VALUE		= "value";
	static final String A_PATTERN	= "pattern";
	static final String A_RULE		= "rule";

// Transformation attributes	
	static final String A_ENABLED = "enabled";
	static final String A_PUBLISH_PARSE_AS_MESSAGE = "publishParseAsMessage";
	static final String A_USE_DATA_NAME_AS_NAME = "useDataNameAsName";
		
// Record parse attributes
	static final String A_USE_INITIATOR_AS_PARSE_NAME = "useInitiatorAsParseName";
	
// Field parse attributes	
	static final String A_USE_LABEL_AS_PARSE_KEY = "useLabelAsParseKey";
	
// Record format attributes
	static final String A_USE_DATA_NAME_AS_INITIATOR = "useDataNameAsInitiator";

// Field format attributes	
	static final String A_APPLY_TO_ALL_FIELDS = "applyToAllFields";
	static final String A_APPLY_TO_REMAINING_FIELDS = "applyToRemainingFields";
	static final String A_USE_DATA_NAME_AS_LABEL = "useDataNameAsLabel";
	static final String A_USE_NAME_AS_KEYED_VALUE_SELECTOR = 
		"useNameAsKeyedValueSelector";
	static final String A_USE_NAME_AS_LABEL = "useNameAsLabel";
	
// List format attributes
	static final String A_LENGTH = "length";
	static final String A_SUPPRESS_FIRST_PREFIX = "suppressFirstPrefix";
	static final String A_SUPPRESS_LAST_POSTFIX = "suppressLastPostfix";
	
// Log attributes
	static final String A_LOG_NAME	= "logName";
	static final String A_LEVEL		= "level";

	//--- Elements 
	static final String E_TRANSFORMATION	= "Transformation";
	
	static final String E_SOURCE	= "Source";
	static final String E_TARGET	= "Target";
	static final String E_BUFFER	= "Buffer";
	static final String E_PARSE		= "Parse";
	static final String E_LOG		= "Log";
	static final String E_FORMAT	= "Format";
	
	static final String E_SWITCH				= "Switch";
	static final String E_CASE					= "Case";
	static final String E_FILE					= "File";
	static final String E_HEADER				= "Header";
	static final String E_FOOTER				= "Footer";
	static final String E_RECORD				= "Record";
	static final String E_SEQUENCER				= "Sequencer";
	static final String E_SEQUENCER_SEPARATOR	= "SequencerSeparator";
	static final String E_INITIATOR				= "Initiator";
	static final String E_INITIATOR_SEPARATOR	= "InitiatorSeparator";
	static final String E_TERMINATOR			= "Terminator";
	static final String E_FIELD_DEFAULTS		= "FieldDefaults";
	static final String E_FIELD					= "Field";
	static final String E_PREFIX				= "Prefix";
	static final String E_LABEL					= "Label";
	static final String E_LABEL_SEPARATOR		= "LabelSeparator";
	static final String E_VALUE					= "Value";
	static final String E_POSTFIX				= "Postfix";
	static final String E_LIST					= "List";
	static final String E_BASIS_SET				= "BasisSet";
	static final String E_BASIS_BUFFER			= "BasisBuffer";
	static final String E_DATA_BUFFER			= "DataBuffer";
	static final String E_DATA_BUFFER_DEFAULTS	= "DataBufferDefaults";
	
	//--- Classes
	static final String C_TRANSFORMATION = 
		"gov.nasa.gsfc.irc.data.transformation.description.DefaultDataTransformationDescriptor";
	static final String C_SOURCE = 
		"gov.nasa.gsfc.irc.data.transformation.description.DataSourceSelectionDescriptor";
	static final String C_TARGET = 
		"gov.nasa.gsfc.irc.data.transformation.description.DataTargetSelectionDescriptor";
	static final String C_BUFFER = 
		"gov.nasa.gsfc.irc.data.selection.description.BufferedDataSelectionDescriptor";
	static final String C_PARSE = 
		"gov.nasa.gsfc.irc.data.transformation.description.DefaultDataParseDescriptor";
	static final String C_SWITCHED_PARSER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SwitchedDataParserDescriptor";
	static final String C_RECORD_PARSER = 
		"gov.nasa.gsfc.irc.data.transformation.description.RecordParserDescriptor";
	static final String C_FIELD_PARSER = 
		"gov.nasa.gsfc.irc.data.transformation.description.FieldParserDescriptor";
	static final String C_VALUE_PARSE = 
		"gov.nasa.gsfc.irc.data.transformation.description.ValueParseDescriptor";
	static final String C_VALUE_PARSER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SimpleValueParserDescriptor";
	static final String C_LIST_VALUE_PARSER = 
		"gov.nasa.gsfc.irc.data.transformation.description.ListValueParserDescriptor";
	static final String C_SWITCHED_VALUE_PARSER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SwitchedValueParserDescriptor";
	static final String C_LOG = 
		"gov.nasa.gsfc.irc.data.transformation.description.DefaultDataLogDescriptor";
	static final String C_SWITCHED_LOGGER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SwitchedDataLoggerDescriptor";
	static final String C_FORMAT = 
		"gov.nasa.gsfc.irc.data.transformation.description.DefaultDataFormatDescriptor";
	static final String C_SWITCHED_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SwitchedDataFormatterDescriptor";
	static final String C_RECORD_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.RecordFormatterDescriptor";
	static final String C_FIELD_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.FieldFormatterDescriptor";
	static final String C_VALUE_FORMAT = 
		"gov.nasa.gsfc.irc.data.transformation.description.ValueFormatDescriptor";
	static final String C_VALUE_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SimpleValueFormatterDescriptor";
	static final String C_LIST_VALUE_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.ListValueFormatterDescriptor";
	static final String C_SWITCHED_VALUE_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.SwitchedValueFormatterDescriptor";
	static final String C_BASIS_SET_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.BasisSetFormatterDescriptor";
	static final String C_DATA_BUFFER_FORMATTER = 
		"gov.nasa.gsfc.irc.data.transformation.description.DataBufferFormatterDescriptor";
	
	// Namespaces
	static final String N_TRANSFORMATIONS	= "TransformationType";
	static final String N_PARSES			= "ParseType";
	static final String N_FORMATS			= "FormatType";
	static final String N_LOGS			= "LogType";
}
