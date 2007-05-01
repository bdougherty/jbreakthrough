/**
 * Breakthrough Game
 * Date: May 1, 2007
 * @author Brad Dougherty
 * Breakthrough Client Game Manager
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class GameManager {
	private ArrayList<BreakthroughListener> listeners = new ArrayList<BreakthroughListener>();
	
	public void connect(String address, String name) {
		
		try {
			// Connect to the server
			fireBeginningConnection();
			Socket sock = new Socket(address, 4567);

			// Set up the reader and writer
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			// Send name to server
			fireStatusChange("statusSendingNameToServer");
			out.write(name);
			out.newLine();
			out.flush();
			fireStatusChange("statusWaitingForOpponent");

			// Recieve back opponents information
			String response = in.readLine();
			fireStatusChange("statusRecievedOpponentName");

			// Parse information
			int team = Integer.parseInt(response.substring(0,1));
			String opponentName = response.substring(2,response.length());
			String myName = name;
			fireStatusChange("statusSettingUpGame");
			fireConnected(team, myName, opponentName);
		}
		catch (IOException e) {
			// SHOULD ADD A CONNECTION EXCEPTION INSTEAD OF BOOLEAN
			fireStatusChange(e.getMessage());
			fireConnected(false);
		}
		
	}
	
	public void addListener(BreakthroughListener listener){
        listeners.add(listener);
    }

    public void removeListener(BreakthroughListener listener){
        listeners.remove(listener);
    }
	
	public void fireStatusChange(String status) {
		StatusChangeEvent event = new StatusChangeEvent(status);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.statusChanged(event);
        }
	}
	
	public void fireBeginningConnection() {
		BeginningConnectionEvent event = new BeginningConnectionEvent();
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.beginningConnection(event);
        }
	}
	
	public void fireConnected(int team, String myName, String opponentName) {
		ConnectionEvent event = new ConnectionEvent(team, myName, opponentName);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connected(event);
        }
	}
	
	public void fireConnected(boolean connected) {
		ConnectionEvent event = new ConnectionEvent(connected);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connected(event);
        }
	}
	
}