package alouw.csc480.search.implementations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.ProblemTransformationOperation;

public final class ProblemStateFactory {

	public static ProblemState getNewProblemState(String stringRepresentationOfState, 
			   							ProblemTransformationOperation operationThatCreatedThisState) {
		return new ProblemStateImpl(stringRepresentationOfState,
				operationThatCreatedThisState);
	}
	
	public static ProblemState getNewProblemState(String stringRepresentationOfState, 
			  							   ProblemTransformationOperation operationThatCreatedThisState,
			  							   int cumulativeTransformationCost) {
		return new ProblemStateImpl(stringRepresentationOfState,
				operationThatCreatedThisState, cumulativeTransformationCost);
	}
}

/*
 * Immutable problem state class that represents one particular 8-Puzzle configuration. 
 * 
 * The following happens when an external agent moves a tile:
 * (1) checks whether the empty tile can be moved in the desired direction; 
 * (2) create a new problem state where the empty tile and the target tile are swapped
 * (3) computes any other useful state information clients may need
 * 
 * A problem state is instantiated from a string representation of a board with 9 tiles (one of which is blank).
 * For example, string "1 2 3 8 0 4 7 6 5" is mapped to
 *	-------------
 *  | 1 | 2 | 3 |
 *  -------------
 *  | 8 | 0 | 4 |
 *  -------------
 *  | 7 | 6 | 5 |
 *  -------------
 * 
 * Numeral "0" is the blank tile which is moved to create a new state. 
 * 
 * @precondition: applying the predecessor transformation to the current state yields a valid prior state
 * 
 */
class ProblemStateImpl implements ProblemState {

	// the dimensions of the 8-puzzle grid
	private static final short ROW_COUNT = 3;
	private static final short COLUMN_COUNT = 3;
	
	// the value of an uninitialized index
	private static final int UNDEFINED_INDEX = -1;
	
	// the location of the zero/blank tile
	private final int zeroRowIndex;
	private final int zeroColumnIndex;
	
	// internal representation of the 8 puzzle grid
	private final short[][] state = new short[ROW_COUNT][COLUMN_COUNT];
	
	// the transformation operation applied to the predecessor state to create *this* successor state
	private final ProblemTransformationOperation predecessorOperation;
	
	// the cost of the transformation operation from predecessor to current
	private final int transformationCostLocal;
	
	// the cost of the transformation operations to date
	private final int transformationCostCume;
	
	// compute once; use many times
	private final int hashCode;
	
	// instantiate a problem state with an internal state derived from the string representation 
	ProblemStateImpl(String stateString, ProblemTransformationOperation predecessorOperation, int cost) {
		this.predecessorOperation = predecessorOperation;
		
		//extract position of tiles from the stateString
		String[] stringArray = stateString.split(" ");
		
		if (stringArray.length != (ROW_COUNT * COLUMN_COUNT)) 
			throw new IllegalArgumentException("Expected " + (ROW_COUNT * COLUMN_COUNT) + "args; received " + stringArray.length);
		
		int rowIndex = 0;
		int colIndex = 0;
		int rowZeroIndex = UNDEFINED_INDEX;
		int columnZeroIndex = UNDEFINED_INDEX; 
		
		for (String strNumber : stringArray) {
			
			assert (rowIndex <= ROW_COUNT && colIndex <= COLUMN_COUNT);
			
			state[rowIndex][colIndex] = Integer.valueOf(Integer.parseInt(strNumber)).shortValue();
			
			if (Integer.valueOf(Integer.parseInt(strNumber)).equals(Integer.valueOf(0))) {
				rowZeroIndex = rowIndex;
				columnZeroIndex = colIndex;
			}
			colIndex ++;
			
			if (colIndex == COLUMN_COUNT) {
				colIndex = 0;
				rowIndex ++;
			} 
		}
		
		this.zeroRowIndex = rowZeroIndex;
		this.zeroColumnIndex = columnZeroIndex;
		
		if (this.zeroRowIndex == UNDEFINED_INDEX && this.zeroColumnIndex == UNDEFINED_INDEX) 
			throw new IllegalArgumentException("Every 8-Puzzle configuration requires a blank (zero) tile");
		
		this.transformationCostLocal = this.getPredecessorTransformationCost();
		this.transformationCostCume = cost + this.transformationCostLocal;
		
		this.hashCode = Arrays.deepHashCode(this.state);
	
	}
	
	// instantiate a problem state with an internal state derived from the string representation 
	// this is the way to create a goalState
	ProblemStateImpl(String stateString, ProblemTransformationOperation predecessorOperation) {
		this(stateString, predecessorOperation, 0);
	}
	
	/* 
	 * Helper function to create a string representation of a 2 dimensional integer array
	 */
	private static String generateStateString(short[][] state) {
		StringBuilder result = new StringBuilder();
		
		for (short rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (short colIndex = 0; colIndex < COLUMN_COUNT; colIndex++) {
				result.append(state[rowIndex][colIndex]);
				if ((rowIndex + colIndex) < (((ROW_COUNT * COLUMN_COUNT) - 1) / 2)) result.append(" ");
			}
		}
		
		return result.toString();
	}
	
	/*
	 * Computes the tile cost (== cost of move) when reversing the operation that created this state
	 */
	private int getPredecessorTransformationCost() {
		int result = 0;
		
		// if the predecessor op is valid, reversing it should yield valid state; i.e. no out of bounds accesses
		switch (this.predecessorOperation.reverseTransformation()) {
			case UP: result = this.state[this.zeroRowIndex-1][this.zeroColumnIndex]; break;
			case DOWN: result= this.state[this.zeroRowIndex+1][this.zeroColumnIndex]; break;
			case LEFT:	result = this.state[this.zeroRowIndex][this.zeroColumnIndex-1]; break;
			case RIGHT: result = this.state[this.zeroRowIndex][this.zeroColumnIndex+1]; break;
			case NONE: //do nothing
				break; 	
		}
		
		return result;
	}
	
	/* 
	 * Generate the successor state derived by applying the operation to the current state
	 * Note: a no-op operation results in *this* state being returned
	 *  
	 * @see alouw.csc480.search.interfaces.ProblemState#applyTransformation(alouw.csc480.search.interfaces.ProblemTransformationOperation)
	 */
	@Override
	public ProblemState applyTransformation(ProblemTransformationOperation operation) throws IllegalStateException {
		if (!this.isTransformationLegal(operation)) 
			throw new IllegalStateException(operation + "is illegal with state " + this.toString());
		
		short[][] newState = new short[ROW_COUNT][COLUMN_COUNT];
		for (short rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (short colIndex = 0; colIndex < COLUMN_COUNT; colIndex++) {
				newState[rowIndex][colIndex] = this.state[rowIndex][colIndex];
			}
		}
		
		switch (operation) {
			case UP: {
				short swap = newState[this.zeroRowIndex-1][this.zeroColumnIndex];
				newState[this.zeroRowIndex-1][this.zeroColumnIndex] = 0;
				newState[this.zeroRowIndex][this.zeroColumnIndex] = swap;
				break;
			}
			case DOWN: {
				short swap = newState[this.zeroRowIndex+1][this.zeroColumnIndex];
				newState[this.zeroRowIndex+1][this.zeroColumnIndex] = 0;
				newState[this.zeroRowIndex][this.zeroColumnIndex] = swap;
				break;
			}	
			case LEFT:	{
				short swap = newState[this.zeroRowIndex][this.zeroColumnIndex-1];
				newState[this.zeroRowIndex][this.zeroColumnIndex-1] = 0;
				newState[this.zeroRowIndex][this.zeroColumnIndex] = swap;
				break;
			}
			case RIGHT: {
				short swap = newState[this.zeroRowIndex][this.zeroColumnIndex+1];
				newState[this.zeroRowIndex][this.zeroColumnIndex+1] = 0;
				newState[this.zeroRowIndex][this.zeroColumnIndex] = swap;
				break;
			}
			case NONE: //do nothing
				break; 	
		}
		
		return ProblemStateFactory.getNewProblemState(ProblemStateImpl.generateStateString(newState), 
				operation, this.transformationCostCume);
	}

	/*
	 * Determines whether there is a valid successor state when applying the operation to the current state
	 */
	@Override
	public boolean isTransformationLegal(ProblemTransformationOperation operation) {
		boolean result = false;
		
		switch (operation) {
			case UP: 	result = (this.zeroRowIndex == 0) ? false: true; break;
			case DOWN:	result = (this.zeroRowIndex == (ROW_COUNT - 1)) ? false: true; break;
			case LEFT:	result = (this.zeroColumnIndex == 0) ? false: true; break;
			case RIGHT: result = (this.zeroColumnIndex == (COLUMN_COUNT - 1)) ? false: true; break;
			case NONE: 	result = true;
		}
		
		return result;
	}

	@Override
	public ProblemTransformationOperation getPredecessorTransformation() {
		return this.predecessorOperation;
	}
	
	@Override
	public String toString() {	
		return generateStateString(this.state);
	}

	@Override
	public Set<ProblemState> getAllValidSucessorStates() {
		Set<ProblemState> successorStates = new HashSet<>();
		
		for (ProblemTransformationOperation op: ProblemTransformationOperation.values()) {
			if (op.equals(ProblemTransformationOperation.NONE)) continue;

			if (this.isTransformationLegal(op)) {
				ProblemState nextState = this.applyTransformation(op);
				successorStates.add(nextState);
			} else {
				//ignore
			}
		}
		
		return successorStates;
	}

	@Override
	public int getLocalTransformationCostFromPredecessorState() {
		return this.transformationCostLocal;
	}

	@Override
	public int getCumulativeTransformationCostFromInitialState() {
		return this.transformationCostCume;
	}
	
	private static short[][] getArrayFromStringRepresentation(String stateString) {
		short[][] stateArray = new short[ROW_COUNT][COLUMN_COUNT];
		
		int rowIndex = 0;
		int colIndex = 0;		
		for (String strNumber : stateString.split(" ")) {
			stateArray[rowIndex][colIndex] = Integer.valueOf(Integer.parseInt(strNumber)).shortValue();
			
			colIndex ++;
			
			if (colIndex == COLUMN_COUNT) {
				colIndex = 0;
				rowIndex ++;
			} 
		}
		
		return stateArray;
	}
	
	private static int getNumberOfTilesOutOfPlace(ProblemState currentState, ProblemState goalState) {
		int result = 0;
		
		short[][] currentStateArray = new short[ROW_COUNT][COLUMN_COUNT];
		short[][] goalStateArray = new short[ROW_COUNT][COLUMN_COUNT];
		
		currentStateArray = ProblemStateImpl.getArrayFromStringRepresentation(currentState.toString());
		goalStateArray = ProblemStateImpl.getArrayFromStringRepresentation(goalState.toString());
		
		for (int rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
				result += (currentStateArray[rowIndex][columnIndex] !=
						   goalStateArray[rowIndex][columnIndex]) ? 1 : 0;
			}	
		}
		
		if (currentState.toString().equals(goalState.toString())) 
			assert(result == 0);
		else assert (result > 0);
		
		return result;
	}
	
	private static int getSumOfManhattanDistances(ProblemState currentState, ProblemState goalState) {
		int result = 0;
		
		// index the goal state by value, e.g. tile value 8 @ coordinates 1, 2
		// would be stored in the index state @ index 8
		// [8][0] references its original row index
		// [8][1] references its original column index
		short[][] indexedGoalState = new short[ROW_COUNT*COLUMN_COUNT][2]; 
		short[][] goalStateArray = new short[ROW_COUNT][COLUMN_COUNT];
		goalStateArray = ProblemStateImpl.getArrayFromStringRepresentation(goalState.toString());
		
		for (short rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (short columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
				int value = goalStateArray[rowIndex][columnIndex];
				
				assert (value < ROW_COUNT * COLUMN_COUNT);
				
				indexedGoalState[value][0] = rowIndex;
				indexedGoalState[value][1] = columnIndex;
			}	
		}
		
		// the # of moves from current to goal for every tile is the min distance in y + min distance in x
	    // e.g. if 4 is @ 0, 1 for current and @ 2, 2 in goal the math is:
		// ABS(goal row - current row) == 2 + ABS(goal column - current column) == 1 ==> 3
		short[][] currentStateArray = new short[ROW_COUNT][COLUMN_COUNT];
		currentStateArray = ProblemStateImpl.getArrayFromStringRepresentation(currentState.toString());
		
		for (short rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (short columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
				int index = currentStateArray[rowIndex][columnIndex];
				
				assert(index < ROW_COUNT * COLUMN_COUNT);
				
				result += Math.abs(indexedGoalState[index][0] - rowIndex) + 
						Math.abs(indexedGoalState[index][1] - columnIndex);
			}	
		}
		
		if (currentState.toString().equals(goalState.toString())) 
			assert(result == 0);
		else assert (result > 0);
		
		return result;
	}
	
	private static int getSumOfManhattanDistancestTimesTileValue (ProblemState currentState, ProblemState goalState) {
		int result = 0;
		
		// index the goal state by value, e.g. tile value 8 @ coordinates 1, 2
		// would be stored in the index state @ index 8
		// [8][0] references its original row index
		// [8][1] references its original column index
		short[][] indexedGoalState = new short[ROW_COUNT*COLUMN_COUNT][2]; 
		short[][] goalStateArray = new short[ROW_COUNT][COLUMN_COUNT];
		goalStateArray = ProblemStateImpl.getArrayFromStringRepresentation(goalState.toString());
		
		for (short rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (short columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
				int value = goalStateArray[rowIndex][columnIndex];
				
				assert (value < ROW_COUNT * COLUMN_COUNT);
				
				indexedGoalState[value][0] = rowIndex;
				indexedGoalState[value][1] = columnIndex;
			}	
		}
		
		// the # of moves from current to goal for every tile is the min distance in y + min distance in x
	    // e.g. if 4 is @ 0, 1 for current and @ 2, 2 in goal the math is:
		// ABS(goal row - current row) == 2 + ABS(goal column - current column) == 1 ==> 3
		// the tweak here is that for each computed value we multiply by the tile value to weight the move cost
		short[][] currentStateArray = new short[ROW_COUNT][COLUMN_COUNT];
		currentStateArray = ProblemStateImpl.getArrayFromStringRepresentation(currentState.toString());
		
		for (short rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
			for (short columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
				int index = currentStateArray[rowIndex][columnIndex];
				
				assert(index < ROW_COUNT * COLUMN_COUNT);
				
				result += (Math.abs(indexedGoalState[index][0] - rowIndex) + 
						   Math.abs(indexedGoalState[index][1] - columnIndex)) * index;
			}	
		}
		
		if (currentState.toString().equals(goalState.toString())) 
			assert(result == 0);
		else assert (result > 0);
		
		return result;
	}

	@Override
	public int getExpectedTransformationCostToGoalState(EvaluationFunction h, ProblemState goalState) {
		int result = 0;
		
		switch(h) {
			case NONE: result = this.transformationCostLocal; break; 
			case CUME_COST: result = this.transformationCostCume; break; 
			case GREEDY: result = ProblemStateImpl.getNumberOfTilesOutOfPlace(this, goalState); break;
			case ASTAR_1: result = this.transformationCostCume + 
					ProblemStateImpl.getNumberOfTilesOutOfPlace(this, goalState); break;
			case ASTAR_2: result = this.transformationCostCume + 
					ProblemStateImpl.getSumOfManhattanDistances(this, goalState); break;
			case ASTAR_3: result = this.transformationCostCume + 
								  getSumOfManhattanDistancestTimesTileValue(this, goalState); break;
			default: result = 0;
		}
		
		return result;
	}
	
	@Override
	public boolean equals(Object that){
		if (this == that) return true;
		if (!(that instanceof ProblemStateImpl)) return false;
		
		ProblemStateImpl thatState = (ProblemStateImpl) that;
		
		return Arrays.deepEquals(this.state, thatState.state); 
	}
	
	@Override 
	public int hashCode() {
		return this.hashCode;
	}

	/* 
	 * Note that sets may *never* call equals and therefore two distinct problem states with the
	 * same local cost could be considered identical and therefore discarded.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ProblemState that) {
		int result = Integer.valueOf(this.transformationCostCume).compareTo(that.getCumulativeTransformationCostFromInitialState());
		result = (result == 0 && this.equals(that) == false) ? 1 : result;
		return result;
	}
}