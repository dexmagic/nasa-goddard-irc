//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//  $Log: AbstractCreatedObject.java,v $
//  Revision 1.3  2006/08/03 20:20:55  chostetter_cvs
//  Fixed proxy BasisBundle name creation bug
//
//  Revision 1.2  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.1  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2004/08/09 17:24:24  tames_cvs
//  Added setMemberId method
//
//  Revision 1.4  2004/07/12 19:04:45  chostetter_cvs
//  Added ability to find BasisBundleId, Components by their fully-qualified name
//
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/07/11 21:39:03  chostetter_cvs
//  AlgorithmTest tweaks
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.11  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.10  2004/06/03 00:19:59  chostetter_cvs
//  Organized imports
//
//  Revision 1.9  2004/06/02 22:33:27  chostetter_cvs
//  Namespace revisions
//
//  Revision 1.8  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
//  Revision 1.7  2004/05/28 05:58:19  chostetter_cvs
//  More Namespace, DataSpace, Descriptor worl
//
//  Revision 1.6  2004/05/27 23:29:26  chostetter_cvs
//  More Namespace related changes
//
//  Revision 1.5  2004/05/27 16:11:17  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.4  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.3  2004/05/16 15:43:08  chostetter_cvs
//  Tweaked JavaDoc
//
//  Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.processing.creation;

import java.io.Serializable;
import java.util.Date;

import gov.nasa.gsfc.commons.numerics.time.TimeStampedObject;
import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberId;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;


/**
 *  A CreatedObject is an Object that has a MemberId and a creation time stamp.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/03 20:20:55 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractCreatedObject 
	implements Serializable, CreatedObject
{
	private MemberId fCreatorId;
	private final Date fTimeStamp = new Date();

	
	/**
	 *  Constructs a new CreatedObject created by the given Creator.
	 *
	 *  @param creator The Creator of the new CreatedObject
	 **/

	public AbstractCreatedObject(Creator creator)
	{
		this(creator.getMemberId());
	}

	
	/**
	 *  Constructs a new CreatedObject created by the Creator having the given 
	 *  MemberId.
	 *
	 *  @param memberId The MemberId of the Creator of the new CreatedObject
	 **/

	public AbstractCreatedObject(MemberId memberId)
	{
		if (memberId != null)
		{
			fCreatorId = memberId;
		}
		else
		{
			fCreatorId = new DefaultMemberId(null, (String)  null);
		}
	}

	
	/**
	 * Returns a clone of this CreatedObject.
	 *
	 * @return A clone of this CreatedObject
	 **/

	protected Object clone()
	{
		AbstractCreatedObject result = null;
		
		try
		{
			result = (AbstractCreatedObject) super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			
		}
		
		result.fCreatorId = fCreatorId;
		
		return (result);
	}
	
	
	/**
	 *  Returns the MemberId of the Creator of this CreatedObject.
	 *
	 *  @return	The MemberId of the Creator of this CreatedObject
	**/

	public MemberId getCreatorId()
	{
		return (fCreatorId);
	}
	
	
	/**
	 *  Returns the timestamp of this CreatedObject (i.e., the Date at which it 
	 *  was constructed).
	 *
	 *  @return	The timestamp of this CreatedObject (i.e., the Date at which it 
	 * 		was constructed)
	 **/

	public Date getTimeStamp()
	{
		return (fTimeStamp);	
	}


	/**
	 *  Compares the timestamp of this CreatedObject to the given Date.
	 *  
	 *  @param date A Date
	 *  @return	The relative order of creation of this CreatedObject with respect 
	 * 		to the given Date
	 **/

	public int compareTo(Date date)
	{
		return (fTimeStamp.compareTo(date));
	}
	
	
	/**
	 *  Compares this CreatedObject to the given TimeStampedObject.
	 *  
	 *  @param object An Object
	 *  @return	The relative order of creation of this CreatedObject with respect 
	 * 		to the given Object
	 **/

	public int compareTo(Object timeStampedObject)
	{
		Date timeStamp = 
			((TimeStampedObject) timeStampedObject).getTimeStamp();
		
		return (fTimeStamp.compareTo(timeStamp));
	}
	
	
	/**
	 * Returns a String representation of this AbstractCreatedObject. 
	 *
	 * <p>Here, simply returns a statement of when and by which Creator this 
	 * AbstractCreatedObject was created.
	 *
	 * @return A String representation of this TimeStampedObject
	**/
	
	public String toString()
	{
		String detail = "[" + getClass() + "], created by " + fCreatorId + 
			" at " + getTimeStamp();
			
		return (detail);
	}
}
