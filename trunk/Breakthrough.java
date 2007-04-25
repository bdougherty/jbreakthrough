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
		JPanel buttonPanel = new JPanel(new GridLayout(8,8));
		for (int i = 0; i < button.length; i++) {
			for (int j = 0; j < button[i].length; j++) {
				button[i][j] = new JButton();
				if (j == 0 || j == 1) {
					button[i][j].setText("");
					button[i][j].setIcon(new ImageIcon("team1.jpg"));
				}
				else if (j == 6 || j == 7) {
					button[i][j].setText("");
					button[i][j].setIcon(new ImageIcon("team2.jpg"));
				}
				
				buttonPanel.add(button[i][j]);
				button[i][j].addActionListener(new ButtonListener(i, j));
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
	
	/**
	 * DEBUG BUTTON LISTENER
	 */
	class ButtonListener implements ActionListener {
		int x, y;
		public ButtonListener(int i, int j) {
			this.y = i;
			this.x = j;
		}
		public void actionPerformed(ActionEvent ae) {
			System.out.println("Coordinates: " + x + "," + y);
		}
	}
	
}