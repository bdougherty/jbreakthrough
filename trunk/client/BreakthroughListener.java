/**
 * Breakthrough Game
 * Date: May 8, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0 beta
 * Breakthrough Listener Abstract Class
 */

public interface BreakthroughListener {
	
	public void statusChanged(StatusChangeEvent e);
	public void connectionBeginning();
	public void connectionError(ConnectionErrorEvent e);
	public void connected(ConnectionEvent e);
	public void pieceMoved(PieceMovedEvent e);
	public void gameOver(GameOverEvent e);
	
}