/**
 * Breakthrough Game
 * Date: Apr 29, 2007
 * @author Brad Dougherty
 * Breakthrough Client Connection Event
 */

import java.io.*;
import java.net.*;

public class ConnectionEvent {
	BufferedReader in;
	BufferedWriter out;
	Socket sock;
	String opponentName, myName;
	int team;
	
	public ConnectionEvent(BufferedReader in, BufferedWriter out, Socket sock, int team, String myName, String opponentName) {
		this.in = in;
		this.out = out;
		this.sock = sock;
		this.team = team;
		this.myName = myName;
		this.opponentName = opponentName;
	}
	
	public BufferedReader getReader() {
		return in;
	}
	
	public BufferedWriter getWriter() {
		return out;
	}
	
	public Socket getSocket() {
		return sock;
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