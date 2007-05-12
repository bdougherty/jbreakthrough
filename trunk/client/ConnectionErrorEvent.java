/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 10, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Client Connection Error Event
 */

public class ConnectionErrorEvent {
	private Exception e;
	private boolean reset;
	
	/**
	 * Constructor
	 * @param e The exception
	 */
	public ConnectionErrorEvent(Exception e, boolean reset) {
		this.e = e;
		this.reset = reset;
	}
	
	/**
	 * Get exception - causes the exception to be thrown
	 * @throws Exception
	 */
	public void getException() throws Exception {
		throw e;
	}
	
	/**
	 * Get message
	 * @return the message from the exception
	 */
	public String getMessage() {
		return e.getMessage();
	}
	
	/**
	 * Should reset
	 * @return whether or not the client should reset
	 */
	public boolean shouldReset() {
		return reset;
	}
	
}