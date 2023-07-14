//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataFormatDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.5  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.4  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.3  2005/09/14 18:03:11  chostetter_cvs
//  Refactored descriptor-based factories
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DefaultDataFormatDescriptor describes the formatting of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DefaultDataFormatDescriptor extends AbstractDataFormatDescriptor
{
	/**
	 * Constructs a new DefaultDataFormatDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DefaultDataFormatDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DefaultDataFormatDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DefaultDataFormatDescriptor		
	**/
	
	public DefaultDataFormatDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DefaultDataFormatDescriptor.
	 *
	 * @param descriptor The DefaultDataFormatDescriptor to be copied
	**/
	
	protected DefaultDataFormatDescriptor(DefaultDataFormatDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DefaultDataFormatDescriptor.
	 *
	 * @return A (deep) copy of this DefaultDataFormatDescriptor
	**/
	
	public Object clone()
	{
		DefaultDataFormatDescriptor result = (DefaultDataFormatDescriptor) super.clone();
		
		return (result);
	}
	
	
	/**
	  * Unmarshalls this DefaultDataFormatDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Currently, a data format can be either switched, as a record, or as 
		// a BasisSet.
		
		if (fDataFormatter == null)
		{
			// Unmarshall the SwitchedDataFormatterDescriptor (if any).
			fDataFormatter = (DataFormatterDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Datatransml.E_SWITCH, 
					Datatransml.C_SWITCHED_FORMATTER, fElement, this, fDirectory);
		}
		
		if (fDataFormatter == null)
		{
			// Unmarshall the BasisSetFormatterDescriptor (if any).
			fDataFormatter = (DataFormatterDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Datatransml.E_BASIS_SET, 
					Datatransml.C_BASIS_SET_FORMATTER, fElement, this, fDirectory);
		}
		
		if (fDataFormatter == null)
		{
			// Unmarshall the RecordFormatterDescriptor (if any).
			fDataFormatter = (DataFormatterDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Datatransml.E_RECORD, 
					Datatransml.C_RECORD_FORMATTER, fElement, this, fDirectory);
		}
	}
}
