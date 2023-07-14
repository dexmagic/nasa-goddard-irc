//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/BasisRequester.java,v 1.14 2005/09/13 22:28:58 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.irc.components.HasComponentState;
import gov.nasa.gsfc.irc.data.events.BasisBundleListener;
import gov.nasa.gsfc.irc.data.events.BasisSetListener;

/**
 * A BasisRequester is a receiver of BasisBundleEvents and BasisSetEvents, all
 * from the same BasisBundle, that is associated with exactly one BasisRequest
 * on that BasisBundle, and that knows how to satisfy its BasisRequest from the
 * BasisSets contained in the stream of Events it receives.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/09/13 22:28:58 $
 * @author Carl F. Hostetter
 * @author Troy Ames
 */
public interface BasisRequester extends 
	BasisBundleListener, BasisSetListener, HasComponentState
{
	/**
	 * Sets the BasisRequest to be satisfied by this BasisRequester to 
	 * the given BasisRequest. 
	 * 
	 * @param basisRequest A BasisRequest
	 * @throws IllegalArgumentException if the given BasisRequest is not valid in 
	 * 		some way
	 */	
	public void setBasisRequest(BasisRequest basisRequest);	
	
	/**
	 * Returns the BasisRequest currently associated with this 
	 * BasisRequester.
	 * 
	 * @return The BasisRequest currently associated with this 
	 * 		BasisRequester
	 */	
	public BasisRequest getBasisRequest();
	
	/**
	 * Sets the downsampling rate of all of this BasisRequester to the given rate. 
	 * To indicate no downsampling, set this rate to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of this BasisRequester
	 */
	public void setDownsamplingRate(int downsamplingRate);
		
	/**
	 * Gets the downsampling rate of this BasisRequester. 
	 * 
	 * @return The downsampling rate of this BasisRequester
	 */
	public int getDownsamplingRate();
	
	/**
	 * Returns a BasisSet that satisfies the BasisRequest associated with this
	 * BasisRequester, or null if there is not yet sufficient data available to
	 * satisfy the BasisRequest.
	 * 
	 * @return A BasisSet that satisfies the BasisRequest associated with this
	 *         BasisRequester, or null if there is not yet sufficient data
	 *         available to satisfy the BasisRequest
	 */	
	public BasisSet satisfyRequest();
		
	/**
	 * Returns a BasisSet that satisfies the BasisRequest associated with this
	 * BasisRequester. This call will block until the request is satisfied or
	 * until this BasisRequester is cleared or stopped (in which case the result
	 * will be null).
	 * 
	 * @return A BasisSet that satisfies the request or null if this requester
	 *         is stopped.
	 * @throws InterruptedException if the block is interrupted
	 */	
	public BasisSet blockingSatisfyRequest()
		throws InterruptedException;
		
	/**
	 * Clears (and releases) all queued data (both pending and satisfying) 
	 * from this BasisRequester. Any currently blocked request will be 
	 * unblocked.
	 */	
	public void clear();

	/**
	 *  Starts this BasisRequester.
	 */ 
	public void start();
		
	/**
	 *  Stops this BasisRequester.
	 */	
	public void stop();
		
	/**
	 *  Kills this BasisRequester. A killed BasisRequester cannot be subsequently 
	 *  restarted or otherwise reused.
	 */	
	public void kill();
		
	/**
	 * Adds the given BasisBundleListener as a listener for BasisBundleEvents 
	 * from this BasisRequester.
	 *
	 * @param listener A BasisBundleListener
	 **/	
	public void addBasisBundleListener(BasisBundleListener listener);
		
	/**
	 * Removes the given BasisBundleListener as a listener for BasisBundleEvents 
	 * from this BasisRequester.
	 *
	 * @param listener A BasisBundleListener
	 **/	
	public void removeBasisBundleListener(BasisBundleListener listener);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: BasisRequester.java,v $
//	Revision 1.14  2005/09/13 22:28:58  tames
//	Changes to refect BasisBundleEvent refactoring.
//	
//	Revision 1.13  2005/09/09 21:32:34  tames
//	Primarily JavaDoc updates.
//	
//	Revision 1.12  2005/08/26 22:13:30  tames_cvs
//	Changes that are an incomplete refactoring. Also added initial support for history.
//	
//	Revision 1.11  2005/05/24 21:12:23  chostetter_cvs
//	Fixed some issues involving changing BasisRequests at run time
//	
//	Revision 1.10  2005/04/16 04:04:21  tames
//	Changes to reflect refactored state and activity packages.
//	
//	Revision 1.9  2005/03/04 18:45:51  chostetter_cvs
//	Can now make both blocking and non-blocking satisfaction requests
//	
//	Revision 1.8  2005/02/24 23:46:41  chostetter_cvs
//	Really fixed starting/stopping/synchronization behavior
//	
//	Revision 1.7  2004/11/10 19:31:28  tames
//	Added a getDownsamplingRate method
//	
//	Revision 1.6  2004/07/24 02:46:11  chostetter_cvs
//	Added statistics calculations to DataBuffers, renamed some classes
//	
//	Revision 1.5  2004/07/22 17:15:00  chostetter_cvs
//	DataRequester clears its BasisRequesters when it stops
//	
//	Revision 1.4  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.3  2004/07/16 15:18:31  chostetter_cvs
//	Revised, refactored Component activity state
//	
//	Revision 1.2  2004/07/09 22:29:11  chostetter_cvs
//	Extensive testing of Input/Output interaction, supports simple BasisRequests
//	
//	Revision 1.1  2004/07/06 21:57:12  chostetter_cvs
//	More BasisRequester, DataRequester work
//	
//	Revision 1.1  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.2  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.1  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
