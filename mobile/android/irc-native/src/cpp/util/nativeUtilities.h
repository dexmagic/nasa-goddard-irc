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

//	07/14/2000	C. Hostetter/588
//
//      Added jstringArrayToStringArray.
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

#include <jni.h>

#ifndef _NATIVE_UTILITIES_
#define _NATIVE_UTILITIES_


/**
  * Throws a new JNI Exception 
  * 
 **/

void throwException(JNIEnv *, char *, char *);


/**
  * Checks the given JNIEnv * for Exception condition 
  * 
 **/

int checkForException(JNIEnv *env);


/**
  *  Converts the given jstring to a UTF string and copies the result 
  *  into the given C string
  * 
 **/

void jstringToString(JNIEnv *env, jstring j_string, char *cString);


/**
  * Converts the given jobjectArray of jstrings to an array of C strings 
  * 
 **/

void jstringArrayToStringArray(JNIEnv *env, jobjectArray jstringArray, 
	char *stringArray[], int numStringsInArray);
	
jlongArray packageLongArray(long *longArray, int numElements);	

#endif
