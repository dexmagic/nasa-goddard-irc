//=== File Prolog ============================================================
//	This code was developed by Century Computing and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC) project.
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

#include "NiDaqAdapter.h"
#include "nidaq.h"
#include "nativeUtilities.h"

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockClear
 * Signature: (SS)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockClear
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort group)
{
	i16 status = 0;
	
	status = DIG_Block_Clear(deviceNumber, group);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[80];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Block_Clear( %d, %d )", 
			status, deviceNumber, group); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Block_Clear\n", status);
	}
}
	
	
/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockIn
 * Signature: (SS[BJJ)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockIn__SS_3BJJ
	(JNIEnv *env, jclass cls, jshort deviceNumber, 
	 jshort group, jbyteArray buffer, jlong offset, jlong count)
{
	i16 status = 0;
	jboolean isCopy;
	
	jsize len = (*env)->GetArrayLength(env, buffer);	
	jbyte *bufferElements = (*env)->GetByteArrayElements(env, buffer, &isCopy);

	if (checkForException(env))
	{
		return;
	}
	
	if (isCopy)
	{
		printf("Warning: using a copy of the array in DIG_Block_In %d\n", bufferElements);
	}
	
	if (offset > len-1) 
	{
		// throw illegal arg exception
		char exceptionStr[80];
		
		sprintf(
			exceptionStr, "Offset of: %d is out-of-range in DIG_Block_In", offset); 
		throwException(env, "java/lang/IllegalArgumentException", exceptionStr);
		return;
	}
	
	status = DIG_Block_In(deviceNumber, group, (short *)(bufferElements + offset), count);
	
	//(*env)->ReleaseByteArrayElements(env, buffer, bufferElements, 0);
	
	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Block_In( %d, %d, %d, %d )", 
			status, deviceNumber, group, (short *)(bufferElements + offset), count); 
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Block_In\n", status);
	}
}




/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockIn
 * Signature: (SSLjava/nio/Buffer;JJ)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockIn__SSLjava_nio_Buffer_2JJ
	(JNIEnv *env, jclass cls, jshort deviceNumber, 
	 jshort group, jobject buffer, jlong offset, jlong count)
{
	i16 status = 0;
	
	// Capacity in bytes
	jlong capacity = (*env)->GetDirectBufferCapacity(env, buffer);	

	jbyte *bufferElements = (*env)->GetDirectBufferAddress(env, buffer);

	if (checkForException(env))
	{
		return;
	}
	
	if (capacity < 0)
	{
		// throw illegal arg exception
		char exceptionStr[80];
		
		sprintf(
			exceptionStr, "Buffer is not a direct java.nio.Buffer or not supported by this VM"); 
		throwException(env, "java/lang/IllegalArgumentException", exceptionStr);
		return;
	}
	
	if (offset > capacity-1) 
	{
		// throw illegal arg exception
		char exceptionStr[80];
		
		sprintf(
			exceptionStr, "Offset of: %d is out-of-range in DIG_Block_In", offset); 
		throwException(env, "java/lang/IllegalArgumentException", exceptionStr);
		return;
	}
		
	status = DIG_Block_In(deviceNumber, group, (short *)(bufferElements + offset), count);
	

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Block_In( %d, %d, %d, %d )", 
			status, deviceNumber, group, (bufferElements + offset), count); 
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Block_In\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockOut
 * Signature: (SS[BJJ)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockOut__SS_3BJJ
	(JNIEnv *env, jclass cls, jshort deviceNumber, 
	 jshort group, jbyteArray buffer, jlong offset, jlong count)
{
	i16 status = 0;
	jboolean isCopy;
	jsize len = (*env)->GetArrayLength(env, buffer);	
	jbyte *bufferElements = (*env)->GetByteArrayElements(env, buffer, &isCopy);

	if (checkForException(env))
	{
		return;
	}
	
	if (isCopy)
	{
		printf("Warning: using a copy of the array in DIG_Block_Out %d\n", bufferElements);
	}
	
	if (offset > len-1) 
	{
		// throw illegal arg exception
		char exceptionStr[80];
		
		sprintf(
			exceptionStr, "Offset of: %d is out-of-range in DIG_Block_Out", offset); 
		throwException(env, "java/lang/IllegalArgumentException", exceptionStr);
		return;
	}
	
	status = DIG_Block_Out(deviceNumber, group, (short *)(bufferElements + offset), count);
	
	//(*env)->ReleaseByteArrayElements(env, buffer, bufferElements, 0);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Block_Out( %d, %d, %d, %d )", 
			status, deviceNumber, group, (short *)(bufferElements + offset), count); 
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Block_Out\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockOut
 * Signature: (SSLjava/nio/Buffer;JJ)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockOut__SSLjava_nio_Buffer_2JJ
	(JNIEnv *env, jclass cls, jshort deviceNumber, 
	 jshort group, jobject buffer, jlong offset, jlong count)
{
	i16 status = 0;
	
	// Capacity in bytes
	jlong capacity = (*env)->GetDirectBufferCapacity(env, buffer);	

	jbyte *bufferElements = (*env)->GetDirectBufferAddress(env, buffer);

	if (checkForException(env))
	{
		return;
	}
	
	if (capacity < 0)
	{
		// throw illegal arg exception
		char exceptionStr[80];
		
		sprintf(
			exceptionStr, "Buffer is not a direct java.nio.Buffer or not supported by this VM"); 
		throwException(env, "java/lang/IllegalArgumentException", exceptionStr);
		return;
	}
	
	if (offset > capacity-1) 
	{
		// throw illegal arg exception
		char exceptionStr[80];
		
		sprintf(
			exceptionStr, "Offset of: %d is out-of-range in DIG_Block_Out", offset); 
		throwException(env, "java/lang/IllegalArgumentException", exceptionStr);
		return;
	}

	status = DIG_Block_Out(deviceNumber, group, (short *)(bufferElements + offset), count);
	
	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Block_Out( %d, %d, %d, %d )", 
			status, deviceNumber, group, (bufferElements + offset), count); 
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Block_Out\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockCheck
 * Signature: (SS)J
 */

JNIEXPORT jlong JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockCheck
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort group)
{
	i16 status = 0;
	u32 remaining = 0;
	
	status = DIG_Block_Check(deviceNumber, group, &remaining);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Block_Check( %d, %d, %d )", 
			status, deviceNumber, group, remaining); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Block_Check\n", status);
	}
	
	
	return remaining;
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digGroupConfig
 * Signature: (SSSSS)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digGroupConfig
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort group, 
	 jshort groupSize, jshort port, jshort direction)
{
	i16 status = 0;
	
	status = DIG_Grp_Config(deviceNumber, group, groupSize, port, direction);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Grp_Config( %d, %d, %d, %d, %d )", 
			status, deviceNumber, group, groupSize, port, direction); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Grp_Config\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digGroupMode
 * Signature: (SSSSSSS)V
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digGroupMode
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort group, 
	 jshort protocol, jshort edge, jshort reqPol, jshort ackPol, jshort delayTime)
{
	i16 status = 0;
	
	status = DIG_Grp_Mode(deviceNumber, group, protocol, edge, reqPol, ackPol, delayTime);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Grp_Mode( %d, %d, %d, %d, %d, %d, %d )", 
			status, deviceNumber, group, protocol, edge, reqPol, ackPol, delayTime); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Grp_Mode\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digOutLine
 * Signature: (SSSS)S
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digOutLine
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort port, jshort line, jshort state)
{
	i16 status = 0;
	
	status = DIG_Out_Line(deviceNumber, port, line, state);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Out_Line( %d, %d, %d, %d )", 
			status, deviceNumber, port, line, state); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Out_Line\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digPrtConfig
 * Signature: (SSSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digPrtConfig
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort port, jshort mode, jshort dir)
{
	i16 status = 0;
	
	status = DIG_Prt_Config(deviceNumber, port, mode, dir);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Prt_Config( %d, %d, %d, %d )", 
			status, deviceNumber, port, mode, dir); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_Prt_Config\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digOutGroup
 * Signature: (SSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digOutGroup
  (JNIEnv *env, jclass cls, jshort deviceNumber, jshort group, jshort groupPattern)
{
	i16 status = 0;
	
	status = DIG_Out_Grp (deviceNumber, group, groupPattern);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from digOutGroup( %d, %d, %d )", 
			status, deviceNumber, group, groupPattern); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from digOutGroup\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digOutPrt
 * Signature: (SSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digOutPrt
  (JNIEnv *env, jclass cls, jshort deviceNumber, jshort port, jshort pattern)
{
	i16 status = 0;
	
	status = DIG_Out_Prt (deviceNumber, port, pattern);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[120];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_Out_Prt( %d, %d, %d )", 
			status, deviceNumber, port, pattern); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from digOutPrt\n", status);
	}
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digInLine
 * Signature: (SSS)S
 */
JNIEXPORT jshort JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digInLine
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort port, jshort line)
{
	i16 status = 0;
	i16 state = 0;
	
	status = DIG_In_Line(deviceNumber, port, line, &state);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[80];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_In_Line( %d, %d, %d, %d)", 
			status, deviceNumber, port, line, state); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_In_Line\n", status);
	}
	
	return state;
}

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digInPrt
 * Signature: (SS)I
 */
JNIEXPORT jint JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digInPrt
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort port)
{
	i16 status = 0;
	i32 pattern = 0;
	
	status = DIG_In_Prt(deviceNumber, port, &pattern);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[80];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_In_Prt( %d, %d, %d)", 
			status, deviceNumber, port, pattern); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_In_Prt\n", status);
	}
	
	return pattern;
}

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digInGrp
 * Signature: (SS)S
 */
JNIEXPORT jshort JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digInGrp
	(JNIEnv *env, jclass cls, jshort deviceNumber, jshort group)
{
	i16 status = 0;
	i16 groupPattern = 0;
	
	status = DIG_In_Grp(deviceNumber, group, &groupPattern);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[80];
		
		sprintf(exceptionStr, 
			"error %d thrown from DIG_In_Grp( %d, %d, %d)", 
			status, deviceNumber, group, groupPattern); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from DIG_In_Grp\n", status);
	}
	
	return groupPattern;
}

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    initDaBoards
 * Signature: (S)S
 */

JNIEXPORT jshort JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_initDaBoards
	(JNIEnv *env, jclass cls, jshort deviceNumber)
{
	i16 status = 0;
	i16 deviceNumberCode = 0;
	
	status = Init_DA_Brds(deviceNumber, &deviceNumberCode);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[80];
		
		sprintf(exceptionStr, 
			"error %d thrown from Init_DA_Brds( %d )", 
			status, deviceNumber); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from Init_DA_Brds\n", status);
	}
	
	return deviceNumberCode;
}


/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    setDaqDeviceInfo
 * Signature: (SII)S
 */

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_setDaqDeviceInfo
	(JNIEnv *env, jclass cls, jshort deviceNumber, jint infoType, jint infoValue)
{
	i16 status = 0;
	
	status = Set_DAQ_Device_Info(deviceNumber, infoType, infoValue);

	// if error occurred throw an exception
	if (status < 0)
	{	
		char exceptionStr[80];
		
		sprintf(exceptionStr, "error %d thrown from Set_DAQ_Device_Info", status); 
		
		throwException(
			env, 
			"gov/nasa/gsfc/irc/library/ports/connections/ni/daq/NiDaqException", 
			exceptionStr);
	}
	else if (status > 0) // warning occurred
	{
		printf("Warning %d thrown from Set_DAQ_Device_Info\n", status);
	}
}

//--- Development History  ---------------------------------------------------
//	
//	$Log: $
//
//	02/8/2002	T. Ames/588
//
//      Added support for direct java.nio.Buffer.
//
//	06/1999	T. Ames/588
//
//      Initial version.
//
