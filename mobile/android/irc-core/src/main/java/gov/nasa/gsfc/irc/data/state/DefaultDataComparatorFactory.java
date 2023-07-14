//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/state/DefaultDataComparatorFactory.java,v 1.3 2005/09/30 20:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataComparatorFactory.java,v $
//  Revision 1.3  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.2  2005/09/14 18:03:11  chostetter_cvs
//  Refactored descriptor-based factories
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

package gov.nasa.gsfc.irc.data.state;

import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementFactory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataComparatorFactory creates new instances of DataComparators.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 */

public class DefaultDataComparatorFactory extends AbstractIrcElementFactory 
	implements DataComparatorFactory
{
	private static DataComparatorFactory fFactory;
	
	
	/**
	 *  Default constructor of a DataComparatorFactory
	 *  
	 */
	
	protected DefaultDataComparatorFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataComparatorFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataComparatorFactory
	 */
	
	public static DataComparatorFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataComparatorFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a new instance of a DataComparator appropriate to 
	 *  the given DataComparatorDescriptor.
	 *  
	 *	@param descriptor A DataComparatorDescriptor
	 *  @return A new instance of a DataComparator appropriate to the given 
	 *		DataComparatorDescriptor
	 */
	
	public DataComparator getDataComparator(DataComparatorDescriptor descriptor)
	{
		DataComparator comparator = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				comparator = (DataComparator) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}

			if (comparator == null)
			{
				comparator = new DefaultDataComparator
					((DataComparatorDescriptor) descriptor);
			}
		}
		
		return (comparator);
	}
}
