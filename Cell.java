package battleship;

/**
 * Game board comprised of 2-dimensional array of cells.
 * Board will notify cell if/when a ship is placed on it.
 * Cell tracks whether or not it has been struck by a missile.
 * @author Dan Hart
 */
public class Cell {

	/**
	 * Boolean to track whether cell has been struck by a missile.
	 */
	private boolean hasBeenAttacked;
	
	/**
	 * Boolean to track whether cell contains part of a ship.
	 */
	private boolean hasShip;
	
	/**
	 * Contains a pointer to the ship residing in this cell, if there is one.
	 */
	private Ship ship;
	
	/**
	 * Initializes cell.
	 * Sets hasBeenAttacked and hasShip to false.
	 * Sets cellState to a blank character.
	 * Sets ship to null.
	 */
	protected Cell() {
		//Initialize cell; set all instance variables to the respective 'null values
		
		hasBeenAttacked = false;
		
		hasShip = false;
		
		ship = null;
	}
	
	/**
	 * Draws the character representing this cell.
	 */
	protected char draw() {
		if( ship == null ) {
			if( hasBeenAttacked )
				return 'x';
			return ' ';
		}
		return ship.drawShipStatusAtCell( hasBeenAttacked );
		}
	
	/**
	 * Returns pointer to ship object contained in this cell.
	 */
	protected Ship getShip() {
		return ship;
	}
	
	/**
	 * Returns whether or not this cell has been attacked.
	 * @return Boolean representing whether or not cell has been attacked.
	 */
	protected boolean hasBeenAttacked() {
		return hasBeenAttacked;
	}
	
	/**
	 * Returns whether or not this cell has a ship.
	 * @return Boolean specifying whether or not this cell has a ship.
	 */
	protected boolean hasShip() {
		return hasShip;
	}
	
	/**
	 * Simple method to notify cell it has been attacked. Changes hasBeenAttacked to true.
	 */
	protected void missileAttack() {
		if(!(this.hasBeenAttacked)) {
			hasBeenAttacked = true;
			if(this.hasShip())
				ship.missileStrike();
		}
	}
	
	/**
	 * Sets the ship pointer to the specified ship.
	 * @param newShip Ship object to which this cell should point.
	 */
	protected void setShip(Ship newShip) {
		ship = newShip;
		hasShip = true;
	}
}
