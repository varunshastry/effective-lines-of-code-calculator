package org.varun.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

/**
 * @author varun
 */
public class PropReader
{

	private static Map<String, PropertyResourceBundle> propMap = new HashMap<String, PropertyResourceBundle>();

	public static PropertyResourceBundle getInstance( String name ) throws IOException
	{
		PropertyResourceBundle pr = propMap.get( name );
		if ( pr == null )
		{
			synchronized (propMap)
			{
				pr = new PropertyResourceBundle( PropReader.class.getResourceAsStream( name ) );
				propMap.put( name, pr );
			}
		}
		return pr;
	}
}
