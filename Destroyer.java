package battleship;

/**
 * Class implementing functionality for the 'Destroyer' ship type.
 * @author Dan Hart
 */
public class Destroyer extends Ship{

	/**
	 * Static immutable variable holding the starting length.
	 */
	private static final int DESTROYER_LENGTH = 2;
	
	/**
	 * Static immutable variable holding character to represent ship on board.
	 */
	private static final char DESTROYER_SYMBOL = 'D';
			
	/**
	 * Call the superclass constructor to set the ship name then explicitly set other parameters.
	 * @param shipName
	 */
	protected Destroyer(String shipName) {
		super(shipName);
		
		hitsRemaining = DESTROYER_LENGTH;
		type = SHIPTYPES.DESTROYER;
	}
	
	/**
	 * Method to determine/return the character representing the ship's status in each cell.
	 * @param cellHasBeenHit Boolean specifying whether or not the cell has been attacked.
	 * @return Character representing ship's status in each cell.
	 */
	protected char drawShipStatusAtCell(boolean cellHasBeenHit) {
		if(cellHasBeenHit)
			return Character.toLowerCase(DESTROYER_SYMBOL);
		return DESTROYER_SYMBOL;
	}
	
	/**
	 * Simple accessor method to return the current number of additional shots the ship can take.
	 * @return Number of additional shots the ship can take.
	 */
	protected int getLength() {
		return DESTROYER_LENGTH;	
	}
}
