//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedValueFormatterDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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
 * A SwitchedValueFormatterDescriptor describes a set of selectable value 
 * format cases.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedValueFormatterDescriptor 
	extends AbstractSwitchedFormatterDescriptor 
	implements ValueFormatterDescriptor
{
	/**
	 * Constructs a new SwitchedValueFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SwitchedValueFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SwitchedValueFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SwitchedValueFormatterDescriptor		
	**/
	
	public SwitchedValueFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SwitchedValueFormatterDescriptor.
	 *
	 * @param descriptor The SwitchedValueFormatterDescriptor to be copied
	**/
	
	protected SwitchedValueFormatterDescriptor
		(SwitchedValueFormatterDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	  * Unmarshalls this SwitchedValueFormatterDescriptor from its associated 
	  * JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		fFormatCasesByName.clear();

		// Unmarshall the value format case descriptors.
		fSerializer.loadChildDescriptorElements(Datastateml.E_CASE, 
			fFormatCasesByName, Datatransml.C_VALUE_FORMAT, 
				fElement, this, fDirectory);
	}
}
