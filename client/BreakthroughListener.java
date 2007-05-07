/**
 * Breakthrough Game
 * Date: May 6, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Listener Abstract Class
 */

public interface BreakthroughListener {
	
	public void statusChanged(StatusChangeEvent e);
	public void connectionBeginning(ConnectionBeginningEvent e);
	public void connectionError(ConnectionErrorEvent e);
	public void connected(ConnectionEvent e);
	public void pieceMoved(PieceMovedEvent e);
	public void gameOver(GameOverEvent e);
	
}