//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: SchemaUtil.java,v $
//  Revision 1.7  2004/09/14 13:56:27  chostetter_cvs
//  Organized imports
//
//  Revision 1.6  2004/09/12 20:43:50  tames
//  Fixed a schema namespace matching bug. Code always assumed the
//  schema didn't have a namespace.
//
//  Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.4  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.3  2004/05/03 19:46:12  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.xml;

import org.jdom.Attribute;
import org.jdom.Document;


/**
 *  This class contains XML Schema utility methods.  
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/14 13:56:27 $
 *  @author		John Higinbotham	
**/
public class SchemaUtil 
{
	public static final String SCHEMA_LOCATION	= "noNamespaceSchemaLocation";
	public static final String SCHEMA_INCLUDE	= "include";
	public static final String SCHEMA_LOC_ATT	= "schemaLocation";
	
	/**
	 * Get the schema file name given a document produced from an XML instance file.
	 *
	 * @param document Document to get schema from
	 * @return schema file name
	 **/
	public static String getSchemaFilename(Document document)
	{
		String rval = null;
		if (document != null)
		{
			Attribute a = 
				document.getRootElement().getAttribute(
					Xsi.A_SCHEMA_LOC, Xsi.NAMESPACE);
			if (a == null)
			{
				a = document.getRootElement().getAttribute(
					Xsi.A_NO_NAMESPACE_SCHEMA_LOC, Xsi.NAMESPACE);
			}
			
			if (a != null)
			{
				rval = a.getValue();
			}
		}
		return rval;
	}

	/**
	 * Check that the document is based on the expected schema and complain if it is not by
	 * throwing a SchemaMismatchException.
	 *
	 * @param document Document to check
	 * @param schemaFilename String representing name of schema document is supposed to be based on
	 **/
	public static void checkSchemaFilename(Document document, String schemaFilename) 
		throws UnexpectedSchemaException
	{
		String actual = getSchemaFilename(document);
		if (actual.indexOf(schemaFilename) < 0)
		{
			throw (new UnexpectedSchemaException(schemaFilename, actual));
		}
	}
}
