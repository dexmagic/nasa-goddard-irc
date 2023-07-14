//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorLibraryUpdateListener.java,v $
//  Revision 1.3  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/06/30 20:42:13  tames_cvs
//  Changed references of command procedures to scripts
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

package gov.nasa.gsfc.irc.description;

/**
 *	A DescriptorLibraryUpdateListener listens for notification of updates that
 *  are generated in reponse to certain changes on the DescriptorLibrary. 
 *	
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:23 $
 *  @author		John Higinbotham 
**/
public interface DescriptorLibraryUpdateListener
{
	/**
	 * Handle instrument update. 
	 * 
	**/
	public void handleInstrumentUpdate();

	/**
	 * Handle PipelineElement update. 
	 * 
	**/
	public void handlePipelineElementUpdate();

	/**
	 * Handle command procedure group update. 
	 * 
	**/
	public void handleScriptGroupUpdate();

	/**
	 * Handle command procedure update. 
	 * 
	**/
	public void handleScriptUpdate();

	/**
	 * Handle library update.
	 * 
	**/
	public void handleLibraryUpdate();
}
