package alouw.csc480.search.implementations;

import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.ProblemTransformationOperation;
import alouw.csc480.search.interfaces.SearchFunction;

public class SearchMain {
	
	public static final String EASY_INITIAL_STATE = "1 3 4 8 6 2 7 0 5";
	public static final String MEDIUM_INITIAL_STATE = "2 8 1 0 4 3 7 6 5";
	public static final String HARD_INITIAL_STATE = "5 6 7 4 0 8 3 2 1";
	public static final String GOAL_STATE = "1 2 3 8 0 4 7 6 5";
	
	public static void main(String[] args) {
		
		final SearchFunction searchFunction = SearchFunction.BREADTH_FIRST;
		final EvaluationFunction evalFunction = EvaluationFunction.NONE;
		final ProblemState goalState = ProblemStateFactory.getNewProblemState(GOAL_STATE, 
				ProblemTransformationOperation.NONE);
		
		SearchTree tree = new SearchTree(searchFunction, evalFunction, 
				ProblemStateFactory.getNewProblemState(EASY_INITIAL_STATE, ProblemTransformationOperation.NONE), goalState);
		tree.search();
		
		if (tree.getSolution().isPresent()) {
			tree.printStats();
			System.out.println("");
			tree.getSolution().get().stream().forEach(x->System.out.println(x.toString()));
		} else System.out.println("Solution not found");
		
		System.out.println("");
		tree = new SearchTree(searchFunction, evalFunction, 
				ProblemStateFactory.getNewProblemState(MEDIUM_INITIAL_STATE, ProblemTransformationOperation.NONE), goalState);
		tree.search();

		if (tree.getSolution().isPresent()) {
			tree.printStats();
			System.out.println("");
			tree.getSolution().get().stream().forEach(x->System.out.println(x.toString()));
		} else System.out.println("Solution not found");
		
		System.out.println("");
		tree = new SearchTree(searchFunction, evalFunction, 
				ProblemStateFactory.getNewProblemState(HARD_INITIAL_STATE, ProblemTransformationOperation.NONE), goalState);
		tree.search();

		if (tree.getSolution().isPresent()) {
			tree.printStats();
			System.out.println("");
			tree.getSolution().get().stream().forEach(x->System.out.println(x.toString()));
		} else System.out.println("Solution not found");		
	}
}
