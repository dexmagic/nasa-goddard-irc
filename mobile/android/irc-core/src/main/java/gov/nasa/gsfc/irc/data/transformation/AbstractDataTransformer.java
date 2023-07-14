//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/AbstractDataTransformer.java,v 1.4 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataTransformer.java,v $
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
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.transformation;

import gov.nasa.gsfc.irc.data.selection.DataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.DefaultDataSelectorFactory;
import gov.nasa.gsfc.irc.data.transformation.description.DataTransformerDescriptor;


/**
 * A DataTransformer selects data from a data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataTransformer implements DataTransformer
{
	protected static final DataSelectorFactory sDataSelectorFactory =
		DefaultDataSelectorFactory.getInstance();
	
	private DataTransformerDescriptor fDescriptor;
	
	private String fName = "Anonymous";
	
	
	/**
	 * Default constructor of a new AbstractDataTransformer.
	 *
	**/
	
	public AbstractDataTransformer()
	{
		
	}
	

	/**
	 * Constructs a new AbstractDataTransformer that will perform the data 
	 * selection described by the given DataTransformerDescriptor.
	 *
	 * @param descriptor A DataTransformerDescriptor
	 * @return A new AbstractDataTransformer that will perform the data selection 
	 * 		described by the given DataTransformerDescriptor		
	**/
	
	public AbstractDataTransformer(DataTransformerDescriptor descriptor)
	{
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataTransformerDescriptor associated with this DataTransformer to 
	 * the given DataTransformerDescriptor, and configures this DataTransformer 
	 * in accordance with it.
	 *
	 * @param descriptor The DataTransformerDescriptor according to which to 
	 * 		configure this DataTransformer
	**/
	
	public void setDescriptor(DataTransformerDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this AbstractDataTransformer in accordance with its current 
	 * DataTransformerDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fName = fDescriptor.getName();
		}
	}
	
	
	/**
	 *  Returns the DataTransformerDescriptor that describes the data parsing 
	 *  performed by this DataTransformer.
	 *  
	 *  @return The DataTransformerDescriptor that describes the data parsing 
	 *  		performed by this DataTransformer
	 */
	
	public DataTransformerDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Returns the name of this DataTransformer.
	 *  
	 *  @return The name of this DataTransformer
	 */
	
	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the fully-qualified name of this DataTransformer.
	 *  
	 *  @return The fully-qualified name of this DataTransformer
	 */
	
	public String getFullyQualifiedName()
	{
		return (fName);
	}
	
	
	/** 
	 *  Returns a String representation of this DataTransformer.
	 *
	 *  @return A String representation of this DataTransformer
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataTransformer: ");
		result.append("\n" + fDescriptor);
		
		return (result.toString());
	}
}
