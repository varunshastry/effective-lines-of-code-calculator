package org.varun.benchmark;

/**
 * @author varun
 */
public class Timer
{
	private long start;
	private long end;

	/**
	 * Initialize object.
	 */
	public Timer()
	{
		super();
	}

	/**
	 * Notes the start time of the timer. 
	 */
	public void start()
	{
		start = System.nanoTime();
	}

	/**
	 * Notes the end time of the timer.
	 */
	public void end()
	{
		end = System.nanoTime();
	}

	/**
	 * Returns timer duration in milli seconds.
	 * @return long
	 */
	public double getTimeInMillis()
	{
		return ( end - start ) / 1000000D;
	}

	/**
	 * Returns timer duration in seconds.
	 * @return double
	 */
	public double getTimeInSec()
	{
		return ( end - start ) / 1000000000D;
	}

	/**
	 * Returns timer duration in nano seconds.
	 * @return long
	 */
	public long getTimeInNanos()
	{
		return ( end - start );
	}

	/**
	 * Resets the timer. 
	 */
	public void reset()
	{
		start = 0;
		end = 0;
	}

}
