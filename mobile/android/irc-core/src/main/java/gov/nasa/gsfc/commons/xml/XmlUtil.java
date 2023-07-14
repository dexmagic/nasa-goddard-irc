//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC)project.
//
//--- Notes ------------------------------------------------------------------
//  Development notes are located at the end of the file.
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

package gov.nasa.gsfc.commons.xml;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.commons.system.resources.ResourceManager;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;


/**
 * This class contains numerous XML utility methods.  
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	 $Date: 2006/05/17 21:09:28 $
 * @author John Higinbotham	
 * @author Troy Ames
**/
public class XmlUtil 
{
	private static final String CLASS_NAME = XmlUtil.class.getName();	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final ResourceManager sResourceManager = Sys.getResourceManager();
	
	/**
	 * Process xml file to produce Document. 
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param validation validation enabled
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(URL xmlFileUrl, boolean validation) 
		throws XmlException
	{
		return processXmlFile(xmlFileUrl, validation, null);
	}

	/**
	 * Process xml file to produce Document. By default, validation is on, 
	 * and schema include processing is off.
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(URL xmlFileUrl) 
		throws XmlException
	{
		return processXmlFile(xmlFileUrl, true, null);
	}

	/**
	 * Process xml file to produce Document. By default, validaiton is on, 
	 * schema include processing is off, and xinclude processing is on.<P>
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param base Base to use for resolving relative URLs
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(URL xmlFileUrl, String base) 
		throws XmlException
	{
		return processXmlFile(xmlFileUrl, true, base);
	}

	/**
	 * Process xml file to produce Document.
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param validation validation enabled
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(InputStream is, boolean validation) 
		throws XmlException
	{
		return processXmlFile(is, validation, null);
	}

	/**
	 * Process xml file to produce Document. Note that validation is on.
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param base Base to use for resolving relative URLs
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(InputStream is, String base) 
		throws XmlException
	{
		return processXmlFile(is, true, base);
	}

	/**
	 * Process xml file to produce Document.
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param validation Should validation be performed?
	 * @param base Base to use for resolving relative URLs
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(
		InputStream is, boolean validation, String base) 
		throws XmlException
	{
		Document rval     = null;
		String baseString = null;

		//---Setup base string
		if (base != null)
		{
			baseString = base.toString();
		}
		try
		{
			//---Parse/Validate XML and build JDOM Document
			XmlParser p = new XmlParser(validation);
			rval        = p.parse(is, baseString);
		}
		catch (Exception e)
		{
			throw (new XmlException(e.getMessage()));
		}
		return rval;
	}

	/**
	 * Process xml file to produce Document. By default, schema include
	 * processing is off.<P>
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param validation Should validation be performed?
	 * @param base Base to use for resolving relative URLs
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(
			URL xmlFileUrl, boolean validation, String base) 
		throws XmlException
	{
		return processXmlFile(xmlFileUrl, validation, false, base);
	}

	/**
	 * Process xml file to produce Document. <P>
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param validation Should validation be performed?
	 * @param schemaInclude If true, handle schema file include tag
	 * @param base Base to use for resolving relative URLs
	 * @return JDOM document generated from specified XML file.
	 **/
	public static Document processXmlFile(URL xmlFileUrl, boolean validation, 
		boolean schemaInclude, String base) 
		throws XmlException
	{
		XmlProcessingResult rval = 
			processXmlFileDetailed(
					xmlFileUrl, validation, schemaInclude, base);
		return rval.getDocument();
	}

	/**
	 * Process xml file to produce Document. <P>
	 *
	 * @param xmlFileUrl Filename of the input XML file to be processed
	 * @param validation Should validation be performed?
	 * @param schemaInclude If true, handle schema file include tag
	 * @param base Base to use for resolving relative URLs
	 * @return XmlProcessingResult
	 **/
	public static XmlProcessingResult processXmlFileDetailed(URL xmlFileUrl, 
		boolean validation, boolean schemaInclude, String base) 
		throws XmlException
	{
		XmlProcessingResult rval = new XmlProcessingResult();
		Document tmp             = null;
		String baseString = null;

		//---Setup base string
		if (base != null)
		{
			baseString = base.toString();
		}

		try
		{
			//---Parse/Validate XML and build JDOM Document
			XmlParser p = new XmlParser(validation);
			Document d = p.parse(xmlFileUrl, baseString);
			rval.setDocument(d);
			rval.setSubtreeMap(p.getSubtreeMap());

			if (schemaInclude)
			{
				Element root       = rval.getDocument().getRootElement();
				List children      = root.getChildren();
				Object[] childAry  = children.toArray();
				Element child      = null;
				URL newurl         = null;
				String newfilename = null;

				for (int i=0; i<childAry.length; i++)
				{
					child = (Element) childAry[i];
					if (child.getName().compareTo(SchemaUtil.SCHEMA_INCLUDE) == 0)
					{
						//---Remove the include element from the parent
						child.detach();

						//---Determine file to load
						newfilename = child.getAttributeValue(SchemaUtil.SCHEMA_LOC_ATT);
						System.out.println("processXmlFileDetailed newfilename:" + newfilename);
						newurl = FileUtil.changeFilename(xmlFileUrl, newfilename);

						//---Load file specified by include
						tmp = processXmlFile(newurl, validation, schemaInclude, base);

						//---Fuse in the sub tree to main tree
						Element subelement = tmp.getRootElement();
						List subchildlist = subelement.getChildren();
						Object[] subchildary = subchildlist.toArray();
						Element subchild = null;
						for (int j=0; j<subchildary.length; j++)
						{
							subchild = (Element) subchildary[j];
							subchild.detach();
							root.addContent(subchild);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw (new XmlException(e.getMessage()));
		}

		return rval;
	}

	/**
	 * Dump the DOM structure to standard out as XML for a given URL.
	 * This method is used as a debug tool to make sure the 3rd party
	 * xml software parses correctly.
	 *
	 * @param Url of document to parse and dump.
	 **/
	public static void dumpDom(URL url)
	{
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		try
		{
			Document doc = processXmlFile(url);
			xmlout.output(doc, System.out);
			System.out.println("\n");
		}
		catch (Exception e)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not output DOM as XML!";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"dumpDom", message);
			}
		}
	}

	/**
	 * Dump the DOM structure to standard out as XML for Document.
	 * This method is used as a debug tool to make sure a JDOM
	 * structure is built as expected.
	 *
	 * @param doc Document to dump.
	 **/
	public static void dumpDom(Document doc)
	{
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		try
		{
			xmlout.output(doc, System.out);
			System.out.println("\n");
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not output DOM as XML!";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"dumpDom", message, ex);
			}
		}
	}

	/**
	 * Dump the DOM structure to standard out as XML.
	 * This method is used as a debug tool to make sure a JDOM
	 * structure is built as expected.
	 *
	 * @param element Element
	 **/
	public static void dumpDom(Element element)
	{
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		try
		{
			xmlout.output(element, System.out);
			System.out.println("\n");
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not output DOM as XML!";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"dumpDom", message, ex);
			}
		}
	}

	/**
	 * Converts the DOM structure to a String
	 *
	 * @param elementOrDocument JDOM Element or Document
	 * @param format the JDOM format to use.  If null, <code>Format.getPrettyFormat()</code> will be used.
	 * @return String XML representation of the DOM structure
	 **/
	public static String domToString(Object elementOrDocument, Format format)
	{

		if (format == null)
		{
			format = Format.getPrettyFormat();
		}
		XMLOutputter xmlout = new XMLOutputter(format);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			if (elementOrDocument instanceof Element)
			{
				xmlout.output((Element)elementOrDocument, baos);	
			}			
			else if (elementOrDocument instanceof Document)
			{
				xmlout.output((Document)elementOrDocument, baos);	
			}	
			else
			{
				throw new IllegalArgumentException("Argument must be JDom Document or Element");
			}
		}
		catch (IOException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not output DOM as XML!";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"domToString", message, ex);
			}
		}
		return baos.toString();
	}

	/**
	 * Dump the DOM structure of the document to a file as XML.
	 * This method can be used to support serialization of descriptors.
	 *
	 * @param doc	Document to dump.
	 * @param file	File to which XML should be written.
	 **/
	public static void dumpDom(Document doc, File file)
	{
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			xmlout.output(doc, fos);
			fos.close();
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not save DOM as XML!";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"dumpDom", message, ex);
			}
		}
	}

	/**
	 * This method allows a caller to check that all the potential files written as  
	 * part of a saveMultipartDOM call are really writeable. If all is good, an empty 
	 * ArrayList is returned, otherwise, the ArrayList contains File entries for those 
	 * files that are readonly. The resulting information should be displayed to the
	 * user so that they can fix any problems prior to saving a multi part document.
	 * 
	 * @param subtreeMap SubtreeMap containing mapping from Elements to URL of source files
	 * @param file File to save to  
	 * @return ArrayList collection of File objects that are readonly 
	**/
	public static ArrayList getReadonlyFiles(SubtreeMap subtreeMap, File file)
	{
		ArrayList rval = new ArrayList();
		URL url        = null;

		if (subtreeMap != null)
		{
			Iterator i = subtreeMap.getSubtreeElements();
			while (i.hasNext())
			{
				url = (URL) subtreeMap.get((Element) i.next());
				addReadonlyFile(FileUtil.urlToFile(url), rval);
			}
		}
		addReadonlyFile(file, rval);
		return rval;
	}

	/**
	 * Check file to see if it is readonly. If so, add an error message to the
	 * provided list.
	 *
	 * @param file File to check for readonly state 
	 * @param list ArrayList to add file to 
	**/
	private static void addReadonlyFile(File file, ArrayList list)
	{
		if (file != null)
		{
			if (file.exists() && !file.canWrite())
			{
				list.add(file);
			}
		}
	}

	/**
	 * This method allows a multipart DOM to be saved to appropriate files. During the 
	 * building phase, a subtree map should have been obtained. It will be used to 
	 * determine the correct ordering to save off and detach subtrees. It is important
	 * to note that readonly files will not be overwritten. A caller should thus call 
	 * the getReadonlyFiles method prior to calling this method. If caller attempts 
	 * to save a multipart DOM, only those files that are writeable will be updated.
	 * <P>
	 * For each subtree (including the root), if a false Boolean value is
	 * associated with the element in the SubtreeMap (via setClientValue()),
	 * then that subtree will be skipped. This will not affect the other subtrees.
	 * 
	 * @param document Document to save off
	 * @param subtreeMap SubtreeMap containing mapping from Elements to URL of source files
	 * @param file File to save to  
	**/
	public static void saveMultipartDOM(Document document, SubtreeMap subtreeMap, 
		File file)
	{
		ArrayList bfList = null;
		Element root     = null;
		Element child    = null;
		Element include  = null;
		URL url          = null;
		Document subdoc  = null;

		//---Build support for traversing original document 
		root   = document.getRootElement();
		bfList = getBFOrdering(root);

		//---Build support for working with a cloned version of the original
		Document documentClone = (Document) document.clone();
		Element  rootClone     = documentClone.getRootElement();
		ArrayList bfListClone  = getBFOrdering(rootClone);
		Element parentClone    = null;
		Element childClone     = null;
		//subtreeMap.dump();
	
		//---Step through the list in reverse (bottom up) and process sub trees
		for (int j=bfList.size()-1 ; j>=0; j--)
		{
			child = (Element) bfList.get(j);

			if (subtreeMap.containsElement(child))
			{
				//---Get info from map
				url = (URL) subtreeMap.get(child);

				//---Setup the xinlude in the parent
				parentClone = (Element) bfListClone.get(bfList.indexOf(child.getParent())); 
				include = new Element(Xinclude.E_INCLUDE, Xinclude.NAMESPACE);
				include.setAttribute(Xinclude.A_HREF, sResourceManager.getResourceNameForPath(url.toString()));
				parentClone.addContent(include);

				//--- Detach sub document from tree
				childClone = (Element) bfListClone.get(j);
				childClone.detach();

				// Save unless there is a "false" client value for the child element
				Object childSaveFlag = subtreeMap.getClientValue(child);
				if (childSaveFlag == null || !(childSaveFlag instanceof Boolean) || ((Boolean) childSaveFlag).booleanValue() == true)
				{
					File childFile = FileUtil.urlToFile(url);
					
					// If the file's directory does not exist, create it
					if (!childFile.getParentFile().exists())
					{
						childFile.getParentFile().mkdirs();
					}
					
					//--- Save sub document
					subdoc = new Document(childClone);
					dumpDom(subdoc, childFile);
				}
			}
		}

		// Save unless there is a "false" client value for the root element
		Object rootSaveFlag = subtreeMap.getClientValue(subtreeMap.getRootElement());
		if (rootSaveFlag == null || !(rootSaveFlag instanceof Boolean) || ((Boolean) rootSaveFlag).booleanValue() == true)
		{
			// If the file's directory does not exist, create it
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			
			//---Finally save the root document
			dumpDom(documentClone, file);
		}
	}

	/**
	 * Get breadth first element ordering.
	 *
	 * @param  root Element to start ordering from 
	 * @return ArrayList containing BF ordering 
	**/
	public static ArrayList getBFOrdering(Element root)
	{
		ArrayList rval = new ArrayList();
		Queue q        = new FifoQueue();
		Element e      = null;
		Iterator i     = null;

		if (root != null)
		{
			//---Prime the loop
			q.add(root);

			//---While there are nodes to explore
			while (q.size() != 0)
			{
				//---Record the order the node was visited
				e = (Element) q.remove();
				rval.add(e);

				//---Determine where to go next
				i = e.getChildren().iterator();
				while (i.hasNext())
				{
					e = (Element) i.next();
					q.add(e);
				}
			}
		}
		return rval;
	}

	/**
	 * The goal of this method is to build a new subtree map for a clone of a old element
	 * tree and its subtree map.
	 *
	 * @param org Original element tree
	 * @param cloned Cloned element tree
	 * @param subtreeMap SubtreeMap representing subtree map
	 * @return A new SubtreeMap representing a subtree map
	**/
	public static SubtreeMap getSubtreeMapForClone(Element org, Element cloned, SubtreeMap subtreeMap)
	{
		ArrayList orgBFO    = getBFOrdering(org);
		ArrayList clonedBFO = getBFOrdering(cloned);
		SubtreeMap rval     = new SubtreeMap();
		rval.setRootOrigin(subtreeMap.getRootOrigin());
		Element element;

		for (int i=0; i<orgBFO.size(); i++)
		{
			element = (Element) orgBFO.get(i);
			if (subtreeMap.containsElement(element))
			{
				rval.set((Element)clonedBFO.get(i), (URL)subtreeMap.get(element));
			}
		}
		return rval;
	}

	/**
	 * Clean empty attributes from an element. Attributes with a zero length string 
	 * will be removed from the specified element. This method can be used to clean
	 * up potentially unneed attributes in an xml document leading to a cleaner looking
	 * xml file.
	 *
	 * @param element Element to process.
	 *
	**/
	public static void cleanEmptyAttributes(Element element)
	{
		//---Create a place to store the names of attributes to be removed
		List markedAttributes = new ArrayList();

		//---Continue only if we have a non-null element 
		if (element != null)
		{
			List l     = element.getAttributes();
			Iterator i = l.iterator();
			Attribute a = null;
			String value = null;
			//---Process each element attribute
			while (i.hasNext())
			{
			 	a = (Attribute) i.next();	
				value = a.getValue();
				
				//---Mark an attribute that should be removed
				if ((value != null && value.length() == 0) || value == null)
				{
					markedAttributes.add(a.getName());
				}
			}

			//---Remove marked attributes
			Iterator j = markedAttributes.iterator();
			while (j.hasNext())
			{
				element.removeAttribute((String) j.next());
			}
		}
	}

	/**
	 * Clean empty attributes from elements in the document. This method can be used to produce
	 * a cleaner file by removing attributes that have a zero length string value from the specified
	 * JDOM document.
	 *
	 * @param document Document to process 
	 *
	**/
	public static void cleanEmptyAttributes(Document document)
	{
		//---Only process non-null documents
		if (document != null)
		{
			Iterator i   = getBFOrdering(document.getRootElement()).iterator();

			//---Process each element in the document
			while (i.hasNext())
			{
				cleanEmptyAttributes((Element) i.next());
			}
		}
	}

	/**
	 * Remove the comments from the DOM to provide a cleaner output for machine
	 * processed XML.
	 *
	 * @param document Document
	**/
	public static void cleanComments(Document document)
	{
		cleanComments(document.getRootElement());
		detachComments(document.getContent());
	}

	/**
	 * Remove the comments from the DOM to provide a cleaner output for machine
	 * processed XML.
	 *
	 * @param elment Element 
	**/
	public static void cleanComments(Element element)
	{
		ArrayList list = getBFOrdering(element);
		Element e;
		for (int i=0; i<list.size(); i++)
		{
			e = (Element) list.get(i);
			detachComments(e.getContent());
		}
	}

	/**
	 * Detach comments found in the list from the DOM to which they belong.
	 *
	 * @param list List
	**/
	private static void detachComments(List list)
	{
		Comment comment;
		Object object;
		for (int j=0; j<list.size(); j++)
		{
			object = list.get(j);
			if (object instanceof Comment)
			{
				comment = (Comment) object;
				comment.detach();
			}
		}
	}
	
	/**
	 * Use XPath selectNodes mechanism to apply XPath expression and return
	 * resulting nodes, elements, etc.
	 * <p>
	 * A JDOM XPath implementation must be in the classpath - current jaxen is used.
	 * @param jdomElement JDOM document or element
	 * @param xpathExpression
	 * @param namespace optional namespace
	 * @return result of applying the XPath expression to the XML
	 * @throws XmlException
	 * 
	 * @see <a href="http://www.jdom.org/docs/apidocs/org/jdom/xpath/XPath.html">JDOM Xpath api</a>
	 */
	public static List selectNodes(Object jdomElement, String xpathExpression, Namespace namespace) throws XmlException
	{
		try
		{			
			XPath xpathExec = XPath.newInstance(xpathExpression);
			if (namespace != null)
			{
				xpathExec.addNamespace(namespace);
			}
			
			List list = xpathExec.selectNodes(jdomElement);

			// Return empty array if nothing matches XPath
			if (list == null)
			{
				list = new ArrayList();
			}
			return list;
		} catch (JDOMException e)
		{
			e.printStackTrace();
			throw new XmlException(e.getMessage());
		}			
	}
	
	/**
	 * Use XPath selectNodes mechanism to apply XPath expression and return
	 * resulting single node, element, etc.  If other than one object is
	 * returned from the XPath processing, an exception is thrown.
	 * <p>
	 * A JDOM XPath implementation must be in the classpath - current jaxen is used.
	 * 
	 * @param jdomElement JDOM document or element
	 * @param xpathExpression
	 * @param namespace optional namespace
	 * @return result of applying the XPath expression to the XML
	 * @throws XmlException if XPath processing returns 0 or 2 or more objects
	 * 
	 * @see <a href="http://www.jdom.org/docs/apidocs/org/jdom/xpath/XPath.html">JDOM Xpath api</a>
	 */
	public static Object selectNode(Object jdomElement, String xpathExpression, Namespace namespace) throws XmlException
	{
		List list = selectNodes(jdomElement, xpathExpression, namespace);
		if (list.size() == 0)		
		{
			throw new XmlException("No elements matched XPath expression: " + xpathExpression);			
		}			
		else if (list.size() > 1)
		{
			throw new XmlException("More than one element matched XPath expression: " + xpathExpression);			
		}
		return list.get(0);

	}
	
	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: XmlUtil.java,v $
//  Revision 1.14  2006/05/17 21:09:28  smaher_cvs
//  Removed extraneous import.
//
//  Revision 1.13  2006/05/17 21:06:12  smaher_cvs
//  Added domToString.
//
//  Revision 1.12  2005/08/19 15:24:58  tames_cvs
//  Removed xinclude related comments and parameters since xinclude is
//  now implemented by the parser.
//
//  Revision 1.11  2005/05/23 15:23:28  tames_cvs
//  Updated to support Xerces parser, XIncludes, and validation.
//
//  Revision 1.10  2004/07/19 21:17:27  tames_cvs
//  *** empty log message ***
//
//  Revision 1.8  2004/07/06 21:53:50  chostetter_cvs
//  Queue is now an interface
//
//  Revision 1.7  2004/06/04 20:29:30  tames_cvs
//  Corrected import statement for Format
//
//  Revision 1.6  2004/06/03 01:38:55  chostetter_cvs
//  Organized inputs
//
//  Revision 1.5  2004/06/01 15:53:14  tames_cvs
//  Disabled validation since it is not currently working
//
//  Revision 1.4  2004/05/27 18:23:15  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.3  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.2  2004/05/03 19:46:12  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
