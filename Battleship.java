package battleship;

/**
 * Class implementing functionality for the 'Battleship' ship type.
 * @author Dan Hart
 */
public class Battleship extends Ship{

	/**
	 * Static immutable variable holding the starting length.
	 */
	private static final int BATTLESHIP_LENGTH = 4;
	
	/**
	 * Static immutable variable holding character to represent ship on board.
	 */
	private static final char BATTLESHIP_SYMBOL = 'B';
			
	/**
	 * Call the superclass constructor to set the ship name then explicitly set other parameters.
	 * @param shipName
	 */
	protected Battleship(String shipName) {
		super(shipName);
		
		hitsRemaining = BATTLESHIP_LENGTH;
		type = SHIPTYPES.BATTLESHIP;
	}
	
	/**
	 * Method to determine/return the character representing the ship's status in each cell.
	 * @param cellHasBeenHit Boolean specifying whether or not the cell has been attacked.
	 * @return Character representing ship's status in each cell.
	 */
	protected char drawShipStatusAtCell(boolean cellHasBeenHit) {
		if(cellHasBeenHit)
			return Character.toLowerCase(BATTLESHIP_SYMBOL);
		return BATTLESHIP_SYMBOL;
	}
	
	/**
	 * Simple accessor method to return the current number of additional shots the ship can take.
	 * @return Number of additional shots the ship can take.
	 */
	protected int getLength() {
		return BATTLESHIP_LENGTH;	
	}
}
