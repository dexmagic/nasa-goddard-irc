#include <jni.h>
#include <stdio.h>
#include "gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB.h"
#include "ugpib.h"
//#include "nativeUtilities.h"

// throwException was copied from nativeUtilities.h
//  if nativeUtilities.h is included, then this method can be removed
void throwException(JNIEnv *env, char *exceptionClass, char *message)
{
    jclass newExcCls = (*env)->FindClass(env, exceptionClass);

    if(newExcCls != 0)
    {
        (*env)->ThrowNew(env, newExcCls, message);
    }
    (*env)->DeleteLocalRef(env, newExcCls);
}


/* The gpiberr method prints information useful for debugging gpib errors
 * and it calls throwException, to throw a Java Exception.
 */
void gpiberr(char *msg, int ud, JNIEnv *env) 
{
    
    /* This routine would notify you that an ib call failed. */
    char exceptionStr[80];
    sprintf(exceptionStr, "GPIB error occurred during %s.", msg);
    printf ("\n%s  Debug info:\n", exceptionStr);
    printf ( "ibsta=&H%x <", ibsta);
    if (ibsta & ERR ) printf (" ERR");
    if (ibsta & TIMO) printf (" TIMO");
    if (ibsta & END ) printf (" END");
    if (ibsta & SRQI) printf (" SRQI");
    if (ibsta & RQS ) printf (" RQS");
    if (ibsta & CMPL) printf (" CMPL");
    if (ibsta & LOK ) printf (" LOK");
    if (ibsta & REM ) printf (" REM");
    if (ibsta & CIC ) printf (" CIC");
    if (ibsta & ATN ) printf (" ATN");
    if (ibsta & TACS) printf (" TACS");
    if (ibsta & LACS) printf (" LACS");
    if (ibsta & DTAS) printf (" DTAS");
    if (ibsta & DCAS) printf (" DCAS");
    printf (" >\n");
    
    printf ("iberr= %d", iberr);
    if (iberr == EDVR) printf (" EDVR < Error>\n");
    if (iberr == ECIC) printf (" ECIC <Not CIC>\n");
    if (iberr == ENOL) printf (" ENOL <No Listener>\n");
    if (iberr == EADR) printf (" EADR <Address error>\n");
    if (iberr == EARG) printf (" EARG <Invalid argument>\n");
    if (iberr == ESAC) printf (" ESAC <Not Sys Ctrlr>\n");
    if (iberr == EABO) printf (" EABO <Op. aborted>\n");
    if (iberr == ENEB) printf (" ENEB <No GPIB board>\n");
    if (iberr == ECAP) printf (" ECAP <No capability>\n");
    if (iberr == EFSO) printf (" EFSO <File sys. error>\n");
    if (iberr == EBUS) printf (" EBUS <Command error>\n");
    if (iberr == ESTB) printf (" ESTB <Status byte lost>\n");
    if (iberr == ESRQ) printf (" ESRQ <SRQ stuck on>\n");
    if (iberr == ETAB) printf (" ETAB <Table Overflow>\n");
    printf ("ibcnt= %d\n", ibcnt);
    printf ("\n");
    
    /* Cause a Java exception to be thrown */
    throwException(env, "gov/nasa/gsfc/irc/library/ports/connections/ni/gpib/GPIBException", exceptionStr);
    
    /* Call the ibonl function to disable device. */
    ibonl (ud,0);
}


JNIEXPORT jint JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB_ibfind (JNIEnv *env, jobject obj, jstring device)
{
    int ud = 0;

    const char *str = (*env)->GetStringUTFChars(env, device, 0);

    if ( (ud = ibfind((char *)str)) < 0)
    {
        gpiberr("ibfind", ud, env);
    }
    
    (*env)->ReleaseStringUTFChars(env, device, str);

    return (jint) ud;
   

}

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB_ibrd (JNIEnv *env, jobject obj,
jint ud, jstring rd, jlong count)
{
    char str[512]; 
    
    /* this used to be declared down where it was used, but
       this was causing a compilation error.  Steve Maher */
    jstring js;

    if( ibrd( (int)ud, str, (unsigned long)count ) & ERR ) 
    {
        gpiberr("ibrd", ud, env);
    }

    str[ibcnt] = '\0';

    js = (*env)->NewStringUTF(env, str);

    rd = js;

}

JNIEXPORT void JNICALL Java_gov_nasa_gsfc_irc_library_ports_connections_ni_gpib_GPIB_ibwrt (JNIEnv *env, jobject obj,
 jint ud, jstring wrt, jlong count)
{

    const char *str = (*env)->GetStringUTFChars(env, wrt, 0);
    
    ibwrt( (int)ud, (char *)str, (unsigned long)count );
    if (ibsta & ERR)
    {
        gpiberr("ibwrt", ud, env);
    }

    (*env)->ReleaseStringUTFChars(env, wrt, str);

}


