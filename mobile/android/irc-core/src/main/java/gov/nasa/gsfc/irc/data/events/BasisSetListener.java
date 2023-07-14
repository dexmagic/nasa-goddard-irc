//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/BasisSetListener.java,v 1.4 2005/07/13 20:09:57 tames Exp $
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

package gov.nasa.gsfc.irc.data.events;


/**
 * A BasisSetEventListener can listen for and receive BasisSetEvents.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/07/13 20:09:57 $
 * @author Carl F. Hostetter
 */

public interface BasisSetListener
{
	/**
	 * Causes this BasisSetListener to receive the given BasisSetEvent.
	 * 
	 * <p>NOTE!: The integrity of the data in the given BasisSet is <em>not</em> 
	 * guaranteed after this method returns. If you need to carry data over from 
	 * one call to the next, you <em>must</em> copy the data (by calling the 
	 * <code>copy()</code> method on the BasisSet(s) to be retained.
	 * 
	 * @param event A BasisSetEvent
	 */
	
	public void receiveBasisSetEvent(BasisSetEvent event);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetListener.java,v $
//  Revision 1.4  2005/07/13 20:09:57  tames
//  File header change only.
//
//  Revision 1.3  2005/04/05 20:35:36  chostetter_cvs
//  Fixed problem with release status of BasisSets from which a copy was made; fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//
