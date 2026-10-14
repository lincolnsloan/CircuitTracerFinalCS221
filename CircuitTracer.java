import java.awt.Point;
import java.util.ArrayList;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail
 * @author lincolnsloan
 */
public class CircuitTracer {
	private Storage<TraceState> stateStore;
	private ArrayList<TraceState> bestPaths;

	/** Launch the program. 
	 * 
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		new CircuitTracer(args); //create this with args
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private void printUsage() {
		System.out.println("Usage: java CircuitTracer <-s | -q> <-c | -g> <filename>");
		System.out.println("  where  -s is to use stack storage");
		System.out.println("         -q is to use queue storage");
		System.out.println("  and    -c is console output");
		System.out.println("         -g is GUI output");
	}
	
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments. Search for shortest paths and report results.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	public CircuitTracer(String[] args) {
		//validate command line args
		if (args.length != 3) {
			printUsage();
			return;
		} else if (!args[0].equals("-s") && !args[0].equals("-q")) {
			printUsage();
			return;
		} else if (!args[1].equals("-c") && !args[1].equals("-g")) {
			printUsage();
			return;
		} 

		//initialize the Storage to use either stack or queue based on args[0]
		if (args[0].equals("-s")) { 
			stateStore = new Storage<TraceState>(Storage.DataStructure.stack);
		} else {	
			stateStore = new Storage<TraceState>(Storage.DataStructure.queue);
		} 

		//read in the CircuitBoard from the given file or print usage if file isn't found
		CircuitBoard board; 
		try {
			board = new CircuitBoard(args[2]);
		} catch (Exception e) { //invalid file name
			System.out.println(e.toString());
			return;
		}

		//add initial traceState objects for each open path around the start
		Point start = new Point(board.getStartingPoint());
		int startRow = start.x; 
		int startCol = start.y; 

		if (board.isOpen(startRow, startCol + 1)) {
			TraceState initialRight = new TraceState(board, startRow, startCol + 1);
			stateStore.store(initialRight);
		} 
		if (board.isOpen(startRow + 1, startCol)) {
			TraceState initialDown = new TraceState(board, startRow + 1, startCol);
			stateStore.store(initialDown);
		}
		if (board.isOpen(startRow, startCol - 1)) {
			TraceState initialLeft = new TraceState(board, startRow, startCol - 1);
			stateStore.store(initialLeft);
		}
		if (board.isOpen(startRow - 1, startCol)) {
			TraceState initialUp = new TraceState(board, startRow - 1, startCol);
			stateStore.store(initialUp);
		}

		//run the search for best paths
		bestPaths = new ArrayList<TraceState>(); 
		while(!stateStore.isEmpty()) {
			TraceState currentState = stateStore.retrieve();

			//check if current TraceState is best or equal to current best, otherwise generate possible paths from current TraceState and store
			if (currentState.isSolution()) { 
				if (bestPaths.isEmpty() || bestPaths.get(0).pathLength() == currentState.pathLength()) {
					bestPaths.add(currentState);
				} else if (currentState.pathLength() < bestPaths.get(0).pathLength()) {
					bestPaths.clear();
					bestPaths.add(currentState);
				}
			} else { 
				int currentRow = currentState.getRow();
				int currentCol = currentState.getCol();

				if (currentState.isOpen(currentRow, currentCol + 1)) {
					TraceState currentStateRight = new TraceState(currentState, currentRow, currentCol + 1);
					stateStore.store(currentStateRight);
				} 
				if (currentState.isOpen(currentRow + 1, currentCol)) {
					TraceState currentStateDown = new TraceState(currentState, currentRow + 1, currentCol);
					stateStore.store(currentStateDown);
				}
				if (currentState.isOpen(currentRow, currentCol - 1)) {
					TraceState currentStateLeft = new TraceState(currentState, currentRow, currentCol - 1);
					stateStore.store(currentStateLeft);
				}
				if (currentState.isOpen(currentRow - 1, currentCol)) {
					TraceState currentStateUp = new TraceState(currentState, currentRow - 1, currentCol);
					stateStore.store(currentStateUp);
				}
			}
		}

		// Console output prints every shortest board. GUI output is not implemented.
		if (args[1].equals("-c")) {
			for (int i = 0; i < bestPaths.size(); i ++) {
				System.out.println(bestPaths.get(i).toString());
			}
		} else {
			System.out.println("GUI output is not available.");
		}
	}
} // class CircuitTracer
