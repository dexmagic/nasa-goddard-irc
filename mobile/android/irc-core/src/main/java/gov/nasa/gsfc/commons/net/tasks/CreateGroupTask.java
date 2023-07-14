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

import net.jxta.discovery.DiscoveryService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

import gov.nasa.gsfc.commons.net.PeerModel;

/**
 * CreateGroupTask is a Runnable task that attempts to create a new
 * subgroup in the parent PeerGroup.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class CreateGroupTask implements Runnable
{
	private static final String CLASS_NAME = 
		CreateGroupTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	private PeerGroup fParentPeerGroup = null;
	private String fGroupName = null;
	private String fComment = null;

	/**
	 * Task constructor that creates a new subgroup in the parent PeerGroup.
	 *
	 * @param   name		the name of the new group
	 * @param   parent	  the immediate parent of this group
	 * @param   comment	 a description String to associate with the new group
	 * @param   peerModel   the current state of this peer in the environment.
	 *
	 * @throws IllegalArgumentException if name, parent, or peerModel is null.
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	 */
	public CreateGroupTask(
		String name, PeerGroup parent, String comment, PeerModel peerModel)
	{
		fPeerModel = peerModel;
		fGroupName = name;
		fParentPeerGroup = parent;
		fComment = comment;

		// Check validity of arguments
		if (fPeerModel == null)
		{
			throw new IllegalArgumentException(
				"Peer model argument cannot be null");
		}
		if (fGroupName == null)
		{
			throw new IllegalArgumentException(
				"Group name argument cannot be null");
		}
		if (fParentPeerGroup == null)
		{
			throw new IllegalArgumentException(
				"Parent peer group argument cannot be null");
		}
	}

	/**
	 * Run this task to create a new subgroup in the parent PeerGroup.
	**/
	public void run()
	{
		createGroup(fGroupName, fParentPeerGroup, fComment);
	}

	/**
	 * Create a new subgroup in the parent PeerGroup.
	 *
	 * @param  name  the name of the new group
	 * @param  parent the immediate parent of this group
	 * @param  comment a description String to associate with the new group
	 */
	private void createGroup(String name, PeerGroup parent, String comment)
	{
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Creating a new group advertisement"
				+ " for:" + name + " in parent:" + parent.getPeerGroupName();
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"createGroup", message);
		}

		try
		{
			// create a new all purpose peergroup.
			// TBD: bug in jxta, the following call will deadlock if parent is
			// not the root group. The work around is to substitute the root.
			// Perhaps a better solution is to copy the parent advertisement?
			//ModuleImplAdvertisement implAdv =
			//	parent.getAllPurposePeerGroupImplAdvertisement();
			ModuleImplAdvertisement implAdv =
				fPeerModel.getRootPeerGroup().getAllPurposePeerGroupImplAdvertisement();
			PeerGroup newGrp = parent.newGroup(null, implAdv, name, comment);

			// Publish this advertisement to other peers/rendezvous peers
			PeerGroupAdvertisement adv = newGrp.getPeerGroupAdvertisement();
			DiscoveryService discovery = parent.getDiscoveryService();
			discovery.remotePublish(adv, DiscoveryService.GROUP);

			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "New Group = " + adv.getName()
					+ " Group ID= " + newGrp.getPeerGroupID();
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"createGroup", message);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Group creation failed";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"createGroup", message, ex);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: CreateGroupTask.java,v $
//	Revision 1.5  2005/01/09 05:40:21  tames
//	Updated to reflect the refactoring of the TaskManager classes.
//	
//	Revision 1.4  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.3  2004/05/27 16:11:08  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//	Further tweaks for new structure, design
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
