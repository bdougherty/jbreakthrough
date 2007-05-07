/**
 * Breakthrough Game
 * Date: May 7, 2007
 * @author Brad Dougherty
 * @version 1.0 beta
 * Breakthrough Client Game Manager
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

public class GameManager {
	private ArrayList<BreakthroughListener> listeners = new ArrayList<BreakthroughListener>();
	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;
	private int team;
	private boolean myTurn = false;
	private boolean firstPieceSent = false;
	private boolean secondPieceSent = false;
	
	/**
	 * Connect method - connects to the server, then loops for input
	 * @param address The address of the server
	 * @param name The name of the player
	 */
	public void connect(String address, String name) {
		
		try {
			// Connect to the server
			fireConnectionBeginning();
			sock = new Socket(address, 16789);

			// Set up the reader and writer
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

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
			team = Integer.parseInt(response.substring(0,1));
			String opponentName = response.substring(2,response.length());
			String myName = name;
			fireStatusChange("statusSettingUpGame");
			fireConnected(team, myName, opponentName);
			
			fireStatusChange("statusOpponentTurn");
			int parsed = 1;
			boolean doLoop = true;
			do {
				
				// Read from the server
				response = in.readLine();
				System.out.println("\nLength of response: "+response.length());
				System.out.println("Raw response: "+response);
				
				if (response.length() > 2) {
					System.out.println("Response: move piece");
					setMyTurn(false);
					movePiece(response.substring(0,3), response.substring(4,7));
					firstPieceSent = false;
					secondPieceSent = false;
				}
				else {
					
					parsed = Integer.parseInt(response);

					if (parsed == 0) {
						System.out.println("Response: your turn");
						setMyTurn(true);
					}
					else if (parsed == -1) {
						System.out.println("Response: Team 1 wins!");
						doLoop = false;
					}
					else if (parsed == -2) {
						System.out.println("Response: Team 2 wins!");
						doLoop = false;
					}
					else if (parsed == -3) {
						System.out.println("Response: Valid selection");
					}
					else if (parsed == -4) {
						System.out.println("Response: Invalid move");
						fireStatusChange("errorMoveInvalid");
						if (firstPieceSent && secondPieceSent) {
							secondPieceSent = false;
						}
						else if (firstPieceSent && !secondPieceSent) {
							firstPieceSent = false;
						}
					}
					else if (parsed == -5) {
						System.out.println("Response: Opponent disconnected");
					}
					
				}
				
			}
			while (doLoop);
			
			// When the game ends
			if (team + parsed == 0) {
				fireGameOver(true);
			}
			else {
				fireGameOver(false);
			}
			
		}
		catch (UnknownHostException uhe) {
			fireConnectionError(uhe);
			fireStatusChange("errorUnknownHost", Color.RED);
		}
		catch (IOException e) {
			fireConnectionError(e);
			fireStatusChange("errorConnectionError", Color.RED);
		}
		
	}
	
	/**
	 * Piece Selected
	 * @param info The formatted string of the piece
	 */
	public synchronized void pieceSelected(String info) {
		try {
			
			if (myTurn) {
				
				if (!firstPieceSent) {
					out.write(info.substring(1,3));
					out.newLine();
					out.flush();
					firstPieceSent = true;
				}
				else if (firstPieceSent) {
					out.write(info.substring(1,3));
					out.newLine();
					out.flush();
					secondPieceSent = true;
				}
			
			}	
			else {
				//Not my Turn
				fireStatusChange("errorNotYourTurn", Color.RED);
			}
			
		}
		catch (IOException e) {
			fireConnectionError(e);
			fireStatusChange("errorConnectionError", Color.RED);
		}
	}
	
	/**
	 * Move Piece
	 * @param first The formatted string of the first piece
	 * @param second The formatted string of the second piece
	 */
	private void movePiece(String first, String second) {
		
		// Parse info for first piece
		int team1 = Integer.parseInt(first.substring(0,1));
		int x1 = Integer.parseInt(first.substring(1,2));
		int y1 = Integer.parseInt(first.substring(2,3));
		
		// Parse info for second piece
		int team2 = Integer.parseInt(second.substring(0,1));
		int x2 = Integer.parseInt(second.substring(1,2));
		int y2 = Integer.parseInt(second.substring(2,3));
		
		fireSetPiece(team1, x1, y1);
		fireSetPiece(team2, x2, y2);
		
	}
	
	private synchronized void setMyTurn(boolean turn) {
		if (turn) {
			fireStatusChange("statusYourTurn");
			this.myTurn = true;
		}
		else if (!turn) {
			fireStatusChange("statusOpponentTurn");
			this.myTurn = false;
		}
	}
	
	public void addListener(BreakthroughListener listener){
        listeners.add(listener);
    }

    public void removeListener(BreakthroughListener listener){
        listeners.remove(listener);
    }
	
	/**
	 * Status change - sends a status change event to the listeners
	 * @param status The resource bundle keyword of the status
	 */
	private void fireStatusChange(String status) {
		StatusChangeEvent event = new StatusChangeEvent(status);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.statusChanged(event);
        }
	}
	
	/**
	 * Status change - sends a status change event to the listeners
	 * @param status The resource bundle keyword of the status
	 * @param color The color to flash the background of the status text
	 */
	private void fireStatusChange(String status, Color color) {
		StatusChangeEvent event = new StatusChangeEvent(status, color);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.statusChanged(event);
        }
	}
	
	/**
	 * Connection beginning - tells the client that the connection attempt is beginning
	 */
	private void fireConnectionBeginning() {
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connectionBeginning();
        }
	}
	
	/**
	 * Connected - tells the client that the connection has been initiated
	 * @param team The integer team that the client has been assigned to
	 * @param myName The name of the client
	 * @param opponentName The name of the assigned opponent
	 */
	private void fireConnected(int team, String myName, String opponentName) {
		ConnectionEvent event = new ConnectionEvent(team, myName, opponentName);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connected(event);
        }
	}
	
	/**
	 * Connection error
	 * @param e The exception returned from the error
	 */
	private void fireConnectionError(Exception e) {
		ConnectionErrorEvent event = new ConnectionErrorEvent(e);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connectionError(event);
        }
	}
	
	/**
	 * Set piece - send an event to the client to tell it to set the specified piece
	 * @param team The team that the piece belongs to
	 * @param x The x coordinate of the piece
	 * @param y The y coordinate of the piece
	 */
	private void fireSetPiece(int team, int x, int y) {
		PieceMovedEvent event = new PieceMovedEvent(team, x, y);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.pieceMoved(event);
        }
	}
	
	/**
	 * Game over
	 * @param won If you won or not
	 */
	private void fireGameOver(boolean won) {
		GameOverEvent event = new GameOverEvent(won);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.gameOver(event);
        }
	}
	
}