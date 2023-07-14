//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.app;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import gov.nasa.gsfc.commons.system.SysManager;
import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.commons.xml.SchemaUtil;
import gov.nasa.gsfc.commons.xml.SubtreeMap;
import gov.nasa.gsfc.commons.xml.XmlProcessingResult;
import gov.nasa.gsfc.commons.xml.XmlUtil;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;
import gov.nasa.gsfc.irc.components.description.Cml;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.components.description.ComponentSetDescriptor;
import gov.nasa.gsfc.irc.data.description.DataSpaceDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.DescriptorException;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.DirectoryDescriptor;
import gov.nasa.gsfc.irc.description.xml.Dml;
import gov.nasa.gsfc.irc.description.xml.LookupTable;
import gov.nasa.gsfc.irc.description.xml.ParameterSetDescriptor;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.description.DeviceSetDescriptor;
import gov.nasa.gsfc.irc.devices.description.Iml;
import gov.nasa.gsfc.irc.devices.description.RootDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptGroupDescriptor;

/**
 * This class provides a central location for methods needed
 * to build descriptors from XML files. Descriptors can be though of as
 * object representations of data contained in individual xml files
 * along with some additional functionality that makes use of the data.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/02/14 20:05:00 $
 * @author John Higinbotham
 * @author Troy Ames
**/

public class DescriptorFramework
{
	private static final String CLASS_NAME = 
		DescriptorFramework.class.getName();
	
	private static final Logger sLogger =  Logger.getLogger(CLASS_NAME);
	
	//---Constants
	private static final String XML_NAMESPACE_PREFIX = "xsi";
	private static final String XML_NAMESPACE        = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String FILE_SEPARATOR 		 = Irc.getResourceManager().getResourcePathSeparator();
	public  static final String XML_TEMP_DIR_PROP    = "xml.temp.directory";
	private static final Namespace XML_NAMESPACE_OBJ = Namespace.getNamespace(XML_NAMESPACE_PREFIX, XML_NAMESPACE);
//	private static final String ANY_XML_BASE_FILE    = "iml.xsd";
	private static final String BASE = Irc.getPreference(IrcPrefKeys.XML_BASE_DIR);

	//---IRC Schema filename constants
	private static final String IML_SCHEMA   = "iml.xsd";
	private static final String MML_SCHEMA   = "mml.xsd";
	private static final String CPML_SCHEMA  = "cpml.xsd";
	private static final String CML_SCHEMA  = "cml.xsd";
	//private static final String PELML_SCHEMA = "pelml.xsd";
	private static final String DML_SCHEMA   = "dml.xsd";
//	private static final String PCML_SCHEMA  = "pcml.xsd";
	private static final String PSML_SCHEMA  = "psml.xsd";
//	private static final String DSML_SCHEMA  = "dsml.xsd";
//	private static final String DMML_SCHEMA  = "dmml.xsd";
//	private static final String PDML_SCHEMA  = "pdml.xsd";
	private static final String DATAML_SCHEMA  = "dataml.xsd";




	//---Vars
    private LookupTable fGlobalLookupTable   = null;  // Shared lookup table

//----------------------------------------------------------------------------------

    /**
     * Construct a new DescriptorFramework.
    **/
    public DescriptorFramework()
    {
		//---Complain if logging is not setup
//        if (! sLogger.isLoggable(Level.INFO))
//        {
//			if (sLogger.isLoggable(Level.WARNING))
//			{
//				String message = 
//					"Descriptor log category NOT enabled. Vital messages will go undetected";
//				
//				sLogger.logp(Level.WARNING, CLASS_NAME, 
//					"DescriptorFramework (ctor)", message);
//			}
//        }
    }

    /**
     * Load a MML XML file and use it for other load calls that would
     * otherwise require a MML specification. This method is useful
     * if several XML files are to be loaded and they all use the same
     * MML file. By reusing the stored results of the MML file load,
     * it is not necessary to reparse the file the next time it is needed.
     * The LookupTable loaded via the call is returned so that the caller
     * can store or use it later if desired.
     *
     * @param mmlUrl URL of MML XML file.
     * @return LookupTable Global mapping loaded.
    **/
    public LookupTable loadAndSetGlobalMap(URL mmlUrl) throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing global mapping XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadAndSetGlobalMap", message);
		}

		checkArgument("loadAndSetGlobalMap", "mmlUrl", mmlUrl);

        //--- Load and save the MML file
        fGlobalLookupTable = loadMml(mmlUrl, BASE);

		//---Save the table to the descriptor library
		Irc.getDescriptorLibrary().addLookupTable(fGlobalLookupTable);

        return fGlobalLookupTable;
    }

	/**
	 * Get the global map in use by this framework.
	 *
	 * @return LookupTable
	**/
	public LookupTable getGlobalMap()
	{
		return fGlobalLookupTable;
	}

	/**
     * Load LookupTable from XML.
     *
     * @param mmlUrl  URL of MML (mapping) XML file.
     * @return LookupTable
	**/
	public LookupTable loadLookupTable(URL mmlUrl) throws DescriptorException
	{
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing type mapping XML:" + mmlUrl + " ...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadLookupTable", message);
		}

		checkArgument("loadLookupTable", "mmlUrl", mmlUrl);

        //--- Load and save the MML file
		LookupTable rval = loadMml(mmlUrl, BASE);
		
        return rval;
	}

    /**
     * This method can be used to change the global mapping lookup table
     * to one that has been loaded in the past. All future calls (not
     * providing a MML file) will load IML, CPML, etc. files using the
     * new global lookup table set by this call.
     *
     * @param lookuptable Previously loaded lookup table.
    **/
    public void setGlobalMap(LookupTable lookuptable) throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Resetting global mapping XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"setGlobalMap", message);
		}
 
		checkArgument("setGlobalMap", "lookuptable", lookuptable);

		fGlobalLookupTable = lookuptable;
		Irc.getDescriptorLibrary().replaceLookupTable(lookuptable);
    }

	/**
	 * Get the value specifed by the key in the specified 
	 * namespace from the global type map. 
	 * Returns null if not found. 
	 *
	 * @param  namespace Namespace to look at. 
	 * @param  key       Key to get value for. 
	 * @return a String value or null if not found. 
	**/
	public String findInGlobalMap(String namespace, String key)
	{
		String result = null;
		
		if (fGlobalLookupTable != null)
		{
			result = fGlobalLookupTable.lookup(namespace, key);
		}
		return result;
	}
	
	/**
	 * Get an instance of the class specifed by the key in the specified 
	 * namespace from the global type map. If the class is a singleton with
	 * a static <code>getInstance</code> method then this method will be used
	 * to get an instance, otherwise the default no argument constructor will
	 * be called.
	 * Returns null if an instance cannot be created. 
	 *
	 * @param  namespace Namespace to look at. 
	 * @param  key	   Key to get value for. 
	 * @return an instance of the class mapped to namespace and key. 
	**/
	public Object instantiateFromGlobalMap(String namespace, String key)
	{
		Object newInstance = null;
		String className = null;
		
		if (fGlobalLookupTable != null)
		{
			className = fGlobalLookupTable.lookup(namespace, key);
		}
		
		if (className != null)
		{
			try
			{
				Class aClass = Irc.loadClass(className);
				
				// Look for a getInstance() method
				try
				{ 
					Method factoryMethod = aClass.getMethod(SysManager.GETINSTANCE, null);
					if (Modifier.isStatic(factoryMethod.getModifiers()))
					{
						// Use getInstance method
						newInstance =  factoryMethod.invoke(null, null);
					}
				}
				catch (NoSuchMethodException ex)
				{
					// Nothing to do here since this is the common case
				}

				if (newInstance == null)
				{
					// Instantiate class using default constructor
					newInstance = Irc.instantiateClass(className);
				}
			}
			catch (Exception e)
			{
				String message = 
					"Failed to get instance for class name: " + className;
						
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"instantiateFromTypemap", message, e);
			}
		}
		else 
		{
			String message = "Failed to locate class "
			+ " for key: " + key 
			+ " and namespace: " + namespace;
				
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"instantiateFromGlobalMap", message);
		}
		
		return newInstance;
	}

	/**
     * Load Instruments from XML.
     *
     * @param imlUrl  URL of IML (instrument) XML file.
     * @param mmlUrl  URL of MML (typemap) XML file.
     * @return DeviceSetDescriptor
    **/
    public RootDescriptor loadInstruments(URL imlUrl, URL mmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing instrument descripton XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadInstruments", message);
		}

		checkArgument("loadInstruments", "imlUrl", imlUrl);
		
        return loadIml(imlUrl, mmlUrl, BASE);
    }

    /**
     * Load Instruments from XML. A call to loadGlobalMappingFile() or
     * setGlobalMap() must be made prior to calling this method.
     *
     * @param imlUrl	URL of IML (instrument) XML file.
     * @return DeviceSetDescriptor
    **/
    public RootDescriptor loadInstruments(URL imlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = 
				"Processing instrument descripton: " + imlUrl.toString();
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadInstruments", message);
		}

		checkArgument("loadInstruments", "imlUrl", imlUrl);
		
        return loadIml(imlUrl, null, BASE);
    }

    /**
     * Load scripts from the XML.
     *
     * @param cpmlUrl  URL of CPML (script) XML file.
     * @param mmlUrl   URL of MML (typemap) XML file.
     * @return ScriptGroupDescriptor
    **/
	public ScriptGroupDescriptor loadScripts(
		URL cpmlUrl,
		URL mmlUrl)
		throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing script description XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadScripts", message);
		}

		checkArgument("loadScripts", "cpmlUrl", cpmlUrl);
		
        return loadCpml(cpmlUrl, mmlUrl, BASE);
    }

    /**
     * Load scripts from XML. A call to loadGlobalMappingFile() or
     * setGlobalMap() must be made prior to calling this method.
     *
     * @param imlUrl     URL of CPML (script)XML file.
     * @return ScriptGroupDescriptor
    **/
    public ScriptGroupDescriptor loadScripts(URL cpmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = 
				"Processing script description XML (w/ shared MML)...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadScripts", message);
		}

		checkArgument("loadScripts", "cpmlUrl", cpmlUrl);
		
        return loadCpml(cpmlUrl, null, BASE);
    }

	/**
	 * This method will store a top level script group descriptor as 
	 * xml to the files from which it came. If a file is not a top 
	 * level descriptor an exception will be thrown. Also if a caller trys to 
	 * save a descriptor and any of the files are readonly, an excpetion is 
	 * thrown. Callers should ensure, in advance that these things do not happen.
	 *
	 * @param descriptor ScriptGroupDescriptor (top-level) to store
	**/
    public void storeCpml(ScriptGroupDescriptor descriptor) 
    	throws DescriptorException
    {
		Document doc   = null;
		Element root   = null;
		SubtreeMap stm = descriptor.getSubtreeMap();

		if (stm == null)
		{
			throw (new DescriptorException(
				"Attempt to perform top level save on non top level script group!"));
		}

		URL url            = stm.getRootOrigin();
		File file          = FileUtil.urlToFile(url);
		ArrayList readonly = XmlUtil.getReadonlyFiles(stm, file);

		if (readonly.size() > 0)
		{
			StringBuffer message = new StringBuffer
				("Unable to perform save because readonly files encountered:\n");
		
			for (int i=0; i<readonly.size(); i++)
			{
				File f = (File) readonly.get(i);
				message.append(f.toString()+"\n");
			}
	
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"storeCpml", message.toString());
			}
			
			throw (new DescriptorException(message.toString()));
		}
		else
		{
			//---Marshall all the descriptors, building a JDOM structure
			root = descriptor.doHybridXmlMarshall();
			root.detach();
			doc = new Document(root);

			//---Clean comments
			XmlUtil.cleanComments(doc);

			//---Save document to file
			if (file != null)
			{
				XmlUtil.saveMultipartDOM(doc, stm, file);
			}
		}
    }

    /**
     * Load Component from XML.
     *
     * @param cmlUrl URL of CML (component) XML file.
     * @return ComponentDescriptor or ComponentSetDescriptor
    **/
    public Descriptor loadComponentElement(URL cmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = 
				"Processing component element descripton XML (w/ shared MML)...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, "loadComponentElement", message);
		}

		checkArgument("loadComponentElement", "cmlUrl", cmlUrl);
		
        return loadCml(cmlUrl, null, BASE);
    }

    /**
     * Load ComponentElement from XML.
     *
     * @param cmlUrl URL of CML (component) XML file.
     * @param mmlUrl  URL of MML (typemap) XML file.
     * @return ComponentDescriptor or ComponentSetDescriptor
    **/
    public Descriptor loadComponentElement(URL cmlUrl, URL mmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing component element descripton XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadComponentElement", message);
		}

		checkArgument("loadComponentElement", "cmlUrl", cmlUrl);
		
        return loadCml(cmlUrl, mmlUrl, BASE);
    }



    /**
     * Load directory from XML. A call to loadGlobalMappingFile() or
     * setGlobalMap() must be made prior to calling this method.
     *
     * @param dmlUrl     URL of DML (directory) XML file.
     * @return DirectoryDescriptor
    **/
    public DirectoryDescriptor loadDirectory(URL dmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = 
				"Processing directory description XML (w/ shared MML)...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, "loadDirectory", message);
		}
		
		checkArgument("loadDirectory", "dmlUrl", dmlUrl);
		
        return loadDml(dmlUrl, null, BASE);
    }

    /**
     * Store the directory descriptor to file as XML.
     *
     * @param dd     DirectoryDescriptor
     * @param file   File to store to
     * @return Document stored to file
    **/
	public Document storeDirectory(DirectoryDescriptor dd, File file) 
		throws DescriptorException
	{
		return storeDml(dd, file);
	}

    /**
     * Load parameter set from XML.
     *
     * @param psmlUrl URL of PSML (parameter set) XML file.
     * @param mmlUrl  URL of MML (typemap) XML file.
     * @return ParameterSetDescriptor
    **/
    public ParameterSetDescriptor loadParameterSet(URL psmlUrl, URL mmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing parameter set descriptor XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadParameterSet", message);
		}

		checkArgument("loadParameterSet", "psmlUrl", psmlUrl);
		
        return loadPsml(psmlUrl, mmlUrl, BASE);
    }

    /**
     * Load parameter set from XML. A call to loadGlobalMappingFile() or
     * setGlobalMap() must be made prior to calling this method.
     *
     * @param psmlUrl     URL of PSML (parameter set) XML file.
     * @return ParameterSetDescriptor
    **/
    public ParameterSetDescriptor loadParameterSet(URL psmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = 
				"Processing parameter set description XML (w/ shared MML)...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, "loadParameterSet", message);
		}

		checkArgument("loadParameterSet", "psmlUrl", psmlUrl);
		
        return loadPsml(psmlUrl, null, BASE);
    }

    /**
     * Load GUI property editor lookup table from XML.
     *
     * @param mmlUrl URL of MML (property editor) XML file.
     * @return LookupTable
    **/
    public LookupTable loadPropertyEditorLookupTable(URL mmlUrl) 
    	throws DescriptorException
    {
		if (sLogger.isLoggable(Level.INFO))
		{
			String message = "Processing GUI Property Editor XML...";
			
			sLogger.logp(Level.INFO, CLASS_NAME, 
				"loadPropertyEditorLookupTable", message);
		}

		checkArgument("loadPropertyEditorLookupTable", "mmlUrl", mmlUrl);
		
        return loadMml(mmlUrl, BASE);
    }

    /**
     * Load MML XML file. Use a custom base location for specifying the schema 
     * location.
     *
     * @param mmlUrl     URL to MML file.
     * @param base       base.
     * @return LookupTable or null if error loading mml xml file.
    **/
	public static LookupTable loadMml(URL mmlUrl, String base) 
		throws DescriptorException
	{
		checkArgument("loadMml", "mmlUrl", mmlUrl);
		DescriptorDirectory directory = new DescriptorDirectory(null);
        Document mmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();

        try
		{
        	mmlDocument = XmlUtil.processXmlFile(mmlUrl, validateEnabled, base);
        	SchemaUtil.checkSchemaFilename(mmlDocument, MML_SCHEMA);
		}
        catch (Exception ex)
		{
			ex.printStackTrace();
        	throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
		Element element = mmlDocument.getRootElement();

		return new LookupTable(null, directory, element);
	}

    /**
     * Load MML XML file. Use a custom base location for specifying the schema location.
     *
     * @param mmlUrl     URL to MML file.
     * @param base       base.
     * @return LookupTable or null if error loading mml xml file.
    **/
	public static LookupTable loadMml(InputStream is, String base) 
		throws DescriptorException
	{
		checkArgument("loadMml", "is", is);
		DescriptorDirectory directory = new DescriptorDirectory(null);
        
        Document mmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();
        
        try
		{
        	mmlDocument = XmlUtil.processXmlFile(is, validateEnabled, base);
        	SchemaUtil.checkSchemaFilename(mmlDocument, MML_SCHEMA);
		}
        catch (Exception ex)
		{
        	throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
		Element element = mmlDocument.getRootElement();

		return new LookupTable(null, directory, element);
	}

    /**
     * Load MML XML file.
     *
     * @param is InputStream for MML file.
     * @return LookupTable or null if error loading mml xml file.
    **/
	public static LookupTable loadMml(InputStream is) throws DescriptorException
	{
		return loadMml(is, BASE);
	}

    /**
     * Load IML XML. If mmlUrl is null, the method uses a MML table loaded
     * via the loadGlobalMappingFile call or one stored in the descriptor
     * library and specified by name via the setGlobalMap call.
     *
     * @param imlUrl  URL of IML file.
     * @param mmlUrl  URL of corresponding MML file.
     * @param base  base.
     * @return DeviceDescriptor
    **/
    public RootDescriptor loadIml(URL imlUrl, URL mmlUrl, String base) 
    	throws DescriptorException
    {
		checkArgument("loadIml", "imlUrl", imlUrl);
        DescriptorDirectory directory = getDirectory(mmlUrl);

        XmlProcessingResult xpr;
        Document imlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();
        
        try
		{
            xpr = XmlUtil.processXmlFileDetailed(
            		imlUrl, validateEnabled, false, base);
            imlDocument = xpr.getDocument();
        	SchemaUtil.checkSchemaFilename(imlDocument, IML_SCHEMA);
		}
        catch (Exception ex)
		{
        	throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
        Element imlElement = imlDocument.getRootElement();
        RootDescriptor rval = null;
        
        if (imlElement.getName().equals(Iml.E_DEVICE_SET))
		{

			rval = new DeviceSetDescriptor(null, directory, imlElement);
			((DeviceSetDescriptor) rval).setSubtreeMap(xpr.getSubtreeMap());
		}
		else
		{
			rval = new DeviceDescriptor(null, directory, imlElement);
			((DeviceDescriptor) rval).setSubtreeMap(xpr.getSubtreeMap());
		}
		
        return rval;
    }

    /**
	 * Save the specified instrument definition to a file (if not null) as IML
	 * and return the document from which the file was created. Callers should
	 * keep in mind that this version of the store method will not preserve the
	 * xinclude file structure. All contents will be written to the file
	 * specified.
	 * 
	 * @param id Instrument descriptor.
	 * @param file File to save XML to.
	 * @return Document
	 */
	public Document storeIml(DeviceDescriptor id, File file)
		throws DescriptorException
    {
		Document doc = null;
		Element root = new Element(Iml.E_DEVICE);

		//---Set the IML schema location
		String schema =
			Irc.getResource(BASE + FILE_SEPARATOR + Iml.V_SCHEMA).toString();
		root.setAttribute(
			new Attribute(
				SchemaUtil.SCHEMA_LOCATION,
				schema,
				XML_NAMESPACE_OBJ));

		//---Marshall all the descriptors, building a JDOM structure
		id.xmlMarshall(root);
		doc = new Document(root);

		//---Save document to file
		if (file != null)
		{
			XmlUtil.dumpDom(doc, file);
		}
		return doc;
    }

	/**
	 * This method will store a top level instrument descriptor as xml to the files
	 * from which it came. If a file is not a top level descriptor an exception
	 * will be thrown. Also if a caller trys to save a descriptor and any of the
	 * files are readonly, an excpetion is thrown. Callers should ensure, in
	 * advance that these things do not happen.
	 *
	 * @param id DeviceDescriptor (top-level) to store
	**/
	public static void storeIml(DeviceDescriptor id)
		throws DescriptorException
    {
		Document doc   = null;
		Element root   = null;
		SubtreeMap stm = id.getSubtreeMap();

		if (stm == null)
		{
			throw (
				new DescriptorException(
					"Attempt to perform top level save on non top level instrument!"));
		}

		URL url            = stm.getRootOrigin();
		File file          = FileUtil.urlToFile(url);
		ArrayList readonly = XmlUtil.getReadonlyFiles(stm, file);

		if (readonly.size() > 0)
		{
			StringBuffer message = new StringBuffer
				("Unable to perform save because readonly files encountered:\n");
			
			for (int i=0; i<readonly.size(); i++)
			{
				File f = (File) readonly.get(i);
				message.append(f.toString()+"\n");
			}
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"storeIml", message.toString());
			}

			throw (new DescriptorException(message.toString()));
		}
		else
		{

			//---Marshall all the descriptors, building a JDOM structure
			root = id.doHybridXmlMarshall();
			root.detach();
			doc  = new Document(root);

			//---Clean comments
			XmlUtil.cleanComments(doc);

			//---Save document to file
			if (file != null)
			{
				XmlUtil.saveMultipartDOM(doc, stm, file);
			}
		}
    }

	/**
	 * Clone an instrument descriptor.
	 *
	 * @param descriptor DeviceDescriptor to clone.
	 * @return DeviceDescriptor cloned from original.
	**/
	public DeviceDescriptor cloneDescriptor(DeviceDescriptor descriptor)
		throws DescriptorException
	{
		DeviceDescriptor rval = null;
		DescriptorDirectory directory = getDirectory(null);
		Element root = descriptor.doHybridXmlMarshall();
		Element rootClone = (Element) root.clone();
		rval = new DeviceDescriptor(null, directory, rootClone);
		rval.setSubtreeMap(
			XmlUtil.getSubtreeMapForClone(
				descriptor.getElement(),
				rval.getElement(),
				descriptor.getSubtreeMap()));
				
		return rval;
	}

	/**
	 * Clone a script group descriptor.
	 *
	 * @param descriptor ScriptGroupDescriptor to clone.
	 * @return ScriptGroupDescriptor cloned from original.
	**/
	public ScriptGroupDescriptor cloneDescriptor(ScriptGroupDescriptor descriptor)
		throws DescriptorException
	{
		ScriptGroupDescriptor rval = null;
		DescriptorDirectory directory = getDirectory(null);
		Element root = descriptor.doHybridXmlMarshall();
		Element rootClone = (Element) root.clone();
		rval = new ScriptGroupDescriptor(null, directory, rootClone);
		rval.setSubtreeMap(
			XmlUtil.getSubtreeMapForClone(
				descriptor.getElement(),
				rval.getElement(),
				descriptor.getSubtreeMap()));
				
		return rval;
	}

    /**
     * Load CPML XML. If mmlUrl is null, the method uses a MML table loaded
     * via the loadGlobalMappingFile call or one stored in the descriptor
     * library and specified by name via the setGlobalMap call.
     *
     * @param cpmlUrl    URL of CPML XML file.
     * @param mmlUrl     URL of MML XML file.
     * @param base       base.
     * @return ScriptGroupDescriptor
    **/
	public ScriptGroupDescriptor loadCpml(
		URL cpmlUrl,
		URL mmlUrl,
		String base)
		throws DescriptorException
	{
		checkArgument("loadCpml", "cpmlUrl", cpmlUrl);
		DescriptorDirectory directory = getDirectory(mmlUrl);

		XmlProcessingResult xpr;
		Document cpmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();

		try
		{
			xpr =
				XmlUtil.processXmlFileDetailed(cpmlUrl, validateEnabled, false, base);
			cpmlDocument = xpr.getDocument();
			SchemaUtil.checkSchemaFilename(cpmlDocument, CPML_SCHEMA);
		}
		catch (Exception ex)
		{
			throw (
				new DescriptorException("Encountered XML Document exception", ex));
		}

		Element cpmlElement = cpmlDocument.getRootElement();
		ScriptGroupDescriptor cpgd =
			new ScriptGroupDescriptor(null, directory, cpmlElement);
		cpgd.setSubtreeMap(xpr.getSubtreeMap());
		
		return cpgd;
	}

    /**
     * Load CML XML. If mmlUrl is null, the method uses a MML table loaded
     * via the loadGlobalMappingFile call or one stored in the descriptor
     * library and specified by name via the setGlobalMap call.
     *
     * @param pamlUrl    URL of PAML XML file.
     * @param mmlUrl     URL of MML XML file.
     * @param base       base.
     * @return ComponentDescriptor
    **/
	public Descriptor loadCml(URL cmlUrl, URL mmlUrl, String base)
		throws DescriptorException
    {
		checkArgument("loadCml", "cmlUrl", cmlUrl);
        DescriptorDirectory directory = getDirectory(mmlUrl);
        
        Document cmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();
        
        try
		{
    		cmlDocument = XmlUtil.processXmlFile(cmlUrl, validateEnabled, base);
    		SchemaUtil.checkSchemaFilename(cmlDocument, CML_SCHEMA);
		}
        catch (Exception ex)
		{
    		throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
        Element cmlElement = cmlDocument.getRootElement();
        Descriptor rval = null;

        if (cmlElement.getName().equals(Cml.E_COMPONENT_SET))
		{
			rval = new ComponentSetDescriptor(null, directory, cmlElement);
		}
		else
		{
			rval = new ComponentDescriptor(null, directory, cmlElement);
		}
        
        return rval;
	}

    /**
     * Load MML XML file for a stream. Use a custom base location for specifying 
     * the schema location. This version of the call is useful for getting 
     * ComponentDescriptors from a jar.
     *
     * @param mmlUrl     URL to MML file.
     * @return LookupTable or null if error loading mml xml file.
    **/
	public Descriptor loadCml(InputStream is, LookupTable lut)
		throws DescriptorException
	{
		checkArgument("loadCml", "is", is);
		DescriptorDirectory directory = new DescriptorDirectory(lut);
        
        Document cmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();
        
        try
		{
    		cmlDocument = XmlUtil.processXmlFile(is, validateEnabled, BASE);
    		SchemaUtil.checkSchemaFilename(cmlDocument, CML_SCHEMA);
		}
        catch (Exception ex)
		{
    		throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
		Element cmlElement = cmlDocument.getRootElement();
        Descriptor rval = null;

        if (cmlElement.getName().equals(Cml.E_COMPONENT_SET))
		{
			rval = new ComponentSetDescriptor(null, directory, cmlElement);
		}
		else
		{
			rval = new ComponentDescriptor(null, directory, cmlElement);
		}
        
        return rval;
	}

    /**
	 * Loads a DataML file. If mmlUrl is null, the method uses a MML table
	 * loaded via the loadGlobalMappingFile call or one stored in the descriptor
	 * library and specified by name via the setGlobalMap call.
	 * 
	 * @param datamlUrl URL of DML XML file.
	 * @param mmlUrl URL of MML XML file.
	 * @param base base.
	 * @return DirectoryDescriptor
	 */
	public DataSpaceDescriptor loadDataml(URL datamlUrl, URL mmlUrl, String base)
			throws DescriptorException
	{
		checkArgument("loadDataml", "datamlUrl", datamlUrl);
		DescriptorDirectory directory = getDirectory(mmlUrl);

		Document datamlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();

		try
		{
			datamlDocument = 
				XmlUtil.processXmlFile(datamlUrl, validateEnabled, base);
			SchemaUtil.checkSchemaFilename(datamlDocument, DATAML_SCHEMA);
		}
		catch (Exception ex)
		{
			throw (new DescriptorException(
					"Encountered XML Document exception", ex));
		}

		Element datamlElement = datamlDocument.getRootElement();
		DataSpaceDescriptor descriptor = new DataSpaceDescriptor(null, directory,
				datamlElement);

		return (descriptor);
	}

//    /**
//     * Load DSML XML. If mmlUrl is null, the method uses a MML table loaded
//     * via the loadGlobalMappingFile call or one stored in the descriptor
//     * library and specified by name via the setGlobalMap call.
//     *
//     * @param dsmlUrl    URL of DSML XML file.
//     * @param mmlUrl     URL of MML XML file.
//     * @param base       URL schema base.
//     * @return DataSequenceDescriptor
//    **/
//    public DataSequenceDescriptor loadDsml(URL dsmlUrl, URL mmlUrl, URL base) throws DescriptorException
//    {
//		checkArgument("loadDsml", "dsmlUrl", dsmlUrl);
//        DescriptorDirectory directory       = getDirectory(mmlUrl);
//        
//        Document dsmlDocument;
//        
//        try
//		{
//        		dsmlDocument = XmlUtil.processXmlFile(dsmlUrl, base);
//        		SchemaUtil.checkSchemaFilename(dsmlDocument, DSML_SCHEMA);
//		}
//        catch (Exception ex)
//		{
//        		throw (new DescriptorException("Encountered XML Document exception", ex));
//		}
//        
//        Element element                     = dsmlDocument.getRootElement();
//        DataSequenceDescriptor descriptor = new DataSequenceDescriptor(null, directory, element);
//        return descriptor;
//    }
//
//    /**
//     * Load DMML XML. If mmlUrl is null, the method uses a MML table loaded
//     * via the loadGlobalMappingFile call or one stored in the descriptor
//     * library and specified by name via the setGlobalMap call.
//     *
//     * @param dmmlUrl    URL of DMML XML file.
//     * @param mmlUrl     URL of MML XML file.
//     * @param base       URL schema base.
//     * @return DataSequenceDescriptor
//    **/
//    public DataMapDescriptor loadDmml(URL dmmlUrl, URL mmlUrl, URL base) throws DescriptorException
//    {
//		checkArgument("loadDmml", "dmmlUrl", dmmlUrl);
//        DescriptorDirectory directory       = getDirectory(mmlUrl);
//        
//        Document dmmlDocument;
//        
//        try
//		{
//        		dmmlDocument = XmlUtil.processXmlFile(dmmlUrl, base);
//        		SchemaUtil.checkSchemaFilename(dmmlDocument, DMML_SCHEMA);
//		}
//        catch (Exception ex)
//		{
//        		throw (new DescriptorException("Encountered XML Document exception", ex));
//		}
//        
//        Element element                     = dmmlDocument.getRootElement();
//        DataMapDescriptor descriptor        = new DataMapDescriptor(null, directory, element);
//        return descriptor;
//    }
//
//    /**
//     * Load PDML XML. If mmlUrl is null, the method uses a MML table loaded
//     * via the loadGlobalMappingFile call or one stored in the descriptor
//     * library and specified by name via the setGlobalMap call.
//     *
//     * @param pdmlUrl    URL of PDML XML file.
//     * @param mmlUrl     URL of MML XML file.
//     * @param base       URL schema base.
//     * @return ParseDefDescriptor
//    **/
//    public ParseDefDescriptor loadPdml(URL pdmlUrl, URL mmlUrl, URL base) throws DescriptorException
//    {
//		checkArgument("loadPdml", "pdmlUrl", pdmlUrl);
//        DescriptorDirectory directory       = getDirectory(mmlUrl);
//        
//        Document pdmlDocument;
//        
//        try
//		{
//        		pdmlDocument = XmlUtil.processXmlFile(pdmlUrl, base);
//        		SchemaUtil.checkSchemaFilename(pdmlDocument, PDML_SCHEMA);
//		}
//        catch (Exception ex)
//		{
//        		throw (new DescriptorException("Encountered XML Document exception", ex));
//		}
//        
//        Element element                     = pdmlDocument.getRootElement();
//        ParseDefDescriptor descriptor       = new ParseDefDescriptor(null, directory, element);
//        return descriptor;
//    }

    /**
     * Load DML XML. If mmlUrl is null, the method uses a MML table loaded
     * via the loadGlobalMappingFile call or one stored in the descriptor
     * library and specified by name via the setGlobalMap call.
     *
     * @param dmlUrl    URL of DML XML file.
     * @param mmlUrl    URL of MML XML file.
     * @param base     base.
     * @return DirectoryDescriptor
    **/
    public DirectoryDescriptor loadDml(URL dmlUrl, URL mmlUrl, String base) 
    		throws DescriptorException
    {
 		checkArgument("loadDml", "dmlUrl", dmlUrl);
        DescriptorDirectory directory = getDirectory(mmlUrl);
        
        Document dmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();

        try
		{
    		dmlDocument = XmlUtil.processXmlFile(dmlUrl, validateEnabled, base);
    		SchemaUtil.checkSchemaFilename(dmlDocument, DML_SCHEMA);
		}
        catch (Exception ex)
		{
    		throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
        Element dmlElement 	= dmlDocument.getRootElement();
        DirectoryDescriptor dd = 
        	new DirectoryDescriptor(null, directory, dmlElement);
        return dd;
    }

    /**
     * Save the specified directory description to a file (if not null) as DML
     * and return the document from which the file was created.
     *
     * @param dd	Directory Description.
     * @param file	File to save XML to.
     * @return Document
    **/
	public Document storeDml(DirectoryDescriptor dd, File file)
		throws DescriptorException
    {
		Document doc = null;
		Element root = new Element(Dml.E_DIRECTORY);

		//---Set the DML schema location
		root.setAttribute(
			new Attribute(SchemaUtil.SCHEMA_LOCATION, Dml.V_SCHEMA, XML_NAMESPACE_OBJ));

		//---Marshall all the descriptors, building a JDOM structure
		dd.xmlMarshall(root);
		doc = new Document(root);

		//---Save document to file
		if (file != null)
		{
			XmlUtil.dumpDom(doc, file);
		}
		return doc;
    }

    /**
     * Load PSML XML. If mmlUrl is null, the method uses a MML table loaded
     * via the loadGlobalMappingFile call or one stored in the descriptor
     * library and specified by name via the setGlobalMap call.
     *
     * @param psmlUrl    URL of PCML XML file.
     * @param mmlUrl     URL of MML XML file.
     * @param base       base.
     * @return ParameterSetDescriptor
    **/
	public ParameterSetDescriptor loadPsml(URL psmlUrl, URL mmlUrl, String base)
		throws DescriptorException
    {
		checkArgument("loadPsml", "psmlUrl", psmlUrl);
        DescriptorDirectory directory = getDirectory(mmlUrl);
        
        Document psmlDocument;
		boolean validateEnabled = Boolean.valueOf(
				Irc.getPreference(IrcPrefKeys.XML_VALIDATE_ENABLED))
				.booleanValue();

        try
		{
        	psmlDocument = XmlUtil.processXmlFile(psmlUrl, validateEnabled, base);
        	SchemaUtil.checkSchemaFilename(psmlDocument, PSML_SCHEMA);
		}
        catch (Exception ex)
		{
        	throw (new DescriptorException("Encountered XML Document exception", ex));
		}
        
        Element psmlElement = psmlDocument.getRootElement();

        return new ParameterSetDescriptor(null, directory, psmlElement);
    }

    /**
     * Get a directory for constructing a descriptor tree. If mmlUrl is null, the
     * method uses a MML table loaded via the loadGlobalMappingFile call or one
     * stored in the descriptor library and specified by name via the setGlobalMap
     * call. Note, it is important that the same directory be used for the entire
     * descriptor tree. Seperate trees should each have their own directory.
     *
     * @param mmlUrl     URL of MML XML file.
     * @return Descriptory Directory.
     **/
	public DescriptorDirectory getDirectory(URL mmlUrl)
		throws DescriptorException
	{
    	DescriptorDirectory directory = null;

    	if (mmlUrl != null)
    	{
    		//--- Load the specified MML file
    		directory = new DescriptorDirectory(loadMml(mmlUrl, BASE));
    	}
    	else
    	{
    		//--- Or if null, use the previously loaded MML file
    		if (fGlobalLookupTable == null)
    		{
    			//--- Complain if NO lookup table has been previously loaded for global use
    			throw (new DescriptorException("Global lookup table has not been set!"));
    		}
    		directory = new DescriptorDirectory(fGlobalLookupTable);
    	}
    	return directory;
    }

	/**
	 * Check argument to identify problems.
	 *
	 * @param methodName Name of method.
	 * @param argumentName Name of argument.
	 * @param value Value of argument.
	**/
	private static void checkArgument(
		String methodName,
		String argumentName,
		Object value)
		throws DescriptorException
	{
		if (value == null)
		{
			DescriptorException e =
				new DescriptorException(
					"argument: "
						+ argumentName
						+ " of method: "
						+ methodName
						+ " can NOT be null!");
			e.fillInStackTrace();
			throw(e);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorFramework.java,v $
//  Revision 1.28  2006/02/14 20:05:00  tames
//  Commented out logging of log level warning since this causes a recursive
//  stack overflow.
//
//  Revision 1.27  2006/01/23 17:59:52  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.26  2006/01/13 03:19:00  tames
//  Added initial support for Component set descriptions.
//
//  Revision 1.25  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.24  2005/08/19 15:25:48  tames_cvs
//  Updated to reflect method signature change in XmlUtil.
//
//  Revision 1.23  2005/07/19 17:57:15  tames_cvs
//  Change to log message only.
//
//  Revision 1.22  2005/07/12 17:23:32  tames
//  Added support for default classes if no typemap file was read.
//
//  Revision 1.21  2005/05/23 15:24:06  tames_cvs
//  Updated to support Xerces parser and validation.
//
//  Revision 1.20  2004/12/03 13:28:00  smaher_cvs
//  Added support for instantiating a singleton in SysMgr.instantiateClass().
//
//  Revision 1.19  2004/10/14 21:16:05  tames_cvs
//  Relocated the method to instantiate a class from the global type map to
//  the DescriptorFramework class.
//
//  Revision 1.18  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.17  2004/10/07 21:38:27  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.16  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.15  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.14  2004/09/10 14:49:03  chostetter_cvs
//  More data description work
//
//  Revision 1.13  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.12  2004/09/04 13:26:07  tames
//  *** empty log message ***
//
//  Revision 1.11  2004/07/19 21:17:49  tames_cvs
//  *** empty log message ***
//
//  Revision 1.9  2004/06/30 20:41:55  tames_cvs
//  Changed references of command procedures to scripts
//
//  Revision 1.8  2004/06/07 14:16:29  tames_cvs
//  Added findInGlobalMap method
//
//  Revision 1.7  2004/06/04 14:25:34  tames_cvs
//  DescriptorLibrary is no longer a singleton.
//
//  Revision 1.5  2004/06/01 16:00:16  tames_cvs
//  Temp debug code only
//
//  Revision 1.3  2004/05/28 22:04:06  tames_cvs
//  added IrcManager logic and bootup implementation
//
//  Revision 1.2  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.1  2004/05/14 20:06:40  chostetter_cvs
//  Moved from description package, as this class "knows" a great deal about IRC as a whole
//
