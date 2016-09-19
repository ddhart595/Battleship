package battleship;

/**
 * Class implementing functionality for the 'Destroyer' ship type.
 * @author Dan Hart
 */
public class Destroyer extends Ship{

	/**
	 * Static immutable variable holding the starting length for a destroyer.
	 */
	private static final int DESTROYER_LENGTH = 2;
	
	/**
	 * 
	 */
	private static final char DESTROYER_SYMBOL = 'D';
	
	//Carrier = 5
	//Battlship = 4
	//Cruiser = 3
	//Submarine = 3
			
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
	 * @return Character representing destroyer's status in each cell.
	 */
	protected char drawShipStatusAtCell(boolean cellHasBeenHit) {
		if(cellHasBeenHit)
			return Character.toLowerCase(DESTROYER_SYMBOL);
		return DESTROYER_SYMBOL;
	}
	
	/**
	 * Simple accessor method to return the current number of additional shots the ship can take.
	 * @return Number of additional shots the destroyer can take.
	 */
	protected int getLength() {
		return DESTROYER_LENGTH;	
	}
}
