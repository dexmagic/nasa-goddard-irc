//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.library.logging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jscience.physics.units.SI;

import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberId;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * This is a java.util.logging.Handler that creates Basis Bundles for each log
 * level (including custom) and publishes log messages to the appropriate basis
 * bundle.
 * <p>
 * An example of using this handler is to add it to a log.props file as follows:
 * <p>
 * <code>handlers=gov.nasa.gsfc.irc.library.logging.BasisBundleLogHandler</code>
 * <p>
 * <p>
 * This handler automatically creates Basis Bundles with the following names which
 * correspond to the Java logging levels:
 * "Severe", "Warning", "Info", "Config", "Fine", "Finer", "Finest", and "Custom".
 * Each Basis Bundle has the following data buffers:
 * <ul>
 * <li> Time - double epoch seconds of log message
 * <li> Message - String message
 * <li> LevelName - String name of log level (for example, "SEVERE")
 * <li> LevelInt - int log level (for example, 900)
 * <li> LogRecord - {@link java.util.logging.LogRecord LogRecord} of log message
 * </ul>
 * <p>
 * Log messages are automatically published to their respective Basis Bundle.
 * Log messages that don't have a Level that matches one of the seven predefined
 * Java logging levels will be published to the "Custom" Basis Bundle.  In this case the <code>LevelName</code>
 * and <code>LevelInt</code> will contain the name and level value of the custom log level.
 * <p>
 * The fully qualified names of the Basis Bundles are
 * "Severe.BasisBundleLogHandler", "Warning.BasisBundleLogHandler", and so on.  An example
 * request to receive logging messages in a BasisSetTcpGeneric descriptor is as follows:
 * 
 * <pre>
 * 
 *  &lt;DataSetRequest&gt;
 *		&lt;Format&gt;xdr&lt;/Format&gt;
 *  
 *		&lt;BasisBundleRequest&gt;
 *			&lt;BasisBundleName&gt;Severe.BasisBundleLogHandler&lt;/BasisBundleName&gt;
 *		&lt;/BasisBundleRequest&gt;
 *  
 *		&lt;BasisBundleRequest&gt;
 *			&lt;BasisBundleName&gt;Warning.BasisBundleLogHandler&lt;/BasisBundleName&gt;
 *		&lt;/BasisBundleRequest&gt;
 *  
 *		&lt;BasisBundleRequest&gt;
 *			&lt;BasisBundleName&gt;Custom.BasisBundleLogHandler&lt;/BasisBundleName&gt;
 *			&lt;RequestAmount&gt;10&lt;/RequestAmount&gt;
 *		&lt;/BasisBundleRequest&gt;				
 * 
 *  &lt;/DataSetRequest&gt;
 *  
 * </pre>
 * <p>
 * This DataSetRequest will request Severe and Warning messages as soon as they're available and
 * custom messages in groups of 10.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Id: BasisBundleLogHandler.java,v 1.5 2006/08/01 19:55:48 chostetter_cvs Exp $
 * @author $Author: chostetter_cvs $
 */
public class BasisBundleLogHandler extends Handler implements BasisBundleSource
{
	private static final String NAME = "BasisBundleLogHandler";	

	//
	// Define the names of the basis bundles
	// I *could use Level.*.toString(), but just in case the names change
	//

	// Need to create our own level to represent "custom" levels.
	// (anonymous subclass needed to access constructor)
	private final static Level sCustomLevel = new Level("custom", 501) {};	
	
	private final static String[] BASIS_BUNDLE_NAMES =
	{ "Severe", "Warning", "Info", "Config", "Fine", "Finer", "Finest",
			"Custom" };

	private final static Level[] BASIS_BUNDLES_LEVELS =
	{ Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE,
			Level.FINER, Level.FINEST, sCustomLevel };	
	
	public static final String BASIS_BUFFER_NAME = "Time";
	private static final String LOG_RECORD_BUFFER_NAME = "LogRecord";
	private static final String LEVEL_NAME_BUFFER_NAME = "LevelName";	
	private static final String LEVEL_INT_BUFFER_NAME = "LevelInt";		
	private static final String MESSAGE_BUFFER_NAME = "Message";	
	
	private static final int BASIS_BUNDLE_SIZE = 2000;
	
	private MemberId fMemberId;

	private Map fLevelToBasisBundleMap;
		
	public BasisBundleLogHandler()
	{
		fMemberId = new DefaultMemberId(this.getName());
		
		fLevelToBasisBundleMap = new HashMap();		
		for (int i = 0; i < BASIS_BUNDLE_NAMES.length; i++)
		{
			BasisBundle basisBundle = buildBasisBundle(BASIS_BUNDLE_NAMES[i]);
			fLevelToBasisBundleMap.put(BASIS_BUNDLES_LEVELS[i], basisBundle);
		}
		
    }

	protected BasisBundle buildBasisBundle(String basisBundleName)
	{
		Set dataBufferDescriptors = new LinkedHashSet();
		
		dataBufferDescriptors.add(new DataBufferDescriptor(MESSAGE_BUFFER_NAME, String.class));
		dataBufferDescriptors.add(new DataBufferDescriptor(LEVEL_NAME_BUFFER_NAME, String.class));		
		dataBufferDescriptors.add(new DataBufferDescriptor(LEVEL_INT_BUFFER_NAME, int.class));				
		dataBufferDescriptors.add(new DataBufferDescriptor(LOG_RECORD_BUFFER_NAME, LogRecord.class));				
				
		BasisBundleDescriptor basisBundleDescriptor = 
			new BasisBundleDescriptor
			(basisBundleName, 
					new DataBufferDescriptor(BASIS_BUFFER_NAME, double.class, 
							SI.SECOND)
							, dataBufferDescriptors);
		
		BasisBundle basisBundle = Irc.getBasisBundleFactory()
		.createBasisBundle(basisBundleDescriptor, this,
            BASIS_BUNDLE_SIZE);		
	
		Irc.getDataSpace().addBasisBundle(basisBundle);
		
		return basisBundle;
	}
		
	

	
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
//	@Override
	public void publish(LogRecord record)
	{
		// 
		// Get the basis bundle that matches the log level
		//
		
		BasisBundle basisBundle = (BasisBundle) fLevelToBasisBundleMap.get(record.getLevel());
		
		// 
		// Must be a custom level, so use the "Custom" basis bundle
		//
		if (basisBundle == null)
		{
			basisBundle = (BasisBundle) fLevelToBasisBundleMap.get(sCustomLevel);			
		}
		
		// Allocate a basis set for a single log message
		BasisSet basisSet = basisBundle.allocateBasisSet(1);
		
		//
		// Write time, message string, level int value, and raw LogRecord
		//
		basisSet.getBasisBuffer().put(0, record.getMillis() * 1000.0);
		basisSet.getDataBuffer(0).put(0, record.getMessage());
		basisSet.getDataBuffer(1).put(0, record.getLevel().getName());		
		basisSet.getDataBuffer(2).put(0, record.getLevel().intValue());				
		basisSet.getDataBuffer(3).put(0, record);		

		// Send it off ..
		basisBundle.makeAvailable(basisSet);
	}
	

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	//	@Override
	public void flush()
	{
		// Nothing to flush
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	//	@Override
	public void close() throws SecurityException
	{
		//
		// Remove all basis bundles from the dataspace
		//
		Collection basisBundles = fLevelToBasisBundleMap.values();
		for (Iterator iter = basisBundles.iterator(); iter.hasNext();)
		{
			BasisBundle basisBundle = (BasisBundle) iter.next();
			Irc.getDataSpace().remove(basisBundle);
		}
	}



	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.BasisBundleSource#getMemberId()
	 */
	public MemberId getMemberId()
	{
		return (fMemberId);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.types.namespaces.HasName#getName()
	 */
	public String getName()
	{
		return NAME;
	}



	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName#getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		return NAME;
	}



	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName#getNameQualifier()
	 */
	public String getNameQualifier()
	{
		// Not really sure what to do here
		return null;
	}
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisBundleLogHandler.java,v $
//	Revision 1.5  2006/08/01 19:55:48  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.4  2006/06/26 20:08:09  smaher_cvs
//	Name changes
//	
//	Revision 1.3  2006/06/14 19:13:14  smaher_cvs
//	Comments.
//	
//	Revision 1.2  2006/06/14 19:06:55  smaher_cvs
//	Tweaked comments.
//	
//	Revision 1.1  2006/06/14 19:00:10  smaher_cvs
//	Initial - working.
//	