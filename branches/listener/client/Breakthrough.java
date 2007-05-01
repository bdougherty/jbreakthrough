/**
 * Breakthrough Game
 * Date: May 1, 2007
 * @author Brad Dougherty
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
	JLabel statusLBL, infoLBL;
	JButton [][] button = new JButton[8][8];
	JButton connectButton;
	int team;
	String myName, opponentName, response;
	ImageIcon titleICO, team1ICO, team2ICO;
	GameManager gameManager;
	
	// Localization
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
		JPanel infoPanel = new JPanel();
		JPanel buttonPanel = new JPanel(new GridLayout(8,0));
		if (team == 2) {
			buttonPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		infoLBL = new JLabel("Woohoo! Welcome to Breakthrough, "+myName+".\nYou are playing against "+opponentName);
		
		for (int i = 0; i < button.length; i++) {
			for (int j = 0; j < button[i].length; j++) {
				
				button[i][j] = new JButton();
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
				
				// FOR DEBUGGING - WILL BE REMOVED FOR FINAL!
				button[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						String info = ae.getActionCommand();
						System.out.println("Team: " + info.substring(0,1) + " Coordinates: " + info.substring(1,2) + ","+ info.substring(2,3));
					}
				});
			}
		}
		
		infoPanel.add(infoLBL);
		add(infoPanel, BorderLayout.NORTH);
		add(buttonPanel);
		
		setTitle("Breakthrough");
		setResizable(false);
		pack();
		setLocationRelativeTo(null); // Center on screen
		setVisible(true);
	}
	
	/**
	 * Listener method for connect button - tells the connection manager to connect
	 */
	private void connectButton_actionPerformed() {
		new Thread() {
			public void run() {
				gameManager.connect(addressTF.getText(), nameTF.getText());
			}
		}.start();
	}
	
	/**
	 * Status changed method - updates the status on the config dialog
	 * @param e the StatusChangeEvent
	 */
	public void statusChanged(final StatusChangeEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					try {
						statusLBL.setText(rb.getString("status")+rb.getString(e.getStatus()));
					}
					catch (Exception ev) {
						statusLBL.setText(rb.getString("status")+e.getStatus());
					}
				}
			}
		);
	}
	
	/**
	 * Begining connection method - updates GUI when beginning the connection
	 * @param e the ConnectionEvent
	 */
	public void beginningConnection(final BeginningConnectionEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					addressTF.setEnabled(false);
					nameTF.setEnabled(false);
					connectButton.setEnabled(false);
				}
			}
		);
	}
	
	/**
	 * Connected method - when the game is fully initialized
	 * @param e the ConnectionEvent
	 */
	public void connected(final ConnectionEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					if (e.connected()) {
						myName = e.getMyName();
						opponentName = e.getOpponentName();
						team = e.getTeam();
						layoutComponents();
						configFrame.setVisible(false);
					}
					addressTF.requestFocus();
					addressTF.setEnabled(true);
					nameTF.setEnabled(true);
					connectButton.setEnabled(true);
				}
			}
		);
	}
	
	/**
	 * Piece moved method - updates GUI when a piece has been moved
	 * @param e the PieceMovedEvent
	 */
	public void pieceMoved(final PieceMovedEvent e) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					// CODE FOR MOVING A PIECE
				}
			}
		);
	}
	
	/**
	 * Main method
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new Breakthrough();
	}
	
}