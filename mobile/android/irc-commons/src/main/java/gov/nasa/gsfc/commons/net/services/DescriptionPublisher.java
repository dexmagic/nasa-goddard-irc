//=== File Prolog ============================================================
//
//	$Header: /cvs/IRC/Dev/source/commons/gov/nasa/gsfc/commons/net/services/DescriptionPublisher.java,v 1.5 2006/01/23 17:59:55 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DescriptionPublisher.java,v $
//	Revision 1.5  2006/01/23 17:59:55  chostetter_cvs
//	Massive Namespace-related changes
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

import gov.nasa.gsfc.commons.net.DescriptionEvent;
import gov.nasa.gsfc.commons.net.DescriptionListener;
import gov.nasa.gsfc.commons.net.NetUtil;

/**
 * The DescriptionPublisher class is a Jxta service that publishes
 * descriptions from the local peer and handles descriptions received from
 * remote peers. This class also handles all request from remote peers as well
 * as requests initiated by the local peer. Descriptions published by this
 * service must be added with the <code>addDescription</code> method. One of
 * the published descriptions should be labeled as the default with the
 * <code>setDefaultDescription</code> method. If a description is not
 * explicitedly set as the default description then the first description added
 * is advertised as the default.
 * <p>
 * Descriptions received from other peers are encapsulated in a
 * DescriptionEvent and passed to all
 * DescriptionListeners registered with this service.
 * </p>
 * <p>
 * The local peer can request a description from a specific remote peer
 * by using the <code>findOneDescription</code> or
 * <code>findDescriptions</code> methods. If the remote peer is not
 * currently visible an attempt is made to dynamically discover the remote
 * peer and retry the request. Repeated retries are attempted until the remote
 * peer is found or an optional timeout is reached.
 * </p>
 * <p>
 * Future enhancements:<br>
 * Allow the option to embed the description in the message instead of sending
 * a URsLog. This would allow a peer to dynamically create or modify a published
 * description and avoid the necessity of having access to a web server.
 * </p>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author	Troy Ames
**/

public class DescriptionPublisher extends GenericService
								  implements PipeMsgListener
{
	private static final String CLASS_NAME = 
		DescriptionPublisher.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	// Service message constants
	/**
	 * Name of this service inserted in all advertisements
	 */
	public static final String SERVICE_NAME =	   "DESCRIPTION_PUBLISHER";
	private static final String DEFAULT_DESCRIPTION_NAME = "DEFAULT_DESC";
	private static final String REQUEST_TYPE =	  "REQUEST";
	private static final String DESCRIPTION_TYPE =  "DESCRIPTION";
	private static final String DESC_NAME_PREFIX =  "[";
	private static final String DESC_NAME_SUFFIX =  "]";

	// Message keys
	private static final String KEY_DESCRIPTION_URL =   "DESCRIPTION_URL";
	private static final String KEY_DESCRIPTION_NAME =  "DESCRIPTION_NAME";
	private static final String KEY_MSG_TYPE =		  "MESSAGE_TYPE";
	private static final String KEY_MSG_SENDER =		"PEER_NAME";
	private static final String KEY_MSG_PIPE =		  "PIPE";

	// Advertisement keys
	// Jxta bug: the Spec advertisement incorrectly filters out custom
	// service elements when caching the advertisement locally. Since "Parm"
	// is a predefined element that is not filtered and we are not otherwise
	// using it, we will use it for holding the list of descriptions. The
	// custom "Descriptions" element is preferred if it worked. Perhaps in the
	// future.
	// private static String KEY_DESCRIPTION_ELEM = "Descriptions";
	private static String KEY_DESCRIPTION_ELEM = "Parm";
	private String KEY_NAME_ATTR = "Name";
	private static final String CLASS_DESCRIPTION =
		"Publisher of XML interface descriptions";

	// Service advertisements
	private ModuleClassAdvertisement fClassAdv =	null;
	private ModuleSpecAdvertisement fSpecAdv =	  null;
	private PipeAdvertisement fPipeAdv =			null;

	// Group Services
	private DiscoveryService fDiscovery =   null;
	private PipeService fPipeSrv =		  null;

	private static String fDefaultDescription = null;
	private boolean fStarted =  false;
	private String fPeerName =  "unknown";
	private int fPeerDiscoveryLimit = 10;
	private Map fDescriptions = new HashMap();
	private InputPipe fPipe =   null;
	private SendRequestHandler fRequestHandler = new SendRequestHandler();
	private Thread fRequestHandlerThread = null;
	private List fDescriptionListeners =
			Collections.synchronizedList(new ArrayList());


	/**
	 * Default constructor for creating service. Service is not initialized
	 * or started.
	 *
	 * @see #init(PeerGroup, ID, Advertisement)
	 * @see #startApp(String)
	 */
	public DescriptionPublisher()
	{
	}

	/**
	 * Initialize the service. If the advertisement parameter is
	 * <code>null</code> then this method will generate the necessary
	 * service advertisements. The service is not started.
	 *
	 * @param group PeerGroup this service is started from
	 * @param assigneId The unqiue ID base of this service in this group.
	 * @param adv   The Advertisement for this service from the
	 *				  PeerGroupAdvertisement.
	 *
	 * @see #setDescriptionMap(Map)
	 * @see #addDescription(String, String)
	 * @see #startApp(String)
	 **/
	public void init(PeerGroup group, ID assignedId, Advertisement adv)
	{
		super.init(group, assignedId, adv);

		// get the name of the peer that initialized the service
		fPeerName = group.getPeerName();

		if (adv == null)
		{
			// Advertisements were not created by caller so create our own.
			ModuleClassID mClassID = IDFactory.newModuleClassID();
			fClassAdv = createClassAdvertisement(mClassID);
			fPipeAdv = createPipeAdvertisement(fGroupId, fPeerName);

			// TBD: Should avoid creating a new ID everytime for this service in
			// the context of the same group and peer. However two peers in the same
			// group cannot use the same ID for this service or their adv will
			// overwrite each other when cached. Could use a combination of the
			// group and peer ids or names to uniquely identify this instance
			// of the service for this peer in this group.
			ModuleSpecID mSpecId = IDFactory.newModuleSpecID(mClassID);
			fSpecAdv = createSpecAdvertisement(
					mSpecId, fPeerName, fDescriptions, fPipeAdv);

			// display the advertisement
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = NetUtil.advToString
					(fSpecAdv, new MimeMediaType("text/plain"));
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"init", message);
			}
		}
		else
		{
			// Get the info from the passed in ImplAdvertisement.
			// TBD: extract the ModuleSpecId from the ImplAdvertisement and
			// discover the Spec advertisement for this service. If it cannot
			// be found then create and publish it.
			// This change is since the 11/21/2001 stable build.
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = 
					"Initializing service from ImplAdvertisement not implemented yet";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"init", message);
			}
		}
	}

	/**
	 * Starts this service and publishes service advertisements if they were
	 * not published by the caller.
	 *
	 * @param args A array of string arguments (currently ignored).
	 * @return int status indication (-1 if an error occurred).
	 */
	public int startApp(String args[])
	{
		int result = 0;

		// Get group services for later use
		fDiscovery = fPeerGroup.getDiscoveryService();
		fPipeSrv = fPeerGroup.getPipeService();

		try
		{
			if (fClassAdv != null)
			{
				// The Module Class advertisement was created, publish
				// it in the local cache and the PeerGroup.
				fDiscovery.publish(fClassAdv, DiscoveryService.ADV);
				fDiscovery.remotePublish(fClassAdv, DiscoveryService.ADV);
			}

			if (fSpecAdv != null)
			{
				// The Module advertisement was created, publish
				// it in the local cache and the PeerGroup.
				fDiscovery.publish(fSpecAdv, DiscoveryService.ADV);
				fDiscovery.remotePublish(fSpecAdv, DiscoveryService.ADV);
			}

			// Create the input pipe endpoint that peers will
			// use to connect to the service
			fPipe = fPipeSrv.createInputPipe(fPipeAdv, this);

			// Create request message sender thread and start it
			fRequestHandlerThread = new Thread(fRequestHandler);
			fRequestHandlerThread.start();

			// Update local advertisements
			getRemoteAdvertisements(null, null, null);

			fStarted = true;
		}
		catch (IOException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Could not start service";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"init", message, ex);
			}

			result = -1;
		}

		return result;
	}

	/**
	 * Stops the service and removes advertisements from local cache.
	 */
	public void stopApp()
	{
		if (fRequestHandlerThread != null)
		{
			fRequestHandlerThread.interrupt();
			fRequestHandlerThread = null;
		}

		try
		{
			if (fSpecAdv != null)
			{
				// Remove my spec advertisement from cache
				fDiscovery.flushAdvertisements(
					fSpecAdv.getID().toString(), DiscoveryService.ADV);
			}

			if (fClassAdv != null)
			{
				// Remove my class advertisement from cache
				fDiscovery.flushAdvertisements(
					fClassAdv.getID().toString(), DiscoveryService.ADV);
			}
		}
		catch (IOException ex)
		{
			// ignore exception since we are stopping anyway
		}

		fStarted = false;
	}

	/**
	 * Set the peer discovery limit for this peer. This specifies the upper limit
	 * of discovery responses per remote peer for advertisement discovery.
	 *
	 * @param  limit discovery limit for this peer
	**/
	public void setPeerDiscoveryLimit(int limit)
	{
		fPeerDiscoveryLimit = limit;
	}

	/**
	 * Sends one request to the specified peer for the published description.
	 * This is an asynchronous call that will try for the specified period of
	 * time to find the peer and send the request. If peerName is null then
	 * this method will request the specified description published by
	 * any peer in the group. If descName is null then it will request the
	 * default description advertised by the specified peer.
	 * <p>
	 * This request will not expire if <code>timeout</code> is negative.
	 * </p>
	 * <p>
	 * Replies to this request will be received via a DescriptionEvent
	 * to all DescriptionListeners.
	 * </p>
	 *
	 * @param  peerName  name of publishing peer
	 * @param  descName  name of description
	 * @param  timeout  the number of milliseconds to keep trying to
	 *				  send the request successfully
	 *
	 * @see DescriptionListener
	 * @see #findDescriptions(String, String, long, int)
	 */
	public void findOneDescription(String peerName, String descName, long timeout)
	{
		// TBD: what should be done if both peerName and descName are null?
		//	  The current implementation will find a default publication from
		//	  a random peer. Who would want to do that.
		if (descName == null)
		{
			descName = DEFAULT_DESCRIPTION_NAME;
		}

		// Give to request handler
		fRequestHandler.add(
			new DescriptionRequest(peerName, descName, timeout, 1));
	}

	/**
	 * Sends one or more requests to get all matching published descriptions.
	 * This is an asynchronous call that will try for the specified period of
	 * time to find the peer and send the request. If peerName is null then
	 * this method will request the specified description published by
	 * any peer in the group. If descName is null then it will request the
	 * default description advertised by the specified peer. If both
	 * peerName and descName are null then this method will request the
	 * default description from all peers in the group. In all cases the
	 * number of requests sent and therefore responses received is limited by
	 * the responseLimit parameter.
	 * <p>
	 * This request will not expire if <code>timeout</code> is negative.
	 * </p>
	 * <p>
	 * Replies to this request will be received via a DescriptionEvent
	 * to all DescriptionListeners.
	 * </p>
	 *
	 *
	 * @param  peerName  name of publishing peer
	 * @param  descName  name of description
	 * @param  timeout  the number of milliseconds to keep trying to
	 *				  send the request successfully
	 * @param  responseLimit  the maximum number of requests to send to
	 *				  publishers.
	 *
	 * @see DescriptionListener
	 */
	public void findDescriptions(
		String peerName, String descName, long timeout, int responseLimit)
	{
		if (descName == null)
		{
			// Look for the default description of a peer
			descName = DEFAULT_DESCRIPTION_NAME;
		}

		// Give to request handler
		fRequestHandler.add(
			new DescriptionRequest(peerName, descName, timeout, responseLimit));
	}

	/**
	 * Set the specified description as the default. If this service is
	 * publishing more than one description then specify which one should
	 * be advertised as the default. If the default description is not set
	 * the first one added with the <code>addDescrition</code> method will
	 * be used as the default.
	 *
	 * @param  name  the default description name
	 * @see #addDescription(String, String)
	 */
	public void setDefaultDescription(String name)
	{
		fDefaultDescription = name;

		// check if advertisement needs to be updated
		validateSpecAdvertisement();
	}

	/**
	 * Add the name of a description to publish with the given url location.
	 * The availability of the description will be advertised to other peers.
	 *
	 * @param  name	 the description name
	 * @param  location the location of the description as a URL
	 *
	 * @throws IllegalArgumentException if name or location is null.
	 */
	public void addDescription(String name, URL location)
	{
		// Check validity of arguments
		if (name == null)
		{
			throw new IllegalArgumentException(
				"Name argument cannot be null");
		}
		if (location == null)
		{
			throw new IllegalArgumentException(
				"Location argument cannot be null");
		}

		if (fDescriptions.isEmpty() && fDefaultDescription == null)
		{
			// Initialize the default description with the first one added
			fDefaultDescription = name;
		}

		fDescriptions.put(name, location);

		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Added description: " + name + " " + location;
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"addDescription", message);
		}

		// check if advertisement needs to be updated
		validateSpecAdvertisement();
	}

	/**
	 * Remove the name of a description from the publication map.
	 *
	 * @param  name  the description name
	 */
	public void removeDescription(String name)
	{
		fDescriptions.remove(name);

		// Reset default description value if necessary
		if (fDefaultDescription != null && fDefaultDescription.equals(name))
		{
			Iterator iter = fDescriptions.keySet().iterator();
			if (iter.hasNext())
			{
				fDefaultDescription = (String) iter.next();
			}
			else
			{
				fDefaultDescription = null;
			}
		}

		// check if advertisement needs to be updated
		validateSpecAdvertisement();
	}

	//--------------------------------------------------------------------------
	// Advertisement utility methods

	/**
	 * Creates the ModuleClassAdvertisement for this service. The Module
	 * class advertisement is a very small advertisement that only
	 * advertises the existence of the service. In order to access the
	 * service, a peer will have to discover the associated module spec
	 * advertisement.
	 * <p>
	 * This method is a static method so an application can optionally create
	 * and publish the advertisement and pass the associated
	 * ModuleImplAdvertisement to an instance of this service during
	 * initialization.
	 * </p>
	 *
	 * @param  mcID Id to associate this advertisement with
	 * @return  a ModuleClassAdvertisement for this service
	 *
	 * @see #createSpecAdvertisement(ModuleClassID, String)
	 */
	public static ModuleClassAdvertisement createClassAdvertisement(
		ModuleClassID mcID)
	{
		// Create the Module class advertisement associated with the service.
		ModuleClassAdvertisement mcAdv = (ModuleClassAdvertisement)
				AdvertisementFactory.newAdvertisement(
				ModuleClassAdvertisement.getAdvertisementType());

		mcAdv.setName("JXTAMOD:" + SERVICE_NAME);
		mcAdv.setDescription(CLASS_DESCRIPTION);

		mcAdv.setModuleClassID(mcID);

		return mcAdv;
	}

	/**
	 * Creates the ModuleSpecAdvertisement associated with the service.
	 * The Module Spec advertisement should contain all the information
	 * necessary for a peer to contact the service.
	 * <p>
	 * This method is a static method so an application can optionally create
	 * and publish the advertisement and pass the associated
	 * ModuleImplAdvertisement to an instance of this service during
	 * initialization.
	 * </p>
	 *
	 * @param  msID	 Id to associate this advertisement with
	 * @param  peerName peer name to associate this advertisement with
	 * @param  descriptions description name and location map to associate
	 *				  this advertisement with
	 * @param  pipeAdv  the PipeAdvertisement to embed in this advertisement
	 * @return  a ModuleSpecAdvertisement for this service
	 *
	 * @see #createPipeAdvertisement(PeerGroupID, String)
	 */
	public static ModuleSpecAdvertisement createSpecAdvertisement(
		ModuleSpecID msID, String peerName, Map descriptions,
		PipeAdvertisement pipeAdv)
	{
		// Create the Module Spec advertisement associated with the service
		ModuleSpecAdvertisement mdAdv = (ModuleSpecAdvertisement)
				AdvertisementFactory.newAdvertisement(
				ModuleSpecAdvertisement.getAdvertisementType());

		// Initialize some of the information fields about the service.
		mdAdv.setName(
			"JXTASPEC:" + SERVICE_NAME + "." + peerName);
		mdAdv.setVersion("Version 1.0");
		mdAdv.setCreator("gsfc.nasa.gov");
		mdAdv.setModuleSpecID(msID);
		// TBD: This should point to a class implementation of the service.
		mdAdv.setSpecURI("http://pioneer.gsfc.nasa.gov/public/irc");
		mdAdv.setPipeAdvertisement(pipeAdv);

		// Create a list of descriptions as a string to insert in advertisement
		StringBuffer descListString = new StringBuffer();
		for (Iterator iter = descriptions.keySet().iterator(); iter.hasNext();)
		{
			descListString.append(DESC_NAME_PREFIX);
			descListString.append(iter.next());
			descListString.append(DESC_NAME_SUFFIX);
		}
		if (fDefaultDescription != null)
		{
			// Add the default description keyword to the description list
			descListString.append(DESC_NAME_PREFIX);
			descListString.append(DEFAULT_DESCRIPTION_NAME);
			descListString.append(DESC_NAME_SUFFIX);
		}

		// The following element may be used as a search key by other peers to
		// find a description.
		StructuredDocument paramDoc =
				StructuredDocumentFactory.newStructuredDocument(
				new MimeMediaType("text/xml"),
				KEY_DESCRIPTION_ELEM, descListString.toString());
		mdAdv.setParam(paramDoc);

		return mdAdv;
	}

	/**
	 * Creates the PipeAdvertisement that is needed by other peers to
	 * contact this service.
	 *
	 * @param  id Id to associate this advertisement with
	 * @param  peerName peer name to associate this advertisement with
	 * @return  a PipeAdvertisement for this service
	 */
	public static PipeAdvertisement createPipeAdvertisement(
		PeerGroupID id, String peerName)
	{
		PipeAdvertisement pipeAdv = (PipeAdvertisement)
			AdvertisementFactory.newAdvertisement(
			PipeAdvertisement.getAdvertisementType());

		// Create unique pipe ID based on group ID
		pipeAdv.setPipeID(IDFactory.newPipeID(id));

		pipeAdv.setName(SERVICE_NAME + "." + peerName);
		pipeAdv.setType(PipeService.UnicastType);

		return pipeAdv;
	}

	/**
	 * Determines if an existing Spec advertisement needs to be updated and
	 * published.
	 */
	private void validateSpecAdvertisement()
	{
		if (fSpecAdv != null)
		{
			ModuleSpecAdvertisement oldAdv = fSpecAdv;

			// Create new Spec Advertisement
			fSpecAdv = createSpecAdvertisement(oldAdv.getModuleSpecID(),
					fPeerName, fDescriptions, fPipeAdv);

			if (fStarted)
			{
				try
				{
					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Flushing old advertisement: " 
							+ oldAdv.getID().toString();
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"validateSpecAdvertisement", message);
					}
					
					// Flush old advertisement and Publish new advertisement
					fDiscovery.flushAdvertisements(
						oldAdv.getID().toString(), DiscoveryService.ADV);

					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Publishing new advertisement: " 
							+ fSpecAdv.getID().toString();
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"validateSpecAdvertisement", message);
					}
					
					fDiscovery.publish(fSpecAdv, DiscoveryService.ADV);
					fDiscovery.remotePublish(fSpecAdv, DiscoveryService.ADV);
				}
				catch(IOException ex)
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Problem updating advertisement in the local cache";
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"validateSpecAdvertisement", message, ex);
					}
				}
			}
		}
	}

	/**
	 * This method will initiate an asynchronous remote discovery for matching
	 * Module Spec advertisements. If successful, matches can be found in the
	 * local cache or the caller can optionally register as a DiscoveryListener.
	 *
	 * @param  peerName name of destination peer, may be null
	 * @param  descName name of description, may be null
	 * @param  listener the listener which will be called back with found
	 *				  advertisements, may be null
	 */
	private void getRemoteAdvertisements(
			String peerName, String descName, DiscoveryListener listener)
	{
		// Retrieve a list of all the advertisements from our cache
		// of responses that match. Try the most specific first.
		if (peerName != null)
		{
			// Request advertisements from the remote peer
			fDiscovery.getRemoteAdvertisements(
				null, DiscoveryService.ADV, KEY_NAME_ATTR,
				"JXTASPEC:" + SERVICE_NAME + "." + peerName,
				fPeerDiscoveryLimit, listener);
		}
		else if (descName != null)
		{
			// Request advertisements from all peers that publish the description
			fDiscovery.getRemoteAdvertisements(
				null, DiscoveryService.ADV, KEY_DESCRIPTION_ELEM,
				"*" + DESC_NAME_PREFIX + descName + DESC_NAME_SUFFIX + "*",
				fPeerDiscoveryLimit, listener);
		}
		else
		{
			// Request advertisements from all remote instances of this service
			fDiscovery.getRemoteAdvertisements(
				null, DiscoveryService.ADV, KEY_NAME_ATTR,
				"JXTASPEC:" + SERVICE_NAME + "." + "*",
				fPeerDiscoveryLimit, listener);
		}
	}

	//--------------------------------------------------------------------------
	// Message handling methods

	/**
	 * Handle all messages received by this service by dispatching them to
	 * other methods depending on message type. This method implements the
	 * {@link net.jxta.pipe.PipeMsgListener} interface and handles all
	 * {@link net.jxta.pipe.PipeMsgEvent} received.
	 *
	 * @param  msg  message received
	 *
	 * @see #handleRequest(Message)
	 * @see #handleDescription(Message)
	 */
	public void pipeMsgEvent(PipeMsgEvent event)
	{
		Message msg = event.getMessage();

		if (msg != null)
		{
			String type = msg.getString(KEY_MSG_TYPE);

			if (REQUEST_TYPE.equals(type))
			{
				handleRequest(msg);
			}
			else if (DESCRIPTION_TYPE.equals(type))
			{
				handleDescription(msg);
			}
			else
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "DescriptionPublisher.handleMessage " +
						"message received had an unknown type: " + type;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"pipeMsgEvent", message);
				}
			}
		}
		else
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Null message received";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"pipeMsgEvent", message);
			}
		}
	}

	/**
	 * Handle all request messages received by this service by sending the
	 * description to the source of the message.
	 *
	 * @param  msg  message received
	 *
	 * @see #sendDescription(String)
	 */
	private void handleRequest(Message msg)
	{
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Request received:" + NetUtil.messageToString(msg);
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"handleRequest", message);
		}

		String name = msg.getString(KEY_DESCRIPTION_NAME);

		if (DEFAULT_DESCRIPTION_NAME.equals(name))
		{
			// Replace the request for the default description with the
			// actual description name
			msg.setString(KEY_DESCRIPTION_NAME, fDefaultDescription);
			name = fDefaultDescription;
		}
		if (fDescriptions.containsKey(name))
		{
			// TBD: Authenticate sender?
			// Reply with the description
			replyWithDescription(msg);
		}
	}

	/**
	 * Handle all description messages received by this service by propagating
	 * a DescriptionEvent to all listeners.
	 *
	 * @param  msg  message received
	 *
	 * @see DescriptionEvent
	 * @see #addDescriptionListener(DescriptionListener)
	 */
	private void handleDescription(Message msg)
	{
		String urlString = msg.getString(KEY_DESCRIPTION_URL);
		String name = msg.getString(KEY_DESCRIPTION_NAME);
		
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Description received:" + name;
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"handleDescription", message);
		}

		if (urlString != null)
		{
			try
			{
				// TBD: Authenticate sender?  Match request with description?
				DescriptionEvent event =
					new DescriptionEvent(this, new URL(urlString));
				fireDescriptionEvent(event);
			}
			catch (MalformedURLException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Received an invalid description message";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"handleDescription", message, ex);
				}
			}
		}
	}


	/**
	 * Sends a description to the sender of the request message. The request
	 * message contains the necessary information for the reply. No attempt is
	 * made to retry if the send fails.
	 *
	 * @param  msg  request message
	 */
	private void replyWithDescription(Message msg)
	{
		String description = msg.getString(KEY_DESCRIPTION_NAME);
		String sender = msg.getString(KEY_MSG_SENDER);

		try
		{
			PipeAdvertisement pipeAdv = null;
			MessageElement pipeElement = msg.getElement(KEY_MSG_PIPE);

			// Extract the pipe advertisement to connect to the sender.
			pipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(
				pipeElement.getType(), pipeElement.getStream());

			// Create the output pipe and message.
			// TBD: How long should the timeout be? or use alternative?
			// Project JXTA is redesigning the Pipe API so this is likely to be
			// fixed or at least change in the near future.
			OutputPipe outPipe = fPipeSrv.createOutputPipe(pipeAdv, 15 * 1000);
			Message descMsg = createDescriptionMessage(description);
			try
			{
				// send the message
				outPipe.send(descMsg);
				
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Message sent to " + sender;
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"replyWithDescription", message);
				}
			}
			catch(IOException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Problem sending the message to the peer";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"replyWithDescription", message, ex);
				}
			}

			outPipe.close();
		}
		catch(IOException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Problem creating output pipe for response";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"replyWithDescription", message, ex);
			}
		}
	}

	/**
	 * Create a request message.
	 *
	 * @param  descName  name of requested description
	 *
	 * @return a Message
	 * @throws IOException if there is a problem creating the message
	 */
	private Message createRequestMessage(String descName) throws IOException
	{
		MimeMediaType mimeType = new MimeMediaType("text/xml");
		InputStream pipeAdvStream =  fPipeAdv.getDocument(mimeType).getStream();

		Message msg = fPipeSrv.createMessage();
		msg.setString(KEY_MSG_SENDER, fPeerName);
		msg.setString(KEY_MSG_TYPE, REQUEST_TYPE);

		// provide the name of the description we are requesting
		msg.setString(KEY_DESCRIPTION_NAME, descName);

		// provide my pipe advertisement for the reply to the request
		msg.addElement(
			msg.newMessageElement(KEY_MSG_PIPE, mimeType, pipeAdvStream));

		return msg;
	}

	/**
	 * Create a description publication message.
	 *
	 * @param name   the published name of the description
	 *
	 * @return a message
	 */
	private Message createDescriptionMessage(String name)
	{
		Message msg = fPipeSrv.createMessage();
		msg.setString(KEY_MSG_SENDER, fPeerName);
		msg.setString(KEY_MSG_TYPE, DESCRIPTION_TYPE);

		// provide information for the description we are publishing
		msg.setString(KEY_DESCRIPTION_URL, fDescriptions.get(name).toString());
		msg.setString(KEY_DESCRIPTION_NAME, name);

		return msg;
	}

	//--------------------------------------------------------------------------
	// Event methods

	/**
	 *  Adds the given DescriptionListener to this source, so
	 *  that it will receive the {@link gov.nasa.gsfc.commons.net.DescriptionEvent}s
	 *  produced.
	 *
	 *  @param listener A DescriptionListener
	**/
	public void addDescriptionListener(DescriptionListener listener)
	{
		int i = fDescriptionListeners.indexOf(listener);

		if (i < 0)
		{
			fDescriptionListeners.add(listener);
		}
	}


	/**
	 *  Removes the given DescriptionListener from this source, so
	 *  that it will no longer receive the DescriptionEvents produced.
	 *
	 *  @param listener A DescriptionListener
	**/
	public void removeDescriptionListener(DescriptionListener listener)
	{
		int i = fDescriptionListeners.indexOf(listener);
		if (i >= 0)
		{
			fDescriptionListeners.remove(i);
		}
	}

	/**
	 * Propagates a Description Event to all registered listeners.
	 *
	 * @param  event  the Description Event to propagate
	 * @see #addDescriptionListener(DescriptionListener)
	 */
	private void fireDescriptionEvent(DescriptionEvent event)
	{
		synchronized(fDescriptionListeners)
		{
			Iterator iter = fDescriptionListeners.iterator();
			while (iter.hasNext())
			{
				DescriptionListener dl = (DescriptionListener)iter.next();
				dl.handleDescriptionEvent(event);
			}
		}
	}

	//--------------------------------------------------------------------------
	// Internal classes used by DescriptionPublisher

	/**
	 * Maintains the current state of a specific description request.
	 */
	private static class DescriptionRequest
	{
		private String fPeer = null;
		private String fDescription = null;
		private long fExporationTime = -1;
		private int fRequestLimit = 1;
		private boolean fSingleRequest = false;
		private long fTimeOfLastDiscovery = 0;

		/**
		 * Creates a DescriptionRequest. A negative timeout will not expire
		 * until the description is found. The number of requests sent and
		 * therefore responses received is limited by the responseLimit
		 * parameter.
		 *
		 * @param  peerName the peer name to discover, null for any peer
		 * @param  descName the name of the description to discover
		 * @param  timeout  the number of milliseconds to keep trying to
		 *				  locate the description
		 * @param  responseLimit  the maximum number of requests to send.
		 */
		public DescriptionRequest(
			String peerName, String descName, long timeout, int responseLimit)
		{
			fPeer = peerName;
			fDescription = descName;
			fRequestLimit = responseLimit;

			if (fRequestLimit == 1)
			{
				fSingleRequest = true;
			}

			if (timeout >= 0)
			{
				fExporationTime = System.currentTimeMillis() + timeout;
			}
		}

		/**
		 * Get the name of the peer.
		 *
		 * @return a String peer name
		 */
		public String getPeerName()
		{
			return fPeer;
		}

		/**
		 * Get the name of the description.
		 *
		 * @return a String description name
		 */
		public String getDescriptionName()
		{
			return fDescription;
		}

		/**
		 * Get the time in milliseconds since last discovery request was sent.
		 *
		 * @return milliseconds since last discovery request was sent.
		 */
		public long getTimeSinceDiscovery()
		{
			return System.currentTimeMillis() - fTimeOfLastDiscovery;
		}

		/**
		 * Update the time that a Discovery request was sent.
		 */
		public void updateDiscoveryTime()
		{
			fTimeOfLastDiscovery = System.currentTimeMillis();
		}

		/**
		 * Notify this DescriptionRequest instance that it was successfully
		 * sent. Should be called after every successful send to update the
		 * response limit status.
		 */
		public void requestSent()
		{
			fRequestLimit--;
		}

		/**
		 * Get the type of request this instance represents.
		 *
		 * @return true if this request should be sent to one publisher
		 */
		public boolean isSingleRequest()
		{
			return fSingleRequest;
		}

		/**
		 * Get the current sent limit state of this request.
		 *
		 * @return true if the limit for number of requests sent is reached
		 */
		public boolean isRequestLimitReached()
		{
			return fRequestLimit <= 0;
		}

		/**
		 * Get the expired state of this request.
		 *
		 * @return true if expired
		 */
		public boolean isExpired()
		{
			if (fExporationTime < 0)
				return false;
			else
				return System.currentTimeMillis() > fExporationTime;
		}
	}

	/**
	 * Send handler class for all request messages waiting to be sent.
	 * The sending of all request messages is delegated to this class.
	 */
	private class SendRequestHandler implements Runnable,
												DiscoveryListener
	{
		private List fQueue = Collections.synchronizedList(new LinkedList());
		private Object fWaitFlag = new Object();
		private boolean fEventFlag = false;
		private HashSet fRequestHistory = new HashSet();

		/**
		 *  Contructs a SendRequestHandler.
		**/
		public SendRequestHandler()
		{
		}

		/**
		 * Discovery Event handler
		 * @param event the event
		 */
		public void discoveryEvent(DiscoveryEvent event)
		{
			synchronized(fWaitFlag)
			{
				fEventFlag = true;
				fWaitFlag.notifyAll();
			}
		}

		/**
		 *  Adds a request for this handler to send.
		 *
		 *  @param request the request to send
		**/
		public void add(DescriptionRequest request)
		{
			// Add it to Q for send
			fQueue.add(request);

			synchronized(fWaitFlag)
			{
				fEventFlag = true;
				// Wake up sender thread
				fWaitFlag.notifyAll();
			}
		}

		/**
		 *  Loops indefinitely trying to send a message for all description
		 *  requests in the queue. The request is added back on the queue if
		 *  the request has not expired, the response limit has not been reached,
		 *  or the send was unsuccessful.
		**/
		public void run()
		{
			// add this to listen for new discovery advertisements
			fDiscovery.addDiscoveryListener(this);

			try
			{
				DescriptionRequest request = null;

				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "SendRequestHandler waiting to send request";
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"run", message);
				}

				while(true)
				{
					synchronized(fWaitFlag)
					{
						if (!fEventFlag)
						{
							// Block until we receive a new discovery event
							// with a new potential advertisement match or
							// a new request is added to the queue.
							if (fQueue.size() > 0)
							{
								// This timed wait should only be necessary
								// if multicast is disabled on the LAN that
								// this peer is attached. We may never
								// receive a discovery event when a new remote
								// peer joins the group.
								fWaitFlag.wait(30 * 1000);
							}
							else
							{
								fWaitFlag.wait();
							}
						}

						// Reset event flag so we know if a Discovery event or
						// a new request is added to the queue while the
						// send handler is processing existing requests.
						fEventFlag = false;
					}

					// Try all requests currently in the Q if any to see
					// if the new advertisement is applicable to an existing
					// request.
					for (int i = fQueue.size(); i > 0; i--)
					{
						request = (DescriptionRequest) fQueue.remove(0);

						if (!request.isExpired())
						{
							boolean requestCompleted = false;

							if (request.isSingleRequest())
							{
								requestCompleted = sendOneRequest(request);
							}
							else
							{
								requestCompleted = sendManyRequests(request);
							}

							if (!requestCompleted)
							{
								if (sLogger.isLoggable(Level.INFO))
								{
									String message = "SendRequestHandler: adding request back to queue: "
										+ request.getDescriptionName();
									
									sLogger.logp(Level.INFO, CLASS_NAME, 
										"run", message);
								}

								// Request is not completed, add it back to
								// Q for retry.
								fQueue.add(request);

								if (request.getTimeSinceDiscovery() > 60 * 1000)
								{
									// Initiate a remote discovery on the
									// description for the next
									// time the request is attempted.
									getRemoteAdvertisements(
											request.getPeerName(),
											request.getDescriptionName(),
											null);

									request.updateDiscoveryTime();
								}
							}
						}
					}
				}
			}
			catch (InterruptedException ex)
			{
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "SendRequestHandler interrupted, exiting";
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"run", message, ex);
				}
			}
		}

		/**
		 * Sends a request message to all matching advertisements.
		 *
		 * @param  request the request
		 *
		 * @return true if this request was completely satisfied
		 * @see #sendRequestMessage(ModuleSpecAdvertisement, DescriptionRequest)
		 */
		private boolean sendManyRequests(DescriptionRequest request)
		{
			ModuleSpecAdvertisement mdsAdv = null;

			// Get all matches
			Iterator advIterator = findAllLocalMatchingAdv(
				request.getPeerName(), request.getDescriptionName());

			while (advIterator.hasNext() && !request.isRequestLimitReached())
			{
				// Get the discovered service advertisement as a Spec
				// Advertisement
				mdsAdv = (ModuleSpecAdvertisement) advIterator.next();

				// Try to send the request
				if (sendRequestMessage(mdsAdv, request))
				{
					String advKey =
						mdsAdv.getName() + request.getDescriptionName();

					// Send was successful, add to history so we do not
					// repeat the request to this peer for this description.
					fRequestHistory.add(advKey);
					request.requestSent();
					
					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "SendRequestHandler request satisfied: "
							+ request.getDescriptionName();
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"sendManyRequests", message);
					}
				}
			}

			return request.isRequestLimitReached();
		}

		/**
		 * Sends a message to one destination peer represented by the request.
		 *
		 * @param  request  the request
		 *
		 * @return true if send was successful, false if it failed
		 * @see #sendRequestMessage(ModuleSpecAdvertisement, DescriptionRequest)
		 */
		private boolean sendOneRequest(DescriptionRequest request)
		{
			boolean result = false;
			ModuleSpecAdvertisement mdsAdv = findLocalMatchingAdv(
				request.getPeerName(), request.getDescriptionName());

			// Try to send the request
			if (sendRequestMessage(mdsAdv, request))
			{
				String advKey = mdsAdv.getName() + request.getDescriptionName();

				// Send was successful, add to history so we do not
				// repeat the request to this peer for this description.
				fRequestHistory.add(advKey);
				request.requestSent();

				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "SendRequestHandler request satisfied: "
						+ request.getDescriptionName();
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"sendOneRequest", message);
				}

				result = true;
			}

			return result;
		}

		/**
		 * Sends a message to the destination peer represented by the
		 * ModuleSpecAdvertisement.
		 *
		 * @param  mdsAdv  the advertisement for the destination of the
		 *				 request message
		 * @param  request the request
		 *
		 * @return true if send was successful, false if it failed
		 */
		private boolean sendRequestMessage(
				ModuleSpecAdvertisement mdsAdv,
				DescriptionRequest request)
		{
			boolean result = false;
			//String descName = request.getDescriptionName();

			if (mdsAdv != null)
			{
				// Extract the pipe information from the advertisement
				PipeAdvertisement pipeAdv = mdsAdv.getPipeAdvertisement();

				OutputPipe outPipe = null;
				try
				{
					// Create the output pipe endpoint to connect
					// to the destination
					// TBD: How long should the timeout be? or use alternative?
					// Project JXTA is redesigning the Pipe API so this is likely
					// to be fixed or at least change in the near future.
					outPipe = fPipeSrv.createOutputPipe(pipeAdv, 15 * 1000);

					Message msg = createRequestMessage(request.getDescriptionName());

					// send the message
					outPipe.send(msg);

					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Message sent to "
							+ mdsAdv.getName();
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"sendRequestMessage", message);
						
						message = "Message: " + NetUtil.messageToString(msg);
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"sendRequestMessage", message);						
					}

					result = true;
				}
				catch(IOException ex)
				{
					if (sLogger.isLoggable(Level.WARNING))
					{
						String message = "Problem sending the message to the peer";
						
						sLogger.logp(Level.WARNING, CLASS_NAME, 
							"sendRequestMessage", message, ex);
					}

					try
					{
						// Remove obsolete advertisement from cache
						fDiscovery.flushAdvertisements(
							mdsAdv.getID().toString(), DiscoveryService.ADV);
					}
					catch (IOException ex2)
					{
						if (sLogger.isLoggable(Level.SEVERE))
						{
							String message = "Could not remove bad advertisement";
							
							sLogger.logp(Level.SEVERE, CLASS_NAME, 
								"sendRequestMessage", message, ex2);
						}
					}
				}

				if (outPipe != null)
				{
					outPipe.close();
				}
			}

			return result;
		}

		//----------------------------------------------------------------------
		// Description discovery utility methods

		/**
		 * This method will find one matching Module Spec advertisement in the
		 * local cache if one exist. Returns null if there are no matches.
		 *
		 * @param  peerName  name of destination peer
		 * @param  descName  name of description
		 *
		 * @return an advertisement if one is found, null if there are no matches
		 */
		private ModuleSpecAdvertisement findLocalMatchingAdv(
				String peerName, String descName)
		{
			ModuleSpecAdvertisement mdsAdv = null;
			Iterator advIterator =
					findAllLocalMatchingAdv(peerName, descName);

			if (advIterator.hasNext())
			{
				mdsAdv = (ModuleSpecAdvertisement) advIterator.next();
			}

			return mdsAdv;
		}


		/**
		 * This method will find all matching Module Spec advertisements in the
		 * local cache. Returns an empty Iterator if there are no matches.
		 *
		 * @param  peerName  name of destination peer
		 * @param  descName  name of description
		 *
		 * @return an Iterator of matches, empty if there are no matches
		 */
		private Iterator findAllLocalMatchingAdv(String peerName, String descName)
		{
			Vector advVector = new Vector();

			// Convert description name into the format the advertisement uses
			String advDescName = DESC_NAME_PREFIX + descName + DESC_NAME_SUFFIX;

			try
			{
				// Retrieve an enumeration of all the advertisements
				// from our cache that are DescriptionPublisher advertisements.
				Enumeration advEnum = fDiscovery.getLocalAdvertisements(
					DiscoveryService.ADV, KEY_NAME_ATTR,
					"JXTASPEC:" + SERVICE_NAME + "." + "*");

				while (advEnum.hasMoreElements())
				{
					// Get the discovered advertisement as a Spec Advertisement
					ModuleSpecAdvertisement mdsAdv =
						(ModuleSpecAdvertisement) advEnum.nextElement();

					// The advertisement name contains the source peer name
					String advName = mdsAdv.getName();

					// Check if advertisement is our own
					if (advName.endsWith(fPeerName))
					{
						if (sLogger.isLoggable(Level.INFO))
						{
							String message = "Advertisement is our own";
							
							sLogger.logp(Level.INFO, CLASS_NAME, 
								"findAllLocalMatchingAdv", message);
						}

						continue;
					}

					if (peerName != null)
					{
						// Check if advertisement is from the requested peer
						if (!advName.endsWith(peerName))
						{
							if (sLogger.isLoggable(Level.INFO))
							{
								String message = "Advertisement not from requested peer: " + 
									peerName;
								
								sLogger.logp(Level.INFO, CLASS_NAME, 
									"findAllLocalMatchingAdv", message);
							}
							
							continue;
						}
					}

					// Check if advertisement has the requested description
					String advDesc = (String) mdsAdv.getParam().getValue();
					if (advDesc.indexOf(advDescName) < 0)
					{
						if (sLogger.isLoggable(Level.INFO))
						{
							String message = "Advertisement did not have description:" + advDesc
								+ " looking for:" + advDescName;
							
							sLogger.logp(Level.INFO, CLASS_NAME, 
								"findAllLocalMatchingAdv", message);
						}

						continue;
					}

					// Check send history to prevent sending duplicate requests to
					// the same peer
					if (fRequestHistory.contains(advName + descName))
					{
						// This request has already been sent
						if (sLogger.isLoggable(Level.INFO))
						{
							String message = "Advertisement from this peer has already been requested:"
								+ " looking for:" + descName;
							
							sLogger.logp(Level.INFO, CLASS_NAME, 
								"findAllLocalMatchingAdv", message);
						}

						continue;
					}

					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Advertisement passed all checks:"
							+ " looking for:" + descName;
						
						sLogger.logp(Level.INFO, CLASS_NAME, 
							"findAllLocalMatchingAdv", message);
					}

					// add to vector since advertisement passed all checks
					advVector.add(mdsAdv);
				}
			}
			catch(IOException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Problem retrieving advertisements from the local cache";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"findAllLocalMatchingAdv", message, ex);
				}
			}

			return advVector.iterator();
		}

	} // end SendRequestHandler class
}