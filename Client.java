package battleship;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Client {

	/**
	 * Instance variable that holds the new line separator depending on the local execution environment.
	 */
	final static String NEW_LINE_CHARACTER = System.getProperty("line.spearator");
	
	/**
	 * Each player needs a name, right?
	 */
	private String playerName;
	
	/**
	 * PrintWriter object client will use to send commands to the server.
	 */
	protected PrintWriter commandWriter;
	
	/**
	 * BufferedReader object client uses to process input from the server.
	 */
	protected BufferedReader commandReader;
	
	/**
	 * Client tracks GameManager in charge of administering the client's game.
	 */
	protected GameManager clientGameManager;
	
	/**
	 * Client's game board; tracks ships client has placed on its game board.
	 */
	private GameBoard clientBoard;
	
	/**
	 * Client's view of opponent's board; tracks cells client has attacked on opponent's board.
	 */
	private GameBoard opponentBoard;
	
	/**
	 * Client constructor; sets input/output streams and game manager to supplied values. Initializes client's and opponent's game boards.
	 * @param serverInput Input stream from server.
	 * @param outputToServer Output stream to server.
	 * @param gameManager Client's game manager.
	 */
	protected Client(BufferedReader serverInput, PrintWriter outputToServer, GameManager gameManager) {
		//Set input/output streams and game manager to supplied values.
		commandReader = serverInput;
		commandWriter = outputToServer;
		clientGameManager = gameManager;
		
		//Initialize the client's game board and its view of the opponent's board.
		clientBoard = new GameBoard(10,10);
		opponentBoard = new GameBoard(10,10);
	}	
	
	/**
	 * Draws player's and opponent's boards.
	 */
	protected void drawBoards() {
		this.commandWriter.println("Your board:" + NEW_LINE_CHARACTER + this.clientBoard.drawBoard() + NEW_LINE_CHARACTER + NEW_LINE_CHARACTER);
		this.commandWriter.println("Target board:" + NEW_LINE_CHARACTER + this.opponentBoard.drawBoard() + NEW_LINE_CHARACTER + NEW_LINE_CHARACTER);
	}
	
	/**
	 * 
	 */
	protected void playGame() throws IOException {
		//Notify the user the game has begun and print the game menu.
		this.commandWriter.println(NEW_LINE_CHARACTER + NEW_LINE_CHARACTER + "Player 2 has joined the game. Begin!");
		
		//StringTokenizer object that will be repeatedly used to process player commands
		StringTokenizer nextCommand;
		
		//While the player and their opponent have at least one ship remaining on their board, continue to process commands.
		while(clientBoard.hasShipsRemaning() && opponentBoard.hasShipsRemaning()) {
			this.commandWriter.println("------------------------");
			
			//Print the player's and opponent's board.
			this.drawBoards();
			
			//Wait for another command from the player.
			this.commandWriter.println("Waiting for next command.");
			
			//Flush buffer to ensure messages are displayed to player.
			this.commandWriter.flush();
			
			//Get and process next line.
			nextCommand = new StringTokenizer(this.commandReader.readLine(), " ");
			
			//Get the user's command character to ensure
			String userCommand = nextCommand.nextToken();
			
			switch (userCommand) {
				case "F":
					if(nextCommand.countTokens() != 3)
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
					this.commandWriter.println("Malformed command, ignoring." + NEW_LINE_CHARACTER);
					this.printMenu();
					this.commandWriter.flush();
			}					
		}
	}
	
	/**
	 * Prints a simple textual menu for the user.
	 */
	protected void printMenu() {
		//The player will use "F" to specify "Fire", followed by the row and column number to attack.
		this.commandWriter.println("To fire a missile, use F, followed by the row and column to attack.");
		this.commandWriter.println("Example: F 2 2");
		
		//To talk trash to their opponent, the player will use "C", followed by the message they wish to send.
		this.commandWriter.println("To send a message to your opponent, use C followed by your message.");
		this.commandWriter.println("Example: C Let's play!");		
	}
	
	protected boolean processChatCommand(String message) {
		this.commandWriter.println(message);
		return true;
	}
	
	/**
	 * Processes user's fire command.
	 */
	protected boolean processFireCommand(String rowNumber, String columnNumber) {
		Ship shipHit = this.opponentBoard.fireMissile(new Position(Integer.valueOf(rowNumber).intValue(), Integer.valueOf(columnNumber).intValue()));
		
		if(shipHit == null) {
			//Miss; notify player and return false.
			this.commandWriter.println("Miss at cell " + rowNumber + " " + columnNumber);
			this.commandWriter.flush();
			return false;
		}
		
		//The player hit one of their opponent's ships; tell them the ship name and return true.
		this.commandWriter.println("HIT! " + shipHit.getName() + " hit at cell " + rowNumber + " " + columnNumber);
		this.commandWriter.flush();
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
