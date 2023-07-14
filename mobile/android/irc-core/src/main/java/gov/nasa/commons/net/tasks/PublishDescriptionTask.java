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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.peergroup.PeerGroup;

import gov.nasa.gsfc.commons.net.PeerModel;
import gov.nasa.gsfc.commons.net.services.DescriptionPublisher;

/**
 * PublishDescriptionTask is a Runnable task that attempts to publish a description
 * with a {@link gov.nasa.gsfc.commons.net.services.DescriptionPublisher} service.
 * A DescriptionPublisher must already exist for this peer in the group.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/01/09 05:40:21 $
 * @author	Troy Ames
**/
public class PublishDescriptionTask implements Runnable
{
	private static final String CLASS_NAME = 
		PublishDescriptionTask.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	// Current state of peer and context that this task will run in.
	private PeerModel fPeerModel = null;

	private String fGroupName = null;
	private String fDescName = null;
	private URL fDescLocation = null;

	/**
	 * Task constructor that creates a PublishDescriptionTask that
	 * will publish the specified description to other peers. A
	 * DescriptionPublisher must already exist for the peer in the specified
	 * group.
	 * The availability of a description will be advertised to other peers by
	 * its name. If groupName is null the description will be published in the
	 * root group.
	 *
	 * @param   groupName   name of the group to publish the description in
	 * @param   descName	name of the description to publish
	 * @param   location	URL location of the description
	 * @param   peerModel   current state of this peer in the environment.
	 *
	 * @throws  IllegalArgumentException if descName, location, or peerModel
	 *		  is null.
	 *
	 * @see	 gov.nasa.gsfc.commons.net.PeerModel
	 * @see	 gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public PublishDescriptionTask(
		String groupName, String descName, URL location, PeerModel peerModel)
	{
		fPeerModel = peerModel;
		fGroupName = groupName;
		fDescName = descName;
		fDescLocation = location;

		// Check validity of arguments
		if (fPeerModel == null)
		{
			throw new IllegalArgumentException(
				"Peer model argument cannot be null");
		}
		if (fDescName == null)
		{
			throw new IllegalArgumentException(
				"Description name argument cannot be null");
		}
		if (fDescLocation == null)
		{
			throw new IllegalArgumentException(
				"Description location argument cannot be null");
		}
	}

	/**
	 * Run this task to publish a description with a DescriptionPublisher service.
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

		// Check if a DescriptionPublisher exist for this group
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

		descPub.addDescription(fDescName, fDescLocation);

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Published " + fDescName + " in group " + 
				fGroupName + " with location " + fDescLocation;
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"run", message);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: PublishDescriptionTask.java,v $
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
