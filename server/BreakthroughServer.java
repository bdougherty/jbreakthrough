import java.io.*;
import java.net.*;
import java.util.Date;

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
			System.out.println("Breakthrough Server Started at: " + ss.getInetAddress().getLocalHost() + "...");
			
			int clientNumber = 1;
			
			//Accepts and pairs clients.
			while(true){
				
				System.out.println("Waiting for clients...");
				Socket sock = ss.accept();
				System.out.println(sock.getInetAddress().getHostAddress() + " Connected...");
				
				try{
				
					if(clientNumber == 1){
					
						team1 = sock;
						clientNumber++;
					}
					else if(clientNumber == 2){
						
						team1.setKeepAlive(true);
						
						team2 = sock;
						
						clientNumber = 1;
						
						ServerThread st = new ServerThread(team1, team2);
						
						st.start();
					}
				}
				catch(SocketException se){
		
					team1.close();
					team1 = sock;
					
					clientNumber = 2;
				}
				catch(Exception e){
				
					e.printStackTrace();
				}
			}
		}
		catch(IOException ioe){
		
			System.out.println(ioe.getMessage());
		}
		catch(Exception e){
				
			System.out.println(e.getMessage());
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
	
	//Game and Clients.
	private Socket team1Sock, team2Sock;
	private String team1Name, team2Name;
	private BufferedReader team1Input, team2Input;
	private PrintWriter team1Output, team2Output;
	private boolean team1Winner, team2Winner;
	private int[][] boardArray;
	
	//Moves.
	private int x1, y1;		//Coordinates for first piece.		
	private int x2,y2;		//Coordinates for move to.
	private boolean firstMoveIsGood;
	private boolean secondMoveIsGood;
	private boolean newSelection;
	private String coordinates;
	
	//Logging information.
	private String ipTeam1, ipTeam2;
	private Date startTime, finishTime;
	
	private boolean disconnected;
	
	/**
	 *	Constructor, sets socket attributes, initializes writers and readers.
	 * @param s1 Team 1 Socket
	 * @param s2 Team 2 Socket
 	 */
	public ServerThread(Socket s1, Socket s2){
	
		disconnected = false;
		
		team1Sock = s1;
		team2Sock = s2;
		
		team1Winner = false;
		team2Winner = false;
		
		x1 = 0;
		y1 = 0;
		
		newSelection = false;
		
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
			//Start time.
			startTime = new Date();
			
			//IP Addresses.
			ipTeam1 = team1Sock.getInetAddress().getHostAddress();
			ipTeam2 = team2Sock.getInetAddress().getHostAddress();
			
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
				
				if(disconnected){
					
					return;
				}
				else{
				
					//Team 1 moves.
					this.team1FirstMove();
				}
				
				if(!team1Winner && !team2Winner){
					
					if(disconnected){
					
						return;
					}
					else{
					
						//Team 2 moves.
						this.team2FirstMove();
					}
				}
			}
			
			//Winner declared.
			if(team1Winner == true){		//Team 1 winner.
			
				team1Output.println("-1");
				team1Output.flush();
				
				team2Output.println("-1");
				team2Output.flush();
				
				// System.out.println("Team 1 winner");
			}
			else if(team2Winner == true){		//Team 2 winner.
			
				team1Output.println("-2");
				team1Output.flush();
				
				team2Output.println("-2");
				team2Output.flush();
				
				// System.out.println("Team 2 winner");
			}
			
			//Finish time.
			finishTime = new Date();
			
			//Send log information.
			logFile();
			
			//Close connections.
			team1Output.close();
			team1Input.close();
			team1Sock.close();
			
			team2Output.close();
			team2Input.close();
			team2Sock.close();
		}
		catch(SocketException se){		//If Client disconnects.
			
			disconnected = true;
			
			team1Output.println("-5");
			team1Output.flush();
				
			try{
				//Close connections.
				team1Output.close();
				team1Input.close();
				team1Sock.close();
			}
			catch(IOException ioe){
			
				System.out.println(ioe.getMessage());
			}			
			
			team2Output.println("-5");
			team2Output.flush();
			
			try{
				//Close connections.
				team2Output.close();
				team2Input.close();
				team2Sock.close();
			}
			catch(IOException ioe){
			
				System.out.println(ioe.getMessage());
				disconnected = true;
				return;
			}
			
			logFile();
			
			return;
		}
		catch(IOException ioe){
		
			ioe.printStackTrace();
			disconnected = true;
			return;
		}
		catch(Exception e){
				
			e.printStackTrace();
			disconnected = true;
			return;
		}
	}
	
	/**
	 *	Controls team 1 piece selection.
 	 */
	private void team1FirstMove(){
		
		firstMoveIsGood = false;
		
		if(!newSelection){
		
			//No winner.
			team1Output.println("0");
			team1Output.flush();
		}
			
		//FIRST MOVE.
		while(!firstMoveIsGood){	
			
			try{
				
				if(!newSelection){		
				
					//Get first move.
					coordinates = team1Input.readLine();
					
					//System.out.println("Got first coord. from P1");
					
					try{
					
						x1 = Integer.parseInt(coordinates.substring(0,1));
					
						y1 = Integer.parseInt(coordinates.substring(1,2));
					}
					catch(NullPointerException npe){
											
						if(team1Sock.isConnected()){
							
							throw new SocketException();
						}
						else{
						
							npe.printStackTrace();
						}
					}
				}
				else if(newSelection){
				
					x1 = x2;
					y1 = y2;
					
					firstMoveIsGood = true;
					this.team1SecondMove();
					break;
				}
				
				if(boardArray[x1][y1] == 1){
				
					team1Output.println("-3");
					team1Output.flush();
					firstMoveIsGood = true;
					
					//System.out.println("Sent valid first coord to P1");
					
					this.team1SecondMove();
				}
				else{
				
					team1Output.println("-4");
					team1Output.flush();
				}
			}			
			catch(NumberFormatException nfe){
			
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(ArrayIndexOutOfBoundsException aiobe) {
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(StringIndexOutOfBoundsException siobe) {
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(SocketException se){		//If Client disconnects.
				
				disconnected = true;
				
				team1Output.println("-5");
				team1Output.flush();
					
				try{
				
					//Close connections.
					team1Output.close();
					team1Input.close();
					team1Sock.close();
				}
				catch(IOException ioe){
				
					ioe.printStackTrace();
				}
				
				team2Output.println("-5");
				team2Output.flush();
				
				try{
				
					//Close connections.
					team2Output.close();
					team2Input.close();
					team2Sock.close();
				}
				catch(IOException ioe){
					
					ioe.printStackTrace();
				}
				finishTime = new Date();
				logFile();
				
				return;
			}
			catch(IOException ioe){
			
				ioe.printStackTrace();
			
				disconnected = true;
				return;
			}
			catch(Exception e){
				
				e.printStackTrace();
				disconnected = true;
				return;
			}
		}
	}
	
	/**
	 *	Controls team 1 destination.
 	 */
	private void team1SecondMove(){
	
		secondMoveIsGood = false;
	
		//SECOND MOVE.
		while(!secondMoveIsGood){
		
			try{
			
				//Get second move.
				coordinates = team1Input.readLine();
				
				//System.out.println("Got 2nd coord from P1.");
				
				try{
				
					x2 = Integer.parseInt(coordinates.substring(0,1));
				
					y2 = Integer.parseInt(coordinates.substring(1,2));
				}
				catch(NullPointerException npe){
						
					if(team1Sock.isConnected()){
						
						throw new SocketException();
					}
					else{
						
						npe.printStackTrace();
					}
				}
				catch(StringIndexOutOfBoundsException siobe) {
					team1Output.println("-4");
					team1Output.flush();
				}
				
				if(boardArray[x2][y2] == 1){		//Selects new piece.
					
					//System.out.println("equals 1");
					team1Output.println("-3");
					team1Output.flush();
					
					newSelection = true;
					this.team1FirstMove();
				}
				else if(boardArray[x2][y2] == 0){		//Moving to empty space.
					
					//System.out.println("equals 0");
					if(x1 + 1 == x2){		//Check for movement right one space.
					
						if(y1 + 1 == y2 || y1 - 1 == y2 || y1 == y2){		//Check for valid diagonal.
						
							//Send formatted string of valid move.
							team1Output.println("0" + x1 + y1 + "," + "1" + x2 + y2);
							team1Output.flush();
							team2Output.println("0" + x1 + y1 + "," + "1" + x2 + y2);
							team2Output.flush();
							
							//System.out.println("Sent valid 2nd coord to P1.");
							
							boardArray[x1][y1] = 0;
							boardArray[x2][y2] = 1;
							
							secondMoveIsGood = true;
							newSelection = false;
							
							//Check for winner.
							if(x2 == 7){
							
								team1Winner = true;
							}
						}
						else{
						
							team1Output.println("-4");
							team1Output.flush();
							
							//System.out.println("Sent invalid 2nd coord to P1.");
						}
					}
					else{
					
						team1Output.println("-4");
						team1Output.flush();
					}
				}
				else if(boardArray[x2][y2] == 2){		//Moving to an enemy space.
				
					//System.out.println("equals 2");
					if(x1 + 1 == x2){		//Check for movement right one space.
					
						if(y1 + 1 == y2 || y1 - 1 == y2){		//Check for valid diagonal attack.
						
							//Send formatted string of valid move.
							team1Output.println("0" + x1 + y1 + "," + "1" + x2 + y2);
							team1Output.flush();
							team2Output.println("0" + x1 + y1 + "," + "1" + x2 + y2);
							team2Output.flush();
							
							boardArray[x1][y1] = 0;
							boardArray[x2][y2] = 1;
							
							secondMoveIsGood = true;
							newSelection = false;
							
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
			catch(SocketException se){		//If Client disconnects.
				
				disconnected = true;
				
				team1Output.println("-5");
				team1Output.flush();
					
				try{
				
					//Close connections.
					team1Output.close();
					team1Input.close();
					team1Sock.close();
				}
				catch(IOException ioe){
				
					ioe.printStackTrace();
				}
				
				team2Output.println("-5");
				team2Output.flush();
				
				try{
				
					//Close connections.
					team2Output.close();
					team2Input.close();
					team2Sock.close();
				}
				catch(IOException ioe){
					
					ioe.printStackTrace();
					disconnected = true;
					return;
				}
				finishTime = new Date();
				logFile();
				
				return;
			}
			catch(IOException ioe){
			
				System.out.println(ioe.getMessage());
			}
			catch(Exception e){
				
				e.printStackTrace();
				disconnected = true;
				return;
			}
		}
	}
	
	/**
	 *	Controls team 2 piece selection.
 	 */
	private void team2FirstMove(){
		
		firstMoveIsGood = false;
		
		if(!newSelection){
		
			//No winner.
			team2Output.println("0");
			team2Output.flush();
		}
		
		//FIRST MOVE.
		while(!firstMoveIsGood){	
			
			try{
				
				if(!newSelection){		
				
					//Get first move.
					coordinates = team2Input.readLine();
					
					//System.out.println("Got first coord from P2.");
					
					try{
					
						x1 = Integer.parseInt(coordinates.substring(0,1));
					
						y1 = Integer.parseInt(coordinates.substring(1,2));
					}
					catch(NullPointerException npe){
						
						if(team1Sock.isConnected()){
							
							throw new SocketException();
						}
						else{
						
							npe.printStackTrace();
						}
					}
					catch(StringIndexOutOfBoundsException siobe) {
						team1Output.println("-4");
						team1Output.flush();
					}
				}
				else if(newSelection){
				
					x1 = x2;
					y1 = y2;
					
					firstMoveIsGood = true;
					this.team2SecondMove();
					break;
				}
				
				if(boardArray[x1][y1] == 2){
				
					team2Output.println("-3");
					team2Output.flush();
					firstMoveIsGood = true;
					
					//System.out.println("Sent valid first coord to P2.");
					
					this.team2SecondMove();
				}
				else{
				
					team2Output.println("-4");
					team2Output.flush();
				}
			}			
			catch(NumberFormatException nfe){
			
				team2Output.println("-4");
				team2Output.flush();
			}
			catch(StringIndexOutOfBoundsException siobe) {
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(ArrayIndexOutOfBoundsException aiobe) {
				team1Output.println("-4");
				team1Output.flush();
			}
			catch(SocketException se){		//If Client disconnects.
				
				disconnected = true;
				
				team1Output.println("-5");
				team1Output.flush();
					
				try{
				
					//Close connections.
					team1Output.close();
					team1Input.close();
					team1Sock.close();
				}
				catch(IOException ioe){
				
					ioe.printStackTrace();
				}
				
				team2Output.println("-5");
				team2Output.flush();
				
				try{
				
					//Close connections.
					team2Output.close();
					team2Input.close();
					team2Sock.close();
				}
				catch(IOException ioe){
					
					ioe.printStackTrace();
					
				}
				finishTime = new Date();
				logFile();
				
				return;
			}
			catch(IOException ioe){
			
				ioe.printStackTrace();
				disconnected = true;
				return;
			}
			catch(Exception e){
				
				e.printStackTrace();
				disconnected = true;
				return;
			}
		}
	}
	
	/**
	 *	Controls team 2 destination.
 	 */
	private void team2SecondMove(){
	
		secondMoveIsGood = false;
	
		//SECOND MOVE.
		while(!secondMoveIsGood){
		
			try{
			
				//Get second move.
				coordinates = team2Input.readLine();
				
				//System.out.println("Got 2nd coord from P2.");
				
				try{
				
					x2 = Integer.parseInt(coordinates.substring(0,1));
					
					y2 = Integer.parseInt(coordinates.substring(1,2));
				}
				catch(NullPointerException npe){
											
					if(team1Sock.isConnected()){
						
						throw new SocketException();
					}
					else{
						
						npe.printStackTrace();
					}
				}
				
				if(boardArray[x2][y2] == 2){		//Selects new piece.
					//System.out.println("equals 2");
					team2Output.println("-3");
					team2Output.flush();
					
					newSelection = true;
					this.team2FirstMove();
				}
				else if(boardArray[x2][y2] == 0){		//Moving to empty space.
					
					//System.out.println("equals 0");
					if(x1 - 1 == x2){		//Check for movement left one space.
					
						if(y1 + 1 == y2 || y1 - 1 == y2 || y1 == y2){		//Check for valid diagonal.
						
							//Send formatted string of valid move.
							team1Output.println("0" + x1 + y1 + "," + "2" + x2 + y2);
							team1Output.flush();
							team2Output.println("0" + x1 + y1 + "," + "2" + x2 + y2);
							team2Output.flush();
							
							//System.out.println("Sent valid 2nd coord to P2.");
							
							boardArray[x1][y1] = 0;
							boardArray[x2][y2] = 2;
							
							secondMoveIsGood = true;
							newSelection = false;
							
							//Check for winner.
							if(x2 == 0){
							
								team2Winner = true;
							}
						}
						else{
						
							team2Output.println("-4");
							team2Output.flush();
							
							//System.out.println("Sent invalid 2nd coord to P2.");
						}
					}
					else{
					
						team2Output.println("-4");
						team2Output.flush();
					}
				}
				else if(boardArray[x2][y2] == 1){		//Moving to an enemy space.
				
					//System.out.println("equals 1");
					
					if(x1 - 1 == x2){		//Check for movement left one space.
					
						if(y1 + 1 == y2 || y1 - 1 == y2){		//Check for valid diagonal attack.
						
							//Send formatted string of valid move.
							team1Output.println("0" + x1 + y1 + "," + "2" + x2 + y2);
							team1Output.flush();
							team2Output.println("0" + x1 + y1 + "," + "2" + x2 + y2);
							team2Output.flush();
							
							boardArray[x1][y1] = 0;
							boardArray[x2][y2] = 2;
							
							secondMoveIsGood = true;
							newSelection = false;
							
							//Check for winner.
							if(x2 == 0){
							
								team2Winner = true;
							}
						}
						else{
						
							team2Output.println("-4");
							team2Output.flush();
						}
					}
				}
			}
			catch(NumberFormatException nfe){
			
				team2Output.println("-4");
				team2Output.flush();
				
			}
			catch(ArrayIndexOutOfBoundsException aie){
			
				team2Output.println("-4");
				team2Output.flush();
				
			}
			catch(SocketException se){		//If Client disconnects.
				
				disconnected = true;
				
				team1Output.println("-5");
				team1Output.flush();
					
				try{
				
					//Close connections.
					team1Output.close();
					team1Input.close();
					team1Sock.close();
				}
				catch(IOException ioe){
				
					ioe.printStackTrace();
				}
				
				team2Output.println("-5");
				team2Output.flush();
				
				try{
				
					//Close connections.
					team2Output.close();
					team2Input.close();
					team2Sock.close();
				}
				catch(IOException ioe){
					
					ioe.printStackTrace();
				}
				finishTime = new Date();
				logFile();
				
				return;
			}
			catch(IOException ioe){
			
				ioe.printStackTrace();
				disconnected = true;
				return;
			}
			catch(Exception e){
				
				e.printStackTrace();
				disconnected = true;
				return;
			}
		}
	}
	
	/**
	 *	Prints information to log file.
	 */
	private synchronized void logFile(){
	
		try{
		
			PrintWriter logOut = new PrintWriter(new FileWriter("BreakthroughLog.txt",true));
			
			if(disconnected){		//Log if game disconnects.
						
				logOut.println(ipTeam1 + "(" + team1Name + ") VS. " + ipTeam2 + "(" + team2Name + ")");
				logOut.println("Game Started: " + startTime);
				logOut.println("Game Finished: " + finishTime);
				logOut.println("GAME DISCONNECTED");
				logOut.println("");
				logOut.flush();
				logOut.close();
			}
			else{		//Log if winner is declared.
			
				//Winner.
				String winner = "";
				
				if(team1Winner){
				
					winner = team1Name;
				}
				else if(team2Winner){
				
					winner = team2Name;
				}
				
				logOut.println(ipTeam1 + "(" + team1Name + ") VS. " + ipTeam2 + "(" + team2Name + ")");
				logOut.println("Game Started: " + startTime);
				logOut.println("Game Finished: " + finishTime);
				logOut.println("Winner: " + winner);
				logOut.println("");
				logOut.flush();
				logOut.close();
			}
		}
		catch(IOException ioe){
		
			ioe.printStackTrace();
		}
		catch(Exception e){
				
			System.out.println(e.getMessage());
		}
	}
}//End thread class