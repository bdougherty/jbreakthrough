/**
 * Breakthrough Game
 * Date: May 6, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Application
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public class Breakthrough extends JFrame implements BreakthroughListener {
	JFrame configFrame;
	JTextField addressTF, nameTF;
	JComboBox piecesCB;
	String[] pieces = {"Default", "Halo", "Custom"};
	JLabel statusLBL, infoLBL, welcomeLBL;
	JButton [][] button = new JButton[8][8];
	JButton connectButton;
	int team;
	String myName, opponentName, response;
	ImageIcon titleICO, team1ICO, team2ICO;
	GameManager gameManager;
	static Breakthrough breakthrough;
	
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
	 * Initializes common components
	 */
	private void initComponents() {
		addressTF = new JTextField(10);
		nameTF = new JTextField(10);
		piecesCB = new JComboBox(pieces);
		statusLBL = new JLabel(rb.getString("status")+rb.getString("statusWaitingForInformation"));
		welcomeLBL = new JLabel();
		infoLBL = new JLabel("Welcome to Breakthrough!");
		titleICO = new ImageIcon("title.png");
		team1ICO = new ImageIcon("team1.jpg");
		team2ICO = new ImageIcon("team2.jpg");
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
		JLabel titleLabel = new JLabel(titleICO);
		JPanel imagePanel = new JPanel();
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		JPanel fieldsPanel = new JPanel();
		JPanel connectPanel = new JPanel();
		JPanel center = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel();
		
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
		//optionsPanel.add(piecesLabel); UNTIL FUNCTIONALITY
		//optionsPanel.add(piecesCB);	IS ADDED
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
		if (team == 2) {
			buttonPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		
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
					statusLBL.setText(rb.getString("status")+rb.getString(e.getStatus()));
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
	public void connectionBeginning(final ConnectionBeginningEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					// Disable the components
					addressTF.setEnabled(false);
					nameTF.setEnabled(false);
					connectButton.setEnabled(false);
				}
			}
		);
	}
	
	/**
	 * Connected method - when the game is fully initialized
	 * @param e The ConnectionEvent
	 */
	public void connected(final ConnectionEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					
					// Get the game information from the event
					myName = e.getMyName();
					opponentName = e.getOpponentName();
					team = e.getTeam();
					welcomeLBL.setText(rb.getString("welcomeMessage")+", "+myName+".\n"+rb.getString("playing")+" "+opponentName);
					
					// Layout the game board and hide the config
					layoutComponents();
					configFrame.setVisible(false);
					
					// Reset the enabled status of components
					addressTF.setEnabled(true);
					nameTF.setEnabled(true);
					connectButton.setEnabled(true);
					
				}
			}
		);
	}
	
	/**
	 * Connection error method - when there is an error connecting
	 * @param e The ConnectionErrorEvent
	 */
	public void connectionError(final ConnectionErrorEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					// Reset the enabled status of components
					addressTF.requestFocus();
					addressTF.setEnabled(true);
					nameTF.setEnabled(true);
					connectButton.setEnabled(true);
					
					// If the connection was reset
					if (e.getMessage().equals("Connection reset")) {
						reset();
						statusChanged(new StatusChangeEvent("errorConnectionLost",Color.RED));
					}
				}
			}
		);
	}
	
	/**
	 * Piece moved method - updates GUI when a piece has been moved
	 * @param e The PieceMovedEvent
	 */
	public void pieceMoved(final PieceMovedEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					
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
			}
		);
	}
	
	public void gameOver(final GameOverEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					
					String message;
					String title;
					if (e.isWinner()) {
						message = "You beat "+opponentName+"!";
						title = "Winner!";
					}
					else {
						message = "Sorry, "+opponentName+" beat the crap out of you!";
						title = "Loser";
					}
					
					int reset = JOptionPane.showConfirmDialog(null, message+"\n\nWould you like to play Breakthrough again?", title, JOptionPane.YES_NO_OPTION, JOptionPane.ÅINFORMATION_MESSAGE);
					if (reset == 0) {
						reset();
					}
					
				}
			}
		);
	}
	
	/**
	 * Reset method - resets the game back to the config screen
	 */
	public void reset() {
		this.setVisible(false);
		breakthrough = new Breakthrough();
	}
	
	/**
	 * Main method
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		breakthrough = new Breakthrough();
	}
	
}