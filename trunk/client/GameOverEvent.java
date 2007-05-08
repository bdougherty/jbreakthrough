/**
 * Breakthrough Game
 * Date: May 6, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Game Over Event
 */

public class GameOverEvent {
	boolean isError;
	boolean winner;
	String error;
	
	public GameOverEvent(boolean winner) {
		this.winner = winner;
		this.isError = false;
		this.error = "";
	}
	
	public GameOverEvent(String error) {
		this.winner = false;
		this.isError = true;
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public boolean isWinner() {
		return winner;
	}
	
}