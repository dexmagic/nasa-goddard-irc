//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//	$Log: Psml.java,v $
//	Revision 1.4  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.1  2004/10/07 21:38:26  chostetter_cvs
//	More descriptor refactoring, initial version of data transformation description
//	
//	Revision 1.2  2004/09/07 05:22:19  tames
//	Descriptor cleanup
//	
//	Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//	Initial version
//	 
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//	*  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//	*  Altered versions of this software must be plainly marked as such.
//	*  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description.xml;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the PSML XML schema. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better understanding
 * of the structure and content of the data used to build the descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/10/14 15:16:51 $
 * @author John Higinbotham
**/

public interface Psml extends Xsd
{
	static final String PKG = "gov,nasa.gsfc.irc.data.description";
	
	//--- Elements 
	static final String E_PARAMETER = "Parameter";

	//--- Classes
	static final String C_PARAMETER = PKG + DOT + "ParameterDescriptor";

	//--- MML Namespaces
	static final String N_PARAMETERSET = "ParameterSetType";
}
