package org.varun.test;

import org.junit.Before;
import org.junit.Test;
import org.varun.core.Constants;
import org.varun.core.PropReader;
import org.varun.gui.IconCache;

import java.io.IOException;
import java.util.PropertyResourceBundle;

/**
 * @author varun
 */
public class IconTest
{
	private PropertyResourceBundle prb;

	@Before
	public void setUp() throws IOException
	{
		prb = PropReader.getInstance( Constants.ICON_MAPPINGS );
	}

	@Test
	public void testIconPaths() throws IOException
	{
		String[] iconTypes =
		{ Constants.AUDIO, Constants.COMPRESSED, Constants.IMAGE, Constants.OFF_DOC, Constants.PDF, Constants.PRESENTATION, Constants.SOURCE_CODE, Constants.SPREADSHEET, Constants.VIDEO, Constants.WEB_PAGE, Constants.DIRECTORY, Constants.GENERIC };
		for ( String iconName : iconTypes )
		{
			String iconPath = prb.getString( iconName );
			org.junit.Assert.assertNotNull( "Could not get the icon at: " + iconPath, IconCache.class.getResource( iconPath ) );
		}
	}

}
