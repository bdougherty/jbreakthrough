/**
 * Breakthrough Game
 * Date: May 8, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0 beta
 * Breakthrough Client Connection Error Event
 */

public class ConnectionErrorEvent {
	Exception e;
	
	public ConnectionErrorEvent(Exception e) {
		this.e = e;
	}
	
	public String getMessage() {
		return e.getMessage();
	}
	
	public void getException() throws Exception {
		throw e;
	}
	
}