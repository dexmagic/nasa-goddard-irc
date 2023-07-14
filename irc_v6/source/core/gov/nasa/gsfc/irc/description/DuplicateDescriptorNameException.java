//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DuplicateDescriptorNameException.java,v $
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
//
//  Revision 1.1.2.1  2004/02/04 22:37:36  chostetter_cvs
//  More package changes.
//  My local CVS version history seems to have gotten hosed, so this may replace everything in the Fix_Pipeline branch as a new addition.
//
//
//   3	IRC	   1.2		 11/13/2001 4:34:30 PMJohn Higinbotham Javadoc
//		update.
//   2	IRC	   1.1		 12/8/2000 10:26:34 AMJohn Higinbotham Code review
//		changes.
//   1	IRC	   1.0		 10/27/2000 5:13:32 PMSteve Clark	 
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description;


/**
 * A DuplicateDescriptorNameException indicates that an attempt has been made 
 * to assign a name to some Descriptor when another Descriptor already has 
 * that name.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version  $Date: 2004/07/12 14:26:23 $
 * @author Steve Clark
 */
 
public class DuplicateDescriptorNameException extends DescriptorException
{
	private static final String MSG_PREFIX		 = "Attempt to redefine ";
	private static final String MSG_ROLE_CONNECTOR = " as ";
	private static final String DEFAULT_NAME	   = "<unspecified>";


	/**
	 * Default constructor.
	 */
	public DuplicateDescriptorNameException()
	{
		this(DEFAULT_NAME);
	}

	/**
	 * Construct a DuplicateDescriptorNameException for some name in the 
	 * specified role.
	 *
	 * @param name Name which has been redefined
	 * @param role Role in which redefinition occurred
	 */
	public DuplicateDescriptorNameException(String name, String role)
	{
		super(MSG_PREFIX + name + MSG_ROLE_CONNECTOR + role);
	}

	/**
	 * Construct a DuplicateDescriptorNameException for some name with no
	 * specified role.
	 *
	 * @param name	Name which has been redefined
	 */
	public DuplicateDescriptorNameException(String name)
	{
		super(MSG_PREFIX + name);
	}
}
