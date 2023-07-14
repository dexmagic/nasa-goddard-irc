//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataFormatterDescriptor.java,v $
//  Revision 1.6  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
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

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.selection.description.KeyedDataSelectorDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataFormatterDescriptor describes a formatter of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public abstract class AbstractDataFormatterDescriptor 
	extends AbstractIrcElementDescriptor implements DataFormatterDescriptor
{
	private DataSourceSelectionDescriptor fSource;
	private DataTargetSelectionDescriptor fTarget;
	
	private boolean fUseDataNameAsName = false;
	private boolean fUseNameAsKeyedValueSelector = false;
	private boolean fOverridesUseOfNameAsKeyedValueSelector = false;
	
	private static Map fCachedKeyedValueSelectors;
	
	
	/**
	 * Constructs a new AbstractDataFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		AbstractDataFormatterDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataFormatterDescriptor		
	**/
	
	public AbstractDataFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_FORMATS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataFormatterDescriptor.
	 *
	 * @param descriptor The AbstractDataFormatterDescriptor to be cloned
	**/
	
	protected AbstractDataFormatterDescriptor
		(AbstractDataFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fSource = descriptor.fSource;
		fTarget = descriptor.fTarget;
		fUseDataNameAsName = descriptor.fUseDataNameAsName;
		fUseNameAsKeyedValueSelector = descriptor.fUseNameAsKeyedValueSelector;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#clone()
	 */
	
	public Object clone()
	{
		AbstractDataFormatterDescriptor result = 
			(AbstractDataFormatterDescriptor) super.clone();
		
		if (fSource != null)
		{
			result.fSource = (DataSourceSelectionDescriptor) fSource.clone();
		}
		
		if (fTarget != null)
		{
			result.fTarget = (DataTargetSelectionDescriptor) fTarget.clone();
		}
		
		result.fUseDataNameAsName = fUseDataNameAsName;
		result.fUseNameAsKeyedValueSelector = fUseNameAsKeyedValueSelector;
		
		return (result);
	}
	
	
	/**
	 * Sets the DataSourceSelectionDescriptor associated with this 
	 * AbstractDataFormatterDescriptor to the given DataSelectorDescription
	 *
	 * @param The DataSourceSelectionDescriptor to be associated with this 
	 * 		AbstractDataFormatterDescriptor
	**/
	
	protected void setSource(DataSourceSelectionDescriptor descriptor)
	{
		fSource = descriptor;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#getSource()
	 */
	
	public DataSourceSelectionDescriptor getSource()
	{
		return (fSource);
	}
	

	/**
	 * Sets the DataTargetSelectionDescriptor associated with this 
	 * AbstractDataFormatterDescriptor to the given DefaultDataSelectionDescriptor
	 *
	 * @param The DataTargetSelectionDescriptor to be associated with this 
	 * 		AbstractDataFormatterDescriptor
	**/
	
	protected void setTarget(DataTargetSelectionDescriptor descriptor)
	{
		fTarget = descriptor;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#getTargetSelection()
	 */
	
	public DataTargetSelectionDescriptor getTarget()
	{
		return (fTarget);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#setName(java.lang.String)
	 */

	public void setName(String name)
	{
		super.setName(name);
		
		if (fUseNameAsKeyedValueSelector)
		{
			useNameAsKeyedValueSelector();
		}
	}
	
	
	/**
	 * Configures this DataFormatterDescriptor to use the name of the data it 
	 * formats as its own name.
	 *
	**/
	
	void useDataNameAsName()
	{
		fUseDataNameAsName = true;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#usesDataNameAsName()
	 */

	public boolean usesDataNameAsName()
	{
		return (fUseDataNameAsName);
	}
	
	
	/**
	 * Configures this DataFormatterDescriptor to select all input data for 
	 * formatting.
	 *
	**/
	
	void selectAllData()
	{
		fUseNameAsKeyedValueSelector = false;
		
		fSource = null;
	}
	
	
	/**
	 * Configures this DataFormatterDescriptor to use its name as a keyed data 
	 * selector for its value.
	 *
	**/
	
	void useNameAsKeyedValueSelector()
	{
		fUseNameAsKeyedValueSelector = true;
		
		String name = getName();
		
		if (name != null)
		{
			if (fCachedKeyedValueSelectors == null)
			{
				fCachedKeyedValueSelectors = new HashMap();
			}
			
			if (fSource == null)
			{
				fSource = new DataSourceSelectionDescriptor();
			}
			
			KeyedDataSelectorDescriptor descriptor = 
				(KeyedDataSelectorDescriptor) 
					fCachedKeyedValueSelectors.get(name);
			
			if (descriptor == null)
			{
				descriptor = new KeyedDataSelectorDescriptor(name);
				fCachedKeyedValueSelectors.put(name, descriptor);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#usesNameAsKeyedValueSelector()
	 */
	
	public boolean usesNameAsKeyedValueSelector()
	{
		return (fUseNameAsKeyedValueSelector);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor#overridesUseOfNameAsKeyedValueSelector()
	 */
	
	public boolean overridesUseOfNameAsKeyedValueSelector()
	{
		return (fOverridesUseOfNameAsKeyedValueSelector);
	}
	
	
	/** 
	 *  Returns a String representation of this AbstractDataFormatterDescriptor.
	 *
	 *  @return A String representation of this AbstractDataFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataFormatterDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fUseDataNameAsName)
		{
			result.append("\nUses data name as name");
		}
		
		if (fUseNameAsKeyedValueSelector)
		{
			result.append("\nUses name as keyed value selector");
		}
		
		if (fSource != null)
		{
			result.append("\nSource: " + fSource);
		}
		
		if (fTarget != null)
		{
			result.append("\nTarget: " + fTarget);
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this AbstractDataFormatterDescriptor from its associated 
	  * JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the DataSourceSelectionDescriptor (if any).
		fSource = (DataSourceSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_SOURCE, Datatransml.C_SOURCE, fElement, 
					this, fDirectory);
		
		// Unmarshall the DataTargetSelectionDescriptor (if any).
		fTarget = (DataTargetSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_TARGET, Datatransml.C_TARGET, fElement, 
					this, fDirectory);

		// Unmarshall the useDataNameAsName attribute (if any).
		fUseDataNameAsName = fSerializer.loadBooleanAttribute
			(Datatransml.A_USE_DATA_NAME_AS_NAME, false, fElement);
		
		
		if (fElement.getAttribute
			(Datatransml.A_USE_NAME_AS_KEYED_VALUE_SELECTOR) != null)
		{
			fOverridesUseOfNameAsKeyedValueSelector = true;
			
			// Unmarshall the useNameAsKeyedValueSelector attribute.
			fUseNameAsKeyedValueSelector = fSerializer.loadBooleanAttribute
				(Datatransml.A_USE_NAME_AS_KEYED_VALUE_SELECTOR, false, fElement);
		}
		else
		{
			fOverridesUseOfNameAsKeyedValueSelector = false;
		}
		
		if (fUseNameAsKeyedValueSelector)
		{
			useNameAsKeyedValueSelector();
		}
	}
}
