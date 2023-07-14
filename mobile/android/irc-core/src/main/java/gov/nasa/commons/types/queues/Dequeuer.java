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

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.Startable;

/**
 * Used to automatically dequeue entries from (typically blocking) queue
 * and feed them to a QueueProcessor.
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/31 16:19:42 $
 * @author	smaher
 */

public class Dequeuer implements Startable
{
    /**
     * Logger for this class
     */
    private static final Logger sLogger = Logger.getLogger(Dequeuer.class.getName());

    /** The thread doing the dequeuing */
    private Thread fDequeueThread;
    
    /** The queue. */
    private Queue fQueue;
    
    /** Class to receive the queue entries */
    private QueueProcessor fProcessor;
    
    /** Whether the dequeuer has been started */
    private boolean fStarted;
    
    /**
     * Construct a dequeuer.  {@link #start} must be called
     * to start the dequeuer.
     * @param queue The queue that will have its entries removed
     * @param processor Where the entries are delivered
     */
    public Dequeuer(Queue queue, QueueProcessor processor)
    {
        fQueue = queue;
        fProcessor = processor;
    }
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.commons.processing.activity.Startable#start()
     */
    public void start()
    {
        fStarted = true; // needs be set before starting the dequeuer thread
        
		if (fDequeueThread == null)
		{
			// Construct a thread to deserialize the objects
			fDequeueThread = new Thread(new Runnable() 
			{
				public void run()
				{
				    continuouslyDequeue();
				}
			});
			fDequeueThread.setName("Dequeuer");
			fDequeueThread.start();
		}
		
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.commons.processing.activity.Startable#stop()
     */
    public void stop()
    {		
		if (fDequeueThread != null)
		{
			try
			{
				fDequeueThread.join(1000);
			}
			catch (InterruptedException e)
			{
				// Nothing really to do here;
			}
			finally
			{
				fDequeueThread = null;
			}
		}
		fStarted = false;
    }

    /* (non-Javadoc)
     * @see gov.nasa.gsfc.commons.processing.activity.Startable#kill()
     */
    public void kill()
    {
        stop();
    }

    
    /**
     * Convenience routine to add an entry to the queue.  Objects
     * can also be added directly to the queue.
     * @param queueEntry Entry to place in queue
     */
    public void enqueue(Object queueEntry)
    {
        if (queueEntry != null)
        {		    
            fQueue.add(queueEntry);
        }
    }

    /**
     * Perform the dequeuing.
     */
    private void continuouslyDequeue()
    {    		
        while (fStarted == true)
        {
            try {
                if (fProcessor != null) {
                    fProcessor.processQueueEntry(fQueue.blockingRemove());                        
                } else {
                    sLogger.logp(Level.SEVERE, "Dequeuer","continuouslyDequeue()",
                    	"Not processing entry because no queue receiver has been set.");
                    
                    Thread.sleep(1000L);  // don't burn CPU!
                }
            } catch (Throwable e) {
                // We want to be extra safe here to make sure the dequeuing isn't interrupted
                String mesg = "Dequeuer caught throwable: " + e.getMessage();
                System.out.println(mesg);
                e.printStackTrace();
                sLogger.logp(Level.SEVERE,"Dequeuer","continuouslyDequeue()",
                        mesg + "  " + e);                    
            }
        }
    }

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.processing.activity.Startable#isStarted()
	 */
	public boolean isStarted()
	{
		return fStarted;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.processing.activity.Startable#isStopped()
	 */
	public boolean isStopped()
	{
		return !fStarted;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.processing.activity.Startable#isKilled()
	 */
	public boolean isKilled()
	{
		return false;
	}

	public QueueProcessor getProcessor()
	{
		return fProcessor;
	}

	public Queue getQueue()
	{
		return fQueue;
	}
}



//--- Development History  ---------------------------------------------------
//
//$Log: Dequeuer.java,v $
//Revision 1.7  2006/03/31 16:19:42  smaher_cvs
//Added remote access to the internal queue and processor.
//
//Revision 1.6  2005/04/16 04:02:44  tames
//Updated to reflect changes in the Startable interface.
//
//Revision 1.5  2005/03/02 03:49:54  tames
//Changed the queue type to Queue in constructor.
//
//Revision 1.4  2005/03/01 23:25:15  chostetter_cvs
//Revised Queue design and package for better blocking behavior
//
//Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//Initial version
//
//Revision 1.2  2004/12/20 15:05:16  smaher_cvs
//Fixed bug 1735.  Added comments.
//
//Revision 1.1  2004/12/19 19:03:33  smaher_cvs
//Used to decouple queue processing threads from the queue consumers.
//

//