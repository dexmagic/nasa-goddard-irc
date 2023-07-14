//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/BasisBundleEvent.java,v 1.9 2005/09/13 22:29:38 tames Exp $
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

import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;

/**
 * A BasisBundleEvent conveys information about the state and structure of a 
 * BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/13 22:29:38 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */
public class BasisBundleEvent extends AbstractBasisBundleEvent
{
	private boolean fHasNewStructure = false;
	private boolean fIsRequestingRelease = false;
	private boolean fIsClosed = false;
		
	/**
	 * Constructs a new BasisBundleEvent for the given source.
	 * 
	 * @param source The BasisBundle source of the new event
	 */
	public BasisBundleEvent(BasisBundle source)
	{
		super(source);
	}
		
	/**
	 * Constructs a new BasisBundleEvent for the given source indicating that 
	 * the BasisBundle indicated by the given BasisBundleId has the new 
	 * structure described by the given BasisBundleDescriptor.
	 * 
	 * @param source The BasisBundle source of the new event
	 * @param descriptor A BasisBundleDescritor describing the new structure 
	 * 		of the indicated BasisBundle
	 */	
	public BasisBundleEvent(BasisBundle source, BasisBundleDescriptor descriptor)
	{
		super(source, descriptor);
		
		fHasNewStructure = true;
	}
		
	/**
	 * Constructs a new BasisBundleEvent for the given source indicating that 
	 * the BasisBundle has a state indicated by the given state flags.
	 * 
	 * @param source The BasisBundle source of the new event
	 * @param basisBundleId The BasisBundleId of the BasisBundle that has 
	 * 		been closed
	 * @param isDiscontinuous True if the BasisBundle
	 */	
	public BasisBundleEvent(BasisBundle source,  
		boolean isRequestingRelease, boolean isClosed)
	{
		super(source);

		fIsRequestingRelease = isRequestingRelease;
		fIsClosed = isClosed;
	}
		
	/**
	 * Returns true if the BasisBundle associated with this BasisBundleEvent 
	 * has changed its structure, false otherwise.
	 * 
	 * @return True if the BasisBundle associated with this BasisBundleEvent 
	 * 		has changed its structure, false otherwise
	 */	
	public boolean hasNewStructure()
	{
		return (fHasNewStructure);
	}
	
	/**
	 * Returns true if the BasisBundle associated with this BasisBundleEvent 
	 * has been closed (i.e. will produce no further data), false otherwise.
	 * 
	 * @return True if the BasisBundle associated with this BasisBundleEvent 
	 * 		has been closed (i.e. will produce no further data), false 
	 * 		otherwise
	 */
	public boolean isClosed()
	{
		return (fIsClosed);
	}
	
	/**
	 * Returns true if the BasisBundle associated with this BasisBundleEvent 
	 * is requesting listeners to release any its BasisSets that it holds, false 
	 * otherwise.
	 * 
	 * @return True if the BasisBundle associated with this BasisBundleEvent 
	 * 		is requesting listeners to release any its BasisSets that it holds, 
	 * 		false otherwise
	 */
	public boolean isRequestingRelease()
	{
		return (fIsRequestingRelease);
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
		
		stringRep.append("\nBasisBundle " + getBasisBundleId());
		
		if (fHasNewStructure)
		{
			stringRep.append("\nHas new structure: ");
			stringRep.append("\nDescriptor: " + getDescriptor());
		}
		
		if (fIsRequestingRelease)
		{
			stringRep.append("\nIs requesting release");
		}
		
		if (fIsClosed)
		{
			stringRep.append("\nIs closed");
		}
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisBundleEvent.java,v $
//  Revision 1.9  2005/09/13 22:29:38  tames
//  Updates to reflect BasisBundleEvent refactoring.
//
//  Revision 1.8  2005/07/13 20:08:43  tames
//  Removed all obsolete references to a discontinuous buffer. Discontinuous
//  buffers are no longer visible to users.
//
//  Revision 1.7  2005/04/05 20:35:36  chostetter_cvs
//  Fixed problem with release status of BasisSets from which a copy was made; fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//
//  Revision 1.6  2005/04/04 15:40:59  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.5  2005/03/22 22:47:06  chostetter_cvs
//  Refactoring of BasisSet allocation, release
//
//  Revision 1.4  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.3  2004/07/28 19:11:25  chostetter_cvs
//  BasisBundle now alerts of impending discontinuity, BasisRequester copies and releases pending data at discontinuity
//
//  Revision 1.2  2004/07/22 16:28:03  chostetter_cvs
//  Various tweaks
//
//  Revision 1.1  2004/07/21 14:21:41  chostetter_cvs
//  Moved into subpackage
//
