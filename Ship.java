package battleship;

import java.util.ArrayList;

/**
 * Superclass for individual ship types. Most functionality will be implemented here.
 * @author Dan Hart
 */
abstract public class Ship {
	
	/**
	 * Array list of cells so each ship may track the cells on which they are positioned.
	 */
	protected ArrayList< Cell > cellsCovered;
	
	/**
	 * Tracks number of hits ship has remaining before being sunk.
	 * Initialized to ship "size" (e.g., 5 for aircraft carrier).
	 */
	protected int hitsRemaining;
	
	/**
	 * Each ship has to have a name, right?
	 */
	protected String shipName;
	
	/**
	 * Specifies ship type.
	 */
	protected SHIPTYPES type;
	
	/**
	 * Simple constructor to set the ship's name to the string specified.
	 * Array list of cells may be set through setPosition() method.
	 * @param shipName String to which this ship's name should be set.
	 */
	protected Ship(String shipName) {
		this.shipName = shipName;
	}
	
	/**
	 * Abstract class to display the appropriate character representing the ship based on ship's status.
	 * @param isHit Boolean specifying whether or not the cell has been attacked.
	 * @return Character representing the ship's status at that cell. 
	 */
	protected abstract char drawShipStatusAtCell( boolean isHit );

	/**
	 * Simple accessor method to return number of additional hits this ship can sustain.
	 * @return Number of additional hits this ship can sustain.
	 */
	protected int getHitsRemaining() {
		return this.hitsRemaining;
	}
	
	/**
	 * Abstract method to obtain the length/number of hits a ship can sustain.
	 * @return Ship length.
	 */
	protected abstract int getLength();
	
	/**
	 * Simple accessor method used to determine how many hits a ship could sustain at the beginning of a game.
	 * Utilizes an abstract method implemented in subclasses.
	 * @return Maximum number of hits a ship can sustain.
	 */
	protected int getMaxHitsPossible() {
		return this.getLength();
	}
	
	/**
	 * Simple accessor method to obtain the ship's name.
	 * @return Ship's name.
	 */
	protected String getName() {
		return this.shipName;
	}
	
	/**
	 * Method to determine if ship has sunk.
	 * @return True if ship has not sunk (i.e., can sustain more hits), false otherwise.
	 */
	protected boolean isAlive() {
		return !(this.hitsRemaining == 0);
	}
	
	/**
	 * Simple method used to decrement the hitsRemaining field as destroyer takes damage.
	 */
	protected void missileStrike() {
		if(this.hitsRemaining > 0)
			hitsRemaining--;
	}
	
	/**
	 * Used to set the cells on which a ship is located when the player puts the ship on the board.
	 * @param cellsCovered Array list of cells on which ship is located.
	 */
	protected void setPosition(ArrayList< Cell > cellsCovered) {
		this.cellsCovered = cellsCovered;
	}
}