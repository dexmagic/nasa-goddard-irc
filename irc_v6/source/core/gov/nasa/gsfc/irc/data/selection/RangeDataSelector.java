//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/RangeDataSelector.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RangeDataSelector.java,v $
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

package gov.nasa.gsfc.irc.data.selection;

import gov.nasa.gsfc.irc.data.selection.description.RangeDataSelectorDescriptor;


/**
 * A RangeDataSelector is an abstract class that performs selection of a range 
 * of indexed data Objects.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public abstract class RangeDataSelector extends AbstractDataSelector
{
	private int fStart = -1;
	private int fLength = 1;
	
	
	/**
	 * Constructs a new RangeDataSelector that will perform the data range 
	 * selection indicated by the given RangeDataSelectorDescriptor.
	 *
	 * @param descriptor A RangeDataSelectorDescriptor
	 * @return A new RangeDataSelector that will perform the data range 
	 * 		selection indicated by the given RangeDataSelectorDescriptor	
	**/
	
	public RangeDataSelector(RangeDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fStart = descriptor.getStart();
		fLength = descriptor.getLength();
	}
	
	
	/**
	 * Returns the current range start index of this RangeDataSelector.
	 *
	 * @return The current range start index of this RangeDataSelector	
	**/
	
	public int getStart()
	{
		return (fStart);
	}
	
	
	/**
	 * Returns the current range length of this RangeDataSelector.
	 *
	 * @return The current range length of this RangeDataSelector	
	**/
	
	public int getLength()
	{
		return (fLength);
	}
}
