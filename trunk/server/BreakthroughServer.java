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
		
			ServerSocket ss = new ServerSocket(4567);
			
			int clientNumber = 1;
			
			//Accepts and pairs clients.
			while(true){
				
				Socket sock = ss.accept();
				
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
	public void team1Move(){
		
		//No winner.
		team1Output.println("0");
		team1Output.flush();
		
		
	}
	
	/**
	 *	Controls moving of team 2.
 	 */
	public void team2Move(){
		
		//No winner.
		team2Output.println("0");
		team2Output.flush();
	}
}//End thread class
 