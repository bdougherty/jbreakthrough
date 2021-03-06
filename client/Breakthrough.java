/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 26, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0.1
 * Breakthrough Client Application GUI
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Breakthrough extends JFrame implements BreakthroughListener {
	private JTextField addressTF;
	static Breakthrough breakthrough;
	private JButton [][] button;
	private JPanel buttonPanel;
	private JFrame configFrame;
	private JButton connectButton;
	private boolean debugMode;
	private GameManager gameManager;
	private JLabel infoLBL;
	private boolean inReplay;
	private BreakthroughLogger logger;
	private String myName;
	private JTextField nameTF;
	private String opponentName;
	private BreakthroughReplay replay;
	private JCheckBox soundCK;
	private JLabel statusLBL;
	private int team;
	private ImageIcon team1Ico;
	private ImageIcon team2Ico;
	private static String[] themes = {"Default", "Halo", "Mario", "OS"};
	private JComboBox themesCB;
	private ImageIcon titleIco;
	private JLabel welcomeLBL;
	
	private final static int BREAKTHROUGH_PORT = 16789;
	private final static String BREAKTHROUGH_VERSION = "1.1";
	private final static String BREAKTHROUGH_DATE = "2007-05-20";
	
	// Internationalization
	private Locale l = Locale.getDefault();
	private ResourceBundle rb = ResourceBundle.getBundle("Breakthrough",l);
	
	/**
	 * Default constructor - creates the connection and game managers, initializes components, shows config dialog
	 */
	public Breakthrough() {
		gameManager = new GameManager(false);
		logger = new BreakthroughLogger();
		replay = new BreakthroughReplay(this, logger);
		gameManager.addListener(this);
		gameManager.addListener(logger);
		init();
		layoutConfig();
	}
	
	/**
	 * Constructor - creates the connection and game managers, initializes components, shows config dialog, fills in information
	 */
	public Breakthrough(String address, String name, int theme, boolean enableSound, boolean debugMode) {
		gameManager = new GameManager(debugMode);
		logger = new BreakthroughLogger();
		replay = new BreakthroughReplay(this, logger);
		gameManager.addListener(this);
		gameManager.addListener(logger);
		init();
		this.debugMode = debugMode;
		layoutConfig();
		addressTF.setText(address);
		nameTF.setText(name);
		themesCB.setSelectedIndex(theme);
		soundCK.setSelected(enableSound);
	}
	
	/**
	 * Listener method for pieces - sends info to game manager when a piece is clicked on
	 * @param info The formatted string of the piece clicked on
	 */
	private void button_actionPerformed(final String info) {
		new Thread() {
			public void run() {
				gameManager.pieceSelected(info);
			}
		}.start();
	}
	
	/**
	 * Listener method for connect button - tells the game manager to connect
	 */
	private void connectButton_actionPerformed() {
		new Thread() {
			public void run() {
				
				// Check to see that the user entered a name
				if (nameTF.getText().equals("")) {
					statusChanged(new StatusChangeEvent("errorMustEnterName", StatusChangeEvent.ERROR_COLOR));
					nameTF.requestFocus();
				}
				else if (nameTF.getText().length() > 40) {
					statusChanged(new StatusChangeEvent("errorNameTooLong", StatusChangeEvent.ERROR_COLOR));
					nameTF.requestFocus();
				}
				else {
					
					String address = addressTF.getText();
					int port = BREAKTHROUGH_PORT;
					try {
						port = Integer.parseInt(address.substring(address.indexOf(":")+1, address.length()));
					}
					catch (Exception e) {}
					
					if (debugMode) {
						System.out.println("Using port: "+port);
					}
					
					gameManager.connect(address, port, nameTF.getText());
				}
				
			}
		}.start();
	}
	
	/**
	 * Connected method - when the game is fully initialized
	 * @param e The ConnectionEvent
	 */
	public void connected(final ConnectionEvent e) {
		// Get the game information from the event
		myName = e.getMyName();
		opponentName = e.getOpponentName();
		team = e.getTeam();
		welcomeLBL.setText(rb.getString("welcomeMessage")+", "+myName+"! "+rb.getString("playing")+" "+opponentName+".");
		
		// Layout the game board and hide the config
		layoutComponents();
		configFrame.setVisible(false);
		
		// Reset the enabled status of components
		setConfigComponentsEnabled(true);
	}
	
	/**
	 * Begining connection method - updates GUI when beginning the connection
	 */
	public void connectionBeginning() {
		// Disable the components
		setConfigComponentsEnabled(false);
		connectButton.requestFocus();
		statusChanged(new StatusChangeEvent("statusConnecting", StatusChangeEvent.CONFIG_COLOR));
	}
	
	/**
	 * Connection error method - when there is an error with the connection
	 * @param e The ConnectionErrorEvent
	 */
	public void connectionError(final ConnectionErrorEvent e) {
		// Reset the enabled status of components
		addressTF.requestFocus();
		setConfigComponentsEnabled(true);
		
		// Prompt the user to play again
		if (e.shouldReset()) {
			promptToPlayAgain(rb.getString("error"), rb.getString("errorConnectionLost"), JOptionPane.ERROR_MESSAGE, false);
		}
	}
	
	/**
	 * Game over - when the game is over, either a win, loss, or error
	 * @param e The GameOverEvent
	 */
	public void gameOver(final GameOverEvent e) {
		String message;
		String title;
		if (e.isError()) {
			message = opponentName+" "+rb.getString("errorOpponentDisconnected");
			title = "Opponent Disconnected";
			
			// If sound is enabled
			playSound("error.wav");
			
			promptToPlayAgain(title, message, JOptionPane.INFORMATION_MESSAGE, false);
		}
		else if (e.isWinner()) {
			message = rb.getString("beat")+" "+opponentName+"!";
			title = rb.getString("winner")+"!";
			
			// If sound is enabled
			// cool-aid guy 'oh-yeah' sound
			playSound("won.wav");
			
			promptToPlayAgain(title, message, JOptionPane.INFORMATION_MESSAGE, true);
			
		}
		else {
			message = rb.getString("lostPart1")+" "+opponentName+" "+rb.getString("lostPart2");
			title = rb.getString("loser");
			
			// If sound is enabled
			playSound("lost.wav");
			
			promptToPlayAgain(title, message, JOptionPane.INFORMATION_MESSAGE, true);
		}
	}
	
	/**
	 * Get My Menu Bar - generates menu bar
	 * @param full Whether or not to generate full menu
	 * @return menuBar The JMenuBar
	 */
	private JMenuBar getMyMenuBar(boolean full) {
		JMenuBar menuBar = new JMenuBar();
		
		if (full) {
			JMenu gameMenu = new JMenu(rb.getString("gameMenu"));
			JMenuItem changeTheme = new JMenuItem(rb.getString("gameMenuChangeTheme")+"...");
			JMenuItem disconnect = new JMenuItem(rb.getString("gameMenuDisconnect"));
			JMenuItem exit = new JMenuItem(rb.getString("gameMenuExit"));
			gameMenu.add(changeTheme);
			gameMenu.add(new JSeparator());
			gameMenu.add(disconnect);
			if (System.getProperty("mrj.version") == null) {
				gameMenu.add(exit);
				exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						promptForExit();
					}
				});
			}
			menuBar.add(gameMenu);
			
			changeTheme.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JOptionPane.showMessageDialog(breakthrough, themesCB, "Select theme", JOptionPane.INFORMATION_MESSAGE);
					setIconTheme(themes[themesCB.getSelectedIndex()]);
				}
			});
			disconnect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					promptForDisconnect();
				}
			});
			
			if (debugMode) {
				JMenu debugMenu = new JMenu("DEBUG");
				JMenuItem changeTeam = new JMenuItem("Change Team");
				JMenuItem simulateWin = new JMenuItem("Simulate Win");
				JMenuItem simulateLoss = new JMenuItem("Simulate Loss");
				JMenuItem simulateReset = new JMenuItem("Reset");

				debugMenu.add(changeTeam);
				debugMenu.add(new JSeparator());
				debugMenu.add(simulateWin);
				debugMenu.add(simulateLoss);
				debugMenu.add(new JSeparator());
				debugMenu.add(simulateReset);

				menuBar.add(debugMenu);

				changeTeam.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						reset();
					}
				});
				simulateWin.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						gameOver(new GameOverEvent(true));
					}
				});
				simulateLoss.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						gameOver(new GameOverEvent(false));
					}
				});
				simulateReset.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						reset();
					}
				});
			}
		}
		
		JMenu helpMenu = new JMenu(rb.getString("helpMenu"));
		JMenuItem rules = new JMenuItem(rb.getString("helpMenuRules")+"...");
		JMenuItem about = new JMenuItem(rb.getString("helpMenuAbout"));
		helpMenu.add(rules);
		helpMenu.add(about);
		menuBar.add(helpMenu);
		
		rules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				JTextArea text = new JTextArea(10,40);
				JScrollPane scroll = new JScrollPane(text);
				text.append(rb.getString("rules"));
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				text.setCaretPosition(0);
				text.setEditable(false);
				
				JOptionPane.showMessageDialog(breakthrough, scroll, rb.getString("rulesDialogTitle"), JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JOptionPane.showMessageDialog(breakthrough, rb.getString("aboutText"), rb.getString("aboutTitle"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		return menuBar;
	}
	
	/**
	 * Initializes common components
	 */
	private void init() {
		// Set the look and feel to the default of the OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {}
		
		button = new JButton[8][8];
		addressTF = new JTextField(10);
		addressTF.setToolTipText(rb.getString("addressTFTooltip"));
		nameTF = new JTextField(10);
		nameTF.setToolTipText(rb.getString("nameTFTooltip"));
		themesCB = new JComboBox(themes);
		themesCB.setToolTipText(rb.getString("themesCBTooltip"));
		soundCK = new JCheckBox(rb.getString("configEnableSoundsBox"),true);
		soundCK.setToolTipText(rb.getString("soundCKTooltip"));
		statusLBL = new JLabel(rb.getString("statusPrefix")+": "+rb.getString("statusWaitingForInformation"));
		welcomeLBL = new JLabel();
		infoLBL = new JLabel(rb.getString("welcomeMessage"));
		titleIco = new ImageIcon(this.getClass().getResource("images/title.png"));
		connectButton = new JButton(rb.getString("configConnectButtonLabel"));
		inReplay = false;
	}
	
	/**
	 * Creates the main window
	 */
	private void layoutComponents() {
		JPanel welcomePanel = new JPanel();
		buttonPanel = new JPanel(new GridLayout(8,0));
		JPanel infoPanel = new JPanel();
		
		// Player will always be on the left
		if (team == 2) {
			buttonPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		
		// Set up buttons
		for (int i = 0; i < button.length; i++) {
			for (int j = 0; j < button[i].length; j++) {
				
				button[i][j] = new JButton();
				button[i][j].putClientProperty("JButton.buttonType","toolbar"); // Square buttons on Mac
				buttonPanel.add(button[i][j]);
				
				if (j == 0 || j == 1) {
					button[i][j].setText("");
					button[i][j].setActionCommand("1"+j+i+"");
				}
				else if (j == 6 || j == 7) {
					button[i][j].setText("");
					button[i][j].setActionCommand("2"+j+i+"");
				}
				else {
					button[i][j].setActionCommand("0"+j+i+"");
				}
				
				button[i][j].addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							button_actionPerformed(e.getActionCommand());
						}
					}
				);
				
			}
		}
		
		// Set up icons
		setIconTheme(themes[themesCB.getSelectedIndex()]);
		
		welcomePanel.add(welcomeLBL);
		infoPanel.add(statusLBL);
		
		this.add(welcomePanel, BorderLayout.NORTH);
		this.add(buttonPanel);
		this.add(infoPanel, BorderLayout.SOUTH);
		this.setJMenuBar(getMyMenuBar(true));
		this.setTitle("Breakthrough");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					promptForExit();
				}
			}
		);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null); // Center on screen
		this.setVisible(true);
	}
	
	/**
	 * Creates the configuration dialog
	 */
	private void layoutConfig() {
		configFrame = new JFrame();
		JLabel addressLabel = new JLabel(rb.getString("configServerAddressLabel")+":");
		JLabel nameLabel = new JLabel(rb.getString("configYourNameLabel")+":");
		JLabel piecesLabel = new JLabel(rb.getString("configTypeOfPiecesLabel")+":");
		JLabel soundLabel = new JLabel(rb.getString("configSoundsLabel")+":");
		JLabel titleLabel = new JLabel(titleIco);
		JPanel imagePanel = new JPanel();
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		JPanel fieldsPanel = new JPanel();
		JPanel connectPanel = new JPanel();
		JPanel center = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel();
		JPanel statusLabelPanel = new JPanel();
		
		addressTF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectButton_actionPerformed();
			}
		});
		nameTF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectButton_actionPerformed();
			}
		});
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectButton_actionPerformed();
			}
		});
		
		imagePanel.add(titleLabel);
		optionsPanel.add(addressLabel);
		optionsPanel.add(addressTF);
		optionsPanel.add(nameLabel);
		optionsPanel.add(nameTF);
		optionsPanel.add(piecesLabel);
		optionsPanel.add(themesCB);
		optionsPanel.add(soundLabel);
		optionsPanel.add(soundCK);
		fieldsPanel.add(optionsPanel);
		connectPanel.add(connectButton);
		
		if (debugMode) {
			JButton debugButton = new JButton("Game Board");
			connectPanel.add(debugButton);
			debugButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int teamSelected = 0;
					do {
						try {
							teamSelected = Integer.parseInt(JOptionPane.showInputDialog(breakthrough,"What team?"));
						}
						catch (Exception e) {
							teamSelected = 0;
						}
						System.out.println("Team selected: "+teamSelected);
					}
					while (teamSelected != 1 && teamSelected != 2);
					team = teamSelected;
					connected(new ConnectionEvent(team, "Debug Mode", "no one"));
				}
			});
		}
		
		center.add(fieldsPanel, BorderLayout.NORTH);
		center.add(connectPanel, BorderLayout.CENTER);
		center.add(new JPanel(), BorderLayout.SOUTH);
		statusLabelPanel.setBackground(Color.LIGHT_GRAY);
		statusLabelPanel.add(statusLBL);
		statusPanel.setBackground(Color.LIGHT_GRAY);
		statusPanel.add(statusLabelPanel);
		
		configFrame.add(imagePanel, BorderLayout.NORTH);
		configFrame.add(center);
		configFrame.add(statusPanel, BorderLayout.SOUTH);
		
		configFrame.setTitle("Breakthrough");
		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//if (System.getProperty("mrj.version") != null) {
			configFrame.setJMenuBar(getMyMenuBar(false));
		//}
		configFrame.setResizable(false);
		configFrame.setBackground(Color.WHITE);
		configFrame.pack();
		configFrame.setLocationRelativeTo(null);
		configFrame.setVisible(true);
	}
	
	/**
	 * Main method
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		
		// Use mac menu bar
		System.setProperty("apple.laf.useScreenMenuBar","true");
		
		// Command-line arguments
		String cmdAddress = "";
		boolean cmdDebug = false;
		String cmdName = "";
		boolean cmdSound = true;
		boolean cmdStart = true;
		int cmdTheme = 0;
		
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].toLowerCase().equals("-a") || args[i].toLowerCase().equals("--address")) {
				try {
					cmdAddress = args[i+1];
				}
				catch (Exception e) {}
			}
			else if (args[i].toLowerCase().equals("-d") || args[i].toLowerCase().equals("--debug")) {
				cmdDebug = true;
			}
			else if (args[i].toLowerCase().equals("-h") || args[i].toLowerCase().equals("--help")) {
				cmdStart = false;
			}
			else if (args[i].toLowerCase().equals("-n") || args[i].toLowerCase().equals("--name")) {
				try {
					cmdName = args[i+1];
				}
				catch (Exception e) {}
 			}
			else if (args[i].toLowerCase().equals("-p") || args[i].toLowerCase().equals("--port")) {
				try {
					cmdAddress = cmdAddress+":"+args[i+1];
				}
				catch (Exception e) {}
			}
			else if (args[i].toLowerCase().equals("-t") || args[i].toLowerCase().equals("--theme")) {
				try {
					cmdTheme = Integer.parseInt(args[i+1]);
				}
				catch (Exception e) {
					for (int t = 0; t < themes.length; t++) {
						if (themes[t].toLowerCase().equals(args[i+1].toLowerCase())) {
							cmdTheme = t;
						}
					}
				}
			}
			
			else if (args[i].toLowerCase().equals("-s") || args[i].toLowerCase().equals("--sound-off")) {
				cmdSound = false;
			}
			
		}
		
		if (cmdStart) {
			breakthrough = new Breakthrough(cmdAddress, cmdName, cmdTheme, cmdSound, cmdDebug);
		}
		else {
			System.out.printf("\nBreakthrough Version "+BREAKTHROUGH_VERSION+" ("+BREAKTHROUGH_DATE+")\n\n");
			
			System.out.printf("Usage: java Breakthrough [OPTIONS]\n");
			
			System.out.printf("Options:\n");
			System.out.printf("-a, --address <address>  The address to connect to\n");
			System.out.printf("-d, --debug              Enable debug mode\n");
			System.out.printf("-h, --help               Display this help message\n");
			System.out.printf("-n, --name <name>        Your name\n");
			System.out.printf("-p, --port <number>      The port number to use (defaults to "+BREAKTHROUGH_PORT+")\n");
			System.out.printf("-t, --theme <name>       The name of the theme to use\n");
			System.out.printf("-s, --sound-off          Disables sounds\n\n");
		}
		
	}
	
	/**
	 * Piece moved method - updates GUI when a piece has been moved
	 * @param e The PieceMovedEvent
	 */
	public void pieceMoved(final PieceMovedEvent e) {
		String info = e.getActionCommand();
		int team = e.getTeam();
		int j = e.getX();
		int i = e.getY();
		
		button[i][j].setActionCommand(info);
		if (team == 0) {
			button[i][j].setIcon(new ImageIcon());
		}
		else if (team == 1) {
			button[i][j].setIcon(team1Ico);
		}
		else if (team == 2) {
			button[i][j].setIcon(team2Ico);
		}
	}
	
	/**
	 * Play a sound
	 * @param file The name of the file
	 */
	private void playSound(String file) {
		if (soundCK.isSelected()) {
			try {
				java.applet.AudioClip clip = java.applet.Applet.newAudioClip(this.getClass().getResource("sounds/"+file));
				clip.play();
			}
			catch (Exception e) {
				// Doesn't matter if the sound doesn't play
			}
		}
	}
	
	/**
	 * Prompt for disconnect
	 */
	private void promptForDisconnect() {
		if (inReplay) {
			reset();
		}
		else {
			int disconnect = JOptionPane.showConfirmDialog(breakthrough, rb.getString("disconnectConfirmation"), rb.getString("disconnect"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (disconnect == JOptionPane.YES_OPTION) {
				reset();
			}
		}
	}
	
	/**
	 * Prompt for exit
	 */
	private void promptForExit() {
		if (inReplay) {
			gameManager.closeSocket();
			System.exit(0);
		}
		else {
			int exit = JOptionPane.showConfirmDialog(breakthrough, rb.getString("exitConfirmation"), rb.getString("exit"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (exit == JOptionPane.YES_OPTION) {
				gameManager.closeSocket();
				System.exit(0);
			}
		}
	}
	
	/**
	 * Prompt to play again
	 */
	private void promptToPlayAgain(String title, String message, int messageType, boolean replayAvailable) {
		if (replayAvailable) {
			
			int reset = JOptionPane.showOptionDialog(breakthrough, message+"\n"+rb.getString("playAgain"), title, JOptionPane.YES_NO_OPTION, messageType, null, new String[] {rb.getString("playAgainButton"),rb.getString("viewReplayButton"),rb.getString("exitButton")}, rb.getString("playAgainButton"));
			
			if (reset == 0) {
				reset();
			}
			else if (reset == 1) {
				inReplay = true;
				gameManager.closeSocket();
				
				// Start the replay in a separate thread
				new Thread() {
					public void run() {
						replay.init();
					}
				}.start();
			}
			else {
				gameManager.closeSocket();
				System.exit(0);
			}
			
		}
		else {
			
			int reset = JOptionPane.showConfirmDialog(breakthrough, message+"\n"+rb.getString("playAgain"), title, JOptionPane.YES_NO_OPTION, messageType);
			
			if (reset == JOptionPane.YES_OPTION) {
				reset();
			}
			else {
				gameManager.closeSocket();
				System.exit(0);
			}
			
		}
		
	}
	
	/**
	 * When the replay is over - either exit or reset
	 * @param action The action to take
	 */
	public void replayOver(String action) {
		if (action.equals("exit")) {
			System.exit(0);
		}
		else if (action.equals("reset")) {
			reset();
		}
	}
	
	/**
	 * Reset method - resets the game back to the config screen
	 */
	private void reset() {
		this.setVisible(false);
		configFrame.setVisible(false);
		gameManager.closeSocket();
		breakthrough = null;
		breakthrough = new Breakthrough(addressTF.getText(), myName, themesCB.getSelectedIndex(), soundCK.isSelected(), debugMode);
	}
	
	/**
	 * Set config components enabled - enables/disables the user inputs on the config dialog
	 * @param enabled Whether to set the components enabled or disabled
	 */
	private void setConfigComponentsEnabled(boolean enabled) {
		addressTF.setEnabled(enabled);
		nameTF.setEnabled(enabled);
		connectButton.setEnabled(enabled);
		themesCB.setEnabled(enabled);
		soundCK.setEnabled(enabled);
	}
	
	/**
	 * Set Icon Theme - sets the icons on the buttons to the specified theme
	 * @param theme The name of the theme
	 */
	private void setIconTheme(String theme) {
		
		String iconFile1 = "team1.png";
		String iconFile2 = "team2.png";
		
		if (theme.equals("Default")) {
			iconFile1 = "team1.png";
			iconFile2 = "team2.png";
		}
		else if (theme.equals("OS")) {
			
			String os = System.getProperty("os.name").toLowerCase();
			
			if (os.equals("mac os x")) {
				
				if (team == 1) {
					iconFile1 = "OS_mac.png";
					iconFile2 = "OS_vista.png";
				}
				else if (team == 2) {
					iconFile1 = "OS_vista.png";
					iconFile2 = "OS_mac.png";
				}
				
			}
			else if (os.startsWith("windows")) {
				
				if (team == 1) {
					iconFile1 = "OS_vista.png";
					iconFile2 = "OS_linux.png";
				}
				else if (team == 2) {
					iconFile1 = "OS_linux.png";
					iconFile2 = "OS_vista.png";
				}
				
			}
			else {
				
				if (team == 1) {
					iconFile1 = "OS_linux.png";
					iconFile2 = "OS_vista.png";
				}
				else if (team == 2) {
					iconFile1 = "OS_vista.png";
					iconFile2 = "OS_linux.png";
				}
				
			}
			
		}
		else {
			/* PNG NAMING CONVENTIONS
			 * 
			 * theme_team[1,2]_(1,2).png
			 * 
			 * team[1,2] indicates which team the icon is for
			 * (1,2) is for the team that the player is on
			 * 
			 */
			iconFile1 = theme+"_team1_"+team+".png";
			iconFile2 = theme+"_team2_"+team+".png";
		}
		
		team1Ico = new ImageIcon(this.getClass().getResource("images/"+iconFile1));
		team2Ico = new ImageIcon(this.getClass().getResource("images/"+iconFile2));
		
		// Loop through buttons and add/change icons
		for (int i = 0; i < button.length; i++) {
			for (int j = 0; j < button[i].length; j++) {
				
				int iconTeam = Integer.parseInt(button[i][j].getActionCommand().substring(0,1));
				button[i][j].setPreferredSize(new Dimension(team1Ico.getIconWidth()+34,team1Ico.getIconHeight()+10));
				// button[i][j].setPreferredSize(new Dimension(109, 85)); // The size of the biggest icons
				
				if (iconTeam == 1) {
					button[i][j].setIcon(team1Ico);
				}
				else if (iconTeam == 2) {
					button[i][j].setIcon(team2Ico);
				}
				
			}
		}
		
		// Resize the window to account for changes in icons
		this.pack();
		
	}
	
	/**
	 * Status changed method - updates the status on the config dialog
	 * @param e The StatusChangeEvent
	 */
	public void statusChanged(final StatusChangeEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					// Change the message and color
					try {
						statusLBL.setText(rb.getString("statusPrefix")+": "+rb.getString(e.getStatus()));
					}
					catch (java.util.MissingResourceException mre) {
						statusLBL.setText(rb.getString("statusPrefix")+": "+e.getStatus());
					}
					
					if (configFrame.isVisible()) {
						statusLBL.getParent().setBackground(e.getColor());
						statusLBL.getParent().getParent().setBackground(e.getColor());
					}
					else {
						statusLBL.getParent().setBackground(e.getColor());
					}

					// Change the color back after 2 seconds
					SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								try {
									Thread.sleep(1000);
								}
								catch (InterruptedException ie) {}
								
								// Set back to correct color
								if (configFrame.isVisible()) {
									statusLBL.getParent().setBackground(StatusChangeEvent.CONFIG_COLOR);
									statusLBL.getParent().getParent().setBackground(StatusChangeEvent.CONFIG_COLOR);
								}
								else {
									statusLBL.getParent().setBackground(null);
								}
							}
						}
					);
				}
			}
		);
	}
	
}