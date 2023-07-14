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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;

import gov.nasa.gsfc.commons.net.PeerModel;

/**
 * StopPeerNetworkTask is a Runnable task that attempts to stop the
 * peer-to-peer network.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class StopPeerNetworkTask implements Runnable
{
	private static final String CLASS_NAME = 
		StopPeerNetworkTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	/**
	 * Task constructor that creates a StopPeerNetworkTask that
	 * will stop the peer-to-peer network.
	 *
	 * @param   peerModel   current state of this peer in the environment.
	 *
	 * @throws  IllegalArgumentException if peerModel is null.
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	**/
	public StopPeerNetworkTask(PeerModel peerModel)
	{
		fPeerModel = peerModel;

		// Check validity of arguments
		if (fPeerModel == null)
		{
			throw new IllegalArgumentException(
				"Peer model argument cannot be null");
		}
	}

	/**
	 * Run this task to stop the peer network.
	**/
	public void run()
	{
		stop();
	}

	/**
	 * Stop the Jxta framework and services.
	 */
	private void stop()
	{
		// Check if Jxta has already been stopped
		if (fPeerModel.getRootPeerGroup() == null)
		{
			// Don't stop again
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Attempted to stop Jxta more than once";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"stop", message);
			}
			
			return;
		}

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Stopping JXTA...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"stop", message);
		}

		Object[] joinedGrps = fPeerModel.getJoinedGroups().toArray();
		for (int i = 0, n = joinedGrps.length; i < n; i++)
		{
			String groupName = (String) joinedGrps[i];

			// Stop DescriptionPublisher service
			if (fPeerModel.isDescriptionServiceStarted(groupName))
			{
				fPeerModel.getDescriptionService(groupName).stopApp();
				fPeerModel.removeDescriptionService(groupName);
			}

			// Leave joined group
			PeerGroup group = fPeerModel.getJoinedGroup(groupName);
			if (group != null)
			{
				leaveGroup(group);
				fPeerModel.removeJoinedGroup(groupName);
			}
		}

		// Stop root peer group
		PeerGroup rootPeerGrp = fPeerModel.getRootPeerGroup();
		rootPeerGrp.stopApp();
		fPeerModel.setRootPeerGroup(null);
	}

	/**
	 * Leave the specified group.
	 */
	private void leaveGroup(PeerGroup group)
	{
		try
		{
			// Resign our identity.
			MembershipService membership = group.getMembershipService();
			membership.resign();
		}
		catch(PeerGroupException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not resign from group " + group;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"leaveGroup", message, ex);
			}
		}

		// Flush advertisements
		try
		{
			DiscoveryService discovery = group.getDiscoveryService();
			discovery.flushAdvertisements(
					group.getPeerID().toString(), DiscoveryService.PEER);
		}
		catch (IOException ex)
		{
			// Don't do anything with the exception since we are stopping.
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Encountered problem while leaving group";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"leaveGroup", message, ex);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: StopPeerNetworkTask.java,v $
//	Revision 1.5  2005/01/09 05:40:21  tames
//	Updated to reflect the refactoring of the TaskManager classes.
//	
//
