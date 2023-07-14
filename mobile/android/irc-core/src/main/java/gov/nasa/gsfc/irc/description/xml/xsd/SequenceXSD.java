//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SequenceXSD.java,v $
//  Revision 1.9  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.8  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.7  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.6  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.5  2004/09/07 14:12:52  tames
//  More descriptor cleanup
//
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

package gov.nasa.gsfc.irc.description.xml.xsd;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractXmlDescriptor;

/**
 * The class provides access to information describing an XML Schema sequence 
 * element and is built directly from an XML Schema file.<P>
 *
 * If you are new to XML Schemas or need a refresher on them, you should consult the
 * W3C XML Schema Primer to gain a better understanding of the terms used in this and
 * related descriptor classes. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version             $Date: 2005/09/13 20:30:12 $
 * @author              John Higinbotham
**/
public class SequenceXSD extends AbstractXmlDescriptor
{
    private Map fGroupRefs; // GroupRefXSD collection
    private Map fElements; // ElementXSD collection


	/**
	 * Constructs a new SequenceXSD having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SequenceXSD 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SequenceXSD will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SequenceXSD		
	**/
	
	public SequenceXSD(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		fGroupRefs = new LinkedHashMap();
		fElements = new LinkedHashMap();
		
		xmlUnmarshall();
	}

	
    /**
     * Get element's GroupRef elements. 
     *
     * @return Iterator of GroupRefXSD elements. 
     *            
    **/
    public Iterator getGroupRefs()
    {
        return fGroupRefs.values().iterator();
    }

    /**
     * Get element's GroupRef element with specified name. 
     *
     * @return GroupRefXSD. 
     *            
    **/
    public GroupRefXSD getGroupRef(String name)
    {
        return (GroupRefXSD) fGroupRefs.get(name);
    }

    /**
     * Get element's elements. 
     *
     * @return Iterator of ElementXSD elements. 
     *            
    **/
    public Iterator getElements()
    {
        return fElements.values().iterator();
    }

    /**
     * Get element's element with specified name. 
     *
     * @return ElementXSD. 
     *            
    **/
    public ElementXSD getElement(String name)
    {
        return (ElementXSD) fElements.get(name);
    }

	/**
	 * Get ordered collection of SequenceItems (Elements and GroupRefs).
	 * This method is useful for validation of an instance document built
	 * for this schema.
	 * 
	 * @return Iterator of SequenceItemXSD objects.
	**/ 
	public Iterator getSequenceItems()
	{
		Iterator rval  = null;
		LinkedList l   = new LinkedList();
		List children  = fElement.getChildren();
		Object[] array = children.toArray();
		Element e      = null;
		String name    = null;
		String elname  = null;

		for (int x=0; x<array.length; x++)
		{
			//System.out.println("x: " + x);
			e    = (Element) array[x];
			elname = e.getName();
			//System.out.println("elname:" + elname);

			if (elname.compareTo(Xsd.E_ELEMENT) == 0)
			{
				//System.out.println("is element");
				name = e.getAttributeValue(Xsd.A_NAME);
				l.add(getElement(name));
				//System.out.println("name:" + name);
			}
			else if (elname.compareTo(Xsd.E_GROUP_REF) == 0)
			{
				//System.out.println("is group ref");
				name = e.getAttributeValue(Xsd.A_REF);
				//System.out.println("name:" + name);
				l.add(getGroupRef(name));
			}
		}
		rval = l.iterator();
		return rval;
	}

    /**
     * Unmarshall object from XML. 
     *
    **/
    private void xmlUnmarshall()
    {
		//--- Load elements 
    	fSerializer.loadChildDescriptorElements(Xsd.E_ELEMENT, fElements, Xsd.C_ELEMENT,
			fElement, this, fDirectory);

		//--- Load group refs 
    	fSerializer.loadChildDescriptorElements(Xsd.E_GROUP_REF, fGroupRefs, Xsd.C_GROUP_REF, Xsd.A_REF,
			fElement, this, fDirectory);
    }
}
