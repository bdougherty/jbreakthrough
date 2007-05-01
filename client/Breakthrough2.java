/**
 * BREAKTHROUGH game client
 * 
 * @version 1.0
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
	
	// Configuration GUI Attributes
	private JFrame configFrame;
	private JTextField address, name;
	private JButton start;
	private JLabel status;
	
	// Game Board GUI Attributes
	private JFrame gameFrame;
	private JLabel infoLabel;
	private JButton [][] button = new JButton[8][8];
	
	// Game Configuration Attributes
	private String myName, myOpponent;
	private int team;
	
	// Connection Attributes
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private Socket sock = null;
	private String response;
	
	/**
	 * Creates the config GUI
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
		imagePanel.add(new JLabel(new ImageIcon("title.png")));
		
		// Fields and connect button
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		optionsPanel.add(new JLabel("Server address:"));
		optionsPanel.add(address = new JTextField("129.21.97.74",10));
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
		
		// Add listener for the start button
		start.addActionListener(new ActionListener(){
			/**
			 * actionPerformed()
			 * @param ae the ActionEvent
			 */
			public void actionPerformed(ActionEvent ae) {
				connectToServer();
			}
		});
		
		// Pack and show
		configFrame.pack();
		configFrame.setLocationRelativeTo(null); // Center on screen
		configFrame.setVisible(true);
		
	}
	
	/**
	 * Creates the game board GUI
	 */
	public void createAndShowGameBoard() {
		
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
					button[i][j].setActionCommand("1"+j+i+"");
				}
				else if (j == 6 || j == 7) {
					button[i][j].setText("");
					button[i][j].setIcon(new ImageIcon("team2.jpg"));
					button[i][j].setActionCommand("2"+j+i+"");
				}
				else {
					button[i][j].setActionCommand("0"+j+i+"");
				}
				
				// Add button to panel
				buttonPanel.add(button[i][j]);
				
				// For debugging
				button[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						String info = ae.getActionCommand();
						System.out.println("Team: " + info.substring(0,1) + " Coordinates: " + info.substring(1,2) + ","+ info.substring(2,3));
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
		
		// Start the game
		// startGame();
		
	}
	
	/**
	 * Starts the game thread
	 */
	private void startGame() {
		// GameThread thread = new GameThread();
		// thread.start();
	}
	
	/**
	 * Starts the connection thread
	 */
	private void connectToServer() {
		ConnectionThread thread = new ConnectionThread();
		thread.start();
	}
	
	/**
	 * Main method - Starts the Configuration GUI
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		new Breakthrough();
	}
	
	/**
	 * Default Constructor - Shows the Configuration frame
	 */
	public Breakthrough() {
		createAndShowConfig();
	}
	
	/**
	 * Connection Thread - Connects to the server, calls the Game Board
	 */
	class ConnectionThread extends Thread {
		/**
		 * Run - Connects to the server
		 */
		public void run() {
			try {
				
				// Change GUI
				address.setEnabled(false);
				name.setEnabled(false);
				start.setEnabled(false);
				status.setText("Status: Connecting to server");

				// Connect to the server
				sock = new Socket(address.getText(), 4567);

				// Set up the reader and writer
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				
				// Sends name to server
				status.setText("Status: Sending name to server");
				out.write(name.getText());
				out.newLine();
				out.flush();
				status.setText("Status: Waiting for opponent");

				// Recieve back opponents information
				response = in.readLine();
				status.setText("Status: Recieved opponent name");
				
				// Parse information
				team = Integer.parseInt(response.substring(0,1));
				myOpponent = response.substring(2,response.length());
				myName = name.getText();
				status.setText("Status: Setting up game");
				
				// Show the game board
				createAndShowGameBoard();
				configFrame.setVisible(false);
				
			}
			catch (NumberFormatException nfe) {
				status.setText("Status: The number returned from the server was not valid");
			}
			catch (IOException ioe) {
				status.setText("Status: An IO error occurred");
			}
			catch (Exception e) {
				status.setText("Status: "+e.getMessage());
			}
			finally {
				
				// Reset the config GUI
				address.setEnabled(true);
				name.setEnabled(true);
				start.setEnabled(true);
				
			}
		}
	}
	
	/**
	 * Game Thread - Handles the running of the game
	 */
	class GameThread extends Thread {
		/**
		 * Run - Connects to the server
		 */
		public void run() {
			
			// CODE HERE
			
		}
	}
	
}