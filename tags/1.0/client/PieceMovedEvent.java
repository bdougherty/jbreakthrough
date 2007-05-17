/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 14, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Client Piece Moved Event
 */

public class PieceMovedEvent {
	private int team;
	private int x;
	private int y;
	
	/**
	 * Constructor
	 * @param team The team to whom the piece belongs
	 * @param x The x-coordinate of the piece
	 * @param y The y-coordinate of the piece
	 */
	public PieceMovedEvent(int team, int x, int y) {
		this.team = team;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get Action Command
	 * @return actionCommand The formatted string describing the piece
	 */
	public String getActionCommand() {
		return team+""+x+""+y;
	}
	
	/**
	 * Get Team
	 * @return team The team number associated with the piece
	 */
	public int getTeam() {
		return team;
	}
	
	/**
	 * Get X
	 * @return x The x-coordinate of the piece
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get Y
	 * @return y The y-coordinate of the piece
	 */
	public int getY() {
		return y;
	}
	
}