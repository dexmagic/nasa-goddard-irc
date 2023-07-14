//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedDataFormatterDescriptor.java,v $
//  Revision 1.6  2006/07/18 19:57:06  chostetter_cvs
//  Fixed setting of wildcard case names
//
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

import java.util.Iterator;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.state.DataComparatorDescriptor;
import gov.nasa.gsfc.irc.data.state.Datastateml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A SwitchedDataFormatterDescriptor describes a set of selectable format cases.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/07/18 19:57:06 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedDataFormatterDescriptor 
	extends AbstractSwitchedFormatterDescriptor
{
	/**
	 * Constructs a new SwitchedDataFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SwitchedDataFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SwitchedDataFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SwitchedDataFormatterDescriptor		
	**/
	
	public SwitchedDataFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SwitchedDataFormatterDescriptor.
	 *
	 * @param descriptor The SwitchedDataFormatterDescriptor to be copied
	**/
	
	protected SwitchedDataFormatterDescriptor
		(SwitchedDataFormatterDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	  * Unmarshalls this SwitchedDataFormatterDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the wildcard FormatCaseDescriptor (if any)
		fFormatCasesByName.clear();

		DefaultDataFormatDescriptor wildcardCase = (DefaultDataFormatDescriptor)
			fSerializer.loadSingleChildDescriptorElement(Datastateml.E_CASE, 
				Datatransml.C_FORMAT, fElement, this, fDirectory);
		
		if (wildcardCase != null)
		{
			String wildcardName = wildcardCase.getName();
			
			if ((wildcardName != null) && wildcardName.equals(Datatransml.WILDCARD))
			{
				Iterator comparators = getCaseSelector().getComparators().iterator();
				
				while (comparators.hasNext())
				{
					DataComparatorDescriptor comparator = (DataComparatorDescriptor) 
						comparators.next();
					
					String caseName = comparator.getName();
					
					DefaultDataFormatDescriptor caseDescriptor = 
						(DefaultDataFormatDescriptor) wildcardCase.clone();
					
					caseDescriptor.setName(caseName);
					
					DataFormatterDescriptor formatter = (DataFormatterDescriptor)
						caseDescriptor.getDataFormatter();
					
					String formatterName = formatter.getName();
					
					if (formatterName.equals(Datatransml.WILDCARD))
					{
						formatter.setName(caseName);
					}
					
					fFormatCasesByName.put(caseName, caseDescriptor);
				}
			}
			else
			{
				// Unmarshall the CaseDescriptors.
				fSerializer.loadChildDescriptorElements(Datastateml.E_CASE, 
					fFormatCasesByName, Datatransml.C_FORMAT, 
						fElement, this, fDirectory);
			}
		}
	}
}
