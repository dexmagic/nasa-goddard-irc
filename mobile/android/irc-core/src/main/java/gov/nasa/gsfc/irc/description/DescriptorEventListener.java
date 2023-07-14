//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   3	IRC	   1.2		 11/13/2001 5:00:05 PMJohn Higinbotham Javadoc
//		update.
//   2	IRC	   1.1		 10/24/2001 10:04:59 AMBhavana Singh   rename
//		handleDescriptorChange to reloadDescriptorLibrary
//   1	IRC	   1.0		 9/28/2001 1:39:42 PM Bhavana Singh   
//  $
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
 *	A DescriptorEventListener listens for DescriptorEvents that are generated
 *  in reponse to changes in a Pipeline Algorithm Description. 
 *	
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:23 $
 *  @author		Bhavana Singh
**/
public interface DescriptorEventListener
{
	/**
	 * Reload the descriptor library to update the available list of pipeline algorithms. 
	 * 
	**/
	public void reloadDescriptorLibrary();
}
