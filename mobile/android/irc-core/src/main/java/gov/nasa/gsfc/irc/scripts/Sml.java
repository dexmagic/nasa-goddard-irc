//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Sml.java,v $
//  Revision 1.3  2004/08/11 05:42:57  tames
//  Script support
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/06/30 20:58:13  tames_cvs
//  Initial Version
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

package gov.nasa.gsfc.irc.scripts;

/**
 * The class serves as a central location for defining the constants associated 
 * with the SML XML schema. A signification portion of the SPL is based on
 * IML so only the additions to SML are defined here. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better understanding
 * of the structure and content of the data used to build the descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version 	$Date: 2004/08/11 05:42:57 $
 * @author	John Higinbotham   
 * @author	Troy Ames   
**/
public interface Sml 
{
	//--- Shared Attributes

	//--- Custom Attributes

	//--- Elements 
	static final String E_SCRIPT_GRP = "ScriptGroup";

	//--- Classes
	static final String C_SCRIPT_GRP = "gov.nasa.gsfc.irc.scripts.description.ScriptGroupDescriptor";
}
