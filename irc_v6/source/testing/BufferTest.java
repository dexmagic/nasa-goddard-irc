import java.nio.ByteBuffer;

import gov.nasa.gsfc.commons.system.time.Stopwatch;

/**
 * Test various ways of holding and accessing data.
 * 
 * @author tames
 */
public class BufferTest
{
	private static int fSize;
	public static final int fMinSize = 1024;
	public static byte fTempValue = 0;

	public static void main(String[] args)
	{
		fSize = 20000 * fMinSize;
		
		ByteBuffer buffer = ByteBuffer.allocate(fSize);
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(fSize);
		ByteBuffer readOnlyBuffer = ByteBuffer.allocate(fSize).asReadOnlyBuffer();
		byte [] array = new byte[fSize];		
		double byteRate = 0.0;
		double benchmark = 1.0d;
		Stopwatch timer1 = new Stopwatch().warmUp();
		
		long bytesProcessed = 0;
		
        System.out.println("java.runtime.name = " + System.getProperty("java.runtime.name"));
        System.out.println("java.runtime.version = " + System.getProperty("java.runtime.version"));
        System.out.println("os.name = " + System.getProperty("os.name"));
        System.out.println("os.version = " + System.getProperty("os.version"));
        System.out.println ("Clock resolution = " + timer1.getTimerResolution() + " ms");
        System.out.println ("");
		System.out.println("Start benchmark...");

		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processArrayWithoutCopy(array);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;
		benchmark = byteRate;
		System.out.println(
			"Array Read Time :"
				+ timer1.getElapsedTime() + "ms "
			+ byteRate + " KBytes per ms "
			+ "(" + (byteRate / benchmark) + ")");

		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		timer1.reset();	
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processArray(array);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;
		System.out.println(
			"Array (Copy) Read Time :"
				+ timer1.getElapsedTime() + "ms "
			+ byteRate + " KBytes per ms "
			+ "(" + (byteRate / benchmark) + ")");


		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		timer1.reset();	
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processBuffer(buffer);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;

		System.out.println(
			"ByteBuffer Read Time:"
				+ timer1.getElapsedTime() + "ms "
				+ byteRate + " KBytes per ms "
				+ "(" + (byteRate / benchmark) + ")");

		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		timer1.reset();	
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processBuffer(readOnlyBuffer);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;

		System.out.println(
			"ByteBuffer (read Only) Read Time:"
				+ timer1.getElapsedTime() + "ms "
				+ byteRate + " KBytes per ms "
				+ "(" + (byteRate / benchmark) + ")");

		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		timer1.reset();	
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processBuffer(directBuffer);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;

		System.out.println(
			"ByteBuffer (direct) Read Time:"
				+ timer1.getElapsedTime() + "ms "
				+ byteRate + " KBytes per ms "
				+ "(" + (byteRate / benchmark) + ")");

		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		timer1.reset();	
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processBufferByBulk(buffer);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;

		System.out.println(
			"ByteBuffer (Bulk 1024 samples) Read Time:"
				+ timer1.getElapsedTime() + "ms "
				+ byteRate + " KBytes per ms "
				+ "(" + (byteRate / benchmark) + ")");

		// Reset for next test ------------------------------------------------
		System.gc();
		pause(1000);
		bytesProcessed = 0;
		timer1.reset();	
		System.out.print("Interval times: ");

		for (int i = 0; i < 200; i++)
		{
			timer1.start();
			bytesProcessed += processBufferBySmallBulk(buffer);
			timer1.stop();

			if (i % 50 == 0)
			{
				System.out.print(timer1.getIntervalTime() + " ");
			}
		}
		
		System.out.println(timer1.getIntervalTime() + "ms");
		byteRate = (double) (bytesProcessed / timer1.getElapsedTime()) / 1000;

		System.out.println(
			"ByteBuffer (Bulk 16 samples) Read Time:"
				+ timer1.getElapsedTime() + "ms "
				+ byteRate + " KBytes per ms "
				+ "(" + (byteRate / benchmark) + ")");

		System.out.println("Finished");
	}
	
	/**
	 * Simulate processing a buffer by using buffer slices for each chunk of
	 * data.
	 * 
	 * @param data the source data
	 * @return number of bytes touched
	 */
	public static long processBuffer(ByteBuffer data)
	{
		long bytesRead = 0;
		int size = data.capacity();
		
		for (int j = 0; j < size; j += fMinSize)
		{
			data.limit(j + fMinSize);
			data.position(j);
			ByteBuffer bufferSlice = data.slice();

			for (int i = 0; i < fMinSize; i++)
			{
				// Cache read value to prevent loop optimization.
				fTempValue = bufferSlice.get(i);
				bytesRead++;
			}
		}

		return bytesRead;
	}

	/**
	 * Simulate processing a buffer by using bulk gets for each chunk of
	 * data.
	 * 
	 * @param data the source data
	 * @return number of bytes touched
	 */
	public static long processBufferByBulk(ByteBuffer data)
	{
		long bytesRead = 0;
		int size = data.capacity();
		byte [] tempArray = new byte[fMinSize];
		
		for (int j = 0; j < size; j += fMinSize)
		{
			data.limit(j + fMinSize);
			data.position(j);
			data.get(tempArray);

			for (int i = 0; i < fMinSize; i++)
			{
				// Cache read value to prevent loop optimization.
				fTempValue = tempArray[i];
				bytesRead++;
			}
		}

		return bytesRead;
	}

	/**
	 * Simulate processing a buffer by using bulk gets for each chunk of
	 * data.
	 * 
	 * @param data the source data
	 * @return number of bytes touched
	 */
	public static long processBufferBySmallBulk(ByteBuffer data)
	{
		long bytesRead = 0;
		int size = data.capacity();
		int smallSize = 16;
		byte [] tempArray = new byte[smallSize];
		
		for (int j = 0; j < size; j += smallSize)
		{
			data.limit(j + smallSize);
			data.position(j);
			data.get(tempArray);

			for (int i = 0; i < smallSize; i++)
			{
				// Cache read value to prevent loop optimization.
				fTempValue = tempArray[i];
				bytesRead++;
			}
		}

		return bytesRead;
	}

	/**
	 * Simulate processing a buffer by copying into a smaller array for each
	 * chunk of data.
	 * 
	 * @param data the source data
	 * @return number of bytes touched
	 */
	public static long processArray(byte[] data)
	{
		long bytesRead = 0;
		int size = data.length;
		byte[] tempArray = new byte[fMinSize];
		
		for (int j = 0; j < size; j += fMinSize)
		{
			//byte[] tempArray = new byte[fMinSize];
			System.arraycopy(data, j, tempArray, 0, fMinSize);

			for (int i = 0; i < tempArray.length; i++)
			{
				// Cache read value to prevent loop optimization.
				fTempValue = tempArray[i];
				bytesRead++;
			}
		}

		return bytesRead;
	}

	/**
	 * Simulate processing a buffer by offset indexing into a large array for
	 * each chunk of data with out copying the source.
	 * 
	 * @param data the source data
	 * @return number of bytes touched
	 */
	public static long processArrayWithoutCopy(byte[] data)
	{
		long bytesRead = 0;
		int size = data.length;
		
		for (int j = 0; j < size; j += fMinSize)
		{
			for (int i = 0; i < fMinSize; i++)
			{
				// Cache read value to prevent loop optimization.
				fTempValue = data[j + i];
				bytesRead++;
			}
		}

		return bytesRead;
	}

	/**
	 * Simulate processing a buffer by copying into a smaller array for each
	 * chunk of data.
	 * 
	 * @param data the source data
	 * @return number of bytes touched
	 */
	public static void pause(long duration)
	{
		try
		{
			Thread.sleep(duration);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
