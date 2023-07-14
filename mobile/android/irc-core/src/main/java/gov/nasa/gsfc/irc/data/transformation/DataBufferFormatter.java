//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataBufferFormatter.java,v $
//  Revision 1.5  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.4  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.3  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.description.DataBufferDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DefaultDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataBufferFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;


/**
 * A DataBufferFormatter formats data into a single sample of single DataBuffer 
 * of a BasisSet.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class DataBufferFormatter extends AbstractDataFormatter
{
	private static final String CLASS_NAME = DataBufferFormatter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private DataBufferFormatterDescriptor fDescriptor;
	
	private DataSelector fSourceSelector;
	private DataSelector fTargetSelector;
	
	private String fDataBufferName;
	private DataFormatter fValue;
	

	
	/**
	 * Constructs a new DataBufferFormatter and configures it in accordance with 
	 * the given DataBufferFormatterDescriptor.
	 *
	 * @param descriptor The DataBufferFormatterDescriptor according to which to 
	 * 		configure the new DataBufferFormatter
	**/
	
	public DataBufferFormatter(DataBufferFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this DataBufferFormatter in accordance with its current 
	 * DataBufferFormatterDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		fSourceSelector = getSource();
		fTargetSelector = getTarget();
		
		if (fDescriptor != null)
		{
			fDataBufferName = fDescriptor.getDataBufferName();
			
			if ((fDataBufferName != null) && (fTargetSelector == null))
			{
				DefaultDataSelectionDescriptor selection = new DefaultDataSelectionDescriptor();
				
				DataBufferDataSelectorDescriptor descriptor = 
					new DataBufferDataSelectorDescriptor(fDataBufferName);
				
				selection.setDataSelector(descriptor);
				
				fTargetSelector = sDataSelectorFactory.getDataSelector(selection);
			}
			
			DataFormatDescriptor valueDescriptor = fDescriptor.getValue();
			
			if (valueDescriptor != null)
			{
				fValue = (DataFormatter) 
					sDataFormatterFactory.getDataFormatter(valueDescriptor);
			}
		}
	}
	
	
	/**
	 * Causes this DataBufferFormatter to format the given data Object as specified 
	 * by its associated DataBufferFormatterDescriptor into the given target Object, 
	 * and return the results.
	 *
	 * @param data The data to be formatted
	 * @param context An optional Map of contextual information
	 * @param target The target to receive the formatted data
	 * @return The result of formatting the given data
	 * @throws UnsupportedOperationException if this DataBufferFormatter is unable 
	 * 		to format the given data
	**/
	
	public Object format(Object data, Map context, Object target)
		throws UnsupportedOperationException
	{
		String result = null;
		
		Object selectedData = data;
		
		if (fSourceSelector != null)
		{
			selectedData = fSourceSelector.select(data);
		}
		
		Object selectedTarget = target;
		
		if (fTargetSelector != null)
		{
			selectedTarget = fTargetSelector.select(target);
		}
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message ="Formatting " + selectedData + " to DataBuffer " + 
				fDataBufferName;
			
			sLogger.logp(Level.FINE, CLASS_NAME, "format", message);
		}
		
		Object formatResult = fValue.format(selectedData, context, selectedTarget);
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = "> Result: " + formatResult;
			
			sLogger.logp(Level.FINE, CLASS_NAME, "format", message);
		}
		
		result = fDataBufferName + "=" + formatResult;
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataBufferFormatter.
	 *
	 *  @return A String representation of this DataBufferFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("DataBufferFormatter: ");
		result.append("\nDescriptor: " + getDescriptor());
		
		return (result.toString());
	}
}
