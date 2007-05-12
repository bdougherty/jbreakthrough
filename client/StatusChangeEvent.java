/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 10, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Client Connection Event
 */

import java.awt.Color;

public class StatusChangeEvent {
	private Color color;
	private String status;
	
	static final Color ERROR_COLOR = new Color(190,2,2);
	static final Color ALERT_COLOR = new Color(0,161,81);
	static final Color CONFIG_COLOR = Color.LIGHT_GRAY;
	
	/**
	 * Constructor
	 * @param status The status text
	 */
	public StatusChangeEvent(String status) {
		this.status = status;
		this.color = null;
	}
	
	/**
	 * Constructor
	 * @param status The status text
	 * @param color The color
	 */
	public StatusChangeEvent(String status, Color color) {
		this.status = status;
		this.color = color;
	}
	
	/**
	 * Get Color
	 * @return color The color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Get Status
	 * @return status The status
	 */
	public String getStatus() {
		return status;
	}
	
}