package battleship;

/**
 * Class implementing functionality for the 'Cruiser' ship type.
 * @author Dan Hart
 */
public class Cruiser extends Ship{

	/**
	 * Static immutable variable holding the starting length.
	 */
	private static final int CRUISER_LENGTH = 3;
	
	/**
	 * Static immutable variable holding character to represent ship on board.
	 */
	private static final char CRUISER_SYMBOL = 'R';
	
	/**
	 * Call the superclass constructor to set the ship name then explicitly set other parameters.
	 * @param shipName
	 */
	protected Cruiser(String shipName) {
		super(shipName);
		
		hitsRemaining = CRUISER_LENGTH;
		type = SHIPTYPES.CRUISER;
	}
	
	/**
	 * Method to determine/return the character representing the ship's status in each cell.
	 * @param cellHasBeenHit Boolean specifying whether or not the cell has been attacked.
	 * @return Character representing ship's status in each cell.
	 */
	protected char drawShipStatusAtCell(boolean cellHasBeenHit) {
		if(cellHasBeenHit)
			return Character.toLowerCase(CRUISER_SYMBOL);
		return CRUISER_SYMBOL;
	}
	
	/**
	 * Simple accessor method to return the current number of additional shots the ship can take.
	 * @return Number of additional shots the ship can take.
	 */
	protected int getLength() {
		return CRUISER_LENGTH;	
	}
}
