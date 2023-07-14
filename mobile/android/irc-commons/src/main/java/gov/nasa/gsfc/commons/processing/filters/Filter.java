//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Filter.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.processing.filters;

/**
 *	This interface defines the abstract concept of a filter.  Basically
 *  a filter is anything that can accept or deny something.  A filter is
 *  is generally applied to a group of objects.  This allows the filter to 
 *  determine a valid subset of a more complete superset.  An often seen
 *  example of this occurs with filtering a list of files based on their
 *  extension (see the java.io.FileFilter) but this interface opens this idea 
 *  up to any object and any criteria.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author Ken Wootton
 */

public interface Filter
{
	/**
	 *	Test whether or not the given object should be included within
	 *  a group of objects.
	 *
	 *  @param objectToTest  the object to test
	 *
	 *  @return  whether or not the object should be included
	 */
	
	public boolean accept(Object objectToTest);
}
