package alouw.csc480.search.interfaces;

import java.util.Set;

/*
 * An abstract definition of a particular state within a problem space. 
 * Each problem state instance is immutable.
 */
public interface ProblemState extends Comparable<ProblemState> {
	
	// returns the local cost of the one transformation from the predecessor state to the current state 
	public int getLocalTransformationCostFromPredecessorState();
	
	// returns the cumulative cost of all the transformations necessary to derive the current from the initial state 
	public int getCumulativeTransformationCostFromInitialState();
	
	// returns the result of a an evaluation function that approximates the cost of all remaining transformations
	// to the goalstate
	public int getExpectedTransformationCostToGoalState(EvaluationFunction h, ProblemState goalState);
	
	// returns the transformation operation that resulted in the current state 
	public ProblemTransformationOperation getPredecessorTransformation();
	
	// tests whether the transformation operation is legal
	public boolean isTransformationLegal(ProblemTransformationOperation operation);
	
	// applies the selected transformation and returns a new, successor problem state
	public ProblemState applyTransformation(ProblemTransformationOperation operation) throws IllegalStateException;
	
	// returns a set of immutable problem states that can legally be derived from applying all transformation
	// operations to the current state ; it does not include the current set
	public Set<ProblemState> getAllValidSucessorStates();
}