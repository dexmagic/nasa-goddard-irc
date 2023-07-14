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

package gov.nasa.gsfc.irc.gui.messages;

import java.util.Comparator;

import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * Comparator used to compare FieldDescriptor objects. 
 * The Strings returned from getDisplayName are used in the comparison.
 *
 * @version	$Date: 2005/01/20 08:08:14 $
 * @author  Troy Ames
 */

public class FieldDescriptorComparator implements Comparator
{
    /** 
     * Compares two Descriptor objects
     */
    public int compare(Object o1, Object o2)
    {
        FieldDescriptor f1 = (FieldDescriptor)o1;
        FieldDescriptor f2 = (FieldDescriptor)o2;

        return f1.getDisplayName().compareTo(f2.getDisplayName());
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: FieldDescriptorComparator.java,v $
//  Revision 1.1  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//