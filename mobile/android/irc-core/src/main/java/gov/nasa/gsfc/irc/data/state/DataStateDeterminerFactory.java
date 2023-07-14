//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/state/DataStateDeterminerFactory.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataStateDeterminerFactory.java,v $
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
 * A DataStateDeterminerFactory creates new instances of DataStateDeterminers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public interface DataStateDeterminerFactory
{
	/**
	 *  Creates and returns a new DataStateDeterminer instance appropriate to the 
	 *  given DataStateDeterminerDescriptor.
	 *  
	 *	@param descriptor A DataStateDeterminerDescriptor
	 *  @return A new DataStateDeterminer instance appropriate to the 
	 *  		given DataStateDeterminerDescriptor
	 */
	
	public DataStateDeterminer getDataStateDeterminer
		(DataStateDeterminerDescriptor descriptor);
}
