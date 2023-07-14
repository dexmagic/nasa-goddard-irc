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
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;

import gov.nasa.gsfc.commons.net.PeerModel;

/**
 * StartPeerNetworkTask is a Runnable task that attempts to start the
 * peer-to-peer network built on top of <a href="http://www.jxta.org">JXTA</a>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class StartPeerNetworkTask implements Runnable
{
	private static final String CLASS_NAME = 
		StartPeerNetworkTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	/**
	 * Task constructor that creates a StartPeerNetworkTask that
	 * will start the peer-to-peer network.
	 *
	 * @param   peerModel   current state of this peer in the environment.
	 *
	 * @throws  IllegalArgumentException if peerModel is null.
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	**/
	public StartPeerNetworkTask(PeerModel peerModel)
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
	 * Run this task to start the peer network.
	**/
	public void run()
	{
		start();
	}

	/**
	 * Start the Jxta framework and services.
	 */
	private void start()
	{
		// Check if Jxta has already been started
		if (fPeerModel.getRootPeerGroup() != null)
		{
			// Don't start again
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Attempted to start Jxta more than once";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"start", message);
			}

			return;
		}

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Starting JXTA...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"start", message);
		}

		try
		{
			// Create, and Start the default jxta NetPeerGroup
			PeerGroup netPeerGroup = PeerGroupFactory.newNetPeerGroup();
			fPeerModel.setPeerName(netPeerGroup.getPeerName());

			// Store group into joined group map for later use
			fPeerModel.putJoinedGroup(
				netPeerGroup.getPeerGroupName(), netPeerGroup);
			fPeerModel.setRootPeerGroup(netPeerGroup);

			DiscoveryService discovery = netPeerGroup.getDiscoveryService();
			try
			{
				// Remove any old cached advertisements from previous runs
				discovery.flushAdvertisements(null, DiscoveryService.ADV);
				discovery.flushAdvertisements(null, DiscoveryService.GROUP);
				discovery.flushAdvertisements(null, DiscoveryService.PEER);
			}
			catch (IOException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Could not flush local cache";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"start", message, ex);
				}
			}

			// Update local cache of advertisements
			discovery.getRemoteAdvertisements(
					null, DiscoveryService.PEER, null, null,
					fPeerModel.getPeerDiscoveryLimit(), null);
			discovery.getRemoteAdvertisements(
					null, DiscoveryService.GROUP, null, null,
					fPeerModel.getPeerDiscoveryLimit(), null);
			discovery.getRemoteAdvertisements(
					null, DiscoveryService.ADV, null, null,
					fPeerModel.getPeerDiscoveryLimit(), null);
		}
		catch (PeerGroupException ex)
		{
			// could not instantiate the group
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Group creation failure, could not start Jxta";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"start", message, ex);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: StartPeerNetworkTask.java,v $
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
