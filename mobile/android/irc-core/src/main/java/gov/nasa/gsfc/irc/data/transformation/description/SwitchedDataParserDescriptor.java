//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedDataParserDescriptor.java,v $
//  Revision 1.4  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.3  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

import gov.nasa.gsfc.irc.data.state.Datastateml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A SwitchedDataParserDescriptor describes a set of selectable parse cases.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedDataParserDescriptor 
	extends AbstractSwitchedParserDescriptor
{
	/**
	 * Constructs a new SwitchedDataParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		SwitchedDataParserDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SwitchedDataParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SwitchedDataParserDescriptor		
	**/
	
	public SwitchedDataParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SwitchedDataParserDescriptor.
	 *
	 * @param descriptor The SwitchedDataParserDescriptor to be copied
	**/
	
	protected SwitchedDataParserDescriptor
		(SwitchedDataParserDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this SwitchedDataParserDescriptor.
	 *
	 * @return A (deep) copy of this SwitchedDataParserDescriptor 
	**/
	
	public Object clone()
	{
		SwitchedDataParserDescriptor result = 
			(SwitchedDataParserDescriptor) super.clone();
		
		return (result);
	}
	

	/**
	  * Unmarshalls this SwitchedDataParserDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the ParseCaseDescriptors (if any).
		fParseCasesByName.clear();
		
		fSerializer.loadChildDescriptorElements(Datastateml.E_CASE, 
			fParseCasesByName, Datatransml.C_PARSE, fElement, this, fDirectory);
	}
}
