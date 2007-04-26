/**
 * BREAKTHROUGH game client
 * 
 * @version .1
 * 
 * @author Brad Dougherty
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Breakthrough {
	
	// Attributes
	private JTextField address, name;
	private JButton start;
	private JLabel infoLabel, status;
	private JButton [][] button = new JButton[8][8];
	private String myName, myOpponent;
	private int team;
	private JFrame configFrame, gameFrame;
	
	/**
	 * Set the game options and show the main GUI
	 * WILL ADD SOCKET IN THE FUTURE
	 */
	private void initializeGame(String myName, String myOpponent, int team) {
		this.myName = myName;
		this.myOpponent = myOpponent;
		this.team = team;
		createAndShowGUI();
	}
	
	/**
	 * Default constructor
	 * Creates the GUI
	 */
	public void createAndShowConfig() {
		
		configFrame = new JFrame();
		
		// Set frame options
		configFrame.setTitle("Breakthrough");
		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		configFrame.setResizable(false);
		configFrame.setBackground(Color.WHITE);
		
		// Image panel
		JPanel imagePanel = new JPanel();
		imagePanel.add(new JLabel(new ImageIcon("title.jpg")));
		
		// Fields and connect button
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		optionsPanel.add(new JLabel("Server address:"));
		optionsPanel.add(address = new JTextField(10));
		optionsPanel.add(new JLabel("Your name:"));
		optionsPanel.add(name = new JTextField(10));
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.add(optionsPanel);
		JPanel connectPanel = new JPanel();
		connectPanel.add(start = new JButton("Connect!"));
		JPanel center = new JPanel(new GridLayout(0,1));
		center.add(fieldsPanel);
		center.add(connectPanel);
		
		// Status panel
		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(Color.LIGHT_GRAY);
		statusPanel.add(status = new JLabel("Status: Waiting for information"));
		
		// Add the panels to the frame
		configFrame.add(imagePanel, BorderLayout.NORTH);
		configFrame.add(center);
		configFrame.add(statusPanel, BorderLayout.SOUTH);
		
		// Add listeners
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				
				// Change GUI
				address.setEnabled(false);
				name.setEnabled(false);
				start.setEnabled(false);
				status.setText("Status: Connecting to server");
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
				
				// Connect to the server
				// Socket sock = new Socket(address.getText(), 4567);

				// Sends name to server
				// out.write(name.getText());
				// out.newLine();
				// out.flush();
				status.setText("Status: Waiting for opponent");
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
				
				// Recieve back opponents information
				// String response = in.readLine();
				int team = 1; // 1 for now... = Integer.parseInt(response.substring(0,1));
				String opponentName = "Travis"; // = response.substring(2,response.length);
				status.setText("Status: Setting up game");
				
				initializeGame(name.getText(), opponentName, team);
				configFrame.setVisible(false);
				
			}
		});
		
		// Pack and show
		configFrame.pack();
		configFrame.setLocationRelativeTo(null); // Center on screen
		configFrame.setVisible(true);
		
	}
	
	/**
	 * Creates the GUI
	 */
	public void createAndShowGUI() {
		
		gameFrame = new JFrame();
		
		// Set frame options
		gameFrame.setTitle("Breakthrough");
		gameFrame.setResizable(false);
		
		// Top information panel
		JPanel infoPanel = new JPanel();
		infoPanel.add(infoLabel = new JLabel("Woohoo! Welcome to Breakthrough, "+myName+".\nYou are playing against "+myOpponent));
		
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
					button[i][j].setIcon(new ImageIcon("team1.jpg"));
					button[i][j].setActionCommand(""+j+i+"1");
				}
				else if (j == 6 || j == 7) {
					button[i][j].setText("");
					button[i][j].setIcon(new ImageIcon("team2.jpg"));
					button[i][j].setActionCommand(""+j+i+"2");
				}
				else {
					button[i][j].setActionCommand(""+j+i+"0");
				}
				
				// Add button to panel
				buttonPanel.add(button[i][j]);
				
				// For debugging
				button[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						String info = ae.getActionCommand();
						System.out.println("Coordinates: " + info.substring(0,1) + "," + info.substring(1,2) + " Team: "+ info.substring(2,3));
					}
				});
			}
		}
		
		// Add the panels to the frame
		gameFrame.add(infoPanel, BorderLayout.NORTH);
		gameFrame.add(buttonPanel);
		
		// Pack and show
		gameFrame.pack();
		gameFrame.setLocationRelativeTo(null); // Center on screen
		gameFrame.setVisible(true);
		
	}
	
	/**
	 * Main method - Starts the Configuration GUI
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		new Breakthrough();
	}
	
	/**
	 * Default Constructor
	 * Shows the Configuration frame
	 */
	public Breakthrough() {
		createAndShowConfig();
	}
	
}