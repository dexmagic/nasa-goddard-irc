//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Annotation.java,v $
//  Revision 1.4  2006/08/01 19:55:48  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.3  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.2  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
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

package gov.nasa.gsfc.commons.processing.annotation;

import java.io.Serializable;

import gov.nasa.gsfc.commons.processing.creation.AbstractCreatedObject;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;


/**
 *  An Annotation is a time-stamped Object used to store a comment.
 *  
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/08/01 19:55:48 $
 *  @author	John Higinbotham 
**/
public class Annotation extends AbstractCreatedObject implements Serializable
{
	//--- Vars
	private String fComment			= null;  // Annotation text,
	private final static int MAX_CHARS = 30;	// Max size of abbreviated comment 

	/**
	 * Create a new Annotation. 
	 * 
	 * @param creatorId The MemberId of the Creator of the new Annotation
	 * @param comment The text of the new Annotation
	**/
	
	public Annotation(MemberId creatorId, String comment)
	{
		super(creatorId);
		
		fComment   = comment;
	}

	/**
	 * Get the annotation comment.
	 *
	 * @return Returns the annotation comment.
	**/
	
	public String getComment()
	{
		return fComment;
	}

	/**
	 * Returns a string representation of this Annotation. 
	 *
	 * @return Returns string representation of annotation for preview displays.
	**/
	
	public String toString()
	{
		String detail;
		
		if (fComment.length() < MAX_CHARS)
		{
			detail = getTimeStamp() +  ":: " + fComment;
		}
		else
		{
			detail = getTimeStamp() +  ":: " + 
				fComment.substring(0,MAX_CHARS - 1) + "...";
		}
		
		return (detail);
	}
}
