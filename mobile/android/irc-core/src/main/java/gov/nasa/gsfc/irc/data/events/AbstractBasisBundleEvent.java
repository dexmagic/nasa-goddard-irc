//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/events/AbstractBasisBundleEvent.java,v 1.3 2006/08/01 19:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.data.events;

import java.util.EventObject;

import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;


/**
 * An AbstractBasisBundleEvent is the common parent event for events from a
 * BasisBundle. BasisBundle events convey information about the state and
 * structure of a BasisBundle, or delivers the BasisBundle's newly available
 * BasisSets.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/08/01 19:55:48 $
 * @author Troy Ames
 */
public abstract class AbstractBasisBundleEvent extends EventObject
{
	private BasisBundleId fBasisBundleId;
	private BasisBundleDescriptor fDescriptor;
	
	/**
	 * Constructs a new Event for the given source.
	 * 
	 * @param source The BasisBundle source of the new event
	 */
	public AbstractBasisBundleEvent(BasisBundle source)
	{
		super(source);
		
		fBasisBundleId = source.getBasisBundleId();
		
		fDescriptor = source.getDescriptor();
	}
		
	/**
	 * Constructs a new Event for the given source indicating that 
	 * the BasisBundle indicated by the given BasisBundleId has the new 
	 * structure described by the given BasisBundleDescriptor.
	 * 
	 * @param source The BasisBundle source of the new event
	 * @param descriptor A BasisBundleDescritor describing the new structure 
	 * 		of the indicated BasisBundle
	 */	
	public AbstractBasisBundleEvent(BasisBundle source, BasisBundleDescriptor descriptor)
	{
		this(source);
		
		fDescriptor = descriptor;
	}

	/**
	 * Returns the BasisBundleId of the BasisBundle that is associated with 
	 * this Event
	 * 
	 * @return The BasisBundleId of the BasisBundle that is associated with 
	 * 		this Event
	 */
	public BasisBundleId getBasisBundleId()
	{
		return (fBasisBundleId);
	}
		
	/**
	 * Returns the BasisBundleDescriptor that describes the structure of 
	 * the BasisBundle associated with this Event, and thus the 
	 * structure of the BasisSets it produces.
	 * 
	 * @return The BasisBundleDescriptor that describes the structure of 
	 * 		the BasisBundle associated with this Event
	 */
	public BasisBundleDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	/**
	 * Returns a String representation of this BasisBundleEvent.
	 * 
	 * @return A String representation of this BasisBundleEvent
	 */
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer(getClass().getName() + " received from " + 
				getSource());
		
		stringRep.append("\nBasisBundle " + fBasisBundleId);
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractBasisBundleEvent.java,v $
//  Revision 1.3  2006/08/01 19:55:48  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.2  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/09/13 22:29:38  tames
//  Updates to reflect BasisBundleEvent refactoring.
//
//
