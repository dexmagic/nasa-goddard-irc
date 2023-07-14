//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.commons.properties.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * BeanInfo for StreamConfiguration bean
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/10 17:29:55 $
 * @author	smaher
 */

public class BBeanInfo extends SimpleBeanInfo
{

    private final static Class beanClass = B.class;
    private final static Class customizerClass = null;
         

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {  
            PropertyDescriptor p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
            
            PropertyDescriptor rv[] = {

	            p4 = new PropertyDescriptor("b", beanClass),             
            };
            
               

         for (int i = 0; i < rv.length; i++)
         {
        	 rv[i].setWriteMethod(null);
         }

         return rv;
        } catch (IntrospectionException e) {
           throw new Error(e.toString());
          }
       }        
       

}



//--- Development History  ---------------------------------------------------
//
//$Log: BBeanInfo.java,v $
//Revision 1.1  2006/05/10 17:29:55  smaher_cvs
//Added test for overriding read-only BeanInfo properties.
//
//Revision 1.1  2006/03/09 16:07:01  smaher_cvs
//Package name refactoring
//
//Revision 1.7  2006/02/14 14:21:17  smaher_cvs
//State refactoring (removed extra NStep processing)
//
//Revision 1.6  2006/01/05 18:23:20  smaher_cvs
//Fixed dropped frame problem when partial frame ended inside stream core, status dump, or stream configuration; name change
//
//Revision 1.5  2005/06/29 19:21:18  smaher_cvs
//Support for firmware V9; @see link update
//
//Revision 1.4  2005/04/01 16:55:01  smaher_cvs
//Renamed B and C parameters.
//
//Revision 1.3  2005/02/12 22:17:28  smaher_cvs
//Added comments; added fireCommandMessage() in execute(); removed Startup component.
//
//Revision 1.2  2005/02/03 21:32:42  smaher_cvs
//Added beaninfo to make properties read only to the editor.
//
//Revision 1.1  2005/01/11 21:37:35  smaher_cvs
//Made into bean and added to "diagnostics" to be viewable in component browser optionally.
//
//Revision 1.1  2004/12/29 21:41:34  smaher_cvs
//Added BasisBundleParserAdapter properties and moved properties to separate package.
//
//Revision 1.1  2004/12/20 21:52:30  smaher_cvs
//Finished properties for Parser.
//
//Revision 1.1  2004/12/17 21:15:01  smaher_cvs
//Testing property editting.
//
//Revision 1.4  2004/12/10 22:03:02  smaher_cvs
//Checkpointing savable properties work.
//
//Revision 1.3  2004/12/06 20:25:32  smaher_cvs
//*** empty log message ***
//
//Revision 1.2  2004/12/04 16:56:47  smaher_cvs
//*** empty log message ***
//
//Revision 1.1  2004/12/03 13:41:41  smaher_cvs
//Working on CommandComponents.
//