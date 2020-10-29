package org.varun.startup;

import org.varun.gui.UIMain;

public class Main
{
	public static void main( String[] args )
	{
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				UIMain.createAndShowGUI();
			}
		} );
	}
}
