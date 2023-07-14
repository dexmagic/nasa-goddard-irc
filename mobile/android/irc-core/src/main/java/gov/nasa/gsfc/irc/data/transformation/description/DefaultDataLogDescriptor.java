//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataLogDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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
 * A DataLogDescriptor describes a means of logging data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DefaultDataLogDescriptor extends AbstractDataLogDescriptor 
{
	/**
	 * Constructs a new DefaultDataLogDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DefaultDataLogDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DefaultDataLogDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DefaultDataLogDescriptor		
	**/
	
	public DefaultDataLogDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DefaultDataLogDescriptor.
	 *
	 * @param descriptor The DefaultDataLogDescriptor to be copied
	**/
	
	protected DefaultDataLogDescriptor(DefaultDataLogDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DefaultDataLogDescriptor.
	 *
	 * @return A (deep) copy of this DefaultDataLogDescriptor
	**/
	
	public Object clone()
	{
		DefaultDataLogDescriptor result = (DefaultDataLogDescriptor) super.clone();
		
		return (result);
	}
	
	
	/**
	  * Unmarshalls this DefaultDataLogDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Currently, a data log can be either switched or by the default 
		//method.
		
		if (fDataLogger == null)
		{
			// Unmarshall the SwitchedDataLoggerDescriptor (if any).
			fDataLogger = (DataLoggerDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Datatransml.E_SWITCH, 
					Datatransml.C_SWITCHED_LOGGER, fElement, this, fDirectory);
		}
		
		if (fDataLogger == null)
		{
			// Unmarshall as a DefaultDataLoggerDescriptor.
			fDataLogger = new DefaultDataLoggerDescriptor
				(fParent, fDirectory, fElement);
		}
	}
}
