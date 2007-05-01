/**
 * Breakthrough Game
 * Date: May 1, 2007
 * @author Brad Dougherty
 * Breakthrough Client Connection Event
 */

import java.io.*;
import java.net.*;

public class ConnectionEvent {
	String opponentName, myName;
	int team;
	boolean connected = true;
	
	public ConnectionEvent(boolean connected) {
		this.connected = connected;
	}
	
	public ConnectionEvent(int team, String myName, String opponentName) {
		this.team = team;
		this.myName = myName;
		this.opponentName = opponentName;
	}
	
	public boolean connected() {
		return connected;
	}
	
	public int getTeam() {
		return team;
	}
	
	public String getMyName() {
		return myName;
	}
	
	public String getOpponentName() {
		return opponentName;
	}
	
}