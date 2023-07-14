//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui;

import java.awt.Container;
import java.beans.PropertyEditorManager;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.swixml.Converter;
import org.swixml.ConverterLibrary;
import org.swixml.Factory;
import org.swixml.SwingEngine;

import gov.nasa.gsfc.irc.app.DescriptorFramework;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.xml.LookupTable;
import gov.nasa.gsfc.irc.description.xml.NamespaceTable;

/**
 * @version	$Date: 2006/03/13 05:16:18 $
 * @author T. Ames
 */
public class DefaultGuiFactory implements GuiFactory
{
	private static final String CLASS_NAME = DefaultGuiFactory.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private SwingEngine fXulEngine = new SwingEngine(this);
	private NamespaceTable fTagTable = null;
	private NamespaceTable fConverterTable = null;
	private NamespaceTable fPropertyTable = null;
	
	/**
	 * Name of NamespaceTable in the global map for custom GUI tags.
	 */
	public String TAG_NAMESPACE = "GuiTagType";

	/**
	 * Name of NamespaceTable in the global map for custom type converters.
	 */
	public String CONVERTER_NAMESPACE = "GuiConverterType";

	/**
	 * Name of NamespaceTable in the global map for custom property editors.
	 */
	public String PROPERTY_EDITOR_NAMESPACE = "PropertyEditors";

	public DefaultGuiFactory()
	{
		ClassLoader loader = Irc.getDefaultLoader();
		fXulEngine.setClassLoader(loader);

		DescriptorFramework desciptorFramework = Irc.getDescriptorFramework();
		LookupTable table = desciptorFramework.getGlobalMap();
		
		// Register custom components
		registerComponents();
		
		// Register custom type converters
		fConverterTable = table.getNamespaceTable(CONVERTER_NAMESPACE);
		registerConverters(fConverterTable);
		
		// Register custom tags
		fTagTable = table.getNamespaceTable(TAG_NAMESPACE);
		registerTags(fXulEngine, fTagTable);

		// Register property editors
		fPropertyTable = table.getNamespaceTable(PROPERTY_EDITOR_NAMESPACE);
		registerPropertyEditors(fPropertyTable);
	}
	
	/**
	 * Renders the specified GUI description. To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param url <code>URL</code> url pointing to an XML descriptor
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(URL url) throws Exception
	{
		Container mainContainer = fXulEngine.render(url);
		
		return mainContainer;
	}

	/**
	 * Renders the specified GUI description. To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param resource resource pointing to an XML descriptor
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(String resource) throws Exception
	{
		Container mainContainer = fXulEngine.render(resource);
		
		return mainContainer;
	}

	/**
	 * Renders the specified GUI description passing a client to the
	 * builder. See SwixML documentation for the role of a client. 
	 * To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param url <code>URL</code> url pointing to an XML descriptor
	 * @param client <code>Object</code> owner of this instance
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(URL url, Object client) 
		throws Exception
	{
		SwingEngine engine = new SwingEngine(client);
		registerConverters(fConverterTable);
		registerTags(engine, fTagTable);
		Container mainContainer = engine.render(url);
		
		return mainContainer;
	}

	/**
	 * Renders the specified GUI description passing a client to the
	 * builder. See SwixML documentation for the role of a client.
	 * To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param resource resource pointing to an XML descriptor
	 * @param client <code>Object</code> owner of this instance
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(String resource, Object client) 
		throws Exception
	{
		SwingEngine engine = new SwingEngine(client);
		registerConverters(fConverterTable);
		registerTags(engine, fTagTable);
		Container mainContainer = engine.render(resource);
		
		return mainContainer;
	}
	
	/**
	 * Registers the classes associated with custom tags from the 
	 * global map.
	 * 
	 * @param engine the SwingEngine to register the tags with
	 * @param tagTable the table containing custom tag definitions
	 */
	protected void registerTags(SwingEngine engine, NamespaceTable tagTable)
	{
		ClassLoader loader = Irc.getDefaultLoader();
		
		if (tagTable != null)
		{
			Iterator keys = tagTable.getValueNames();
			
			while (keys.hasNext())
			{
				String tag = (String) keys.next();
				String className = (String) tagTable.getValueByName(tag);
				try
				{
					Class tagClass = loader.loadClass(className);
					// Register tag
					System.out.println("$$ Registering tag:" + tag + " for "
							+ tagClass);
					
					// Determine if class is a template or factory
					if (Factory.class.isAssignableFrom(tagClass))
					{
						// Register class Factory instance
						System.out.println("$$ Registering tag:" + tag + " for Factory "
							+ tagClass);
						Factory factory = (Factory) tagClass.newInstance();
						engine.getTaglib().registerTag(tag, factory);						
					}
					else
					{
						// Register class template
						engine.getTaglib().registerTag(tag, tagClass);
					}
				}
				catch (ClassNotFoundException e)
				{
					String message = 
						"Could not register class for tag " + tag;

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"registerTags", message);
				}
				catch (InstantiationException e)
				{
					String message = 
						"Could not instantiate Factory class " + className;

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"registerTags", message, e);
				}
				catch (IllegalAccessException e)
				{
					String message = 
						"Could not instantiate Factory class " + className;

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"registerTags", message, e);
				}
			}
		}
	}

	/**
	 * Registers the classes associated with custom type converters from the 
	 * global map.
	 * 
	 * @param table the table containing custom converter definitions
	 */
	protected void registerConverters(NamespaceTable table)
	{
		ClassLoader loader = Irc.getDefaultLoader();
		
		if (table != null)
		{
			Iterator types = table.getValueNames();
			
			while (types.hasNext())
			{
				String type = (String) types.next();
				String converterName = (String) table.getValueByName(type);
				try
				{
					Class typeClass = loader.loadClass(type);
					Converter converterInstance = 
						(Converter) Irc.instantiateClass(converterName);
					// Register converter
					ConverterLibrary.getInstance().register(typeClass, converterInstance);
				}
				catch (ClassNotFoundException e)
				{
					String message = 
						"Could not find class " + type + " or " + converterName;

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"registerConverters", message, e);
				}
				catch (Exception e)
				{
					String message = 
						"Could not instantiate class " + converterName;

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"registerConverters", message, e);
				}
			}
		}
	}
	
	/**
	 * Registers custom components with the Swing UIManager.
	 */
    protected void registerComponents()
    {
    	// TODO this needs to check what the current look and feel is and 
    	// not assume it is metal.
		UIManager.put("AxisScrollBarUI", 
			"gov.nasa.gsfc.irc.gui.swing.plaf.metal.MetalAxisScrollBarUI");
    }
    
	/**
	 * Registers the property editor classes associated with types from the 
	 * global map.
	 * 
	 * @param table the table containing custom converter definitions
	 */
    protected void registerPropertyEditors(NamespaceTable table)
    {
		if (table != null)
		{
			Iterator types = table.getValueNames();
			
			while (types.hasNext())
			{
				String type = (String) types.next();
				String editor = (String) table.getValueByName(type);
				
				try
				{
					Class typeClass = Irc.loadClass(type);
					Class editorClass = Irc.loadClass(editor);
					
					// Register property editor
					PropertyEditorManager.registerEditor(typeClass, editorClass);
				}
				catch (ClassNotFoundException e)
				{
					String message = 
						"Could not register editor " + editor 
						+ " for type " + type;

					sLogger.logp(Level.WARNING, CLASS_NAME, 
							"registerPropertyEditors", message, e);
				}
			}
		}
    }
    
	/**
	 * Returns the Object with the given id or null.
	 * 
	 * @param id <code>String</code> assigned name
	 * @return the Object with the given name or null if not found.
	 */
	public Object find(String id)
	{
		return fXulEngine.getIdMap().get(id);
	}

	/**
	 * Returns the root component of the generated Swing UI.
	 * 
	 * @return the root component of the javax.swing ui
	 */
	public Container getRootComponent()
	{
		return fXulEngine.getRootComponent();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultGuiFactory.java,v $
//  Revision 1.7  2006/03/13 05:16:18  tames
//  Removed System.out.println debug statement.
//
//  Revision 1.6  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/12/15 15:27:58  tames
//  Changed return type of the find method to allow finding any id Object not
//  just Components.
//
//  Revision 1.4  2005/10/28 18:43:30  tames
//  Changed the log message when a ClassCastException results from registering
//  tags.
//
//  Revision 1.3  2005/10/11 02:53:55  tames
//  Added registerComponents method.
//
//  Revision 1.2  2005/05/23 20:00:38  tames_cvs
//  Update to reflect the relocation of the getDefaultLoader method.
//
//  Revision 1.1  2005/01/10 23:09:09  tames_cvs
//  Renamed GuiBuilder to GuiFactory to be consistent with other factory
//  names in the IRC framework.
//
//  Revision 1.9  2005/01/07 20:51:19  tames
//  Added capability to register property editors given in TypeMap.
//
//  Revision 1.8  2004/11/08 23:05:33  tames
//  Modified registerTag method to handle Factories as well as templates.
//
//  Revision 1.7  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.6  2004/08/31 22:03:01  tames
//  Added find method
//
//  Revision 1.5  2004/08/29 22:45:40  tames
//  Added getRootComponent method
//
//  Revision 1.4  2004/08/26 14:36:08  tames
//  Render no longer sets the frame visible
//
//  Revision 1.3  2004/08/23 13:58:52  tames
//  Changed api and added support for defining custom tags from the
//  global map.
//
//  Revision 1.2  2004/06/08 19:07:16  tames_cvs
//  now displays gui passed to it
//
//  Revision 1.1  2004/06/08 14:18:26  tames_cvs
//  Corrected class name
//
//  Revision 1.1  2004/06/07 14:36:38  tames_cvs
//  Initial Version
//
