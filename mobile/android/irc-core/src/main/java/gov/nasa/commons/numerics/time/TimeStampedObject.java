//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//  $Log: TimeStampedObject.java,v $
//  Revision 1.3  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.2  2004/05/27 18:23:04  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
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
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  A TimeStampedObject records its time of construction.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2005/04/06 14:59:46 $
 *  @author Carl F. Hostetter
**/

public class TimeStampedObject implements HasTimeStamp
{
	private static final String CLASS_NAME = 
		TimeStampedObject.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
		
	private final Date fTimeStamp;
	
	
	/**
	 *  Returns the timestamp of this TimeStampedObject (i.e., the Date at  
	 *  which it was constructed).
	 *
	 *  @return	The timestamp of this TimeStampedObject (i.e., the Date at  
	 *  	which it was constructed)
	 **/

	public TimeStampedObject()
	{
		fTimeStamp = new Date();
		
		if (sLogger.isLoggable(Level.FINEST))
		{
			String message = "New " + getClass() + " created at " + fTimeStamp;
			
			sLogger.logp(Level.FINEST, CLASS_NAME, 
				"TimeStampedObject (ctor)", message);
		}
	}


	/**
	 *  Returns the timestamp of this TimeStampedObject (i.e., the Date at  
	 *  which it was constructed).
	 *
	 *  @return	The timestamp of this TimeStampedObject (i.e., the Date at  
	 *  	which it was constructed)
	**/

	public Date getTimeStamp()
	{
		return (fTimeStamp);	
	}


	/**
	 *  Compares the timestamp of this TimeStampedObject to the given Date.
	 *  
	 *  @param date A Date
	 *  @return	The relative order of this TimeStampedObject with respect 
	 *  	to the given Date
	 **/

	public int compareTo(Date date)
	{
		return (fTimeStamp.compareTo(date));
	}
	
	
	/**
	 *  Compares this TimeStampedObject to the given Object.
	 *  
	 *  @param object An Object
	 *  @return	The relative order of this TimeStampedObject with respect 
	 *  	to the given Object
	**/

	public int compareTo(Object object)
	{
		return (fTimeStamp.compareTo((Date) object));
	}
	
	
	/**
	 * Returns a String representation of this TimeStampedObject. 
	 *
	 * <p>Here, simply returns the timestamp of this TimeStampedObject.
	 *
	 * @return A String representation of this TimeStampedObject
	**/
	
	public String toString()
	{
		String detail = "TimeStampedObject constructed at " + fTimeStamp;
		
		return (detail);
	}
}
