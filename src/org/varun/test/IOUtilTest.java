package org.varun.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.varun.core.IOUtil;

/**
 * Tests methods in IOUtil
 * 
 * @author varun
 * 
 */
public class IOUtilTest
{

	private String tempRoot;
	private String binFile;
	private String textFile;

	private static final Logger logger = Logger.getLogger( "IOUtilTest" );

	@Before
	public void setUp() throws IOException
	{
		logger.log( Level.INFO, "**************** Set Up ****************" );

		// Create a directory hierarchy to test for recursion
		tempRoot = System.getProperty( "java.io.tmpdir" ) + "/junit.IOUtilTest/";

		File tempRootFile = new File( tempRoot );
		IOUtil.deleteDirectory( tempRootFile );

		File tempDir = new File( tempRoot );
		boolean mkdirs = tempDir.mkdirs();

		if ( !mkdirs )
		{
			throw new RuntimeException( "Could not create tmp dir" );
		}

		int length = 512;
		int[] intArr = new int[length];
		double[] dbArr = new double[length];
		Random random = new Random();

		for ( int i = 0; i < length; i++ )
		{
			intArr[i] = i;
			dbArr[i] = random.nextDouble();
		}

		binFile = tempRoot + "bin.file";

		// Create a binary file
		DataOutputStream outputStream = new DataOutputStream( new FileOutputStream( binFile ) );
		for ( int i = 0; i < length; i++ )
		{
			outputStream.writeInt( intArr[i] );
			outputStream.writeDouble( dbArr[i] );
		}
		outputStream.close();

		// Create a text file
		textFile = tempRoot + "text.file";
		BufferedWriter writer = new BufferedWriter( new FileWriter( textFile ) );
		writer.write( "line 1" );
		writer.write( "00000000000000000000" );
		writer.write( "sjdfhkshdklfsj" );
		writer.close();
	}

	@Test
	public void testDetectEncoding() throws IOException
	{
	}

	/**
	 * Test method for {@link IOUtil#isBinary(java.io.InputStream)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testIsBinary() throws IOException
	{
		FileInputStream inputStream = new FileInputStream( binFile );
		boolean binary = IOUtil.isBinary( inputStream );
		assertEquals( true, binary );
		inputStream.close();

		inputStream = new FileInputStream( textFile );
		binary = IOUtil.isBinary( inputStream );
		assertEquals( false, binary );
		inputStream.close();
	}

	/**
	 * Test method for {@link IOUtil#containsNullCharacter(java.io.Reader)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testContainsNullCharacter() throws IOException
	{

		// Binary file will have null characters
		FileInputStream inputStream = new FileInputStream( binFile );
		BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
		boolean containsNullCharacter = IOUtil.containsNullCharacter( reader );
		assertEquals( true, containsNullCharacter );
		reader.close();

		// Text file will not have null characters
		inputStream = new FileInputStream( textFile );
		reader = new BufferedReader( new InputStreamReader( inputStream ) );
		containsNullCharacter = IOUtil.containsNullCharacter( reader );
		assertEquals( false, containsNullCharacter );
		reader.close();
	}

	@After
	public void tearDown()
	{
		logger.log( Level.INFO, "**************** Tear Down ****************" );
		IOUtil.deleteDirectory( new File( tempRoot ) );
	}
}
