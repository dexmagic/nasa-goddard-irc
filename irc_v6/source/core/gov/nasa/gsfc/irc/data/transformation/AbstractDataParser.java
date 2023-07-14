//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/AbstractDataParser.java,v 1.7 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataParser.java,v $
//  Revision 1.7  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.6  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.5  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
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

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.DataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.DefaultDataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataParserDescriptor;


/**
 * A DataParser selects data from a data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataParser implements DataParser
{
	protected static final DataSelectorFactory sDataSelectorFactory = 
		DefaultDataSelectorFactory.getInstance();
	protected static final DataParserFactory sDataParserFactory = 
		DefaultDataParserFactory.getInstance();
		
	private DataParserDescriptor fDescriptor;
	
	private String fName = "Anonymous";
	
	private DataSelector fSource;
	
	
	/**
	 * Default constructor of a new AbstractDataParser.
	 *
	**/
	
	public AbstractDataParser()
	{
		
	}
	

	/**
	 * Constructs a new AbstractDataParser that will perform the data 
	 * selection described by the given DataParserDescriptor.
	 *
	 * @param descriptor A DataParserDescriptor
	 * @return A new AbstractDataParser that will perform the data parsing 
	 * 		described by the given DataParserDescriptor		
	**/
	
	public AbstractDataParser(DataParserDescriptor descriptor)
	{
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataParserDescriptor associated with this DataParser to 
	 * the given DataParserDescriptor, and configures this DataParser 
	 * in accordance with it.
	 *
	 * @param descriptor The DataParserDescriptor according to which to 
	 * 		configure this DataParser
	**/
	
	public void setDescriptor(DataParserDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this AbstractDataParser in accordance with its current 
	 * AbstractDataParserDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fName = fDescriptor.getName();
			
			DataSelectionDescriptor dataSelectionDescriptor = 
				fDescriptor.getSource();
			
			if (dataSelectionDescriptor != null)
			{
				fSource = sDataSelectorFactory.getDataSelector
					(dataSelectionDescriptor);
			}
		}
	}
	
	
	/**
	 *  Returns the AbstractDataParserDescriptor that describes the data parsing 
	 *  performed by this DataParser.
	 *  
	 *  @return The AbstractDataParserDescriptor that describes the data parsing 
	 *  		performed by this DataParser
	 */
	
	public DataParserDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Sets the name of this DataParser to the given name.
	 *  
	 *  @return The name of this DataParser
	 */
	
	protected void setName(String name)
	{
		fName = name;
	}
	
	
	/**
	 *  Returns the name of this DataParser.
	 *  
	 *  @return The name of this DataParser
	 */
	
	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the fully-qualified name of this DataParser.
	 *  
	 *  @return The fully-qualified name of this DataParser
	 */
	
	public String getFullyQualifiedName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the source DataSelector associated with this DataParser (if any).
	 *  
	 *  @return The source DataSelector associated with this DataParser (if any)
	 */
	
	public DataSelector getSource()
	{
		return (fSource);
	}
	
	
	/** 
	 *  Returns a String representation of this DataParser.
	 *
	 *  @return A String representation of this DataParser
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataParser: ");
		result.append("\n" + fDescriptor);
		
		return (result.toString());
	}
}
