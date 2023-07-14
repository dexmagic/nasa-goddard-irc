//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/DataRequester.java,v 1.19 2006/01/02 03:48:09 tames Exp $
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
import gov.nasa.gsfc.irc.data.events.DataListener;


/**
 * A DataRequester contains and manages a set of BasisRequests representing a
 * coordinated request for data across one or more BasisBundles, according to a
 * selectable data request satisfaction rule. A DataRequester can further be
 * configured to issue and satisfy its current composite request continuously or
 * only a specified number of times; and reports the result of each such request
 * to any interested listeners packaged in a DataSet. A DataRequester must be
 * started before it will issue and satisfy requests, and it can be paused and
 * resumed while engaged in making requests.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/02 03:48:09 $
 * @author Carl F. Hostetter
 */

public interface DataRequester extends HasComponentState, HasBasisRequests
{
	/**
	 *  Adds the given BasisRequest to the Set of BasisRequests managed by 
	 *  this DataRequester.  If this DataRequester is not already a 
	 *  listener on the BasisBundle indicated in the given BasisRequest, it 
	 *  will add itself as one.
	 *  
	 *  @param basisRequest A BasisRequest
	 */	 
	public void addBasisRequest(BasisRequest basisRequest);
		
	/**
	 *  Sets the given array as the BasisRequests managed 
	 *  by this DataRequester replacing any previously specified requests.
	 *  
	 *  @param basisRequests An array of BasisRequests
	 */	 
	public void setBasisRequests(BasisRequest[] basisRequests);
		
	/**
	 *  Removes all BasisRequests from this DataRequester.
	 *  
	 *  @param basisBundleId The BasisBundleId of the BasisBundle from which 
	 * 		all BasisRequests are to be removed
	 */	 
	public void removeBasisRequests();
		
	/**
	 *  Removes all BasisRequests from this BasisRequester that are made on the 
	 *  BasisBundle specified by the given BasisBundleId.
	 *  
	 *  @param basisBundleId The BasisBundleId of the BasisBundle from which 
	 * 		all BasisRequests are to be removed
	 */
	 
	public void removeBasisRequests(BasisBundleId basisBundleId);
	
	
	/**
	 *  Removes the given BasisRequest from the Set of BasisRequests managed 
	 *  by this DataRequester.
	 *  
	 *  @param basisRequest The BasisRequest to remove
	 */
	 
	public void removeBasisRequest(BasisRequest basisRequest);
	
	
	/**
	 *  Sets the DataRequestSatisfactionRuleType indicator according to which this 
	 *  DataRequester is to satisfy its current Set of BasisRequests to the given 
	 *  DataRequestSatisfactionRuleType.
	 *  
	 *  @param rule The DataRequestSatisfactionRuleType indicator according to which 
	 *  		this DataRequester is to satisfy its current Set of BasisRequests
	 */
	 
	public void setDataRequestSatisfactionRule(DataRequestSatisfactionRuleType rule);
	
	
	/**
	 *  Returns the DataRequestSatisfactionRuleType indicator according to which 
	 *  this DataRequester is currently configured to satisfy its current Set of 
	 *  BasisRequests.
	 *  
	 *  @return The DataRequestSatisfactionRuleType indicator according to which 
	 *  		this DataRequester is currently configured to satisfy its current Set 
	 *  		of BasisRequests
	 */
	 
	public DataRequestSatisfactionRuleType getDataRequestSatisfactionRule();
	
	
	/**
	 *  Sets the repetition of this DataRequester to the given value. If 
	 *  this DataRequester is not currently continuous, then the repetition 
	 *  defines the number of requests that the DataRequester will issue 
	 *  when it is started.
	 *  
	 *  @param numRepetitions The number of repetitions this DataRequester 
	 * 		should make when started
	 */
	 
	public void setRepetition(int numRepetitions);
	
	
	/**
	 *  Returns the number of request repetitions this DataRequester will 
	 *  make when it is next started. If this DataRequester is currently 
	 *  continuous, then the result will be Integer.NaN.
	 *  
	 *  @param numRepetitions The number of repetitions this DataRequester 
	 * 		will make when started
	 */
	 
	public int getRepetition();
	
	
	/**
	 *  After this DataRequester has been started, this will return the 
	 *  current number of unsatisfied request repetitions remaining for this 
	 *  DataRequester to satisfy. If this DataRequester has not yet been 
	 *  started, the result is the same as the current repetition value. In 
	 *  either case, if this DataRequester is currently continuous, then the 
	 *  result will always be Integer.NaN.
	 *  
	 *  @return The current number of unsatisfied request repetitions remaining 
	 * 		for this DataRequester to satisfy
	 */
	 
	public int getRemainingRepetitions();
	
	
	/**
	 *  Blocks on the calling Thread until this DataRequester has stopped.
	 *  
	 *  @throws InterruptedException if the wait is interrupted by another Thread
	 */
	 
	public void waitUntilStopped()
		throws InterruptedException;
	
	
	/**
	 *  Causes this DataRequester to repeat its current Set of BasisRequests 
	 *  indefinitely, once it is started and while not paused. To put this 
	 *  DataRequester back out of continuous mode, use <code>setRepeitition</code> 
	 *  to set a finite number of repetitions.
	 *  
	 */
	 
	public void setContinuous();
	
	
	/**
	 *  Returns true if this DataRequester is currently in continuous mode, 
	 *  false otherwise.
	 *  
	 *  @return True if this DataRequester is currently in continuous mode, 
	 *  		false otherwise
	 */
	 
	public boolean isContinuous();


	/**
	 * Sets the downsampling rate of all of the BasisRequesters of this 
	 * DataRequester to the given rate. To indicate no downsampling, set this rate 
	 * to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of the BasisRequesters of this 
	 * 		DataRequester
	 */
	
	public void setDownsamplingRate(int downsamplingRate);

	
	/**
	 * Gets the downsampling rate of the BasisRequester of this 
	 * DataRequester.
	 * 
	 * @return The downsampling rate of the BasisRequesters of this 
	 * 		DataRequester
	 */
	
	public int getDownsamplingRate();
	
	
	/**
	 *  Starts this DataRequester.
	 * 
	 */
	 
	public void start();
	
	
	/**
	 *  Stops this DataRequester.
	 *  
	 */
	
	public void stop();
	
	
	/**
	 *  Kills this DataRequester. A killed DataRequester cannot be subsequently 
	 *  restarted or otherwise reused.
	 * 
	 */
	
	public void kill();


	/**
	 * Adds the given DataListener as a listener for Data Events 
	 * from this DataRequester.
	 *
	 * @param listener A DataListener
	 **/
	
	public void addDataListener(DataListener listener);
	
	
	/**
	 * Removes the given DataListener as a listener for Data Events 
	 * from this DataRequester.
	 *
	 * @param listener A DataListener
	 **/
	
	public void removeDataListener(DataListener listener);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DataRequester.java,v $
//	Revision 1.19  2006/01/02 03:48:09  tames
//	Updated to reflect changes to the HasBasisRequests interface.
//	
//	Revision 1.18  2005/09/13 22:28:58  tames
//	Changes to refect BasisBundleEvent refactoring.
//	
//	Revision 1.17  2005/04/16 04:04:21  tames
//	Changes to reflect refactored state and activity packages.
//	
//	Revision 1.16  2005/03/24 18:16:34  chostetter_cvs
//	Fixed problem with releasing unused portions of BasisSets, changed waiting on DataRequestSatisfier repetitions to reach zero to waiting for stop
//	
//	Revision 1.15  2005/03/22 20:32:19  chostetter_cvs
//	Added ability to wait for all repetitions to complete
//	
//	Revision 1.14  2005/03/04 18:46:25  chostetter_cvs
//	Can now choose among three request satisfaction rules: all, any, and first
//	
//	Revision 1.13  2004/11/10 19:31:28  tames
//	Added a getDownsamplingRate method
//	
//	Revision 1.12  2004/07/24 02:46:11  chostetter_cvs
//	Added statistics calculations to DataBuffers, renamed some classes
//	
//	Revision 1.11  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.10  2004/07/16 15:18:31  chostetter_cvs
//	Revised, refactored Component activity state
//	
//	Revision 1.9  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.8  2004/07/06 21:57:12  chostetter_cvs
//	More BasisRequester, DataRequester work
//	
//	Revision 1.7  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.6  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.5  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.4  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.3  2004/06/15 23:27:20  chostetter_cvs
//	Removed pause ActivityState
//	
//	Revision 1.2  2004/06/14 21:23:50  chostetter_cvs
//	More BasisRequest work
//	
//	Revision 1.1  2004/06/11 17:27:56  chostetter_cvs
//	Further Input-related work
//	
