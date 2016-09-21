package battleship;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Client {

	/**
	 * Client's game board; tracks ships client has placed on its game board.
	 */
	private GameBoard clientBoard;
	
	/**
	 * Client tracks GameManager in charge of administering the client's game.
	 */
	private GameManager clientGameManager;
		
	/**
	 * BufferedReader object client uses to process input from the server.
	 */
	protected BufferedReader clientReader;
	
	/**
	 * PrintWriter object client will use to send commands to the server.
	 */
	protected PrintWriter clientWriter;
	
	/**
	 * Instance variable that holds the new line separator depending on the local execution environment.
	 */
	final static String NEW_LINE_CHARACTER = System.getProperty("line.separator");
	
	/**
	 * Client's view of opponent's board; tracks cells client has attacked on opponent's board.
	 */
	private GameBoard opponentBoard;
	
	/**
	 * Each player needs a name, right?
	 */
	private String playerName;
	
	/**
	 * Client constructor; sets input/output streams and game manager to supplied values. Initializes client's and opponent's game boards.
	 * @param serverInput Input stream from server.
	 * @param outputToServer Output stream to server.
	 * @param gameManager Client's game manager.
	 */
	protected Client(BufferedReader serverInput, PrintWriter outputToServer, GameManager gameManager) {
		//Set input/output streams and game manager to supplied values.
		clientReader = serverInput;
		clientWriter = outputToServer;
		clientGameManager = gameManager;
		
		//Initialize the client's game board and its view of the opponent's board.
		clientBoard = new GameBoard(10,10);
		opponentBoard = new GameBoard(10,10);
	}
	
	/**
	 * Draws player's and opponent's boards.
	 */
	protected void drawBoards() {
		this.clientWriter.println(NEW_LINE_CHARACTER + "Your board:" + NEW_LINE_CHARACTER + this.clientBoard.drawBoard() + NEW_LINE_CHARACTER);
		this.clientWriter.println("Target board:" + NEW_LINE_CHARACTER + this.opponentBoard.drawBoard() + NEW_LINE_CHARACTER);
	}
	
	/**
	 * Simple accessor method to return the client's game board.
	 * Used when player is placing ships on the board.
	 */
	protected GameBoard getClientGameBoard() {
		return this.clientBoard;
	}
	
	/**
	 * Simple accessor method to get the player's name.
	 * @return Player's name.
	 */
	protected String getPlayerName() {
		return this.playerName;
	}
		
	/**
	 * As long as both players have ships remaining on their board, repeatedly asks user to input command then processes.
	 */
	protected void playGame() throws IOException {
		//Notify the user the game has begun and print the game menu.
		this.clientWriter.println(NEW_LINE_CHARACTER + NEW_LINE_CHARACTER + "Player 2 has joined the game. Begin!");
		
		//StringTokenizer object that will be repeatedly used to process player commands
		StringTokenizer nextCommand;
		
		//While the player and their opponent have at least one ship remaining on their board, continue to process commands.
		while(clientBoard.hasShipsRemaining() && this.clientGameManager.getOpponent(this).getClientGameBoard().hasShipsRemaining()) {
			this.clientWriter.println("------------------------");
			this.printMenu();
		
			//Print the player's and opponent's board.
			this.drawBoards();
			
			//Wait for another command from the player.
			this.clientWriter.println("Waiting for next command.");
			
			//Flush buffer to ensure messages are displayed to player.
			this.clientWriter.flush();
			
			//Get and process next line.
			while(!(this.clientReader.ready())) {}
			nextCommand = new StringTokenizer(this.clientReader.readLine(), " ");
			
			//Get the user's command character to ensure
			String userCommand = nextCommand.nextToken();
			
			switch (userCommand) {
				case "F":
					if(nextCommand.countTokens() != 2)
						//Malformed fire command; too many or not enough parameters.
						break;
					//Process fire command.
					this.processFireCommand(nextCommand.nextToken(), nextCommand.nextToken());
				case "C":
					if(!(nextCommand.countTokens() > 0))
						//Malformed chat command; not enough parameters.
						break;
					//Get player's message and send to opponent.
					String message = "";
					while(nextCommand.hasMoreTokens())
						message += nextCommand.nextToken();
					this.processChatCommand(message);
				case "D":
					if(nextCommand.countTokens() != 0)
						//Malformed draw command; too many parameters.
						break;
					this.drawBoards();
				default: 
					this.clientWriter.println("Malformed command, ignoring." + NEW_LINE_CHARACTER);
					this.printMenu();
					this.clientWriter.flush();
			}					
		}
	}
	
	/**
	 * Prints a simple textual menu for the user.
	 */
	protected void printMenu() {
		//The player will use "F" to specify "Fire", followed by the row and column number to attack.
		this.clientWriter.println("To fire a missile, use F, followed by the row and column to attack.");
		this.clientWriter.println("Example: F 2 2");
		
		//To talk trash to their opponent, the player will use "C", followed by the message they wish to send.
		this.clientWriter.println("To send a message to your opponent, use C followed by your message.");
		this.clientWriter.println("Example: C Let's play!");		
	}
	
	/**
	 * Processes user's chat command and sends message to other player.
	 * @param message Message player wants to send to opponent.
	 * @return true when message successfully sent.
	 */
	
	protected boolean processChatCommand(String message) {
		//Get the opponent's writer object and use it to send message to opponent.
		this.clientGameManager.getOpponent(this).clientWriter.println(message);
		return true;
	}
	
	/**
	 * Processes user's fire command.
	 */
	protected boolean processFireCommand(String rowNumber, String columnNumber) {
		Ship shipHit = this.clientGameManager.getOpponent(this).getClientGameBoard().fireMissile(new Position(Integer.valueOf(rowNumber).intValue(), Integer.valueOf(columnNumber).intValue()));
		
		//Set the ship for the cell attacked on our representation of the opponent's board equal to the returned ship.
		//Get the cell attacked.
		Cell attackedCell = this.opponentBoard.gameBoardCells.get(Integer.parseInt(rowNumber)).get(Integer.parseInt(columnNumber));
			
		//Let the cell know it has been attacked.
		attackedCell.missileAttack();
			
		//Set the cell's ship to the ship that was hit.
		attackedCell.setShip(shipHit);
		
		if(shipHit == null) {
			//Miss; notify player and return false.
			this.clientWriter.println("Miss at cell " + rowNumber + " " + columnNumber);
			this.clientWriter.flush();
			return false;
		}
		
		//The player hit one of their opponent's ships; tell them the ship name and return true.
		this.clientWriter.println("HIT! " + shipHit.getName() + " hit at cell " + rowNumber + " " + columnNumber);		
		this.clientWriter.flush();
		
		return true;
	}
	
	/**
	 * Allows the user to set their player name.
	 * @param playerName Name to which to set the player's name.
	 */
	protected void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
