/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB */

#ifndef _Included_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB
#define _Included_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB
 * Method:    ibfind
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB_ibfind
  (JNIEnv *, jclass, jstring);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB
 * Method:    ibrd
 * Signature: (ILjava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB_ibrd
  (JNIEnv *, jclass, jint, jstring, jlong);

/*
 * Class:     gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB
 * Method:    ibwrt
 * Signature: (ILjava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB_ibwrt
  (JNIEnv *, jclass, jint, jstring, jlong);

#ifdef __cplusplus
}
#endif
#endif
