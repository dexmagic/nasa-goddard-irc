//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: HasTimeStamp.java,v $
//	Revision 1.2  2006/01/23 17:59:55  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.2  2004/06/02 23:00:49  chostetter_cvs
//	Namespace, TimeStamp tweaks
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

package gov.nasa.gsfc.commons.numerics.time;

import java.util.Date;


/**
 *  The HasTimeStamp interface specifies the methods that all Objects having 
 *  a TimeStamp must implement.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/01/23 17:59:55 $
 *  @author Carl F. Hostetter
**/

public interface HasTimeStamp
{
	/**
	 *  Returns the TimeStamp of this Object.
	 *
	 *  @return	The TimeStamp of this Object
	**/

	public Date getTimeStamp();
	
	
	/**
	 *  Compares the timestamp of this CreatedObject to the given Date.
	 *  
	 *  @param date A Date
	 *  @return	The relative order of creation of this CreatedObject with respect 
	 * 		to the given Date
	 **/

	public int compareTo(Date date);
	

	/**
	 *  Compares this CreatedObject to the given TimeStampedObject.
	 *  
	 *  @param object An Object
	 *  @return	The relative order of creation of this CreatedObject with respect 
	 * 		to the given Object
	 **/

	public int compareTo(Object timeStampedObject);
}
