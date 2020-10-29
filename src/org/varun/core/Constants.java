package org.varun.core;

/**
 * @author varun
 */
public interface Constants
{
	// Ineffective LOC
	String OPENING_BRACE = "{";
	String CLOSING_BRACE = "}";
	String SEMICOLON = ";";
	String IMPORT_STMT = "import";
	String PACKAGE_STMT = "package";
	
	// Comment LOC
	String SINGLE_LINE_COMMENT = "//";
	String MULTI_LINE_COMMENT_START = "/*";
	String MULTI_LINE_COMMENT_END = "*/";

	// Filemask constants
	String ASTERISK = "\\*";
	String WILD_CHARS = "[a-zA-Z0-9_]*";

	//Icon Types
	String AUDIO = "audio";
	String COMPRESSED = "compressed";
	String IMAGE = "image";
	String OFF_DOC = "offDoc";
	String PDF = "pdf";
	String PRESENTATION = "presentation";
	String SOURCE_CODE = "sourceCode";
	String SPREADSHEET = "spreadsheet";
	String VIDEO = "video";
	String WEB_PAGE = "webpage";
	String DOCUMENT_OPEN = "document-open";
	String CALCULATE = "calculate";
	String GENERIC = "generic";
	String DIRECTORY = "directory";

	//Properties files
	String ICON_MAPPINGS = "/org/varun/resources/iconMappings.properties";
}
