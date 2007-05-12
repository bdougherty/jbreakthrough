/**
 * Breakthrough Game<br />
 * RIT 4002-219 Final Project<br />
 * Date: May 10, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0
 * Breakthrough Client Connection Event
 */

import java.io.*;
import java.net.*;

public class ConnectionEvent {
	private String myName;
	private String opponentName;
	private int team;
	
	/**
	 * Constructor
	 * @param team The team number assigned
	 * @param myName The client's name
	 * @param opponentName The name of the opponent
	 */
	public ConnectionEvent(int team, String myName, String opponentName) {
		this.team = team;
		this.myName = myName;
		this.opponentName = opponentName;
	}
	
	/**
	 * Get opponent name
	 * @return the opponent's name
	 */
	public String getOpponentName() {
		return opponentName;
	}
	
	/**
	 * Get my name
	 * @return my name
	 */
	public String getMyName() {
		return myName;
	}
	
	/**
	 * Get team number
	 * @return the team number assigned
	 */
	public int getTeam() {
		return team;
	}
	
}