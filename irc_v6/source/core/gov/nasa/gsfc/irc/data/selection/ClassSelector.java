//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/ClassSelector.java,v 1.1 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ClassSelector.java,v $
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

package gov.nasa.gsfc.irc.data.selection;

import gov.nasa.gsfc.irc.data.selection.description.ClassSelectorDescriptor;


/**
 * A ClassSelector selects Class of a given Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class ClassSelector extends AbstractDataSelector
{
	/**
	 * Constructs a new ClassSelector that will select the Class of any given 
	 * data Object.
	 *
	 * @param descriptor A ClassSelectorDescriptor
	 * @return A new ClassSelector that will select the Class of any given data 
	 * 		Object		
	**/
	
	public ClassSelector(ClassSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 *  Returns the Class of the given data Object.
	 *  
	 *  @param data A data Object
	 *  @return The Class of the given data Object
	 */
	
	public Object select(Object data)
	{
		Class result = null;
		
		if (data != null)
		{
			result = data.getClass();
		}
		else
		{
			super.select(data);
		}
		
		return (result);
	}
}
