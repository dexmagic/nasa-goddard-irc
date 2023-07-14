//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/types/synchronization/CyclicBarrier.java,v 1.1 2004/12/19 19:02:46 smaher_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.commons.types.synchronization;

/**
 * A thread barrier in the traditional sense.  It allows multiple threads to 
 * synchronize in terms of all reaching the barrier at the same time.  Threads
 * will block until all threads have called the barrier method.  The name "Cyclic"
 * refers to the fact that the barrier can be reused after all threads have reached
 * the barrier.
 * <P>
 * This code was adapted from <a href="http://www.informit.com/articles/article.asp?p=167821&seqNum=5">
 * this article</a>
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/19 19:02:46 $
 * @author	smaher
 */
public class CyclicBarrier {
    
    protected final int parties;
    protected int count;   // parties currently being waited for
    protected int resets = 0; // times barrier has been tripped
    
    /**
     * Create a barrier that expects the specified number of
     * parties to utilize.
     * @param numParties
     */
    public CyclicBarrier(int numParties)
    { 
        count = parties = numParties;
    }
    
    /**
     * Block until all parties have called this method.
     * @return the number of other threads that were still waiting when the barrier was entered,
     * @throws InterruptedException
     */
    public synchronized int barrier() throws InterruptedException { 
        int index = --count;
        if (index > 0) {    // not yet tripped
            int r = resets;    // wait until next reset
            
            do { wait(); } while (resets == r);
            
        }
        else {         // trip 
            count = parties;   // reset count for next time
            ++resets;
            notifyAll();     // cause all other parties to resume
        }
        
        return index;
    }
}
