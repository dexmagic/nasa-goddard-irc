/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq */

#ifndef _Included_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
#define _Included_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
#ifdef __cplusplus
extern "C" {
#endif
/* Inaccessible static: niDaqLibrary */
/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockCheck
 * Signature: (SS)J
 */
JNIEXPORT jlong JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockCheck
  (JNIEnv *, jclass, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockClear
 * Signature: (SS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockClear
  (JNIEnv *, jclass, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockIn
 * Signature: (SS[BJJ)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockIn__SS_3BJJ
  (JNIEnv *, jclass, jshort, jshort, jbyteArray, jlong, jlong);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockIn
 * Signature: (SSLjava/nio/Buffer;JJ)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockIn__SSLjava_nio_Buffer_2JJ
  (JNIEnv *, jclass, jshort, jshort, jobject, jlong, jlong);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockOut
 * Signature: (SS[BJJ)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockOut__SS_3BJJ
  (JNIEnv *, jclass, jshort, jshort, jbyteArray, jlong, jlong);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digBlockOut
 * Signature: (SSLjava/nio/Buffer;JJ)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digBlockOut__SSLjava_nio_Buffer_2JJ
  (JNIEnv *, jclass, jshort, jshort, jobject, jlong, jlong);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digGroupConfig
 * Signature: (SSSSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digGroupConfig
  (JNIEnv *, jclass, jshort, jshort, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digGroupMode
 * Signature: (SSSSSSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digGroupMode
  (JNIEnv *, jclass, jshort, jshort, jshort, jshort, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digInLine
 * Signature: (SSS)S
 */
JNIEXPORT jshort JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digInLine
  (JNIEnv *, jclass, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digInPrt
 * Signature: (SS)I
 */
JNIEXPORT jint JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digInPrt
  (JNIEnv *, jclass, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digInGrp
 * Signature: (SS)S
 */
JNIEXPORT jshort JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digInGrp
  (JNIEnv *, jclass, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digPrtConfig
 * Signature: (SSSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digPrtConfig
  (JNIEnv *, jclass, jshort, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digOutGroup
 * Signature: (SSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digOutGroup
  (JNIEnv *, jclass, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digOutLine
 * Signature: (SSSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digOutLine
  (JNIEnv *, jclass, jshort, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    digOutPrt
 * Signature: (SSS)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_digOutPrt
  (JNIEnv *, jclass, jshort, jshort, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    initDaBoards
 * Signature: (S)S
 */
JNIEXPORT jshort JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_initDaBoards
  (JNIEnv *, jclass, jshort);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq
 * Method:    setDaqDeviceInfo
 * Signature: (SII)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_daq_NiDaq_setDaqDeviceInfo
  (JNIEnv *, jclass, jshort, jint, jint);

#ifdef __cplusplus
}
#endif
#endif