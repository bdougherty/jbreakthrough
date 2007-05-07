import java.io.*;
import java.net.*;

/**
 *	219- Final Project, Server for Breakthrough Game.
 * Professor Whittington
 *	@author Kevin Harris, Brad Dougherty
 *	@version 1.0
 */
public class BreakthroughServer {

	/**
	 *	Constructor, Starts server and pairs clients.
	 */
	public BreakthroughServer(){
		
			Socket team1 = null;
			Socket team2 = null;
			
		try{
		
			ServerSocket ss = new ServerSocket(16789);
			System.out.println("Breakthrough Server Started...");
			
			int clientNumber = 1;
			
			//Accepts and pairs clients.
			while(true){
				
				System.out.println("Waiting for clients...");
				Socket sock = ss.accept();
				System.out.println("Client Connected...");
				
				if(clientNumber == 1){
				
					team1 = sock;
					clientNumber++;
				}
				else if(clientNumber == 2){
				
					team2 = sock;
					
					clientNumber = 1;
					
					ServerThread st = new ServerThread(team1, team2);
					
					st.start();
				}
			}
		}
		catch(IOException ioe){
		
			System.out.println(ioe.getMessage());
		}
	}
	
	/**
	 *	Main Method, Calls constructor.
	 */
	public static void main(String[] args){
	
		new BreakthroughServer();
	}
}

/**
 *	Thread class for each client pair.
 */
class ServerThread extends Thread{

	private Socket team1Sock, team2Sock;
	private String team1Name, team2Name;
	private BufferedReader team1Input, team2Input;
	private PrintWriter team1Output, team2Output;
	private boolean team1Winner, team2Winner;
	private int[][] boardArray;
	
	/**
	 *	Constructor, sets socket attributes, initializes writers and readers.
	 * @param s1 Team 1 Socket
	 * @param s2 Team 2 Socket
 	 */
	public ServerThread(Socket s1, Socket s2){
	
		team1Sock = s1;
		team2Sock = s2;
		
		team1Winner = false;
		team2Winner = false;
		
		boardArray = new int[8][8];
		
		//Create readers and writers.
		try{
		
			team1Input = new BufferedReader(new InputStreamReader(team1Sock.getInputStream()));
			team2Input = new BufferedReader(new InputStreamReader(team2Sock.getInputStream()));
			
			team1Output = new PrintWriter(new OutputStreamWriter(team1Sock.getOutputStream()));
			team2Output = new PrintWriter(new OutputStreamWriter(team2Sock.getOutputStream()));
		}
		catch(IOException ioe){
		
			System.out.println(ioe.getMessage());
		}
	}
	
	/**
	 *	Control of each game.
 	 */
	public void run(){
		
		try{
		
			//Accept names.
			team1Name = team1Input.readLine();
			team2Name = team2Input.readLine();
			
			//Create array to hold board.
			for(int x = 0; x < 8; x++){
			
				for(int y = 0; y < 8; y++){
				
					//Team 1.
					if(x == 0 || x == 1){
					
						boardArray[x][y] = 1;
					}
					
					//Team 2.
					else if(x == 6 || x == 7){
					
						boardArray[x][y] = 2;
					}
					
					//Empty Cells.
					else{
						
						boardArray[x][y] = 0;
					}
				}
			}
			
			//Send each client team number and opponent.
			team1Output.println("1," + team2Name);
			team1Output.flush();
			team2Output.println("2," + team1Name);
			team2Output.flush();
			
			//Players move.
			while(!team1Winner && !team2Winner){
				
				//Team 1 moves.
				this.team1Move();
				
				if(!team1Winner && !team2Winner){
				
					//Team 2 moves.
					this.team2Move();
				}
			}
			
			//Winner declared.
			if(team1Winner == true){		//Team 1 winner.
			
				team1Output.println(-1);
				team1Output.flush();
				
				team2Output.println(-1);
				team2Output.flush();
			}
			else if(team2Winner == true){		//Team 2 winner.
			
				team1Output.println(-2);
				team1Output.flush();
				
				team2Output.println(-2);
				team2Output.flush();
			}
		}
		catch(IOException ioe){
		
			System.out.println(ioe.getMessage());
		}
	}
	
	/**
	 *	Controls moving of team 1.
 	 */
	private void team1Move(){
	
		int x1 = 0;		//Coordinates for first piece.
		int y1 = 0;		
		int x2,y2;		//Coordinates for move to.
		boolean firstMoveIsGood = false;
		boolean secondMoveIsGood = false;
		String coordinates;
		
		//No winner.
		team1Output.println("0");
		team1Output.flush();
		
		//FIRST MOVE.
		while(!firstMoveIsGood){	
			
			try{
						
				//Get first move.
				coordinates = team1Input.readLine();
				System.out.println("Received team 1 move");
				
				x1 = Integer.parseInt(coordinates.substring(0,1));
				
				y1 = Integer.parseInt(coordinates.substring(1,2));
				
				if(boardArray[x1][y1] == 1){
				
					team1Output.println("-3");
					team1Output.flush();
					System.out.println("Sent: -3 valid");
					firstMoveIsGood = true;
				}
				else{
				
					team1Output.println("-4");
					team1Output.flush();
					System.out.println("Sent: -4 not valid");
				}
			}			
			catch(NumberFormatException nfe){
			
				team1Output.println("-4");
				team1Output.flush();
				System.out.println("Sent: -4 not valid");
			}
			catch(IOException ioe){
			
				System.out.println(ioe.getMessage());
			}
		}
		
		//SECOND MOVE.
		while(!secondMoveIsGood){
		
			try{
			
				//Get second move.
				coordinates = team1Input.readLine();
				System.out.println("Received team 1 move");
				
				x2 = Integer.parseInt(coordinates.substring(0,1));
				
				y2 = Integer.parseInt(coordinates.substring(1,2));
				
				if(boardArray[x2][y2] == 1){		//Selects new piece.
				
					team1Output.println("-3");
					team1Output.flush();
					System.out.println("Sent: -3 valid");
				}
				else if(boardArray[x2][y2] == 0){		//Moving to empty space.
				
					if(x1 + 1 == x2){		//Check for movement right one space.
					
						if(y1 + 1 == y2 || y1 - 1 == y2 || y1 == y2){		//Check for valid diagonal.
						
							//Send formatted string of valid move.
							team1Output.println("0" + x1 + y1 + "," + "1" + x2 + y2);
							team1Output.flush();
							team2Output.println("0" + x1 + y1 + "," + "1" + x2 + y2);
							team2Output.flush();
							
							secondMoveIsGood = true;
							
							//Check for winner.
							if(x2 == 7){
							
								team1Winner = true;
							}
						}
						else{
						
							team1Output.println("-4");
							team1Output.flush();
						}
					}
					else{
					
						team1Output.println("-4");
						team1Output.flush();
					}
				}
			}
			catch(NumberFormatException nfe){
			
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(ArrayIndexOutOfBoundsException aie){
			
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(IOException ioe){
			
				System.out.println(ioe.getMessage());
			}
		}
	}
	
	/**
	 *	Controls moving of team 2.
 	 */
	private void team2Move(){
		
		//No winner.
		team2Output.println("0");
		team2Output.flush();
	}
}//End thread class