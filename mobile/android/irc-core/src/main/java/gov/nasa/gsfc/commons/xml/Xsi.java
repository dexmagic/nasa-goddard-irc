//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   2	IRC	   1.1		 11/13/2001 5:00:10 PMJohn Higinbotham Javadoc
//		update.
//   1	IRC	   1.0		 9/13/2001 6:11:22 PM John Higinbotham 
//  $
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

package gov.nasa.gsfc.commons.xml;

import org.jdom.Namespace;

/**
 * The class serves as a central location for defining some constants 
 * associated with XML instance files. <P>
 *
 * Developers should be certain to refer to the XML Schema to gain a better understanding  
 * of the structure and content of the data used to build the descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2004/09/12 20:43:50 $
 * @author			  John Higinbotham   
**/
public interface Xsi 
{
	public final static String URI					   = "http://www.w3.org/2001/XMLSchema-instance";
	public final static String PREFIX					= "xsi";
	public final static Namespace NAMESPACE			  = Namespace.getNamespace(PREFIX, URI);
	public final static String A_NO_NAMESPACE_SCHEMA_LOC = "noNamespaceSchemaLocation";
	public final static String A_SCHEMA_LOC = "schemaLocation";
}
