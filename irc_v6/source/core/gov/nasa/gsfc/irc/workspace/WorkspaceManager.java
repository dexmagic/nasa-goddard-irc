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

package gov.nasa.gsfc.irc.workspace;

import java.io.Reader;
import java.io.Writer;

/**
 * The interface for a Workspace manager. A Workspace is a snapshot of 
 * application state that can be saved and restored across instances of the
 * virtual machine.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/04 14:21:10 $
 * @author 	Troy Ames
 */
public interface WorkspaceManager
{
	/**
	 * Save the workspace to the given writer. 
	 *
	 * @param writer the Writer to use for workspace information. 
	**/
	public void saveWorkspace(Writer writer);
	
	/**
	 * Restore the workspace from the given reader. 
	 *
	 * @param reader the Reader to use for workspace information. 
	**/
	public void restoreWorkspace(Reader reader);
}


//--- Development History  ---------------------------------------------------
//
//  $Log: WorkspaceManager.java,v $
//  Revision 1.1  2005/04/04 14:21:10  tames
//  Initial support for workspaces.
//
//