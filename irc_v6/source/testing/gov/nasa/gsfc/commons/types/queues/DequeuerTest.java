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

package gov.nasa.gsfc.commons.types.queues;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import gov.nasa.gsfc.commons.types.queues.Dequeuer;
import gov.nasa.gsfc.commons.types.queues.KeepLatestBoundedQueue;
import gov.nasa.gsfc.commons.types.queues.QueueProcessor;
import gov.nasa.gsfc.commons.types.synchronization.CyclicBarrier;

/**
 * Tests Dequeuer (and CyclicBarrier)
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/11 15:46:19 $
 * @author	smaher
 */

public class DequeuerTest extends TestCase implements QueueProcessor
{
    private static ByteBuffer dequeuedBuffer;
    CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    CyclicBarrier cyclicBarrier2 = new CyclicBarrier(2);


    public void testDequeuer() throws InterruptedException
    
    {
        int numBufs = 10;
        int qSize = 5;

        ByteBuffer bufs[] = new ByteBuffer[10];
        
        KeepLatestBoundedQueue Q = new KeepLatestBoundedQueue(qSize);
        Dequeuer DQ = new Dequeuer(Q, this);


        for (int i = 0; i < 10; i++)
        {
        	ByteBuffer buf = ByteBuffer.allocate(numBufs * 2);
        	buf.position(i);
        	DQ.enqueue(buf);
        	
        }
        
        
        boolean needToStart = true;
        for (int i = numBufs - qSize; i < numBufs; i++)
        {    
            if (needToStart) {
                DQ.start();
                needToStart = false;
            }
            cyclicBarrier.barrier();
            assertEquals(i, dequeuedBuffer.position());
            cyclicBarrier2.barrier();
        }
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.port.QueueProcessor#parseBuffer(java.nio.ByteBuffer)
     */
    public void processQueueEntry(Object queueEntry)
    {

        dequeuedBuffer = (ByteBuffer) queueEntry;
        try {
            cyclicBarrier.barrier();
            
            System.out.println("Parser was sent buffer number: " + dequeuedBuffer.position());
            cyclicBarrier2.barrier();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

}



//--- Development History  ---------------------------------------------------
//
//$Log: DequeuerTest.java,v $
//Revision 1.1  2006/01/11 15:46:19  smaher_cvs
//Moved JUnit test classes into same package as classes under test.
//
//Revision 1.3  2005/03/01 23:25:15  chostetter_cvs
//Revised Queue design and package for better blocking behavior
//
//Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//Initial version
//
//Revision 1.1  2004/12/19 19:04:26  smaher_cvs
//Added tests for Dequeuer
//
//Revision 1.1  2004/12/10 22:02:04  smaher_cvs
//*** empty log message ***
//