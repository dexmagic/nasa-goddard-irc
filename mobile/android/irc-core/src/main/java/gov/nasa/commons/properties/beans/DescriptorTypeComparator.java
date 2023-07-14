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
package gov.nasa.gsfc.commons.properties.beans;

import java.beans.PropertyDescriptor;
import java.util.Comparator;

/**
 * Comparator used to compare java.beans.PropertyDescriptor objects. 
 * The Strings returned from getPropertyType().getName() are used in the 
 * comparison.
 *
 * @version $Date $
 * @author	Troy Ames
 */

public class DescriptorTypeComparator implements Comparator
{
    /** 
     * Compares two PropertyDescriptor objects
     */
    public int compare(Object o1, Object o2)
    {
    	PropertyDescriptor p1 = (PropertyDescriptor)o1;
    	PropertyDescriptor p2 = (PropertyDescriptor)o2;

        String p1ClassName = p1.getPropertyType().getName();
        String p2ClassName = p2.getPropertyType().getName();
        return p1ClassName.compareTo(p2ClassName);
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorTypeComparator.java,v $
//  Revision 1.1  2005/01/27 21:55:15  tames
//  Relocated.
//
//