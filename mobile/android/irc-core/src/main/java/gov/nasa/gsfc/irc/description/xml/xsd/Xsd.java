//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.description.xml.xsd;

/**
 * The interface serves as a central location for defining the constants associated 
 * with the XML Schema.
 *
 * If you are new to XML Schemas or need a refresher on them, you should consult the
 * W3C XML Schema Primer to gain a better understanding of the terms used in this and
 * related descriptor classes. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version             $Date: 2006/03/29 21:31:12 $
 * @author              John Higinbotham
**/
public interface Xsd 
{
	//--- Enumerations 
	static final String PKG = "gov.nasa.gsfc.irc.description.xml.xsd";
	public static final String DOT = ".";

	//--- Schema Enumerations 
	static final String MAX_OCCURS_UNBOUNDED = "unbounded";
	static final String USE_REQUIRED         = "required";
	static final String USE_OPTIONAL         = "optional";
	static final String USE_DEFAULT          = "default";
	static final String XSD_STRING           = "xsd:string";
	static final String XSD_INT              = "xsd:int";
	static final String XSD_BOOLEAN          = "xsd:boolean";

	//--- Attributes
	static final String A_BASE		= "base";
	static final String A_VALUE		= "value";
	static final String A_NAME		= "name";
	static final String A_TYPE		= "type";
	static final String A_MIN_OCCURS	= "minOccurs";
	static final String A_MAX_OCCURS	= "maxOccurs";
	static final String A_ABSTRACT	= "abstract";
	static final String A_USE			= "use";
	static final String A_DEFAULT		= "default";
	static final String A_REF			= "ref";

	//--- Elements 
	static final String E_SIMPLE_TYPE		= "simpleType";
	static final String E_COMPLEX_TYPE	= "complexType";
	static final String E_GROUP			= "group";
	static final String E_ELEMENT			= "element";
	static final String E_RESTRICTION		= "restriction";
	static final String E_ENUMERATION		= "enumeration";
	static final String E_CHOICE			= "choice";
	static final String E_COMPLEX_CONTENT	= "complexContent";
	static final String E_ATTRIBUTE		= "attribute";
	static final String E_EXTENSION		= "extension";
	static final String E_GROUP_REF		= "group";
	static final String E_SEQUENCE		= "sequence";

	//--- Classes
	static final String C_SIMPLE_TYPE 	= PKG + DOT + "SimpleTypeXSD";
	static final String C_COMPLEX_TYPE	= PKG + DOT + "ComplexTypeXSD";
	static final String C_GROUP			= PKG + DOT + "GroupXSD";
	static final String C_ELEMENT			= PKG + DOT + "ElementXSD";
	static final String C_RESTRICTION		= PKG + DOT + "RestrictionXSD";
	static final String C_ENUMERATION 	= PKG + DOT + "EnumerationXSD";
	static final String C_CHOICE 			= PKG + DOT + "ChoiceXSD";
	static final String C_COMPLEX_CONTENT 	= PKG + DOT + "ComplexContentXSD";
	static final String C_ATTRIBUTE		= PKG + DOT + "AttributeXSD";
	static final String C_EXTENSION 		= PKG + DOT + "ExtensionXSD";
	static final String C_GROUP_REF 		= PKG + DOT + "GroupRefXSD";
	static final String C_SEQUENCE		= PKG + DOT + "SequenceXSD";
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Xsd.java,v $
//  Revision 1.7  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.6  2005/01/07 20:33:09  tames
//  Changed localizedName to displayName to match bean naming
//  conventions.
//
//  Revision 1.5  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/09/07 14:12:52  tames
//  More descriptor cleanup
//
