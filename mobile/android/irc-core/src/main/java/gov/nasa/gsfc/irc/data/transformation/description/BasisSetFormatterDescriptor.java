//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFormatterDescriptor.java,v $
//  Revision 1.4  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A BasisSetFormatterDescriptor describes the formatting of a Map of data 
 * across one or more channels of a single sample into a single sample of a 
 * BasisSet.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class BasisSetFormatterDescriptor extends AbstractDataFormatterDescriptor
{
	private DataBufferFormatterDescriptor fBasisValue;
	private DataBufferFormatterDescriptor fDataSampleDefaults;
	private Map fDataSamplesByName = new LinkedHashMap();
	

	/**
	 * Constructs a new BasisSetFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new BasisSetFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BasisSetFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BasisSetFormatterDescriptor		
	**/
	
	public BasisSetFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given BasisSetFormatterDescriptor.
	 *
	 * @param descriptor The BasisSetFormatterDescriptor to be copied
	**/
	
	protected BasisSetFormatterDescriptor
		(BasisSetFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fBasisValue = descriptor.fBasisValue;
		fDataSampleDefaults = descriptor.fDataSampleDefaults;
		fDataSamplesByName = descriptor.fDataSamplesByName;
	}
	

	/**
	 * Returns a (deep) copy of this BasisSetFormatterDescriptor.
	 *
	 * @return A (deep) copy of this BasisSetFormatterDescriptor 
	**/
	
	public Object clone()
	{
		BasisSetFormatterDescriptor result = 
			(BasisSetFormatterDescriptor) super.clone();
		
		result.fBasisValue = (DataBufferFormatterDescriptor) fBasisValue.clone();
		result.fDataSampleDefaults = (DataBufferFormatterDescriptor) 
			fDataSampleDefaults.clone();
		result.fDataSamplesByName = new LinkedHashMap(fDataSamplesByName);
		
		return (result);
	}
	

	/**
	 * Returns the BasisBundle name associated with this 
	 * BasisSetFormatterDescriptor.
	 *
	 * @return The BasisBundle name associated with this 
	 * 		BasisSetFormatterDescriptor 
	**/
	
	public String getBasisBundleName()
	{
		return (getName());
	}
	

	/**
	 * Returns the basis DataBufferFormatterDescriptor associated with this 
	 * BasisSetFormatterDescriptor.
	 *
	 * @return The basis DataBufferFormatterDescriptor associated with this 
	 * 		BasisSetFormatterDescriptor
	**/
	
	public DataBufferFormatterDescriptor getBasisValue()
	{
		return (fBasisValue);
	}
	

	/**
	 * Returns the (unmodifiable) Set of DataSampleFormatterDescriptors associated 
	 * with this BasisSetFormatterDescriptor.
	 *
	 * @return The (unmodifiable) Set of DataSampleFormatterDescriptors associated 
	 * 		with this BasisSetFormatterDescriptor 
	**/
	
	public Collection getDataSamples()
	{
		return (Collections.unmodifiableCollection(fDataSamplesByName.values()));
	}
	

	/** 
	 *  Returns a String representation of this BasisSetFormatterDescriptor.
	 *
	 *  @return A String representation of this BasisSetFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("BasisSetFormatterDescriptor: ");
		result.append("\n" + super.toString());
		
		result.append("\nBasisBundle name: " + getBasisBundleName());

		if (fBasisValue != null)
		{
			result.append("\nBasis value: " + fBasisValue);
		}

		if (fDataSamplesByName != null)
		{
			int numDataSamples = fDataSamplesByName.size();
			
			result.append("\nHas " + numDataSamples + " data samples: ");
			
			Iterator dataSamples = fDataSamplesByName.values().iterator();
			
			for (int i = 1; dataSamples.hasNext(); i++)
			{
				DataBufferFormatterDescriptor dataSample = 
					(DataBufferFormatterDescriptor) dataSamples.next();
				
				result.append("\n" + i + ": " + dataSample);
			}
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this BasisSetFormatterDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the basis DataBufferFormatterDescriptor.
		fBasisValue = (DataBufferFormatterDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_BASIS_BUFFER, 
				Datatransml.C_DATA_BUFFER_FORMATTER, fElement, this, fDirectory);
		
		// Unmarshall the default DataBufferFormatterDescriptor (if any)
		fDataSampleDefaults = (DataBufferFormatterDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_DATA_BUFFER_DEFAULTS, 
				Datatransml.C_DATA_BUFFER_FORMATTER, fElement, this, fDirectory);
		
		// Unmarshall the other DataSampleFormatterDescriptors (if any).
		fDataSamplesByName.clear();
		
		fSerializer.loadChildDescriptorElements(Datatransml.E_DATA_BUFFER, 
			fDataSamplesByName, Datatransml.C_DATA_BUFFER_FORMATTER, 
				fElement, this, fDirectory);
		
		// Set the DataBufferFormatterDescriptor defaults (if any)
		
		if (fDataSampleDefaults != null)
		{
			boolean usesNameAsKeyedValueSelector = 
				fDataSampleDefaults.usesNameAsKeyedValueSelector();
			
			DataFormatDescriptor defaultValue = fDataSampleDefaults.getValue();
			
			// First set the defaults for the BasisBuffer.
			if (usesNameAsKeyedValueSelector && 
				! fBasisValue.overridesUseOfNameAsKeyedValueSelector() &&
				(fBasisValue.getSource() == null))
			{
				fBasisValue.useNameAsKeyedValueSelector();
			}
			
			if ((defaultValue != null) && (fBasisValue.getValue() == null))
			{
				fBasisValue.setValue(defaultValue);
			}
			
			// Now set the defaults for the other DataBuffers.
			Iterator dataSamples = fDataSamplesByName.values().iterator();
			
			while (dataSamples.hasNext())
			{
				DataBufferFormatterDescriptor dataSample = 
					(DataBufferFormatterDescriptor) dataSamples.next();
				
				if (usesNameAsKeyedValueSelector && 
					! dataSample.overridesUseOfNameAsKeyedValueSelector() && 
					(dataSample.getSource() == null))
				{
					dataSample.useNameAsKeyedValueSelector();
				}
				
				if ((defaultValue != null) && (dataSample.getValue() == null))
				{
					dataSample.setValue(defaultValue);
				}
			}
		}
	}
}
