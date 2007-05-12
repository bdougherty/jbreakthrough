/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 10, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Listener Abstract Class
 */

public interface BreakthroughListener {
	
	public void connected(ConnectionEvent e);
	public void connectionBeginning();
	public void connectionError(ConnectionErrorEvent e);
	public void gameOver(GameOverEvent e);
	public void pieceMoved(PieceMovedEvent e);
	public void statusChanged(StatusChangeEvent e);
	
}