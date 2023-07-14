// === File Prolog ============================================================
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.logging;

import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import gov.nasa.gsfc.commons.system.resources.ResourceManager;
import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberId;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.app.DescriptorFramework;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataSpaceDescriptor;
import gov.nasa.gsfc.irc.description.DescriptorException;
import gov.nasa.gsfc.irc.gui.logging.HandlerUtil;
import gov.nasa.gsfc.irc.gui.logging.TextPaneHandler;

/**
 * BasisSetHandler publishes log records to the dataspace.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 25, 2005 1:46:12 PM
 * @author Peyush Jain
 */

public class BasisSetHandler extends Handler implements BasisBundleSource
{
    private static final String CLASS_NAME = BasisSetHandler.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    public static final int DEFAULT_BASIS_BUNDLE_SIZE = 10;
    private String fDataMlUrl = "resources/xml/LogData.xml";

    private BasisBundle fOutputBasisBundle;
    private BasisSet fBasisSet;
    private DefaultMemberId fMemberId;
    
    /**
     * Constructor
     */
    public BasisSetHandler()
    {
    		configure();

        // create basisbundleSourceId
        fMemberId = new DefaultMemberId("Log Handler");

        // create descriptor
        DataSpaceDescriptor dataDescriptor = createDataDescriptor(fDataMlUrl);

        // create basisBundle and add it to the dataspace
        createBasisBundle(dataDescriptor);
    }

    /**
     * Configure a BasisSetHandler from LogManager properties and/or default
     * values as specified in the class javadoc.
     */
    private void configure()
    {
        String cname = TextPaneHandler.class.getName();

        setLevel(HandlerUtil.getLevelProperty(cname + ".level", Level.INFO));
        setFilter(HandlerUtil.getFilterProperty(cname + ".filter", null));
        setFormatter(HandlerUtil.getFormatterProperty(cname + ".formatter",
                new SimpleFormatter()));
        try
        {
            setEncoding(HandlerUtil.getStringProperty(cname + ".encoding", null));
        }
        catch (Exception ex)
        {
            try
            {
                setEncoding(null);
            }
            catch (Exception ex2)
            {
                // doing a setEncoding with null should always work.
                // assert false;
            }
        }
    }

    /**
     * Publishes a log record to the dataspace.
     * 
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    public void publish(LogRecord record)
    {
        if (isLoggable(record))
        {
            fBasisSet = fOutputBasisBundle.allocateBasisSet(1);

            // keeping it simple here because there is only 1 databuffer and 1 sample
            fBasisSet.getBasisBuffer().put(0, record.getMillis());
            fBasisSet.getDataBuffer(0).put(0, record);

            fOutputBasisBundle.makeAvailable(fBasisSet);
        }
    }

    /**
     * Create a DataDescriptor from the specified DataML file.
     * 
     * @param dataMlName file name of the DataML file
     * @return a new DataDescriptor
     */
    private DataSpaceDescriptor createDataDescriptor(String dataMlName)
    {
        ResourceManager resourceMgr = Irc.getResourceManager();
        URL dataMlUrl = resourceMgr.getResource(dataMlName);
        DataSpaceDescriptor descriptor = null;

        if (sLogger.isLoggable(Level.INFO))
        {
            String message = " DataMl URL: " + dataMlUrl;

            sLogger.logp(Level.INFO, CLASS_NAME, "createDataDescriptor",
                    message);
        }

        DescriptorFramework descriptorFramework = Irc.getDescriptorFramework();

        try
        {
            descriptor = descriptorFramework.loadDataml(dataMlUrl, null, null);
        }
        catch (DescriptorException ex)
        {
            if (sLogger.isLoggable(Level.SEVERE))
            {
                String message = "loadDataMl() failed";
                sLogger.logp(Level.SEVERE, CLASS_NAME, "createDataDescriptor",
                        message, ex);
            }
        }

        return (descriptor);
    }

    /**
     * Creates an output BasisBundle for use by this InputAdapter, according to
     * its current DataDescriptor.
     */
    private void createBasisBundle(DataSpaceDescriptor dataDescriptor)
    {
        if (dataDescriptor != null)
        {
            BasisBundleDescriptor descriptor = (BasisBundleDescriptor)dataDescriptor
                    .getBasisBundleDescriptors().iterator().next();

            if (descriptor != null)
            {
                fOutputBasisBundle = Irc.getBasisBundleFactory()
                        .createBasisBundle(descriptor, this, 
                               DEFAULT_BASIS_BUNDLE_SIZE);
                
                Irc.getDataSpace().addBasisBundle(fOutputBasisBundle);
            }
        }
    }

    /**
     * Returns the MemberId of this BasisSetHandler.
     * 
     * @return The MemberId of this BasisSetHandler
     */
    public MemberId getMemberId()
    {
        return (fMemberId);
    }

    /**
     * Sets the name of this BasisSetHandler to the given name.
     * 
     * @param The desired new name of this BasisSetHandler
     * @throws PropertyVetoException if the attempted name change is vetoed
     */

    protected void setName(String name)
    		throws PropertyVetoException
    {
    	fMemberId.setName(name);
    }

    /**
     * Returns the name of this BasisSetHandler.
     * 
     * @return The name of this BasisSetHandler.
     */
    public String getName()
    {
        return (fMemberId.getName());
    }

    /**
     * Returns the name qualifier of this BasisSetHandler within whatever
     * Namespace it may occupy.
     * 
     * @return The name qualifier of this BasisSetHandler within whatever
     * 		Namespace it may occupy
     */
    public String getNameQualifier()
    {
        return (fMemberId.getNameQualifier());
    }

    /**
     * Returns the fully-qualified name of this BasisSetHandler within whatever
     * Namespace it may occupy (i.e., of the form "<BasisSetHandler name> of 
     * <Namespace name>".
     * 
     * @return The fully-qualified name of this BasisSetHandler within whatever
     * 		Namespace it may occupy (i.e., of the form "<BasisSetHandler name> of 
     * 		<Namespace name>"
     */
    public String getFullyQualifiedName()
    {
        return (fMemberId.getFullyQualifiedName());
    }

   /**
     * @see java.util.logging.Handler#close()
     */
    public void close() throws SecurityException
    {
    }

    /**
     * @see java.util.logging.Handler#flush()
     */
    public void flush()
    {
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: BasisSetHandler.java,v $
// Revision 1.2  2006/08/01 19:55:48  chostetter_cvs
// Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
// Revision 1.1  2006/02/01 23:00:49  tames
// Relocated.
//
// Revision 1.3  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.2  2005/09/08 22:18:32  chostetter_cvs
// Massive Data Transformation-related changes
//
// Revision 1.1  2005/09/01 17:11:26  pjain_cvs
// Adding to CVS.
//
//