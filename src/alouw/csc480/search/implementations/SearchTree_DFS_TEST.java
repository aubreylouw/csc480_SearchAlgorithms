package alouw.csc480.search.implementations;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.ProblemTransformationOperation;
import alouw.csc480.search.interfaces.SearchFunction;
import alouw.csc480.search.interfaces.TreeNode;

public class SearchTree_DFS_TEST {

	public static final String EASY_INITIAL_STATE = "1 3 4 8 6 2 7 0 5";
	public static final String MEDIUM_INITIAL_STATE = "2 8 1 0 4 3 7 6 5";
	public static final String HARD_INITIAL_STATE = "5 6 7 4 0 8 3 2 1";
	
	public static final String GOAL_STATE_STRING = "1 2 3 8 0 4 7 6 5";
	
	public static final ProblemState GOAL_STATE = ProblemStateFactory.getNewProblemState(GOAL_STATE_STRING, 
			ProblemTransformationOperation.NONE);
	
	public static final SearchFunction SEARCH_ALGO = SearchFunction.DEPTH_FIRST;
	public static final EvaluationFunction EVAL_FUNCTION = EvaluationFunction.NONE;

	@Test
	public void testWithEasyInitialState() {
		final ProblemState initialState = ProblemStateFactory.getNewProblemState(EASY_INITIAL_STATE, 
				ProblemTransformationOperation.NONE);
		
		SearchTree tree = new SearchTree(SEARCH_ALGO, EVAL_FUNCTION, initialState, GOAL_STATE);
		boolean solutionFound = tree.search();
		
		Assert.assertTrue(solutionFound);
		Assert.assertTrue(tree.getSolution().isPresent());
				
		final List<TreeNode> solutionList = new ArrayList<>();
		
		tree.getSolution().get().stream().skip(tree.getSolution().get().size()-1).forEach(x -> {
			solutionList.add(x);
		});
		
		Assert.assertEquals(1, solutionList.size());
		Assert.assertEquals(GOAL_STATE, solutionList.get(0).getProblemState());
		Assert.assertEquals(109155, solutionList.get(0).getDepth());
	}
	
	@Test
	public void testWithMediumInitialState() {
		final ProblemState initialState = ProblemStateFactory.getNewProblemState(MEDIUM_INITIAL_STATE, 
				ProblemTransformationOperation.NONE);
		
		SearchTree tree = new SearchTree(SEARCH_ALGO, EVAL_FUNCTION, initialState, GOAL_STATE);
		boolean solutionFound = tree.search();
		
		Assert.assertTrue(solutionFound);
		Assert.assertTrue(tree.getSolution().isPresent());
				
		final List<TreeNode> solutionList = new ArrayList<>();
		
		tree.getSolution().get().stream().skip(tree.getSolution().get().size()-1).forEach(x -> {
			solutionList.add(x);
		});
		
		Assert.assertEquals(1, solutionList.size());
		Assert.assertEquals(GOAL_STATE, solutionList.get(0).getProblemState());
		Assert.assertEquals(106321, solutionList.get(0).getDepth());
	}
	
	@Test
	public void testWithHardInitialState() {
		final ProblemState initialState = ProblemStateFactory.getNewProblemState(HARD_INITIAL_STATE, 
				ProblemTransformationOperation.NONE);
		
		SearchTree tree = new SearchTree(SEARCH_ALGO, EVAL_FUNCTION, initialState, GOAL_STATE);
		boolean solutionFound = tree.search();
		
		Assert.assertTrue(solutionFound);
		Assert.assertTrue(tree.getSolution().isPresent());
				
		final List<TreeNode> solutionList = new ArrayList<>();
		
		tree.getSolution().get().stream().skip(tree.getSolution().get().size()-1).forEach(x -> {
			solutionList.add(x);
		});
		
		Assert.assertEquals(1, solutionList.size());
		Assert.assertEquals(GOAL_STATE, solutionList.get(0).getProblemState());
		Assert.assertEquals(72686, solutionList.get(0).getDepth());
	}
}
