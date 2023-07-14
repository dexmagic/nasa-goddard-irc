//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.commons.system.time;

/**
 * A simple Stopwatch for timing intervals and elapsed time.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/05/11 15:44:49 $
 * @author	Troy Ames
 **/
public class Stopwatch
{
	private long fStopTime = 0;
	private long fStartTime = 0;
	private long fCumulativeTime = 0;
	
	private boolean fStarted = false;
	
	/**
	 * Default constructor for the Stopwatch class
	 */
	public Stopwatch()
	{
		super();
	}

	/**
	 * Start the Stopwatch. Does nothing if this Stopwatch is already started.
	 * 
	 * @return this instance.
	 */
	public synchronized Stopwatch start()
	{
		if (!fStarted)
		{
			fStartTime = System.currentTimeMillis();
			fStarted = true;
		}

		return this;
	}
	
	/**
	 * Stop the Stopwatch. If the Stopwatch is started then stopping it will
	 * update the cumulative elapsed time and stop the timer. Does nothing if
	 * already stopped.
	 * 
	 * @return this instance.
	 */
	public synchronized Stopwatch stop()
	{
		if (fStarted)
		{
			fStopTime = System.currentTimeMillis();
			fCumulativeTime += fStopTime - fStartTime;
			fStarted = false;
		}
		
		return this;
	}
	
	/**
	 * Get the elapsed cumulative time of all the interval times since the last
	 * reset or instance construction.
	 * 
	 * @return elapsed time in milliseconds
	 */
	public synchronized long getElapsedTime()
	{
		long result = fCumulativeTime;
		
		if (fStarted)
		{
			result += System.currentTimeMillis() - fStartTime;
		}
		
		return result;
	}
	
	/**
	 * Get the interval time since the last <code>start</code>. If the 
	 * Stopwatch is currently stopped then this returns the interval between 
	 * the last <code>start</code> and <code>stop</code> method calls.
	 * 
	 * @return interval time in milliseconds
	 */
	public synchronized long getIntervalTime()
	{
		long result;
		
		if (fStarted)
		{
			result = System.currentTimeMillis() - fStartTime;
		}
		else
		{
			result = fStopTime - fStartTime;
		}
		
		return result;
	}
	
	/**
	 * Resets the Stopwatch. Interval and elapsed cumulative times will be 
	 * set to 0 and the Stopwatch will be stopped.
	 * 
	 * @return this instance
	 */
	public synchronized Stopwatch reset()
	{
		fStarted = false;
		fStopTime = 0;
		fStartTime = 0;
		fCumulativeTime = 0;
		
		return this;
	}
	
	/**
	 * Get the timer resolution of this Stopwatch for this platform.
	 * 
	 * @return the timer resolution in milliseconds
	 */
	public synchronized long getTimerResolution()
	{
        long time = System.currentTimeMillis();
        long previousTime = time;
        long clockDelta = 0;

        for (int i = 0; i < 5; ++ i)
        {
            // Busy wait until system time changes:
            while (time == previousTime)
            {
                time = System.currentTimeMillis ();
            }
            clockDelta += time - previousTime;
            previousTime = time;
        }
        
        return clockDelta/5;
	}
	
	/**
	 * Warm up the JIT for this class. Some JIT implementations will compile 
	 * method calls after so many iterations. This method attempts to force this 
	 * before any timing to achieve the most accurate results. This method
	 * will also reset the Stopwatch.
	 * 
	 * @return this instance
	 */
	public synchronized Stopwatch warmUp()
	{
		// Prime the JIT compiler
        // JIT/hotspot warmup:
        for (int r = 0; r < 3000; ++ r) 
        {
        	start();
        	stop();
        }
        
        reset();
        
        return this;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: Stopwatch.java,v $
//  Revision 1.2  2005/05/11 15:44:49  tames_cvs
//  Synchronized methods for multithreaded clients.
//
//  Revision 1.1  2005/05/10 15:17:55  tames_cvs
//  Initial version
//
//