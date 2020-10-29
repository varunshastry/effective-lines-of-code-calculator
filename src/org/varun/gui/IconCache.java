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
package org.varun.gui;

import org.varun.core.Constants;
import org.varun.core.PropReader;

import java.io.IOException;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Loads and stores icon images in a {@link WeakHashMap}.
 * 
 * @author varun
 */
public class IconCache
{

	private static Map<String, Icon> iconMap = new WeakHashMap<String, Icon>();

	/**
	 * Tries to get the icon for the given iconName. If an icon is not found, 
	 * then it tries to load it from the file system. The iconName-file name 
	 * mappings are available in iconMappings.properties.
	 * 
	 * @param iconName
	 * @return {@link Icon}
	 */
	public static Icon getIcon( String iconName )
	{
		Icon icon = iconMap.get( iconName );

		//If icon is not present in cache, create a new icon and put it.
		if ( icon == null )
		{
			PropertyResourceBundle prb = null;
			try
			{
				prb = PropReader.getInstance( Constants.ICON_MAPPINGS );
			}
			catch ( IOException ex )
			{
				Logger.getLogger( CustomFileView.class.getName() ).log( Level.SEVERE, null, ex );
			}
			icon = new ImageIcon( IconCache.class.getResource( prb.getString( iconName ) ) );
			putIcon( iconName, icon );
		}
		return icon;
	}

	/**
	 * Puts the icon with the given iconName in cache.
	 * 
	 * @param iconName
	 * @param icon
	 */
	public static void putIcon( String iconName, Icon icon )
	{
		iconMap.put( iconName, icon );
	}
}
