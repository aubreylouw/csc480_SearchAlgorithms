package alouw.csc480.search.implementations;

import java.util.Arrays;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.ProblemTransformationOperation;

public class ProblemStateFactoryTEST {
	
	@Rule
	public ExpectedException instantiationException = ExpectedException.none();
	
	@Rule
	public ExpectedException transformationException = ExpectedException.none();
	
	private static final String goalState = "1 2 3 5 0 4 6 7 8";
	private static final ProblemState problemGoal = ProblemStateFactory.getNewProblemState(goalState, 
			ProblemTransformationOperation.NONE);
	
	private static final String stateOne = "1 2 3 4 0 5 6 7 8";
	private static final ProblemTransformationOperation operationOne = ProblemTransformationOperation.DOWN;
	private static final ProblemState problemOne = ProblemStateFactory.getNewProblemState(stateOne, operationOne, 10);
	
	private static final String stateTwo = "8 7 6 5 4 3 2 1 0";
	private static final ProblemTransformationOperation operationTwo = ProblemTransformationOperation.RIGHT;
	private static final ProblemState problemTwo = ProblemStateFactory.getNewProblemState(stateTwo, operationTwo, 8);
	
	@Test
	public void satisifyCuriosity() {
		int[][] one = new int [3][3];
		int[][] two = new int [3][3];
		
		//[[5, 6, 7], [4, 0, 8], [3, 2, 1]] vs [[1, 2, 3], [8, 0, 4], [7, 6, 5]
		one[0][1] = 5;		one[0][1] = 6;		one[0][2] = 7;
		one[1][1] = 4;		one[1][1] = 0;		one[1][2] = 8;
		one[2][1] = 3;		one[2][1] = 2;		one[2][2] = 1;
		
		two[0][1] = 5;		two[0][1] = 6;		two[0][2] = 7;
		two[1][1] = 4;		two[1][1] = 8;		two[1][2] = 0;
		two[2][1] = 3;		two[2][1] = 2;		two[2][2] = 1;
		
		Assert.assertNotEquals(one.hashCode(), two.hashCode());
		Assert.assertNotEquals(Arrays.hashCode(one), Arrays.hashCode(two));
	}
	
	//Verify the object is correctly constructed
	@Test
	public void testLegalProblemStateFactoryExecution() {
		
		Assert.assertEquals(problemOne.getPredecessorTransformation(), operationOne);
		Assert.assertEquals(stateOne, problemOne.toString());
		Assert.assertEquals(2, problemOne.getLocalTransformationCostFromPredecessorState());
		Assert.assertEquals(12, problemOne.getCumulativeTransformationCostFromInitialState());
				
		Assert.assertEquals(problemTwo.getPredecessorTransformation(), operationTwo);
		Assert.assertEquals(stateTwo, problemTwo.toString());
		Assert.assertEquals(1, problemTwo.getLocalTransformationCostFromPredecessorState());
		Assert.assertEquals(9, problemTwo.getCumulativeTransformationCostFromInitialState());
		
		Assert.assertTrue(problemOne.compareTo(problemTwo) > 0);
		Assert.assertTrue(problemOne.compareTo(problemOne) == 0);
		Assert.assertTrue(problemTwo.compareTo(problemOne) < 0);
	}
	
	@Test
	public void testSuccessorStates() {
		// canonical example
		Set<ProblemState> setOne = problemOne.getAllValidSucessorStates();
		Assert.assertEquals(4, setOne.size());
		for (ProblemState s: setOne) {
			Assert.assertEquals(12, s.getCumulativeTransformationCostFromInitialState()-
									s.getLocalTransformationCostFromPredecessorState());
			Assert.assertTrue(s.getCumulativeTransformationCostFromInitialState() > 12);
		}
		
		// corner case
		Set<ProblemState> setTwo = problemTwo.getAllValidSucessorStates();
		Assert.assertEquals(2, setTwo.size());
		for (ProblemState s: setTwo) {
			Assert.assertEquals(9, s.getCumulativeTransformationCostFromInitialState()-
								   s.getLocalTransformationCostFromPredecessorState());
			Assert.assertTrue(s.getCumulativeTransformationCostFromInitialState() > 9);
		}
	}
	
	@Test
	public void testCountOfTilesOutOfPlaceCalculation() {
		Assert.assertEquals(14, problemOne.getExpectedTransformationCostToGoalState(EvaluationFunction.ASTAR_1, problemGoal));
		Assert.assertEquals(17, problemTwo.getExpectedTransformationCostToGoalState(EvaluationFunction.ASTAR_1, problemGoal));
	}
	
	@Test 
	public void testManhattenDistanceCalculation() {
		Assert.assertEquals(16, problemOne.getExpectedTransformationCostToGoalState(EvaluationFunction.ASTAR_2, problemGoal));
		Assert.assertEquals(29, problemTwo.getExpectedTransformationCostToGoalState(EvaluationFunction.ASTAR_2, problemGoal));
	}
	
	@Test
	public void testApplyTransformation() {
		// INITIAL STATE: 1 2 3 4 0 5 6 7 8"
		
		ProblemState stateDown = problemOne.applyTransformation(ProblemTransformationOperation.DOWN);
		Assert.assertEquals("1 2 3 4 7 5 6 0 8", stateDown.toString());
		
		ProblemState stateUp = problemOne.applyTransformation(ProblemTransformationOperation.UP);
		Assert.assertEquals("1 0 3 4 2 5 6 7 8", stateUp.toString());
		
		ProblemState stateRight = problemOne.applyTransformation(ProblemTransformationOperation.RIGHT);
		Assert.assertEquals("1 2 3 4 5 0 6 7 8", stateRight.toString());
		
		ProblemState stateLeft = problemOne.applyTransformation(ProblemTransformationOperation.LEFT);
		Assert.assertEquals("1 2 3 0 4 5 6 7 8", stateLeft.toString());
	}
	
	@Test
	public void testEquals() {
		Assert.assertFalse(problemOne.equals(problemTwo));
		
		// create a problem state copy but vary non-equals state fields
		final ProblemState problemOneCopy = ProblemStateFactory.getNewProblemState(stateOne, 
				ProblemTransformationOperation.NONE, 0);
		Assert.assertTrue(problemOne.equals(problemOneCopy));
		
		Assert.assertFalse(ProblemStateFactory.getNewProblemState("1 3 4 8 6 2 7 0 5", ProblemTransformationOperation.NONE, 0).equals(
							ProblemStateFactory.getNewProblemState("1 2 3 8 0 4 7 6 5", ProblemTransformationOperation.NONE, 0)));
	}
	
	// instantiation should fail if puzzle config does not have at least one zero tile
	@Test
	public void testIllegalProblemStateFactoryExecution() {
		instantiationException.expect(IllegalArgumentException.class);
		ProblemStateFactory.getNewProblemState("1 2 3 4 5 6 7 8 9", operationOne, 0);
	}	
	
	// cannot move off the board to the RIGHT
	@Test
	public void testIllegalProblemStateTransformations_RIGHT() {
		transformationException.expect(IllegalStateException.class);
		ProblemState problem = ProblemStateFactory.getNewProblemState("1 2 3 4 5 0 6 7 8", 
				ProblemTransformationOperation.RIGHT, 0);
		Assert.assertEquals(false, problem.applyTransformation(ProblemTransformationOperation.RIGHT));
		problem.applyTransformation(ProblemTransformationOperation.RIGHT);
	}
	
	// cannot move off the board to the LEFT
	@Test
	public void testIllegalProblemStateTransformations_LEFT() {
		transformationException.expect(IllegalStateException.class);
		ProblemState problem = ProblemStateFactory.getNewProblemState("0 2 3 1 4 5 6 7 8", 
				ProblemTransformationOperation.LEFT, 0);
		Assert.assertEquals(false, problem.applyTransformation(ProblemTransformationOperation.LEFT));
		problem.applyTransformation(ProblemTransformationOperation.LEFT);
	}
	
	// cannot move off the board DOWN
	@Test
	public void testIllegalProblemStateTransformations_DOWN() {
		transformationException.expect(IllegalStateException.class);
		ProblemState problem = ProblemStateFactory.getNewProblemState("1 2 3 4 5 6 0 7 8", 
				ProblemTransformationOperation.DOWN, 0);
		Assert.assertEquals(false, problem.applyTransformation(ProblemTransformationOperation.DOWN));
		problem.applyTransformation(ProblemTransformationOperation.DOWN);
	}
	
	// cannot move off the board UP
	@Test
	public void testIllegalProblemStateTransformations_UP() {
		transformationException.expect(IllegalStateException.class);
		ProblemState problem = ProblemStateFactory.getNewProblemState("1 2 0 4 5 6 1 7 8", 
				ProblemTransformationOperation.UP, 0);
		Assert.assertEquals(false, problem.applyTransformation(ProblemTransformationOperation.UP));
		problem.applyTransformation(ProblemTransformationOperation.UP);
	}
}
