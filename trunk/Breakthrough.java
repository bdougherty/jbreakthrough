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
	JButton [][] buttons = { { new JButton("0,0"), new JButton("1,0"), new JButton("2,0"), new JButton("3,0"), new JButton("4,0"), new JButton("5,0"), new JButton("6,0"), new JButton("7,0") },
							 { new JButton("0,1"), new JButton("1,1"), new JButton("2,1"), new JButton("3,1"), new JButton("4,1"), new JButton("5,1"), new JButton("6,1"), new JButton("7,1") },
							 { new JButton("0,2"), new JButton("1,2"), new JButton("2,2"), new JButton("3,2"), new JButton("4,2"), new JButton("5,2"), new JButton("6,2"), new JButton("7,2") },
							 { new JButton("0,3"), new JButton("1,3"), new JButton("2,3"), new JButton("3,3"), new JButton("4,3"), new JButton("5,3"), new JButton("6,3"), new JButton("7,3") },
							 { new JButton("0,4"), new JButton("1,4"), new JButton("2,4"), new JButton("3,4"), new JButton("4,4"), new JButton("5,4"), new JButton("6,4"), new JButton("7,4") },
							 { new JButton("0,5"), new JButton("1,5"), new JButton("2,5"), new JButton("3,5"), new JButton("4,5"), new JButton("5,5"), new JButton("6,5"), new JButton("7,5") },
							 { new JButton("0,6"), new JButton("1,6"), new JButton("2,6"), new JButton("3,6"), new JButton("4,6"), new JButton("5,6"), new JButton("6,6"), new JButton("7,6") },
							 { new JButton("0,7"), new JButton("1,7"), new JButton("2,7"), new JButton("3,7"), new JButton("4,7"), new JButton("5,7"), new JButton("6,7"), new JButton("7,7") } };
	int [][] pieces = new int[8][8];
	
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
		
		// Buttons
		JPanel buttonPanel = new JPanel(new GridLayout(0,8));
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {
				// buttons[i][j].setSize(25,25);
				if (j == 0 || j == 1) {
					buttons[i][j].setText("");
					buttons[i][j].setIcon(new ImageIcon("team1.jpg"));
				}
				else if (j == 6 || j == 7) {
					buttons[i][j].setText("");
					buttons[i][j].setIcon(new ImageIcon("team2.jpg"));
				}
				
				buttonPanel.add(buttons[i][j]);
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