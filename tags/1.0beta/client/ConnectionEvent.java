/**
 * Breakthrough Game
 * Date: May 6, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Connection Event
 */

import java.io.*;
import java.net.*;

public class ConnectionEvent {
	String opponentName, myName;
	int team;
	
	public ConnectionEvent(int team, String myName, String opponentName) {
		this.team = team;
		this.myName = myName;
		this.opponentName = opponentName;
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