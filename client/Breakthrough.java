/**
 * Breakthrough Game
 * Date: May 7, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Application
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.*;
import javax.swing.*;

public class Breakthrough extends JFrame implements BreakthroughListener {
	private static Breakthrough breakthrough;
	private GameManager gameManager;
	private ImageIcon team1ICO;
	private ImageIcon team2ICO;
	private ImageIcon titleICO;
	private int team;
	private JButton [][] button;
	private JButton connectButton;
	private JCheckBox soundCK;
	private JComboBox piecesCB;
	private JFrame configFrame;
	private JLabel infoLBL;
	private JLabel statusLBL;
	private JLabel welcomeLBL;
	private JTextField addressTF;
	private JTextField nameTF;
	private String[] pieces = {"Default", "Halo", "Mario"};
	private String myName;
	private String opponentName;
	private String response;
	
	// Internationalization
	Locale l = Locale.getDefault();
	ResourceBundle rb = ResourceBundle.getBundle("Breakthrough",l);
	
	/**
	 * Default constructor - creates the connection and game managers, initializes components, shows config dialog
	 */
	public Breakthrough() {
		gameManager = new GameManager();
		gameManager.addListener(this);
		initComponents();
		layoutConfig();
	}
	
	/**
	 * Constructor - creates the connection and game managers, initializes components, shows config dialog, fills in information
	 */
	public Breakthrough(String address, String name, int theme) {
		gameManager = new GameManager();
		gameManager.addListener(this);
		initComponents();
		layoutConfig();
		addressTF.setText(address);
		nameTF.setText(name);
		piecesCB.setSelectedIndex(theme);
	}
	
	/**
	 * Initializes common components
	 */
	private void initComponents() {
		button = new JButton[8][8];
		addressTF = new JTextField(10);
		nameTF = new JTextField(10);
		piecesCB = new JComboBox(pieces);
		soundCK = new JCheckBox("Enable",true);
		statusLBL = new JLabel(rb.getString("status")+": "+rb.getString("statusWaitingForInformation"));
		welcomeLBL = new JLabel();
		infoLBL = new JLabel("Welcome to Breakthrough!");
		titleICO = new ImageIcon(this.getClass().getResource("images/title.png"));
		connectButton = new JButton(rb.getString("connectButton"));
	}
	
	/**
	 * Creates the configuration dialog
	 */
	private void layoutConfig() {
		configFrame = new JFrame();
		JLabel addressLabel = new JLabel(rb.getString("serverAddress"));
		JLabel nameLabel = new JLabel(rb.getString("yourName"));
		JLabel piecesLabel = new JLabel(rb.getString("typeOfPieces"));
		JLabel soundLabel = new JLabel(rb.getString("sounds"));
		JLabel titleLabel = new JLabel(titleICO);
		JPanel imagePanel = new JPanel();
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		JPanel fieldsPanel = new JPanel();
		JPanel connectPanel = new JPanel();
		JPanel center = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel();
		
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
		optionsPanel.add(piecesCB);
		optionsPanel.add(soundLabel);
		optionsPanel.add(soundCK);
		fieldsPanel.add(optionsPanel);
		connectPanel.add(connectButton);
		center.add(fieldsPanel, BorderLayout.NORTH);
		center.add(connectPanel, BorderLayout.CENTER);
		center.add(new JPanel(), BorderLayout.SOUTH);
		statusPanel.setBackground(Color.LIGHT_GRAY);
		statusPanel.add(statusLBL);
		
		configFrame.add(imagePanel, BorderLayout.NORTH);
		configFrame.add(center);
		configFrame.add(statusPanel, BorderLayout.SOUTH);
		
		configFrame.setTitle("Breakthrough");
		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		configFrame.setResizable(false);
		configFrame.setBackground(Color.WHITE);
		configFrame.pack();
		configFrame.setLocationRelativeTo(null);
		configFrame.setVisible(true);
	}
	
	/**
	 * Creates the main window
	 */
	private void layoutComponents() {
		JPanel infoPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new GridLayout(8,0));
		
		// Player will always be on the left
		if (team == 2) {
			buttonPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		
		// Set up icons
		String kind = pieces[piecesCB.getSelectedIndex()];
		if (kind.equals("Default")) {
			team1ICO = new ImageIcon(this.getClass().getResource("images/team1.png"));
			team2ICO = new ImageIcon(this.getClass().getResource("images/team2.png"));
		}
		else {
			team1ICO = new ImageIcon(this.getClass().getResource("images/"+kind+"_team1_"+team+".png"));
			team2ICO = new ImageIcon(this.getClass().getResource("images/"+kind+"_team2_"+team+".png"));
		}
		
		// Set up buttons
		for (int i = 0; i < button.length; i++) {
			for (int j = 0; j < button[i].length; j++) {
				
				button[i][j] = new JButton();
				button[i][j].putClientProperty("JButton.buttonType","toolbar"); // Square buttons on Mac
				buttonPanel.add(button[i][j]);
				
				if (j == 0 || j == 1) {
					button[i][j].setText("");
					button[i][j].setIcon(team1ICO);
					button[i][j].setActionCommand("1"+j+i+"");
				}
				else if (j == 6 || j == 7) {
					button[i][j].setText("");
					button[i][j].setIcon(team2ICO);
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
		
		infoPanel.add(welcomeLBL, BorderLayout.NORTH);
		infoPanel.add(statusLBL, BorderLayout.SOUTH);
		add(infoPanel, BorderLayout.NORTH);
		add(buttonPanel);
		
		setTitle("Breakthrough");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null); // Center on screen
		setVisible(true);
	}
	
	/**
	 * Listener method for connect button - tells the game manager to connect
	 */
	private void connectButton_actionPerformed() {
		new Thread() {
			public void run() {
				statusLBL.getParent().setBackground(Color.LIGHT_GRAY);
				
				// Check to see that the user entered a name
				if (nameTF.getText().equals("")) {
					statusChanged(new StatusChangeEvent("errorMustEnterName", Color.RED));
					nameTF.requestFocus();
				}
				else {
					gameManager.connect(addressTF.getText(), nameTF.getText());
				}
				
			}
		}.start();
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
	 * Status changed method - updates the status on the config dialog
	 * @param e The StatusChangeEvent
	 */
	public void statusChanged(final StatusChangeEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					final Color prevColor = statusLBL.getParent().getBackground();

					// Change the message and color
					statusLBL.setText(rb.getString("status")+": "+rb.getString(e.getStatus()));
					statusLBL.getParent().setBackground(e.getColor());

					// Change the color back after 2 seconds
					SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								try {
									Thread.sleep(1000);
								}
								catch (InterruptedException ie) {}
								statusLBL.getParent().setBackground(prevColor);
							}
						}
					);
				}
			}
		);
	}
	
	/**
	 * Begining connection method - updates GUI when beginning the connection
	 * @param e The ConnectionEvent
	 */
	public void connectionBeginning() {
		// Disable the components
		setConfigComponentsEnabled(false);
		statusChanged(new StatusChangeEvent("statusConnecting"));
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
		welcomeLBL.setText(rb.getString("welcomeMessage")+", "+myName+".\n "+rb.getString("playing")+" "+opponentName);
		
		// Layout the game board and hide the config
		layoutComponents();
		configFrame.setVisible(false);
		
		// Reset the enabled status of components
		setConfigComponentsEnabled(true);
	}
	
	/**
	 * Connection error method - when there is an error connecting
	 * @param e The ConnectionErrorEvent
	 */
	public void connectionError(final ConnectionErrorEvent e) {
		// Reset the enabled status of components
		addressTF.requestFocus();
		setConfigComponentsEnabled(true);
		
		// If the connection was reset
		if (e.getMessage().equals("Connection reset")) {
			reset();
			statusChanged(new StatusChangeEvent("errorConnectionLost",Color.RED));
		}
	}
	
	private void setConfigComponentsEnabled(boolean enabled) {
		addressTF.setEnabled(enabled);
		nameTF.setEnabled(enabled);
		connectButton.setEnabled(enabled);
		piecesCB.setEnabled(enabled);
		soundCK.setEnabled(enabled);
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
			button[y][x].setIcon(team1ICO);
		}
		else if (team == 2) {
			button[y][x].setIcon(team2ICO);
		}
	}
	
	public void gameOver(final GameOverEvent e) {
		String message;
		String title;
		if (e.isError()) {
			System.out.println("Error");
			message = opponentName+" "+rb.getString("errorOpponentDisconnected");
			title = "Opponent Disconnected";
			
			// If sound is enabled
			if (soundCK.isEnabled()) {
				//playSound("opponentdisconnect.wav");
			}
		}
		else if (e.isWinner()) {
			System.out.println("You win");
			message = "You beat "+opponentName+"!";
			title = "Winner!";
			
			// If sound is enabled
			if (soundCK.isEnabled()) {
				//playSound("success.wav");
			}
		}
		else {
			System.out.println("You lose");
			message = "Sorry, "+opponentName+" kicked your ass!";
			title = "Loser";
			
			// If sound is enabled
			if (soundCK.isEnabled()) {
				playSound("error.wav");
			}
		}
		
		int reset = JOptionPane.showConfirmDialog(null, message+"\n"+rb.getString("playAgain"), title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (reset == 0) {
			reset();
		}
		else {
			System.exit(0);
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
	 * Reset method - resets the game back to the config screen
	 */
	public void reset() {
		this.setVisible(false);
		breakthrough = new Breakthrough(addressTF.getText(), myName, piecesCB.getSelectedIndex());
	}
	
	/**
	 * Main method
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		breakthrough = new Breakthrough();
	}
	
}