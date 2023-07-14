//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC) project.
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
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.xml;

import org.jdom.Namespace;

/**
 * The class serves as a central location for defining some constants 
 * associated with the XML include specification. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better 
 * of the structure and content of the data used to build the descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2005/05/25 19:18:02 $
 * @author			  John Higinbotham   
**/
public interface Xinclude 
{
	public final static String URI		  = "http://www.w3.org/2003/XInclude";
	public final static String PREFIX	   = "xinclude";
	public final static Namespace NAMESPACE = Namespace.getNamespace(PREFIX, URI);
	public final static String A_HREF	   = "href";
	public final static String A_PARSE	  = "parse";
	public final static String E_INCLUDE	= "include";
	public final static String V_XML		= "xml";
	public final static String V_TEXT	   = "text";
}

//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   3	IRC	   1.2		 11/14/2001 9:04:47 AMJohn Higinbotham Javadoc
//		updates.
//   2	IRC	   1.1		 11/13/2001 5:00:09 PMJohn Higinbotham Javadoc
//		update.
//   1	IRC	   1.0		 9/13/2001 6:11:21 PM John Higinbotham 
//  $
