//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFormatter.java,v $
//  Revision 1.6  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.5  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/14 20:14:48  chostetter_cvs
//  Added ability to use context information to disambiguate (yes, Bob, disambiguate) BasisSet data selection
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
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

package gov.nasa.gsfc.irc.data.transformation;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.selection.BasisSetDataSelector;
import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.description.BasisSetDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DefaultDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.BasisSetFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataBufferFormatterDescriptor;


/**
 * A BasisSetFormatter formats a basis value and a set of data values into 
 * a single sample of a BasisSet.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:16 $ 
 * @author Carl F. Hostetter   
**/

public class BasisSetFormatter extends AbstractDataFormatter
{
	private static final String CLASS_NAME = BasisSetFormatter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private BasisSetFormatterDescriptor fDescriptor;
	
	private DataSelector fSourceSelector;
	private DataSelector fTargetSelector;
	
	private String fBasisBundleName;
	
	private DataBufferFormatter fBasisValue;
	private Set fDataSamples = new LinkedHashSet();
	
	
	/**
	 * Constructs a new BasisSetFormatter and configures it in accordance with 
	 * the given BasisSetFormatterDescriptor.
	 *
	 * @param descriptor The BasisSetFormatterDescriptor according to which to 
	 * 		configure the new BasisSetFormatter
	**/
	
	public BasisSetFormatter(BasisSetFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this BasisSetFormatter in accordance with its current 
	 * BasisSetFormatterDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		fSourceSelector = getSource();
		fTargetSelector = getTarget();
		
		if (fDescriptor != null)
		{
			fBasisBundleName = fDescriptor.getBasisBundleName();
			
			if ((fBasisBundleName != null) && (fTargetSelector == null))
			{
				DefaultDataSelectionDescriptor selection = 
					new DefaultDataSelectionDescriptor();
				
				BasisSetDataSelectorDescriptor descriptor = 
					new BasisSetDataSelectorDescriptor(fBasisBundleName);
				
				selection.setDataSelector(descriptor);
				
				fTargetSelector = sDataSelectorFactory.getDataSelector(selection);
			}
			
			DataBufferFormatterDescriptor basisValueDescriptor = 
				fDescriptor.getBasisValue();
			
			if (basisValueDescriptor != null)
			{
				fBasisValue = (DataBufferFormatter) 
					sDataFormatterFactory.getDataFormatter(basisValueDescriptor);
			}
			
			Iterator dataSampleDescriptors = fDescriptor.getDataSamples().iterator();
			
			while (dataSampleDescriptors.hasNext())
			{
				DataBufferFormatterDescriptor dataSampleDescriptor = 
					(DataBufferFormatterDescriptor) dataSampleDescriptors.next();
				
				DataBufferFormatter dataSample = (DataBufferFormatter) 
					sDataFormatterFactory.getDataFormatter(dataSampleDescriptor);
				
				fDataSamples.add(dataSample);
			}
		}
	}
	
	
	/**
	 * Causes this BasisSetFormatter to format the given data Object as specified 
	 * by its associated BasisSetFormatterDescriptor into the given target Object, 
	 * and return the results.
	 *
	 * @param data The data to be formatted
	 * @param context An optional Map of contextual information
	 * @param target The target to receive the formatted data
	 * @return The result of formatting the given data
	 * @throws UnsupportedOperationException if this BasisSetFormatter is unable 
	 * 		to format the given data
	**/
	
	public Object format(Object data, Map context, Object target)
		throws UnsupportedOperationException
	{
		Object selectedData = data;
		
		if (fSourceSelector != null)
		{
			selectedData = fSourceSelector.select(data);
		}
		
		Object selectedTarget = target;
		
		if (fTargetSelector != null)
		{
			if (fTargetSelector instanceof BasisSetDataSelector)
			{
				selectedTarget = fTargetSelector.select(context);
			}
			else
			{
				selectedTarget = fTargetSelector.select(data);
			}
		}
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message ="Formatting " + selectedData + " to BasisBundle " + 
				fBasisBundleName;
				
			sLogger.logp(Level.FINE, CLASS_NAME, "format", message);
		}
		
		if (fBasisValue != null)
		{
			fBasisValue.format(selectedData, context, selectedTarget);
		}
		
		Iterator dataSamples = fDataSamples.iterator();
		
		while (dataSamples.hasNext())
		{
			DataBufferFormatter dataSample = (DataBufferFormatter) 
				dataSamples.next();
			
			dataSample.format(selectedData, context, selectedTarget);
		}
		
		if ((selectedTarget != null) && (selectedTarget instanceof BasisSet))
		{
			((BasisSet) selectedTarget).makeAvailable();
		}
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = ">>> Results: " + selectedTarget;
			
			sLogger.logp(Level.FINE, CLASS_NAME, "format", message);
		}
		
		return (selectedTarget);
	}
	
	
	/** 
	 *  Returns a String representation of this BasisSetFormatter.
	 *
	 *  @return A String representation of this BasisSetFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("BasisSetFormatter: ");
		result.append("\nDescriptor: " + getDescriptor());
		
		return (result.toString());
	}
}
