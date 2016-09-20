package battleship;

import java.util.ArrayList;
import java.util.ListIterator;
import java.lang.StringBuilder;

public class GameBoard {

	/**
	 * Array of ships holding the ships that have been placed on the board.
	 */
	protected ArrayList<Ship> boardShips;
	
	/**
	 * 2-dimensional array of cells representing game board.
	 */
	protected ArrayList<ArrayList <Cell>> gameBoardCells;
	
	/**
	 * New line character, set based on current environment.
	 */
	protected static final String NEW_LINE_CHARACTER = System.getProperty("line.separator"); 
	
	/**
	 * Number of columns on game board.
	 */
	//Will update to dynamically ask user how many columns game board should have.
	private int numColumns;
	
	/**
	 * Number of rows on game board.
	 */
	//Will update to dynamically ask user how many rows game board should have.
	private int numRows;
	
	/**
	 * Initializes game board's array list of cells to numRows * numColumns.
	 */
	protected GameBoard(int numRows, int numColumns) {
		//Set the number of rows and columns to the provided values.
		this.numRows = numRows;
		this.numColumns = numColumns;
		
		//Initialize the game board cells and the array of ships currently on the board.		
		gameBoardCells = new ArrayList<ArrayList<Cell>>();
		
		//First add numRow ArrayList<Cell> to the game board.
		for (int index = 0; index < numRows; index++)
			gameBoardCells.add(new ArrayList<Cell>(numColumns));
		
		//Iterate through each row and initialize each cell individually.
		for(ArrayList<Cell> rows : gameBoardCells)
			for (int index = 0; index < numColumns; index++)
				rows.add(new Cell());
				
		boardShips = new ArrayList<Ship>();
	}
	
	protected StringBuilder drawBoard() {
		StringBuilder gameBoard = new StringBuilder();
		
		gameBoard.append("+");
		
		for (int index = 0; index < numColumns; index++)
			gameBoard.append("-");
		
		gameBoard.append("+" + NEW_LINE_CHARACTER);
		
		for(ArrayList<Cell> row : gameBoardCells) {
			gameBoard.append("+");

			ListIterator<Cell> rowCells = row.listIterator();
			
			while(rowCells.hasNext())
				gameBoard.append(Character.toString(rowCells.next().draw()));
			gameBoard.append("+" + NEW_LINE_CHARACTER);
		}
		
		gameBoard.append("+");
		
		for (int index = 0; index < numColumns; index++)
			gameBoard.append("-");
		
		gameBoard.append("+" + NEW_LINE_CHARACTER);
		
		return gameBoard;
		
	}
	
	/**
	 * Adds ship to game board.
	 * Must check to ensure ship will fit on game board and does not overlap with another ship.
	 * @param ship Ship to be added to the board.
	 * @param sternPosition Position of the ship's stern.
	 * @param shipHeading Direction in which the ship is facing.
	 * @return True if ship successfully added to board, false otherwise.
	 */
	protected boolean addShip(Ship ship, Position sternPosition, HEADING shipHeading) {
		//Save the ship's length into a local variable so it make be compared to board bounds without having to repeatedly call ship.getLength().
		int shipLength = ship.getLength();
		
		//Create iterators for the ship's rows and columns.
		ListIterator<Cell> rowCellIterator;
		
		//Declare variable to track next row to be checked in the NORTH and SOUTH cases.
		int nextRow;
		
		//Check the stern position's row and column to make sure they are not outside the board bounds.
		int sternRow = sternPosition.getRow();
		int sternColumn = sternPosition.getColumn();
		
		if(sternRow < 0 || sternColumn < 0 || sternRow > this.numRows - 1 || sternColumn > this.numColumns - 1)
			return false;
		
		//Ship's stern position is valid, need to make sure ship fits on board.
		switch (shipHeading) {
			case NORTH:
				if(sternRow - shipLength < 0)
					return false;
				break;
			case SOUTH:
				if(sternRow + shipLength > this.numRows - 1)
					return false;
				break;
			case EAST:
				if(sternColumn + shipLength > this.numColumns - 1)
					return false;
				break;
			case WEST:
				if(sternColumn - shipLength < 0)
					return false;
		}
		
		//Ship stern position is valid and it fits on board. Ensure it does not overlap with another ship.
		switch (shipHeading) {
			case NORTH:
				//Check the cells in the sternColumn column in the shipLength - 1 rows above the stern's position.
				nextRow = sternRow;
				while(shipLength > 0) {
					if(gameBoardCells.get(nextRow).get(sternColumn).hasShip())
						return false;
					nextRow--;
					shipLength--;
				}
				break;
			case SOUTH:
				//Check the cells in the sternColumn column in the shipLength - 1 rows below the stern's position.
				nextRow = sternRow;
				while(shipLength > 0) {
					if(gameBoardCells.get(nextRow).get(sternColumn).hasShip())
						return false;
					nextRow++;
					shipLength--;
				}
				break;
			case EAST:
				//Get an iterator for the cells in the row to which the ship is to be added.
				rowCellIterator = gameBoardCells.get(sternRow).listIterator();
				
				//Set the iterator to the column in which the ship's stern will be placed.
				while(rowCellIterator.nextIndex() < sternColumn)
					rowCellIterator.next();
				
				//If there is a ship in one of the columns that will be occupied by the ship to be added, return false.
				//We have already verified ship will fit on board; do not need to invoke the hasNext method on the iterator.
				//Note the use of next(); the ship ship is pointing east so we want to check the subsequent cells.
				while(shipLength > 0) {
					if(rowCellIterator.next().hasShip())
						return false;
					shipLength--;
				}
				break;
			case WEST:
				//Get an iterator for the cells in the row to which the ship is to be added.
				rowCellIterator = gameBoardCells.get(sternRow).listIterator();
				
				//Set the iterator to the column in which the ship's stern will be placed.
				while(rowCellIterator.nextIndex() < sternColumn)
					rowCellIterator.next();
				
				//If there is a ship in one of the columns that will be occupied by the ship to be added, return false.
				//We have already verified ship will fit on board; do not need to invoke the hasPrevious method on the iterator.
				//Note the use of previous(); the ship ship is pointing west so we want to check the prior cells.
				while(shipLength > 0) {
					if(rowCellIterator.previous().hasShip())
						return false;
					shipLength--;
				}		
		}
		
		//Ship is within board bounds and does not overlay another ship. Add it to the board.
		boardShips.add(ship);
		
		//Create a list of cell positions covered by the ship to notify the ship of the cells it covers and to notify the cells they now have a ship.
		ArrayList<Cell> shipCells = new ArrayList<Cell>();
		
		//Reset shipLength, sternRow and sternColumn variables to ensure we grab the correct number of cells.
		shipLength = ship.getLength();
		sternRow = sternPosition.getRow();
		sternColumn = sternPosition.getColumn();
		
		
		switch (shipHeading) {
			case NORTH:
				while(shipLength > 0) {
					shipCells.add(gameBoardCells.get(sternRow).get(sternColumn));
					sternRow--;
					shipLength--;
				}		
				break;
			case SOUTH:
				while(shipLength > 0) {
					shipCells.add(gameBoardCells.get(sternRow).get(sternColumn));
					sternRow++;
					shipLength--;
				}
				break;
			case EAST:
				//Create a list iterator for the stern's row and fast-forward to the stern position.
				rowCellIterator = gameBoardCells.get(sternRow).listIterator();
				while(rowCellIterator.nextIndex() < sternColumn)
					rowCellIterator.next();
				
				//Add each cell covered by the ship to the list of cells.
				while(shipLength > 0) {
					shipCells.add(rowCellIterator.next());
					shipLength--;
					
				}
				break;
			case WEST:
				//Create a list iterator for the stern's row and fast-forward to the stern position.
				rowCellIterator = gameBoardCells.get(sternRow).listIterator();
				while(rowCellIterator.nextIndex() < sternColumn)
					rowCellIterator.next();
				
				//Add each cell covered by the ship to the list of cells.
				while(shipLength > 0) {
					shipCells.add(rowCellIterator.previous());
					shipLength--;
					
				}
		}
		
		//Notify the ship as to the cells it covers.
		ship.setPosition(shipCells);
		
		//Notify each cell it now has a ship and tell it what ship it has.
		for(Cell currentCell: shipCells)
			currentCell.setShip(ship);
		
		//Ship successfully added to board. Return true.
		return true;
	}
	
	/**
	 * Processes a missile fired at a certain cell
	 * @param cellAttacked Position that as been attacked.
	 * @return Reference to the ship that was hit, null if no ship hit.
	 */
	protected Ship fireMissile(Position cellAttacked) {
		//Check to ensure the position attacked is on the game board.
		int rowAttacked = cellAttacked.getRow();
		int columnAttacked = cellAttacked.getColumn();
		
		if(rowAttacked < 0 || rowAttacked > this.numRows - 1 || columnAttacked < 0 || columnAttacked > this.numRows - 1)
			//Cell attacked is off the board; return null
			return null;
		
		//Get the cell that was attacked.
		Cell attackedCell = gameBoardCells.get(cellAttacked.getRow()).get(cellAttacked.getColumn());
		
		//Tell the cell it has been attacked. If the cell has a ship, it will tell the ship it has been attacked.
		attackedCell.missileAttack();
		
		//If the cell doesn't have a ship, return null.
		if(!(attackedCell.hasShip()))
			return null;
		
		//If the cell attacked has a ship, return the ship.
		return attackedCell.getShip();
	}
	
	/**
	 * Simple method to test and see if any ships are still alive on the board.
	 * @return Returns true if at least one ship is still remaining, false otherwise.
	 */
	protected boolean hasShipsRemaning() {
		for(Ship thisShip : boardShips) {
			if(!thisShip.isAlive())
				return false;
		}
		return true;
	}
}
