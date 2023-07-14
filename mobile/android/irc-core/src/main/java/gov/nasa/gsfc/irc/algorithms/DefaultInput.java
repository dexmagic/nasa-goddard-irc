//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/algorithms/DefaultInput.java,v 1.35 2006/05/23 15:55:47 smaher_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.algorithms;

import java.util.Collection;

import gov.nasa.gsfc.irc.components.AbstractManagedComponent;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.DataRequestSatisfactionRuleType;
import gov.nasa.gsfc.irc.data.DataRequester;
import gov.nasa.gsfc.irc.data.DefaultDataRequester;
import gov.nasa.gsfc.irc.data.events.DataListener;


/**
 *  An Input is a DataRequester Component.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/05/23 15:55:47 $
 *  @author	Carl F. Hostetter
**/
public class DefaultInput extends AbstractManagedComponent implements Input
{
	private DataRequester fDataRequester;	
	
	
	/**
	 *	Constructs a new Input having a default name. Note that the new Input will 
	 *  need to have its ComponentManager (typically an Algorithm) set (if any is 
	 *  desired).
	 *
	 */
	
	public DefaultInput()
	{
		super(Input.DEFAULT_NAME);
		
		fDataRequester = new DefaultDataRequester(this);
	}
	
	
	/**
	 *  Constructs a new Input having the given base name. Note that the new Input 
	 *  will need to have its ComponentManager (typically an Algorithm) set (if any 
	 *  is desired).
	 * 
	 *  @param name The base name of the new Input
	 **/

	public DefaultInput(String name)
	{
		super(name);
		
		fDataRequester = new DefaultDataRequester(this);
	}
	
	
	/**
	 *	Constructs a new Input configured according to the given 
	 *  ComponentDescriptor. Note that the new Input will need to have its 
	 *  ComponentManager (typically an Algorithm) set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new Input
	 */
	
	public DefaultInput(ComponentDescriptor descriptor)
	{
		super(descriptor);
		
		fDataRequester = new DefaultDataRequester(this);
	}
		
//----------------------------------------------------------------------
//	Object-related methods
//----------------------------------------------------------------------
	
	/**
	 * Returns a String representation of this Input.
	 * 
	 * @return A String representation of this Input
	 */
	public String toString()
	{
		String stringRep = super.toString() + "\n" + fDataRequester;
		
		return (stringRep);
	}

//----------------------------------------------------------------------
//	DataRequester-related methods
//----------------------------------------------------------------------
	
	/**
	 * Returns an array of BasisRequests currently managed by this Input.
	 * 
	 * @return The array of BasisRequests currently managed by this Input
	 */
	public BasisRequest[] getBasisRequests()
	{
		return (fDataRequester.getBasisRequests());
	}
	
	/**
	 * Sets the array of BasisRequests currently managed by this Input.
	 * 
	 * @param requests the array of BasisRequests
	 */
	public void setBasisRequests(BasisRequest[] requests)
	{
		removeBasisRequests();
		
		for (int i = 0; i < requests.length; i++)
		{
			addBasisRequest(requests[i]);
		}
	}
		
	/**
	 * Returns the Set of BasisRequests currently managed by this Input that are
	 * made on the BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle on which the
	 *            BasisRequests to get are made
	 * @return The Set of BasisRequests currently managed by this Input that are
	 *         made on the BasisBundle specified by the given BasisBundleId
	 */	 
	public Collection getBasisRequests(BasisBundleId basisBundleId)
	{
		return (fDataRequester.getBasisRequests(basisBundleId));
	}	
	
	/**
	 * Adds the given BasisRequest to the Set of BasisRequests managed by this
	 * Input.
	 * 
	 * @param basisRequest A BasisRequest
	 */	 
	public void addBasisRequest(BasisRequest basisRequest)
	{
		try
		{
			fDataRequester.addBasisRequest(basisRequest);
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}	
	
	/**
	 * Removes all BasisRequests from this Input.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which all
	 *            BasisRequests are to be removed
	 */ 
	public void removeBasisRequests()
	{
		fDataRequester.removeBasisRequests();
	}
		
	/**
	 * Removes all BasisRequests from this Input that are made on the
	 * BasisBundle specified by the given BasisBundleId.
	 * 
	 * @param basisBundleId The BasisBundleId of the BasisBundle from which all
	 *            BasisRequests are to be removed
	 */	 
	public void removeBasisRequests(BasisBundleId basisBundleId)
	{
		fDataRequester.removeBasisRequests(basisBundleId);
	}	
	
	/**
	 * Removes the given BasisRequest from the Set of BasisRequests managed by
	 * this Input.
	 * 
	 * @param basisRequest The BasisRequest to remove
	 */	 
	public void removeBasisRequest(BasisRequest basisRequest)
	{
		fDataRequester.removeBasisRequest(basisRequest);
	}
	
	/**
	 * Sets the DataRequestSatisfactionRuleType indicator according to which
	 * this Input is to satisfy its current Set of BasisRequests to the given
	 * DataRequestSatisfactionRuleType.
	 * 
	 * @param rule The DataRequestSatisfactionRuleType indicator according to
	 *            which this Input is to satisfy its current Set of
	 *            BasisRequests
	 */
	public void setDataRequestSatisfactionRule(DataRequestSatisfactionRuleType rule)
	{
		fDataRequester.setDataRequestSatisfactionRule(rule);
	}	
	
	/**
	 * Returns the DataRequestSatisfactionRuleType indicator according to which
	 * this Input is currently configured to satisfy its current Set of
	 * BasisRequests.
	 * 
	 * @return The DataRequestSatisfactionRuleType indicator according to which
	 *         this Input is currently configured to satisfy its current Set of
	 *         BasisRequests
	 */	 
	public DataRequestSatisfactionRuleType getDataRequestSatisfactionRule()
	{
		return (fDataRequester.getDataRequestSatisfactionRule());
	}
		
	/**
	 * Sets the repetition of this Input to the given value. If this Input is
	 * not currently continuous, then the repetition defines the number of
	 * requests that the Input will issue when it is started.
	 * 
	 * @param numRepetitions The number of repetitions this DataRequester should
	 *            make when started
	 */
	public void setRepetition(int numRepetitions)
	{
		fDataRequester.setRepetition(numRepetitions);
	}
		
	/**
	 * Returns the number of request repetitions this Input will make when it is
	 * next started. If this Input is currently continuous, then the result will
	 * be 0.
	 * 
	 * @param numRepetitions The number of repetitions this Input will make when
	 *            started
	 */ 
	public int getRepetition()
	{
		return (fDataRequester.getRepetition());
	}	
	
	/**
	 * After this Input has been started, this will return the current number of
	 * unsatisfied request repetitions remaining for this Input to satisfy. If
	 * this Input has not yet been started, the result is the same as the
	 * current repetition value. In either case, if this Input is currently
	 * continuous, then the result will always be 0.
	 * 
	 * @return The current number of unsatisfied request repetitions remaining
	 *         for this Input to satisfy
	 */	 
	public int getRemainingRepetitions()
	{
	   return (fDataRequester.getRemainingRepetitions());
	}
	
	/**
	 * Blocks on the calling Thread until this Input has stopped.
	 * 
	 * @throws InterruptedException if the wait is interrupted by another Thread
	 */ 
	public void waitUntilStopped()
		throws InterruptedException
	{
		fDataRequester.waitUntilStopped();
	}
	
	/**
	 * Causes this Input to repeat its current Set of BasisRequests
	 * indefinitely, once it is started and while not paused. To put this Input
	 * back out of continuous mode, use <code>setRepeitition</code> to set a
	 * finite number of repetitions.
	 */	 
	public void setContinuous()
	{
		fDataRequester.setContinuous();
	}
		
	/**
	 * Returns true if this Input is currently in continuous mode, false
	 * otherwise.
	 * 
	 * @return True if this Input is currently in continuous mode, false
	 *         otherwise
	 */	 
	public boolean isContinuous()
	{
		return (fDataRequester.isContinuous());
	}
	
	/**
	 * Sets the downsampling rate applied to all BasisSets received by this Input 
	 * to the given rate. To indicate no downsampling, set this rate to 0.
	 * 
	 * @param downsamplingRate The downsampling rate of all BasisSets received by 
	 * 		this Input
	 */
	public void setDownsamplingRate(int downsamplingRate)
	{
		fDataRequester.setDownsamplingRate(downsamplingRate);
	}
	
	/**
	 * Gets the downsampling rate applied to all BasisSets received by this 
	 * Input. 
	 * 
	 * @return The downsampling rate of this BasisSet
	 */
	public int getDownsamplingRate()
	{
		return fDataRequester.getDownsamplingRate();
	}

	/**
	 * Causes this Input to start issuing and satisfying its Set of
	 * BasisRequests.
	 */
	public void start()
	{
		try
		{
			super.start();
			
			fDataRequester.start();
		}
		catch (Exception ex)
		{
			declareException(ex);
		}				
	}
	
	/**
	 * Causes this DataRequester to stop issuing and satisfying its Set of
	 * BasisRequests. The DataRequester will complete any request it is in the
	 * midst of, then gracefully cease activity. If subsequently restarted, the
	 * DataRequester will start issuing and satisfying requests again, according
	 * to the state of its BasisRequests and repetitions when it is restarted,
	 * as though it were the first start since its constructiion.
	 */
	public void stop()
	{
		if (fDataRequester != null)
		{
			fDataRequester.stop();
		}
		
		super.stop();
	}
	
	/**
	 * Causes this Input to immediately cease operation and release any
	 * allocated resources. A killed Input cannot subsequently be restarted or
	 * otherwise reused.
	 */
	public void kill()
	{
		if (fDataRequester != null)
		{
			fDataRequester.kill();
		}
		fDataRequester = null;
		
		super.kill();
	}
	
	/**
	 * Adds the given DataListener as a listener for Data Events from 
	 * this Input.
	 *
	 * @param listener An InputListener
	 **/
	public void addDataListener(DataListener listener)
	{
		fDataRequester.addDataListener(listener);
	}
	
	/**
	 * Removes the given DataListener as a listener for Data Events 
	 * from this Input.
	 *
	 * @param listener A DataListener
	 **/
	public void removeDataListener(DataListener listener)
	{
		fDataRequester.removeDataListener(listener);
	}
	
	/**
	 * Adds the given InputListener as a listener for Data Events from this 
	 * Input.
	 *
	 * @param listener An InputListener
	 **/	
	public void addInputListener(InputListener listener)
	{
		this.addDataListener(listener);
	}
	
	/**
	 * Removes the given InputListener as a listener for Data Events from 
	 * this Input.
	 *
	 * @param listener An InputListener
	 **/
	public void removeInputListener(InputListener listener)
	{
		this.removeDataListener(listener);
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultInput.java,v $
//	Revision 1.35  2006/05/23 15:55:47  smaher_cvs
//	Added check for null data requester to avoid NullException if kill() called twice.
//	
//	Revision 1.34  2006/03/14 14:57:15  chostetter_cvs
//	Simplified Namespace, Manager, Component-related constructors
//	
//	Revision 1.33  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.32  2006/01/24 16:19:16  chostetter_cvs
//	Changed default ComponentManager behavior, default is now none
//	
//	Revision 1.31  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.30  2006/01/02 03:44:08  tames
//	Updated to reflect changes to the HasBasisRequests interface.
//	
//	Revision 1.29  2005/09/13 22:27:47  tames
//	Changes to reflect DataRequester refactoring
//	
//	Revision 1.28  2005/08/26 22:07:49  tames_cvs
//	Moved setting of DataRequester to constructor. This will eventually use a
//	factory.
//	
//	Revision 1.27  2005/07/12 17:06:28  tames
//	Comment change only.
//	
//	Revision 1.26  2005/03/24 18:16:34  chostetter_cvs
//	Fixed problem with releasing unused portions of BasisSets, changed waiting on DataRequestSatisfier repetitions to reach zero to waiting for stop
//	
//	Revision 1.25  2005/03/22 20:32:19  chostetter_cvs
//	Added ability to wait for all repetitions to complete
//	
//	Revision 1.24  2005/03/04 18:46:25  chostetter_cvs
//	Can now choose among three request satisfaction rules: all, any, and first
//	
//	Revision 1.23  2005/01/27 21:38:02  chostetter_cvs
//	Implemented new exception state and default exception behavior for Objects having ActivityState
//	
//	Revision 1.22  2004/11/10 19:31:14  tames
//	Added a getDownsamplingRate method
//	
//	Revision 1.21  2004/07/24 02:46:11  chostetter_cvs
//	Added statistics calculations to DataBuffers, renamed some classes
//	
//	Revision 1.20  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.19  2004/07/16 15:18:31  chostetter_cvs
//	Revised, refactored Component activity state
//	
//	Revision 1.18  2004/07/14 22:24:53  chostetter_cvs
//	More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//	
//	Revision 1.17  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.16  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.15  2004/07/09 22:29:11  chostetter_cvs
//	Extensive testing of Input/Input interaction, supports simple BasisRequests
//	
//	Revision 1.14  2004/07/06 21:57:12  chostetter_cvs
//	More BasisRequester, DataRequester work
//	
//	Revision 1.13  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.12  2004/07/02 02:33:30  chostetter_cvs
//	Renamed DataRequest to BasisRequest
//	
//	Revision 1.11  2004/06/29 22:46:13  chostetter_cvs
//	Fixed broken CVS-generated comments. Grrr.
//	
//	Revision 1.10  2004/06/29 22:39:39  chostetter_cvs
//	Successful testing of data flow from an Output to an Input, 
//  with simplest form of BasisRequest (requesting all data). 
//  Also tweaked Composites.
//	
//	Revision 1.9  2004/06/15 23:26:19  chostetter_cvs
//	More ActivityState-related tweaks
//	
//	Revision 1.8  2004/06/15 23:23:08  chostetter_cvs
//	Removed pause ActivityState
//	
//	Revision 1.7  2004/06/15 22:21:12  chostetter_cvs
//	More DataSetRequester work
//	
//	Revision 1.6  2004/06/14 21:23:50  chostetter_cvs
//	More BasisRequest work
//	
//	Revision 1.5  2004/06/11 17:27:56  chostetter_cvs
//	Further Input-related work
//	
//	Revision 1.4  2004/06/08 14:21:53  chostetter_cvs
//	Added child/parent relationship to Components
//	
//	Revision 1.3  2004/06/04 05:34:42  chostetter_cvs
//	Further data, Algorithm, and Component work
//	
//	Revision 1.2  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.2  2004/05/27 23:09:26  chostetter_cvs
//	More Namespace related changes
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//	
