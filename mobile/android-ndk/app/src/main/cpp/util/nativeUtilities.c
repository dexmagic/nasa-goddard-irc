//=== File Prolog ============================================================
//	This code was developed by Century Computing and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC) project.
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	????	T. Ames/588
//
//      Initial version.
//
//	07/12/2000	C. Hostetter/588
//
//      Added checkForException
//
//	07/14/2000	C. Hostetter/588
//      Added jstringArrayToStringArray.
//
//	07/17/2000	C. Hostetter/588
//      Added jstringToString.
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
#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#include <jni.h>
#include <string.h>
#include <stdlib.h>

#include "nativeUtilities.h"

//#include "nativeAlgorithm.h"


/**
  * Throws a new JNI Exception 
  * 
 **/

void throwException(JNIEnv *env, char *exceptionClass, char *message)
{
	jclass newExcCls = (*env)->FindClass(env, exceptionClass);
	
	// if newExcCls is NULL, an exception has already been thrown
	if (newExcCls != 0)
    {
    	(*env)->ThrowNew(env, newExcCls, message);
	}
	
	// free the local reference
	(*env)->DeleteLocalRef(env, newExcCls);
}

/**
  * Checks the given JNIEnv * for Exception condition 
  * 
 **/

int checkForException(JNIEnv *env)
{
	int result = FALSE;
	
	if ((*env)->ExceptionOccurred(env))
	{
		result = TRUE;
		
		// Exception occurred in latest native call. 
		// Print it, clear it, and return.
		
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}
	
	return (result);
}


/**
  *  Converts the given jstring to a UTF string and copies the result 
  *  into the given C string
  * 
 **/

void jstringToString(JNIEnv *env, jstring j_string, char *cString)
{
	const char *utfString = NULL;

	if ((j_string != NULL) && (cString != NULL))
	{
		// Convert the Unicode jstring to 8-bit UTF (ASCII, /0 termintated)
		
		utfString = (*env)->GetStringUTFChars(env, j_string, NULL);
			
		if (checkForException(env))
		{
			return;
		}
		
		// Copy the 8-bit UTF (ASCII, /0 termintated) field to the C string
		
		strcpy(cString, utfString);
		
		// Release the 8-bit UTF (ASCII, /0 termintated) back to Java
	
		(*env)->ReleaseStringUTFChars(env, j_string, utfString);
	}
		
	return;
}


/**
  * Converts the given jobjectArray of jstrings to an array of C strings 
  * 
 **/

void jstringArrayToStringArray(JNIEnv *env, jobjectArray jstringArray, 
	char *stringArray[], int numStringsInArray)
{
	jstring	j_string			= NULL;
	int j_stringLength			= 0;
	char  *cString				= NULL;
	int i = 0;
	
	for (i = 0; i < numStringsInArray; i++)
	{
		// Get the next jstring from the jobjectArray
		
		j_string = (*env)->GetObjectArrayElement(env, jstringArray, i);
			
		if (checkForException(env))
		{
			return;
		}
		
		// Convert the jstring to a C string
		
		j_stringLength = (*env)->GetStringLength(env, j_string);
		cString = (char *) malloc(j_stringLength * sizeof(char));
		jstringToString(env, j_string, cString);
		
		// Add the C string to our array of C strings
		
		stringArray[i] = cString;
	}
	
	return;
}


jlongArray packageLongArray(long *longArray, int numElements)
{
    jlongArray resultArray; 

    resultArray = (*env)->NewLongArray(env, numElements);
	if (resultArray == NULL)
	{
		throwException(env, EXCEPTION_CLASS, "Couldn't allocate NewLongArray");
		return NULL; 		
	}  
	
    jlong *pResultArray;    
	pResultArray = (jlong *) (*env)->GetLongArrayElements(env, resultArray, 0);
	if (pResultArray == NULL)
	{
		throwException(env, EXCEPTION_CLASS, "GetLongArrayElements failed");
		return NULL; 		
	}

	int i;
	for (i = 0; i < numElements; i++)
	{
		pResultArray[i] = longArray[i];
	}	
	
	(*env)->ReleaseLongArrayElements(env, resultArray, pResultArray, 0);
	    		        
    return resultArray;

}