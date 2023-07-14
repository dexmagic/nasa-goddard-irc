//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataParserDescriptor.java,v $
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2006/03/31 21:57:39  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.1  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
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

package gov.nasa.gsfc.irc.data.transformation.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataParserDescriptor describes a parser of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class AbstractDataParserDescriptor extends AbstractIrcElementDescriptor 
	implements DataParserDescriptor
{
	private DataSourceSelectionDescriptor fSource;
	
	
	/**
	 * Constructs a new AbstractDataParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataParserDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataParserDescriptor		
	**/
	
	public AbstractDataParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_PARSES);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataParserDescriptor.
	 *
	 * @param descriptor The AbstractDataParserDescriptor to be copied
	**/
	
	public AbstractDataParserDescriptor(AbstractDataParserDescriptor descriptor)
	{
		super(descriptor);
		
		fSource = descriptor.getSource();
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataParserDescriptor#clone()
	 */
	
	public Object clone()
	{
		AbstractDataParserDescriptor result = 
			(AbstractDataParserDescriptor) super.clone();
		
		if (fSource != null)
		{
			result.fSource = (DataSourceSelectionDescriptor) fSource.clone();
		}
		
		return (result);
	}
	
	
	/**
	 * Sets the DataSourceSelectionDescriptor associated with this 
	 * DataParserDescriptor to the given DataSelectionDescriptor.
	 *
	 * @param The new DataSourceSelectionDescriptor of this DataParserDescriptor 
	**/
	
	void setSource(DataSourceSelectionDescriptor descriptor)
	{
		fSource = descriptor;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataParserDescriptor#getSource()
	 */
	
	public DataSourceSelectionDescriptor getSource()
	{
		return (fSource);
	}
	

	/** 
	 *  Returns a String representation of this DataParserDescriptor.
	 *
	 *  @return A String representation of this DataParserDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataParserDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fSource != null)
		{
			result.append("\nSource: " + fSource);
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataParserDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the DataSourceSelectionDescriptor (if any).
		fSource = (DataSourceSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_SOURCE, Datatransml.C_SOURCE, fElement, 
					this, fDirectory);
	}
}
