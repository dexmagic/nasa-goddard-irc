//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/BasisSetEvent.java,v 1.6 2005/09/13 22:29:38 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Devlopment history is located at the end of the file.
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
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;


/**
 * A BasisSetEvent contains a BasisSet of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/13 22:29:38 $
 * @author Carl F. Hostetter
 * @author	Troy Ames
 */
public class BasisSetEvent extends AbstractBasisBundleEvent
{
	private BasisSet fBasisSet;
	private boolean fIsStartOfNewBasisSequence = false;
	
	/**
	 * Constructs a new BasisSetEvent for the given BasisBundle source and 
	 * containing the given BasisSet.
	 * 
	 * @param source The BasisBundle source of the new BasisSetEvent
	 * @param basisSet A BasisSet
	 */
	public BasisSetEvent(BasisBundle source, BasisSet basisSet)
	{
		super(source);
		
		fBasisSet = basisSet;
	}
		
	/**
	 * Constructs a new BasisSetEvent for the given BasisBundle source and 
	 * containing the given BasisSet, and indicating that the given BasisSet starts 
	 * a new monotonic sequence of basis values in the source BasisBundle according 
	 * to the given flag.
	 * 
	 * @param source The BasisBundle source of the new BasisSetEvent
	 * @param basisSet A BasisSet
	 * @param startsNewBasisSequence True if the given BasisSet starts a new 
	 * 		monotonic sequence of basis values in the source BasisBundle, false 
	 * 		otherwise
	 */	
	public BasisSetEvent(BasisBundle source, BasisSet basisSet, 
		boolean startsNewBasisSequence)
	{
		super(source);
		
		fBasisSet = basisSet;
		
		fIsStartOfNewBasisSequence = startsNewBasisSequence;
	}
	
	
	/**
	 * Returns the BasisSet contained in this BasisSetEvent.
	 * 
	 * @return The BasisSet contained in this BasisSetEvent
	 */
	public BasisSet getBasisSet()
	{
		return (fBasisSet);
	}

	/**
	 * Returns true if the BasisSet associated with this BasisSetEvent starts a new 
	 * monotonic sequence of basis values in its source BasisBundle, false otherwise.
	 * 
	 * @return True if the BasisSet associated with this BasisSetEvent starts a new 
	 * 		monotonic sequence of basis values in its source BasisBundle, false 
	 * 		otherwise
	 */
	public boolean isStartOfNewBasisSequence()
	{
		return (fIsStartOfNewBasisSequence);
	}
		
	/**
	 * Returns a String representation of this BasisSetEvent.
	 * 
	 * @return A String representation of this BasisSetEvent
	 */	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer("BasisSet created at: " + 
			getBasisSet().getTimeStamp() + " by " + 
			getBasisSet().getBasisBundleSourceId() + "\n");
		
		int numSamples = getBasisSet().getSize();
		
		stringRep.append("Size: " + numSamples + "\n");
		
		DataBuffer basisBuffer = getBasisSet().getBasisBuffer();
		
		if (fIsStartOfNewBasisSequence)
		{
			stringRep.append("\nIs start of new basis sequence");
		}
		
		if (basisBuffer.isRealValued())
		{
			stringRep.append("First basis value: " + 
				basisBuffer.getAsDouble(0) + "\n");
			stringRep.append("Last basis value: " + 
				basisBuffer.getAsDouble(getBasisSet().getSize() - 1) + "\n");
		}
		else
		{
			stringRep.append("First basis value: " + 
				basisBuffer.getAsLong(0) + "\n");
			stringRep.append("Last basis value: " + 
				basisBuffer.getAsLong(getBasisSet().getSize() - 1) + "\n");
		}

		stringRep.append("Basis amount: " + getBasisSet().getBasisAmount());
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetEvent.java,v $
//  Revision 1.6  2005/09/13 22:29:38  tames
//  Updates to reflect BasisBundleEvent refactoring.
//
//  Revision 1.5  2005/07/13 20:09:45  tames
//  File header change only.
//
//  Revision 1.4  2005/04/05 20:35:36  chostetter_cvs
//  Fixed problem with release status of BasisSets from which a copy was made; fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//
//  Revision 1.3  2005/04/04 15:40:59  chostetter_cvs
//  Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
//  Revision 1.2  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.1  2004/07/21 14:21:41  chostetter_cvs
//  Moved into subpackage
//
//  Revision 1.11  2004/07/14 00:33:49  chostetter_cvs
//  More Algorithm, data testing. Fixed slice bug.
//
//  Revision 1.10  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.9  2004/07/11 07:30:35  chostetter_cvs
//  More data request work
//
//  Revision 1.8  2004/06/30 20:56:20  chostetter_cvs
//  BasisSet is now an interface
//
//  Revision 1.7  2004/06/16 22:17:05  chostetter_cvs
//  Check in for long weekend
//
//  Revision 1.6  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.5  2004/06/04 21:14:30  chostetter_cvs
//  Further tweaks in support of data testing
//
//  Revision 1.4  2004/06/04 17:28:32  chostetter_cvs
//  More data tweaks. Ready for testing.
//
//  Revision 1.3  2004/06/04 05:34:42  chostetter_cvs
//  Further data, Algorithm, and Component work
//
//  Revision 1.2  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
