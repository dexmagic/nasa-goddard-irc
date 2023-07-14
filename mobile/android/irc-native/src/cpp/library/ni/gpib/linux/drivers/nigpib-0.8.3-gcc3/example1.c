/* * Link this program with cib.o * */ 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <errno.h>
#include "ugpib.h" 
/* Position of the Message Available bit. */ 
#define MAVbit 0x10 
char buffer[101]; 
int loop, m, num_listeners, SRQasserted; 
double sum; 
Addr4882_t instruments[32], result[31], fluke; 
short statusByte; 
void gpiberr(char *msg); 
/* gpib error function */ 

main() 
{ 
    short enableList[1];
    int nsamples=10;
    system("clear"); 
    /* Your GPIB board must be the Controller-In-Charge to perform * the Find All Listeners protocol. */ 
    SendIFC(0); 
    if (ibsta & ERR) 
    { 
        gpiberr ("SendIFC Error"); 
        exit(1); 
    } 
    /* Create an array with all of the valid GPIB primary addresses. * 
    This array will be given to the Find All Listeners protocol. */ 
    for (loop = 0; loop <= 30; loop++) 
    { 
        instruments[loop] = loop; 
    } 
    instruments[31] = NOADDR; 
    /* Mark the end of the array.*/ 
    /* Find all of the listeners on the bus. */ 
    printf("Finding all listeners on the bus...\n");
    FindLstn(0, instruments, result, 31); 
    if (ibsta & ERR) 
    { 
        gpiberr("FindLstn Error"); 
        exit(1); 
    } 
    num_listeners = ibcnt - 1; 
    printf("Number of instruments found = %d\n", num_listeners); 
    /* Now send the *IDN? command to each of the devices that you * found. * 
    * The GPIB board is at address 0 by default. Your GPIB board * 
    does not respond to *IDN?, so skip it. */ 
    for (loop = 1; loop <= num_listeners; loop++) 
    { 
        Send(0, result[loop], "*IDN?", 5L, NLend); 
        if (ibsta & ERR) 
        { 
            gpiberr("Send Error"); 
            exit(1); 
        } 
        Receive(0, result[loop], buffer, 10L, STOPend); 
        if (ibsta & ERR) 
        { 
            gpiberr("Receive Error"); 
            exit(1); 
        } 
        buffer[ibcnt] = '\0'; 
        printf("The instrument at address %d is a %s\n", GetPAD(result[loop]), buffer); 
        if (strncmp(buffer, "HEWLETT-PA", 10) == 0) 
        { 
            fluke = result[loop]; 
            printf("**** We found the HP Voltmeter ****\n"); 
            break; 
        } 
        else
        {
            printf("address %d\n", result[loop]);
        }
    } 
    if (loop > num_listeners) 
    { 
        printf("Did not find the HP Voltmeter!\n"); 
        exit(1); 
    } 
    SendLLO(0);
    enableList[0] = fluke;
    EnableRemote (0, enableList);
    /* Reset the Fluke. */ 
    DevClear(0, fluke); 
    if (ibsta & ERR) 
    { 
        gpiberr("DevClear Error"); 
        exit(1); 
    } 
    Send(0, fluke, "*RST", 4L, NLend);
    if (ibsta & ERR) 
    { 
        gpiberr("Send *RST Error"); 
        exit(1); 
    } 
    /* Set up for a test. Allow the Fluke to assert SRQ when it * has a message to send. */ 
    Send(0, fluke, "CONF:VOLT:DC 10,0.003", 21L, NLend); 
    if (ibsta & ERR) 
    { 
        gpiberr("Send Setup Error"); 
        exit(1); 
    } 
    Send(0, fluke, "TRIG:SOUR BUS", 21L, NLend); 
    if (ibsta & ERR) 
    { 
        gpiberr("Send Setup Error"); 
        exit(1); 
    } 
    sum = 0.0; 
    nsamples=2000;
    for (m=0; m < nsamples ; m++) 
    { 
        /* Trigger the Fluke. */ 
        Send(0, fluke, "*TRG; READ?", 11L, NLend); 
        if (ibsta & ERR) 
        { 
            gpiberr("Send Trigger Error"); 
            exit(1); 
        } 
#if 0
        /* Wait for the Fluke to assert SRQ, meaning it is * ready with the measurement. */ 
        WaitSRQ(0, &SRQasserted); 
        if (!SRQasserted) 
        { 
            printf("SRQ is not asserted. The Fluke is not "); 
            printf("ready.\n"); 
            exit(1); 
        } 
        /* Read its status byte. Be sure that the MAV * (Message Available) bit is set. */ 
        ReadStatusByte(0, fluke, &statusByte); 
        if (ibsta & ERR) 
        { 
            gpiberr("ReadStatusByte Error"); 
            exit(1); 
        } 
        if (!(statusByte & MAVbit)) 
        { 
            gpiberr("Improper Status Byte"); 
            printf(" Status Byte = 0x%x\n", statusByte); 
            exit(1); 
        }
#endif
        /* Read the measurement. */ 
        Receive(0, fluke, buffer, 100L, STOPend); 
        if (ibsta & ERR) 
        { 
            gpiberr("Receive Error"); 
            exit(1); 
        } 
        buffer[ibcnt] = '\0'; 
        printf("Reading : %s", buffer); 
        sum = sum + atof(buffer); 
    } 
    printf(" The average of the %d readings is : ", nsamples); 
    printf(" %f\n", sum/nsamples); 
    /* Call the ibonl function to disable the hardware and * software. */ 
    ibonl (0,0); 
} 

void gpiberr(char *msg) 
{ 

    printf ("%s\n", msg); 
    printf ( "ibsta=&H%x ", ibsta, "< "); 
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
    printf (">\n"); 
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
    /* Call the ibonl function to disable the hardware and * software. */ 
    ibonl (0,0); 
}

