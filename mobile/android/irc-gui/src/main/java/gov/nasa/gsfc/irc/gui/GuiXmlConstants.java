//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui;

/**
 * This static class advertises any XML constants needed by the GUI.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/09/22 18:16:29 $
 * @author	Ken Wootton
 * @author 	Troy Ames
 */
public class GuiXmlConstants
{
	/**
	 * Name space key for property editor/renderer information in a typemap file.
	 */
	public static final String PROPERTY_CELL_LOOK_UP =
		"PropertyCellCustomization";

	/**
	 * Name space key for cell editors in a typemap file.
	 */
	public static final String CELL_EDITOR_NAMESPACE = 
		"PropertyCellEditors";

	/**
	 * Name space key for cell renderers in a typemap file.
	 */
	public static final String CELL_RENDERER_NAMESPACE = 
		"PropertyCellRenderers";

	/**
	 * Look up table for the global typemap.
	 */
	public static final String TYPE_MAP_LOOK_UP = "GlobalMappings";

	/**
	 * Name space for the type map.
	 */
	public static final String TYPE_NAMESPACE = "Type";
	
	// Private constructor
	private GuiXmlConstants()
	{
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: GuiXmlConstants.java,v $
//  Revision 1.3  2004/09/22 18:16:29  tames_cvs
//  Added Javadoc comments.
//
//  Revision 1.2  2004/09/21 22:05:55  tames_cvs
//  Changed to a class
//
//  Revision 1.1  2004/09/16 21:09:30  jhiginbotham_cvs
//  Initial version.
// 
