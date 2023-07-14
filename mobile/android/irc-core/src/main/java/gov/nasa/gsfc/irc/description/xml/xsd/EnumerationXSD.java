//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: EnumerationXSD.java,v $
//  Revision 1.7  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.6  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractXmlDescriptor;

/**
 * The class provides access to information describing an XML Schema enumeration 
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
public class EnumerationXSD extends AbstractXmlDescriptor
{
    private String fValue;  // Enumeration value 
    

	/**
	 * Constructs a new EnumerationXSD having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new EnumerationXSD 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		EnumerationXSD will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		EnumerationXSD		
	**/
	
	public EnumerationXSD(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

    /**
     * Get element's value. 
     *
     * @return Value string. 
     *            
    **/
    public String getValue()
    {
        return fValue;
    }

    /**
     * Unmarshall object from XML. 
     *
    **/
    private void xmlUnmarshall()
    {
		//--- Load value 
		fValue = fSerializer.loadStringAttribute(Xsd.A_VALUE, fValue, fElement);
    }
}
