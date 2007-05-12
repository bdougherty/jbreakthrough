/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 11, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
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
import java.io.*;
import javax.swing.*;
import java.net.*;

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
	private String myName;
	private JTextField nameTF;
	private String opponentName;
	private JCheckBox soundCK;
	private JLabel statusLBL;
	private int team;
	private ImageIcon team1Ico;
	private ImageIcon team2Ico;
	private String[] themes = {"Default", "Halo", "Mario", "OS"};
	private JComboBox themesCB;
	private ImageIcon titleIco;
	private JLabel welcomeLBL;
	
	private final int BREAKTHROUGH_PORT = 16789;
	
	// Internationalization
	private Locale l = Locale.getDefault();
	private ResourceBundle rb = ResourceBundle.getBundle("Breakthrough",l);
	
	/**
	 * Default constructor - creates the connection and game managers, initializes components, shows config dialog
	 */
	public Breakthrough() {
		gameManager = new GameManager(false);
		gameManager.addListener(this);
		init();
		layoutConfig();
	}
	
	/**
	 * Constructor - creates the connection and game managers, initializes components, shows config dialog, fills in information
	 */
	public Breakthrough(String address, String name, int theme, boolean enableSound, boolean debugMode) {
		gameManager = new GameManager(debugMode);
		gameManager.addListener(this);
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
				else {
					gameManager.connect(addressTF.getText(), BREAKTHROUGH_PORT, nameTF.getText());
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
			
			int reset = JOptionPane.showConfirmDialog(this, rb.getString("errorConnectionLost")+"\n"+rb.getString("playAgain"), rb.getString("error"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			
			if (reset == 0) {
				reset();
			}
			else {
				System.exit(0);
			}
			
		}
		
		/*try {
			
			// If the connection was reset
			if (e.getMessage().equals("Connection reset")) {
				reset();
				statusChanged(new StatusChangeEvent("errorConnectionLost", StatusChangeEvent.ERROR_COLOR));
			}
			else if (e.getMessage.equals("Connection error")) {
				reset();
			}
			
		}
		catch (NullPointerException np) {
			statusChanged(new StatusChangeEvent("errorInvalidResponse", StatusChangeEvent.ERROR_COLOR));
		}*/
	}
	
	/**
	 * Game over - when a winner has been declared
	 * @param e The GameOverEvent
	 */
	public void gameOver(final GameOverEvent e) {
		String message;
		String title;
		if (e.isError()) {
			System.out.println("Error");
			message = opponentName+" "+rb.getString("errorOpponentDisconnected");
			title = "Opponent Disconnected";
			
			// If sound is enabled
			if (soundCK.isSelected()) {
				playSound("error.wav");
			}
		}
		else if (e.isWinner()) {
			message = rb.getString("beat")+" "+opponentName+"!";
			title = rb.getString("winner")+"!";
			
			// If sound is enabled
			if (soundCK.isSelected()) {
				playSound("success.wav");
			}
		}
		else {
			message = rb.getString("lostPart1")+" "+opponentName+" "+rb.getString("lostPart2");
			title = rb.getString("loser");
			
			// If sound is enabled
			if (soundCK.isSelected()) {
				playSound("error.wav");
			}
		}
		
		int reset = JOptionPane.showConfirmDialog(breakthrough, message+"\n"+rb.getString("playAgain"), title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (reset == 0) {
			reset();
		}
		else {
			System.exit(0);
		}
	}
	
	/**
	 * Get My Menu Bar - generates menu bar
	 * @param full Whether or not to generate full menu
	 * @return menuBar The JMenuBar
	 */
	private JMenuBar getMyMenuBar(boolean full) {
		JMenuBar menuBar = new JMenuBar();
		
		if (debugMode) {
			JMenu debugMenu = new JMenu("DEBUG");
			JMenuItem changeTeam = new JMenuItem("Change Team");
			JMenuItem simulateLoss = new JMenuItem("Simulate Loss");
			JMenuItem simulateWin = new JMenuItem("Simulate Win");
			
			debugMenu.add(changeTeam);
			debugMenu.add(new JSeparator());
			debugMenu.add(simulateLoss);
			debugMenu.add(simulateWin);
			menuBar.add(debugMenu);
			
			changeTeam.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						reset();
					}
				}
			);
			simulateLoss.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						gameOver(new GameOverEvent(false));
					}
				}
			);
			simulateWin.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						gameOver(new GameOverEvent(true));
					}
				}
			);
		}
		
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
				exit.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							promptForExit();
						}
					}
				);
			}
			menuBar.add(gameMenu);
			
			changeTheme.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						JOptionPane.showMessageDialog(breakthrough, themesCB, "Select theme", JOptionPane.INFORMATION_MESSAGE);
						setIconTheme(themes[themesCB.getSelectedIndex()]);
					}
				}
			);
			disconnect.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						
						int disconnect = JOptionPane.showConfirmDialog(breakthrough, rb.getString("disconnectConfirmation"), rb.getString("disconnect"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
						if (disconnect == 0) {
							reset();
						}
						
					}
				}
			);
			
		}
		
		JMenu helpMenu = new JMenu(rb.getString("helpMenu"));
		JMenuItem rules = new JMenuItem(rb.getString("helpMenuRules")+"...");
		helpMenu.add(rules);
		menuBar.add(helpMenu);
		
		rules.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					
					JTextArea text = new JTextArea(10,40);
					JScrollPane scroll = new JScrollPane(text);
					text.append(rb.getString("rules"));
					text.setLineWrap(true);
					text.setWrapStyleWord(true);
					text.setCaretPosition(0);
					text.setEditable(false);
					
					JOptionPane.showMessageDialog(breakthrough, scroll,rb.getString("rulesDialogTitle"),JOptionPane.INFORMATION_MESSAGE);
					
				}
			}
		);
		
		return menuBar;
	}
	
	/**
	 * Initializes common components
	 */
	private void init() {
		button = new JButton[8][8];
		addressTF = new JTextField(10);
		nameTF = new JTextField(10);
		themesCB = new JComboBox(themes);
		soundCK = new JCheckBox(rb.getString("configEnableSoundsBox"),true);
		statusLBL = new JLabel(rb.getString("statusPrefix")+": "+rb.getString("statusWaitingForInformation"));
		welcomeLBL = new JLabel();
		infoLBL = new JLabel(rb.getString("welcomeMessage"));
		titleIco = new ImageIcon(this.getClass().getResource("images/title.png"));
		connectButton = new JButton(rb.getString("configConnectButtonLabel"));
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
		add(welcomePanel, BorderLayout.NORTH);
		add(buttonPanel);
		add(infoPanel, BorderLayout.SOUTH);
		setJMenuBar(getMyMenuBar(true));
		setTitle("Breakthrough");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					promptForExit();
				}
			}
		);
		setResizable(false);
		pack();
		setLocationRelativeTo(null); // Center on screen
		setVisible(true);
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
		
		addressTF.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					connectButton_actionPerformed();
				}
			}
		);
		nameTF.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					connectButton_actionPerformed();
				}
			}
		);
		connectButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					connectButton_actionPerformed();
				}
			}
		);
		
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
			debugButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						int teamSelected = Integer.parseInt(JOptionPane.showInputDialog(breakthrough,"What team?"));
						team = teamSelected;
						connected(new ConnectionEvent(team, "Debug Mode", "no one"));
					}
				}
			);
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
		if (System.getProperty("mrj.version") != null) {
			configFrame.setJMenuBar(getMyMenuBar(false));
		}
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
		
		// Get command line arguments
		if (args.length == 0) {
			breakthrough = new Breakthrough();
		}
		else if (args.length == 1) {
			breakthrough = new Breakthrough(args[0], "", 0, true, false);
		}
		else if (args.length == 2) {
			breakthrough = new Breakthrough(args[0], args[1], 0, true, false);
		}
		else if (args.length == 3) {
			breakthrough = new Breakthrough(args[0], args[1], Integer.parseInt(args[2]), true, false);
		}
		else if (args.length == 4) {
			breakthrough = new Breakthrough(args[0], args[1], Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), false);
		}
		else if (args.length >= 5) {
			breakthrough = new Breakthrough(args[0], args[1], Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]));
		}
		
	}
	
	/**
	 * Piece moved method - updates GUI when a piece has been moved
	 * @param e The PieceMovedEvent
	 */
	public void pieceMoved(final PieceMovedEvent e) {
		String info = e.getActionCommand();
		int team = e.getTeam();
		int x = e.getX();
		int y = e.getY();
		
		button[y][x].setActionCommand(info);
		if (team == 0) {
			button[y][x].setIcon(new ImageIcon());
		}
		else if (team == 1) {
			button[y][x].setIcon(team1Ico);
		}
		else if (team == 2) {
			button[y][x].setIcon(team2Ico);
		}
	}
	
	/**
	 * Play a sound
	 * @param file The name of the file
	 */
	private void playSound(String file) {
		try {
			java.applet.AudioClip clip = java.applet.Applet.newAudioClip(this.getClass().getResource("sounds/"+file));
			clip.play();
		}
		catch (Exception e) {}
	}
	
	/**
	 * Prompt for exit
	 */
	private void promptForExit() {
		int exit = JOptionPane.showConfirmDialog(breakthrough, rb.getString("exitConfirmation"), rb.getString("exit"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (exit == 0) {
			gameManager.closeSocket();
			System.exit(0);
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