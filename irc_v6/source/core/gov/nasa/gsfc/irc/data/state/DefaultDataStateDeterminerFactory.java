//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/state/DefaultDataStateDeterminerFactory.java,v 1.3 2005/09/30 20:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataStateDeterminerFactory.java,v $
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
 * A DataStateDeterminerFactory creates new instances of DataStateDeterminers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 */

public class DefaultDataStateDeterminerFactory extends AbstractIrcElementFactory 
	implements DataStateDeterminerFactory
{
	private static DataStateDeterminerFactory fFactory;
	
	
	/**
	 *  Default constructor of a DataStateDeterminerFactory
	 *  
	 */
	
	protected DefaultDataStateDeterminerFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataStateDeterminerFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataStateDeterminerFactory
	 */
	
	public static DataStateDeterminerFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataStateDeterminerFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a new instance of a DataStateDeterminer appropriate to 
	 *  the given DataStateDeterminerDescriptor.
	 *  
	 *	@param descriptor A DataStateDeterminerDescriptor
	 *  @return A new instance of a DataStateDeterminer appropriate to the given 
	 *		DataStateDeterminerDescriptor
	 */
	
	public DataStateDeterminer getDataStateDeterminer
		(DataStateDeterminerDescriptor descriptor)
	{
		DataStateDeterminer determiner = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				determiner = (DataStateDeterminer) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}
			
			if (determiner == null)
			{
				determiner = new DefaultDataStateDeterminer
					((DataStateDeterminerDescriptor) descriptor);
			}
		}
		
		return (determiner);
	}
}
