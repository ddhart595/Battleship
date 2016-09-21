package battleship;

import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.lang.NumberFormatException;

public class GameManager {

	/**
	 * Socket listening for incoming connections.
	 */
	private ServerSocket connectionListener;
	
	/**
	 * List of clients belonging to the game administered by this manager.
	 */
	private ArrayList<Client> gameClients;
	
	private static final String NEW_LINE_CHARACTER = System.getProperty("line.separator");
	
	protected GameManager() {
		try {
			connectionListener = new ServerSocket(15527);
		}
		catch(IOException error) {
			System.out.println("Connection error " + error + ".");
		}		
		
		//Initialize the array lists of clients and client networking objects.
		gameClients = new ArrayList<Client>();
	}
	
	/**
	 * Returns list of clients belonging to game manager.
	 * Used in main method to get list of clients to determine name of winner.
	 * @return List of game clients.
	 */
	protected ArrayList<Client> getGameClients() {
		return gameClients;
	}
	
	/**
	 * Returns a reference to the the opponent Client object.
	 * @param requestingPlayer Player requesting a reference to their opponent's client object.
	 * @return Reference to opponent's Client object.
	 */
	protected Client getOpponent(Client requestingPlayer) {
		//Do not compare Client objects based on name as two players may have the same name.
		//Here, we want to find the other Client object from the one making this request and return a reference to it.
		if(requestingPlayer == gameClients.get(0))
			return gameClients.get(1);
		return gameClients.get(0);
	}
	
	/**
	 * Allows players to set their player names.
	 */
	protected void initializePlayers() {
		//Asynchronously ask users for their preferred player name and have them add ships to the board.
		gameClients.parallelStream().forEach(client -> {
			try {
				//Ask the user what name they would like to use.
				client.clientWriter.println("What user name would you like to use?");
				client.clientWriter.flush();
				
				//Wait for user input and set the client's user name accordingly.
				client.setPlayerName(client.clientReader.readLine());
			}
			catch (IOException error) {
				System.out.println("There was an error setting the players' names: " + error + ".");
			}
				
			//Allow each player to place their ships on the board.
			//This boolean will be used to repeat the request to place a given ship type if the user's input is malformed.
			boolean shipNotAdded = true;
				
			//Iterate through each ship type, allowing the user to place one of each.
			for(SHIPTYPES shipType : SHIPTYPES.values()) {
				//While the user's input was malformed and the ship was not added, repeat the loop.
				while(shipNotAdded) {
					//Give user instructions on how to specify ship placement.
					client.clientWriter.println(NEW_LINE_CHARACTER + "Place your " + shipType + " on the board." + NEW_LINE_CHARACTER);
					client.clientWriter.println("Specify name followed by stern position and ship direction." + NEW_LINE_CHARACTER);
					client.clientWriter.println("Example: Boaty McBoatface 2 3 EAST" + NEW_LINE_CHARACTER);
					client.clientWriter.println("If placement is invalid, you will be asked to place ship again." + NEW_LINE_CHARACTER);
					client.clientWriter.flush();
				
					//Tokenize the response from the user.
					StringTokenizer shipPlacement = null;
					try {
						shipPlacement = new StringTokenizer(client.clientReader.readLine());
					}
					catch(IOException error) {
					}
					
					try {
						//Ensure input correctly received from user.tokenized and there are at least 4 tokens (ship name, row number, column number and direction). If not, skip rest of loop and try again.
						if(shipPlacement != null && shipPlacement.countTokens() >= 3) {
							String shipName = "";
							//Assuming the user's input was not malformed, all but the last 3 tokens should belong to the ship's name (in case of compound names).
							while(shipPlacement.countTokens() > 3)
								shipName += shipPlacement.nextToken(" ") + " ";
							
							//The next two tokens should be parseable as ints and the last should be a string specifying the ship's direction.
							int rowNumber = Integer.parseInt(shipPlacement.nextToken());
							int columnNumber = Integer.parseInt(shipPlacement.nextToken());
							String heading = shipPlacement.nextToken();
							
							switch (shipType) {
								case BATTLESHIP:
									shipNotAdded = !(client.getClientGameBoard().addShip(new Battleship(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case CARRIER:
									shipNotAdded = !(client.getClientGameBoard().addShip(new Carrier(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case DESTROYER:
									shipNotAdded = !(client.getClientGameBoard().addShip(new Destroyer(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case CRUISER:
									shipNotAdded = !(client.getClientGameBoard().addShip(new Cruiser(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case SUBMARINE:
									shipNotAdded = !(client.getClientGameBoard().addShip(new Submarine(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
							}
						}
					}
					catch(NumberFormatException error) {
						//The coordinates the user specified could not be read as ints. Have them try again.
						client.clientWriter.println("The coordinates you specified could not be parsed as ints. Please try again.");
						client.clientWriter.flush();
					}
					
					if(shipNotAdded)
						client.clientWriter.println("There was a problem adding the " + shipType + " to the board at the location specified. Please try again.");
					client.clientWriter.flush();
				}
				shipNotAdded = true;
				//Player successfully added a ship to their board. Print the boards for them.
				if(shipNotAdded)
					client.drawBoards();
			}
			client.drawBoards();
		});
	}
	
	/**
	 * Once all players have joined game, call clients' playGame method to begin the game.
	 */
	protected void playGame() {
		//Asynchronously call clients' playGame method to begin game.
		gameClients.parallelStream().forEach(client -> {
			try {
				client.playGame();
			}
			catch(IOException error) {
				System.out.println("There was an error during game play: " + error + ".");
			}
		});
	}
	
	/**
	 * Waits for connections from 2 players
	 * @return true once two players have successfully connected.
	 */
	protected boolean waitForPlayersToConnect() {
		//Set the number of players for a given game.
		int maxNumberOfPlayers = 2;
		
		//Initialize a socket on which to listen for incoming connections.
		Socket playerSocket = null;
		
		//Wait for all players to connect.
		while(maxNumberOfPlayers > 0) {
			try{
				//When a player connects, get an input and output stream on the socket and create a new Client with the streams.
				playerSocket = connectionListener.accept();
				
				//Create new client and add to the array of clients.
				gameClients.add(new Client(new BufferedReader(new InputStreamReader(playerSocket.getInputStream())), new PrintWriter(playerSocket.getOutputStream()), this));
				
				//Decrement the number of players to ensure we do not have too many players join the game.
				maxNumberOfPlayers--;
			}
			catch(IOException error) {
				System.out.println("Connection error: " + error + ".");
			}
		}
		return true;
	}
	
	/**
	 * Main driver for Battleship. Creates game manager, waits for players to join and then launches game. Notifies players when one wins and closes connections.
	 * @param args Command line arguments; unused.
	 * @throws IOException Problems establishing connection between server and clients may result in IOException being thrown. 
	 */
	public static void main( String [] args ) throws IOException {
		//Create game manager to administer game.
		GameManager gameManager = new GameManager();
		
		//Print status messages to server console; players will not see messages printed using System.out. Must use client.clientWriter to send message to user once they have connected.
		System.out.println( "<----------Welcome to Battleship!---------->" );
		System.out.println( "Waiting for two players to connect to TCP:15527" );
		
		//Once server is listening, wait for players to connect.
		gameManager.waitForPlayersToConnect();
		System.out.println( "Clients have joined!!!");		
		
		//Start game once all players have joined, initialize players (allow them to select name and place ships on board).
		gameManager.initializePlayers();
		
		//After initialization, launch game.
		System.out.println( gameManager.gameClients.get(0).getPlayerName() + " vs " + gameManager.gameClients.get(1).getPlayerName() + " Fire!" );
		gameManager.playGame();		
		
		//Once playGame() returns, one player has won. Determine winner, tell both players and close connection.
		String winnerName = "";
		if(gameManager.getGameClients().get(0).getClientGameBoard().hasShipsRemaining())
			winnerName = gameManager.getGameClients().get(0).getPlayerName();
		else
			winnerName = gameManager.getGameClients().get(1).getPlayerName();

		for(Client client : gameManager.getGameClients()) {
			client.clientWriter.println("Game over! " + winnerName + " is the winner!" + NEW_LINE_CHARACTER + NEW_LINE_CHARACTER + "Server shutting down.");
			client.clientWriter.flush();
			client.clientWriter.close();
			client.clientReader.close();
		}
			
		System.out.println( "Shutting down server now... Disconnecting Clients..." );
	}
}
