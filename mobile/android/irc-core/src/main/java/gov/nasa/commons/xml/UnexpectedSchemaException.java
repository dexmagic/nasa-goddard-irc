//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: UnexpectedSchemaException.java,v $
//  Revision 1.4  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.2  2004/05/03 19:46:12  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/05/03 18:06:02  chostetter_cvs
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



/**
 * An UnexpectedSchemaException indicates that a document is not based on the expected 
 * schema type. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/07/12 14:26:24 $
 * @author	  John Higinbotham   
**/
public class UnexpectedSchemaException extends XmlException
{
	//---Error message
	private final static String MSG1 = "Encountered unexpected schema type!";

	//---Actual schema 
	String fActual = null;
	
	//---Expected schema
	String fExpected = null;
	
//----------------------------------------------------------------------------------

	/**
	 *  Default constructor.
	**/
	public UnexpectedSchemaException()
	{
		super(MSG1);
	}

	/**
	 *  Constructs a UnexpectedException with details. 
	 *
	 *  @param expected Expected schema. 
	 *  @param actual Actual schema. 
	**/
	public UnexpectedSchemaException(String expected, String actual)
	{
		super(MSG1 + " (expected="+expected+" actual="+actual+")");
		fActual = actual;
		fExpected = expected;
	}
	
	/**
	 * Get the name of the actual schema. 
	 * @return String Name of schema
	**/
	public String getActualSchemaName()
	{
		return fActual;
	}
	
	/**
	 * Get the name of the expected schema.
	 * @return String Name o fschema
	**/
	public String getExpectedSchemaName()
	{
		return fExpected;
	}
}
