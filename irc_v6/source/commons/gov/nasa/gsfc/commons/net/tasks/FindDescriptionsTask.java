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

package gov.nasa.gsfc.commons.net.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.peergroup.PeerGroup;

import gov.nasa.gsfc.commons.net.PeerModel;
import gov.nasa.gsfc.commons.net.services.DescriptionPublisher;

/**
 * FindDescriptionsTask is a Runnable task that attempts to locate and request
 * descriptions.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class FindDescriptionsTask implements Runnable
{
	private static final String CLASS_NAME = 
		FindDescriptionsTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private static final int DEFAULT_RESPONSE_LIMIT = 1;

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	private String fGroupName = null;
	private String fDescName = null;
	private String fSourcePeerName = null;
	private long fTimeout = 0;
	private int fResponseLimit = 0;

	/**
	 * Task constructor that creates a FindDescriptionsTask task that attempts to
	 * to find only one matching description. This peer must be a member of the
	 * specified group. If <code>groupName</code> is null then the search for
	 * the description is made in the root peer group. A
	 * {@link gov.nasa.gsfc.commons.net.services.DescriptionPublisher} must also
	 * be started and initialized for the specified group. This constructor is
	 * has the same result as calling the
	 * {@link #FindDescriptions(String, String, String, long, int, PeerModel)}
	 * constructor with the response limit parameter equal to 1.
	 *
	 * @param   groupName	  name of the group to search for the description
	 * @param   sourcePeerName publishing peer of the description
	 * @param   descName	   name of the description to search for
	 * @param   timeout		number of milliseconds to search for matches.
	 * @param   peerModel	  current state of this peer in the environment.
	 *
	 * @throws IllegalArgumentException if peerModel is null
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	 * @see	 gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	 */
	public FindDescriptionsTask(
		String groupName, String sourcePeerName, String descName,
		long timeout, PeerModel peerModel)
	{
		this(groupName, sourcePeerName, descName, timeout,
			DEFAULT_RESPONSE_LIMIT, peerModel);
	}

	/**
	 * Task constructor that creates a FindDescriptionsTask that attempts to
	 * to find many matching descriptions. This peer must be a member of the
	 * specified group. If <code>groupName</code> is null then the search for
	 * the description is made in the root peer group. A
	 * {@link gov.nasa.gsfc.commons.net.services.DescriptionPublisher} must also
	 * be started and initialized for the specified group.
	 * <p>
	 * Running this task will result in an asynchronous search for the specified
	 * period of time to find the peer and send the request. If peerName is null
	 * then this method will request the specified description published by
	 * any peer in the group. If descName is null then it will request the
	 * default description advertised by the specified peer. If both
	 * peerName and descName are null then this method will request the
	 * default description from all peers in the group. In all cases the
	 * number of requests sent and therefore maximum number of responses
	 * received is limited by the responseLimit parameter.
	 * </p>
	 *
	 * @param   groupName	  name of the group to search for the description
	 * @param   sourcePeerName publishing peer of the description
	 * @param   descName	   name of the description to search for
	 * @param   timeout		number of milliseconds to search for matches.
	 * @param   responseLimit  maximum number of responses.
	 * @param   peerModel	  current state of this peer in the environment.
	 *
	 * @throws IllegalArgumentException if peerModel is null or
	 *		 responseLimit &lt; 1.
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	 * @see	 gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public FindDescriptionsTask(
		String groupName, String sourcePeerName, String descName, long timeout,
		int responseLimit, PeerModel peerModel)
	{
		fPeerModel = peerModel;
		fGroupName = groupName;
		fDescName = descName;
		fTimeout = timeout;
		fResponseLimit = responseLimit;
		fSourcePeerName = sourcePeerName;

		// Check validity of arguments
		if (fPeerModel == null)
		{
			throw new IllegalArgumentException("Peer model cannot be null");
		}
		if (fResponseLimit < 1)
		{
			throw new IllegalArgumentException(
				"Response limit argument cannot be less than 1. Passed value = "
				+ fResponseLimit);
		}
	}

	/**
	 * Run this task to find a description.
	**/
	public void run()
	{
		if (fGroupName == null)
		{
			// Use default root group
			fGroupName = fPeerModel.getRootPeerGroup().getPeerGroupName();
		}

		if (!fPeerModel.isJoinedGroup(fGroupName))
		{
			// We must be a member of the group
			new JoinGroupTask(fGroupName, fPeerModel).run();
		}

		PeerGroup grp = fPeerModel.getJoinedGroup(fGroupName);

		// Check if we are a member of the group
		if (grp == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Not a member of the group " + fGroupName;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"run", message);
			}

			return;
		}

		DescriptionPublisher descPub =
			fPeerModel.getDescriptionService(fGroupName);

		// Check if a DescriptionPublisher exists for this group
		if (descPub == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "No DescriptionPublisher has been initialized in "
					+ fGroupName;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"run", message);
			}

			return;
		}

		descPub.findDescriptions(
			fSourcePeerName, fDescName, fTimeout, fResponseLimit);

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Requested " + fDescName + " in group " + 
				fGroupName + " published by " + fSourcePeerName;
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"run", message);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: FindDescriptionsTask.java,v $
//	Revision 1.5  2005/01/09 05:40:21  tames
//	Updated to reflect the refactoring of the TaskManager classes.
//	
//	Revision 1.4  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.3  2004/05/27 18:21:45  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//	Further tweaks for new structure, design
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
