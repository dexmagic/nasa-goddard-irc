//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedDataLoggerDescriptor.java,v $
//  Revision 1.2  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.state.DataStateDeterminerDescriptor;
import gov.nasa.gsfc.irc.data.state.Datastateml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A SwitchedDataLoggerDescriptor describes a set of selectable log cases.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedDataLoggerDescriptor extends AbstractDataLoggerDescriptor
{
	private DataStateDeterminerDescriptor fCaseSelector;
	private Map fLogCasesByName = new LinkedHashMap();
	
	
	/**
	 * Constructs a new SwitchedDataLoggerDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SwitchedDataLoggerDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SwitchedDataLoggerDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SwitchedDataLoggerDescriptor		
	**/
	
	public SwitchedDataLoggerDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SwitchedDataLoggerDescriptor.
	 *
	 * @param descriptor The SwitchedDataLoggerDescriptor to be copied
	**/
	
	protected SwitchedDataLoggerDescriptor
		(SwitchedDataLoggerDescriptor descriptor)
	{
		super(descriptor);
		
		fCaseSelector = descriptor.fCaseSelector;
		fLogCasesByName = descriptor.fLogCasesByName;
	}
	

	/**
	 * Returns a (deep) copy of this SwitchedDataLoggerDescriptor.
	 *
	 * @return A (deep) copy of this SwitchedDataLoggerDescriptor 
	**/
	
	public Object clone()
	{
		SwitchedDataLoggerDescriptor result = 
			(SwitchedDataLoggerDescriptor) super.clone();
		
		result.fCaseSelector = (DataStateDeterminerDescriptor) fCaseSelector.clone();
		result.fLogCasesByName = new LinkedHashMap(fLogCasesByName);
		
		return (result);
	}
	

	/** 
	 *  Returns the DeterminerDescriptor of the case selector associated with this 
	 *  SwitchedDataLoggerDescriptor.
	 *
	 *  @return The DeterminerDescriptor of the case selector associated with this 
	 *  		SwitchedDataLoggerDescriptor
	**/
	
	public DataStateDeterminerDescriptor getCaseSelector()
	{
		return (fCaseSelector);
	}
	
	
	/** 
	 *  Returns a Map (by name) of the LogCaseDescriptors associated with this 
	 *  SwitchedDataLoggerDescriptor.
	 *
	 *  @return A Map (by name) of the LogCaseDescriptors associated with this 
	 *  		SwitchedDataLoggerDescriptor
	**/
	
	public Map getLogCases()
	{
		return (Collections.unmodifiableMap(fLogCasesByName));
	}
	
	
	/** 
	 *  Returns a String representation of this SwitchedDataLoggerDescriptor.
	 *
	 *  @return A String representation of this SwitchedDataLoggerDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SwitchedDataLoggerDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fCaseSelector != null)
		{
			result.append("\nCase Selector" + fCaseSelector);
		}
		
		if ((fLogCasesByName != null) && (fLogCasesByName.size() > 0))
		{
			result.append("\nLog Cases: ");
			
			Iterator cases = fLogCasesByName.values().iterator();
			
			while (cases.hasNext())
			{
				DataLogDescriptor descriptor = (DataLogDescriptor) cases.next();
				
				result.append("\n" + descriptor);
			}
		}
		else
		{
			result.append("\nEmpty (no log cases)");
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this SwitchedDataLoggerDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the DeterminerDescriptor.
		fCaseSelector = (DataStateDeterminerDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datastateml.E_CASE_SELECTION, Datastateml.C_DATA_STATE_DETERMINER, 
					fElement, this, fDirectory);
		
		// Unmarshall the LogCaseDescriptors (if any)
		fLogCasesByName.clear();

		fSerializer.loadChildDescriptorElements(Datastateml.E_CASE, 
			fLogCasesByName, Datatransml.C_LOG, fElement, this, fDirectory);
	}
}
