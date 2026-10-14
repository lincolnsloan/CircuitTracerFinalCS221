import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Represents a 2D circuit board as read from an input file.
 *  
 * @author mvail
 */
public class CircuitBoard {
	/** current contents of the board */
	private char[][] board;
	/** location of row,col for '1' */
	private Point startingPoint;
	/** location of row,col for '2' */
	private Point endingPoint;

	//constants you may find useful
	private final int ROWS; //initialized in constructor
	private final int COLS; //initialized in constructor
	private final char OPEN = 'O';	//capital 'o', an open position
	private final char CLOSED = 'X';//a blocked position
	private final char TRACE = 'T';	//part of the trace connecting 1 to 2
	private final char START = '1';	//the starting component
	private final char END = '2';	//the ending component
	private final String ALLOWED_CHARS = "" + OPEN + CLOSED + TRACE + START + END;

	/** Construct a CircuitBoard from a given board input file, where the first
	 * line contains the number of rows and columns as ints and each subsequent
	 * line is one row of characters representing the contents of that position.
	 * Valid characters are as follows:
	 *  'O' an open position
	 *  'X' an occupied, unavailable position
	 *  '1' first of two components needing to be connected
	 *  '2' second of two components needing to be connected
	 *  'T' is not expected in input files - represents part of the trace
	 *   connecting components 1 and 2 in the solution
	 * 
	 * @param filename
	 * 		file containing a grid of characters
	 * @throws FileNotFoundException if Scanner cannot open or read the file
	 * @throws InvalidFileFormatException for any file formatting or content issue
	 */
	public CircuitBoard(String filename) throws FileNotFoundException {
		Scanner fileScan = new Scanner(new File(filename));
		int numStarts = 0;
		int numEnds = 0;

		try {
			//get ROWS and COLS from first line and initialize
			if (!fileScan.hasNextLine()) {
				throw new InvalidFileFormatException("File is empty");
			}
			String[] dimensions = fileScan.nextLine().trim().split("\\s+");
			if (dimensions.length != 2) { 
				throw new InvalidFileFormatException("First line of file must contain two integers for number of rows and columns in grid");
			}
			int rows;
			int cols;
			try {
				rows = Integer.parseInt(dimensions[0]);
				cols = Integer.parseInt(dimensions[1]);
			} catch (NumberFormatException e) {
				throw new InvalidFileFormatException("First line of file must contain two integers for number of rows and columns in grid");
			}
			if (rows <= 0 || cols <= 0) {
				throw new InvalidFileFormatException("Number of rows and columns must be positive integers");
			}
			ROWS = rows;
			COLS = cols;
			board = new char[ROWS][COLS];
				
			//loop through and populate board array
			for (int row = 0; row < ROWS; row++) {
				if (!fileScan.hasNextLine()) { 
					throw new InvalidFileFormatException("Board has fewer rows than declared");
				}
				String[] line = fileScan.nextLine().trim().split("\\s+");

				if (line.length < COLS) {
					throw new InvalidFileFormatException("Row " + (row + 1) + " has fewer columns than declared");
				} else if (line.length > COLS) {
					throw new InvalidFileFormatException("Row " + (row + 1) + " has more columns than declared");
				}
				
				for (int col = 0; col < COLS; col++) {
					if (line[col].length() != 1) {
						throw new InvalidFileFormatException("Each board position must be a single character. Valid characters include: O, X, T, 1, 2");
					}
					char currentChar = line[col].charAt(0);
					board[row][col] = currentChar;
					
					//assign and count START and END
					if (currentChar == START) { 
						numStarts++;
						startingPoint = new Point(row, col);
					} else if (currentChar == END) { 
						numEnds++;
						endingPoint = new Point(row, col);
					} else if (ALLOWED_CHARS.indexOf(currentChar) == -1) { 
						throw new InvalidFileFormatException("Board has invalid character. Valid characters include: O, X, T, 1, 2");
					}
				}
			}
	
			if (fileScan.hasNextLine()) {
				throw new InvalidFileFormatException("Board has more rows than declared");
			}
			if (numStarts != 1 || numEnds != 1) {
				throw new InvalidFileFormatException("Board must contain exactly one '1' for the starting point and one '2' for the ending point");
			}
		} catch (NoSuchElementException e) {
			throw new InvalidFileFormatException("File is empty or ended before the board was complete");
		} finally {
			fileScan.close();
		}
	}
	
	/** Copy constructor - duplicates original board
	 * 
	 * @param original board to copy
	 */
	public CircuitBoard(CircuitBoard original) {
		board = original.getBoard();
		startingPoint = new Point(original.startingPoint);
		endingPoint = new Point(original.endingPoint);
		ROWS = original.numRows();
		COLS = original.numCols();
	}

	/** Utility method for copy constructor
	 * @return copy of board array */
	private char[][] getBoard() {
		char[][] copy = new char[board.length][board[0].length];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}
	
	/** Return the char at board position x,y
	 * @param row row coordinate
	 * @param col col coordinate
	 * @return char at row, col
	 */
	public char charAt(int row, int col) {
		return board[row][col];
	}
	
	/** Return whether given board position is open
	 * @param row
	 * @param col
	 * @return true if position at (row, col) is open 
	 */
	public boolean isOpen(int row, int col) {
		if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
			return false;
		}
		return board[row][col] == OPEN;
	}
	
	/** Set given position to be a 'T'
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public void makeTrace(int row, int col) {
		if (!isOpen(row, col)) {
			if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
				throw new OccupiedPositionException("row " + row + ", col " + col + " is outside the board");
			}
			throw new OccupiedPositionException("row " + row + ", col " + col + " contains '" + board[row][col] + "'");
		}
		board[row][col] = TRACE;
	}
	
	/** @return starting Point(row,col) */
	public Point getStartingPoint() {
		return new Point(startingPoint);
	}
	
	/** @return ending Point(row,col) */
	public Point getEndingPoint() {
		return new Point(endingPoint);
	}
	
	/** @return number of rows in this CircuitBoard */
	public int numRows() {
		return ROWS;
	}
	
	/** @return number of columns in this CircuitBoard */
	public int numCols() {
		return COLS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				str.append(board[row][col] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}
}// class CircuitBoard
