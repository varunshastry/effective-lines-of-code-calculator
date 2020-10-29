package org.varun.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author varun 
 */
public class IOUtil
{

	private static final int BOM16 = 0xfeff;
	private static final byte[] UTF8BOM =
	{ ( byte ) 0xef, ( byte ) 0xbb, ( byte ) 0xbf };

	private IOUtil()
	{
	}

	/**
	 * Algorithm for detecting encoding was taken from the jEdit project (<a
	 * href="http://www.jedit.org/">http://www.jedit.org/</a>).
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static String detectEncoding( InputStream inputStream ) throws IOException
	{
		byte[] mark = new byte[4];
		int count = inputStream.read( mark );

		byte low = ( byte ) ( BOM16 & 0xff );
		byte high = ( byte ) ( ( BOM16 >> 8 ) & 0xff );
		if ( count >= 4 )
		{
			if ( mark[0] == low && mark[1] == high && mark[2] == 0x00 && mark[3] == 0x00 )
			{
				return "X-UTF-32LE-BOM";
			}
			else if ( mark[0] == 0x00 && mark[1] == 0x00 && mark[2] == high && mark[3] == low )
			{
				return "X-UTF-32BE-BOM";
			}
		}
		if ( count >= 2 )
		{
			if ( mark[0] == low && mark[1] == high )
			{
				return "x-UTF-16LE-BOM";
			}
			else if ( mark[0] == high && mark[1] == low )
			{
				// "x-UTF-16BE-BOM" does not available.
				// But an encoder for "UTF-16" actually uses
				// big endian with corresponding BOM. It just
				// works as "UTF-16BE with BOM".
				return "UTF-16";
			}
		}

		if ( count >= UTF8BOM.length )
		{
			int i = 0;
			while ( i < UTF8BOM.length )
			{
				if ( mark[i] != UTF8BOM[i] )
				{
					break;
				}
				++i;
			}
			if ( i == UTF8BOM.length )
			{
				return "UTF-8Y";
			}
		}

		return null;
	}

	public static boolean isBinary( InputStream in ) throws IOException
	{
		BufferedInputStream markedStream = getMarkedStream( in );
		String encoding = detectEncoding( markedStream );
		// If an encoding is detected, this is a text stream
		if ( encoding != null )
		{
			return false;
		}
		// Read the stream in system default encoding. The encoding
		// might be wrong. But enough for binary detection.
		markedStream.reset();
		return containsNullCharacter( new InputStreamReader( markedStream ) );
	}

	public static boolean containsNullCharacter( Reader reader ) throws IOException
	{
		int nbChars = 100;
		int authorized = 1;
		for ( long i = 0L; i < nbChars; i++ )
		{
			int c = reader.read();
			if ( c == -1 )
			{
				return false;
			}
			if ( c == 0 )
			{
				authorized--;
				if ( authorized == 0 )
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a marked, rewindable stream.
	 * Calling reset() method rewinds the stream to its beginning.
	 * But reset() can fail if too long bytes were read.
	 */
	public static BufferedInputStream getMarkedStream( InputStream in )
	{
		int bufferSize = 8192;
		BufferedInputStream markable = new BufferedInputStream( in, bufferSize );
		assert ( markable.markSupported() );
		markable.mark( bufferSize );
		return markable;
	}

	/**
	 * Recursively deletes a directory indicated by the <code>path</code>
	 * argument.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteDirectory( File path )
	{
		if ( path.exists() )
		{
			File[] files = path.listFiles();
			for ( File file : files )
			{
				if ( file.isDirectory() )
				{
					deleteDirectory( file );
				}
				else
				{
					boolean delete = file.delete();
					if ( !delete )
					{
						throw new RuntimeException( "Could not delete file: " + file.getAbsolutePath() );
					}
				}
			}
		}
		return ( path.delete() );
	}
}