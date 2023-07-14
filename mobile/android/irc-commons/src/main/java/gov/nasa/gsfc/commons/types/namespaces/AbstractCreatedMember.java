//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.util.Date;

import gov.nasa.gsfc.commons.processing.creation.CreatedObject;
import gov.nasa.gsfc.commons.processing.creation.DefaultCreatedObject;


/**
 *  A CreatedMember is a Member that has a Creator.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/03 20:20:55 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractCreatedMember extends AbstractMember 
	implements CreatedMember
{
	private CreatedObject fCreationInfo;
	
	
	/**
	 *  Constructs a new CreatedMember having the given base name, and name 
	 *  qualifier, and created by the Creator that has the given MemberId.
	 * 
	 *  @param name The base name of the new CreatedMember
	 *  @param nameQualifier The name qualifier of the new CreatedMember
	 *  @param creatorId The MemberId of the Creator of the new CreatedMember
	 **/

	public AbstractCreatedMember(String name, String nameQualifier, 
		MemberId creatorId)
	{
		super(name, nameQualifier);
		
		fCreationInfo = new DefaultCreatedObject(creatorId);
	}
		
	
	/**
	 *  Constructs a new CreatedMember having the given base name, and whose name 
	 *  qualifier is set to the fully-qualified name of the given Object, and 
	 *  created by the Creator that has the given MemberId. If the given Object 
	 *  has a fully-qualified name property, the name qualifier of this Member will 
	 *  be updated as needed to reflect any subsequent changes in the fully-
	 *  qualified name of the given Object.
	 * 
	 *  @param name The base name of the new CreatedMember
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new CreatedMember
	 *  @param creatorId The MemberId of the Creator of the new CreatedMember
	 **/

	public AbstractCreatedMember(String name, HasFullyQualifiedName nameQualifier, 
		MemberId creatorId)
	{
		super(name, nameQualifier);
		
		fCreationInfo = new DefaultCreatedObject(creatorId);
	}
	
	
	/**
	 * Returns a clone of this CreatedMember.
	 *
	 * @return A clone of this CreatedMember
	 **/

	public Object clone()
	{
		AbstractCreatedMember result = null;
			
		result = (AbstractCreatedMember) super.clone();
		
		result.fCreationInfo = 
			new DefaultCreatedObject(fCreationInfo.getCreatorId());
		
		return (result);
	}
	
	
	/**
	 *  Sets the MemberId of the Creator of this CreatedMember to the given 
	 *  MemberId.
	 *
	 *  @param creatorId The new MemberId of the Creator of this CreatedMember
	**/
	
	protected void setCreatorId(MemberId creatorId)
	{
		super.setMemberId(creatorId);
	}
	
		
	/**
	 *  Returns the MemberId of the Creator of this CreatedMember.
	 *
	 *  @return	The MemberId of the Creator of this CreatedMember
	 **/

	public MemberId getCreatorId()
	{
		return (fCreationInfo.getCreatorId());
	}
	

	/**
	 *  Returns the timestamp of this CreatedMember (i.e., the Date at which it 
	 *  was constructed).
	 *
	 *  @return	The timestamp of this CreatedMember (i.e., the Date at which it 
	 * 		was constructed)
	 **/

	public Date getTimeStamp()
	{
		return (fCreationInfo.getTimeStamp());
	}
	

	/**
	 *  Compares the timestamp of this CreatedMember to the given Date.
	 *  
	 *  @param date A Date
	 *  @return	The relative order of creation of this CreatedMember with respect 
	 * 		to the given Date
	 **/

	public int compareTo(Date date)
	{
		return (fCreationInfo.compareTo(date));
	}
	

	/**
	 *  Compares this CreatedMember to the given TimeStampedObject.
	 *  
	 *  @param object An Object
	 *  @return	The relative order of creation of this CreatedMember with respect 
	 * 		to the given Object
	 **/

	public int compareTo(Object timeStampedObject)
	{
		return (fCreationInfo.compareTo(timeStampedObject));
	}
	
	
	/**
	 *  Returns a String representation of this CreatedMember.
	 *
	 *  @return A String representation of this CreatedMember
	 **/

	public String toString()
	{
		return (super.toString() + "\n" + fCreationInfo);
	}
}

//--- Development History ----------------------------------------------------
//
// $Log: AbstractCreatedMember.java,v $
// Revision 1.3  2006/08/03 20:20:55  chostetter_cvs
// Fixed proxy BasisBundle name creation bug
//
// Revision 1.2  2006/08/01 19:55:47  chostetter_cvs
// Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
// Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
//
//
