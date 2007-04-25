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

public class ConfigGUI extends JFrame {
	
	// Attributes
	private JTextField address, name;
	private JButton start;
	private JLabel status;
	
	/**
	 * Default constructor
	 * Creates the GUI
	 */
	public ConfigGUI() {
		
		// Set frame options
		setTitle("Breakthrough");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBackground(Color.WHITE);
		
		// Image panel
		JPanel imagePanel = new JPanel();
		imagePanel.add(new JLabel(new ImageIcon("title.jpg")));
		
		// Server Address
		JPanel serverAddressPanel = new JPanel();
		serverAddressPanel.add(new JLabel("Server address:"));
		serverAddressPanel.add(address = new JTextField(8));
		
		// Name
		JPanel namePanel = new JPanel();
		namePanel.add(new JLabel("Your name:"));
		namePanel.add(name = new JTextField(8));
		
		// Options panel
		JPanel optionsPanel = new JPanel(new GridLayout(0,1));
		optionsPanel.add(serverAddressPanel);
		optionsPanel.add(namePanel);
		optionsPanel.add(start = new JButton("Connect!"));
		
		// Status panel
		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(Color.LIGHT_GRAY);
		statusPanel.add(status = new JLabel("Status: Waiting for information"));
		
		// Add the panels to the frame
		add(imagePanel, BorderLayout.NORTH);
		add(optionsPanel);
		add(statusPanel, BorderLayout.SOUTH);
		
		// Pack and show
		pack();
		setLocationRelativeTo(null); // Center on screen
		setVisible(true);
		
	}
	
	/**
	 * Main method FOR DEBUGGING
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		new ConfigGUI();
	}
	
}