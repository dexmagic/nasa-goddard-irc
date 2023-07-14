//=== File Prolog ============================================================
//
//	$Header: /cvs/IRC/Dev/source/commons/gov/nasa/gsfc/commons/net/test/TestJxta.java,v 1.6 2005/04/04 15:40:59 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log:
//	 1	IRC	   1.0		 12/11/2001 5:33:15 PMTroy Ames
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

package gov.nasa.gsfc.commons.net.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;

import gov.nasa.gsfc.commons.net.NetUtil;
import gov.nasa.gsfc.commons.net.PeerNetworkManager;

/**
 * The TestJxta class for testing Jxta and the gov.nasa.gsfc.commons.net classes.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/04/04 15:40:59 $
 * @author	Troy Ames
**/
public class TestJxta implements Runnable, DiscoveryListener
{
	private static final String CLASS_NAME = 
		TestJxta.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private DiscoveryService fDiscovery;
	private PeerAdvertisement fPeerAdv;

	public TestJxta(DiscoveryService discovery)
	{
		fDiscovery = discovery;
	}

	/**
	 * This thread basically loops for ever discovering peers
	 * every minute, and displaying the results.
	 */
	public void run() {
		try
		{
			// Add ourselves as a DiscoveryListener for DiscoveryResponse events
			fDiscovery.addDiscoveryListener(this);
			//fDiscovery.getLocalAdvertisements(Discovery.PEER, null, null);

			fDiscovery.getRemoteAdvertisements(
					null, DiscoveryService.PEER, null, null, 10, null);
			fDiscovery.getRemoteAdvertisements(
					null, DiscoveryService.GROUP, null, null, 10, null);
			fDiscovery.getRemoteAdvertisements(
					null, DiscoveryService.ADV, null, null, 10, null);

			while (true)
			{
				// wait a bit before sending a discovery message
				try
				{
					Thread.sleep(30*60 * 1000);
				}
				catch(Exception e)
				{
				}
/*
				System.out.println("Sending a Discovery Message");
				// look for any peer
				fDiscovery.getRemoteAdvertisements(
					null, fDiscovery.PEER, null, null, 5);
				//look for any group
				fMyQueryId = fDiscovery.getRemoteAdvertisements(
					null, Discovery.GROUP, null, null, 5);

				//look for any jxta
				fMyQueryId = fDiscovery.getRemoteAdvertisements(
					null, Discovery.ADV, null, null, 5);
					*/
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * By implementing DiscoveryListener we must define this method
	 * to deal with discovery responses
	 */
	public void discoveryEvent(DiscoveryEvent ev) {

		DiscoveryResponseMsg res = ev.getResponse();

		// let's get the responding peer's advertisement
		String aRes = res.getPeerAdv();
		try
		{

			InputStream is = new ByteArrayInputStream( (aRes).getBytes() );

			StructuredTextDocument doc = (StructuredTextDocument)
				StructuredDocumentFactory.newStructuredDocument(
				new MimeMediaType( "text/xml" ), is );
			fPeerAdv = (PeerAdvertisement)
				AdvertisementFactory.newAdvertisement(doc);
			
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = " [  Got a Discovery Response ["+
					res.getResponseCount()+ " elements]  from peer : "
					+ fPeerAdv.getName() +"  ]";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"discoveryEvent", message);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		Enumeration responses = res.getResponses();

		String str=null;
		Advertisement adv=null;

		while (responses.hasMoreElements())
		{

			try
			{
				str = (String) responses.nextElement();

				// the following illustrates how to create
				// the advertisement object from a response
				adv =(Advertisement)
				AdvertisementFactory.newAdvertisement
					  (new MimeMediaType ("text/xml"),
					   new ByteArrayInputStream
					   (str.getBytes()));
				
				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Elements:"+ NetUtil.advToString
						(adv, new MimeMediaType("text/plain"));
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"discoveryEvent", message);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * Print Usage information
	 */
	public static void help()
	{
		System.out.println("Commands:");
		System.out.println("  find <name>   -find the description");
		System.out.println("  newGrp <name> -join and/or create the group");
		System.out.println("  changeGrp [<name>} -change the default group. "
				+ "If name is not specified");
		System.out.println("					  "
				+ "then change to the root. Determines which group the ");
		System.out.println("					  "
				+ "peers, groups, and advs commands operate on.");

		System.out.println("  peers  -send a remote request for peer info");
		System.out.println("  groups -send a remote request for group info");
		System.out.println("  advs   -send a remote request for advertisement info");
		System.out.println("  flushPeer  -flush peer adv from current group");
		System.out.println("  flushGroup -flush group adv from current group");
		System.out.println("  flushAdv   -flush adv from current group");
		System.out.println("  help   -prints this message");
		System.out.println("  exit   -exits the program");
		System.out.println(" ");
	}

	/**
	 * Main method for testing the jxta peer manager code. Type
	 * <code>exit</code> to stop the application. Type <code>help</code> for
	 * a list of valid commands.
	 * <p>
	 * USAGE: <code>TestJxta [[-g <groupName>] [-d <name> <url>]... ]...</code>
	 * </p>
	 * <p>OPTIONS: <br>
	 * [-g <groupName>] joins a group of the given name. All descriptions
	 * following this option will be published on this group unless
	 * another group is specified.<br>
	 * [-d <name> <url>] sets the location of the published description
	 * for this peer. <br>
	 * <name> required name to use for publishing the description. Other peers
	 * will use this name to locate the description.
	 * </p>
	 * <p>Example use:
	 * <code>java -cp classes </code>
	 * <code>gov.nasa.gsfc.irc.testing.net.TestJxta </code>
	 * <code>-g test -d device http://somewhere.gov/desc.xml</code>
	 * </p>
	 *
	 * @param args the string array of arguments passed to the simulator
	 */
	public static void main(String[] args)
	{
		// With the following properties set we will not be prompted for
		// name and password when jxta is started.
		System.setProperty("net.jxta.tls.principal", "tames");
		System.setProperty("net.jxta.tls.password", "password");

		PeerNetworkManager peerMgr = null;

		sLogger.setLevel(Level.ALL);
		
		sLogger.logp(Level.INFO, CLASS_NAME, "main", "Starting TextJxta...");

		peerMgr = new PeerNetworkManager();

		// Parse arguments
		for (int i=0; i < args.length; ++i)
		{
			String groupName = null;

			if (args[i].equals("-d"))
			{
				String descName = args[++i];
				String urlString = args[++i];
				System.out.println("Name:" + descName +" urlString:" + urlString);
				try
				{
					peerMgr.publishDescription(
							groupName, descName, new URL(urlString));
				}
				catch (MalformedURLException ex)
				{
					ex.printStackTrace();
					System.out.println("Skipping parameter");
				}
			}

			if (args[i].equals("-g"))
			{
				groupName = args[++i];
				System.out.println("Group name:" + groupName);
				peerMgr.joinGroup(groupName);
			}
		}

		DiscoveryService discovery = peerMgr.getRootPeerGroup().getDiscoveryService();
		TestJxta test = new TestJxta(discovery);
		//new Thread(test).start();

		String line = null;

		// Wait for user input
		try
		{
			String description = null;
			BufferedReader buffer =
				new BufferedReader(new InputStreamReader(System.in));
			line = buffer.readLine();
			// Chceck for exit command
			while (!line.equalsIgnoreCase("exit"))
			{
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreElements())
				{
					String token = (String) st.nextElement();
					// Check for find command
					if (token.equalsIgnoreCase("find"))
					{
						if (st.hasMoreElements())
						{
							description = (String) st.nextElement();
							// find the description
							peerMgr.findDescriptions(
								null, null, description, 120 * 1000, 1);
						}
						else
						{
							System.out.println("missing description name");
							System.out.println("USAGE: find <name>");
						}
					}
					else if (token.equalsIgnoreCase("peers"))
					{
						discovery.getRemoteAdvertisements(
								null, DiscoveryService.PEER, null, null, 10, test);
					}
					else if (token.equalsIgnoreCase("advs"))
					{
						discovery.getRemoteAdvertisements(
								null, DiscoveryService.ADV, null, null, 10, test);
					}
					else if (token.equalsIgnoreCase("groups"))
					{
						discovery.getRemoteAdvertisements(
								null, DiscoveryService.GROUP, null, null, 10, test);
					}
					else if (token.equalsIgnoreCase("flushGrp"))
					{
						discovery.flushAdvertisements(null, DiscoveryService.GROUP);;
					}
					else if (token.equalsIgnoreCase("flushPeer"))
					{
						discovery.flushAdvertisements(null, DiscoveryService.PEER);;
					}
					else if (token.equalsIgnoreCase("flushAdv"))
					{
						discovery.flushAdvertisements(null, DiscoveryService.ADV);;
					}
					else if (token.equalsIgnoreCase("help"))
					{
						help();
					}
					else if (token.equalsIgnoreCase("newGrp"))
					{
						if (st.hasMoreElements())
						{
							String name = (String) st.nextElement();
							// find the description
							peerMgr.joinGroup(name);
						}
						else
						{
							System.out.println("missing group name");
							System.out.println("USAGE: newGrp <name>");
						}
					}
					else if (token.equalsIgnoreCase("changeGrp"))
					{
						if (st.hasMoreElements())
						{
							String name = (String) st.nextElement();
							// find the group
							PeerGroup pg = peerMgr.getPeerGroup(name);
							if (pg != null)
							{
								discovery = pg.getDiscoveryService();
							}
							else
							{
								System.out.println("Not a member of group: " + name);
							}
						}
						else
						{
							// Reset discovery to root peer group
							discovery =
								peerMgr.getRootPeerGroup().getDiscoveryService();;
						}
					}
					else
					{
						System.out.println("Unknown command: " + token);
					}
				}

				line = buffer.readLine();
			}
		}
		catch (IOException ex)
		{
			System.out.println(
				"Exception reading from InputStream: " + ex.getMessage());
		}

		peerMgr.stop();
		sLogger.logp(Level.INFO, CLASS_NAME, "main", "Exiting TextJxta...");
		System.exit(0);
	}
}