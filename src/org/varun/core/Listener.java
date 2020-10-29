package org.varun.core;

import javax.swing.JProgressBar;

public class Listener
{
	private JProgressBar progressBar;

	public JProgressBar getProgressBar()
	{
		return progressBar;
	}

	public void setProgressBar( JProgressBar progressBar )
	{
		this.progressBar = progressBar;
	}

	public void setMaxSize( int size )
	{
		progressBar.setMaximum( size );
	}

	public void setCount( int count )
	{
		progressBar.setValue( count );
	}
}
