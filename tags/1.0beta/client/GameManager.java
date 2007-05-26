/**
 * Breakthrough Game
 * Date: May 9, 2007
 * @author Brad Dougherty, Kevin Harris
 * @version 1.0 beta
 * Breakthrough Client Game Manager
 */

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class GameManager {
	private ArrayList<BreakthroughListener> listeners = new ArrayList<BreakthroughListener>();
	private BufferedReader in;
	private BufferedWriter out;
	private final Color ERROR_COLOR = new Color(190,2,2);
	private final Color ALERT_COLOR = new Color(0,161,81);
	private Socket sock;
	private int team;
	private boolean myTurn = false;
	private boolean firstPieceSent = false;
	private boolean secondPieceSent = false;
	
	/**
	 * Add Listener - adds a listener
	 * @param listener The listener to add
	 */
	public void addListener(BreakthroughListener listener){
        listeners.add(listener);
    }
	
	/**
	 * Connect method - connects to the server, then loops for input
	 * @param address The address of the server
	 * @param name The name of the player
	 */
	public void connect(String address, int port, String name) {
		
		try {
			// Connect to the server
			fireConnectionBeginning();
			sock = new Socket(address, port);

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
			
			/* Server response loop
			 * 
			 * This loop waits for a server response. When the response it recieved,
			 * the length is used to determine the type, either coordinates or error code.
			 * 
			 */
			fireStatusChange("statusOpponentTurn");
			int parsed = 1;
			boolean doLoop = true;
			do {
				
				response = in.readLine();
				System.out.println("\nLength of response: "+response.length());
				System.out.println("Raw response: "+response);
				
				if (response.length() > 2) {
					
					/* FORMAT OF RESPONSE
					 * 
					 *  ###,###
					 * team number, x, y
					 * 
					 */
					
					System.out.println("Response: move piece");
					setMyTurn(false);
					movePiece(response.substring(0,3), response.substring(4,7));
					firstPieceSent = false;
					secondPieceSent = false;
				}
				else {
					
					/* RESPONSE CODES
					 * 
					 *  0 -> Turn beginning
					 * -1 -> Team 1 is the winner
					 * -2 -> Team 2 is the winner
					 * -3 -> Confirmation of valid selection
					 * -4 -> Invalid selection
					 * -5 -> Opponent disconnected
					 * 
					 */
					
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
						fireStatusChange("errorMoveInvalid", ERROR_COLOR);
						if (firstPieceSent && secondPieceSent) {
							secondPieceSent = false;
						}
						else if (firstPieceSent && !secondPieceSent) {
							firstPieceSent = false;
						}
					}
					else if (parsed == -5) {
						System.out.println("Response: Opponent disconnected");
						setMyTurn(false);
						doLoop = false;
					}
					
				}
				
			}
			while (doLoop);
			
			// When the game ends
			if (team + parsed == 0) {
				fireGameOver(true);
			}
			else if (parsed == -5) {
				fireGameOver("errorOpponentDisconnected");
			}
			else {
				fireGameOver(false);
			}
			
		}
		catch (UnknownHostException uhe) {
			fireConnectionError(uhe);
			fireStatusChange("errorUnknownHost", ERROR_COLOR);
		}
		catch (IOException e) {
			fireConnectionError(e);
			fireStatusChange("errorConnectionError", ERROR_COLOR);
		}
		
	}
	
	/**
	 * Piece Selected - triggered when the player clicks on a piece.
	 * Sends the information (xy) to the server.
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
				fireStatusChange("errorNotYourTurn", ERROR_COLOR);
			}
			
		}
		catch (IOException e) {
			fireConnectionError(e);
			fireStatusChange("errorConnectionError", ERROR_COLOR);
		}
	}
	
	/**
	 * Remove Listener - removes a listener
	 * @param listener The listener to remove
	 */
    public void removeListener(BreakthroughListener listener){
        listeners.remove(listener);
    }
	
	/**
	 * Connected - tells the listeners that the connection has been initiated
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
	 * Connection Beginning - tells the listeners that the connection attempt is beginning
	 */
	private void fireConnectionBeginning() {
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.connectionBeginning();
        }
	}
	
	/**
	 * Connection Error - sends the exception to the listeners encapsulated in a ConnectionErrorEvent
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
	 * Game Over - Tells the listeners that the game is over and a winner has been disclosed
	 * @param won If the player has won or not
	 */
	private void fireGameOver(boolean won) {
		GameOverEvent event = new GameOverEvent(won);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.gameOver(event);
        }
	}
	
	/**
	 * Game Over - Tells the listeners that the game is over because of an error
	 * @param error The resource bundle keyword of the error
	 */
	private void fireGameOver(String error) {
		GameOverEvent event = new GameOverEvent(error);
		Iterator iter = new ArrayList<BreakthroughListener>(listeners).iterator();
        while (iter.hasNext()) {
            BreakthroughListener listener = (BreakthroughListener) iter.next();
            listener.gameOver(event);
        }
	}
	
	/**
	 * Set Piece - send an event to the client to tell it to set the specified piece
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
	 * Status Change - sends a status change event to the listeners
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
	 * Status Change - sends a status change event to the listeners
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
	 * Move Piece - triggered when both pieces have been sent and validated.
	 * Sends the pieces that have been moved to the client.
	 * @param first The formatted string of the first piece (txy)
	 * @param second The formatted string of the second piece (txy)
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
	
	/**
	 * Set My Turn - registers that it is currently the player's turn
	 * @param turn What to set the turn to (true for player's turn)
	 */
	private synchronized void setMyTurn(boolean turn) {
		if (turn) {
			fireStatusChange("statusYourTurn", ALERT_COLOR);
			this.myTurn = true;
		}
		else if (!turn) {
			fireStatusChange("statusOpponentTurn");
			this.myTurn = false;
		}
	}
	
}