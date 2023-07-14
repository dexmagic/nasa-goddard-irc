//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/net/services/GenericService.java,v 1.2 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: 
//	 1	IRC	   1.0		 12/11/2001 5:33:09 PMTroy Ames	   
//	$
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

package gov.nasa.gsfc.commons.net.services;

import net.jxta.document.Advertisement;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.service.Service;

/**
 * The GenericService class is the minimum implementation for a Jxta service.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2004/07/12 14:26:24 $
 * @author	Troy Ames
**/
public abstract class GenericService extends Object implements Service
{

	/**
	 * The advertisement passed by init(). By
	 * default, this is handed out again if anyone asks for our adv.
	 **/
	protected ModuleImplAdvertisement fImplAdv;

	/**
	 * The PeerGroup pointer passed by init().
	 **/
	protected PeerGroup fPeerGroup;

	/**
	 * The PeerGroup Advertisement.
	 **/
	protected PeerGroupAdvertisement fPeerGroupAdv;

	/**
	 * The assigned Id passed by init().
	 **/
	protected ID fAssignedId;

	/**
	 * The PeerGroup Id.
	 **/
	protected PeerGroupID fGroupId;

	/**
	 * Initialize the service. By default
	 * just store the parameters.
	 *
	 * @param group PeerGroup this service is started from
	 * @param assigneId The unqiue ID of this service in this group.
	 * @param implAdv   The Advertisement for this service from the
	 *				  PeerGroupAdvertisement.
	 **/
	public void init(PeerGroup group, ID assignedId, Advertisement implAdv)
	{
		fPeerGroup = group;
		fAssignedId = assignedId;
		fPeerGroupAdv = (PeerGroupAdvertisement)group.getPeerGroupAdvertisement();
		fGroupId = group.getPeerGroupID();
		fImplAdv = (ModuleImplAdvertisement)implAdv;
	}

	/**
	 * Supply arguments and starts this service if it hadn't started by itself.
	 *
	 * @param args A array of string arguments.
	 * @return int status indication.
	 */
	public int startApp(String args[])
	{
		return 0;
	}

	/**
	 * Ask this service to stop.
	 *
	 * This request is currently ignored.
	 */
	public void stopApp()
	{
	}

	/**
	 * Returns the interface for this service. Some services return proxy
	 * versions of themselves to impose security upon the service object or to
	 * count references to the service. The default implementation returns
	 * <code>this</code>.
	 *
	 * @return the interface version of this service.
	 */
	public Service getInterface()
	{
		return this;
	}

	/**
	 * Returns the ModuleImplAdvertisement associated with this Service.
	 *
	 * @return the ModuleImplAdvertisement associated with this Service.
	 */
	public Advertisement getImplAdvertisement()
	{
		return fImplAdv;
	}
}
