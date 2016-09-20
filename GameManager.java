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
	 * List of clients belonging to the game administered by this manager.
	 */
	private ArrayList<Client> gameClients;
	
	/**
	 * Socket listening for incoming connections.
	 */
	private ServerSocket connectionListener;
	
	/**
	 * Array list of client networking objects to map clients to input/output streams for parallel operations.
	 */
	private ArrayList<ClientCommunicationsPackage> clientCommunications;
	
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
		clientCommunications = new ArrayList<ClientCommunicationsPackage>();
	}
	
	protected class ClientCommunicationsPackage {
		private Client client;
		private PrintWriter clientWriter;
		private BufferedReader clientReader;
		
		protected ClientCommunicationsPackage(Client client, PrintWriter clientWriter, BufferedReader clientReader) {
			this.client = client;
			this.clientWriter = clientWriter;
			this.clientReader = clientReader;
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
		if(requestingPlayer == gameClients.get(0))
			return gameClients.get(1);
		return gameClients.get(0);
	}
	
	/**
	 * Allows players to set their player names.
	 */
	protected void initPlayers() {
		//Asynchronously ask users for their preferred player name and have them add ships to the board.
		clientCommunications.parallelStream().forEach(client -> {
			try {
				//Ask the user what name they would like to use.
				client.clientWriter.println("What user name would you like to use?");
				client.clientWriter.flush();
				
				//Wait for user input and set the client's user name accordingly.
				client.client.setPlayerName(client.clientReader.readLine());
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
								shipName += shipPlacement.nextToken(" ");
							
							//The next two tokens should be parseable as ints and the last should be a string specifying the ship's direction.
							int rowNumber = Integer.parseInt(shipPlacement.nextToken());
							int columnNumber = Integer.parseInt(shipPlacement.nextToken());
							String heading = shipPlacement.nextToken();
							
							switch (shipType) {
								case BATTLESHIP:
									shipNotAdded = !(client.client.getClientGameBoard().addShip(new Battleship(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case CARRIER:
									shipNotAdded = !(client.client.getClientGameBoard().addShip(new Carrier(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case DESTROYER:
									shipNotAdded = !(client.client.getClientGameBoard().addShip(new Destroyer(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case CRUISER:
									shipNotAdded = !(client.client.getClientGameBoard().addShip(new Cruiser(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
									break;
								case SUBMARINE:
									shipNotAdded = !(client.client.getClientGameBoard().addShip(new Submarine(shipName), new Position(rowNumber, columnNumber), HEADING.valueOf(heading.toUpperCase())));
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
					client.client.drawBoards();
			}
			client.client.drawBoards();
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
				
				BufferedReader inputStream = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
				PrintWriter outputStream = new PrintWriter(playerSocket.getOutputStream());
				Client joiningClient = new Client(inputStream, outputStream, this);
				gameClients.add(joiningClient);
				
				//Add a reader and writer to the server to read input from the user and write output to the user.
				clientCommunications.add(new ClientCommunicationsPackage(joiningClient, outputStream, inputStream));
				
				//Decrement the number of players to ensure we do not have too many players join the game.
				maxNumberOfPlayers--;
			}
			catch(IOException error) {
				System.out.println("Connection error: " + error + ".");
			}
		}
		return true;
	}
	
	//Main driver for the program... Hit Crtl-F11 in eclipse to launch the server...
	//Of course, it has to compile first...
	public static void main( String [] args ) throws IOException
	{
		GameManager m = new GameManager();
		
		System.out.println( "<<<---BattleShip--->>>" );
		System.out.println( "Waiting for two players to connect to TCP:10000" );
		m.waitForPlayersToConnect();
		System.out.println( "Clients have joined!!!");		
		m.initPlayers();
		System.out.println( m.gameClients.get(0).getPlayerName() + " vs " + m.gameClients.get(1).getPlayerName() + " Let's Rumble..." );
		m.playGame();		
		System.out.println( "Shutting down server now... Disconnecting Clients..." );
	}
}
