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
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

import gov.nasa.gsfc.commons.net.NetUtil;
import gov.nasa.gsfc.commons.net.PeerModel;

/**
 * JoinGroupTask is a Runnable task that attempts to join a group. If the group
 * cannot be found then this task will create it.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class JoinGroupTask implements Runnable
{
	private static final String CLASS_NAME = 
		JoinGroupTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private static String NAME_ATTRIBUTE = "Name";

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	private String fGroupName = null;

	/**
	 * Task constructor that creates a JoinGroupTask that
	 * will attempt to join an existing group. If the group cannot be found then
	 * it will be created and then joined.
	 * <p>
	 * Parameter group <code>name</code> must be a fully qualified name.
	 * An example of a fully qualified name is <code>A.B.C</code> if
	 * "." is the group delimiter {@link PeerModel.GROUP_DELIM}. In this example
	 * group A is the parent of B. B is a subgroup of A and the parent of
	 * group C. The default root peer group is not specified so A in the example
	 * is a subgroup of the root.
	 * </p>
	 *
	 * @param   name	  name of the group to join
	 * @param   peerModel current state of this peer in the environment.
	 *
	 * @throws  IllegalArgumentException if name or peerModel is null.
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	**/
	public JoinGroupTask(String name, PeerModel peerModel)
	{
		fPeerModel = peerModel;
		fGroupName = name;

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
	}

	/**
	 * Run this task to join a group.
	**/
	public void run()
	{
		joinGroup(fGroupName);
	}

	/**
	 * Join the group with the given fully qualified group name.
	 * This method will create the group if it does not exist.
	 *
	 * @param  qualifiedName  the fully qualified name of the group
	 *
	 * @return a PeerGroup, null if PeerGroup cannot be joined
	 *
	 * @see #GROUP_DELIM
	 */
	private PeerGroup joinGroup(String qualifiedName)
	{
		PeerGroup parent = fPeerModel.getRootPeerGroup();
		String subgroupName = qualifiedName;
		PeerGroup joinedGrp = fPeerModel.getJoinedGroup(qualifiedName);

		// Check if we have already joined this group
		if (joinedGrp == null)
		{
			int delimIndex = qualifiedName.lastIndexOf(PeerModel.GROUP_DELIM);
			if (delimIndex > 0)
			{
				// qualifiedName represents a group hierarchy
				String parentName = qualifiedName.substring(0,delimIndex);
				subgroupName = qualifiedName.substring(delimIndex + 1);

				// Must be a member of the parent to join a subgroup
				parent = joinGroup(parentName);

				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "parentHierarchy: " + parentName
					+ " subgroupName: " + subgroupName;
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"joinGroup", message);
				}
			}

			joinedGrp = joinGroup(subgroupName, parent);

			if (joinedGrp != null)
			{
				// Remember that we have joined
				fPeerModel.putJoinedGroup(subgroupName, joinedGrp);
			}
		}

		return joinedGrp;
	}

	/**
	 * This method will join the group with the given name as a
	 * subgroup of the parent.
	 *
	 * @param  name   the name of the group
	 * @param  parent the PeerGroup to create the new group in
	 *
	 * @return a PeerGroup, null if PeerGroup cannot be joined
	 */
	private PeerGroup joinGroup(String name, PeerGroup parent)
	{
		PeerGroup joinedGrp = null;

		try
		{
			DiscoveryService discovery = parent.getDiscoveryService();

			// Check to see if group was already created by another peer
			// TBD: redesign to use a callback with a timeout?
			for (int i=0; joinedGrp == null && i < 6; i++)
			{
				// Retrieve a list of all group advertisements
				// from our cache that match this group
				Enumeration advEnum =
					discovery.getLocalAdvertisements(
					DiscoveryService.GROUP, NAME_ATTRIBUTE, name);

				if (advEnum.hasMoreElements())
				{
					// Group exists so try to join it
					// Get the discovered group advertisement
					PeerGroupAdvertisement grpAdv =
							(PeerGroupAdvertisement) advEnum.nextElement();

					joinedGrp = joinGroup(parent.newGroup(grpAdv));
				}
				else
				{
					// Group is not known by this peer or does not exists
					// Try to discover group by updating local cache
					discovery.getRemoteAdvertisements(
						null, DiscoveryService.GROUP,
						NAME_ATTRIBUTE, name,
						fPeerModel.getPeerDiscoveryLimit(), null);
					try
					{
						Thread.sleep(5 * 1000);
					}
					catch (Exception ex)
					{
					}
				}
			}

			if (joinedGrp == null)
			{
				// Group does not exists so try to create it
				new CreateGroupTask(name, parent, null, fPeerModel).run();

				// Retrieve the new group from our local cache
				Enumeration advEnum =
					discovery.getLocalAdvertisements(
					DiscoveryService.GROUP, NAME_ATTRIBUTE, name);

				if (advEnum.hasMoreElements())
				{
					// Group exists so try to join it
					// Get the discovered group advertisement
					PeerGroupAdvertisement grpAdv =
							(PeerGroupAdvertisement) advEnum.nextElement();

					joinedGrp = joinGroup(parent.newGroup(grpAdv));
				}
			}

			if (joinedGrp != null)
			{
				// Remember that we have joined
				fPeerModel.putJoinedGroup(name, joinedGrp);
				
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Group:" + name
					+ " successful join in parent: " + parent.getPeerGroupName();
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"joinGroup", message);
				}
			}
		}
		catch(IOException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Problem retrieving advertisements from the local cache";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"joinGroup", message, ex);
			}
		}
		catch(PeerGroupException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Problem joining existing group " + name;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"joinGroup", message, ex);
			}
		}

		return joinedGrp;
	}

	/**
	 * This method will join the existing PeerGroup.
	 *
	 * @param  grp  the PeerGroup to join
	 *
	 * @return the PeerGroup, null if PeerGroup cannot be joined
	 */
	private PeerGroup joinGroup(PeerGroup grp)
	{
		PeerGroup joinedGrp = null;
		StructuredDocument creds = null;

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Joining peer group...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"joinGroup", message);
		}

		try
		{
			// Generate the credentials for the Peer Group
			// using the default authenticator "null"
			AuthenticationCredential authCred =
			new AuthenticationCredential(grp, null, creds);

			// Get the MembershipService from the peer group
			MembershipService membership = grp.getMembershipService();

			// Get the Authenticator from the Authentication creds
			Authenticator auth = membership.apply(authCred);

			// Complete the authentication
			completeAuthentication(auth);

			// Check if everything is okay to join the group
			if (auth.isReadyForJoin())
			{
				Credential myCred = membership.join(auth);
				// TBD: what do I do with the credential it returns?

				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Successfully joined group " +
						grp.getPeerGroupName();
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"joinGroup", message);
				}

				// display the credential as a plain text document.
				NetUtil.docToString(
						myCred.getDocument(new MimeMediaType("text/plain")));

				// Publish the fact that this peer has joined the group
				PeerAdvertisement peerAdv = grp.getPeerAdvertisement();
				DiscoveryService srv = grp.getDiscoveryService();
				srv.publish(peerAdv, DiscoveryService.PEER);
				srv.remotePublish(peerAdv, DiscoveryService.PEER);
				joinedGrp = grp;
			}
			else
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to join group "
						+ "because isReady was false";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"joinGroup", message);
				}
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Failure in authentication";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"joinGroup", message, ex);
			}
		}

		return joinedGrp;
	}

	/**
	 * This method will complete the authentication process for joining a
	 * group using the specified {@link Authenticator}. Authentication is
	 * a required step enabling the peer to join or be denied membership in
	 * a group.
	 *
	 * @param  auth  the Authenticator to use for authentication.
	 */
	private void completeAuthentication(Authenticator auth)
	{
		// TBD: Currently the default authenticator does not need any additional
		// work. Depending on requirements we may want to create our own
		// Authenticator or use a secure one provided by the Jxta
		// project. Currently the set of Authenticators provided by Jxta are
		// being revised so I am deferring this for now.
	}

}

//--- Development History  ---------------------------------------------------
//
//	$Log: JoinGroupTask.java,v $
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
