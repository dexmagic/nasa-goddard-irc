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

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import gov.nasa.gsfc.commons.properties.beans.BeanUtilities;

/**
 *
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/10 17:29:55 $
 * @author	smaher
 */

public class BeanUtilitiesTest extends TestCase
{
    public class Bean1
    {
        Float fFloat;
        int fInt;
        public Float getFloat()
        {
            return fFloat;
        }
        public void setFloat(Float f)
        {
            fFloat = f;
        }
        public int getInt()
        {
            return fInt;
        }
        public void setInt(int i)
        {
            fInt = i;
        }

    }
    
    public class Bean2
    {
        Float fFloat;
        int fInt;
        public Float getFloat()
        {
            return fFloat;
        }
        public void setFloat(Float f)
        {
            fFloat = f;
        }
        public int getInt()
        {
            return fInt;
        }
        public void setInt(int i)
        {
            fInt = i;
        }

    }
    
    public void testUniquePropertyPrimitive() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Bean1 b1 = new Bean1();
        Bean1 b2 = new Bean1();
        assertFalse(BeanUtilities.isPropertyValueInBeanUnique("int", b1, new Object[] {b2}));
        b2.setInt(4);
        assertTrue(BeanUtilities.isPropertyValueInBeanUnique("int", b1, new Object[] {b2}));
        assertTrue(BeanUtilities.isPropertyValueInBeanUnique("int", b1, new Object[] {b1, b2}));
    }

    public void testUniquePropertyNonSourceBean() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Integer b1 = new Integer(0);
        Bean1 b2 = new Bean1();
        assertFalse(BeanUtilities.isPropertyValueUnique("int", b1, new Object[] {b2}));
        b2.setInt(4);
        assertTrue(BeanUtilities.isPropertyValueUnique("int", b1, new Object[] {b2}));
        
        // there's no Integer.getInt() method ... should print exception but return true 
        assertTrue(BeanUtilities.isPropertyValueUnique("int", b1, new Object[] {b1, b2}));
    }

    public void testUniquePropertySourceInOtherBeans() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Bean1 b1 = new Bean1();
        Bean1 b2 = new Bean1();
        assertFalse(BeanUtilities.isPropertyValueInBeanUnique("int", b1, new Object[] {b2}));
        b2.setInt(4);
        assertTrue(BeanUtilities.isPropertyValueInBeanUnique("int", b1, new Object[] {b1}));
        assertTrue(BeanUtilities.isPropertyValueInBeanUnique("int", b1, new Object[] {b1, b2}));
    }
    
    public void testUniquePropertyObject() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Bean1 b1 = new Bean1();
        Bean1 b2 = new Bean1();
        assertFalse(BeanUtilities.isPropertyValueInBeanUnique("float", b1, new Object[] {b2}));
        b2.setFloat(new Float(5.0f));
        assertTrue(BeanUtilities.isPropertyValueInBeanUnique("float", b1, new Object[] {b2}));

    }


    
    public void testUniquePropertyTwoClasses() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Bean1 b1 = new Bean1();
        Bean2 b2 = new Bean2();
        assertFalse(BeanUtilities.isPropertyValueInBeanUnique("float", b1, new Object[] {b2}));
        b2.setFloat(new Float(5.0f));
        assertTrue(BeanUtilities.isPropertyValueInBeanUnique("float", b1, new Object[] {b2}));
    }

	public void testCopyPropertiesSubclass() throws Exception
	{
		B b1 = new B();
		B b2 = new B();
		b1.setA(11);
		b1.setB(12);		
		b2.setA(21);
		b2.setB(22);
		
		BeanUtilities.copyProperties(b1, b2);
		assertEquals(11, b2.getA());
		assertEquals(12, b2.getB());		
	}

	public void testRestoreDefaultsSubclass() throws Exception
	{
		B b1 = new B();
		b1.setA(11);
		b1.setB(12);		
	
		
		BeanUtilities.restorePropertyDefaults(b1, null, false);
		assertEquals(-1, b1.getA());
		assertEquals(1, b1.getB());		
	}
	
	public void testReadOnlyBeanInfo() throws Exception
	{
		
		B b1 = new B();
		b1.setA(11);
		b1.setB(12);		
		
		BeanUtilities.restorePropertyDefaults(b1, null, true);	
		assertEquals(11, b1.getA());
		assertEquals(1, b1.getB());			
	}
}



//--- Development History  ---------------------------------------------------
//
//$Log: BeanUtilitiesTest.java,v $
//Revision 1.3  2006/05/10 17:29:55  smaher_cvs
//Added test for overriding read-only BeanInfo properties.
//
//Revision 1.2  2006/03/07 20:05:19  smaher_cvs
//Added version of restoreDefaultProperties that allows ignoring certain properties; needed to allow ignoring "name" b/c new namespace code was producing "name#1#2.IRC" names.
//
//Revision 1.1  2006/01/11 15:45:39  smaher_cvs
//Polished some bean property methods by changing to better name and exposing exceptions correctly.
//
//Revision 1.2  2005/03/15 17:19:32  chostetter_cvs
//Made DataBuffer an interface, organized imports
//
//Revision 1.1  2005/01/21 20:03:26  smaher_cvs
//Added methods to check uniqueness of property value across numerous beans.
//