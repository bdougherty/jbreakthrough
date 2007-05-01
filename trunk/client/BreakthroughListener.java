/**
 * Breakthrough Game
 * Date: Apr 29, 2007
 * @author Brad Dougherty
 * Breakthrough Listener Abstract Class
 */

public interface BreakthroughListener {
	
	public void statusChanged(StatusChangeEvent e);
	public void beginningConnection(BeginningConnectionEvent e);
	public void connected(ConnectionEvent e);
	public void pieceMoved(PieceMovedEvent e);
	
}