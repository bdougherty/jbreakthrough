/**
 * Breakthrough Game
 * Date: Apr 29, 2007
 * @author Brad Dougherty
 * Breakthrough Client Connection Manager
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectionManager {
	private ArrayList<BreakthroughListener> listeners = new ArrayList<BreakthroughListener>();
	
	public void connect(String address, String name) {
		
		try {
			// Connect to the server
			Socket sock = new Socket(address, 4567);

			// Set up the reader and writer
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			// Send name to server
			fireStatusChange("Sending name to server");
			out.write(name);
			out.newLine();
			out.flush();
			fireStatusChange("Waiting for opponent");

			// Recieve back opponents information
			String response = in.readLine();
			fireStatusChange("Recieved opponent name");

			// Parse information
			int team = Integer.parseInt(response.substring(0,1));
			String opponentName = response.substring(2,response.length());
			String myName = name;
			fireStatusChange("Setting up game");
			fireConnected(in, out, sock, team, myName, opponentName);
		}
		catch (IOException e) {
			fireStatusChange(e.getMessage());
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
	
	public void fireConnected(BufferedReader in, BufferedWriter out, Socket sock, int team, String myName, String opponentName) {
		ConnectionEvent event = new ConnectionEvent(in, out, sock, team, myName, opponentName);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connected(event);
        }
	}
	
}