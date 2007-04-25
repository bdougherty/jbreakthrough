/**
 * BREAKTHROUGH game
 * 
 * @version .1
 * 
 * @author Brad Dougherty
 * @author Kevin Harris
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Breakthrough extends JFrame {
	
	// Attributes
	JLabel infoLabel;
	JButton [][] button = new JButton[8][8];
	int [][] pieces = new int[8][8];
	int team = 1; // TEMP FOR DEBUGGING
	
	/**
	 * Default constructor
	 * Creates the GUI
	 */
	public Breakthrough() {
		
		// Set frame options
		setTitle("Breakthrough");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		// Top information panel
		JPanel infoPanel = new JPanel();
		infoPanel.add(infoLabel = new JLabel("Woohoo!"));
		
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
		add(infoPanel, BorderLayout.NORTH);
		add(buttonPanel);
		
		// Pack and show
		pack();
		setLocationRelativeTo(null); // Center on screen
		setVisible(true);
		
	}
	
	/**
	 * Main method
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		new Breakthrough();
	}
	
}