/*
 * Copyright (C) 2008 varun
 *
 * This file is part of LOC Calculator.
 *
 * LOC Calculator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LOC Calculator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LOC Calculator.  If not, see <http://www.gnu.org/licenses/>. 
 */

package org.varun.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.varun.core.IOUtil;
import org.varun.core.LOCCount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author varun
 */
public class LOCCountTest
{

	private String tempRoot;
	private String[] fileNames;
	private LOCCount locEmpty;
	private LOCCount locNonEmpty;

	private static final Logger logger = Logger.getLogger( "LOCCountTest" );

	/**
	 * Creates temporary dirs and files.
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		logger.log( Level.INFO, "**************** Set Up ****************" );

		//Create a directory hierarchy to test for recursion
		tempRoot = System.getProperty( "java.io.tmpdir" ) + "/junit.LOCCountTest/";

		File tempRootFile = new File( tempRoot );
		IOUtil.deleteDirectory( tempRootFile );

		String tmpDirs = tempRoot + "dir1/dir2/dir3";
		File tempDir = new File( tmpDirs );
		boolean mkdirs = tempDir.mkdirs();

		if ( !mkdirs )
		{
			throw new RuntimeException( "Could not create tmp dirs" );
		}

		logger.log( Level.INFO, "Temporary directories have been created." );

		tempDir = new File( tempRoot );

		//Create some files in the directories
		fileNames = new String[]
		{ tempRoot + "file1", tempRoot + "file2", tempRoot + "dir1/file", tempRoot + "dir1/dir2/file", tempRoot + "dir1/dir2/dir3/file1", tempRoot + "dir1/dir2/dir3/file2" };

		for ( String path : fileNames )
		{
			createTempFile( path );
		}

		locEmpty = new LOCCount( tempDir.getAbsolutePath(), true, false, "*.*", "*.*" );
		locNonEmpty = new LOCCount( tempDir.getAbsolutePath(), true, true, "*.*", "*.*" );

		locEmpty.setLogLevel( Level.FINEST );
		locNonEmpty.setLogLevel( Level.FINEST );

		locEmpty.process();
		locNonEmpty.process();
	}

	private void createTempFile( String filePath ) throws IOException
	{

		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( filePath ) ) );

		bw.write( "Line 1\n" );
		bw.write( "Line 2\n" );
		bw.write( "Line 3\n" );
		bw.write( "Line 4\n" );

		//Add some Empty lines
		bw.write( "\n" );
		bw.write( "  \n" );

		bw.write( "Line 5\n" );
		bw.write( "Line 6\n" );

		bw.close();

		logger.log( Level.INFO, "Temp File : " + filePath + " created." );
	}

	/**
	 * Cleans up the temporary dirs and files created while setUp.
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{

		logger.log( Level.INFO, "**************** Tear Down ****************" );

		File tempRootFile = new File( tempRoot );
		IOUtil.deleteDirectory( tempRootFile );
	}

	/**
	 * Test method for {@link org.varun.core.LOCCount#getLoc()}.
	 */
	@Test
	public void testGetLoc()
	{

		logger.log( Level.INFO, "**************** testGetLoc ****************" );

		//6 files with 8 lines each. Total 48 lines
		assertEquals( 48, locEmpty.getLoc() );

		//6 files with 6 non-empty lines each. Total 36 lines
		assertEquals( 36, locNonEmpty.getLoc() );
	}

	/**
	 * Test method for {@link org.varun.core.LOCCount#getEmptyLines()}.
	 */
	@Test
	public void testGetEmptyLines()
	{

		logger.log( Level.INFO, "**************** testGetEmptyLines ****************" );

		//6 files with 2 non-empty lines each. Total 12 lines
		assertEquals( 12, locNonEmpty.getEmptyLines() );
	}

	/**
	 * Test method for {@link org.varun.core.LOCCount#getFileListRecursive(java.io.File, java.util.List)}.
	 */
	@Test
	public void testGetFileListRecursive()
	{

		logger.log( Level.INFO, "**************** testGetFileListRecursive ****************" );

		LOCCount locCount = new LOCCount( new File( tempRoot ).getAbsolutePath(), true, false, "*.*", "*.*" );
		List<File> fileList = locCount.getFileListRecursive( new File( tempRoot ), new ArrayList<File>() );

		//In all there are 6 files
		assertEquals( 6, fileList.size() );

		//Now verify the file names
		int count = 0;
		List<File> existFiles = new ArrayList<File>();
		for ( String path : fileNames )
		{
			File file = new File( path );
			if ( !existFiles.contains( file ) && fileList.contains( file ) )
			{
				existFiles.add( file );
				count++;
			}
		}

		//There should have been 6 mathces
		assertEquals( 6, count );
	}

	/**
	 * Test method for {@link org.varun.core.LOCCount#getFileListNonRecursive(java.io.File)}.
	 */
	@Test
	public void testGetFileListNonRecursive()
	{

		logger.log( Level.INFO, "**************** testGetFileListNonRecursive ****************" );

		LOCCount locCount = new LOCCount( ".", false, true, "*.*", "" );
		List<File> fileList = locCount.getFileListNonRecursive( new File( tempRoot ) );

		//There should be are 2 files in temp root
		assertEquals( 2, fileList.size() );

		//Now verify the file names
		String[] filePaths =
		{ tempRoot + "file1", tempRoot + "file2" };

		int count = 0;
		List<File> existFiles = new ArrayList<File>();
		for ( String path : filePaths )
		{
			File file = new File( path );
			if ( !existFiles.contains( file ) && fileList.contains( file ) )
			{
				existFiles.add( file );
				count++;
			}
		}

		//There should have been 2 mathces
		assertEquals( 2, count );
	}
}