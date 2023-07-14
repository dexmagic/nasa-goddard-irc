//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PluginBus.java,v $
//  Revision 1.2  2004/05/27 18:21:26  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
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

package gov.nasa.gsfc.commons.app.plugins;

import java.util.logging.Logger;


/**
 *  Base class for working with application plugins.  
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/05/27 18:21:26 $
 *  @author John Higinbotham	
**/

public class PluginBus
{
	private static final String CLASS_NAME = 
		PluginBus.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	/**
	 * Returns the Logger to be used by clients of the PluginBus.
	 *
	 * @return The Logger to be used by clients of the PluginBus 
	**/
	
	public static Logger getLogger()
	{
		return (sLogger);
	}
}
