/* simNIDAQ.c 
	A series of dummy calls to simulate the behavior of the NIDAQ interfaces in order to
	test the package outside of an environment with a working card.
	Most of the calls will do nothing other than returning a zero, so they are really very
	low fidelity.
	The exception are the DMA calls which will actually write incrementing values into
	the buffer.
*/

#include "nidaq.h"
#include "nidaqerr.h"

#include <stdlib.h>
#include <stdio.h>

#define	DEBUG	1
#define	INPUT	0
#define	INPUT_EDGE	3
#define	OUTPUT	1
#define	OUTPUT_EDGE	4

i32 totalInputCount = 0;
i32 maxBlockCheckInterval = 1000; /* This is the number of records read between BlockChecks */
i32 currentDMARemaining = 0;
i16 *BufferPtr = NULL;
i32 currentBufferPosition = 0;
i16 mode = 0;




i16 WINAPI DIG_Out_Line (
   i16      slot,
   i16      port,
   i16      linenum,
   i16      status)
{
	if (DEBUG)
		printf(" DIG_Out_Line: %d %d %d %d\n", slot, port, linenum, status);
	return noError;
}
   
   
   
i16 WINAPI Set_DAQ_Device_Info (
   i16      deviceNumber,
   u32      infoType,
   u32      infoVal)
{
	if (DEBUG)
		printf(" Set_DAQ_Device_Info: %d %d %d\n", deviceNumber, infoType, infoVal);
	return noError;
}

i16 WINAPI DIG_Grp_Config (
   i16      slot,
   i16      grp,
   i16      grpsize,
   i16      port,
   i16      direction)
{
	mode = direction;
	
	if (DEBUG)
		printf(" DIG_Grp_Config: %d %d %d %d %d\n",slot, grp, grpsize, port, direction);
	return noError;
}


i16 WINAPI DIG_Prt_Config (
   i16      slot,
   i16      port,
   i16      mode,
   i16      dir)
{
	if (DEBUG)
		printf(" DIG_Prt_Config: %d %d %d %d\n", slot, port, mode, dir);
	return noError;
}


i16 WINAPI DIG_Out_Grp (
   i16      deviceNumber,
   i16      group,
   i16      groupPattern)
{
	if (DEBUG)
		printf(" DIG_Out_Grp: %d %d %d\n", deviceNumber, group, groupPattern);
	return noError;
}


i16 WINAPI DIG_In_Grp (
   i16      deviceNumber,
   i16      group,
   i16      FAR * groupPattern)
{
	if (DEBUG)
		printf(" DIG_In_Grp: %d %d %d\n", deviceNumber, group, groupPattern);
	return noError;
}


i16 WINAPI DIG_In_Line (
   i16      deviceNumber,
   i16      port,
   i16      line,
   i16		FAR * state)
{
	if (DEBUG)
		printf(" DIG_In_Line: %d %d %d %d\n", deviceNumber, port, line, state);
	return noError;
}


i16 WINAPI DIG_In_Prt (
   i16      deviceNumber,
   i16      port,
   i32      FAR * pattern)
{
	if (DEBUG)
		printf(" DIG_In_Prt: %d %d %d\n", deviceNumber, port, pattern);
	return noError;
}

i16 WINAPI DIG_Out_Prt (
   i16      deviceNumber,
   i16      port,
   i32      pattern)
{
	if (DEBUG)
		printf(" DIG_Out_Prt: %d %d %d\n", deviceNumber, port, pattern);
	return noError;
}


i16 WINAPI DIG_Grp_Mode (
   i16      slot,
   i16      grp,
   i16      sigType,
   i16      edge,
   i16      reqpol,
   i16      ackpol,
   i16      settleTime)
{
	if (DEBUG)
		printf(" DIG_Grp_Mode: %d %d %d %d %d %d %d\n",
			slot, grp, sigType, edge, reqpol, ackpol, settleTime);
	return noError;
}
   
i16 WINAPI DIG_Block_Check (
   i16      slot,
   i16      grp,
   u32 *    remaining)
{
	int nread, i;
   

	if( BufferPtr == NULL) 
	{
   		(*remaining) = currentDMARemaining;
   		return memConfigError;
   	}
	
	nread = 1 + ((float) rand()) * ((float) maxBlockCheckInterval) /  ((float) RAND_MAX);
	nread = (nread <= currentDMARemaining) ? nread : currentDMARemaining;
	//nread = currentDMARemaining; // simulate filling up the buffer every time

	if (DEBUG)
		printf(" DIG_Block_Check: %d\n", BufferPtr);
	for (i=0; i< nread ; i++)
   	{
   		if ((mode == INPUT) || (mode == INPUT_EDGE))
   		{
   			//(*BufferPtr) = totalInputCount++;
   			(*BufferPtr) = 1;
   			BufferPtr++;
   		}
   		
   		currentDMARemaining--;
	}
	
   	(*remaining) = currentDMARemaining;
   	
	if (DEBUG)
		printf(" DIG_Block_Check: %d %d %d %d\n",slot, grp, BufferPtr, *remaining);

   	return noError;
}
   		

i16 WINAPI DIG_Block_Clear (
   i16      slot,
   i16      grp)
{
	if (DEBUG)
		printf(" DIG_Block_Clear: %d %d\n",slot, grp);
	BufferPtr = NULL;
	
	return noError;
}

i16 WINAPI DIG_Block_In (
   i16      slot,
   i16      grp,
   i16 *    buffer,
   u32      cnt)
{
	if (buffer == NULL)
	{
		return memConfigError;  /*Actually some other error probably is better */
	}
	
	BufferPtr = buffer;
	currentDMARemaining = cnt;
	if (DEBUG)
		printf(" DIG_Block_In:  Count: %i Buffer:%d\n", cnt, BufferPtr) ;
	return noError;
}

i16 WINAPI DIG_Block_Out (
   i16      slot,
   i16      grp,
   i16 *    buffer,
   u32      cnt)
{
	if (buffer == NULL)
	{
		return memConfigError;  /*Actually some other error probably is better */
	}
	
	BufferPtr = buffer;
	currentDMARemaining = cnt;

	if (DEBUG)
		printf(" DIG_Block_Out:  Count: %i Buffer:%d\n", cnt, BufferPtr) ;
	return noError;
}

i16 WINAPI Init_DA_Brds (
	i16        slot,
	i16        FAR * brdCode)
{
	*brdCode = 211;
	return noError;
}

