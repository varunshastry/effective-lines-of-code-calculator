package org.varun.gui;

import org.varun.core.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

/**
 * Provides custom icons for files and directories.
 * 
 * @author varun
 */
public class CustomFileView extends FileView
{

	//Pattern for extracting file extension
	private static Pattern pattern = Pattern.compile( "\\.([^\\.]*)$" );

	private static Map<String, List<String>> iconTypeMap = new HashMap<String, List<String>>();

	//Create a map of icon types and file extensions
	static
	{
		iconTypeMap.put( Constants.AUDIO, Arrays.asList( new String[]
		{ "wav", "mp3", "flac", "wma", "ra" } ) );
		iconTypeMap.put( Constants.COMPRESSED, Arrays.asList( new String[]
		{ "deb", "gz", "pkg", "rar", "zip", "jar", "war", "ear" } ) );
		iconTypeMap.put( Constants.IMAGE, Arrays.asList( new String[]
		{ "jpeg", "jpg", "gif", "png", "tiff", "bmp" } ) );
		iconTypeMap.put( Constants.OFF_DOC, Arrays.asList( new String[]
		{ "odt", "odf", "doc", "docx" } ) );
		iconTypeMap.put( Constants.PDF, Arrays.asList( new String[]
		{ "pdf" } ) );
		iconTypeMap.put( Constants.PRESENTATION, Arrays.asList( new String[]
		{ "odf", "ppt", "pptx" } ) );
		iconTypeMap.put( Constants.SOURCE_CODE, Arrays.asList( new String[]
		{ "java", "c", "cpp", "js", "css", "sh", "py", "txt" } ) );
		iconTypeMap.put( Constants.SPREADSHEET, Arrays.asList( new String[]
		{ "ods", "xls", "xlsx" } ) );
		iconTypeMap.put( Constants.VIDEO, Arrays.asList( new String[]
		{ "avi", "mpg", "mpeg", "ogg", "mp4", "mkv", "wmv", "rm", "asf", "mov", "3gp", "flv", "m4v" } ) );
		iconTypeMap.put( Constants.WEB_PAGE, Arrays.asList( new String[]
		{ "htm", "html", "xhtml", "jsp", "jspf", "jsf", "asp", "aspx", "php" } ) );
	}

	@Override
	public Icon getIcon( File file )
	{
		String iconType = getFileType( file );
		Icon icon = IconCache.getIcon( iconType );
		return icon;
	}

	private static String getFileType( File file )
	{

		if ( file.isDirectory() )
		{
			return Constants.DIRECTORY;
		}

		Matcher matcher = pattern.matcher( file.getName() );

		//If file has an extension
		if ( matcher.find() )
		{

			String extension = matcher.group( 1 );

			//Find iconType for the extension in the iconTypeMap
			for ( String fileType : iconTypeMap.keySet() )
			{
				List<String> list = iconTypeMap.get( fileType );
				if ( list.contains( extension.toLowerCase() ) )
				{
					return fileType;
				}
			}
		}

		return Constants.GENERIC;
	}
}