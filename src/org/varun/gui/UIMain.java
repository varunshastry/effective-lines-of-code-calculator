package org.varun.gui;

import org.varun.core.Constants;
import org.varun.core.LOCCount;
import org.varun.core.Listener;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

@SuppressWarnings( "serial" )
public class UIMain extends JPanel implements ActionListener
{

	private JButton openButton;
	private JTextArea log;
	private JTextField textField;
	private JButton calculateButton;
	private JFileChooser fc;
	private JCheckBox recursiveOption;
	private JTextField inclFilesInput;
	private JTextField exclFilesInput;
	private JProgressBar progressBar;

	public UIMain()
	{
		//Use border layout
		super( new BorderLayout() );

		//Create a file chooser
		fc = new JFileChooser();
		fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		fc.setFileView( new CustomFileView() );

		JPanel fileChooserPanel = new JPanel(); //use FlowLayout

		//Location label and text field
		textField = new JTextField( 30 );
		textField.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
		InputMap inputMap = textField.getInputMap( JComponent.WHEN_FOCUSED );

		//Call Calculate on pressing Enter
		KeyStroke key = KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 );
		inputMap.put( key, "enter" );
		textField.getActionMap().put( "enter", new AbstractAction()
		{
			public void actionPerformed( ActionEvent e )
			{
				calculateButton.doClick();
			}
		} );

		JLabel label = new JLabel( "Location" );

		//Create the browse button.
		openButton = new JButton( "Open" );
		openButton.setMnemonic( KeyEvent.VK_O );
		openButton.addActionListener( this );
		openButton.setIcon( IconCache.getIcon( Constants.DOCUMENT_OPEN ) );

		fileChooserPanel.add( label );
		fileChooserPanel.add( textField );
		fileChooserPanel.add( openButton );

		//Options panel
		JPanel optionsPanel = new JPanel();
		recursiveOption = new JCheckBox( "Scan Recursively" );
		recursiveOption.setMnemonic( KeyEvent.VK_S );
		recursiveOption.setSelected( true );
		recursiveOption.setLocation( 5, 25 );

		optionsPanel.add( recursiveOption );

		//Calculate panel
		JPanel calculatePanel = new JPanel();
		calculateButton = new JButton( "Calculate LOC" );
		calculateButton.setMnemonic( KeyEvent.VK_C );
		calculateButton.addActionListener( this );
		calculateButton.setIcon( IconCache.getIcon( Constants.CALCULATE ) );
		calculatePanel.add( calculateButton );

		JPanel fileMaskPanel = new JPanel();

		JLabel inclFilesLabel = new JLabel( "Include files:" );
		fileMaskPanel.add( inclFilesLabel );

		inclFilesInput = new JTextField( 10 );
		fileMaskPanel.add( inclFilesInput );

		JLabel exclFilesLabel = new JLabel( "Exclude files:" );
		fileMaskPanel.add( exclFilesLabel );

		exclFilesInput = new JTextField( 10 );
		fileMaskPanel.add( exclFilesInput );

		//Create the progress bar
		JPanel bottomPanel = new JPanel( new BorderLayout() );
		progressBar = new JProgressBar();
		progressBar.setMinimum( 0 );
		progressBar.setCursor( null );
		progressBar.setStringPainted( true );

		//Create the log area
		log = new JTextArea( 10, 40 );
		log.setMargin( new Insets( 5, 5, 5, 5 ) );
		log.setEditable( false );
		JScrollPane logScrollPane = new JScrollPane( log );
		logScrollPane.setBorder( new EtchedBorder( EtchedBorder.LOWERED ) );

		bottomPanel.add( progressBar, BorderLayout.PAGE_START );
		bottomPanel.add( logScrollPane, BorderLayout.PAGE_END );

		//Add the buttons and the log to this panel.

		add( fileChooserPanel, BorderLayout.PAGE_START );
		add( fileMaskPanel, BorderLayout.LINE_START );
		add( optionsPanel );
		add( calculatePanel, BorderLayout.LINE_END );
		add( bottomPanel, BorderLayout.PAGE_END );

		addGlobalKeyBindings();
	}

	@SuppressWarnings( "serial" )
	private void addGlobalKeyBindings()
	{
		InputMap inputMap = getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW );

		//Close frame on pressing Esc
		KeyStroke key = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 );
		inputMap.put( key, "esc" );
		getActionMap().put( "esc", new AbstractAction()
		{

			public void actionPerformed( ActionEvent e )
			{
				JPanel panel = ( JPanel ) e.getSource();
				JFrame frame = ( JFrame ) panel.getTopLevelAncestor();
				frame.dispose();
			}
		} );
	}

	public void actionPerformed( ActionEvent e )
	{

		// Handle open button action.
		if ( e.getSource() == openButton )
		{
			int returnVal = fc.showOpenDialog( UIMain.this );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = fc.getSelectedFile();
				textField.setText( file.getPath() );
			}

			// Handle calculate button action.
		}
		else if ( e.getSource() == calculateButton )
		{

			log.setText( "" );
			calculateButton.setEnabled( false );
			progressBar.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
			progressBar.setValue( progressBar.getMinimum() );

			//Create a worker thread for calculating loc
			SwingWorker<List<String>> swingWorker = new SwingWorker<List<String>>()
			{

				@Override
				public List<String> construct()
				{
					List<String> messages = new ArrayList<String>();
					LOCCount counter = new LOCCount( textField.getText(), recursiveOption.isSelected(), true, inclFilesInput.getText(), exclFilesInput.getText() );
					try
					{

						// Set listener in counter. This will update the
						// progressBar.
						Listener listener = new Listener();
						listener.setProgressBar( progressBar );
						counter.setListener( listener );

						counter.process();
						messages = counter.getMessages();
					}
					catch ( Exception e )
					{
						messages.add( "File or Directory not found" );
						e.printStackTrace();
					}
					return messages;
				}

				@Override
				public void finished()
				{
					List<String> messages = getValue();
					for ( String msg : messages )
					{
						log.append( msg + "\n" );
					}
					calculateButton.setEnabled( true );
					progressBar.setCursor( null );
				}
			};
			swingWorker.start();
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public static void createAndShowGUI()
	{
		//String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try
		{
			UIManager.setLookAndFeel( lookAndFeel );
			UIManager.put( "FileChooser.readOnly", Boolean.TRUE );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		JFrame frame = new JFrame( "LOC Calculator" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setLocation( 350, 284 );
		frame.setSize( 350, 284 );

		//Create and set up the content pane.
		JComponent newContentPane = new UIMain();
		newContentPane.setOpaque( true ); //content panes must be opaque
		frame.setContentPane( newContentPane );

		//Display the window.
		SwingUtilities.updateComponentTreeUI( frame );
		frame.pack();
		frame.setVisible( true );
		frame.setResizable( false );
	}

	public static void main( String[] args )
	{
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		} );
	}
}