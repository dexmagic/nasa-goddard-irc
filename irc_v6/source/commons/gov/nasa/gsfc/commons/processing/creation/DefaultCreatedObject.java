//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//  $Log: DefaultCreatedObject.java,v $
//  Revision 1.1  2006/08/03 20:20:55  chostetter_cvs
//  Fixed proxy BasisBundle name creation bug
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

package gov.nasa.gsfc.commons.processing.creation;

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

public class DefaultCreatedObject extends AbstractCreatedObject
{
	/**
	 *  Constructs a new CreatedObject created by the given Creator.
	 *
	 *  @param creator The Creator of the new CreatedObject
	 **/

	public DefaultCreatedObject(Creator creator)
	{
		super(creator);
	}

	
	/**
	 *  Constructs a new CreatedObject created by the Creator having the given 
	 *  MemberId.
	 *
	 *  @param creatorId The MemberId of the Creator of the new CreatedObject
	 **/

	public DefaultCreatedObject(MemberId creatorId)
	{
		super(creatorId);
	}

	
	/**
	 * Returns a clone of this CreatedObject.
	 *
	 * @return A clone of this CreatedObject
	 **/

	protected Object clone()
	{
		DefaultCreatedObject result = null;
		
		result = (DefaultCreatedObject) super.clone();

		return (result);
	}
}
