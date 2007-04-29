/**
 * Breakthrough Game
 * Date: Apr 29, 2007
 * @author Brad Dougherty
 * Breakthrough Client Application
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Breakthrough extends JFrame implements BreakthroughListener {
	JFrame configFrame;
	JTextField addressTF, nameTF;
	JLabel statusLBL, infoLBL;
	JButton [][] button = new JButton[8][8];
	JButton connectButton;
	int team;
	String myName, opponentName, response;
	ImageIcon titleICO, team1ICO, team2ICO;
	ConnectionManager connectionManager;
	GameManager gameManager;
	
	/**
	 * Default constructor - creates the connection and game managers, initializes components, shows config dialog
	 */
	public Breakthrough() {
		connectionManager = new ConnectionManager();
		connectionManager.addListener(this);
		gameManager = new GameManager();
		initComponents();
		layoutConfig();
	}
	
	/**
	 * Initializes common components
	 */
	private void initComponents() {
		addressTF = new JTextField(10);
		nameTF = new JTextField(10);
		statusLBL = new JLabel("Status: Waiting for information");
		infoLBL = new JLabel("Welcome to Breakthrough!");
		titleICO = new ImageIcon("title.png");
		team1ICO = new ImageIcon("team1.jpg");
		team2ICO = new ImageIcon("team2.jpg");
		connectButton = new JButton("Connect!");
	}
	
	/**
	 * Creates the configuration dialog
	 */
	private void layoutConfig() {
		configFrame = new JFrame();
		JLabel addressLabel = new JLabel("Server address:");
		JLabel nameLabel = new JLabel("Your name:");
		JLabel titleLabel = new JLabel(titleICO);
		JPanel imagePanel = new JPanel();
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		JPanel fieldsPanel = new JPanel();
		JPanel connectPanel = new JPanel();
		JPanel center = new JPanel(new GridLayout(0,1));
		JPanel statusPanel = new JPanel();
		
		connectButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					connectButton_actionPerformed();
				}
			}
		);
		
		configFrame.setTitle("Breakthrough");
		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		configFrame.setResizable(false);
		configFrame.setBackground(Color.WHITE);
		
		imagePanel.add(titleLabel);
		optionsPanel.add(addressLabel);
		optionsPanel.add(addressTF);
		optionsPanel.add(nameLabel);
		optionsPanel.add(nameTF);
		fieldsPanel.add(optionsPanel);
		connectPanel.add(connectButton);
		center.add(fieldsPanel);
		center.add(connectPanel);
		statusPanel.setBackground(Color.LIGHT_GRAY);
		statusPanel.add(statusLBL);
		
		configFrame.add(imagePanel, BorderLayout.NORTH);
		configFrame.add(center);
		configFrame.add(statusPanel, BorderLayout.SOUTH);
		
		configFrame.pack();
		configFrame.setLocationRelativeTo(null);
		configFrame.setVisible(true);
	}
	
	/**
	 * Creates the main window
	 */
	private void layoutComponents() {
		setTitle("Breakthrough");
		setResizable(false);
		
		// Top information panel
		JPanel infoPanel = new JPanel();
		infoPanel.add(infoLBL = new JLabel("Woohoo! Welcome to Breakthrough, "+myName+".\nYou are playing against "+opponentName));
		
		// Reverse the board if you are team 2
		JPanel buttonPanel = new JPanel(new GridLayout(8,0));
		if (team == 2) {
			buttonPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		
		// Create and add the buttons to the array and panel
		for (int i = 0; i < button.length; i++) {
			for (int j = 0; j < button[i].length; j++) {
				
				// Create the button
				button[i][j] = new JButton();
				
				// Initial startup positions
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
				
				// Add button to panel
				buttonPanel.add(button[i][j]);
				
				// FOR DEBUGGING - WILL BE REMOVED FOR FINAL!
				button[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						String info = ae.getActionCommand();
						System.out.println("Team: " + info.substring(0,1) + " Coordinates: " + info.substring(1,2) + ","+ info.substring(2,3));
					}
				});
			}
		}
		
		// Add the panels to the frame
		add(infoPanel, BorderLayout.NORTH);
		add(buttonPanel);
		
		// Pack and show
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
				connectionManager.connect(addressTF.getText(), nameTF.getText());
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
					statusLBL.setText("Status: "+e.getStatus());
				}
			}
		);
	}
	
	/**
	 * Begining connection method - updates GUI when beginning the connection
	 * @param e the ConnectionEvent
	 */
	public void beginningConnection(final ConnectionEvent e) {
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
		myName = e.getMyName();
		opponentName = e.getOpponentName();
		initComponents();
		configFrame.setVisible(false);
	}
	
	/**
	 * Main method
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new Breakthrough();
	}
	
}