/**
 * Breakthrough Game
 * Date: May 6, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Connection Event
 */

import java.awt.Color;

public class StatusChangeEvent {
	String status;
	Color color;
	
	public StatusChangeEvent(String status) {
		this.status = status;
		this.color = Color.LIGHT_GRAY;
	}
	
	public StatusChangeEvent(String status, Color color) {
		this.status = status;
		this.color = color;
	}
	
	public String getStatus() {
		return status;
	}
	
	public Color getColor() {
		return color;
	}
	
}