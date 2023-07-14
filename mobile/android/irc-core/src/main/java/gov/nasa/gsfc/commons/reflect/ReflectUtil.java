//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   1	IRC	   1.0		 12/14/2001 5:21:24 PMKen Wootton	 
//  $
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

package gov.nasa.gsfc.commons.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *	This class contains utility methods that can assist with 
 *  with class reflection.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Ken Wootton
 */
public class ReflectUtil
{
	private static final String CLASS_NAME = 
		ReflectUtil.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	
	//  Parameter array for a string constructor
	private static final Class[] SINGLE_STR_PARAM = {String.class};

	//  Reflection error messages
	private static final String MISSING_METHOD_MSG = 
		"Cannot create new Object from String without a constructor that " +
		"takes a String for class ";
	private static final String INSTANCE_PROB_MSG = 
		"Error instantiating value with String constructor:  ";

	/**
	 *	Create an object of the proper class from the given string.
	 *  The class to create is retrieved from the property inspector.
	 *  This method assumes that the created class has a constructor
	 *  that accepts a single string.
	 *
	 *  @param str  the string representation of the object to create
	 *  @param class  the class of the object to create
	 *
	 *  @return  the created object or, if unable to create the object, null
	 *
	 *  @throws InvalidParameterException  the created class threw an exception
	 *	within its constructor
	 *  @throws NumberFormatException  the created class cannot properly 
	 *	format the given number (this is only thrown for classes that
	 *	are instances of Number)
	 */
	public static Object createObjectFromString(String str, Class targetClass)
		throws InvalidParameterException, NumberFormatException
	{
		Object value = null;

		if (targetClass != null)
		{
			try
			{
				//  Use introspection to create an object of the proper
				//  type.  We want to use a constructor that takes a string.
				Constructor strCtor =
					targetClass.getConstructor(SINGLE_STR_PARAM);
				
				//  Create the paramater list of one for the constructor.
				String[] ctorParams = new String[1];
				ctorParams[0] = str;

				value = strCtor.newInstance(ctorParams);
			}

			//  Catch the obvious error of not having this constructor.
			catch (NoSuchMethodException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = MISSING_METHOD_MSG + targetClass;
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"createObjectFromString", message, ex);
				}
			}

			//  The underlying class threw an exception.
			catch (InvocationTargetException ex)
			{
				if (ex.getTargetException() instanceof NumberFormatException)
				{
					throw (NumberFormatException) ex.getTargetException();
				}
				else
				{
					//  TBD:  Report to exception manager.  The underlying
					//		class let us down in some other way.
					throw new InvalidParameterException();
				}
			}

			//  Group the rest of the problems together (see the newInstance
			//  call for specifics).
			catch(Exception ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = INSTANCE_PROB_MSG;
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"createObjectFromString", message, ex);
				}
			}
		}

		return value;
	}

}

