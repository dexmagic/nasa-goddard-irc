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

package gov.nasa.gsfc.irc.data.description;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the DataML XML schema. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better 
 * understanding of the structure and content of the data used to build the 
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/06/26 20:10:29 $
 * @author Carl F. Hostetter
**/

public interface Dataml extends Xsd
{
	static final String V_SCHEMA = "dataml.xsd";
	
	static final String _BOOLEAN		= "boolean";
	static final String _BYTE			= "byte";
	static final String _CHAR			= "char";
	static final String _SHORT		= "short";
	static final String _INT			= "int";
	static final String _LONG			= "long";
	static final String _FLOAT		= "float";
	static final String _DOUBLE		= "double";
	static final String BOOLEAN		= "Boolean";
	static final String BYTE			= "Byte";
	static final String CHARACTER		= "Character";
	static final String SHORT			= "Short";
	static final String INTEGER		= "Integer";
	static final String LONG			= "Long";
	static final String FLOAT			= "Float";
	static final String DOUBLE		= "Double";
	static final String STRING		= "String";
	static final String COMPARABLE	= "Comparable";
	static final String BIG_DECIMAL	= "BigDecimal";
	static final String BIG_INTEGER	= "BigInteger";
	static final String BIT_ARRAY		= "BitArray";
	static final String DATE			= "Date";
	static final String BASIS_BUFFER	= "BasisBuffer";
	static final String DATA_BUFFER	= "DataBuffer";
	static final String BASIS_BUNDLE	= "BasisBundle";
	static final String ASCII			= "ASCII";
	static final String UNICODE		= "Unicode";

	//--- Attributes
	static final String A_UNITS			= "units";
	static final String A_COADD_HINT		= "coaddHint";	
	static final String A_SIZE			= "size";
	static final String A_EXPERT			= "expert";
	static final String A_HIDDEN			= "hidden";
	static final String A_PREFERRED		= "preferred";
	static final String A_CONSTRAINED		= "constrained";
	static final String A_BOUND			= "bound";
	static final String A_REQUIRED		= "required";
	static final String A_PROPERTY_NAME	= "propertyName";
	static final String A_HIGH			= "high";
	static final String A_LOW				= "low";
	static final String A_READ_ONLY		= "readOnly";
	static final String A_PREFIX_GROUP_NAME_TO_BUNDLE_NAMES = 
		"prefixGroupNameToBundleNames";
	static final String A_SUPPRESS_FIRST_FIELD_PREFIX = 
		"suppressFirstFieldPrefix";
	static final String A_SUPPRESS_LAST_FIELD_POSTFIX = 
		"suppressLastFieldPostfix";
	static final String A_USE_FIELD_NAME_AS_FIELD_LABEL = 
		"useFieldNameAsFieldLabel";
	static final String A_USE_NAME_AS_LABEL = 
		"useNameAsLabel";

	//--- Elements 
	static final String E_DATA				= "Data";
	static final String E_DATA_BUFFER			= "DataBuffer";
	static final String E_DATA_BUNDLE			= "DataBundle";
	static final String E_DIMENSION			= "Dimension";
	static final String E_PIXEL_BUNDLE		= "PixelBundle";
	static final String E_BASIS_BUFFER		= "BasisBuffer";
	static final String E_BASIS_BUNDLE		= "BasisBundle";
	static final String E_BASIS_BUNDLE_DEFAULTS	= "BasisBundleDefaults";
	static final String E_BASIS_BUNDLE_GROUP	= "BasisBundleGroup";
	static final String E_DATA_MAP			= "DataMap";
	static final String E_DATA_MAP_ENTRY		= "DataMapEntry";
	static final String E_LIST_CONSTRAINT		= "ListConstraint";
	static final String E_RANGE_CONSTRAINT		= "RangeConstraint";
	static final String E_BIT_RANGE_CONSTRAINT	= "BitRangeConstraint";
	static final String E_CHOICE				= "Choice";
	
	//--- Classes
	static final String C_DATA_ELEMENT =  
		"gov.nasa.gsfc.irc.data.description.DataElementDescriptor";
	static final String C_DATA_SPACE =  
		"gov.nasa.gsfc.irc.data.description.DataSpaceDescriptor";
	static final String C_DATA_BUFFER = 
		"gov.nasa.gsfc.irc.data.description.DataBufferDescriptor";
	static final String C_DATA_BUNDLE = 
		"gov.nasa.gsfc.irc.data.description.DataBundleDescriptor";
	static final String C_DIMENSION = 
		"gov.nasa.gsfc.irc.data.description.DimensionDescriptor";
	static final String C_PIXEL_BUNDLE = 
		"gov.nasa.gsfc.irc.data.description.PixelBundleDescriptor";
	static final String C_BASIS_BUNDLE = 
		"gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor";
	static final String C_BASIS_BUNDLE_GROUP = 
		"gov.nasa.gsfc.irc.data.description.BasisBundleGroupDescriptor";
	static final String C_DATA_MAP = 
		"gov.nasa.gsfc.irc.data.description.DataMapDescriptor";
	static final String C_DATA_MAP_ENTRY = 
		"gov.nasa.gsfc.irc.data.description.DataMapEntryDescriptor";
	static final String C_LIST_CONSTRAINT = 
		"gov.nasa.gsfc.irc.data.description.ListConstraintDescriptor";
	static final String C_CHOICE = 
		"gov.nasa.gsfc.irc.data.description.ChoiceValueDescriptor";
	static final String C_RANGE_CONSTRAINT = 
		"gov.nasa.gsfc.irc.data.description.RangeConstraintDescriptor";
	static final String C_BIT_RANGE_CONSTRAINT = 
		"gov.nasa.gsfc.irc.data.description.BitRangeConstraintDescriptor";
	static final String C_RECORD = 
		"gov.nasa.gsfc.irc.data.description.RecordDescriptor";
	static final String C_FIELD = 
		"gov.nasa.gsfc.irc.data.description.FieldDescriptor";

	//--- Namespaces
	static final String N_DATA = "DataType";
	static final String N_CONSTRAINTS = "ConstraintType";
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Dataml.java,v $
//  Revision 1.21  2006/06/26 20:10:29  smaher_cvs
//  Added coadd hints
//
//  Revision 1.20  2006/03/31 21:57:39  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.19  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.18  2005/02/14 22:06:01  chostetter_cvs
//  Revised data formatting, includes binary formatting with mask
//
//  Revision 1.17  2005/01/20 08:10:50  tames
//  Changes to support choice descriptors
//
//  Revision 1.16  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.15  2005/01/07 20:27:39  tames
//  Added support for bean feature like attributes.
//
//  Revision 1.14  2004/12/13 04:14:32  chostetter_cvs
//  Message formatting: text and (initial form) binary
//
//  Revision 1.13  2004/11/16 20:55:56  chostetter_cvs
//  Initial refactoring for addition of data parsing
//
//  Revision 1.12  2004/11/15 20:33:08  chostetter_cvs
//  Fixed remaining Message formatting issues
//
//  Revision 1.11  2004/11/09 22:51:29  chostetter_cvs
//  Further data transformation work
//
//  Revision 1.10  2004/10/18 22:58:15  chostetter_cvs
//  More data transformation work
//
//  Revision 1.9  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.8  2004/10/15 17:24:46  jhiginbotham_cvs
//  Fixed list constraint so that choices would be correctly unmarshalled.
//
//  Revision 1.7  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.6  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.5  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.4  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/09/10 14:49:03  chostetter_cvs
//  More data description work
//
//  Revision 1.2  2004/09/07 18:02:39  chostetter_cvs
//  Defines DataSpaceDescriptor as top-most level of data description
//
//  Revision 1.1  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
