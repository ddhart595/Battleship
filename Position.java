package battleship;

/**
 * Simple class to represent cell positions on board at which ships may be positioned and missiles fired.
 * @author Dan Hart
 */
public class Position {
	
	/**
	 * Column for this position.
	 */
	protected int column;
	
	/**
	 * Row for this position.
	 */
	protected int row;
	
	/**
	 * Simple constructor to create new position and initialize the row and column values to those provided.
	 * @param row Row for this position.
	 * @param column Column for this position.
	 */
	protected Position(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	/**
	 * Simple accessor method to return position's column number. Used in determining equivalence between two positions.
	 * @return Column number.
	 */
	protected int getColumn() {
		return this.column;
	}
	
	/**
	 * Simple accessor method to return position's row number. Used in determining equivalence between two positions.
	 * @return Row number.
	 */
	protected int getRow() {
		return this.row;
	}
	
	/**
	 * Method to determine if this position is equivalent to another, specified position. Compares row and column numbers to determine equivalence.
	 * @param otherPosition Position object to which this position should be compared.
	 * @return True if the two positions are equal (row and columns numbers match, respectively), false otherwise.
	 */
	protected boolean isEqual(Position otherPosition) {
		return(this.getColumn() == otherPosition.getColumn() && this.getRow() == otherPosition.getRow());
	}
}
