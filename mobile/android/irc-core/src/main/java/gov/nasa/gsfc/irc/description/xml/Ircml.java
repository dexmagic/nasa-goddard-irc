//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Ircml.java,v $
//  Revision 1.6  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.5  2004/11/19 21:40:49  smaher_cvs
//  Added A_ABBREVIATION.
//
//  Revision 1.4  2004/11/16 20:55:56  chostetter_cvs
//  Initial refactoring for addition of data parsing
//
//  Revision 1.3  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.2  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
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

package gov.nasa.gsfc.irc.description.xml;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the IRC XML schema. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better 
 * understanding of the structure and content of the data used to build the 
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/03/29 21:31:12 $
 * @author Carl F. Hostetter
**/

public interface Ircml extends Xsd
{
	static final String V_SCHEMA = "ircml.xsd";

	public static final String LOOKUP_TABLE = "GlobalTypeMap";
	
	//--- Attributes
	static final String A_DISPLAY_NAME = "displayName";
	static final String A_DESCRIPTION	= "description";
	
	//--- Elements 
	static final String E_PARAMETER = "Parameter";
	
	//--- Classes
	static final String C_PARAMETER = 
		"gov.nasa.gsfc.irc.description.xml.ParameterDescriptor";
	
	// Namespaces
	static final String N_IRC = "IrcType";
}
