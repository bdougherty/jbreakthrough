/**
 * Breakthrough Game
 * Date: May 6, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Piece Moved Event
 */

public class PieceMovedEvent {
	int team, x, y;
	
	public PieceMovedEvent(int team, int x, int y) {
		this.team = team;
		this.x = x;
		this.y = y;
	}
	
	public int getTeam() {
		return team;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getActionCommand() {
		return team+""+x+""+y;
	}
	
}