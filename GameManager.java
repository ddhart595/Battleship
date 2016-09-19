package battleship;

import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GameManager {
	
	/**
	 * List of clients belonging to the game administered by this manager.
	 */
	private ArrayList<Client> gameClients;
	
	/**
	 * Socket listening for incoming connnections.
	 */
	private ServerSocket connectionListener;
	
	protected GameManager() {
		try {
			connectionListener = new ServerSocket(15527);
		}
		catch(IOException error) {
			System.out.println("Connection error " + error + ".");
		}		
	}
	
	/**
	 * Returns a reference to the the opponent Client object.
	 * @param requestingPlayer Player requesting a reference to their opponent's client object.
	 * @return Reference to opponent's Client object.
	 */
	protected Client getOpponent(Client requestingPlayer) {
		//Do not compare Client objects based on name as two players may have the same name.
		//Here, we want to find the other Client object from the one making this request and return a reference to it.
		if(requestingPlayer == gameClients.get(1))
			return gameClients.get(2);
		return gameClients.get(1);
	}
	
	protected void playGame() {
		
	}
	
	protected boolean waitForPlayersToConnect() {
		int maxNumberOfPlayers = 2;
		
		Socket playerSocket = null;
		
		while(maxNumberOfPlayers > 0) {
			try{
				playerSocket = connectionListener.accept();
				gameClients.add(new Client(new BufferedReader(new InputStreamReader(playerSocket.getInputStream())), new PrintWriter(playerSocket.getOutputStream()), this));
				maxNumberOfPlayers--;
			}
			catch(IOException error) {
				System.out.println("Connection error: " + error + ".");
			}
		}
		return true;
	}
}
