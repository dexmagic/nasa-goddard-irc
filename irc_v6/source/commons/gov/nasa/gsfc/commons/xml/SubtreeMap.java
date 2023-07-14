//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   3	IRC	   1.2		 6/10/2002 3:17:00 PM John Higinbotham Support to
//		allow xinlcude editing.
//   2	IRC	   1.1		 1/8/2002 3:41:21 PM  John Higinbotham Update dump
//		method.
//   1	IRC	   1.0		 1/4/2002 5:38:47 PM  John Higinbotham 
//  $
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.xml;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

/**
 * This class is used to keep track of the origins of subtrees that compose a 
 * JDOM element tree structure. In short, the xinclude processor will fuse
 * together multiple xml files into a single JDOM tree by processing xinclude
 * elements found in the documents. When applications are interested in knowing
 * where various parts of the tree came from they can refer to this object
 * which can be stored in a convient location such as the root element of 
 * a descriptor tree. Essentianly this object will help users find if a given
 * element in the tree is the root of a sub tree. If it is a root there will
 * be an associated URL which indicates the origin of that tree.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2004/07/12 14:26:24 $
 * @author			  John Higinbotham 
**/
public class SubtreeMap
{
	private URL		fRootUrl;
	private Element	fRootElement;
	private Map		fMap;
	private Map		fClientValues;

//---------------------------------------------------------------------------

//DEV: we will also want to account for circular links

	/**
	 * Construct a new SubtreeMap. 
	 *
	**/
	public SubtreeMap()
	{
		fMap = new HashMap();
		fClientValues = new HashMap();
	}

	/**
	 *  Add the contents of a SubtreeMap to the current one.
	 *
	 * @param map SubtreeMap
	**/
	public void addTo(SubtreeMap map)
	{
		set(map.getRootElement(), map.getRootOrigin());
		setClientValue(map.getRootElement(), map.getClientValue(map.getRootElement()));
		Iterator i = map.getSubtreeElements();
		Element ele;
		URL	 url;
		Object clientValue;
		while (i.hasNext())
		{
			ele = (Element) i.next();
			url = map.get(ele); 
			clientValue = map.getClientValue(ele);
			set(ele,url);
			setClientValue(ele, clientValue);
		}
	}

	/**
	 * Set the origin of the document (or root) of the tree.
	 *
	 * @param url URL of root
	**/
	public void setRootOrigin(URL url)
	{
		fRootUrl = url;
	}

	/**
	 * Get the origin of the document (or root) of the tree.
	 *
	 * @return URL of root
	**/
	public URL getRootOrigin()
	{
		return fRootUrl;
	}

	/**
	 * Set the root element of the tree.
	 *
	 * @param element Root Element 
	**/
	public void setRootElement(Element element)
	{
		fRootElement = element;
	}

	/**
	 * Get the root element of the tree.
	 *
	 * @return Root Element 
	**/
	public Element getRootElement()
	{
		return fRootElement;
	}

	/**
	 * Get the origin for a given subtree root element.
	 *
	 * @param element Element for which an origin is requested 
	 * @return URL of origin of element, or null if not a subtree root
	**/
	public URL get(Element element)
	{
		URL rval = null;
		rval = (URL) fMap.get(element);
		return rval;
	}

	/**
	 * Set the origin of a subtree by associating the url from which
	 * the subtree root came.
	 *
	 * @param element Element of subtree root
	 * @param url URL origin of subtree
	**/
	public void set(Element element, URL url)
	{
		fMap.put(element, url);
	}

	/**
	 * Gets the optional client value for a given subtree root element.
	 * Each subtree root element contained within the SubtreeMap may
	 * have an optional client value associated with it. It is the responsibility
	 * of the client to derive any meaning from this value.
	 *
	 * @param element Element for which a client value is requested 
	 * @return client value object, or null if no client value exists for element
	**/
	public Object getClientValue(Element element)
	{
		Object rval = null;
		rval = fClientValues.get(element);
		return rval;
	}

	/**
	 * Sets the optional client value for a given subtree root element.
	 *
	 * @param element Element of subtree root
	 * @param value	value object for element, or null if no value exists
	**/
	public void setClientValue(Element element, Object value)
	{
		fClientValues.put(element, value);
	}

	/**
	 * Determine if this object contains an origin URL entry for a 
	 * particular element.
	 *
	 * @param element Element for which an entry is being requested
	 * @return True if element found, false otherwise. 
	**/
	public boolean containsElement(Element element)
	{
		boolean rval = false;
		if (fMap.containsKey(element))
		{
			rval = true;
		}
		return rval;
	}

	/**
	 * Get the subtree elements. 
	 *
	 * @return Iterator of Elements representing subtrees. 
	**/
	public Iterator getSubtreeElements()
	{
		Set s = fMap.keySet();
		Iterator rval = s.iterator();
		return rval;
	}

	/**
	 * Debug method to view contents of the subtree map.
	 *
	**/
	public void dump()
	{
 		System.out.println("=== SubtreeMap ===");
		System.out.println("root : " + fRootElement.getName());
		if (fRootElement.getAttribute("name") != null)
		{
			System.out.println("   name=" + fRootElement.getAttribute("name").getValue());
		}
		System.out.println("   url=" + fRootUrl.toString());
		Iterator i = getSubtreeElements();
		URL url = null;
		while (i.hasNext())
		{
			Element e = (Element) i.next();
			url = (URL) get(e);
			System.out.println("sub-element: " + e.getName());
			if (e.getAttribute("name") != null)
			{
				System.out.println("   name=" + e.getAttribute("name").getValue());
			}
			System.out.println("   url=" + url);
		}
 		System.out.println("=== end SubtreeMap ===");
	}
}
