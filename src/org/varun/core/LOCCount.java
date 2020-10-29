package org.varun.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code LOCCount} class calculates lines of code in a given file or all
 * files in a directory or all files in a directory and its sub-directories. It
 * can ignore white spaces while calculating LOC count and report the no. of
 * white spaces.
 * 
 * @author varun
 */
public class LOCCount
{
	private int noOfSourceFiles;
	private int noOfNonSourceFiles;

	private int linesOfCode;
	private int effectiveLinesOfCode;
	private int ineffectiveLinesOfCode;
	private int importStatements;
	private int packageStatements;
	private int commentedLines;
	private int emptyLines;
	private List<File> fileList;
	private boolean ignoreEmptyLines;
	private Listener listener;

	private boolean isMultiLineCommentStarted = false;
	private boolean isMultiLineStatementStarted = false;

	private HashSet<String> fileNameSetToBeIncluded;
	private HashSet<String> fileNameSetToBeExcluded;

	private static final Logger logger = Logger.getLogger( "loccount" );

	/**
	 * Constructor. Forms a list of files for which to calculate lines of code
	 * count.
	 * 
	 * @param fileLocation
	 *            Can be a file or directory.
	 * @param scanRecursive
	 *            Flag to indicate whether to scan <tt>fileLocation</tt>
	 *            recursively for files if it is a directory.
	 * @param ignoreEmptyLines
	 *            Ignore white spaces while calculating line count.
	 * @param string 
	 */
	public LOCCount( String fileLocation, boolean scanRecursive, boolean ignoreEmptyLines, String inclFilesList, String exclFilesList )
	{

		//Initialize the logger
		logger.addHandler( new ConsoleHandler() );

		//Turn off logging by default
		logger.setLevel( Level.OFF );

		this.ignoreEmptyLines = ignoreEmptyLines;

		initializeFileNamesToBeMasked( inclFilesList, exclFilesList );

		logger.log( Level.INFO, "Getting file list..." );
		if ( scanRecursive )
		{
			fileList = getFileListRecursive( new File( fileLocation ), new ArrayList<File>() );
		}
		else
		{
			fileList = getFileListNonRecursive( new File( fileLocation ) );
		}

		for ( Iterator<File> it = fileList.iterator(); it.hasNext(); )
		{
			File file = it.next();
			for ( String mask : fileNameSetToBeExcluded )
			{
				mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
				if ( file.getName().matches( mask ) )
					it.remove();
			}
		}
	}

	private void initializeFileNamesToBeMasked( String inclFilesList, String exclFilesList )
	{
		fileNameSetToBeIncluded = new HashSet<String>();
		if ( !inclFilesList.isEmpty() )
		{
			String[] masksList = inclFilesList.split( Constants.SEMICOLON );
			for ( String mask : masksList )
				fileNameSetToBeIncluded.add( mask.trim() );
		}
		fileNameSetToBeExcluded = new HashSet<String>();
		if ( !exclFilesList.isEmpty() )
		{
			String[] masksList = exclFilesList.split( Constants.SEMICOLON );
			for ( String mask : masksList )
				fileNameSetToBeExcluded.add( mask.trim() );
		}
	}

	public int getLoc()
	{
		return this.linesOfCode;
	}

	public int getEmptyLines()
	{
		return this.emptyLines;
	}

	/**
	 * Scans the list of files obtained in
	 * {@link #LOCCount(String, boolean, boolean)} to calculate lines of code
	 * and white space counts. Binary files are ignored.
	 * 
	 * @throws IOException
	 */
	public void process() throws IOException
	{
		logger.log( Level.INFO, "Calculating loc count..." );

		if ( listener != null )
		{
			listener.setMaxSize( fileList.size() );
		}

		Iterator<File> it = fileList.iterator();

		int count = 0;

		while ( it.hasNext() )
		{

			if ( listener != null )
			{
				count++;
				listener.setCount( count );
			}

			File file = it.next();
			BufferedReader reader = null;
			try
			{

				FileInputStream fileInputStream = new FileInputStream( file );
				BufferedInputStream markedStream = IOUtil.getMarkedStream( fileInputStream );

				String encoding = IOUtil.detectEncoding( markedStream );
				// If an encoding is detected, this is a text stream
				if ( encoding == null )
				{
					markedStream.reset();
					String line = null;
					reader = new BufferedReader( new InputStreamReader( markedStream ) );

					while ( ( line = reader.readLine() ) != null )
					{
						line = line.trim();
						updateLocStatistics( line );
						linesOfCode++;
					}
					logger.log( Level.INFO, "Closing File: " + file.getAbsolutePath() );
					reader.close();
					noOfSourceFiles++;
				}
				else
					noOfNonSourceFiles++;
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace();
				throw e;
			}
		}

		logger.log( Level.INFO, "Done." );
	}

	private void updateLocStatistics( String line )
	{
		if ( ignoreEmptyLines && line.length() == 0 )
		{
			emptyLines++;
			return;
		}

		if ( isMultiLineCommentStarted )
		{
			if ( line.endsWith( Constants.MULTI_LINE_COMMENT_END ) )
			{
				commentedLines++;
				isMultiLineCommentStarted = false;
				return;
			}
			else if ( line.contains( Constants.MULTI_LINE_COMMENT_END ) )
			{
				effectiveLinesOfCode++;
				commentedLines++;
				isMultiLineCommentStarted = false;
				return;
			}
			return;
		}

		switch( line )
		{
		case Constants.OPENING_BRACE:
		case Constants.CLOSING_BRACE:
		case Constants.SEMICOLON:
		{
			if ( isMultiLineStatementStarted )
			{
				effectiveLinesOfCode++;
				isMultiLineStatementStarted = false;
			}
			else
				ineffectiveLinesOfCode++;
			return;
		}
		}

		if ( line.startsWith( Constants.IMPORT_STMT ) )
		{
			importStatements++;
			return;
		}

		if ( line.startsWith( Constants.PACKAGE_STMT ) )
		{
			packageStatements++;
			return;
		}

		if ( line.startsWith( Constants.SINGLE_LINE_COMMENT ) )
		{
			commentedLines++;
			return;
		}
		else if ( line.contains( Constants.SINGLE_LINE_COMMENT ) )
		{
			commentedLines++;
			effectiveLinesOfCode++;
			return;
		}

		if ( line.startsWith( Constants.MULTI_LINE_COMMENT_START ) )
		{
			commentedLines++;
			isMultiLineCommentStarted = true;
			return;
		}
		else if ( line.contains( Constants.MULTI_LINE_COMMENT_START ) )
		{
			effectiveLinesOfCode++;
			commentedLines++;
			isMultiLineCommentStarted = true;
			return;
		}

		if ( line.endsWith( Constants.MULTI_LINE_COMMENT_END ) )
		{
			commentedLines++;
			isMultiLineCommentStarted = false;
			return;
		}
		else if ( line.contains( Constants.MULTI_LINE_COMMENT_END ) )
		{
			effectiveLinesOfCode++;
			commentedLines++;
			isMultiLineCommentStarted = false;
			return;
		}

		if ( !line.contains( Constants.SEMICOLON ) )
		{
			isMultiLineStatementStarted = true;
			return;
		}
		effectiveLinesOfCode++;
	}

	/**
	 * Recursively scans all files codes present under <tt>file</tt> and
	 * returns them as a <code>List</code>. If <tt>file</tt> is of file
	 * type, returns a <code>List</code> with only one file.
	 * 
	 * @param file
	 * @param fileList
	 * @return A list of <code>File</code> objects.
	 */
	public List<File> getFileListRecursive( File file, List<File> fileList )
	{
		// Recursion could have been used for getting file list, but it would
		// mean a long stack of function calls for a deep directory tree
		List<File> dirList = new ArrayList<File>();

		if ( file.isDirectory() )
		{
			dirList.add( file );
		}
		else
		{
			fileList.add( file );
		}

		while ( !dirList.isEmpty() )
		{

			// Use a separate list to hold processed/new directories as dirList
			// cannot be modified while it is being read.
			List<File> rmvList = new ArrayList<File>();
			List<File> addList = new ArrayList<File>();

			for ( File file1 : dirList )
			{
				File[] files = file1.listFiles();
				for ( File file2 : files )
				{
					if ( file2.isDirectory() )
					{
						addList.add( file2 );
					}
					else
					{
						for ( String mask : fileNameSetToBeIncluded )
						{
							mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
							if ( file2.getName().matches( mask ) )
								fileList.add( file2 );
						}
						for ( String mask : fileNameSetToBeExcluded )
						{
							mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
							if ( file2.getName().matches( mask ) )
								fileList.remove( file2 );
						}
					}
				}
				rmvList.add( file1 );
			}

			for ( File file1 : rmvList )
			{
				dirList.remove( file1 );
			}
			for ( File file1 : addList )
			{
				dirList.add( file1 );
			}
		}

		return fileList;
	}

	/**
	 * If <tt>file</tt> is of file type, returns a <code>List</code>
	 * containing it. If it is a directory, returns a <code>List</code> of all
	 * files present in the directory excluding sub-directories.
	 * 
	 * @param file
	 * @return A list of <code>File</code> objects.
	 */
	public List<File> getFileListNonRecursive( File file )
	{
		List<File> fileList = new ArrayList<File>();
		if ( file.isDirectory() )
		{
			File[] files = file.listFiles();
			for ( int i = 0; i < files.length; i++ )
			{
				File file2 = files[i];
				if ( file2.isFile() )
				{
					for ( String mask : fileNameSetToBeIncluded )
					{
						mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
						if ( file2.getName().matches( mask ) )
							fileList.add( file2 );
					}
					for ( String mask : fileNameSetToBeExcluded )
					{
						mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
						if ( file2.getName().matches( mask ) )
							fileList.remove( file2 );
					}
					fileList.add( file2 );
				}
			}
		}
		else
		{
			for ( String mask : fileNameSetToBeIncluded )
			{
				mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
				if ( file.getName().matches( mask ) )
					fileList.add( file );
			}
			for ( String mask : fileNameSetToBeExcluded )
			{
				mask = mask.replaceAll( Constants.ASTERISK, Constants.WILD_CHARS );
				if ( file.getName().matches( mask ) )
					fileList.remove( file );
			}
		}

		return fileList;
	}

	/**
	 * @return A list of <code>File</code> objects.
	 */
	public List<File> getFileList()
	{
		return fileList;
	}

	/**
	 * Returns the results set in {@link #process()} as a <code>List</code> of
	 * <code>String</code>s.
	 * 
	 * @return A list of <code>String</code> objects.
	 */
	public List<String> getMessages()
	{
		List<String> messages = new ArrayList<String>();
		if ( noOfSourceFiles == 0 )
		{
			messages.add( "No source files found." );
			return messages;
		}
		messages.add( "No. of source files : " + noOfSourceFiles );
		messages.add( "No. of non-source files : " + noOfNonSourceFiles );
		if ( ignoreEmptyLines )
		{
			messages.add( "Total no. of non empty lines : " + linesOfCode );
			messages.add( "Empty Lines : " + emptyLines );
		}
		else
		{
			messages.add( "Total no. of lines : " + linesOfCode );
		}
		messages.add( "Total effective lines of code : " + effectiveLinesOfCode );
		messages.add( "Total ineffective lines of code : " + ineffectiveLinesOfCode );
		messages.add( "Total package statements : " + packageStatements );
		messages.add( "Total import statements : " + importStatements );
		messages.add( "Total commented lines of code : " + commentedLines );

		return messages;
	}

	public void setLogLevel( Level level )
	{
		logger.setLevel( level );
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{

		try
		{
			LOCCount counter = new LOCCount( "/home/varun/Programming/workspace-jsf/loccount/src", true, true, "*.*", "*.*" );

			counter.setLogLevel( Level.INFO );
			counter.process();
			System.out.println( "No. of files : " + counter.getFileList().size() );
			System.out.println( "Total no. of lines : " + counter.getLoc() );
			System.out.println( "Empty Lines   : " + counter.getEmptyLines() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public Listener getListener()
	{
		return listener;
	}

	public void setListener( Listener listener )
	{
		this.listener = listener;
	}
}