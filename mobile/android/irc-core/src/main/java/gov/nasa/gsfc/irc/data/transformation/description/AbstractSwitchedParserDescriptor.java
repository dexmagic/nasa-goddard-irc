//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractSwitchedParserDescriptor.java,v $
//  Revision 1.2  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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
 * A SwitchedParserDescriptor describes a set of selectable parse cases.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public abstract class AbstractSwitchedParserDescriptor 
	extends AbstractDataParserDescriptor implements SwitchedParserDescriptor
{
	private DataStateDeterminerDescriptor fCaseSelector;
	protected Map fParseCasesByName = new LinkedHashMap();
	
	
	/**
	 * Constructs a new SwitchedParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SwitchedParserDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SwitchedParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SwitchedParserDescriptor		
	**/
	
	public AbstractSwitchedParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SwitchedParserDescriptor.
	 *
	 * @param descriptor The SwitchedParserDescriptor to be copied
	**/
	
	protected AbstractSwitchedParserDescriptor
		(AbstractSwitchedParserDescriptor descriptor)
	{
		super(descriptor);
		
		fCaseSelector = descriptor.fCaseSelector;
		fParseCasesByName = descriptor.fParseCasesByName;
	}
	

	/**
	 * Returns a (deep) copy of this SwitchedParserDescriptor.
	 *
	 * @return A (deep) copy of this SwitchedParserDescriptor 
	**/
	
	public Object clone()
	{
		AbstractSwitchedParserDescriptor result = 
			(AbstractSwitchedParserDescriptor) super.clone();
		
		result.fCaseSelector = (DataStateDeterminerDescriptor) fCaseSelector.clone();
		result.fParseCasesByName = new LinkedHashMap(fParseCasesByName);
		
		return (result);
	}
	

	/** 
	 *  Returns the DataStateDeterminerDescriptor of the case selector 
	 *  associated with this SwitchedParserDescriptor.
	 *
	 *  @return The DataStateDeterminerDescriptor of the case selector 
	 *  	associated with this SwitchedParserDescriptor
	**/
	
	public DataStateDeterminerDescriptor getCaseSelector()
	{
		return (fCaseSelector);
	}
	
	
	/** 
	 *  Returns a Map (by name) of the ParseCaseDescriptors associated with this 
	 *  SwitchedParserDescriptor.
	 *
	 *  @return A Map (by name) of the ParseCaseDescriptors associated with this 
	 *  		SwitchedParserDescriptor
	**/
	
	public Map getParseCases()
	{
		return (Collections.unmodifiableMap(fParseCasesByName));
	}
	
	
	/** 
	 *  Returns a String representation of this SwitchedParserDescriptor.
	 *
	 *  @return A String representation of this SwitchedParserDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SwitchedParserDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fCaseSelector != null)
		{
			result.append("\nCase Selector" + fCaseSelector);
		}
		
		if ((fParseCasesByName != null) && (fParseCasesByName.size() > 0))
		{
			result.append("\nParse Cases: ");
			
			Iterator cases = fParseCasesByName.values().iterator();
			
			while (cases.hasNext())
			{
				DataParseDescriptor descriptor = 
					(DataParseDescriptor) cases.next();
				
				result.append("\n" + descriptor);
			}
		}
		else
		{
			result.append("\nEmpty (no parse cases)");
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this SwitchedParserDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the DeterminerDescriptor.
		fCaseSelector = (DataStateDeterminerDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datastateml.E_CASE_SELECTION, Datastateml.C_DATA_STATE_DETERMINER, 
					fElement, this, fDirectory);
	}
}
