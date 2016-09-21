package battleship;

/**
 * Class implementing functionality for the 'Carrier' ship type.
 * @author Dan Hart
 */
public class Carrier extends Ship{

	/**
	 * Static immutable variable holding the starting length.
	 */
	private static final int CARRIER_LENGTH = 5;
	
	/**
	 * Static immutable variable holding character to represent ship on board.
	 */
	private static final char CARRIER_SYMBOL = 'C';
			
	/**
	 * Call the superclass constructor to set the ship name then explicitly set other parameters.
	 * @param shipName
	 */
	protected Carrier(String shipName) {
		super(shipName);
		
		hitsRemaining = CARRIER_LENGTH;
		type = SHIPTYPES.CARRIER;
	}
	
	/**
	 * Method to determine/return the character representing the ship's status in each cell.
	 * @param cellHasBeenHit Boolean specifying whether or not the cell has been attacked.
	 * @return Character representing ship's status in each cell.
	 */
	protected char drawShipStatusAtCell(boolean cellHasBeenHit) {
		if(cellHasBeenHit)
			return Character.toLowerCase(CARRIER_SYMBOL);
		return CARRIER_SYMBOL;
	}
	
	/**
	 * Simple accessor method to return the current number of additional shots the ship can take.
	 * @return Number of additional shots the ship can take.
	 */
	protected int getLength() {
		return CARRIER_LENGTH;	
	}
}
