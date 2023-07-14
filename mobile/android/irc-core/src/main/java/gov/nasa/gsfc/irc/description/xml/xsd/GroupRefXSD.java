//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: GroupRefXSD.java,v $
//  Revision 1.5  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/09/07 14:12:52  tames
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

/**
 * The class provides access to information describing an XML Schema group ref 
 * element and is built directly from an XML Schema file.<P>
 *
 * If you are new to XML Schemas or need a refresher on them, you should consult the
 * W3C XML Schema Primer to gain a better understanding of the terms used in this and
 * related descriptor classes. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version             $Date: 2004/10/14 15:16:50 $
 * @author              John Higinbotham
**/
public class GroupRefXSD extends SequenceItemXSD 
{
    private String fRefString;  // Ref type string 
    

	/**
	 * Constructs a new GroupRefXSD having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new GroupRefXSD 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		GroupRefXSD will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		GroupRefXSD		
	**/
	
	public GroupRefXSD(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

    /**
     * Get ref string. 
     *
     * @return String. 
     *            
    **/
    public String getRef()
    {
        return fRefString;
    }

    /**
     * Unmarshall object from XML. 
     *
    **/
    private void xmlUnmarshall()
    {
		//--- Load type 
		fRefString = fSerializer.loadStringAttribute(Xsd.A_REF, fRefString, fElement);
    }
}
