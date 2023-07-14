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

import gov.nasa.gsfc.commons.net.DescriptionListener;
import gov.nasa.gsfc.commons.net.PeerModel;
import gov.nasa.gsfc.commons.net.services.DescriptionPublisher;

/**
 * InitializeDescriptionPublisherTask is a Runnable task that attempts create and
 * initialize a {@link gov.nasa.gsfc.commons.net.services.DescriptionPublisher}. If
 * a DescriptionPublisher already exist for this peer in the group
 * then this task will do nothing and return.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class InitializeDescriptionPublisherTask implements Runnable
{
	private static final String CLASS_NAME = 
		InitializeDescriptionPublisherTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	private String fGroupName = null;
	private DescriptionListener fDescriptionListener = null;

	/**
	 * Task constructor that creates a InitializeDescriptionPublisherTask that
	 * will create and initialize a DescriptionPublisher service in the
	 * specified group if the service does not exist.
	 *
	 * @param   groupName	  name of the group to create the service in
	 * @param   descListener   the listener to register with the new service
	 * @param   peerModel	  current state of this peer in the environment.
	 *
	 * @throws  IllegalArgumentException if peerModel is null
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	 * @see	 gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public InitializeDescriptionPublisherTask(
		String groupName, DescriptionListener descListener, PeerModel peerModel)
	{
		fPeerModel = peerModel;
		fGroupName = groupName;
		fDescriptionListener = descListener;

		// Check validity of arguments
		if (fPeerModel == null)
		{
			throw new IllegalArgumentException("Peer model cannot be null");
		}
	}

	/**
	 * Run this task to initialize a DescriptionPublisher service.
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
			// Try to join the group
			new JoinGroupTask(fGroupName, fPeerModel).run();
		}

		PeerGroup grp = fPeerModel.getJoinedGroup(fGroupName);

		// Check if we are currently member of the group
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

		// Check if a DescriptionPublisher exist for this group
		if (!fPeerModel.isDescriptionServiceStarted(fGroupName))
		{
			// create and start a DescriptionPublisher for the group
			DescriptionPublisher service = new DescriptionPublisher();
			service.setPeerDiscoveryLimit(fPeerModel.getPeerDiscoveryLimit());
			service.init(grp, null, null);
			service.addDescriptionListener(fDescriptionListener);
			service.startApp(null);

			fPeerModel.putDescriptionService(fGroupName, service);

			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Started a DescriptionPublisher in group "
					+ fGroupName;
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"run", message);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: InitializeDescriptionPublisherTask.java,v $
//	Revision 1.5  2005/01/09 05:40:21  tames
//	Updated to reflect the refactoring of the TaskManager classes.
//	
//
