//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SchemaXSD.java,v $
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
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractXmlDescriptor;

/**
 * The class provides access to information describing an XML Schema root
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
public class SchemaXSD extends AbstractXmlDescriptor
{
    private Map fSimpleTypes;  // SimpleTypeXSD collection 
    private Map fComplexTypes;  // ComplexTypeXSD collection
    private Map fGroups;  // GroupXSD collection
    private ElementXSD fRootElement;  // Schema root element


	/**
	 * Constructs a new SchemaXSD having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SchemaXSD 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SchemaXSD will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SchemaXSD		
	**/
	
	public SchemaXSD(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
        fSimpleTypes  = new LinkedHashMap();
        fComplexTypes = new LinkedHashMap();
        fGroups       = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

    /**
     * Get schema's root element. 
     *
     * @return Root element. 
     *            
    **/
    public ElementXSD getRootElement()
    {
       return fRootElement;
    }

    /**
     * Get specific SimpleType element associated with this element. 
     *
     * @param  name Name of element
     * @return Requested element 
     *            
    **/
    public SimpleTypeXSD getSimpleType(String name)
    {
        return (SimpleTypeXSD) fSimpleTypes.get(name);
    }

    /**
     * Get all SimpleType elements. 
     *
     * @return All SimpleTypeXSD elements associated with this element. 
     *            
    **/
    public Iterator getSimpleTypes()
    {
        return fSimpleTypes.values().iterator();
    }

    /**
     * Get specific ComplexType element associated with this element. 
     *
     * @param  name Name of element
     * @return Requested element 
     *            
    **/
    public ComplexTypeXSD getComplexType(String name)
    {
       return (ComplexTypeXSD) fComplexTypes.get(name);
    }

    /**
     * Get all ComplexType elements. 
     *
     * @return All ComplexTypeXSD elements associated with this element. 
     *            
    **/
    public Iterator getComplexTypes()
    {
       return fComplexTypes.values().iterator();
    }

    /**
     * Get specific Group element associated with this element. 
     *
     * @param  name Name of element
     * @return Requested element 
     *            
    **/
    public GroupXSD getGroup(String name)
    {
        return (GroupXSD) fGroups.get(name);
    }

    /**
     * Get all Group elements. 
     *
     * @return All GroupXSD elements associated with this element. 
     *            
    **/
    public Iterator getGroups()
    {
        return fGroups.values().iterator();
    }

    /**
     * Unmarshall object from XML. 
     *
    **/
    private void xmlUnmarshall()
    {
		//--- Load SimpleTypes
        fSerializer.loadChildDescriptorElements(Xsd.E_SIMPLE_TYPE, fSimpleTypes, Xsd.C_SIMPLE_TYPE,
			fElement, this, fDirectory);

		//--- Load ComplexTypes
        fSerializer.loadChildDescriptorElements(Xsd.E_COMPLEX_TYPE, fComplexTypes, Xsd.C_COMPLEX_TYPE,
			fElement, this, fDirectory);

		//--- Load Groups
        fSerializer.loadChildDescriptorElements(Xsd.E_GROUP, fGroups, Xsd.C_GROUP,
			fElement, this, fDirectory);

		//--- Load Elements
	 	fRootElement = (ElementXSD) fSerializer.loadSingleChildDescriptorElement(Xsd.E_ELEMENT, 
			Xsd.C_ELEMENT, fElement, this, fDirectory);
    }
}
