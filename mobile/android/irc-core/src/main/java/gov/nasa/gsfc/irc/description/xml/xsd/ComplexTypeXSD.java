//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ComplexTypeXSD.java,v $
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
 * The class provides access to information describing an XML Schema complexType 
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
public class ComplexTypeXSD extends AbstractXmlDescriptor
{
	private boolean fIsAbstract = false;
    private ComplexContentXSD fComplexContent; // Only one of fComplexContent and fSequence
    private SequenceXSD fSequence; // may be non null as restricted by XML schemas
    private Map fAttributes; // AttributeXSD collection

    
	/**
	 * Constructs a new ComplexTypeXSD having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ComplexTypeXSD 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ComplexTypeXSD will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ComplexTypeXSD		
	**/
	
	public ComplexTypeXSD(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		fAttributes = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

    /**
     * Determine if element is abstract. 
     *
     * @return true if abstract, false otherwise 
     *            
    **/
    public boolean isAbstract()
    {
        return fIsAbstract;
    }

    /**
     * Get element's complex content element. 
     *
     * @return ComplexContentXSD. 
     *            
    **/
    public ComplexContentXSD getComplexContent()
    {
        return fComplexContent;
    }

    /**
     * Get element's sequence element. 
     *
     * @return SequenceXSD. 
     *            
    **/
    public SequenceXSD getSequence()
    {
        return fSequence;
    }

    /**
     * Get element's attribute elements. 
     *
     * @return Iterator of AttributeXSD elements. 
     *            
    **/
    public Iterator getAttributes()
    {
        return fAttributes.values().iterator();
    }

    /**
     * Get element's attribute element with specified name. 
     *
     * @param name Name of attribute
     * @return AttributeXSD. 
     *            
    **/
    public AttributeXSD getAttribute(String name)
    {
        return (AttributeXSD) fAttributes.get(name);
    }

    /**
     * Unmarshall object from XML. 
     *
    **/
    private void xmlUnmarshall()
    {
		//--- Load ComplexContent 
        fComplexContent = (ComplexContentXSD) fSerializer.loadSingleChildDescriptorElement(Xsd.E_COMPLEX_CONTENT,  
			Xsd.C_COMPLEX_CONTENT, fElement, this, fDirectory);

		//--- Load Sequence 
        fSequence= (SequenceXSD) fSerializer.loadSingleChildDescriptorElement(Xsd.E_SEQUENCE, 
			Xsd.C_SEQUENCE, fElement, this, fDirectory);

		//--- Load attributes
        fSerializer.loadChildDescriptorElements(Xsd.E_ATTRIBUTE, fAttributes, Xsd.C_ATTRIBUTE,
			fElement, this, fDirectory);

		//--- Load abstract flag 
		fIsAbstract = fSerializer.loadBooleanAttribute(Xsd.A_ABSTRACT, fIsAbstract, fElement);
    }
}
