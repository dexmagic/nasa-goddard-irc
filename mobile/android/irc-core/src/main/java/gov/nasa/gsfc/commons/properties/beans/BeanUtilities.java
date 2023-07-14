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

import java.util.logging.Level;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Some utilities related to working with bean properties.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/10 17:18:23 $
 * @author	smaher
 */

abstract public class BeanUtilities
{
	/**
	 * Logger for this class
	 */
	private static final Logger sLogger = Logger.getLogger(BeanUtilities.class
			.getName());

    /**
     * Copy all public properties from sourceBean to destBean.
     * Calls all setters in destBean that have a matching getter in sourceBean, 
     * using the getter in the sourceBean to get the property value.
     * @param sourceBean
     * @param destBean
     */
    public static void copyProperties(Object sourceBean, Object destBean) throws Exception
    {
        copyProperties(sourceBean, destBean, null, false);
    }
    
    
    /**
     * Copy public properties from sourceBean to destBean that are defined in
     * the sourceBean's bean info class(es).
     * Calls all setters in destBean that have a matching getter in sourceBean, 
     * using the getter in the sourceBean to get the property value.  Optionally,
     * a set of property names can be provided that will not be copied.  Read-only
     * properties, as defined in the BeanInfo, are still written.
     * @param sourceBean
     * @param destBean
     * @param ignoredProperties Names of propeties that shouldn't be copied, null otherwise
     * @param ignoreBeanInfoClasses Whether to ignore BeanInfo classes associated with the beans (which
     * for example, may specify read-only parameters that fail to receive a copy operation)
     * @throws Exception 
     */
    public static void copyBeanInfoPropertiesAndOverrideReadOnly(Object sourceBean, Object destBean, String ignoredProperties[]) throws Exception {

        // Get BeanInfo instances for both classes using Introspector
        BeanInfo sourceBeanInfo = null;
        BeanInfo destAllInfo = null;        
        try {
        	int flags = Introspector.USE_ALL_BEANINFO;
            sourceBeanInfo = Introspector.getBeanInfo(sourceBean.getClass(), flags);
            
        	flags = Introspector.IGNORE_ALL_BEANINFO;
            destAllInfo = Introspector.getBeanInfo(destBean.getClass(), flags);
        } catch (IntrospectionException e1) {

            e1.printStackTrace();
        }
        
        PropertyDescriptor[] sourceBeanInfoProps = sourceBeanInfo.getPropertyDescriptors();
        
        PropertyDescriptor[] destAllProps = destAllInfo.getPropertyDescriptors();        
        

        PropertyDescriptor[] destProps = new PropertyDescriptor[sourceBeanInfoProps.length];        
        
        for (int i = 0; i < sourceBeanInfoProps.length; i++)
		{
			String propName = sourceBeanInfoProps[i].getName().toLowerCase();
			for (int j = 0; j < destAllProps.length; j++)
			{
				if (propName.equals(destAllProps[j].getName().toLowerCase()))
				{
					destProps[i] = destAllProps[j];
					break;
				}
			}
		}
        
        copyProperties(sourceBean, destBean, sourceBeanInfoProps, destProps, ignoredProperties);
    }
    
    
    /**
	     * Copy public properties from sourceBean to destBean.
	     * Calls all setters in destBean that have a matching getter in sourceBean, 
	     * using the getter in the sourceBean to get the property value.  Optionally,
	     * a set of property names can be provided that will not be copied.
	     * @param sourceBean
	     * @param destBean
	     * @param ignoredProperties Names of propeties that shouldn't be copied, null otherwise
	     * @param copyOnlyBeanInfoProperties Whether to ignore BeanInfo classes associated with the beans (which
	     * for example, may specify read-only parameters that fail to receive a copy operation)
	     * @throws Exception 
	     */
	    public static void copyProperties(Object sourceBean, Object destBean, String ignoredProperties[], boolean copyOnlyBeanInfoProperties) throws Exception {
	
	        // Get BeanInfo instances for both classes using Introspector
	        BeanInfo sourceBeanInfo = null;
	        BeanInfo destBeanInfo = null;
	        try {
	        	int flags = (copyOnlyBeanInfoProperties == false) ? Introspector.IGNORE_ALL_BEANINFO : Introspector.USE_ALL_BEANINFO;
	            destBeanInfo = Introspector.getBeanInfo(destBean.getClass(), flags);
	            sourceBeanInfo = Introspector.getBeanInfo(sourceBean.getClass(), flags);
	        } catch (IntrospectionException e1) {
	
	            e1.printStackTrace();
	        }
	        
	        PropertyDescriptor sourceProps[] = sourceBeanInfo.getPropertyDescriptors();
	        PropertyDescriptor destProps[] = destBeanInfo.getPropertyDescriptors();
	        
	        copyProperties(sourceBean, destBean, sourceProps, destProps, ignoredProperties);
	    }
	    
	    
	    private static void copyProperties(Object sourceBean, Object destBean, 
	    		PropertyDescriptor sourceProps[], PropertyDescriptor destProps[], 
	    		String ignoredProperties[]) throws Exception
	    {
	        
	        if (ignoredProperties != null)
	        {
	            Arrays.sort(ignoredProperties);
	        }
	        
	        // For each write method in the destination class, try to find a matching
	        // read method in the source class.  If we find it, copy the property
	        // from the source to the destination.	        
	        for (int di = 0; di < destProps.length; di++)
	        {
	            
	        	Method destWriteMethod = destProps[di].getWriteMethod();
	            if (destWriteMethod != null)
	            {      
	                
	                String readMethodName = destWriteMethod.getName().replaceFirst("set", "get");
	                
	                if (ignoredProperties != null)
	                {
	                    String propertyName = readMethodName.substring(3);
	                    if (Arrays.binarySearch(ignoredProperties, propertyName) >= 0)
	                    {
	//                        System.out.println("copyProperties: skipped property: " + propertyName);
	                        continue;
	                    }
	                }
	                
	                for (int si = 0; si < sourceProps.length; si++)
	                {                   
	                    Method sourceReadMethod = sourceProps[si].getReadMethod();
	  
	                    if (sourceReadMethod != null && sourceReadMethod.getName().equals(readMethodName))
	                    {
	                        
	                        try {
	                            destWriteMethod.invoke(destBean, new Object[] {sourceReadMethod.invoke(sourceBean, null)});
	                        } catch (IllegalArgumentException e) {
	                        	e.printStackTrace();
	                        	sLogger.warning("Exception while calling write method -  : exception: "	+ e);
	                        	throw new Exception(e);            
	                        } catch (IllegalAccessException e) {
	                        	e.printStackTrace();
	                        	sLogger.warning("Exception while calling write method -  : exception: "	+ e);
	                        	throw new Exception(e);  
	                        } catch (InvocationTargetException e) {
	                        	e.printStackTrace();
	                        	sLogger.warning("Exception while calling write method -  : exception: "	+ e);
	                        	throw new Exception(e);  
	                        }                  
	                    }
	                }
	            }
	            else
	            {
	            	if (sLogger.isLoggable(Level.FINE))
	            	{
	            		sLogger
	            		.fine("Object, Object, String[], boolean - No write method for property : " + destProps[di].getName());
	            	}
	            }
	        }
	        
	    }

	/**
     * Copy public properties from sourceBean to destBean.
     * Calls all setters in destBean that have a matching getter in sourceBean, 
     * using the getter in the sourceBean to get the property value.  Optionally,
     * a set of property names can be provided.  These properties will not be copied.
     * @param sourceBean
     * @param destBean
     * @param ignoredProperties Names of propeties that shouldn't be copied, null otherwise
     * @param ignoreBeanInfoClasses Whether to ignore BeanInfo classes associated with the beans (which
     * for example, may specify read-only parameters that fail to receive a copy operation)
     * @throws Exception 
     */
    public static void copyProperties(Object sourceBean, Object destBean, String propertyNamesToCopy[]) throws Exception 
    
    {
    	if (sourceBean == null || destBean == null || propertyNamesToCopy == null)
    	{
    		throw new IllegalArgumentException("Null argument (source, dest, names) = " + sourceBean + ", " + destBean + ", " + propertyNamesToCopy);
    	}
    	
        // Get BeanInfo instances for both classes using Introspector
        BeanInfo sourceBeanInfo = null;
        BeanInfo destBeanInfo = null;
        try {
        	int flags = Introspector.IGNORE_ALL_BEANINFO;
            destBeanInfo = Introspector.getBeanInfo(destBean.getClass(), flags);
            sourceBeanInfo = Introspector.getBeanInfo(sourceBean.getClass(), flags);
        } catch (IntrospectionException e1) {

            e1.printStackTrace();
        }

        Arrays.sort(propertyNamesToCopy);
        
        // For each write method in the destination class, try to find a matching
        // read method in the source class.  If we find it, copy the property
        // from the source to the destination.
        PropertyDescriptor sourceProps[] = sourceBeanInfo.getPropertyDescriptors();
        PropertyDescriptor destProps[] = destBeanInfo.getPropertyDescriptors();
        int numCopied = 0;        
        for (int di = 0; di < destProps.length; di++)
        {
            
            Method destWriteMethod = destProps[di].getWriteMethod();
            if (destWriteMethod != null)
            {      
                
                String readMethodName = destWriteMethod.getName().replaceFirst("set", "get");
                
                String propertyName = readMethodName.substring(3);
                

                if (Arrays.binarySearch(propertyNamesToCopy, propertyName) >= 0)
                {
                	for (int si = 0; si < sourceProps.length; si++)
                	{                   
                		Method sourceReadMethod = sourceProps[si].getReadMethod();
                		
                		if (sourceReadMethod != null && sourceReadMethod.getName().equals(readMethodName))
                		{
                			
                			try {
                				destWriteMethod.invoke(destBean, new Object[] {sourceReadMethod.invoke(sourceBean, null)});

								if (sLogger.isLoggable(Level.FINE))
								{
									sLogger
											.fine("Object, Object, String[] - Copying property : propertyName="
													+ propertyName);
								}

                				numCopied++;
                			} catch (IllegalArgumentException e) {
                				e.printStackTrace();
                				sLogger.warning("Exception while calling write method -  : exception: "	+ e);
                				throw new Exception(e);            
                			} catch (IllegalAccessException e) {
                				e.printStackTrace();
                				sLogger.warning("Exception while calling write method -  : exception: "	+ e);
                				throw new Exception(e);  
                			} catch (InvocationTargetException e) {
                				e.printStackTrace();
                				sLogger.warning("Exception while calling write method -  : exception: "	+ e);
                				throw new Exception(e);  
                			}                  
                		}
                	}
                }
            }
        }
        
        if (numCopied != propertyNamesToCopy.length)
        {
        	throw new IllegalArgumentException("Provided " + propertyNamesToCopy.length + " to copy but only " + numCopied + " were copied.");
        }
        
    }
    
    /**
     * Restores bean properties to their default values.  Does not work for inner classes.
     * @param bean
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void restorePropertyDefaults(Object bean) throws Exception
    {
    	restorePropertyDefaults(bean, null, false);

    }
    
    /**
     * Restores bean properties to their default values.  If <code>copyOnlyBeanInfoProperties</code>
     * is true, then only those properties defined in the bean's bean info class(es) will
     * be restored (including supposedly "read-only" classes).  Properties named in <code>ignoredProperties</code> will not be 
     * restored.  
     * <p>
     * Note: does not work for inner classes.
     * @param bean
     * @param ignoredProperties Names of properties to ignore when restoring
     * @param copyOnlyBeanInfoProperties Whether to copy only the properties in the bean info
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void restorePropertyDefaults(Object bean, String[] ignoredProperties, boolean copyOnlyBeanInfoProperties) throws Exception
    {
    	Object placeholderBean = null;    
    	Class c = bean.getClass();
    	placeholderBean = c.newInstance();
    	if (copyOnlyBeanInfoProperties == true)
    	{
    		BeanUtilities.copyBeanInfoPropertiesAndOverrideReadOnly(placeholderBean, bean, ignoredProperties);
    	}
    	else
    	{
    		BeanUtilities.copyProperties(placeholderBean, bean, ignoredProperties, copyOnlyBeanInfoProperties);    	
    	}
    }    
    
    /**
     * Compares a property value of a source bean with the property value in an array of other source
     * beans and returns true if the source bean's value is unique.  NULL values are treated as values
     * (e.g., if source and "other" property are NULL the method returns false).  If the other property
     * doesn't exist, it is ignored.  The properties are retrieved using a getter method.  For example,
     * for a property name "myValue", this method will look for methods named <code>getMyValue()</code>.
     * <P>
     * The exceptions are thrown only when trying to retrieve the property value on the <code>sourceBean</code>.
     * 
     * @param propertyName Name of the property
     * @param sourceBean Bean containing ource value
     * @param otherBeans Beans to be compared to.
     * @return true if the property's value in <code>sourceBean</code> is different than the property's value in all the <code>otherBeans</code>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public static boolean isPropertyValueInBeanUnique(String propertyName, Object sourceBean, Object otherBeans[]) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        String propertyMethodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
        boolean ret = true;
        Object sourceValue = null;

        sourceValue = sourceBean.getClass().getMethod(propertyMethodName, null).invoke(sourceBean, null);

        for (int i = 0; otherBeans != null && i < otherBeans.length; i++)
            
        {
            Object otherBean = otherBeans[i];
            
            // Don't consider the case of the sourceBean being in the otherBeans
            if (otherBean == sourceBean)
            {
                continue;
            }
            
            Object otherValue = null;
            try
            {
                otherValue = otherBean.getClass().getMethod(propertyMethodName, null).invoke(otherBean, null);
                
                if (sourceValue != otherValue)  // handles NULLs as well
                {
                    if (sourceValue != null && sourceValue.equals(otherValue))
                    {
                        ret = false;
                        break;
                    }
                }
                else
                {
                    ret = false;
                    break;                        
                }
                
            } catch (Exception e)
            {
                e.printStackTrace();
            }      
        }

        
        return ret;
    }

    /**
     * Compares a property value with the property value in an array of other source
     * beans and returns true if the source bean's value is unique.  NULL values are treated as values
     * (e.g., if source and "other" property are NULL the method returns false).  If the other property
     * doesn't exist, it is ignored.  The properties are retrieved using a getter method.  For example,
     * for a property name "myValue", this method will look for methods named <code>getMyValue()</code>.
     * <P>
     * The exceptions are thrown only when trying to retrieve the property value on the <code>sourceBean</code>.
     * 
     * @param propertyName Name of the property
     * @param sourceBean Bean containing ource value
     * @param otherBeans Beans to be compared to.
     * @return true if the property's value in <code>sourceBean</code> is different than the property's value in all the <code>otherBeans</code>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public static boolean isPropertyValueUnique(String propertyName, Object sourceValue, Object otherBeans[]) 
    {
        String propertyMethodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
        boolean ret = true;
    
        for (int i = 0; otherBeans != null && i < otherBeans.length; i++)
            
        {
            Object otherBean = otherBeans[i];
            Object otherValue = null;
            try
            {
                otherValue = otherBean.getClass().getMethod(propertyMethodName, null).invoke(otherBean, null);
                
                if (sourceValue != otherValue)  // handles NULLs as well
                {
                    if (sourceValue != null && sourceValue.equals(otherValue))
                    {
                        ret = false;
                        break;
                    }
                }
                else
                {
                    ret = false;
                    break;                        
                }
                
            } catch (Exception e)
            {
                // The method doesn't exist in the other bean or is inaccessible.  We don't care in this case.  It means that
                // the property is unique with respect to this object
            }      
        }
    
        
        return ret;
    }

	/**
	* Get all fields that are public or accessible through getXXX() methods of this business object.
	*
	* @return Hashtable with field names (keys) and field values (values).
	*/
	public static Hashtable getPublicFieldsAndValues(Object obj)
	{
	
	    // To allow for field prefixes like fFieldName..
	    final int NUMBER_FIELD_CHARS_TO_IGNORE_IN_BEGINNING = 1;
	    Hashtable result = new Hashtable();
	
	    Class currentClass = obj.getClass();
	    do
	    {
	        Field[] fields = currentClass.getDeclaredFields();
	        int count = fields.length;
	        for (int i = 0; i < count; i++)
	        { //for each field
	            Field field = fields[i];
	            String name = field.getName();
	            if (name.indexOf("class") == 0)
	            {
	                continue; // ignore "class$0" entry
	            }
	            name = name.substring(NUMBER_FIELD_CHARS_TO_IGNORE_IN_BEGINNING);
	            try
	            {
	                Object value = field.get(obj); //may throw
	                                                // IllegalAccessException
	                                                // for private fields
	                if (value == null)
	                {
	                    value = "null";
	                }
	                result.put(name, value);
	            } catch (Exception e)
	            {
	                //    try to call a possible get() method:
	                StringBuffer sb = new StringBuffer("get"); //method name
	                sb.append(name.toUpperCase().charAt(0));
	                if (name.length() > 1)
	                {
	                    sb.append(name.substring(1));
	                }
	
	                try
	                {
	                    Method method = currentClass.getDeclaredMethod(sb
	                            .toString(), null);
	                    Object value = method.invoke(obj, null); //get()
	                                                              // methods
	                                                              // usually
	                                                              // don't have
	                                                              // arguments
	                    if (value == null)
	                    {
	                        value = "null";
	                    }
	                    result.put(name, value);
	                } catch (IllegalAccessException iae)
	                {
	                    /* ignore */
	                } catch (InvocationTargetException ite)
	                {
	                    /* ignore */
	                } catch (NoSuchMethodException nsme)
	                {
	                    /* ignore */
	                }
	            }
	        }//next field
	        currentClass = currentClass.getSuperclass();
	    } while (currentClass != null);
	
	    return result;
	}//getReflectionFields()

}



//--- Development History  ---------------------------------------------------
//
//$Log: BeanUtilities.java,v $
//Revision 1.11  2006/05/10 17:18:23  smaher_cvs
//Added ability to copy properties only in BeanInfo classes - even if they're read only.
//
//Revision 1.10  2006/03/07 20:10:08  smaher_cvs
//Removed extraneous sysout
//
//Revision 1.9  2006/03/07 20:04:27  smaher_cvs
//Added version of restoreDefaultProperties that allows ignoring certain properties; needed to allow ignoring "name" b/c new namespace code was producing "name#1#2.IRC" names.
//
//Revision 1.8  2006/03/02 16:09:22  smaher_cvs
//Added getPublicFieldsAndValues
//
//Revision 1.7  2006/02/15 18:04:04  smaher_cvs
//Added ability to ignore BeanInfo classes when copying properties
//
//Revision 1.6  2006/01/11 15:45:39  smaher_cvs
//Polished some bean property methods by changing to better name and exposing exceptions correctly.
//
//Revision 1.5  2005/01/23 14:52:31  smaher_cvs
//Ignore exception in isPropertyUnique since they're common-place
//(and harmless).
//
//Revision 1.4  2005/01/21 20:03:26  smaher_cvs
//Added methods to check uniqueness of property value across numerous beans.
//
//Revision 1.3  2005/01/20 14:05:50  smaher_cvs
//Allow specifying properties that aren't copied in order to support property file suffix capability.
//
//Revision 1.2  2005/01/19 22:04:39  smaher_cvs
//Added support for write-only properties in copyProperties.
//
//Revision 1.1  2004/12/21 20:03:05  smaher_cvs
//Initial checkin.  This is a simple framework to support property encoding and decoding.
//