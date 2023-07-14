//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/state/AbstractDataComparator.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataComparator.java,v $
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

package gov.nasa.gsfc.irc.data.state;


/**
 * A DataComparator compares two data items.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataComparator implements DataComparator
{
	private DataComparatorDescriptor fDescriptor;
	
	private String fName;
	
	
	/**
	 * Default constructor of a new AbstractDataComparator.
	 *
	**/
	
	public AbstractDataComparator()
	{
		
	}
	

	/**
	 * Constructs a new AbstractDataComparator that will perform the data 
	 * selection described by the given DataComparatorDescriptor.
	 *
	 * @param descriptor A DataComparatorDescriptor
	 * @return A new AbstractDataComparator that will perform the data comparison 
	 * 		described by the given DataComparatorDescriptor		
	**/
	
	public AbstractDataComparator(DataComparatorDescriptor descriptor)
	{
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataComparatorDescriptor associated with this DataComparator to 
	 * the given DataComparatorDescriptor, and configures this DataComparator 
	 * in accordance with it.
	 *
	 * @param descriptor The DataComparatorDescriptor according to which to 
	 * 		configure this DataComparator
	**/
	
	public void setDescriptor(DataComparatorDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}


	/**
	 * Configures this DataComparator in accordance with its current 
	 * DataComparatorDescriptor
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
	 *  Returns the DataComparatorDescriptor that describes the data parsing 
	 *  performed by this DataComparator.
	 *  
	 *  @return The DataComparatorDescriptor that describes the data parsing 
	 *  		performed by this DataComparator
	 */
	
	public DataComparatorDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Returns the name of this DataStreamParser.
	 *  
	 *  @return The name of this DataStreamParser
	 */
	
	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the fully-qualified name of this DataStreamParser.
	 *  
	 *  @return The fully-qualified name of this DataStreamParser
	 */
	
	public String getFullyQualifiedName()
	{
		return (fName);
	}
	
	
	/**
	 *  Compares the given data Object and returns true if the comparison matches, 
	 *  false otherwise.
	 *  
	 *	@param data A data Object
	 *  @return True if the comparison is a match, false otherwise
	 */
	
	public abstract boolean compare(Object data);
	
	
	/** 
	 *  Returns a String representation of this DataComparator.
	 *
	 *  @return A String representation of this DataComparator
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataComparator: ");
		result.append("\n" + fDescriptor);
		
		return (result.toString());
	}
}
