/**
 * Breakthrough Game
 * Date: Apr 29, 2007
 * @author Brad Dougherty
 * Breakthrough Listener Abstract Class
 */

public interface BreakthroughListener {
	
	public void statusChanged(StatusChangeEvent e);
	public void beginningConnection(ConnectionEvent e);
	public void connected(ConnectionEvent e);
	
}