//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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


package gov.nasa.gsfc.irc.library.processors.strategies.coadder;

import gov.nasa.gsfc.irc.data.BasisSet;


/**
 *  A coadder strategy receives a basis set from the CoadderProcessor and returns
 *  an array with indices into the basis set that define when coadding should occur.
 *  A strategy based on a fixed number of samples per coadd would simply fill the array with
 *  regularly spaced intervals (taking into account leftover samples from previous
 *  basis set) that indicate when coadding should occur.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Id: CoadderStrategy.java,v 1.8 2006/06/26 20:07:16 smaher_cvs Exp $
 *  @author Steve maher
**/
public interface CoadderStrategy {
    
    /**
     * Special index code that indicates not to use any indices (data) from
     * the current basis set and instead use the existing contents
     * in the coadder algorithm.
     */
    public static final int INDEX_MEANING_COADD_PREVIOUS_LEFTOVERS = -1;

    /**
     * Return an array containing the zero-based indices into
     * the provided basis set at which point coadditions should
     * be performed.  The coadd for the current group of samples
     * <b>INCLUDES</b> the value at the specified index.
     * <p>
     * The index values in the array MUST be sorted in ascending
     * order; duplicates are allowed and will be ignored in the
     * CoaddProcessor.  The reasoning for this contract is that
     * sorted values are inherent to some coadder strategies, so don't 
     * sort unecessarily in this CoaddProcessor.  Furthermore, duplicate removal is relatively easy to
     * do in the CoaddProcessor, so don't force the coadder strategies to
     * do it.
     * <p>
     * Example: returning [1,3] would specifying coadding items 0,1
     * (possibly including carry-over from previous basis set) and coadding
     * items 2,3.
     * @param basisSet
     * @param numberSamplesSinceLastCoadd
     * @return
     */
    int[] getCoaddIndexPoints(BasisSet basisSet, int numberSamplesSinceLastCoadd);    
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: CoadderStrategy.java,v $
//	Revision 1.8  2006/06/26 20:07:16  smaher_cvs
//	Comments
//	
//	Revision 1.7  2006/05/10 17:29:11  smaher_cvs
//	Changed coadd index array to be a primitive array; fixed some bugs; have a working chop coadder with this checkin.
//	
//	Revision 1.6  2005/09/27 07:46:41  smaher_cvs
//	Implemented IntraBasisSetDataThresholdStrategy (not tested yet)
//	
//	Revision 1.5  2005/09/23 20:56:45  smaher_cvs
//	Implemented and tested new Coadder with strategy pattern
//	
//	Revision 1.4  2005/09/19 14:55:18  smaher_cvs
//	Comments
//	
//	Revision 1.3  2005/09/19 14:51:16  smaher_cvs
//	Comment
//	
//	Revision 1.2  2005/09/16 20:58:43  smaher_cvs
//	Checkpointing Coadder Strategy design.
//	
//	Revision 1.1  2005/09/16 20:41:52  smaher_cvs
//	Checkpointing Coadder performance improvements.
//	