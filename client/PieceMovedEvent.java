/**
 * Breakthrough Game
 * Date: Apr 29, 2007
 * @author Brad Dougherty
 * Breakthrough Client Piece Moved Event
 */

public class PieceMovedEvent {
	String movedFrom, movedTo;
	
	public PieceMovedEvent(String from, String to) {
		this.movedFrom = from;
		this.movedTo = to;
	}
	
	public String getMovedFrom() {
		return movedFrom;
	}
	
	public String getMovedTo() {
		return movedTo;
	}
	
	public int getFromTeam() {
		return Integer.parseInt(movedFrom.substring(0,1));
	}
	
	public int getFromX() {
		return Integer.parseInt(movedFrom.substring(1,2));
	}
	
	public int getFromY() {
		return Integer.parseInt(movedFrom.substring(2,3));
	}
	
	public int getToTeam() {
		return Integer.parseInt(movedTo.substring(0,1));
	}
	
	public int getToX() {
		return Integer.parseInt(movedTo.substring(1,2));
	}
	
	public int getToY() {
		return Integer.parseInt(movedTo.substring(2,3));
	}
	
}