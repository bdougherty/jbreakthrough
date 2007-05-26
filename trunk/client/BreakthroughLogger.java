/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 26, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0.1
 * Breakthrough Client Logging
 */

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class BreakthroughLogger implements BreakthroughListener {
	private int currentMove = 0;
	private boolean firstPiece = false;
	private boolean gameOver = false;
	private String log = "";
	private ArrayList<String> moves;
	private String name;
	private String opponent;
	private ArrayList<String> status;
	private int team;
	private int totalMoves;
	
	// Internationalization
	private Locale l = Locale.getDefault();
	private ResourceBundle rb = ResourceBundle.getBundle("Breakthrough",l);
	
	/**
	 * Constructor
	 * @param breakthrough The breakthrough class
	 */
	public BreakthroughLogger() {
		moves = new ArrayList<String>();
		status = new ArrayList<String>();
	}
	
	/**
	 * Connected method - when the game is fully initialized
	 * @param e The ConnectionEvent
	 */
	public void connected(ConnectionEvent e) {
		team = e.getTeam();
		name = e.getMyName();
		opponent = e.getOpponentName();
	}
	
	/**
	 * Game over - when the game is over, records total number of moves for the game
	 * @param e The GameOverEvent
	 */
	public void gameOver(GameOverEvent e) {
		totalMoves = currentMove;
		gameOver = true;
	}
	
	/**
	 * Get move - returns the specified move from the ArrayList
	 * @param i The move to request
	 * @return move The string representing the move
	 */
	public String getMove(int i) {
		return moves.get(i);
	}
	
	/**
	 * Get total moves - returns the total number of moves if the game has completed
	 * @return totalMoves The total number of moves
	 */
	public int getTotalMoves() {
		if (gameOver) {
			return totalMoves;
		}
		else {
			return 0;
		}
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
	 * Status changed method - records each status change
	 * @param e The StatusChangeEvent
	 */
	public void statusChanged(StatusChangeEvent e) {
		try {
			status.add(rb.getString(e.getStatus()));
		}
		catch (java.util.MissingResourceException mre) {
			status.add(e.getStatus());
		}
	}
	
	// Nothing to log when these methods are called
	public void connectionBeginning() {}
	public void connectionError(ConnectionErrorEvent e) {}
	
}