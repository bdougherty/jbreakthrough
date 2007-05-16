/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 16, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Client Replay
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

public class BreakthroughReplay implements BreakthroughListener, ActionListener {
	private Breakthrough breakthrough;
	private int currentMove = 0;
	private boolean firstPiece = false;
	private JButton forward;
	private String log = "";
	private ArrayList<String> moves;
	private JButton pause;
	private JButton play;
	private JButton rewind;
	private int team;
	private Timer timer;
	private int totalMoves;
	
	// Internationalization
	private Locale l = Locale.getDefault();
	private ResourceBundle rb = ResourceBundle.getBundle("Breakthrough",l);
	
	/**
	 * Constructor
	 * @param breakthrough The breakthrough class
	 */
	public BreakthroughReplay(Breakthrough breakthrough) {
		this.breakthrough = breakthrough;
		moves = new ArrayList<String>();
	}
	
	/**
	 * This gets triggered by the timer - updates display with the next move
	 * @param ae The action event
	 */
	public void actionPerformed(ActionEvent ae) {
		if (currentMove >= totalMoves) {
			timer.stop();
			play.setEnabled(true);
			pause.setEnabled(false);
			enableButtons();
		}
		else {
			String thisMove = moves.get(currentMove);

			// Parse info for first piece
			int team1 = Integer.parseInt(thisMove.substring(0,1));
			int x1 = Integer.parseInt(thisMove.substring(1,2));
			int y1 = Integer.parseInt(thisMove.substring(2,3));

			// Parse info for second piece
			int team2 = Integer.parseInt(thisMove.substring(4,5));
			int x2 = Integer.parseInt(thisMove.substring(5,6));
			int y2 = Integer.parseInt(thisMove.substring(6,7));

			breakthrough.pieceMoved(new PieceMovedEvent(team1, x1, y1));
			breakthrough.pieceMoved(new PieceMovedEvent(team2, x2, y2));

			currentMove++;
		}
	}
	
	/**
	 * Connected method - when the game is fully initialized
	 * @param e The ConnectionEvent
	 */
	public void connected(ConnectionEvent e) {
		team = e.getTeam();
	}
	
	/**
	 * Determines whether the rewind and forward buttons should be enabled
	 */
	private void enableButtons() {
		if (currentMove <= 0) {
			rewind.setEnabled(false);
			forward.setEnabled(true);
		}
		else if (currentMove >= totalMoves) {
			rewind.setEnabled(true);
			forward.setEnabled(false);
		}
		else {
			rewind.setEnabled(true);
			forward.setEnabled(true);
		}
	}
	
	/**
	 * Game over - when the game is over, records total number of moves for the game
	 * @param e The GameOverEvent
	 */
	public void gameOver(GameOverEvent e) {
		totalMoves = currentMove;
	}
	
	/**
	 * Updates the board to the specified move
	 * @param m The move to go to
	 * @param source The source object to give to the ActionEvent
	 */
	private void goToMove(int m, Object source) {
		resetBoard();
		for (int i = 0; i <= m; i++) {
			actionPerformed(new ActionEvent(source, 1, "Moving to "+m));
		}
		enableButtons();
	}
	
	/**
	 * Initializes the replay control GUI
	 */
	public void init() {
		
		final JFrame replayController = new JFrame("Replay");
		
		JPanel controls = new JPanel();
		rewind = new JButton(new ImageIcon(this.getClass().getResource("images/control_rewind.png")));
		rewind.setEnabled(false);
		play = new JButton(new ImageIcon(this.getClass().getResource("images/control_play.png")));
		pause = new JButton(new ImageIcon(this.getClass().getResource("images/control_pause.png")));
		pause.setEnabled(false);
		forward = new JButton(new ImageIcon(this.getClass().getResource("images/control_fastforward.png")));
		controls.add(rewind);
		controls.add(play);
		controls.add(pause);
		controls.add(forward);
		
		JPanel gameControls = new JPanel(new BorderLayout());
		JButton newGame = new JButton(rb.getString("playAgainButton"));
		JButton exit = new JButton(rb.getString("exit"));
		gameControls.add(new JPanel(new FlowLayout()).add(newGame), BorderLayout.NORTH);
		gameControls.add(new JPanel(new FlowLayout()).add(exit), BorderLayout.SOUTH);
		
		replayController.add(controls, BorderLayout.NORTH);
		replayController.add(new JSeparator(), BorderLayout.CENTER);
		replayController.add(gameControls, BorderLayout.SOUTH);
		
		rewind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				goToMove(currentMove-2, rewind);
			}
		});
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				playReplay();
			}
		});
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pauseReplay();
			}
		});
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				goToMove(currentMove, forward);
			}
		});
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replayController.setVisible(false);
				breakthrough.replayOver("reset");
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				breakthrough.replayOver("exit");
			}
		});
		
		int locX = (int)breakthrough.getLocation().getX()+(int)breakthrough.getSize().getWidth();
		int locY = (int)breakthrough.getLocation().getY();
		replayController.setLocation(locX, locY);
		replayController.setResizable(false);
		replayController.pack();
		replayController.setVisible(true);
		
		timer = new Timer(1000, this);
		
		resetBoard();
		
	}
	
	/**
	 * Pauses the replay
	 */
	private void pauseReplay() {
		timer.stop();
		play.setEnabled(true);
		pause.setEnabled(false);
		enableButtons();
	}
	
	/**
	 * Piece moved method - records each piece movement
	 * @param e The PieceMovedEvent
	 */
	public void pieceMoved(PieceMovedEvent e) {
		
		if (!firstPiece) {
			log = e.getActionCommand()+",";
			firstPiece = true;
		}
		else if (firstPiece) {
			log = log + e.getActionCommand();
			firstPiece = false;
			moves.add(log);
			log = "";
			currentMove++;
		}
		
	}
	
	/**
	 * Plays the replay - restarts if at the end
	 */
	private void playReplay() {
		if (currentMove >= totalMoves) {
			resetBoard();
		}
		timer.start();
		rewind.setEnabled(false);
		play.setEnabled(false);
		pause.setEnabled(true);
		forward.setEnabled(false);
	}
	
	/**
	 * Resets the game board
	 */
	private void resetBoard() {
		
		currentMove = 0;
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				if (j == 0 || j == 1) {
					breakthrough.pieceMoved(new PieceMovedEvent(1, j, i));
				}
				else if (j == 6 || j == 7) {
					breakthrough.pieceMoved(new PieceMovedEvent(2, j, i));
				}
				else {
					breakthrough.pieceMoved(new PieceMovedEvent(0, j, i));
				}
				
			}
		}
		
	}
	
	// Don't need to do anything when these methods are called
	public void connectionBeginning() {}
	public void connectionError(ConnectionErrorEvent e) {}
	public void statusChanged(StatusChangeEvent e) {}
	
}