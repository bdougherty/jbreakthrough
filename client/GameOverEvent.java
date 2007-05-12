/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 10, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Client Game Over Event
 */

public class GameOverEvent {
	private String error;
	private boolean isError;
	private boolean winner;
	
	/**
	 * Constructor
	 * @param winner Whether the player is the winner or not
	 */
	public GameOverEvent(boolean winner) {
		this.winner = winner;
		this.isError = false;
		this.error = "";
	}
	
	/**
	 * Constructor
	 * @param error The error
	 */
	public GameOverEvent(String error) {
		this.winner = false;
		this.isError = true;
		this.error = error;
	}
	
	/**
	 * Get error
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	
	/**
	 * Is error
	 * @param isError Whether or not an error occurred
	 */
	public boolean isError() {
		return isError;
	}
	
	/**
	 * Is winner
	 * @param winner Whether the player is the winner or not
	 */
	public boolean isWinner() {
		return winner;
	}
	
}